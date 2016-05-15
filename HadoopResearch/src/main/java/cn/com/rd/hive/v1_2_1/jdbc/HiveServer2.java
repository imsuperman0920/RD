package cn.com.rd.hive.v1_2_1.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class HiveServer2 {
	/**
	 * 驱动类名
	 */
	private static String DRIVERNAME = "org.apache.hive.jdbc.HiveDriver";
	
	public static void main(String[] args) throws Exception {
		// 加载驱动类
		Class.forName(DRIVERNAME);
		// JDBC 连接对象
		Connection conn = DriverManager.getConnection("jdbc:hive2://rd.hadoop14:10000/default", "hive", "hive");
		// 执行对象
		Statement statement = conn.createStatement();
		// 测试用表名
		String tableName = "rdtest";
		// 判断表是否存在，存在则 DROP
		statement.execute("drop table if exists " + tableName);
		// 创建测试表
		statement.execute("create table " + tableName + " (key int, value string)");
		// 查询当前的所有表名
		ResultSet resultSet = statement.executeQuery("show tables");
		if (resultSet.next()) {
			System.out.println(resultSet.getString(1));
		}
	}
}
