package org.nuxeo.project.sample;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.google.gson.Gson;

@ManagedBean(name = "highchartDemo")
@SessionScoped
public class HighchartDemo {

	private static final Log log = LogFactory.getLog(HighchartDemo.class);

	Connection connection;

	// Chart1
	private String listOfFNbrT;
	private String listOfFNbrV;
	private String listOfFNbrP;

	List<Map<String, Object>> listOfFactureNbrV;
	List<Map<String, Object>> listOfFactureNbrT;
	List<Map<String, Object>> listOfFactureNbrP;

	// Chart3
	private String workflownombreFV;

	List<Map<String, Object>> workflownombrefactureV;

	// Chart2
	private String listOfWorkflowsNbr;
	private String listOfWorkflowsAvg;
	private String listOfWorkflowsMin;
	private String listOfWorkflowsMax;
	
	List<Map<String, Object>> listOfWorkflowAvg;
	List<Map<String, Object>> listOfWorkflowMax;
	List<Map<String, Object>> listOfWorkflowMin;
	List<Map<String, Object>> listOfWorkflowNbr;
	
	// Chart4
	private String listOfWorkflowsRJ;
	private String workflowsREJETMin;
	private String workflowsREJETMax;
	private String workflowsREJETAvg;
	
	List<Map<String, Object>> listOfWorkflowRJ;
	List<Map<String, Object>> workflowREJETMin;
	List<Map<String, Object>> workflowREJETMax;
	List<Map<String, Object>> workflowREJETAvg;

	public HighchartDemo() {

		// Chart1
		listOfFactureNbrV = new ArrayList<>();
		listOfFactureNbrT = new ArrayList<>();
		listOfFactureNbrP = new ArrayList<>();

		// Chart3
		workflownombrefactureV = new ArrayList<>();

		// Chart2
		listOfWorkflowAvg = new ArrayList<>();
		listOfWorkflowMax = new ArrayList<>();
		listOfWorkflowMin = new ArrayList<>();
		listOfWorkflowNbr = new ArrayList<>();
		
		// Chart4
		this.listOfWorkflowRJ = new ArrayList<>();
		this.workflowREJETMin = new ArrayList<>();
		this.workflowREJETMax = new ArrayList<>();
		this.workflowREJETAvg = new ArrayList<>();
	}

	public void buttonJsonDemo() {
		if (this.connection != null) {
			log.error("buttonJsonDemo NOT NULL .......................");

			// Chart1
			//String queryFactureNbrV = "select group1 as name, count(distinct numfacture) as y from workflowfactureresponsable where statutfacture like '%Valid%' group by group1";
			//String queryFactureNbrT = "select group1 as name, count(distinct numfacture) as y from workflowfactureresponsable where statutfacture like '%En traitement%' group by group1";
			//String queryFactureNbrP = "select valideurfinal as name, count(distinct numerofacture) as y from facturefournisseur  f, hierarchy h where f.id=h.id and name not like '%.trashed' and  h.parentid='cf29a679-40dd-4094-97a0-e7cf2f4ef692' and statutfacture like '%Rej%'  group by valideurfinal";

			// Chart3
			String queryWorkflownombrefactureV = "SELECT f.statutfacture as name,round(count(f.id)*100/(select count(facturefournisseur.id) from facturefournisseur,hierarchy h where facturefournisseur.id=h.id and h.parentid='cf29a679-40dd-4094-97a0-e7cf2f4ef692' and h.name not like '%.trashed' and  statutfacture is not null ),2) as y  FROM public.facturefournisseur f, hierarchy h where f.id=h.id and name not like '%.trashed' and (h.parentid='cf29a679-40dd-4094-97a0-e7cf2f4ef692') and  statutfacture  is not null group by statutfacture";
			
			// Chart2
			String queryWorkflowNbr = "SELECT count(numfacture) as y,currentuser1 as name from workflownumFactVF  where statutfacture like '%Valid%'  group by currentuser1";
			String queryWorkflowMax = "SELECT currentuser1 as name, max(validation1) as y from workflowHightchart2 where validation1 is not null  group by currentuser1";
			String queryWorkflowMin = "SELECT currentuser1 as name,  min(validation1) as y from workflowHightchart2 where validation1 is not null  group by currentuser1";
			String queryWorkflowAvg = "SELECT currentuser1 as name,avg(validation1) as y from workflowHightchart2 where validation1 is not null group by currentuser1";
			
			// Chart4
			String querydocrejete = "SELECT count(f.id) as y,valideurfinal as name FROM public.facturefournisseur f, hierarchy h where f.id=h.id and name not like '%.trashed' and ( h.parentid='cc50c2fd-2129-4301-a3fe-a0db90cf483f' or h.parentid='cd680778-7717-4593-b346-688e9e0c5061' or h.parentid='cf29a679-40dd-4094-97a0-e7cf2f4ef692') and  statutfacture like '%Reje%'  and valideurfinal is not null group by valideurfinal";
			String queryWorkflowREJETMin = "SELECT valideurfinal as name,  min(validation) as y from workflowHightchartREJET2 where validation is not null  group by valideurfinal";
			String queryWorkflowREJETMax = "SELECT valideurfinal as name,  max(validation) as y from workflowHightchartREJET2 where validation is not null  group by valideurfinal";
			String queryWorkflowREJETAvg = "SELECT valideurfinal as name,  avg(validation) as y from workflowHightchartREJET2 where validation is not null  group by valideurfinal";

			try {
				QueryRunner queryRunner = new QueryRunner();

				// Chart1
			//	this.listOfFactureNbrV = ((List) queryRunner.query(this.connection, queryFactureNbrV,
				//		new MapListHandler()));
			//	this.listOfFactureNbrT = ((List) queryRunner.query(this.connection, queryFactureNbrT,
					//	new MapListHandler()));
			//	this.listOfFactureNbrP = ((List) queryRunner.query(this.connection, queryFactureNbrP,
					//	new MapListHandler()));

				// Chart3
				this.workflownombrefactureV = ((List) queryRunner.query(this.connection, queryWorkflownombrefactureV,
						new MapListHandler()));
				
				// Chart2
				this.listOfWorkflowNbr = ((List) queryRunner.query(this.connection, queryWorkflowNbr,
						new MapListHandler()));
				this.listOfWorkflowMax = ((List) queryRunner.query(this.connection, queryWorkflowMax,
						new MapListHandler()));
				this.listOfWorkflowMin = ((List) queryRunner.query(this.connection, queryWorkflowMin,
						new MapListHandler()));
				this.listOfWorkflowAvg = ((List) queryRunner.query(this.connection, queryWorkflowAvg,
						new MapListHandler()));
				
				// Chart4
				this.listOfWorkflowRJ = ((List) queryRunner.query(this.connection, querydocrejete,
						new MapListHandler()));
				this.workflowREJETMin = ((List) queryRunner.query(this.connection, queryWorkflowREJETMin,
						new MapListHandler()));
				this.workflowREJETMax = ((List) queryRunner.query(this.connection, queryWorkflowREJETMax,
						new MapListHandler()));
				this.workflowREJETAvg = ((List) queryRunner.query(this.connection, queryWorkflowREJETAvg,
						new MapListHandler()));

			} catch (SQLException se) {
				log.error("Couldn't query the database zakaria........", se);
			}
		} else {
			log.error("buttonJsonDemo NULL .......................");
		}
	}

