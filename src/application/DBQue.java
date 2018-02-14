package application;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Alert.AlertType;

public class DBQue {
	Connection con = null;
	PreparedStatement pstmt;
	ResultSet rs;
	ShowMessage show;
	
	public List<String> getDB() {
		List<String> list =  new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("C:/Uni_Cool/KCM_IP.DAT"));
			String ip = br.readLine();
			String license = br.readLine();
			list.add(ip);
			list.add(license);
			br.close();
		} catch (Exception e) {
			show = new ShowMessage(AlertType.ERROR, "에러", e.toString());
		}
		return list;
	}
	public Connection dbConn() {
			List<String> list = getDB();
			String DBurl = "jdbc:sqlserver://"+list.get(0)+":1433;databaseName="+list.get(1);
			String user = "sa";
			String pwd = "unicool";
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				con = DriverManager.getConnection(DBurl, user, pwd);
			} catch (Exception e) {
				show = new ShowMessage(AlertType.ERROR, "에러", e.toString());
			}
			return con;
	}
	public ResultSet getRS(String sql) throws SQLException {
		con = dbConn();
		pstmt = con.prepareStatement(sql);
		rs = pstmt.executeQuery();
		return rs;
	}
	
	public void updatetDB(String sql) throws SQLException {
		con = dbConn();
		pstmt = con.prepareStatement(sql);
		pstmt.executeUpdate();
		closeDB();
	}
	public int updatetDBResult(String sql) throws SQLException {
		con = dbConn();
		pstmt = con.prepareStatement(sql);
		int s = pstmt.executeUpdate();
		return s;
	}
	
	public void insertDB(String sql) throws SQLException {
		con = dbConn();
		pstmt = con.prepareStatement(sql);
		pstmt.executeUpdate();
		closeDB();
	}
	
	public void deleteDB(String sql) throws SQLException{
		con = dbConn();
		pstmt = con.prepareStatement(sql);
		pstmt.executeUpdate();
		closeDB();
	}
	
	public void closeDB() {
		if(rs!=null) {try {rs.close();}catch(SQLException e){show = new ShowMessage(AlertType.ERROR, "에러", e.toString());}}
		if(pstmt!=null) {try {pstmt.close();}catch(SQLException e){show = new ShowMessage(AlertType.ERROR, "에러", e.toString());}}
		if(con!=null) {try {con.close();}catch(SQLException e){show = new ShowMessage(AlertType.ERROR, "에러", e.toString());}}
	}
}
