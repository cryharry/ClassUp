package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class StudentBean {
	private StringProperty st_id;
	private IntegerProperty st_class;
	private IntegerProperty st_ban;
	private IntegerProperty st_num;
	private StringProperty st_name;
	private IntegerProperty count;
	
	public IntegerProperty getCount() {
		return count;
	}
	public void setCount(IntegerProperty count) {
		this.count = count;
	}
	
	public StringProperty getSt_name() {
		return st_name;
	}
	public void setSt_name(StringProperty st_name) {
		this.st_name = st_name;
	}
	public StringProperty getSt_id() {
		return st_id;
	}
	public void setSt_id(StringProperty st_id) {
		this.st_id = st_id;
	}
	public IntegerProperty getSt_class() {
		return st_class;
	}
	public void setSt_class(IntegerProperty st_class) {
		this.st_class = st_class;
	}
	public IntegerProperty getSt_ban() {
		return st_ban;
	}
	public void setSt_ban(IntegerProperty st_ban) {
		this.st_ban = st_ban;
	}
	public IntegerProperty getSt_num() {
		return st_num;
	}
	public void setSt_num(IntegerProperty st_num) {
		this.st_num = st_num;
	}
}
