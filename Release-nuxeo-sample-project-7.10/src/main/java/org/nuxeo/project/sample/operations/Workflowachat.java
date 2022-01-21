
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
import java.util.ArrayList;
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
import org.jboss.seam.annotations.In;
import org.jboss.seam.core.Events;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.project.sample.Methode;
import org.nuxeo.project.sample.services.DisplayInfoOrException;
import org.richfaces.application.FacesMessages;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Operation(id = "Document.Workflowachat", category = "Document", label = "Workflowachat")
public class Workflowachat {
	public static final String ID = "Document.Workflowachat";

	@Context
	protected CoreSession session;

	@Context
	protected AutomationService service;

	@Context
	protected OperationContext ctx = new OperationContext(this.session);

	private Methode method = new Methode();

	private final Log log = LogFactory.getLog(Workflowachat.class);

	private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();

	@Context
	protected ContentViewActions contentViewActions;

	private Connection connection;
	private Statement stmt = null;
	private ResultSet resultSet = null;
	String respoachat = null;
	@In(create = true)
	protected Map<String, String> messages;
	@In(create = true)
	protected transient NavigationContext navigationContext;
	@In(create = true, required = false)
	protected FacesMessages facesMessages;
	@In(create = true)
	protected transient CoreSession documentManager;

	CoreSession sessionInsideFolder;

	String configDocumentId;

	List<String> usersCategory = new ArrayList<String>();
	List<String> usersAchat = new ArrayList<String>();
	List<String> usersParts = new ArrayList<String>();

