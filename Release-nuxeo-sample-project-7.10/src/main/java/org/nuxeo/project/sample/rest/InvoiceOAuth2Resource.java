package org.nuxeo.project.sample.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.eclipse.jdt.internal.core.Assert;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.nuxeo.project.sample.beans.Batch;
import org.nuxeo.project.sample.beans.Fournisseur;
import org.nuxeo.project.sample.beans.GlobalInvoice;
import org.nuxeo.project.sample.beans.Invoice;
import org.nuxeo.project.sample.beans.InvoiceData;
import org.nuxeo.project.sample.beans.Message;
import org.nuxeo.project.sample.beans.Message2;
import org.nuxeo.project.sample.beans.MessageErreur;
import org.nuxeo.project.sample.beans.Messageupdateinvoice;
import org.nuxeo.project.sample.beans.Product;
import org.nuxeo.project.sample.beans.UserData;
import org.nuxeo.project.sample.complementary.Constant;
import org.nuxeo.project.sample.services.DatePojo;
import org.primefaces.push.annotation.PathParam;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.gson.Gson;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.star.security.CertificateException;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;


@Path("/invoice")
public class InvoiceOAuth2Resource {
 
	private String accessToken;
	private static Log log = LogFactory.getLog(Invoice.class);
	private static ResourceBundle bundle = ResourceBundle.getBundle("Config");
	private HttpSession session;

 
 @POST
 @Path("/findInvoice")
 @Produces(MediaType.APPLICATION_JSON)
 public JSONObject findAR(Invoice invoice, @Context HttpServletRequest request) throws KeyManagementException, NoSuchAlgorithmException {
	 log.error(" =============== FIND PRODUCT =============== ");
	 log.error(" === < barre code : " + invoice.getNumFacture() + " > ===");

	 session = request.getSession();
	 log.error(" === < Session id : " + session.getId() + " > ===");
	 //accessToken = session.getAttribute("accessToken").toString();
	 accessToken=Constant.access_token;
	 log.error(" === < Access token : " + accessToken + " > ===");
	  ClientResponse response = find(invoice.getNumFacture());
	  if(response != null) {
		  JSONObject json = response.getEntity(JSONObject.class);
		  log.error(" === < Json : " + json + " > ===");
		  log.error(" =============== FIND PRODUCT SUCCESS =============== ");
		  return json;
	  }
	  log.error(" =============== FIND PRODUCT RETURN NULL =============== ");
	 return null;
	 
 }
 
