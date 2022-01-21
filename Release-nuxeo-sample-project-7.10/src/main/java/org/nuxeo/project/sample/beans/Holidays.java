package org.nuxeo.project.sample.beans;

import java.util.Date;

public class Holidays {

	private String entitled;
	private Date beginDate;
	private Date endDate;
	
	public Holidays() {
		 
	}
	
	public Holidays(String entitled, Date beginDate, Date endDate) {
		super();
		this.entitled = entitled;
		this.beginDate = beginDate;
		this.endDate = endDate;
	}
	public String getEntitled() {
		return entitled;
	}
	public void setEntitled(String entitled) {
		this.entitled = entitled;
	}
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "Holidays [entitled=" + entitled + ", beginDate=" + beginDate + ", endDate=" + endDate + "]";
	}
	
	
	
}
