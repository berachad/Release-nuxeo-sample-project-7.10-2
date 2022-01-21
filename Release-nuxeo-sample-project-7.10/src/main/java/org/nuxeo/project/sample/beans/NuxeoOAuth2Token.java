package org.nuxeo.project.sample.beans;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "NuxeoOAuth2Token" )
@XmlAccessorType(XmlAccessType.FIELD)
public class NuxeoOAuth2Token {

	protected int id;
    protected String serviceName;
    protected String nuxeoLogin;
    protected String accessToken;
    protected String clientId;
    protected Calendar creationDate; 
    private String refreshToken;
    private Long expirationTimeMilliseconds;
    private boolean isShared;
    protected String serviceLogin;
    
    
	public NuxeoOAuth2Token() {
		super();
		// TODO Auto-generated constructor stub
	}


	public NuxeoOAuth2Token(int id, String serviceName, String nuxeoLogin, String accessToken, String clientId,
			Calendar creationDate, String refreshToken, Long expirationTimeMilliseconds, boolean isShared,
			String serviceLogin) {
		super();
		this.id = id;
		this.serviceName = serviceName;
		this.nuxeoLogin = nuxeoLogin;
		this.accessToken = accessToken;
		this.clientId = clientId;
		this.creationDate = creationDate;
		this.refreshToken = refreshToken;
		this.expirationTimeMilliseconds = expirationTimeMilliseconds;
		this.isShared = isShared;
		this.serviceLogin = serviceLogin;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getServiceName() {
		return serviceName;
	}


	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}


	public String getNuxeoLogin() {
		return nuxeoLogin;
	}


	public void setNuxeoLogin(String nuxeoLogin) {
		this.nuxeoLogin = nuxeoLogin;
	}


	public String getAccessToken() {
		return accessToken;
	}


	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}


	public String getClientId() {
		return clientId;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}


	public Calendar getCreationDate() {
		return creationDate;
	}


	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}


	public String getRefreshToken() {
		return refreshToken;
	}


	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}


	public Long getExpirationTimeMilliseconds() {
		return expirationTimeMilliseconds;
	}


	public void setExpirationTimeMilliseconds(Long expirationTimeMilliseconds) {
		this.expirationTimeMilliseconds = expirationTimeMilliseconds;
	}


	public boolean isShared() {
		return isShared;
	}


	public void setShared(boolean isShared) {
		this.isShared = isShared;
	}


	public String getServiceLogin() {
		return serviceLogin;
	}


	public void setServiceLogin(String serviceLogin) {
		this.serviceLogin = serviceLogin;
	}


	@Override
	public String toString() {
		return "NuxeoOAuth2Token [id=" + id + ", serviceName=" + serviceName + ", nuxeoLogin=" + nuxeoLogin
				+ ", accessToken=" + accessToken + ", clientId=" + clientId + ", creationDate=" + creationDate
				+ ", refreshToken=" + refreshToken + ", expirationTimeMilliseconds=" + expirationTimeMilliseconds
				+ ", isShared=" + isShared + ", serviceLogin=" + serviceLogin + "]";
	}
    
}
