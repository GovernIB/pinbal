/**
 * 
 */
package es.caib.pinbal.logic.helper;

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
import fr.opensagres.odfdom.converter.core.ODFConverterException;
import fr.opensagres.odfdom.converter.pdf.PdfConverter;
import fr.opensagres.odfdom.converter.pdf.PdfOptions;
import lombok.RequiredArgsConstructor;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.core.office.OfficeUtils;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.ExternalOfficeManager;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.springframework.stereotype.Component;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Helper per a convertir documents a altres formats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@Component
public class ConversioTipusDocumentHelper {

	private static final String CONVERSIO_TIPUS_OPENOFFICE = "openoffice";
	private static final String CONVERSIO_TIPUS_XDOCREPORT = "xdocreport";

	private final ConfigHelper configHelper;


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
				DocumentFormat outputFormat = DefaultDocumentFormatRegistry.getFormatByExtension(extensioSortida);
				if (inputFormat == null || outputFormat == null) {
					throw new IOException("Format de document no suportat: " + extensioEntrada + " -> " + extensioSortida);
				}
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
        stamper.setMoreInfo(new HashMap<>(reader.getInfo()));
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
			return DefaultDocumentFormatRegistry.getFormatByExtension(extensio);
		else
			return null;
	}

	private void convertAmbServeiOpenOffice(
			InputStream in,
			DocumentFormat inputFormat,
			OutputStream out,
			DocumentFormat outputFormat) throws IOException {
		String host = getOpenOfficeHost();
		String port = getOpenOfficePort();
		OfficeManager officeManager = null;
		try {
			officeManager = ExternalOfficeManager.builder()
					.hostName(host)
					.portNumbers(Integer.parseInt(port))
					.connectFailFast(true)
					.build();
			officeManager.start();
			LocalConverter.builder()
					.officeManager(officeManager)
					.build()
					.convert(in)
					.as(inputFormat)
					.to(out)
					.as(outputFormat)
					.execute();
		} catch (OfficeException ex) {
			throw new IOException("Error convertint amb el servei OpenOffice/LibreOffice", ex);
		} finally {
			if (officeManager != null) {
				OfficeUtils.stopQuietly(officeManager);
			}
		}
	}

	private String getConversioTipus() {
		return configHelper.getConfig("es.caib.pinbal.conversio.tipus", CONVERSIO_TIPUS_OPENOFFICE);
	}

	private boolean isConversioTipusOpenOffice() {
		String conversioTipus = getConversioTipus();
		return conversioTipus == null || CONVERSIO_TIPUS_OPENOFFICE.equalsIgnoreCase(conversioTipus);
	}
	private boolean isConversioTipusXdocreport() {
		String conversioTipus = getConversioTipus();
		return CONVERSIO_TIPUS_XDOCREPORT.equalsIgnoreCase(conversioTipus);
	}


	private String getOpenOfficeHost() {
		return configHelper.getConfig("es.caib.pinbal.conversio.open.office.host");
	}
	private String getOpenOfficePort() {
		return configHelper.getConfig("es.caib.pinbal.conversio.open.office.port");
	}

}
