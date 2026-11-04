package es.caib.pinbal.logic.helper;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Base64;

/**
 * Conversió dels valors dels camps de dades específiques de tipus ADJUNT_XML rebuts
 * com a text (API de recobriment v2) a un {@link Document}, que és el que espera
 * {@code XmlHelper.crearDadesEspecifiques} per inserir el fitxer com a nodes XML
 * (igual que fa el formulari web amb el fitxer pujat).
 *
 * El valor s'ha d'enviar codificat en base64. Per compatibilitat també s'accepta
 * l'XML directament com a text si comença per '&lt;'.
 *
 * El contingut ve d'un client extern: es desactiven els DTD i les entitats externes (XXE).
 */
public final class AdjuntXmlHelper {

	private AdjuntXmlHelper() {}

	/**
	 * @param valor XML codificat en base64 (o l'XML en text pla)
	 * @return el document XML; null si el valor és null o buit
	 * @throws IllegalArgumentException si el valor no és base64 vàlid o no conté un XML ben format
	 */
	public static Document parse(String valor) {
		if (valor == null || valor.trim().isEmpty()) {
			return null;
		}
		String trimmed = valor.trim();
		try {
			DocumentBuilder builder = newDocumentBuilder();
			if (trimmed.startsWith("<")) {
				return builder.parse(new InputSource(new StringReader(trimmed)));
			}
			byte[] contingut;
			try {
				// El decodificador MIME ignora salts de línia i espais (base64 amb línies de 76 caràcters)
				contingut = Base64.getMimeDecoder().decode(trimmed);
			} catch (IllegalArgumentException ex) {
				throw new IllegalArgumentException("El valor no està codificat en base64 vàlid", ex);
			}
			if (contingut.length == 0) {
				throw new IllegalArgumentException("El valor no està codificat en base64 vàlid");
			}
			// Es parseja com a bytes perquè el parser detecti la codificació de la declaració XML
			return builder.parse(new ByteArrayInputStream(contingut));
		} catch (SAXException | IOException ex) {
			throw new IllegalArgumentException("El contingut no és un document XML vàlid: " + ex.getMessage(), ex);
		} catch (ParserConfigurationException ex) {
			throw new IllegalStateException("No s'ha pogut configurar el parser XML", ex);
		}
	}

	private static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
		// Mateixa configuració que el formulari web (no namespace-aware) per generar el mateix XML
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		factory.setXIncludeAware(false);
		factory.setExpandEntityReferences(false);
		DocumentBuilder builder = factory.newDocumentBuilder();
		// Evita que el gestor per defecte escrigui els errors per stderr; es propaguen com a excepció
		builder.setErrorHandler(new ErrorHandler() {
			@Override
			public void warning(SAXParseException ex) {}
			@Override
			public void error(SAXParseException ex) throws SAXException { throw ex; }
			@Override
			public void fatalError(SAXParseException ex) throws SAXException { throw ex; }
		});
		return builder;
	}

}
