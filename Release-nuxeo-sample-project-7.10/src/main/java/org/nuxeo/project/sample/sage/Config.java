package org.nuxeo.project.sample.sage;

public class Config {

	private String clientId;
	private String secretClient;
	private String signingClient;
	private String key;
	private String url;
	private String scope;
	private String endpoint;
	private String lienfournisseur;
	private String lienfichierlog;

	public String getLienfichierlog() {
		return lienfichierlog;
	}
	public void setLienfichierlog(String lienfichierlog) {
		this.lienfichierlog = lienfichierlog;
	}
	public String getLienfournisseur() {
		return lienfournisseur;
	}
	public void setLienfournisseur(String lienfournisseur) {
		this.lienfournisseur = lienfournisseur;
	}
	public String getAddress_region_id() {
		return address_region_id;
	}
	public void setAddress_region_id(String address_region_id) {
		this.address_region_id = address_region_id;
	}
	private String endpointCentral;
	private String usurl;
	private String address_region_id;
	
	
	
	public Config(String clientId, String key, String secretClient, String signingClient, String url, String scope, String endpoint,
			String endpointCentral, String usurl,String address_region_id) {
		super();
		this.clientId = clientId;
		this.key = key;
		this.secretClient = secretClient;
		this.signingClient = signingClient;
		this.url = url;
		this.scope = scope;
		this.endpoint = endpoint;
		this.endpointCentral = endpointCentral;
		this.address_region_id = address_region_id;
		this.usurl = usurl;
	}
	public Config(String clientId, String key, String secretClient, String signingClient, String url, String scope, String endpoint,
			String endpointCentral, String usurl,String address_region_id,String lienfournisseur) {
		super();
		this.clientId = clientId;
		this.key = key;
		this.secretClient = secretClient;
		this.signingClient = signingClient;
		this.url = url;
		this.scope = scope;
		this.endpoint = endpoint;
		this.endpointCentral = endpointCentral;
		this.address_region_id = address_region_id;
		this.usurl = usurl;
		this.lienfournisseur=lienfournisseur;
	}
	public Config(String clientId, String key, String secretClient, String signingClient, String url, String scope, String endpoint,
			String endpointCentral, String usurl,String address_region_id,String lienfournisseur,String lienfichierlog) {
		super();
		this.clientId = clientId;
		this.key = key;
		this.secretClient = secretClient;
		this.signingClient = signingClient;
		this.url = url;
		this.scope = scope;
		this.endpoint = endpoint;
		this.endpointCentral = endpointCentral;
		this.address_region_id = address_region_id;
		this.usurl = usurl;
		this.lienfournisseur=lienfournisseur;
		this.lienfichierlog=lienfichierlog;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getSecretClient() {
		return secretClient;
	}
	public void setSecretClient(String secretClient) {
		this.secretClient = secretClient;
	}
	public String getSigningClient() {
		return signingClient;
	}
	public void setSigningClient(String signingClient) {
		this.signingClient = signingClient;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public String getEndpointCentral() {
		return endpointCentral;
	}
	public void setEndpointCentral(String endpointCentral) {
		this.endpointCentral = endpointCentral;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getUsurl() {
		return usurl;
	}
	public void setUsurl(String usurl) {
		this.usurl = usurl;
	}
	
	
}
