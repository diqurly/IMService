package org.diqurly.config;

import io.netty.channel.Channel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.SSLException;

import org.apache.log4j.Logger;
import org.diqurly.connect.ConnectManage;
import org.diqurly.connect.ConnectServerHandler;
import org.diqurly.connect.thread.CacheConnectThread;
import org.diqurly.group.GroupServiceHandler;
import org.diqurly.handler.DChandlerInterface;
import org.diqurly.handler.DhandlerInterface;
import org.diqurly.other.OtherServiceHandler;
import org.diqurly.packet.Packet;
import org.diqurly.route.MessageRouteThread;
import org.diqurly.service.ClientHandler;
import org.diqurly.user.UserManage;

public class ConfiguratorAbstract {
	public static final String PROPERTY_FILENAME_PROP_KEY = "--property-file";
	public static final String PROPERTY_FILENAME_PROP_DEF = "config/init.properties";
	private static final Logger log = Logger.getLogger(ConfiguratorAbstract.class);
	private Map<String, String> initProperties = new LinkedHashMap<String, String>(
			100);

	private ThreadGroup cTServersGroup;
	private ThreadGroup serversGroup;
	
	private ConnectManage<Channel> connectMange = new ConnectManage<Channel>();
	private UserManage<Channel> userManage = new UserManage<Channel>();
	
	public void init(String[] args) {
		Log.init();
		parseArgs(args);
	}

	/**
	 * 配置文件解析
	 * 
	 * @param args
	 */
	private void parseArgs(String[] args) {

		if ((args != null) && (args.length > 0)) {
			for (int i = 0; i < args.length; i++) {
				String key = null;
				String val = null;

				if ((key == null) && args[i].startsWith("-")) {
					key = args[i];
					val = args[++i];
				}
				if ((key != null) && (val != null)) {
					initProperties.put(key, val);

					// System.out.println("Setting defaults: " + key + "=" +
					// val.toString());
					log.info("Setting defaults: "+key+" ="+val.toString());
				} // end of if (key != null)
			} // end of for (int i = 0; i < args.length; i++)
		}

		String property_filenames = (String) initProperties
				.get(PROPERTY_FILENAME_PROP_KEY);
		if (property_filenames != null) {
			String[] prop_files = property_filenames.split(",");

			if (prop_files.length == 1) {
				File f = new File(prop_files[0]);
				if (!f.exists()) {
					log.warn("Provided property file "
							+f.getAbsolutePath()+" does NOT EXISTS! Using default one "
							+PROPERTY_FILENAME_PROP_DEF);
					prop_files[0] = PROPERTY_FILENAME_PROP_DEF;
				}
			}

			for (String property_filename : prop_files) {
				try {
					Properties defProps = new Properties();
					defProps.load(new FileReader(property_filename));
					Set<String> prop_keys = defProps.stringPropertyNames();

					for (String key : prop_keys) {
						String value = defProps.getProperty(key).trim();
						if (key.startsWith("-") || key.equals("config-type")) {
							initProperties.put(key.trim(), value);
							log.info("Added default config parameter: ("+key+"="+value+")");
						}
					}

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					log.warn("Given property file was not found: "+property_filename,e);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					log.warn("Can not read property file: "+ property_filename, e);
				}
			}

		}

	}

	/**
	 * 获取配置文件map
	 * 
	 * @return
	 */
	public Map<String, String> getDefConfigParams() {
		return initProperties;
	}

	public void start() {
		if (initProperties.get(ConfigConst.ISDISTRIBUTED_P) != null) {
			if (Boolean.parseBoolean( initProperties.get(ConfigConst.ISDISTRIBUTED_P))) {
				ConfigConst.ISDISTRIBUTED=true;
				// 开启分布式
				// 获取自身角色
				ConfigConst.DISTRIBUTED_ROLE = Integer.parseInt(initProperties
						.get(ConfigConst.DISTRIBUTED_ROLE_P));
				switch (ConfigConst.DISTRIBUTED_ROLE) {
				case ConfigConst.DISTRIBUTED_CS:
					initCS();
					break;
				case ConfigConst.DISTRIBUTED_GROUP:
					initGroup();
					break;
				case ConfigConst.DISTRIBUTED_OTHER:
					initOther();

					break;
				case ConfigConst.DISTRIBUTED_STATUS:
					initStatus();
					break;
				}
			} else {
				initUndistributed();
			}
			sync();
		}else
		{
			initUndistributed();
			sync();
		}
	}


	/**
	 * 不开启分布式,开启各种组件
	 * 
	 * @param initProperties
	 */
	private void initUndistributed() {
		openServiceThread("csc");
	}

