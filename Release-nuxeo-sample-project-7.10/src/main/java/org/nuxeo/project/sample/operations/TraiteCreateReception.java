package org.nuxeo.project.sample.operations;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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

@Operation(id = TraiteCreateReception.ID, category = Constants.CAT_DOCUMENT, label = "Traite Reception")
public class TraiteCreateReception {

	public static final String ID = "Document.TraiteCreateReception";

	@Context
	protected CoreSession session;
	@Context
	protected AutomationService service;
	@Context
	protected OperationContext ctx = new OperationContext(session);

	private final Log log = LogFactory.getLog(TraiteCreateReception.class);

	private MethodsShared method = new MethodsShared();
	private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();

	@OperationMethod
	public void run(DocumentModel input) throws OperationException {
		DocumentModel doc = (DocumentModel) service.run(ctx, "Seam.GetCurrentDocument");
		DocumentModel oldDocument = session.getDocument(input.getRef());
		List<Map<String, Serializable>> productsByolddoc = (List<Map<String, Serializable>>) oldDocument
				.getPropertyValue("reception:produit");
		System.out.println("productsByolddoc : " + productsByolddoc);

		List<Map<String, Serializable>> productsByaftermod = (List<Map<String, Serializable>>) doc
				.getPropertyValue("reception:produit");
		System.out.println("productsByaftermod : " + productsByaftermod);

		String codes_prod = "";
		// get List code old Products
		if (productsByolddoc.size() > 0 || productsByaftermod.size() > 0) {
			for (int k = 0; k < productsByolddoc.size(); k++) {
				String puInvoice;
				Serializable puInvoiceSbl = productsByolddoc.get(k).get("code_produit");
				puInvoice = (String) puInvoiceSbl.toString();
				codes_prod += "'" + puInvoice + "',";
			}
			// get List codes new Products
			for (int k = 0; k < productsByaftermod.size(); k++) {
				String puInvoice;
				Serializable puInvoiceSbl = productsByaftermod.get(k).get("code_produit");
				puInvoice = (String) puInvoiceSbl.toString();
				codes_prod += "'" + puInvoice + "',";
			}
			codes_prod = codes_prod.substring(0, codes_prod.length() - 1);
			// get List Products
			DocumentModelList documentModelListProducts = method.getProduits(codes_prod, session, ctx, service);
			for (int k = 0; k < productsByaftermod.size(); k++) {
				Long qteInvoice = null;
				String puInvoice = null;
				Serializable qteInvoiceSbl = productsByaftermod.get(k).get("quantite");
				Serializable puInvoiceSbl = productsByaftermod.get(k).get("code_produit");
				if (qteInvoiceSbl != null && puInvoiceSbl != null) {
					qteInvoice = (Long) qteInvoiceSbl;
					puInvoice = (String) puInvoiceSbl.toString();
					// log.error("[INFO] === Product quantite NEW : " + qteInvoice+ " ===");
					// log.error("[INFO] === Product code produit NEW : " + puInvoice+ " ===");
				}
				for (int j = 0; j < productsByolddoc.size(); j++) {
					Long qteInvoice_oldprd = null;
					String puInvoice_oldprd = null;
					Serializable qteInvoice_old = productsByolddoc.get(j).get("quantite");
					Serializable puInvoice_old = productsByolddoc.get(j).get("code_produit");
					if (qteInvoice_old != null && puInvoice_old != null) {
						qteInvoice_oldprd = (Long) qteInvoice_old;
						puInvoice_oldprd = (String) puInvoice_old.toString();
						// log.error("[INFO] === Product quantite OLD : " + qteInvoice_oldprd+ " ===");
						// log.error("[INFO] === Product code produit OLD : " + puInvoice_oldprd+ "
						// ===");
					}
					if (puInvoice.equals(puInvoice_oldprd)) {
						DocumentModel prd = method.getProduit(documentModelListProducts, puInvoice_oldprd);
						log.error("[INFO] === Product code produit EQUAL : ===" + puInvoice_oldprd + "==== -");
						prd.setPropertyValue("Produit:quantiteEnStock",
								((Long) prd.getPropertyValue("Produit:quantiteEnStock")) - qteInvoice_oldprd);
						session.saveDocument(prd);
						session.save();
					}
				}
				DocumentModel prd_new = method.getProduit(documentModelListProducts, puInvoice);
				log.error("[INFO] === Product code : ===" + puInvoice + "==== +");
				prd_new.setPropertyValue("Produit:quantiteEnStock",
						((Long) prd_new.getPropertyValue("Produit:quantiteEnStock")) + qteInvoice);
				session.saveDocument(prd_new);
				session.save();
			}
		}
	}
}
