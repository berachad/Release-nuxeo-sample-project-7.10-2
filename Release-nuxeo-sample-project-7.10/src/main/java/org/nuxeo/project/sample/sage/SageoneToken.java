package org.nuxeo.project.sample.sage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.project.sample.services.MethodsShared;

/**
 * Servlet implementation class AccessToken
 */
@WebServlet("/SageoneToken")
public class SageoneToken extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	//private static ResourceBundle bundle = ResourceBundle.getBundle("ConfigSage");
	public MethodsShared MethodsShared;
	private HttpSession session ;
	/**
	 * POST request to exchange authorisation code for access_token
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MethodsShared = new MethodsShared();
		String requestURL = MethodsShared.Config().getEndpoint();
		System.out.println("requestURL : " + requestURL);
		String clientId = MethodsShared.Config().getClientId();
		System.out.println("clientId : " + clientId);
		String secret = MethodsShared.Config().getSecretClient();
		System.out.println("secret : " + secret);
		String callbackUrl = MethodsShared.Config().getUrl();
		System.out.println("callbackUrl : " + callbackUrl);
		String address_region_id = MethodsShared.Config().getAddress_region_id();
		System.out.println("address_region_id  : " + address_region_id);
		String code = req.getParameter("code");
		System.out.println("code : " + code);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("client_id", clientId));
		params.add(new BasicNameValuePair("client_secret", secret));
		params.add(new BasicNameValuePair("code", code));
		params.add(new BasicNameValuePair("grant_type", "authorization_code"));
		params.add(new BasicNameValuePair("redirect_uri", callbackUrl));
	    String[] chaine=callbackUrl.split("/");
	    String nouveauchaine=chaine[0]+"/"+chaine[1]+"/"+chaine[2]+"/"+chaine[3]+"/";
	
		try {
			HttpPost request = new HttpPost(requestURL);
			request.addHeader("Content-Type", "application/x-www-form-urlencoded");
			request.setEntity(new UrlEncodedFormEntity(params));
			HttpClient httpclient = HttpClients.createDefault();
			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();
			JSONObject jsonResponse = new JSONObject(EntityUtils.toString(entity));
			String token = jsonResponse.getString("access_token");
			System.out.println("token : " + token);
			session = req.getSession();
			session.setAttribute("sageToken", token);
			session.setAttribute("code", code);
			
			//----------------- transferer des taxes ----------------------------------
			ArrayList<NameValuePair> paramsTaxe = new ArrayList<NameValuePair>();
			paramsTaxe.add(new BasicNameValuePair("request_method", "get"));
			paramsTaxe.add(new BasicNameValuePair("endpoint", "tax_rates"));
			paramsTaxe.add(new BasicNameValuePair("code", code));
			paramsTaxe.add(new BasicNameValuePair("access_token",token));
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet requestTaxes = new HttpGet(nouveauchaine+"SageoneData2");
			URI uri;
			try {
				uri = new URIBuilder(requestTaxes.getURI()).addParameters(paramsTaxe).build();
				requestTaxes.setURI(uri);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			HttpResponse responseTaxe = client.execute(requestTaxes); 
			//----------------- transferer des vendors ----------------------------------
			ArrayList<NameValuePair> paramsvendor = new ArrayList<NameValuePair>();
			paramsvendor.add(new BasicNameValuePair("request_method", "get"));
			paramsvendor.add(new BasicNameValuePair("endpoint",  "ledger_accounts"));
			paramsvendor.add(new BasicNameValuePair("code", code));
			paramsvendor.add(new BasicNameValuePair("access_token",token));
			HttpClient clientvendor = HttpClientBuilder.create().build();
			HttpGet requestvendors = new HttpGet(nouveauchaine+"SageoneData3");
			URI urivendor;
			try {
				urivendor = new URIBuilder(requestvendors.getURI()).addParameters(paramsvendor).build();
				requestvendors.setURI(urivendor);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			HttpResponse responsevendor = client.execute(requestvendors); 
			//----------------------fin-------------------------------------------
			//System.out.println("le resultat : " + nouveauchaine+"SageoneData3");
			//System.out.println("le resultat 2 : " +responsevendor);
			//resp.sendRedirect(resp.encodeRedirectURL(nouveauchaine+"login.jsp"));
			//--------------------transfer des compte comptable-------------------
			ArrayList<NameValuePair> paramscompte = new ArrayList<NameValuePair>();
			paramscompte.add(new BasicNameValuePair("request_method", "get"));
			paramscompte.add(new BasicNameValuePair("endpoint","contacts?contact_type_id=VENDOR"));
			paramscompte.add(new BasicNameValuePair("code", code));
			paramscompte.add(new BasicNameValuePair("access_token",token));
			HttpClient clientcompte = HttpClientBuilder.create().build();
			HttpGet requestcomptes = new HttpGet(nouveauchaine+"SageoneData4");
			URI uricomptes;
			try {
				uricomptes = new URIBuilder(requestcomptes.getURI()).addParameters(paramscompte).build();
				requestcomptes.setURI(uricomptes);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			HttpResponse responsecompte = clientcompte.execute(requestcomptes); 
			//----------------------fin-------------------------------------------
			//System.out.println("le resultat : " + nouveauchaine+"SageoneData3");
			//System.out.println("le resultat 2 : " +responsecompte);
			resp.sendRedirect(resp.encodeRedirectURL(nouveauchaine+"login.jsp"));
			//--------------------------------------------------------------------
			
			
			
			
			
			
			
			/*resp.getWriter().println("<div role='tabpanel' class='tab-pane fade in active' id='get'>");
			resp.getWriter().println("<form action='/e-Doc360-SG2/SageoneData4' method='get'>");
			resp.getWriter().println("<input name='request_method' type='hidden' value='get'>");
			resp.getWriter().println("<input name='endpoint' type='hidden' value='contacts?contact_type_id=VENDOR'>");
			resp.getWriter().println("<input name='data' type='hidden' value=''>");
			resp.getWriter().println("<input name='access_token' type='hidden' value='" + token + "'>");
			//resp.getWriter().println("<input name='resource_owner_id' type='hidden' value='" + resourceOwnerId + "'>");
			resp.getWriter().println("<input type='submit' value='Import vendors' class='btn btn-primary'>");
			resp.getWriter().println("</form>");
			
			
			//String resourceOwnerId = jsonResponse.getString("resource_owner_id");
			//System.out.println("resourceOwnerId : " + resourceOwnerId);

			resp.setContentType("text/html") ;
			resp.getWriter().println("<html>");
			resp.getWriter().println("<head>");
			resp.getWriter().println("<link type=\"text/css\" rel=\"stylesheet\" href=\"../sample_app.css\">");
			resp.getWriter().println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>");
			resp.getWriter().println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\" integrity=\"sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7\" crossorigin=\"anonymous\">");
			resp.getWriter().println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css\" integrity=\"sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r\" crossorigin=\"anonymous\">");
			resp.getWriter().println("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\" integrity=\"sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS\" crossorigin=\"anonymous\"></script>");
			resp.getWriter().println("<title>Exchange data</title>");
			resp.getWriter().println("</head>");
			resp.getWriter().println("<body>");
			resp.getWriter().println("<header class=\"navbar navbar-fixed-top navbar-inverse\">");
			resp.getWriter().println("<div class='container'>");
			resp.getWriter().println("<a id=\"logo\" href=\"/e-Doc360-SG2\">e-Doc360-SG2</a>");
			resp.getWriter().println("</div>");
			resp.getWriter().println("</header>");
			resp.getWriter().println("<div class='container'>");
			resp.getWriter().println("<div class='col-md-6 col-md-offset-3'>");
			resp.getWriter().println("<h3>Successfully authenticated!</h3>");
			resp.getWriter().println("<p>Your Access Token is: " + token + "</p>");
			//resp.getWriter().println("<p>Your resource_owner_id is: " + resourceOwnerId + "</p>");
//			resp.getWriter().println("<ul class='nav nav-tabs' role='tablist'>");
//			resp.getWriter().println("<li role='presentation' class='active'><a href='#get' aria-controls='get' role='tab' data-toggle='tab'>GET</a></li>");
//			resp.getWriter().println("<li role='presentation'><a href='#post' aria-controls='post' role='tab' data-toggle='tab'>POST</a></li>");
//			resp.getWriter().println("<li role='presentation'><a href='#put' aria-controls='put' role='tab' data-toggle='tab'>PUT</a></li>");
//			resp.getWriter().println("<li role='presentation'><a href='#delete' aria-controls='delete' role='tab' data-toggle='tab'>DELETE</a></li></ul>");
			resp.getWriter().println("<div class='tab-content'>");
			resp.getWriter().println("<div role='tabpanel' class='tab-pane fade in active' id='get'>");
			resp.getWriter().println("<h3>GET request</h3>");
			resp.getWriter().println("<form action='/e-Doc360-SG2/SageoneData' method='get'>");
			resp.getWriter().println("<input name='request_method' type='hidden' value='get'>");
			resp.getWriter().println("<label for='endpoint'>Endpoint</label>");
			resp.getWriter().println("<input name='endpoint' type='text' class='form-control' required='true'>");
			resp.getWriter().println("<p>Example: accounts/v3/contacts</p>");
			resp.getWriter().println("<input name='data' type='hidden' value=''>");
			resp.getWriter().println("<input name='access_token' type='hidden' value='" + token + "'>");
			//resp.getWriter().println("<input name='resource_owner_id' type='hidden' value='" + resourceOwnerId + "'>");
			resp.getWriter().println("<input type='submit' value='GET' class='btn btn-primary'>");
			resp.getWriter().println("</form>");
			resp.getWriter().println("</div>");
			resp.getWriter().println("<div role='tabpanel' class='tab-pane fade in active' id='get'>");
			resp.getWriter().println("<form action='/e-Doc360-SG2/sageone_data_1' method='get'>");
			resp.getWriter().println("<input name='request_method' type='hidden' value='get'>");
			resp.getWriter().println("<input name='endpoint' type='hidden' value='/contacts'>");
			resp.getWriter().println("<input name='data' type='hidden' value=''>");
			resp.getWriter().println("<input name='access_token' type='hidden' value='" + token + "'>");
			//resp.getWriter().println("<input name='resource_owner_id' type='hidden' value='" + resourceOwnerId + "'>");
			resp.getWriter().println("<input type='submit' value='Import bcs' class='btn btn-primary'>");
			resp.getWriter().println("</form>");
			resp.getWriter().println("</div>");
			resp.getWriter().println("<div role='tabpanel' class='tab-pane fade in active' id='get'>");
			resp.getWriter().println("<form action='/e-Doc360-SG2/SageoneData2' method='get'>");
			resp.getWriter().println("<input name='request_method' type='hidden' value='get'>");
			resp.getWriter().println("<input name='address_region_id' type='text'>");
			resp.getWriter().println("<input name='endpoint' type='hidden' value='tax_rates'>");
			//resp.getWriter().println("<input name='username' type='text' value='Administrator'>");
			//resp.getWriter().println("<input name='password' type='text' value='Administrator'>");
			resp.getWriter().println("<input name='data' type='hidden' value=''>");
			resp.getWriter().println("<input name='access_token' type='hidden' value='" + token + "'>");
			//resp.getWriter().println("<input name='resource_owner_id' type='hidden' value='" + resourceOwnerId + "'>");
			resp.getWriter().println("<input type='submit' value='Import taxes' class='btn btn-primary'>");
			resp.getWriter().println("</form>");
			resp.getWriter().println("</div>");
			resp.getWriter().println("<div role='tabpanel' class='tab-pane fade in active' id='get'>");
			resp.getWriter().println("<form action='/e-Doc360-SG2/SageoneData4' method='get'>");
			resp.getWriter().println("<input name='request_method' type='hidden' value='get'>");
			resp.getWriter().println("<input name='endpoint' type='hidden' value='contacts?contact_type_id=VENDOR'>");
			resp.getWriter().println("<input name='data' type='hidden' value=''>");
			resp.getWriter().println("<input name='access_token' type='hidden' value='" + token + "'>");
			//resp.getWriter().println("<input name='resource_owner_id' type='hidden' value='" + resourceOwnerId + "'>");
			resp.getWriter().println("<input type='submit' value='Import vendors' class='btn btn-primary'>");
			resp.getWriter().println("</form>");
			resp.getWriter().println("</div>");
			resp.getWriter().println("<div role='tabpanel' class='tab-pane fade in active' id='get'>");
			resp.getWriter().println("<form action='/e-Doc360-SG2/SageoneData3' method='get'>");
			resp.getWriter().println("<input name='request_method' type='hidden' value='get'>");
			resp.getWriter().println("<input name='endpoint' type='hidden' value='ledger_accounts'>");
			resp.getWriter().println("<input name='data' type='hidden' value=''>");
			resp.getWriter().println("<input name='access_token' type='hidden' value='" + token + "'>");
			//resp.getWriter().println("<input name='resource_owner_id' type='hidden' value='" + resourceOwnerId + "'>");
			resp.getWriter().println("<input type='submit' value='Import compte comptable' class='btn btn-primary'>");
			resp.getWriter().println("</form>");
			resp.getWriter().println("</div>");*/
			resp.getWriter().println("<div role='tabpanel' class='tab-pane fade in active' id='get'>");
			resp.getWriter().println("<form action='/e-Doc360-SG2/SageoneData5' method='post'>");
			resp.getWriter().println("<input name='request_method' type='hidden' value='post'>");
			resp.getWriter().println("<input name='endpoint' type='hidden' value='purchase_invoices'>");
			resp.getWriter().println("<input name='data' type='hidden' value=''>");
			resp.getWriter().println("<input name='access_token' type='hidden' value='" + token + "'>");
			//resp.getWriter().println("<input name='resource_owner_id' type='hidden' value='" + resourceOwnerId + "'>");
			resp.getWriter().println("<input type='submit' value='Export facture' class='btn btn-primary'>");
			resp.getWriter().println("</form>");
			resp.getWriter().println("</div>");
