/*    */ package org.nuxeo.project.sample;

import java.io.Serializable;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.nuxeo.ecm.automation.core.util.DocumentHelper;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.util.BaseURL;

@Scope(ScopeType.CONVERSATION)
@Name("pdfManager")
public class PdfManagerBean implements Serializable {
	private static final long serialVersionUID = 1L;
	@In(create = true)
	protected transient NavigationContext navigationContext;
	@In(create = true)
	protected transient CoreSession documentManager;
	private String path;
	private Blob blob;
	public String mimeTypeBlob;
	private String maValeurConvert = "";

	public void buttonPDF() {
		DocumentModel currentDoc = this.navigationContext.getCurrentDocument();
		DocumentModel doc = this.documentManager.getSourceDocument(currentDoc.getRef());

		if (doc.getPropertyValue("file:content") != null) {
			this.blob = (Blob) doc.getPropertyValue("file:content");
			String nameConetnt = blob.getFilename();
			String[] table2 = nameConetnt.split("\\.");
			String maValeur = table2[table2.length - 1];
			Events.instance().raiseEvent("tasksCacheReset", new Object[0]);
			String[] extentionImage = { "jpg", "jpeg", "png", "gif", "tif", "tiff", "psd", "raw", "ai", "svg", "ps",
					"eps", "epsi", "epsf", "xcf", "bmp" };
			String[] extentionExcelWord = { "xlsx", "xlsm", "xlsb", "xltx", "xltm", "xls", "xlt", "xml", "xlam", "xla",
					"xlw", "docx", "docm", "dotx", "dotm" };
			maValeurConvert = maValeur.toLowerCase();
			if (maValeurConvert.equals("pdf")) {
				String path = BaseURL.getBaseURL().toString() + "viewer/web/viewer.html?file="
						+ BaseURL.getBaseURL().toString() + "api/v1/id/" + doc.getId() + "/@blob/blobholder:0";
				setPath(path); // Path PDF
				setBlob(blob);
				setMaValeurConvert(maValeurConvert);
			}
			Events.instance().raiseEvent("tasksCacheReset", new Object[0]);
 
			for (int i = 0; i < extentionImage.length; i++) {
				if (maValeurConvert.equals(extentionImage[i])) {
					String path = BaseURL.getBaseURL().toString() + "restAPI/preview/default/" + doc.getId()
							+ "/default/?blobPostProcessing=true";
					setPath(path); // Path IMAGE
					setBlob(blob);
					setMaValeurConvert(maValeurConvert);
				}
			}
			for (int i = 0; i < extentionExcelWord.length; i++) {
				if (maValeurConvert.equals(extentionExcelWord[i])) {
					String path = null;
					setPath(path);
					setBlob(blob);
					setMaValeurConvert(maValeurConvert);
				}
			}
			Events.instance().raiseEvent("tasksCacheReset", new Object[0]);
		}
	}

	public String getMaValeurConvert() {
		return maValeurConvert;
	}

	public void setMaValeurConvert(String maValeurConvert) {
		this.maValeurConvert = maValeurConvert;
	}

	public void removePDF() {
		DocumentModel currentDoc = this.navigationContext.getCurrentDocument();
		DocumentModel doc = this.documentManager.getSourceDocument(currentDoc.getRef());
		DocumentHelper.removeProperty(doc, "file:content");
		doc = this.documentManager.saveDocument(doc);
	}

	public Blob getBlob() {
		return blob;
	}

	public void setBlob(Blob blob) {
		this.blob = blob;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}

/*
 * Author : Berachad 
 * Affichage PDF. 
 * (52.0) JD-Core Version: 1.1.1
 */