package org.nuxeo.project.sample;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.user.center.dashboard.jsf.JSFDashboardActions;
import org.postgresql.util.PGInterval;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.google.gson.Gson;

@Scope(ScopeType.CONVERSATION)
@Name("DashBoardSupport")
public class DashBoardSupport extends Dashboard {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(DashBoardSupport.class);

	@In(create = true)
	protected transient CoreSession coreSession;

	@In(create = true)
	protected transient NavigationContext navigationContext;

	private Calendar now = Calendar.getInstance();
	public short annee = (short) this.now.get(1);

	private Connection connection;

	@In(create = true)
	protected transient CoreSession documentManager;

	String codesDa = "";
	String domainName = JSFDashboardActions.getMyDomainSelected();

	public String getDAIds() {

		String docIds = new String();

		DocumentModelList docs = this.documentManager.query(
				"SELECT * FROM Document WHERE ecm:primaryType = 'da' AND ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0"
						+ " AND ecm:currentLifeCycleState != 'deleted'");

		// System.out.println("docs : " + docs);

		if (!docs.isEmpty()) {
			System.out.println("!docs.isEmpty() : " + !docs.isEmpty());
			for (DocumentModel documentModel : docs) {
				docIds += "'" + documentModel.getId() + "',";
			}
			docIds = docIds.substring(0, docIds.length() - 1);
		}
		// System.out.println("docIds : " + docIds);
		return docIds;
	}

	public String getValidationTimeDaPerDA() {
		System.out.println("\n\n\n\n\n\n\nDashBoardSupport getValidationTimeDaPerDA()");

		System.out.println("\n\nDashBoardSupportgetAnnee() : " + getAnnee());

		List<Map<String, Object>> queryResult = new ArrayList<>();
		List<Map<String, Object>> listOfMaps = new ArrayList<>();

		Dashboard.annee = getAnnee();
		System.out.println("Dashboard.annee : " + Dashboard.annee);

		System.out.println("\n\ngetdomainName( : " + getdomainName());

		if (getdomainName().equals("/Brinks Maroc")) {

			String domainDaIds = null;

			domainDaIds = getDAIds();

			Map<String, Object> map = null;

			if (!domainDaIds.isEmpty()) {
				System.out.println("this.connection : " + this.connection);
				if (this.connection != null) {

					String querycount = "select distinct (d.W_validation_date - d.W_launch_date ) as interval, dc.title "
							+ "from da d, dublincore dc " + "where d.id=dc.id "
							+ "and date_part('year', d.W_validation_date) ='" + getAnnee()
							+ "' and d.statuworkflow='Validé' and d.id IN (" + domainDaIds
							+ ") and d.W_launch_date is not null and d.W_validation_date is not null";
					// System.out.println("\n\n\nquerycount " + querycount);
					try {
						QueryRunner queryRunner = new QueryRunner();
						System.out.println("\n\npypy");
						queryResult = (List<Map<String, Object>>) queryRunner.query(this.connection, querycount,
								(ResultSetHandler) new MapListHandler());
						System.out.println("\nqueryResult : " + queryResult);
					} catch (SQLException se) {
						se = se;
						throw new RuntimeException("Couldn't query the database ........", se);
					}
				} else {
					log.error("buttonJsonDA NULL .......................");
				}
				System.out.println("queryResult1 : " + queryResult);

				for (Map<String, Object> singleDaIntervale : queryResult) {

					System.out.println("\n\nsingleDaIntervale : " + singleDaIntervale);

					map = new HashMap<>();

					String nameDA = (String) singleDaIntervale.get("title");
					PGInterval intervale = (PGInterval) singleDaIntervale.get("interval");

					float inrevaleTimePerHour = 0.0F;

					if (intervale.getYears() != 0) {
						inrevaleTimePerHour += (intervale.getYears() * 365);
						System.out.println(" dateMoyenne.getYears()  : " + intervale.getYears());
						System.out.println(" inrevaleTimePerHour  : " + inrevaleTimePerHour);
					}

					if (intervale.getMonths() != 0) {
						inrevaleTimePerHour += (intervale.getMonths() * 30);
						System.out.println(" intervale.getMonths()  : " + intervale.getMonths());
						System.out.println(" inrevaleTimePerHour  : " + inrevaleTimePerHour);
					}

					if (intervale.getDays() != 0) {
						inrevaleTimePerHour += (intervale.getDays() * 24);
						System.out.println(" intervale.getDays()  : " + intervale.getDays());
						System.out.println(" inrevaleTimePerHour  : " + inrevaleTimePerHour);
					}

					if (intervale.getHours() != 0) {
						inrevaleTimePerHour += intervale.getHours();
						System.out.println(" intervale.getHours()  : " + intervale.getHours());
						System.out.println(" inrevaleTimePerHour  : " + inrevaleTimePerHour);
					}

					if (intervale.getMinutes() != 0) {
						inrevaleTimePerHour += (intervale.getMinutes() / 60);
						System.out.println(" intervale.getMinutes()  : " + intervale.getMinutes());
						System.out.println(" inrevaleTimePerHour  : " + inrevaleTimePerHour);
					}

					map.put("nameDA", nameDA);
					map.put("intervale", Float.valueOf(inrevaleTimePerHour));

					listOfMaps.add(map);

					System.out.println("listOfMaps : " + listOfMaps);
				}
			}
		}
		return (new Gson()).toJson(listOfMaps);
	}

