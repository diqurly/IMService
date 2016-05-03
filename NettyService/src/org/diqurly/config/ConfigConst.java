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
