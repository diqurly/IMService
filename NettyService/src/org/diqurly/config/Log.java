package org.diqurly.config;

/**
 * log操作类
 * @author diqurly
 *
 */
public class Log {
	/**
	 * log配置文件初始化
	 */
	public static void init() {
		//读取xml格式配置
		org.apache.log4j.xml.DOMConfigurator.configure("config/log4j.xml");
		//读取properties格式配置
		//PropertyConfigurator.configure( "config/log4j.properties" );
	}

	/**
	 * 设置是否保存debug日志
	 * 
	 * @param enabled
	 */
	public static void setDebugEnabled(boolean enabled) {
		final org.apache.log4j.Level newLevel;
		if (enabled) {
			newLevel = org.apache.log4j.Level.ALL;
		} else {
			newLevel = org.apache.log4j.Level.INFO;
		}
		org.apache.log4j.LogManager.getRootLogger().setLevel(newLevel);
	}

}
