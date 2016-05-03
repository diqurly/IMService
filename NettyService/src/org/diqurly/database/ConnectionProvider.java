package org.diqurly.database;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProvider {

	public boolean isPooled();

	public Connection getConnection() throws SQLException;

	public void start() throws PropertyVetoException;

	public void restart() throws PropertyVetoException;

	public void destroy();
}
