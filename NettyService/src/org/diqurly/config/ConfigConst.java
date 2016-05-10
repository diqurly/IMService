package org.diqurly.config;
/**
 * ���ó�����Ϣ
 * @author diqurly
 *
 */
public class ConfigConst {
	/**
	 * ״̬��ʶ������
	 */
	public final static int DISTRIBUTED_STATUS = 1;
	/**
	 * �ͻ��˷��������ӹ��������
	 */
	public final static int DISTRIBUTED_CS = 2;
	/**
	 * Ⱥ������
	 */
	public final static int DISTRIBUTED_GROUP = 3;
	/**
	 * ������Ϣ������
	 */
	public final static int DISTRIBUTED_OFFLINE = 4;
	/**
	 * ����ҵ�������
	 */
	public final static int DISTRIBUTED_OTHER = 5;
					
	/**
	 * �Ƿ����ֲ�ʽ
	 */
	public static boolean ISDISTRIBUTED=false;
	/**
	 * �ֲ�ʽ��ɫ
	 * �ͻ��˷��������ӹ����ɫ CS connect 2
	 * ״̬��ʶ��ɫ status identifier 1
	 * Ⱥ��ɫ group 3
	 * ������Ϣ��ɫ offline message 4
	 * ����ҵ���ɫ other business 5
	 */
	public static int DISTRIBUTED_ROLE=2;
	/**
	 * Ⱥ����������У����
	 */
	public static String GROUP_CHECK_CODE=null;
	/**
	 * ״̬����������У����
	 */
	public static String STATUS_CHECK_CODE=null;
	/**
	 * ��������������У����
	 */
	public static String OTHER_CHECK_CODE=null;
	/**
	 * CS����������У����
	 */
	public static String CS_CHECK_CODE=null;
	
	
	
	/**
	 * �����ļ���ȡ----start-------
	 */
	
	/**
	 * ��ȡ�ֲ�ʽ
	 */
	public static final String ISDISTRIBUTED_P = "--distribution-mode";
	/**
	 * ��ȡ�����ɫ
	 */
	public static final String DISTRIBUTED_ROLE_P = "--role";
	/**
	 * �����ɫ��CS������������ͻ���ͨѶ�˿ڣ���������CS���������ݽ����˿�
	 */
	public static final String PROT_P = "--c2s-port";
	/**
	 * CS����������У����
	 */
	public static final String CODE_P = "--c2s-check-code";
	/**
	 * �Ƿ���ssl
	 */
	public static final String SSL_P = "--c2s-ssl";
	/**
	 * ����ҵ����������ݽ����˿�
	 */
	public static final String O_PROT_P = "--o2s-port";
	/**
	 * ����ҵ�����������У����
	 */
	public static final String O_CODE_P = "--o2s-check-code";
	/**
	 * �Ƿ���ssl
	 */
	public static final String O_SSL_P = "--o2s-ssl";
	/**
	 * Ⱥ���������ݽ����˿�
	 */
	public static final String G_PROT_P = "--g2s-port";
	/**
	 * Ⱥ����������У����
	 */
	public static final String G_CODE_P = "--g2s-check-code";
	/**
	 * �Ƿ���ssl
	 */
	public static final String G_SSL_P = "--g2s-ssl";
	/**
	 * Ⱥ��������ַ
	 */
	public static final String GROUP_SERVICE_URL = "--group-service-url";
	/**
	 * Ⱥ�������˿�
	 */
	public static final String GROUP_SERVICE_PORT = "--group-service-port";
	/**
	 * Ⱥ����������У����
	 */
	public static final String GROUP_SERVICE_CODE = "--group-service-code";
	/**
	 * Ⱥ������ssl����
	 */
	public static final String GROUP_SERVICE_SSL = "--group-service-ssl";
	/**
	 * ״̬��������ַ
	 */
	public static final String STATUS_SERVICE_URL = "--status-service-url";
	/**
	 * ״̬�������˿�
	 */
	public static final String STATUS_SERVICE_PORT = "--status-service-port";
	/**
	 * ״̬����������У����
	 */
	public static final String STATUS_SERVICE_CODE = "--status-service-code";
	/**
	 * ״̬������ssl����
	 */
	public static final String STATUS_SERVICE_SSL = "--status-service-ssl";
	/**
	 * ����ҵ���������ַ
	 */
	public static final String OTHER_SERVICE_URL = "--other-service-url";
	/**
	 * ����ҵ��������˿�
	 */
	public static final String OTHER_SERVICE_PORT = "--other-service-port";
	/**
	 * ����ҵ������У����
	 */
	public static final String OTHER_SERVICE_CODE = "--other-service-code";
	/**
	 * ����ҵ������У����
	 */
	public static final String OTHER_SERVICE_SSL = "--other-service-ssl";
	
	
	
	/**
	 * �����ļ���ȡ----end-------
	 */
	
	
}
