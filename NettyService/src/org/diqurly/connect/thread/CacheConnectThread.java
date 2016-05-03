package org.diqurly.connect.thread;

import io.netty.channel.Channel;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存处理线程
 * 
 * @author diqurly
 * @param <E>
 *
 */
public class CacheConnectThread<E extends Channel> extends Thread {
	private boolean isStop = false;
	private ConcurrentHashMap<E, Long> connects;
	//间隔扫描时间
	private int interval = 5000;
	//过期时间
	private int expiredTime=5000;
	//错误提示信息
	private String errorMsg=null;

	public CacheConnectThread(ConcurrentHashMap<E, Long> connects) {
		this.connects = connects;
		errorMsg = new org.diqurly.packet.Error(1100).toJson();
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
				// 错误中断
				this.interrupt();
			}
		}
	}

	public void close() {
		isStop = true;
	}

	/**
	 * 设置扫描间隔长度（单位毫秒,默认5000毫秒）
	 * 
	 * @param length
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}

	/**
	 * 移除过期连接 
	 * 5s等待时间，5s内未验证则断开连接
	 * 过期则移除
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
