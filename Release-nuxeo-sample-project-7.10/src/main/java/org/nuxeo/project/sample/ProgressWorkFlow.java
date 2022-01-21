package org.nuxeo.project.sample;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;

@Scope(ScopeType.CONVERSATION)
@Name("ProgressWorkFlow")
public class ProgressWorkFlow
  implements Serializable
{  
  @In(create = true)
  protected transient NavigationContext navigationContext;
   
   private int nombreparticipant = 0;
   private int nombreparticipantDA = 0;
   private int nombreparticipantContrat = 0;
   private int nombreparticipantProduit= 0;
   private int nombreparticipantFournisseur = 0;
   private String PartcipantFournisseur = null;

   
public void setNombreparticipant(int particp) {
	 this.nombreparticipant =particp;
}

public int getNombreparticipant() throws SQLException {
	 
    
	  int nbrvalid=0;
	  int nbrrejete=0;
	  int calcule=0;
	  int particpant=0;
		DocumentModel doc = this.navigationContext.getCurrentDocument();

		String participants=(String)doc.getPropertyValue("FF1:participant");
		if (participants != null) {
		String[]nbparticipants=participants.split(";");
		int nbrpr=nbparticipants.length;
		for (int i = 0; i < nbparticipants.length; i++) {
			if(nbparticipants[i].indexOf("Validé")!=-1)
			{
				nbrvalid=nbrvalid+1; 
			}
			else if(nbparticipants[i].indexOf("Rejeté")!=-1)
			{
				nbrrejete=nbrrejete+1;	 
			}	
		}
		
		particpant=nbrvalid+nbrrejete; 
		 calcule=((particpant*100)/nbrpr);
		}
		return calcule;	
  }


public void setNombreparticipantDA(int particp) {
	 this.nombreparticipantDA =particp;
} 

public int getNombreparticipantDA() throws SQLException {
	 
   
	  int nbrvalid=0;
	  int nbrrejete=0;
	  int calcule=0;
	  int particpant=0;
		DocumentModel doc = this.navigationContext.getCurrentDocument();

		String participants=(String)doc.getPropertyValue("da:participant");
		if (participants != null) {
		String[]nbparticipants=participants.split(";");
		int nbrpr=nbparticipants.length;
		for (int i = 0; i < nbparticipants.length; i++) {
			if(nbparticipants[i].indexOf("Validé")!=-1)
			{
				nbrvalid=nbrvalid+1; 
			}
			else if(nbparticipants[i].indexOf("Rejeté")!=-1)
			{
				nbrrejete=nbrrejete+1;	 
			}	
		}
		
		particpant=nbrvalid+nbrrejete; 
		 calcule=((particpant*100)/nbrpr);
		}
		return calcule;	
 }

 public void setNombreparticipantContrat(int particp) {
	 this.nombreparticipantContrat =particp;
}

public int getNombreparticipantContrat() throws SQLException {
	 
   
	  int nbrvalid=0;
	  int nbrrejete=0;
	  int calcule=0;
	  int particpant=0;
		DocumentModel doc = this.navigationContext.getCurrentDocument();

		String participants=(String)doc.getPropertyValue("contrat:participant");
		if (participants != null) {
		String[]nbparticipants=participants.split(";");
		int nbrpr=nbparticipants.length;
		for (int i = 0; i < nbparticipants.length; i++) {
			if(nbparticipants[i].indexOf("Validé")!=-1)
			{
				nbrvalid=nbrvalid+1; 
			}
			else if(nbparticipants[i].indexOf("Rejeté")!=-1)
			{
				nbrrejete=nbrrejete+1;	 
			}	
		}
		
		particpant=nbrvalid+nbrrejete; 
		 calcule=((particpant*100)/nbrpr);
		}
		return calcule;	
 }


public void setNombreparticipantProduit(int particp) {
	 this.nombreparticipantProduit =particp;
}

public int getNombreparticipantProduit() throws SQLException {
	 
  
	  int nbrvalid=0;
	  int nbrrejete=0;
	  int calcule=0;
	  int particpant=0;
		DocumentModel doc = this.navigationContext.getCurrentDocument();

		String participants=(String)doc.getPropertyValue("Produit:participant");
		if (participants != null) {
		String[]nbparticipants=participants.split(";");
		int nbrpr=nbparticipants.length;
		for (int i = 0; i < nbparticipants.length; i++) {
			if(nbparticipants[i].indexOf("Validé")!=-1)
			{
				nbrvalid=nbrvalid+1; 
			}
			else if(nbparticipants[i].indexOf("Rejeté")!=-1)
			{
				nbrrejete=nbrrejete+1;	 
			}	
		}
		
		particpant=nbrvalid+nbrrejete; 
		 calcule=((particpant*100)/nbrpr);
		}
		return calcule;	
}

public void setNombreparticipantFournisseur(int particp) {
	 this.nombreparticipantFournisseur =particp;
}



public int getNombreparticipantFournisseur() throws SQLException {
	 
 
	  int nbrvalid=0;
	  int nbrrejete=0;
	  int calcule=0;
	  int particpant=0;
		DocumentModel doc = this.navigationContext.getCurrentDocument();

		String participants=(String)doc.getPropertyValue("fiche-fournisseur:participant");
		if (participants != null) {
		String[]nbparticipants=participants.split(";");
		int nbrpr=nbparticipants.length;
		for (int i = 0; i < nbparticipants.length; i++) {
			if(nbparticipants[i].indexOf("Validé")!=-1)
			{
				nbrvalid=nbrvalid+1; 
			}
			else if(nbparticipants[i].indexOf("Rejeté")!=-1)
			{
				nbrrejete=nbrrejete+1;	 
			}	
		}
		
		particpant=nbrvalid+nbrrejete; 
		 calcule=((particpant*100)/nbrpr);
		}
		return calcule;	
}


public void setPartcipantFournisseur(String particp) {
	 this.PartcipantFournisseur =particp;
}


public String getPartcipantFournisseur() throws SQLException { 
    System.out.println("Je suis la PR");
	DocumentModel doc = this.navigationContext.getCurrentDocument();

	String participants=(String)doc.getPropertyValue("fiche-fournisseur:participant");
	  System.out.println("participants TEST"+participants);
	return participants;	
}

}

 

