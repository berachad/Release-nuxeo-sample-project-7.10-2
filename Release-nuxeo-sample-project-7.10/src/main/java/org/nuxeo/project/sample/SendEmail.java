package org.nuxeo.project.sample;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@WebServlet("/SendEmailServlett")
public class SendEmail extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		      throws ServletException, IOException {
		 String Subject ="Demande d'inscription";
		 String to = "aberachad@360businessventures.com";
		 String FirstName = request.getParameter("FirstName");
	     String LastName = request.getParameter("LastName"); 
	     String Entreprise = request.getParameter("Entreprise");
	     String Email = request.getParameter("Email");
	     String PassWord = request.getParameter("PassWord");
	     String ConformPassword = request.getParameter("ConformPassword");
	       
	        // do some processing here...
	         
	        // get response writer
	        response.setContentType("text/html;charset=UTF-8");
	        PrintWriter writer = response.getWriter();
	         
	        // build HTML code
	        String htmlRespone = "<html>";
	 
	        Mail.send(to,FirstName, LastName, Entreprise,Email,PassWord,ConformPassword,  Subject); 
	        // return response
	        writer.println(htmlRespone);	 
	        
	        response.sendRedirect("login.jsp");  
	        }
	// Method to handle POST method request.
	   public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {

	      doGet(request, response);
	   }

}
