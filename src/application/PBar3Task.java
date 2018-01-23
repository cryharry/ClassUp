package application;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;

import javafx.concurrent.Task;

public class CopyTask extends Task<Boolean> {
	DBQue db = new DBQue();
	String dbName = db.getDB().get(1);
	Date backupDate = new Date();
	
	@Override
	protected Boolean call() throws Exception {
		Connection con = db.con;
		Statement stmt = con.createStatement();
		String sql = "BACKUP DATABASE "+dbName+" TO DISK 'C:/Uni_Cool/BackUp/"+backupDate+".bak'";
		Boolean backupCheck = stmt.execute(sql);
		updateProgress(getWorkDone(), getProgress());
		return backupCheck;
	}
	
}
