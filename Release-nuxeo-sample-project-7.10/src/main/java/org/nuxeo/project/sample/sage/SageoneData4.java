package org.nuxeo.project.sample.sage;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.project.sample.beans.LedgerAccounts;
import org.nuxeo.project.sample.beans.Taxe;
import org.nuxeo.project.sample.beans.Vendor;
import org.nuxeo.project.sample.complementary.Constant;
import org.nuxeo.project.sample.services.MethodsShared;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.star.security.CertificateException;

/**
 * Servlet implementation class GetData
 */
@WebServlet("/SageoneData4")
public class SageoneData4 extends HttpServlet {
	private static Log log = LogFactory.getLog(SageoneData4.class);
	private static final long serialVersionUID = 1L;
	//private static ResourceBundle bundle = ResourceBundle.getBundle("ConfigSage");
	public MethodsShared MethodShared;
	
	/* GET and DELETE requests */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MethodShared = new MethodsShared();
		String requestMethod = req.getParameter("request_method").toUpperCase();
		String endpoint = MethodShared.Config().getUsurl() + req.getParameter("endpoint");
		String params;
		String nonce = Nonce.generateNonce();
		String signingSecret = MethodShared.Config().getSigningClient();
		String accessToken = req.getParameter("access_token");
		String resourceOwnerId =null;
		String signature = null;
		try {
			URIBuilder builder = new URIBuilder(endpoint);
			URI uri = builder.build();
			HttpRequestBase request = requestMethod.equals("GET") ? new HttpGet(uri) : new HttpDelete(uri);
			setRequestHeaders(nonce, accessToken, resourceOwnerId, signature, request);
			// Make Request
			HttpClient httpclient = HttpClients.createDefault();
			HttpResponse response = httpclient.execute(request);
			renderResponse(resp, response,req);
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

			renderResponse(resp, response,req);
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
		request.addHeader("Accept", "*/*");
		request.addHeader("Content-Type", "application/json");
		//request.addHeader("User-Agent", "SageOneSampleApp");
		//request.addHeader("X-Site", resourceOwnerId);
		//request.addHeader("ocp-apim-subscription-key", MethodShared.Config().getKey());
	}