 @POST
 @Path("/updateInvoice")
 @Produces(MediaType.APPLICATION_JSON)
 public Response updateAR(Invoice invoice, @Context HttpServletRequest request) throws KeyManagementException, NoSuchAlgorithmException, ParseException {
	 log.error(" =============== UPDATE PRODUCT =============== ");
	 log.error(" === < Barre code : " + invoice.getNumFacture() + " > ===");
	 session = request.getSession();
	 log.error(" === < Session id : " + session.getId() + " > ===");
	// accessToken = session.getAttribute("accessToken").toString();
	 accessToken=Constant.access_token;
	 log.error(" === < Access token : " + accessToken + " > ===");
	 System.out.println("Data invoice"+invoice.toString());

	  ClientResponse response = update(invoice);
	  if(response != null) 
	  {   
		  
		  JSONObject json = response.getEntity(JSONObject.class);
		  if(json.containsKey("Invoice"))
          {
		  log.error(" === < Json : " + json + " > ===");
		  log.error(" =============== UPDATE PRODUCT SUCCESS =============== ");
		  
			GenericEntity<Messageupdateinvoice> generic = new GenericEntity<Messageupdateinvoice>(new Messageupdateinvoice(new MessageErreur("Invoice updated"))){};
			log.error(" =============== Authentication SUCCESS =============== ");
			return Response.status(200).entity(generic).build();
          }
		  else 
		  {
			  GenericEntity<InvoiceData> generic = new GenericEntity<InvoiceData>(new InvoiceData()){};
		    	log.error(" =============== Authentication success =============== ");
		  	  	log.error(" =============== UPDATE PRODUCT RETURN NULL =============== ");
				 		return Response.status(200).entity(generic).build();
  
          }
	  }
		GenericEntity<InvoiceData> generic1 = new GenericEntity<InvoiceData>(new InvoiceData()){};
    	log.error(" =============== Authentication success =============== ");
  	  	log.error(" =============== UPDATE PRODUCT RETURN NULL =============== ");
		 		return Response.status(200).entity(generic1).build();
 }

 
 @POST
 @Path("/searchInvoice")
 @Produces(MediaType.APPLICATION_JSON)
 public Response searchAR(Invoice invoice, @Context HttpServletRequest request) throws KeyManagementException, NoSuchAlgorithmException, ParseException, JsonParseException, IOException {
	 log.error(" =============== SEARCH PRODUCT =============== ");
	 log.error(" === < Barre code : " + invoice.getNumFacture()+ " > ===");
	 //mapper.readValue(jsonWithNullString, DatePojo.class); // Throws the exception
	 //Assert.assertNull(jsonWithNullString);
	 session = request.getSession();
	 log.error(" === < Session id : " + session.getId() + " > ===");
	 log.error(" === < Access token : " + session.toString() + " > ===");
	 //accessToken = session.getAttribute("accessToken").toString();
	 accessToken=Constant.access_token;
	 log.error(" === < Access token : " + accessToken + " > ===");
	  ClientResponse response = search(invoice);
	  if(response != null) {
		  JSONObject json = response.getEntity(JSONObject.class);
		  log.error(" === < Json : " + json + " > ===");
		  log.error(" =============== SEARCH PRODUCT SUCCESS =============== ");
		  //return json;
		  Date datee=null;
  		  Date date=null; 
		  String numInvoice="";
		  String num_bc="";
		  Double totalht=0.0;
		  String totalttc="0.0";
		  Double totaltva=0.0;
		  String etatfacture="";
		  String lien="";
		  String devise="";
		  String methodepaiement="";
		  String naturecomptable="";
		  Double taxe1=0.0;
		  Double taxe2=0.0;
		  ArrayList<String> list =new ArrayList<String>();
		  list.add("numFacture");
		  list.add("num_bc");
		  list.add("montantHT");
		  list.add("montantTTC");
		  list.add("totaltva");
		  list.add("etatfacture");
		  list.add("date");
		  list.add("dateexpiration");
		  list.add("lien");
		  list.add("taxe1");
		  list.add("taxe2");
		  list.add("methoddepaiement");
		  list.add("naturecomptable");
		  list.add("devise");
		  list.add("NomFournisseur");
			 
		  
		  
		  if (json instanceof JSONObject) 
		  {
			  
              JSONObject object = (JSONObject) json;
              if(object.containsKey("Invoice"))
              {   //JSONArray jarr = new JSONArray();
    		  	  //.add(object.get("Invoice"));
    		  	 // System.out.println("Invoice  "+json);
    		  	 // JSONObject Obj = jarr.getJSONObject(0);
            	 JSONObject Obj = (JSONObject)object.get("Invoice");
            	  	for (int i = 0; i < list.size(); i++) 
            	  	{
			 
            	  		
                	
            	  	  if (Obj.containsKey(list.get(i)))
            	  	  {
            	  		  if(list.get(i).equals("numFacture"))
            	  		  {
            	  			  numInvoice= Obj.getString("numFacture");
            	  		  }
            	  		  else if(list.get(i).equals("NomFournisseur"))
            	  		  {
	            		  num_bc= Obj.getString("NomFournisseur") ;
            	  		  }
            	  		  else if(list.get(i).equals("montantHT"))
            	  		  {
	            		  totalht= Obj.getDouble("montantHT") ;
            	  		  }
            	  		  else if(list.get(i).equals("montantTTC"))
            	  		  {
	            		  totalttc= Obj.getString("montantTTC") ;
            	  		  }
            	  		  else if(list.get(i).equals("totaltva"))
            	  		  {
	            		  totaltva= Obj.getDouble("totaltva") ;
            	  		  }
            	  		 else if(list.get(i).equals("taxe1"))
           	  		  {
	            		  taxe1= Obj.getDouble("taxe1") ;
           	  		  }
            	  		 else if(list.get(i).equals("taxe2"))
           	  		  {
	            		  taxe2= Obj.getDouble("taxe2") ;
           	  		  }
            	  		  else if(list.get(i).equals("etatfacture"))
            	  		  {
	            		  etatfacture= Obj.getString("etatfacture") ;
            	  		  }
            	  		 else if(list.get(i).equals("naturecomptable"))
            	  		 {
	            		  naturecomptable= Obj.getString("naturecomptable") ;
           	  		  		}
            	  		else if(list.get(i).equals("methoddepaiement"))
             	  		  {
  	            		  methodepaiement= Obj.getString("methoddepaiement") ;
             	  		  }
            	  		else if(list.get(i).equals("devise"))
            	  		{
	            		  devise= Obj.getString("devise") ;
            	  		}
            	  		  else if(list.get(i).equals("lien"))
            	  		  {
	            		  lien= bundle.getString("BaseUrl")+Obj.getString("lien") ;
            	  		  }
            	  		  else if(list.get(i).equals("date"))
            	  		  {
            	  		  DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            	  		  String dateStr = Obj.getString("date");
            	  		  SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            	  		  SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM d, yyyy");
            	  		DateTime dateTime = new DateTime( dateStr, DateTimeZone.UTC );
            	  		  date=dateTime.toDate();
            	  		//date= dateParser.parse(dateStr);    
            	  		  }else if(list.get(i).equals("dateexpiration"))
            	  		  {
	            		  	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
		     	          	String dateStr = Obj.getString("dateexpiration");
		          		    SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		          		    SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM d, yyyy");
		          		    DateTime dateTime = new DateTime( dateStr, DateTimeZone.UTC );
		          		    datee=dateTime.toDate();
	            	  		
		          		    //datee= dateParser.parse(dateStr); 
            	  		  }
            	  	  }
            	  	}
            	  	  	GenericEntity<GlobalInvoice> generic = new GenericEntity<GlobalInvoice>(new GlobalInvoice(new Invoice(date,num_bc,numInvoice, totalht,
                				taxe1,taxe2,
                				naturecomptable,methodepaiement,devise,totaltva,
                				etatfacture,lien,totalht))){};
            	  	log.error(" =============== Authentication success =============== ");
            	  	return Response.status(200).entity(generic).build();	
              }
              else 
              {
            	  	log.error(" =============== SEARCH PRODUCT RETURN NULL =============== ");
          			GenericEntity<InvoiceData> generic = new GenericEntity<InvoiceData>(new InvoiceData()){};
                	log.error(" =============== Authentication success =============== ");
                	return Response.status(200).entity(generic).build();
              
              }
		  }
          
		  //GenericEntity<Invoice> generic = new GenericEntity<Invoice>(new Invoice(numInvoice,date,num_bc,datee,totalht,totalttc,totaltva,etatfacture,lien)){};
			
		  //return Response.status(200).entity(generic).build();
	  }
	  	log.error(" =============== SEARCH PRODUCT RETURN NULL =============== ");
		GenericEntity<InvoiceData> generic = new GenericEntity<InvoiceData>(new InvoiceData()){};
      	log.error(" =============== Authentication success =============== ");
      	return Response.status(200).entity(generic).build();
    
 }
 
