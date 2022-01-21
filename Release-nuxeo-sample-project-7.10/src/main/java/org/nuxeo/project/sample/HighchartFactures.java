package org.nuxeo.project.sample;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

@Scope(ScopeType.CONVERSATION)
@Name("highchartFC")
public class HighchartFactures
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @In(create=true)
  protected transient CoreSession coreSession;
  private static final Log log = LogFactory.getLog(HighchartFactures.class);
  @In(create=true)
  protected transient NavigationContext navigationContext;
  @In(create=true)
  protected transient WebActions webActions;
  @In(create=true)
  protected transient CoreSession documentManager;
  @In(create=true)
  protected transient FacesMessages facesMessages;
  @In(create=true)
  protected transient ResourcesAccessor resourcesAccessor;
  private String firstName;
  private String lastName;
  private String txt2;
  private String isbn;
  private String txt1;
  private String anom;
  public List<Integer> listVal;
  int ctp;
  Connection connection;
  List<Map<String, Object>> listOfFactureNbr;
  List<Map<String, Object>> listOfFactureNbrEC;
  List<Map<String, Object>> listOfFactureEnCoursNbr;
  List<Map<String, Object>> listOfWorkflowNbr;
  List<Map<String, Object>> listOfFactureNbrV;
  List<Map<String, Object>> listOfFactureNbrT;
  List<Map<String, Object>> listDelaiMoyen;
  List<Map<String, Object>> listDelaiScan;
  List<Map<String, Object>> listDelaiWorkflow;
  List<Map<String, Object>> listDelaiVR;
  private List<DelaiReport> delaiMoyen;
  private List<DelaiReport> nbrFacture;
  DelaiReport dr;
  List<Map<String, Object>> workflownombrefactureV;
  List<Map<String, Object>> listOfInvoiceEnpj;
  List<Map<String, Object>> listOfLiasse;
  List<Map<String, Object>> listService;
  private String listOfFNbr;
  private String listOfFNbrEC;
  private String listOfFactureECNbr;
  private String listOfFNbrT;
  private String listOfFNbrV;
  private Date dateDebut;
  private Date dateFin;
  private Date dateDebut2;
  private Date dateFin2;
  String reportDateDebut;
  String reportDateFin;
  String reportDateDebut2;
  String reportDateFin2;
  List<Object> listhc;
  private List<String> capitals;
  
  public Date getDateDebut()
  {
    Calendar cal2 = Calendar.getInstance();
    cal2.add(5, -30);
    return cal2.getTime();
  }
  
  public Date getDateDebut2()
  {
    Calendar cal2 = Calendar.getInstance();
    cal2.add(5, -30);
    return cal2.getTime();
  }
  
  public Date getDateFin2()
  {
    Calendar cal2 = Calendar.getInstance();
    cal2.add(5, 30);
    return cal2.getTime();
  }
  
  public List<DelaiReport> getDelaiMoyen()
  {
    return this.delaiMoyen;
  }
  
  public void setDelaiMoyen(List<DelaiReport> delaiMoyen)
  {
    this.delaiMoyen = delaiMoyen;
  }
  
  public List<DelaiReport> getNbrFacture()
  {
    return this.nbrFacture;
  }
  
  public void setNbrFacture(List<DelaiReport> nbrFacture)
  {
    this.nbrFacture = nbrFacture;
  }
  
  public void setTxt2(String txt2)
  {
    System.out.println(txt2);
    
    this.txt2 = txt2;
  }
  
  public String getTxt2()
  {
    return this.txt2;
  }
  
  public void setDateDebut2(Date dateDebut)
  {
    this.dateDebut2 = dateDebut;
  }
  
  public void setDateFin2(Date dateFin)
  {
    this.dateFin2 = dateFin;
  }
  
  public void setDateDebut(Date dateDebut)
  {
    this.dateDebut = dateDebut;
  }
  
  public Date getDateFin()
  {
    Calendar cal2 = Calendar.getInstance();
    cal2.add(5, 30);
    return cal2.getTime();
  }
  
  public void setDateFin(Date dateFin)
  {
    this.dateFin = dateFin;
  }
  
  public void setListhc(List<Object> listhc)
  {
    this.listhc = listhc;
  }
  
  public HighchartFactures()
  {
    this.ctp = 0;
    this.connection = null;
    
    this.delaiMoyen = new ArrayList();
    this.nbrFacture = new ArrayList();
    
    this.dateDebut = new Date();
    this.dateFin = new Date();
    this.dateDebut2 = new Date();
    this.dateFin2 = new Date();
    
    this.listhc = new ArrayList();
    
    this.capitals = null;
  }
  
  public void setAnom(String anom)
  {
    this.anom = anom;
  }
  
  public Connection getConnection()
  {
    return this.connection;
  }
  
  public void setConnection(Connection connection)
  {
    this.connection = connection;
  }
  
  public String getAnom()
  {
    if (this.anom == null) {
      return "";
    }
    return "EL HAKIMI Anouar";
  }
  
  public String QueryRunner()
  {
    return new Gson().toJson(this.listOfLiasse);
  }
  
  public void setListVal(List<Integer> listVal)
  {
    listVal = listVal;
  }
  
  public void buttonJsonFACTURE()
  {
    if (this.connection != null)
    {
      log.error("buttonJsonFACTURE NOT NULL .......................");
      Gson gson = new Gson();
      
      String queryFactureNbr = "select y, name from highchartwfacturevf";
      String queryFactureNbrEC = "select y, name from highchartwfactureecf";
      
      String queryFactureNbrV = "select y, name from highchartwfacturevr";
      String queryFactureNbrT = "select y, name from highchartwfactureecr";
      
      this.delaiMoyen.clear();
      this.nbrFacture.clear();
      this.listOfFactureNbr = null;
      this.listOfFactureNbrEC = null;
      try
      {
        QueryRunner queryRunner = new QueryRunner();
        
        this.listOfFactureNbr = ((List)queryRunner.query(this.connection, queryFactureNbr, new MapListHandler()));
        this.listOfFactureNbrEC = ((List)queryRunner.query(this.connection, queryFactureNbrEC, new MapListHandler()));
        
        this.listOfFactureNbrV = ((List)queryRunner.query(this.connection, queryFactureNbrV, new MapListHandler()));
        this.listOfFactureNbrT = ((List)queryRunner.query(this.connection, queryFactureNbrT, new MapListHandler()));
      }
      catch (SQLException se)
      {
        se = 
        
          se;se = se;se = se;se = se;se = se;throw new RuntimeException("Couldn't query the database ayoub........", se);
      }
      finally {}
    }
    else
    {
      log.error("buttonJsonFACTURE NULL .......................");
    }
  }
  
  public void buttonJson()
  {
    log.error("buttonJson");
    Gson gson = new Gson();
    
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    this.reportDateDebut = df.format(Long.valueOf(getDateDebut().getTime()));
    this.reportDateFin = df.format(Long.valueOf(getDateFin().getTime()));
    
    log.error(this.reportDateDebut);
    log.error(this.reportDateFin);
    
    String queryWorkflowNbr = "SELECT count(distinct numfacture) as y,currentuser1 as name from workflownumFactVF  where statutfacture like '%Valid%'  group by currentuser1";
    
    Connection connection = null;
    this.listOfWorkflowNbr = null;
    try
    {
      String driverName = "org.postgresql.Driver";
      Class.forName(driverName);
      
      InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("../datasources-config.xml");
      try
      {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(input));
        XPath xpath = XPathFactory.newInstance().newXPath();
        String url = (String)xpath.compile("//component//extension//datasource/@url").evaluate(document, XPathConstants.STRING);
        String driver = (String)xpath.compile("//component//extension//datasource/@driverClassName").evaluate(document, XPathConstants.STRING);
        String username = (String)xpath.compile("//component//extension//datasource/@username").evaluate(document, XPathConstants.STRING);
        String password = (String)xpath.compile("//component//extension//datasource/@password").evaluate(document, XPathConstants.STRING);
        
        connection = DriverManager.getConnection(url, username, password);
        
        log.error(connection.toString());
      }
      catch (FileNotFoundException e)
      {
        e.printStackTrace();
      }
      catch (ParserConfigurationException e)
      {
        e.printStackTrace();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Could not find the database driver");
    }
    catch (Exception e)
    {
      System.err.println("Could not connect to the database");
    }
    try
    {
      QueryRunner queryRunner = new QueryRunner();
      
      this.listOfWorkflowNbr = ((List)queryRunner.query(connection, queryWorkflowNbr, new MapListHandler()));
    }
    catch (SQLException se)
    {
      se = 
      
        se;se = se;se = se;se = se;se = se;throw new RuntimeException("Couldn't query the database ayoub........", se);
    }
    finally {}
    String jsonRLiasse = gson.toJson(this.listOfLiasse);
  }
  
  public String getListOfFNbr()
  {
    buttonJsonFACTURE();
    log.error("gson : " + new Gson().toJson(this.listOfFactureNbr));
    return new Gson().toJson(this.listOfFactureNbr);
  }
  
  public String getListOfFNbrEC()
  {
    buttonJsonFACTURE();
    log.error("gson : " + new Gson().toJson(this.listOfFactureNbrEC));
    return new Gson().toJson(this.listOfFactureNbrEC);
  }
  
  public String getListOfFNbrV()
  {
    buttonJsonFACTURE();
    return new Gson().toJson(this.listOfFactureNbrV);
  }
  
  public String getListOfFNbrT()
  {
    buttonJsonFACTURE();
    return new Gson().toJson(this.listOfFactureNbrT);
  }
  
  public String getListOfFactureECNbr()
  {
    buttonJsonFACTURE();
    return new Gson().toJson(this.listOfFactureEnCoursNbr);
  }
  
  public List<Object> getListhc()
  {
    return this.listhc;
  }
  
  public String getListOfWorkflowNbr()
  {
    buttonJsonFACTURE();
    return new Gson().toJson(this.listOfWorkflowNbr);
  }
  
  public String getFirstName()
  {
    if (this.firstName == null) {
      this.firstName = "";
    }
    return this.firstName;
  }
  
  public void setFirstName(String s)
  {
    this.firstName = s;
  }
  
  public String getLastName()
  {
    if (this.lastName == null) {
      this.lastName = "";
    }
    return this.lastName;
  }
  
  public void setLastName(String s)
  {
    this.lastName = s;
  }
  
  public void actualiserClient()
  {
    this.lastName = this.firstName;
  }
  
  public void eventChange()
  {
    this.lastName = "123";
  }
  
  public String getIsbn()
  {
    if (this.isbn == null) {
      return "";
    }
    return this.isbn;
  }
  
  public void setIsbn(String s)
  {
    this.isbn = s;
  }
  
  public static void main(String[] args)
  {
    HighchartFactures b = new HighchartFactures();
    b.buttonJson();
  }
  
  public void connection()
  {
    log.error("connexion ....................");
    try
    {
      String driverName = "org.postgresql.Driver";
      Class.forName(driverName);
      
      InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("../datasources-config.xml");
      try
      {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(input));
        
        XPath xpath = XPathFactory.newInstance().newXPath();
        String url = (String)xpath.compile("//component//extension//datasource/@url").evaluate(document, XPathConstants.STRING);
        
        String driver = (String)xpath.compile("//component//extension//datasource/@driverClassName").evaluate(document, XPathConstants.STRING);
        
        String username = (String)xpath.compile("//component//extension//datasource/@username").evaluate(document, XPathConstants.STRING);
        
        String password = (String)xpath.compile("//component//extension//datasource/@password").evaluate(document, XPathConstants.STRING);
        
        this.connection = DriverManager.getConnection(url, username, password);
        log.error(this.connection.toString());
      }
      catch (FileNotFoundException e)
      {
        e.printStackTrace();
      }
      catch (ParserConfigurationException e)
      {
        e.printStackTrace();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Could not find the database driver");
    }
    catch (Exception e)
    {
      System.err.println("Could not connect to the database");
    }
  }
  
  public class DelaiReport
  {
    String id;
    String valeur;
    
    public DelaiReport() {}
    
    public String getId()
    {
      return this.id;
    }
    
    public void setId(String id)
    {
      this.id = id;
    }
    
    public String getValeur()
    {
      return this.valeur;
    }
    
    public void setValeur(String valeur)
    {
      this.valeur = valeur;
    }
  }
}
