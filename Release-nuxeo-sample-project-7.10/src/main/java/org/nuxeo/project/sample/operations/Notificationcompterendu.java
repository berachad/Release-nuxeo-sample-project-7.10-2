package org.nuxeo.project.sample.operations;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
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
import org.nuxeo.ecm.user.center.profile.UserProfileService;
import org.nuxeo.project.sample.beans.User;
import org.nuxeo.project.sample.services.DisplayInfoOrException;
import org.nuxeo.project.sample.services.MethodsShared;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.ecm.platform.ui.web.util.BaseURL;
import org.nuxeo.ecm.platform.web.common.locale.LocaleProvider;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Operation(id = Notificationcompterendu.ID, category = Constants.CAT_DOCUMENT, label = "Notification compte rendu")
public class Notificationcompterendu{
	
	public static final String ID = "Document.Notificationcompterendu";

	@Context
	protected CoreSession session;
	@Context
	protected AutomationService service;
	@Context
	protected OperationContext ctx = new OperationContext(session);
     
    private final Log log = LogFactory.getLog(Notificationcompterendu.class);
    
    private MethodsShared method = new MethodsShared();
	private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();
	String text="";
	final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	String linkOfCompterendu="";
	String locale=null;
	boolean status_email=false;
	 
		
	 @OperationMethod
	 public void run(DocumentModel input) throws OperationException 
	 {  
		 DocumentModel doc = (DocumentModel) service.run(ctx, "Context.FetchDocument");
		 String IdDoc= doc.getId();
		 String responsable = (String) doc.getProperty("compte_rendu", "responsable");
		 
		 List<Map<String, Serializable>> actions = (List<Map<String, Serializable>>) doc.getPropertyValue("compte_rendu:actions");
		 UserProfileService ups = Framework.getService(UserProfileService.class);
		 DocumentModel profile2 = ups.getUserProfileDocument(responsable, session);
		  Locale local = null;
		 local = Framework.getLocalService(LocaleProvider.class).getLocale(session);
		  if (local == null) {
		    locale = Locale.getDefault().toString();
		        }
		  else {
			  locale=local.toString();
				  
		  }
		 linkOfCompterendu = "\n" + BaseURL.getBaseURL().toString() + "/nxdoc/default/" +IdDoc + "/view_documents?tabIds=%3A&conversationId=0NXMAIN";
		 User user1=method.findUserByUsername(responsable);
		 String email_responsable =(String)user1.getEmail();   
		 String mailfrom = "bv.info@360businessventures.com";
		 String  mailuser = "bv.info@360businessventures.com";
		 if (!isValid(email_responsable)) 
		 {
			 status_email=true;	 
 
		 }
		
		 actions.forEach(action->
		 {
			 String responsable_action = (String) action.get("responsable");
			 User user2=method.findUserByUsername(responsable_action);
			 String email=(String)user2.getEmail(); 
			 if (!isValid(email)) 
			 {
				 status_email=true;	 
	 
			 }
			
		});
		 
		 if(status_email==false)
		 {
		 if(locale.equals("fr") || locale.equals("fr_FR") || locale.equals("fr_CA")) 
			{
			 
			 text=text+"<table width='100%'  style=\"border: 1px solid #c8c9cc;text-align: center; border-collapse: collapse;width: 100%;\" align='center' >"
		                + "<tr align='center' >"
		                + "<td style=\"background-color:rgb(39, 155, 228);color: white\" ><b>Action<b></td>"
		                + "<td style=\"background-color:rgb(39, 155, 228);color: white\"><b>Affectée à<b></td>"
		                + "<td style=\"background-color:rgb(39, 155, 228);color: white\"><b>Délai de réalisation <b></td>"
		                + "</tr>";	
			}else 
			{
				 text=text+"<table width='100%'  style=\"border: 1px solid #c8c9cc;text-align: center; border-collapse: collapse;width: 100%;\" align='center'>"
			                + "<tr align='center' style=\"border:none;\">"
			                + "<td style=\"background-color:rgb(39, 155, 228);color: white\"><b>Action<b></td>"
			                + "<td style=\"background-color:rgb(39, 155, 228);color: white\"><b>Affected to<b></td>"
			                + "<td style=\"background-color:rgb(39, 155, 228);color: white\"><b>Deadline<b></td>"
			                + "</tr>";		
			}
		 //parcours des actions et l'envoie d'une notification à chaque responsable d'action
		 actions.forEach(action->
		 {
			 String responsable_action = (String) action.get("responsable");
			 String description = (String) action.get("description");
			 User user2=method.findUserByUsername(responsable_action);
			 String email=(String)user2.getEmail();
			 GregorianCalendar gcal = (GregorianCalendar)action.get("date_realisation");  
			 text=text+"<tr align='center'>"+"<td><a href=\""+linkOfCompterendu+"\">" +description+ "</td>"
                      + "<td>" + responsable_action + "</td>"
					  +"<td>" + format.format((Date)gcal.getTime()) + "</td></tr>";
			 try{
				 if(!email.equals(null))
				 {
						if(locale.equals("fr") || locale.equals("fr_FR") || locale.equals("fr_CA")) 
						{
							sendEmail(email,mailfrom,mailuser,"360@&Fg7","Action affectee",description,responsable,(Date)gcal.getTime());
						}
						else 
						{
							sendEmailAnglais(email,mailfrom,mailuser,"360@&Fg7","Affected action",description,responsable,(Date)gcal.getTime());
							
						}
				 }
				 else
				 {
					 status_email=true;
				 }
			 }
			 catch(Exception ex)
			 {
				 System.out.println(ex.getMessage());
			 } 
		 }); 
		 try{
				if(locale.equals("fr") || locale.equals("fr_FR") || locale.equals("fr_CA")) 
					{
						sendEmailResponsable(email_responsable,mailfrom,mailuser,"360@&Fg7","Liste des actions",text);
					}
					else 
					{
						sendEmailResponsableAnglais(email_responsable,mailfrom,mailuser,"360@&Fg7","Actions list",text);
					}
			 
		    }
		 catch(Exception ex)
		 {
			 System.out.println(ex.getMessage());
		 }
		 
	 }else {
		 try {
				displayInfoOrException.sendMessage(ctx, service, AddMessage.ID, "#{messages['label.saisie.email']}");
			} catch (OperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }
		 
		 
	 }
	 public void sendEmail(String to, String from, final String username, final String password, String subject, String description,String responsablecompte,Date date_realisation) throws Exception
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
	     String contentEmail = "Prière de noter que l'action  <a href=\""+linkOfCompterendu+"\">"+description+"</a> vous a été affecté par "+ responsablecompte+". La date limite de réalisation de cette action est le "+format.format(date_realisation) +".<br/>Cordialement.<br/>CET EMAIL EST ENVOYE VIA LA SOLUTION DE 360 BUSINESS VENTURES-www.360businessventures.com ";
	     message.setText(contentEmail);
	     Multipart multipart = new MimeMultipart();
	     MimeBodyPart textBodyPart = new MimeBodyPart();
	     textBodyPart.setText(contentEmail, "utf-8","html");
	     multipart.addBodyPart(textBodyPart);  // add the text part
	     message.setContent(multipart);
	     Transport.send(message);
	     System.out.println("Sent message successfully.... from bv.info@360businessventures.com");
	   }
	   catch (MessagingException e)
	   {
	     throw new RuntimeException(e);
	   }
	 }
	 public void sendEmailResponsable(String to, String from, final String username, final String password, String subject, String text) throws Exception
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
	     String contentEmail = "Prière de noter que vous avez affecté les actions suivantes:"+text+"</table><br/>Cordialement.<br/> CET EMAIL EST ENVOYE VIA LA SOLUTION DE 360 BUSINESS VENTURES-www.360businessventures.com ";
	     message.setText(contentEmail);
	     Multipart multipart = new MimeMultipart();
	     MimeBodyPart textBodyPart = new MimeBodyPart();
	     textBodyPart.setText(contentEmail, "utf-8","html");
	     multipart.addBodyPart(textBodyPart);  // add the text part
	     message.setContent(multipart);
	     Transport.send(message);
	     System.out.println("Sent message successfully au responsable.... from bv.info@360businessventures.com");
	   }
	   catch (MessagingException e)
	   {
	     throw new RuntimeException(e);
	   }
	 }
	 public void sendEmailAnglais(String to, String from, final String username, final String password, String subject, String description,String responsablecompte,Date date_realisation) throws Exception
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
	     String contentEmail = "Please be advised that the action <a href=\""+linkOfCompterendu+"\">"+description+"</a>has been affected by "+ responsablecompte+".The deadline for carrying out this action is   "+format.format(date_realisation) +".<br/>Sincerely.<br/>THIS EMAIL IS SENT FROM 360 BUSINESS VENTURES-www.360businessventures.com ";
	     message.setText(contentEmail);
	     Multipart multipart = new MimeMultipart();
	     MimeBodyPart textBodyPart = new MimeBodyPart();
	     textBodyPart.setText(contentEmail, "utf-8","html");
	     multipart.addBodyPart(textBodyPart);  // add the text part
	     message.setContent(multipart);
	     Transport.send(message);
	     System.out.println("Sent message successfully.... from bv.info@360businessventures.com");
	   }
	   catch (MessagingException e)
	   {
	     throw new RuntimeException(e);
	   }
	 }
	 public void sendEmailResponsableAnglais(String to, String from, final String username, final String password, String subject, String text) throws Exception
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
	     String contentEmail = "Please note that you have affected the following actions:"+text+"</table><br/>Sincerely.<br/>THIS EMAIL IS SENT FROM 360 BUSINESS VENTURES-www.360businessventures.com .";
	     message.setText(contentEmail);
	     Multipart multipart = new MimeMultipart();
	     MimeBodyPart textBodyPart = new MimeBodyPart();
	     textBodyPart.setText(contentEmail, "utf-8","html");
	     multipart.addBodyPart(textBodyPart);  // add the text part
	     message.setContent(multipart);
	     Transport.send(message);
	     System.out.println("Sent message successfully au responsable.... from bv.info@360businessventures.com");
	   }
	   catch (MessagingException e)
	   {
	     throw new RuntimeException(e);
	   }
	 }
	 public  boolean isValid(String email) 
	    { 
	        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+ 
	                            "[a-zA-Z0-9_+&*-]+)*@" + 
	                            "(?:[a-zA-Z0-9-]+\\.)+[a-z" + 
	                            "A-Z]{2,7}$"; 
	                              
	        Pattern pat = Pattern.compile(emailRegex); 
	        if (email == null) 
	            return false; 
	        return pat.matcher(email).matches(); 
	    } 
}
