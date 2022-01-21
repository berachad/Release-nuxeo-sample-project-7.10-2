package org.nuxeo.project.sample.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
@XmlRootElement(name = "ListInvoice" )
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListInvoice {
	 @JsonProperty("InvoiceSage")
	private List<InvoiceSage>InvoiceSage;

	public List<InvoiceSage> getInvoiceSage() {
		return InvoiceSage;
	}

	public void setInvoiceSage(List<InvoiceSage> invoiceSage) {
		InvoiceSage = invoiceSage;
	}

}
