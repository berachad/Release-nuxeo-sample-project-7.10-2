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
@Name("HightcharRH")
public class HightcharRH implements Serializable{
	
	  private static final long serialVersionUID = 1L;
	  private static final Log log = LogFactory.getLog(HightcharRH.class);
	  
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
	  
	
	  public int getMois()
	  {
	    return this.mois;
	  }
	  
	  public TreeMap<Double, String> getMoiss()
	  {
		  Moiss.put(1.0, "Janvier");
		  Moiss.put(2.0, "Février");
		  Moiss.put(3.0, "Mars");
		  Moiss.put(4.0, "Avril");
		  Moiss.put(5.0, "Mai");
		  Moiss.put(6.0, "Juin");
		  Moiss.put(7.0, "Juillet");
		  Moiss.put(8.0, "Aôut");
		  Moiss.put(9.0, "Septembre");
		  Moiss.put(10.0, "Octobre");
		  Moiss.put(11.0, "Novembre");
		  Moiss.put(12.0, "Décembre");
		 
		 
	    return  (TreeMap<Double, String>) Moiss;
	  }
	  public void setMois(int mois)
	  {
	    this.mois = mois;
	  }
	  
	  public short getAnnee()
	  {
	    return this.annee;
	  }
	  
	  public void setAnnee(short annee)
	  {
	    this.annee = annee;
	  }
	  public Connection getConnection()
	  {
	    return this.connection;
	  }
	  
	  public void setConnection(Connection connection)
	  {
	    this.connection = connection;
	  }
	  
	  public String getNbrEmployeparmois()
	  {long valeur = method.getCountSalary(getAnnee());
		  for(Double key: getMoiss().keySet()) { 
			  long count = 0;
		  if (this.connection != null)
		    {
		      String queryNbremployeparmois = "select COUNT(*) from employe em where statut_employe='ACTIF' and date_part('year', date_embauche) ='" + getAnnee() +"' and date_part('month', date_embauche)  <='" + key +"'";
		      try
			    {
			      QueryRunner queryRunner = new QueryRunner();
			      this.ListNbrEmploye = ((List)queryRunner.query(this.connection, queryNbremployeparmois, new MapListHandler())); 
			      for(int j=0;j<ListNbrEmploye.size();j++) {
		    			for(Map.Entry<String, Object> entry1 : ListNbrEmploye.get(j).entrySet()) {
		    				if(entry1.getKey().equals("count") && entry1.getValue() != null) {
		    					count = (Long) entry1.getValue();
		    				}
		    			}
			      }
			      Map<String, Object> map1=new HashMap<>();
		    		map1.put("name",(Object) getMoiss().get(key));
		    		map1.put("y",(Object) (count + valeur));
		    		maps1.add(map1);
		    		System.out.println("List of maps : "+map1.toString()); 
			    }
			    catch (SQLException se)
			    {
			      se = se;
			     throw new RuntimeException("Couldn't query the database ........", se);
			    }
		     }
		    else
		    {
		      log.error("buttonJsonDA NULL .......................");
		    }
		  }
		  System.out.println("List of maps : "+ListNbrEmploye.size()); 
	    return new Gson().toJson(this.maps1);
	  }
	  public String getNbrReliquat()
	  {
	    buttonJsonDA();
	    return new Gson().toJson(this.Listreliquat);
	  }
	  public String getNbrEmplGenre()
	  {
	    buttonJsonDA();
	    return new Gson().toJson(this.ListNbrEmplGenre);
	  }
	  
	  public String getlistOftotalparMoisAnnee()
	  {DecimalFormat df2 = new DecimalFormat("#.##");
	    buttonJsonDA();
	    

	    int sal = 0;
	    
	    for(Double key: getMoiss().keySet()) { 
		sal = method.getSumSalary(getAnnee());
    	System.out.println("Key mois : " + key + " valeur : " + getMoiss().get(key));
    		for(int j=0;j<listOftotalparMois.size();j++) {
    			System.out.println("listOftotalparMois : " +listOftotalparMois.get(j).toString());
    			double result = 0.0D;
    			for(Map.Entry<String, Object> entry1 : listOftotalparMois.get(j).entrySet()) {
    				
    				if(entry1.getKey().equals("date_part")) {
    					System.out.println("DEPUIS BD"); 
    					System.out.println("KEY : " +entry1.getKey() + " VALUE : " +entry1.getValue());
    					 result = (Double) entry1.getValue();
    				}if(entry1.getKey().equals("tarif_negocie") && entry1.getValue() != null) {
    					 if(result <= key ) {
    						 if(entry1.getValue() instanceof Integer) {
    								sal=sal+ ((Integer) entry1.getValue()).intValue();
    							}else if(entry1.getValue() instanceof Long) {
    								Long salLong = new Long(sal);
    								salLong=salLong+ ((Long) entry1.getValue()).longValue();
    								sal = salLong.intValue();
    							} 
   	 	 	    		 System.out.println("saliare : "+sal); 
   	    			  }
    				}
    				
    			}
	    		 
	    	  
	    	}
    		Map<String, Object> map1=new HashMap<>();
    		map1.put("name",(Object) getMoiss().get(key));
    		map1.put("y",Long.valueOf(sal));
    		maps.add(map1);
    		System.out.println("List of maps : "+map1.toString()); 
    	//listOftotalparMoisAnnee.add(maps);
    	}	
	  
	  //  System.out.println(this.listOftotalparMoisAnnee.toString());
	    return new Gson().toJson(this.maps);
	  }
	  
	  
	  
