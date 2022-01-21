package org.nuxeo.project.sample.services;

//AriLiquide

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.jsf.operations.AddMessage;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.project.sample.beans.NuxeoOAuth2Token;
import org.nuxeo.project.sample.beans.OAuth2Client;
import org.nuxeo.project.sample.beans.Sql;
import org.nuxeo.project.sample.beans.User;
import org.nuxeo.project.sample.complementary.Constant;
import org.nuxeo.project.sample.sage.Config;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import net.sf.json.JSONObject;

public class MethodsShared {

	private final Log log = LogFactory.getLog(MethodsShared.class);
	private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private Connection connection;
	private Statement stmt = null;
	private PreparedStatement updateSales = null;
	private ResultSet resultSet = null;
	private CoreSession session;
	private static ResourceBundle bundle = ResourceBundle.getBundle("Config");
	private static final long MILLISECONDS_EXPIRATION = 3600000;

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

	public int getSumSalary(short year) {
		int sum = 0;
		try {
			stmt = getConnection().createStatement();
			resultSet = stmt.executeQuery(
					"select COALESCE(sum(em.tarif_negocie), 0) as sum from employe em, hierarchy h WHERE em.statut_employe='ACTIF' AND date_part('year', date_embauche) <'"
							+ year + "' AND h.id = em.id and h.name not like '%.trashed'");
			while (resultSet.next()) {
				sum = resultSet.getInt("sum");
			}
			// connection.close();
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
		System.out.println("La somme des salaires avant : " + year + " est : " + sum);
		return sum;
	}

	public DocumentModelList getProduits(String codes, CoreSession session, OperationContext ctx,
			AutomationService service) throws OperationException {
		String query = "SELECT * FROM Document WHERE ecm:parentId  IS NOT NULL AND ecm:primaryType = 'Produit' AND Produit:Code in("
				+ codes + ")" + Constant.LOG_COMMENT_STATE_DELETED1;
		DocumentModelList docProds = session.query(query);
		System.out.println("docProds : " + docProds);

		if (docProds.size() > 0) {
			log.error("[INFO] == Size of list Products==");
			log.error("[INFO] == Size of list purchase order is : " + docProds.size() + " ==");
			return docProds;
		}
		return null;
	}

	public DocumentModel getProduit(DocumentModelList Products, String code) throws OperationException {

		for (DocumentModel doc : Products) {
			if (doc.getPropertyValue("Produit:Code").equals(code)) {
				return doc;
			}
		}

		return null;
	}

	public long getCountSalary(short year) {
		long count = 0;
		try {
			stmt = getConnection().createStatement();
			resultSet = stmt.executeQuery(
					"select COALESCE(COUNT(*), 0) as count from employe em, hierarchy h WHERE em.statut_employe='ACTIF' AND date_part('year', date_embauche) <'"
							+ year + "' AND h.id = em.id and h.name not like '%.trashed'");
			while (resultSet.next()) {
				count = Long.parseLong(resultSet.getString("count"));
			}
			// connection.close();
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
		System.out.println("Le nombre des employés avant : " + year + " est : " + count);
		return count;
	}

	public DocumentModel getNumberOfSequence(CoreSession session, OperationContext ctx, AutomationService service)
			throws OperationException {
		String query = "SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/SG2/Configuration/' AND maxALD:maxnumDA IS NOT NULL";
		DocumentModelList docSeq = session.query(query);

		if (!docSeq.isEmpty()) {
			log.error("[INFO] == Size of Sequence list associated with bc : " + docSeq.size() + " ==");
			return docSeq.get(0);

		} else {
			log.error("[INFO] == Sequence list associated with bc is empty ==");
			displayInfoOrException.sendMessage(ctx, service, AddMessage.ID,
					"Le document sequence pour bon commande n'existe pas !.");
			return null;
		}
	}

	public DocumentModel getBCByNumBC(String numBC, CoreSession session, OperationContext ctx,
			AutomationService service) throws OperationException {
		if (numBC != null) {
			String query = "SELECT * FROM Document WHERE ecm:parentId  IS NOT NULL AND ecm:primaryType = 'Bon_Commande' AND bon_commande:num_commande = '"
					+ numBC + Constant.LOG_COMMENT_STATE_DELETED;
			DocumentModelList docBCs = session.query(query);
			if (docBCs.size() > 0) {
				log.error("[INFO] == Size of list purchase order is : " + numBC + " ==");
				log.error("[INFO] == Size of list purchase order is : " + docBCs.size() + " ==");
				return docBCs.get(0);
			}
			return null;
		}
		return null;
	}

	public DocumentModel getRegelementByNum(String numReglement, CoreSession session, OperationContext ctx,
			AutomationService service) throws OperationException {
		if (numReglement != null) {
			String query = "SELECT * FROM Document WHERE ecm:parentId  IS NOT NULL AND ecm:primaryType = 'Reglement' AND DocTypeRegelemnt:NumDeReglement = '"
					+ numReglement + Constant.LOG_COMMENT_STATE_DELETED;
			DocumentModelList docReglement = session.query(query);
			if (docReglement.size() > 0) {
				log.error("[INFO] == num of  Reglement is : " + numReglement + " ==");
				log.error("[INFO] == Size of list Reglement : " + docReglement.size() + " ==");
				return docReglement.get(0);
			}
			return null;
		}
		return null;
	}

	public DocumentModel getBCByNumBC(String numBC, CoreSession session, AutomationService service)
			throws OperationException {
		String query = "SELECT * FROM Document WHERE ecm:parentId  IS NOT NULL AND ecm:primaryType = 'Bon_Commande' AND bon_commande:num_commande = "
				+ numBC + Constant.LOG_COMMENT_STATE_DELETED1;
		DocumentModelList docBCs = session.query(query);
		if (docBCs.size() > 0) {
			log.error("[INFO] == Size of list purchase order is : " + numBC + " ==");
			log.error("[INFO] == Size of list purchase order is : " + docBCs.size() + " ==");
			return docBCs.get(0);
		}
		return null;
	}

	public DocumentModel getBlByNumBL(String numBl, CoreSession session, AutomationService service)
			throws OperationException {
		if (numBl != null) {
			String query = "SELECT * FROM Document WHERE ecm:parentId  IS NOT NULL AND ecm:primaryType = 'reception' AND reception:num_reception = '"
					+ numBl + Constant.LOG_COMMENT_STATE_DELETED;
			DocumentModelList docBCs = session.query(query);
			if (docBCs.size() > 0) {
				log.error("[INFO] == Size of list bon livraison is : " + numBl + " ==");
				log.error("[INFO] == Size of list bon livraison is : " + docBCs.size() + " ==");
				return docBCs.get(0);
			}
			return null;
		}
		return null;
	}

	public DocumentModel getReceptionByNumBC(String numBC, CoreSession session, OperationContext ctx,
			AutomationService service) throws OperationException {
		String query = "SELECT * FROM Document WHERE ecm:parentId  IS NOT NULL AND ecm:primaryType = 'reception' AND reception:num_commande = "
				+ numBC + Constant.LOG_COMMENT_STATE_DELETED1;
		DocumentModelList docRpt = session.query(query);
		log.error("[INFO] == Size of list reception is : " + docRpt.size() + " ==");
		if (docRpt.size() > 0) {
			return docRpt.get(0);
		}
		return null;
	}

	public DocumentModel getProduitByCode(String numProduit, CoreSession session, OperationContext ctx,
			AutomationService service) throws OperationException {
		String query = "SELECT * FROM Document WHERE  ecm:primaryType = 'Produit' AND Produit:Code = '" + numProduit
				+ Constant.LOG_COMMENT_STATE_DELETED;
		DocumentModelList docproduit = session.query(query);
		System.out.println("docproduit : " + docproduit);

		log.error("[INFO] == Size of list reception is : " + docproduit.size() + " ==");
		if (docproduit.size() > 0) {
			return docproduit.get(0);
		}
		return null;
	}

	public User findUserByUsername(String username) {
		System.out.println("findUserByUsername daldjjjjjjjjjjjjjjjjjjjjjjjdddddddddd ");
		HashSet<User> users = new HashSet<>();
		try {
			stmt = getConnection().createStatement();
			resultSet = stmt.executeQuery(
					"select h.id,us.\"firstName\",us.\"lastName\",us.email, us.username, us.password, us.company from users us, hierarchy h where us.username = '"
							+ username + "' and h.name = us.username and h.name not like '%.trashed'");
			while (resultSet.next()) {
				users.add(new User(resultSet.getString("username"), resultSet.getString("password"),
						resultSet.getString("company"), resultSet.getString("id"), resultSet.getString("firstName"),
						resultSet.getString("lastName")));
			}
			// connection.close();
			if (users.iterator().hasNext()) {
				User u = users.iterator().next();
				log.error(
						" === < From DB. username : " + u.getUsername() + ", password : " + u.getPassword() + " > ===");
				return u;
			} else {
				return null;
			}

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
		return null;
	}

	public NuxeoOAuth2Token saveNuxeoOAuth2Token(String clientId, String nuxeoLogin) {
		final String serviceName = "org.nuxeo.server.token.store";
		final long expirationTime = 3600000;
		int id = getNuxeoOAuth2Token().size();
		String accessToken = RandomStringUtils.random(32, true, true);
		String refreshToken = RandomStringUtils.random(64, true, true);
		Date date = new Date();
		String rq1 = "INSERT INTO public.\"oauth2Tokens\"(\"clientId\", \"expirationTimeMilliseconds\", \"nuxeoLogin\", id, \"serviceName\", \"accessToken\", \"serviceLogin\", \"sharedWith\", \"creationDate\", \"isShared\", \"refreshToken\")VALUES";
		String rq2 = " ('" + clientId + "', " + expirationTime + ", '" + nuxeoLogin + "', " + (id + 1) + ", '"
				+ serviceName + "', '" + accessToken + "', null, null, '" + format.format(date) + "', false, '"
				+ refreshToken + "')";

		try {
			getConnection().setAutoCommit(false);
			updateSales = getConnection().prepareStatement(rq1 + rq2);
			int row = updateSales.executeUpdate();
			log.error(" === < Access Token : " + accessToken + ", Refresh Token : " + refreshToken + ", Row number : "
					+ row + " > ===");
			getConnection().close();
			return new NuxeoOAuth2Token(id + 1, serviceName, nuxeoLogin, accessToken, clientId, null, refreshToken,
					expirationTime, false, null);
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
		return null;
	}

	public List<NuxeoOAuth2Token> getNuxeoOAuth2Token() {
		List<NuxeoOAuth2Token> oauth2s = new ArrayList<>();
		try {
			stmt = getConnection().createStatement();
			resultSet = stmt.executeQuery("SELECT * FROM \"oauth2Tokens\"");

			while (resultSet.next()) {
				NuxeoOAuth2Token o = new NuxeoOAuth2Token(resultSet.getInt("id"), resultSet.getString("serviceName"),
						resultSet.getString("nuxeoLogin"), resultSet.getString("accessToken"),
						resultSet.getString("clientId"), null, resultSet.getString("refreshToken"),
						resultSet.getLong("expirationTimeMilliseconds"), resultSet.getBoolean("isShared"),
						resultSet.getString("serviceLogin"));
				// System.out.println(o.toString());
				oauth2s.add(o);
			}
			// connection.close();
			return oauth2s;
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
		return null;
	}

	public OAuth2Client getOAuth2Clients(String clientId) {
		List<OAuth2Client> oauth2client = new ArrayList<>();
		try {
			stmt = getConnection().createStatement();
			resultSet = stmt.executeQuery("SELECT * FROM \"oauth2Clients\" WHERE \"clientId\" = '" + clientId + "'");

			while (resultSet.next()) {
				OAuth2Client o = new OAuth2Client(resultSet.getInt("id"), resultSet.getString("clientId"),
						resultSet.getString("clientSecret"), resultSet.getString("name"),
						resultSet.getBoolean("enabled"));
				// System.out.println(o.toString());
				oauth2client.add(o);
			}
			// connection.close();
			log.error(" === < " + oauth2client.get(0).toString() + " > ===");
			return oauth2client.get(0);
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
		return null;
	}

	public Sql executQuery(String table, String column, String type) {
		// TODO Auto-generated method stub
		String query = "alter table " + table + " alter column " + column + " type " + type;
		try {
			getConnection().setAutoCommit(false);
			updateSales = getConnection().prepareStatement(query);

			System.out.println("Query : " + query);
			int row = updateSales.executeUpdate();
			System.out.println("Row : " + row);
			getConnection().close();
			return new Sql(table, column, type);
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
		return null;
	}

	public MethodsShared(CoreSession session) {
		this.session = session;

	}

	public MethodsShared() {
	}

	public void mergePdfFiles(List<InputStream> inputPdfList, OutputStream outputStream) throws Exception {

		// Create document and pdfReader objects.
		com.itextpdf.text.Document document = new com.itextpdf.text.Document();
		List<PdfReader> readers = new ArrayList<PdfReader>();
		int totalPages = 0;

		// Create pdf Iterator object using inputPdfList.
		Iterator<InputStream> pdfIterator = inputPdfList.iterator();

		// Create reader list for the input pdf files.
		while (pdfIterator.hasNext()) {
			InputStream pdf = pdfIterator.next();
			PdfReader pdfReader = new PdfReader(pdf);
			readers.add(pdfReader);
			totalPages = totalPages + pdfReader.getNumberOfPages();
		}

		// Create writer for the outputStream
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);

		// Open document.
		document.open();

		// Contain the pdf data.
		PdfContentByte pageContentByte = writer.getDirectContent();

		PdfImportedPage pdfImportedPage;
		int currentPdfReaderPage = 1;
		Iterator<PdfReader> iteratorPDFReader = readers.iterator();

		// Iterate and process the reader list.
		while (iteratorPDFReader.hasNext()) {
			PdfReader pdfReader = iteratorPDFReader.next();
			// Create page and add content.
			while (currentPdfReaderPage <= pdfReader.getNumberOfPages()) {
				document.newPage();
				pdfImportedPage = writer.getImportedPage(pdfReader, currentPdfReaderPage);
				pageContentByte.addTemplate(pdfImportedPage, 0, 0);
				currentPdfReaderPage++;
			}
			currentPdfReaderPage = 1;
		}

		// Close document and outputStream.
		outputStream.flush();
		document.close();
		outputStream.close();

		// log.error(" **** < files merged successfully. > ****");
	}

	public List<User> getUsers() {
		HashSet<User> users = new HashSet<>();
		try {
			stmt = getConnection().createStatement();
			resultSet = stmt.executeQuery(
					"select us.email, us.username, us.password,us.company from users us, hierarchy h where h.name = us.username and h.name not like '%.trashed'");

			while (resultSet.next()) {
				users.add(new User(resultSet.getString("username"), resultSet.getString("password"),
						resultSet.getString("company"), resultSet.getString("email"), null, null));
			}
			// connection.close();
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

		if (!users.isEmpty()) {
			return new ArrayList<User>(users);
		} else {
			return null;
		}
	}

	public DocumentModelList getDocumentByParentIdANDType(String parentId, String type, CoreSession session) {
		String query = "SELECT * FROM Document WHERE ecm:parentId = '" + parentId + "' AND ecm:primaryType = '" + type
				+ Constant.LOG_COMMENT_STATE_DELETED;
		return session.query(query);
	}

	public NuxeoOAuth2Token findLastNuxeoOAuth2Token(String username) throws ParseException {
		HashSet<NuxeoOAuth2Token> oauth2s = new HashSet<>();
		try {
			stmt = getConnection().createStatement();
			resultSet = stmt.executeQuery(
					"select * from \"oauth2Tokens\" oauth2 where oauth2.\"nuxeoLogin\" = '" + username + "'");
			while (resultSet.next()) {
				Date date = format.parse(resultSet.getString("creationDate"));
				Calendar calender = Calendar.getInstance();
				calender.setTime(date);
				NuxeoOAuth2Token oauth2 = new NuxeoOAuth2Token(resultSet.getInt("id"),
						resultSet.getString("serviceName"), resultSet.getString("nuxeoLogin"),
						resultSet.getString("accessToken"), resultSet.getString("clientId"), calender,
						resultSet.getString("refreshToken"), resultSet.getLong("expirationTimeMilliseconds"),
						resultSet.getBoolean("isShared"), resultSet.getString("serviceLogin"));
				oauth2s.add(oauth2);
			}
			// connection.close();
			if (oauth2s.iterator().hasNext()) {
				NuxeoOAuth2Token oa = oauth2s.iterator().next();
				// log.error(" === < From DB. nuxeoLogin : " + oa.getNuxeoLogin() + ", clientId
				// : " + oa.getClientId() + " > ===");
				return oa;
			} else {
				return null;
			}
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
		return null;
	}

	public NuxeoOAuth2Token findNuxeoOAuth2Token(String nuxeoLogin, String accessToken) throws ParseException {
		HashSet<NuxeoOAuth2Token> oauth2s = new HashSet<>();
		try {
			stmt = getConnection().createStatement();
			resultSet = stmt.executeQuery("select * from \"oauth2Tokens\" oauth2 where oauth2.\"nuxeoLogin\" = '"
					+ nuxeoLogin + "' and oauth2.\"accessToken\" = '" + accessToken + "'");
			while (resultSet.next()) {
				Date date = format.parse(resultSet.getString("creationDate"));
				Calendar calender = Calendar.getInstance();
				calender.setTime(date);
				// log.error(" === < From DB. Creation date : " + calender + ", date : " + date
				// + " > ===");
				NuxeoOAuth2Token oauth2 = new NuxeoOAuth2Token(resultSet.getInt("id"),
						resultSet.getString("serviceName"), resultSet.getString("nuxeoLogin"),
						resultSet.getString("accessToken"), resultSet.getString("clientId"), calender,
						resultSet.getString("refreshToken"), resultSet.getLong("expirationTimeMilliseconds"),
						resultSet.getBoolean("isShared"), resultSet.getString("serviceLogin"));
				oauth2s.add(oauth2);
			}
			// connection.close();
			if (oauth2s.iterator().hasNext()) {
				NuxeoOAuth2Token oa = oauth2s.iterator().next();
				// log.error(" === < From DB. nuxeoLogin : " + oa.getNuxeoLogin() + ", clientId
				// : " + oa.getClientId() + " > ===");
				return oa;
			} else {
				return null;
			}
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
		return null;
	}

	public DocumentModelList getBCByInvoiceNum(String invoicenum) {
		// TODO Auto-generated method stub
		String query = "SELECT * FROM Document WHERE ecm:primaryType = 'Bon_Commande' AND bon_commande:Num_Facture = '"
				+ invoicenum + Constant.LOG_COMMENT_STATE_DELETED;
		DocumentModelList list = session.query(query);
		System.out.println(" liste " + list.size());
		if (list.size() > 0) {
			log.error("[getBCByInvoiceNum] == Size of list purchase order is : " + list.size() + " == num "
					+ list.size());
			return list;
		}
		return null;

	}

	public DocumentModelList getBLByNumBC(String numBc) {
		// TODO Auto-generated method stub
		String query = "SELECT * FROM Document WHERE ecm:primaryType = 'reception' AND reception:num_commande = '"
				+ numBc + Constant.LOG_COMMENT_STATE_DELETED;
		System.out.println("query   " + query);
		DocumentModelList list = session.query(query);
		if (list.size() > 0) {
			log.error("[getBLByNumBC] == Size of list BL is : " + list.size());

			return list;
		}
		return null;

	}

	public DocumentModelList getBLByBC(String facnum) {
		// TODO Auto-generated method stub
		String query = "SELECT * FROM Document WHERE ecm:primaryType = 'reception' AND reception:num_facture = '"
				+ facnum + Constant.LOG_COMMENT_STATE_DELETED;
		DocumentModelList list = session.query(query);
		if (list.size() > 0) {
			log.error("[getBLByBC] == Size of list BL is : " + list.size() + " == num " + list.size());
			return list;
		}
		return null;

	}

	public void downloadZip(List<File> fs, String nameFileZip) throws FileNotFoundException, IOException {

		List<File> files = fs;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

		response.reset();
		response.setHeader("Content-Type", "application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + nameFileZip + ".zip");

		OutputStream responseOutputStream = response.getOutputStream();
		InputStream fileInputStream = new FileInputStream(createZip(files, nameFileZip));

		byte[] bytesBuffer = new byte[2048];
		int bytesRead;
		while ((bytesRead = fileInputStream.read(bytesBuffer)) > 0) {
			responseOutputStream.write(bytesBuffer, 0, bytesRead);
		}

		responseOutputStream.flush();
		fileInputStream.close();
		responseOutputStream.close();
		facesContext.responseComplete();
	}

	public File createZip(List<File> files, String nameFileZip) throws IOException {

		FileOutputStream zipFile = new FileOutputStream(new File(Constant.PATH_FILES + "/" + nameFileZip + ".zip"));
		ZipOutputStream output = new ZipOutputStream(zipFile);

		for (File file : files) {
			ZipEntry zipEntry = new ZipEntry(file.getName());
			output.putNextEntry(zipEntry);

			FileInputStream pdfFile = new FileInputStream(new File(file.getPath()));
			IOUtils.copy(pdfFile, output); // this method belongs to apache IO Commons lib!
			pdfFile.close();
			output.closeEntry();
		}
		output.finish();
		output.close();
		return new File(Constant.PATH_FILES + "/" + nameFileZip + ".zip");

	}

	public Config Config() {
		List<Config> oauth2client = new ArrayList<>();
		try {
			stmt = getConnection().createStatement();
			resultSet = stmt.executeQuery("SELECT * FROM configsy");

			while (resultSet.next()) {

				Config Config = new Config(resultSet.getString("clientid"), resultSet.getString("apimsubscriptionkey"),
						resultSet.getString("clientsecret"), resultSet.getString("signingsecret"),
						resultSet.getString("callbackurl"), resultSet.getString("scope"),
						resultSet.getString("tokenendpoint"), resultSet.getString("authendpoint"),
						resultSet.getString("baseendpoint"), resultSet.getString("adress_region"),
						resultSet.getString("lienfournisseur"), resultSet.getString("lienfichierlog"));
				// System.out.println(o.toString());
				oauth2client.add(Config);
			}
			// connection.close();
			return oauth2client.get(0);
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

	public String verifyExpirationAccessToken(String nuxeoLogin, String accessToken) throws ParseException {
		JSONObject message = new JSONObject();
		if (!StringUtils.isEmpty(nuxeoLogin) && !StringUtils.isEmpty(accessToken)) {
			NuxeoOAuth2Token oauth2 = this.findNuxeoOAuth2Token(nuxeoLogin, accessToken);
			if (oauth2 != null) {
				Date creationDate = oauth2.getCreationDate().getTime();
				Date dateNow = format.parse(format.format(new Date()));

				long diffInMillies1 = Math.abs(dateNow.getTime() - creationDate.getTime());
//					 message.put("title", "Failed operation");
//					 message.put("body", "The duration of your access token has expired, please authenticate again.");					 message.put("status", (short) 401);
//					 message.put("date", format.format(new Date()));
				System.out.println("la difference" + diffInMillies1);
				System.out.println("la MILLISECONDS_EXPIRATION" + MILLISECONDS_EXPIRATION);

				return (diffInMillies1 < MILLISECONDS_EXPIRATION) ? null
						: "The duration of your access token has expired, please authenticate again.";
			}
		} else {
//				 message.put("title", "Failed operation");
//				 message.put("body", "Before sending this request, you must authenticate.");
//				 message.put("status", (short) 401);
//				 message.put("date", format.format(new Date()));
			return "Before sending this request, you must authenticate.";
		}
		return "your access token is valid";
	}

}
