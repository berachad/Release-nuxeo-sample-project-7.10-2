package org.nuxeo.project.sample;


import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.nuxeo.ecm.automation.core.operations.document.BlockPermissionInheritance;
import java.util.Map;
import java.util.HashMap;

import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;






@Operation(id = CreateVevent.ID, category = Constants.CAT_DOCUMENT, label = "Create Vevent")
public class CreateVevent {
	
	@Context
	protected CoreSession session;
	@Context
	protected AutomationService service;
	@Context
	protected OperationContext ctx = new OperationContext(session);

	public static final String ID = "Document.CreateVevent";

    private final Log log = LogFactory.getLog(CreateVevent.class);

	  @OperationMethod
	  public void run() throws OperationException {
		  	System.out.println("hihihihihihi");


//	  	String query = "SELECT * FROM Document WHERE ecm:uuid = '13963f87-e8cc-44a7-bcc4-6089d4ccfc24' AND ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0 AND ecm:currentLifeCycleState != 'deleted'";
//	  	DocumentModelList list  = session.query(query);
//	  	DocumentModel file = session.getDocument(list.get(0).getRef());
	  	System.out.println("hhhhhhhhhhhhh");
//	  	String title = (String) doc.getPropertyValue("dc:title");
//        var NomEmploye = doc.getPropertyValue("Demande_conge:Nom_employe");
//        var Matricule = doc.getPropertyValue("Demande_conge:Matricule");
//        var Departement = doc.getPropertyValue("Demande_conge:Departement");
//        var Superviseur = doc.getPropertyValue("Demande_conge:Superviseur");
//        var DateDebut = doc.getPropertyValue("Demande_conge:DateDebut");
//        var DateFin = input.getProperty("Demande_conge:DateFin");
	  }
	  
	  
}
