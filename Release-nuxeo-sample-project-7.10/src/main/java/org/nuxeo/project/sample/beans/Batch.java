package org.nuxeo.project.sample.beans;

import javax.xml.bind.annotation.XmlAccessType;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Batch" )
@XmlAccessorType(XmlAccessType.FIELD)
public class Batch {
	
	private String Id;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public Batch(String id) {
		super();
		Id = id;
	}

}
