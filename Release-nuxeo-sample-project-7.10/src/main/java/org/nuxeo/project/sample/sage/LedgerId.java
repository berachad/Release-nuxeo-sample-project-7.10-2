package org.nuxeo.project.sample.sage;

public class LedgerId {
	
	private String idged;
	private String idsage;
	public String getIdged() {
		return idged;
	}
	public LedgerId() {
		super();
	}
	public LedgerId(String idged, String idsage) {
		super();
		this.idged = idged;
		this.idsage = idsage;
	}
	public void setIdged(String idged) {
		this.idged = idged;
	}
	public String getIdsage() {
		return idsage;
	}
	public void setIdsage(String idsage) {
		this.idsage = idsage;
	}

}
