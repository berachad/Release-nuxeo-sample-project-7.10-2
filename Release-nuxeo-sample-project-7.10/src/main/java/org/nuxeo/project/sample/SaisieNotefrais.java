package org.nuxeo.project.sample;

import static org.nuxeo.ecm.user.center.profile.UserProfileConstants.USER_PROFILE_MATRICULE_FIELD;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.user.center.profile.UserProfileService;
import org.nuxeo.runtime.api.Framework;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Operation(id = SaisieNotefrais.ID, category = Constants.CAT_DOCUMENT, label = "Print note frais")
public class SaisieNotefrais {
	private final Log log = LogFactory.getLog(SaisieNotefrais.class);
	private Connection connection;
	private Statement stmt = null;
	private ResultSet resultSet = null;
	@Context
	protected CoreSession session;
	@Context
	protected AutomationService service;
	@Context
	protected transient UserManager userManager;
	@Context
	protected Principal currentUser;
	@Context
	protected OperationContext ctx = new OperationContext(session);
	@Context
	protected transient CoreSession documentManager;
	private static final long serialVersionUID = 1L;
	public static final String ID = "Document.SaisieNotefrais";

	@OperationMethod
	public void run() throws OperationException, ParseException {
		DocumentModel doc = (DocumentModel) service.run(ctx, "Context.FetchDocument");
		if (doc.getPropertyValue("note_frais:numero_order") == null) {
			FacesContext fContext = FacesContext.getCurrentInstance();
			UserProfileService ups = Framework.getService(UserProfileService.class);
			String userName = (String) getCurrentUserModel().getPropertyValue("user:username");
			String usercompany = (String) getCurrentUserModel().getPropertyValue("user:company");
			DocumentModel profile = ups.getUserProfileDocument(userName, documentManager);
			String matricule = (String) profile.getPropertyValue(USER_PROFILE_MATRICULE_FIELD);
			try {
				stmt = getConnection().createStatement();
				resultSet = stmt.executeQuery(
						"select employe.superviseur,employe.Departement,employe.nom,employe.prenom,employe.Matricule from employe, hierarchy  where hierarchy.id = employe.id and hierarchy.name not like '%.trashed' and employe.Matricule='"
								+ matricule + "'");
				if (resultSet.next()) {
					doc.setPropertyValue("note_frais:nom", resultSet.getString("nom"));
					doc.setPropertyValue("note_frais:prenom", resultSet.getString("prenom"));
					doc.setPropertyValue("note_frais:departement", resultSet.getString("Departement"));
					session.saveDocument(doc);
					session.save();
				}
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (stmt != null) {
						System.out.println("closing the statement");
						stmt.close();
					}

				} catch (Exception e) {
					System.out.println("cant close stmt : " + e);
				}
				;
				try {

					if (resultSet != null) {
						System.out.println("closing the resultSet");
						resultSet.close();
					}

				} catch (Exception e) {
					System.out.println("cant close resultSet : " + e);
				}
				;
				try {
					if (connection != null) {
						System.out.println("closing the connection");
						connection.close();

					}
				} catch (Exception e) {
					System.out.println("cant close connection : " + e);
				}
				;
			}
		}
	}

	protected DocumentModel getCurrentUserModel() {
		return userManager.getUserModel(currentUser.getName());
	}

	private Connection getConnection() {
		InputStream input = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("../datasources-config.xml");
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(input));
			XPath xpath = XPathFactory.newInstance().newXPath();
			String url = (String) xpath.compile("//component//extension//datasource/@url").evaluate(document,
					XPathConstants.STRING);
			String driver = (String) xpath.compile("//component//extension//datasource/@driverClassName")
					.evaluate(document, XPathConstants.STRING);
			String username = (String) xpath.compile("//component//extension//datasource/@username").evaluate(document,
					XPathConstants.STRING);
			String password = (String) xpath.compile("//component//extension//datasource/@password").evaluate(document,
					XPathConstants.STRING);
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

}