 @POST
 @Path("/fournisseur")
 @Produces(MediaType.APPLICATION_JSON)
 public JSONObject vendors() throws KeyManagementException, NoSuchAlgorithmException, ParseException, JsonParseException, IOException 
 {
	 log.error(" =============== SEARCH PRODUCT =============== ");
	 //mapper.readValue(jsonWithNullString, DatePojo.class); // Throws the exception
	 //Assert.assertNull(jsonWithNullString);
	 //log.error(" === < Session id : " + session.getId() + " > ===");
	// log.error(" === < Access token : " + session.toString() + " > ===");
	 //accessToken = session.getAttribute("accessToken").toString();
	 accessToken=Constant.access_token;
	 log.error(" === < Access token : " + accessToken + " > ===");
	  
	   Client client = createClient(Fournisseur.class);
	   log.error(" === < Access token 2: " + accessToken + " > ===");
		
		WebResource webResource = client.resource(bundle.getString("BaseUrl") + "/api/v1/vendor");
		Builder builder = webResource
				.accept(MediaType.WILDCARD)
				.header("content-type", MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken);
		System.out.println(client.resource(bundle.getString("BaseUrl"))+"/api/v1/vendor" + "/getall");
		log.error(" === < Access token 3: " + accessToken + " > ===");
		ClientResponse response = builder.put(ClientResponse.class);
		
		  if(response != null) {
		  JSONObject json = response.getEntity(JSONObject.class);
		  log.error(" === <Json : " + json + " > ===");
			
		  return json;
  }
	  	return null;
	  	   
 }
 
