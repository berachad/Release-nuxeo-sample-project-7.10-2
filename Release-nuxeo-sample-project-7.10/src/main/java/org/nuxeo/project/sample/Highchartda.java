package org.nuxeo.project.sample;

import com.google.gson.Gson;
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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
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
@Name("highchartda")
public class Highchartda implements Serializable
{
	//id  Dossiers Da server ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc

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
  public List<Integer> listVal;
  private List<Map<String, Object>> listOfWorkflowAvg1;
  private List<Map<String, Object>> listOfWorkflowMax1;
  private List<Map<String, Object>> listOfWorkflowMin1;
  private List<Map<String, Object>> listOfDaValidate;
  private List<Map<String, Object>> listOfDaReject;
  private List<Map<String, Object>> listOfDaStatu;
  private Connection connection;
  private List<Map<String, Object>> listOfLiasse;
  // Ajouter par soukaina
   private List<Map<String, Object>> listOfDaDemandeParGestionnaire;
   private List<Map<String, Object>> listOfDadelaiParGestionnaire;
   private List<Map<String, Object>> listOfDevisParD=new ArrayList<Map<String, Object>>();
   private TreeMap<Double, String> Moiss= new TreeMap<Double, String>();

  private List<Map<String, Object>> listOfDaCategorie;
  private List<Map<String, Object>> listOfDaDemandeur;
  private Calendar now = Calendar.getInstance();
  private int mois = this.now.get(2) + 1;
  private int annee = this.now.get(1);
  
   //*Annee1 et mois1 Gain réalisé par demande
  private int mois1 = this.now.get(2) + 1;
  private int annee1 = this.now.get(1);
  
    //*Annee2 et mois2 Gain réalisé par demande
  private int mois2 = this.now.get(2) + 1;
  private int annee2 = this.now.get(1);


   //*Annee3 et mois3 Nombre des demandes traités par géstionnaire
  private int mois3 = this.now.get(2) + 1;
  private int annee3 = this.now.get(1);

  //*Annee4 et mois4 Nombre de demandes d'achat par statut
  private int mois4 = this.now.get(2) + 1;
  private int annee4 = this.now.get(1);

  //*Annee5 et mois5 Nombre des demandes d'achats par catégorie
  private int mois5 = this.now.get(2) + 1;
  private int annee5 = this.now.get(1);

  //*Annee6 et mois6 Nombre de demandes d'achats par demandeur
  private int mois6 = this.now.get(2) + 1;
  private int annee6 = this.now.get(1);

  //*Annee7 et mois7 Temps de validation par responsable
  private int mois7 = this.now.get(2) + 1;
  private int annee7 = this.now.get(1);

  //*Annee8 et mois8 Nombre de demandes d'achat  validées par responsable
  private int mois8 = this.now.get(2) + 1;
  private int annee8 = this.now.get(1);
  //*Annee9 et mois9 Nombre de demandes d'achat rejetées par responsable
  private int mois9 = this.now.get(2) + 1;
  private int annee9 = this.now.get(1);
  
  
   
   public int getMois()
  {
    return this.mois;
  }
  
  public void setMois(int mois)
  {
    this.mois = mois;
  }
  
  public int getAnnee()
  {
    return this.annee;
  }
  
  public void setAnnee(int annee)
  {
    this.annee = annee;
  }

  
  //*Annee1 et mois1 Gain réalisé par demande
  
  public int getMois1()
  {
    return this.mois1;
  }
  
  public void setMois1(int mois)
  {
    this.mois1= mois;
  }
  
  public int getAnnee1()
  {
    return this.annee1;
  }
  
  public void setAnnee1(int annee)
  {
    this.annee1 = annee;
  }

 //*Annee2 et mois2 Délai de traitement par gestionnaire
  
  public int getMois2()
  {
    return this.mois2;
  }
  
  public void setMois2(int mois)
  {
    this.mois2= mois;
  }
  
  public int getAnnee2()
  {
    return this.annee2;
  }
  
  public void setAnnee2(int annee)
  {
    this.annee2 = annee;
  }