	public String getdomainName() {
		return JSFDashboardActions.getMyDomainSelected();
	}

	public void setdomainName(String domainName) {
		this.domainName = domainName;
	}

	public short getAnnee() {
		System.out.println("this.annee : " + this.annee);
		return this.annee;
	}

	public void setAnnee(short annee) {
		this.annee = annee;
	}

	@Override
	public void connection() {
		System.out.println("\n\n\n\nHammmmmmmmm");
		try {
			String driverName = "org.postgresql.Driver";
			Class.forName(driverName);
			InputStream input = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("../datasources-config.xml");
			try {
				Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.parse(new InputSource(input));
				XPath xpath = XPathFactory.newInstance().newXPath();
				String url = (String) xpath.compile("//component//extension//datasource/@url").evaluate(document,
						XPathConstants.STRING);
				String username = (String) xpath.compile("//component//extension//datasource/@username")
						.evaluate(document, XPathConstants.STRING);
				String password = (String) xpath.compile("//component//extension//datasource/@password")
						.evaluate(document, XPathConstants.STRING);
				this.connection = DriverManager.getConnection(url, username, password);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			System.err.println("Could not find the database driver");
		} catch (Exception e) {
			System.err.println("Could not connect to the database");
		}
	}

	@Override
	public void buttonJson() {
		System.out.println("\n\nbuttonJson");
		Connection connection = null;

		try {
			String driverName = "org.postgresql.Driver";
			Class.forName(driverName);
			InputStream input = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("../datasources-config.xml");

			try {
				Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.parse(new InputSource(input));
				XPath xpath = XPathFactory.newInstance().newXPath();
				String url = (String) xpath.compile("//component//extension//datasource/@url").evaluate(document,
						XPathConstants.STRING);
				String username = (String) xpath.compile("//component//extension//datasource/@username")
						.evaluate(document, XPathConstants.STRING);
				String password = (String) xpath.compile("//component//extension//datasource/@password")
						.evaluate(document, XPathConstants.STRING);
				connection = DriverManager.getConnection(url, username, password);
				log.error(connection.toString());

				getValidationTimeDaPerDA();
				getlistOftotalparMoisAnneeValid();
				getlistOftotalparMoisAnneeValid1();
				getlistOftotalparMoisAnnee();
				getlistOftotalparMoisAVG();
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (ParserConfigurationException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

		} catch (ClassNotFoundException e) {

			System.err.println("Could not find the database driver");
		} catch (Exception e) {

			System.err.println("Could not connect to the database");
		}
	}

}
