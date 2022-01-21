package org.nuxeo.project.sample.operations;

import java.io.PrintStream;
import java.security.Principal;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.international.LocaleSelector;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.user.center.profile.UserProfileService;
import org.nuxeo.project.sample.services.DisplayInfoOrException;
import org.nuxeo.project.sample.services.MethodsShared;
import org.nuxeo.runtime.api.Framework;

@Operation(id="Document.Changertemplate", category="Document", label="Create")
public class Changertemplate
{
  public static final String ID = "Document.Changertemplate";
  @Context
  protected CoreSession session;
  @Context
  protected AutomationService service;
  @Context
  protected OperationContext ctx = new OperationContext(this.session);
  @Context
  protected LocaleSelector localeSelector;
  private final Log log = LogFactory.getLog(Createbc.class);
  @Context
  protected Principal currentUser;
  private MethodsShared ms = new MethodsShared();
  private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();
  
  @OperationMethod
  public void run()
    throws OperationException
  {
    DocumentModel doc = (DocumentModel)this.service.run(this.ctx, "Context.FetchDocument");
    
    System.out.println("doc get langue : " + doc.getPropertyValue("dc:langue"));
    if (getLocale() != null)
    {
      doc.setPropertyValue("dc:langue", getLocale());
      this.session.saveDocument(doc);
      this.session.save();
    }
  }
  
  public String getLocale()
  {
    String lang = null;
    
    NuxeoPrincipal principal = (NuxeoPrincipal)this.session.getPrincipal();
    UserProfileService userProfileService = (UserProfileService)Framework.getService(UserProfileService.class);
    DocumentModel userProfileDoc = userProfileService.getUserProfileDocument(this.session);
    if (userProfileDoc.getPropertyValue("userprofile:locale") != null) {
      lang = (String)userProfileDoc.getPropertyValue("userprofile:locale");
    }
    if (StringUtils.isEmpty(lang))
    {
      String currentLocale = this.localeSelector.getLocaleString();
      lang = currentLocale;
    }
    return lang.toString();
  }
}
