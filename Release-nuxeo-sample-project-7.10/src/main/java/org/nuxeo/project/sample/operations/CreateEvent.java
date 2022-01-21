package org.nuxeo.project.sample.operations;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

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
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.project.sample.services.DisplayInfoOrException;
import org.nuxeo.project.sample.services.MethodsShared;

@Operation(id = CreateEvent.ID, category = Constants.CAT_DOCUMENT, label = "Traite Reception")
public class CreateEvent {
	
	@Context
	protected CoreSession session;
	@Context
	protected AutomationService service;
	@Context
	protected OperationContext ctx = new OperationContext(session);
    private  final Log log = LogFactory.getLog(CalculeRetention.class);
    private MethodsShared method = new MethodsShared();
	private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();
	public static final String ID = "Document.CreateEvent";
	private HttpSession session2=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	 @OperationMethod
	  public void  run(DocumentModel input) throws OperationException 
	 {
		 DocumentModel doc = (DocumentModel) service.run(ctx, "Context.FetchDocument");
		 DocumentModelList result = session.query(
					"SELECT * FROM Document WHERE ecm:mixinType != 'HiddenInNavigation' AND ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0 AND ecm:currentLifeCycleState != 'deleted' and ecm:primaryType=\"Agenda\"");
		  if(!result.isEmpty()) 
		  {
			DocumentModel docAgenda = (DocumentModel)result.get(0); 
			List<Map<String, Serializable>> listAction=(List<Map<String, Serializable>>) doc.getPropertyValue("compte_rendu:actions");
		 	for (int j = 0; j < listAction.size(); j++) 
		 	{    System.out.printf("le docAgenda path  :"+ docAgenda.getPath().toString());			
				 DocumentModel VEVENT = this.session.createDocumentModel(docAgenda.getPath().toString(), "VEVENT"+(Serializable)((Map)listAction.get(j)).get("description"), "VEVENT");
				 VEVENT.setProperty("dublincore", "title", (Serializable)((Map)listAction.get(j)).get("description"));
				 VEVENT.setProperty("dublincore", "description", (Serializable)((Map)listAction.get(j)).get("description"));
				 VEVENT.setProperty("vevent", "dtstart",(Serializable)((Map)listAction.get(j)).get("date_action"));
				 VEVENT.setProperty("vevent", "dtend",(Serializable)((Map)listAction.get(j)).get("date_realisation"));
				 VEVENT.setProperty("vevent", "responsable", (Serializable)((Map)listAction.get(j)).get("responsable"));
				 VEVENT.setProperty("vevent", "status_action", (Serializable)((Map)listAction.get(j)).get("statut"));
		         this.session.createDocument(VEVENT);  
			}
		  }
		 else 
		 {
			 DocumentModel agenda = this.session.createDocumentModel(doc.getPath().toString(), "Agenda" ,"Agenda");
			 agenda.setProperty("dublincore", "title", "actions");
			 DocumentModel agenda2=this.session.createDocument(agenda); 	
			 List<Map<String, Serializable>> listAction=(List<Map<String, Serializable>>) doc.getPropertyValue("compte_rendu:actions");
			 for (int j = 0; j < listAction.size(); j++) 
			 {
				 DocumentModel VEVENT = this.session.createDocumentModel(agenda2.getPath().toString(), "VEVENT"+(Serializable)((Map)listAction.get(j)).get("description"), "VEVENT");
				 VEVENT.setProperty("dublincore", "title", (Serializable)((Map)listAction.get(j)).get("description"));
				 VEVENT.setProperty("VEVENT", "description", (Serializable)((Map)listAction.get(j)).get("description"));
				 VEVENT.setProperty("VEVENT", "dtstart",(Serializable)((Map)listAction.get(j)).get("date_action"));
				 VEVENT.setProperty("VEVENT", "dtend",(Serializable)((Map)listAction.get(j)).get("date_realisation"));
				 VEVENT.setProperty("VEVENT", "responsable", (Serializable)((Map)listAction.get(j)).get("responsable"));
				 VEVENT.setProperty("VEVENT", "responsable", (Serializable)((Map)listAction.get(j)).get("statut"));
		         this.session.createDocument(VEVENT);  
			 }
		 }
		 
	 }
}
