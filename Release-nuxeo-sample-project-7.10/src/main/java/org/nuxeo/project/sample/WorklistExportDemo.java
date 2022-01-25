//package org.nuxeo.project.sample;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.io.Serializable;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
// vvvvvvvvvvvvvvvvv
//import javax.inject.Inject;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.nuxeo.ecm.automation.AutomationService;
//import org.nuxeo.ecm.automation.OperationContext;
//import org.nuxeo.ecm.automation.OperationException;
//import org.nuxeo.ecm.automation.core.Constants;
//import org.nuxeo.ecm.automation.core.annotations.Context;
//import org.nuxeo.ecm.automation.core.annotations.Operation;
//import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
//import org.nuxeo.ecm.automation.core.operations.blob.ConcatenatePDFs;
//import org.nuxeo.ecm.automation.core.util.BlobList;
//import org.nuxeo.ecm.automation.jsf.operations.AddInfoMessage;
//import org.nuxeo.ecm.automation.jsf.operations.AddMessage;
//import org.nuxeo.ecm.core.api.Blob;
//import org.nuxeo.ecm.core.api.CoreSession;
//import org.nuxeo.ecm.core.api.DocumentModel;
//import org.nuxeo.ecm.core.api.DocumentModelList;
//import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
//import org.nuxeo.ecm.webapp.clipboard.ClipboardActionsBean;
//import org.nuxeo.project.sample.beans.RGA;
//import org.nuxeo.project.sample.complementary.Constant;
//import org.nuxeo.project.sample.services.DisplayInfoOrException;
//import org.nuxeo.project.sample.services.MethodsShared;
//import org.apache.commons.lang3.StringUtils;
//
//
//@Operation(id = WorklistExportDemo.ID, category = Constants.CAT_DOCUMENT, label = "Worklist Export Demo")
//public class WorklistExportDemo {
//
//	public static final String ID = "Document.WorklistExportDemo";
//	private final Log log = LogFactory.getLog(WorklistExportDemo.class);
//	
//	@Context
//	protected CoreSession session;
//
//	@Context
//	protected AutomationService service;
//
//	@Context
//	protected OperationContext ctx = new OperationContext(session);
//
//	@Inject
//	private ClipboardActionsBean clipboardActionsBean = new ClipboardActionsBean();
//	
//	private DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();
//	private Date date = new Date();
//	private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
//	private MethodsShared method;
//	private String globaleWriteStream, invoiceBlobNull;
//	private String chainExistenceRGAs = "";
//
//	
//	@OperationMethod
//	public void run() throws OperationException, IOException {
//
//		DocumentModelList docs = (DocumentModelList) service.run(ctx, "Seam.GetSelectedDocuments");
//
//		if (!docs.isEmpty()) {
//			method = new MethodsShared(session);
//				//log.error(Constant.INFO_DASHES);
//				//log.error("[INFO] =====< " + docs.size() + " Document(s) Selected >=====");
//				//log.error(Constant.INFO_DASHES);
//
//				exportWorklistAsZip(docs);
//				
//		} else {
//				//log.error(Constant.INFO_DASHES);
//				//log.error("[INFO] =====< No Document Selected >=====");
//				//log.error(Constant.INFO_DASHES);
//		}
//	}
//
//	private void exportWorklistAsZip(List<DocumentModel> docs) throws OperationException, IOException {
//		new File(Constant.PATH_FILES).mkdir();
//		//Contient tous les factures qui n'ont pas attachés avec des fichiers pdf.
//		invoiceBlobNull = "";
//		// Original Files content
//		//BlobList fileContents = new BlobList();
//
//		File f = new File("console.log");
//		FileOutputStream is = new FileOutputStream(f);
//		OutputStreamWriter osw = new OutputStreamWriter(is);
//		BufferedWriter w = new BufferedWriter(osw);
//
//		w.write("Nombre de factures générées : " + docs.size() + "\n\n");
//
//		// Affectation File to log Blob
//
//		//log.error(Constant.INFO_DASHES);
//		//docs c'est une list qui contient tous les factures sélectionnées 
//		for (int i = 0; i < docs.size(); i++) {
//			if (docs.get(i).getType().equals(Constant.FACTURE)) {
//				//fileContent ce blob doit comprte un accusé de réception barré et une facture signé
//				Blob fileContent = (Blob) docs.get(i).getPropertyValue(Constant.FILE_CONTENT);
//				String invoiceNum = (String) docs.get(i).getProperty(Constant.FACTURE, Constant.NUM_FACTURE);
//				String customerCode = (String) docs.get(i).getProperty(Constant.FACTURE, Constant.CODE_CLIENT_FACTURE);
//				String customerName = (String) docs.get(i).getProperty(Constant.FACTURE, Constant.CLIENT_FACTURE);
//				String rga = (String) docs.get(i).getProperty(Constant.FACTURE, Constant.RGA_FACTURE);
//				if(fileContent != null) {
//				
//				//log.error("[INFO] =====< Selected invoice number : " + invoiceNum + ", Customer code : " + customerCode + ", Customer name : " + customerName + Constant.LOG_COMMENT_1);
//
//				w.write("|-- Numéro Facture : " + invoiceNum + ", Code Client : " + customerCode + "\n");
//
//				//load file content of document type invoice which contains : invoice file and AR file
//				
//				//fileContents.add(fileContent);
//				
//				// PDFs to Merge which contains : BLs and BCs
//				BlobList blobs = new BlobList();
//				
//				if((boolean) docs.get(i).getProperty(Constant.FACTURE, Constant.REQUIRED_BL_BC_FACTURE)) {
//					if(method.getFicheCLient(customerCode) != null) {
//					boolean blRequired = method.getFicheCLient(customerCode).isBLRequered();
//					boolean bcRequired = method.getFicheCLient(customerCode).isBCRequered();
//					List<String> listnumBCsByBL = new ArrayList<>();
//						if(blRequired && bcRequired) {
//							//retourne une list des numéro de bon cammande.
//							listnumBCsByBL = addBLToBlobs(invoiceNum, blobs, w, true);
//								for(int k=0; k<listnumBCsByBL.size();k++) {
//									//log.error("[INFO] =====< BC number :" + listnumBCsByBL.get(k) + " >=====");
//									addBCToBlobs(listnumBCsByBL.get(k), blobs, w);
//								}
//						}else if(blRequired && !bcRequired) {
//								addBLToBlobs(invoiceNum, blobs, w, true);
//						}else if(!blRequired && bcRequired) {
//							//blObligatoir = false pour que la list blobs ne puisse pas contenir un bl qui n'est oblogatoir.
//								listnumBCsByBL = addBLToBlobs(invoiceNum, blobs, w, false);
//								for(int k=0; k<listnumBCsByBL.size();k++) {
//									//log.error("[INFO] =====< BC number :" + listnumBCsByBL.get(k) + " >=====");
//									addBCToBlobs(listnumBCsByBL.get(k), blobs, w);
//								}
//						}
//				}
//			}
//				
//				
//				
//				// Concatenate PDFs	
//				//La concaténation de chaque facture avec ces BLs et BCs associés.
//				ctx.setInput(blobs);
//				ctx.put("blobToAppend", fileContent);
//				Map<String, Object> params = new HashMap<>();
//				params.put("filename", rga + "-" + customerCode + "_" + invoiceNum + ".pdf");
//				params.put("blob_to_append", "blobToAppend");
//				Blob blobMix = (Blob) service.run(ctx, ConcatenatePDFs.ID, params);
//				blobMix.setMimeType(Constant.MIME_TYPE_PDF);
//				//ut.addSource(blobMix.getFile().getPath());
//				docs.get(i).setPropertyValue(Constant.FILE_CONTENT, (Serializable) blobMix);
//				//session.saveDocument(docs.get(i));
//				//session.save();
//				
////				if(!listNameRGAs.contains(rga)) {
////					RGA newRga = new RGA();
////					listNameRGAs.add(rga);
////					newRga.setName(rga);
////					newRga.getBlobs().add(blobMix);
////					listRGAs.add(newRga);
////				}else {
////					for(RGA r:listRGAs) {
////						if(r.getName().equalsIgnoreCase(rga)){
////							r.getBlobs().add(blobMix);
////						}
////					}
////				}
//			}else {
//				invoiceBlobNull = invoiceBlobNull.concat(", " + invoiceNum);
//			}
//				if(StringUtils.isEmpty(rga)) {
//					chainExistenceRGAs = chainExistenceRGAs.concat(", " + invoiceNum);
//				}
//			}
//				
//		}
////		for(RGA r:listRGAs) {
////			DocumentModel FileGlobale = null;
////			for(Blob b:r.getBlobs()) {
////				inputPdfList.add(new FileInputStream(b.getFile().getPath()));
////			}
////			 outputStream = new FileOutputStream(Constant.PATH_FILES + "/" + r.getName() + "-File-Globale.pdf");
////			 try {
////					globaleFileMixt = method.mergePdfFiles(inputPdfList, outputStream, r.getName());
////				} catch (Exception e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////			 FileGlobale = session.createDocumentModel("/default-domain/workspaces/BV360/Fichiers", r.getName() + "-File-Globale", "File");
////				
////				FileGlobale.setProperty(Constant.DUBLINCORE, "title", r.getName() + "-File-Globale");
////				Blob blobGBL = Blobs.createBlob(globaleFileMixt);
////				blobGBL.setMimeType(Constant.MIME_TYPE_PDF);
////				FileGlobale.setPropertyValue(Constant.FILE_CONTENT, (Serializable) blobGBL);
////				FileGlobale = session.createDocument(FileGlobale);
////				docs.add(FileGlobale);
////		}
//		 
////		ut.setDestinationFileName(Constant.PATH_FILES + "/File-Globale.pdf");
////		try {
////			ut.mergeDocuments();
////		} catch (COSVisitorException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//		w.close();
//
//		// docs.add(logDocumentModel);
//
//		File frga = new File("globaleRGA.csv");
//		FileOutputStream isrga = new FileOutputStream(frga);
//		OutputStreamWriter oswrga = new OutputStreamWriter(isrga);
//		BufferedWriter wrga = new BufferedWriter(oswrga);
//
//		wrga.write("RGA; Nombre de factures;\n");
//
//		// Globale RGA Log Blob File
//		Blob globaleRGALogBlob = new FileBlob(frga);
//
//
//		f = new File("globale.csv");
//		is = new FileOutputStream(f);
//		osw = new OutputStreamWriter(is);
//		w = new BufferedWriter(osw);
//
//		w.write("Num Facture; Nom Client; Num BL; Fichier joint BL; Exigence BL; Num BC; Fichier joint BC; Exigence BC;\n");
//
//		// Globale Log Blob File
//		Blob globaleLogBlob = new FileBlob(f);
//
//		List<RGA> rgas = new ArrayList<>();
//		for (int i = 0; i < docs.size(); i++) {
//			if (docs.get(i).getType().equals(Constant.FACTURE)) {
//				String nameRGA = (String) docs.get(i).getProperty(Constant.FACTURE, Constant.RGA_FACTURE);
//				RGA rga = getRGAByName(rgas, nameRGA);
//				if (rga != null) {
//					int number = rga.getNumber();
//					rga.setNumber(number + 1);
//				} else {
//					RGA newRGA = new RGA(nameRGA);
//					rgas.add(newRGA);
//				}
//			}
//		}
//
//		//log.error(Constant.INFO_DASHES);
//		for (RGA rga2 : rgas) {
//			wrga.write(rga2.getName() + "; " + rga2.getNumber() + ";\n");
//			//log.error("[INFO] =====< RGA Name : " + rga2.getName() + ", RGA Number : " + rga2.getNumber() + Constant.LOG_COMMENT_1);
//			for (DocumentModel doc : docs) {
//				//lister les factures par rga
//				String nameRGA = (String) doc.getProperty(Constant.FACTURE, Constant.RGA_FACTURE);
//				if (doc.getType().equals(Constant.FACTURE) && rga2.getName().equals(nameRGA)) {
//					String invoiceNum = (String) doc.getProperty(Constant.FACTURE, Constant.NUM_FACTURE);
//					String customerCode = (String) doc.getProperty(Constant.FACTURE, Constant.CODE_CLIENT_FACTURE);
//					String customerName = (String) doc.getProperty(Constant.FACTURE, Constant.CLIENT_FACTURE);
//
//					if((boolean) doc.getProperty(Constant.FACTURE, Constant.REQUIRED_BL_BC_FACTURE)) {
//						if(method.getFicheCLient(customerCode) != null) {
//						boolean blRequired = method.getFicheCLient(customerCode).isBLRequered();
//						boolean bcRequired = method.getFicheCLient(customerCode).isBCRequered();
//						if(blRequired)
//						addBLToGlobaleBlobs(invoiceNum, customerName, bcRequired, w);
//						if(!blRequired && bcRequired)
//							addBCToGlobaleBlobsAndBLNotRequired(invoiceNum, customerName, w);
//					}else {
//						//log.error(Constant.INFO_DASHES);
//						//log.error("Le code fiche client : " + customerCode + "n'existe pas");
//						//log.error(Constant.INFO_DASHES);
//					}
//					}
//				}
//			}
//		}
//		
//		wrga.close();
//		//si les factures sont attachés
//		if(StringUtils.isEmpty(invoiceBlobNull)) {
//			if(StringUtils.isEmpty(chainExistenceRGAs)) {
//				
//		DocumentModel globaleRGALogDocumentModel = session.createDocumentModel("/default-domain/workspaces/BV360/Fichiers", "globaleRGALogDoc", "File");
//		globaleRGALogDocumentModel.setProperty(Constant.DUBLINCORE, "title", "globaleRGALogDoc "+formatter.format(date));
//		globaleRGALogDocumentModel.setPropertyValue(Constant.FILE_CONTENT, (Serializable) globaleRGALogBlob);
//		session.createDocument(globaleRGALogDocumentModel);
//		
//		w.close();
//		
//		DocumentModel globaleLogDocumentModel = session.createDocumentModel("/default-domain/workspaces/BV360/Fichiers", "globaleLogDoc", "File");
//		globaleLogDocumentModel.setProperty(Constant.DUBLINCORE, "title", "globaleLogDoc "+formatter.format(date));
//		globaleLogDocumentModel.setPropertyValue(Constant.FILE_CONTENT, (Serializable) globaleLogBlob);
//		session.createDocument(globaleLogDocumentModel);
//		
//		docs.add(globaleRGALogDocumentModel);
//		docs.add(globaleLogDocumentModel);
//		clipboardActionsBean.exportWorklistAsZip(docs);
////		for(String r: listNameRGAs) {
////	        new File(Constant.PATH_FILES + "/" + r + "-File-Globale.pdf").delete();
////		}
//
//		//setOriginalFileContent(docs, fileContents);
//		//log.error("===== < Finish >=====");
//		//log.error(Constant.INFO_DASHES);
//		displayInfoOrException.sendMessage(ctx, service, AddInfoMessage.ID, "L'opération est réussi");
//		}else {
//			displayInfoOrException.sendMessage(ctx, service, AddMessage.ID, "Factures n'ont pas des rgas : " + chainExistenceRGAs + ".");
//		}
//		}else {
//			//si les factures ne sont pas attachés
//			displayInfoOrException.sendMessage(ctx, service, AddMessage.ID, "Ces factures n'ont pas des fichiers attachés : " + invoiceBlobNull + ".");
//		}
//	}
//
//	private void addBLToGlobaleBlobs(String invoiceNum,String customerName, boolean bcRequired, BufferedWriter w) throws IOException {
//		globaleWriteStream = "";
//		List<DocumentModel> docs = method.getListBLByNumFacture(invoiceNum);
//
//		if(!docs.isEmpty()) {
//		for (int j = 0; j < docs.size(); j++) {
//			boolean blobBLExist = false;
//			globaleWriteStream = "";
//				String numBL = (String) docs.get(j).getProperty(Constant.BON_LIVRAISON, Constant.NUM_BL);
//				String numBCByBL = (String) docs.get(j).getProperty(Constant.DUBLINCORE, Constant.NUM_BC);
//				Blob blob = (Blob) docs.get(j).getPropertyValue(Constant.FILE_CONTENT);
//				if(!StringUtils.isEmpty(numBL)) {
//					if (blob == null) {
//							globaleWriteStream = globaleWriteStream.concat(invoiceNum + "; " + customerName + "; " + numBL + "; Non; Oui");
//							addBCToGlobaleBlobs(numBCByBL, bcRequired, blobBLExist, w);
//					}if(blob != null && bcRequired){
//						blobBLExist = true;
//						globaleWriteStream = globaleWriteStream.concat(invoiceNum + "; " + customerName + "; " + numBL + "; Oui; Oui");
//						addBCToGlobaleBlobs(numBCByBL, bcRequired, blobBLExist, w);
//					}
//				}else {
//					//si le numéro de bl est null
//					w.write(invoiceNum + "; " + customerName + "; Non;; Oui;;;;\n");
//				}
//		}
//		} 
//		else {
//			if(bcRequired) {
//				//Le document BL et BC n'existent pas dans la solution et ils sont obligatoires
//				w.write(invoiceNum + "; " + customerName + "; Non;; Oui; Non;; Oui;\n");
//			}
//			else {
//				//Le document BL et BC n'existent pas dans la solution et Bls obligatoire
//				w.write(invoiceNum + "; " + customerName + "; Non;; Oui;;;;\n");	
//			}
//		}
//	}
//
//	private void addBCToGlobaleBlobs(String numBC, boolean bcRequired, boolean blobBLExist, BufferedWriter w) throws IOException {
//		if(bcRequired) {
//			List<DocumentModel> docs = method.getListBCByNumBC(numBC);
//			if(!docs.isEmpty()) {
//				for (int j = 0; j < docs.size(); j++) {
//					Blob blob = (Blob) docs.get(j).getPropertyValue(Constant.FILE_CONTENT);
//					if (blob == null) {					
//							w.write(globaleWriteStream + "; " + numBC + "; Non; Oui;\n");
//					}else if(!blobBLExist) {
//						w.write(globaleWriteStream + "; " + numBC + "; Oui; Oui;\n");
//					}
//				}
//			}else {//numéro de bc n'existe pas est obligatoir
//					w.write(globaleWriteStream + "; Non;; Oui;\n");
//			}
//		}else {
//			w.write(globaleWriteStream + ";;;;\n");
//		}
//	}
//
//	private void addBCToGlobaleBlobsAndBLNotRequired(String invoiceNum, String customerName, BufferedWriter w) throws IOException {
//		    List<DocumentModel> docsBl = method.getListBLByNumFacture(invoiceNum);
//		    if(!docsBl.isEmpty()) {
//		    for (int i = 0; i < docsBl.size(); i++) {
//		    String numBCByBL = (String) docsBl.get(i).getProperty(Constant.DUBLINCORE, Constant.NUM_BC);
//		    String numBL = (String) docsBl.get(i).getProperty(Constant.BON_LIVRAISON, Constant.NUM_BL);
//		    //Si le numéro de bc null
//		    if(!StringUtils.isEmpty(numBCByBL)) {
//			List<DocumentModel> docs = method.getListBCByNumBC(numBCByBL);
//			if(!docs.isEmpty()) {
//				for (int j = 0; j < docs.size(); j++) {
//					Blob blob = (Blob) docs.get(j).getPropertyValue(Constant.FILE_CONTENT);
//					if (blob == null) {	//numéro de bl existe et bc existe mais le file content de bc null			
//							w.write(invoiceNum + "; " + customerName + "; " + numBL + ";;; " + numBCByBL + "; Non; Oui;\n");
//					}
//				}
//			}else {//numéro de bl existe mais numéro de bc n'existe pas 
//					w.write(invoiceNum + "; " + customerName + "; " + numBL + ";;; Non;; Oui;\n");
//			}
//		}else {//numéro de bl existe mais numéro de bc n'existe pas 
//					w.write(invoiceNum + "; " + customerName + "; " + numBL + ";;; Non;; Oui;\n");
//				}
//		    }
//		    }else {
//		    	//Bls n'existent pas et ils ne sont pas obligatoires et les BCs obligatoires
//		    	w.write(invoiceNum + "; " + customerName + "; Non;;; Non;; Oui;\n");
//		    }
//	}
//	//insére les fichier attachés au bon de livraison dans le variable blobs et remplit la list numBCs par les numéros de bons commande depuis le document bon livraison
//	private List<String> addBLToBlobs(String InvoiceNum, BlobList blobs, BufferedWriter w, boolean bLObligatoire) throws IOException {
//		
//		List<String> numBCs = new ArrayList<>();
//		DocumentModelList docs = method.getListBLByNumFacture(InvoiceNum);
//
//		for (int j = 0; j < docs.size(); j++) {
//			String customerCode = (String) docs.get(j).getProperty(Constant.BON_LIVRAISON, Constant.CODE_CLIENT_BL);
//			String numBL = (String) docs.get(j).getProperty(Constant.BON_LIVRAISON, Constant.NUM_BL);
//			String numBC = (String) docs.get(j).getProperty(Constant.DUBLINCORE, Constant.NUM_BC);
//			
//			//log.error("[INFO] =====< BL number :" + numBL + " , Customer code : " + customerCode + " >=====");
//			numBCs.add(numBC);
//			
//			Blob blob = (Blob) docs.get(j).getPropertyValue(Constant.FILE_CONTENT);
//			if (blob != null && bLObligatoire) {
//					w.write("|	|-- Numéro Bl : " + numBL + "\n");
//					blobs.add(blob);
//			} else {
//					w.write("|	|-- Numéro Bl : " + numBL + " ( BL est Obligatoire )\n");
//			}
//		}
//		return numBCs;
//	}
//
//	private void addBCToBlobs(String numBC, BlobList blobs, BufferedWriter w) throws IOException {
//
//		List<DocumentModel> docs = method.getListBCByNumBC(numBC);
//
//		for (int j = 0; j < docs.size(); j++) {
//			Blob blob = (Blob) docs.get(j).getPropertyValue(Constant.FILE_CONTENT);
//				if (blob != null) {
//						w.write("|	|-- Numéro BC : " + numBC + "\n");
//						blobs.add(blob);
//				} else {
//						w.write("|	|-- Numéro BC : " + numBC + " ( BC est Obligatoire )\n");
//				}
//		}
//	}
//
////	private void setOriginalFileContent(List<DocumentModel> docs, BlobList fileContents) {
////		for (int i = 0; i < docs.size(); i++) {
////			if (docs.get(i).getType().equals(Constant.FACTURE)) {
////				docs.get(i).setPropertyValue(Constant.FILE_CONTENT, (Serializable) fileContents.get(i));
////				session.saveDocument(docs.get(i));
////				session.save();
////			}
////		}
////	}
//
//	private RGA getRGAByName(List<RGA> rgas, String name) {
//		for (RGA rga : rgas) {
//			if(rga != null && !StringUtils.isEmpty(name)) {
//				if (name.equals(rga.getName())) {
//					return rga;
//				}
//			}
//		}
//		return null;
//	}
//
//}
