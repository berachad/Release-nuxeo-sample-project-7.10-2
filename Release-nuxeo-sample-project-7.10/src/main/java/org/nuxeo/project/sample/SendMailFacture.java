package org.nuxeo.project.sample;

import javax.mail.Transport;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.Properties;

import java.io.IOException; 
import java.nio.file.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
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
import org.nuxeo.ecm.automation.core.operations.blob.ConcatenatePDFs;
import org.nuxeo.ecm.automation.core.util.BlobList;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.webapp.clipboard.ClipboardActionsBean;
import javax.mail.Session;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


@Operation(id = SendMailFacture.ID, category = Constants.CAT_DOCUMENT, label = "Worklist Export Demo")
public class SendMailFacture {

	@Context
	protected CoreSession session;

	@Context
	protected AutomationService service;

	@Context
	protected OperationContext ctx = new OperationContext(session);

	public static final String ID = "Document.SendMailFacture";

	@Inject
	private ClipboardActionsBean clipboardActionsBean = new ClipboardActionsBean();

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

	@OperationMethod
	public void run() throws Exception 
	{
		new File(Path_files).mkdir();
		//String query = "SELECT * FROM Document WHERE ecm:parentId IS NOT NULL AND ecm:primaryType = 'FactureFournisseur' AND  FF1:statutFacture = 'Rejeté'"+ LOG_COMMENT_STATE_DELETED;
		//List<DocumentModel> documentModelList = session.query(query);
		List<DocumentModel> documentModelList = (List<DocumentModel>) service.run(ctx, "Seam.GetSelectedDocuments");
		for (int j = 0; j < documentModelList.size(); j++) 
		{
			String Nomfour = (String) documentModelList.get(j).getPropertyValue("FF1:NomFournisseur");
			String Numfact = (String) documentModelList.get(j).getPropertyValue("FF1:NumeroFacture");
			setA.add(Nomfour);
			setNumerosFac.add(Numfact);
			System.out.println(Nomfour);
			System.out.println(Numfact);
	    }
		//liste des noms fournisseurs vers arrayList
		   Stream stream = setA.stream();
		   ArrayList<String> namesList = new ArrayList<>(setA);
		//liste des nums fournisseurs vers arrayList
		   Stream strm = setNumerosFac.stream();
		   String numsFactures="";
		   ArrayList<String> numlists = new ArrayList<>(setNumerosFac);
		 
		   
		for(int j=0; j<numlists.size(); j++) 
		 {
				numsFactures+="'"+numlists.get(j)+"',";
		 }
		   numsFactures=numsFactures.substring(0,numsFactures.length()-1);
		   System.out.println("liste des numeros factures"+numsFactures);	
		for(int i=0; i<namesList.size(); i++) 
		{
		  System.out.println("le "+i+ "indice  "+namesList.get(i));
		  fileConcate(namesList.get(i),i,numsFactures);
		}	
	}
public File fileConcate(String nameFornisseur, int index,String numlists) throws Exception 
{    
	 final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
	 FileOutputStream fileOut;
	 Workbook workbook = new XSSFWorkbook();
     String[] columns = { "Nom de facture", "Date facture", "Montant facture", "commentaire rejet"};
     String nameFile = "liste_des_factures.xlsx";
     File f = new File(nameFile);
     Sheet sheet =workbook.createSheet("Factures");
	 Font headerFont = workbook.createFont();
	 headerFont.setFontHeightInPoints((short) 11);
     CellStyle headerCellStyle = workbook.createCellStyle();
     headerCellStyle.setFont(headerFont);
	  // Create a Row
      Row headerRow = sheet.createRow(0);
      for (int i = 0; i < columns.length; i++) 
      {
	      Cell cell = headerRow.createCell(i);
	      cell.setCellValue(columns[i]);
	      cell.setCellStyle(headerCellStyle);
	   }
    int rowNum = 1;
    String mailTo="";
	String mailfrom="";
	String mailuser="";
	ArrayList<File> files = new ArrayList<>();
	String query = "SELECT * FROM Document WHERE ecm:parentId IS NOT NULL AND ecm:primaryType = 'FactureFournisseur' AND FF1:NumeroFacture in ("+numlists+")  AND FF1:NomFournisseur ='"+ nameFornisseur +"'"+LOG_COMMENT_STATE_DELETED;
	System.out.println("listes     "+query);
	List<DocumentModel> documentModelList = session.query(query);
	for (int j = 0; j < documentModelList.size(); j++) 
	{	
     Blob fileContent = (Blob) documentModelList.get(j).getPropertyValue("file:content");
     mailTo = (String) documentModelList.get(j).getPropertyValue("FF1:E-mail");
     mailfrom = "bv.info@360businessventures.com";
     mailuser = "bv.info@360businessventures.com";
     files.add(fileContent.getFile());
     //creation une ligne pour chaque facture
     Row row = sheet.createRow(rowNum++);
       int i = 0;
     row.createCell(i).setCellValue((String) documentModelList.get(j).getPropertyValue("dc:title"));
     GregorianCalendar gcal = (GregorianCalendar) documentModelList.get(j).getPropertyValue("FF1:DateFacture");
     System.out.print("Gregorian date: "  + gcal.getTime());   
     System.out.println((documentModelList.get(j).getPropertyValue("FF1:DateFacture")).getClass());
     row.createCell(++i).setCellValue(format.format((Date)gcal.getTime()));
     row.createCell(++i).setCellValue(String.format("%.0f", documentModelList.get(j).getPropertyValue("FF1:TotalHT")).replace("," , "."));
     row.createCell(++i).setCellValue((String) documentModelList.get(j).getPropertyValue("FF1:commentairerejet"));    
     for (int s = 0; s < columns.length; s++) {
	      sheet.autoSizeColumn(s);
	    }
      fileOut = new FileOutputStream(f);
	    workbook.write(fileOut);
	    fileOut.close();
	}
	PDFMergerUtility ut = new PDFMergerUtility();
	for(int i = 0; i<files.size(); i++ ) 
	{
		try 
		{
			ut.addSource(files.get(i));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	ut.setDestinationFileName(Path_files+"\\Facture"+index+".pdf");
	ut.mergeDocuments();
	sendEmail(mailTo,mailfrom,mailuser,"360@&Fg7","Factures rejete",Path_files+"\\Facture"+index+".pdf",f.getName());
	 try
        { 
            Files.deleteIfExists(Paths.get(Path_files+"\\Facture"+index+".pdf")); 
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



public void sendEmail(String to, String from, final String username, final String password, String subject, String path,String fileexsl) throws Exception
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
   String contentEmail = "Merci de noter que les factures ci-dessous ont été rejetées,si les factures sont réglées ultérieurement,prière de ne pas tenir compte de ce mail.\n\n Cordialement. \n CET EMAIL EST ENVOYE VIA LA SOLUTION DE 360 BUSINESS VENTURES-www.360businessventures.com ";
    message.setText(contentEmail);
    Multipart multipart = new MimeMultipart();
    MimeBodyPart textBodyPart = new MimeBodyPart();
    textBodyPart.setText(contentEmail, "utf-8");
    MimeBodyPart attachmentBodyPart= new MimeBodyPart();
    DataSource source = new FileDataSource(path); // ex : "C:\\test.pdf"
    attachmentBodyPart.setDataHandler(new DataHandler(source));
    attachmentBodyPart.setFileName(source.getName());
    multipart.addBodyPart(textBodyPart);  // add the text part
    multipart.addBodyPart(attachmentBodyPart); // add the attachement part
    DataSource src = new FileDataSource(fileexsl);
    BodyPart messageBodyPart1 = new MimeBodyPart();        
    messageBodyPart1.setDataHandler(new DataHandler(src));
    messageBodyPart1.setFileName(src.getName());
    multipart.addBodyPart(messageBodyPart1);
    message.setContent(multipart);
    Transport.send(message);
    System.out.println("Sent message successfully.... from bv.info@360businessventures.com");
  }
  catch (MessagingException e)
  {
    throw new RuntimeException(e);
  }
}





	









}