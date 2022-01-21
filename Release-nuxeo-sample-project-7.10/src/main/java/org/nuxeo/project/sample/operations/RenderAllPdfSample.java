package org.nuxeo.project.sample.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
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
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.template.api.TemplateProcessorService;
import org.nuxeo.template.api.adapters.TemplateBasedDocument;
import org.nuxeo.template.api.adapters.TemplateSourceDocument;

@Scope(ScopeType.CONVERSATION)
@Name("RenderAllPdfSample")
//Il permet de traiter la rendition de l'accusé de réception associé au facture et la conversion et ajouter le code à barre au accusé de réception et faire la concaténation de l'accusé de réception avec la facture
@Operation(id = RenderAllPdfSample.ID, category = Constants.CAT_DOCUMENT, label = "Concatenation Accusé de réception et facture")
public class RenderAllPdfSample {

	public static final String ID = "Document.RenderAllPdfSample";

	@Context
	protected AutomationService service;

	private CoreSession session;

	@Context
	protected OperationContext ctx = new OperationContext(session);

	@In(create = true)
	protected transient NavigationContext navigationContext;

	@In(create = true, required = false)
	protected transient CoreSession documentManager;

	CoreSession sessionInsideFolderContract;

	DocumentModel firstDocContract;

	@OperationMethod
	public void run() throws OperationException, IOException {

		System.out.println("\n\nrun the render the PDF");

		DocumentModelList DocsContract = (DocumentModelList) service.run(ctx, "Seam.GetSelectedDocuments");
		System.out.println("DocsContract : " + DocsContract);

		// getting the first folder to avoid making the request inside the loop
		firstDocContract = DocsContract.get(0);
		System.out.println("firstDocContract : " + firstDocContract);

		// we got the session based on the folder and not the currentDocument
		sessionInsideFolderContract = firstDocContract.getCoreSession();
		System.out.println("sessionInsideFolderContract : " + sessionInsideFolderContract);

		// currentTemplateBasedDocument.getSourceTemplates()

		List<SelectItem> items = new ArrayList<SelectItem>();

		List<TemplateSourceDocument> sources = getBindableTemplatesForDocument();
		System.out.println("sources : " + sources);

		for (TemplateSourceDocument sd : sources) {
			System.out.println("sd : " + sd);

			DocumentModel doc = sd.getAdaptedDoc();
			System.out.println("doc : " + doc);

			String label = doc.getTitle();
			System.out.println("label : " + label);

			if (doc.isVersion()) {
				label = label + " (V " + doc.getVersionLabel() + ")";
			}
			items.add(new SelectItem(doc.getId(), label));
		}

	}

	public List<TemplateSourceDocument> getBindableTemplatesForDocument() {

//		DocumentModel currentDocument = navigationContext.getCurrentDocument();
//		System.out.println("currentDocument : " + currentDocument);

//		String targetType = currentDocument.getType();
//		System.out.println("targetType : " + targetType);

		TemplateProcessorService tps = Framework.getLocalService(TemplateProcessorService.class);
		System.out.println("tps : " + tps);

		List<DocumentModel> templates = tps.getAvailableTemplateDocs(sessionInsideFolderContract, "Contrat");
		System.out.println("templates : " + templates);

		List<TemplateSourceDocument> result = new ArrayList<TemplateSourceDocument>();

		TemplateBasedDocument currentTBD = firstDocContract.getAdapter(TemplateBasedDocument.class);
		System.out.println("currentTBD : " + currentTBD);

		List<String> alreadyBoundTemplateNames = new ArrayList<String>();

		if (currentTBD != null) {
			alreadyBoundTemplateNames = currentTBD.getTemplateNames();
			System.out.println("alreadyBoundTemplateNames : " + alreadyBoundTemplateNames);
		}

		for (DocumentModel template : templates) {
			System.out.println("tata");
			TemplateSourceDocument templateTSD = template.getAdapter(TemplateSourceDocument.class);
			System.out.println("templateTSD : " + templateTSD);

			if (!alreadyBoundTemplateNames.contains(templateTSD.getName())) {
				result.add(templateTSD);
			}
		}

		return result;

	}

}
