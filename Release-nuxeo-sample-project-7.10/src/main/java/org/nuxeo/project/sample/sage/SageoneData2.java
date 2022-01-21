package org.nuxeo.project.sample.sage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import javax.faces.context.FacesContext;
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
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.jboss.seam.annotations.In;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.project.sample.beans.Product;
import org.nuxeo.project.sample.beans.Taxe;
import org.nuxeo.project.sample.beans.User;
import org.nuxeo.project.sample.complementary.Constant;
import org.nuxeo.project.sample.rest.ProductOAuth2Resource;
import org.nuxeo.project.sample.services.MethodsShared;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.icu.text.SimpleDateFormat;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.star.security.CertificateException;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
/**
 * Servlet implementation class GetData
 */
@WebServlet("/SageoneData2")
public class SageoneData2 extends HttpServlet {
	
	private static Log log = LogFactory.getLog(SageoneData2.class);
	private static final long serialVersionUID = 1L;
	//private static ResourceBundle bundle = ResourceBundle.getBundle("ConfigSage");
	public MethodsShared MethodShared;
    protected CoreSession session;
	private String currentUser;
	
	/* GET and DELETE requests */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MethodShared = new MethodsShared();
		String requestMethod = req.getParameter("request_method").toUpperCase();
		String endpoint = MethodShared.Config().getUsurl() + req.getParameter("endpoint");
		String params;
		String nonce = Nonce.generateNonce();
		String signingSecret = MethodShared.Config().getSigningClient();
		String accessToken = req.getParameter("access_token");
		String resourceOwnerId = null;
		params = new String();
		String signature = null;
		String address_region_id = MethodShared.Config().getAddress_region_id();
		System.out.println("address_region_id  : " + address_region_id);
		
