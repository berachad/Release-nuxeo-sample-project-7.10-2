package org.nuxeo.project.sample.operations;

import java.text.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.project.sample.Methode;
import org.nuxeo.project.sample.services.DisplayInfoOrException;

@Operation(id="Document.ReinitialiserWorkflow", category="Document", label="ReinitialiserWorkflow")
public class ReinitialiserWorkflow
{
  public static final String ID = "Document.ReinitialiserWorkflow";
  @Context
  protected CoreSession session;
  @Context
  protected AutomationService service;
  @Context
  protected OperationContext ctx = new OperationContext(this.session);
  private Methode method = new Methode();
  private final Log log = LogFactory.getLog(Workflowachat.class);
  private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();
  
  @OperationMethod
  public void run(DocumentModel input)
    throws OperationException, ParseException
  {
    DocumentModel doc = (DocumentModel)this.service.run(this.ctx, "Seam.GetCurrentDocument");
    String statusdoc = doc.getCurrentLifeCycleState();
    if (statusdoc.equals("Approved"))
    {
      this.session.reinitLifeCycleState(doc.getRef());
      this.session.save();
    }
  }
}
