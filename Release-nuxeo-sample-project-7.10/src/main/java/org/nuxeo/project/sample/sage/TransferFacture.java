package org.nuxeo.project.sample.sage;

import java.util.HashMap;

import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import javax.faces.context.FacesContext;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;

import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.PropertyException;
import org.nuxeo.project.sample.beans.InvoiceSage;
import org.nuxeo.project.sample.beans.LedgerAccounts;
import org.nuxeo.project.sample.beans.Taxe;
import org.nuxeo.project.sample.beans.Vendor;
import org.nuxeo.project.sample.complementary.Constant;
import org.nuxeo.project.sample.services.DisplayInfoOrException;
import org.nuxeo.project.sample.services.MethodsShared;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.star.security.CertificateException;

import org.nuxeo.ecm.automation.core.util.DocumentHelper;
import org.nuxeo.ecm.automation.jsf.operations.AddMessage;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;

@Operation(id = TransferFacture.ID, category = Constants.CAT_DOCUMENT, label = "Print Info")
public class TransferFacture implements Filter ,Serializable{

	@Context
	protected CoreSession session;
	private final Log log = LogFactory.getLog(TransferFacture.class);
	private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); 
	private Date date = new Date();
	@Context
	protected AutomationService service;

	private String currentUser;
	@Context
	protected transient UserManager userManager;
	@Context
	protected OperationContext ctx = new OperationContext(session);

	 private static final long serialVersionUID = 1L;
    public String idVendor;
	public static final String ID = "Document.TransferFacture";
	private HttpSession session2=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();
	private MethodsShared ms = new MethodsShared();
	ArrayList<LedgerId> idsLeger=new ArrayList<LedgerId>();
	ArrayList<LedgerId>Vendors=new ArrayList<LedgerId>();
		
	@OperationMethod
	public void run() throws OperationException, ServletException, IOException, KeyManagementException, NoSuchAlgorithmException, PropertyException, JSONException, ParseException 
	{	//String userName = (String) getCurrentUserModel().getPropertyValue("user:username");
	    log.error("login to  "+session2.getAttribute("currentUser"));
	    String login=session2.getAttribute("currentUser").toString();
		if((String)session2.getAttribute("sageToken")!=null && (String)session2.getAttribute("code")!=null)
		{
			 if(ms.verifyExpirationAccessToken(login, session2.getAttribute("sageToken").toString()).equals("your access token is valid"))
			 {
    	  DocumentModelList docs = (DocumentModelList) this.service.run(this.ctx, "Seam.GetSelectedDocuments");
		  if (!docs.isEmpty()) 
		  {
				log.error(Constant.INFO_DASHES);
				log.error("[INFO] =====< " + docs.size() + " Document(s) Selected >=====");
				try {
					Transfererfacturesselectionnee(docs);
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  } else 
		  {
				log.error(Constant.INFO_DASHES);
				log.error("[INFO] =====< No Document Selected >=====");
				log.error(Constant.INFO_DASHES);
				displayInfoOrException.sendMessage(ctx, service, AddMessage.ID, "Veuillez sélectionner au moins un document.");
		 } 
		}else 
		  {
			displayInfoOrException.sendMessage(ctx, service, AddMessage.ID,ms.verifyExpirationAccessToken(login, session2.getAttribute("sageToken").toString()).toString());
			
		  }
		}
		else 
		{
		     log.error("[INFO] =====< No connecxion found >=====");
			 displayInfoOrException.sendMessage(ctx, service, AddMessage.ID, "Veuillez connecter au sageOne .");
			
		}
	}
	
	 private void Transfererfacturesselectionnee(DocumentModelList docs) throws OperationException, URISyntaxException, ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException, PropertyException, JSONException 
	 {   
		 File f = new File("rapport transfer_"+formatter.format(date)+".csv");
		 FileOutputStream is = new FileOutputStream(f);
		 OutputStreamWriter osw = new OutputStreamWriter(is);
		 BufferedWriter w = new BufferedWriter(osw);
		 w.write("Numéro facture ; Transférer ; Cause;\r\n");
		 VendorsLedgers(docs);
		 VerifySage(Vendors);
		 for(DocumentModel doc:docs) 
		 { 
			if(doc.getPropertyValue("FF1:transfer_state").equals("non transferer"))
			{  
				List<Map<String, Serializable>> products = (List<Map<String, Serializable>>) doc.getPropertyValue("FF1:produit");
				 	Calendar dateexpiration = (Calendar) doc.getPropertyValue("FF1:date_echeance");
				 	Calendar date = (Calendar) doc.getPropertyValue("FF1:DateFacture");
				 	String idNewvendure=fetchidsageVendors(doc.getPropertyValue("FF1:NomFournisseur").toString());
				 	System.out.println("la date est       "+date.getTime());
				 	String invoice="{\"purchase_invoice\":{"; 
				 	invoice=invoice+"\"contact_id\":\""+fetchidsageVendors(doc.getPropertyValue("FF1:NomFournisseur").toString())+"\",";
				 	invoice=invoice+"\"date\":\""+date.getTime()+"\",";
				 	invoice=invoice+"\"due_date\":\""+dateexpiration.getTime()+"\",";
				 	invoice=invoice+"\"contact_name\":\""+doc.getPropertyValue("FF1:NomFournisseur")+"\",";
				   invoice=invoice+"\"invoice_lines\":[";
				   invoice=invoice+"{\"description\":\""+"Description général"+"\",";
				   invoice=invoice+"\"ledger_account_id\":\""+doc.getPropertyValue("FF1:naturecomptable")+"\",";
				   invoice=invoice+"\"quantity\":\""+"0"+"\",";
				   invoice=invoice+"\"tax_rate_id\":\"CA_ZERO\",";
				   invoice=invoice+"\"unit_price\":\""+"0"+"\",";
				   invoice=invoice+"\"tax_amount\":\""+"0"+"\"},";
				   if(!products.isEmpty())
			       {
			      for(int j=0; j<products.size(); j++) 
					{
					       invoice=invoice+"{\"description\":\""+products.get(j).get("description")+"\",";
						   invoice=invoice+"\"ledger_account_id\":\""+products.get(j).get("ledger").toString()+"\",";
						   invoice=invoice+"\"quantity\":\""+products.get(j).get("quantite")+"\",";
						   invoice=invoice+"\"tax_rate_id\":\""+products.get(j).get("taxe")+"\",";
						   invoice=invoice+"\"unit_price\":\""+products.get(j).get("montant")+"\",";
						   invoice=invoice+"\"tax_amount\":\""+products.get(j).get("taux")+"\"";
						   //invoice=invoice+"\"discount_amount\":\""+(Double.parseDouble(products.get(j).get("taux").toString())*Double.parseDouble(products.get(j).get("montant").toString()))+"\"";
						    invoice=invoice+"},"; 
					 }
			      
			         
			    }
//				else 
//				{ 	   invoice=invoice+"\"invoice_lines\":[";
//					   invoice=invoice+"{\"description\":\""+"item 1"+"\",";
//					   invoice=invoice+"\"ledger_account_id\":\""+ms.Ledger(session).getPropertyValue("ledger_account:code")+"\",";
//					   invoice=invoice+"\"quantity\":\""+"0"+"\",";
//					   invoice=invoice+"\"tax_rate_id\":\"CA_ZERO\",";
//					   invoice=invoice+"\"unit_price\":\""+"0"+"\",";
//					   invoice=invoice+"\"tax_amount\":\""+"0"+"\"";
//					   //invoice=invoice+"\"discount_amount\":\""+(Double.parseDouble(products.get(j).get("taux").toString())*Double.parseDouble(products.get(j).get("montant").toString()))+"\"";
//					   //invoice=invoice+"},"; 
//					   invoice=invoice+",\"discount_amount\":\""+doc.getPropertyValue("FF1:TotalHT").toString()+"\""+"}]}}";
//				
//				}
				   
				 invoice=invoice.substring(0, invoice.length()-2);
				 invoice=invoice+",\"discount_amount\":\""+doc.getPropertyValue("FF1:TotalHT").toString()+"\""+"}]}}";
			     String endpoint = ms.Config().getUsurl() +"purchase_invoices";
				 System.out.println("Invoice 2 >>>>>>>>>>>>>>>>>"+invoice);
				 URIBuilder builder = new URIBuilder(endpoint);
				 URI uri = builder.build();
				 StringEntity entity = new StringEntity(invoice);
				 HttpRequestBase request;
				 request = new HttpPost(uri);
				 setRequestHeaders((String)session2.getAttribute("sageToken"),request);
				 ((HttpPost) request).setEntity(entity);
				 HttpClient   httpClient    = HttpClientBuilder.create().build();
				 HttpResponse response = httpClient.execute(request);
				 log.error("[INFO] =====<"+response.toString()+" >=====");
				 log.error("[INFO] =====<"+response.getStatusLine().toString()+" >=====");
					String callbackUrl = ms.Config().getUrl();
				 if(response.getStatusLine().toString().indexOf("201")!= -1)
				 {   
					 if(!idNewvendure.equals(doc.getPropertyValue("FF1:CodeFournisseur").toString()))
					 {
						doc.setPropertyValue("FF1:CodeFournisseur", idNewvendure);	
						session.saveDocument(doc);
						session.save();
					 }
					 String[] chaine=callbackUrl.split("/");
					 String tokentransfer = Constant.access_token;
					 log.error("[INFO] =====< token "+tokentransfer+" >=====");
					 String nouveauchaine=chaine[0]+"/"+chaine[1]+"/"+chaine[2]+"/"+chaine[3];
					 Client client1 = createClient(InvoiceSage.class);
					 WebResource webResource2 = client1.resource(nouveauchaine + Constant.PATH_START_INVOICE2 + "/updateInvoice");
					 Builder builder3 = webResource2
							.accept(MediaType.APPLICATION_JSON)
							.header("content-type", MediaType.APPLICATION_JSON)
							.header("Authorization", "Bearer " + tokentransfer);
					 ClientResponse responseTRANSFER = builder3.put(ClientResponse.class,doc.getName());  
					 log.error(" === < Status update invoice : " + responseTRANSFER.getStatus() + " > ===");
					 log.error(" === < Status update invoice : " + responseTRANSFER.toString() + " > ===");	 
					
					 w.write(doc.getPropertyValue("FF1:NumeroFacture")+";Oui;Aucun;\n");
				 }
				 else
				 {
					 log.error(" === non transférer");
						
					 LedgersFound(doc,w);
					 log.error(" ===  log non transférer");
						
					 
					 //w.write(doc.getPropertyValue("FF1:CodeFournisseur")+";Non;"+response.getStatusLine().toString()+";\n");
							 
				 }
				 
				 }
			idsLeger.clear();
		 }	
		     
			displayInfoOrException.sendMessage(ctx, service, AddMessage.ID, "opération reussi.");
			log.error("createDocument");
			w.close();
			String lienfichier=ms.Config().getLienfichierlog();
            DocumentModel doc2 = session.createDocumentModel(lienfichier, "testDoc", "File");
            Blob blob2 = Blobs.createBlob(f); 
            doc2.setPropertyValue("file:content",(Serializable) blob2);
            doc2.setPropertyValue("dc:title","rapport transfer_"+formatter.format(date)+".csv");
            session.createDocument(doc2);
			 
	 }

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		MethodsShared mes = new MethodsShared();

		((HttpServletResponse)response).sendRedirect(mes.Config().getEndpointCentral() + "?response_type=code&client_id=" + mes.Config().getClientId() + "&redirect_uri=" + mes.Config().getUrl() + "&scope=" + mes.Config().getScope());
	
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
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
	 private void setRequestHeaders(String accessToken, HttpRequestBase request) 
	 {
			request.addHeader("Authorization", "Bearer " + accessToken);
			request.addHeader("Accept", "*/*");
			request.addHeader("Content-Type", "application/json");
	}
	 public boolean verify(String codeVendor,String nomVendor,DocumentModelList listDocs) throws URISyntaxException, ClientProtocolException, IOException, JSONException
	 {      ArrayList<Boolean>listcreate =new ArrayList<Boolean>();
		    MethodsShared MethodShared = new MethodsShared();
		    boolean findvendor=false;
		    boolean findledger=false;
 			String requestMethod = "GET";
			String endpoint = MethodShared.Config().getUsurl() + "contacts?contact_type_id=VENDOR&attributes=name,percentage";
			String params;
			String accessToken = session2.getAttribute("sageToken").toString();
			URIBuilder builder = new URIBuilder(endpoint);
			URI uri = builder.build();
			HttpRequestBase request = requestMethod.equals("GET") ? new HttpGet(uri) : new HttpDelete(uri);
			setRequestHeaders(accessToken,request);
			HttpClient httpclient = HttpClients.createDefault();
			HttpResponse response = httpclient.execute(request);
			List<String> list = new ArrayList<String>();
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
				JSONObject jsonResponse = new JSONObject(parsed);
				JSONArray  jsonArrayResponse = jsonResponse.getJSONArray("$items");
				for (int i = 0; i < jsonArrayResponse.length(); i++) {
		            JSONObject objects = jsonArrayResponse.getJSONObject(i);
		            Iterator<String> key = objects.keys();
		            while (key.hasNext()) 
		                {
		                  String k = key.next().toString();
		                  list.add(objects.getString(k));
		                }
		            }
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
				    }
				  for (int i = 0; i < vendorNews.size(); i++) 
				  {
					  if(vendorNews.get(i).getId().equals(codeVendor))
					  {
						  findvendor=true;
						  break;  
					  }
				  }
				  if(findvendor==false)
				  {
				     //create vendor au sage
				     String vendor ="{ \"contact\": {\"contact_type_ids\": [\"VENDOR\"],\"name\": \""+nomVendor+"\"}}";
				     String endpointvendor = ms.Config().getUsurl() +"contacts";
				     System.out.println("la requete de création vendor est"+vendor);
					 URIBuilder builder2 = new URIBuilder(endpointvendor);
					 URI uri2 = builder2.build();
					 StringEntity entity2 = new StringEntity(vendor);
					 HttpRequestBase request2;
					 request2 = new HttpPost(uri2);
					 setRequestHeaders((String)session2.getAttribute("sageToken"),request2);
					 ((HttpPost) request2).setEntity(entity2);
					 HttpClient   httpClient2    = HttpClientBuilder.create().build();
					 HttpResponse response2 = httpClient2.execute(request2);
					 log.error("[INFO] =====<"+response2.toString()+" >=====");
					 log.error("[INFO] =====<"+response2.getStatusLine().toString()+" >=====");
					 log.error("[INFO] =====<"+response2+" >=====");
					 if(response2.getStatusLine().toString().indexOf("201")!=-1)
					 {
					  HttpEntity entity3 = response2.getEntity();
					    if (entity3 != null) 
					    {
							StringBuilder parsedEntity2 = new StringBuilder(EntityUtils.toString(entity3));
							String resultat = parsedEntity2.toString();
							log.error("[INFO] =====<"+resultat+" >=====");
							JSONObject jsonResponse2 = new JSONObject(resultat);	
							log.error("[INFO] =====jsonResponse2   <"+jsonResponse2+" >=====");
							System.out.println("l'id  de vendorest"+jsonResponse2.get("id"));
							idVendor=jsonResponse2.get("id").toString();
							findvendor=true;
					   }
				    }
			      }
				  }
			     if(listDocs!=null)
			     {
					String address_region_id = MethodShared.Config().getAddress_region_id();
					String address_region_ide="?address_region_id="+address_region_id;
			      	String endpointLedger = MethodShared.Config().getUsurl() + "ledger_accounts"+address_region_ide+"&visible_in=expenses";
				  	URIBuilder builderLedger = new URIBuilder(endpointLedger);
					URI uriLedger = builderLedger.build();
					HttpRequestBase requestLedger= requestMethod.equals("GET") ? new HttpGet(uriLedger) : new HttpDelete(uriLedger);
					setRequestHeaders(accessToken,requestLedger);
					// Make Request
					HttpClient httpclientLedger = HttpClients.createDefault();
					HttpResponse responseLedger = httpclientLedger.execute(requestLedger);
				    List<String> listledger = new ArrayList<String>();
					HttpEntity entityLedger = responseLedger.getEntity();
					if (entityLedger != null) 
					{
						StringBuilder parsedEntityLedger = new StringBuilder(EntityUtils.toString(entityLedger));
						String entLedger = parsedEntityLedger.toString();
					    System.out.println("----------------sage ledger----------------------------"+entLedger);
					    System.out.println("----------------fin sage ledger----------------------------");
						  
						if(entLedger.startsWith("[")) 
						{   parsedEntityLedger.deleteCharAt(0);
							parsedEntityLedger.deleteCharAt(parsedEntityLedger.length() -1);
						}
						String parsed22 = parsedEntityLedger.toString();
						JSONObject jsonResponseLedger = new JSONObject(parsed22);
						JSONArray  jsonArrayResponseLedger = jsonResponseLedger.getJSONArray("$items");
						for (int i = 0; i < jsonArrayResponseLedger.length(); i++)
						  {
				            JSONObject objectsLedger = jsonArrayResponseLedger.getJSONObject(i);
				            Iterator<String> keyLedger = objectsLedger.keys();
				            while (keyLedger.hasNext()) 
				                {
				                  String k = keyLedger.next().toString();
				                  listledger.add(objectsLedger.getString(k));
				                }
				            }
						 List<LedgerAccounts> Ledger = new ArrayList<LedgerAccounts>();
						 JSONArray arrLedger = jsonResponseLedger.getJSONArray("$items");
						  for (int i = 0; i < arrLedger.length(); i++) {
						    	 LedgerAccounts ledger=new LedgerAccounts();
						    	 Iterator iterLedger = arrLedger.getJSONObject(i).keys();
								 while(iterLedger.hasNext()){
								   String keyLedger = (String)iterLedger.next();
								   if(keyLedger.equals("id"))
								   {
								   String value = arrLedger.getJSONObject(i).getString(keyLedger);
								   ledger.setId(value);
							       }

								 }
								 Ledger.add(ledger);
						    }
						  for(int j=0;j<listDocs.size();j++)
						  { 
							  boolean findledgeritem=false;
							  listcreate.add(false);
							  for (int i = 0; i < Ledger.size(); i++) 
							  {     System.out.println("id is sage est"+Ledger.get(i).getId());
							  		System.out.println("id is  get est"+listDocs.get(j).getPropertyValue("ledger_account:code"));
								  if(Ledger.get(i).getId().equals(listDocs.get(j).getPropertyValue("ledger_account:code")))
								  {
								      System.out.println("entrer dans la boucle");
									  findledgeritem=true;
									  listcreate.remove(listcreate.size()-1);
									  listcreate.add(findledgeritem);
									  break;  
								  }
							
							  }
							  if(findledgeritem==false)
							  {
								 
								     String ledgeritem ="{\"ledger_account\": {\"ledger_account_type_id\":\""+listDocs.get(j).getPropertyValue("ledger_account:Category")+"\",\"included_in_chart\":"+listDocs.get(j).getPropertyValue("ledger_account:Included_in_chart")+",\"name\":\""+listDocs.get(j).getPropertyValue("ledger_account:Name")+"\",\"display_name\":\" "+listDocs.get(j).getPropertyValue("ledger_account:Display_name")+"\" , "
								     		+ "\"nominal_code\":"+listDocs.get(j).getPropertyValue("ledger_account:Nominal_code")+"}}";
								     String endpointledger = ms.Config().getUsurl() +"ledger_accounts";
								     System.out.println("la requete de création ledger  est"+ledgeritem);
									 URIBuilder builderledger = new URIBuilder(endpointledger);
									 URI uriledger = builderledger.build();
									 StringEntity entity2 = new StringEntity(ledgeritem);
									 HttpRequestBase request2;
									 request2 = new HttpPost(uriledger);
									 setRequestHeaders((String)session2.getAttribute("sageToken"),request2);
									 ((HttpPost) request2).setEntity(entity2);
									 HttpClient   httpClient2    = HttpClientBuilder.create().build();
									 HttpResponse response2 = httpClient2.execute(request2);
									 log.error("[INFO] =====ledger "+j+" <"+response2.toString()+" >=====");
									 log.error("[INFO] =====ledger "+j+"<"+response2.getStatusLine().toString()+" >=====");
									 log.error("[INFO] =====ledger================== "+j+" <"+response2+" >=====");
									 if(response2.getStatusLine().toString().indexOf("201")!=-1)
									 {
									  HttpEntity entity3 = response2.getEntity();
									    if (entity3 != null) 
									    {
											StringBuilder parsedEntity2 = new StringBuilder(EntityUtils.toString(entity3));
											String resultat = parsedEntity2.toString();
											log.error("[INFO] ===== ledger save "+j+" <"+resultat+" >=====");
											JSONObject jsonResponse2 = new JSONObject(resultat);	
											log.error("[INFO] =====jsonResponse2   <"+jsonResponse2+" >=====");
											System.out.println("l'id ledger  est"+jsonResponse2.get("id"));
											//idVendor=jsonResponse2.get("id").toString();
											
											for(int p=0;p<idsLeger.size();p++)
											{
												if(idsLeger.get(p).getIdged().equals(listDocs.get(j).getPropertyValue("ledger_account:code")))
												{
													idsLeger.get(p).setIdsage(jsonResponse2.get("id").toString());	
												}
											}
											
									   }
									    findledgeritem=true; 
									    listcreate.remove(listcreate.size()-1);
									    listcreate.add(findledgeritem);
								     }
								  
							  }
							  
							}
						  
						 
						
						  }
				
	               }
				
				
				
				
	    if(listcreate.contains(false)|| findvendor == false)		
		 return false;
	    
	    
	    return true;
	 }
	 public String fetchidsageVendors(String idged)
	  {
		 for(int p=0;p<Vendors.size();p++)
			{
				if(Vendors.get(p).getIdged().equals(idged))
				{
					return Vendors.get(p).getIdsage();
				}
			}
		 
		  return null;
	  }
	 public String fetchidsage(String idged)
	  {
		 for(int p=0;p<idsLeger.size();p++)
			{
				if(idsLeger.get(p).getIdged().equals(idged))
				{
					return idsLeger.get(p).getIdsage();
				}
			}
		 
		  return null;
	  }
	DocumentModelList listdocs;
	public void VerifySage(ArrayList<LedgerId> Vendors) throws ClientProtocolException, URISyntaxException, IOException, JSONException
{
	for (LedgerId Vendor : Vendors) 
	{
		if(!ExistVendor(Vendor.getIdged()))
		{
			Vendor.setIdsage(CreateVendor(Vendor.getIdged()));
		}	
	}
	    
	
//	for (LedgerId Ledger : idsLeger) 
//	{
//		if(!ExistLedger(Ledger.getIdged().toString()))
//		{
//			
//		}	
//	}
	
	
}
public boolean VendorsLedgers(DocumentModelList docs)
{       
	    String codes_ledger="";
		for (DocumentModel doc : docs) 
		{   if(doc.getPropertyValue("FF1:transfer_state").equals("non transferer"))
			{
				Vendors.add(new LedgerId(doc.getPropertyValue("FF1:NomFournisseur").toString(),doc.getPropertyValue("FF1:CodeFournisseur").toString()));
//				List<Map<String, Serializable>> products = (List<Map<String, Serializable>>) doc.getPropertyValue("FF1:produit");
//				if(!products.isEmpty())
//				{
//					for(int j=0; j<products.size(); j++) 
//					{
//						codes_ledger =codes_ledger+"'"+products.get(j).get("ledger")+"',";
//						idsLeger.add(new LedgerId(products.get(j).get("ledger").toString(),products.get(j).get("ledger").toString()));
//					}
//			   }
			}
		}
		
	return true;
}
public void LedgersFound(DocumentModel doc,BufferedWriter w) throws PropertyException, IOException, URISyntaxException, JSONException
		{       
			   String codes_ledger="[";
			   String codes_taxe="[";

			      boolean findledger=true;
			   	  boolean findtaxe=true;
				  if(doc.getPropertyValue("FF1:transfer_state").equals("non transferer"))
					{
						List<Map<String, Serializable>> products = (List<Map<String, Serializable>>) doc.getPropertyValue("FF1:produit");
						if(!products.isEmpty())
						{
							for(int j=0; j<products.size(); j++) 
							{
								//codes_ledger =codes_ledger+"'"+products.get(j).get("ledger")+"',";
								if(!ExistLedger(products.get(j).get("ledger").toString()))
								{
								findledger=false;
								codes_ledger =codes_ledger+"'"+products.get(j).get("ledger")+"',";
								}
								if(!ExistTaxe(products.get(j).get("taxe").toString()))
								{
								findtaxe=false;
								codes_taxe =codes_taxe+"'"+products.get(j).get("taxe")+"',";
								}
							}
					     }
					}
		
	 if(codes_ledger.length()>1)
	 {
		 codes_ledger=codes_ledger.substring(0, codes_ledger.length()-1);
		 w.write(doc.getPropertyValue("FF1:NumeroFacture")+";Non;"+codes_ledger+"]des comptes comptables invalides;\r\n");
	 }
	 if(codes_taxe.length()>1)
	 {
		 codes_taxe=codes_taxe.substring(0, codes_taxe.length()-1);
		 w.write(doc.getPropertyValue("FF1:NumeroFacture")+";Non;"+codes_taxe+"]des taxes invalides;\r\n");
	 }
}
public boolean ExistVendor(String nomvendor) throws URISyntaxException, ClientProtocolException, IOException, JSONException
   {      
	    ArrayList<Boolean>listcreate =new ArrayList<Boolean>();
	    MethodsShared MethodShared = new MethodsShared();
	    boolean findvendor=false;
	  	String requestMethod = "GET";
		String endpoint = MethodShared.Config().getUsurl() + "contacts?contact_type_id=VENDOR&attributes=name,percentage";
		String params;
		String accessToken = session2.getAttribute("sageToken").toString();
		URIBuilder builder = new URIBuilder(endpoint);
		URI uri = builder.build();
		HttpRequestBase request = requestMethod.equals("GET") ? new HttpGet(uri) : new HttpDelete(uri);
		setRequestHeaders(accessToken,request);
		HttpClient httpclient = HttpClients.createDefault();
		HttpResponse response = httpclient.execute(request);
		List<String> list = new ArrayList<String>();
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
			JSONObject jsonResponse = new JSONObject(parsed);
			if(jsonResponse.has("$items"))
			{
			JSONArray  jsonArrayResponse = jsonResponse.getJSONArray("$items");
				for (int i = 0; i < jsonArrayResponse.length(); i++) 
				{
					JSONObject objects = jsonArrayResponse.getJSONObject(i);
					Iterator<String> key = objects.keys();
					while (key.hasNext()) 
	                {
	                  String k = key.next().toString();
	                  list.add(objects.getString(k));
	                }
	            }
				List<Vendor> vendorNews = new ArrayList<Vendor>();
				JSONArray arr = jsonResponse.getJSONArray("$items");
					for (int i = 0; i < arr.length(); i++) 
					{
						Vendor vendor=new Vendor();
						Iterator iter = arr.getJSONObject(i).keys();
					 while(iter.hasNext())
					 {
					   String key = (String)iter.next();
					  	if(key.equals("name"))
					  	{
					  		String value = arr.getJSONObject(i).getString(key);
					  		vendor.setName(value);
					  	}
					 }
					 vendorNews.add(vendor);
					}
					for (int i = 0; i < vendorNews.size(); i++) 
					{
						if(vendorNews.get(i).getName().equals(nomvendor))
						{
							findvendor=true;
							break;  
						}
					
			}       }  
		}
	   	return findvendor;
    }

