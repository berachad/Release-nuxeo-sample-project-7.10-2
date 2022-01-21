package org.nuxeo.project.sample;

import java.io.Serializable;
import java.security.Principal;
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
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.template.api.adapters.TemplateBasedDocument;

@Operation(id = ParallelWorkFLow.ID, category = Constants.CAT_DOCUMENT, label = "Print Info")
public class ParallelWorkFLow {

	@Context
	protected CoreSession session;

	@Context
	protected AutomationService service;

	@Context
	protected OperationContext ctx = new OperationContext(session);
	@Context
	protected Principal currentUser;

	private static final long serialVersionUID = 1L;
	protected transient UserManager userManager;
	private NuxeoPrincipal currentUs;
	public static final String ID = "Document.ParallelWorkFLow";
	private List<String> templatesNames = new ArrayList();

	@OperationMethod
	public void run() throws OperationException {
		// System.out.println("\n\n\nParallelWorkFLow \n");

		// System.out.println("\n\nrunning the printInfoParallel");

		DocumentModel doc = (DocumentModel) service.run(ctx, "Seam.GetCurrentDocument");
		// System.out.println("doc : doc : " + doc);

		TemplateBasedDocument templateBasedDocument = (TemplateBasedDocument) doc
				.getAdapter(TemplateBasedDocument.class);

		if (templateBasedDocument == null) {
			// System.out.println("le document attestation ne contient pas les templates
			// associ�");
		} else {
			this.templatesNames = templateBasedDocument.getTemplateNames();
		}

		if (doc.getType().equals("FactureFournisseur")) {
			final String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('"
					+ doc.getId()
					+ "') and ecm:name like '%ParallelDocumentReview%' and ecm:currentLifeCycleState = 'running' ";
			// System.out.println("QUERY_PAYEES : " + QUERY_PAYEES);

			// String query = String.format(QUERY_PAYEES);

			DocumentModelList workflows = session.query(QUERY_PAYEES);
			// System.out.println("parallel workflows : " + workflows);

			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			String participants = "";
			String[] data;

			// var_SerielDocumentReview
			String lines[] = ((String[]) workflows.get(0).getPropertyValue("var_ParallelDocumentReview:participants"));

			if (lines != null) {
				for (String s : lines) {
					data = s.split(":");
					participants = participants + data[1] + ": En traitement;";
				}
			}

			doc.setPropertyValue("FF1:participant", (Serializable) participants);
			doc.setPropertyValue("FF1:statutFacture", "En traitement");

			// System.out.println(currentLifeCycleState);
			session.saveDocument(doc);
			session.save();
		} else if (doc.getType().equals("da")) {

			System.out.println("doc.getCurrentLifeCycleState() : " + doc.getCurrentLifeCycleState());
			System.out.println("doc.getAllowedStateTransitions() : " + doc.getAllowedStateTransitions());

			// System.out.println("doc.getType().equals(\"da\") : " +
			// doc.getType().equals("da"));

			// System.out.println(" doc.getId() : " + doc.getId());

			String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('" + doc.getId()
					+ "') and ecm:name like '%ParallelDocumentReview%' and ecm:currentLifeCycleState = 'running' ";
			// System.out.println("QUERY_PAYEES : " + QUERY_PAYEES);

			// String query = String.format(QUERY_PAYEES, new Object[0]);

			DocumentModelList workflows = this.session.query(QUERY_PAYEES);
			// System.out.println("parallel workflows : " + workflows);
			// System.out.println("workflows.get(0) : " + workflows.get(0));

			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			String participants = "";

			String[] lines = (String[]) ((DocumentModel) workflows.get(0))
					.getPropertyValue("var_ParallelDocumentReview:participants");
			// System.out.println("lines : " + lines);

			if (lines != null) {
				for (String s : lines) {
					String[] data = s.split(":");
					if (templateBasedDocument != null) {
						for (String templatesName : this.templatesNames) {
							// System.out.println("templatesName aa : " + templatesName);
							DocumentModel d = templateBasedDocument.getSourceTemplateDoc(templatesName);
							setPermission(this.session, d, data[1], "ReadWrite", true);
						}
					}
					participants = participants + data[1] + ": En traitement;";
				}
			}
			doc.setPropertyValue("da:participant", participants);
			doc.setPropertyValue("da:statuworkflow", "En traitement");

			// System.out.println(currentLifeCycleState);
			this.session.saveDocument(doc);
			this.session.save();
		}
		if (doc.getType().equals("attest_travail")) {
			String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('" + doc.getId()
					+ "') and ecm:name like '%ParallelDocumentReview%' and ecm:currentLifeCycleState = 'running' ";

			// String query = String.format(QUERY_PAYEES, new Object[0]);
			DocumentModelList workflows = this.session.query(QUERY_PAYEES);
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			String participants = "";

			String[] lines = (String[]) ((DocumentModel) workflows.get(0))
					.getPropertyValue("var_ParallelDocumentReview:participants");
			if (lines != null) {
				for (String s : lines) {
					String[] data = s.split(":");
					if (templateBasedDocument != null) {
						for (String templatesName : this.templatesNames) {
							// System.out.println("templatesName aa : " + templatesName);
							DocumentModel d = templateBasedDocument.getSourceTemplateDoc(templatesName);
							setPermission(this.session, d, data[1], "ReadWrite", true);
						}
					}
					participants = participants + data[1] + ": En traitement;";
				}
			}
			doc.setPropertyValue("attest_travail:participant", participants);
			doc.setPropertyValue("attest_travail:statuworkflow", "En traitement");

			// System.out.println(currentLifeCycleState);
			this.session.saveDocument(doc);
			this.session.save();
		}
		if (doc.getType().equals("Demande_conge")) {
			String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('" + doc.getId()
					+ "') and ecm:name like '%ParallelDocumentReview%' and ecm:currentLifeCycleState = 'running' ";

			// String query = String.format(QUERY_PAYEES, new Object[0]);
			DocumentModelList workflows = this.session.query(QUERY_PAYEES);
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			String participants = "";

			String[] lines = (String[]) ((DocumentModel) workflows.get(0))
					.getPropertyValue("var_ParallelDocumentReview:participants");
			if (lines != null) {
				for (String s : lines) {
					String[] data = s.split(":");
					if (templateBasedDocument != null) {
						for (String templatesName : this.templatesNames) {
							// System.out.println("templatesName aa : " + templatesName);
							DocumentModel d = templateBasedDocument.getSourceTemplateDoc(templatesName);
							setPermission(this.session, d, data[1], "ReadWrite", true);
						}
					}
					participants = participants + data[1] + ": En traitement;";
				}
			}
			doc.setPropertyValue("Demande_conge:participant", participants);
			doc.setPropertyValue("Demande_conge:statuworkflow", "En traitement");

			// System.out.println(currentLifeCycleState);
			this.session.saveDocument(doc);
			this.session.save();
		}
		if (doc.getType().equals("Contrat")) {
			String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('" + doc.getId()
					+ "') and ecm:name like '%ParallelDocumentReview%' and ecm:currentLifeCycleState = 'running' ";

			// String query = String.format(QUERY_PAYEES, new Object[0]);

			DocumentModelList workflows = this.session.query(QUERY_PAYEES);
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			String participants = "";

			String[] lines = (String[]) ((DocumentModel) workflows.get(0))
					.getPropertyValue("var_ParallelDocumentReview:participants");
			if (lines != null) {
				for (String s : lines) {
					String[] data = s.split(":");
					if (templateBasedDocument != null) {
						for (String templatesName : this.templatesNames) {
							// System.out.println("templatesName aa : " + templatesName);
							DocumentModel d = templateBasedDocument.getSourceTemplateDoc(templatesName);
							setPermission(this.session, d, data[1], "ReadWrite", true);
						}
					}
					participants = participants + data[1] + ": En traitement;";
				}
			}
			doc.setPropertyValue("contrat:participant", participants);
			doc.setPropertyValue("contrat:statuworkflow", "En traitement");

			// System.out.println(currentLifeCycleState);
			this.session.saveDocument(doc);
			this.session.save();
		}
		if (doc.getType().equals("Produit")) {
			String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('" + doc.getId()
					+ "') and ecm:name like '%ParallelDocumentReview%' and ecm:currentLifeCycleState = 'running' ";

			// String query = String.format(QUERY_PAYEES, new Object[0]);

			DocumentModelList workflows = this.session.query(QUERY_PAYEES);
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			String participants = "";

			String[] lines = (String[]) ((DocumentModel) workflows.get(0))
					.getPropertyValue("var_ParallelDocumentReview:participants");
			if (lines != null) {
				for (String s : lines) {
					String[] data = s.split(":");
					if (templateBasedDocument != null) {
						for (String templatesName : this.templatesNames) {
							// System.out.println("templatesName aa : " + templatesName);
							DocumentModel d = templateBasedDocument.getSourceTemplateDoc(templatesName);
							setPermission(this.session, d, data[1], "ReadWrite", true);
						}
					}
					participants = participants + data[1] + ": En traitement;";
				}
			}
			doc.setPropertyValue("Produit:participant", participants);
			doc.setPropertyValue("Produit:statuworkflow", "En traitement");

			// System.out.println(currentLifeCycleState);
			this.session.saveDocument(doc);
			this.session.save();
		}
		if (doc.getType().equals("fiche-fournisseur")) {
			String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('" + doc.getId()
					+ "') and ecm:name like '%ParallelDocumentReview%' and ecm:currentLifeCycleState = 'running' ";

			// String query = String.format(QUERY_PAYEES, new Object[0]);

			DocumentModelList workflows = this.session.query(QUERY_PAYEES);
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			String participants = "";

			String[] lines = (String[]) ((DocumentModel) workflows.get(0))
					.getPropertyValue("var_ParallelDocumentReview:participants");
			if (lines != null) {
				for (String s : lines) {
					String[] data = s.split(":");
					if (templateBasedDocument != null) {
						for (String templatesName : this.templatesNames) {
							// System.out.println("templatesName aa : " + templatesName);
							DocumentModel d = templateBasedDocument.getSourceTemplateDoc(templatesName);
							setPermission(this.session, d, data[1], "ReadWrite", true);
						}
					}
					participants = participants + data[1] + ": En traitement;";
				}
			}
			doc.setPropertyValue("fiche-fournisseur:participant", participants);
			doc.setPropertyValue("fiche-fournisseur:statuworkflow", "En traitement");

			// System.out.println(currentLifeCycleState);
			this.session.saveDocument(doc);
			this.session.save();
		}
	}

	protected void setPermission(CoreSession session, DocumentModel doc, String userName, String permission,
			boolean isGranted) {
		ACP acp = session.getACP(doc.getRef());
		ACL localACL = acp.getOrCreateACL("local");
		localACL.add(new ACE(userName, permission, isGranted));
		session.setACP(doc.getRef(), acp, true);
		session.save();
	}

	protected DocumentModel getCurrentUserModel() {
		// System.out.println("hhhhhhuser" + this.currentUser.getName());
		// System.out.println("iiiiiiiuser" +
		// this.userManager.getUserModel(this.currentUser.getName()));

		return this.userManager.getUserModel(this.currentUser.getName());
	}
}
