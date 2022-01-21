package org.nuxeo.project.sample.operations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.nuxeo.ecm.automation.jsf.operations.AddMessage;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.project.sample.complementary.Constant;
import org.nuxeo.project.sample.services.DisplayInfoOrException;
import org.nuxeo.project.sample.services.MethodsShared;

@Operation(id = TreatFactureFournisseur.ID, category = Constants.CAT_DOCUMENT, label = "Treat facture fournisseur")
public class TreatFactureFournisseur {

	public static final String ID = "Document.TreatFactureFournisseur";

	@Context
	protected CoreSession session;
	@Context
	protected AutomationService service;
	@Context
	protected OperationContext ctx = new OperationContext(session);
	// LocaleProvider localeProvider = Framework.getService(LocaleProvider.class);
	// String locale = localeProvider.getLocale(session).toString();
	private final Log log = LogFactory.getLog(TreatFactureFournisseur.class);

	private MethodsShared ms = new MethodsShared();
	private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();
	private List<Map<String, Serializable>> productsByRpt;

	@OperationMethod
	public void run() throws OperationException {
		DocumentModelList docs = (DocumentModelList) this.service.run(this.ctx, "Seam.GetSelectedDocuments");

		if (!docs.isEmpty()) {
			log.error(Constant.INFO_DASHES);
			log.error("[INFO] =====< " + docs.size() + " Document(s) Selected >=====");
			compareProductFFWithBC(docs);
		} else {
			log.error(Constant.INFO_DASHES);
			log.error("[INFO] =====< No Document Selected >=====");
			log.error(Constant.INFO_DASHES);
			log.error("[INFO] == The selected document is not of type provider invoice ==");
			displayInfoOrException.sendMessage(ctx, service, AddMessage.ID, "#{messages['label.select.doc']}");

		}
	}