 @POST
 @Path("/createInvoice")
 @Produces(MediaType.APPLICATION_JSON)
 public Response createPr(Invoice invoice, @Context HttpServletRequest request) throws KeyManagementException, NoSuchAlgorithmException 
 {
	 log.error(" =============== UPDATE PRODUCT =============== ");
	 log.error(" === < Barre code : " + invoice+ " > ===");
	 session = request.getSession();
	 log.error(" === < Session id : " + session.getId() + " > ===");
	 //accessToken = session.getAttribute("accessToken").toString();
	 accessToken=Constant.access_token;
	 log.error(" === < Access token : " + accessToken + " > ===");
	  ClientResponse response = create(invoice);
	  
	  if(response != null) {
		  JSONObject json = response.getEntity(JSONObject.class);
			
		  if(json.containsKey("Invoice"))
		  {
			  
		  }
		  
	    GenericEntity<Messageupdateinvoice> generic = new GenericEntity<Messageupdateinvoice>(new Messageupdateinvoice(new MessageErreur("Invoice created"))){};
		log.error(" =============== Authentication SUCCESS =============== ");
		return Response.status(200).entity(generic).build();
   
        }
       else 
      {
		//GenericEntity<ProductDataNotFound> generic = new GenericEntity<ProductDataNotFound>	(new ProductDataNotFound()) {};
		log.error(" =============== Authentication field=============== ");
		return null;
        }
 }
 
 public ClientResponse find(String barreCode) throws KeyManagementException, NoSuchAlgorithmException 
 {
		Client client = createClient(Invoice.class);
		WebResource webResource = client.resource(bundle.getString("BaseUrl") + Constant.PATH_START_INVOICE + "/findInvoice/" +barreCode);
		Builder builder = webResource.accept(MediaType.APPLICATION_JSON).header("content-type", MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accessToken);
		ClientResponse response = builder.get(ClientResponse.class);
		log.error(" === < Request url find Product : " + webResource.getURI() + " > ===");
		log.error(" === < Status find Product : " + response.getStatus() + " > ===");
		if(response.getStatus() == 200)
		{
			return response;
		}else if(response.getStatus() == 404)
		{
			Client client1 = createClient(Message.class);
			WebResource webResource1 = client1.resource(bundle.getString("BaseUrl") + Constant.PATH_START_INVOICE + "/findInvoice/" +barreCode);
			Builder builder1 = webResource1.accept(MediaType.APPLICATION_JSON).header("content-type", MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accessToken);
			ClientResponse response1 = builder1.get(ClientResponse.class);
			return response1;
		}
		return null;
 }

