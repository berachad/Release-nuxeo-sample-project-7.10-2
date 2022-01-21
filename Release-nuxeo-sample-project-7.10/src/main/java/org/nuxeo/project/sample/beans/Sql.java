package org.nuxeo.project.sample.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Sql" )
@XmlAccessorType(XmlAccessType.FIELD)
public class Sql {

	private String table;
	private String column;
	private String type;
	
	
	public Sql(String table, String column, String type) {
		super();
		this.table = table;
		this.column = column;
		this.type = type;
	}
	public Sql() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
