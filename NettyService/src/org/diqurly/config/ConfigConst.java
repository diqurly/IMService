package org.diqurly.config;
/**
 * 配置常量信息
 * @author diqurly
 *
 */
public class ConfigConst {
	/**
	 * 状态标识服务器
	 */
	public final static int DISTRIBUTED_STATUS = 1;
	/**
	 * 客户端服务器连接管理服务器
	 */
	public final static int DISTRIBUTED_CS = 2;
	/**
	 * 群服务器
	 */
	public final static int DISTRIBUTED_GROUP = 3;
	/**
	 * 离线消息服务器
	 */
	public final static int DISTRIBUTED_OFFLINE = 4;
	/**
	 * 其它业务服务器
	 */
	public final static int DISTRIBUTED_OTHER = 5;
					
	/**
	 * 是否开启分布式
	 */
	public static boolean ISDISTRIBUTED=false;
	/**
	 * 分布式角色
	 * 客户端服务器连接管理角色 CS connect 2
	 * 状态标识角色 status identifier 1
	 * 群角色 group 3
	 * 离线消息角色 offline message 4
	 * 其它业务角色 other business 5
	 */
	public static int DISTRIBUTED_ROLE=2;
	
	
}