public String CreateVendor(String nomvendor) throws URISyntaxException, ClientProtocolException, IOException, JSONException
   {      	
		     String id="";
		     String vendor ="{ \"contact\": {\"contact_type_ids\": [\"VENDOR\"],\"name\": \""+nomvendor+"\"}}";
		     String endpointvendor = ms.Config().getUsurl() +"contacts";
		     System.out.println("la requete de création vendor est"+vendor);
			 URIBuilder builder2 = new URIBuilder(endpointvendor);
			 URI uri2 = builder2.build();
			 StringEntity entity2 = new StringEntity(vendor);
			 HttpRequestBase request2;
			 request2 = new HttpPost(uri2);
			 setRequestHeaders((String)session2.getAttribute("sageToken"),request2);
			 ((HttpPost) request2).setEntity(entity2);
			 HttpClient   httpClient2    = HttpClientBuilder.create().build();
			 HttpResponse response2 = httpClient2.execute(request2);
			 log.error("[INFO] =====<"+response2.getStatusLine().toString()+" >=====");
			 if(response2.getStatusLine().toString().indexOf("201")!=-1)
			 {
			  HttpEntity entity3 = response2.getEntity();
			    if (entity3 != null) 
			    {
					StringBuilder parsedEntity2 = new StringBuilder(EntityUtils.toString(entity3));
					String resultat = parsedEntity2.toString();
					//log.error("[INFO] =====<"+resultat+" >=====");
					JSONObject jsonResponse2 = new JSONObject(resultat);	
					//log.error("[INFO] =====jsonResponse2   <"+jsonResponse2+" >=====");
					System.out.println("l'id  de vendorest"+jsonResponse2.get("id"));
					id=jsonResponse2.get("id").toString();
					CreateFournisseur(id,nomvendor);
			    }
		     }
	     
	    
	   
	   	return id;
    }
