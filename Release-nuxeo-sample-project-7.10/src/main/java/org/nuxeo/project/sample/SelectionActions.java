package org.nuxeo.project.sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.jsf.operations.AddMessage;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.project.sample.beans.Produit;
import org.nuxeo.project.sample.services.DisplayInfoOrException;
import org.nuxeo.project.sample.services.MethodsShared;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
@Scope(ScopeType.CONVERSATION)
@Name("SelectionActions")
public class SelectionActions implements Serializable{
	private List<Produit> produits=null;
	private Object value;
	private Date date_commande;
	public Date getDate_commande() {
		return date_commande;
	}
	public void setDate_commande(Date date_commande) {
		this.date_commande = date_commande;
	}
	public String getFournisseur() {
		return fournisseur;
	}
	public void setFournisseur(String fournisseur) {
		this.fournisseur = fournisseur;
	}
	private String fournisseur;
    private MethodsShared ms = new MethodsShared();
    private static final long serialVersionUID = 1L;
    @In(create = true)
    protected transient CoreSession documentManager;
    private static final Log log = LogFactory.getLog(SelectionActions.class);
    @In(create=true)
    protected transient NavigationContext navigationContext;
    @In(create=true)
    protected transient WebActions webActions;
    @In(create=true)
    protected transient FacesMessages facesMessages;
    @In(create=true)
	protected AutomationService service;
	private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();
	private FacesContext context;
	UIComponent component;

	public Object getValue()
	{
		return value;
	}
	public void setValue(Object value)
	{
		this.value = value;
	}
	
	
	public void changeListener(ValueChangeEvent event) 
	
	{   DocumentModel currentDoc = navigationContext.getCurrentDocument();
		this.setDate_commande(null);
		this.setFournisseur(null);
	    produits=new ArrayList<Produit>();
		System.out.println("Valeur num commande1");
	    System.out.println("Valeur num commande2"+event);
	    Object newValue = event.getNewValue();
	    if(newValue!=null)
	    {
	    	 System.out.println("Valeur num commande"+newValue);	   
	    	 try 
	    	 {
			 DocumentModel doc = ms.getBCByNumBC((String)newValue, documentManager, service);
			 if(doc!=null)
			 {
				if(doc.getPropertyValue("bon_commande:date_commande")!=null)
			 this.setDate_commande(((Calendar)doc.getPropertyValue("bon_commande:date_commande")).getTime());
				if(doc.getPropertyValue("bon_commande:vendorname")!=null)
			 this.setFournisseur((String)doc.getPropertyValue("bon_commande:vendorname"));
			 // currentDoc.setPropertyValue("reception:date_commande", doc.getPropertyValue("bon_commande:date_commande"));
			 //currentDoc.setPropertyValue("reception:vendorname", doc.getPropertyValue("bon_commande:vendorname"));
			 System.out.println("Valeur num commande"+ this.date_commande);	   
			 System.out.println("Valeur num commande"+this.fournisseur);
			 List<Map<String, Serializable>> products = (List<Map<String, Serializable>>) doc.getPropertyValue("bon_commande:produit");
			 	for(int k=0; k<products.size(); k++) 
			     {
			 		Serializable qt = products.get(k).get("quantite");
					Serializable desc = products.get(k).get("produit");
					Serializable code = products.get(k).get("code_produit");
					 System.out.println("code"+code+ " description"+desc+"  quantité"+qt);	   
					produits.add(new Produit(code.toString(),(String)desc,((Long) qt).longValue()));
			 
			 
	    	     } 
			 }
			 else
			 {
					log.error("[INFO] == num BC Invalid==");
						 
			 }
	    	 }catch (OperationException e) {
	    			e.printStackTrace();
	    		}
	   }else {
			log.error("[INFO] == Erreur de remplissage null==");
			
		   
	   }
	      
	    
	}
	public List<Produit> getProduits() {
		return produits;
	}
	public void setProduits(List<Produit> produits) {
		this.produits = produits;
	}
	
}