		try {
			String address_region_ide="?address_region_id="+address_region_id;
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<"+endpoint+address_region_ide+"<<<<<<<<<<<<<<<<<<<<<<<<<<");
			URIBuilder builder = new URIBuilder(endpoint+address_region_ide);
			URI uri = builder.build();
			HttpRequestBase request = requestMethod.equals("GET") ? new HttpGet(uri) : new HttpDelete(uri);
			setRequestHeaders(nonce, accessToken, resourceOwnerId, signature, request);
			HttpClient httpclient = HttpClients.createDefault();
			HttpResponse response = httpclient.execute(request);
			renderResponse(resp, response, req);
			request.releaseConnection();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	 public Client createTaxe(Taxe taxe) throws KeyManagementException, NoSuchAlgorithmException, ClientProtocolException, IOException, JSONException
	 {          MethodShared = new MethodsShared();
		 		DocumentModel doc = null;
		     	String PARENT_PATH = "/default-domain/workspaces/Entreprise X/Dossier TAXE";
			 	Gson gson = new Gson();
			 	String token = Constant.access_token;	
			 	
			 	
			 	
			 	///
				Client client = createClient(Taxe.class);
				String callbackUrl = MethodShared.Config().getUrl();
				String[] chaine=callbackUrl.split("/");
	   			String nouveauchaine=chaine[0]+"/"+chaine[1]+"/"+chaine[2]+"/"+chaine[3];
				WebResource webResource = client.resource(nouveauchaine+ Constant.PATH_START_TAXE + "/createTaxe");
				Builder builder = webResource
						.accept(MediaType.APPLICATION_JSON)
						.header("content-type", MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + token);
				ClientResponse response = builder.put(ClientResponse.class, gson.toJson(taxe));
				log.error(" === < Request json object  create taxe : " + gson.toJson(taxe) + " > ===");
				log.error(" === < Status create taxe : " + response.getStatus() + " > ===");
				log.error(" === < Status create taxe 2 : " + response.toString() + " > ===");
				if(response.getStatus() == 200){
					return null;
				}
				else if(response.getStatus()==500){
						
					return client;
					
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

			renderResponse(resp, response, req);
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
	private void renderResponseV2(HttpServletResponse resp, HttpResponse response, HttpServletRequest req) throws IOException, JSONException, KeyManagementException, NoSuchAlgorithmException {
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
           System.out.println("  donnees "+parsed);
			List<String> list = new ArrayList<String>();
			
			JSONObject jsonResponse = new JSONObject(parsed);
			 System.out.println("  donnees "+jsonResponse);
				
			JSONArray  jsonArrayResponse = jsonResponse.getJSONArray("$items");
			Map<String, Object> mapList=toMap(jsonResponse);
			for (int i = 0; i < jsonArrayResponse.length(); i++) {
	            JSONObject objects = jsonArrayResponse.getJSONObject(i);
	            Iterator<String> key = objects.keys();
	            while (key.hasNext()) {
	                String k = key.next().toString();
	               // JSONObject objects1 =  objects.getJSONObject("myHashMap");
	                System.out.println("............");
	                //Iterator<String> key1 = objects1.keys();
	                System.out.println("key1 : " + key);
	                //System.out.println("key1 : " + key1);
//	                while (key1.hasNext()) {
//	                	String k1 = key1.next().toString();
//	                	System.out.println("Key : " + key + ", value : " + objects.getString(key));
//	                } 
	                list.add(objects.getString(k));

	               
	               
	                }
	            }
			 System.out.println("............"+list.toString());
			 System.out.println("............"+mapList.toString());
				
			List<Taxe> taxes = new ArrayList<Taxe>();
			for(int k=0; k<list.size(); k++) {
				Taxe taxe = new Taxe();
			String requestMethod = req.getParameter("request_method").toUpperCase();
			String endpoint = MethodShared.Config().getUsurl() + "tax_rates/"+list.get(k);
			System.out.println("endpoint 1 : " + endpoint);
			String params;
			String nonce = Nonce.generateNonce();
			//String signingSecret = MethodShared.Config().getSigningClient();
			String accessToken = req.getParameter("access_token");
			System.out.println("accessToken 1 : " + accessToken);
			//String resourceOwnerId = req.getParameter("resource_owner_id");   
			//System.out.println("resourceOwnerId 1 : " + resourceOwnerId);
			params = req.getParameter("data");

			// Generate the signature
			//SageoneSigner s = new SageoneSigner(requestMethod, endpoint, params, nonce, signingSecret, accessToken, resourceOwnerId);
			String signature = null;
			URIBuilder builder;
			try {
				builder = new URIBuilder(endpoint);
				URI uri = builder.build();
				HttpRequestBase request = requestMethod.equals("GET") ? new HttpGet(uri) : new HttpDelete(uri);
				setRequestHeaders(nonce, accessToken, null, signature, request);

				// Make Request
				HttpClient httpclient = HttpClients.createDefault();
				HttpResponse response1 = httpclient.execute(request);
				System.out.println("status response 1 : " + response1.getStatusLine());
				HttpEntity entity1 = response1.getEntity();
				if (entity1 != null) {
					System.out.println("1111111111111");
				//convertir json to sting	
					StringBuilder parsedEntity1 = new StringBuilder(EntityUtils.toString(entity1));
					String ent1 = parsedEntity1.toString();
					System.out.println("22222222222");
					if(ent1.startsWith("[")) 
					{
						parsedEntity1.deleteCharAt(0);
						parsedEntity1.deleteCharAt(parsedEntity.length() -1);
					}
					System.out.println("3333333333333");
					String parsed1 = parsedEntity1.toString();
					System.out.println("444444444");
					JSONObject jsonResponse1 = new JSONObject(parsed1).getJSONObject("myHashMap");
					System.out.println("555555555");
					Iterator<String> key2 = jsonResponse1.keys();
					while (key2.hasNext()) {
						System.out.println("666666666666");
		                String k2 = key2.next().toString();
		                System.out.println("k2");
		                System.out.println("7777777777");
		                if(k2.equals("id")) {
			                taxe.setCode(jsonResponse1.getString(k2));
		                }else if(k2.equals("displayed_as")) {
		                	taxe.setName(jsonResponse1.getString(k2));
		                }else if(k2.equals("percentage")) {
		                	taxe.setPercentage(Double.parseDouble(jsonResponse1.getString(k2)));
		                }else if(k2.equals("is_combined_rate")) {
		                	taxe.setCombined(Boolean.parseBoolean(jsonResponse1.getString(k2)));
		                }else if(k2.equals("is_visible")) {
		                	taxe.setVisible(Boolean.parseBoolean(jsonResponse1.getString(k2)));
		                }
					
				}
					taxes.add(taxe);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int q=0; q<taxes.size(); q++) {
				System.out.println("yasssssssssssssss");
				createTaxe(taxes.get(q));
				System.out.println("walllllllllllll");
			}
		}
			
			
			
			
			
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
	/* render the response */
	private void renderResponse(HttpServletResponse resp, HttpResponse response, HttpServletRequest req) throws IOException, JSONException, KeyManagementException, NoSuchAlgorithmException {

		// get the response into pretty json for output

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
            System.out.println("le parsed   "+parsed);
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
			 List<Taxe> taxesNews = new ArrayList<Taxe>();
			 JSONArray arr = jsonResponse.getJSONArray("$items");
			  for (int i = 0; i < arr.length(); i++) {
			    	 Taxe taxe = new Taxe();
			    	 Iterator iter = arr.getJSONObject(i).keys();
					 while(iter.hasNext()){
					   String key = (String)iter.next();
					   if(key.equals("id"))
					  {
					   String value = arr.getJSONObject(i).getString(key);
					   taxe.setCode(value);
			           taxe.setPath("tax_rates/"+value); 
					  }

					 }
					 taxesNews.add(taxe);
			          System.out.println("la taxe "+taxe);
			    }	    
		        List<Taxe> taxesList = new ArrayList<Taxe>();
		        for(int k=0; k<taxesNews.size(); k++) {
					Taxe taxe = new Taxe();
				String requestMethod = req.getParameter("request_method").toUpperCase();
				String endpoint = MethodShared.Config().getUsurl() + taxesNews.get(k).getPath();
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
						Iterator<String> key2 = jsonResponse1.keys();
						while (key2.hasNext()) {
						    String k2 = key2.next().toString();
			                if(k2.equals("agency")) {
				                taxe.setAgency(jsonResponse1.getString(k2));
			                }else if(k2.equals("name"))
			                {
			                	taxe.setName(jsonResponse1.getString(k2));
			                }
			                else if(k2.equals("id")) {
			                	taxe.setCode(jsonResponse1.getString(k2));
			                }else if(k2.equals("percentage")) 
			                {
			                	taxe.setPercentage(Double.parseDouble(jsonResponse1.getString(k2)));
			                }else if(k2.equals("is_combined_rate")) 
			                {
			                	taxe.setCombined(Boolean.parseBoolean(jsonResponse1.getString(k2)));
			                }else if(k2.equals("is_visible")) 
			                {
			                	taxe.setVisible(Boolean.parseBoolean(jsonResponse1.getString(k2)));
			                }
			                else if(k2.equals("updated_at")) 
			                {
			                	String aTime = jsonResponse1.getString(k2);
			                	taxe.setUpdated_at(aTime);  
			                }
						
					}
					}
					    taxesList.add(taxe);
						System.out.println("taxe  new   "+taxe.toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
		        }
			for(int q=0; q<taxesList.size(); q++) 
			{
			  System.out.println("resultat de création "+createTaxe(taxesList.get(q)));
			}

			
			
		} 
	}


}
