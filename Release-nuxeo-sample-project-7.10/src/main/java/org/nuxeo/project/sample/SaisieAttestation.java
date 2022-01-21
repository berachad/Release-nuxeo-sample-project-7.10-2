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

@Operation(id = SaisieAttestation.ID, category = Constants.CAT_DOCUMENT, label = "Print Info Congé")

public class SaisieAttestation {

	private final Log log = LogFactory.getLog(SaisieAttestation.class);
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
	public static final String ID = "Document.SaisieAttestation";

	@OperationMethod
	public void run() throws OperationException, ParseException {
		log.error("=================== RUN =======================");
		DocumentModel doc = (DocumentModel) service.run(ctx, "Context.FetchDocument");
		// session.createDocument(doc);
		FacesContext fContext = FacesContext.getCurrentInstance();
		UserProfileService ups = Framework.getService(UserProfileService.class);
		String userName = (String) getCurrentUserModel().getPropertyValue("user:username");
		String usercompany = (String) getCurrentUserModel().getPropertyValue("user:company");

		log.error("=================== RUN =======================");
		DocumentModel profile = ups.getUserProfileDocument(userName, documentManager);
		log.error("=================== RUN =======================");
		String matricule = (String) profile.getPropertyValue(USER_PROFILE_MATRICULE_FIELD);
		log.error("=================== RUN =======================");
		System.out.println("username offff:" + profile.getPropertyValue(USER_PROFILE_MATRICULE_FIELD));
		log.error("=================== RUN =======================");

		try {
			stmt = getConnection().createStatement();
			resultSet = stmt.executeQuery(
					"select employe.superviseur,employe.Departement,employe.nom,employe.prenom,employe.Matricule from employe, hierarchy  where hierarchy.id = employe.id and hierarchy.name not like '%.trashed' and employe.Matricule='"
							+ matricule + "'");
			log.error("----------------ERRREEEE");

			if (resultSet.next()) {
				log.error("----------------ERRREEEE" + resultSet.getString("Matricule"));
				doc.setPropertyValue("attest_travail:Matricule", resultSet.getString("Matricule"));
				doc.setPropertyValue("attest_travail:entreprise", usercompany);
				doc.setPropertyValue("attest_travail:nom_signataire", resultSet.getString("Superviseur"));
				doc.setPropertyValue("attest_travail:nom", resultSet.getString("nom"));
				doc.setPropertyValue("attest_travail:prenom", resultSet.getString("prenom"));
				session.saveDocument(doc);
				session.save();
				log.error("----------------ERRREEEEeeeeeeeeeeeee");

			}
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
