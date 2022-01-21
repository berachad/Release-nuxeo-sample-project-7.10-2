package org.nuxeo.project.sample.beans;

public class Message2 {
	private MessageErreur erreur;
	
	
	public Message2(MessageErreur error) {
		this.erreur=error;
	}


	public MessageErreur getErreur() {
		return erreur;
	}


	public void setErreur(MessageErreur erreur) {
		this.erreur = erreur;
	}
}
