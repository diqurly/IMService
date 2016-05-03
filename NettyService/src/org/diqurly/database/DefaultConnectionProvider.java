package org.diqurly.database;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.diqurly.service.DService;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DefaultConnectionProvider implements ConnectionProvider {
	private static final Logger log = Logger
			.getLogger(DefaultConnectionProvider.class.getName());
	public static final String USER_DB_TYPE="--user-db";
	public static final String USER_DB_URI="--user-db-uri";
	
	public static final String USER_DB_MINPOOLSIZE="--user-db-minpoolsize";
	public static final String USER_DB_MAXPOOLSIZE="--user-db-maxpoolsize";
	public static final String USER_DB_ACQUIREINCREMENT="--user-db-acquireincrement";
	
	private ComboPooledDataSource cpds;
	private String driverClass;
	private String jdbcUrl;
	private int minPoolSize = 5;
	private int maxPoolSize = 25;
	private int acquireIncrement = 5;

	
	public DefaultConnectionProvider()
	{
		loadProperties(DService.getConfigurator().getDefConfigParams());
	}
	
	@Override
	public boolean isPooled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		return cpds.getConnection();
	}

	@Override
	public void start() throws PropertyVetoException {
		// TODO Auto-generated method stub
		if (cpds == null) {
			cpds = new ComboPooledDataSource();

			cpds.setDriverClass(driverClass);
			cpds.setJdbcUrl(jdbcUrl);
			cpds.setMinPoolSize(minPoolSize);
			cpds.setAcquireIncrement(acquireIncrement);
			cpds.setMaxPoolSize(maxPoolSize);

			// cpds.setDriverClass("com.mysql.jdbc.Driver"); // loads the jdbc
			// driver
			// cpds.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/tigasedb?user=tigase&password=tigase12");

			// the settings below are optional -- c3p0 can work with defaults
			// cpds.setMinPoolSize(5);
			// cpds.setAcquireIncrement(5);
			// cpds.setMaxPoolSize(20);
		}
	}

	@Override
	public void restart() throws PropertyVetoException {
		// TODO Auto-generated method stub
		cpds = null;
		start();

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		cpds.close();
		cpds = null;
	}

	/**
	 * 加载数据库配置文件
	 */
	private void loadProperties(Map<String, Object> initProperties) {
		try {
			if("mysql".equalsIgnoreCase((String) initProperties.get(USER_DB_TYPE)))
			{
				driverClass="com.mysql.jdbc.Driver";
			}else
			{
				
			}
			if(initProperties.get(USER_DB_URI)!=null)
			{
				jdbcUrl=(String) initProperties.get(USER_DB_URI);
			}else
			{
				log.log(Level.CONFIG,"user db uri is null");
				System.exit(0);
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.log(Level.CONFIG,"user db type is null:"+e.getMessage());
			System.exit(1);
		}
		try {
			if(initProperties.get(USER_DB_MINPOOLSIZE)!=null)
			{
				minPoolSize=(int) initProperties.get(USER_DB_MINPOOLSIZE);
			}
			if(initProperties.get(USER_DB_MAXPOOLSIZE)!=null)
			{
				maxPoolSize=(int) initProperties.get(USER_DB_MAXPOOLSIZE);
			}
			if(initProperties.get(USER_DB_ACQUIREINCREMENT)!=null)
			{
				acquireIncrement=(int) initProperties.get(USER_DB_ACQUIREINCREMENT);
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.log(Level.INFO,"user db type is null:"+e.getMessage());
		}
		
	}

	/**
	 * 更改数据库驱动
	 * 
	 * @param driverClass
	 */
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

}
