package org.nuxeo.project.sample;

import javax.mail.Transport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.Properties;
import java.nio.file.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.jsf.operations.AddMessage;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.project.sample.services.DisplayInfoOrException;
import javax.mail.Session;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;


@Operation(id = SendMailBC.ID, category = Constants.CAT_DOCUMENT, label = "Worklist Export Demo")
public class SendMailBC {

	@Context
	protected CoreSession session;
	@Context
	protected AutomationService service;
	@Context
	protected OperationContext ctx = new OperationContext(session);
	public static final String ID = "Document.SendMailBC";
	private final Log log = LogFactory.getLog(SendMailFacture.class);
	private static final String INFO_DASHES = "[INFO] ==================================";
	private static final String ERROR_DASHES = "[ERROR] ==================================";
	private static final String LOG_COMMENT_STATE_DELETED = " AND ecm:currentLifeCycleState != 'deleted'";
	private static final String FACTURE = "facture";
	private static final String DUBLINCORE = "dublincore";
	private static final String LOG_COMMENT_1 = " >=====";
	private static final String FILE_CONTENT = "file:content";
	private ArrayList<Contact> contacts = new ArrayList<>();
	Set setA = new HashSet();
	Set setNumerosFac = new HashSet();
	String globaleWriteStream = "";
	private static final String Path_files="../dossier_factures";
	private boolean mailExist=false;
	private boolean statusExist=false;
	private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();
	

