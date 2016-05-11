package org.diqurly.config;

/**
 * log������
 * @author diqurly
 *
 */
public class Log {
	/**
	 * log�����ļ���ʼ��
	 */
	public static void init() {
		//��ȡxml��ʽ����
		org.apache.log4j.xml.DOMConfigurator.configure("config/log4j.xml");
		//��ȡproperties��ʽ����
		//PropertyConfigurator.configure( "config/log4j.properties" );
	}

	/**
	 * �����Ƿ񱣴�debug��־
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
