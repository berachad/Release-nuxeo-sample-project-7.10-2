//package org.nuxeo.project.sample.xlsImport;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//import javax.faces.context.FacesContext;
//import javax.servlet.http.HttpServletResponse;
//
//import org.jboss.seam.Component;
//import org.nuxeo.ecm.automation.AutomationService;
//import org.nuxeo.ecm.automation.OperationContext;
//import org.nuxeo.ecm.automation.core.Constants;
//import org.nuxeo.ecm.automation.core.annotations.Context;
//import org.nuxeo.ecm.automation.core.annotations.Operation;
//import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
//import org.nuxeo.ecm.core.api.CoreSession;
//import org.nuxeo.ecm.core.schema.SchemaManager;
//import org.nuxeo.ecm.core.schema.types.Field;
//import org.nuxeo.ecm.platform.types.TypeManager;
//import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
//import org.nuxeo.project.sample.complementary.Constant;
//import org.nuxeo.runtime.api.Framework;
//
//import jxl.Workbook;
//import jxl.write.Label;
//import jxl.write.WritableSheet;
//import jxl.write.WritableWorkbook;
//import jxl.write.WriteException;
//
//
//
//@Operation(id = ExportModelXLS.ID, category = Constants.CAT_DOCUMENT, label = "Export Model XLS")
//public class ExportModelXLS {
//
//	public static final String ID = "Document.ExportModelXLS";
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
//	@Context
//	protected transient NavigationContext navigationContext;
//	
//	private static final String EXCEL_FILE_LOCATION = Constant.PATH_FILES + "/xls_model.xls";
//
//	
//	@OperationMethod
//	public void run() throws Exception {
//				//log.error(Constant.INFO_DASHES);
//				downloadXLSModel();
//	}
//	
//	public void downloadXLSModel() throws IOException {
//		 NavigationContext navigationContext = (NavigationContext) Component.getInstance("navigationContext");
////	      NavigationContext navigationContext = Framework.getService(NavigationContext.class);
//		  SchemaManager tm = Framework.getService(SchemaManager.class);
//		  TypeManager t = Framework.getService(TypeManager.class);
//		  Collection<org.nuxeo.ecm.platform.types.Type> types = t.getAllowedSubTypes(navigationContext.getCurrentDocument().getType());
//		  //types.forEach(vv -> System.out.println("vvvvv : " +vv));
//		  //System.out.println("1111 : " + types.iterator().next().getId());
//		  //System.out.println("2222 : " + types.iterator().next().getLabel());
//	        List<Field> fields = new ArrayList<>(tm.getDocumentType(types.iterator().next().getId()).getFields());
//			  for(int i=0; i<fields.size(); i++) {
//				  //System.out.println("nnnnnnn : " + fields.get(i).getName());
//			  }
//			  createXLSByFields(fields);
//			 
//				download(types.iterator().next().getId());
//			
//	  }
//	
//	private void createXLSByFields(List<Field> fields) {
//		  WritableWorkbook myFirstWbook = null;
//	        try {
//
//	            myFirstWbook = Workbook.createWorkbook(new File(EXCEL_FILE_LOCATION));
//	            WritableSheet excelSheet = myFirstWbook.createSheet("Sheet 1", 0);
//	            List<Field> fs = new ArrayList<Field>();
//	            for(int j=0; j<fields.size(); j++) {
//	            	if(fields.get(j).getName().toString().contains(":")) {
//		            fs.add(fields.get(j));
//	            	}
//	            }
//	            Label label = new Label(0, 0, "name");
//                excelSheet.addCell(label);	
//                Label label1 = new Label(1, 0, "type");
//                excelSheet.addCell(label1);	
//	            for(int i=2; i<fs.size()+2; i++) {
//	            		Label label3 = new Label(i, 0, fs.get(i-2).getName().toString().split(":")[1]);
//		                excelSheet.addCell(label3);		            	
//	            }
//	            myFirstWbook.write();
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        } catch (WriteException e) {
//	            e.printStackTrace();
//	        } finally {
//
//	            if (myFirstWbook != null) {
//	                try {
//	                    myFirstWbook.close();
//	                } catch (IOException e) {
//	                    e.printStackTrace();
//	                } catch (WriteException e) {
//	                    e.printStackTrace();
//	                }
//	            }
//	        }
//	  }
//	
//	private void download(String type) throws IOException {
//		  File file = new File(EXCEL_FILE_LOCATION);
//		  
//			FacesContext facesContext = FacesContext.getCurrentInstance();
//		    HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
//		
//		    response.reset();
//		    response.setHeader("Content-Type", "application/octet-stream");
//		    //Modifié par Yassine
//		    //System.out.println("fiil : " +file.getName());
//		    response.setHeader("Content-Disposition", "attachment;filename=" + type + "_" + file.getName());
//		
//		    OutputStream responseOutputStream = response.getOutputStream();
//		    InputStream fileInputStream = new FileInputStream(file);
//		
//		    byte[] bytesBuffer = new byte[2048];
//		    int bytesRead;
//		    while ((bytesRead = fileInputStream.read(bytesBuffer)) > 0) {
//		        responseOutputStream.write(bytesBuffer, 0, bytesRead);
//		    }
//		
//		    responseOutputStream.flush();
//		    fileInputStream.close();
//		    responseOutputStream.close();
//		    facesContext.responseComplete();
//		    new File(EXCEL_FILE_LOCATION).delete();
//	  }
//
//}
//
