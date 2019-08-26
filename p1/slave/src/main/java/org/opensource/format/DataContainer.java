package org.opensource.format;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataContainer implements Serializable{

	private static final long serialVersionUID = 1L;

	private List<List<String>> values = new ArrayList<>();
	
	public DataContainer() {
	}
	
	public void add(List<String> rowList){
		values.add(rowList);
	}
	
	public int getRowCount(){
		return values.size();
	}
	
	public List<List<String>> getRowList(){
		return values;
	}
}
