package org.nuxeo.project.sample;

public class listEm {

	private int mois;
	private double tarif;
	
	
	public listEm(int mois, double tarif) {
		super();
		this.mois = mois;
		this.tarif = tarif;
	}
	public int getMois() {
		return mois;
	}
	public void setMois(int mois) {
		this.mois = mois;
	}
	public double getTarif() {
		return tarif;
	}
	public void setTarif(double tarif) {
		this.tarif = tarif;
	}
	
	
}
