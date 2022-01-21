package org.nuxeo.project.sample.rest;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
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
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.nuxeo.project.sample.beans.GlobalProduct;
import org.nuxeo.project.sample.beans.Invoice;
import org.nuxeo.project.sample.beans.InvoiceData;
import org.nuxeo.project.sample.beans.Message;
import org.nuxeo.project.sample.beans.Message2;
import org.nuxeo.project.sample.beans.MessageErreur;
import org.nuxeo.project.sample.beans.Messageupdateinvoice;
import org.nuxeo.project.sample.beans.Product;
import org.nuxeo.project.sample.beans.GlobalProduct;
import org.nuxeo.project.sample.beans.GlobalProductNotFound;
import org.nuxeo.project.sample.beans.ProductDataNotFound;
import org.nuxeo.project.sample.complementary.Constant;

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
import com.sun.star.security.CertificateException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Path("/product")
public class ProductOAuth2Resource {
 
	private String accessToken;
	private static Log log = LogFactory.getLog(ProductOAuth2Resource.class);
	private static ResourceBundle bundle = ResourceBundle.getBundle("Config");
	private HttpSession session;

 
 @POST
 @Path("/findProduct")
 @Produces(MediaType.APPLICATION_JSON)
 public JSONObject findAR(Product product, @Context HttpServletRequest request) throws KeyManagementException, NoSuchAlgorithmException {
	 log.error(" =============== FIND PRODUCT =============== ");
	 log.error(" === < barre code : " + product.getBarreCode() + " > ===");
	 session = request.getSession();
	 log.error(" === < Session id : " + session.getId() + " > ===");
	// accessToken = session.getAttribute("accessToken").toString();
	 accessToken=Constant.access_token;
	 log.error(" === < Access token : " + accessToken + " > ===");
	  ClientResponse response = find(product.getBarreCode());
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
 @Path("/updateProduct")
 @Produces(MediaType.APPLICATION_JSON)
 public Response updateAR(Product product, @Context HttpServletRequest request) throws KeyManagementException, NoSuchAlgorithmException {
	 log.error(" =============== UPDATE PRODUCT =============== ");
	 log.error(" === < Barre code : " + product.getBarreCode() + " > ===");
	 session = request.getSession();
	 log.error(" === < Session id : " + session.getId() + " > ===");
	 //accessToken = session.getAttribute("accessToken").toString();
	 accessToken=Constant.access_token;
	 log.error(" === < Access token : " + accessToken + " > ===");
	  ClientResponse response = update(product);
	  if(response != null) {
		  JSONObject json = response.getEntity(JSONObject.class);
		  log.error(" === < Json : " + json + " > ===");
		  log.error(" =============== UPDATE PRODUCT SUCCESS =============== ");
		 // return json;
		  String barreCode="";
		  Long quantityInStock=0L;
		  Long quantityRequested=0L;
		  Double quantity=0.0;
		  Double unitPrice=0.0;
		  String name="";
		  String unites="";
		  ArrayList<String> list =new ArrayList<String>();
		  list.add("barreCode");
		  list.add("quantityInStock");
		  list.add("quantityRequested");
		  list.add("quantity");
		  list.add("unitPrice");
		  list.add("name");
		  list.add("unites");
		 
		  if (json instanceof JSONObject) 
		  {
              JSONObject object = (JSONObject) json;
              if(object.containsKey("Product"))
              {  
            	    JSONObject Obj = object.getJSONObject("Product");
            	  	for (int i = 0; i < list.size(); i++) 
            	  	{
            	  	  if (Obj.containsKey(list.get(i)))
            	  	  {
            	  		  if(list.get(i).equals("barreCode"))
            	  		  {
            	  			barreCode= Obj.getString("barreCode");
            	  		  }
            	  		  else if(list.get(i).equals("quantityInStock"))
            	  		  {
            	  			quantityInStock= Obj.getLong("quantityInStock") ;
            	  		  }
            	  		  else if(list.get(i).equals("quantityRequested"))
            	  		  {
            	  			quantityRequested= Obj.getLong("quantityRequested") ;
            	  		  }
            	  		  else if(list.get(i).equals("quantity"))
            	  		  {
            	  			quantity= Obj.getDouble("quantity") ;
            	  		  }
            	  		  else if(list.get(i).equals("unitPrice"))
            	  		  {
            	  			unitPrice= Obj.getDouble("unitPrice") ;
            	  		  }
            	  		  else if(list.get(i).equals("name"))
            	  		  {
            	  			name= Obj.getString("name") ;
            	  		  }
            	  		  else if(list.get(i).equals("unites"))
            	  		  {
            	  			unites= Obj.getString("unites") ;
            	  		  }
            	  	  }
            	  	}
            	  	GenericEntity<GlobalProduct> generic = new GenericEntity<GlobalProduct>	(new GlobalProduct(new Product(barreCode,quantityInStock,quantity,quantityRequested,unitPrice,name,unites))) {};
        			log.error(" =============== Authentication field=============== ");
        			
        			return Response.status(200).entity(generic).build();
              }
              else 
              {
            		GenericEntity<ProductDataNotFound> generic = new GenericEntity<ProductDataNotFound>	(new ProductDataNotFound()) {};
        			log.error(" =============== Authentication field=============== ");
      			return Response.status(200).entity(generic).build();
		       }

		  	//GenericEntity<GlobalProduct> genericEnty = new GenericEntity<GlobalProduct>	(new GlobalProduct(new Product(barreCode,quantityInStock,quantity,quantityRequested,unitPrice,name,unites))) {};
			//log.error(" =============== Authentication field=============== ");
			
			//return Response.status(200).entity(genericEnty).build();
			
		  //GenericEntity<ProductDataNotFound> generic = new GenericEntity<ProductDataNotFound>	(new ProductDataNotFound()) {};
		  //log.error(" =============== Authentication field=============== ");
		//return Response.status(200).entity(generic).build();
	  }
	  }
	  log.error(" =============== UPDATE PRODUCT RETURN NULL =============== ");
	 return null;
 }
 
 @POST
 @Path("/searchProduct")
 @Produces(MediaType.APPLICATION_JSON)
 public Response searchAR(Product product, @Context HttpServletRequest request) throws KeyManagementException, NoSuchAlgorithmException {
	 log.error(" =============== SEARCH PRODUCT =============== ");
	 log.error(" === < Barre code : " + product.getBarreCode() + " > ===");
	 session = request.getSession();
	 log.error(" === < Session id : " + session.getId() + " > ===");
	 //accessToken = session.getAttribute("accessToken").toString();
	 accessToken=Constant.access_token;
	 log.error(" === < Access token : " + accessToken + " > ===");
	  ClientResponse response = search(product);
	  if(response != null) 
	  {
		  JSONObject json = response.getEntity(JSONObject.class);
		  log.error(" === < Json : " + json + " > ===");
		  log.error(" =============== SEARCH PRODUCT SUCCESS =============== ");
		 // return json;
		  String barreCode="";
		  Long quantityInStock=0L;
		  Long quantityRequested=0L;
		  Double quantity=0.0;
		  Double unitPrice=0.0;
		  String name="";
		  String unites="";
		  ArrayList<String> list =new ArrayList<String>();
		  list.add("barreCode");
		  list.add("quantityInStock");
		  list.add("quantityRequested");
		  list.add("quantity");
		  list.add("unitPrice");
		  list.add("name");
		  list.add("unites");
		 
		  if (json instanceof JSONObject) 
		  {
              JSONObject object = (JSONObject) json;
              
              
              
              if(object.containsKey("Product"))
              {  
            	  

            	// JSONObject json1 = new JSONObject(json);            
            	 if (json.has("Product")) {
           	      JSONObject Obj = json.optJSONObject("Product");
           	      if (Obj != null) {

            	  for (int i = 0; i < list.size(); i++) 
            	  {
          	  	  if (Obj.containsKey(list.get(i)))
          	  	  {
          	  		  if(list.get(i).equals("barreCode"))
          	  		  {
          	  			barreCode= Obj.getString("barreCode");
          	  		  }
          	  		  else if(list.get(i).equals("quantityInStock"))
          	  		  {
          	  			quantityInStock= Obj.getLong("quantityInStock") ;
          	  		  }
          	  		  else if(list.get(i).equals("quantityRequested"))
          	  		  {
          	  			quantityRequested= Obj.getLong("quantityRequested") ;
          	  		  }
          	  		  else if(list.get(i).equals("quantity"))
          	  		  {
          	  			quantity= Obj.getDouble("quantity") ;
          	  		  }
          	  		  else if(list.get(i).equals("unitPrice"))
          	  		  {
          	  			unitPrice= Obj.getDouble("unitPrice") ;
          	  		  }
          	  		  else if(list.get(i).equals("name"))
          	  		  {
          	  			name= Obj.getString("name") ;
          	  		  }
          	  		  else if(list.get(i).equals("unites"))
          	  		  {
          	  			unites= Obj.getString("unites") ;
          	  		  }
          	  	  }
          	  	}
           	      
           	   } 
//           	   else 
//           	      {
//           	          JSONArray array = json.optJSONArray("Product");
//                  }
           	  } else {
            	      // Do nothing or throw exception if "data" is a mandatory field
            	  }
            	  
            	  
            	    //JSONObject Obj = object.getJSONObject("Product");
//            	  	for (int i = 0; i < list.size(); i++) 
//            	  	{
//            	  	  if (Obj.containsKey(list.get(i)))
//            	  	  {
//            	  		  if(list.get(i).equals("barreCode"))
//            	  		  {
//            	  			barreCode= Obj.getString("barreCode");
//            	  		  }
//            	  		  else if(list.get(i).equals("quantityInStock"))
//            	  		  {
//            	  			quantityInStock= Obj.getLong("quantityInStock") ;
//            	  		  }
//            	  		  else if(list.get(i).equals("quantityRequested"))
//            	  		  {
//            	  			quantityRequested= Obj.getLong("quantityRequested") ;
//            	  		  }
//            	  		  else if(list.get(i).equals("quantity"))
//            	  		  {
//            	  			quantity= Obj.getDouble("quantity") ;
//            	  		  }
//            	  		  else if(list.get(i).equals("unitPrice"))
//            	  		  {
//            	  			unitPrice= Obj.getDouble("unitPrice") ;
//            	  		  }
//            	  		  else if(list.get(i).equals("name"))
//            	  		  {
//            	  			name= Obj.getString("name") ;
//            	  		  }
//            	  		  else if(list.get(i).equals("unites"))
//            	  		  {
//            	  			unites= Obj.getString("unites") ;
//            	  		  }
//            	  	  }
//            	  	}
            	  	GenericEntity<GlobalProduct> generic = new GenericEntity<GlobalProduct>	(new GlobalProduct(new Product(barreCode,quantityInStock,quantity,quantityRequested,unitPrice,name,unites))) {};
        			log.error(" =============== Authentication field=============== ");
        			
        			return Response.status(200).entity(generic).build();
              }
              else 
              {
            		GenericEntity<ProductDataNotFound> generic = new GenericEntity<ProductDataNotFound>	(new ProductDataNotFound()) {};
        			log.error(" =============== Authentication field=============== ");
      			return Response.status(200).entity(generic).build();
		       }
         	
	  }
	 
 
 }
	  log.error(" =============== SEARCH PRODUCT RETURN NULL =============== ");
		 return null;
 }
 public ClientResponse find(String barreCode) throws KeyManagementException, NoSuchAlgorithmException {
		Client client = createClient(Product.class);
		WebResource webResource = client.resource(bundle.getString("BaseUrl") + Constant.PATH_START_Product + "/findProduct/" +barreCode);
		Builder builder = webResource.accept(MediaType.APPLICATION_JSON).header("content-type", MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accessToken);
		ClientResponse response = builder.get(ClientResponse.class);
		log.error(" === < Request url find Product : " + webResource.getURI() + " > ===");
		log.error(" === < Status find Product : " + response.getStatus() + " > ===");
		if(response.getStatus() == 200){
			return response;
		}else if(response.getStatus() == 404){
			Client client1 = createClient(Message.class);
			WebResource webResource1 = client1.resource(bundle.getString("BaseUrl") + Constant.PATH_START_Product + "/findProduct/" +barreCode);
			Builder builder1 = webResource1.accept(MediaType.APPLICATION_JSON).header("content-type", MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accessToken);
			ClientResponse response1 = builder1.get(ClientResponse.class);
			return response1;
		}
		return null;
 }

 public ClientResponse update(Product product) throws KeyManagementException, NoSuchAlgorithmException {
		Client client = createClient(Product.class);
		Gson gson = new Gson();
		WebResource webResource = client.resource(bundle.getString("BaseUrl") + Constant.PATH_START_Product + "/updateProduct");
		Builder builder = webResource
				.accept(MediaType.APPLICATION_JSON)
				.header("content-type", MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken);
		ClientResponse response = builder.put(ClientResponse.class, gson.toJson(product));
		log.error(" === < Request url find Product : " + webResource.getURI() + " > ===");
		log.error(" === < Status find Product : " + response.getStatus() + " > ===");
		if(response.getStatus() == 200){
			return response;
		}else if(response.getStatus() == 404) {
			Client client1 = createClient(Product.class);
			Gson gson1 = new Gson();
			WebResource webResource1 = client1.resource(bundle.getString("BaseUrl") + Constant.PATH_START_Product + "/updateProduct");
			Builder builder1 = webResource1
					.accept(MediaType.APPLICATION_JSON)
					.header("content-type", MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + accessToken);
			ClientResponse response1 = builder1.put(ClientResponse.class, gson1.toJson(product));
			return response1;
		}
		return null;	
}
 
 public ClientResponse search(Product product) throws KeyManagementException, NoSuchAlgorithmException {
		Client client = createClient(Product.class);
		Gson gson = new Gson();
		WebResource webResource = client.resource(bundle.getString("BaseUrl") + Constant.PATH_START_Product + "/searchProduct");
		Builder builder = webResource
				.accept(MediaType.APPLICATION_JSON)
				.header("content-type", MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken);
		ClientResponse response = builder.put(ClientResponse.class, gson.toJson(product));
		log.error(" === < Request url search Product : " + webResource.getURI() + " > ===");
		log.error(" === < Status search Product : " + response.getStatus() + " > ===");
		if(response.getStatus() == 200){
			return response;
		}else if(response.getStatus() == 404) {
			Client client1 = createClient(Product.class);
			Gson gson1 = new Gson();
			WebResource webResource1 = client1.resource(bundle.getString("BaseUrl") + Constant.PATH_START_Product + "/searchProduct");
			Builder builder1 = webResource1
					.accept(MediaType.APPLICATION_JSON)
					.header("content-type", MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + accessToken);
			ClientResponse response1 = builder1.put(ClientResponse.class, gson1.toJson(product));
			return response1;
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
 } public ClientResponse create(Product product) throws KeyManagementException, NoSuchAlgorithmException {
		Client client = createClient(Product.class);
		Gson gson = new Gson();
		WebResource webResource = client.resource(bundle.getString("BaseUrl") + Constant.PATH_START_Product + "/createProduct");
		Builder builder = webResource
				.accept(MediaType.APPLICATION_JSON)
				.header("content-type", MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken);
		ClientResponse response = builder.put(ClientResponse.class, gson.toJson(product));
		log.error(" === < Request url find Product : " + webResource.getURI() + " > ===");
		log.error(" === < Status find Product : " + response.getStatus() + " > ===");
		if(response.getStatus() == 200){
			return response;
		}else if(response.getStatus() == 404) {
			Client client1 = createClient(Product.class);
			Gson gson1 = new Gson();
			WebResource webResource1 = client1.resource(bundle.getString("BaseUrl") + Constant.PATH_START_Product + "/createProduct");
			Builder builder1 = webResource1
					.accept(MediaType.APPLICATION_JSON)
					.header("content-type", MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + accessToken);
			ClientResponse response1 = builder1.put(ClientResponse.class, gson1.toJson(product));
			return response1;
		}
		return null;	
}
 @POST
 @Path("/createProduct")
 @Produces(MediaType.APPLICATION_JSON)
 public Response createPr(Product product, @Context HttpServletRequest request) throws KeyManagementException, NoSuchAlgorithmException {
	 log.error(" =============== UPDATE PRODUCT =============== ");
	 log.error(" === < Barre code : " + product.getBarreCode() + " > ===");
	 session = request.getSession();
	 log.error(" === < Session id : " + session.getId() + " > ===");
	 //accessToken = session.getAttribute("accessToken").toString();
	 accessToken=Constant.access_token;
	 log.error(" === < Access token : " + accessToken + " > ===");
	  ClientResponse response = create(product);
	  
	  if(response != null) {
		  
	    GenericEntity<Messageupdateinvoice> generic = new GenericEntity<Messageupdateinvoice>(new Messageupdateinvoice(new MessageErreur("Product created"))){};
		log.error(" =============== Authentication SUCCESS =============== ");
		return Response.status(200).entity(generic).build();
   
        }
       else 
      {
		//GenericEntity<ProductDataNotFound> generic = new GenericEntity<ProductDataNotFound>	(new ProductDataNotFound()) {};
		log.error(" =============== Authentication field=============== ");
		return null;
         }
	 
//	  if(response != null) {
//		  
//		  
//		  
//		  JSONObject json = response.getEntity(JSONObject.class);
//		  
//		  log.error(" === < Json : " + json + " > ===");
//		  log.error(" =============== CREATE PRODUCT SUCCESS =============== ");
//		 // return json;
//		  String barreCode="";
//		  Long quantityInStock=0L;
//		  Long quantityRequested=0L;
//		  Double quantity=0.0;
//		  Double unitPrice=0.0;
//		  String name="";
//		  String unites="";
//		  String compteComptable="";
//		  String categorie="";
//		  Long quantityMin=0L;
//		  ArrayList<String> list =new ArrayList<String>();
//		  list.add("barreCode");
//		  list.add("quantityInStock");
//		  list.add("quantityRequested");
//		  list.add("quantity");
//		  list.add("unitPrice");
//		  list.add("name");
//		  list.add("unites");
//		  list.add("compteComptable");
//		  list.add("categorie");
//		  list.add("quantityMin");
//		  
//		  if (json instanceof JSONObject) 
//		  {
//              JSONObject object = (JSONObject) json;
//              if(object.containsKey("Product"))
//              {  
//            	    JSONObject Obj = object.getJSONObject("Product");
//            	  	for (int i = 0; i < list.size(); i++) 
//            	  	{
//            	  	  if (Obj.containsKey(list.get(i)))
//            	  	  {
//            	  		  if(list.get(i).equals("barreCode"))
//            	  		  {
//            	  			barreCode= Obj.getString("barreCode");
//            	  		  }
//            	  		  else if(list.get(i).equals("quantityInStock"))
//            	  		  {
//            	  			quantityInStock= Obj.getLong("quantityInStock") ;
//            	  		  }
//            	  		  else if(list.get(i).equals("quantityRequested"))
//            	  		  {
//            	  			quantityRequested= Obj.getLong("quantityRequested") ;
//            	  		  }
//            	  		  else if(list.get(i).equals("quantity"))
//            	  		  {
//            	  			quantity= Obj.getDouble("quantity") ;
//            	  		  }
//            	  		  else if(list.get(i).equals("unitPrice"))
//            	  		  {
//            	  			unitPrice= Obj.getDouble("unitPrice") ;
//            	  		  }
//            	  		  else if(list.get(i).equals("name"))
//            	  		  {
//            	  			name= Obj.getString("name") ;
//            	  		  }
//            	  		  else if(list.get(i).equals("unites"))
//            	  		  {
//            	  			unites= Obj.getString("unites") ;
//            	  		  }
//            	  		else if(list.get(i).equals("quantityMin"))
//          	  		  {
//            	  			quantityMin= Obj.getLong("quantityMin") ;
//          	  		  }
//            	  		else if(list.get(i).equals("compteComptable"))
//          	  		  {
//            	  			compteComptable= Obj.getString("compteComptable") ;
//          	  		  }
//            	  		else if(list.get(i).equals("categorie"))
//          	  		  {
//            	  			categorie= Obj.getString("categorie") ;
//          	  		  }
//            	  	  }
//            	  	}
//            	  	GenericEntity<GlobalProduct> generic = new GenericEntity<GlobalProduct>	(new GlobalProduct(new Product(barreCode,quantityInStock,quantity,quantityRequested,unitPrice,name,unites,categorie,compteComptable,quantityMin))) {};
//            	  	
//        			log.error(" =============== Authentication success=============== ");
//        			
//        			return Response.status(200).entity(generic).build();
//              }
//              else 
//              {
//            		GenericEntity<ProductDataNotFound> generic = new GenericEntity<ProductDataNotFound>	(new ProductDataNotFound()) {};
//        			log.error(" =============== Authentication field=============== ");
//      			return Response.status(200).entity(generic).build();
//		       }
//              
//              
//
//		  	//GenericEntity<GlobalProduct> genericEnty = new GenericEntity<GlobalProduct>	(new GlobalProduct(new Product(barreCode,quantityInStock,quantity,quantityRequested,unitPrice,name,unites))) {};
//			//log.error(" =============== Authentication field=============== ");
//			
//			//return Response.status(200).entity(genericEnty).build();
//			
//		  //GenericEntity<ProductDataNotFound> generic = new GenericEntity<ProductDataNotFound>	(new ProductDataNotFound()) {};
//		  //log.error(" =============== Authentication field=============== ");
//		//return Response.status(200).entity(generic).build();
//	  }
//	  }
	 // log.error(" =============== UPDATE PRODUCT RETURN NULL =============== ");

 }

}