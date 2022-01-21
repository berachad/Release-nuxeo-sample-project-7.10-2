
package org.nuxeo.project.sample;

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

@Operation(id = "Document.SaisieAutomatiqueConge", category = "Document", label = "Print Info Congé")
public class SaisieAutomatiqueConge {
	private final Log log = LogFactory.getLog(SaisieAutomatiqueConge.class);
	private Connection connection;
	private Statement stmt = null;
	private ResultSet resultSet = null;

	@Context
	protected CoreSession session;

	@Context
	protected AutomationService service;

	@Context
	protected OperationContext ctx = new OperationContext(this.session);

	@Context
	protected transient UserManager userManager;

	@Context
	protected Principal currentUser;
	@Context
	protected transient CoreSession documentManager;
	private static final long serialVersionUID = 1L;
	public static final String ID = "Document.SaisieAutomatiqueConge";

	@OperationMethod
	public void run() throws OperationException, ParseException {
		this.log.error("=================== RUN =======================");
		DocumentModel doc = (DocumentModel) this.service.run(this.ctx, "Context.FetchDocument");

		FacesContext fContext = FacesContext.getCurrentInstance();
		UserProfileService ups = (UserProfileService) Framework.getService(UserProfileService.class);
		String userName = (String) getCurrentUserModel().getPropertyValue("user:username");
		this.log.error("=================== RUN =======================");
		DocumentModel profile = ups.getUserProfileDocument(userName, this.documentManager);
		this.log.error("=================== RUN =======================");
		String matricule = (String) profile.getPropertyValue("userprofile:matricule");
		this.log.error("=================== RUN =======================");
		System.out.println("username offff:" + profile.getPropertyValue("userprofile:matricule"));
		this.log.error("=================== RUN =======================");

		try {
			this.stmt = getConnection().createStatement();
			this.resultSet = this.stmt.executeQuery(
					"select employe.superviseur,employe.departement,employe.nom,employe.prenom,employe.Matricule from employe, hierarchy  where hierarchy.id = employe.id and hierarchy.name not like '%.trashed' and employe.Matricule='"
							+ matricule + "'");
			this.log.error("----------------ERRREEEE");

			if (this.resultSet.next()) {

				this.log.error("----------------ERRREEEE" + this.resultSet.getString("Matricule"));
				doc.setPropertyValue("Demande_conge:Matricule", this.resultSet.getString("Matricule"));
				doc.setPropertyValue("Demande_conge:Departement", this.resultSet.getString("Departement"));
				doc.setPropertyValue("Demande_conge:Superviseur", this.resultSet.getString("Superviseur"));
				doc.setPropertyValue("Demande_conge:Nom_employe",
						this.resultSet.getString("nom") + " " + this.resultSet.getString("prenom"));
				// this.session.saveDocument(doc);
				// this.session.save();
				this.log.error("----------------ERRREEEEeeeeeeeeeeeee");
			}

			this.connection.close();
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

	protected DocumentModel getCurrentUserModel() {
		return this.userManager.getUserModel(this.currentUser.getName());
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
			this.connection = DriverManager.getConnection(url, username, password);
		} catch (SAXException e1) {

			e1.printStackTrace();
		} catch (IOException e1) {

			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {

			e1.printStackTrace();
		} catch (XPathExpressionException e) {

			e.printStackTrace();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return this.connection;
	}
}

/*
 * Location: C:\Users\imane\Desktop\prblm sample\bon sample de monsieur
 * ahmed\!\org\nuxeo\project\sample\SaisieAutomatiqueConge.class Java compiler
 * version: 8 (52.0) JD-Core Version: 1.1.1
 */