package es.caib.pinbal.core.helper;

import es.caib.pinbal.core.dto.ArbreRespostaDto;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ArbreRespostaDocumentHelperTest {

	private static final String PDF_BASE64 = "JVBERi0xLjQKJcTl8uXrp/Og0MTGCjEgMCBvYmoKPDwKL1R5cGUgL0NhdGFsb2cKPj4KZW5kb2JqCg==";
	private static final String SIGN_BASE64 = "U0dWc2JHOGdVMmxuYm1GMGRYSmw=";

	@Test
	public void haDeConvertirNodeDocumentalSimpleEnDescarrega() {
		ArbreRespostaDto document = ArbreRespostaDto.builder()
				.titol("Certificat")
				.xpath("DatosEspecificos/Certificat")
				.descripcio(PDF_BASE64)
				.document(true)
				.build();

		ArbreRespostaDocumentHelper.prepararDocuments(document);

		assertEquals(PDF_BASE64, document.getDocumentContingutBase64());
		assertEquals("application/pdf", document.getDocumentMimeType());
		assertEquals("Certificat.pdf", document.getDocumentNom());
		assertTrue(document.isDocumentPdf());
		assertNull(document.getDescripcio());
	}

	@Test
	public void haDeRecuperarMetadadesIDesferBase64DeSubcamps() {
		ArbreRespostaDto document = ArbreRespostaDto.builder()
				.titol("Document adjunt")
				.xpath("DatosEspecificos/Document")
				.document(true)
				.build();
		document.addFill(fill("Fecha", "2026-04-09", "DatosEspecificos/Document/Fecha"));
		document.addFill(fill("Descripcion", "Resolucio", "DatosEspecificos/Document/Descripcion"));
		document.addFill(fill("Nombre", "resolucion.pdf", "DatosEspecificos/Document/Nombre"));
		document.addFill(fill("Tamano", "1024", "DatosEspecificos/Document/Tamano"));
		document.addFill(fill("Fichero", PDF_BASE64, "DatosEspecificos/Document/Fichero"));
		document.addFill(fill("Firma", SIGN_BASE64, "DatosEspecificos/Document/Firma"));

		ArbreRespostaDocumentHelper.prepararDocuments(document);

		assertEquals(PDF_BASE64, document.getDocumentContingutBase64());
		assertEquals("resolucion.pdf", document.getDocumentNom());
		assertEquals("application/pdf", document.getDocumentMimeType());
		assertTrue(document.isDocumentPdf());
		assertNotNull(document.getFills());
		assertEquals(4, document.getFills().size());
		assertFalse(conteXpath(document, "DatosEspecificos/Document/Fichero"));
		assertFalse(conteXpath(document, "DatosEspecificos/Document/Firma"));
	}

	@Test
	public void haDeConsiderarRespostaComASegmentIgnorableAlCompararXpathsDocumentals() {
		Set<String> xpathsDocument = ArbreRespostaDocumentHelper.normalitzarXpathsDocument(Arrays.asList(
				"DatosEspecificos/Retorno/CertificadoDatosDiscapacidad/ValidezPermanente"));

		assertTrue(ArbreRespostaDocumentHelper.esXpathDocument(
				"DatosEspecificos/Respuesta/Retorno/CertificadoDatosDiscapacidad/ValidezPermanente",
				xpathsDocument));
	}

	private static ArbreRespostaDto fill(String titol, String descripcio, String xpath) {
		return ArbreRespostaDto.builder()
				.titol(titol)
				.descripcio(descripcio)
				.xpath(xpath)
				.build();
	}

	private static boolean conteXpath(ArbreRespostaDto node, String xpath) {
		if (node.getFills() == null) {
			return false;
		}
		for (ArbreRespostaDto fill : node.getFills()) {
			if (xpath.equals(fill.getXpath())) {
				return true;
			}
		}
		return false;
	}
}
