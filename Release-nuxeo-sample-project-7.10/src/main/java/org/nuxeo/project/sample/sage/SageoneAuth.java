package org.nuxeo.project.sample.sage;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.nuxeo.project.sample.services.MethodsShared;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Servlet implementation class SageoneAuth
 */
@WebServlet("/SageoneAuth")
public class SageoneAuth extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    public MethodsShared MethodsShared;

	/**
	 * Redirect the user to the authorisation url with the required query params
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MethodsShared = new MethodsShared();

		System.out.println("SAGE : " + resp.encodeRedirectURL(MethodsShared.Config().getEndpointCentral() + "?response_type=code&client_id=" + MethodsShared.Config().getClientId() + "&redirect_uri=" + MethodsShared.Config().getUrl() + "&scope=" + MethodsShared.Config().getScope()));
		resp.sendRedirect(resp.encodeRedirectURL(MethodsShared.Config().getEndpointCentral() + "?response_type=code&client_id=" + MethodsShared.Config().getClientId() + "&redirect_uri=" + MethodsShared.Config().getUrl() + "&scope=" + MethodsShared.Config().getScope()));
	    System.out.println("---------------------le code after redirection------------------");
	    String code = req.getParameter("code");
		System.out.println("code : " + code);
		
		//Creating a HttpClient object
	      CloseableHttpClient httpclient = HttpClients.createDefault();

	      //Creating a HttpGet object
	      HttpGet httpget = new HttpGet(MethodsShared.Config().getEndpointCentral() + "?response_type=code&client_id=" + MethodsShared.Config().getClientId() + "&redirect_uri=" + MethodsShared.Config().getUrl() + "&scope=" + MethodsShared.Config().getScope());

	      //Printing the method used
	      System.out.println("Request Type: "+httpget.getMethod());

	      //Executing the Get request
	      HttpResponse httpresponse = httpclient.execute(httpget);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
