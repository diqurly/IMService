package org.diqurly.util;

/**
 * 内存工具
 * 
 * @author diqurly
 *
 */
public class MemoryUtil {

	private java.text.DecimalFormat df = new java.text.DecimalFormat("#.0000");
private Runtime rt = Runtime.getRuntime();
	
	
	
	/**
	 * 获取当前内存使用情况
	 */
	public String getNowMemory()
	{
		
//		System.out.println("可用的内存总量：" +byteToString( rt.freeMemory()));
//		System.out.println("当前处理器数量：" +  rt.availableProcessors());
//		System.out.println("总内存（在最小内存和最大内存中间动态变化）：" + byteToString( rt.totalMemory()));
//		System.out.println("最大内存：" + byteToString( rt.maxMemory()));
		
		
		//可用
	//	long free = rt.freeMemory();
		//已用
		long useMemory = rt.totalMemory()-rt.freeMemory();
		//总内存
		long count = rt.maxMemory();
		
		
	//	return "已用："+byteToString(useMemory)+"--总内存："+byteToString(count);
		return "已用："+useMemory+"--总内存："+byteToString(count);
		
		
		
	}
	
	
	
	
	
	/**
	 * 字节转换
	 * 
	 * @param b
	 * @return
	 */
	private String byteToString(long b) {
		float kb = b / 1024;
		if ((int) kb <= 0) {
			return df.format(b) + " byte";
		}

		float m = kb / 1024;

		if ((int) kb <= 0) {
			return df.format(kb) + " kb";

		}
		float g = m / 1024;
		if ((int) g <= 0) {
			return df.format(m) + " m";
		}
		return df.format(g) + " G";

	}
}
