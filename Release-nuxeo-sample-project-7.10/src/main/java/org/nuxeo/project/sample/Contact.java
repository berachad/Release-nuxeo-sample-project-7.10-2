package org.nuxeo.project.sample;

import org.nuxeo.ecm.core.api.Blob;

public class Contact {

	private String email;
	private Blob file;
	private int index;
	
	
	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public Blob getFile() {
		return file;
	}


	public void setFile(Blob file) {
		this.file = file;
	}


	public Contact(String email, Blob file) {
		super();
		this.email = email;
		this.file = file;
		
	}
	
	
}
