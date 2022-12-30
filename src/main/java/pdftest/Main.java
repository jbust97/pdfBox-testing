package pdftest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

public class Main {
	private static PDDocument cargarDocumento(String path) throws IOException {
		File file = new File(path);
		PDDocument document = PDDocument.load(file);
		return document;
	}
	
	private static void finalizarDocumento(PDDocument document, String path) throws IOException {
		document.save(path);
		document.close();
	}
	
	public static void crearDocumento(String path) throws IOException {
		PDDocument document = new PDDocument();
		for(int i=0; i < 5; i++) {
			PDPage page = new PDPage();
			document.addPage(page);
		}
		finalizarDocumento(document,path);

	}
	public static void rellenarDocumento(String path) throws IOException {
		PDDocument document = cargarDocumento(path);
		for(int i=0; i < 5; i++) {
			PDPage page = document.getPage(i);
		      PDPageContentStream contentStream = new PDPageContentStream(document, page);
		      contentStream.beginText(); 
		      
		      contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
		      contentStream.newLineAtOffset(25, 725);
		      contentStream.setLeading(14.5f);
		      
		      String text = "Esta es la pagina número " + (i+1) + " del PDF";
		      String text1 = "Esta es la segunda linea que escribimos.";
		      
		      contentStream.showText(text);  
		      contentStream.newLine();
		      contentStream.showText(text1);
		      contentStream.endText();
		      contentStream.close();
		}
	    finalizarDocumento(document,path);

	}
	public static void eliminarPagina(String path,int page) throws IOException {
		PDDocument document = cargarDocumento(path);
		int noOfPages= document.getNumberOfPages();
		System.out.print(noOfPages);
		document.removePage(page);
		finalizarDocumento(document,path);
	}
	public static void mergeDocumento(String [] paths,String destinationPath) throws IOException {
		PDFMergerUtility PDFmerger = new PDFMergerUtility();
		PDFmerger.setDestinationFileName(destinationPath);

		for(String path: paths) {
			File file = new File(path);
			PDFmerger.addSource(file);
		}
		PDFmerger.mergeDocuments(null);
	}
	public static void extractToPNG(String path) throws IOException {
		PDDocument document = cargarDocumento(path);
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		for (int page = 0; page < document.getNumberOfPages(); ++page)
		{ 
		    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

		    // suffix in filename will be used as the file format
		    ImageIOUtil.writeImage(bim, path + "-" + (page+1) + ".png", 300);
		}
		document.close();
	}
	public static void agregarWaterMark(String path,String waterMarkPath,String destination) throws IOException {
        PDDocument document = cargarDocumento(path);
        HashMap<Integer, String> overlayGuide = new HashMap<Integer, String>();
		for(int i=0; i<document.getNumberOfPages(); i++){
            overlayGuide.put(i+1, waterMarkPath);
            //watermark.pdf is the document which is a one page PDF with your watermark image in it. 
            //Notice here, you can skip pages from being watermarked.
        }
        Overlay overlay = new Overlay();
        overlay.setInputPDF(document);
        PDDocument documentOverlay = overlay.overlay(overlayGuide);
        finalizarDocumento(documentOverlay,destination);
        document.close();
	}
	
	
	public static void main(String[] args) throws IOException {
		String path = "/home/hugo/Documents/notes/my_doc.pdf";
		String[] sources = {path,"/home/hugo/Documents/notes/Villamil 2022.pdf"};
		String mergedPath = "/home/hugo/Documents/notes/merged.pdf";
		String waterMarkPath = "/home/hugo/Documents/notes/data.pdf";
		String waterMarkedPDF = "/home/hugo/Documents/notes/waterMarkedPDF.pdf";
		crearDocumento(path);
		rellenarDocumento(path);
		extractToPNG(path);
		mergeDocumento(sources,mergedPath);
		agregarWaterMark(path,waterMarkPath,waterMarkedPDF);
		eliminarPagina(path,2);
	}
}
