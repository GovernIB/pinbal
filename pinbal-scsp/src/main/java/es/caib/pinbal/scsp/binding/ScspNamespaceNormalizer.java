package es.caib.pinbal.scsp.binding;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Normalitza les declaracions de namespaces d'una petició SCSP generada per JAXB perquè tengui
 * la mateixa forma que generava SCSP 4 (JiBX):
 * <ul>
 *   <li>L'element {@code DatosEspecificos} va sense prefix i amb el seu propi {@code xmlns}
 *   per defecte, i els fills hereten aquest namespace sense redeclarar-lo.</li>
 *   <li>L'element arrel no conté declaracions de namespaces SCSP que no s'utilitzen.</li>
 * </ul>
 * Només es canvien prefixos i declaracions de namespaces: els noms qualificats (namespace + nom
 * local) de tots els elements i atributs, l'ordre, els valors i els atributs no canvien.
 * <p>
 * S'aplica sobre el DOM que es passa a la llibreria SCSP, abans que aquesta el posi dins el
 * missatge SOAP i el signi.
 */
public final class ScspNamespaceNormalizer {

	public static final String DATOS_ESPECIFICOS_NS = "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos";
	public static final String DATOS_ESPECIFICOS = "DatosEspecificos";

	/** Namespaces que JAXB pot declarar a l'arrel sense necessitat-los (classes del context SCSP). */
	static final Set<String> NAMESPACES_SCSP = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			DATOS_ESPECIFICOS_NS,
			"http://intermediacion.redsara.es/scsp/esquemas/V3/peticion",
			"http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta",
			"http://intermediacion.redsara.es/scsp/esquemas/V3/solicitudRespuesta",
			"http://intermediacion.redsara.es/scsp/esquemas/V3/confirmacionPeticion")));

	private static final String XMLNS_NS = XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
	private static final String DEFAULT_PREFIX = "";

	private ScspNamespaceNormalizer() {
	}

	public static void normalize(Element root) {
		if (root == null) {
			return;
		}
		for (Element datosEspecificos : findDatosEspecificos(root)) {
			Map<String, String> scopePare = inScope(datosEspecificos.getParentNode());
			datosEspecificos.setPrefix(null);
			fixup(datosEspecificos, scopePare);
		}
		removeUnusedRootDeclarations(root);
	}

	private static List<Element> findDatosEspecificos(Element root) {
		List<Element> result = new ArrayList<>();
		Deque<Element> pendents = new ArrayDeque<>();
		pendents.push(root);
		while (!pendents.isEmpty()) {
			Element e = pendents.pop();
			if (DATOS_ESPECIFICOS.equals(e.getLocalName()) && DATOS_ESPECIFICOS_NS.equals(e.getNamespaceURI())) {
				// No es baixa dins DatosEspecificos: el fixup ja tracta tot el subarbre
				result.add(e);
				continue;
			}
			for (Node n = e.getLastChild(); n != null; n = n.getPreviousSibling()) {
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					pendents.push((Element) n);
				}
			}
		}
		return result;
	}

	/**
	 * Recorre el subarbre: elimina les declaracions redundants (mateix binding que el que ja hi ha
	 * en àmbit) i afegeix les que falten perquè el prefix de cada element i atribut quedi lligat al
	 * seu namespace.
	 */
	private static void fixup(Element element, Map<String, String> scopePare) {
		Map<String, String> scope = new HashMap<>(scopePare);
		for (Attr declaracio : namespaceDeclarations(element)) {
			String prefix = declaredPrefix(declaracio);
			String uri = declaracio.getValue();
			if (uri.equals(scopePare.getOrDefault(prefix, ""))) {
				element.removeAttributeNode(declaracio);
			} else {
				scope.put(prefix, uri);
			}
		}
		ensureDeclared(element, element.getPrefix(), element.getNamespaceURI(), scope);
		NamedNodeMap attrs = element.getAttributes();
		List<Attr> atributsAmbNamespace = new ArrayList<>();
		for (int i = 0; i < attrs.getLength(); i++) {
			Attr attr = (Attr) attrs.item(i);
			if (!isNamespaceDeclaration(attr) && attr.getNamespaceURI() != null && attr.getPrefix() != null) {
				atributsAmbNamespace.add(attr);
			}
		}
		for (Attr attr : atributsAmbNamespace) {
			ensureDeclared(element, attr.getPrefix(), attr.getNamespaceURI(), scope);
		}
		for (Node n = element.getFirstChild(); n != null; n = n.getNextSibling()) {
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				fixup((Element) n, scope);
			}
		}
	}

	private static void ensureDeclared(Element element, String prefix, String uri, Map<String, String> scope) {
		String p = prefix != null ? prefix : DEFAULT_PREFIX;
		String u = uri != null ? uri : "";
		if (XMLConstants.XML_NS_PREFIX.equals(p) || XMLConstants.XMLNS_ATTRIBUTE.equals(p)) {
			return;
		}
		if (!p.isEmpty() && u.isEmpty()) {
			return;
		}
		if (!u.equals(scope.getOrDefault(p, ""))) {
			element.setAttributeNS(
					XMLNS_NS,
					p.isEmpty() ? XMLConstants.XMLNS_ATTRIBUTE : XMLConstants.XMLNS_ATTRIBUTE + ":" + p,
					u);
			scope.put(p, u);
		}
	}

	private static void removeUnusedRootDeclarations(Element root) {
		Set<String> prefixosUsats = new HashSet<>();
		collectUsedPrefixes(root, prefixosUsats);
		for (Attr declaracio : namespaceDeclarations(root)) {
			String prefix = declaredPrefix(declaracio);
			if (!prefix.isEmpty()
					&& NAMESPACES_SCSP.contains(declaracio.getValue())
					&& !prefixosUsats.contains(prefix)) {
				root.removeAttributeNode(declaracio);
			}
		}
	}

	private static void collectUsedPrefixes(Element root, Set<String> prefixos) {
		Deque<Element> pendents = new ArrayDeque<>();
		pendents.push(root);
		while (!pendents.isEmpty()) {
			Element e = pendents.pop();
			if (e.getPrefix() != null) {
				prefixos.add(e.getPrefix());
			}
			NamedNodeMap attrs = e.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++) {
				Attr attr = (Attr) attrs.item(i);
				if (isNamespaceDeclaration(attr)) {
					continue;
				}
				if (attr.getPrefix() != null) {
					prefixos.add(attr.getPrefix());
				}
				// Valors de tipus QName (p.ex. xsi:type="ns2:Tipus")
				String valor = attr.getValue();
				int dosPunts = valor != null ? valor.indexOf(':') : -1;
				if (dosPunts > 0) {
					prefixos.add(valor.substring(0, dosPunts).trim());
				}
			}
			for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					pendents.push((Element) n);
				}
			}
		}
	}

	/** Bindings prefix → namespace en àmbit al node indicat (declaracions dels ancestres inclòs ell). */
	private static Map<String, String> inScope(Node node) {
		Deque<Element> ancestres = new ArrayDeque<>();
		for (Node n = node; n != null && n.getNodeType() == Node.ELEMENT_NODE; n = n.getParentNode()) {
			ancestres.push((Element) n);
		}
		Map<String, String> scope = new HashMap<>();
		for (Element e : ancestres) {
			for (Attr declaracio : namespaceDeclarations(e)) {
				scope.put(declaredPrefix(declaracio), declaracio.getValue());
			}
		}
		return scope;
	}

	private static List<Attr> namespaceDeclarations(Element element) {
		NamedNodeMap attrs = element.getAttributes();
		List<Attr> result = new ArrayList<>();
		for (int i = 0; i < attrs.getLength(); i++) {
			Attr attr = (Attr) attrs.item(i);
			if (isNamespaceDeclaration(attr)) {
				result.add(attr);
			}
		}
		return result;
	}

	private static boolean isNamespaceDeclaration(Attr attr) {
		if (XMLNS_NS.equals(attr.getNamespaceURI())) {
			return true;
		}
		String name = attr.getName();
		return XMLConstants.XMLNS_ATTRIBUTE.equals(name) || name.startsWith(XMLConstants.XMLNS_ATTRIBUTE + ":");
	}

	private static String declaredPrefix(Attr declaracio) {
		String name = declaracio.getName();
		return name.startsWith(XMLConstants.XMLNS_ATTRIBUTE + ":")
				? name.substring(XMLConstants.XMLNS_ATTRIBUTE.length() + 1)
				: DEFAULT_PREFIX;
	}

}
