package org.nuxeo.project.sample.rest;

import java.security.KeyManagementException;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.project.sample.beans.GlobaluserData;
import org.nuxeo.project.sample.beans.Invoice;
import org.nuxeo.project.sample.beans.Message;
import org.nuxeo.project.sample.beans.Message2;
import org.nuxeo.project.sample.beans.MessageErreur;
import org.nuxeo.project.sample.beans.NuxeoOAuth2Token;
import org.nuxeo.project.sample.beans.Sql;
import org.nuxeo.project.sample.beans.User;
import org.nuxeo.project.sample.beans.UserData;
import org.nuxeo.project.sample.complementary.Constant;
import org.nuxeo.project.sample.services.MethodsShared;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.star.security.CertificateException;
import org.nuxeo.ecm.directory.PasswordHelper;
import org.nuxeo.ecm.platform.web.common.vh.VirtualHostHelper;

import java.util.Base64;
import java.util.Collections;

@Path("/auth")
public class LoginOAuth2Resource {
 
	private String accessToken;
	private static Log log = LogFactory.getLog(LoginOAuth2Resource.class);
	private MethodsShared ms = new MethodsShared();
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static ResourceBundle bundle = ResourceBundle.getBundle("Config");
	private HttpSession session;
	
	
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response auth(User user, @Context HttpServletRequest request) throws KeyManagementException, NoSuchAlgorithmException, JSONException {
		log.error(Constant.INFO_DASHES);
		log.error(" =============== LOGIN =============== ");
		Client client = createClient(User.class);
		String username = user.getUsername();
		String password = user.getPassword();
		String base = VirtualHostHelper.getBaseURL(request);
		//log.error(" === < username : " + username + ", password : " + password + " > ===");
		String auth = username + ":" + password;
//		byte[] encodedAuth = Base64.encodeBase64(
//		  auth.getBytes());
		String encodedAuth=Base64.getEncoder().encodeToString((username+":"+password).getBytes());
		String authHeader = "Basic " + new String(encodedAuth);
		///String name = "username";
//		//String password = "secret";
		//String authString = name + ":" + password;
		client.setFollowRedirects(false);
		WebResource webResource = client.resource(bundle.getString("BaseUrl") +"/oauth2/authorization?response_type=code&client_id=restapi&redirect_uri="+client.resource(bundle.getString("BaseUrl"))+"/login.jsp&client_secret=mobileApp123");
		Builder builder = webResource.header("Authorization",authHeader).accept(MediaType.APPLICATION_JSON);
		ClientResponse response = builder.get(ClientResponse.class);
		//log.error(" === < Request url find code : " + response.getLocation().getAuthority() + " > ===");
		//log.error(" === < Status find user : " + response.getStatus() + " > ===");
		//$codee=($response->transferStats->handlerStats['url']);
		//$code=strstr($codee,"code=",false);
		//$first = substr($code, 5);
//		   if (response.getLinkHeader() == null) {
//		    throw new RuntimeException("Could not find create-with-id URL");
//		   }
//		   Link link = response.getLinkHeader().getLinkByTitle("create-with-id");
//		   if (link == null) {
//		    throw new RuntimeException("Could not find create-with-id URL");
//		   }
//		   url = link.getHref();
//		 }
//		 targetUri = ResteasyUriBuilder.fromTemplate(url);
//		

		 //MultivaluedMap<String, String> jerseyHeaders = response.getHeaders();
		 	
//		     for (Entry<String, List<String>> entry : jerseyHeaders.entrySet()) {
//		      if (!entry.getValue().isEmpty()) {
//		    	  System.out.println("get key"+entry.getKey());
//		    	  System.out.println("get value"+entry.getValue().get(0));
//		    	  for (int i = 0; i < entry.getValue().size(); i++) {
//		    		  
//		    		  System.out.println("get value "+i+" "+entry.getValue().get(i));
//				    	
//				}
//				    
//		      }
//		    }
		    	    
		if(response.getStatus()==302)
		{
			  String code = response.getHeaders().getFirst("Location").split("code=")[1];
			WebResource webResourceToken = client.resource(bundle.getString("BaseUrl") +"/oauth2/token?client_id=restapi&client_secret=mobileApp123&grant_type=authorization_code&redirect_uri="+client.resource(bundle.getString("BaseUrl"))+"/login.jsp&code="+code);
			Builder builder2 = webResourceToken.accept(MediaType.APPLICATION_JSON)
					.header("content-type", MediaType.APPLICATION_JSON);
			ClientResponse response2 = builder2.get(ClientResponse.class);
			log.error(" === < Status find user1 : " + response2 + " > ===");
			
			
		     if(response2.getStatus()==200)
		     {
		    	 String output=response2.getEntity(String.class);
		    	 JSONObject JsonObj = new JSONObject(output);
		    	 log.error(" === < Status find user2 : " + output + " > ===");
					
					
		    	 if (JsonObj.has("access_token")) 
		    	    {   log.error(" === < Status Entree: ===");
					
		    		    String get_access = JsonObj .getString("access_token");
		    		    this.session = request.getSession(true);
		    		    log.error(" === < Status session: " + this.session.getId() +" > ===");
						
		    	        //session.setAttribute("accessToken",get_access);
		    		    Constant.access_token=get_access;
		    	        log.error(" === < Status find user3 : " + this.session + " > ===");
						
		    		   // String get_username = JsonObj .getString("username");
		    		    //log.error(" === < Status NuxeoLogin : " + output + " > ===");
						User userDB = ms.findUserByUsername(username);
			    		GenericEntity<GlobaluserData> genericUser = new GenericEntity<GlobaluserData>(new GlobaluserData(new UserData(userDB.getEmail(),userDB.getFirstName(),userDB.getLastName(),userDB.getUsername(),get_access))){};
			
					log.error(" =============== Authentication success =============== ");
					return Response.status(200).entity(genericUser).build();		    	// GenericEntity<NuxeoOAuth2Token> genericToken = new GenericEntity<NuxeoOAuth2Token>(new NuxeoOAuth2Token()){};
			
		    		}
		    		else {
		    			GenericEntity<Message2> generic = new GenericEntity<Message2>(new Message2(new MessageErreur())){};
		    		 	log.error(" =============== Authentication failed3 =============== ");
		    			return Response.status(200).entity(generic).build();

		    		}
								
		     }
		 	GenericEntity<Message2> generic = new GenericEntity<Message2>(new Message2(new MessageErreur())){};
		 	log.error(" =============== Authentication failed1 =============== ");
			return Response.status(200).entity(generic).build();

		}
		GenericEntity<Message2> generic = new GenericEntity<Message2>(new Message2(new MessageErreur())){};
	 	log.error(" =============== Authentication failed2 =============== ");
		return Response.status(200).entity(generic).build();
	
		
	}
	
	@POST
	@Path("/sql")
	@Produces(MediaType.APPLICATION_JSON)
	public Response auth(Sql sql, @Context HttpServletRequest request) {
		log.error(Constant.INFO_DASHES);
		log.error(" =============== SQL =============== ");
		String table = sql.getTable();
		String column = sql.getColumn();
		String type = sql.getType();
		log.error(" === < table : " + table + " > ===");
		Sql sql1 = ms.executQuery(table, column, type);
		if(sql1 != null) {
			
				GenericEntity<Sql> generic = new GenericEntity<Sql>(sql1){};
				log.error(" =============== EXECUTE SUCCESS =============== ");
				return Response.status(200).entity(generic).build();
			}
		else {
			GenericEntity<Message> generic = new GenericEntity<Message>(new Message("Execute failed", "", (short) 404, format.format(new Date()))){};
			log.error(" =============== EXECUTE failed =============== ");
			return Response.status(404).entity(generic).build();
		}
		
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