 //*Annee3 et mois3 Nombre des demandes traités par géstionnaire

   public int getMois3()
  {
    return this.mois3;
  }
  
  public void setMois3(int mois)
  {
    this.mois3= mois;
  }
  
  public int getAnnee3()
  {
    return this.annee3;
  }
  
  public void setAnnee3(int annee)
  {
    this.annee3 = annee;
  }


  
   public int getMois4()
  {
    return this.mois4;
  }
  
  public void setMois4(int mois)
  {
    this.mois4= mois;
  }
  
  public int getAnnee4()
  {
    return this.annee4;
  }
  
  public void setAnnee4(int annee)
  {
    this.annee4 = annee;
  }



  public int getMois5()
  {
    return this.mois5;
  }
  
  public void setMois5(int mois)
  {
    this.mois5= mois;
  }
  
  public int getAnnee5()
  {
    return this.annee5;
  }
  
  public void setAnnee5(int annee)
  {
    this.annee5 = annee;
  }


  public int getMois6()
  {
    return this.mois6;
  }
  
  public void setMois6(int mois)
  {
    this.mois6= mois;
  }
  
  public int getAnnee6()
  {
    return this.annee6;
  }
  
  public void setAnnee6(int annee)
  {
    this.annee6 = annee;
  }



   public int getMois7()
  {
    return this.mois7;
  }
  
  public void setMois7(int mois)
  {
    this.mois7= mois;
  }
  
  public int getAnnee7()
  {
    return this.annee7;
  }
  
  public void setAnnee7(int annee)
  {
    this.annee7 = annee;
  }


  public int getMois8()
  {
    return this.mois8;
  }
  
  public void setMois8(int mois)
  {
    this.mois8= mois;
  }
  
  public int getAnnee8()
  {
    return this.annee8;
  }
  
  public void setAnnee8(int annee)
  {
    this.annee8 = annee;
  }



  public int getMois9()
  {
    return this.mois9;
  }
  
  public void setMois9(int mois)
  {
    this.mois9= mois;
  }
  
  public int getAnnee9()
  {
    return this.annee9;
  }
  
  public void setAnnee9(int annee)
  {
    this.annee9 = annee;
  }  
  
  public Connection getConnection()
  {
    return this.connection;
  }
  
  public void setConnection(Connection connection)
  {
    this.connection = connection;
  }

