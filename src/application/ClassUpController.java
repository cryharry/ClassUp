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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
	public HBox excelHBox;
	@FXML
	public Button xlsLoadBtn, up3grade;
	@FXML
	public TableView<StudentBean> xls3Table;
	@FXML
	public TableColumn<StudentBean, Integer> class3Col, ban3Col, num3Col;
	@FXML
	public TableColumn<StudentBean, String> stid3Col, name3Col, subject3Col;
	@FXML
	public ToggleButton oldExcel, newExcel;
	@FXML
	public ToggleGroup excelGroup;
	@FXML
	public TextField xlsFileName;
	@FXML
	public ComboBox<String> sheetCombo;
	
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
	ShowMessage show;
	Boolean fileCheck = true;
	String excelSelect = "old";
	ObservableList<String> comboItem = FXCollections.observableArrayList();
	TableView<ExcelTableBean> testTable = new TableView<ExcelTableBean>();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		sql = "SELECT * FROM student WHERE class=3";
		try {
			rs = dbQue.getRS(sql);
			if(rs.next()) {
				fileCheck = false;
			}
		} catch (SQLException e) {
			show = new ShowMessage(AlertType.ERROR, "에러", e.toString());
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
					show = new ShowMessage(AlertType.INFORMATION, "백업완료", "백업완료되었습니다.");
					
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
			show = new ShowMessage(AlertType.ERROR, "에러", e.toString());
		}
		dbQue.closeDB();
	}

	public void deleteStudent(ObservableList<StudentBean> stInfo) {
		cps = new CreateProcessStage();
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
				fileCheck = true;
				setSelectTab(tab2grade);
			}
		});
	}
	
	@FXML
	public void LoadExcel(ActionEvent btn) {
		tab2VBox.getChildren().clear();
		sheetCombo.getItems().clear();
		excelName = null;
		excelPath = "";
		xlsFileName.setText("");
		xlsList.clear();
		if(tabPane.getSelectionModel().getSelectedIndex()==1) {
			setCreateUpClass(2, excelSelect);
		} else if(tabPane.getSelectionModel().getSelectedIndex()==2) {
			setCreateUpClass(1, excelSelect);
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
					try {
						int result = dbQue.updatetDBResult(sql);
						if(result==1) {
							xlsList.get(i).setResult(new SimpleStringProperty("성공"));
						} else {
							xlsList.get(i).setResult(new SimpleStringProperty("실패"));
						}
						if(select==2) {
							//xls2Table.setItems(xlsList);
						} else {
							//xls1Table.setItems(xlsList);
						}
					} catch (SQLException e) {
						show = new ShowMessage(AlertType.ERROR, "에러", e.toString());
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
				show = new ShowMessage(AlertType.INFORMATION, "완료", "2학년 진급처리가 완료되었습니다.");
				cps.hideStage();
				setSelectTab(tab1grade);
			}
		});
	}

	
	public void setCreateUpClass(int select, String excelSelect) {
		fc = new FileChooser();
		fc.setTitle("진급처리용 엑셀파일 불러오기");
		FileChooser.ExtensionFilter xlsFilter = new FileChooser.ExtensionFilter("Excel Files", "*.xls","*.xlsx");
		fc.getExtensionFilters().add(xlsFilter);
		excelName =  fc.showOpenDialog(primaryStage);
		if(excelName.getPath()!=null) {
			excelPath = excelName.getPath().replace("\\", "/");
			xlsFileName.setText(excelPath);
		
			try {
				FileInputStream fis = new FileInputStream(new File(excelPath));
				Workbook xls = WorkbookFactory.create(fis);
				int sheetSize = xls.getNumberOfSheets();
				for(int i=0; i<sheetSize; i++) {
					comboItem.add(xls.getSheetName(i));
				}
				xlsList = excelTableSet(select, xls, excelSelect);
				/*
				if(select==2) {
					jun2BanCol.setCellValueFactory(xlsList->xlsList.getValue().getJunBan().asObject());
					jun2NumCol.setCellValueFactory(xlsList->xlsList.getValue().getJunNum().asObject());
					subject2Col.setCellValueFactory(xlsList->xlsList.getValue().getSubject());
					name2Col.setCellValueFactory(xlsList->xlsList.getValue().getName());
					up2BanCol.setCellValueFactory(xlsList->xlsList.getValue().getUpBan().asObject());
					up2NumCol.setCellValueFactory(xlsList->xlsList.getValue().getUpNum().asObject());
					xls2Table.setItems(xlsList);
							
					up2grade = new Button("2학년 진급처리");
					tab2VBox.getChildren().add(up2grade);
					up2grade.setOnAction(event->{
						updateClass(xlsList, 2);
					});
				} else {
					jun1BanCol.setCellValueFactory(xlsList->xlsList.getValue().getJunBan().asObject());
					jun1NumCol.setCellValueFactory(xlsList->xlsList.getValue().getJunNum().asObject());
					subject1Col.setCellValueFactory(xlsList->xlsList.getValue().getSubject());
					name1Col.setCellValueFactory(xlsList->xlsList.getValue().getName());
					up1BanCol.setCellValueFactory(xlsList->xlsList.getValue().getUpBan().asObject());
					up1NumCol.setCellValueFactory(xlsList->xlsList.getValue().getUpNum().asObject());
					xls1Table.setItems(xlsList);
					
					up1grade = new Button("1학년 진급처리");
					tab1VBox.getChildren().add(up1grade);
					up1grade.setOnAction(event->{
						updateClass(xlsList, 1);
					});
				}
				*/
			} catch (Exception e) {
				show = new ShowMessage(AlertType.ERROR, "에러", e.toString());
			}
		}
	}
	private void getHeader(Sheet sheet) {
		ArrayList<String> data2 = new ArrayList<>();
		outer:
		for(int p=0;p<8;p++) {
			Row headrRow = sheet.getRow(p);
			if(headrRow!=null) {
				int headerCells = headrRow.getPhysicalNumberOfCells();
				for(int k=0; k<=headerCells; k++) {
					Cell headerCell = headrRow.getCell(k);{
						if(headerCell!=null) {
							for(int q=0;q<sheet.getNumMergedRegions();q++) {
								CellRangeAddress region = sheet.getMergedRegion(q);
								
								int regionCell = region.getFirstColumn();
								int regionRow = region.getFirstRow();
								
								if(regionRow == headerCell.getRowIndex() && regionRow == headerCell.getColumnIndex()) {
									//System.out.println("RegionCell : "+ regionCell + ", RegionRow : " + regionRow);
									//System.out.println("Region : "+sheet.getRow(regionRow).getCell(regionCell).getStringCellValue());
									continue outer;
								}
							}
							switch (headerCell.getCellType()) {
							case Cell.CELL_TYPE_FORMULA:
								continue;
							case Cell.CELL_TYPE_NUMERIC:
								continue;
							case Cell.CELL_TYPE_STRING:
								if(headerCell.getStringCellValue().contains("학년도")
										||headerCell.getStringCellValue().contains("이전반")
										||headerCell.getStringCellValue().contains("과")
										||headerCell.getStringCellValue().contains("2018")) {
									continue;
								} else {
									data2.add(headerCell.getStringCellValue());
									if(headerCell.getStringCellValue().contains("학년")) {
										System.out.println("Header RowNum: "+headerCell.getRowIndex());
									}
									break;
								}
							case Cell.CELL_TYPE_BLANK:
								continue;
							case Cell.CELL_TYPE_ERROR:
								continue;
							
							default:
								break;
							}
						}
					}
				}
			}
			TableColumn testCol[] = new TableColumn[data2.size()];
			for(int l=0;l<data2.size();l++) {
				if(data2.get(l).contains("진급학적")) {
					continue;
				}
				testCol[l] = new TableColumn(data2.get(l));
				testTable.getColumns().addAll(testCol[l]);
			}
		}
	}

	private ObservableList<ExcelTableBean> excelTableSet(int select, Workbook xls, String excelSelect) {
		sheetCombo.setItems(comboItem);
		sheetCombo.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				
				Sheet sheet = xls.getSheet(newValue);
				String data = "";
				tab2VBox.getChildren().removeAll();
				testTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
				testTable.getSelectionModel().setCellSelectionEnabled(true);
				testTable.addEventFilter(MouseEvent.MOUSE_PRESSED, (event)-> {
					if(event.isShortcutDown() || event.isShiftDown()) {
						event.consume();
					}
				});
				testTable.getFocusModel().focusedCellProperty().addListener((obs, odVal, newVal) -> {
					if(newVal.getTableColumn() != null) {
						testTable.getSelectionModel().selectRange(0, newVal.getTableColumn(), testTable.getItems().size(), newVal.getTableColumn());
						System.out.println("Selected TableColumn: "+ newVal.getTableColumn().getText());
						System.out.println("Selected Column Index: "+ newVal.getColumn());
					}
				});
				 
				
				//행의 수
				int rows = sheet.getPhysicalNumberOfRows();
				// 헤더가져오기
				getHeader(sheet);
				
				
				tab2VBox.getChildren().add(testTable);
				/*
				//행의 수만큼 반복
				int excelSelectIndex = 0;
				int cellSelIndex = 0;
				if(excelSelect.equals("new")) {
					excelSelectIndex = 7;
					cellSelIndex = 1;
				} else {
					excelSelectIndex = 1;
				}
					for(int rowIndex=excelSelectIndex; rowIndex<=rows; rowIndex++) {
					Row row = sheet.getRow(rowIndex);
					if(row != null) {
						//셀의 수
						int cells = row.getPhysicalNumberOfCells();
						
						//셀의 수만큼 반복
						for(int cellIndex = cellSelIndex; cellIndex<=cells; cellIndex++) {
							Cell cell = row.getCell(cellIndex);
							if(excelSelect.equals("new")) {
								if(cellIndex==7 || cellIndex==10) {
									continue;
								}
							}
							if(cell==null) {
								continue;
							} else {
								switch (cell.getCellType()) {
								case Cell.CELL_TYPE_FORMULA:
									data += cell.getCellFormula()+",";
									break;
								case Cell.CELL_TYPE_NUMERIC:
									data += String.valueOf(Math.round(cell.getNumericCellValue()))+",";
									break;
								case Cell.CELL_TYPE_STRING:
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
								case Cell.CELL_TYPE_BLANK:
									continue;
								case Cell.CELL_TYPE_ERROR:
									data += cell.getErrorCellValue()+",";
								default:
									break;
								}
								if(excelSelect.equals("new")) {
									if(cellIndex==16) {
										data+="}";
									}
								} else {
									if(cellIndex==7) {
										data+="}";
									}
								}
							}
							
						}
						//System.out.println("-------------");
					}
				}
				data = data.replace(",}", "}");
				System.out.println(data);
				String[] stData = data.split("}");
				for(int z=0;z<stData.length;z++) {
					String[] splitData = stData[z].split(",");
					xlsBean = new ExcelTableBean();
					for(int y=0; y<splitData.length; y++) {
						if(excelSelect.equals("new")) {
							setExcelBean("new", splitData, y);
						} else {
							setExcelBean("old", splitData, y);
						}
					}
					xlsList.add(xlsBean);
				}*/
			}

			private void setExcelBean(String sel, String[] splitData, int y) {
				if(sel.equals("new")) {
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
				} else {
					switch (y) {
					case 0:
						xlsBean.setJunClass(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
						break;
					case 1:
						xlsBean.setJunBan(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
						break;
					case 2:
						xlsBean.setJunNum(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
						break;
					case 3:
						xlsBean.setName(new SimpleStringProperty(splitData[y]));
						break;
					case 4:
						xlsBean.setUpClass(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
						break;
					case 5:
						xlsBean.setUpBan(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
						break;
					case 6:
						xlsBean.setUpNum(new SimpleIntegerProperty(Integer.parseInt(splitData[y])));
						break;
					case 7:
						xlsBean.setSubject(new SimpleStringProperty(splitData[y]));
						break;
					default:
						break;
					}
			
				}
			}
		});
		return xlsList;
	}

}
