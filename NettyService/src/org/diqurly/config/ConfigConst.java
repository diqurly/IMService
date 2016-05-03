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
	
	
	
	
	
	
	
	
	public static final String ISDISTRIBUTED_P="--distribution-mode";
	public static final String DISTRIBUTED_ROLE_P="--role";
	public static final String PROT_P="--c2s-port";
	public static final String GROUP_SERVICE_URL="--group-service-url";
	public static final String GROUP_SERVICE_PORT="--group-service-url";
	public static final String STATUS_SERVICE_URL="--status-service-url";
	public static final String STATUS_SERVICE_PORT="--status-service-port";
	public static final String OTHER_SERVICE_URL="--other-service-url";
	public static final String OTHER_SERVICE_PORT="--other-service-port";
	
	
	
	
	
	
	
}
