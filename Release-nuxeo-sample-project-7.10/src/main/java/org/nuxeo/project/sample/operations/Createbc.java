package org.nuxeo.project.sample.operations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.jsf.operations.AddMessage;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.project.sample.complementary.Constant;
import org.nuxeo.project.sample.services.DisplayInfoOrException;
import org.nuxeo.project.sample.services.MethodsShared;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;


@Operation(id = Createbc.ID, category = Constants.CAT_DOCUMENT, label = "Create")
public class Createbc {
	
	public static final String ID = "Document.Createbc";

	@Context
	protected CoreSession session;
	@Context
	protected AutomationService service;
	@Context
	protected OperationContext ctx = new OperationContext(session);

    private final Log log = LogFactory.getLog(Createbc.class);
    
    private MethodsShared ms = new MethodsShared();
	private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();

	  @OperationMethod
	  public void run() throws OperationException {
		  
		  DocumentModelList docs = (DocumentModelList) this.service.run(this.ctx, "Seam.GetSelectedDocuments");
		  
			  if (!docs.isEmpty()) {
					log.error(Constant.INFO_DASHES);
					log.error("[INFO] =====< " + docs.size() + " Document(s) Selected >=====");
					createBCbyDA(docs);
			  } else {
					log.error(Constant.INFO_DASHES);
					log.error("[INFO] =====< No Document Selected >=====");
					log.error(Constant.INFO_DASHES);
					displayInfoOrException.sendMessage(ctx, service, AddMessage.ID, "#{messages['label.select.doc']}");
					
			  } 
	  }
	  
	  private void createBCbyDA(DocumentModelList docs) throws OperationException {
		  
				for(DocumentModel doc:docs) {
					if(doc.getType().equals("da")) {
						
					String workflowStatus = doc.getCurrentLifeCycleState();
					log.error("[INFO] == Workflow status of da : " + workflowStatus + " ==");
					String title = (String) doc.getTitle();
					log.error("[INFO] == title of da : " + title + " ==");
					String applicant = (String) doc.getProperty("da", "nom_du_demandeur");
					log.error("[INFO] == Name applicant of da : " + applicant + " ==");
					Integer num_da =(Integer) doc.getProperty("da", "num_da");
					log.error("[INFO] == Number of da : " + num_da + " ==");
					
					if(workflowStatus.equals("Approved")) {
						
					List<Map<String, Serializable>> choix = (List<Map<String, Serializable>>) doc.getPropertyValue("da:choix");
					log.error("[INFO] ======== < List of complex fields of da > ========");
					 
						log.error("[INFO] ======== < Product size " + choix.size() + " of number da : " + num_da + " > ========");
						boolean exist = false;
						List<Map<String, Serializable>> list = new ArrayList<>();
						for(int j=0; j<choix.size(); j++) {
							
							log.error("[INFO] ======== < Product " + j + " of number da : " + num_da + " > ========");
							
							if(choix.get(j).get("statut_devis").equals("Validé")) { 
								
								Map<String, Serializable> complexField = new HashMap<>();
								complexField.put("produit", choix.get(j).get("produit"));
								log.error("[INFO] === Product name : " + choix.get(j).get("produit") + " ===");
								
								complexField.put("quantite", choix.get(j).get("quantite"));
								log.error("[INFO] === Quantity : " + choix.get(j).get("quantite") + " ===");
								
								complexField.put("total_ligne", choix.get(j).get("total_ligne"));
								log.error("[INFO] === Total line : " + choix.get(j).get("total_ligne") + " ===");
								
								complexField.put("prixunitaire", choix.get(j).get("prixunitaire"));
								log.error("[INFO] === unit price : " + choix.get(j).get("prixunitaire") + " ===");
								
								complexField.put("code_produit", choix.get(j).get("code_produit"));
								log.error("[INFO] === product code : " + choix.get(j).get("code_produit") + " ===");
								
								list.add((Map<String, Serializable>) complexField);
								exist = true;
							}else {
								log.error("[INFO] ======== < Product " + j + " of number da : " + num_da + " n'est pas validé > ========");
							}
						}
					 if(exist == true) {
						// DocumentModel sq = ms.getNumberOfSequence(session, ctx, service);
						// long num  = (long) sq.getProperty("maxALD", "maxnumDA") + 1;
						// log.error("[INFO] == numéro sequence : " + num + " ==");
						 
					DocumentModel docBC = session.createDocumentModel(Constant.ROOT_FOLDER_BC, "BC_"+num_da, "Bon_Commande");
					docBC.setProperty(Constant.DUBLINCORE, "title", "BC_"+num_da);
					docBC.setProperty("bon_commande", "nom_du_demandeur", applicant);
					docBC.setProperty("bon_commande", "num_commande",(Object) ("BC_"+num_da));
					docBC.setProperty("bon_commande", "num_da", num_da);
					docBC.setProperty("bon_commande", "produit", list);
					session.createDocument(docBC);
					
					list.clear();
					
					//sq.setPropertyValue("maxALD:maxnumDA", num);
					//session.saveDocument(sq);
					session.save();
					}
					}else {
						displayInfoOrException.sendMessage(ctx, service, AddMessage.ID, "#{messages['label.msgda.valid']}");
						
					}
				}
			 }
			 log.error(Constant.INFO_DASHES);
	  }
}
