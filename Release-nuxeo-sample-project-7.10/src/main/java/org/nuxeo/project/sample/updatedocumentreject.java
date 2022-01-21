package org.nuxeo.project.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.Serializable;

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
import javax.faces.context.FacesContext;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

import org.nuxeo.ecm.automation.core.util.DocumentHelper;

@Operation(id = updatedocumentreject.ID, category = Constants.CAT_DOCUMENT, label = "Print Info")
public class updatedocumentreject {

	@Context
	protected CoreSession session;

	@Context
	protected AutomationService service;

	private String currentUser;

	@Context
	protected OperationContext ctx = new OperationContext(session);

	private static final long serialVersionUID = 1L;

	public static final String ID = "Document.updatedocumentreject";

	@OperationMethod
	public void run() throws OperationException {

		DocumentModel doc = (DocumentModel) service.run(ctx, "Seam.GetCurrentDocument");
		if (doc.getType().equals("FactureFournisseur")) {
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();

			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("FF1:participant");

			data = s.split(";");

			if (s != null) {
				for (String d : data) {
					champ = d.split(":");
					if (champ[0].equals(currentUser)) {
						participants = participants + champ[0] + ": Rejeté;";
					} else {
						participants = participants + champ[0] + ":" + champ[1] + ";";
					}
				}
			}

			doc.setPropertyValue("FF1:participant", (Serializable) participants);
			doc.setPropertyValue("FF1:statutFacture", "Rejeté");
			session.saveDocument(doc);
			session.save();
		}

		else if (doc.getType().equals("da")) {

			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();
			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("da:participant");

			data = s.split(";");

			if (s != null) {
				for (String d : data) {
					champ = d.split(":");
					if (champ[0].equals(currentUser)) {
						participants = participants + champ[0] + ": Rejeté;";
					} else {
						participants = participants + champ[0] + ":" + champ[1] + ";";
					}
				}
			}
			doc.setPropertyValue("da:participant", (Serializable) participants);
			doc.setPropertyValue("da:statuworkflow", "Rejeté");
			session.saveDocument(doc);
			session.save();
		} else if (doc.getType().equals("attest_travail")) {
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();

			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("attest_travail:participant");

			data = s.split(";");

			if (s != null) {
				for (String d : data) {
					champ = d.split(":");
					if (champ[0].equals(currentUser)) {
						participants = participants + champ[0] + ": Rejeté;";
					} else {
						participants = participants + champ[0] + ":" + champ[1] + ";";
					}
				}
			}

			doc.setPropertyValue("attest_travail:participant", (Serializable) participants);
			doc.setPropertyValue("attest_travail:statuworkflow", "Rejeté");
			session.saveDocument(doc);
			session.save();
		} else if (doc.getType().equals("Contrat")) {
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();

			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("contrat:participant");

			data = s.split(";");

			if (s != null) {
				for (String d : data) {
					champ = d.split(":");
					if (champ[0].equals(currentUser)) {
						participants = participants + champ[0] + ": Rejeté;";
					} else {
						participants = participants + champ[0] + ":" + champ[1] + ";";
					}
				}
			}

			doc.setPropertyValue("contrat:participant", (Serializable) participants);
			doc.setPropertyValue("contrat:statuworkflow", "Rejeté");
			session.saveDocument(doc);
			session.save();
		} else if (doc.getType().equals("Demande_conge")) {
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();

			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("Demande_conge:participant");

			data = s.split(";");

			if (s != null) {
				for (String d : data) {
					champ = d.split(":");
					if (champ[0].equals(currentUser)) {
						participants = participants + champ[0] + ": Rejeté;";
					} else {
						participants = participants + champ[0] + ":" + champ[1] + ";";
					}
				}
			}

			doc.setPropertyValue("Demande_conge:participant", (Serializable) participants);
			doc.setPropertyValue("Demande_conge:statuworkflow", "Rejeté");
			session.saveDocument(doc);
			session.save();
		} else if (doc.getType().equals("Produit")) {
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();

			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("Produit:participant");
			data = s.split(";");

			if (s != null) {
				for (String d : data) {
					champ = d.split(":");
					if (champ[0].equals(currentUser)) {
						participants = participants + champ[0] + ": Rejeté;";
					} else {
						participants = participants + champ[0] + ":" + champ[1] + ";";
					}
				}
			}
			doc.setPropertyValue("Produit:participant", (Serializable) participants);
			doc.setPropertyValue("Produit:statuworkflow", "Rejeté");
			session.saveDocument(doc);
			session.save();
		} else if (doc.getType().equals("fiche-fournisseur")) {
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();
			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("fiche-fournisseur:participant");

			data = s.split(";");

			if (s != null) {
				for (String d : data) {
					champ = d.split(":");
					if (champ[0].equals(currentUser)) {
						participants = participants + champ[0] + ": Rejeté;";
					} else {
						participants = participants + champ[0] + ":" + champ[1] + ";";
					}
				}
			}

			doc.setPropertyValue("fiche-fournisseur:participant", (Serializable) participants);
			doc.setPropertyValue("fiche-fournisseur:statuworkflow", "Rejeté");
			session.saveDocument(doc);
			session.save();
		}
	}

}