	private void compareProductFFWithBC(DocumentModelList docs) throws OperationException {

		for (DocumentModel doc : docs) {

			if (doc.getType().equals("FactureFournisseur")) {

				String numberInvoice = (String) doc.getPropertyValue("FF1:NumeroFacture");
				log.error("[INFO] == number invoice from provider invoice : " + numberInvoice + " ==");

				String numBC = (String) doc.getPropertyValue("FF1:NumeroBC");

				log.error("[INFO] == number Purchase Order from provider invoice : " + numBC + " ==");

				List<Map<String, Serializable>> products = (List<Map<String, Serializable>>) doc
						.getPropertyValue("FF1:produit");
				System.out.println("products : " + products);

				log.error("[INFO] ======== < List of complex fields from provider invoice > ========");

				log.error("[INFO] ======== < Product size " + products.size() + " of number provider invoice : "
						+ numberInvoice + " > ========");
				List<Map<String, Serializable>> listBc = new ArrayList<Map<String, Serializable>>();
				log.error("[INFO] == number totalbc : ------------- ==");

				List<Map<String, Serializable>> listRpt = new ArrayList<Map<String, Serializable>>();
				DocumentModel docBC = ms.getBCByNumBC(numBC, session, ctx, service);
				Double totalht = (Double) doc.getPropertyValue("FF1:TotalHT");
				Long totalbc = (Long) docBC.getPropertyValue("bon_commande:subtotal");
				log.error("[INFO] == number totalbc : " + totalbc + " ==");

				if (docBC != null) {
					if (products.size() > 0) {

						List<Map<String, Serializable>> productsByBC = (List<Map<String, Serializable>>) docBC
								.getPropertyValue("bon_commande:produit");
						String statutProduit = "";
						for (int j = 0; j < products.size(); j++) {

							log.error("[INFO] ======== < Product " + j + " of number provider invoice : "
									+ numberInvoice + " > ========");
							Serializable qteInvoiceSbl = products.get(j).get("quantite");
							Serializable puInvoiceSbl = products.get(j).get("taux");
							if (qteInvoiceSbl != null && puInvoiceSbl != null) {
								Long qteInvoice = ((Double) qteInvoiceSbl).longValue();
								double puInvoice = Double.parseDouble(puInvoiceSbl.toString());

								Map<String, Serializable> complexField = new HashMap<>();
								complexField.put("nom", products.get(j).get("nom"));
								log.error("[INFO] === Product name : " + products.get(j).get("nom") + " ===");

								complexField.put("code_produit", products.get(j).get("code_produit"));
								log.error("[INFO] === Product code : " + products.get(j).get("code_produit") + " ===");

								complexField.put("quantite", qteInvoice);
								log.error("[INFO] === Quantity : " + qteInvoice + " ===");

								complexField.put("taux", puInvoice);
								log.error("[INFO] === Prix unitaire : " + puInvoice + " ===");

								complexField.put("montant", products.get(j).get("montant"));
								log.error("[INFO] === Total ligne : " + products.get(j).get("montant") + " ===");

								for (int i = 0; i < productsByBC.size(); i++) {

									Serializable qteBcSbl = productsByBC.get(i).get("quantite");
									Serializable puBcSbl = productsByBC.get(i).get("prixunitaire");
									if (qteBcSbl != null && puBcSbl != null) {
										Long qteBc = ((Long) qteBcSbl).longValue();
										double puBc = Double.parseDouble(puBcSbl.toString());

										log.error(
												"[INFO] === Product name : " + productsByBC.get(i).get("nom") + " ===");
										log.error("[INFO] === Product code : " + productsByBC.get(i).get("code_produit")
												+ " ===");
										log.error("[INFO] === Quantity : " + qteBc + " ===");
										log.error("[INFO] === Total ligne : " + productsByBC.get(i).get("total_ligne")
												+ " ===");
										log.error("[INFO] === Prix unitaire : " + puBc + " ===");

										if (products.get(j).get("code_produit")
												.equals(productsByBC.get(i).get("code_produit"))) {
											if (qteInvoice == qteBc && puInvoice == puBc) {
												listBc.add((Map<String, Serializable>) complexField);
												log.error("[INFO] === Product : " + productsByBC.get(i).get("nom")
														+ " added ===");
											} else if (qteInvoice != qteBc && puInvoice == puBc) {
												statutProduit = statutProduit + "BC:qte:"
														+ products.get(j).get("code_produit") + ",";
											} else if (qteInvoice == qteBc && puInvoice != puBc) {
												statutProduit = statutProduit + "BC:taux:"
														+ products.get(j).get("code_produit") + ",";
											} else {
												statutProduit = statutProduit + "BC:taux_qte:"
														+ products.get(j).get("code_produit") + ",";

											}
										}
									}
								}
								// DocumentModel docRpt = ms.getReceptionByNumBC(numBC, session, ctx, service);
								productsByRpt = (List<Map<String, Serializable>>) docBC
										.getPropertyValue("bon_commande:produit_valid");

								if (productsByRpt != null) {

									// productsByRpt = (List<Map<String, Serializable>>)
									// docRpt.getPropertyValue("reception:produit");
									// productsByRpt = (List<Map<String, Serializable>>)
									// docBC.getPropertyValue("bon_commande:produit_valid");

									for (int k = 0; k < productsByRpt.size(); k++) {
										Serializable qteRptSbl = productsByRpt.get(k).get("quantite_receptionner");
										Serializable puRptSbl = productsByRpt.get(k).get("prixunitaire");

										if (qteRptSbl != null && puRptSbl != null) {

											Long qteRpt = ((Long) qteRptSbl).longValue();
											double puRpt = Double.parseDouble(puRptSbl.toString());

											log.error("[INFO] === Product name : " + productsByBC.get(k).get("produit")
													+ " ===");
											log.error("[INFO] === Product code : "
													+ productsByBC.get(k).get("code_produit") + " ===");
											log.error("[INFO] === Quantity : " + qteRpt + " ===");
											log.error("[INFO] === Total ligne : "
													+ productsByBC.get(k).get("total_ligne") + " ===");
											log.error("[INFO] === Prix unitaire : " + puRpt + " ===");

											if (products.get(j).get("code_produit")
													.equals(productsByRpt.get(k).get("code_produit"))) {
												log.error(
														"[INFO] === Product Code of Purchase order and invoice are equal ===");
												if (qteInvoice == qteRpt) {
													listRpt.add((Map<String, Serializable>) complexField);
													log.error("[INFO] === Product : "
															+ productsByBC.get(k).get("produit") + " added===");
												} else {
													statutProduit = statutProduit + "RC:qte:"
															+ products.get(j).get("code_produit") + ",";

												}

											}
										}
									}
								}
								if (productsByBC.size() >= products.size() && productsByRpt.size() >= products.size()
										&& products.size() != 0 && products.size() == listBc.size()
										&& products.size() == listRpt.size()) {
									log.error("[INFO] *********** Size of list : " + listBc.size() + "***********");
									doc.setPropertyValue("FF1:etat_facture", "Validated");
									doc.setPropertyValue("FF1:statutProduit", statutProduit);

									session.saveDocument(doc);
									session.save();
								} else if (products.size() != listBc.size() && products.size() != 0) {
									log.error("[INFO] *********** Size of list : " + listBc.size() + "***********");
									doc.setPropertyValue("FF1:etat_facture", "Invalidated");
									doc.setPropertyValue("FF1:statutProduit", statutProduit);
									log.error("[INFO] *********** " + statutProduit + "***********");
									session.saveDocument(doc);
									session.save();
									log.error("[INFO] *********** must launch workfolw ***********");

								}
							}
						}
					} else {
						if (totalbc != null && totalht != null) {
							log.error("[INFO] ***********totalht " + totalht + "***********");
							log.error("[INFO] ***********totalbc " + totalbc + "***********");
							double totall = totalbc.doubleValue();
							if (totalht == totall) {
								log.error("[INFO] ***********totalhtdfdfd " + totall + "***********");
								log.error("[INFO] ***********totalbcdfdfdf " + totalht + "***********");
								doc.setPropertyValue("FF1:etat_facture", "Validated");
								session.saveDocument(doc);
								session.save();
							} else {
								log.error("[INFO] ***********totalhtdfdfdtttt " + totall + "***********");
								log.error("[INFO] ***********totalbcklmkmk " + totalht + "***********");
								doc.setPropertyValue("FF1:etat_facture", "Invalidated");
								session.saveDocument(doc);
								session.save();
							}
						}
					}
				} else {
					log.error("[INFO] == list of purchase order is empty ==");
					displayInfoOrException.sendMessage(ctx, service, AddMessage.ID,
							"#{messages['label.boncmd.num']}" + "  " + numBC);
				}
				listBc.clear();
			} else {
				log.error("[INFO] == The selected document is not of type provider invoice ==");
				displayInfoOrException.sendMessage(ctx, service, AddMessage.ID,
						"#{messages['label.boncommande.select']}");
			}
		}
		log.error(Constant.INFO_DASHES);
	}
}
