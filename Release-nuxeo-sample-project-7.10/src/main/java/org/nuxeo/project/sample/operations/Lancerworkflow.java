package org.nuxeo.project.sample.operations;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.view.facelets.Facelet;
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
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.project.sample.Methode;
import org.nuxeo.project.sample.services.DisplayInfoOrException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Operation(id = "Document.Lancerworkflow", category = "Document", label = "Traite Reception")
public class Lancerworkflow {
	public static final String ID = "Document.Lancerworkflow";

	@Context
	protected CoreSession session;

	private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();

	@Context
	protected AutomationService service;

	@Context
	protected OperationContext ctx = new OperationContext(session);

	private Methode method = new Methode();

	private final Log log = LogFactory.getLog(Lancerworkflow.class);

	boolean isGroupResponsibleDA = false;

	private Statement stmt = null;
	private Connection connection;
	private ResultSet resultSet = null;

	String categoryResponsable;

	boolean isGroupcategoryResponsable;
	CoreSession sessionInsideFolder;

//	Map<String, Serializable> usersMap;
//	List<Map<String, Serializable>> listUsersMap = new ArrayList<>();

	@SuppressWarnings("unchecked")
	@OperationMethod
	public void run(DocumentModel input) throws OperationException, ParseException {

		System.out.println("\n\n\n***************** dont run forest run******************");

		DocumentModel documentDA = (DocumentModel) service.run(ctx, "Context.FetchDocument");
		// //System.out.println("documentDA : " + documentDA);

//pour obtenir des données du document parent (dossier)

		fillLaunchDate(documentDA);

		DocumentModel folderDA = session.getDocument(documentDA.getParentRef());
		// //System.out.println("folderDA : " + folderDA);

		String nameworkflow_da1 = (String) folderDA.getPropertyValue("dc:config_workflow_da");
		// //System.out.println("nameworkflow_da1 " + nameworkflow_da1);

		Double totalht = (Double) documentDA.getPropertyValue("da:total_lignes");

		String responsibleDA = (String) documentDA.getPropertyValue("da:responsable");
		// System.out.println("responsibleDA : " + responsibleDA);

		// List<String> users = new ArrayList<String>();
		List<String> usersDepartment = new ArrayList<String>();
		List<String> usersCategory = new ArrayList<String>();

		// users.add("user:" + responsableparticipant1);
		ArrayList<String> work = new ArrayList<>();

//		DocumentModelList docss = session
//				.query("select * from Document where ecm:primaryType='config_workflow' and config_workflow:nom = '"
//						+ nameworkflow_da1
//						+ "' and ecm:isCheckedInVersion = 0 AND ecm:mixinType != 'HiddenInNavigation' AND ecm:currentLifeCycleState != 'deleted'");
//		////System.out.println("DFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF" + docss);

		// we got the session based on the folder and not the currentDocument
		sessionInsideFolder = documentDA.getCoreSession();
		// //System.out.println("sessionInsideFolder : " + sessionInsideFolder);

		List<DocumentModel> parents = sessionInsideFolder.getParentDocuments(documentDA.getRef());
		// ////System.out.println("parents : " + parents);

		DocumentModel workSpaceFolder = (DocumentModel) parents.get(2);
		// //System.out.println("workSpaceFolder : " + workSpaceFolder);
		//// System.out.println("workSpaceFolder . path : " +
		// workSpaceFolder.getPathAsString());

		String departmentName = (String) documentDA.getPropertyValue("da:departement_da");

		DocumentModelList departments = sessionInsideFolder
				.query("select * from Document where ecm:primaryType='departement' and ecm:path STARTSWITH '"
						+ workSpaceFolder.getPathAsString() + "'   and departement:departement_name='" + departmentName
						+ "' and ecm:isCheckedInVersion = 0 AND ecm:mixinType != 'HiddenInNavigation' AND ecm:currentLifeCycleState != 'deleted'");
		// System.out.println("departments : " + departments);

		String departmentResponsible = (String) departments.get(0).getPropertyValue("departement:responsable");
		// System.out.println("departmentResponsible : " + departmentResponsible);

		documentDA.setPropertyValue("da:responsable", departmentResponsible);

		sessionInsideFolder.saveDocument((DocumentModel) documentDA);
		sessionInsideFolder.save();

		responsibleDA = departmentResponsible;

		DocumentModelList foldersInsideWorkspace = sessionInsideFolder.getChildren(workSpaceFolder.getRef());

		DocumentModel folderChild;
		DocumentModel documentConfigWorkFlow = null;

		for (DocumentModel folder : foldersInsideWorkspace) {
			// //System.out.println("\n\n***********************loop on folders of the
			// workflow***************************");
			// ////System.out.println("folder : " + folder);

			String fldType = folder.getType();
			// ////System.out.println("fldType : " + fldType);

			if (fldType.equals("dossier_config_workflow")) {
				// //System.out.println("\n\nchildType is dossier_config_workflow : " +
				// fldType.equals("dossier_config_workflow"));

				folderChild = folder;
				// //System.out.println("folderChild : " + folderChild);

				DocumentModelList documentsConfigWorkFlow = sessionInsideFolder.getChildren(folderChild.getRef());
				// //System.out.println("documentsConfigWorkFlow : " + documentsConfigWorkFlow);

				// Iterator iterator = documentModelList1.iterator();
				if (!documentsConfigWorkFlow.isEmpty()) {
					// DocumentModel document = (DocumentModel) iterator.next();
					documentConfigWorkFlow = (DocumentModel) documentsConfigWorkFlow.get(0);
					// //System.out.println("documentConfigWorkFlow : " + documentConfigWorkFlow);
					// configDocumentId = document.getId();
					break;
				}
			}
		}

		String categorie = (String) documentDA.getPropertyValue("da:categorie");
		// //System.out.println("categorie : " + categorie);

		if (documentConfigWorkFlow != null) {
			// for (DocumentModel docc1 : docss) {
			String workflow1 = (String) documentConfigWorkFlow.getPropertyValue("config_workflow:workflow");
			// //System.out.println("config_workflow:workflow : " + workflow1);

			work.add(workflow1);
		}
		// }
		if (categorie != null) {
			if (documentConfigWorkFlow != null) {
				// for (DocumentModel docc : docss) {
				List<Map<String, Serializable>> categories = (List<Map<String, Serializable>>) documentConfigWorkFlow
						.getPropertyValue("config_workflow:categorie_prd");
				for (int j = 0; j < categories.size(); j++) {
					if (((Serializable) ((Map) categories.get(j)).get("categorie")).equals(categorie)) {

						categoryResponsable = (String) categories.get(j).get("personne");
						// //System.out.println("categoryResponsable : " + categoryResponsable);

						// users.add("user:" + ((Map) categories.get(j)).get("personne"));
						break;
					}
				}
				// }
			}
		}

//		if (docss != null)
//			for (DocumentModel docc2 : docss) {
//				List<Map<String, Serializable>> participants = (List<Map<String, Serializable>>) docc2
//						.getPropertyValue("config_workflow:participants_workflow");
//				for (Map<String, Serializable> participant : participants) {
//					////System.out.println("bonjouuuuuuuuuuuuuuuuuuur" + participant);
//					// users.add("user:" + participant.get("personne"));
//
//				}
//			}

		try {
			// //System.out.println("inside try 2 :");

			// create the jdbc connection
			stmt = getConnection().createStatement();
			// //System.out.println("after stmt : " + stmt);
			// retireiving a list of objects that contain groups and its users
			resultSet = stmt.executeQuery("select * from user2group");
			// //System.out.println("response resultSet : " + resultSet);

			while (resultSet.next()) {

				// //System.out.println("\n******** looping in every goups & users of the
				// request
				// ********");
				// //System.out.println("resultSet.next()");

				String userId = resultSet.getString("userId");
				// //System.out.println("userId : " + userId);

				String groupId = resultSet.getString("groupId");
				// //System.out.println("groupId : " + groupId);

				// //System.out.println("responsibleDA : " + responsibleDA);
				// checking if the sendEmail of the notif if a group
				if ((responsibleDA != null) && (responsibleDA.equals(groupId))) {
					// usersMap = new HashMap<>();
					// //System.out.println("responsibleDA equals " + groupId+ " then we gonna take
					// its user and push it to the list of users");

					// if the sendEmail is a group initilize isGroup
					isGroupResponsibleDA = true;

					// add the user of the group to the list
					// users.add("user:" + userId);

					usersDepartment.add("user:" + userId);

					// usersMap.put("user", "user:" + userId);
					// //System.out.println("usersMap : " + usersMap);

					// listUsersMap.add(usersMap);

				}
				// //System.out.println("usersDepartment : " + usersDepartment);
				// //System.out.println("listUsersMap : " + listUsersMap);
				// //System.out.println("users list : " + users);

				// //System.out.println("categoryResponsable : " + categoryResponsable);
				if ((categoryResponsable != "") && (categoryResponsable.equals(groupId))) {
					// usersMap = new HashMap<>();
					// //System.out.println("categoryResponsable equals " + groupId+ " then we gonna
					// take its user and push it to the list of users");

					// if the sendEmail is a group initilize isGroup
					isGroupcategoryResponsable = true;

					// add the user of the group to the list
					// users.add("user:" + userId);

					usersCategory.add("user:" + userId);

					// usersMap.put("2", "user:" + userId);
					// //System.out.println("usersMap : " + usersMap);

					// listUsersMap.add(usersMap);
				}
				// //System.out.println("usersCategory : " + usersCategory);
				// //System.out.println("listUsersMap : " + listUsersMap);
				// //System.out.println("users list : " + users);

			}

			// //System.out.println("listUsersMap before respDA : " + listUsersMap);
			if (!isGroupResponsibleDA && responsibleDA != null) {
				// usersMap = new HashMap<>();
				// //System.out.println("responsableparticipant1 is not a goup");
				// then the sendEmail contain a user and not a group, add it to the list
				// users.add("user:" + responsibleDA);

				usersDepartment.add("user:" + responsibleDA);

				// usersMap.put("1", "user:" + responsibleDA);
				// //System.out.println("usersMap : " + usersMap);

				// listUsersMap.add(usersMap);
				// //System.out.println("listUsersMap : " + listUsersMap);

				// //System.out.println("users list : " + users);

				// //System.out.println("usersDepartment : " + usersDepartment);
			}

			// //System.out.println("listUsersMap before Respcategory : " + listUsersMap);
			if (!isGroupcategoryResponsable && categoryResponsable != null) {
				// //System.out.println("listUsersMap before Respcategory1 : " + listUsersMap);
				// usersMap = new HashMap();
				// //System.out.println("listUsersMap before Respcategory2 : " + listUsersMap);
				// //System.out.println("categoryResponsable is not a goup");
				// then the sendEmail contain a user and not a group, add it to the list
				// users.add("user:" + categoryResponsable);

				usersCategory.add("user:" + categoryResponsable);

				// usersMap.put("2", "user:" + categoryResponsable);
				// //System.out.println("usersMap : " + usersMap);

				// //System.out.println("listUsersMap before Respcategory3 : " + listUsersMap);

				// listUsersMap.add(usersMap);
				// //System.out.println("listUsersMap4 : " + listUsersMap);

				// //System.out.println("users list : " + users);
				// //System.out.println("usersCategory : " + usersCategory);
			}

			// ////System.out.println("listUsersMap : " + listUsersMap);

		} catch (Exception e) {
			// //System.out.println(" exepc " + e);

		} finally {
			try {
				if (stmt != null) {
					// //System.out.println("closing the statement");
					stmt.close();
				}

			} catch (Exception e) {
				// //System.out.println("cant close stmt : " + e);
			}
			;
			try {

				if (resultSet != null) {
					// //System.out.println("closing the resultSet");
					resultSet.close();
				}

			} catch (Exception e) {
				// //System.out.println("cant close resultSet : " + e);
			}
			;
			try {
				if (connection != null) {
					// //System.out.println("closing the connection");
					connection.close();

				}
			} catch (Exception e) {
				// //System.out.println("cant close connection : " + e);
			}
			;

		}

//		List<String> users1 = new ArrayList<String>();
//
//		for (Map<String, Serializable> mapp : listUsersMap) {
//			////System.out.println("mapp : " + mapp);
//			////System.out.println("mapp.keySet() : " + mapp.keySet());
//			if (mapp.keySet().equals("1")) {
//				////System.out.println("u got it");
//			}
//		}

		List<String> users3 = new ArrayList<String>();
		// //System.out.println("after initilizing the users3 : " + users3);

		for (int k = 0; k < usersDepartment.size(); k++) {
			users3.add(usersDepartment.get(k));
		}
		// //System.out.println("users3 : " + users3);

		for (int k = 0; k < usersCategory.size(); k++) {
			users3.add(usersCategory.get(k));
		}
		// //System.out.println("users3 : " + users3);

//		List<String> users3 = new ArrayList<String>();
//		users3.add("group:goupA");

		// //System.out.println("l'ensemble des participants dans le workflow" + users);

		String s2 = work.get(0);
		if (s2.equals("SerialDocumentReview")) {
			String participant2 = "";
			boolean validerautomatique = false;
			Map<String, Object> params = new HashMap<>();
			params.put("id", s2);
			params.put("start", Boolean.valueOf(true));
			service.run(ctx, "Context.StartWorkflow", params);
			String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('"
					+ documentDA.getId() + "')";
			DocumentModelList workflows = session.query(QUERY_PAYEES);
			// //System.out.println("workflows : " + workflows);
			// //System.out.println("workflows.size() : " + workflows.size());

			// //System.out.println("((String[]) ((DocumentModel) workflows.get(0))"
//					+ "getPropertyValue(\"var_SerialDocumentReview:participants\")).length : "
//					+ ((String[]) ((DocumentModel) workflows.get(0))
//							.getPropertyValue("var_SerialDocumentReview:participants")).length);

			for (int k = 0; k < ((String[]) ((DocumentModel) workflows.get(0))
					.getPropertyValue("var_SerialDocumentReview:participants")).length; k++) {

				// //System.out.println("workflows.get(0)).getPropertyValue(\"var_SerialDocumentReview:participants\"))
				// : "
//						+ ((String[]) ((DocumentModel) workflows.get(0))
//								.getPropertyValue("var_SerialDocumentReview:participants"))[k]);
			}

			if (workflows.size() > 0)
				if (((String[]) ((DocumentModel) workflows.get(0))
						.getPropertyValue("var_SerialDocumentReview:participants")).length == 0) {

					((DocumentModel) workflows.get(0)).setPropertyValue("var_SerialDocumentReview:participants",
							(Serializable) users3);

					// documentDA.setPropertyValue("da:validator1", users3.get(0));
//					for (int c = 1; c <= users3.size(); c++) {
//						documentDA.setPropertyValue("da:validator" + c, users3.get(c-1));
//					}

					session.saveDocument((DocumentModel) workflows.get(0));
					session.save();

//					sessionInsideFolder.saveDocument(documentDA);
//					sessionInsideFolder.save();

//					System.out
//							.println("documentDA:validator" + 1 + " : " + documentDA.getPropertyValue("da:validator1"));

//					for (int c = 1; c <= users3.size(); c++) {
//						//System.out.println(
//								"documentDA:validator" + c + " : " + documentDA.getPropertyValue("da:validator" + c));
//					}

					for (int k = 0; k < ((String[]) ((DocumentModel) workflows.get(0))
							.getPropertyValue("var_SerialDocumentReview:participants")).length; k++) {

						// //System.out.println(
//								"workflows.get(0)).getPropertyValue(\"var_SerialDocumentReview:participants\")) : "
//										+ ((String[]) ((DocumentModel) workflows.get(0))
//												.getPropertyValue("var_SerialDocumentReview:participants"))[k]);
					}

				}
		} else {
			Map<String, Object> params = new HashMap<>();
			params.put("id", s2);
			params.put("start", Boolean.valueOf(true));
			service.run(ctx, "Context.StartWorkflow", params);
			String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('"
					+ documentDA.getId() + "')";
			DocumentModelList workflows = session.query(QUERY_PAYEES);
			if (workflows.size() > 0)
				if (((String[]) ((DocumentModel) workflows.get(0))
						.getPropertyValue("var_ParallelDocumentReview:participants")).length == 0) {
					((DocumentModel) workflows.get(0)).setPropertyValue("var_ParallelDocumentReview:participants",
							(Serializable) users3);
					session.saveDocument((DocumentModel) workflows.get(0));
					session.save();
				}
		}
	}

	public void fillLaunchDate(DocumentModel documentDA) throws ParseException {
	 

		// display time and date using toString()
		Date dNow = new Date(System.currentTimeMillis());
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 

		// LocalDate localDate = LocalDate.now();

		String w1LaunchDate = (String) documentDA.getPropertyValue("da:W_launch_date"); 

		if (w1LaunchDate == null) { 
			documentDA.setPropertyValue("da:W_launch_date", ft.format(dNow));
		}

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

/*
 * Location:
 * C:\Users\Kaoutar\Desktop\integration_retention08_04_2021\nuxeo-sample-project
 * -7.10\!\org\nuxeo\project\sample\operations\Lancerworkflow.class Java
 * compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */