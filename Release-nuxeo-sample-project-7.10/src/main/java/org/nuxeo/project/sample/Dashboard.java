package org.nuxeo.project.sample;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import org.nuxeo.ecm.user.center.dashboard.jsf.JSFDashboardActions;
import org.postgresql.util.PGInterval;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.google.gson.Gson;

@Scope(ScopeType.CONVERSATION)
@Name("Dashboard")
public class Dashboard implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(Dashboard.class);

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
//	  @In(create = true)
//	  protected CoreSession session;

	List<Map<String, Object>> listFactureVAlide;

	protected List<Map<String, Object>> listOftotalparMois;

	protected List<Map<String, Object>> listOftotalparMoisAVG;
	protected List<Map<String, Object>> listOftotalparMoisAnneeValid;
	protected List<Map<String, Object>> listOftotalparMoisAnneeValid1;

	protected TreeMap<Double, String> Moiss = new TreeMap<>();
	protected List<Map<String, Object>> maps = new ArrayList<>();
	protected List<Map<String, Object>> maps1 = new ArrayList<>();

	protected Long nombreFacture = Long.valueOf(0L);
	protected Long factureValide = Long.valueOf(0L);
	protected String montantFacture = "";

	protected PGInterval dateMoyenneFacture;
	public static short annee = (short) Calendar.getInstance().get(1);
	protected String domainName = JSFDashboardActions.getMyDomainSelected();
	protected String DomaineName = "/Brinks Maroc";

	public String getdomainName() {
		return JSFDashboardActions.getMyDomainSelected();
	}

	public void setdomainName(String domainName) {
		this.domainName = domainName;
	}

	// public String getdomainName() {
//		return JSFDashboardActions.getMyDomainSelected();
//	}
//  public void setdomainName(String domainName) {
//		this.domainName = domainName;
//	}

//		public boolean isAdministrator() {
//			 Principal currentUser = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
//			    boolean nxUser = ((NuxeoPrincipal)currentUser).isAdministrator();
//		 return nxUser;
//		}
//		 public String  adminMetohde() {
//			  String Requete="";
//			 if(isAdministrator()){
//					Requete="and ecm:path STARTSWITH '"+getdomainName()+"'";
//					  System.out.println("le test 1 "+Requete);
//			    	  System.out.println("-------------------------------------------------------------------");
//				}
//			 return Requete;
// 			 }

//		  String Requete1=adminMetohde();

	protected String query = "SELECT * FROM Document WHERE ecm:primaryType = 'FactureFournisseur' AND ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0 AND ecm:currentLifeCycleState != 'deleted'";
	protected String queryDa = "SELECT * FROM Document WHERE ecm:primaryType = 'da' AND ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0 AND ecm:currentLifeCycleState != 'deleted'";

	protected String codesFacture = "";
	protected String codesDa = "";

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

	public void factureNxql() {
		DocumentModelList docs = documentManager.query(query);
		if (docs != null) {
			for (DocumentModel documentModel : docs) {

				codesFacture = codesFacture + "'" + documentModel.getId() + "'" + ",";

			}
			if (codesFacture.length() > 0) {
				codesFacture = codesFacture.substring(0, codesFacture.length() - 1);
			} else {

			}
		}
	}

	public void factureNxqlDa() {
		DocumentModelList docs = documentManager.query(queryDa);
		if (docs != null) {
			for (DocumentModel documentModel : docs) {

				codesDa = codesDa + "'" + documentModel.getId() + "'" + ",";

			}
			if (codesDa.length() > 0) {
				codesDa = codesDa.substring(0, codesDa.length() - 1);
			} else {

			}
		}
	}

