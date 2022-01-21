package org.nuxeo.project.sample;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.In;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.user.center.profile.UserProfileService;
import org.nuxeo.project.sample.beans.Graphe;
import org.nuxeo.project.sample.complementary.Constant;
import org.nuxeo.runtime.api.Framework;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.user.User;
import javax.faces.context.FacesContext;

@ManagedBean(name = "BeanGraphes")
@SessionScoped
public class BeanGraphes {

	private static final Log log = LogFactory.getLog(BeanGraphes.class);

	private Connection connection;

	@In(create = true)
    protected transient NavigationContext navigationContext;
		private Statement stmt = null;
	private ResultSet resultSet = null;
	@In(create = true)
	protected CoreSession session;
	@In(create = true)
	protected Principal currentUser;
	@In(create = true, required = false)
	protected transient CoreSession documentManager;
	@In(create = true)
    protected transient UserManager userManager;	
	//retourne l'utilisateur courant
	  protected List<String> getGroups() 
	  {     
		UserProfileService ups = Framework.getService(UserProfileService.class);
		 Principal currentUser = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
		 NuxeoPrincipal nxUser=(NuxeoPrincipal)currentUser;
		 return   nxUser.getGroups();	          
	  }
	 
	public BeanGraphes() {
     
	}
	
	private Connection getConnection() {
	    InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("../datasources-config.xml");
	    try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(input));
			XPath xpath = XPathFactory.newInstance().newXPath();
	        String url = (String)xpath.compile("//component//extension//datasource/@url").evaluate(document, XPathConstants.STRING);
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

	public List<Graphe> getGraphes() 
	{
		getConnection();
		if (this.connection != null) {
		log.error("getGraphes NOT NULL .......................");

					
				return getGraphesMethode();
		} else {
		log.error("getGraphes NULL .......................");
			return null;
		}
	}

	public List<Graphe> getGraphesMethode() 
	{
		
		List<Graphe> Listederoulant = new ArrayList<Graphe>();
		List<String> getGroups = getGroups();
	     for (String group : getGroups) 
	     {
	    	 if(group.equals("Achat"))
	    	 {
	    		 Listederoulant.add(new Graphe("label.graphe.achat","graphes Achat"));	 
	    	 }
	    	 if(group.equals("Compta"))
	    	 {
	    		 Listederoulant.add(new Graphe("label.graphe.compta","graphes Comptabilité"));	 
	    	 }
	    	 if(group.equals("RH"))
	    	 {
	    		 Listederoulant.add(new Graphe("label.graphe.rh","graphes Ressource humaine"));	 
	    	 }
			
		 }
		
	  return Listederoulant;
	}		

}
