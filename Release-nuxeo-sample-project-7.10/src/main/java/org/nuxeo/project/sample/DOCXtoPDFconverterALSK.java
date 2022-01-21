package org.nuxeo.project.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.nuxeo.project.sample.complementary.Constant;

public class DOCXtoPDFconverterALSK {
	public File parseDocx(String docx, String fileName) throws IOException {
		String path = Constant.PATH_FILES + "/Rendu"+fileName.replaceAll(".docx", ".pdf");

		// 1) Load DOCX into XWPFDocument
		InputStream in= new FileInputStream(new File(docx));
		XWPFDocument document = new XWPFDocument(in);

		// 2) Prepare Pdf options
		PdfOptions options = PdfOptions.create();

		// 3) Convert XWPFDocument to Pdf
		OutputStream out;
		
			out = new FileOutputStream(new File(path));
			
				PdfConverter.getInstance().convert(document, out, options);
				out.flush();
				out.close();
				in.close();
			
		return new File(path);
		
	}

	
}