	@OperationMethod
	public void run(DocumentModel input) throws OperationException, ParseException {
		// System.out.println("\n\n\n\n ***************** excute the button
		// **********************");

		DocumentModel documentDA = (DocumentModel) this.service.run(this.ctx, "Seam.GetCurrentDocument");

		ACP acp = documentDA.getACP();
		System.out.println("acp : " + acp);

		// acp.

		// System.out.println("documentDA : " + documentDA);
		System.out.println("documentDA.getCurrentLifeCycleState() : " + documentDA.getCurrentLifeCycleState());
		System.out.println("documentDA.getAllowedStateTransitions() : " + documentDA.getAllowedStateTransitions());

		System.out.println("documentDA.getCurrentLifeCycleState() : " + documentDA.getCurrentLifeCycleState());

//		if (documentDA.getCurrentLifeCycleState() != "Approved") {
//			new Throwable("not approved yet");
//		}

		String categorie = (String) documentDA.getPropertyValue("da:categorie");
		// System.out.println("categorie : " + categorie);

		// we got the session based on the folder and not the currentDocument
		sessionInsideFolder = documentDA.getCoreSession();
		System.out.println("sessionInsideFolder : " + sessionInsideFolder);

		List<DocumentModel> parents = sessionInsideFolder.getParentDocuments(documentDA.getRef());
		System.out.println("parents : " + parents);
		System.out.println("parents.size() : " + parents.size());

		int b = 0;
		for (DocumentModel enfant : parents) {

			System.out.println("enfant : " + b + " : " + enfant);
		}

		DocumentModel workSpaceFolder = (DocumentModel) parents.get(2);
		// //System.out.println("workSpaceFolder : " + workSpaceFolder);

		DocumentModelList foldersInsideWorkspace = sessionInsideFolder.getChildren(workSpaceFolder.getRef());

		DocumentModel folderChild;

		String folderDepartmentId = null;

		DocumentModel documentConfigWorkFlow = null;

		String categoryResponsable = null;

		for (DocumentModel folder : foldersInsideWorkspace) {
			// //System.out.println("\n\n***********************loop on folders of the
			// workflow***************************");
			// //System.out.println("folder : " + folder);

			String fldType = folder.getType();
			// //System.out.println("fldType : " + fldType);

			if (fldType.equals("dossier_config_workflow")) {

				// System.out.println("\n\nchildType is dossier_config_workflow : " +
				// fldType.equals("dossier_config_workflow"));

				folderChild = folder;
				// //System.out.println("folderChild : " + folderChild);

				DocumentModelList documentsConfigWorkFlow = sessionInsideFolder.getChildren(folderChild.getRef());
				// //System.out.println("documentConfigWorkFlow : " + documentConfigWorkFlow);

				if (!documentsConfigWorkFlow.isEmpty()) {
					documentConfigWorkFlow = (DocumentModel) documentsConfigWorkFlow.get(0);
					// System.out.println("documentConfigWorkFlow : " + documentConfigWorkFlow);
					configDocumentId = documentConfigWorkFlow.getId();
				}
			}
			if (fldType.equals("dossier_departement")) {
				folderDepartmentId = folder.getId();
			}
		}

		if (categorie != null) {
			if (documentConfigWorkFlow != null) {
				// for (DocumentModel docc : docss) {
				List<Map<String, Serializable>> categories = (List<Map<String, Serializable>>) documentConfigWorkFlow
						.getPropertyValue("config_workflow:categorie_prd");
				for (int j = 0; j < categories.size(); j++) {
					if (((Serializable) ((Map) categories.get(j)).get("categorie")).equals(categorie)) {

						categoryResponsable = (String) categories.get(j).get("personne");
						// System.out.println("categoryResponsable of the 2nd workflow : " +
						// categoryResponsable);

						// users.add("user:" + ((Map) categories.get(j)).get("personne"));
						break;
					}
				}
				// }
			}
		}

		Double totalht = Double.valueOf(0.0D);
		String statusdoc = documentDA.getCurrentLifeCycleState();
		String responsableDA = (String) documentDA.getPropertyValue("da:responsable");
		// System.out.println("responsableDA : " + responsableDA);

		String dastatus = (String) documentDA.getPropertyValue("da:statut");
		List<Map<String, Serializable>> responsable_limite = new ArrayList<Map<String, Serializable>>();
		List<Map<String, Serializable>> total_devis = (List<Map<String, Serializable>>) documentDA.getPropertyValue("da:devis");
	      Serializable total_ligne;
		for (int k = 0; k < total_devis.size(); k++)     {
		      Serializable status_devis = (Serializable)((Map)total_devis.get(k)).get("statut_devis");
		      String status_devischar = status_devis.toString();

		      if (status_devischar.equals("Validé"))
		      {
		        total_ligne = (Serializable)((Map)total_devis.get(k)).get("total_ligne");
		        Double total_ligned = (Double)total_ligne;
		        totalht = Double.valueOf(totalht.doubleValue() + total_ligned.doubleValue());
		        System.out.println(" totalhtavant : " + totalht);
		      }
		    } 
		    documentDA.setPropertyValue("da:total_lignes", totalht);
		    System.out.println(" totalhtafter : " + totalht);
		    this.session.saveDocument(documentDA);
		responsable_limite = liste_configuration();
		// //System.out.println("unique msg to show where the error can be 4");

		ArrayList<String> users = new ArrayList();
		ArrayList<String> usersworkflow = new ArrayList();
		System.out.println(" totalht1 : " + totalht);
		if (folderDepartmentId != null) {

			DocumentModelList workflows1 = this.session.query(
					"SELECT * FROM Document WHERE ecm:primaryType='departement' and ecm:parentId='" + folderDepartmentId
							+ "' AND departement:departement_name LIKE '%chat%' and ecm:isCheckedInVersion = 0 AND ecm:mixinType != 'HiddenInNavigation' AND ecm:currentLifeCycleState != 'deleted'");

			if (workflows1 != null) {
				for (DocumentModel docc : workflows1) {
					// System.out.println("docc : " + docc);

					respoachat = (String) docc.getPropertyValue("departement:responsable");
					// System.out.println("respoachat : " + totalht);
					// //System.out.println(respoachat +"***************departement:responsable");
					// usersworkflow.add("user:" + respoachat);
					// System.out.println("usersworkflow : " + usersworkflow);
					// //System.out.println(respoachat +"***************departement:responsable");
				}

			} else {

				this.displayInfoOrException.sendMessage(this.ctx, this.service, "WebUI.AddMessage",
						"#{messages['label.permission.users']}");

			}
		}

		// usersworkflow.add("user:" + responsableparticipant1);

		String participant = null;
		boolean validerautomatique = false;
		// //System.out.println("le status " + statusdoc);
		String QUERY_P = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('" + documentDA.getId()
				+ "')";
		DocumentModelList workflowsbyid = this.session.query(QUERY_P);
		// //System.out.println("le workflow size :" + workflowsbyid.size());

		for (int i = 0; i < responsable_limite.size(); i++) {
			double limite1 = (double) responsable_limite.get(i).get("limite1");
			double limit2 = (double) responsable_limite.get(i).get("limit2");
			// System.out.println("totalht : " + totalht);
			// System.out.println("limite1 : " + limite1);
			// System.out.println("limit2 : " + limit2);

			if (((Double) ((Map) responsable_limite.get(i)).get("limite1")).doubleValue() <= totalht.doubleValue())
				if (((Double) ((Map) responsable_limite.get(i)).get("limit2")).doubleValue() > 0.0D
						&& ((Double) ((Map) responsable_limite.get(i)).get("limit2")).doubleValue() >= totalht
								.doubleValue()) {

					participant = ((Serializable) ((Map) responsable_limite.get(i)).get("responsable")).toString();

					// System.out.println("participant : " + participant);

					users = new ArrayList();
				}
		}

		boolean isGroupCategoryResponsable = false;
		boolean isGrouprespoachat = false;
		boolean isGroupparticipant = false;

//		//System.out.println("checking the participant  ");
//		if (participant == null) {
//			//System.out.println("participant is null");
//		} else if (participant == "") {
//			//System.out.println("participant is empty string");
//		}

		try {
			// System.out.println("inside try 17 :");

			// create the jdbc connection
			stmt = getConnection().createStatement();
			// System.out.println("after stmt : " + stmt);
			// retireiving a list of objects that contain groups and its users
			resultSet = stmt.executeQuery("select * from user2group");
			// System.out.println("response resultSet : " + resultSet);

			while (resultSet.next()) {
				// System.out.println("\n******** looping in every goups & users of the request
				// ********");
				// System.out.println("resultSet1.next()");

				String userId = resultSet.getString("userId");
				// System.out.println("userId : " + userId);

				String groupId = resultSet.getString("groupId");
				// System.out.println("groupId : " + groupId);

				// System.out.println("categoryResponsable : " + categoryResponsable);
				// checking if the sendEmail of the notif if a group
				if ((categoryResponsable != null) && (categoryResponsable.equals(groupId))) {
					// System.out.println("categoryResponsable equals " + groupId + " then we gonna
					// take its user and push it to the list of users");

					// if the sendEmail is a group initilize isGroup
					isGroupCategoryResponsable = true;

					// add the user of the group to the list
					users.add(userId);

					usersCategory.add(userId);
				}
				// System.out.println("usersCategory : " + usersCategory);
				// System.out.println("users list : " + users);

				// System.out.println("respochat : " + respoachat);
				if ((respoachat != null) && (respoachat.equals(groupId))) {
					// System.out.println("String respoachat equals " + groupId + " then we gonna
					// take its user and push it to the list of users");

					// if the sendEmail is a group initilize isGroup
					isGrouprespoachat = true;

					// add the user of the group to the list
					users.add(userId);

					usersAchat.add(userId);
				}
				// System.out.println("usersAchat : " + usersAchat);
				// System.out.println("users list : " + users);

				// System.out.println("participant : " + participant);
				if ((participant != null) && (participant.equals(groupId))) {
					// System.out.println("String participant equals " + groupId + " then we gonna
					// take its user and push it to the list of users");

					// if the sendEmail is a group initilize isGroup
					isGroupparticipant = true;

					// add the user of the group to the list
					users.add(userId);

					usersParts.add(userId);
				}
				// System.out.println("usersParts : " + usersParts);
				// System.out.println("users list : " + users);

			}

			if ((!isGroupCategoryResponsable) && (categoryResponsable != null)) {
				// System.out.println("\n\ncategoryResponsable is not a goup");
				// then the sendEmail contain a user and not a group, add it to the list
				users.add(categoryResponsable);
				// System.out.println("users list : " + users);

				usersCategory.add(categoryResponsable);
				// System.out.println("usersCategory : " + usersCategory);
			}

			if ((!isGrouprespoachat) && (respoachat != null)) {
				// System.out.println("respoachat is not a goup");
				// then the sendEmail contain a user and not a group, add it to the list
				users.add(respoachat);
				// System.out.println("users list : " + users);

				usersAchat.add(respoachat);
				// System.out.println("usersAchat : " + usersAchat);
			}

			if ((!isGroupparticipant) && (participant != null)) {
				// System.out.println("participant is not a goup");
				// then the sendEmail contain a user and not a group, add it to the list
				users.add(participant);
				// System.out.println("users list : " + users);

				usersParts.add(participant);
				// System.out.println("usersParts : " + usersParts);
			}

		} catch (Exception e) {
			// System.out.println(" exepc " + e);

		} finally {
			try {
				if (stmt != null) {
					// System.out.println("closing the statement");
					stmt.close();
				}

			} catch (Exception e) {
				// System.out.println("cant close stmt : " + e);
			}
			;
			try {

				if (resultSet != null) {
					// System.out.println("closing the resultSet");
					resultSet.close();
				}

			} catch (Exception e) {
				// System.out.println("cant close resultSet : " + e);
			}
			;
			try {
				if (connection != null) {
					// System.out.println("closing the connection");
					connection.close();

				}
			} catch (Exception e) {
				// System.out.println("cant close connection : " + e);
			}
			;

		}

		List<String> users2 = new ArrayList<String>();
		// System.out.println("after initilizing the users3 : " + users2);

		for (int k = 0; k < usersCategory.size(); k++) {
			users2.add("user:" + usersCategory.get(k));
		}
		// System.out.println("users2 : " + users2);

		for (int k = 0; k < usersAchat.size(); k++) {
			users2.add("user:" + usersAchat.get(k));
		}
		// System.out.println("users2 : " + users2);

		for (int k = 0; k < usersParts.size(); k++) {
			users2.add("user:" + usersParts.get(k));
		}
		// System.out.println("users2 : " + users2);

		if (totalht.doubleValue() != 0.0D) {
			// System.out.println("totalht.doubleValue() != 0.0D");
			if (!validerautomatique) {

				// System.out.println("!validerautomatique");
				// //System.out.println("le workflow " + validerautomatique);
				String utilisateur = "";

				for (int i = 0; i < users.size(); i++) {
					utilisateur = users.get(i).toString();
					// System.out.println("utilisateur : " + utilisateur);

					usersworkflow.add("user:" + utilisateur);

					// System.out.println("usersworkflow : " + usersworkflow);
				}

				Map<String, Object> params = new HashMap<>();
				params.put("id", "SerialDocumentReview");
				params.put("start", Boolean.valueOf(true));
				// System.out.println("params : " + params);

				this.service.run(this.ctx, "Context.StartWorkflow", params);

				// System.out.println("doc of something : " + documentDA);
				String QUERY_PAYEES = "SELECT * FROM DocumentRoute WHERE docri:participatingDocuments IN ('"
						+ documentDA.getId() + "') and ecm:name like '%SerialDocumentReview%'";
				// System.out.println("QUERY_PAYEES : " + QUERY_PAYEES);

				DocumentModelList workflows = this.session.query(QUERY_PAYEES);
				// System.out.println("workflows : " + workflows);

				// System.out.println("workflows.size() : " + workflows.size());

				for (DocumentModel eachWorkFlow : workflows) {

					// System.out.println("eachWorkFlow.getType() : " + eachWorkFlow.getType());
					// System.out.println("eachWorkFlow.getName() : " + eachWorkFlow.getName());
					// System.out.println("eachWorkFlow.getTitle() : " + eachWorkFlow.getTitle());
					// System.out.println("eachWorkFlow.getDocumentType() : " +
					// eachWorkFlow.getDocumentType());

					// check if the workflow has a participant
					if (eachWorkFlow.getName().contains("SerialDocumentReview")) {
						// System.out.println("eachWorkFlow.getName().contains(\"SerialDocumentReview\")");

						// System.out.println("\n\nlooping on each workflow");
						// System.out.println("eachWorkFlow : " + eachWorkFlow);
						// //System.out.println("participants of eachworkflow : ");

						// System.out.println("check participant : " +
						// eachWorkFlow.getPropertyValue("var_SerialDocumentReview:participants"));

						for (int k = 0; k < ((String[]) eachWorkFlow
								.getPropertyValue("var_SerialDocumentReview:participants")).length; k++) {

							// System.out.println("eachWorkFlow participants : " + ((String[]) eachWorkFlow
							// .getPropertyValue("var_SerialDocumentReview:participants"))[k]);
						}
						if (((String[]) eachWorkFlow
								.getPropertyValue("var_SerialDocumentReview:participants")).length == 0) {
							// System.out.println("workflow has no user");

							Events.instance().raiseEvent("tasksCacheReset", new Object[0]);

							// System.out.println("eachWorkFlow.setPropertyValue");
							eachWorkFlow.setPropertyValue("var_SerialDocumentReview:participants",
									(Serializable) users2);

							for (int k = 0; k < ((String[]) eachWorkFlow
									.getPropertyValue("var_SerialDocumentReview:participants")).length; k++) {

								// System.out.println(
								// "eachWorkFlow.getPropertyValue(\"var_SerialDocumentReview:participants\")) :
								// " + ((String[])
								// eachWorkFlow.getPropertyValue("var_SerialDocumentReview:participants"))[k]);
							}

//							documentDA.setPropertyValue("da:statuworkflow", "En traitement");
//							documentDA.getCoreSession().saveDocument(documentDA);
//							documentDA.getCoreSession().save();

							this.session.saveDocument(eachWorkFlow);
							// System.out.println("after saveDocument");
							this.session.save();
							// System.out.println("after save");
							Events.instance().raiseEvent("workflowNewProcessStarted", new Object[0]);
							Events.instance().raiseEvent("tasksCacheReset", new Object[0]);
						}
					}

				}

				// //System.out.println("\n\n");

				// //System.out.println("this.displayInfoOrException");
				this.displayInfoOrException.sendMessage(this.ctx, this.service, "WebUI.AddMessage",
						"#{messages['label.workflow.launched']}");
				// facesMessages.add(StatusMessage.Severity.INFO,
				// messages.get("workflow.feedback.info.taskEnded"));
			}

			System.out.println(" totalht : " + totalht);

		} else {
			// //System.out.println("big else");
			this.displayInfoOrException.sendMessage(this.ctx, this.service, "WebUI.AddMessage",
					"#{messages['label.saisie.totaldevis']}");
		}
	}

	public List<Map<String, Serializable>> liste_configuration() throws ParseException {
		// //System.out.println("unique msg to show where the error can be 1");
		List<Map<String, Serializable>> list = new ArrayList<Map<String, Serializable>>();

		try {
			stmt = getConnection().createStatement();
			resultSet = stmt.executeQuery(
					"select h.id, ls.id, ls.responsable, ls.limite1, ls.limit2 from hierarchy h, liste_workflow ls "
							+ "where h.parentid='" + configDocumentId
							+ "' and h.primarytype ='liste_workflow' and h.id=ls.id order by limite1");

			// System.out.println("unique msg to show where the error can be 2");

			while (resultSet.next()) {
				// //System.out.println("unique msg to show where the error can be 3");
				Map<String, Serializable> map1 = new HashMap<>();
				map1.put("responsable", resultSet.getString("responsable"));
				map1.put("limite1", resultSet.getDouble("limite1"));
				map1.put("limit2", resultSet.getDouble("limit2"));
				list.add(map1);
				// System.out.println("list of the reponsableParts : " + list);
			}
			// connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					// System.out.println("closing the statement");
					stmt.close();
				}

			} catch (Exception e) {
				// System.out.println("cant close stmt : " + e);
			}
			;
			try {

				if (resultSet != null) {
					// System.out.println("closing the resultSet");
					resultSet.close();
				}

			} catch (Exception e) {
				// System.out.println("cant close resultSet : " + e);
			}
			;
			try {
				if (connection != null) {
					// System.out.println("closing the connection");
					connection.close();

				}
			} catch (Exception e) {
				// System.out.println("cant close connection : " + e);
			}
			;

		}
		return list;

	}

	public Connection getConnection() {
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
