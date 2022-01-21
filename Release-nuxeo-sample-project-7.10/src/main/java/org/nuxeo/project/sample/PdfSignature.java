package org.nuxeo.project.sample;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.platform.ui.web.util.BaseURL;
import org.nuxeo.ecm.platform.web.common.locale.LocaleProvider;

import static org.nuxeo.ecm.core.api.event.CoreEventConstants.REPOSITORY_NAME;
import org.nuxeo.ecm.automation.core.util.DocumentHelper;
import org.nuxeo.runtime.api.Framework;


import java.util.Locale;
import org.jboss.seam.international.LocaleSelector;

@Scope(ScopeType.CONVERSATION)
@Name("PdfSign")
public class PdfSignature implements Serializable {

    private static final long serialVersionUID = 1L;

    @In(create = true)
    protected transient NavigationContext navigationContext;
	
	@In(create = true)
    protected transient CoreSession documentManager;

    private String path;
    private Blob blob;
	public String mimeTypeBlob;
    
//	public void buttonPDF(){
//		
//        DocumentModel currentDoc = navigationContext.getCurrentDocument();
//		DocumentModel doc = documentManager.getSourceDocument(currentDoc.getRef());
//
//
//		if(doc.getPropertyValue("file:content")!=null){
//			
//			blob = (Blob) doc.getPropertyValue("file:content");
//			
//			LocaleProvider localeProvider = Framework.getService(LocaleProvider.class);
//			String locale = localeProvider.getLocale(documentManager).toString();
//			if(StringUtils.isEmpty(locale)) {
//				if(locale.equals("fr") || locale.equals("fr_FR") || locale.equals("fr_CA")) {
//					String path = BaseURL.getBaseURL().toString()+"viewer/webS/viewerFr.html?file="+BaseURL.getBaseURL().toString()+"api/v1/id/"+doc.getId()+"/@blob/blobholder:0";
//					setPath(path);
//					System.out.println(path);
//				}else {
//					String path = BaseURL.getBaseURL().toString()+"viewer/webS/viewer.html?file="+BaseURL.getBaseURL().toString()+"api/v1/id/"+doc.getId()+"/@blob/blobholder:0";
//					setPath(path);
//					System.out.println(path);
//				}
//			}else {
//				String path = BaseURL.getBaseURL().toString()+"viewer/webS/viewer.html?file="+BaseURL.getBaseURL().toString()+"api/v1/id/"+doc.getId()+"/@blob/blobholder:0";
//				setPath(path);
//				System.out.println(path);
//			}
//			
//			
//		}
//	}
	public void buttonPDF()
	{
        DocumentModel currentDoc = navigationContext.getCurrentDocument();
		DocumentModel doc = documentManager.getSourceDocument(currentDoc.getRef());
		if(doc.getPropertyValue("file:content")!=null)
		{
			    blob = (Blob) doc.getPropertyValue("file:content");
				String path = BaseURL.getBaseURL().toString()+"viewer/webS/viewer.html?file="+BaseURL.getBaseURL().toString()+"api/v1/id/"+doc.getId()+"/@blob/blobholder:0";
				setPath(path);
				System.out.println(path);
		}
	 }
	
	

	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
