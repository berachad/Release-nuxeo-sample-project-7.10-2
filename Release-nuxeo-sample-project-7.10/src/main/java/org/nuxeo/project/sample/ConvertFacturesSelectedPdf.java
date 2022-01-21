/*     */ package org.nuxeo.project.sample;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.nuxeo.ecm.automation.AutomationService;
/*     */ import org.nuxeo.ecm.automation.OperationContext;
/*     */ import org.nuxeo.ecm.automation.OperationException;
/*     */ import org.nuxeo.ecm.automation.core.annotations.Context;
/*     */ import org.nuxeo.ecm.automation.core.annotations.Operation;
/*     */ import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
/*     */ import org.nuxeo.ecm.core.api.Blob;
/*     */ import org.nuxeo.ecm.core.api.Blobs;
/*     */ import org.nuxeo.ecm.core.api.CoreSession;
/*     */ import org.nuxeo.ecm.core.api.DocumentModel;
/*     */ import org.nuxeo.ecm.core.api.DocumentModelList;
/*     */ import org.nuxeo.template.api.adapters.TemplateBasedDocument;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Operation(id = "Document.ConvertFacturesSelectedPdf", category = "Document", label = "convert pdf")
/*     */ public class ConvertFacturesSelectedPdf
/*     */ {
/*     */   @Context
/*     */   protected CoreSession session;
/*     */   @Context
/*     */   protected AutomationService service;
/*     */   @Context
/*  32 */   protected OperationContext ctx = new OperationContext(this.session);
/*     */ 
/*     */   
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public static final String ID = "Document.ConvertFacturesSelectedPdf";
/*     */   
/*  39 */   private final Log log = LogFactory.getLog(ConvertFacturesSelectedPdf.class);
/*     */   
/*     */   @OperationMethod
/*     */   public void run() throws OperationException {
/*  43 */     File fileDirectory = new File("../TemplatesPdf");
/*  44 */     fileDirectory.mkdir();
/*  45 */     DocumentModelList docs = (DocumentModelList)this.service.run(this.ctx, "Seam.GetSelectedDocuments");
/*  46 */     System.out.println(((DocumentModel)docs.get(0)).getType());
/*  47 */     if (((DocumentModel)docs.get(0)).getType().equals("Contrat")) {
/*  48 */       System.out.println(((DocumentModel)docs.get(0)).getType());
/*  49 */       String templateName = "Template Contrat";
/*  50 */       System.out.println(templateName);
/*  51 */       convert(docs, templateName);
/*  52 */     } else if (((DocumentModel)docs.get(0)).getType().equals("attest_travail")) {
/*  53 */       String templateName = "Template Attestation";
/*  54 */       System.out.println(((DocumentModel)docs.get(0)).getType());
/*  55 */       System.out.println("Template" + templateName);
/*  56 */       convert(docs, templateName);
/*  57 */     } else if (((DocumentModel)docs.get(0)).getType().equals("Bon_Commande")) {
/*  58 */       String templateName = "Template Bon Commande";
/*  59 */       System.out.println(((DocumentModel)docs.get(0)).getType());
/*  60 */       System.out.println("Template" + templateName);
/*  61 */       convert(docs, templateName);
/*     */     } 
				else if (((DocumentModel)docs.get(0)).getType().equals("da")) {
/*  58 */       String templateName = "Template Demande d'achat";
/*  59 */       System.out.println(((DocumentModel)docs.get(0)).getType());
/*  60 */       System.out.println("Template" + templateName);
/*  61 */       convert(docs, templateName);
/*     */     } 

/*     */   }
/*     */   
/*     */   public File readDirectory(File file, String fileName) {
/*  66 */     File filePdf = new File("Initial.pdf");
/*  67 */     if (file.isDirectory()) {
/*  68 */       File[] folder = file.listFiles();
/*  69 */       for (File f : folder) {
/*  70 */         readDirectory(f, fileName);
/*     */       }
/*     */     } else {
/*     */       
/*  74 */       System.out.println(file.getName());
/*     */       try {
/*  76 */         DOCXtoPDFconverter dOCXtoPDFconverter = new DOCXtoPDFconverter();
/*  77 */         filePdf = dOCXtoPDFconverter.parseDocx(file.getPath(), fileName);
/*  78 */       } catch (IOException e) {
/*  79 */         System.err.println("Error: " + e.getMessage());
/*     */       } 
/*     */     } 
/*     */     
/*  83 */     return filePdf;
/*     */   }
/*     */   public void convert(DocumentModelList docs, String templateName) {
/*  86 */     for (DocumentModel doc : docs) {
/*  87 */       TemplateBasedDocument tbd = (TemplateBasedDocument)doc.getAdapter(TemplateBasedDocument.class);
/*  88 */       if (tbd == null) {
/*  89 */         System.out.println("TemplateBasedDocument is null");
/*     */       }
/*  91 */       Blob rendition = tbd.renderWithTemplate(templateName);
/*  92 */       File filePdf = readDirectory(rendition.getFile(), rendition.getFilename());
/*     */       
/*     */       try {
/*  95 */         Blob blob = Blobs.createBlob(filePdf);
/*  96 */         blob.setFilename(filePdf.getName());
/*  97 */         doc.setProperty("file", "content", blob);
/*  98 */         this.session.saveDocument(doc);
/*  99 */         this.session.save();
/* 100 */       } catch (IOException e) {
/*     */         
/* 102 */         e.printStackTrace();
/*     */       } 
/*     */       
/* 105 */       System.out.println("...." + filePdf.getName());
/* 106 */       System.out.println("Finished....");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\imane\Desktop\prblm sample\bon sample  de monsieur ahmed\!\org\nuxeo\project\sample\ConvertFacturesSelectedPdf.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */