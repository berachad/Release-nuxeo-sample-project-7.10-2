package org.nuxeo.project.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import javax.faces.context.FacesContext;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

import org.nuxeo.ecm.automation.core.util.DocumentHelper;

@Operation(id = updatedocumentparallel.ID, category = Constants.CAT_DOCUMENT, label = "Print Info")
public class updatedocumentparallel {

	@Context
	protected CoreSession session;

	@Context
	protected AutomationService service;

	private String currentUser;

	private String workflowType;

	@Context
	protected OperationContext ctx = new OperationContext(session);

	private static final long serialVersionUID = 1L;
	private static  Integer nombre_valid = 0;

	public static final String ID = "Document.updatedocumentparallel";
	
	@OperationMethod
	public void run() throws OperationException 
	{


		DocumentModel doc = (DocumentModel) service.run(ctx, "Seam.GetCurrentDocument");
		Date actuelle = new Date();
	    DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
	     String dat = dateFormat.format(actuelle);
	       System.out.println(dat);
	 
        System.out.println("-----------------------------------------------------------------");
 
		if(doc.getType().equals("FactureFournisseur"))
		{
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();
			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("FF1:participant");
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			data = s.split(";");
		    doc.setPropertyValue("FF1:statutFacture","Validé");
			System.out.println(currentLifeCycleState);
			session.saveDocument(doc);
			session.save();
		}
		else if(doc.getType().equals("da"))
		{
			System.out.println("Je suis la : Statut Validé 3");

			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();
			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("da:participant");
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			data = s.split(";");
		    doc.setPropertyValue("da:statuworkflow","Validé");
			System.out.println(currentLifeCycleState);
			session.saveDocument(doc);
			session.save();
		}
		else if(doc.getType().equals("attest_travail"))
		{
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();
			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("attest_travail:participant");
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			data = s.split(";");
		    doc.setPropertyValue("attest_travail:statuworkflow","Validé");
			System.out.println(currentLifeCycleState);
			session.saveDocument(doc);
			session.save();
		}
		else if(doc.getType().equals("Contrat"))
		{
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();
			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("contrat:participant");
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			data = s.split(";");
		    doc.setPropertyValue("contrat:statuworkflow","Validé");
			System.out.println(currentLifeCycleState);
			session.saveDocument(doc);
			session.save();
		}
		else if(doc.getType().equals("Produit"))
		{
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();
			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("Produit:participant");
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			data = s.split(";");
		    doc.setPropertyValue("Produit:statuworkflow","Validé");
			System.out.println(currentLifeCycleState);
			session.saveDocument(doc);
			session.save();
		}
		else if(doc.getType().equals("fiche-fournisseur"))
		{
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();
			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("fiche-fournisseur:participant");
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			data = s.split(";");
		    doc.setPropertyValue("fiche-fournisseur:statuworkflow","Validé");
			System.out.println(currentLifeCycleState);
			session.saveDocument(doc);
			session.save();
		}
		else if(doc.getType().equals("Demande_conge"))
		{
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();
			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("Demande_conge:participant");
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			data = s.split(";");
		    doc.setPropertyValue("Demande_conge:statuworkflow","Validé");
			System.out.println(currentLifeCycleState);
			session.saveDocument(doc);
			session.save();
		}
	}

}
