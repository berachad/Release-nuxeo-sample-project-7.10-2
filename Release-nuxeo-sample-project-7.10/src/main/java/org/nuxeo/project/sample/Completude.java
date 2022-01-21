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
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Scope(ScopeType.CONVERSATION)
@Name("Completude")
//Il permet de traiter la rendition de l'accusé de réception associé au facture et la conversion et ajouter le code à barre au accusé de réception et faire la concaténation de l'accusé de réception avec la facture
@Operation(id = Completude.ID, category = Constants.CAT_DOCUMENT, label = "Concatenation Accusé de réception et facture")
public class Completude {

	public static final String ID = "Document.Completude";
	private static final Log log = LogFactory.getLog(Completude.class);
	private CoreSession session;
	private List<String> types;

	private static final long serialVersionUID = 1L;
	@In(create = true)
	protected transient CoreSession coreSession;

	@In(create = true)
	protected transient static NavigationContext navigationContext;

	@In(create = true)
	protected transient WebActions webActions;

	public void setTypes(List<String> array) {
		types = array;
	}

	@In(create = true)
	protected transient static CoreSession documentManager;

	@In(create = true)
	protected transient FacesMessages facesMessages;

	@In(create = true)
	protected DocumentsListsManager documentsListsManager;

	@Context
	protected AutomationService service;

	@Context
	protected OperationContext ctx = new OperationContext(session);

	DocumentModel parent;

	private Statement stmt = null;
	private Connection connection = null;
	private ResultSet resultSet = null;

	List<String> configurationTypeList;

	String statut = "Incomplet";

	CoreSession sessionInsideFolder;

	// return a list of categories from document model list
	public List<String> getTypesOfDocuments() {

		// initialize the list of types in every call of the method
		types = new ArrayList<String>();

		// get all the parents
		DocumentModel currentDocument = navigationContext.getCurrentDocument();
		System.out.println("currentDocument.getRef() : " + currentDocument.getRef());
		List<DocumentModel> parents = documentManager.getParentDocuments(currentDocument.getRef());
		System.out.println("parents : " + parents);

		List<DocumentModel> children = this.documentManager.getChildren(currentDocument.getRef());
		System.out.println("children : " + children);

		// retrieve only the fourth parent which is the "dossierPrincipal"
		parent = parents.get(3);
		System.out.println("parent : " + parent);

		// the current dossierPrincipal
		String currentParentId = parent.getId();

		System.out.println("documentManager : " + documentManager);

		// retrieve list of types document from the table "types_documents_desc"
		DocumentModelList docs = documentManager.query(
				"select * from Document where ecm:primaryType='DossierPrincipal' and ecm:uuid= '" + currentParentId
						+ "'  and ecm:isCheckedInVersion = 0 AND ecm:mixinType != 'HiddenInNavigation' AND ecm:currentLifeCycleState != 'deleted'");
		System.out.println("docs : " + docs);

		// if the doc is not null
		if (docs != null) {
			// get the first element of the DocumentModelList
			DocumentModel documentModel = docs.get(0);

			// list of maps
			List<Map<String, Serializable>> objectTypes = (List<Map<String, Serializable>>) documentModel
					.getPropertyValue("DossierPrincipal:Types_Documents");

			for (Map<String, Serializable> objectType : objectTypes) {
				// add the value of the map's key (in that case 'Type') to the list of types
				types.add(objectType.get("Type").toString());
			}

		}
		System.out.println("get the test " + types);
		return types;
	}

