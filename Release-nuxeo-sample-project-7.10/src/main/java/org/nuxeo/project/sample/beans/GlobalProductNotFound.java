package org.nuxeo.project.sample.beans;

public class GlobalProductNotFound {
	private ProductDataNotFound productData;

	public ProductDataNotFound getProductData() {
		return productData;
	}

	public void setProductData(ProductDataNotFound productData) {
		this.productData = productData;
	}

	public GlobalProductNotFound(ProductDataNotFound productData) {
		super();
		this.productData = productData;
	}
}