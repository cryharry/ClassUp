package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

public class CopyTask extends Task<Void> {
	DBQue dbQue = new DBQue();
	Alert alert;
	
	@Override
	protected Void call() throws Exception {
		Runtime run = Runtime.getRuntime();
		DBQue db = new DBQue();
		String dbIp = db.getDB().get(0);
		String dbName = db.getDB().get(1);
		File f = new File("C:\\Uni_Cool\\bak.sql");
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDD");
		BufferedWriter out = new BufferedWriter(new FileWriter(f));
		out.write("BACKUP DATABASE ["+dbName+"] TO DISK='C:\\Uni_Cool\\"+sdf.format(today)+"-"+dbName+".bak'");
		out.flush();
		out.close();
				
		Process pr = run.exec("cmd.exe /c sqlcmd -s "+dbIp+":1433 -d "+dbName+" -i \"C:\\Uni_Cool\\bak.sql\"");
		return null;
	} 
}
