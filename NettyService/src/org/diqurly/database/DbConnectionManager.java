package org.diqurly.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据库连接管理器
 * 
 * @author diqurly
 *
 */
public class DbConnectionManager {
	private static final Logger Log = LoggerFactory
			.getLogger(DbConnectionManager.class);
	private static ConnectionProvider connectionProvider;

	public static Connection getConnection() throws SQLException {
		if (connectionProvider == null) {
			connectionProvider = new DefaultConnectionProvider();
			return connectionProvider.getConnection();
		}
		return connectionProvider.getConnection();
	}

	public static void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (Exception e) {
				Log.error(e.getMessage(), e);
			}
		}
	}

	public static void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				Log.error(e.getMessage(), e);
			}
		}
	}

	public static void closeConnection(Statement stmt, Connection con) {
		closeStatement(stmt);
		closeConnection(con);
	}

}