public boolean ExistLedger(String idledger) throws URISyntaxException, ClientProtocolException, IOException, JSONException
{    boolean findledgeritem=false;   
	String address_region_id = ms.Config().getAddress_region_id();
	String address_region_ide="?address_region_id="+address_region_id;
  	String endpointLedger = ms.Config().getUsurl() + "ledger_accounts"+address_region_ide+"&visible_in=expenses";
  	URIBuilder builderLedger = new URIBuilder(endpointLedger);
	URI uriLedger = builderLedger.build();
	String requestMethod = "GET";
	String accessToken = session2.getAttribute("sageToken").toString();
	HttpRequestBase requestLedger= requestMethod.equals("GET") ? new HttpGet(uriLedger) : new HttpDelete(uriLedger);
	setRequestHeaders(accessToken,requestLedger);
	// Make Request
	HttpClient httpclientLedger = HttpClients.createDefault();
	HttpResponse responseLedger = httpclientLedger.execute(requestLedger);
    List<String> listledger = new ArrayList<String>();
	HttpEntity entityLedger = responseLedger.getEntity();
	if (entityLedger != null) 
	{
		StringBuilder parsedEntityLedger = new StringBuilder(EntityUtils.toString(entityLedger));
		String entLedger = parsedEntityLedger.toString();  
		if(entLedger.startsWith("[")) 
		{   parsedEntityLedger.deleteCharAt(0);
			parsedEntityLedger.deleteCharAt(parsedEntityLedger.length() -1);
		}
		String parsed22 = parsedEntityLedger.toString();
		JSONObject jsonResponseLedger = new JSONObject(parsed22);
		JSONArray  jsonArrayResponseLedger = jsonResponseLedger.getJSONArray("$items");
		for (int i = 0; i < jsonArrayResponseLedger.length(); i++)
		  {
            JSONObject objectsLedger = jsonArrayResponseLedger.getJSONObject(i);
            Iterator<String> keyLedger = objectsLedger.keys();
            while (keyLedger.hasNext()) 
                {
                  String k = keyLedger.next().toString();
                  listledger.add(objectsLedger.getString(k));
                }
            }
		 List<LedgerAccounts> Ledger = new ArrayList<LedgerAccounts>();
		 JSONArray arrLedger = jsonResponseLedger.getJSONArray("$items");
		  for (int i = 0; i < arrLedger.length(); i++) 
		  {
		    	 LedgerAccounts ledger=new LedgerAccounts();
		    	 Iterator iterLedger = arrLedger.getJSONObject(i).keys();
				 while(iterLedger.hasNext()){
				   String keyLedger = (String)iterLedger.next();
				   if(keyLedger.equals("id"))
				   {
				   String value = arrLedger.getJSONObject(i).getString(keyLedger);
				   ledger.setId(value);
			       }
				 }
				 Ledger.add(ledger);
		    } 
			 
			  for (int i = 0; i < Ledger.size(); i++) 
			  {    if(Ledger.get(i).getId().equals(idledger))
				  {
				      findledgeritem=true;
					  break;  
				  }
			
			  }
}
	return findledgeritem; 	
}

