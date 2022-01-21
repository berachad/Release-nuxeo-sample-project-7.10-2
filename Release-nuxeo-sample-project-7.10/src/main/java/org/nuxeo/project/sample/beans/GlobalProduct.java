package org.nuxeo.project.sample.beans;

public class GlobalProduct {
	private Product productData;

	public Product getProductData() {
		return productData;
	}

	public void setProductData(Product productData) {
		this.productData = productData;
	}

	public GlobalProduct(Product productData) {
		super();
		this.productData = productData;
	}
	

}
