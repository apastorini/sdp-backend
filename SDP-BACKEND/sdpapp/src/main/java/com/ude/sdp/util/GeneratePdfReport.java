package com.ude.sdp.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.ude.sdp.repository.sql.FileRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import com.itextpdf.text.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class GeneratePdfReport {

	@Autowired
	FileRepository fileRepository;
	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);

	/** Inner class to add a header and a footer. */
	static class HeaderFooter extends PdfPageEventHelper {

		 private Image total;
		 private PdfTemplate t;
		//https://memorynotfound.com/adding-header-footer-pdf-using-itext-java/
		 @Override
		    public void onEndPage(PdfWriter writer, Document document) {
			 	addHeader(writer);
		        addFooter(writer);
		    }

		
		 
		    public void onOpenDocument(PdfWriter writer, Document document) {
		        t = writer.getDirectContent().createTemplate(30, 16);
		        try {
		            total = Image.getInstance(t);
		            total.setRole(PdfName.ARTIFACT);
		        } catch (DocumentException de) {
		            throw new ExceptionConverter(de);
		        }
		    }
		 
		    private void addHeader(PdfWriter writer){
		        PdfPTable header = new PdfPTable(2);
		        try {
		            // set defaults
		            header.setWidths(new int[]{2, 24});
		            header.setTotalWidth(527);
		            header.setLockedWidth(true);
		            header.getDefaultCell().setFixedHeight(30);
		            header.getDefaultCell().setBorder(Rectangle.BOTTOM);
		            header.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);

		            // add image
		            ClassPathResource imgFile = new ClassPathResource("/Universidad-de-la-Empresa.png");
		            //Image logo = Image.getInstance(HeaderFooter.class.getResource("/Universidad-de-la-Empresa.png"));
		            Image logo = Image.getInstance(imgFile.getURL());
		            header.addCell(logo);

		            // add text
		            PdfPCell text = new PdfPCell();
		            text.setPaddingBottom(15);
		            text.setPaddingLeft(10);
		            text.setBorder(Rectangle.BOTTOM);
		            text.setBorderColor(BaseColor.LIGHT_GRAY);
		            text.addElement(new Phrase("Sistema de Detección de Plagio", new Font(Font.FontFamily.HELVETICA, 12)));
		            text.addElement(new Phrase("Universidad de la Empresa", new Font(Font.FontFamily.HELVETICA, 8)));
		            header.addCell(text);

		            // write content
		            header.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());
		        } catch(DocumentException de) {
		            throw new ExceptionConverter(de);
		        } catch (MalformedURLException e) {
		            throw new ExceptionConverter(e);
		        } catch (IOException e) {
		            throw new ExceptionConverter(e);
		        }
		    }

		    private void addFooter(PdfWriter writer){
		        PdfPTable footer = new PdfPTable(3);
		        try {
		            // set defaults
		            footer.setWidths(new int[]{24, 2, 1});
		            footer.setTotalWidth(527);
		            footer.setLockedWidth(true);
		            footer.getDefaultCell().setFixedHeight(30);
		            footer.getDefaultCell().setBorder(Rectangle.TOP);
		            footer.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);

		            // add copyright
		            footer.addCell(new Phrase("\u00A9 Universidad de la Empresa-SDP", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

		            // add current page count
		            footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
		            footer.addCell(new Phrase(String.format("Página %d", writer.getPageNumber()), new Font(Font.FontFamily.HELVETICA, 8)));

		            // add placeholder for total page count
		            PdfPCell totalPageCount = new PdfPCell(total);
		           // PdfPCell totalPageCount = new PdfPCell();
		            totalPageCount.setBorder(Rectangle.TOP);
		            totalPageCount.setBorderColor(BaseColor.LIGHT_GRAY);
		            footer.addCell(totalPageCount);

		            // write page
		            PdfContentByte canvas = writer.getDirectContent();
		            //canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
		            footer.writeSelectedRows(0, -1, 34, 50, canvas);
		            //canvas.endMarkedContentSequence();
		        } catch(DocumentException de) {
		            throw new ExceptionConverter(de);
		        }
		    }
		    
	 }
		

	public ByteArrayInputStream reportFromJson(JsonObject informeTotal) {

		
		Document document = new Document();
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.setPageSize(PageSize.A4);
			document.setMargins(30, 30, 100, 50);
			document.setMarginMirroring(false);
			
			// add header and footer
			HeaderFooter event = new HeaderFooter();
	        writer.setPageEvent(event);
			document.open();
			document.addCreationDate();
			document.addTitle("Informe Plagio");
			//document.newPage();
			//http://www.vogella.com/tutorials/JavaPDF/article.html
			
			Paragraph preface = new Paragraph("Resumen");
			preface.setAlignment(Element.ALIGN_CENTER);
			preface.setFont(catFont);
			document.add(preface);

	       
			String nombreArchivo = informeTotal.get("Archivo_Principal").getAsString();
			String idAnalisis = informeTotal.get("id_Analisis").getAsString();
			String fechaInicial = null;
			if(informeTotal.get("Fecha_Incial")!=null) {
				fechaInicial=informeTotal.get("Fecha_Incial").getAsString();
			}
			
			String fechaFinal = informeTotal.get("Fecha_Final").getAsString();
			JsonArray archivosComparar = informeTotal.getAsJsonArray("Archivos_Comparar");
			JsonArray informesPorArchivo = informeTotal.getAsJsonArray("Informes_por_Archivo");
			String coincidenciasTotales = informeTotal.get("Coincidencias_Totales").getAsString();

			String p1 = "Nombre Archivo: " + nombreArchivo + "\n" + "Id Analisis " + idAnalisis + "\nFecha "
					+ fechaFinal +"\nCoincidencias Totales: " + coincidenciasTotales  + "\n";
			if(archivosComparar!=null) {
				p1=p1 + "Archivos a comparar: \n";
				for(int h=0;h<archivosComparar.size();h++) {
					p1=p1+archivosComparar.get(h).getAsString() + "\n";
				}
			}

			// Creating Paragraphs
			Paragraph paragraph1 = new Paragraph(p1);

			// Adding paragraphs to document
			paragraph1.setSpacingBefore(30.0f);
			paragraph1.setSpacingAfter(30.0f);
			document.add(paragraph1);

			if (informesPorArchivo != null) {
				for (int i = 0; i < informesPorArchivo.size(); i++) {
					JsonObject informePorArchivo = informesPorArchivo.get(i).getAsJsonObject();
					String nombreArchivoActual = informePorArchivo.get("Archivo").getAsString();
					String cantCoincidenciasPorArchivo = informePorArchivo.get("Cantidad_Concidencias").getAsString();
					String pf = "Archivo: " + nombreArchivoActual + "\n Total de Coincidencias: "
							+ cantCoincidenciasPorArchivo;
					Paragraph paragraphFile = new Paragraph(pf);
					paragraphFile.setSpacingBefore(10.0f);
					paragraphFile.setSpacingAfter(10.0f);
					document.add(paragraphFile);
					JsonArray frasesPorArchivo = informePorArchivo.getAsJsonArray("Frases");
					if (frasesPorArchivo != null && frasesPorArchivo.size()>0) {
						PdfPTable table = new PdfPTable(3);
						table.setSpacingBefore(30.0f); // Space Before table starts, like margin-top in CSS
						table.setSpacingAfter(30.0f);
						table.setWidthPercentage(80);
						table.setWidths(new int[] { 5, 1, 1 });
						Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

						PdfPCell hcell;
						hcell = new PdfPCell(new Phrase("Fragmento", headFont));
						hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(hcell);

						hcell = new PdfPCell(new Phrase("Pág. Original", headFont));
						hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(hcell);

						hcell = new PdfPCell(new Phrase("Pág. Archivo", headFont));
						hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(hcell);

						for (int j = 0; j < frasesPorArchivo.size(); j++) {
							JsonObject frasePorArchivo=frasesPorArchivo.get(j).getAsJsonObject(); 
							String fragmento=frasePorArchivo.get("Fragmento").getAsString();
							String pagOriginal=frasePorArchivo.get("Pag_Original").getAsString();
							JsonArray mismaFraseCorpus=frasePorArchivo.get("Frases_Corpus").getAsJsonArray();
							if(mismaFraseCorpus!=null) {
								for(int k=0;k<mismaFraseCorpus.size();k++) {
									JsonObject fragmentoCorpus=mismaFraseCorpus.get(k).getAsJsonObject();
									String pagCorpus=fragmentoCorpus.get("Pag_Corpus").getAsString();
									table.addCell(fragmento);
									table.addCell(pagOriginal);
									table.addCell(pagCorpus);

								}
							}
							
						}
						//Por cada archivo se agrega una tabla
						document.add(table);
					}
				}
			}

			document.close();
			return new ByteArrayInputStream(out.toByteArray());
		} catch (DocumentException ex) {

			ex.printStackTrace();
			return null;
		}

	}

}