public boolean ExistTaxe(String idtaxe) throws URISyntaxException, ClientProtocolException, IOException, JSONException
{       String address_region_id = ms.Config().getAddress_region_id();
	    ArrayList<Boolean>listcreate =new ArrayList<Boolean>();
	    MethodsShared MethodShared = new MethodsShared();
	    boolean findvendor=false;
	  	String requestMethod = "GET";
		String endpoint = MethodShared.Config().getUsurl() + "tax_rates?address_region_id="+address_region_id;
		String params;
		String accessToken = session2.getAttribute("sageToken").toString();
		URIBuilder builder = new URIBuilder(endpoint);
		URI uri = builder.build();
		HttpRequestBase request = requestMethod.equals("GET") ? new HttpGet(uri) : new HttpDelete(uri);
		setRequestHeaders(accessToken,request);
		HttpClient httpclient = HttpClients.createDefault();
		HttpResponse response = httpclient.execute(request);
		List<String> list = new ArrayList<String>();
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
			JSONObject jsonResponse = new JSONObject(parsed);
			JSONArray  jsonArrayResponse = jsonResponse.getJSONArray("$items");
				for (int i = 0; i < jsonArrayResponse.length(); i++) 
				{
					JSONObject objects = jsonArrayResponse.getJSONObject(i);
					Iterator<String> key = objects.keys();
					while (key.hasNext()) 
	                {
	                  String k = key.next().toString();
	                  list.add(objects.getString(k));
	                }
	            }
				List<Taxe> taxeNews = new ArrayList<Taxe>();
				JSONArray arr = jsonResponse.getJSONArray("$items");
					for (int i = 0; i < arr.length(); i++) 
					{
						Taxe taxe=new Taxe();
						Iterator iter = arr.getJSONObject(i).keys();
					 while(iter.hasNext())
					 {
					   String key = (String)iter.next();
					  	if(key.equals("id"))
					  	{
					  		String value = arr.getJSONObject(i).getString(key);
					  		taxe.setId(value);
					  	}
					 }
					 taxeNews.add(taxe);
					}
					for (int i = 0; i < taxeNews.size(); i++) 
					{
						if(taxeNews.get(i).getId().equals(idtaxe))
						{
							findvendor=true;
							break;  
						}
					}  
		}
	   	return findvendor;
 }

