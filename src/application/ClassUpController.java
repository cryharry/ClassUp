package application;

import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ClassUpController implements Initializable {

	List<String> list = new ArrayList<String>();
	DBQue dbQue = new DBQue();
	ResultSet rs;
	String sql, excelPath;
	FileChooser fc;
	File excelName;
	
	@FXML
	private Stage primaryStage;
	@FXML
	private TextField xlsFileName;
	@FXML
	private Button xlsLoadBtn;
	@FXML
	private ChoiceBox<String> sheetChoice;
	@FXML
	private ToggleButton up3grade, up2grade, up1grade;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
}
