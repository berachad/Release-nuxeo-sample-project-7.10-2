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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import org.nuxeo.ecm.automation.jsf.operations.AddMessage;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.project.sample.beans.Holidays;
import org.nuxeo.project.sample.services.DisplayInfoOrException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Operation(id = updatedocumentDemandeConge.ID, category = Constants.CAT_DOCUMENT, label = "Print Info")
public class updatedocumentDemandeConge {

	private final Log log = LogFactory.getLog(updatedocumentDemandeConge.class);
	private Connection connection;
	private Statement stmt = null;
	private ResultSet resultSet = null;
	private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();
	@Context
	protected CoreSession session;

	@Context
	protected AutomationService service;

	private String currentUser;

	@Context
	protected OperationContext ctx = new OperationContext(session);

	private static final long serialVersionUID = 1L;

	public static final String ID = "Document.updatedocumentDemandeConge";

	@OperationMethod
	public void run() throws OperationException, ParseException {

		DocumentModel doc = (DocumentModel) service.run(ctx, "Seam.GetCurrentDocument");
		if (doc.getType().equals("Demande_conge")) {
			FacesContext fContext = FacesContext.getCurrentInstance();
			currentUser = fContext.getExternalContext().getUserPrincipal().toString();
			String[] data, champ;
			String participants = "", s = (String) doc.getPropertyValue("Demande_conge:participant");
			String currentLifeCycleState = doc.getCurrentLifeCycleState();
			data = s.split(";");
			if (s != null) {
				for (String d : data) {
					champ = d.split(":");
					if (champ[0].equals(currentUser)) {
						participants = participants + champ[0] + ": Validé;";
					} else {
						participants = participants + champ[0] + ":" + champ[1] + ";";
					}
				}
				if (data[data.length - 1].split(":")[0].equals(currentUser)) {

					// Validation d'un demande congé
					List<Holidays> hs = getHolidays();
					Calendar calendare_incremente = (Calendar) doc.getPropertyValue("Demande_conge:DateDebut");
					Date date_incremente = calendare_incremente.getTime();
					Calendar calendar_debut = (Calendar) doc.getPropertyValue("Demande_conge:DateDebut");
					Date date_debut = calendar_debut.getTime();
					Calendar calendar_fin = (Calendar) doc.getPropertyValue("Demande_conge:DateFin");
					Date date_fin = calendar_fin.getTime();
					String matricule = (String) doc.getPropertyValue("Demande_conge:Matricule");
					List<Date> joursC = getdaysBetween(date_debut, date_fin);
					int numberReliquat = 0;
					for (int i = 0; i < hs.size(); i++) {
						List<Date> joursF = getdaysBetween(hs.get(i).getBeginDate(), hs.get(i).getEndDate());
						for (int j = 0; j < joursF.size(); j++) {
							for (int k = 0; k < joursC.size(); k++) {
								if (joursF.get(j).compareTo(joursC.get(k)) == 0) {
									numberReliquat++;
								}
							}
						}
					}

					for (int i = 0; i < joursC.size(); i++) {
						if (joursC.get(i).getDay() == 0 || joursC.get(i).getDay() == 6) {
							joursC.remove(i);
						}
					}

					if (setReliquatEmpoiye(matricule, (joursC.size() - numberReliquat)))
						doc.setPropertyValue("Demande_conge:statuworkflow", "Validé");
					else
						displayInfoOrException.sendMessage(ctx, service, AddMessage.ID,
								"#{messages['label.valide.conge']}");

				}
			}
			doc.setPropertyValue("Demande_conge:participant", (Serializable) participants);
			session.saveDocument(doc);
			session.save();

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

	public List<Holidays> getHolidays() throws ParseException {
		List<Holidays> hs = new ArrayList();
		try {
			stmt = getConnection().createStatement();
			resultSet = stmt.executeQuery(
					"select hs. datedebut, hs.datefin, hs.intitule from nom_ferier hs, hierarchy h where h.id = hs.id and h.name not like '%.trashed'");

			while (resultSet.next()) {

				hs.add(new Holidays(resultSet.getString("intitule"), resultSet.getDate("datedebut"),
						resultSet.getDate("datefin"))); 
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

		if (!hs.isEmpty()) {
			return hs;
		} else {
			return null;
		}
	}

	private List<Date> getdaysBetween(Date date_debut, Date date_fin) {
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

		while (dateIncrement.compareTo(date_fin) < 1) {
			jours.add(dateIncrement);
			Calendar c = Calendar.getInstance();
			c.setTime(dateIncrement);
			c.add(Calendar.DATE, 1);
			dateIncrement = c.getTime();

		}
		for (int i = 0; i < jours.size(); i++) {
			if (jours.get(i).getDay() == 0 || jours.get(i).getDay() == 6) {
				jours.remove(i);

			}
		}
//        for(int i=0; i<jours.size(); i++) {
//
//        	
//        }

		return jours;
	}

	public boolean setReliquatEmpoiye(String matricule, int reliquat) {

		String query = "SELECT * FROM Document WHERE ecm:parentId  IS NOT NULL AND ecm:primaryType = 'employe' AND employe:Matricule = '"
				+ matricule + "' AND ecm:currentLifeCycleState != 'deleted'";
		DocumentModelList docs = session.query(query);
		Long val = (Long) docs.get(0).getProperty("employe", "reliquat");
		int valeur = val.intValue();
		if (valeur < reliquat) {
			return false;

		}

		docs.get(0).setPropertyValue("employe:reliquat", (valeur - reliquat));
		session.saveDocument(docs.get(0));
		session.save();
		return true;
	}

}