public boolean CreateLedger(DocumentModel ledger,DocumentModel facture) throws URISyntaxException, ClientProtocolException, IOException, JSONException
{    boolean created=false; 	
	 String ledgeritem ="{\"ledger_account\":{\"ledger_account_type_id\":\""+facture.getPropertyValue("FF1:ledger_typeid")+"\",\"name\":\""+facture.getPropertyValue("FF1:naturecomptable")+"\",\"display_name\":\" "+ledger.getPropertyValue("FF1:naturecomptable")+"\" , "
     		+ "\"nominal_code\":"+ledger.getPropertyValue("FF1:nominale_code")+"}}";
     String endpointledger = ms.Config().getUsurl() +"ledger_accounts";
     System.out.println("la requete de création ledger  est"+ledgeritem);
	 URIBuilder builderledger = new URIBuilder(endpointledger);
	 URI uriledger = builderledger.build();
	 StringEntity entity2 = new StringEntity(ledgeritem);
	 HttpRequestBase request2;
	 request2 = new HttpPost(uriledger);
	 setRequestHeaders((String)session2.getAttribute("sageToken"),request2);
	 ((HttpPost) request2).setEntity(entity2);
	 HttpClient   httpClient2    = HttpClientBuilder.create().build();
	 HttpResponse response2 = httpClient2.execute(request2);
	 log.error("[INFO] =====ledger <"+response2.getStatusLine().toString()+" >=====");
	 if(response2.getStatusLine().toString().indexOf("201")!=-1)
	 {
	  HttpEntity entity3 = response2.getEntity();
	    if (entity3 != null) 
	    {
			StringBuilder parsedEntity2 = new StringBuilder(EntityUtils.toString(entity3));
			String resultat = parsedEntity2.toString();
			JSONObject jsonResponse2 = new JSONObject(resultat);	
			for(int p=0;p<idsLeger.size();p++)
			{
				if(idsLeger.get(p).getIdged().equals(ledger.getPropertyValue("ledger_account:code")))
				{
					idsLeger.get(p).setIdsage(jsonResponse2.get("id").toString());
					
				}
			}
			
	   }
	    created= true;
     }
	 return created;
}
 
