package org.nuxeo.project.sample.operations;

import java.util.Calendar;
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
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.project.sample.services.DisplayInfoOrException;
import org.nuxeo.project.sample.services.MethodsShared;

@Operation(id="Document.CalculeRetention", category="Document", label="Traite Reception")
public class CalculeRetention
{
  public static final String ID = "Document.CalculeRetention";
  @Context
  protected CoreSession session;
  @Context
  protected AutomationService service;
  @Context
  protected OperationContext ctx = new OperationContext(this.session);
  private final Log log = LogFactory.getLog(CalculeRetention.class);
  private MethodsShared method = new MethodsShared();
  private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();
  
  @OperationMethod
  public DocumentModel run(DocumentModel input)
    throws OperationException
  {
    DocumentModel doc = (DocumentModel)this.service.run(this.ctx, "Context.FetchDocument");
    
    String nameClass = (String)doc.getPropertyValue("dc:classP");
    
    Calendar dateExpiration = null;
    
    DocumentModelList result = this.session.query("SELECT * FROM Document WHERE config:class = '" + nameClass + "'");
    
    DocumentModelList resultDoc = this.session.query("SELECT * FROM Document WHERE conf_r:doc_type = '" + doc
      .getType() + "' ");
    if (!result.isEmpty())
    {
      DocumentModel docClass = (DocumentModel)result.get(0);
      String uniteC = (String)docClass.getPropertyValue("dc:unite");
      Long dureeLong = (Long)docClass.getPropertyValue("dc:duree");
      String action = (String)docClass.getPropertyValue("dc:action");
      String evenment = (String)docClass.getPropertyValue("dc:evenment1");
      Long moisLong = (Long)docClass.getPropertyValue("config:mois");
      String prefixdate = null;
      String prefixdateExpiration = null;
      String prefixdate_fin = null;
      String prefix = null;
      
      int duree1 = dureeLong.intValue();
      if ((evenment.equals("Document date")) || (evenment.equals("date du document")))
      {
        Calendar datecont = (Calendar)doc.getPropertyValue("dc:created");
        
        Calendar datecontrat = Calendar.getInstance();
        datecontrat.setTime(datecont.getTime());
        if (datecontrat != null)
        {
          if ((uniteC.equals("Année")) || (uniteC.equals("Year"))) {
            datecontrat.add(1, duree1);
          } else if ((uniteC.equals("Mois")) || (uniteC.equals("Month"))) {
            datecontrat.add(2, duree1);
          } else if ((uniteC.equals("Jour")) || (uniteC.equals("Day"))) {
            datecontrat.add(5, duree1);
          }
          dateExpiration = datecontrat;
        }
      }
      else if (!resultDoc.isEmpty())
      {
        int duree = dureeLong.intValue();
        int mois = moisLong.intValue();
        
        prefix = (String)((DocumentModel)resultDoc.get(0)).getPropertyValue("conf_r:prefix");
        prefixdate = prefix + ":" + (String)((DocumentModel)resultDoc.get(0)).getPropertyValue("conf_r:date_doc");
        prefixdateExpiration = prefix + ":" + (String)((DocumentModel)resultDoc.get(0)).getPropertyValue("conf_r:date_exp");
        prefixdate_fin = prefix + ":" + (String)((DocumentModel)resultDoc.get(0)).getPropertyValue("conf_r:date_fin");
        if ((evenment.equals("Date d'expiration")) || (evenment.equals("Expiration date")))
        {
          Calendar dateexppp = (Calendar)doc.getPropertyValue(prefixdateExpiration);
          
          Calendar dateExpir = Calendar.getInstance();
          dateExpir.setTime(dateexppp.getTime());
          if (dateExpir != null)
          {
            if ((uniteC.equals("Année")) || (uniteC.equals("Year"))) {
              dateExpir.add(1, duree);
            } else if ((uniteC.equals("Mois")) || (uniteC.equals("Month"))) {
              dateExpir.add(2, duree);
            } else if ((uniteC.equals("Jour")) || (uniteC.equals("Day"))) {
              dateExpir.add(5, duree);
            }
            dateExpiration = dateExpir;
          }
        }
        else if ((evenment.equals("Fin de contrat")) || (evenment.equals("End of contract")))
        {
          Calendar datecontratfinold = (Calendar)doc.getPropertyValue(prefixdate_fin);
          
          Calendar datecontratfin = Calendar.getInstance();
          datecontratfin.setTime(datecontratfinold.getTime());
          if (datecontratfin != null)
          {
            if ((uniteC.equals("Année")) || (uniteC.equals("Year"))) {
              datecontratfin.add(1, duree);
            } else if ((uniteC.equals("Mois")) || (uniteC.equals("Month"))) {
              datecontratfin.add(2, duree);
            } else if ((uniteC.equals("Jour")) || (uniteC.equals("Day"))) {
              datecontratfin.add(5, duree);
            }
            dateExpiration = datecontratfin;
          }
        }
        else if ((evenment.equals("Fin d'année")) || (evenment.equals("End of year")))
        {
          Calendar datefactureold = (Calendar)doc.getPropertyValue(prefixdate);
          
          Calendar datefacture = Calendar.getInstance();
          datefacture.setTime(datefactureold.getTime());
          
          this.log.error("la end of datefacture son mois est" + (datefacture.get(2) + 1));
          if (datefacture != null) {
            if (mois >= datefacture.get(2) + 1)
            {
              Calendar EndYear = Calendar.getInstance();
              EndYear.setTime(datefacture.getTime());
              EndYear.set(2, mois - 1);
              EndYear.set(5, EndYear.getActualMaximum(5));
              if ((uniteC.equals("Année")) || (uniteC.equals("Year"))) {
                EndYear.add(1, duree);
              } else if ((uniteC.equals("Mois")) || (uniteC.equals("Month"))) {
                EndYear.add(2, duree);
              } else if ((uniteC.equals("Jour")) || (uniteC.equals("Day"))) {
                EndYear.add(5, duree);
              }
              dateExpiration = EndYear;
            }
            else
            {
              datefacture.add(1, 1);
              Calendar EndYear = Calendar.getInstance();
              EndYear.setTime(datefacture.getTime());
              EndYear.set(2, mois - 1);
              EndYear.set(5, EndYear.getActualMaximum(5));
              if ((uniteC.equals("Année")) || (uniteC.equals("Year"))) {
                EndYear.add(1, duree);
              } else if ((uniteC.equals("Mois")) || (uniteC.equals("Month"))) {
                EndYear.add(2, duree);
              } else if ((uniteC.equals("Jour")) || (uniteC.equals("Day"))) {
                EndYear.add(5, duree);
              }
              dateExpiration = EndYear;
            }
          }
        }
      }
      else
      {
        this.displayInfoOrException.sendMessage(this.ctx, this.service, "WebUI.AddMessage", "#{messages['label.retention.message']}");
      }
    }
    doc.setPropertyValue("dc:expire", dateExpiration);
    return doc;
  }
}
