package es.caib.pinbal.core.helper;

import es.caib.pinbal.core.dto.ArbreRespostaDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ArbreRespostaDocumentHelper {

	private static final int MIN_BASE64_LENGTH = 16;

	private ArbreRespostaDocumentHelper() {
	}

	public static void prepararDocuments(ArbreRespostaDto node) {
		if (node == null) {
			return;
		}

		if (node.getFills() != null) {
			for (ArbreRespostaDto fill : node.getFills()) {
				prepararDocuments(fill);
			}
		}

		if (!node.isDocument()) {
			return;
		}

		DocumentInfo documentInfo = extreureDocumentInfo(node);
		if (!documentInfo.teContingut()) {
			return;
		}

		node.setDocumentContingutBase64(documentInfo.contingutBase64);
		node.setDocumentMimeType(documentInfo.getMimeType());
		node.setDocumentPdf(documentInfo.esPdf());
		node.setDocumentNom(generarNomFitxer(node.getTitol(), documentInfo));

		if (esBase64Probable(node.getDescripcio())) {
			node.setDescripcio(null);
		}

		filtrarCampsBinaris(node);
	}

	public static Set<String> normalitzarXpathsDocument(List<String> xpaths) {
		Set<String> normalitzats = new HashSet<String>();
		if (xpaths == null) {
			return normalitzats;
		}
		for (String xpath : xpaths) {
			String normalitzat = normalitzarXpathDocument(xpath);
			if (!esBuit(normalitzat)) {
				normalitzats.add(normalitzat);
			}
		}
		return normalitzats;
	}

	public static boolean esXpathDocument(String xpathNode, Set<String> xpathsDocument) {
		if (xpathsDocument == null || xpathsDocument.isEmpty()) {
			return false;
		}
		String normalitzat = normalitzarXpathDocument(xpathNode);
		return !esBuit(normalitzat) && xpathsDocument.contains(normalitzat);
	}

	private static DocumentInfo extreureDocumentInfo(ArbreRespostaDto node) {
		DocumentInfo documentInfo = new DocumentInfo();
		visitarNode(node, node, documentInfo);
		return documentInfo;
	}

	private static void visitarNode(ArbreRespostaDto arrel, ArbreRespostaDto actual, DocumentInfo documentInfo) {
		if (actual == null) {
			return;
		}

		String segment = obtenirDarrerSegment(actual.getXpath());
		String valor = normalitzar(actual.getDescripcio());

		if (documentInfo.nomFitxer == null && esCampNomFitxer(segment) && !esBuit(valor)) {
			documentInfo.nomFitxer = valor;
		}
		if (documentInfo.mimeType == null && esCampMimeType(segment) && !esBuit(valor)) {
			documentInfo.mimeType = valor;
		}
		if (documentInfo.contingutBase64 == null && esContingutDocument(arrel, actual, segment, valor)) {
			documentInfo.contingutBase64 = valor;
		}

		if (actual.getFills() != null) {
			for (ArbreRespostaDto fill : actual.getFills()) {
				visitarNode(arrel, fill, documentInfo);
			}
		}
	}

	private static boolean esContingutDocument(ArbreRespostaDto arrel, ArbreRespostaDto actual, String segment, String valor) {
		if (esBuit(valor) || !esBase64Probable(valor)) {
			return false;
		}

		if (arrel == actual && (actual.getFills() == null || actual.getFills().isEmpty())) {
			return true;
		}

		return esCampContingut(segment);
	}

	private static void filtrarCampsBinaris(ArbreRespostaDto node) {
		if (node.getFills() == null) {
			return;
		}

		List<ArbreRespostaDto> fillsNets = new ArrayList<>(node.getFills());
		Iterator<ArbreRespostaDto> iterator = fillsNets.iterator();
		while (iterator.hasNext()) {
			ArbreRespostaDto fill = iterator.next();
			String segment = obtenirDarrerSegment(fill.getXpath());
			if (fill.getFills() == null || fill.getFills().isEmpty()) {
				if (esBase64Probable(fill.getDescripcio()) && (esCampContingut(segment) || esCampFirma(segment))) {
					iterator.remove();
					continue;
				}
			}
			filtrarCampsBinaris(fill);
		}
		node.setFills(fillsNets);
	}

	private static String generarNomFitxer(String titol, DocumentInfo documentInfo) {
		String nomBase = sanejarNomFitxer(documentInfo.nomFitxer);
		if (esBuit(nomBase)) {
			nomBase = sanejarNomFitxer(titol);
		}
		if (esBuit(nomBase)) {
			nomBase = "document";
		}

		if (documentInfo.esPdf() && !nomBase.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
			return nomBase + ".pdf";
		}

		if (nomBase.contains(".")) {
			return nomBase;
		}

		return nomBase + ".bin";
	}

	private static String sanejarNomFitxer(String nom) {
		String valor = normalitzar(nom);
		if (esBuit(valor)) {
			return null;
		}
		return valor.replaceAll("[\\\\/:*?\"<>|]", "_");
	}

	private static String obtenirDarrerSegment(String xpath) {
		String valor = normalitzar(xpath);
		if (esBuit(valor)) {
			return "";
		}

		int index = valor.lastIndexOf('/');
		if (index == -1 || index == valor.length() - 1) {
			return valor.toLowerCase(Locale.ROOT);
		}
		return valor.substring(index + 1).toLowerCase(Locale.ROOT);
	}

	private static String normalitzarXpathDocument(String xpath) {
		String valor = normalitzar(xpath);
		if (esBuit(valor)) {
			return null;
		}

		String[] parts = valor.split("/");
		List<String> net = new ArrayList<String>();
		for (String part : parts) {
			String segment = normalitzar(part);
			if (esBuit(segment)) {
				continue;
			}
			if ("respuesta".equalsIgnoreCase(segment)) {
				continue;
			}
			net.add(segment);
		}

		if (net.isEmpty()) {
			return null;
		}
		return join(net, "/");
	}

	private static String join(List<String> parts, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parts.size(); i++) {
			if (i > 0) {
				sb.append(separator);
			}
			sb.append(parts.get(i));
		}
		return sb.toString();
	}

	private static boolean esCampContingut(String segment) {
		return "fichero".equals(segment)
				|| "fitxer".equals(segment)
				|| "archivo".equals(segment)
				|| "arxiu".equals(segment)
				|| "file".equals(segment)
				|| "contenido".equals(segment)
				|| "contingut".equals(segment)
				|| "documento".equals(segment)
				|| "document".equals(segment)
				|| "pdf".equals(segment);
	}

	private static boolean esCampFirma(String segment) {
		return "firma".equals(segment)
				|| "signatura".equals(segment)
				|| "signature".equals(segment);
	}

	private static boolean esCampNomFitxer(String segment) {
		return "nombre".equals(segment)
				|| "nom".equals(segment)
				|| "filename".equals(segment)
				|| "nomfitxer".equals(segment)
				|| "nombrefichero".equals(segment);
	}

	private static boolean esCampMimeType(String segment) {
		return "mimetype".equals(segment)
				|| "mime".equals(segment)
				|| "contenttype".equals(segment)
				|| "tipusmime".equals(segment);
	}

	private static boolean esBase64Probable(String valor) {
		String text = normalitzar(valor);
		if (esBuit(text)) {
			return false;
		}

		String compactat = text.replaceAll("\\s+", "");
		if (compactat.length() < MIN_BASE64_LENGTH || compactat.length() % 4 != 0) {
			return false;
		}

		if (!compactat.matches("^[A-Za-z0-9+/]+={0,2}$")) {
			return false;
		}
		return true;
	}

	private static String normalitzar(String valor) {
		return valor == null ? null : valor.trim();
	}

	private static boolean esBuit(String valor) {
		return valor == null || valor.trim().isEmpty();
	}

	private static final class DocumentInfo {
		private String contingutBase64;
		private String nomFitxer;
		private String mimeType;

		private boolean teContingut() {
			return !esBuit(contingutBase64);
		}

		private String getMimeType() {
			if (!esBuit(mimeType)) {
				return mimeType;
			}
			if (esPdf()) {
				return "application/pdf";
			}
			return "application/octet-stream";
		}

		private boolean esPdf() {
			if (!esBuit(mimeType) && mimeType.toLowerCase(Locale.ROOT).contains("pdf")) {
				return true;
			}
			if (!esBuit(nomFitxer) && nomFitxer.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
				return true;
			}
			return !esBuit(contingutBase64) && contingutBase64.startsWith("JVBER");
		}
	}
}
