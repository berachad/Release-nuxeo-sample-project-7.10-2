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

@Operation(id = updatedocument.ID, category = Constants.CAT_DOCUMENT, label = "Print Info")
public class updatedocument {

	@Context
	protected CoreSession session;

	@Context
	protected AutomationService service;

	private String currentUser;

	@Context
	protected OperationContext ctx = new OperationContext(session);

	private static final long serialVersionUID = 1L;

	public static final String ID = "Document.updatedocument";

	@OperationMethod
	public void run() throws OperationException {

		DocumentModel doc = (DocumentModel) service.run(ctx, "Seam.GetCurrentDocument");
		if (doc.getType().equals("FactureFournisseur")) {
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();

			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("FF1:participant");
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			data = s.split(";");

			if (s != null) {
				for (String d : data) {
					champ = d.split(":");
					if (champ[0].equals(currentUser)) {
						participants = participants + champ[0] + ": Validé;";
					} else {
						participants = participants + champ[0] + ":" + champ[1] + ";";
					}
				}

				if (data[data.length - 1].split(":")[0].equals(currentUser)) {

					doc.setPropertyValue("FF1:statutFacture", "Validé");
				}

			}
			doc.setPropertyValue("FF1:participant", (Serializable) participants);
			session.saveDocument(doc);
			session.save();
		} else if (doc.getType().equals("da")) {

			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();

			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("da:participant");
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			data = s.split(";");

			if (s != null) {
				for (String d : data) {
					champ = d.split(":");
					if (champ[0].equals(currentUser)) {
						participants = participants + champ[0] + ": Validé;";
					} else {
						participants = participants + champ[0] + ":" + champ[1] + ";";
					}
				}

				if (data[data.length - 1].split(":")[0].equals(currentUser)) {

					doc.setPropertyValue("da:statuworkflow", "Validé");
				}

			}
			doc.setPropertyValue("da:participant", (Serializable) participants);
			session.saveDocument(doc);
			session.save();
		} else if (doc.getType().equals("attest_travail")) {
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();

			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("attest_travail:participant");
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			data = s.split(";");

			if (s != null) {
				for (String d : data) {
					champ = d.split(":");
					if (champ[0].equals(currentUser)) {
						participants = participants + champ[0] + ": Validé;";
					} else {
						participants = participants + champ[0] + ":" + champ[1] + ";";
					}
				}

				if (data[data.length - 1].split(":")[0].equals(currentUser)) {

					doc.setPropertyValue("attest_travail:statuworkflow", "Validé");
				}

			}
			doc.setPropertyValue("attest_travail:participant", (Serializable) participants);
			session.saveDocument(doc);
			session.save();
		} else if (doc.getType().equals("Produit")) {
			System.out.println("Je suis la : Produit ");
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();

			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("Produit:participant");
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			data = s.split(";");

			if (s != null) {
				for (String d : data) {
					champ = d.split(":");
					if (champ[0].equals(currentUser)) {
						participants = participants + champ[0] + ": Validé;";
					} else {
						participants = participants + champ[0] + ":" + champ[1] + ";";
					}
				}

				if (data[data.length - 1].split(":")[0].equals(currentUser)) {

					doc.setPropertyValue("Produit:statuworkflow", "Validé");
				}

			}
			doc.setPropertyValue("Produit:participant", (Serializable) participants);
			session.saveDocument(doc);
			session.save();
		} else if (doc.getType().equals("fiche-fournisseur")) {
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();

			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("fiche-fournisseur:participant");
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			data = s.split(";");

			if (s != null) {
				for (String d : data) {
					champ = d.split(":");
					if (champ[0].equals(currentUser)) {
						participants = participants + champ[0] + ": Validé;";
					} else {
						participants = participants + champ[0] + ":" + champ[1] + ";";
					}
				}

				if (data[data.length - 1].split(":")[0].equals(currentUser)) {

					doc.setPropertyValue("fiche-fournisseur:statuworkflow", "Validé");
				}

			}
			doc.setPropertyValue("fiche-fournisseur:participant", (Serializable) participants);
			session.saveDocument(doc);
			session.save();
		} else if (doc.getType().equals("Contrat")) {
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();

			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("contrat:participant");
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			data = s.split(";");

			if (s != null) {
				for (String d : data) {
					champ = d.split(":");
					if (champ[0].equals(currentUser)) {
						participants = participants + champ[0] + ": Validé;";
					} else {
						participants = participants + champ[0] + ":" + champ[1] + ";";
					}
				}

				if (data[data.length - 1].split(":")[0].equals(currentUser)) {

					doc.setPropertyValue("contrat:statuworkflow", "Validé");
				}

			}
			doc.setPropertyValue("contrat:participant", (Serializable) participants);
			session.saveDocument(doc);
			session.save();
		} else if (doc.getType().equals("Demande_conge")) {
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();

			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("Demande_conge:participant");
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			data = s.split(";");

			if (s != null) {
				for (String d : data) {
					champ = d.split(":");
					if (champ[0].equals(currentUser)) {
						participants = participants + champ[0] + ": Validé;";
					} else {
						participants = participants + champ[0] + ":" + champ[1] + ";";
					}
				}

				if (data[data.length - 1].split(":")[0].equals(currentUser)) {

					doc.setPropertyValue("Demande_conge:statuworkflow", "Validé");
				}

			}
			doc.setPropertyValue("Demande_conge:participant", (Serializable) participants);
			session.saveDocument(doc);
			session.save();
		}
	}

}