	/* render the response */
	private void renderResponse(HttpServletResponse resp, HttpResponse response, HttpServletRequest req) throws IOException, JSONException, KeyManagementException, NoSuchAlgorithmException {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			StringBuilder parsedEntity = new StringBuilder(EntityUtils.toString(entity));
			String ent = parsedEntity.toString();
			if(ent.startsWith("[")) 
			{
				parsedEntity.deleteCharAt(0);
				parsedEntity.deleteCharAt(parsedEntity.length() -1);
			}
			String parsed = parsedEntity.toString();
			List<String> list = new ArrayList<String>();
			JSONObject jsonResponse = new JSONObject(parsed);
			JSONArray  jsonArrayResponse = jsonResponse.getJSONArray("$items");
			Map<String, Object> mapList=toMap(jsonResponse);
			for (int i = 0; i < jsonArrayResponse.length(); i++) {
	            JSONObject objects = jsonArrayResponse.getJSONObject(i);
	            Iterator<String> key = objects.keys();
	            while (key.hasNext()) 
	                {
	                  String k = key.next().toString();
	                  list.add(objects.getString(k));
	                }
	            }
			JSONObject obj = new JSONObject(mapList);
			 List<Vendor> vendorNews = new ArrayList<Vendor>();
			 JSONArray arr = jsonResponse.getJSONArray("$items");
			  for (int i = 0; i < arr.length(); i++) {
			    	 Vendor vendor=new Vendor();
			    	 Iterator iter = arr.getJSONObject(i).keys();
					 while(iter.hasNext()){
					   String key = (String)iter.next();
					   if(key.equals("id"))
					  {
					   String value = arr.getJSONObject(i).getString(key);
					   vendor.setId(value);
					   vendor.setPath("contacts/"+value);
					  }

					 }
					 vendorNews.add(vendor);
			          System.out.println("la vendor "+vendor);
			    }
			  
			  
			  List<Vendor> vendorList = new ArrayList<Vendor>();
		        for(int k=0; k<vendorNews.size(); k++) {
		        	Vendor vendor=new Vendor();
				String requestMethod = req.getParameter("request_method").toUpperCase();
				String endpoint = MethodShared.Config().getUsurl() + vendorNews.get(k).getPath();
				String params;
				String nonce = null;
				String signingSecret = MethodShared.Config().getSigningClient();
				String accessToken = req.getParameter("access_token");
				String resourceOwnerId = null;
				params = req.getParameter("data");
				String signature = null;
				URIBuilder builder;
				try {
					builder = new URIBuilder(endpoint);
					URI uri = builder.build();
					HttpRequestBase request = requestMethod.equals("GET") ? new HttpGet(uri) : new HttpDelete(uri);
					setRequestHeaders(nonce, accessToken, resourceOwnerId, signature, request);
					// Make Request
					HttpClient httpclient = HttpClients.createDefault();
					HttpResponse response1 = httpclient.execute(request);
					HttpEntity entity1 = response1.getEntity();
					if (entity1 != null) {
						StringBuilder parsedEntity1 = new StringBuilder(EntityUtils.toString(entity1));
						String ent1 = parsedEntity1.toString();
						if(ent1.startsWith("[")) {
							parsedEntity1.deleteCharAt(0);
							parsedEntity1.deleteCharAt(parsedEntity.length() -1);
						}
						String parsed1 = parsedEntity1.toString();
						JSONObject jsonResponse1 = new JSONObject(parsed1);
					    if(jsonResponse1 instanceof JSONObject)
					    {
					    String type="";	
//						Iterator<String> key22 = jsonResponse1.keys();
//						while (key22.hasNext()) 
//						{
//							 String k22 = key22.next().toString();
//							 if(k22.equals("contact_types")) 
//							 {
//								
//				                	String Data=jsonResponse1.getString(k22); // reading the string value 
//				                	if(Data!=null)
//				                	{
//				                	//JSONObject json = (JSONObject) new JSONParser().parse(Data);
//				                	//JSONObject convertedObject = new Gson().fromJson(Data, JSONObject.class);
//				                    System.out.println("le type est "+Data.substring(1,Data.length()-1));
//				                	JSONObject convertedObject = new JSONObject(Data.substring(1,Data.length()-1));
//				                	type=(String) convertedObject.getString("id");
//				                	}
//				                	
//				                }
//				    		
//				    	}
//						if(type.equals("VENDOR"))
//						{
					    Iterator<String> key2 = jsonResponse1.keys(); 
					  while (key2.hasNext()) {
						  String k2 = key2.next().toString();
							
						     if(k2.equals("name")) {
			                	vendor.setName(jsonResponse1.getString(k2));
			                }
			                else if(k2.equals("updated_at")) 
			                {
			                	String aTime = jsonResponse1.getString(k2);
			                	vendor.setUpdated_at(aTime);  
			                }
			                else if(k2.equals("id")) 
			                {
			                	vendor.setId(jsonResponse1.getString(k2));  
			                }
						
					}
					    //}
					}
						vendorList.add(vendor);
						System.out.println("taxe  new   "+vendor.toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
		        }
			for(int q=0; q<vendorList.size(); q++) 
			{
			  System.out.println("resultat de création "+createVendor(vendorList.get(q)));
			} 
			  
			  
			  
			  
			  
			  
			  
			  
			  
			  
			  
			  
			  
			  
			  
			//Gson gson = new GsonBuilder().setPrettyPrinting().create();
			//String prettyJson = gson.toJson(jsonResponse);
//			resp.setContentType("text/html") ;
//			resp.getWriter().println("<html>");
//			resp.getWriter().println("<head>");
//			resp.getWriter().println("<link type=\"text/css\" rel=\"stylesheet\" href=\"../sample_app.css\">");
//			resp.getWriter().println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>");
//			resp.getWriter().println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\" integrity=\"sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7\" crossorigin=\"anonymous\">");
//			resp.getWriter().println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css\" integrity=\"sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r\" crossorigin=\"anonymous\">");
//			resp.getWriter().println("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\" integrity=\"sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS\" crossorigin=\"anonymous\"></script>");
//			resp.getWriter().println("<title>Response</title>");
//			resp.getWriter().println("</head>");
//			resp.getWriter().println("<body>");
//			resp.getWriter().println("<header class=\"navbar navbar-fixed-top navbar-inverse\">");
//			resp.getWriter().println("<div class='container'>");
//			resp.getWriter().println("<a id=\"logo\" href=\"/e-Doc360-SG2\">e-Doc360-SG2</a>");
//			resp.getWriter().println("</div>");
//			resp.getWriter().println("</header>");
//			resp.getWriter().println("<div class='container'>");
//			resp.getWriter().println("<h1>Sage One Data</h1>");
//			resp.getWriter().println("<pre>" + prettyJson + "</pre>");
//			resp.getWriter().println("</div>");
//			resp.getWriter().println("</body>");
//			resp.getWriter().println("</html>");
		} else
		{
			System.out.println("Failure");
		}
	}
	public static Map<String, Object> toMap(JSONObject object) throws JSONException {
	    Map<String, Object> map = new HashMap<String, Object>();
	 
	    Iterator<String> keysItr = object.keys();
	    while(keysItr.hasNext()) {
	        String key = keysItr.next();
	        Object value = object.get(key);
	 
	        if(value instanceof JSONArray) {
	            value = toList((JSONArray) value);
	        }
	 
	        else if(value instanceof JSONObject) {
	            value = toMap((JSONObject) value);
	        }
	        map.put(key, value);
	    }
	    return map;
	}
	public static List<Object> toList(JSONArray array) throws JSONException {
	    List<Object> list = new ArrayList<Object>();
	    for(int i = 0; i < array.length(); i++) {
	        Object value = array.get(i);
	        if(value instanceof JSONArray) {
	            value = toList((JSONArray) value);
	        }
	 
	        else if(value instanceof JSONObject) {
	            value = toMap((JSONObject) value);
	        }
	        list.add(value);
	    }
	    return list;
	}
	 public Client createVendor(Vendor vendor) throws KeyManagementException, NoSuchAlgorithmException, ClientProtocolException, IOException, JSONException
	 {			MethodShared = new MethodsShared();
		 		DocumentModel doc = null;
		     	String PARENT_PATH = "/default-domain/workspaces/Entreprise X/Dossier Compte";
			 	Gson gson = new Gson();
			 	String token = Constant.access_token;	
			 	String callbackUrl = MethodShared.Config().getUrl();	
				Client client = createClient(Taxe.class);
				String[] chaine=callbackUrl.split("/");
	   			String nouveauchaine=chaine[0]+"/"+chaine[1]+"/"+chaine[2]+"/"+chaine[3];
				WebResource webResource = client.resource(nouveauchaine+ Constant.PATH_START_VENDOR + "/createVendor");
				Builder builder = webResource
						.accept(MediaType.APPLICATION_JSON)
						.header("content-type", MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + token);
				ClientResponse response = builder.put(ClientResponse.class, gson.toJson(vendor));
				log.error(" === < Request json object  create taxe : " + gson.toJson(vendor) + " > ===");
				log.error(" === < Status create vendor : " + response.getStatus() + " > ===");
				log.error(" === < Status create vendor : " + response.toString() + " > ===");
				if(response.getStatus() == 200){
					return client;
				}
				else if(response.getStatus()==500){
						
					return null;
					
				}
				return null;	
			
			
			
	}
	 
	 public Client createClient(Class c) throws KeyManagementException, NoSuchAlgorithmException {
		 ClientConfig clientConfig = new DefaultClientConfig();
		 clientConfig.getProperties()
	     .put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
	             new HTTPSProperties(
	                     SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER,
	                     SSLUtil.getInsecureSSLContext()));
			clientConfig.getClasses().add(c);
			clientConfig.getClasses().add(JacksonJsonProvider.class);
			Client client = Client.create(clientConfig);
			return client;
	 }
	 private static class SSLUtil {
	     protected static SSLContext getInsecureSSLContext()
	             throws KeyManagementException, NoSuchAlgorithmException {
	         final TrustManager[] trustAllCerts = new TrustManager[]{
	                 new X509TrustManager() {
	                     public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                         return null;
	                     }

	                     public void checkClientTrusted(
	                             final java.security.cert.X509Certificate[] arg0, final String arg1)
	                             throws CertificateException {
	                         // do nothing and blindly accept the certificate
	                     }

	                     public void checkServerTrusted(
	                             final java.security.cert.X509Certificate[] arg0, final String arg1)
	                             throws CertificateException {
	                         // do nothing and blindly accept the server
	                     }

	                 }
	         };

	         final SSLContext sslcontext = SSLContext.getInstance("SSL");
	         sslcontext.init(null, trustAllCerts,
	                 new java.security.SecureRandom());
	         return sslcontext;
	     }
	 }
}
