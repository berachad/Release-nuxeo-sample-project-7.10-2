package org.nuxeo.project.sample.beans;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LedgerAccounts" )
@XmlAccessorType(XmlAccessType.FIELD)
public class LedgerAccounts {
	private String nominal_code;
	private String name;
	private String category;
	private String category_group;
	private String display_name;
	private boolean visible_in_banking;
	private boolean included_in_chart;
	private String id;
	private String path;
	private String updated_at;
	public String getUpdated_at() {
		return updated_at;
	}
	@Override
	public String toString() {
		return "LedgerAccounts [nominal_code=" + nominal_code + ", name=" + name + ", category=" + category
				+ ", category_group=" + category_group + ", display_name=" + display_name + ", visible_in_banking="
				+ visible_in_banking + ", included_in_chart=" + included_in_chart + ", id=" + id + ", path=" + path
				+ ", updated_at=" + updated_at + "]";
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNominal_code() {
		return nominal_code;
	}
	public void setNominal_code(String nominal_code) {
		this.nominal_code = nominal_code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCategory_group() {
		return category_group;
	}
	public void setCategory_group(String category_group) {
		this.category_group = category_group;
	}
	public String getDisplay_name() {
		return display_name;
	}
	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}
	public boolean getVisible_in_banking() {
		return visible_in_banking;
	}
	public void setVisible_in_banking(boolean visible_in_banking) {
		this.visible_in_banking = visible_in_banking;
	}
	public boolean getIncluded_in_chart() {
		return included_in_chart;
	}
	public void setIncluded_in_chart(boolean included_in_chart) {
		this.included_in_chart = included_in_chart;
	}

	public LedgerAccounts(String nominal_code, String name, String category, String category_group, String display_name,
			boolean visible_in_banking, boolean included_in_chart) {
		super();
		this.nominal_code = nominal_code;
		this.name = name;
		this.category = category;
		this.category_group = category_group;
		this.display_name = display_name;
		this.visible_in_banking = visible_in_banking;
		this.included_in_chart = included_in_chart;
	}
	
	public LedgerAccounts() {
		super();
	}
	
	

}
