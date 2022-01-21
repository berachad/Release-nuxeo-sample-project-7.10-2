package org.nuxeo.project.sample.beans;

public class OAuth2Client {

	
	private Integer id;
	private String clientId;
	private String clientSecret;
	private String name;
	private boolean enabled;
	
	
	public OAuth2Client() {
		super();
		// TODO Auto-generated constructor stub
	}


	public OAuth2Client(Integer id, String clientId, String clientSecret, String name, boolean enabled) {
		super();
		this.id = id;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.name = name;
		this.enabled = enabled;
	}


	public String getClientId() {
		return clientId;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}


	@Override
	public String toString() {
		return "OAuth2Client [id=" + id + ", clientId=" + clientId + ", clientSecret=" + clientSecret + ", name=" + name
				+ ", enabled=" + enabled + "]";
	}
	
	
}