//la page dashboard.xhtml 
// nombre du  factures en traitemnt
// nombre du  da en traitemnt

	public Long getNombreFacture() {

		factureNxql();
		factureNxqlDa();
		if (getdomainName().equals(DomaineName)) { 
			if (codesDa.equals("")) {
				this.nombreFacture = 0L;

			} else {
				if (this.connection != null) {
					try {
						QueryRunner queryRunner = new QueryRunner();
						String queryFactureNbr = "select count(d.id) from da d where  d.statuworkflow='En traitement' and id IN ("
								+ codesDa + ")";
						this.nombreFacture = (Long) queryRunner.query(this.connection, queryFactureNbr,
								(ResultSetHandler) new ScalarHandler());
					} catch (SQLException se) {
						throw new RuntimeException("Couldn't query the database soukaina ........", se);
					}

				} else {

					log.error("listOfFactureNbr NULL .......................");
				}
			}
			return this.nombreFacture;
		} else { 
			if (codesFacture.equals("")) {
				this.nombreFacture = 0L; 
			} else {
				if (this.connection != null) {
					try {
						QueryRunner queryRunner = new QueryRunner();
						String queryFactureNbr = "select count(f.id) from FactureFournisseur f where  f.statutfacture='En traitement' and id IN ("
								+ codesFacture + ")";
						this.nombreFacture = (Long) queryRunner.query(this.connection, queryFactureNbr,
								(ResultSetHandler) new ScalarHandler());
					} catch (SQLException se) {
						throw new RuntimeException("Couldn't query the database soukaina ........", se);
					}

				} else {

					log.error("listOfFactureNbr NULL .......................");
				}
			}

			return this.nombreFacture;
		}

	}

//nombre du factue valide
//nombre du da valide
	public Long getFactureValide() {
		factureNxql();
		factureNxqlDa();
		if (getdomainName().equals(DomaineName)) {
			System.out.println("------- nombre du DA valide   ---------- ");
			if (codesDa.equals("")) {
				this.nombreFacture = 0L;
			} else {

				if (this.connection != null) {

					try {

						QueryRunner queryRunner = new QueryRunner();
						String queryFactureNbr = "select count(d.id) from da d where  d.statuworkflow='Validé' and id IN ("
								+ codesDa + ")";
						if (nombreFacture == null) {
							this.nombreFacture = 0L;
						}

						else {
							this.factureValide = (Long) queryRunner.query(this.connection, queryFactureNbr,
									(ResultSetHandler) new ScalarHandler());
						}
					} catch (SQLException se) {
						throw new RuntimeException("Couldn't query the database ........", se);
					}

				} else {

					log.error("listOfFactureNbr NULL .......................");
				}

			}

			return this.factureValide;

		} else {
			if (codesFacture.equals("")) {
				this.nombreFacture = 0L;
			} else {
				if (this.connection != null) {

					try {

						QueryRunner queryRunner = new QueryRunner();
						String queryFactureNbr = "select count(f.id) from facturefournisseur f where  f.statutfacture='Validé' and id IN ("
								+ codesFacture + ")";
						if (nombreFacture == null) {
							this.nombreFacture = 0L;
						}

						else {
							this.factureValide = (Long) queryRunner.query(this.connection, queryFactureNbr,
									(ResultSetHandler) new ScalarHandler());
						}
					} catch (SQLException se) {
						throw new RuntimeException("Couldn't query the database soukaina ........", se);
					}

				} else {

					log.error("listOfFactureNbr NULL .......................");
				}

			}

			return this.factureValide;

		}
	}

