package org.nuxeo.project.sample;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.core.Events;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.user.center.profile.UserProfileService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.ecm.automation.core.Constants;
import static org.nuxeo.ecm.user.center.profile.UserProfileConstants.USER_PROFILE_MATRICULE_FIELD;


@Operation(id = SoumettreDocument.ID, category = Constants.CAT_DOCUMENT, label = "Print Info")

public class SoumettreDocument {
	
	
	@Context
	protected CoreSession session;
	@Context
	protected AutomationService service;
	@Context
	protected OperationContext ctx = new OperationContext(session);
	private static final long serialVersionUID = 1L;
	public static final String ID = "Document.SoumettreDocument";
    private final Log log = LogFactory.getLog(SoumettreDocument.class);
	private Methode method = new Methode();

	@OperationMethod
	public void run() throws OperationException, ParseException {
		DocumentModel doc = (DocumentModel) service.run(ctx, "Seam.GetCurrentDocument");
		List<Map<String, Serializable>> depenses = (List<Map<String, Serializable>>) doc.getPropertyValue("note_frais:dependense");
		double total=0;
		List<String>listMatricule= new ArrayList<String>();
		// get type workflow
		 String query_workflow ="SELECT * FROM Document WHERE ecm:primaryType='configuration_notefrais' and ecm:mixinType != 'HiddenInNavigation' AND ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0 AND ecm:currentLifeCycleState != 'deleted'";
         DocumentModelList result  = session.query(query_workflow);
         String type_workflow="";
         if(!result.isEmpty())
		  {
        	 type_workflow= result.get(0).getPropertyValue("configuration_notefrais:type_workflow").toString();
		  }
        // total des dépenses
		for(int i=0; i<depenses.size(); i++) 
		{
			total=total+((Double)depenses.get(i).get("valeur")).doubleValue();
		}
		 log.error("[INFO] === depenses Total : " + total + " ===");
		 String departement =(String)doc.getPropertyValue("note_frais:departement");
		 List<Map<String, Serializable>> agent_limite=new ArrayList<Map<String, Serializable>>() ;
		 log.error("[INFO] === departement : " + departement + " ===");
		 log.error("[INFO] === size agent_limite : " + agent_limite.size() + " ===");
     	 agent_limite=method.liste_configuration_notefrais();
		 log.error("[INFO] === size agent_limite : " + agent_limite.size() + " ===");
	     	
		  for(int i=0;i<agent_limite.size(); i++)
		  {
	        if(agent_limite.get(i).get("departement").toString().equals(departement) && ((Double)agent_limite.get(i).get("limite")).doubleValue()<= total)
	        {
	          String query2 ="SELECT * FROM Document WHERE ecm:primaryType='employe' and ecm:fulltext.employe:Departement like '" + departement + "' and ecm:fulltext.employe:profession like '" + agent_limite.get(i).get("agent_employe") + "'  and ecm:mixinType != 'HiddenInNavigation' AND ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0 AND ecm:currentLifeCycleState != 'deleted'";
	          DocumentModelList list2  = session.query(query2);
	 		 log.error("[INFO] === size list2 : " + list2.size() + " ===");

			  if(!list2.isEmpty())
			  {
				  if(list2.get(0).getPropertyValue("employe:Matricule").toString()!=null)
				  {
			      listMatricule.add(list2.get(0).getPropertyValue("employe:Matricule").toString()) ;  
				  }
			  }
	        }
	       }
		  
		if(!listMatricule.isEmpty())
		{    
			  try {
				List<String> getUsers=method.getUsers(listMatricule);
				for (int i = 0; i < getUsers.size(); i++) 
				{
			    doc.setPropertyValue("note_frais:group"+(i+1), getUsers.get(i));
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// Run Automation service
		//mohcine session
				
			  
		
	    Map<String, Object> params = new HashMap<String, Object>();
	    if(type_workflow.equals("SerialDocumentReview"))
	    {
	    	params.put("id","SerialDocumentReview");
			params.put("start", true);
			service.run(ctx, "Context.StartWorkflow",params);
			final String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('" + doc.getId() + "')";
			DocumentModelList workflows = session.query(QUERY_PAYEES);
			ArrayList users=new ArrayList<String>();
			if(workflows.size()>0)
			{
				Events.instance().raiseEvent("workflowNewProcessStarted");
				
				if(((String[]) workflows.get(0).getPropertyValue("var_SerialDocumentReview:participants")).length==0)
				{
					 try {
						  
							List<String> getUsers=method.getUsers(listMatricule);
							for (int i = 0; i < getUsers.size(); i++) 
							{
						    users.add("user:"+getUsers.get(i));
							}
						 } catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    workflows.get(0).setPropertyValue("var_SerialDocumentReview:participants",(Serializable)users);	
			    
			    session.saveDocument(workflows.get(0));
				session.save();
				Events.instance().raiseEvent("tasksCacheReset"); 
			    
				}
				else {
					String lines[] = ((String[]) workflows.get(0).getPropertyValue("var_SerialDocumentReview:participants"));
					String participants = "Err";
					String[] data;
						if(lines != null)
						{
							for (String s: lines) 
							{  
							   
							    participants = participants + s;
							}
						}
						System.out.println(participants);
					
					
				}
			}
	    	
	    }
	    else
	    {
	    	params.put("id","ParallelDocumentReview");
			params.put("start", true);
			service.run(ctx, "Context.StartWorkflow",params);
			final String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('" + doc.getId() + "')";
			DocumentModelList workflows = session.query(QUERY_PAYEES);
			ArrayList users=new ArrayList<String>();
			if(workflows.size()>0)
			{
				Events.instance().raiseEvent("workflowNewProcessStarted");
				if(((String[]) workflows.get(0).getPropertyValue("var_ParallelDocumentReview:participants")).length==0)
				{
					 try {
						  
							List<String> getUsers=method.getUsers(listMatricule);
							for (int i = 0; i < getUsers.size(); i++) 
							{
						    users.add("user:"+getUsers.get(i));
							}
						 } catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    workflows.get(0).setPropertyValue("var_ParallelDocumentReview:participants",(Serializable)users);	
			    session.saveDocument(workflows.get(0));
				session.save();
				Events.instance().raiseEvent("tasksCacheReset");
				System.out.println("refreshh 2"); 
			    
				}
				else {
					String lines[] = ((String[]) workflows.get(0).getPropertyValue("var_SerialDocumentReview:participants"));
					String participants = "Err";
					String[] data;
						if(lines != null)
						{
							for (String s: lines) 
							{  
							   
							    participants = participants + s;
							}
						}
						System.out.println(participants);
					
					
				}
			}
	    	
	    }
	
		session.saveDocument(doc);
		session.save();
		
		}       	
		
	}

}
