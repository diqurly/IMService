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
	 * �����ļ�����
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
	 * ��ȡ�����ļ�map
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
				// �����ֲ�ʽ
				// ��ȡ�����ɫ
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
	 * �������ֲ�ʽ,�����������
	 * 
	 * @param initProperties
	 */
	private void initUndistributed() {
		openServiceThread("csc");
	}

	/**
	 * CS��������ɫ��ʼ��
	 * 
	 * @param initProperties
	 */
	private void initCS() {

		// 1������������������

		openClientThread("group|status|other");

		// ����һ���˿�������ͻ��˵����ݽ���
		// 2��������˿�

		openServiceThread("csc");

	}

	/**
	 * Ⱥ��������ʼ��
	 * 
	 * @param initProperties
	 */
	private void initGroup() {

		// 1������������������

		openClientThread("status");

		// ���������˿ڣ��ֱ�����CS������������ҵ������������ݽ���

		ConfigConst.CS_CHECK_CODE = initProperties.get(ConfigConst.CODE_P);
		ConfigConst.OTHER_CHECK_CODE = initProperties.get(ConfigConst.O_CODE_P);
		openServiceThread("cs|other");

	}

	/**
	 * ����ҵ���������ʼ��
	 * 
	 * @param initProperties
	 */
	private void initOther() {
		// 1������������������
		openClientThread("group|status");

		// ����һ���˿�����CS�����������ݽ���
		ConfigConst.CS_CHECK_CODE = initProperties.get(ConfigConst.CODE_P);
		openServiceThread("cs");

	}

	/**
	 * ״̬��������ʼ��
	 * 
	 * @param initProperties
	 */
	private void initStatus() {
		// �迪�������˿ڣ��ֱ�����CS��������Ⱥ������������ҵ������������ݽ���

		ConfigConst.CS_CHECK_CODE = initProperties.get(ConfigConst.CODE_P);
		ConfigConst.OTHER_CHECK_CODE = initProperties.get(ConfigConst.O_CODE_P);
		ConfigConst.GROUP_CHECK_CODE = initProperties.get(ConfigConst.G_CODE_P);

		openServiceThread("cs|other|group");
	}

	
	
	/**
	 * ������Ҫ����������
	 * @param serviceName��Ҫ�����ķ������ƣ������|�����ʽΪgroup|other|status
	 */
	private void openClientThread(String serviceName)
	{
		cTServersGroup=new ThreadGroup("connect to other servers");
		String[] services = serviceName.split("\\|");
		for (String service : services) {

			// ��1��Ⱥ����������
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
					log.error("��Ⱥ����������ʧ��",e2);
				}
			}
			// ��2��״̬����������
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
					log.error("��״̬��ʶ����������ʧ��",e2);
				}
			}
			// ��3����������������
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
					log.error("������ҵ�����������ʧ��",e2);
				}
			}
		}
	}
	
	
	/**
	 * ���������������������߳�
	 * 
	 * @param ssl
	 *            �Ƿ���ssl
	 * @param host
	 * @param port
	 * @param handler
	 * @param name
	 *            �߳�����
	 * @param role
	 *            ��������ɫ
	 * @throws SSLException
	 */
	private void openClientThread(boolean ssl ,String host,int port,DChandlerInterface handler,String name,int role) throws SSLException
	{
		ConnectToServicesThread connectThread=new ConnectToServicesThread(ssl, host, port, handler);
		new Thread(cTServersGroup, connectThread, name).start();
		//connectThread.getChannel();

	}
	/**
	 * �ر����������������ӵ��߳���
	 */
	public void stopCTSGroup() {
		cTServersGroup.interrupt();
		cTServersGroup = null;
	}

	/**
	 * ������Ҫ�򿪵ķ���˿�
	 * 
	 * @param serviceName��Ҫ�����ķ�������
	 *            �������|�����ʽΪcs|other|group
	 */
	private void openServiceThread(String serviceName) {
		serversGroup = new ThreadGroup("servers");
		
		String[] services = serviceName.split("\\|");
		for (String service : services) {
			// ����cs���������Ӷ˿�
			/**
			 * ״̬�������Ķ˿ڿ�������cs��������������ʱ��Ҫ����cs���������б�ţ����ڶ���Ϣת��
			 * 1����cs�������Ͽ�����ʱ�������̨�����������пͻ������ӽ���״̬�ı䣬��Ϊ����״̬�����ߣ����ݲ�ѯ��ʱ��
			 * ��״̬�������Ķ˿ڿ�����
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

			// ����Ⱥ���������Ӷ˿�
			if ("group".equals(service)) {
				BlockingQueue<Packet> groupQueue = new LinkedBlockingQueue<Packet>(
						2000);
				openServiceThread(Boolean.parseBoolean(initProperties.get(ConfigConst.G_SSL_P)),
						Integer.parseInt(initProperties
								.get(ConfigConst.G_PROT_P)),
						new GroupServiceHandler(connectMange, groupQueue),
						"groupService");
			}

			// ��������ҵ����������Ӷ˿�
			if ("other".equals(service)) {
				BlockingQueue<Packet> otherQueue = new LinkedBlockingQueue<Packet>(
						2000);
				openServiceThread(Boolean.parseBoolean(initProperties.get(ConfigConst.O_SSL_P)),
						Integer.parseInt(initProperties
								.get(ConfigConst.O_PROT_P)),
						new OtherServiceHandler(connectMange, otherQueue),
						"otherService");
			}
			
			
			//������ͻ������Ӷ˿�
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
	 * ��������˿�
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
	 * �ر����������������ӵ��߳���
	 */
	public void stopServiceGroup() {
		serversGroup.interrupt();
		serversGroup = null;
		this.unsync();
	}
	
	/**
	 * �����߳�����
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
	 * �������
	 */
	private void unsync()
	{
		this.notify();
	}
}
