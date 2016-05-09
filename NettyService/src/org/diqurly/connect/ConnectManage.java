package org.diqurly.connect;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * ���ӹ��� ����Ⱥ���ֲ�ʽ����
 * 
 * @author diqurly
 * @param <E>
 *
 */
public class ConnectManage<E extends Channel> {
	// private BlockingQueue<ChannelHandlerContext> cacheConnects;
	// private ConcurrentSkipListSet<ConnectInfo<E>> cacheConnects;
	private ConcurrentHashMap<E, Long> cacheConnects;

	// ֻ���ڿ�����������Ⱥ������²���Ч��
	// �����жϸ����ӷ��͵���Ϣ�Ƿ�Ϸ�
	private ConcurrentSkipListSet<E> connects;
	// ���Ҷ�Ӧ��ɫ�ķ����������ڲ�ͬ������֮����Ϣת��
	private ConcurrentHashMap<Integer, E> services;
	// Ⱥ���������ӹ���
	private ConcurrentSkipListSet<E> groupConnects;
	// �������������ӹ���
	private ConcurrentSkipListSet<E> otherConnects;

	
	
	public ConnectManage()
	{
		init();
	}
	
	public void init() {
		// cacheConnects = new
		// ArrayBlockingQueue<ChannelHandlerContext>(capacity);
		// cacheConnects =new ConcurrentSkipListSet<ConnectInfo<E>>();
		cacheConnects = new ConcurrentHashMap<E, Long>();
		connects = new ConcurrentSkipListSet<E>();
		services = new ConcurrentHashMap<Integer, E>();
		
		groupConnects= new ConcurrentSkipListSet<E>();
		otherConnects= new ConcurrentSkipListSet<E>();
	}

	/**
	 * ��ȡ��������
	 * 
	 * @return
	 */
	public ConcurrentHashMap<E, Long> getCacheConnects() {
		return this.cacheConnects;
	}

	/**
	 * �жϸ������Ƿ�Ϸ�
	 * 
	 * @param connect
	 * @return
	 */
	public boolean isCheck(E connect) {
		return connects.contains(connect);
	}

	/**
	 * ��ȡ��ؽ�ɫ��ͨ��
	 * 
	 * @param role
	 * @return
	 */
	public E getChannel(int role) {
		return services.get(role);
	}

	/**
	 * ��ӻ������ӣ����ڶ����ӽ��г�ʱ��֤
	 * 
	 * @param connect
	 */
	public void addCacheCo(E connect) {
		cacheConnects.put(connect, System.currentTimeMillis());
	}

	/**
	 * �Ƴ���������
	 * 
	 * @param connect
	 */
	public void rmCacheCo(E connect) {
		cacheConnects.remove(connect);
	}

	/**
	 * ���ͨ��У��ķ�������
	 * 
	 * @param connect
	 */
	public void addConnect(E connect) {
		connects.add(connect);
	}

	/**
	 * �Ƴ���������
	 * 
	 * @param connect
	 */
	public void rmConnect(E connect) {
		connects.remove(connect);
		connect.close();
	}

	/**
	 * ��ӽ�ɫ ����ӳ���ϵ
	 * 
	 * @param role
	 * @param connect
	 */
	public void addChannel(int role, E connect) {
		services.put(role, connect);
	}

	/**
	 * �Ƴ���ɫ ����ӳ���ϵ
	 * 
	 * @param role
	 */
	public void rmChannel(int role) {
		services.get(role).close();
		services.remove(role);
	}

	/**
	 * ��ɫ�������Ƿ����
	 * 
	 * @param role
	 * @return ����true ������false
	 */
	public boolean existRole(int role) {
		return services.contains(role);
	}
	/**
	 * Ⱥ�����������Ƿ����
	 * @param connect
	 * @return
	 */
	public boolean existGroupConnect(E connect)
	{
	return	groupConnects.contains(connect);
	}
	/**
	 * ���ͨ����֤��Ⱥ����������
	 * @param connect
	 */
	public void addGroupConnect(E connect)
	{
		groupConnects.add(connect);
	}
	/**
	 * �Ƴ�Ⱥ����������
	 * @param connect
	 */
	public void removeGroupConnect(E connect)
	{
		groupConnects.remove(connect);
		connect.close();
	}
	/**
	 * ��ȡȺ����������
	 * @return
	 */
	public ConcurrentSkipListSet<E> getGroupConnects()
	{
		return groupConnects;
	}
	
	/**
	 * ���������������Ƿ����
	 * @param connect
	 * @return
	 */
	public boolean existOtherConnect(E connect)
	{
	return	otherConnects.contains(connect);
	}
	/**
	 * ���ͨ����֤����������������
	 * @param connect
	 */
	public void addOtherConnect(E connect)
	{
		otherConnects.add(connect);
	}
	/**
	 * �Ƴ���������������
	 * @param connect
	 */
	public void removeOtherConnect(E connect)
	{
		otherConnects.remove(connect);
		connect.close();
	}
	/**
	 * ��ȡ��������������
	 * @return
	 */
	public ConcurrentSkipListSet<E> getOtherConnects()
	{
		return otherConnects;
	}
}