public String CreateFournisseur(String idvendeur,String nomVendor) throws URISyntaxException, ClientProtocolException, IOException, JSONException
{   
	DocumentModel doc = null;
	String lienvendor=ms.Config().getLienfournisseur();
	log.error(" === create fournisseur  dans la ged ");
	doc = session.createDocumentModel(lienvendor, idvendeur, "fiche-fournisseur");
    doc.setPropertyValue("dc:title", idvendeur); 
    doc.setPropertyValue("fiche-fournisseur:nom-fournisseur",nomVendor); 
    doc.setPropertyValue("fiche-fournisseur:code-fournisseur", idvendeur); 
    doc.setPropertyValue("fiche-fournisseur:updated_at",Calendar.getInstance().getTime());
	session.createDocument(doc);
	return idvendeur;
}

protected DocumentModel getCurrentUserModel() {
    return userManager.getUserModel(currentUser);
}



//public boolean CreateLedger(documentM) throws URISyntaxException, ClientProtocolException, IOException, JSONException
//{   
//	DocumentModel doc = null;
//	doc = session.createDocumentModel(Constant.PARENT_PATH_VENDOR, idvendeur, "fiche-fournisseur");
//    doc.setPropertyValue("dc:title", idvendeur); 
//    doc.setPropertyValue("fiche-fournisseur:nom-fournisseur",nomVendor); 
//    doc.setPropertyValue("fiche-fournisseur:code-fournisseur", idvendeur); 
//    doc.setPropertyValue("fiche-fournisseur:updated_at",Calendar.getInstance().getTime());
//	session.createDocument(doc);
//	return true;
//}
 }

