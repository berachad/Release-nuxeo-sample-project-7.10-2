package org.nuxeo.project.sample;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.project.sample.services.MethodsShared;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


@Scope(ScopeType.CONVERSATION)
@Name("HightcharFacture")
public class Highchartfacture implements Serializable{
	
	  private static final long serialVersionUID = 1L;
	  private static final Log log = LogFactory.getLog(Highchartfacture.class);
	  
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
	  
	  private Connection connection;
	  
	  List<Map<String, Object>> listOfFactureNbr;
	  List<Map<String, Object>> listOfFactureMax;
	  List<Map<String, Object>> listFactureEnProjet;
	  List<Map<String, Object>> listFatureEnTraitement;
	  List<Map<String, Object>> listFactureVAlide;
	  List<Map<String, Object>> listFactureRejete;
	
	  List<Map<String, Object>> listOfInvoicevalide;
	  List<Map<String, Object>> listOfInvoiceRejet;
	  List<Map<String, Object>> listOfInvoiceEntraitement;
	  List<Map<String, Object>> listOfInvoiceEnpj;
	  private List<Map<String, Object>> listOfWorkflowAvg1;
	  private List<Map<String, Object>> listOfWorkflowMax1;
	  private List<Map<String, Object>> listOfWorkflowMin1;
	  private String listOfFNbr;
	  private Integer valueInt;
	  private Integer valueLong;
	// Ajouter par soukaina
	  private List<Map<String, Object>> listOftotalparMois;
	  private List<Map<String, Object>> listOftotalparMoisAnnee;
	  private TreeMap<Double, String> Moiss= new TreeMap<Double, String>();
	  private List<Map<String, Object>> ListNbrEmplGenre;
	  private List<Map<String, Object>> Listreliquat;
	  private List<Map<String, Object>> ListNbrEmploye = new ArrayList<Map<String,Object>>(); 
	  private List<Map<String, Object>> maps = new ArrayList<Map<String,Object>>();
	  private List<Map<String, Object>> maps1 = new ArrayList<Map<String,Object>>();

	  private Calendar now = Calendar.getInstance();
	  private int mois = this.now.get(2) + 1;
	  private short annee = (short) this.now.get(1);
	  private MethodsShared method = new MethodsShared();
	  public Connection getConnection()
	  {
	    return this.connection;
	  }
	  
	  public void setConnection(Connection connection)
	  {
	    this.connection = connection;
	  }
	  
	  public String getListOfInvoicevalide()
	  {
		  if (this.connection != null)
		    {
			  String queryllistOfInvoicevalide = "SELECT statutfacture as name,count(f.id) as y FROM public.facturefournisseur f, hierarchy h where f.id=h.id and name not like '%.trashed' and statutfacture like '%Valid%' and date_part('year',datefacture)='" + getAnnee() + "'  group by statutfacture ";
			  try
			  {
				  QueryRunner queryRunner = new QueryRunner();
				  this.listOfInvoicevalide = ((List)queryRunner.query(connection, queryllistOfInvoicevalide, new MapListHandler()));
	      	  }
			  catch (SQLException se)
			  {
				  throw new RuntimeException("Couldn't query the database ", se);
			  }
			  finally {}
		    }
		  else
		  {
	      log.error("buttonJsonFACTURE NULL .......................");
		  }  
	    return new Gson().toJson(this.listOfInvoicevalide);
	  }
	  
	  public String getListOfInvoiceRejet()
	  {
		  if (this.connection != null)
		    {
		      log.error("buttonJsonFACTURE NOT NULL .......................");
		      
		       String querylistOfInvoiceRejet = "SELECT statutfacture as name,count(f.id) as y FROM public.facturefournisseur f, hierarchy h where f.id=h.id and name not like '%.trashed' and statutfacture like '%Rejet%' and date_part('year',datefacture)='" + getAnnee() + "' group by statutfacture";
		       this.listOfInvoiceRejet = null;
		      try
		      {
		        QueryRunner queryRunner = new QueryRunner();
		         this.listOfInvoiceRejet = ((List)queryRunner.query(connection, querylistOfInvoiceRejet, new MapListHandler()));
		      }
		      catch (SQLException se)
		      {
		       throw new RuntimeException("Couldn't query the database ", se);
		      }
		      finally {}
		    }
		    else
		    {
		      log.error("buttonJsonFACTURE NULL .......................");
		    }
	    return new Gson().toJson(this.listOfInvoiceRejet);
	  }
	  
	  public String getListOfInvoiceEntraitement()
	  {
		  
		  if (this.connection != null)
		    { 
			      String querylistOfInvoiceEntraitement = "SELECT statutfacture as name,count(f.id) as y FROM public.facturefournisseur f, hierarchy h where f.id=h.id and name not like '%.trashed' and statutfacture like '%En traitement%' and date_part('year',datefacture)='" + getAnnee() + "' group by statutfacture";
			      this.listOfInvoiceEntraitement = null;
			      try
			      {
			        QueryRunner queryRunner = new QueryRunner();
			         this.listOfInvoiceEntraitement = ((List)queryRunner.query(connection, querylistOfInvoiceEntraitement, new MapListHandler()));
			      }
			      catch (SQLException se)
			      {
			         throw new RuntimeException("Couldn't query the database ", se);
			      }
			      finally {}
		     }
		  else
			 {
			  log.error("buttonJsonFACTURE NULL .......................");
		     }
	    return new Gson().toJson(this.listOfInvoiceEntraitement);
	  }
	  
