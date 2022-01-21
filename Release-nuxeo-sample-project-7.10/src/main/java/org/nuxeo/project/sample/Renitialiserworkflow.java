package org.nuxeo.project.sample;

import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

@Operation(id = Renitialiserworkflow.ID, category = Constants.CAT_DOCUMENT, label = "Traite Reception")

public class Renitialiserworkflow {
	public static final String ID = "Document.renitialiserworkflow";
	@Context
	protected CoreSession session;
	@Context
	protected AutomationService service;
	@Context
	protected OperationContext ctx = new OperationContext(session);
	 private final Log log = LogFactory.getLog(Renitialiserworkflow.class);
    @OperationMethod
    public void run() throws OperationException, ParseException 
	{   //System.out.println("le doc module"+input.getTitle());
    	//DocumentModel doc = (DocumentModel) service.run(ctx, "Context.FetchDocument");
    	DocumentModel doc2 = (DocumentModel) service.run(ctx, "Seam.GetCurrentDocument");
    	System.out.println("le doc module 2"+doc2.getTitle());
    	//System.out.println("le doc module 1"+doc.getTitle());
    	
    	session.reinitLifeCycleState(doc2.getRef());
    	session.save();
	}

}
