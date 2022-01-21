package org.nuxeo.project.sample.operations;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.nuxeo.project.sample.services.DisplayInfoOrException;
import org.nuxeo.project.sample.services.MethodsShared;

@Operation(id="Document.ChangeStateRendu", category="Document", label="Change color state compte rendu")
public class ChangeStateRendu
{
  public static final String ID = "Document.ChangeStateRendu";
  @Context
  protected CoreSession session;
  @Context
  protected AutomationService service;
  @Context
  protected OperationContext ctx = new OperationContext(this.session);
  private final Log log = LogFactory.getLog(ChangeStateRendu.class);
  private MethodsShared ms = new MethodsShared();
  private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();
  
  @OperationMethod
  public void run()
    throws OperationException
  {
    System.out.printf("--------------Retention compte rendu creation event ------------------------------- \n", new Object[0]);
    DocumentModel doc = (DocumentModel)this.service.run(this.ctx, "Context.FetchDocument");
    List<Map<String, Serializable>> actions = (List)doc.getPropertyValue("compte_rendu:actions");
    List<Map<String, Serializable>> actionsNew = new ArrayList();
    for (int k = 0; k < actions.size(); k++)
    {
      Map<String, Serializable> map = new HashMap();
      Serializable date_realisation = (Serializable)((Map)actions.get(k)).get("date_realisation");
      Serializable status = (String)((Map)actions.get(k)).get("statut");
      Calendar calendarInst = Calendar.getInstance();
      Calendar date2 = (Calendar)date_realisation;
      String color = "black";
      if (date2.compareTo(calendarInst) < 0) {
        if (status != null) {
          if (status.equals("Réalisée"))
          {
            System.out.printf("le document :" + doc.getName() + " est Réalisée", new Object[0]);
            color = "green";
          }
          else
          {
            System.out.printf("le document :" + doc.getName() + " est non réalisée", new Object[0]);
            color = "red";
          }
        }
      }
      map.put("description", (Serializable)((Map)actions.get(k)).get("description"));
      map.put("date_action", (Serializable)((Map)actions.get(k)).get("date_action"));
      map.put("responsable", (Serializable)((Map)actions.get(k)).get("responsable"));
      map.put("date_realisation", (Serializable)((Map)actions.get(k)).get("date_realisation"));
      map.put("statut", (Serializable)((Map)actions.get(k)).get("statut"));
      map.put("color_action", color);
      if (status != null) {
        if (status.equals("Réalisée")) {
          map.put("date_mod", calendarInst);
        }
      }
      System.out.printf("le document son action  :" + ((Map)actions.get(k)).get("description") + " est " + color + " color_action", new Object[0]);
      actionsNew.add(map);
    }
    doc.setPropertyValue("compte_rendu:actions", (Serializable)actionsNew);
  }
}
