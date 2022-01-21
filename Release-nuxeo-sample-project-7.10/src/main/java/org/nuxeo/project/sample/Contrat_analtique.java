package org.nuxeo.project.sample;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.postgresql.util.PGInterval;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import com.google.gson.Gson;

@Scope(ScopeType.CONVERSATION)
@Name("Contrat_analtique")
public class Contrat_analtique implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(Contrat_analtique.class);

	@In(create = true)
	protected transient CoreSession coreSession;

	@In(create = true)
	protected transient NavigationContext navigationContext;

	@In(create = true)
	protected transient WebActions webActions;

	@In(create = true)
	protected transient CoreSession documentManager;

	@In(create = true)
	protected transient FacesMessages facesMessages;
	private Connection connection;

	List<Map<String, Object>> listNumberTotalOfContart;

	private List<Map<String, Object>> listOftotalparMoisAVG;
	private TreeMap<Double, String> Moiss = new TreeMap<>();

	private List<Map<String, Object>> maps = new ArrayList<>();

	private Long nombreContrat = Long.valueOf(0L);
	private Long nombreContratValide = Long.valueOf(0L);
	private Long nombreContratExpire = Long.valueOf(0L);
	private Long nombreContratCreer = Long.valueOf(0L);

	private Long numberTotalOfContart = Long.valueOf(0L);
	private String montantContrat = "";

	private Calendar now = Calendar.getInstance();
	private short annee = (short) this.now.get(1);

	private String query = "SELECT * FROM Document WHERE ecm:primaryType = 'Contrat' AND ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0 AND ecm:currentLifeCycleState != 'deleted'";

	String idsOfContrat = "";
	String contratNXQL = "";

	public Connection getConnection() {
		return this.connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void buttonJson() {
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
				getlistOftotalparMoisAVG();
				getnombreContratValide();
				getnombreContratExpire();
				getnombreContratCreer();
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

	public void connection() {
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

// list ids of contart with NXQL 
	public void contratNXQL() {
		DocumentModelList docs = documentManager.query(query);
		if (docs != null) {
			for (DocumentModel documentModel : docs) {

				idsOfContrat = idsOfContrat + "'" + documentModel.getId() + "'" + ",";

			}
			if (idsOfContrat.length() > 0) {
				idsOfContrat = idsOfContrat.substring(0, idsOfContrat.length() - 1);
			} else {

			}
		}

	}

// get number total of contrat in progress
	public Long getnombreContrat() {

		contratNXQL();
		if (idsOfContrat.equals("")) {
			this.nombreContrat = 0L;

		} else {
			if (this.connection != null) {
				try {
					QueryRunner queryRunner = new QueryRunner();
					String queryFactureNbr = "select count(c.id) from Contrat c where c.statuworkflow='En traitement' and id IN ("
							+ idsOfContrat + ")";

					this.nombreContrat = (Long) queryRunner.query(this.connection, queryFactureNbr,
							(ResultSetHandler) new ScalarHandler());
				} catch (SQLException se) {
					throw new RuntimeException("Couldn't query the database soukaina ........", se);
				}

			} else {

				log.error("listOfFactureNbr NULL .......................");
			}
		}
		return this.nombreContrat;
	}

	// get number total of contrat
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Long getNumberTotalOfContart() {
		contratNXQL();
		if (idsOfContrat.equals("")) {
			this.numberTotalOfContart = 0L;
		} else {
			if (this.connection != null) {

				try {
					QueryRunner queryRunner = new QueryRunner();
					String queryFactureNbr = "select count(c.id) from Contrat c where id IN (" + idsOfContrat + ")";

					if (numberTotalOfContart == null) {
						this.numberTotalOfContart = 0L;
					}

					else {
						this.numberTotalOfContart = (Long) queryRunner.query(this.connection, queryFactureNbr,
								(ResultSetHandler) new ScalarHandler());
					}
				} catch (SQLException se) {
					throw new RuntimeException("Couldn't query the database ........", se);
				}
			} else {
				log.error("listOfFactureNbr NULL .......................");
			}
		}
		return this.numberTotalOfContart;
	}

// get Amount of all contrat
	@SuppressWarnings("unchecked")
	public String getmontantContrat() {
		DecimalFormat df = new DecimalFormat("#.##");
		contratNXQL();
		if (idsOfContrat.equals("")) {
			this.montantContrat = "0.0";
			;
		} else {
			if (this.connection != null) {
				try {
					BigDecimal sum;
					QueryRunner queryRunner = new QueryRunner();
					String queryFactureNbr = "select SUM(c.Val_contrat) from Contrat c where c.id IN (" + idsOfContrat
							+ ")";
					sum = (BigDecimal) queryRunner.query(this.connection, queryFactureNbr,
							(ResultSetHandler) new ScalarHandler());

					if (sum == null) {
						System.out.println("sum null" + sum);
						this.montantContrat = "";
					} else {
						this.montantContrat = String.format("%,.2f", sum);
					}

				} catch (SQLException se) {
					throw new RuntimeException("Couldn't query the database ........", se);
				}

			} else {

				log.error("listOfFactureNbr NULL .......................");
			}

		}
		return this.montantContrat;

	}

	public short getAnnee() {
		return this.annee;
	}

	public void setAnnee(short annee) {
		this.annee = annee;
	}

//Les graphes 
// Temps moyenne de traitement par mois 
	public TreeMap<Double, String> getMoiss() {
		this.Moiss.put(Double.valueOf(1.0D), "Janvier");
		this.Moiss.put(Double.valueOf(2.0D), "Février");
		this.Moiss.put(Double.valueOf(3.0D), "Mars");
		this.Moiss.put(Double.valueOf(4.0D), "Avril");
		this.Moiss.put(Double.valueOf(5.0D), "Mai");
		this.Moiss.put(Double.valueOf(6.0D), "Juin");
		this.Moiss.put(Double.valueOf(7.0D), "Juillet");
		this.Moiss.put(Double.valueOf(8.0D), "Aôut");
		this.Moiss.put(Double.valueOf(9.0D), "Septembre");
		this.Moiss.put(Double.valueOf(10.0D), "Octobre");
		this.Moiss.put(Double.valueOf(11.0D), "Novembre");
		this.Moiss.put(Double.valueOf(12.0D), "Décembre");

		return this.Moiss;
	}

	@SuppressWarnings("unchecked")
	public String getlistOftotalparMoisAVG() {

		this.listOftotalparMoisAVG = null;
		this.maps = new ArrayList<>();
		if (this.connection != null) {

			String querycountValid = "select AVG(enddate-startdate), date_part('month', rn.enddate) from dublincore dc, task t, hierarchy h, route_node rn where dc.id=t.targetdocumentid and t.processid=h.parentid and (t.type='Task6d8' OR t.type='Task328d') and (h.name='Task6d8' OR  h.name='Task328d') and h.id=rn.id and date_part('year', rn.enddate) ='"
					+ getAnnee() + "' and dc.id IN (" + idsOfContrat + ") group by date_part('month', rn.enddate)";

			try {
				QueryRunner queryRunner = new QueryRunner();
				this.listOftotalparMoisAVG = (List<Map<String, Object>>) queryRunner.query(this.connection,
						querycountValid, (ResultSetHandler) new MapListHandler());

			} catch (SQLException se) {
				throw new RuntimeException("Couldn't query the database ........", se);
			}
		} else {

			log.error("buttonJsonDA NULL .......................");
		}
		int result = 1;
		for (Iterator<Double> iterator = getMoiss().keySet().iterator(); iterator.hasNext();) {
			double key = ((Double) iterator.next()).doubleValue();
			System.out.println("key : " + key);
			int mois = (int) key;
			int dateCalcule = 0;
			for (int j = 0; j < this.listOftotalparMoisAVG.size();) {
				for (Map.Entry<String, Object> entry1 : (Iterable<Map.Entry<String, Object>>) ((Map) this.listOftotalparMoisAVG
						.get(j)).entrySet()) {

					if (((String) entry1.getKey()).equals("date_part")) {

						result = Integer.valueOf(((Double) entry1.getValue()).intValue()).intValue();
					}
					if (((String) entry1.getKey()).equals("avg") && entry1.getValue() != null) {

						if (result == mois) {

							PGInterval dateMoyenne = (PGInterval) entry1.getValue();
							if (dateMoyenne.getYears() != 0) {
								dateCalcule += dateMoyenne.getYears() * 365;
							}
							if (dateMoyenne.getMonths() != 0) {
								dateCalcule += dateMoyenne.getMonths() * 30;
							}
							if (dateMoyenne.getDays() != 0) {
								dateCalcule += dateMoyenne.getDays() * 24;

							}
							if (dateMoyenne.getHours() != 0) {
								dateCalcule += dateMoyenne.getHours();

							}
							if (dateMoyenne.getMinutes() != 0) {
								dateCalcule += dateMoyenne.getMinutes() / 60;

							}
						}

					}
				}

				j++;
			}
			Map<String, Object> map1 = new HashMap<>();

			map1.put("name", getMoiss().get(Double.valueOf(key)));
			map1.put("y", Long.valueOf(dateCalcule));
			this.maps.add(map1);
		}
		return (new Gson()).toJson(this.maps);
	}

//get number total of contrat Validé
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Long getnombreContratValide() {
		contratNXQL();
		if (idsOfContrat.equals("")) {
			this.nombreContratValide = 0L;

		} else {
			if (this.connection != null) {
				try {
					QueryRunner queryRunner = new QueryRunner();
					String queryFactureNbr = "select count(c.id) from Contrat c, dublincore dc where c.statuworkflow='Validé' and c.id IN ("
							+ idsOfContrat + ") and date_part('year', dc.created) ='" + getAnnee() + "' and c.id=dc.id";

					this.nombreContratValide = (Long) queryRunner.query(this.connection, queryFactureNbr,
							(ResultSetHandler) new ScalarHandler());

				} catch (SQLException se) {
					throw new RuntimeException("Couldn't query the database soukaina ........", se);
				}

			} else {

				log.error("listOfFactureNbr NULL .......................");
			}
		}

		return this.nombreContratValide;

	}

	public Long getnombreContratExpire() {
		contratNXQL();
		if (idsOfContrat.equals("")) {
			this.nombreContratExpire = 0L;

		} else {
			if (this.connection != null) {
				try {
					QueryRunner queryRunner = new QueryRunner();
					String queryFactureNbr = "select count(c.id) from Contrat c, dublincore dc where c.statu='label.expired' and c.id IN ("
							+ idsOfContrat + ") and date_part('year', dc.created) ='" + getAnnee() + "' and c.id=dc.id";

					this.nombreContratExpire = (Long) queryRunner.query(this.connection, queryFactureNbr,
							(ResultSetHandler) new ScalarHandler());

				} catch (SQLException se) {
					throw new RuntimeException("Couldn't query the database soukaina ........", se);
				}

			} else {

				log.error("listOfFactureNbr NULL .......................");
			}
		}
		return this.nombreContratExpire;

	}

	public Long getnombreContratCreer() {
		contratNXQL();
		if (idsOfContrat.equals("")) {
			this.nombreContratCreer = 0L;
		} else {
			if (this.connection != null) {
				try {

					QueryRunner queryRunner = new QueryRunner();
					String queryFactureNbr = "select count(c.id) from Contrat c,dublincore dc where c.id IN ("
							+ idsOfContrat + ") and date_part('year', dc.created) ='" + getAnnee() + "' and c.id=dc.id";

					if (nombreContratCreer == null) {
						this.nombreContratCreer = 0L;
					}

					else {
						this.nombreContratCreer = (Long) queryRunner.query(this.connection, queryFactureNbr,
								(ResultSetHandler) new ScalarHandler());
					}
				} catch (SQLException se) {
					throw new RuntimeException("Couldn't query the database ........", se);
				}
			} else {
				log.error("listOfFactureNbr NULL .......................");
			}
		}
		return this.nombreContratCreer;
	}

}

/*
 * Class : Les Statistique de contrat xhtml : Graphes_contrat.xhtml Java
 * compiler version: 8 (52.0) JD-Core Version: 1.1.1
 */