	// the methode that
	@OperationMethod
	public void run() throws OperationException, IOException {
		System.out.println("\n\nexecute the button ! ");

		// initialize the list of types in every call of the method
		types = new ArrayList<String>();
		configurationTypeList = new ArrayList<String>();

		// get all the selected foldersType2
		DocumentModelList foldersSelected = (DocumentModelList) service.run(ctx, "Seam.GetSelectedDocuments");
		System.out.println("foldersSelected : " + foldersSelected);

		// getting the first folder to avoid making the request inside the loop
		DocumentModel firstFolder = foldersSelected.get(0);
		System.out.println("firstFolder : " + firstFolder);

		// we got the session based on the folder and not the currentDocument
		sessionInsideFolder = firstFolder.getCoreSession();
		System.out.println("sessionInsideFolder : " + sessionInsideFolder);

		// retrive all the parents of the currentfolder based on the its coresession
		List<DocumentModel> listOfParent = sessionInsideFolder.getParentDocuments(firstFolder.getRef());
		System.out.println("listOfParent : " + listOfParent);

		// retrieve the closet father folder which is in that case the principaleFolder
		DocumentModel PrincipaleParentFolder = listOfParent.get(3);
		System.out.println("PrincipaleParentFolder : " + PrincipaleParentFolder);

		// list of maps to store the objectList on it
		List<Map<String, Serializable>> objectTypes = (List<Map<String, Serializable>>) PrincipaleParentFolder
				.getPropertyValue("DossierPrincipal:Types_Documents");
		System.out.println("objectTypes : " + objectTypes);

//		// getting the parent id to use it on the request
//		String currentParentId = PrincipaleParentFolder.getId();
//		System.out.println("currentParentId : " + currentParentId);
//
//		// retrieve list of types document from the table "types_documents_desc"
//		DocumentModelList principaleFolder = sessionInsideFolder.query(
//				"select * from Document where ecm:primaryType='DossierPrincipal' and ecm:uuid= '" + currentParentId
//						+ "'  and ecm:isCheckedInVersion = 0 AND ecm:mixinType != 'HiddenInNavigation' AND ecm:currentLifeCycleState != 'deleted'");
//		System.out.println("principaleFolder : " + principaleFolder);
//
//		// get the first element of the DocumentModelList to avoid looping on the list
//		// because we only have one element
//		DocumentModel documentModel = principaleFolder.get(0);
//		System.out.println("documentModel : " + documentModel);
//
//		// list of maps to store the objectList on it
//		List<Map<String, Serializable>> objectTypes = (List<Map<String, Serializable>>) documentModel
//				.getPropertyValue("DossierPrincipal:Types_Documents");
//		System.out.println("objectTypes : " + objectTypes);

		// if the doc is not null
		// if (principaleFolder != null) {

		// loop on the object to get all the types from it
		for (Map<String, Serializable> objectType : objectTypes) {
			// add the value of the map's key (in that case 'Type') to the list of types
			types.add(objectType.get("Type").toString());
		}
		System.out.println("types of the principale folder : " + types);
		// }

		int i = 1;
		// loop in every single folder
		for (DocumentModel folder : foldersSelected) {

			System.out.println("\n\n*********************************** -------- looping on foldersType2 loop number : "
					+ i + " --------- ************************************");
			i++;

			// initilize the list of the documentsType
			configurationTypeList = new ArrayList<String>();
			statut = "";

			System.out.println("folder : " + folder);
			System.out.println("folderId : " + folder.getId());

			DocumentModelList dossierType2 = sessionInsideFolder.query(
					"select * from Document where ecm:primaryType='Dossier_type2' and ecm:uuid= '" + folder.getId()
							+ "' and ecm:isCheckedInVersion = 0 AND ecm:mixinType != 'HiddenInNavigation' AND ecm:currentLifeCycleState != 'deleted'");
			System.out.println("dossierType2 : " + dossierType2);
			System.out.println("property statut : " + dossierType2.get(0).getPropertyValue("Dossier_type2:Status"));

			// getting the whole documents inside the folderTypes2
			DocumentModelList listOfChildren = sessionInsideFolder.getChildren(folder.getRef());
			System.out.println("listOfChildren : " + listOfChildren);

			int numberOfDocuments = listOfChildren.size();
			System.out.println("numberOfDocuments : " + numberOfDocuments);

			int numberOfTypes = types.size();
			System.out.println("size of the types : " + types.size());

			if (numberOfDocuments < numberOfTypes) {
				System.out.println("\n\nnumberOfDocuments<numberOfTypes\n\n");
				statut = "Incomplet";
				dossierType2.get(0).setPropertyValue("Dossier_type2:Status", statut);
				System.out.println("property statut : " + dossierType2.get(0).getPropertyValue("Dossier_type2:Status"));

				sessionInsideFolder.saveDocument(dossierType2.get(0));
				sessionInsideFolder.save();
				continue;
			}

			for (DocumentModel child : listOfChildren) {
				System.out.println("\n********** looop in every document ************");
				System.out.println("child : " + child);

				String childId = child.getId();
				System.out.println("childId : " + childId);

				try {

					// create the jdbc connection
					stmt = getConnection().createStatement();

					// retireiving a list of objects that contain groups and its users
					resultSet = stmt.executeQuery("select * from docs_generique where id='" + childId + "' ");
					System.out.println("resultSet : " + resultSet);

					// on each object of resultSet (the object contain group and user)
					while (resultSet.next()) {
						// retrieving elements from resultSet
						String configurationType = resultSet.getString("configuration_type");
						System.out.println("configurationType : " + configurationType);

						configurationTypeList.add(configurationType);
					}
					System.out.println("configurationTypeList : " + configurationTypeList);

				} catch (Exception e) {
					System.out.println("exepc  " + e);

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

			// check if the types are all in the documents
			System.out.println("\nvalidate the list");

			if (!configurationTypeList.isEmpty()) {
				System.out.println("!configurationTypeList.isEmpty()");
				for (String type : types) {
					System.out.println("type : " + type);
					if (configurationTypeList.contains(type)) {
						System.out.println("the configurationTypeList contain the type : " + type);
						statut = "Complet";
					} else if (!configurationTypeList.contains(type)) {
						System.out.println("configurationTypeList dosnt contain the type : " + type);
						statut = "Incomplet";
						break;
					}
				}
			}

			if (statut == "Complet") {
				System.out.println("statut is valid ");

			} else {
				System.out.println("statut is invalid ");
			}

			dossierType2.get(0).setPropertyValue("Dossier_type2:Status", statut);
			System.out.println("property statut : " + dossierType2.get(0).getPropertyValue("Dossier_type2:Status"));

			sessionInsideFolder.saveDocument(dossierType2.get(0));
			sessionInsideFolder.save();

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
