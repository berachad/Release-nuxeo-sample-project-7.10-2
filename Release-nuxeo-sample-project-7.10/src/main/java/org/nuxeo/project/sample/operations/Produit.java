package org.nuxeo.project.sample.operations;

public class Produit {
	public String code;
	public String desc;
	public Long qt;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Long getQt() {
		return qt;
	}
	public void setQt(Long qt) {
		this.qt = qt;
	}
	public Produit(String code, String desc, Long qt) {
		this.code = code;
		this.desc = desc;
		this.qt = qt;
	}

}
