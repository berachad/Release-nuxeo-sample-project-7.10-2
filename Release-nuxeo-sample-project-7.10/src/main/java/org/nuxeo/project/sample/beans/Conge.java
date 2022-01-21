package org.nuxeo.project.sample.beans;

import java.util.Calendar;

public class Conge {
	private Calendar beginDate;
	private Calendar endDate;
	private String matricule;
	public String getMatricule() {
		return matricule;
	}
	public void setMatricule(String matricule) {
		this.matricule = matricule;
	}
	public Conge(Calendar beginDate, Calendar endDate, String matricule) {
		super();
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.matricule = matricule;
	}
	public Calendar getBeginDate() {
		return beginDate;
	}
	public Conge(Calendar beginDate, Calendar endDate) {
		this.beginDate = beginDate;
		this.endDate = endDate;
	}
	public void setBeginDate(Calendar beginDate) {
		this.beginDate = beginDate;
	}
	public Calendar getEndDate() {
		return endDate;
	}
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

}
