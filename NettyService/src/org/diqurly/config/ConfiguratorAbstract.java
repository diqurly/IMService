package org.diqurly.config;

import io.netty.channel.Channel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLException;

import org.diqurly.connect.ConnectManage;
import org.diqurly.connect.ConnectServerHandler;
import org.diqurly.connect.thread.CacheConnectThread;
import org.diqurly.packet.Packet;
import org.diqurly.route.MessageRouteThread;
import org.diqurly.service.ServerBC;
import org.diqurly.user.UserManage;

public class ConfiguratorAbstract {
	public static final String PROPERTY_FILENAME_PROP_KEY = "--property-file";
	public static final String PROPERTY_FILENAME_PROP_DEF = "config/init.properties";
	private static final Logger log = Logger
			.getLogger(ConfiguratorAbstract.class.getName());
	private Map<String, String> initProperties = new LinkedHashMap<String, String>(
			100);

	public void init(String[] args) {
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
					log.log(Level.CONFIG, "Setting defaults: {0} = {1}",
							new Object[] { key, val.toString() });
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
					log.log(Level.WARNING,
							"Provided property file {0} does NOT EXISTS! Using default one {1}",
							new String[] { f.getAbsolutePath(),
									PROPERTY_FILENAME_PROP_DEF });
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
							log.log(Level.CONFIG,
									"Added default config parameter: ({0}={1})",
									new Object[] { key, value });
						}
					}

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.log(Level.WARNING,
							"Given property file was not found: {0}",
							property_filename);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.log(Level.WARNING, "Can not read property file: "
							+ property_filename, e);
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
				// 开启分布式
				// 获取自身角色
				ConfigConst.DISTRIBUTED_ROLE = Integer.parseInt(initProperties
						.get(ConfigConst.DISTRIBUTED_ROLE_P));
				switch (ConfigConst.DISTRIBUTED_ROLE) {
				case ConfigConst.DISTRIBUTED_CS:
					initCS(initProperties);
					break;
				case ConfigConst.DISTRIBUTED_GROUP:
					initGroup(initProperties);
					break;
				case ConfigConst.DISTRIBUTED_OTHER:
					initOther(initProperties);

					break;
				case ConfigConst.DISTRIBUTED_STATUS:
					initStatus(initProperties);
					break;
				}
			} else {
				initUndistributed(initProperties);
			}
		}
	}

	/**
	 * 不开启分布式
	 */
	private void initUndistributed(Map<String, String> initProperties) {
		int port = Integer.parseInt(initProperties.get(ConfigConst.PROT_P));

		ConnectManage<Channel> connectMange = new ConnectManage<Channel>();

		UserManage<Channel> userManage = new UserManage<Channel>();
		BlockingQueue<Packet> queue = new LinkedBlockingQueue<Packet>(2000);

		CacheConnectThread<Channel> cacheConnectThread = new CacheConnectThread<Channel>(
				connectMange.getCacheConnects());
		MessageRouteThread<Channel> messageRouteThread = new MessageRouteThread<Channel>(
				connectMange, userManage, queue);
		try {
			ServerBC aa = new ServerBC(port, new ConnectServerHandler(
					connectMange, userManage, queue));
			cacheConnectThread.start();
			messageRouteThread.start();
			aa.start();
		} catch (CertificateException | SSLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * CS服务器角色初始化
	 * 
	 * @param initProperties
	 */
	private void initCS(Map<String, String> initProperties) {
		// 开启一个端口用于与客户端的数据交流

		int port = Integer.parseInt(initProperties.get(ConfigConst.PROT_P));

		String groupUrl = initProperties.get(ConfigConst.GROUP_SERVICE_URL);
		int groupPort = Integer.parseInt(initProperties
				.get(ConfigConst.GROUP_SERVICE_PORT));
		String groupCode=initProperties
				.get(ConfigConst.GROUP_SERVICE_CODE);
		String statusUrl = initProperties.get(ConfigConst.STATUS_SERVICE_URL);
		int statusPort = Integer.parseInt(initProperties
				.get(ConfigConst.STATUS_SERVICE_PORT));
		String statusCode=initProperties
				.get(ConfigConst.STATUS_SERVICE_CODE);
		String otherUrl = initProperties.get(ConfigConst.OTHER_SERVICE_URL);
		int otherPort = Integer.parseInt(initProperties
				.get(ConfigConst.OTHER_SERVICE_PORT));
		String otherCode=initProperties
				.get(ConfigConst.OTHER_SERVICE_CODE);
	}

	/**
	 * 群服务器初始化
	 * 
	 * @param initProperties
	 */
	private void initGroup(Map<String, String> initProperties) {
		// 开启两个端口，分别用于CS服务器、其它业务服务器的数据交流

		int port = Integer.parseInt(initProperties.get(ConfigConst.PROT_P));
		ConfigConst.CS_CHECK_CODE = initProperties.get(ConfigConst.PROT_C);
		int portO = Integer.parseInt(initProperties.get(ConfigConst.O_PROT_P));
		ConfigConst.OTHER_CHECK_CODE = initProperties.get(ConfigConst.O_PROT_C);

		String statusUrl = initProperties.get(ConfigConst.STATUS_SERVICE_URL);
		int statusPort = Integer.parseInt(initProperties
				.get(ConfigConst.STATUS_SERVICE_PORT));

	}

	/**
	 * 其它业务服务器初始化
	 * 
	 * @param initProperties
	 */
	private void initOther(Map<String, String> initProperties) {
		// 开启一个端口用于CS服务器的数据交流

		int port = Integer.parseInt(initProperties.get(ConfigConst.PROT_P));

		String groupUrl = initProperties.get(ConfigConst.GROUP_SERVICE_URL);
		int groupPort = Integer.parseInt(initProperties
				.get(ConfigConst.GROUP_SERVICE_PORT));
		String statusUrl = initProperties.get(ConfigConst.STATUS_SERVICE_URL);
		int statusPort = Integer.parseInt(initProperties
				.get(ConfigConst.STATUS_SERVICE_PORT));

	}

	/**
	 * 状态服务器初始化
	 * 
	 * @param initProperties
	 */
	private void initStatus(Map<String, String> initProperties) {
		// 需开启三个端口，分别用于CS服务器、群服务器、其他业务服务器的数据交流

		int port = Integer.parseInt(initProperties.get(ConfigConst.PROT_P));
		ConfigConst.CS_CHECK_CODE = initProperties.get(ConfigConst.PROT_C);
		int portO = Integer.parseInt(initProperties.get(ConfigConst.O_PROT_P));
		ConfigConst.OTHER_CHECK_CODE = initProperties.get(ConfigConst.O_PROT_C);
		int portG = Integer.parseInt(initProperties.get(ConfigConst.G_PROT_P));
		ConfigConst.GROUP_CHECK_CODE = initProperties.get(ConfigConst.G_PROT_C);
	}

}
