package org.nuxeo.project.sample.sage;

import java.io.IOException;
import java.io.StringReader;
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
import javax.ws.rs.core.Response;

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
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.project.sample.beans.InvoiceSage;
import org.nuxeo.project.sample.beans.LedgerAccounts;
import org.nuxeo.project.sample.beans.ListInvoice;
import org.nuxeo.project.sample.beans.Taxe;
import org.nuxeo.project.sample.beans.Vendor;
import org.nuxeo.project.sample.complementary.Constant;
import org.nuxeo.project.sample.services.MethodsShared;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.star.security.CertificateException;

import net.sf.json.JSON;

/**
 * Servlet implementation class GetData
 */
@WebServlet("/SageoneData5")
public class SageoneData5 extends HttpServlet {
	private static Log log = LogFactory.getLog(SageoneData5.class);
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
			HttpRequestBase request = requestMethod.equals("GET") ? new HttpGet(uri) : new HttpPost(uri);
			setRequestHeaders(nonce, accessToken, resourceOwnerId, signature, request);
			// Make Request
			HttpClient httpclient = HttpClients.createDefault();
			
//			{
//				  "purchase_invoice": {
//				    "contact_id": "string",
//				    "date": "2020-08-13",
//				    "due_date": "2020-08-13",
//				    "postponed_accounting": true,
//				    "cis_applicable_amount": 0,
//				    "base_currency_cis_applicable_amount": 0,
//				    "total_after_cis_deduction": 0,
//				    "base_currency_total_after_cis_deduction": 0,
//				    "base_currency_total_itc_amount": 0,
//				    "total_itc_amount": 0,
//				    "base_currency_total_itr_amount": 0,
//				    "total_itr_amount": 0,
//				    "part_recoverable": true,
//				    "contact_name": "string",
//				    "contact_reference": "string",
//				    "reference": "string",
//				    "vendor_reference": "string",
//				    "notes": "string",
//				    "total_quantity": 0,
//				    "net_amount": 0,
//				    "tax_amount": 0,
//				    "total_amount": 0,
//				    "currency_id": "string",
//				    "exchange_rate": 0,
//				    "inverse_exchange_rate": 0,
//				    "base_currency_net_amount": 0,
//				    "base_currency_tax_amount": 0,
//				    "base_currency_total_amount": 0,
//				    "status_id": "string",
//				    "tax_address_region_id": "string",
//				    "withholding_tax_rate": 0,
//				    "withholding_tax_amount": 0,
//				    "base_currency_withholding_tax_amount": 0,
//				    "invoice_lines": [
//				      {
//				        "description": "string",
//				        "ledger_account_id": "string",
//				        "quantity": 0,
//				        "unit_price": 0,
//				        "is_purchase_for_resale": true,
//				        "product_id": "string",
//				        "service_id": "string",
//				        "trade_of_asset": true,
//				        "net_amount": 0,
//				        "tax_rate_id": "string",
//				        "tax_amount": 0,
//				        "total_amount": 0,
//				        "base_currency_unit_price": 0,
//				        "unit_price_includes_tax": true,
//				        "base_currency_net_amount": 0,
//				        "base_currency_tax_amount": 0,
//				        "base_currency_total_amount": 0,
//				        "eu_goods_services_type_id": "string",
//				        "gst_amount": 0,
//				        "pst_amount": 0,
//				        "tax_recoverable": true
//				      }
//				    ],
//				    "tax_analysis": [
//				      {
//				        "tax_rate_id": "string",
//				        "net_amount": 0,
//				        "tax_amount": 0,
//				        "total_amount": 0,
//				        "goods_amount": 0,
//				        "service_amount": 0
//				      }
//				    ]
//				  }
//				}
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
//		MethodShared = new MethodsShared();
//		String requestMethod = req.getParameter("request_method").toUpperCase();
//		String endpoint = MethodShared.Config().getUsurl() + req.getParameter("endpoint");
//		String params;
//		String nonce = Nonce.generateNonce();
//		String signingSecret = MethodShared.Config().getSigningClient();
//		String accessToken = req.getParameter("access_token");
//		String resourceOwnerId = req.getParameter("resource_owner_id");
//
//		// get the body params as a JSON string
//		params = req.getParameter("data");
//
//		// Generate the signature
//		SageoneSigner s = new SageoneSigner(requestMethod, endpoint, params, nonce, signingSecret, accessToken, resourceOwnerId);
//		String signature = s.signature();
//
//		try {
//			URIBuilder builder = new URIBuilder(endpoint);
//			URI uri = builder.build();
//
//			StringEntity entity = new StringEntity(params);
//			HttpRequestBase request;
//
//			if (requestMethod.equals("POST")) {
//				request = new HttpPost(uri);
//				((HttpPost) request).setEntity(entity);
//			}
//			else
//			{
//				request = new HttpPut(uri);
//				((HttpPut) request).setEntity(entity);
//			}
//
//			setRequestHeaders(nonce, accessToken, resourceOwnerId, signature, request);
//
//			// Make Request
//			HttpClient httpclient = HttpClients.createDefault();
//			HttpResponse response = httpclient.execute(request);
//
//			renderResponse(resp, response,req);
//			request.releaseConnection();
//		}
//		catch (Exception e)
//		{
//			System.out.println(e.getMessage());
//		}
		
		
		
		
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
			HttpRequestBase request = requestMethod.equals("POST") ? new HttpGet(uri) : new HttpDelete(uri);
			setRequestHeaders(nonce, accessToken, resourceOwnerId, signature, request);
			// Make Request
			HttpClient httpclient = HttpClients.createDefault();
			JSONObject json=getAllInvoice();
			System.out.println("invoice   "+json);	
     		// String invoice="{\"purchase_invoice\":{";
//
//				
//for (InvoiceSage iterable_element : json.getInvoiceSage()) {
//	   invoice=invoice+"\"contact_id\":\""+iterable_element.getCodefournisseur()+"\"";
//	   invoice=invoice+"\"date\":\""+iterable_element.getDate()+"\"";
//	   invoice=invoice+"\"due_date\":\""+iterable_element.getDateexpiration()+"\"";
//	   invoice=invoice+"\"contact_name\":\""+iterable_element.getNomFournisseur()+"\"}";
//		
	
//}            
		JSONArray  jsonArrayResponse =json.getJSONArray("InvoiceSage");
			  for (int i = 0; i < jsonArrayResponse.length(); i++) {
				  String name="";
				  String invoice="{\"purchase_invoice\":{"; 
		    	  	 Iterator iter = jsonArrayResponse.getJSONObject(i).keys();
		    	 //String invoice="{\"purchase_invoice\":{";
					 while(iter.hasNext()){
				   String key = (String)iter.next();
				   if(key.equals("name"))
					  {
				       name=jsonArrayResponse.getJSONObject(i).getString(key);
					  }
					   if(key.equals("codefournisseur"))
					  {
						   invoice=invoice+"\"contact_id\":\""+jsonArrayResponse.getJSONObject(i).getString(key)+"\",";
					  }
				   else if(key.equals("date"))
				   {
						   invoice=invoice+"\"date\":\""+jsonArrayResponse.getJSONObject(i).getString(key)+"\",";
							
					   }
					   else if(key.equals("dateexpiration"))
					   {
						   invoice=invoice+"\"due_date\":\""+jsonArrayResponse.getJSONObject(i).getString(key)+"\",";
							
					   }
					   else if(key.equals("NomFournisseur"))
					   { 
					   
						   invoice=invoice+"\"contact_name\":\""+jsonArrayResponse.getJSONObject(i).getString(key)+"\",";
							
					   }
					   
					   else if(key.equals("products")) 
					   {
						   JSONObject jsonproduit=jsonArrayResponse.getJSONObject(i).getJSONObject(key);
						  	 Iterator iterprod = jsonproduit.keys();
					    	 //String invoice="{\"invoice_lines\":[";
						  	invoice=invoice+"\"invoice_lines\":[{";
								 while(iterprod.hasNext()){
							   String keyprd = (String)iterprod.next();
							   if(keyprd.equals("description"))
							   { 
							   
								   invoice=invoice+"\"description\":\""+jsonproduit.getString(keyprd)+"\",";
									
							   }
							   else if(keyprd.equals("ledger_account_id"))
							   { 
							   
								   invoice=invoice+"\"ledger_account_id\":\""+jsonproduit.getString(keyprd)+"\",";
									
							   }
							   else if(keyprd.equals("quantity"))
							   { 
							   
								   invoice=invoice+"\"quantity\":\""+jsonproduit.getString(keyprd)+"\",";
									
							   }
							   else if(keyprd.equals("taxe_rate_id"))
							   { 
							   
								   invoice=invoice+"\"tax_rate_id\":\""+jsonproduit.getString(keyprd)+"\",";
									
							   }
							   else if(keyprd.equals("unitPrice"))
							   { 
							   
								   invoice=invoice+"\"unit_price\":\""+jsonproduit.getString(keyprd)+"\",";
									
							   }
							   else if(keyprd.equals("taxe_amount")&& jsonproduit.getString(keyprd)!= "" )
							   { 
							   
								   invoice=invoice+"\"tax_amount\":\""+jsonproduit.getString(keyprd)+"\",";
									
							   }
							   else if(keyprd.equals("discount_amount")&& jsonproduit.getString(keyprd)!= "" )
							   { 
							   
								   invoice=invoice+"\"discount_amount\":\""+jsonproduit.getString(keyprd)+"\",";
									
							   }
								 }
						   //invoice=invoice+" \"invoice_lines\": ["+jsonArrayResponse.getJSONObject(i).getString(key)+"]";
					   
					   
					   }
					   
					  }
					  System.out.println("Invoice 1 "+invoice);
						 
					    invoice=invoice.substring(0, invoice.length()-1)+"}]}}";
					   params=invoice;
					   System.out.println("Invoice 2 "+invoice);
					 	//try {
							URIBuilder builder2 = new URIBuilder(endpoint);
							URI uri1 = builder2.build();
				
							StringEntity entity = new StringEntity(params);
						HttpRequestBase request2;
				
							if (requestMethod.equals("POST")) 
							{
							System.out.println("Methode  "+requestMethod);
							System.out.println("endpoint  "+endpoint);
							request = new HttpPost(uri);
							((HttpPost) request).setEntity(entity);
							setRequestHeaders(nonce, accessToken, resourceOwnerId, signature, request);
							
							}
					 	
//					 	Gson gson = new GsonBuilder().setPrettyPrinting().create();
//					 	StringEntity postingString = new StringEntity(gson.toJson(invoice));
					 	HttpClient   httpClient    = HttpClientBuilder.create().build();
//					 	HttpPost     post          = new HttpPost(postUrl);
//					 	StringEntity postingString = new StringEntity(gson.toJson(pojo1));//gson.tojson() converts your pojo to json
//					 	post.setEntity(postingString);
//					 	post.setHeader("Content-type", "application/json");
//					 	HttpResponse  response = httpClient.execute(post);
//					 	HttpResponse response = httpclient.execute(request,(HttpContext) invoice);
//     					renderResponse(resp, response,req);
					 	HttpResponse response = httpclient.execute(request);
					    //request.releaseConnection();
					 	System.out.println("response   "+response);
					 	
						
					// }

					 	String tokentransfer = Constant.access_token;		
						Client client1 = createClient(InvoiceSage.class);
						String callbackUrl = MethodShared.Config().getUrl();
						String[] chaine=callbackUrl.split("/");
	   					String nouveauchaine=chaine[0]+"/"+chaine[1]+"/"+chaine[2]+"/"+chaine[3];
						WebResource webResource2 = client1.resource(nouveauchaine + Constant.PATH_START_INVOICE2 + "/updateInvoice");
						Builder builder3 = webResource2
								.accept(MediaType.APPLICATION_JSON)
								.header("content-type", MediaType.APPLICATION_JSON)
								.header("Authorization", "Bearer " + tokentransfer);
						ClientResponse responseTRANSFER = builder3.put(ClientResponse.class,name);  
						log.error(" === < Status update invoice : " + responseTRANSFER.getStatus() + " > ===");
						log.error(" === < Status update invoice : " + responseTRANSFER.toString() + " > ===");
		}
	   
		}catch (Exception e)
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
			System.out.println("le items 1"+jsonResponse.getJSONArray("$items"));
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
			 List<LedgerAccounts> ledgerNews = new ArrayList<LedgerAccounts>();
				System.out.println("le items 2"+jsonResponse.getJSONArray("$items"));
				
			 JSONArray arr = jsonResponse.getJSONArray("$items");
			  for (int i = 0; i < arr.length(); i++) {
			    	 LedgerAccounts ledger=new LedgerAccounts();
			    	 Iterator iter = arr.getJSONObject(i).keys();
					 while(iter.hasNext()){
					   String key = (String)iter.next();
					   if(key.equals("id"))
					  {
					   String value = arr.getJSONObject(i).getString(key);
					   ledger.setId(value);
					   ledger.setPath("ledger_accounts/"+value);
					  }

					 }
					 ledgerNews.add(ledger);
			          System.out.println("la ledger "+ledger);
			    }
			  
			  
			  List<LedgerAccounts> ledgerList = new ArrayList<LedgerAccounts>();
		        for(int k=0; k<ledgerNews.size(); k++) {
		        	 LedgerAccounts ledger=new LedgerAccounts();
				String requestMethod = req.getParameter("request_method").toUpperCase();
				String endpoint = MethodShared.Config().getUsurl() + ledgerNews.get(k).getPath();
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
			                if(k2.equals("displayed_as")) {
			                	ledger.setName(jsonResponse1.getString(k2));
			                	ledger.setDisplay_name(jsonResponse1.getString(k2));
			                }else if(k2.equals("ledger_account_group")) {
			                	String categorie_group="";
			                	String Data=jsonResponse1.getString(k2); // reading the string value 
			                	if(Data!=null)
			                	{
			                	//JSONObject json = (JSONObject) new JSONParser().parse(Data);
			                	//JSONObject convertedObject = new Gson().fromJson(Data, JSONObject.class);
			                	JSONObject convertedObject = new JSONObject(Data);
			                	 categorie_group=(String) convertedObject.getString("id");
			                	}
			                	ledger.setCategory_group(categorie_group);
			                
			                }else if(k2.equals("id")) {
			                	ledger.setId(jsonResponse1.getString(k2));
			                }else if(k2.equals("nominal_code")) {
			                	ledger.setNominal_code(jsonResponse1.getString(k2));
			                }else if(k2.equals("ledger_account_type")) {
			                	String cat="";
			                	String Data=jsonResponse1.getString(k2); // reading the string value 
			                	if(Data!=null)
			                	{
			                	//JSONObject convertedObject = new Gson().fromJson(Data, JSONObject.class);
			                	JSONObject convertedObject = new JSONObject(Data);
			                	 cat=(String) convertedObject.getString("id");
			                	}
			                	ledger.setCategory(cat);
			                }else if(k2.equals("included_in_chart")) {
			                	ledger.setIncluded_in_chart(Boolean.parseBoolean(jsonResponse1.getString(k2)));
			                }
						else if(k2.equals("visible_in_banking")) {
		                	ledger.setVisible_in_banking(Boolean.parseBoolean(jsonResponse1.getString(k2)));
		                }
			                else if(k2.equals("updated_at")) 
			               {
			                	String aTime = jsonResponse1.getString(k2);
			                	ledger.setUpdated_at(aTime);  
			                }
						
					}
					}
					    ledgerList.add(ledger);
						System.out.println("taxe  new   "+ledger.toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
		        }
			for(int q=0; q<ledgerList.size(); q++) 
			{
			 // System.out.println("resultat de création "+createledger(ledgerList.get(q)));
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
	 public JSONObject getAllInvoice() throws KeyManagementException, NoSuchAlgorithmException, ClientProtocolException, IOException, JSONException
	 {          MethodShared = new MethodsShared();
		 		DocumentModel doc = null;
		     	Gson gson = new Gson();
			 	String token = Constant.access_token;		
				Client client = createClient(InvoiceSage.class);
				String callbackUrl = MethodShared.Config().getUrl();
						String[] chaine=callbackUrl.split("/");
	   					String nouveauchaine=chaine[0]+"/"+chaine[1]+"/"+chaine[2]+"/"+chaine[3];
				WebResource webResource = client.resource(nouveauchaine+ Constant.PATH_START_INVOICE2 + "/liste");
				Builder builder = webResource
						.accept(MediaType.APPLICATION_JSON)
						.header("content-type", MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + token);
				ClientResponse response = builder.get(ClientResponse.class);
					log.error(" === < Status create get invoices : " + response.getStatus() + " > ===");
				log.error(" === < Status get invoices  2 : " + response.toString() + " > ===");
				if(response != null) {
					if(response.getStatus() == 200){
						 System.out.println("le json est "+response);
						 //String jsonString = EntityUtils.toString(response.getEntity(c));
						  System.out.println(">>>>>>>>>>>>le json est <<<<<<<");
						  String jsonInvoice = response.getEntity(String.class);
						  //ObjectMapper mapper = new ObjectMapper();
						 // ListInvoice weather = mapper.readValue(json, ListInvoice.class);
						  JSONObject body = new JSONObject(jsonInvoice);;
						  //ListInvoice ronaldo = new ObjectMapper().readValue(jsonInvoice, ListInvoice.class);
						  System.out.println("le json est "+body);
						 //ObjectMapper objectMapper = new ObjectMapper(); 
						// objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
						 //InvoiceSage c = objectMapper.readValue(json.toString(), InvoiceSage.class);

					 //JSONArray json = response.getEntity(JSONObject.class).getJSONArray("InvoiceSage");
						  System.out.println("le json est "+body);
				     return body;
					}
					else if(response.getStatus()==500){
						
						return null;
						
					}
					
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
