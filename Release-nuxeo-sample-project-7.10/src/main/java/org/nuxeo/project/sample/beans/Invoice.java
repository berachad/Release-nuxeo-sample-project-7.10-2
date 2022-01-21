package org.nuxeo.project.sample.beans;

import java.util.Date;
import java.util.List;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonDeserialize;


@XmlRootElement(name = "Invoice" )
@XmlAccessorType(XmlAccessType.FIELD)
public class Invoice {
	private Double montantTTC;
	
	
	public Double getMontantTTC() {
		return montantTTC;
	}


	public void setMontantTTC(Double montantTTC) {
		this.montantTTC = montantTTC;
	}


	public Double getMontantHT() {
		return montantHT;
	}


	public void setMontantHT(Double montantHT) {
		this.montantHT = montantHT;
	}


	public Double getTaxe1() {
		return taxe1;
	}


	public void setTaxe1(Double taxe1) {
		this.taxe1 = taxe1;
	}


	public Double getTaxe2() {
		return taxe2;
	}


	public void setTaxe2(Double taxe2) {
		this.taxe2 = taxe2;
	}


	

	

	public String getNaturecomptable() {
		return naturecomptable;
	}


	public void setNaturecomptable(String naturecomptable) {
		this.naturecomptable = naturecomptable;
	}


	public String getMethoddepaiement() {
		return methoddepaiement;
	}


	public Invoice(String numFacture, Double montantHT, Double taxe1, Double taxe2,
			String naturecomptable, String methoddepaiement, String devise, Date date, Double totalht, String totalttc,
			Double totalTax) {
		super();
		this.numFacture = numFacture;
		this.montantHT = montantHT;
		this.taxe1 = taxe1;
		this.taxe2 = taxe2;
		this.naturecomptable = naturecomptable;
		this.methoddepaiement = methoddepaiement;
		this.devise = devise;
		this.date = date;
		this.totalht = totalht;
		this.totalttc = totalttc;
		this.totalTax = totalTax;
	}
	public Invoice(Date date, String nomFournisseur, String numFacture, Double montantHT,
			Double taxe1, Double taxe2,
			 String naturecomptable, String methoddepaiement, String devise, Double totalTax,
			String etatfacture, String lien, Double montantTTC) {
		super();
		this.date = date;
		this.NomFournisseur = nomFournisseur;
		this.numFacture = numFacture;
		this.montantHT = montantHT;
		this.taxe1 = taxe1;
		this.taxe2 = taxe2;
		this.naturecomptable = naturecomptable;
		this.methoddepaiement = methoddepaiement;
		this.devise = devise;
		this.totalTax = totalTax;
		this.etatfacture = etatfacture;
		this.lien = lien;
		this.montantTTC = montantTTC;
	}


	public void setMethoddepaiement(String methoddepaiement) {
		this.methoddepaiement = methoddepaiement;
	}


	public String getDevise() {
		return devise;
	}


	public void setDevise(String devise) {
		this.devise = devise;
	}






	private String NomFournisseur;
	private String id;
	private String numFacture;
	private Long timestampDate;
	private Long timestampDateexpiration;
	private Double montantHT;
	  public String getNomFournisseur() {
		return NomFournisseur;
	}


	public void setNomFournisseur(String nomFournisseur) {
		NomFournisseur = nomFournisseur;
	}






	private Double taxe1;
	  private Double taxe2;
	  private String naturecomptable;
	  private String methoddepaiement;
	  private String devise;
	public Long getTimestampDate() {
		return timestampDate;
	}


	public void setTimestampDate(Long timestampDate) {
		this.timestampDate = timestampDate;
	}


	public Long getTimestampDateexpiration() {
		return timestampDateexpiration;
	}


	public void setTimestampDateexpiration(Long timestampDateexpiration) {
		this.timestampDateexpiration = timestampDateexpiration;
	}






   private String file;
	public String getFile() {
	return file;
}


public void setFile(String file) {
	this.file = file;
}






