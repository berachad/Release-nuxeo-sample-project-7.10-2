package org.nuxeo.project.sample.sage;

import java.io.IOException;
import java.io.Serializable;


import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
@Name("sage")
public class SageMB implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private final Log log = LogFactory.getLog(SageMB.class);

	public void connect() throws IOException {
		log.error(" === CONNECT TO SAGE === ");
		FacesContext.getCurrentInstance().getExternalContext().redirect("http://localhost:8083/e-Doc360-SG2/sageone_auth");
	}
}