//			resp.getWriter().println("<div role='tabpanel' class='tab-pane fade' id='post'>");
//			resp.getWriter().println("<h3>POST request</h3>");
//			resp.getWriter().println("<form action='/e-Doc360-SG2/sageone_data' method='post'>");
//			resp.getWriter().println("<input name='request_method' type='hidden' value='post'>");
//			resp.getWriter().println("<label for='endpoint'>Endpoint</label>");
//			resp.getWriter().println("<input name='endpoint' type='text' class='form-control' required='true'>");
//			resp.getWriter().println("<p>Example: accounts/v3/contacts</p>");
//			resp.getWriter().println("<label for='data'>Post data</label>");
//			resp.getWriter().println("<textarea id='data' class='form-control' name='data'></textarea>");
//			resp.getWriter().println("<p>Example: {\"contact\": {\"contact_type_ids\":[\"CUSTOMER\"], \"name\": \"Joe Bloggs\"}}</p>");
//			resp.getWriter().println("<input name='access_token' type='hidden' value='" + token + "'>");
//			resp.getWriter().println("<input name='resource_owner_id' type='hidden' value='" + resourceOwnerId + "'>");
//			resp.getWriter().println("<input type='submit' value='POST' class='btn btn-primary'>");
//			resp.getWriter().println("</form>");
//			resp.getWriter().println("</div>");
//			resp.getWriter().println("<div role='tabpanel' class='tab-pane fade' id='put'>");
//			resp.getWriter().println("<h3>PUT request</h3>");
//			resp.getWriter().println("<form action='/e-Doc360-SG2/sageone_data' method='post'>");
//			resp.getWriter().println("<input name='request_method' type='hidden' value='put'>");
//			resp.getWriter().println("<label for='endpoint'>Endpoint</label>");
//			resp.getWriter().println("<input name='endpoint' type='text' class='form-control' required='true'>");
//			resp.getWriter().println("<p>Example: accounts/v3/contacts/:id</p>");
//			resp.getWriter().println("<label for='data'>Post data</label>");
//			resp.getWriter().println("<textarea id='data' class='form-control' name='data'></textarea>");
//			resp.getWriter().println("<p>Example: {\"contact\": {\"contact_type_ids\":[\"CUSTOMER\"], \"name\": \"Joe Bloggs\"}}</p>");
//			resp.getWriter().println("<input name='access_token' type='hidden' value='" + token + "'>");
//			resp.getWriter().println("<input name='resource_owner_id' type='hidden' value='" + resourceOwnerId + "'>");
//			resp.getWriter().println("<input type='submit' value='PUT' class='btn btn-primary'>");
//			resp.getWriter().println("</form>");
//			resp.getWriter().println("</div>");
//			resp.getWriter().println("<div role='tabpanel' class='tab-pane fade' id='delete'>");
//			resp.getWriter().println("<h3>DELETE request</h3>");
//			resp.getWriter().println("<form action='/e-Doc360-SG2/sageone_data' method='get'>");
//			resp.getWriter().println("<input name='request_method' type='hidden' value='delete'>");
//			resp.getWriter().println("Endpoint: <input name='endpoint' type='text' class='form-control' required='true'>");
//			resp.getWriter().println("<p>Example: accounts/v3/contacts/:id</p>");
//			resp.getWriter().println("<input name='data' type='hidden' value=''>");
//			resp.getWriter().println("<input name='access_token' type='hidden' value='" + token + "'>");
//			resp.getWriter().println("<input name='resource_owner_id' type='hidden' value='" + resourceOwnerId + "'>");
//			resp.getWriter().println("<input type='submit' value='DELETE' class='btn btn-primary'>");
//			resp.getWriter().println("</form>");
//			resp.getWriter().println("</div>");
		/*	resp.getWriter().println("</div>");
			resp.getWriter().println("</div>");
			resp.getWriter().println("</div>");
			resp.getWriter().println("</body>");
			resp.getWriter().println("</html>");
	*/	} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
