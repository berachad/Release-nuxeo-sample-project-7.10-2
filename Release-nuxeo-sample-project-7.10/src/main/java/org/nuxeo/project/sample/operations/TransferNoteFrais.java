package org.nuxeo.project.sample.operations;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;

import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.jsf.operations.AddMessage;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.project.sample.complementary.Constant;

@Operation(id = TransferNoteFrais.ID, category = Constants.CAT_DOCUMENT, label = "transferer les notes de frais")
public class TransferNoteFrais {
	@Context
	protected CoreSession session;

	@Context
	protected AutomationService service;

	private String currentUser;

	@Context
	protected OperationContext ctx = new OperationContext(session);

	private static final long serialVersionUID = 1L;
	private Blob b;
	public static final String ID = "Document.TransferNoteFrais";
	@OperationMethod
	public void run() throws OperationException 
	
	{
		//get dossier
 DocumentModel doc = (DocumentModel) service.run(ctx, "Seam.GetCurrentDocument");
// recuperer la valeur de champ path
 
 String path=(String)doc.getPropertyValue("Dossier_note_frais:path");
// get docs selectionées
	List<DocumentModel> docs = (List<DocumentModel>) service.run(this.ctx, "Seam.GetSelectedDocuments");
	
	
	for (DocumentModel d: docs) { 
		List<Map<String, Serializable>> dep = (List<Map<String, Serializable>>) d.getPropertyValue("note_frais:dependense");
	// foreach et recuperer le file content de chaque doc et cree un document dans le path
		
		
		
		//for (Map<String, Serializable> f: dep) 
		
		for(int i=0; i<dep.size(); i++) {
			DocumentModel docnotefrais = session.createDocumentModel(path, d.getName(),"File");
		      
			docnotefrais.setProperty(Constant.DUBLINCORE, "title",(String)d.getPropertyValue("dc:title")+" : "+dep.get(i).get("description"));
			
			
			Blob recu = (Blob)dep.get(i).get("recu");
			docnotefrais.setProperty("file", "content",recu);
			
			
			session.createDocument(docnotefrais);
			session.save();
		}
		
		
		
	 }
	
	
	}

}
