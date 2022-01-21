package org.nuxeo.project.sample.beans;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ObjectJson" )
@XmlAccessorType(XmlAccessType.FIELD)
public class ObjectJson {

	private String username;
	private String password;
	private String clientId;
	private String method;
	private String invoiceNum;
	private String customerCode;
	private String barreCode;
	private Calendar date;
	private String customerName;
	private String status;
	private String reason;
	private double lat;
	private double lang;
	private Calendar scanDate;
	
	
	public ObjectJson() {
		super();
		// TODO Auto-generated constructor stub
	}


	public ObjectJson(String username, String password, String clientId, String method, String invoiceNum, String customerCode,
			String barreCode, Calendar date, String customerName, String status, String reason, double lat, double lang,
			Calendar scanDate) {
		super();
		this.username = username;
		this.password = password;
		this.clientId = clientId;
		this.method = method;
		this.invoiceNum = invoiceNum;
		this.customerCode = customerCode;
		this.barreCode = barreCode;
		this.date = date;
		this.customerName = customerName;
		this.status = status;
		this.reason = reason;
		this.lat = lat;
		this.lang = lang;
		this.scanDate = scanDate;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getClientId() {
		return clientId;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public String getMethod() {
		return method;
	}


	public void setMethod(String method) {
		this.method = method;
	}


	public String getInvoiceNum() {
		return invoiceNum;
	}


	public void setInvoiceNum(String invoiceNum) {
		this.invoiceNum = invoiceNum;
	}


	public String getCustomerCode() {
		return customerCode;
	}


	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}


	public String getBarreCode() {
		return barreCode;
	}


	public void setBarreCode(String barreCode) {
		this.barreCode = barreCode;
	}


	public Calendar getDate() {
		return date;
	}


	public void setDate(Calendar date) {
		this.date = date;
	}


	public String getCustomerName() {
		return customerName;
	}


	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getReason() {
		return reason;
	}


	public void setReason(String reason) {
		this.reason = reason;
	}


	public double getLat() {
		return lat;
	}


	public void setLat(double lat) {
		this.lat = lat;
	}


	public double getLang() {
		return lang;
	}


	public void setLang(double lang) {
		this.lang = lang;
	}


	public Calendar getScanDate() {
		return scanDate;
	}


	public void setScanDate(Calendar scanDate) {
		this.scanDate = scanDate;
	}
	
	
}
