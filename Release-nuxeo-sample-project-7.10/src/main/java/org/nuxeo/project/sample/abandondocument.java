package org.nuxeo.project.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.Serializable;

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

@Operation(id = abandondocument.ID, category = Constants.CAT_DOCUMENT, label = "Print Info")
public class abandondocument {

	@Context
	protected CoreSession session;

	@Context
	protected AutomationService service;

	private String currentUser;

	@Context
	protected OperationContext ctx = new OperationContext(session);

	 private static final long serialVersionUID = 1L;

	public static final String ID = "Document.abandondocument";
	
	@OperationMethod
	public void run() throws OperationException {

		DocumentModel doc = (DocumentModel) service.run(ctx, "Seam.GetCurrentDocument");
		if(doc.getType().equals("FactureFournisseur"))
		{
			String participants = "";
	        doc.setPropertyValue("FF1:participant", (Serializable) participants);
	        doc.setPropertyValue("FF1:statutFacture","Générée");
	        session.saveDocument(doc);
			session.save();
		}
		if(doc.getType().equals("da"))
		{
			String participants = "";
	        doc.setPropertyValue("da:participant", (Serializable) participants);
	        doc.setPropertyValue("da:statuworkflow","Générée");
	        session.saveDocument(doc);
			session.save();
		}
		if(doc.getType().equals("attest_travail"))
		{
			String participants = "";
	        doc.setPropertyValue("attest_travail:participant", (Serializable) participants);
	        doc.setPropertyValue("attest_travail:statuworkflow","Générée");
	        session.saveDocument(doc);
			session.save();
		}
		if(doc.getType().equals("Contrat"))
		{
			String participants = "";
	        doc.setPropertyValue("contrat:participant", (Serializable) participants);
	        doc.setPropertyValue("contrat:statuworkflow","Générée");
	        session.saveDocument(doc);
			session.save();
		}
		if(doc.getType().equals("Demande_conge"))
		{
			String participants = "";
	        doc.setPropertyValue("Demande_conge:participant", (Serializable) participants);
	        doc.setPropertyValue("Demande_conge:statuworkflow","Générée");
	        session.saveDocument(doc);
			session.save();
		}
		if(doc.getType().equals("fiche-fournisseur"))
		{
			String participants = "";
	        doc.setPropertyValue("fiche-fournisseur:participant", (Serializable) participants);
	        doc.setPropertyValue("fiche-fournisseur:statuworkflow","Générée");
	        session.saveDocument(doc);
			session.save();
		}
		if(doc.getType().equals("Produit"))
		{
			String participants = "";
	        doc.setPropertyValue("Produit:participant", (Serializable) participants);
	        doc.setPropertyValue("Produit:statuworkflow","Générée");
	        session.saveDocument(doc);
			session.save();
		}
		
		//TEST
	}

}
