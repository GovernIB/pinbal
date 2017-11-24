/**
 * 
 */
package es.caib.pinbal.core.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;

import javax.activation.MimetypesFileTypeMap;

import org.odftoolkit.odfdom.converter.core.ODFConverterException;
import org.odftoolkit.odfdom.converter.pdf.PdfConverter;
import org.odftoolkit.odfdom.converter.pdf.PdfOptions;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.springframework.stereotype.Component;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.DocumentFormatRegistry;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Helper per a convertir documents a altres formats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ConversioTipusDocumentHelper {

	private static final String CONVERSIO_TIPUS_OPENOFFICE = "openoffice";
	private static final String CONVERSIO_TIPUS_XDOCREPORT = "xdocreport";

	private DocumentFormatRegistry documentFormatRegistry;



	public void convertir(
			String arxiuNom,
			InputStream arxiuContingut,
			String extensioSortida,
			OutputStream sortida) throws ODFConverterException, IOException {
		String extensioEntrada = extensioPerNomArxiu(arxiuNom);
		boolean copiarEntradaSortida = true;
		if (	extensioEntrada != null &&
				extensioSortida != null &&
				!extensioEntrada.equalsIgnoreCase(extensioSortida)) {
			if (isConversioTipusOpenOffice()) {
				DocumentFormat inputFormat = formatPerNomArxiu(arxiuNom);
				DocumentFormat outputFormat = getDocumentFormatRegistry().getFormatByFileExtension(extensioSortida);
				convertAmbServeiOpenOffice(
						arxiuContingut,
						inputFormat,
						sortida,
						outputFormat);
				copiarEntradaSortida = false;
			} else if (
					isConversioTipusXdocreport() &&
					"odt".equalsIgnoreCase(extensioEntrada) &&
					"pdf".equalsIgnoreCase(extensioSortida)) {
				try {
					OdfTextDocument document = OdfTextDocument.loadDocument(arxiuContingut);
					PdfOptions options = PdfOptions.create();
					PdfConverter.getInstance().convert(document, sortida, options);
				} catch (Exception ex) {
					throw new RuntimeException("Error convertint amb XDOCReport", ex);
				}
				copiarEntradaSortida = false;
			}
		}
		if (copiarEntradaSortida) {
			byte[] buffer = new byte[1024];
			int len = arxiuContingut.read(buffer);
			while (len >= 0) {
				sortida.write(buffer, 0, len);
				len = arxiuContingut.read(buffer);
			}
			arxiuContingut.close();
			sortida.close();
		}
	}
	public String nomArxiuConvertit(
			String arxiuNom,
			String extensioSortida) {
		//DocumentFormat outputFormat = getDocumentFormatRegistry().getFormatByFileExtension(extensioSortida);
		int indexPunt = arxiuNom.lastIndexOf(".");
		if (indexPunt != -1) {
			String nom = arxiuNom.substring(0, indexPunt);
			return nom + "." + extensioSortida;
		} else {
			return arxiuNom + "." + extensioSortida;
		}
	}
	public String getArxiuMimeType(String nomArxiu) {
		return new MimetypesFileTypeMap().getContentType(nomArxiu);
	}

	public void convertirPdfToPdfa(
			InputStream original,
			OutputStream sortida) throws DocumentException, IOException {
        PdfReader reader = new PdfReader(original);
        PdfStamper stamper = new PdfStamper(
        		reader,
        		sortida);
        Document document = new Document();
        PdfWriter writer = stamper.getWriter();
        int numberPages = reader.getNumberOfPages();
        writer.setPDFXConformance(PdfWriter.PDFA1B);
        document.open();
        PdfDictionary outi = new PdfDictionary(PdfName.OUTPUTINTENT);
        outi.put(PdfName.OUTPUTCONDITIONIDENTIFIER, new PdfString("sRGB IEC61966-2.1"));
        outi.put(PdfName.INFO, new PdfString("sRGB IEC61966-2.1"));
        outi.put(PdfName.S, PdfName.GTS_PDFA1);
        writer.getExtraCatalog().put(PdfName.OUTPUTINTENTS, new PdfArray(outi));  
        PdfImportedPage p = null;
        Image image;
        for (int i = 0; i < numberPages; i++) {
            p = writer.getImportedPage(reader, i + 1);
            image = Image.getInstance(p);
            document.add(image);
        }
        writer.createXmpMetadata();
        document.close();
        stamper.setMoreInfo(reader.getInfo());
        stamper.close();
    }



	private String extensioPerNomArxiu(String nomArxiu) {
		int indexPunt = nomArxiu.lastIndexOf(".");
		if (indexPunt != -1)
			return nomArxiu.substring(indexPunt + 1);
		else
			return null;
	}
	private DocumentFormat formatPerNomArxiu(String nomArxiu) {
		String extensio = extensioPerNomArxiu(nomArxiu);
		if (extensio != null)
			return getDocumentFormatRegistry().getFormatByFileExtension(extensio);
		else
			return null;
	}

	private boolean isConversioTipusOpenOffice() {
		String conversioTipus = getConversioTipus();
		return conversioTipus == null || CONVERSIO_TIPUS_OPENOFFICE.equalsIgnoreCase(conversioTipus);
	}
	private boolean isConversioTipusXdocreport() {
		String conversioTipus = getConversioTipus();
		return CONVERSIO_TIPUS_XDOCREPORT.equalsIgnoreCase(conversioTipus);
	}
	private String getConversioTipus() {
		return PropertiesHelper.getProperties().getProperty("es.caib.pinbal.conversio.tipus");
	}

	private void convertAmbServeiOpenOffice(
			InputStream in,
			DocumentFormat inputFormat,
			OutputStream out,
			DocumentFormat outputFormat) throws ConnectException {
		String host = getOpenOfficeHost();
		String port = getOpenOfficePort();
		OpenOfficeConnection connection = null;
		try {
			connection = new SocketOpenOfficeConnection(
					host,
					new Integer(port));
			connection.connect();
			DocumentConverter converter = new StreamOpenOfficeDocumentConverter(
					connection,
					getDocumentFormatRegistry());
			converter.convert(
					in,
					inputFormat,
					out,
					outputFormat);
		} finally {
			if (connection != null) connection.disconnect();
		}
	}
	private String getOpenOfficeHost() {
		return PropertiesHelper.getProperties().getProperty("es.caib.pinbal.conversio.open.office.host");
	}
	private String getOpenOfficePort() {
		return PropertiesHelper.getProperties().getProperty("es.caib.pinbal.conversio.open.office.port");
	}
	private DocumentFormatRegistry getDocumentFormatRegistry() {
		if (documentFormatRegistry == null)
			documentFormatRegistry = new DefaultDocumentFormatRegistry();
		return documentFormatRegistry;
	}

}
