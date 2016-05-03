package org.diqurly.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfiguratorAbstract {
	public static final String PROPERTY_FILENAME_PROP_KEY = "--property-file";
	public static final String PROPERTY_FILENAME_PROP_DEF = "config/init.properties";
	private static final Logger log = Logger
			.getLogger(ConfiguratorAbstract.class.getName());
	private Map<String, Object> initProperties = new LinkedHashMap<String, Object>(
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
				Object val = null;

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
	public Map<String, Object> getDefConfigParams() {
		return initProperties;
	}

	public void start() {
		if (initProperties.get(ConfigConst.ISDISTRIBUTED_P) != null) {
			if ((boolean) initProperties.get(ConfigConst.ISDISTRIBUTED_P)) {
				// 开启分布式
				// 获取自身角色
				ConfigConst.DISTRIBUTED_ROLE = (int) initProperties
						.get(ConfigConst.DISTRIBUTED_ROLE_P);
				switch (ConfigConst.DISTRIBUTED_ROLE) {
				case ConfigConst.DISTRIBUTED_CS:
					String groupUrl=(String) initProperties.get(ConfigConst.GROUP_SERVICE_URL);
					int groupPort=(int) initProperties.get(ConfigConst.GROUP_SERVICE_PORT);
					String statusUrl=(String) initProperties.get(ConfigConst.STATUS_SERVICE_URL);
					int statusPort=(int) initProperties.get(ConfigConst.STATUS_SERVICE_PORT);
					String otherUrl=(String) initProperties.get(ConfigConst.OTHER_SERVICE_URL);
					int otherPort=(int) initProperties.get(ConfigConst.OTHER_SERVICE_PORT);
					break;
				case ConfigConst.DISTRIBUTED_GROUP:
					String statusUrl=(String) initProperties.get(ConfigConst.STATUS_SERVICE_URL);
					int statusPort=(int) initProperties.get(ConfigConst.STATUS_SERVICE_PORT);

					break;
				case ConfigConst.DISTRIBUTED_OTHER:
					String groupUrl=(String) initProperties.get(ConfigConst.GROUP_SERVICE_URL);
					int groupPort=(int) initProperties.get(ConfigConst.GROUP_SERVICE_PORT);
					String statusUrl=(String) initProperties.get(ConfigConst.STATUS_SERVICE_URL);
					int statusPort=(int) initProperties.get(ConfigConst.STATUS_SERVICE_PORT);

					break;
				case ConfigConst.DISTRIBUTED_STATUS:

					break;

				}
			} else {

			}
		}
	}

}
