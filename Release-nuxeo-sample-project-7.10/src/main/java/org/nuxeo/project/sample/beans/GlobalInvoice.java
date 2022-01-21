package org.nuxeo.project.sample.beans;

public class GlobalInvoice {
	private Invoice invoiceData;

	public GlobalInvoice(Invoice invoiceData) {
		super();
		this.invoiceData = invoiceData;
	}

	public Invoice getInvoiceData() {
		return invoiceData;
	}

	public void setInvoiceData(Invoice invoiceData) {
		this.invoiceData = invoiceData;
	}
	

}
