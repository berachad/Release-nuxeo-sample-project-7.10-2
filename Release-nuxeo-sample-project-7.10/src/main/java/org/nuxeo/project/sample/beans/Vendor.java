package org.nuxeo.project.sample.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Vendor" )
@XmlAccessorType(XmlAccessType.FIELD)
public class Vendor {
	
	private String id;
	private String name;
	private String path;
	private String updated_at;
	
	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Vendor() {
		
	}
	
	public Vendor(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getId(){
		return id;
	}

	public void setId(String id){
		this.id = id;
	}
	
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}
}
