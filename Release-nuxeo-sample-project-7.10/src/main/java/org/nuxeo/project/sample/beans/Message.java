package org.nuxeo.project.sample.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name = "Message" )
@XmlAccessorType(XmlAccessType.FIELD)
public class Message {

	private String title;
	private String body;
	private short status;
	private String date;
	private MessageErreur erreur;
	
	public Message(MessageErreur error) {
		this.erreur=error;
	}


	public Message(String title, String body, short status, String date) {
		super();
		this.title = title;
		this.body = body;
		this.date = date;
		this.status = status;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getBody() {
		return body;
	}


	public void setBody(String body) {
		this.body = body;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}
	
	public short getStatus() {
		return status;
	}


	public void setStatus(short status) {
		this.status = status;
	}
	
	
}
