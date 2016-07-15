/**
 * 
 */
package es.caib.pinbal.scsp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAll;
import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaEnumerationFacet;
import org.apache.ws.commons.schema.XmlSchemaFacet;
import org.apache.ws.commons.schema.XmlSchemaGroupBase;
import org.apache.ws.commons.schema.XmlSchemaMaxLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaMinLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import es.caib.pinbal.scsp.tree.Node;
import es.caib.pinbal.scsp.tree.Tree;
import es.scsp.common.domain.Servicio;

/**
 * Mètodes d'ajuda per a la gestió de dades específiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class XmlHelper {

	public Tree<DadesEspecifiquesNode> getArbrePerDadesEspecifiques(
			final Servicio servicio) throws Exception {
		Tree<DadesEspecifiquesNode> tree = new Tree<DadesEspecifiquesNode>();
		InputStream is = getInputStreamXsdDadesEspecifiques(servicio);
		if (is != null) {
			XmlSchemaCollection schemaCol = new XmlSchemaCollection();
			schemaCol.setSchemaResolver(
					new URIResolver() {
						@Override
						public InputSource resolveEntity(
								String targetNamespace,
								String schemaLocation,
								String baseUri) {
							try {
								return new InputSource(
										getScspResourceInputStream(
												servicio,
												schemaLocation));
							} catch (Exception ex) {
								return null;
							}
						}
					});
			XmlSchema schema = schemaCol.read(new StreamSource(is), null);
			XmlSchemaElement datosEspecificosElement = schema.getElementByName("DatosEspecificos");
			List<String> path = new ArrayList<String>();
			afegirElement(
					servicio,
					tree,
					null,
					path,
					schema,
					datosEspecificosElement);
		}
		return tree;
	}

	public Map<String, String> getDadesEspecifiquesXml(
			String xmlPeticion) throws Exception {
		Map<String, String> dades = new HashMap<String, String>();
		if (xmlPeticion != null) {
			Document doc = xmlToDocument(
					new ByteArrayInputStream(xmlPeticion.getBytes()));
			NodeList nl = doc.getElementsByTagNameNS("*", "DatosEspecificos");
			if (nl.getLength() > 0) {
				List<String> path = new ArrayList<String>();
				recorrerDocument(
						nl.item(0),
						path,
						dades,
						true);
			}
		}
		return dades;
	}

	public Element crearDadesEspecifiques(
			Servicio servicio,
			Map<String, String> dadesEspecifiques) throws Exception {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		Document doc = fac.newDocumentBuilder().newDocument();
		Element datosEspecificos = doc.createElement("DatosEspecificos");
		datosEspecificos.setAttribute(
				"xmlns",
				getXmlnsPerDadesEspecifiques(servicio));
		if (dadesEspecifiques != null) {
			for (Node<DadesEspecifiquesNode> node: getArbrePerDadesEspecifiques(servicio).toList()) {
				String path = node.getData().getPath().substring(1);
				String valor = dadesEspecifiques.get(path);
				if (valor != null && valor.length() > 0) {
					String[] pathParts = path.substring("DatosEspecificos/".length()).split("/");
					Element elementActual = datosEspecificos;
					for (String pathPart: pathParts) {
						NodeList nodeList = elementActual.getElementsByTagName(pathPart);
						Element elementTrobat = null;
						if (nodeList.getLength() > 0) {
							for (int i = 0; i < nodeList.getLength(); i++) {
								org.w3c.dom.Node n = nodeList.item(i);
								if (n.getParentNode().equals(elementActual)) {
									elementTrobat = (Element)n;
									break;
								}
							}
						}
						if (elementTrobat == null) {
							Element nou = doc.createElement(pathPart);
							elementActual.appendChild(nou);
							elementActual = nou;
						} else {
							elementActual = elementTrobat;
						}
					}
					elementActual.setTextContent(valor);
				}
			}
		}
		doc.appendChild(datosEspecificos);
		Element docElement = doc.getDocumentElement();
		LOGGER.debug("Dades específiques generades: " + nodeToString(docElement));
		return docElement;
	}

	public Element copiarDadesEspecifiquesRecobriment(
			Servicio servicio,
			Element datosEspecificosRebuts) throws Exception {
		Element datosEspecificos = crearDadesEspecifiques(servicio, null);
		NodeList nodes = datosEspecificosRebuts.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			datosEspecificos.appendChild(
					datosEspecificos.getOwnerDocument().importNode(
							nodes.item(i),
							true));
		}
		return datosEspecificos;
	}

	public String getXmlSolicitudTransmision(
			String xmlPeticio,
			String idSolicitud) throws Exception {
		Document doc = xmlToDocument(new ByteArrayInputStream(xmlPeticio.getBytes()));
		NodeList nlDg = doc.getElementsByTagName("IdSolicitud");
		for (int i = 0; i < nlDg.getLength(); i++) {
			org.w3c.dom.Node n = nlDg.item(i);
			if (n.getParentNode().getNodeName().equals("Transmision") && idSolicitud.equals(n.getTextContent())) {
				return nodeToString(n.getParentNode().getParentNode().getParentNode());
			}
		}
		return null;
	}

	public String getXmlPeticion(
			String xmlPeticio,
			String idPeticion) throws Exception {
		Document doc = xmlToDocument(new ByteArrayInputStream(xmlPeticio.getBytes()));
		NodeList nlDg = doc.getElementsByTagName("IdPeticion");
		for (int i = 0; i < nlDg.getLength(); i++) {
			org.w3c.dom.Node n = nlDg.item(i);
			if (n.getParentNode().getNodeName().equals("Atributos") && idPeticion.equals(n.getTextContent())) {
				return nodeToString(n.getParentNode().getParentNode());
			}
		}
		return null;
	}



	@SuppressWarnings("unchecked")
	private void afegirElement(
			Servicio servicio,
			Tree<DadesEspecifiquesNode> tree,
			Node<DadesEspecifiquesNode> pare,
			List<String> path,
			XmlSchema schema,
			XmlSchemaElement element) {
		long minOccurs = element.getMinOccurs();
		long maxOccurs = element.getMaxOccurs();
		DadesEspecifiquesNode dadesNode = new DadesEspecifiquesNode();
		Node<DadesEspecifiquesNode> node = new Node<DadesEspecifiquesNode>(dadesNode);
		if (!path.isEmpty())
			dadesNode.setPath(pathToString(path) + "/" + element.getName());
		else
			dadesNode.setPath("/" + element.getName());
		path.add(element.getName());
		if (element.getSchemaType() instanceof XmlSchemaComplexType) {
			XmlSchemaComplexType complexType = (XmlSchemaComplexType)element.getSchemaType();
			if (complexType.getParticle() instanceof XmlSchemaGroupBase) {
				XmlSchemaGroupBase gb = (XmlSchemaGroupBase)complexType.getParticle();
				int groupType = 0;
				if (gb instanceof XmlSchemaAll)
					groupType = DadesEspecifiquesNode.GROUP_TYPE_ALL;
				else if (gb instanceof XmlSchemaChoice)
					groupType = DadesEspecifiquesNode.GROUP_TYPE_CHOICE;
				else if (gb instanceof XmlSchemaSequence)
					groupType = DadesEspecifiquesNode.GROUP_TYPE_SCHEMA;
				dadesNode.setNom(element.getName());
				dadesNode.setComplex(true);
				dadesNode.setGroupType(groupType);
				dadesNode.setGroupMin(minOccurs);
				dadesNode.setGroupMax(maxOccurs);
				Iterator<?> iti = gb.getItems().getIterator();
				while (iti.hasNext()) {
					Object item = iti.next();
					if (item instanceof XmlSchemaElement) {
						XmlSchemaElement elementPerAfegir = (XmlSchemaElement)item;
						if (elementPerAfegir.getRefName() != null) {
							elementPerAfegir = schema.getElementByName(elementPerAfegir.getRefName());
						}
						if (elementPerAfegir != null) {
							afegirElement(
									servicio,
									tree,
									node,
									path,
									schema,
									elementPerAfegir);
						}
					} else if (item instanceof XmlSchemaSequence) {
						XmlSchemaSequence seq = (XmlSchemaSequence)item;
						for (int i = 0; i < seq.getItems().getCount(); i++) {
							XmlSchemaObject itemn = seq.getItems().getItem(i);
							if (itemn instanceof XmlSchemaElement) {
								XmlSchemaElement elementPerAfegir = (XmlSchemaElement)itemn;
								if (elementPerAfegir.getRefName() != null)
									elementPerAfegir = schema.getElementByName(elementPerAfegir.getRefName());
								afegirElement(
										servicio,
										tree,
										node,
										path,
										schema,
										elementPerAfegir);
							} else {
								LOGGER.error("No s'ha pogut processar element dins seqüència de les dades específiques (servei=" + servicio.getCodCertificado() + ", className=" + itemn.getClass().getName() + ")");
							}
						}
					} else if (item instanceof XmlSchemaChoice) {
						XmlSchemaChoice cho = (XmlSchemaChoice)item;
						for (int i = 0; i < cho.getItems().getCount(); i++) {
							XmlSchemaObject itemn = cho.getItems().getItem(i);
							if (itemn instanceof XmlSchemaElement) {
								XmlSchemaElement elementPerAfegir = (XmlSchemaElement)itemn;
								if (elementPerAfegir.getRefName() != null)
									elementPerAfegir = schema.getElementByName(elementPerAfegir.getRefName());
								afegirElement(
										servicio,
										tree,
										node,
										path,
										schema,
										elementPerAfegir);
							} else {
								LOGGER.error("No s'ha pogut processar element dins choice de les dades específiques (servei=" + servicio.getCodCertificado() + ", className=" + itemn.getClass().getName() + ")");
							}
						}
					} else {
						LOGGER.error("No s'ha pogut processar element de les dades específiques (servei=" + servicio.getCodCertificado() + ", className=" + item.getClass().getName() + ")");
					}
				}
			}
		} else {
			dadesNode.setNom(element.getName());
			dadesNode.setComplex(false);
			XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType)element.getSchemaType();
			if (simpleType.getContent() != null && simpleType.getContent() instanceof XmlSchemaSimpleTypeRestriction) {
				XmlSchemaSimpleTypeRestriction restriction = (XmlSchemaSimpleTypeRestriction)simpleType.getContent();
				Iterator<XmlSchemaFacet> it = restriction.getFacets().getIterator();
				while (it.hasNext()) {
					XmlSchemaFacet facet = (XmlSchemaFacet)it.next();
					if (facet instanceof XmlSchemaMinLengthFacet) {
						XmlSchemaMinLengthFacet mlFacet = (XmlSchemaMinLengthFacet)facet;
						dadesNode.setMinLength(new Integer((String)mlFacet.getValue()));
					} else if (facet instanceof XmlSchemaMaxLengthFacet) {
						XmlSchemaMaxLengthFacet mlFacet = (XmlSchemaMaxLengthFacet)facet;
						dadesNode.setMaxLength(new Integer((String)mlFacet.getValue()));
					} else if (facet instanceof XmlSchemaEnumerationFacet) {
						XmlSchemaEnumerationFacet enumFacet = (XmlSchemaEnumerationFacet)facet;
						dadesNode.addEnumValue((String)enumFacet.getValue());
					}
				}
			}
		}
		path.remove(path.size() - 1);
		if (pare == null)
			tree.setRootElement(node);
		else
			pare.addChild(node);
	}

	private void recorrerDocument(
			org.w3c.dom.Node node,
			List<String> path,
			Map<String, String> dades,
			boolean incloureAlPath) {
		if (incloureAlPath) {
			if (node.getPrefix() != null) {
				path.add(node.getNodeName().substring(
						node.getPrefix().length() + 1));
			} else {
				path.add(node.getNodeName());
			}
		}
		if (node.hasChildNodes()) {
			NodeList fills = node.getChildNodes();
			for (int i = 0; i < fills.getLength(); i++) {
				org.w3c.dom.Node fill = fills.item(i);
				if (fill.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					recorrerDocument(
							fill,
							path,
							dades,
							true);
				}
				if (fill.getNodeType() == org.w3c.dom.Node.TEXT_NODE && fills.getLength() == 1) {
					dades.put(
							pathToString(path),
							node.getTextContent());
				}
			}
		} else {
			dades.put(
					pathToString(path),
					node.getTextContent());
		}
		if (incloureAlPath) {
			path.remove(path.size() - 1);
		}
	}
	private Document xmlToDocument(InputStream is) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = null;
		builder = factory.newDocumentBuilder();
		Document doc = builder.parse(is);
		is.close();
		return doc;
	}
	private String nodeToString(org.w3c.dom.Node node) throws Exception {
		StringWriter writer = new StringWriter();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(node), new StreamResult(writer));
		return writer.toString();
	}
	private String pathToString(List<String> path) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < path.size(); i++) {
			sb.append("/");
			sb.append(path.get(i));
		}
		return sb.toString();
	}

	private InputStream getScspResourceInputStream(
			Servicio servicio,
			String arxiuNom) throws Exception {
		String versionEsquema = servicio.getVersionEsquema();
		if (versionEsquema != null) {
			int index = versionEsquema.lastIndexOf("V");
			if (index != -1) {
				String esquema = "/schemas/" + servicio.getCodCertificado() + "v" + versionEsquema.substring(index + 1) + "/" + arxiuNom;
				InputStream is = getClass().getResourceAsStream(esquema);
				if (is == null) {
					esquema = "/schemas/" + servicio.getCodCertificado() + "/" + arxiuNom;
					is = getClass().getResourceAsStream(esquema);
				}
				return is;
			} else {
				throw new Exception("No s'ha pogut obtenir l'esquema pel servicio (codi=" + servicio.getCodCertificado() + ")");
			}
		} else {
			throw new Exception("No s'ha pogut obtenir la versió de l'esquema pel servicio (codi=" + servicio.getCodCertificado() + ")");
		}
	}
	private String getXmlnsPerDadesEspecifiques(
			Servicio servicio) throws Exception {
		InputStream is = getInputStreamXsdDadesEspecifiques(servicio);
		if (is == null)
			return null;
		XmlSchemaCollection schemaCol = new XmlSchemaCollection();
		XmlSchema schema = schemaCol.read(new StreamSource(is), null);
		XmlSchemaElement datosEspecificosElement = schema.getElementByName("DatosEspecificos");
		return datosEspecificosElement.getQName().getNamespaceURI();
	}

	private InputStream getInputStreamXsdDadesEspecifiques(
			Servicio servicio) throws Exception {
		InputStream is = getScspResourceInputStream(
				servicio,
				"datos-especificos.xsd");
		if (is == null) {
			is = getScspResourceInputStream(
					servicio,
					"datos-especificos-entrada.xsd"); // ADWSRDT1
		}
		if (is == null) {
			is = getScspResourceInputStream(
					servicio,
					"datos-especificos-ent.xsd"); // VDRSFWS02
		}
		if (is == null) {
			is = getScspResourceInputStream(
					servicio,
					"peticion_datos-especificos.xsd"); // AEATCCC1
		}
		return is;
	}



	public class DadesEspecifiquesNode {
		public static final int GROUP_TYPE_ALL = 0;
		public static final int GROUP_TYPE_CHOICE = 1;
		public static final int GROUP_TYPE_SCHEMA = 2;
		private String nom;
		private boolean complex = false;
		private int groupType = 0;
		private long groupMin = 0;
		private long groupMax = 0;
		private int minLength = 0;
		private int maxLength = 0;
		private List<String> enumValues = new ArrayList<String>();
		private String path;
		public String getNom() {
			return nom;
		}
		public void setNom(String nom) {
			this.nom = nom;
		}
		public boolean isComplex() {
			return complex;
		}
		public void setComplex(boolean complex) {
			this.complex = complex;
		}
		public int getGroupType() {
			return groupType;
		}
		public void setGroupType(int groupType) {
			this.groupType = groupType;
		}
		public long getGroupMin() {
			return groupMin;
		}
		public void setGroupMin(long groupMin) {
			this.groupMin = groupMin;
		}
		public long getGroupMax() {
			return groupMax;
		}
		public void setGroupMax(long groupMax) {
			this.groupMax = groupMax;
		}
		public int getMinLength() {
			return minLength;
		}
		public void setMinLength(int minLength) {
			this.minLength = minLength;
		}
		public int getMaxLength() {
			return maxLength;
		}
		public void setMaxLength(int maxLength) {
			this.maxLength = maxLength;
		}
		public List<String> getEnumValues() {
			return enumValues;
		}
		public void setEnumValues(List<String> enumValues) {
			this.enumValues = enumValues;
		}
		public void addEnumValue(String value) {
			this.enumValues.add(value);
		}
		public boolean isEnum() {
			return enumValues.size() > 0;
		}
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		public String toString() {
			if (complex) {
				String groupTypeStr = "";
				switch (groupType) {
				case 0:
					groupTypeStr = "A";
					break;
				case 1:
					groupTypeStr = "C";
					break;
				case 2:
					groupTypeStr = "S";
					break;
				}
				return nom + " (C, " + groupTypeStr + ", " + groupMin + ", " + groupMax + ")";
			} else {
				if (isEnum()) {
					return nom + " (S, enum" + enumValues + ")";
				} else {
					return nom + " (S, " + minLength + ", " + maxLength + ")";
				}
			}
		}
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XmlHelper.class);

}
