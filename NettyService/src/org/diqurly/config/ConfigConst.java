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
	
	
}
