package org.nuxeo.project.sample.sage;

import java.io.IOException;
import java.net.URI;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.project.sample.services.MethodsShared;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class GetData
 */
@WebServlet("/SageoneData")
public class SageoneData extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	//private static ResourceBundle bundle = ResourceBundle.getBundle("ConfigSage");
	public MethodsShared MethodShared;
	
	/* GET and DELETE requests */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MethodShared = new MethodsShared();
		String requestMethod = req.getParameter("request_method").toUpperCase();
		String endpoint = MethodShared.Config().getUsurl() + req.getParameter("endpoint");
		System.out.println("endpoint   "+endpoint);
		System.out.println("request_method   "+requestMethod);
		
		String params;
		String nonce = Nonce.generateNonce();
		String signingSecret = MethodShared.Config().getSigningClient();
		String accessToken = req.getParameter("access_token");
		String resourceOwnerId =null; //req.getParameter("resource_owner_id");
		System.out.println("access_token   "+accessToken);
		
		//set the body params as an empty string for GET requests
		params = new String();

		// Generate the signature
		//SageoneSigner s = new SageoneSigner(requestMethod, endpoint, params, nonce, signingSecret, accessToken, resourceOwnerId);
		String signature = null;

		try {
			URIBuilder builder = new URIBuilder(endpoint);
			URI uri = builder.build();
			HttpRequestBase request = requestMethod.equals("GET") ? new HttpGet(uri) : new HttpDelete(uri);
			setRequestHeaders(nonce, accessToken, resourceOwnerId, signature, request);
			System.out.println("Request  "+request.getAllHeaders());;
			System.out.println("uri  "+uri.getPath());;

			
			// Make Request
			HttpClient httpclient = HttpClients.createDefault();
			HttpResponse response = httpclient.execute(request);

			renderResponse(resp, response);
			request.releaseConnection();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	/* POST and PUT requests */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MethodShared = new MethodsShared();
		String requestMethod = req.getParameter("request_method").toUpperCase();
		String endpoint = MethodShared.Config().getUsurl() + req.getParameter("endpoint");
		System.out.println("endpoint   "+endpoint);
		String params;
		String nonce = Nonce.generateNonce();
		String signingSecret = MethodShared.Config().getSigningClient();
		String accessToken = req.getParameter("access_token");
		String resourceOwnerId = req.getParameter("resource_owner_id");

		// get the body params as a JSON string
		params = req.getParameter("data");

		// Generate the signature
		SageoneSigner s = new SageoneSigner(requestMethod, endpoint, params, nonce, signingSecret, accessToken, resourceOwnerId);
		String signature = s.signature();

		try {
			URIBuilder builder = new URIBuilder(endpoint);
			URI uri = builder.build();

			StringEntity entity = new StringEntity(params);
			HttpRequestBase request;

			if (requestMethod.equals("POST")) {
				request = new HttpPost(uri);
				((HttpPost) request).setEntity(entity);
			}
			else
			{
				request = new HttpPut(uri);
				((HttpPut) request).setEntity(entity);
			}

			setRequestHeaders(nonce, accessToken, resourceOwnerId, signature, request);

			// Make Request
			HttpClient httpclient = HttpClients.createDefault();
			HttpResponse response = httpclient.execute(request);

			renderResponse(resp, response);
			request.releaseConnection();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	/* set the request headers */
	private void setRequestHeaders(String nonce, String accessToken, String resourceOwnerId, String signature, HttpRequestBase request) {
		//request.addHeader("X-Signature", signature);
		//request.addHeader("X-Nonce", nonce);
		request.addHeader("Authorization", "Bearer " + accessToken);
		request.addHeader("Accept", "application/json");
		request.addHeader("Content-Type", "application/json");
		//request.addHeader("User-Agent", "SageOneSampleApp");
		//request.addHeader("X-Site", resourceOwnerId);
		//request.addHeader("ocp-apim-subscription-key", MethodShared.Config().getKey());
	
	}

	/* render the response */
	private void renderResponse(HttpServletResponse resp, HttpResponse response) throws IOException, JSONException {
		// get the response into pretty json for output

		HttpEntity entity = response.getEntity();
		if (entity != null) {

			StringBuilder parsedEntity = new StringBuilder(EntityUtils.toString(entity));
			String ent = parsedEntity.toString();

			if(ent.startsWith("[")) {
				parsedEntity.deleteCharAt(0);
				parsedEntity.deleteCharAt(parsedEntity.length() -1);
			}

			String parsed = parsedEntity.toString();

			JSONObject jsonResponse = new JSONObject(parsed);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String prettyJson = gson.toJson(jsonResponse);
			resp.setContentType("text/html") ;
			resp.getWriter().println("<html>");
			resp.getWriter().println("<head>");
			resp.getWriter().println("<link type=\"text/css\" rel=\"stylesheet\" href=\"../sample_app.css\">");
			resp.getWriter().println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>");
			resp.getWriter().println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\" integrity=\"sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7\" crossorigin=\"anonymous\">");
			resp.getWriter().println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css\" integrity=\"sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r\" crossorigin=\"anonymous\">");
			resp.getWriter().println("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\" integrity=\"sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS\" crossorigin=\"anonymous\"></script>");
			resp.getWriter().println("<title>Response</title>");
			resp.getWriter().println("</head>");
			resp.getWriter().println("<body>");
			resp.getWriter().println("<header class=\"navbar navbar-fixed-top navbar-inverse\">");
			resp.getWriter().println("<div class='container'>");
			resp.getWriter().println("<a id=\"logo\" href=\"/e-Doc360-SG2\">e-Doc360-SG2</a>");
			resp.getWriter().println("</div>");
			resp.getWriter().println("</header>");
			resp.getWriter().println("<div class='container'>");
			resp.getWriter().println("<h1>Sage One Data</h1>");
			resp.getWriter().println("<pre>" + prettyJson + "</pre>");
			resp.getWriter().println("</div>");
			resp.getWriter().println("</body>");
			resp.getWriter().println("</html>");
		} else
		{
			System.out.println("Failure");
		}
	}
}
