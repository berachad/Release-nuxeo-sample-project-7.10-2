package org.nuxeo.project.sample.beans;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.nuxeo.ecm.core.api.Blob;

@XmlRootElement(name = "Invoice2" )
@XmlAccessorType(XmlAccessType.FIELD)
public class Invoice2 {
	
	
	private String numberInvoice;
	private String agent;
	private Blob blobMix;
	private String rga;
	private String customerName;
	private String customerCode;
	private double nbrInvoices;
	private int nbrEnv;
	
	 
	public Invoice2() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Invoice2(String numberInvoice, String agent, String rga, String customerName, String customerCode, Blob blobMix, double nbrInvoices, int nbrEnv) {
		super();
		this.numberInvoice = numberInvoice;
		this.agent = agent;
		this.blobMix = blobMix;
		this.rga = rga;
		this.customerName = customerName;
		this.customerCode = customerCode;
		this.nbrInvoices = nbrInvoices;
		this.nbrEnv = nbrEnv;
	}
	
	public String getNumberInvoice() {
		return numberInvoice;
	}

  
	public void setNumberInvoice(String numberInvoice) {
		this.numberInvoice = numberInvoice;
	}

 
	public String getAgent() {
		return agent;
	}
 

	public void setAgent(String agent) {
		this.agent = agent;
	}
	
	public String getRga() {
		return rga;
	}

	public void setRga(String rga) {
		this.rga = rga;
	}
	
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}


	public Blob getBlobMix() {
		return blobMix;
	}


	public void setBlobMix(Blob blobMix) {
		this.blobMix = blobMix;
	}
	
	public double getNbrInvoices() {
		return nbrInvoices;
	}

	public double getNbrEnv() {
		return nbrEnv;
	}

}
