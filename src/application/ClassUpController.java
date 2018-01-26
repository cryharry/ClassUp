package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ClassUpController implements Initializable {
	@FXML
	public Stage primaryStage, processStage;
	@FXML
	public TabPane tabPane;
	@FXML
	public Tab tab3grade, tab2grade, tab1grade;
	@FXML
	public VBox tab3Vbox, tab2VBox, tab1VBox;
	@FXML
	public Button xlsLoad2Btn, xlsLoad1Btn, up3grade, up2grade, up1grade;
	@FXML
	public TableView<StudentBean> xls3Table;
	@FXML
	public TableView<ExcelTableBean> xls2Table, xls1Table;
	@FXML
	public TableColumn<StudentBean, Integer> class3Col, ban3Col, num3Col;
	@FXML
	public TableColumn<StudentBean, String> stid3Col, name3Col, subject3Col;
	@FXML
	public TextField xls2FileName, xls1FileName;
	@FXML
	public ComboBox<String> sheet2Combo, sheet1Combo;
	@FXML
	public TableColumn<ExcelTableBean, Integer>	jun2ClassCol, jun2BanCol, jun2NumCol, up2ClassCol,
		up2BanCol, up2NumCol, jun1ClassCol, jun1BanCol, jun1NumCol, up1ClassCol, up1BanCol, up1NumCol;
	@FXML
	public TableColumn<ExcelTableBean, String> name1Col, name2Col, subject2Col, result2Col,
		subject1Col, result1Col;
	
	List<String> list = new ArrayList<String>();
	ObservableList<StudentBean> studentList = FXCollections.observableArrayList();
	ObservableList<ExcelTableBean> xlsList = FXCollections.observableArrayList();
	ExcelTableBean xlsBean;
	StudentBean stBean;
	DBQue dbQue = new DBQue();
	ResultSet rs;
	String sql, excelPath;
	FileChooser fc;
	File excelName;
	Alert alert;
	DBQue db = new DBQue();
	String dbIp = db.getDB().get(0);
	String dbName = db.getDB().get(1);
	Date today = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDD");
	CreateProcessStage cps;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		File path = new File("C:/Uni_Cool");
		File[] f_list = path.listFiles();
		Boolean fileCheck = false;
		for(File file : f_list) {
			if(file.getName().equals(sdf.format(today)+"-"+dbName+".bak")) {
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
			tabPane.getSelectionModel().select(tab3grade);
			tab2grade.setDisable(true);
			tab1grade.setDisable(true);
			Task<Void> copyTask = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					Runtime run = Runtime.getRuntime();
					
					File f = new File("C:\\Uni_Cool\\bak.sql");
					
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
					}
					return null;
				}
				
			};
			new Thread(copyTask).start();
			copyTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					alert = new Alert(AlertType.INFORMATION);
					alert.setContentText("백업완료");
					alert.showAndWait();
					
					File f = new File("C:\\Uni_Cool\\bak.sql");
					f.delete();
					
					setSelectTab(tab3grade);
				}
			});
			
		}
	}

	public void setSelectTab(Tab selectTab) {
		if(selectTab.equals(tab3grade)) {
			up3grade = new Button();
			up3grade.setText("졸업처리");
			tab3Vbox.getChildren().add(up3grade);
			up3grade.setOnAction(event->{
				SetUp3Grade();
			});
		} else if(selectTab.equals(tab2grade)) {
			tab2grade.setDisable(false);
			tabPane.getSelectionModel().select(tab2grade);
			tab1grade.setDisable(true);
			tab3grade.setDisable(true);
		} else {
			tab1grade.setDisable(false);
			tabPane.getSelectionModel().select(tab1grade);
			tab3grade.setDisable(true);
			tab2grade.setDisable(true);
		}
	}
	
	public void SetUp3Grade() {
		up3grade.setDisable(true);
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
			xls3Table.setItems(studentList);
			
			deleteStudent(studentList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dbQue.closeDB();
	}

	public void deleteStudent(ObservableList<StudentBean> stInfo) {
		cps.showStage();
		Task<Void> delStTask = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				for(int i=0;i<stInfo.size();i++) {
					String st_id = stInfo.get(i).getSt_id().getValue();
					String sql2 = "";
					sql2 = "SELECT name FROM sys.tables WHERE name in ('merit','parent_info','election_cand'"
							+ ",'election_list','food_info','idcard_issue','mem_info','mem_week','studend_end'"
							+ ",'studenthistory','studentin','studentinout','student')";
					rs = dbQue.getRS(sql2);
					sql = "";
					while(rs.next()) {
						sql += "DELETE FROM "+rs.getString("name")+" WHERE st_id = '"+st_id+"'";
					}
					DBQue db = new DBQue();
					//System.out.println(sql);
					db.deleteDB(sql);
					String studentInfo = stInfo.get(i).getSt_class().getValue()+"학년 "+stInfo.get(i).getSt_ban().getValue()+"반 "
							+stInfo.get(i).getSt_num().getValue()+"번 "+stInfo.get(i).getSt_name().getValue();
					xls3Table.getItems().remove(stInfo.get(i));
					updateMessage(studentInfo+" 졸업 처리 중~~");
					updateProgress(i+1, stInfo.size());
					i--;
					Thread.sleep(100);
				}
				return null;
			}
			
		};
		cps.bindProperty(delStTask);
		new Thread(delStTask).start();
		delStTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				cps.hideStage();
				setSelectTab(tab2grade);
			}
		});
	}
	
	@FXML
	public void LoadExcel(ActionEvent btn) {
		if(btn.getSource()==xlsLoad2Btn) {
			setCreateUpClass(2);
		} else if(btn.getSource()==xlsLoad1Btn) {
			setCreateUpClass(1);
		}
	}
	public void updateClass(ObservableList<ExcelTableBean> xlsList, int select) {
		cps = new CreateProcessStage();
		cps.showStage();
		Task<Void> upTask = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				for(int i=0; i<xlsList.size(); i++) {
					int junClass = xlsList.get(i).getJunClass().asObject().getValue();
					int junBan = xlsList.get(i).getJunBan().asObject().getValue();
					int junNum = xlsList.get(i).getJunNum().asObject().getValue();
					String subject = xlsList.get(i).getSubject().getValue();
					String name = xlsList.get(i).getName().getValue();
					int upClass = xlsList.get(i).getUpClass().asObject().getValue();
					int upBan = xlsList.get(i).getUpBan().asObject().getValue();
					int upNum = xlsList.get(i).getUpNum().asObject().getValue();
					sql = "UPDATE student SET CLASS="+upClass+", BAN="+upBan+", num="+upNum
							+" WHERE class="+junClass+" AND ban="+junBan+" AND num="+junNum
							+" AND subject='"+subject+"' AND name='"+name+"'";
					//System.out.println(i);
					//System.out.println(sql);
					try {
						int result = dbQue.updatetDBResult(sql);
						if(result==1) {
							xlsList.get(i).setResult(new SimpleStringProperty("성공"));
						} else {
							xlsList.get(i).setResult(new SimpleStringProperty("실패"));
						}
						if(select==2) {
							result2Col.setCellValueFactory(xlsList->xlsList.getValue().getResult());
							xls2Table.setItems(xlsList);
							xls2Table.setVisible(false);
							xls2Table.setVisible(true);
						} else {
							result1Col.setCellValueFactory(xlsList->xlsList.getValue().getResult());
							xls1Table.setItems(xlsList);
							xls1Table.setVisible(false);
							xls1Table.setVisible(true);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					updateProgress(i, xlsList.size());
					Thread.sleep(100);
				}
				return null;
			}
			
		};
		cps.bindProperty(upTask);
		new Thread(upTask).start();
		upTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				System.out.println("완료");
				cps.hideStage();
				setSelectTab(tab1grade);
			}
		});
	}
	
	public void setCreateUpClass(int select) {
		fc = new FileChooser();
		fc.setTitle("진급처리용 나이스 엑셀파일 불러오기");
		FileChooser.ExtensionFilter xlsFilter = new FileChooser.ExtensionFilter("xls file(*.xls)", "*.xls");
		fc.getExtensionFilters().add(xlsFilter);
		excelName =  fc.showOpenDialog(primaryStage);
		excelPath = excelName.getPath().replace("\\", "/");
		if(select==2) {
			xls2FileName.setText(excelPath);
		} else {
			xls1FileName.setText(excelPath);
		}
		
		try {
			FileInputStream fis = new FileInputStream(excelPath);
			HSSFWorkbook xls = new HSSFWorkbook(fis);
			int sheetSize = xls.getNumberOfSheets();
			ObservableList<String> combo2Item = FXCollections.observableArrayList();
			for(int i=0; i<sheetSize; i++) {
				combo2Item.add(xls.getSheetName(i));
			}
			if(select==2) {
				sheet2Combo.setItems(combo2Item);
				sheet2Combo.valueProperty().addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
						
						HSSFSheet sheet = xls.getSheet(newValue);
						String data = "";
						//행의 수
						int rows = sheet.getPhysicalNumberOfRows();
						//행의 수만큼 반복
						for(int rowIndex=7; rowIndex<=rows; rowIndex++) {
							HSSFRow row = sheet.getRow(rowIndex);
							if(row != null) {
								//셀의 수
								int cells = row.getPhysicalNumberOfCells();
								//셀의 수만큼 반복
								for(int cellIndex = 1; cellIndex<=cells; cellIndex++) {
									HSSFCell cell = row.getCell(cellIndex);
									if(cellIndex==7 || cellIndex==10) {
										continue;
									}
									if(cell==null) {
										continue;
									} else {
										switch (cell.getCellType()) {
										case HSSFCell.CELL_TYPE_FORMULA:
											data += cell.getCellFormula()+",";
											break;
										case HSSFCell.CELL_TYPE_NUMERIC:
											data += String.valueOf(Math.round(cell.getNumericCellValue()))+",";
											break;
										case HSSFCell.CELL_TYPE_STRING:
											if(cell.getStringCellValue().equals("-")
													||cell.getStringCellValue().contains("진급반")
													||cell.getStringCellValue().contains("학년도")
													||cell.getStringCellValue().contains("학년")
													||cell.getStringCellValue().contains("학과")
													||cell.getStringCellValue().contains("반")
													||cell.getStringCellValue().contains("번호")
													||cell.getStringCellValue().contains("성명")
													||cell.getStringCellValue().contains("기준성적")
													||cell.getStringCellValue().contains("이전학적")) {
												continue;
											}
											data += cell.getStringCellValue()+",";
											break;
										case HSSFCell.CELL_TYPE_BLANK:
											continue;
										case HSSFCell.CELL_TYPE_ERROR:
											data += cell.getErrorCellValue()+",";
										default:
											break;
										}
										if(cellIndex==16) {
											data+="}";
										}
									}
									
								}
								//System.out.println("-------------");
							}
						}
						data = data.replace(",}", "}");
						String[] stData = data.split("}");
						for(int z=0;z<stData.length;z++) {
							String[] splitData = stData[z].split(",");
							xlsBean = new ExcelTableBean();
							for(int y=0; y<splitData.length; y++) {
								
								switch (y) {
								case 0:
									xlsBean.setUpClass(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
									break;
								case 1:
									xlsBean.setSubject(new SimpleStringProperty(splitData[y]));
									break;
								case 2:
									xlsBean.setUpBan(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
									break;
								case 3:
									xlsBean.setUpNum(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
									break;
								case 4:
									xlsBean.setName(new SimpleStringProperty(splitData[y]));
									break;
								case 5:
									xlsBean.setJunClass(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
									break;
								case 7:
									xlsBean.setJunBan(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
									break;
								case 8:
									xlsBean.setJunNum(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
									break;
								default:
									break;
								}
							}
							xlsList.add(xlsBean);
						}
						jun2ClassCol.setCellValueFactory(xlsList->xlsList.getValue().getJunClass().asObject());
						jun2BanCol.setCellValueFactory(xlsList->xlsList.getValue().getJunBan().asObject());
						jun2NumCol.setCellValueFactory(xlsList->xlsList.getValue().getJunNum().asObject());
						subject2Col.setCellValueFactory(xlsList->xlsList.getValue().getSubject());
						name2Col.setCellValueFactory(xlsList->xlsList.getValue().getName());
						up2ClassCol.setCellValueFactory(xlsList->xlsList.getValue().getUpClass().asObject());
						up2BanCol.setCellValueFactory(xlsList->xlsList.getValue().getUpBan().asObject());
						up2NumCol.setCellValueFactory(xlsList->xlsList.getValue().getUpNum().asObject());
						xls2Table.setItems(xlsList);
						
						up2grade = new Button("2학년 진급처리");
						tab2VBox.getChildren().add(up2grade);
						up2grade.setOnAction(event->{
							updateClass(xlsList, 2);
						});
					}
				});
			} else {
				sheet1Combo.setItems(combo2Item);
				sheet1Combo.valueProperty().addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
						
						HSSFSheet sheet = xls.getSheet(newValue);
						String data = "";
						//행의 수
						int rows = sheet.getPhysicalNumberOfRows();
						//행의 수만큼 반복
						for(int rowIndex=0; rowIndex<=rows; rowIndex++) {
							HSSFRow row = sheet.getRow(rowIndex);
							if(row != null) {
								//셀의 수
								int cells = row.getPhysicalNumberOfCells();
								//셀의 수만큼 반복
								for(int cellIndex = 0; cellIndex<=cells; cellIndex++) {
									HSSFCell cell = row.getCell(cellIndex);
									if(cellIndex==7 || cellIndex==10) {
										continue;
									}
									if(cell==null) {
										continue;
									} else {
										switch (cell.getCellType()) {
										case HSSFCell.CELL_TYPE_FORMULA:
											data += cell.getCellFormula()+",";
											break;
										case HSSFCell.CELL_TYPE_NUMERIC:
											data += String.valueOf(Math.round(cell.getNumericCellValue()))+",";
											break;
										case HSSFCell.CELL_TYPE_STRING:
											if(cell.getStringCellValue().equals("-")
													||cell.getStringCellValue().contains("진급반")
													||cell.getStringCellValue().contains("학년도")
													||cell.getStringCellValue().contains("학년")
													||cell.getStringCellValue().contains("학과")
													||cell.getStringCellValue().contains("반")
													||cell.getStringCellValue().contains("번호")
													||cell.getStringCellValue().contains("성명")
													||cell.getStringCellValue().contains("기준성적")
													||cell.getStringCellValue().contains("이전학적")) {
												continue;
											}
											data += cell.getStringCellValue()+",";
											break;
										case HSSFCell.CELL_TYPE_BLANK:
											continue;
										case HSSFCell.CELL_TYPE_ERROR:
											data += cell.getErrorCellValue()+",";
										default:
											break;
										}
										if(cellIndex==16) {
											data+="}";
										}
									}
									
								}
								//System.out.println("-------------");
							}
						}
						data = data.replace(",}", "}");
						String[] stData = data.split("}");
						for(int z=0;z<stData.length;z++) {
							String[] splitData = stData[z].split(",");
							xlsBean = new ExcelTableBean();
							for(int y=0; y<splitData.length; y++) {
								
								switch (y) {
								case 0:
									xlsBean.setUpClass(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
									break;
								case 1:
									xlsBean.setSubject(new SimpleStringProperty(splitData[y]));
									break;
								case 2:
									xlsBean.setUpBan(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
									break;
								case 3:
									xlsBean.setUpNum(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
									break;
								case 4:
									xlsBean.setName(new SimpleStringProperty(splitData[y]));
									break;
								case 5:
									xlsBean.setJunClass(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
									break;
								case 7:
									xlsBean.setJunBan(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
									break;
								case 8:
									xlsBean.setJunNum(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
									break;
								default:
									break;
								}
							}
							xlsList.add(xlsBean);
						}
						jun1ClassCol.setCellValueFactory(xlsList->xlsList.getValue().getJunClass().asObject());
						jun1BanCol.setCellValueFactory(xlsList->xlsList.getValue().getJunBan().asObject());
						jun1NumCol.setCellValueFactory(xlsList->xlsList.getValue().getJunNum().asObject());
						subject1Col.setCellValueFactory(xlsList->xlsList.getValue().getSubject());
						name1Col.setCellValueFactory(xlsList->xlsList.getValue().getName());
						up1ClassCol.setCellValueFactory(xlsList->xlsList.getValue().getUpClass().asObject());
						up1BanCol.setCellValueFactory(xlsList->xlsList.getValue().getUpBan().asObject());
						up1NumCol.setCellValueFactory(xlsList->xlsList.getValue().getUpNum().asObject());
						xls1Table.setItems(xlsList);
						
						up1grade = new Button("1학년 진급처리");
						tab1VBox.getChildren().add(up1grade);
						up1grade.setOnAction(event->{
							updateClass(xlsList, 1);
						});
					}
				});
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