	 public String   Month(Integer i)
	  {
	 String []weekday =new String[12];
	 weekday[1] =  "Janvier";
	 weekday[2] = "Février";
	 weekday[3] = "Mars";
	 weekday[4] = "Avril";
	 weekday[5] = "Mai";
	 weekday[6] = "Juin";
	 weekday[7] = "Juillet";
	 weekday[8] = "Aôut";
	 weekday[9] = "Septembre";
	 weekday[10] = "Octobre";
	 weekday[11] = "Novembre";
	 weekday[12] = "Décembre";
	 
	    return weekday[i];
	 }
	 
	  public void buttonJsonDA()
	  {this.listOftotalparMois=null;
	    this.ListNbrEmploye=null;
	    maps = new ArrayList<Map<String,Object>>();
		maps1 = new ArrayList<Map<String,Object>>();
		 if (this.connection != null)
	    {
	      log.error("buttonJsonDA NOT ");
	      System.out.println("le mois est : " + getMois());
	      System.out.println("L'annee est : " + getAnnee());
	      String queryTotalSalaire = "select  date_part('month', date_embauche) ,em.tarif_negocie from employe em where statut_employe='ACTIF' and date_part('year', date_embauche) ='" + getAnnee() +"'";
	      //String queryTotalSalaireAnnee = "select em.tarif_negocie,date_part('month', date_embauche) from employe em where statut_employe='ACTIF' and date_part('year', date_embauche)='" + getAnnee() +"'";
		    
	      String queryNbrEmplGenre = "select COUNT(*)as y,civilite as name from employe where statut_employe='ACTIF' and date_part('year', date_embauche)<='" + getAnnee() +"'  group by civilite";
	      String queryNbrreliqaut = "select COUNT(*) as y, reliquat as name from employe where statut_employe='ACTIF' and date_part('year', date_embauche)<='" + getAnnee() +"'  group by reliquat";
	      //String queryNbremployeparmois = "select COUNT(*) as y, date_part('month', date_embauche) as name from employe where statut_employe='ACTIF' and date_part('year', date_embauche)<='" + getAnnee() +"'  group by date_part('month', date_embauche)";
	      
	      
	      String queryNbremployeparmois = "select  date_part('month', date_embauche) ,COUNT(*) from employe em where statut_employe='ACTIF' and date_part('year', date_embauche) <='" + getAnnee() +"' group by date_part('month', date_embauche)";

	      //String queryTotalSalaire = "select sum(tarif_negocie)as y,date_part('month', date_embauche)as name from employe where statut_employe='ACTIF' and date_part('year', date_embauche)<='" + getAnnee() +"'  group by date_part('month', date_embauche)";
	      //String queryNbrEmplGenre = "select COUNT(*)as y,civilite as name from employe where statut_employe='ACTIF' and date_part('year', date_embauche)<='" + getAnnee() +"'  group by civilite";
	      //String queryNbrreliqaut = "select COUNT(*)as y,reliquat as name from employe where statut_employe='ACTIF' and date_part('year', date_embauche)<='" + getAnnee() +"'  group by reliquat";
	      //String queryNbremployeparmois = "select COUNT(*)as y,date_part('month', date_embauche) as name from employe where statut_employe='ACTIF' and date_part('year', date_embauche)<='" + getAnnee() +"'  group by date_part('month', date_embauche)";
	   
	      
	      try
		    {
		      QueryRunner queryRunner = new QueryRunner();
		      this.listOftotalparMois = ((List)queryRunner.query(this.connection, queryTotalSalaire, new MapListHandler())); 
		      System.out.println("size of queryTotalSalaire : " +listOftotalparMois.size());
		      //this.listOftotalparMoisAnne = ((List)queryRunner.query(this.connection, queryTotalSalaireAnnee, new MapListHandler())); 
		      this.ListNbrEmplGenre = ((List)queryRunner.query(this.connection, queryNbrEmplGenre, new MapListHandler())); 
		      this.Listreliquat = ((List)queryRunner.query(this.connection, queryNbrreliqaut, new MapListHandler())); 
		      this.ListNbrEmploye = ((List)queryRunner.query(this.connection, queryNbremployeparmois, new MapListHandler())); 
				 
		    }
		    catch (SQLException se)
		    {
		      se = se;
		     throw new RuntimeException("Couldn't query the database zakaria........", se);
		    }
	     }
	    else
	    {
	      log.error("buttonJsonDA NULL .......................");
	    }
	  }
	  
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
	  public void selectBusinessTravelLink(String event) throws IOException {
		
		// some stuff
		FacesContext.getCurrentInstance().getExternalContext().redirect("chartsRH2.xhtml");
		}  


}
