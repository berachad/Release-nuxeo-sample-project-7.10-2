package org.nuxeo.project.sample;
import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
public class Mail {

	 public static void send(String to, String FirstName, 
             String LastName, String Entreprise,String Email, String PassWord, String ConformPassword, String Subject) {
	 
	        String host = "smtp.gmail.com";
	        
	        Properties props = new Properties();
	        props.put("mail.smtp.auth", "true");
	        
	        props.put("mail.pop3.host", "localhost");
	        props.put("mail.pop3.port", "110");
	        props.put("mail.pop3.user", "anonymous");
	        props.put("mail.smtp.host", "smtp.ipage.com");
	        props.put("mail.smtp.port", "587");
	     
	         
	       String mailfrom = "bv.info@360businessventures.com";
	   	String password="360@&Fg7";
	        Session session = Session.getInstance(props,new javax.mail.Authenticator()
	        {
	            protected PasswordAuthentication getPasswordAuthentication()
	            {
	            
					
	            	return new PasswordAuthentication(mailfrom, password);
	            }
	        });

	        try {
	 
 
	            MimeMessage message = new MimeMessage(session);
	            message.setFrom(new InternetAddress(mailfrom));
	            message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
	            message.setSubject(Subject); 
	            String content = ("<html>" +
	                    "<body>" +
	            	    "<p style=\"color: #279BE4; font-family: Arial; font-size: 14px; font-weight: bold;\" >Bonjour Merci de créer un compte avec les informations suivantes :</p></br>"+
	                    "<table>" +
	                    "<tr style=\"color: #2b333e; font-family: Arial; font-size: 14px; font-weight: bold;\">" +
	                    "<td style=\"padding: 10px;\">Nom : </td>" +
	                    "<td style=\"padding: 10px;\">"+FirstName+"</td>" +"</tr>" +
	                    "<tr style=\"color: #2b333e; font-family: Arial; font-size: 14px; font-weight: bold;\">" +
	                    "<td style=\"padding: 10px;\">Prenom : </td>" +
	                    "<td style=\"padding: 10px;\">"+LastName+"</td>" +"</tr>" +
	                    "<tr style=\"color: #2b333e; font-family: Arial; font-size: 14px; font-weight: bold;\">" +
	                    "<td style=\"padding: 10px;\">Entreprise : </td>" +
	                    "<td style=\"padding: 10px;\">"+Entreprise+"</td>" +"</tr>"+
	                    "<tr style=\"color: #2b333e; font-family: Arial; font-size: 14px; font-weight: bold;\">" +
	                    "<td style=\"padding: 10px;\">Email : </td>" +
	                    "<td style=\"padding: 10px;\">"+Email+"</td>" +"</tr>" +
	                    "<tr style=\"color: #2b333e; font-family: Arial; font-size: 14px; font-weight: bold;\">" +
	                    "<td style=\"padding: 10px;\">Mot de passe : </td>" +
	                    "<td style=\"padding: 10px;\">"+PassWord+"</td>" +"</tr>"+
	                    "<tr style=\"color: #2b333e; font-family: Arial; font-size: 14px; font-weight: bold;\">" +
	                    "<td style=\"padding: 10px;\">Confirmation mot de passe : </td>" +
	                    "<td style=\"padding: 10px;\">"+ConformPassword+"</td>" +"</tr>"+
	                    "</table>"
	                     +
	                    "</br></br><p style=\"color: #279BE4; font-family: Arial; font-size: 14px; font-weight: bold;\">Cordialement</p>"
	                   ); 
	            message.setContent(content, "text/html");
	          
	         
	            Transport.send(message);
	        }
	        catch(Exception e) {
	    	     e.printStackTrace();
	        }
	    }

	 

}
