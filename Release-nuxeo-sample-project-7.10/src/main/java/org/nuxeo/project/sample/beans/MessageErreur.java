package org.nuxeo.project.sample.beans;

public class MessageErreur {
	
	private String text="Bad request wrong Username & password";

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public MessageErreur(String text) {
		super();
		this.text = text;
	}
	public MessageErreur() {
		super();
	}
	

}
