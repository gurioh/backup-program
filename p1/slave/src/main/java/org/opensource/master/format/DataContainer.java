package org.opensource.master.format;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataContainer implements Serializable{

	private static final long serialVersionUID = 1L;
	String slaveID;
	private List<List<String>> values = new ArrayList<>();
	
	public DataContainer() {
	}
	
	public void add(List<String> rowList){
		values.add(rowList);
	}
	
	public int getRowCount(){
		return values.size();
	}
	
	public String getSlaveID() {
		return slaveID;
	}

	public void setSlaveID(String slaveID) {
		this.slaveID = slaveID;
	}
	
	public List<List<String>> getRowList(){
		return values;
	}
}