	  public String getListOfInvoiceEnpj()
	  {
	    buttonJson();
	    return new Gson().toJson(this.listOfInvoiceEnpj);
	  }
//	  public void buttonJsonFACTURE()
//	  {
//	    if (this.connection != null)
//	    {
//	      log.error("buttonJsonFACTURE NOT NULL .......................");
//	      Gson gson = new Gson();
//	      
//	       String querylistOfInvoiceRejet = "SELECT statutfacture as name,count(f.id) as y FROM public.facturefournisseur f, hierarchy h where f.id=h.id and name not like '%.trashed' and statutfacture like '%Rejet%' group by statutfacture";
//	      String querylistOfInvoiceEntraitement = "SELECT statutfacture as name,count(f.id) as y FROM public.facturefournisseur f, hierarchy h where f.id=h.id and name not like '%.trashed' and statutfacture like '%En traitement%' group by statutfacture";
//	      String querylistOfInvoiceEnpj = "SELECT statutfacture as name,count(f.id) as y FROM public.facturefournisseur f, hierarchy h where f.id=h.id and name not like '%.trashed' and statutfacture like '%Générée%' group by statutfacture";
//	      
//	      this.listOfInvoicevalide = null;
//	      this.listOfInvoiceRejet = null;
//	      this.listOfInvoiceEntraitement = null;
//	      this.listOfInvoiceEnpj = null;
//	      try
//	      {
//	        QueryRunner queryRunner = new QueryRunner();
//	        this.listOfInvoicevalide = ((List)queryRunner.query(connection, queryllistOfInvoicevalide, new MapListHandler()));
//	        this.listOfInvoiceRejet = ((List)queryRunner.query(connection, querylistOfInvoiceRejet, new MapListHandler()));
//	        this.listOfInvoiceEntraitement = ((List)queryRunner.query(connection, querylistOfInvoiceEntraitement, new MapListHandler()));
//	        this.listOfInvoiceEnpj = ((List)queryRunner.query(connection, querylistOfInvoiceEnpj, new MapListHandler()));
//	     
//	     }
//	      catch (SQLException se)
//	      {
//	       throw new RuntimeException("Couldn't query the database ", se);
//	      }
//	      finally {}
//	    }
//	    else
//	    {
//	      log.error("buttonJsonFACTURE NULL .......................");
//	    }
//	  } 

