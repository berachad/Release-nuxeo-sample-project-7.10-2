package org.nuxeo.project.sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.operations.blob.ConcatenatePDFs;
import org.nuxeo.ecm.automation.core.util.BlobList;
import org.nuxeo.ecm.automation.jsf.operations.AddInfoMessage;
import org.nuxeo.ecm.automation.jsf.operations.AddMessage;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.webapp.clipboard.ClipboardActionsBean;
import org.nuxeo.project.sample.complementary.Constant;
import org.nuxeo.project.sample.services.DisplayInfoOrException;
import org.nuxeo.project.sample.services.MethodsShared;
import org.nuxeo.template.api.adapters.TemplateBasedDocument;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfBorderArray;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

//Il permet de traiter la rendition de l'accusé de réception associé au facture et la conversion et ajouter le code à barre au accusé de réception et faire la concaténation de l'accusé de réception avec la facture
@Operation(id = ConcatenationFactureBC.ID, category = Constants.CAT_DOCUMENT, label = "Concatenation Accusé de réception et facture")
public class ConcatenationFactureBC {

	@Context
	protected CoreSession session;
	
	@Context
	protected AutomationService service;

	@Context
	protected OperationContext ctx = new OperationContext(session);
	
	private static DisplayInfoOrException displayInfoOrException = new DisplayInfoOrException();	
	private static Log log = LogFactory.getLog(ConcatenationFactureBC.class);
	public static final String ID = "Document.ConcatenationFactureBC";
	private Blob blobARTemplatePdf;
	private String path = "";
	@Inject
	private ClipboardActionsBean clipboardActionsBean = new ClipboardActionsBean();
	private Date date = new Date();
	private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
	private MethodsShared method;
	private String globaleWriteStream, invoiceBlobNull;
	private String chainExistenceRGAs = "";
	private List<File> fs = new ArrayList<File>();
	private Date dateZipName = new Date();
	private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
    