// Montant du factures en traitement 
// Montant du da en traitement 
	public String getMontantFacture() {
		DecimalFormat df = new DecimalFormat("#.##");
		factureNxql();
		factureNxqlDa();
		if (getdomainName().equals(DomaineName)) { 
			if (codesDa.equals("")) {
				this.nombreFacture = 0L;
			} else {
				if (this.connection != null) {

					try {
						//
						System.out.println("\n\nnew QueryRunner() : " + new QueryRunner());
						QueryRunner queryRunner = new QueryRunner();
						String queryFactureNbr = "select SUM(d.total_lignes) from da d where  d.statuworkflow='En traitement' and d.id IN ("
								+ codesDa + ")";

						Double sum = (Double) queryRunner.query(this.connection, queryFactureNbr,
								(ResultSetHandler) new ScalarHandler());

						if (sum == null) {
							this.montantFacture = df.format(0L);
						}

						else {
							this.montantFacture = df.format(sum);
						}

					}

					catch (SQLException se) {
						throw new RuntimeException("Couldn't query the database ........", se);
					}

				} else {

					log.error("listOfFactureNbr NULL .......................");
				}

			}

			return this.montantFacture;

		} else { 
			if (codesFacture.equals("")) {
				this.nombreFacture = 0L;
			} else {
				if (this.connection != null) {

					try {
						QueryRunner queryRunner = new QueryRunner();
						String queryFactureNbr = "select SUM(f.totalht) from facturefournisseur f where  f.statutfacture='En traitement' and f.id IN ("
								+ codesFacture + ")";
						Double sum = (Double) queryRunner.query(this.connection, queryFactureNbr,
								(ResultSetHandler) new ScalarHandler());

						if (sum == null) {
							this.montantFacture = df.format(0L);
						}

						else {
							this.montantFacture = df.format(sum);
						}

					}

					catch (SQLException se) {
						throw new RuntimeException("Couldn't query the database soukaina ........", se);
					}

				} else {

					log.error("listOfFactureNbr NULL .......................");
				}

			}

			return this.montantFacture;

		}

	}

	public short getAnnee() {

		return this.annee;
	}

	public void setAnnee(short annee) {
		this.annee = annee;
	}

//  le tempe moyenne du facture valide par jour
//  le tempe moyenne du da valide par jour

	public Integer getDateMoyenneFacture() {
		factureNxql();
		factureNxqlDa();
		Integer calcul = Integer.valueOf(0);
		if (getdomainName().equals(DomaineName)) {

			if (codesDa.equals("")) {
				this.nombreFacture = 0L;
			} else {
				if (this.connection != null) {

					try {
						QueryRunner queryRunner = new QueryRunner();
						String queryFactureNbr = "select AVG( date_validate - created) from Dublincore,da where date_validate is not null and Dublincore.id=da.id and statuworkflow='Validé' and da.id IN ("
								+ codesDa + ")";
						this.dateMoyenneFacture = (PGInterval) queryRunner.query(this.connection, queryFactureNbr,
								(ResultSetHandler) new ScalarHandler());
						if (this.dateMoyenneFacture != null) {
							if (this.dateMoyenneFacture.getYears() != 0) {
								calcul = Integer.valueOf(calcul.intValue() + this.dateMoyenneFacture.getYears() * 365);
							}
							if (this.dateMoyenneFacture.getMonths() != 0) {
								calcul = Integer.valueOf(calcul.intValue() + this.dateMoyenneFacture.getMonths() * 30);
							}
							if (this.dateMoyenneFacture.getDays() != 0) {
								calcul = Integer.valueOf(calcul.intValue() + this.dateMoyenneFacture.getDays());
							}
						}

					} catch (SQLException se) {
						throw new RuntimeException("Couldn't query the database soukaina ........", se);
					}

				} else {

					log.error("listOfFactureNbr NULL .......................");
				}

			}
			return calcul;
		} else {

			if (codesFacture.equals("")) {
				this.nombreFacture = 0L;
			} else {
				if (this.connection != null) {

					try {
						QueryRunner queryRunner = new QueryRunner();
						String queryFactureNbr = "select AVG( date_validate - created) from Dublincore,facturefournisseur where date_validate is not null and Dublincore.id=facturefournisseur.id and statutfacture='Validé' and facturefournisseur.id IN ("
								+ codesFacture + ")";
						this.dateMoyenneFacture = (PGInterval) queryRunner.query(this.connection, queryFactureNbr,
								(ResultSetHandler) new ScalarHandler());
						if (this.dateMoyenneFacture != null) {
							if (this.dateMoyenneFacture.getYears() != 0) {
								calcul = Integer.valueOf(calcul.intValue() + this.dateMoyenneFacture.getYears() * 365);
							}
							if (this.dateMoyenneFacture.getMonths() != 0) {
								calcul = Integer.valueOf(calcul.intValue() + this.dateMoyenneFacture.getMonths() * 30);
							}
							if (this.dateMoyenneFacture.getDays() != 0) {
								calcul = Integer.valueOf(calcul.intValue() + this.dateMoyenneFacture.getDays());
							}
						}

					} catch (SQLException se) {
						throw new RuntimeException("Couldn't query the database soukaina ........", se);
					}

				} else {

					log.error("listOfFactureNbr NULL .......................");
				}

			}
			return calcul;
		}

	}

