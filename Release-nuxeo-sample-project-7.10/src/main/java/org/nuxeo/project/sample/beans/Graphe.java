package org.nuxeo.project.sample.beans;

public class Graphe {
	
	protected String label;
	protected String value;
	
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public Graphe(String label,String value)
	{
		this.label=label;
		this.value=value;
	}

}
