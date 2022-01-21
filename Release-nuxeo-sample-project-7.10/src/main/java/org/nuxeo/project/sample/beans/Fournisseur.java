package org.nuxeo.project.sample.beans;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Fournisseur" )
@XmlAccessorType(XmlAccessType.FIELD)
public class Fournisseur {
    private String title;
	private String code_fournisseur;
	private String nom_fournisseur;
	private String email;
	private String adresse;
	private String telephone;
	private String fax;
	private String ice;
	private String identifiantfiscal;
	private String patente;
	private String RC;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCode_fournisseur() {
		return code_fournisseur;
	}
	public void setCode_fournisseur(String code_fournisseur) {
		this.code_fournisseur = code_fournisseur;
	}
	public String getNom_fournisseur() {
		return nom_fournisseur;
	}
	public void setNom_fournisseur(String nom_fournisseur) {
		this.nom_fournisseur = nom_fournisseur;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAdresse() {
		return adresse;
	}
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getIce() {
		return ice;
	}
	public void setIce(String ice) {
		this.ice = ice;
	}
	public String getIdentifiantfiscal() {
		return identifiantfiscal;
	}
	public void setIdentifiantfiscal(String identifiantfiscal) {
		this.identifiantfiscal = identifiantfiscal;
	}
	public String getPatente() {
		return patente;
	}
	public void setPatente(String patente) {
		this.patente = patente;
	}
	public String getRC() {
		return RC;
	}
	public void setRC(String rC) {
		RC = rC;
	}
	public Fournisseur(String title, String code_fournisseur, String nom_fournisseur, String email, String adresse,
			String telephone, String fax, String ice, String identifiantfiscal, String patente, String rC) {
		super();
		this.title = title;
		this.code_fournisseur = code_fournisseur;
		this.nom_fournisseur = nom_fournisseur;
		this.email = email;
		this.adresse = adresse;
		this.telephone = telephone;
		this.fax = fax;
		this.ice = ice;
		this.identifiantfiscal = identifiantfiscal;
		this.patente = patente;
		RC = rC;
	}
	public Fournisseur() {
		super();
	}
	
	
}
