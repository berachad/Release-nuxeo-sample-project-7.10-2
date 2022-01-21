package org.nuxeo.project.sample.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Product" )
@XmlAccessorType(XmlAccessType.FIELD)
public class Product {
	
	private String barreCode;
	private String description;
	private Long quantityMin;
	private Long quantityInStock;
	private Long quantityRequested;
	private Double quantity;
	private Double unitPrice;
	private String unites;
	private String name;
	private String categorie;
	private String compteComptable;
	
	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}
	public Long getQuantityMin() {
		return quantityMin;
	}


	public void setQuantityMin(Long quantityMin) {
		this.quantityMin = quantityMin;
	}
	public String getCategorie() {
		return categorie;
	}


	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}


	public String getCompteComptable() {
		return compteComptable;
	}


	public void setCompteComptable(String compteComptable) {
		this.compteComptable = compteComptable;
	}
	
	public Product() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Product(String barreCode, String description, long quantityInStock, Double quantity, Long quantityRequested, Double unitPrice, String name) 
	{
		super();
		this.barreCode = barreCode;
		this.quantityInStock = quantityInStock;
		this.quantityRequested = quantityRequested;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.name = name;
	}
	public Product(String barreCode,long quantityInStock, Double quantity, Long quantityRequested, Double unitPrice, String name, String unite) 
	{
		super();
		this.barreCode = barreCode;
		this.quantityInStock = quantityInStock;
		this.quantityRequested = quantityRequested;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.name = name;
		this.unites = unite;
	}
	public Product(String barreCode,long quantityInStock, Double quantity, Long quantityRequested, Double unitPrice, String name, String unite,String categorie,String comptecomptable,Long minquantity) 
	{
		super();
		this.barreCode = barreCode;
		this.quantityInStock = quantityInStock;
		this.quantityRequested = quantityRequested;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.name = name;
		this.unites = unite;
		this.categorie = categorie;
		this.compteComptable=comptecomptable;
		this.quantityMin=minquantity;
	}
	
	


	public String getBarreCode() {
		return barreCode;
	}


	public String getUnites() {
		return unites;
	}


	public void setUnites(String unites) 
	{
		this.unites = unites;
	}


	public void setBarreCode(String barreCode) {
		this.barreCode = barreCode;
	}


	public Long getQuantityInStock() {
		return quantityInStock;
	}


	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	
	public Double getQuantity() {
		return quantity;
	}


	public void setQuantityInStock(Long quantityInStock) {
		this.quantityInStock = quantityInStock;
	}
	

	public Double getUnitPrice() {
		return unitPrice;
	}


	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	

	public Long getQuantityRequested() {
		return quantityRequested;
	}


	public void setQuantityRequested(Long quantityRequested) {
		this.quantityRequested = quantityRequested;
	}


	@Override
	public String toString() {
		return "Product [barreCode=" + barreCode + ", quantityInStock="
				+ quantityInStock + ", quantityRequested=" + quantityRequested + ",unité="+unites+",quantity=" + quantity
				+ ", unitPrice=" + unitPrice + ", name=" + name + "]";
	}


}
