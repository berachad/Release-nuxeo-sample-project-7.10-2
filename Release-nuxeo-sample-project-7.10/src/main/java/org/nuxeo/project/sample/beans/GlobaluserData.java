package org.nuxeo.project.sample.beans;

public class GlobaluserData {
 private UserData userData;

public UserData getUserData() {
	return userData;
}

public GlobaluserData(UserData userData) {
	super();
	this.userData = userData;
}

public void setUserData(UserData userData) {
	this.userData = userData;
}
 
}
