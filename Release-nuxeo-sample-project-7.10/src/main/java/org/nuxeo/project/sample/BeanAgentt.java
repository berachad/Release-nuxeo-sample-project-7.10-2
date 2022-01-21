package org.nuxeo.project.sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.jsf.OperationHelper;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Scope(ScopeType.CONVERSATION)
@Name("BeanAgentt")
public class BeanAgentt implements Serializable {
	private static final Log log = LogFactory.getLog(BeanAgentt.class);
	private Connection connection;
	private CoreSession session;
	private List<String> agentts;
	private List<String> utilisateurs;
	private ArrayList<String> selectiondoc;
	private Statement stmt = null;
	private ResultSet resultSet = null;
	// private ResultSet resultSett = null;
	@In(create = true)
	protected transient NavigationContext navigationContext;
	@In(create = true)
	protected transient CoreSession documentManager;
	@In(create = true)
	protected transient AutomationService service;

	private Connection getConnection() {
		InputStream input = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("../datasources-config.xml");
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(input));
			XPath xpath = XPathFactory.newInstance().newXPath();
			String url = (String) xpath.compile("//component//extension//datasource/@url").evaluate(document,
					XPathConstants.STRING);
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

	public List<String> getAgentts(String id) {
		getConnection();
		log.error("id .......................");
		log.error(id);
		if (this.connection != null) {
			log.error("getAgents NOT NULL .......................");
			this.agentts = null;
			return getAgenttsMethode(id);
		}
		log.error("getAgents NULL .......................");
		return null;
	}

	public void setAgentts(List<String> agents) {
		this.agentts = agents;
	}

	public void setUtilisateurs(List<String> users) {
		this.utilisateurs = users;
	}

	public void setSelectiondoc(ArrayList<String> selectiondoc) {
		this.selectiondoc = selectiondoc;
	}

	public void runworkflow() {
		log.error("getIDFIN");
	}

	public List<String> getAgenttsMethode(String id) {
		String agent = null;
		List<String> names = new ArrayList();
		try {
			this.stmt = getConnection().createStatement();
			this.resultSet = this.stmt.executeQuery(
					"select label from workflows w where w.id in (select workflow_id from documents d where d.label like  '%"
							+ id + "') OR w.id not in (select workflow_id from documents)");
			while (this.resultSet.next()) {
				agent = this.resultSet.getString("label");
				log.error("getAgents **************************");
				log.error(agent);
				if (!StringUtils.isEmpty(agent)) {
					names.add(agent);
				}
			}
			// this.connection.close();
		} catch (SQLException localSQLException) {
			localSQLException.printStackTrace();
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

		return names;
	}

	public List<String> getUtilisateurs() {
		System.out.println("debut");
		String groupname = null;
		String username = null;
		ArrayList<String> users = new ArrayList();
		try {
			this.stmt = getConnection().createStatement();
			String query = "SELECT * FROM Document WHERE ecm:primaryType = 'Domain'  AND ecm:currentLifeCycleState != 'deleted'";
			DocumentModelList tenant = this.documentManager.query(query);
			System.out.println("tenantid");
			System.out.println(
					tenant.size() + "le tenant" + ((DocumentModel) tenant.get(0)).getPropertyValue("dc:title"));
			if (tenant.size() > 0) {
				this.resultSet = this.stmt.executeQuery("select \"username\" from users  where  \"tenantId\" = '"
						+ ((DocumentModel) tenant.get(0)).getPropertyValue("dc:title") + "'");
				while (this.resultSet.next()) {
					username = this.resultSet.getString("username");
					System.out.println("username   " + username);
					users.add(username);
				}
			}
			this.connection.close();
			return users;
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
		return null;
	}

	public ArrayList<String> getSelectiondoc() {
		this.selectiondoc = new ArrayList();
		ArrayList<DocumentModel> res = (ArrayList) OperationHelper.getDocumentListManager()
				.getWorkingList("CURRENT_SELECTION");
		for (int i = 0; i < res.size(); i++) {
			this.selectiondoc.add(((DocumentModel) res.get(i)).getId());
		}
		return this.selectiondoc;
	}
}
