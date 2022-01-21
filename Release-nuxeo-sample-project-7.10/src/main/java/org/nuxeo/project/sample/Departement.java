package org.nuxeo.project.sample;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.event.ValueChangeEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;

@Scope(ScopeType.CONVERSATION)
@Name("Departement")
public class Departement
{
  private static final Log log = LogFactory.getLog(Departement.class);
  private CoreSession session;
  private List<String> departements;
  private List<String> categorie;
  private String responsable = "";
  private static final long serialVersionUID = 1L;
  @In(create=true)
  protected transient CoreSession coreSession;
  @In(create=true)
  protected transient NavigationContext navigationContext;
  @In(create=true)
  protected transient WebActions webActions;
  @In(create=true)
  protected transient CoreSession documentManager;
  @In(create=true)
  protected transient FacesMessages facesMessages;
  List<String> types;
  
  public List<String> getDepartements()
  {
    this.departements = new ArrayList();
    
    DocumentModel currentFolderDA = this.navigationContext.getCurrentDocument();
    
    List<DocumentModel> parents = this.documentManager.getParentDocuments(currentFolderDA.getRef());
    
    DocumentModel workSpaceFolder = (DocumentModel)parents.get(2);
    
    DocumentModelList foldersInsideWorkspace = this.documentManager.getChildren(workSpaceFolder.getRef());
    for (DocumentModel folder : foldersInsideWorkspace)
    {
      String fldType = folder.getType();
      if (fldType.equals("dossier_departement"))
      {
        DocumentModel folderChild = folder;
        
        DocumentModelList documentDepartment = this.documentManager.getChildren(folderChild.getRef());
        for (DocumentModel document : documentDepartment) {
          this.departements.add(document.getPropertyValue("departement:departement_name").toString());
        }
      }
    }
    return this.departements;
  }
  
  public void setDepartements(List<String> array)
  {
    this.departements = array;
  }
  
  public void doSomething(ValueChangeEvent event)
  {
    String name_responsable = "";
    Object newValue = event.getNewValue();
    
    DocumentModelList docss = this.documentManager.query("select * from Document where ecm:primaryType='departement' and departement:departement_name='" + newValue + "' and ecm:isCheckedInVersion = 0 AND ecm:mixinType != 'HiddenInNavigation' AND ecm:currentLifeCycleState != 'deleted'");
    if (docss != null) {
      name_responsable = ((DocumentModel)docss.get(0)).getPropertyValue("departement:responsable").toString();
    }
    setResponsable(name_responsable);
  }
  
  public String getResponsable()
  {
    return this.responsable;
  }
  
  public void setResponsable(String resp)
  {
    this.responsable = resp;
  }
  
  public void setCategorie(List<String> categorie)
  {
    this.categorie = categorie;
  }
  
  public List<String> getCategorie()
  {
    this.categorie = new ArrayList();
    
    DocumentModel folderDACurrentDocument = this.navigationContext.getCurrentDocument();
    
    List<DocumentModel> parents = this.documentManager.getParentDocuments(folderDACurrentDocument.getRef());
    
    DocumentModel workSpaceFolder = (DocumentModel)parents.get(2);
    
    DocumentModelList foldersInsideWorkspace = this.documentManager.getChildren(workSpaceFolder.getRef());
    for (DocumentModel folder : foldersInsideWorkspace)
    {
      String fldType = folder.getType();
      if (fldType.equals("dossier_config_workflow"))
      {
        DocumentModel folderChild = folder;
        
        DocumentModelList documentConfigWorkFlow = this.documentManager.getChildren(folderChild.getRef());
        if (!documentConfigWorkFlow.isEmpty())
        {
          DocumentModel document = (DocumentModel)documentConfigWorkFlow.get(0);
          
          List<Map<String, Serializable>> workFlowConfigCategories = (List)document.getPropertyValue("config_workflow:categorie_prd");
          for (Map<String, Serializable> category : workFlowConfigCategories) {
            this.categorie.add(((Serializable)category.get("categorie")).toString());
          }
          break;
        }
      }
    }
    return this.categorie;
  }
  
  public List<String> getTypesOfDocuments()
    throws ParseException
  {
    this.types = new ArrayList();
    
    DocumentModelList docs = this.documentManager.query("select * from Document where ecm:primaryType='DossierPrincipal' and ecm:isCheckedInVersion = 0 AND ecm:mixinType != 'HiddenInNavigation' AND ecm:currentLifeCycleState != 'deleted'");
    if (docs != null) {
      for (DocumentModel documentModel : docs)
      {
        List<Map<String, Serializable>> objectTypes = (List)documentModel.getPropertyValue("DossierPrincipal:Types_Documents");
        for (int i = 0; i < objectTypes.size(); i++) {
          this.types.add(
            ((Serializable)((Map)objectTypes.get(i)).get("Type"))
            .toString());
        }
      }
    }
    return this.types;
  }
}
