package org.diqurly.connect.thread;

import io.netty.channel.Channel;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;

/**
 * ���洦���߳�
 * 
 * @author diqurly
 * @param <E>
 *
 */
public class CacheConnectThread<E extends Channel> extends Thread {
	private boolean isStop = false;
	private ConcurrentHashMap<E, Long> connects;
	//���ɨ��ʱ��
	private int interval = 5000;
	//����ʱ��
	private int expiredTime=5000;
	//������ʾ��Ϣ
	private String errorMsg=null;

	public CacheConnectThread(ConcurrentHashMap<E, Long> connects) {
		this.connects = connects;
		try {
			errorMsg=new org.diqurly.packet.Error(1100).toJson();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setName("CacheConnectThread");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!isStop) {
			try {
				Thread.sleep(interval);
				if (connects.size() > 0)
					expiredRemove();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// �����ж�
				this.interrupt();
			}
		}
	}

	public void close() {
		isStop = true;
	}

	/**
	 * ����ɨ�������ȣ���λ����,Ĭ��5000���룩
	 * 
	 * @param length
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}

	/**
	 * �Ƴ��������� 
	 * 5s�ȴ�ʱ�䣬5s��δ��֤��Ͽ�����
	 * �������Ƴ�
	 */
	private void expiredRemove() {
		Set<Entry<E, Long>> entrys = connects.entrySet();
		Iterator<Entry<E, Long>> iter = entrys.iterator();
		long now = System.currentTimeMillis();
		while (iter.hasNext()) {
			Entry<E, Long> entry = iter.next();			
			if ((now - entry.getValue()) >= expiredTime) {
				E connect = entry.getKey();
				connect.writeAndFlush(errorMsg);
				connect.close();
				iter.remove();
			}

		}	
		
	}

}
