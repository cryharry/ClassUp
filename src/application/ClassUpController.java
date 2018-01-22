package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ClassUpController implements Initializable {

	List<String> list = new ArrayList<String>();
	DBQue dbQue = new DBQue();
	ResultSet rs;
	String sql, excelPath;
	FileChooser fc;
	File excelName;
	private CopyTask copyTask;
	
	@FXML
	private Stage primaryStage;
	@FXML
	private TabPane tabPane;
	@FXML
	private TextField xlsFileName;
	@FXML
	private Button xlsLoadBtn, up3grade, up2grade, up1grade;
	@FXML
	private ChoiceBox<String> sheetChoice;
	@FXML
	private TableView<StudentBean> xlsTable;
	@FXML
	private ProgressBar pBar3, pBar2, pBar1;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		databaseBak();
	}

	private void databaseBak() {
		Runtime run = Runtime.getRuntime();
		DBQue db = new DBQue();
		String dbIp = db.getDB().get(0);
		String dbName = db.getDB().get(1);
		try {
			Process pr = run.exec("cmd.exe /c sqlcmd -s "+dbIp+" -d "+dbName+" -F:\bak.sql");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		try {
			Connection con = db.dbConn();
			
			Statement stmt = con.createStatement();
			
			String sql = "BACKUP DATABASE '"+dbName+"' TO DISK 'C:/Uni_Cool/BackUp/"+dbName+"/"+sdf.format(new Date())+".bak'";
			System.out.println(sql);
			Boolean backupCheck = stmt.execute(sql);
			System.out.println(backupCheck);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*pBar3 = new ProgressBar();
		pBar3.setDisable(false);
		pBar3.setProgress(0);
		copyTask = new CopyTask();
		pBar3.progressProperty().unbind();
		pBar3.progressProperty().bind(copyTask.progressProperty());
		
		copyTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent arg0) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setContentText("백업완료");
				alert.showAndWait();
			}
			
		});
*/	}
	
}
