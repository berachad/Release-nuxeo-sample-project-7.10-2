package org.nuxeo.project.sample;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.poi.xwpf.converter.core.XWPFConverterException;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * @author sakthidasans
 *
 */
public class DOCXtoPDFconverter {

	/**
	 * @param docx
	 * 				 the original docx
	 * 
	 * @param pdf
	 * 				the resulting pdf
	 * 
	 * @throws IOException
	 */
	public File parseDocx(String docx, String fileName) throws IOException {

		// 1) Load DOCX into XWPFDocument
		InputStream in= new FileInputStream(new File(docx));
		XWPFDocument document = new XWPFDocument(in);
		
		
		// 2) Prepare Pdf options
		PdfOptions options = PdfOptions.create();

		// 3) Convert XWPFDocument to Pdf
		OutputStream out;
		
			out = new FileOutputStream(new File("../TemplatesPdf/"+fileName.replaceAll(".docx", ".pdf")));
			
				PdfConverter.getInstance().convert(document, out, options);
				out.flush();
				out.close();
				in.close();
			

		File filePdf = new File("../TemplatesPdf/"+fileName.replaceAll(".docx", ".pdf"));
		
		
		return filePdf;
		
		
//		if (Desktop.isDesktopSupported()) {
//		    try {
//		        Desktop.getDesktop().open(filePdf);
//		    } catch (IOException ex) {
//		        // no application registered for PDFs
//		    }
//		}
	}

	
}