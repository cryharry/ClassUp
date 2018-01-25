package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class ExcelTableBean {
	private StringProperty st_id;
	private IntegerProperty junClass;
	private IntegerProperty junBan;
	private IntegerProperty junNum;
	private StringProperty subject;
	private StringProperty name;
	private IntegerProperty upClass;
	private IntegerProperty upBan;
	private IntegerProperty upNum;
	private StringProperty result;
	
	public StringProperty getSt_id() {
		return st_id;
	}
	public void setSt_id(StringProperty st_id) {
		this.st_id = st_id;
	}
	public IntegerProperty getJunClass() {
		return junClass;
	}
	public void setJunClass(IntegerProperty junClass) {
		this.junClass = junClass;
	}
	public IntegerProperty getJunBan() {
		return junBan;
	}
	public void setJunBan(IntegerProperty junBan) {
		this.junBan = junBan;
	}
	public IntegerProperty getJunNum() {
		return junNum;
	}
	public void setJunNum(IntegerProperty junNum) {
		this.junNum = junNum;
	}
	public StringProperty getSubject() {
		return subject;
	}
	public void setSubject(StringProperty subject) {
		this.subject = subject;
	}
	public StringProperty getName() {
		return name;
	}
	public void setName(StringProperty name) {
		this.name = name;
	}
	public IntegerProperty getUpClass() {
		return upClass;
	}
	public void setUpClass(IntegerProperty upClass) {
		this.upClass = upClass;
	}
	public IntegerProperty getUpBan() {
		return upBan;
	}
	public void setUpBan(IntegerProperty upBan) {
		this.upBan = upBan;
	}
	public IntegerProperty getUpNum() {
		return upNum;
	}
	public void setUpNum(IntegerProperty upNum) {
		this.upNum = upNum;
	}
	public StringProperty getResult() {
		return result;
	}
	public void setResult(StringProperty result) {
		this.result = result;
	}
	
	
}
