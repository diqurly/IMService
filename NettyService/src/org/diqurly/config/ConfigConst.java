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
	/**
	 * 群服务器连接校验码
	 */
	public static String GROUP_CHECK_CODE=null;
	/**
	 * 状态服务器连接校验码
	 */
	public static String STATUS_CHECK_CODE=null;
	/**
	 * 其它服务器连接校验码
	 */
	public static String OTHER_CHECK_CODE=null;
	/**
	 * CS服务器连接校验码
	 */
	public static String CS_CHECK_CODE=null;
	
	
	
	/**
	 * 配置文件读取----start-------
	 */
	
	/**
	 * 获取分布式
	 */
	public static final String ISDISTRIBUTED_P = "--distribution-mode";
	/**
	 * 获取自身角色
	 */
	public static final String DISTRIBUTED_ROLE_P = "--role";
	/**
	 * 如果角色是CS服务器则是与客户端通讯端口，否则将是与CS服务器数据交流端口
	 */
	public static final String PROT_P = "--c2s-port";
	/**
	 * CS服务器连接校验码
	 */
	public static final String PROT_C = "--c2s-check-code";
	/**
	 * 其它业务服务器数据交流端口
	 */
	public static final String O_PROT_P = "--o2s-port";
	/**
	 * 其它业务服务器连接校验码
	 */
	public static final String O_PROT_C = "--o2s-check-code";
	/**
	 * 群服务器数据交流端口
	 */
	public static final String G_PROT_P = "--g2s-port";
	/**
	 * 群服务器连接校验码
	 */
	public static final String G_PROT_C = "--g2s-check-code";
	/**
	 * 群服务器地址
	 */
	public static final String GROUP_SERVICE_URL = "--group-service-url";
	/**
	 * 群服务器端口
	 */
	public static final String GROUP_SERVICE_PORT = "--group-service-port";
	/**
	 * 群服务器连接校验码
	 */
	public static final String GROUP_SERVICE_CODE = "--group-service-code";
	/**
	 * 状态服务器地址
	 */
	public static final String STATUS_SERVICE_URL = "--status-service-url";
	/**
	 * 状态服务器端口
	 */
	public static final String STATUS_SERVICE_PORT = "--status-service-port";
	/**
	 * 状态服务器连接校验码
	 */
	public static final String STATUS_SERVICE_CODE = "--status-service-code";
	/**
	 * 其它业务服务器地址
	 */
	public static final String OTHER_SERVICE_URL = "--other-service-url";
	/**
	 * 其它业务服务器端口
	 */
	public static final String OTHER_SERVICE_PORT = "--other-service-port";
	/**
	 * 其它业务连接校验码
	 */
	public static final String OTHER_SERVICE_CODE = "--other-service-code";
	
	
	/**
	 * 配置文件读取----end-------
	 */
	
	
}
