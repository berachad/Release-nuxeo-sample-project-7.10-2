package org.nuxeo.project.sample;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.project.sample.complementary.Constant;
import org.nuxeo.project.sample.services.MethodsShared;

@Operation(id = "Document.Modificationreception", category = "Document", label = "Print Info")
public class Modificationreception {
	@Context
	protected CoreSession session;
	@Context
	protected AutomationService service;
	@Context
	protected OperationContext ctx = new OperationContext(this.session);
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(Modificationreception.class);
	private static final String LOG_COMMENT_STATE_DELETED = " AND ecm:currentLifeCycleState != 'deleted'";
	private MethodsShared method = new MethodsShared();

	@OperationMethod
	public void run(DocumentModel input) throws OperationException, ParseException {
		DocumentModel doc = null;
		if (Constant.Log_template.equals("0")) {
			doc = (DocumentModel) this.service.run(this.ctx, "Seam.GetCurrentDocument");
		} else {
			doc = session.getDocument(input.getRef());
		}
		//
		List<Map<String, Serializable>> productscommander = (List<Map<String, Serializable>>) doc
				.getPropertyValue("bon_commande:produit");
		System.out.println("productscommander : " + productscommander);

		List<Map<String, Serializable>> productsreception = (List<Map<String, Serializable>>) doc
				.getPropertyValue("bon_commande:produit_valid");
		System.out.println("productsreception : " + productsreception);

		log.error("[INFO] === productsreception SIZE :" + productsreception.size() + "===");

		String codes_prod = "";
		// get List code old Products
		if (productsreception.size() > 0 || productscommander.size() > 0) {
			for (int k = 0; k < productscommander.size(); k++) {
				Serializable codeInvoice = productscommander.get(k).get("code_produit");
				String code_prd = (String) codeInvoice.toString();
				codes_prod += "'" + code_prd + "',";
			}
			for (int j = 0; j < productsreception.size(); j++) {
				Serializable codeInvoicevalid = productsreception.get(j).get("code_produit");
				String code_prdvalid = (String) codeInvoicevalid.toString();
				codes_prod += "'" + code_prdvalid + "',";
			}
			codes_prod = codes_prod.substring(0, codes_prod.length() - 1);
			DocumentModelList documentModelListProducts = method.getProduits(codes_prod, session, ctx, service);
			for (int k = 0; k < productscommander.size(); k++) {
				Serializable codeInvoice = productscommander.get(k).get("code_produit");
				Serializable qtcommande = productscommander.get(k).get("quantite_receptionner");
				Serializable status_prd = productscommander.get(k).get("statut_produit");
				String code_prd = (String) codeInvoice.toString();
				String status = (String) status_prd.toString();
				Long qtcommandelong = (Long) qtcommande;
				log.error("[INFO] === code_prd value :" + code_prd + "===");
				log.error("[INFO] === STATUS value :" + status + "===");
				if (status.equals("Valid")) {
					log.error("[INFO] === VAL333 value  ===");
					Integer val = 0;
					Long reception = 0L;
					for (int j = 0; j < productsreception.size(); j++) {
						log.error("[INFO] === VAL333 value  ===");
						Serializable codeInvoicevalid = productsreception.get(j).get("code_produit");
						String code_prdvalid = (String) codeInvoicevalid.toString();
						Serializable qtcommandevalid = productsreception.get(j).get("quantite_receptionner");
						Long qtcommandelongvalid = (Long) qtcommandevalid;
						log.error("[INFO] === VAL332 value :" + code_prdvalid + "===");
						log.error("[INFO] === VAL334 value :" + code_prd + "===");
						log.error("[INFO] === VAL332 value :" + qtcommandelongvalid + "===");
						log.error("[INFO] === VAL334 value :" + qtcommandelong + "===");

						if (code_prdvalid.equals(code_prd)) {
							val = 1;
							log.error("[INFO] === VAL value :" + val + "===");

							if (qtcommandelongvalid != qtcommandelong) {
								val = 2;
								reception = qtcommandelongvalid;
								log.error("[INFO] === VAL33 value :" + val + "===");
							}
						}
					}
					if (val == 0) {
						log.error("[INFO] === VAL111 value :" + val + "===");

						if (qtcommandelong != null) {
							DocumentModel prd = method.getProduit(documentModelListProducts, code_prd);
							if (prd != null) {
								prd.setPropertyValue("Produit:quantiteEnStock",
										((Long) prd.getPropertyValue("Produit:quantiteEnStock")) + qtcommandelong);
								session.saveDocument(prd);
								session.save();
								log.error("[INFO] === VAL11 value :" + val + "===");

							}
						}
					}
					if (val == 2) {
						log.error("[INFO] === VAL222 value :" + val + "===");

						if (qtcommandelong != null) {
							DocumentModel prd = method.getProduit(documentModelListProducts, code_prd);
							if (prd != null) {
								prd.setPropertyValue("Produit:quantiteEnStock",
										(((Long) prd.getPropertyValue("Produit:quantiteEnStock")) - reception)
												+ qtcommandelong);
								session.saveDocument(prd);
								session.save();
							}
						}
						log.error("[INFO] === VAL 22 value :" + val + "===");
					}

				} else {
					Long val_rec = 0L;
					log.error("[INFO] === VAL valuZe :" + val_rec + "===");

					for (int j = 0; j < productsreception.size(); j++) {
						Serializable codeInvoicevalid = productscommander.get(j).get("code_produit");
						String code_prdvalid = (String) codeInvoicevalid.toString();
						Serializable qtcommandevalid = productscommander.get(j).get("quantite_receptionner");
						Long qtcommandelongvalid = (Long) qtcommandevalid;
						if (code_prd.equals(code_prdvalid)) {
							val_rec = qtcommandelongvalid;
							log.error("[INFO] === VAL0 value :" + val_rec + "===");

						}
					}
					if (val_rec != 0) {
						log.error("[INFO] === VAL1 value :" + val_rec + "===");

						DocumentModel prd = method.getProduit(documentModelListProducts, code_prd);
						if (prd != null) {
							log.error("[INFO] === VAL2 value :" + val_rec + "===");
							prd.setPropertyValue("Produit:quantiteEnStock",
									(((Long) prd.getPropertyValue("Produit:quantiteEnStock")) - val_rec));
							session.saveDocument(prd);
							session.save();
						}
					}
				}

			}
		}
//		   for(int k=0; k<productscommander.size(); k++) 
//			{   List<Map<String, Serializable>> productsvalid=new ArrayList<Map<String, Serializable>>();
//				List<Map<String, Serializable>> productscommand=new ArrayList<Map<String, Serializable>>();
//				   	Serializable status_prd = productscommander.get(k).get("statut_produit");
//			   	String status =(String)status_prd.toString();
//			    	Map<String, Serializable> map1=new HashMap<>();
//				map1.put("produit",(Serializable) productscommander.get(k).get("produit"));
//				map1.put("prixunitaire",(Serializable) productscommander.get(k).get("prixunitaire"));
//				map1.put("quantite", (Serializable) productscommander.get(k).get("quantite"));
//				double total=(Long)productscommander.get(k).get("quantite")*(Double)productscommander.get(k).get("prixunitaire");
//				map1.put("total_ligne",(Serializable)total);
//				map1.put("code_produit",(Serializable) productscommander.get(k).get("code_produit"));
//				map1.put("statut_produit",(Serializable) productscommander.get(k).get("statut_produit"));
//				Long qt=(Long)productscommander.get(k).get("quantite")-(Long)productscommander.get(k).get("quantite_receptionner");
//				map1.put("quantite_receptionner", (Serializable)productscommander.get(k).get("quantite_receptionner"));
//				map1.put("quantite_calculer",(Serializable) qt);
//				productscommand.add(map1);
//                if(productscommander.get(k).get("statut_produit").equals("Valid"))
//                {
//                	status_prd = productscommander.get(k).get("statut_produit");
//    			   	status =(String)status_prd.toString();
//    			   	
//    			   	Map<String, Serializable> map2=new HashMap<>();
//    			   	map2.put("produit",(Serializable) productscommander.get(k).get("produit"));
//    			   	map2.put("prixunitaire",(Serializable) productscommander.get(k).get("prixunitaire"));
//    			   	map2.put("quantite", (Serializable) productscommander.get(k).get("quantite"));
//    				double total1=(Long)productscommander.get(k).get("quantite")*(Double)productscommander.get(k).get("prixunitaire");
//    				map2.put("total_ligne",(Serializable)total);
//    				map2.put("code_produit",(Serializable) productscommander.get(k).get("code_produit"));
//    				map2.put("statut_produit",(Serializable) productscommander.get(k).get("statut_produit"));
//    				Long qt1=(Long)productscommander.get(k).get("quantite")-(Long)productscommander.get(k).get("quantite_receptionner");
//    				map2.put("quantite_receptionner", (Serializable)productscommander.get(k).get("quantite_receptionner"));
//    				map2.put("quantite_calculer",(Serializable) qt);
//    				productsvalid.add(map2);	
//                }
//
//                doc.getPropertyValue("bon_commande:produit");
//                doc.setPropertyValue("bon_commande:produit_valid",(Serializable)productsvalid);
//                doc.setPropertyValue("bon_commande:produit",(Serializable)productscommand);
//                session.
//                session.save();
//             
//			}

		Constant.Log_template = "0";

	}
}
