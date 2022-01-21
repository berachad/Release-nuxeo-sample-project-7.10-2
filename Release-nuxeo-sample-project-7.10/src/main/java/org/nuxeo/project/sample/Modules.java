package org.nuxeo.project.sample;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.nuxeo.ecm.user.center.dashboard.jsf.JSFDashboardActions;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.local.LocalSession;
import org.nuxeo.ecm.core.model.Session;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.platform.ui.web.util.BaseURL;
import org.postgresql.util.PGInterval;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.nuxeo.ecm.core.api.DocumentModel;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped; 
@Scope(ScopeType.CONVERSATION)
@Name("Modules")  
public class Modules implements Serializable
  {
	  private String listWorkspaces ="";  
	  private String workSpace ="";
	  private String createWorkspaces =""; 
	  private String dossiers ="";
	  private String template ="";
	  private String sections ="";   
	  

	private static final long serialVersionUID = 1L;
	  private static final Log log = LogFactory.getLog(Modules.class);
	  
	  @In(create = true)
	  protected transient CoreSession coreSession; 
	  
	  @In(create = true)
	  protected transient NavigationContext navigationContext;
	  
	  @In(create = true)
	  protected transient WebActions webActions;
	  
	  @In(create = true)
	  protected transient CoreSession documentManager;
	
	  @In(create = true)
	  protected transient FacesMessages facesMessages;
	// Domain selected dropdown list  
	  String domainName = JSFDashboardActions.getMyDomainSelected();
	   
	  public String getdomainName() {
		return JSFDashboardActions.getMyDomainSelected();
	}


	public void setdomainName(String domainName) {
		this.domainName = domainName;
	}
 
	//list workspaces
	  public String  getListWorkspaces() {
		  String Requete="";
	  ArrayList<Documents> listDocument = new ArrayList<Documents>();
		if(isAdministrator()){
			Requete="And ecm:path STARTSWITH '"+getdomainName()+"'";
		}
	  String query = "SELECT * FROM Workspace where ecm:primaryType=\"Workspace\" "+Requete; 
	  
	  DocumentModelList docs = documentManager.query(query);
	  if (docs!=null) {
	     for (DocumentModel documentModel : docs) { 
	    	  
	    	 listWorkspaces=listWorkspaces+"'"+documentModel.getId()+"'"+",";
	    	 Documents d = new Documents(documentModel.getName(),documentModel.getId(),BaseURL.getBaseURL().toString()+"nxpath/default"+documentModel.getPath().toString()+"@view_documents?tabIds=MAIN_TABS%3Adocuments%2C%3A&conversationId=0NXMAIN1");
	    	 listDocument.add(d); 
		}
 	  } 	
	  String json = new Gson().toJson(listDocument);
	  return (json);  }  
 
	  public void setListWorkspaces(String listWorkspaces) {
		this.listWorkspaces = listWorkspaces;
	}
	   
	  // create workspaces (disebeled)
		public String getCreateWorkspaces() {
		ArrayList<Documents> listDomain = new ArrayList<Documents>();
			String query1 = "SELECT * FROM Domain"; 
			 DocumentModelList docs = documentManager.query(query1);
//			 System.out.println("a query1 a:"+query1);
			  if (docs!=null) {
			     for (DocumentModel documentModel : docs) {
			    	 createWorkspaces=documentModel.getName();
			    	 Documents WorkSpace = new Documents(documentModel.getName(),documentModel.getId(),BaseURL.getBaseURL().toString()+"nxpath/default"+documentModel.getPath().toString()+"/workspaces@create_workspace?mainTabId=MAIN_TABS%3Adocuments&tabIds=MAIN_TABS%3Adocuments%2CTREE_EXPLORER%3Anavtree_CONTENT_TREE%2CUSER_CENTER%3Amodules&conversationId=0NXMAIN");
			    	 listDomain.add(WorkSpace);      																																					
				   } 
				}
			  String json = new Gson().toJson(listDomain);
			  return (json); 
			}  
public void setCreateWorkspaces(String createWorkspaces) {
			this.createWorkspaces = createWorkspaces;
		}

// Floders docType
public String getDossiers() {
 String Requete="";  
	ArrayList<Documents> listDossiers = new ArrayList<Documents>();
	if(isAdministrator()){
		Requete="And ecm:path STARTSWITH '"+getdomainName()+"'";
	}
		String queryDossier = "SELECT * FROM Document WHERE ecm:primaryType IN('folderInvoice','folderAR','folderBC','dossierficheclient','Dossier_Demande_conge','dossiercompterendu','Dossier_attest_travail','folderemploye','Dossier_ordre_mission','Dossier_bulletins_paie','Dossier_utilisation','dossierfour','Dossier_reception','Dossier_note_frais','FolderBL','Dossier_Produit','FolderFacture','dossierDA','Dossier_bon_commande','FolderContrat','folder_configcompterendu','Dossier_configuration_notefrais','Dossier_Max_ordrermission','folder_configurationda','Dossier_max','doss_retention','FolderConfiguration','Config-Contrat','Config-Retention','Config-Archivage','folder_activation','folderequipement','Config-Activation','Config-Equipement','Config-Calandrier','Config-Courrier','folder_userlicense',"
				+ "'dossier_config_workflow','dossier_departement','folder_configurationda') "+Requete; 
		 DocumentModelList docs = documentManager.query(queryDossier); 
		  if (docs!=null) {
		     for (DocumentModel documentModel : docs) { 
		    	 dossiers=dossiers+"'"+documentModel.getName()+"'"+",";  
		    	 Documents dossiers = new Documents(documentModel.getName(),documentModel.getId(),BaseURL.getBaseURL().toString()+"nxpath/default"+documentModel.getPath().toString()+"@view_documents?tabIds=%3A&conversationId=0NXMAIN",documentModel.getType().toString());
		    	 listDossiers.add(dossiers);   
			   } 
			}
		  String json = new Gson().toJson(listDossiers );
		  return (json); 
		}   
		public void setDossiers(String dossiers) {
	   this.dossiers = dossiers;
}
		
		//Template
		public String getTemplate() {
			String Requete ="";
			ArrayList<Documents> listTemplate = new ArrayList<Documents>();
			if(isAdministrator()){
				Requete="And ecm:path STARTSWITH '"+getdomainName()+"'";
			}
			
				String queryTemplate = "SELECT * FROM Document WHERE ecm:primaryType IN ('WebTemplateSource','TemplateSource') "+Requete;   
  
				 DocumentModelList docs = documentManager.query(queryTemplate); 
				  if (docs!=null) {
				     for (DocumentModel documentModel : docs) {
				    	  
				    	 template=template+"'"+documentModel.getName()+"'"+","; 
				    	  
				    	 Documents template = new Documents(documentModel.getName(),documentModel.getId(),BaseURL.getBaseURL().toString()+"nxpath/default"+documentModel.getPath().toString()+"@view_documents?tabIds=%3A&conversationId=0NXMAIN",documentModel.getType().toString());
				    	 listTemplate.add(template);                 
				    	 
					   } 
					}
				  String json = new Gson().toJson(listTemplate );
				  return (json); 
				} 
		public void setTemplate(String template) {
			   this.template = template;
		}
		public String getSections() {
		  String Requete ="";
			ArrayList<Documents> listSections = new ArrayList<Documents>();
			
			if(isAdministrator()){
				Requete=" And ecm:path STARTSWITH '"+getdomainName()+"'";
			}
			
				String querySections = "SELECT * FROM document where ecm:primaryType='Section'"+Requete; 
			 
				 DocumentModelList docs = documentManager.query(querySections); 
				  if (docs!=null) {
				     for (DocumentModel documentModel : docs) { 
				    	 sections=sections+"'"+documentModel.getName()+"'"+",";  
				    	 Documents sections = new Documents(documentModel.getName(),documentModel.getId(),BaseURL.getBaseURL().toString()+"nxpath/default"+documentModel.getPath().toString()+"@view_documents?tabIds=%3A&conversationId=0NXMAIN",documentModel.getType().toString());
				    	 listSections.add(sections);  
					   } 
					}
				  String json = new Gson().toJson(listSections );
				  return (json); 
				} 
		public void setSections(String sections) {
			   this.sections = sections;
		} 
		public boolean isAdministrator() {
			 Principal currentUser = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
			    boolean nxUser = ((NuxeoPrincipal)currentUser).isAdministrator();
		 return nxUser;
		}
		
}