  public String QueryRunner()
  {
    return new Gson().toJson(this.listOfLiasse);
  }

  
  public void buttonJsonDA()
  {
    if (this.connection != null)
    {
      log.error("buttonJsonDA NOT NULL .......................");
      
      String queryWorkflowAvg1 = "SELECT AVG(date_part('day'::text, r.enddate - r.startdate )) as y, ti.actor as name from da d INNER JOIN hierarchy h2 on h2.id=d.id INNER JOIN task t  on t.targetdocumentid=d.id INNER JOIN task_info ti on ti.taskdocid = t.id INNER JOIN route_node r on r.nodeid = t.type where ti.status = 'validate' and h2.parentid='ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc' and h2.name not like '%.trashed' and  date_part('month', r.enddate)='" + getMois7() + "' and date_part('year', r.enddate)='" + getAnnee7() + "' GROUP BY ti.actor";
      String queryWorkflowMax1 = "SELECT MAX(date_part('day'::text, r.enddate - r.startdate )) as y, ti.actor as name from da d INNER JOIN hierarchy h2 on h2.id=d.id INNER JOIN task t  on t.targetdocumentid=d.id INNER JOIN task_info ti on ti.taskdocid = t.id INNER JOIN route_node r on r.nodeid = t.type where ti.status = 'validate' and h2.parentid='ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc' and h2.name not like '%.trashed' and  date_part('month', r.enddate)='" + getMois7() + "' and date_part('year', r.enddate)='" + getAnnee7() + "' GROUP BY ti.actor";
      String queryWorkflowMin1 = "SELECT MIN(date_part('day'::text, r.enddate - r.startdate )) as y, ti.actor as name from da d INNER JOIN hierarchy h2 on h2.id=d.id INNER JOIN task t  on t.targetdocumentid=d.id INNER JOIN task_info ti on ti.taskdocid = t.id INNER JOIN route_node r on r.nodeid = t.type where ti.status = 'validate' and h2.parentid='ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc' and h2.name not like '%.trashed' and  date_part('month', r.enddate)='" + getMois7() + "' and date_part('year', r.enddate)='" + getAnnee7() + "' GROUP BY ti.actor";
     // String queryDaValidate = "SELECT count(ti.status) as y, ti.actor as name from da d INNER JOIN task t  on t.targetdocumentid=d.id INNER JOIN task_info ti on ti.taskdocid = t.id INNER JOIN hierarchy h on h.id=d.id where ti.status = 'validate' and h.parentid='ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc' and h.name not like '%.trashed'  and  date_part('month', r.enddate)='" + getMois() + "' and date_part('year', r.enddate)='" + getAnnee() + "' GROUP BY ti.actor";
     
      String queryDaValidate = " select count(r.button) as y, r.lastactor as name from route_node r where r.button = 'validate' and  date_part('year', r.enddate)='" + getAnnee8() + "'and date_part('month', r.enddate)='" + getMois8() + "' and r.id in (select distinct h.id from task t inner join hierarchy h on h.parentid = t.processid where t.id in (select t.id from task t where t.targetdocumentid in (select d.id from da d inner join hierarchy h on h.id = d.id where h.parentid = 'ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc')) ) group by r.lastactor";

      //String queryDaReject = "SELECT count(ti.status) as y, ti.actor as name from da d INNER JOIN task t  on t.targetdocumentid=d.id INNER JOIN task_info ti on ti.taskdocid = t.id INNER JOIN hierarchy h on h.id=d.id where ti.status = 'reject' and h.parentid='ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc' and h.name not like '%.trashed' GROUP BY ti.actor";
      
       String queryDaReject ="select count(r.button) as y, r.lastactor as name from route_node r where r.button = 'reject' and  date_part('year', r.enddate)='" + getAnnee9() + "'and date_part('month', r.enddate)='" + getMois9() + "' and r.id in (select distinct h.id from task t inner join hierarchy h on h.parentid = t.processid where t.id in (select t.id from task t where t.targetdocumentid in (select d.id from da d inner join hierarchy h on h.id = d.id where h.parentid = 'ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc')) ) group by r.lastactor"; 
      String queryDaStatu = "SELECT count(d.id) as y, m.lifecyclestate as name FROM da d JOIN hierarchy h ON h.id = d.id JOIN misc m ON m.id = h.id WHERE h.parentid = 'ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc' AND h.name not like '%.trashed' and date_part('month', d.date_da)='" + getMois4() + "' and date_part('year', d.date_da)='" + getAnnee4() + "' group by m.lifecyclestate";
      
     



      try
      {
        QueryRunner queryRunner = new QueryRunner();
        
        String queryDaCategorie = "select count(d.categorie) as y,categorie as name from da d INNER JOIN hierarchy h on h.id=d.id where categorie notnull and h.parentid='ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc' AND h.name not like '%.trashed' and date_part('month', d.date_da)='" + getMois5() + "' and date_part('year', d.date_da)='" + getAnnee5() + "'  group by categorie";
        String queryDADemandeur = "select count(d.id) as y,nom_du_demandeur as name from da d INNER JOIN hierarchy h on h.id=d.id where h.parentid='ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc' AND h.name not like '%.trashed' and  date_part('month', d.date_da)='" + getMois6() + "' and date_part('year', d.date_da)='" + getAnnee6() + "'  group by nom_du_demandeur";

        this.listOfWorkflowAvg1 = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryWorkflowAvg1, new MapListHandler()));
        this.listOfWorkflowMax1 = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryWorkflowMax1, new MapListHandler()));
        this.listOfWorkflowMin1 = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryWorkflowMin1, new MapListHandler()));
        this.listOfDaValidate = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryDaValidate, new MapListHandler()));
        this.listOfDaReject = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryDaReject, new MapListHandler()));
        this.listOfDaStatu = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryDaStatu, new MapListHandler()));
        this.listOfDaCategorie = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryDaCategorie, new MapListHandler()));
        this.listOfDaDemandeur = ((List<Map<String, Object>>) queryRunner.query(this.connection, queryDADemandeur, new MapListHandler()));
      
      }
      catch (SQLException se)
      {
      throw new RuntimeException("Couldn't query the database zakaria........", se);
      }
      finally {}
    }
    else
    {
      log.error("buttonJsonDA NULL .......................");
    }
  }
  
  public void getResultDAByCategorie()
  {
    if (this.connection != null)
    {
      log.error("buttonJsonDA NOT NULL .......................");
      
      String queryWorkflowAvg1 = "SELECT AVG(date_part('day'::text, r.enddate - r.startdate )) as y, ti.actor as name from da d INNER JOIN hierarchy h2 on h2.id=d.id INNER JOIN task t  on t.targetdocumentid=d.id INNER JOIN task_info ti on ti.taskdocid = t.id INNER JOIN route_node r on r.nodeid = t.type where ti.status = 'validate' and h2.name not like '%.trashed' and  date_part('month', r.enddate)='" + getMois7() + "' and date_part('year', r.enddate)='" + getAnnee7() + "' GROUP BY ti.actor";
      String queryWorkflowMax1 = "SELECT MAX(date_part('day'::text, r.enddate - r.startdate )) as y, ti.actor as name from da d INNER JOIN hierarchy h2 on h2.id=d.id INNER JOIN task t  on t.targetdocumentid=d.id INNER JOIN task_info ti on ti.taskdocid = t.id INNER JOIN route_node r on r.nodeid = t.type where ti.status = 'validate' and h2.name not like '%.trashed' and  date_part('month', r.enddate)='" + getMois7() + "' and date_part('year', r.enddate)='" + getAnnee7() + "' GROUP BY ti.actor";
      String queryWorkflowMin1 = "SELECT MIN(date_part('day'::text, r.enddate - r.startdate )) as y, ti.actor as name from da d INNER JOIN hierarchy h2 on h2.id=d.id INNER JOIN task t  on t.targetdocumentid=d.id INNER JOIN task_info ti on ti.taskdocid = t.id INNER JOIN route_node r on r.nodeid = t.type where ti.status = 'validate' and and h2.name not like '%.trashed' and  date_part('month', r.enddate)='" + getMois7() + "' and date_part('year', r.enddate)='" + getAnnee7() + "' GROUP BY ti.actor";
     // String queryDaValidate = "SELECT count(ti.status) as y, ti.actor as name from da d INNER JOIN task t  on t.targetdocumentid=d.id INNER JOIN task_info ti on ti.taskdocid = t.id INNER JOIN hierarchy h on h.id=d.id where ti.status = 'validate' and h.parentid='ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc' and h.name not like '%.trashed'  and  date_part('month', r.enddate)='" + getMois() + "' and date_part('year', r.enddate)='" + getAnnee() + "' GROUP BY ti.actor";
     
      String queryDaValidate = " select count(r.button) as y, r.lastactor as name from route_node r where r.button = 'validate' and  date_part('year', r.enddate)='" + getAnnee8() + "'and date_part('month', r.enddate)='" + getMois8() + "' and r.id in (select distinct h.id from task t inner join hierarchy h on h.parentid = t.processid where t.id in (select t.id from task t where t.targetdocumentid in (select d.id from da d inner join hierarchy h on h.id = d.id where h.primarytype = 'da')) ) group by r.lastactor";

      //String queryDaReject = "SELECT count(ti.status) as y, ti.actor as name from da d INNER JOIN task t  on t.targetdocumentid=d.id INNER JOIN task_info ti on ti.taskdocid = t.id INNER JOIN hierarchy h on h.id=d.id where ti.status = 'reject' and h.parentid='ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc' and h.name not like '%.trashed' GROUP BY ti.actor";
      
       String queryDaReject ="select count(r.button) as y, r.lastactor as name from route_node r where r.button = 'reject' and  date_part('year', r.enddate)='" + getAnnee9() + "'and date_part('month', r.enddate)='" + getMois9() + "' and r.id in (select distinct h.id from task t inner join hierarchy h on h.parentid = t.processid where t.id in (select t.id from task t where t.targetdocumentid in (select d.id from da d inner join hierarchy h on h.id = d.id where h.primarytype = 'da')) ) group by r.lastactor"; 
      String queryDaStatu = "SELECT count(d.id) as y, m.lifecyclestate as name FROM da d JOIN hierarchy h ON h.id = d.id JOIN misc m ON m.id = h.id WHERE h.primarytype = 'da' AND h.name not like '%.trashed' and date_part('month', d.date_da)='" + getMois4() + "' and date_part('year', d.date_da)='" + getAnnee4() + "' group by m.lifecyclestate";
      
      // ajouter par Soukaina
      String queryDaDemandeParGestionnaire = "select count(d.id) as y, \"userId\" as name from da d,hierarchy h, da_select_users_multi_fields ds, user2group usg where  (usg.\"userId\"=ds.item or usg.\"groupId\"=ds.item) and  d.id=ds.id and h.parentId='ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc' and \"groupId\"='Achat' and h.id=d.id AND h.name not like '%.trashed'  and date_part('month', d.date_da)='" + getMois3() + "' and date_part('year', d.date_da)='" + getAnnee3() + "' group by \"userId\" ";
      String queryDadelaiParGestionnaire = "select AVG(date_part('day'::text, d.date_devis - d.date_affectation )) as y, \"userId\" as name  from da d,hierarchy h, da_select_users_multi_fields ds, user2group usg where  (usg.\"userId\"=ds.item or usg.\"groupId\"=ds.item) and  d.id=ds.id and h.parentId='ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc' and \"groupId\"='Achat' and h.id=d.id AND h.name not like '%.trashed' and date_part('month', d.date_da)='" + getMois2() + "' and date_part('year', d.date_da)='" + getAnnee2() + "' group by \"userId\" ";
      System.out.println("le mois est : " + getMois());
      System.out.println("L'annee est : " + getAnnee());
      String queryDevisParDa = "select m.month, dc.title as name,  cast (Max(choix.quantite*(max-choix.prixunitaire)) as decimal (18,2)) as y from (select date_part('month', dc.created) as month, Max(b1.prixunitaire)  as max, d.id from dublincore dc, bon_devis_bontype b1, hierarchy h1, (select dd.id, dd.description from da dd, hierarchy hh where hh.parentid='ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc' and hh.id=dd.id and hh.name not like '%.trashed') d where h1.parentid=d.id and h1.id=b1.id and dc.id=d.id and date_part('month', dc.created)='" + getMois1() + "' and date_part('year', dc.created)='" + getAnnee1() + "' group by d.id, dc.created) m, da da inner join hierarchy h on h.parentId= da.id inner join dublincore dc on dc.id= da.id inner join choix_bontype choix on choix.id=h.id where h.name not like '%.trashed' and choix.statut_devis = 'Validé' and m.id=da.id group by m.month, dc.title";
      



      try
      {
        QueryRunner queryRunner = new QueryRunner();
        
        //String delaiScan = "select avg(date_part('day'::text, datescan - created)) as delai from facturefournisseur, dublincore where facturefournisseur.id = dublincore.id";
        //String delaiWorkflow = "select avg(date_part('day'::text, datescan - datefacture)) as delai from facturefournisseur";
        //String delaiVR = "select avg(date_part('day'::text, datevalidationfinal - datedebutworkflow)) as delai from facturefournisseur";
        //String factureEnProjet = "select count(id) as nbr from facturefournisseur where statutfacture is null";
        //String fatureEnTraitement = "select count(id) as nbr from facturefournisseur where statutfacture like '%En traitement%'";
        //String factureVAlide = "select count(id) as nbr from facturefournisseur where statutfacture like '%Valid%'";
        //String factureRejete = "select count(id) as nbr from facturefournisseur where statutfacture like '%Rejet%'";
        String queryDaCategorie = "select count(d.categorie) as y,categorie as name from da d INNER JOIN hierarchy h on h.id=d.id where categorie notnull and h.parentid='ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc' AND h.name not like '%.trashed' and date_part('month', d.date_da)='" + getMois5() + "' and date_part('year', d.date_da)='" + getAnnee5() + "'  group by categorie";
        String queryDADemandeur = "select count(d.id) as y,nom_du_demandeur as name from da d INNER JOIN hierarchy h on h.id=d.id where h.parentid='ff5bc6c3-605c-4fb7-a38f-6a6e8fb987bc' AND h.name not like '%.trashed' and  date_part('month', d.date_da)='" + getMois6() + "' and date_part('year', d.date_da)='" + getAnnee6() + "'  group by nom_du_demandeur";

        this.listOfWorkflowAvg1 = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryWorkflowAvg1, new MapListHandler()));
        this.listOfWorkflowMax1 = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryWorkflowMax1, new MapListHandler()));
        this.listOfWorkflowMin1 = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryWorkflowMin1, new MapListHandler()));
        this.listOfDaValidate = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryDaValidate, new MapListHandler()));
        this.listOfDaReject = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryDaReject, new MapListHandler()));
        this.listOfDaStatu = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryDaStatu, new MapListHandler()));
        this.listOfDaCategorie = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryDaCategorie, new MapListHandler()));
        this.listOfDaDemandeur = ((List<Map<String, Object>>) queryRunner.query(this.connection, queryDADemandeur, new MapListHandler()));
     
      }
      catch (SQLException se)
      {
      throw new RuntimeException("Couldn't query the database zakaria........", se);
      }
      finally {}
    }
    else
    {
      log.error("buttonJsonDA NULL .......................");
    }
  }
  
  public String getListOfWorkflowsAvg()
  {
	  if (this.connection != null)
	  {
	      log.error("listOfWorkflowAvg1  NOT NULL .......................");
	      try
	      {
	        QueryRunner queryRunner = new QueryRunner();
	        String queryWorkflowAvg1 = "SELECT AVG(date_part('day'::text, r.enddate - r.startdate )) as y, ti.actor as name from da d INNER JOIN hierarchy h2 on h2.id=d.id INNER JOIN task t  on t.targetdocumentid=d.id INNER JOIN task_info ti on ti.taskdocid = t.id INNER JOIN route_node r on r.nodeid = t.type where ti.status = 'validate' and h2.primarytype = 'da' and h2.name not like '%.trashed' and  date_part('month', r.enddate)='" + getMois7() + "' and date_part('year', r.enddate)='" + getAnnee7() + "' GROUP BY ti.actor";
	        this.listOfWorkflowAvg1 = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryWorkflowAvg1, new MapListHandler()));
         }
	      catch (SQLException se)
	      {
	      throw new RuntimeException("Couldn't query the database soukaina ........", se);
	      }
	      
	   }
	   else
	   {
	      log.error("listOfWorkflowAvg1 NULL .......................");
	   }
	  
	  return new Gson().toJson(this.listOfWorkflowAvg1);
  }
  
  public String getListOfWorkflowsMax()
  {
	  if (this.connection != null)
	  {
	      log.error("listOfWorkflowMax1  NOT NULL .......................");
	      try
	      {
	        QueryRunner queryRunner = new QueryRunner();
	        String queryWorkflowMax1 = "SELECT MAX(date_part('day'::text, r.enddate - r.startdate )) as y, ti.actor as name from da d INNER JOIN hierarchy h2 on h2.id=d.id INNER JOIN task t  on t.targetdocumentid=d.id INNER JOIN task_info ti on ti.taskdocid = t.id INNER JOIN route_node r on r.nodeid = t.type where ti.status = 'validate' and h2.primarytype = 'da' and h2.name not like '%.trashed' and  date_part('month', r.enddate)='" + getMois7() + "' and date_part('year', r.enddate)='" + getAnnee7() + "' GROUP BY ti.actor";
	        this.listOfWorkflowMax1 = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryWorkflowMax1, new MapListHandler()));
         }
	      catch (SQLException se)
	      {
	      throw new RuntimeException("Couldn't query the database soukaina ........", se);
	      }
	      
	   }
	   else
	   {
	      log.error("listOfWorkflowMax1 NULL .......................");
	   }
    return new Gson().toJson(this.listOfWorkflowMax1);
  }
  
  public String getListOfWorkflowsMin()
  {
	  if (this.connection != null)
	  {
	      log.error("listOfWorkflowMin1  NOT NULL .......................");
	      try
	      {
	        QueryRunner queryRunner = new QueryRunner();
	        String queryWorkflowMin1 = "SELECT MIN(date_part('day'::text, r.enddate - r.startdate )) as y, ti.actor as name from da d INNER JOIN hierarchy h2 on h2.id=d.id INNER JOIN task t  on t.targetdocumentid=d.id INNER JOIN task_info ti on ti.taskdocid = t.id INNER JOIN route_node r on r.nodeid = t.type where ti.status = 'validate' and h2.primarytype = 'da' and h2.name not like '%.trashed' and  date_part('month', r.enddate)='" + getMois7() + "' and date_part('year', r.enddate)='" + getAnnee7() + "' GROUP BY ti.actor";
	        this.listOfWorkflowMin1 = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryWorkflowMin1, new MapListHandler()));
         }
	      catch (SQLException se)
	      {
	      throw new RuntimeException("Couldn't query the database soukaina ........", se);
	      }
	      
	   }
	   else
	   {
	      log.error("listOfWorkflowMin1 NULL .......................");
	   }
    return new Gson().toJson(this.listOfWorkflowMin1);
  }
  
  public String getListOfDaValidates()
  {
	  if (this.connection != null)
	  {
	      log.error("listOfDaValidate  NOT NULL .......................");
	      try
	      {
	        QueryRunner queryRunner = new QueryRunner();
	        String queryDaValidate = " select count(r.button) as y, r.lastactor as name from route_node r where r.button = 'validate' and  date_part('year', r.enddate)='" + getAnnee8() + "'and date_part('month', r.enddate)='" + getMois8() + "' and r.id in (select distinct h.id from task t inner join hierarchy h on h.parentid = t.processid where t.id in (select t.id from task t where t.targetdocumentid in (select d.id from da d inner join hierarchy h on h.id = d.id where h.primarytype = 'da')) ) group by r.lastactor";
	        this.listOfDaValidate = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryDaValidate, new MapListHandler()));
         }
	      catch (SQLException se)
	      {
	      throw new RuntimeException("Couldn't query the database soukaina ........", se);
	      }
	      
	   }
	   else
	   {
	      log.error("listOfDaValidate NULL .......................");
	   }
    return new Gson().toJson(this.listOfDaValidate);
  }
  
  public String getListOfDaRejects()
  {
	  if (this.connection != null)
	  {
	      log.error("listOfDaReject  NOT NULL .......................");
	      try
	      {
	        QueryRunner queryRunner = new QueryRunner();
	        String queryDaReject ="select count(r.button) as y, r.lastactor as name from route_node r where r.button = 'reject' and  date_part('year', r.enddate)='" + getAnnee9() + "'and date_part('month', r.enddate)='" + getMois9() + "' and r.id in (select distinct h.id from task t inner join hierarchy h on h.parentid = t.processid where t.id in (select t.id from task t where t.targetdocumentid in (select d.id from da d inner join hierarchy h on h.id = d.id where h.primarytype = 'da')) ) group by r.lastactor"; 
	        this.listOfDaReject = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryDaReject, new MapListHandler()));
         }
	      catch (SQLException se)
	      {
	      throw new RuntimeException("Couldn't query the database soukaina ........", se);
	      }
	      
	   }
	   else
	   {
	      log.error("listOfDaReject NULL .......................");
	   }
    return new Gson().toJson(this.listOfDaReject);
  }
  
  public String getListOfDaStatus()
  {
	  if (this.connection != null)
	  {
	      log.error("listOfDaStatu  NOT NULL .......................");
	      try
	      {
	        QueryRunner queryRunner = new QueryRunner();
	        String queryDaStatu = "SELECT count(d.id) as y, m.lifecyclestate as name FROM da d JOIN hierarchy h ON h.id = d.id JOIN misc m ON m.id = h.id WHERE h.primarytype = 'da' AND h.name not like '%.trashed' and date_part('month', d.date_da)='" + getMois4() + "' and date_part('year', d.date_da)='" + getAnnee4() + "' group by m.lifecyclestate";
	        this.listOfDaStatu = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryDaStatu, new MapListHandler()));
         }
	      catch (SQLException se)
	      {
	      throw new RuntimeException("Couldn't query the database soukaina ........", se);
	      }
	      
	   }
	   else
	   {
	      log.error("listOfDaStatu NULL .......................");
	   }
    return new Gson().toJson(this.listOfDaStatu);
  }
  
  public String getListOfDaCategories()
  {
 
	  if (this.connection != null)
	    {
	      log.error("getListOfDaDemandeurs  NOT NULL .......................");
	      try
	      {
	        QueryRunner queryRunner = new QueryRunner();
	         String queryDaCategorie = "select count(d.categorie) as y,categorie as name from da d INNER JOIN hierarchy h on h.id=d.id where categorie notnull and h.primarytype = 'da' AND h.name not like '%.trashed' and date_part('month', d.date_da)='" + getMois5() + "' and date_part('year', d.date_da)='" + getAnnee5() + "'  group by categorie" ;
	         this.listOfDaCategorie = ((List<Map<String, Object>>)queryRunner.query(this.connection, queryDaCategorie, new MapListHandler()));
	      }
	      catch (SQLException se)
	      {
	      throw new RuntimeException("Couldn't query the database soukaina ........", se);
	      }
	      
	    }
	    else
	    {
	      log.error("getResultDAByDemandeur NULL .......................");
	    }
    return new Gson().toJson(this.listOfDaCategorie);
  }
  
  public String getListOfDaDemandeurs()
  {
	  if (this.connection != null)
	    {
	      log.error("getListOfDaDemandeurs  NOT NULL .......................");
	      try
	      {
	        QueryRunner queryRunner = new QueryRunner();
	         String queryDADemandeur = "select count(d.id) as y,nom_du_demandeur as name from da d INNER JOIN hierarchy h on h.id=d.id where h.primarytype = 'da' AND h.name not like '%.trashed' and  date_part('month', d.date_da)='" + getMois6() + "' and date_part('year', d.date_da)='" + getAnnee6() + "'  group by nom_du_demandeur";
	        this.listOfDaDemandeur = ((List<Map<String, Object>>) queryRunner.query(this.connection, queryDADemandeur, new MapListHandler()));
	      }
	      catch (SQLException se)
	      {
	      throw new RuntimeException("Couldn't query the database soukaina ........", se);
	      }
	      
	    }
	    else
	    {
	      log.error("listOfDaDemandeur NULL .......................");
	    }
    return new Gson().toJson(this.listOfDaDemandeur);
  }

  //ajouter par soukaina

 
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
  
  public TreeMap<Double, String> getMoiss()
  {
	  Moiss.put(1.0, "Janvier");
	  Moiss.put(2.0, "Février");
	  Moiss.put(3.0, "Mars");
	  Moiss.put(4.0, "Avril");
	  Moiss.put(5.0, "Mai");
	  Moiss.put(6.0, "Juin");
	  Moiss.put(7.0, "Juillet");
	  Moiss.put(8.0, "Aôut");
	  Moiss.put(9.0, "Septembre");
	  Moiss.put(10.0, "Octobre");
	  Moiss.put(11.0, "Novembre");
	  Moiss.put(12.0, "Décembre");
	 
	 
    return  (TreeMap<Double, String>) Moiss;
  }
  
}
