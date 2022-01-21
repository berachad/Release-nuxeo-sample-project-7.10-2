package org.nuxeo.project.sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.template.api.adapters.TemplateBasedDocument;

@Operation(id = PrintInfo.ID, category = Constants.CAT_DOCUMENT, label = "Print Info")
public class PrintInfo {

	@Context
	protected CoreSession session;

	@Context
	protected AutomationService service;

	@Context
	protected OperationContext ctx = new OperationContext(session);

	private static final long serialVersionUID = 1L;

	public static final String ID = "Document.PrintInfo";
	private List<String> templatesNames = new ArrayList();

	@OperationMethod
	public void run() throws OperationException {

		System.out.println("serial with ayoub");

		DocumentModel doc = (DocumentModel) service.run(ctx, "Seam.GetCurrentDocument");
		TemplateBasedDocument templateBasedDocument = (TemplateBasedDocument) doc
				.getAdapter(TemplateBasedDocument.class);
		if (templateBasedDocument == null) {
			System.out.println("le document DA ne contient pas les templates associ�");
		} else {
			this.templatesNames = templateBasedDocument.getTemplateNames();
		}
		if (doc.getType().equals("FactureFournisseur")) {
			final String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('"
					+ doc.getId() + "') and ecm:name like '%SerialDocumentReview%' ";

			String query = String.format(QUERY_PAYEES);
			DocumentModelList workflows = session.query(query);
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			String participants = "";
			String[] data;
			int n = workflows.size();

			String lines[] = ((String[]) workflows.get((n - 1))
					.getPropertyValue("var_SerialDocumentReview:participants"));
			if (lines != null) {
				for (String s : lines) {
					data = s.split(":");
					participants = participants + data[1] + ": En traitement;";
				}
			}
			doc.setPropertyValue("FF1:participant", (Serializable) participants);
			doc.setPropertyValue("FF1:statutFacture", "En traitement");
			session.saveDocument(doc);
			session.save();
		}

		else if (doc.getType().equals("da")) {
			String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('" + doc.getId()
					+ "') and ecm:name like '%SerialDocumentReview%' ";

			String query = String.format(QUERY_PAYEES, new Object[0]);
			DocumentModelList workflows = this.session.query(query);
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			String participants = "";

			int n = workflows.size();

			String[] lines = (String[]) ((DocumentModel) workflows.get(n - 1))
					.getPropertyValue("var_SerialDocumentReview:participants");
			if (lines != null) {
				for (String s : lines) {
					String[] data = s.split(":");
					if (templateBasedDocument != null) {
						for (String templatesName : this.templatesNames) {
							System.out.println("templatesName aa : " + templatesName);
							DocumentModel d = templateBasedDocument.getSourceTemplateDoc(templatesName);
							setPermission(this.session, d, data[1], "ReadWrite", true);
						}
					}
					participants = participants + data[1] + ": En traitement;";
					System.out.println("participants 1 : " + participants);
				}
			}
			doc.setPropertyValue("da:participant", participants);
			doc.setPropertyValue("da:statuworkflow", "En traitement");

			this.session.saveDocument(doc);
			this.session.save();
		}
		else if (doc.getType().equals("Produit")) {
			String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('" + doc.getId()
					+ "') and ecm:name like '%SerialDocumentReview%' ";

			String query = String.format(QUERY_PAYEES, new Object[0]);
			DocumentModelList workflows = this.session.query(query);
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			String participants = "";

			int n = workflows.size();

			String[] lines = (String[]) ((DocumentModel) workflows.get(n - 1))
					.getPropertyValue("var_SerialDocumentReview:participants");
			if (lines != null) {
				for (String s : lines) {
					String[] data = s.split(":");
					if (templateBasedDocument != null) {
						for (String templatesName : this.templatesNames) {
							System.out.println("templatesName aa : " + templatesName);
							DocumentModel d = templateBasedDocument.getSourceTemplateDoc(templatesName);
							setPermission(this.session, d, data[1], "ReadWrite", true);
						}
					}
					participants = participants + data[1] + ": En traitement;";
					System.out.println("participants 1 : " + participants);
				}
			}
			doc.setPropertyValue("Produit:participant", participants);
			doc.setPropertyValue("Produit:statuworkflow", "En traitement");

			this.session.saveDocument(doc);
			this.session.save();
		}

		else if (doc.getType().equals("fiche-fournisseur")) {
			String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('" + doc.getId()
					+ "') and ecm:name like '%SerialDocumentReview%' ";

			String query = String.format(QUERY_PAYEES, new Object[0]);
			DocumentModelList workflows = this.session.query(query);
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			String participants = "";

			int n = workflows.size();

			String[] lines = (String[]) ((DocumentModel) workflows.get(n - 1))
					.getPropertyValue("var_SerialDocumentReview:participants");
			if (lines != null) {
				for (String s : lines) {
					String[] data = s.split(":");
					if (templateBasedDocument != null) {
						for (String templatesName : this.templatesNames) {
							System.out.println("templatesName aa : " + templatesName);
							DocumentModel d = templateBasedDocument.getSourceTemplateDoc(templatesName);
							setPermission(this.session, d, data[1], "ReadWrite", true);
						}
					}
					participants = participants + data[1] + ": En traitement;";
					System.out.println("participants 1 : " + participants);
				}
			}
			doc.setPropertyValue("fiche-fournisseur:participant", participants);
			doc.setPropertyValue("fiche-fournisseur:statuworkflow", "En traitement");

			this.session.saveDocument(doc);
			this.session.save();
		}


		else if (doc.getType().equals("attest_travail")) {
			String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('" + doc.getId()
					+ "') and ecm:name like '%SerialDocumentReview%' ";

			String query = String.format(QUERY_PAYEES, new Object[0]);
			DocumentModelList workflows = this.session.query(query);
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			String participants = "";

			int n = workflows.size();

			String[] lines = (String[]) ((DocumentModel) workflows.get(n - 1))
					.getPropertyValue("var_SerialDocumentReview:participants");
			if (lines != null) {
				for (String s : lines) {
					String[] data = s.split(":");
					if (templateBasedDocument != null) {
						for (String templatesName : this.templatesNames) {
							System.out.println("templatesName aa : " + templatesName);
							DocumentModel d = templateBasedDocument.getSourceTemplateDoc(templatesName);
							setPermission(this.session, d, data[1], "ReadWrite", true);
						}
					}
					participants = participants + data[1] + ": En traitement;";
					System.out.println("participants 1 : " + participants);
				}
			}
			doc.setPropertyValue("attest_travail:participant", participants);
			doc.setPropertyValue("attest_travail:statuworkflow", "En traitement");

			this.session.saveDocument(doc);
			this.session.save();
		} else if (doc.getType().equals("Contrat")) {
			String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('" + doc.getId()
					+ "')";

			String query = String.format(QUERY_PAYEES, new Object[0]);
			DocumentModelList workflows = this.session.query(query);
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			String participants = "";

			int n = workflows.size();

			String[] lines = (String[]) ((DocumentModel) workflows.get(n - 1))
					.getPropertyValue("var_SerialDocumentReview:participants");
			if (lines != null) {
				for (String s : lines) {
					String[] data = s.split(":");
					if (templateBasedDocument != null) {
						for (String templatesName : this.templatesNames) {
							System.out.println("templatesName aa : " + templatesName);
							DocumentModel d = templateBasedDocument.getSourceTemplateDoc(templatesName);
							setPermission(this.session, d, data[1], "ReadWrite", true);
						}
					}
					participants = participants + data[1] + ": En traitement;";
					System.out.println("participants 1 : " + participants);
				}
			}
			doc.setPropertyValue("contrat:participant", participants);
			doc.setPropertyValue("contrat:statuworkflow", "En traitement");

			this.session.saveDocument(doc);
			this.session.save();
		} else if (doc.getType().equals("Demande_conge")) {
			String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('" + doc.getId()
					+ "') and ecm:name like '%SerialDocumentReview%' ";

			String query = String.format(QUERY_PAYEES, new Object[0]);
			DocumentModelList workflows = this.session.query(query);
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			String participants = "";

			int n = workflows.size();

			String[] lines = (String[]) ((DocumentModel) workflows.get(n - 1))
					.getPropertyValue("var_SerialDocumentReview:participants");
			if (lines != null) {
				for (String s : lines) {
					String[] data = s.split(":");
					if (templateBasedDocument != null) {
						for (String templatesName : this.templatesNames) {
							System.out.println("templatesName aa : " + templatesName);
							DocumentModel d = templateBasedDocument.getSourceTemplateDoc(templatesName);
							setPermission(this.session, d, data[1], "ReadWrite", true);
						}
					}
					participants = participants + data[1] + ": En traitement;";
					System.out.println("participants 1 : " + participants);
				}
			}
			doc.setPropertyValue("Demande_conge:participant", participants);
			doc.setPropertyValue("Demande_conge:statuworkflow", "En traitement");

			this.session.saveDocument(doc);
			this.session.save();
		}
	}

	protected void setPermission(DocumentModel doc, String name, String permission) {
		ACP acp = doc.getACP();
		ACL localACL = acp.getOrCreateACL("local");
		localACL.add(new ACE(name, permission, true));
		doc.setACP(acp, true);
	}

	protected void setPermission(CoreSession session, DocumentModel doc, String userName, String permission,
			boolean isGranted) {
		ACP acp = session.getACP(doc.getRef());
		ACL localACL = acp.getOrCreateACL("local");
		localACL.add(new ACE(userName, permission, isGranted));
		session.setACP(doc.getRef(), acp, true);
		session.save();
	}

}