	/**
	 * CS服务器角色初始化
	 * 
	 * @param initProperties
	 */
	private void initCS() {

		// 1建立其它服务器连接

		openClientThread("group|status|other");

		// 开启一个端口用于与客户端的数据交流
		// 2开启服务端口

		openServiceThread("csc");

	}

	/**
	 * 群服务器初始化
	 * 
	 * @param initProperties
	 */
	private void initGroup() {

		// 1建立其它服务器连接

		openClientThread("status");

		// 开启两个端口，分别用于CS服务器、其它业务服务器的数据交流

		ConfigConst.CS_CHECK_CODE = initProperties.get(ConfigConst.CODE_P);
		ConfigConst.OTHER_CHECK_CODE = initProperties.get(ConfigConst.O_CODE_P);
		openServiceThread("cs|other");

	}

	/**
	 * 其它业务服务器初始化
	 * 
	 * @param initProperties
	 */
	private void initOther() {
		// 1建立其它服务器连接
		openClientThread("group|status");

		// 开启一个端口用于CS服务器的数据交流
		ConfigConst.CS_CHECK_CODE = initProperties.get(ConfigConst.CODE_P);
		openServiceThread("cs");

	}

	/**
	 * 状态服务器初始化
	 * 
	 * @param initProperties
	 */
	private void initStatus() {
		// 需开启三个端口，分别用于CS服务器、群服务器、其他业务服务器的数据交流

		ConfigConst.CS_CHECK_CODE = initProperties.get(ConfigConst.CODE_P);
		ConfigConst.OTHER_CHECK_CODE = initProperties.get(ConfigConst.O_CODE_P);
		ConfigConst.GROUP_CHECK_CODE = initProperties.get(ConfigConst.G_CODE_P);

		openServiceThread("cs|other|group");
	}

	
	
	/**
	 * 建立需要开启的连接
	 * @param serviceName需要开启的服务名称，多个用|割开，格式为group|other|status
	 */
	private void openClientThread(String serviceName)
	{
		cTServersGroup=new ThreadGroup("connect to other servers");
		String[] services = serviceName.split("\\|");
		for (String service : services) {

			// （1）群服务器连接
			if ("group".equals(service)) {
				BlockingQueue<Packet> groupQueue = new LinkedBlockingQueue<Packet>(
						2000);
				try {
					openClientThread(
							Boolean.parseBoolean(initProperties.get(ConfigConst.GROUP_SERVICE_SSL)),
							initProperties.get(ConfigConst.GROUP_SERVICE_URL),
							Integer.parseInt(initProperties.get(ConfigConst.GROUP_SERVICE_PORT)),
							new ClientHandler(
									connectMange,
									groupQueue,
									ConfigConst.DISTRIBUTED_GROUP,
									initProperties.get(ConfigConst.GROUP_SERVICE_CODE)),
							"groupClient", ConfigConst.DISTRIBUTED_GROUP);
				} catch (NumberFormatException | SSLException e2) {
					// TODO Auto-generated catch block
					log.error("与群服务器连接失败",e2);
				}
			}
			// （2）状态服务器连接
			if ("status".equals(service)) {
				BlockingQueue<Packet> statusQueue = new LinkedBlockingQueue<Packet>(
						2000);
				try {
					openClientThread(
							Boolean.parseBoolean(initProperties.get(ConfigConst.STATUS_SERVICE_SSL)),
							initProperties.get(ConfigConst.STATUS_SERVICE_URL),
							Integer.parseInt(initProperties.get(ConfigConst.STATUS_SERVICE_PORT)),
							new ClientHandler(
									connectMange,
									statusQueue,
									ConfigConst.DISTRIBUTED_STATUS,
									initProperties.get(ConfigConst.STATUS_SERVICE_CODE)),
							"statusClient",
							ConfigConst.DISTRIBUTED_STATUS);
				} catch (NumberFormatException | SSLException e2) {
					// TODO Auto-generated catch block
					log.error("与状态标识服务器连接失败",e2);
				}
			}
			// （3）其它服务器连接
			if ("other".equals(service)) {			
				BlockingQueue<Packet> otherQueue = new LinkedBlockingQueue<Packet>(
						2000);
				try {
					openClientThread(
							Boolean.parseBoolean(initProperties.get(ConfigConst.OTHER_SERVICE_SSL)),
							initProperties.get(ConfigConst.OTHER_SERVICE_URL),
							Integer.parseInt(initProperties.get(ConfigConst.OTHER_SERVICE_PORT)),
							new ClientHandler(
									connectMange,
									otherQueue,
									ConfigConst.DISTRIBUTED_OTHER,
									initProperties.get(ConfigConst.OTHER_SERVICE_CODE)),
							"otherClient",
							ConfigConst.DISTRIBUTED_OTHER);
				} catch (NumberFormatException | SSLException e2) {
					// TODO Auto-generated catch block
					log.error("与其它业务服务器连接失败",e2);
				}
			}
		}
	}
	
	
	/**
	 * 开启与其他服务器连接线程
	 * 
	 * @param ssl
	 *            是否开启ssl
	 * @param host
	 * @param port
	 * @param handler
	 * @param name
	 *            线程名称
	 * @param role
	 *            服务器角色
	 * @throws SSLException
	 */
	private void openClientThread(boolean ssl ,String host,int port,DChandlerInterface handler,String name,int role) throws SSLException
	{
		ConnectToServicesThread connectThread=new ConnectToServicesThread(ssl, host, port, handler);
		new Thread(cTServersGroup, connectThread, name).start();
		//connectThread.getChannel();

	}
	/**
	 * 关闭与其他服务器连接的线程组
	 */
	public void stopCTSGroup() {
		cTServersGroup.interrupt();
		cTServersGroup = null;
	}