	   public void buttonJson()
	  {
	    log.error("buttonJson");
	    
	    Connection connection = null;
	    this.listOftotalparMois=null;
	    this.ListNbrEmploye=null;
	    try
	    {
	      String driverName = "org.postgresql.Driver";
	      Class.forName(driverName);
	      
	      InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("../datasources-config.xml");
	      try
	      {
	        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(input));
	        XPath xpath = XPathFactory.newInstance().newXPath();
	        String url = (String)xpath.compile("//component//extension//datasource/@url").evaluate(document, XPathConstants.STRING);
	        String username = (String)xpath.compile("//component//extension//datasource/@username").evaluate(document, XPathConstants.STRING);
	        String password = (String)xpath.compile("//component//extension//datasource/@password").evaluate(document, XPathConstants.STRING);
	        connection = DriverManager.getConnection(url, username, password);
	        log.error(connection.toString());
	      }
	      catch (FileNotFoundException e)
	      {
	        e.printStackTrace();
	      }
	      catch (ParserConfigurationException e)
	      {
	        e.printStackTrace();
	      }
	      catch (IOException e)
	      {
	        e.printStackTrace();
	      }
	    }
	    catch (ClassNotFoundException e)
	    {
	      System.err.println("Could not find the database driver");
	    }
	    catch (Exception e)
	    {
	      System.err.println("Could not connect to the database");
	    }
	   
	  }
	  	  
	  public void connection()
	  {
	    log.error("connexion ....................");
	    try
	    {
	      String driverName = "org.postgresql.Driver";
	      Class.forName(driverName);
	      
	      InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("../datasources-config.xml");
	      try
	      {
	        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(input));
	        
	        XPath xpath = XPathFactory.newInstance().newXPath();
	        String url = (String)xpath.compile("//component//extension//datasource/@url").evaluate(document, XPathConstants.STRING);
	       	        
	        String username = (String)xpath.compile("//component//extension//datasource/@username").evaluate(document, XPathConstants.STRING);
	        
	        String password = (String)xpath.compile("//component//extension//datasource/@password").evaluate(document, XPathConstants.STRING);
	        
	        this.connection = DriverManager.getConnection(url, username, password);
	        log.error(this.connection.toString());
	      }
	      catch (FileNotFoundException e)
	      {
	        e.printStackTrace();
	      }
	      catch (ParserConfigurationException e)
	      {
	        e.printStackTrace();
	      }
	      catch (IOException e)
	      {
	        e.printStackTrace();
	      }
	    }
	    catch (ClassNotFoundException e)
	    {
	      System.err.println("Could not find the database driver");
	    }
	    catch (Exception e)
	    {
	      System.err.println("Could not connect to the database");
	    }
	  }
	  
	  public short getAnnee()
	  {
	    return this.annee;
	  }
	  
	  public void setAnnee(short annee)
	  {
	    this.annee = annee;
	  }
	  
	  public String getListOfWorkflowsAvg()
	  {
		  if (this.connection != null)
		  {
		      log.error("listOfWorkflowAvg1  NOT NULL .......................");
		      try
		      {
		        QueryRunner queryRunner = new QueryRunner();
		        String queryWorkflowAvg1 = "SELECT AVG(date_part('day'::text, r.enddate - r.startdate )) as y, ti.actor as name from facturefournisseur d INNER JOIN hierarchy h2 on h2.id=d.id INNER JOIN task t  on t.targetdocumentid=d.id INNER JOIN task_info ti on ti.taskdocid = t.id INNER JOIN route_node r on r.nodeid = t.type where ti.status = 'validate' and h2.name not like '%.trashed' and date_part('year', r.enddate)='" + getAnnee() + "' GROUP BY ti.actor";
		        this.listOfWorkflowAvg1 = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryWorkflowAvg1, new MapListHandler()));
	            }
		      catch (SQLException se)
		      {
		      throw new RuntimeException("Couldn't query the database soukaina ........", se);
		      }
		      
		   }
		   else
		   {
		      log.error("listOfWorkflowAvg1 NULL .......................");
		   }
		  
		  return new Gson().toJson(this.listOfWorkflowAvg1);
	  }
	  
	  public String getListOfWorkflowsMax()
	  {
		  if (this.connection != null)
		  {
		      log.error("listOfWorkflowMax1  NOT NULL .......................");
		    try
		      {
		        QueryRunner queryRunner = new QueryRunner();
		        String queryWorkflowMax1 = "SELECT MAX(date_part('day'::text, r.enddate - r.startdate )) as y, ti.actor as name from facturefournisseur d INNER JOIN hierarchy h2 on h2.id=d.id INNER JOIN task t  on t.targetdocumentid=d.id INNER JOIN task_info ti on ti.taskdocid = t.id INNER JOIN route_node r on r.nodeid = t.type where ti.status = 'validate' and h2.name not like '%.trashed'  and date_part('year', r.enddate)='" + getAnnee() + "' GROUP BY ti.actor";
		        this.listOfWorkflowMax1 = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryWorkflowMax1, new MapListHandler()));  	     
		      }
		       catch (SQLException se)
		       {
		         throw new RuntimeException("Couldn't query the database soukaina ........", se);
		       }
		      
		   }
		   else
		   {
		      log.error("listOfWorkflowMax1 NULL .......................");
		   }
	    return new Gson().toJson(this.listOfWorkflowMax1);
	  }
	  
	  public String getListOfWorkflowsMin()
	  {
		  if (this.connection != null)
		  {
		      log.error("listOfWorkflowMin1  NOT NULL .......................");
		      try
		      {
		        QueryRunner queryRunner = new QueryRunner();
		        String queryWorkflowMin1 = "SELECT MIN(date_part('day'::text, r.enddate - r.startdate )) as y, ti.actor as name from facturefournisseur d INNER JOIN hierarchy h2 on h2.id=d.id INNER JOIN task t  on t.targetdocumentid=d.id INNER JOIN task_info ti on ti.taskdocid = t.id INNER JOIN route_node r on r.nodeid = t.type where ti.status = 'validate' and h2.name not like '%.trashed' and date_part('year',r.enddate)='"+getAnnee()+"' GROUP BY ti.actor";
		        this.listOfWorkflowMin1 = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryWorkflowMin1, new MapListHandler()));	      
		      }
		      catch (SQLException se)
		      {
		      throw new RuntimeException("Couldn't query the database soukaina ........", se);
		      }
		      
		   }
		   else
		   {
		      log.error("listOfWorkflowMin1 NULL .......................");
		   }
	    return new Gson().toJson(this.listOfWorkflowMin1);
	  }
	 
	  
	  public String getListOfFNbr()
	  {
		  if (this.connection != null)
		  {
		      log.error("listOfFactureNbr  NOT NULL .......................");
		      try
		      {
		        QueryRunner queryRunner = new QueryRunner();
		        String queryFactureNbr = "select count(id) as y, nomfournisseur as name from facturefournisseur where statutfacture like '%Valid%' and date_part('year',datefacture)='" + getAnnee() + "'  group by nomfournisseur";
                 this.listOfFactureNbr = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryFactureNbr, new MapListHandler()));
                
		      }
		      catch (SQLException se)
		      {
		      throw new RuntimeException("Couldn't query the database soukaina ........", se);
		      }
		      
		   }
		   else
		   {
		      log.error("listOfFactureNbr NULL .......................");
		   }
	    return new Gson().toJson(this.listOfFactureNbr);
	  }
	 
		    
}
