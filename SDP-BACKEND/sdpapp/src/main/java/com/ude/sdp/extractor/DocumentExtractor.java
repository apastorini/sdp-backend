package com.ude.sdp.extractor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

public class DocumentExtractor {

	/*
	 * private byte[] toPdfDocx(ByteArrayOutputStream docx) { try { InputStream
	 * isFromFirstData = new ByteArrayInputStream(docx.toByteArray());
	 * 
	 * XWPFDocument document = new XWPFDocument(isFromFirstData); PdfOptions options
	 * = PdfOptions.create();
	 * 
	 * // make new file in c:\temp\ OutputStream out = new FileOutputStream(new
	 * File("c:\\tmp\\HelloWord.pdf")); PdfConverter.getInstance().convert(document,
	 * out, options);
	 * 
	 * // return byte array for return in http request. ByteArrayOutputStream pdf =
	 * new ByteArrayOutputStream(); PdfConverter.getInstance().convert(document,
	 * pdf, options);
	 * 
	 * document.write(pdf); document.close(); return pdf.toByteArray(); } catch
	 * (Exception e) { e.printStackTrace(); return null; } }
	 */

	public static ByteArrayOutputStream convertDoc2Pdf(String fileType, ByteArrayInputStream in) throws IOException {
		ByteArrayOutputStream pdfDeSaidaOutputStream = new ByteArrayOutputStream();
		try {
			if(fileType.toUpperCase().equals("DOCX")) {
				XWPFDocument docx = new XWPFDocument(in);
				org.apache.poi.xwpf.converter.pdf.PdfOptions options = null;
				
				org.apache.poi.xwpf.converter.pdf.PdfConverter.getInstance().convert(docx, pdfDeSaidaOutputStream, options);
				File someFile = new File(fileType + ".pdf");
				FileOutputStream fos = new FileOutputStream(someFile);
				fos.write(pdfDeSaidaOutputStream.toByteArray());
				fos.flush();
				fos.close();
				return pdfDeSaidaOutputStream;
			}else {
				// DOC
					// fis = new FileInputStream(new File(FilePath));

				HWPFDocument doc = new HWPFDocument(in);
				WordExtractor we = new WordExtractor(doc);
				String k = we.getText();
				// System.out.println(we.getText());
				//we.close();
				Document document = new Document();
				PdfWriter.getInstance(document, pdfDeSaidaOutputStream);

				document.open();
				//BaseFont courier = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252,
				//BaseFont.EMBEDDED);
				
				document.add(new Paragraph(k));//.setFont(new Font(courier, 25, Font.NORMAL, BaseColor.BLUE)));

				document.close();
				pdfDeSaidaOutputStream.close();

				return pdfDeSaidaOutputStream;

			
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * public static ByteArrayOutputStream convertDoc2Pdf3(String fileType,
	 * ByteArrayInputStream in) { ByteArrayOutputStream out = new
	 * ByteArrayOutputStream(); String k = null; //
	 * https://stackoverrun.com/es/q/11922679 try { // FileInputStream fis = new
	 * FileInputStream(in); if (fileType.equals("DOCX")) { XWPFDocument adoc = new
	 * XWPFDocument(in); XWPFWordExtractor xwe = new XWPFWordExtractor(adoc); k =
	 * xwe.getText(); // System.out.print(k); xwe.close(); PdfOptions options =
	 * PdfOptions.create(); // OutputStream out = new FileOutputStream(new
	 * File(pdfPath)); PdfConverter.getInstance().convert(adoc, out, options);
	 * return out; } else {// DOC // fis = new FileInputStream(new File(FilePath));
	 * 
	 * HWPFDocument doc = new HWPFDocument(in); WordExtractor we = new
	 * WordExtractor(doc); k = we.getText(); // System.out.println(we.getText());
	 * we.close(); Document document = new Document();
	 * PdfWriter.getInstance(document, out);
	 * 
	 * // document.open(); // BaseFont courier =
	 * BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252, // BaseFont.EMBEDDED);
	 * // document.add(new Paragraph(k));
	 * 
	 * document.close(); out.close();
	 * 
	 * return out;
	 * 
	 * }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * return out; }
	 */

	/*
	 * public static ByteArrayOutputStream convertDoc2Pdf2(String fileType,
	 * ByteArrayInputStream in) { ByteArrayOutputStream out = null; //
	 * http://documents4j.com/#/ try {
	 * 
	 * IConverter converter = LocalConverter.make();
	 * 
	 * out = new ByteArrayOutputStream();
	 * 
	 * boolean conversion = false; if (fileType.equals("DOC")) {
	 * 
	 * conversion =
	 * converter.convert(in).as(DocumentType.MS_WORD).to(out).as(DocumentType.PDFA)
	 * .prioritizeWith(1000) // optional .execute(); } else {
	 * 
	 * if (fileType.equals("DOCX")) { conversion =
	 * converter.convert(in).as(DocumentType.MS_WORD).to(out).as(DocumentType.PDFA)
	 * .prioritizeWith(1000) // optional .execute(); }
	 * 
	 * }
	 * 
	 * if (conversion) { System.out.println("Conversion correcta");
	 * 
	 * } in.close(); out.flush(); out.close();
	 * 
	 * } catch (IOException e) { e.printStackTrace();
	 * 
	 * }
	 * 
	 * return (out); }
	 */
	/*
	 * public void pru() {
	 * 
	 * String k = null; OutputStream fileForPdf = null; try {
	 * 
	 * String fileName = "/document/test2.doc"; // Below Code is for .doc file if
	 * (fileName.endsWith(".doc")) { HWPFDocument doc = new HWPFDocument(new
	 * FileInputStream(fileName)); WordExtractor we = new WordExtractor(doc); k =
	 * we.getText();
	 * 
	 * fileForPdf = new FileOutputStream(new File("/document/DocToPdf.pdf"));
	 * we.close(); } else if (fileName.endsWith(".docx")) { XWPFDocument docx = new
	 * XWPFDocument(new FileInputStream(fileName)); // using XWPFWordExtractor Class
	 * XWPFWordExtractor we = new XWPFWordExtractor(docx); k = we.getText();
	 * 
	 * fileForPdf = new FileOutputStream(new File("/document/DocxToPdf.pdf"));
	 * we.close(); }
	 * 
	 * Document document = new Document(); PdfWriter.getInstance(document,
	 * fileForPdf);
	 * 
	 * document.open();
	 * 
	 * document.add(new Paragraph(k));
	 * 
	 * document.close(); fileForPdf.close();
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */
}