 public ClientResponse update(Invoice invoice) throws KeyManagementException, NoSuchAlgorithmException {
		Client client = createClient(Invoice.class);
		Gson gson = new Gson();
		WebResource webResource = client.resource(bundle.getString("BaseUrl") + Constant.PATH_START_INVOICE + "/updateInvoice");
		Builder builder = webResource
				.header("content-type", MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken);
		
		ClientResponse response = builder.put(ClientResponse.class, gson.toJson(invoice));
		log.error(" === < Request url find Invoice : " + webResource.getURI() + " > ===");
		log.error(" === < Status find Invoice : " + response.getStatus() + " > ===");
		if(response.getStatus() == 200){
			return response;
		}else if(response.getStatus() == 404) {
			Client client1 = createClient(Product.class);
			Gson gson1 = new Gson();
			WebResource webResource1 = client1.resource(bundle.getString("BaseUrl") + Constant.PATH_START_INVOICE + "/updateInvoice");
			Builder builder1 = webResource1
					.accept(MediaType.APPLICATION_JSON)
					.header("content-type", MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + accessToken);
			ClientResponse response1 = builder1.put(ClientResponse.class, gson1.toJson(invoice));
			return response1;
		}
		return null;	
}
 
 public ClientResponse search(Invoice invoice) throws KeyManagementException, NoSuchAlgorithmException {
		Client client = createClient(Invoice.class);
		Gson gson = new Gson();
		WebResource webResource = client.resource(bundle.getString("BaseUrl") + Constant.PATH_START_INVOICE + "/searchInvoice");
		Builder builder = webResource
				.accept(MediaType.APPLICATION_JSON)
				.header("content-type", MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken);
		ClientResponse response = builder.put(ClientResponse.class, gson.toJson(invoice));
		log.error(" === < Request url search Product : " + webResource.getURI() + " > ===");
		log.error(" === < Status search Product : " + response.getStatus() + " > ===");
		if(response.getStatus() == 200){
			return response;
		}else if(response.getStatus() == 404) {
			Client client1 = createClient(Invoice.class);
			Gson gson1 = new Gson();
			WebResource webResource1 = client1.resource(bundle.getString("BaseUrl") + Constant.PATH_START_INVOICE + "/searchInvoice");
			Builder builder1 = webResource1
					.accept(MediaType.APPLICATION_JSON)
					.header("content-type", MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + accessToken);
			ClientResponse response1 = builder1.put(ClientResponse.class, gson1.toJson(invoice));
			return response1;
		}
		return null;	
}
 
