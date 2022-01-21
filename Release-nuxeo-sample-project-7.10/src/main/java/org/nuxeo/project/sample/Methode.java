package org.nuxeo.project.sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.util.SystemOutLogger;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.project.sample.beans.Conge;
import org.nuxeo.project.sample.beans.Holidays;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.nuxeo.ecm.core.api.CoreSession;

public class Methode {
	  private static final Log log = LogFactory.getLog(ModificationConfiguration.class);
	  private static final String LOG_COMMENT_STATE_DELETED = " AND ecm:currentLifeCycleState != 'deleted'";
	  private Connection connection;
	  private Statement stmt = null;
	  private ResultSet resultSet = null;
	  public Integer getAnneeConfiguration()
		{
			Integer Annee=0;
			
			try {
				stmt = getConnection().createStatement();
				resultSet = stmt.executeQuery("select date_part('year', hs.datedebut) as year, hs.datefin, hs.intitule from nom_ferier hs, hierarchy h where h.id = hs.id and h.name not like '%.trashed'");
			
				if(resultSet.next())
			Annee=resultSet.getInt("year");
				System.out.println("annee de configuration"+Annee);
				
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return Annee;
	  }
	  public List<Date> getdaysBetween(Date date_debut, Date date_fin)
		{
			Calendar cd = Calendar.getInstance(); 
			cd.setTime(date_debut);
			cd.set(Calendar.HOUR, 12);
			cd.set(Calendar.SECOND, 00);
			cd.set(Calendar.MINUTE, 00);
			cd.set(Calendar.MILLISECOND, 00);
			date_debut = cd.getTime();

			Calendar cf = Calendar.getInstance(); 
			cf.setTime(date_fin);
			cf.set(Calendar.HOUR, 12);
			cf.set(Calendar.SECOND, 00);
			cf.set(Calendar.MINUTE, 00);
			cf.set(Calendar.MILLISECOND, 00);
			date_fin = cf.getTime();
	        List<Date> jours = new ArrayList<>();
	        Date dateIncrement = date_debut;
	        
	        while(dateIncrement.compareTo(date_fin) < 1) {
	        	log.error("********************* BETWEEN : " + dateIncrement + " ET : " + date_fin +" ***********************");
	        	jours.add(dateIncrement);
	    		Calendar c = Calendar.getInstance(); 
	    		c.setTime(dateIncrement);
	    		c.add(Calendar.DATE, 1);
	    		dateIncrement = c.getTime();
	    		log.error("ggggggggg : " + dateIncrement);

	    		log.error("***************************************************************");
	        }
	        for(int i=0; i<jours.size(); i++) {
	        	if(jours.get(i).getDay() == 0 || jours.get(i).getDay() == 6) {
	        		log.error("Les joursSuprimerrrrrrrrrrrr : " + jours.get(i));
	        		jours.remove(i);
	        		
	        	}
	        }
	        for(int i=0; i<jours.size(); i++) {
	    		log.error("----------------");
	    		log.error("le jour : " + i + " : " + jours.get(i));
	    		log.error("----------------");

	        	
	        }
	        
	        return jours;
		}
	  public List<Holidays> getHolidays() throws ParseException
		{
			List<Holidays> hs = new ArrayList();
			try {
				stmt = getConnection().createStatement();
				resultSet = stmt.executeQuery("select hs. datedebut, hs.datefin, hs.intitule from nom_ferier hs, hierarchy h where h.id = hs.id and h.name not like '%.trashed'");
			
				while(resultSet.next()) {
					
					hs.add(new Holidays(resultSet.getString("intitule"),resultSet.getDate("datedebut"),resultSet.getDate("datefin")));
				    System.out.println("Resultat : " + hs.get(0));
				}
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!hs.isEmpty()) {
		        System.out.println("bbbbb  1 : " + hs.get(0).getBeginDate());
		        System.out.println("bbbbb  2 : " + hs.get(0).getEndDate());


				return hs;
			}else {
				return null;
			}
		}
	  public List<Conge> getConge(CoreSession session) throws ParseException
		    {
		  	List<Conge> hs = new ArrayList();
			try {
				String query="SELECT * FROM Document WHERE ecm:mixinType != 'HiddenInNavigation' AND ecm:primaryType='Demande_conge' and  Demande_conge:statuworkflow='Validé' and  ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0 AND ecm:currentLifeCycleState != 'deleted'";
				DocumentModelList docs = session.query(query);
						for(int i=0;i<docs.size();i++) 
				{
					   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
					    Calendar calendar = (Calendar)docs.get(i).getProperty("Demande_conge","DateDebut");
					   Calendar calendar2 =(Calendar) docs.get(i).getProperty("Demande_conge","DateFin");
					    hs.add(new Conge(calendar,calendar2,docs.get(i).getProperty("Demande_conge","Matricule").toString()));
					   System.out.println("Resultat : " + hs.get(0));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!hs.isEmpty()) {
		        System.out.println("bbbbb  1 : " + hs.get(0).getBeginDate());
		        System.out.println("bbbbb  2 : " + hs.get(0).getEndDate());


				return hs;
			}else {
				return null;
			}
		}
	  public List<String>EmployesConge(List<String> Employes,String matricule)
   {
	   for (String mat : Employes) {
	       if (matricule.equals(mat)) 
	       {
	         return Employes;
	       }
	     }
	   return null;
   }
	  public void setReliquatEmpoiye(String matricule, int reliquat,CoreSession session) 
	  {
		String query = "SELECT * FROM Document WHERE ecm:parentId  IS NOT NULL AND ecm:primaryType = 'employe' AND employe:Matricule = '" + matricule + "' AND ecm:currentLifeCycleState != 'deleted'";		
	    DocumentModelList docs = session.query(query);
	    System.out.println("Matricule: "+matricule);
	    System.out.println("Siiiiiiiiiiiiiiiize : "+docs.size());
	    Long val = (Long)docs.get(0).getProperty("employe","reliquat");
	    Long nbrconge = (Long)docs.get(0).getProperty("employe","nb_conge");

	    int valeur =val.intValue();
	    log.error("----------------ERRREEEE");
	    docs.get(0).setPropertyValue("employe:reliquat", (nbrconge - reliquat));
	    log.error("----------------valeur"+valeur);
	    log.error("----------------reliquat"+reliquat);
	    log.error("----------------reliquat"+(nbrconge - reliquat));
	    session.saveDocument(docs.get(0));
	    session.save();
	}
      public Connection getConnection() 
	  {
			    InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("../datasources-config.xml");
			    try {
					Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(input));
					XPath xpath = XPathFactory.newInstance().newXPath();
			        String url = (String)xpath.compile("//component//extension//datasource/@url").evaluate(document, XPathConstants.STRING);
			        String driver = (String)xpath.compile("//component//extension//datasource/@driverClassName").evaluate(document, XPathConstants.STRING);
			        String username = (String)xpath.compile("//component//extension//datasource/@username").evaluate(document, XPathConstants.STRING);
			        String password = (String)xpath.compile("//component//extension//datasource/@password").evaluate(document, XPathConstants.STRING);
			        connection = DriverManager.getConnection(url, username, password);
				} catch (SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
				e1.printStackTrace();
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return connection;
	   }
      
      public List<String> getUsers( List<String> matricules) throws ParseException
    		{
    	  	List<String> listUsers=new ArrayList<String>();
    	  	for (int i = 0; i < matricules.size(); i++) 
    	  	{
    	  	  try 
	    	  {
	  				String parentId="";
	  				stmt = getConnection().createStatement();
	  				resultSet = stmt.executeQuery("select parentid from userprofile p, hierarchy h where h.id = p.id and h.name not like '%.trashed' and matricule='"+matricules.get(i)+"'");
	  				while(resultSet.next()) 
	  				{	
	  				 parentId=resultSet.getString("parentid");
	  				 listUsers.add(this.getUser(parentId));
	  				 System.out.println(this.getUser(parentId));
	  				}
	  			    connection.close();
    	  	  } catch (SQLException e) 
			  {
  				  // TODO Auto-generated catch block
  				  e.printStackTrace();
  			  }
    	  
    	  }
    	    return listUsers;
      }
     
      public String getUser(String parentid) throws ParseException
		{
    	  String name="";
		 try {
				
				stmt = getConnection().createStatement();
				resultSet = stmt.executeQuery("select name from hierarchy h  where h.id ='"+parentid+"' and h.primarytype='Workspace'");
				while(resultSet.next()) 
				{	
				 name=resultSet.getString("name");
				}
		    connection.close();
			} catch (SQLException e) 
		      {
				// TODO Auto-generated catch block
				e.printStackTrace();
			  }
		  return name;
		}
      public List<Map<String, Serializable>> liste_configuration_notefrais() throws ParseException
		{
			 List<Map<String, Serializable>> list =new ArrayList<Map<String, Serializable>>();
		 try {
				stmt = getConnection().createStatement();
				resultSet = stmt.executeQuery("select ls.departement,ls.agent_employe,ls.limite from hierarchy h ,liste_dep_employe ls  where h.id =ls.id and h.name not like '%.trashed' order by limite");
				while(resultSet.next()) 
				{
		         Map<String, Serializable> map1=new HashMap<>();
		         map1.put("departement", resultSet.getString("departement"));
		         map1.put("agent_employe", resultSet.getString("agent_employe"));
		         map1.put("limite", resultSet.getDouble("limite"));
				list.add(map1);
				}
				connection.close();
			} catch (SQLException e) 
		      {
		    	e.printStackTrace();
			  }
		  return list;
		}
      public List<Map<String, Serializable>> liste_configuration() throws ParseException
		{
			 List<Map<String, Serializable>> list =new ArrayList<Map<String, Serializable>>();
		 try {
				stmt = getConnection().createStatement();
				resultSet = stmt.executeQuery("select ls.responsable,ls.limite1,ls.limit2 from hierarchy h ,liste_workflow ls  where h.id =ls.id and h.name not like '%.trashed' order by limite1");
				while(resultSet.next()) 
				{
		         Map<String, Serializable> map1=new HashMap<>();
		         map1.put("responsable", resultSet.getString("responsable"));
		         map1.put("limite1", resultSet.getDouble("limite1"));
		         map1.put("limit2", resultSet.getDouble("limit2"));
				list.add(map1);
				}
				connection.close();
			} catch (SQLException e) 
		      {
		    	e.printStackTrace();
			  }
		  return list;
		}
      public List<String> liste_categorie() throws ParseException
	  {
			 List<String> list =new ArrayList<String>();
		 try {
				stmt = getConnection().createStatement();
				resultSet = stmt.executeQuery("select ls.personne,ls.categorie from hierarchy h ,liste_categorie ls  where h.id =ls.id and h.name not like '%.trashed'");
				while(resultSet.next()) 
				{
		  		list.add(resultSet.getString("categorie"));
				}
				connection.close();
			} catch (SQLException e) 
		      {
		    	e.printStackTrace();
			  }
		  return list;
      }

}
