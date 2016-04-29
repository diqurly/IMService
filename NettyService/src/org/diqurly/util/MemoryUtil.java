package org.diqurly.util;

/**
 * �ڴ湤��
 * 
 * @author diqurly
 *
 */
public class MemoryUtil {

	private java.text.DecimalFormat df = new java.text.DecimalFormat("#.0000");
private Runtime rt = Runtime.getRuntime();
	
	
	
	/**
	 * ��ȡ��ǰ�ڴ�ʹ�����
	 */
	public String getNowMemory()
	{
		
//		System.out.println("���õ��ڴ�������" +byteToString( rt.freeMemory()));
//		System.out.println("��ǰ������������" +  rt.availableProcessors());
//		System.out.println("���ڴ棨����С�ڴ������ڴ��м䶯̬�仯����" + byteToString( rt.totalMemory()));
//		System.out.println("����ڴ棺" + byteToString( rt.maxMemory()));
		
		
		//����
	//	long free = rt.freeMemory();
		//����
		long useMemory = rt.totalMemory()-rt.freeMemory();
		//���ڴ�
		long count = rt.maxMemory();
		
		
	//	return "���ã�"+byteToString(useMemory)+"--���ڴ棺"+byteToString(count);
		return "���ã�"+useMemory+"--���ڴ棺"+byteToString(count);
		
		
		
	}
	
	
	
	
	
	/**
	 * �ֽ�ת��
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
