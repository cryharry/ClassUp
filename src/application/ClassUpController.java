package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ClassUpController implements Initializable {
	@FXML
	public Stage primaryStage;
	@FXML
	public TabPane tabPane;
	@FXML
	public Tab tab3grade, tab2grade, tab1grade;
	@FXML
	public TextField xlsFileName;
	@FXML
	public Button xlsLoadBtn, up3grade, up2grade, up1grade;
	@FXML
	public ChoiceBox<String> sheetChoice;
	@FXML
	public TableView<StudentBean> xls3Table, xls2Table, xls1Table;
	@FXML
	public TableColumn<StudentBean, Integer> class3Col, ban3Col, num3Col,
		jun2ClassCol, jun2BanCol, jun2NumCol, up2ClassCol, up2BanCol, up2NumCol,
		jun1ClassCol, jun1BanCol, jun1NumCol, up1ClassCol, up1BanCol, up1NumCol;
	@FXML
	public TableColumn<StudentBean, String> stid1Col, stid2Col, stid3Col, name1Col, name2Col,
		name3Col, subject1Col, subject2Col, subject3Col, result1Col, result2Col, result3Col;
	@FXML
	public ProgressBar pBar3, pBar2, pBar1;
	
	List<String> list = new ArrayList<String>();
	ObservableList<StudentBean> studentList = FXCollections.observableArrayList();
	StudentBean stBean;
	DBQue dbQue = new DBQue();
	ResultSet rs;
	String sql, excelPath;
	FileChooser fc;
	File excelName;
	Alert alert;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		File path = new File("C:/Uni_Cool");
		File[] f_list = path.listFiles();
		Boolean fileCheck = false;
		for(File file : f_list) {
			if(file.getName().endsWith(".bak")) {
				fileCheck = true;	
			}
		}
		databaseBack(fileCheck);
	}
	
	private void databaseBack(Boolean fileCheck) {
		if(fileCheck) {
			tabPane.getSelectionModel().select(tab2grade);
			setSelectTab(tab2grade);
		} else {
			Task<Void> copyTask = new Task<Void>() {

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
					BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));
					while(true) {
						String s = br.readLine();
						if(s==null)break;
						System.out.println(s);
						/*alert = new Alert(AlertType.INFORMATION);
						alert.setContentText(s);
						alert.showAndWait();*/
					}
					
					return null;
				}
				
			};
			new Thread(copyTask).start();
			tabPane.getSelectionModel().select(tab3grade);
			setSelectTab(tab3grade);
		}
	}

	private void setSelectTab(Tab selectTab) {
		if(selectTab.equals(tab3grade)) {
			tab2grade.setDisable(true);
			tab1grade.setDisable(true);
		} else if(selectTab.equals(tab2grade)) {
			tab1grade.setDisable(true);
			tab3grade.setDisable(true);
		} else {
			tab3grade.setDisable(true);
			tab2grade.setDisable(true);
		}
	}
	
	@FXML
	public void setUp3Grade() {
		sql = "SELECT st_id, class, ban, num, subject, name FROM student WHERE class=3 ORDER BY ban,num";
		try {
			rs = dbQue.getRS(sql);
			while(rs.next()) {
				stBean = new StudentBean();
				stBean.setSt_id(new SimpleStringProperty(rs.getString("st_id")));
				stBean.setSt_class(new SimpleIntegerProperty(rs.getInt("class")));
				stBean.setSt_ban(new SimpleIntegerProperty(rs.getInt("ban")));
				stBean.setSt_num(new SimpleIntegerProperty(rs.getInt("num")));
				stBean.setSt_subject(new SimpleStringProperty(rs.getString("subject")));
				stBean.setSt_name(new SimpleStringProperty(rs.getString("name")));
				studentList.add(stBean);
			}
			stid3Col.setCellValueFactory(studentList->studentList.getValue().getSt_id());
			class3Col.setCellValueFactory(studentList->studentList.getValue().getSt_class().asObject());
			ban3Col.setCellValueFactory(studentList->studentList.getValue().getSt_ban().asObject());
			num3Col.setCellValueFactory(studentList->studentList.getValue().getSt_num().asObject());
			subject3Col.setCellValueFactory(studentList->studentList.getValue().getSt_subject());
			name3Col.setCellValueFactory(studentList->studentList.getValue().getSt_name());
			result3Col.setCellValueFactory(null);
			xls3Table.setItems(studentList);
			
			File f = new File("C:\\Uni_Cool\\bak.sql");
			f.delete();
			
			deleteStudent(studentList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dbQue.closeDB();
	}

	public void deleteStudent(ObservableList<StudentBean> stInfo) {
		final double barWidth = 100.0d;
		pBar3 = new ProgressBar();
		pBar3.setProgress(0);
		pBar3.setPrefWidth(barWidth);
		Task<Void> pBar3Task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				for(int i=0;i<stInfo.size();i++) {
					if(isCancelled()) {
						break;
					}
					String st_id = stInfo.get(i).getSt_id().getValue();
					sql = "DELETE FROM MERIT WHERE st_id='"+st_id+"'";
					sql += "DELETE FROM parent_info WHERE st_id='"+st_id+"'";
					sql += "DELETE FROM Election_CAND WHERE st_id='"+st_id+"'";
					sql += "DELETE FROM Election_List WHERE st_id='"+st_id+"'";
					sql += "DELETE FROM food_info WHERE st_id='"+st_id+"'";
					sql += "DELETE FROM idcard_issue WHERE st_id='"+st_id+"'";
					sql += "DELETE FROM mem_info WHERE st_id='"+st_id+"'";
					sql += "DELETE FROM mem_week WHERE st_id='"+st_id+"'";
					sql += "DELETE FROM studend_end WHERE st_id='"+st_id+"'";
					sql += "DELETE FROM studenthistory WHERE st_id='"+st_id+"'";
					sql += "DELETE FROM studentin WHERE st_id='"+st_id+"'";
					sql += "DELETE FROM studentinout WHERE st_id='"+st_id+"'";
					sql += "DELETE FROM student WHERE st_id='"+st_id+"'";
					dbQue.deleteDB(sql);
					System.out.println(i);
					updateProgress(i+1, stInfo.size());
					//Thread.sleep(100);
				}
				return null;
			}
			
		};
		pBar3.progressProperty().unbind();
		pBar3.progressProperty().bind(pBar3Task.progressProperty());
		new Thread(pBar3Task).start();
	}
}