	public void connection() {
		log.error("connexion ....................");
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
				log.error(this.connection.toString());
			} catch (FileNotFoundException fnfe) {
				log.error(fnfe.getMessage(), fnfe);
			} catch (ParserConfigurationException pce) {
				log.error(pce.getMessage(), pce);
			} catch (IOException ioe) {
				log.error(ioe.getMessage(), ioe);
			}
		} catch (ClassNotFoundException cnfe) {
			log.error("Could not find the database driver", cnfe);
		} catch (Exception e) {
			log.error("Could not connect to the database", e);
		}
	}

	// Getters And Setters
	public Connection getConnection() {
		return this.connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public String getListOfFNbrT() {
		buttonJsonDemo();
		return new Gson().toJson(this.listOfFactureNbrT);
	}

	public void setListOfFNbrT(String listOfFNbrT) {
		this.listOfFNbrT = listOfFNbrT;
	}

	public String getListOfFNbrV() {
		buttonJsonDemo();
		return new Gson().toJson(this.listOfFactureNbrV);
	}

	public void setListOfFNbrV(String listOfFNbrV) {
		this.listOfFNbrV = listOfFNbrV;
	}

	public String getListOfFNbrP() {
		buttonJsonDemo();
		return new Gson().toJson(this.listOfFactureNbrP);
	}

	public void setListOfFNbrP(String listOfFNbrP) {
		this.listOfFNbrP = listOfFNbrP;
	}

	public List<Map<String, Object>> getListOfFactureNbrV() {
		return listOfFactureNbrV;
	}

	public void setListOfFactureNbrV(List<Map<String, Object>> listOfFactureNbrV) {
		this.listOfFactureNbrV = listOfFactureNbrV;
	}

	public List<Map<String, Object>> getListOfFactureNbrT() {
		return listOfFactureNbrT;
	}

	public void setListOfFactureNbrT(List<Map<String, Object>> listOfFactureNbrT) {
		this.listOfFactureNbrT = listOfFactureNbrT;
	}

	public List<Map<String, Object>> getListOfFactureNbrP() {
		return listOfFactureNbrP;
	}

	public void setListOfFactureNbrP(List<Map<String, Object>> listOfFactureNbrP) {
		this.listOfFactureNbrP = listOfFactureNbrP;
	}

	public List<Map<String, Object>> getWorkflownombrefactureV() {
		return workflownombrefactureV;
	}

	public void setWorkflownombrefactureV(List<Map<String, Object>> workflownombrefactureV) {
		this.workflownombrefactureV = workflownombrefactureV;
	}

	public String getWorkflownombreFV() {
		buttonJsonDemo();
		return new Gson().toJson(this.workflownombrefactureV);
	}

	public void setWorkflownombreFV(String workflownombreFV) {
		this.workflownombreFV = workflownombreFV;
	}

	public String getListOfWorkflowsNbr() {
		buttonJsonDemo();
		return new Gson().toJson(this.listOfWorkflowNbr);
	}

	public void setListOfWorkflowsNbr(String listOfWorkflowsNbr) {
		this.listOfWorkflowsNbr = listOfWorkflowsNbr;
	}

	public String getListOfWorkflowsAvg() {
		buttonJsonDemo();
		return new Gson().toJson(this.listOfWorkflowAvg);
	}

	public void setListOfWorkflowsAvg(String listOfWorkflowsAvg) {
		this.listOfWorkflowsAvg = listOfWorkflowsAvg;
	}

	public String getListOfWorkflowsMin() {
		buttonJsonDemo();
		return new Gson().toJson(this.listOfWorkflowMin);
	}

	public void setListOfWorkflowsMin(String listOfWorkflowsMin) {
		this.listOfWorkflowsMin = listOfWorkflowsMin;
	}

	public String getListOfWorkflowsMax() {
		buttonJsonDemo();
		return new Gson().toJson(this.listOfWorkflowMax);
	}

	public void setListOfWorkflowsMax(String listOfWorkflowsMax) {
		this.listOfWorkflowsMax = listOfWorkflowsMax;
	}

	public List<Map<String, Object>> getListOfWorkflowAvg() {
		return listOfWorkflowAvg;
	}

	public void setListOfWorkflowAvg(List<Map<String, Object>> listOfWorkflowAvg) {
		this.listOfWorkflowAvg = listOfWorkflowAvg;
	}

	public List<Map<String, Object>> getListOfWorkflowMax() {
		return listOfWorkflowMax;
	}

	public void setListOfWorkflowMax(List<Map<String, Object>> listOfWorkflowMax) {
		this.listOfWorkflowMax = listOfWorkflowMax;
	}

	public List<Map<String, Object>> getListOfWorkflowMin() {
		return listOfWorkflowMin;
	}

	public void setListOfWorkflowMin(List<Map<String, Object>> listOfWorkflowMin) {
		this.listOfWorkflowMin = listOfWorkflowMin;
	}

	public List<Map<String, Object>> getListOfWorkflowNbr() {
		return listOfWorkflowNbr;
	}

	public void setListOfWorkflowNbr(List<Map<String, Object>> listOfWorkflowNbr) {
		this.listOfWorkflowNbr = listOfWorkflowNbr;
	}

	public List<Map<String, Object>> getListOfWorkflowRJ() {
		return listOfWorkflowRJ;
	}

	public void setListOfWorkflowRJ(List<Map<String, Object>> listOfWorkflowRJ) {
		this.listOfWorkflowRJ = listOfWorkflowRJ;
	}

	public String getListOfWorkflowsRJ() {
		buttonJsonDemo();
		return new Gson().toJson(this.listOfWorkflowRJ);
	}

	public void setListOfWorkflowsRJ(String listOfWorkflowsRJ) {
		this.listOfWorkflowsRJ = listOfWorkflowsRJ;
	}

	public String getWorkflowsREJETMin() {
		buttonJsonDemo();
		return new Gson().toJson(this.workflowREJETMin);
	}

	public void setWorkflowsREJETMin(String workflowsREJETMin) {
		this.workflowsREJETMin = workflowsREJETMin;
	}

	public List<Map<String, Object>> getWorkflowREJETMin() {
		return workflowREJETMin;
	}

	public void setWorkflowREJETMin(List<Map<String, Object>> workflowREJETMin) {
		this.workflowREJETMin = workflowREJETMin;
	}

	public String getWorkflowsREJETMax() {
		buttonJsonDemo();
		return new Gson().toJson(this.workflowREJETMax);
	}

	public void setWorkflowsREJETMax(String workflowsREJETMax) {
		this.workflowsREJETMax = workflowsREJETMax;
	}

	public String getWorkflowsREJETAvg() {
		buttonJsonDemo();
		return new Gson().toJson(this.workflowREJETAvg);
	}

	public void setWorkflowsREJETAvg(String workflowsREJETAvg) {
		this.workflowsREJETAvg = workflowsREJETAvg;
	}

	public List<Map<String, Object>> getWorkflowREJETMax() {
		return workflowREJETMax;
	}

	public void setWorkflowREJETMax(List<Map<String, Object>> workflowREJETMax) {
		this.workflowREJETMax = workflowREJETMax;
	}

	public List<Map<String, Object>> getWorkflowREJETAvg() {
		return workflowREJETAvg;
	}

	public void setWorkflowREJETAvg(List<Map<String, Object>> workflowREJETAvg) {
		this.workflowREJETAvg = workflowREJETAvg;
	}

}