	@OperationMethod
	public void run() throws Exception,OperationException
	{
		new File(Path_files).mkdir();
		List<DocumentModel> documentModelList = (List<DocumentModel>) service.run(ctx, "Seam.GetSelectedDocuments");
		for (int j = 0; j < documentModelList.size(); j++) 
		{
		       String status =(String) documentModelList.get(j).getCurrentLifeCycleState();
		        if(!status.equals("Approved"))
		        {
		        	statusExist=true;
		         	displayInfoOrException.sendMessage(ctx, service, AddMessage.ID, "#{messages['label.bc.valid']}");
		        }
		 }
		 for (int j = 0; j < documentModelList.size(); j++) 
		 {
		        String mail =mailFournisseur((String) documentModelList.get(j).getPropertyValue("bon_commande:vendorname"));
		        if(mail ==null)
		        {	
		        mailExist=true;
		       	displayInfoOrException.sendMessage(ctx, service, AddMessage.ID, "#{messages['label.saisie.adr']}");
		        }
		 }
		for (int j = 0; j < documentModelList.size(); j++)
	    {
	      String Nomfour = (String)((DocumentModel)documentModelList.get(j)).getPropertyValue("bon_commande:vendorname");
	      Long Numfact = (Long)((DocumentModel)documentModelList.get(j)).getPropertyValue("bon_commande:num_commande");
	      this.setA.add(Nomfour);
	      this.setNumerosFac.add(Numfact);
	      System.out.println(Nomfour);
	      System.out.println(Numfact);
	    }
		if(mailExist==false && statusExist==false) 
		{  //liste des ice fournisseurs vers arrayList
		   Stream stream = setA.stream();
		   ArrayList<String> namesList = new ArrayList<>(setA);
		   //liste des nums fournisseurs vers arrayList
		   Stream strm = setNumerosFac.stream();
		   String numsFactures="";
		   ArrayList<Long> numlists = new ArrayList<>(setNumerosFac);
		   for(int j=0; j<numlists.size(); j++) 
		   {
			numsFactures+= numlists.get(j).toString()+",";
		   }
		   numsFactures=numsFactures.substring(0,numsFactures.length()-1);
		   System.out.println("liste des numeros factures"+numsFactures);	
		  for(int i=0; i<namesList.size(); i++) 
		  {
			System.out.println("le "+i+ "indice  "+namesList.get(i));
			fileConcate(namesList.get(i),i,numsFactures);
		  }
		  Map<String, Object> paramss = new HashMap<String, Object>();
		  paramss.put("message", "<b>Message:</b> <br/>" +"#{messages['label.envoie.msg']}");
		  service.run(ctx, "Seam.AddInfoMessage", paramss);
		}
	}
public File fileConcate(String ICEFornisseur, int index,String numlists) throws Exception 
{    
    String mailTo="";
	String mailfrom="";
	String mailuser="";
	ArrayList<File> files = new ArrayList<>();
	String query = "SELECT * FROM Document WHERE ecm:parentId IS NOT NULL AND ecm:primaryType = 'Bon_Commande' AND bon_commande:num_commande in ("+numlists+")  AND bon_commande:vendorname='"+ ICEFornisseur +"'"+LOG_COMMENT_STATE_DELETED;
	System.out.println("listes     "+query);
	List<DocumentModel> documentModelList = session.query(query);
	for (int j = 0; j < documentModelList.size(); j++) 
	{	
	 Blob fileContent = (Blob) documentModelList.get(j).getPropertyValue("file:content");
	 mailTo = (String)mailFournisseur((String) documentModelList.get(j).getPropertyValue("bon_commande:vendorname")) ;
	 mailfrom = "bv.info@360businessventures.com";
	 mailuser = "bv.info@360businessventures.com";
	 files.add(fileContent.getFile());
	}
	PDFMergerUtility ut = new PDFMergerUtility();
	for(int i = 0; i<files.size(); i++ ) 
	{
		try 
		{
			ut.addSource(files.get(i));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	ut.setDestinationFileName(Path_files+"\\BC"+index+".pdf");
	ut.mergeDocuments();
	sendEmail(mailTo,mailfrom,mailuser,"360@&Fg7","Bons Commande Validees",Path_files+"\\BC"+index+".pdf");
	 try
        { 
            Files.deleteIfExists(Paths.get(Path_files+"\\BC"+index+".pdf")); 
        } 
        catch(NoSuchFileException e) 
        { 
            System.out.println("No such file/directory exists"); 
        } 
        catch(DirectoryNotEmptyException e) 
        { 
            System.out.println("Directory is not empty."); 
        } 
        catch(IOException e) 
        { 
            System.out.println("Invalid permissions."); 
        } 
          
        System.out.println("Deletion successful."); 
     
	return null;
}



public void sendEmail(String to, String from, final String username, final String password, String subject, String path) throws Exception
{
  String host = "smtp.gmail.com";  
  Properties props = new Properties();
  props.put("mail.smtp.auth", "true");
  props.put("mail.pop3.host", "localhost");
  props.put("mail.pop3.port", "110");
  props.put("mail.pop3.user", "anonymous");
  props.put("mail.smtp.host", "smtp.ipage.com");
  props.put("mail.smtp.port", "587");
  Session session = Session.getInstance(props, new Authenticator()
  {
    protected PasswordAuthentication getPasswordAuthentication()
    {
      return new PasswordAuthentication(username, password);
    }
  });
  try
  {
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(from));
    System.out.println("Sent message"+to);
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
    message.setSubject(subject);
   String contentEmail = "Merci de noter que les BCs ci-dessous ont été validées.\n\n Cordialement. \n CET EMAIL EST ENVOYÉ VIA LA SOLUTION DE 360 BUSINESS VENTURES-www.360businessventures.com ";
    message.setText(contentEmail);
    Multipart multipart = new MimeMultipart();
    MimeBodyPart textBodyPart = new MimeBodyPart();
    textBodyPart.setText(contentEmail, "utf-8");
    MimeBodyPart attachmentBodyPart= new MimeBodyPart();
    DataSource source = new FileDataSource(path); 
    attachmentBodyPart.setDataHandler(new DataHandler(source));
    attachmentBodyPart.setFileName(source.getName());
    multipart.addBodyPart(textBodyPart);  // add the text part
    multipart.addBodyPart(attachmentBodyPart); // add the attachement part
    message.setContent(multipart);
    Transport.send(message);
    System.out.println("Sent message successfully.... from bv.info@360businessventures.com");
  }
  catch (MessagingException e)
  {
     throw new Exception(e);
  }
  

  }



public String  mailFournisseur(String nomfournisseur)
{
	  String querie = "SELECT * FROM Document WHERE ecm:parentId IS NOT NULL AND ecm:primaryType = 'fiche-fournisseur' AND fiche-fournisseur:nom-fournisseur='"+ nomfournisseur +"'"+LOG_COMMENT_STATE_DELETED;
	  System.out.println("listes     "+querie);
	  List<DocumentModel> documentModelListICE = session.query(querie);   
      String mail =null;
      if(documentModelListICE.size()>0)
    	  mail=(String) documentModelListICE.get(0).getPropertyValue("fiche-fournisseur:E-mail");
	  if(mail!=null)
	   {
	     return mail;
	   }
     return null;
 }

}