package org.nuxeo.project.sample.operations;

import java.security.Principal;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.international.LocaleSelector;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.user.center.profile.UserProfileConstants;
import org.nuxeo.ecm.user.center.profile.UserProfileService;
import org.nuxeo.project.sample.services.DisplayInfoOrException;
import org.nuxeo.project.sample.services.MethodsShared;
import org.nuxeo.runtime.api.Framework;

@Operation(id = afterWorkflowFinish.ID, category = Constants.CAT_DOCUMENT, label = "Create")
public class afterWorkflowFinish {
	public static final String ID = "Document.afterWorkflowFinish";

	@Context
	protected CoreSession session;
	@Context
	protected AutomationService service;
	@Context
	protected OperationContext ctx = new OperationContext(session);
	@Context
	protected LocaleSelector localeSelector;
	private final Log log = LogFactory.getLog(Createbc.class);
	@Context
	protected Principal currentUser;
	private MethodsShared ms = new MethodsShared();
	private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();

	@OperationMethod
	public void run() throws OperationException {
	System.out.println("---------------------- afterWorkflowFinish-----------------------");

}
}