	@OperationMethod
	public void run() throws OperationException, IOException 
	{

		DocumentModelList docs = (DocumentModelList) service.run(ctx, "Seam.GetSelectedDocuments");

		if (!docs.isEmpty()) 
		{
			    method = new MethodsShared(session);
				log.error(Constant.INFO_DASHES);
				log.error("[INFO] =====< " + docs.size() + " Document(s) Selected >=====");
				log.error(Constant.INFO_DASHES);

				exportWorklistAsZip(docs);
				
		} else {
				//log.error(Constant.INFO_DASHES);
				log.error("[INFO] =====< No Document Selected >=====");
				//log.error(Constant.INFO_DASHES);
		}
	}
	private void exportWorklistAsZip(List<DocumentModel> docs) throws OperationException, IOException 
	{
		
		new File(Constant.PATH_FILES).mkdir();
		invoiceBlobNull = "";
		for (int i = 0; i < docs.size(); i++) 
		{	BlobList blobs = new BlobList();
		
			if (docs.get(i).getType().equals(Constant.FACTURE)) 
			{
				Blob fileContent = (Blob) docs.get(i).getPropertyValue(Constant.FILE_CONTENT);
				String invoiceNum = (String) docs.get(i).getProperty(Constant.FACTURE, Constant.NUM_FACTURE);
				String bcnum = (String) docs.get(i).getProperty(Constant.FACTURE, Constant.Num_BC);
				String BLnum = (String) docs.get(i).getProperty(Constant.FACTURE, "NumeroBL");
				String Reglementnum = (String) docs.get(i).getProperty(Constant.FACTURE, "Numreglement");
				
				DocumentModel docBL=method.getBlByNumBL(BLnum,session,service);
				DocumentModel docPO=method.getBCByNumBC(bcnum,session,ctx,service);
				DocumentModel docReglement=method.getRegelementByNum(Reglementnum,session,ctx,service);
				DocumentModelList docBc=method.getBCByInvoiceNum(invoiceNum);
				ArrayList<String> listnumBCsByNumInvoice =(ArrayList) ListeBC(invoiceNum);
				
				
				if(fileContent != null) 
				{
					
					if(docBL!=null)
					{
						Blob blobBL = (Blob) docBL.getPropertyValue(Constant.FILE_CONTENT);
						blobs.add(blobBL);
					}
					if(docPO!=null)
					{
						Blob blobBC = (Blob) docPO.getPropertyValue(Constant.FILE_CONTENT);
						blobs.add(blobBC);
					}
					if(docBc!=null)
					{
						addBCToBlobs(invoiceNum, blobs);
					}
					if(docReglement!=null)
					{
						System.out.println(" num Reglement "+Reglementnum);
						Blob blobReglement = (Blob) docReglement.getPropertyValue(Constant.FILE_CONTENT);
						blobs.add(blobReglement);
						System.out.println(" num Reglement >>>>>>>>> "+Reglementnum);
						
					}
					for (int j = 0; j < listnumBCsByNumInvoice.size(); j++) 
					{
						System.out.println(" num Bc "+listnumBCsByNumInvoice.get(j));
						addBLToBlobs(listnumBCsByNumInvoice.get(j), blobs);
					}
					ctx.setInput(blobs);
					ctx.put("blobToAppend", fileContent);
					Map<String, Object> params = new HashMap<>();
					params.put("filename",invoiceNum + ".pdf");
					params.put("blob_to_append", "blobToAppend");
					Blob blobMix = (Blob) service.run(ctx, ConcatenatePDFs.ID, params);
					blobMix.setMimeType(Constant.MIME_TYPE_PDF);
					//ut.addSource(blobMix.getFile().getPath());
					docs.get(i).setPropertyValue(Constant.FILE_CONTENT, (Serializable) blobMix);
					fs.add(blobMix.getFile());
				}else
				{
					invoiceBlobNull = invoiceBlobNull.concat(", " + invoiceNum);
				}
				
			
				
			}
			
		}
		if(StringUtils.isEmpty(invoiceBlobNull)) 
		{
			
			
			String exportName = "Liasse-Client_" + format.format(dateZipName);
			method.downloadZip(fs, exportName);
			new File(Constant.PATH_FILES + "/" + exportName +".zip").delete();
			displayInfoOrException.sendMessage(ctx, service, AddInfoMessage.ID, "L'opération est réussi");
				
		}
	  else 
	  {
		//si les factures ne sont pas attachés
		displayInfoOrException.sendMessage(ctx, service, AddMessage.ID, "Ces factures n'ont pas des fichiers attachés : " + invoiceBlobNull + ".");
	  }
		
			
				
		
	}
	private void addBLToBlobs(String boncommandenum, BlobList blobs) throws IOException 
	{
		DocumentModelList docs = method.getBLByNumBC(boncommandenum);
		if(docs!=null)
		{
		 for (int j = 0; j < docs.size(); j++)
		 {
			Blob blob = (Blob) docs.get(j).getPropertyValue(Constant.FILE_CONTENT);
				if (blob != null) {
						blobs.add(blob);
				} 
		 }
		}
		 
	}
	private void addBCToBlobs(String numinvoice, BlobList blobs) throws IOException 
	{ 

		List<DocumentModel> docs = method.getBCByInvoiceNum(numinvoice);
		if(docs!=null)
		{
		for (int j = 0; j < docs.size(); j++) {
			
			Blob blob = (Blob) docs.get(j).getPropertyValue(Constant.FILE_CONTENT);
				if (blob != null) {
						blobs.add(blob);
				} 
		}
		}
	}
	private List<String> ListeBC(String InvoiceNum) throws IOException 
	{
		
		List<String> numBCs = new ArrayList<>();
		DocumentModelList docs = method.getBCByInvoiceNum(InvoiceNum);
		if(docs!=null)
		{
		for (int j = 0; j < docs.size(); j++) 
		{
		   String numBC = (String) docs.get(j).getPropertyValue(Constant.num_commande);
		   numBCs.add(numBC);
		}
		}
		return numBCs;
	}
	
}