 public Client createClient(Class c) throws KeyManagementException, NoSuchAlgorithmException 
 {
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
 public ClientResponse create(Invoice invoice) throws KeyManagementException, NoSuchAlgorithmException 
 {
		Client client = createClient(Invoice.class);
		Gson gson = new Gson();
		WebResource webResource = client.resource(bundle.getString("BaseUrl") + Constant.PATH_START_INVOICE + "/createFacture");
		Builder builder = webResource
				.accept(MediaType.APPLICATION_JSON)
				.header("content-type", MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken);
		ClientResponse response = builder.put(ClientResponse.class, gson.toJson(invoice));
		log.error(" === < Request url find Invoice : " + webResource.getURI() + " > ===");
		log.error(" === < Status find Invoice : " + response.getStatus() + " > ===");
		if(response.getStatus() == 200){
			return response;
		}else if(response.getStatus() == 404) {
			Client client1 = createClient(Product.class);
			Gson gson1 = new Gson();
			WebResource webResource1 = client1.resource(bundle.getString("BaseUrl") + Constant.PATH_START_INVOICE + "/createFacture");
			Builder builder1 = webResource1
					.accept(MediaType.APPLICATION_JSON)
					.header("content-type", MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + accessToken);
			ClientResponse response1 = builder1.put(ClientResponse.class, gson1.toJson(invoice));
			return response1;
		}
		return null;	
}
 public String createWithFile(File input) throws KeyManagementException, NoSuchAlgorithmException 
 {      System.out.println("-----createWithFile------------- ");
		Client client = createClient(Invoice.class);
		Gson gson = new Gson();
		WebResource webResource = client.resource(bundle.getString("BaseUrl") +"/api/v1/upload/");
		Builder builder = webResource
				.header("Authorization", "Bearer " + accessToken);
		ClientResponse response = builder.post(ClientResponse.class);
		System.out.println(" the response"+ response);
     	log.error(" === < Status create batch : " + response.getStatus() + " > ===");
		if(response.getStatus() == 201){
			 JSONObject json = response.getEntity(JSONObject.class);
			 Object bachId=json.get("batchId");
			 Client client2 = createClient(Invoice.class);
			WebResource webResource2 = client2.resource(bundle.getString("BaseUrl") +"/api/v1/upload/"+bachId+"/0");
			Builder builder2 = webResource2
					.header("Authorization", "Bearer " + accessToken)
					.header("content-type", "application/octet-stream")
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.header("X-File-Name", input.getName())
					.header("X-File-Type", "application/msword");
					
			ClientResponse response2 = builder2.post(ClientResponse.class,input);
			System.out.println(" the response"+ response2);
			System.out.println("ereeur  "+webResource2.head().getHeaders().getFirst("Authorization"));
			JSONObject json2 = response2.getEntity(JSONObject.class);
			log.error(" === < Status create batch : " + response.getStatus() + " > ===");
			log.error(" === < resulat : " + json2 + " > ===");
			
			return bachId.toString();
			
		 
		}else if(response.getStatus() == 404) {
		log.error(" === < Request url create batch : " + webResource.getURI() + " > ===");
			
			return null;
		}
		log.error(" === < Status ----------- > ===");
		
		return null;	
}
 
 
 
 @POST
 @Path("/createInvoiceWithFile")
 @Consumes(MediaType.MULTIPART_FORM_DATA)
 @Produces(MediaType.APPLICATION_JSON)
 public Response createPrWhithFile(@Context HttpServletRequest request,@FormDataParam("inputfile") File inputfile) throws  KeyManagementException, NoSuchAlgorithmException, IOException 
 {
	 //Invoice invoice =new Invoice();
	 log.error(" =============== CREATE PRODUCT =============== ");
	 InputStream targetStream = new FileInputStream(inputfile);
	 session = request.getSession();
	 log.error(" === < Session id : " + session.getId() + " > ===");
	 //accessToken = session.getAttribute("accessToken").toString();
	 accessToken=Constant.access_token;
	 log.error(" === < Access token : " + accessToken + " > ===");
	 //log.error(" === < file name : " + targetStream. + " > ===");
	 //log.error(" === < file path : " + targetStream.getPath() + " > ===");
			 
	 String batchId = createWithFile(inputfile);
	 System.out.println("le resulat bach id  "+batchId);
	 Client client = createClient(Invoice.class);
	 Gson gson1 = new Gson();
	 String nomfichier=inputfile.getName();
	 WebResource webResource2 = client.resource(bundle.getString("BaseUrl") +"/api/v1/path/OCP%20Maroc/workspaces/RH");
	 Builder builder2 = webResource2
				.header("Authorization", "Bearer " + accessToken)
				.header("Content-type", MediaType.APPLICATION_JSON);
	 JSONObject jsonObject2 = new JSONObject();
	 JSONObject ProperitesfILE = new JSONObject();
	 ProperitesfILE.put("upload-batch", batchId);
	 ProperitesfILE.put("upload-fileId", "0");
	 JSONObject Properites = new JSONObject();
	 Properites.put("dc:title", nomfichier);
	 Properites.put("file:content",ProperitesfILE );
	 jsonObject2.put("entity-type", "document");
	 jsonObject2.put("name", nomfichier);
	 jsonObject2.put("type", "FactureFournisseur");
	 jsonObject2.put("properties", Properites);
	 System.out.println("la chaine "+jsonObject2);
	 ClientResponse response2 = builder2.post(ClientResponse.class,gson1.toJson(jsonObject2));
	 System.out.println("le resulat >>>>>>>>>>>>>>>>>>>>hhhhhhh "+response2);
	  
//	  Client client3 = createClient(Invoice.class);
//		 Gson gson2 = new Gson();
//		 WebResource webResource = client3.resource(bundle.getString("BaseUrl") +"/api/v1/invoice/editfacture");
//		 Builder builder = webResource
//					.header("Authorization", "Bearer " + accessToken)
//					.header("content-type", "application/json");
//		 ClientResponse response = builder.post(ClientResponse.class,gson2.toJson(getInvoiceFromJson(invoice)));
//		
	 // JSONObject json2 = response2.getEntity(JSONObject.class);
		// System.out.println("le resulat >>>>>>>>>>>>>>>>>>>> "+getInvoiceFromJson(invoice));
		  
	 // System.out.println("le resulat >>>>>>>>>>>>>>>>>>>> "+response);
	  
		 
	  if(response2 != null) {
//		  JSONObject json = response.getEntity(JSONObject.class);
//			
//		  if(json.containsKey("Invoice"))
//		  {
//			  
//		  }
		  
	    GenericEntity<Messageupdateinvoice> generic = new GenericEntity<Messageupdateinvoice>(new Messageupdateinvoice(new MessageErreur("Invoice created"))){};
		log.error(" =============== Authentication SUCCESS =============== ");
		return Response.status(200).entity(generic).build();
   
        }
       else 
      {
		//GenericEntity<ProductDataNotFound> generic = new GenericEntity<ProductDataNotFound>	(new ProductDataNotFound()) {};
		log.error(" =============== Authentication field=============== ");
		return null;
        }
 }
 
 @POST
 @Path("/createFacturefile")
 @Produces(MediaType.APPLICATION_JSON)
 public JSONObject createFacturefile(Invoice invoice, @Context HttpServletRequest request) throws KeyManagementException, NoSuchAlgorithmException, ParseException 
 {
	 log.error(" =============== CREATE FACTURE WITH FILE =============== ");
	 log.error(" === < barre code : " + invoice.getNumFacture() + " > ===");

	 //session = request.getSession();
	 //log.error(" === < Session id : " + session.getId() + " > ===");
	 //accessToken = session.getAttribute("accessToken").toString();
	 accessToken=Constant.access_token;
	 log.error(" === < Access token : " + accessToken + " > ===");
	 //SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ");
	 //java.util.Date date = new java.util.Date( df.parse( invoice.getDate().toString() ).getTime() );
	 DateFormat formatter;
	 Date date;
	 //formatter = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
	 //date = formatter.parse(invoice.getDate().toString());
	 //long timestamp = date.getTime();
	 invoice.setTimestampDate(invoice.getTimestampDate());
	 Client client = createClient(Invoice.class);
	 Gson gson1 = new Gson();
	 WebResource webResource = client.resource(bundle.getString("BaseUrl") + Constant.PATH_START_INVOICE +"/editfacture");
	 Builder builder = webResource
				.header("Authorization", "Bearer " + accessToken)
				.header("content-type", "application/json");
	 ClientResponse response = builder.put(ClientResponse.class,gson1.toJson(invoice));
	 
	  //ClientResponse response = find(invoice.getNumFacture());
	  if(response != null) {
		  JSONObject json = response.getEntity(JSONObject.class);
		  log.error(" === < Json : " + json + " > ===");
		  log.error(" =============== FIND PRODUCT SUCCESS =============== ");
		  return json;
	  }
	  log.error(" =============== FIND PRODUCT RETURN NULL =============== ");
	 return null;
	 
 }
 
 
 
 
 
 
 
 
 
 
}