	/**
	 * 开启需要打开的服务端口
	 * 
	 * @param serviceName需要开启的服务名称
	 *            ，多个用|割开，格式为cs|other|group
	 */
	private void openServiceThread(String serviceName) {
		serversGroup = new ThreadGroup("servers");
		
		String[] services = serviceName.split("\\|");
		for (String service : services) {
			// 开启cs服务器连接端口
			/**
			 * 状态服务器的端口开启：与cs服务器建立连接时需要对其cs服务器进行编号，用于对消息转发
			 * 1，与cs服务器断开连接时，需对这台服务器的所有客户端连接进行状态改变，改为离线状态（或者，根据查询的时候）
			 * 非状态服务器的端口开启：
			 */
			if ("cs".equals(service)) {
				BlockingQueue<Packet> csQueue = new LinkedBlockingQueue<Packet>(
						2000);
				openServiceThread(
						Boolean.parseBoolean(initProperties.get(ConfigConst.SSL_P)),
						Integer.parseInt(initProperties.get(ConfigConst.PROT_P)),
						new GroupServiceHandler(connectMange, csQueue),
						"csService");
			}

			// 开启群服务器连接端口
			if ("group".equals(service)) {
				BlockingQueue<Packet> groupQueue = new LinkedBlockingQueue<Packet>(
						2000);
				openServiceThread(Boolean.parseBoolean(initProperties.get(ConfigConst.G_SSL_P)),
						Integer.parseInt(initProperties
								.get(ConfigConst.G_PROT_P)),
						new GroupServiceHandler(connectMange, groupQueue),
						"groupService");
			}

			// 开启其它业务服务器连接端口
			if ("other".equals(service)) {
				BlockingQueue<Packet> otherQueue = new LinkedBlockingQueue<Packet>(
						2000);
				openServiceThread(Boolean.parseBoolean(initProperties.get(ConfigConst.O_SSL_P)),
						Integer.parseInt(initProperties
								.get(ConfigConst.O_PROT_P)),
						new OtherServiceHandler(connectMange, otherQueue),
						"otherService");
			}
			
			
			//开启与客户端连接端口
			if ("csc".equals(service)) {
				BlockingQueue<Packet> queue = new LinkedBlockingQueue<Packet>(2000);
				openServiceThread(Boolean.parseBoolean(initProperties.get(ConfigConst.SSL_P)),
						Integer.parseInt(initProperties
								.get(ConfigConst.PROT_P)),
						new ConnectServerHandler(connectMange, userManage, queue),
						"cscService");
				MessageRouteThread<Channel> messageRouteThread = new MessageRouteThread<Channel>(
						connectMange, userManage, queue);
				messageRouteThread.start();
					
			}
			
		}		
		
		CacheConnectThread<Channel> cacheConnectThread = new CacheConnectThread<Channel>(
				connectMange.getCacheConnects());
		cacheConnectThread.start();
		

	}
	/**
	 * 开启服务端口
	 * @param ssl
	 * @param port
	 * @param handler
	 * @param name
	 */
	private void openServiceThread(boolean ssl,int port,DhandlerInterface handler,String name)
	{
		ServicesThread seriveThread=new ServicesThread(ssl, port, handler);
		new Thread(serversGroup,seriveThread,name).start();
	}
	
	
	/**
	 * 关闭与其他服务器连接的线程组
	 */
	public void stopServiceGroup() {
		serversGroup.interrupt();
		serversGroup = null;
		this.unsync();
	}
	
	/**
	 * 用于线程阻塞
	 */
	private void sync()
	{
		synchronized(this)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 解除阻塞
	 */
	private void unsync()
	{
		this.notify();
	}
}
