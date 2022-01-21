package org.nuxeo.project.sample;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import org.nuxeo.project.sample.beans.Conge;
import org.nuxeo.project.sample.beans.Holidays;

@Operation(id="Document.ModificationConfiguration", category="Document", label="Print Info")
public class ModificationConfiguration {
	 @Context
	  protected CoreSession session;
	  @Context
	  protected AutomationService service;
	  @Context
	  protected OperationContext ctx = new OperationContext(this.session);
	  private static final long serialVersionUID = 1L;
	  private static final Log log = LogFactory.getLog(ModificationConfiguration.class);
	  private static final String LOG_COMMENT_STATE_DELETED = " AND ecm:currentLifeCycleState != 'deleted'";
	  private Methode method = new Methode();
	  @OperationMethod
	  public void run() throws OperationException,ParseException
     {     
		DocumentModel doc = (DocumentModel)this.service.run(this.ctx, "Seam.GetCurrentDocument");
  		Integer Anne=method.getAnneeConfiguration();
  		//liste des congés
	    List<Conge> documents = method.getConge(session);
		List<String> ListMatr=new ArrayList<String>();
		//charger Liste des matricules des employés
		for(int f=0;f<documents.size();f++)
		 {
			String matriculeconge = documents.get(f).getMatricule();
			List<String> EmployesConge=method.EmployesConge(ListMatr, matriculeconge);
			if(EmployesConge==null)
			{
			  ListMatr.add(matriculeconge);	
			}   	
		 }
		for(int g=0;g<ListMatr.size();g++)
		{
			log.error(" Matricule Employe______________________________________________: "+ListMatr.get(g));
		    
			int Jourconge=0;
			for(int f=0;f<documents.size();f++)
			{
				
					if(ListMatr.get(g).equals(documents.get(f).getMatricule()))
				   {
					Calendar calendare_incremente=documents.get(f).getBeginDate();
					Date date_incremente = calendare_incremente.getTime();
				  	Calendar calendar_debut=documents.get(f).getBeginDate();
				  	Date date_debut = calendar_debut.getTime();
				  	Calendar calendar_fin=documents.get(f).getEndDate();
				  	Date date_fin = calendar_fin.getTime();
				  	List<Holidays> hs = method.getHolidays();
				  	List<Date> joursC = method.getdaysBetween(date_debut, date_fin);
					int numberReliquat = 0;
					for(int t=0; t<joursC.size(); t++) 
			        {
			        	if(joursC.get(t).getDay() == 0 || joursC.get(t).getDay() == 6) 
			        	{
			         		joursC.remove(t);	
			        	}
			        }
					for(int i=0; i<hs.size(); i++) 
			        {
						
					  	List<Date> joursF = method.getdaysBetween(hs.get(i).getBeginDate(), hs.get(i).getEndDate());
				        for(int j=0; j<joursF.size(); j++) 
				        {
				        	for(int k=0; k<joursC.size(); k++)
				        	{
				         		  if(joursF.get(j).compareTo(joursC.get(k)) ==0 ) 
				        		  {
				            		log.error("vvvvvvvvvvvvvvvvvvvv");
				        			numberReliquat++;  
				        		  }
				        	}	
				        }
				        
						
			        }
					 
				     System.out.println("JOURS CONGE RESTE : "+ (joursC.size() - numberReliquat));
				     Jourconge=Jourconge+(joursC.size() - numberReliquat);
				}
			}	
		 //modification reliquat d'un employe
	     method.setReliquatEmpoiye(ListMatr.get(g),Jourconge,session);	
		}
	  }
	  
		
	


}