	private Date date;
	private String num_bc;
	private Date dateexpiration ;
	private Double totalht;
	private String totalttc;
	private Double totalTax;
	private String etatfacture ;
	private String lien;

	
	
	
	public Invoice(Double montantTTC, String nomFournisseur, String id, String numFacture, Long timestampDate,
			Double montantHT, Double taxe1, Double taxe2, String naturecomptable, String methoddepaiement,
			String devise, String file, Date date, String num_bc, Date dateexpiration, Double totalht, String totalttc,
			Double totalTax, String etatfacture, String lien) {
		super();
		this.montantTTC = montantTTC;
		NomFournisseur = nomFournisseur;
		this.id = id;
		this.numFacture = numFacture;
		this.timestampDate = timestampDate;
		this.montantHT = montantHT;
		this.taxe1 = taxe1;
		this.taxe2 = taxe2;
		this.naturecomptable = naturecomptable;
		this.methoddepaiement = methoddepaiement;
		this.devise = devise;
		this.file = file;
		this.date = date;
		this.num_bc = num_bc;
		this.dateexpiration = dateexpiration;
		this.totalht = totalht;
		this.totalttc = totalttc;
		this.totalTax = totalTax;
		this.etatfacture = etatfacture;
		this.lien = lien;
	}


	public Invoice(String num, Date date, String num_bc, Date dateexpiration, Double totalht,
			String totalttc, Double totalTax, String etatfacture,String lien) {
		
	
		this.numFacture = num;
		this.date = date;
		this.num_bc = num_bc;
		this.dateexpiration = dateexpiration;
		this.totalht = totalht;
		this.totalttc = totalttc;
		this.totalTax = totalTax;
		this.etatfacture = etatfacture;
		this.lien=lien;
	System.out.println(toString());
	}


	public void setNum_bc(String num_bc) {
		this.num_bc = num_bc;
	}

	@JsonDeserialize(using = CustomerDateAndTimeDeserialize.class)
	public void setDateexpiration(Date dateexpiration) {
		this.dateexpiration = dateexpiration;
	}

	
	public void setTotalht(Double totalht) {
		this.totalht = totalht;
	}


	public void setTotalttc(String totalttc) {
		this.totalttc = totalttc;
	}


	public void settotalTax(Double totalTax) {
		this.totalTax = totalTax;
	}


	public void setEtatfacture(String etatfacture) {
		this.etatfacture = etatfacture;
	}


	public Invoice() {
		super();
		// TODO Auto-generated constructor stub
	}




	public String getId() {
		return id;
	}


	

	public String getLien() {
		return lien;
	}


	public void setLien(String lien) {
		this.lien = lien;
	}


	public String getNum_bc() {
		return num_bc;
	}

	@JsonDeserialize(using = CustomerDateAndTimeDeserialize.class)
	public Date getDateexpiration() {
		return dateexpiration;
	}


	public Double getTotalht() {
		return totalht;
	}


	public String getTotalttc() {
		return totalttc;
	}


	public Double gettotalTax() {
		return totalTax;
	}


	public String getEtatfacture() {
		return etatfacture;
	}


	public void setId(String id) {
		this.id = id;
	}


	
	@JsonDeserialize(using = CustomerDateAndTimeDeserialize.class)
	public Date getDate() {
		return date;
	}

	@JsonDeserialize(using = CustomerDateAndTimeDeserialize.class)
	public void setDate(Date date) {
		this.date = date;
	}


	




	@Override
	public String toString() {
		return "Invoice [numInvoice=" + numFacture + ", date=" + date +", num_bc=" + num_bc
				+ ", dateexpiration=" + dateexpiration + ", totalht=" + totalht + ", totalttc=" + totalttc
				+ ", totalTax=" + totalTax + ", etatfacture=" + etatfacture + "]";
	}


	public String getNumFacture() {
		return numFacture;
	}


	public void setNumFacture(String numFacture) {
		this.numFacture = numFacture;
	}


	
	

}