//la page graphes.xhtml 
	protected Statement stmt = null;
	protected ResultSet resultSet = null;

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

//  Montant des factures en traitement par mois 
	public String getlistOftotalparMoisAnnee() { 
		factureNxql();
		factureNxqlDa();
		this.listOftotalparMois = null;
		this.maps = new ArrayList<>();
		this.maps1 = new ArrayList<>();
		DecimalFormat df2 = new DecimalFormat("#.##");
		if (getdomainName().equals(DomaineName)) {
			if (codesDa.equals("")) {
				this.nombreFacture = 0L;
			} else {
				if (this.connection != null) {
 
					String queryTotalSalaire = "select  date_part('month', d.created) ,total_lignes from da f,dublincore d  where statuworkflow='En traitement' and date_part('year', d.created) ='"
							+ getAnnee() + "'and d.id=f.id and f.id IN (" + codesDa + ")";

					try {
						QueryRunner queryRunner = new QueryRunner();
						this.listOftotalparMois = (List<Map<String, Object>>) queryRunner.query(this.connection,
								queryTotalSalaire, (ResultSetHandler) new MapListHandler());

					} catch (SQLException se) {

						se = se;
						throw new RuntimeException("Couldn't query the database ........", se);
					}

				} else {

					log.error("buttonJsonDA NULL .......................");
				}

				for (Double key : getMoiss().keySet()) {

					int montant = 0;
					for (int j = 0; j < this.listOftotalparMois.size(); j++) {
						double result = 0.0D;

						for (Map.Entry<String, Object> entry1 : (Iterable<Map.Entry<String, Object>>) ((Map) this.listOftotalparMois
								.get(j)).entrySet()) {

							if (((String) entry1.getKey()).equals("date_part")) {
								result = ((Double) entry1.getValue()).doubleValue();
							}
							if (((String) entry1.getKey()).equals("total_lignes") && entry1.getValue() != null
									&& result == key.doubleValue()) {
								if (entry1.getValue() instanceof Integer) {

									montant += ((Integer) entry1.getValue()).intValue();
									continue;
								}
								if (entry1.getValue() instanceof Long) {

									Long salLong = new Long(montant);
									salLong = Long
											.valueOf(salLong.longValue() + ((Long) entry1.getValue()).longValue());
									montant = salLong.intValue();
									continue;
								}
								if (entry1.getValue() instanceof Double) {

									Double salLong = new Double(montant);
									salLong = Double.valueOf(
											salLong.doubleValue() + ((Double) entry1.getValue()).doubleValue());
									montant = salLong.intValue();
								}
							}
						}
					}

					Map<String, Object> map1 = new HashMap<>();
					map1.put("name", getMoiss().get(key));
					map1.put("y", Long.valueOf(montant));
					this.maps.add(map1);
				}
			}

			return (new Gson()).toJson(this.maps);

		} else {
			if (codesFacture.equals("")) {
				this.nombreFacture = 0L;
			} else {
				if (this.connection != null) {

					String queryTotalSalaire = "select  date_part('month', d.created) ,totalht from facturefournisseur f,dublincore d  where statutfacture='En traitement' and date_part('year', d.created) ='"
							+ getAnnee() + "'and d.id=f.id and f.id IN (" + codesFacture + ")";

					try {
						QueryRunner queryRunner = new QueryRunner();
						this.listOftotalparMois = (List<Map<String, Object>>) queryRunner.query(this.connection,
								queryTotalSalaire, (ResultSetHandler) new MapListHandler());

					} catch (SQLException se) {

						se = se;
						throw new RuntimeException("Couldn't query the database ........", se);
					}

				} else {

					log.error("buttonJsonDA NULL .......................");
				}

				for (Double key : getMoiss().keySet()) {

					int montant = 0;
					for (int j = 0; j < this.listOftotalparMois.size(); j++) {
						double result = 0.0D;

						for (Map.Entry<String, Object> entry1 : (Iterable<Map.Entry<String, Object>>) ((Map) this.listOftotalparMois
								.get(j)).entrySet()) {

							if (((String) entry1.getKey()).equals("date_part")) {
								result = ((Double) entry1.getValue()).doubleValue();
							}
							if (((String) entry1.getKey()).equals("totalht") && entry1.getValue() != null
									&& result == key.doubleValue()) {
								if (entry1.getValue() instanceof Integer) {

									montant += ((Integer) entry1.getValue()).intValue();
									continue;
								}
								if (entry1.getValue() instanceof Long) {

									Long salLong = new Long(montant);
									salLong = Long
											.valueOf(salLong.longValue() + ((Long) entry1.getValue()).longValue());
									montant = salLong.intValue();
									continue;
								}
								if (entry1.getValue() instanceof Double) {

									Double salLong = new Double(montant);
									salLong = Double.valueOf(
											salLong.doubleValue() + ((Double) entry1.getValue()).doubleValue());
									montant = salLong.intValue();
								}
							}
						}
					}

					Map<String, Object> map1 = new HashMap<>();
					map1.put("name", getMoiss().get(key));
					map1.put("y", Long.valueOf(montant));
					this.maps.add(map1);
				}
			}

			return (new Gson()).toJson(this.maps);
		}

	}

	public int getSumMontant(short year) {

		int sum = 0;
		try {
			this.stmt = getConnection().createStatement();
			this.resultSet = this.stmt.executeQuery(
					"select COALESCE(sum(f.totalht), 0) as sum from facturefournisseur f,dublincore d, hierarchy h WHERE f.statutfacture='En traitement' AND date_part('year', d.created) <'"
							+ year + "' AND d.id=f.id AND h.id = f.id and h.name not like '%.trashed' ");
			while (this.resultSet.next()) {
				sum = this.resultSet.getInt("sum");
			}
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

		return sum;
	}

	public int getSumMontantValid(short year) {

		int sum = 0;
		try {
			this.stmt = getConnection().createStatement();
			this.resultSet = this.stmt.executeQuery(
					"select count(*) sum from facturefournisseur f,dublincore d, hierarchy h WHERE f.statutfacture='Validé' AND date_part('year', d.created) <'"
							+ year + "' AND d.id=f.id AND h.id = f.id and h.name not like '%.trashed'");
			while (this.resultSet.next()) {
				sum = this.resultSet.getInt("sum");
			}
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

		return sum;
	}

//  les factures valides par mois 
	public String getlistOftotalparMoisAnneeValid() { 
		factureNxql();
		factureNxqlDa();
		this.listOftotalparMoisAnneeValid = null;
		this.maps = new ArrayList<>();
		this.maps1 = new ArrayList<>();

		if (getdomainName().equals(DomaineName)) {
			if (codesDa.equals("")) {
				this.nombreFacture = 0L;
			} else {
				if (this.connection != null) {

					String querycount = "select  date_part('month', d.created) ,count(*) nombrefacture from da f,dublincore d  where statuworkflow='Validé' and f.id IN ("
							+ codesDa + ") and date_part('year', d.created) ='" + getAnnee()
							+ "'and d.id=f.id group by date_part('month', d.created) ";
					try {
						QueryRunner queryRunner = new QueryRunner();
						this.listOftotalparMoisAnneeValid = (List<Map<String, Object>>) queryRunner
								.query(this.connection, querycount, (ResultSetHandler) new MapListHandler());

					} catch (SQLException se) {

						se = se;
						throw new RuntimeException("Couldn't query the database ........", se);
					}

				} else {

					log.error("buttonJsonDA NULL .......................");
				}
				int result = 0;
				for (Double key : getMoiss().keySet()) {

					int montant = 0;

					for (int j = 0; j < this.listOftotalparMoisAnneeValid.size();) {

						for (Map.Entry<String, Object> entry1 : (Iterable<Map.Entry<String, Object>>) ((Map) this.listOftotalparMoisAnneeValid
								.get(j)).entrySet()) {

							if (((String) entry1.getKey()).equals("date_part")) {

								result = Integer.valueOf(((Double) entry1.getValue()).intValue()).intValue();
							}
							if (((String) entry1.getKey()).equals("nombrefacture") && entry1.getValue() != null
									&& result == key.doubleValue()) {
								if (entry1.getValue() instanceof Integer) {

									montant += ((Integer) entry1.getValue()).intValue();
									continue;
								}
								if (entry1.getValue() instanceof Long) {

									Long salLong = new Long(montant);
									salLong = Long
											.valueOf(salLong.longValue() + ((Long) entry1.getValue()).longValue());
									montant = salLong.intValue();
								}
							}
						}

						j++;
					}

					Map<String, Object> map1 = new HashMap<>();
					map1.put("name", getMoiss().get(key));
					map1.put("y", Long.valueOf(montant));
					this.maps.add(map1);
				}
			}

			return (new Gson()).toJson(this.maps);
		} else {
			if (codesFacture.equals("")) {
				this.nombreFacture = 0L;
			} else {
				if (this.connection != null) {

					String querycount = "select  date_part('month', d.created) ,count(*) nombrefacture from facturefournisseur f,dublincore d  where statutfacture='Validé' and f.id IN ("
							+ codesFacture + ") and date_part('year', d.created) ='" + getAnnee()
							+ "'and d.id=f.id group by date_part('month', d.created) ";
					try {
						QueryRunner queryRunner = new QueryRunner();
						this.listOftotalparMoisAnneeValid = (List<Map<String, Object>>) queryRunner
								.query(this.connection, querycount, (ResultSetHandler) new MapListHandler());

					} catch (SQLException se) {

						se = se;
						throw new RuntimeException("Couldn't query the database ........", se);
					}

				} else {

					log.error("buttonJsonDA NULL .......................");
				}
				int result = 0;
				for (Double key : getMoiss().keySet()) {

					int montant = 0;

					for (int j = 0; j < this.listOftotalparMoisAnneeValid.size();) {

						for (Map.Entry<String, Object> entry1 : (Iterable<Map.Entry<String, Object>>) ((Map) this.listOftotalparMoisAnneeValid
								.get(j)).entrySet()) {

							if (((String) entry1.getKey()).equals("date_part")) {

								result = Integer.valueOf(((Double) entry1.getValue()).intValue()).intValue();
							}
							if (((String) entry1.getKey()).equals("nombrefacture") && entry1.getValue() != null
									&& result == key.doubleValue()) {
								if (entry1.getValue() instanceof Integer) {

									montant += ((Integer) entry1.getValue()).intValue();
									continue;
								}
								if (entry1.getValue() instanceof Long) {

									Long salLong = new Long(montant);
									salLong = Long
											.valueOf(salLong.longValue() + ((Long) entry1.getValue()).longValue());
									montant = salLong.intValue();
								}
							}
						}

						j++;
					}

					Map<String, Object> map1 = new HashMap<>();
					map1.put("name", getMoiss().get(key));
					map1.put("y", Long.valueOf(montant));
					this.maps.add(map1);
				}
			}

			return (new Gson()).toJson(this.maps);
		}

	}

	public String getlistOftotalparMoisAnneeValid1() {
		factureNxql();
		factureNxqlDa();
		this.listOftotalparMoisAnneeValid1 = null;
		this.maps = new ArrayList<>();
		this.maps1 = new ArrayList<>();

		if (getdomainName().equals(DomaineName)) {
			if (codesDa.equals("")) {
				this.nombreFacture = 0L;
			} else {
				if (this.connection != null) {

					String querycount = "select  date_part('month', d.created) ,count(*) nombrefacture from da f,dublincore d  where statuworkflow='En traitement' and f.id IN ("
							+ codesDa + ") and date_part('year', d.created) ='" + getAnnee()
							+ "'and d.id=f.id group by date_part('month', d.created) ";
					try {
						QueryRunner queryRunner = new QueryRunner();
						this.listOftotalparMoisAnneeValid1 = (List<Map<String, Object>>) queryRunner
								.query(this.connection, querycount, (ResultSetHandler) new MapListHandler());

					} catch (SQLException se) {

						se = se;
						throw new RuntimeException("Couldn't query the database ........", se);
					}

				} else {

					log.error("buttonJsonDA NULL .......................");
				}
				int result = 0;
				for (Double key : getMoiss().keySet()) {

					int montant = 0;

					for (int j = 0; j < this.listOftotalparMoisAnneeValid1.size();) {

						for (Map.Entry<String, Object> entry1 : (Iterable<Map.Entry<String, Object>>) ((Map) this.listOftotalparMoisAnneeValid1
								.get(j)).entrySet()) {

							if (((String) entry1.getKey()).equals("date_part")) {

								result = Integer.valueOf(((Double) entry1.getValue()).intValue()).intValue();
							}
							if (((String) entry1.getKey()).equals("nombrefacture") && entry1.getValue() != null
									&& result == key.doubleValue()) {
								if (entry1.getValue() instanceof Integer) {

									montant += ((Integer) entry1.getValue()).intValue();
									continue;
								}
								if (entry1.getValue() instanceof Long) {

									Long salLong = new Long(montant);
									salLong = Long
											.valueOf(salLong.longValue() + ((Long) entry1.getValue()).longValue());
									montant = salLong.intValue();
								}
							}
						}

						j++;
					}

					Map<String, Object> map1 = new HashMap<>();
					map1.put("name", getMoiss().get(key));
					map1.put("y", Long.valueOf(montant));
					this.maps.add(map1);
				}
			}

			return (new Gson()).toJson(this.maps);
		} else {
			if (codesFacture.equals("")) {
				this.nombreFacture = 0L;
			} else {
				if (this.connection != null) {

					String querycount = "select  date_part('month', d.created) ,count(*) nombrefacture from facturefournisseur f,dublincore d  where statutfacture='Validé' and f.id IN ("
							+ codesFacture + ") and date_part('year', d.created) ='" + getAnnee()
							+ "'and d.id=f.id group by date_part('month', d.created) ";
					try {
						QueryRunner queryRunner = new QueryRunner();
						this.listOftotalparMoisAnneeValid1 = (List<Map<String, Object>>) queryRunner
								.query(this.connection, querycount, (ResultSetHandler) new MapListHandler());

					} catch (SQLException se) {

						se = se;
						throw new RuntimeException("Couldn't query the database ........", se);
					}

				} else {

					log.error("buttonJsonDA NULL .......................");
				}
				int result = 0;
				for (Double key : getMoiss().keySet()) {

					int montant = 0;

					for (int j = 0; j < this.listOftotalparMoisAnneeValid1.size();) {

						for (Map.Entry<String, Object> entry1 : (Iterable<Map.Entry<String, Object>>) ((Map) this.listOftotalparMoisAnneeValid1
								.get(j)).entrySet()) {

							if (((String) entry1.getKey()).equals("date_part")) {
								result = Integer.valueOf(((Double) entry1.getValue()).intValue()).intValue();
							}
							if (((String) entry1.getKey()).equals("nombrefacture") && entry1.getValue() != null
									&& result == key.doubleValue()) {
								if (entry1.getValue() instanceof Integer) {
									montant += ((Integer) entry1.getValue()).intValue();
									continue;
								}
								if (entry1.getValue() instanceof Long) {
									Long salLong = new Long(montant);
									salLong = Long
											.valueOf(salLong.longValue() + ((Long) entry1.getValue()).longValue());
									montant = salLong.intValue();
								}
							}
						}

						j++;
					}

					Map<String, Object> map1 = new HashMap<>();
					map1.put("name", getMoiss().get(key));
					map1.put("y", Long.valueOf(montant));
					this.maps.add(map1);

				}
			}

			return (new Gson()).toJson(this.maps);
		}

	}

	public Integer getSumMoyenTime(short year) {

		Integer sum = Integer.valueOf(0);
		try {
			QueryRunner queryRunner = new QueryRunner();
			String queryFactureNbr = "select AVG( date_validate - created) sum from facturefournisseur f,dublincore d, hierarchy h WHERE f.statutfacture='Validé' AND date_part('year', d.created) <'\" + year + \"' AND d.id=f.id AND h.id = f.id and h.name not like '%.trashed' ";
			PGInterval dateMoyenne = (PGInterval) queryRunner.query(this.connection, queryFactureNbr,
					(ResultSetHandler) new ScalarHandler());
			if (dateMoyenne.getYears() != 0) {
				sum = Integer.valueOf(sum.intValue() + dateMoyenne.getYears() * 365);
			}
			if (this.dateMoyenneFacture.getMonths() != 0) {
				sum = Integer.valueOf(sum.intValue() + dateMoyenne.getMonths() * 30);
			}
			if (this.dateMoyenneFacture.getDays() != 0) {
				sum = Integer.valueOf(sum.intValue() + dateMoyenne.getDays());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sum;
	}

	public String getlistOftotalparMoisAVG() {

		this.listOftotalparMoisAVG = null;
		this.maps = new ArrayList<>();
		this.maps1 = new ArrayList<>();
		if (this.connection != null) {

			String querycountValid = "select date_part('month', d.created) ,AVG( date_validate - created) tempsmoyen from facturefournisseur f,dublincore d  where statutfacture='Validé' and date_part('year', d.created) ='"
					+ getAnnee() + "'and d.id=f.id group by date_part('month', d.created) ";

			try {
				QueryRunner queryRunner = new QueryRunner();
				this.listOftotalparMoisAVG = (List<Map<String, Object>>) queryRunner.query(this.connection,
						querycountValid, (ResultSetHandler) new MapListHandler());

			} catch (SQLException se) {

				se = se;
				throw new RuntimeException("Couldn't query the database ........", se);
			}

		} else {

			log.error("buttonJsonDA NULL .......................");
		}
		int result = 0;
		for (Iterator<Double> iterator = getMoiss().keySet().iterator(); iterator.hasNext();) {
			double key = ((Double) iterator.next()).doubleValue();

			int mois = (int) key;
			int montant = 0;

			for (int j = 0; j < this.listOftotalparMoisAVG.size();) {
				for (Map.Entry<String, Object> entry1 : (Iterable<Map.Entry<String, Object>>) ((Map) this.listOftotalparMoisAVG
						.get(j)).entrySet()) {

					if (((String) entry1.getKey()).equals("date_part")) {

						result = Integer.valueOf(((Double) entry1.getValue()).intValue()).intValue();
					}
					if (((String) entry1.getKey()).equals("tempsmoyen") && entry1.getValue() != null) {

						if (result == mois) {

							PGInterval dateMoyenne = (PGInterval) entry1.getValue();

							if (dateMoyenne.getYears() != 0) {
								montant += dateMoyenne.getYears() * 365;
							}
							if (dateMoyenne.getMonths() != 0) {
								montant += dateMoyenne.getMonths() * 30;
							}
							if (dateMoyenne.getDays() != 0) {
								montant += dateMoyenne.getDays();
							}
						}
					}

				}

				j++;
			}

			Map<String, Object> map1 = new HashMap<>();

			map1.put("name", getMoiss().get(Double.valueOf(key)));
			map1.put("y", Long.valueOf(montant));
			this.maps.add(map1);
		}

		return (new Gson()).toJson(this.maps);
	}
	// Les Documents En traitement par mois

}

/*
 * class Dashboard : table de bord de la GED Créer par ayoub berachad la page
 * xhtml : Graphes.xhtml and user_jsf_dashboard.xhtml Java compiler version: 8
 * (52.0) JD-Core Version: 1.1.1
 */