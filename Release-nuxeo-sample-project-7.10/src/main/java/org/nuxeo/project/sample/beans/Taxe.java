package org.nuxeo.project.sample.beans;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Taxe" )
@XmlAccessorType(XmlAccessType.FIELD)
public class Taxe {

	private String id;
	private String name;
	private double percentage;
	private String agency;
	private boolean combined;
	private boolean visible;
	private String path;
	private String Code;
	private String updated_at;

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public Taxe() {
		
	}
	
	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Taxe(String id, String name, double percentage, String agency, boolean combined, boolean visible, String path,String Code) {
		super();
		this.id = id;
		this.name = name;
		this.percentage = percentage;
		this.agency = agency;
		this.combined = combined;
		this.visible = visible;
		this.path = path;
		this.Code = Code;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		return "Taxe [id=" + id + ", name=" + name + ", percentage=" + percentage + ", agency=" + agency + ", combined="
				+ combined + ", visible=" + visible + ", path=" + path + ", Code=" + Code + "]";
	}

	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	public String getAgency() {
		return agency;
	}
	public void setAgency(String agency) {
		this.agency = agency;
	}
	public boolean isCombined() {
		return combined;
	}
	public void setCombined(boolean combined) {
		this.combined = combined;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
}
