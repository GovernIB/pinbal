/**
 * 
 */
package es.caib.pinbal.scsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.scsp.bean.common.Emisor;
import es.scsp.bean.common.Procedimiento;
import es.scsp.bean.common.Solicitante;
import es.scsp.bean.common.Titular;
import es.scsp.bean.common.TransmisionDatos;

/**
 * Helper per a generar el justificant.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class JustificantArbreHelper implements MessageSourceAware {

	private MessageSource messageSource;



	public ElementArbre generarArbre(
			TransmisionDatos transmision,
			String idPeticion,
			Locale locale) throws ParserConfigurationException {
		ElementArbre arrel = new ElementArbre("/");
		ElementArbre elementSolicitud = new ElementArbre(
				messageSource.getMessage(
						"dades.generiques.solicitud",
						null,
						"Solicitud",
						locale));
		elementSolicitud.addFill(
				new ElementArbre(
						messageSource.getMessage(
								"dades.generiques.identificador.peticio",
								null,
								"Identificador de la petición",
								locale),
						idPeticion));
		if (transmision.getDatosGenericos() != null) {
			elementSolicitud.addFill(
					new ElementArbre(
							messageSource.getMessage(
									"dades.generiques.identificador.solicitud",
									null,
									"Identificador de la solicitud",
									locale),
							transmision.getDatosGenericos().getTransmision().getIdSolicitud()));
			elementSolicitud.addFill(
					new ElementArbre(
							messageSource.getMessage(
									"dades.generiques.identificador.transmissio",
									null,
									"Identificador de la transmisión",
									locale),
							transmision.getDatosGenericos().getTransmision().getIdTransmision()));
			elementSolicitud.addFill(
					new ElementArbre(
							messageSource.getMessage(
									"dades.generiques.data.solicitud",
									null,
									"Fecha de la solicitud",
									locale),
							transmision.getDatosGenericos().getTransmision().getFechaGeneracion()));
			Emisor emisor = transmision.getDatosGenericos().getEmisor();
			if (emisor != null) {
				elementSolicitud.addFill(
						new ElementArbre(
								messageSource.getMessage(
										"dades.generiques.emisor",
										null,
										"Emisor",
										locale),
										emisor.getNifEmisor() + " - " + emisor.getNombreEmisor()));
			}
			Solicitante solicitante = transmision.getDatosGenericos().getSolicitante();
			if (solicitante != null) {
				elementSolicitud.addFill(
						new ElementArbre(
								messageSource.getMessage(
										"dades.generiques.solicitant",
										null,
										"Solicitante",
										locale),
								solicitante.getIdentificadorSolicitante() + " - " + solicitante.getNombreSolicitante()));
				elementSolicitud.addFill(
						new ElementArbre(
								messageSource.getMessage(
										"dades.generiques.unitat.tramitadora",
										null,
										"Unidad tramitadora",
										locale),
								getUnidadTramitadora(
										solicitante.getNombreSolicitante(),
										solicitante.getUnidadTramitadora())));
				if (solicitante.getFuncionario() != null) {
					elementSolicitud.addFill(
							new ElementArbre(
									messageSource.getMessage(
											"dades.generiques.funcionari",
											null,
											"Funcionario",
											locale),
									solicitante.getFuncionario().getNifFuncionario() + " - " + solicitante.getFuncionario().getNombreCompletoFuncionario()));
				}
				elementSolicitud.addFill(
						new ElementArbre(
								messageSource.getMessage(
										"dades.generiques.procediment",
										null,
										"Procedimiento",
										locale),
								getProcedimiento(
										solicitante.getFinalidad(),
										solicitante.getProcedimiento())));
				elementSolicitud.addFill(
						new ElementArbre(
								messageSource.getMessage(
										"dades.generiques.finalitat",
										null,
										"Finalidad",
										locale),
								getFinalidad(solicitante.getFinalidad())));
				elementSolicitud.addFill(
						new ElementArbre(
								messageSource.getMessage(
										"dades.generiques.consentiment",
										null,
										"Consentimiento",
										locale),
								solicitante.getConsentimiento().toString()));
				String expediente = getExpediente(
						solicitante.getFinalidad(),
						solicitante.getIdExpediente());
				if (expediente != null && !expediente.trim().isEmpty()) {
					elementSolicitud.addFill(
							new ElementArbre(
									messageSource.getMessage(
											"dades.generiques.expedient",
											null,
											"Expediente",
											locale),
									expediente));
				}
			}
			arrel.addFill(elementSolicitud);
			Titular titular = transmision.getDatosGenericos().getTitular();
			if (titular != null) {
				ElementArbre elementTitular = new ElementArbre(
						messageSource.getMessage(
								"dades.generiques.dades.personals",
								null,
								"Datos personales",
								locale));
				if (titular.getTipoDocumentacion() != null) {
					elementTitular.addFill(
							new ElementArbre(
									titular.getTipoDocumentacion().toString(),
									titular.getDocumentacion()));
				}
				String nombreCompleto = titular.getNombreCompleto();
				String nombre = titular.getNombre();
				String apellido1 = titular.getApellido1();
				String apellido2 = titular.getApellido2();
				if ((nombre != null && !nombre.isEmpty()) || (nombreCompleto != null && !nombreCompleto.isEmpty())) {
					elementTitular.addFill(
							new ElementArbre(
									messageSource.getMessage(
											"dades.generiques.nom",
											null,
											"Nombre",
											locale),
									(nombreCompleto != null && !nombreCompleto.isEmpty()) ? nombreCompleto : nombre));
				}
				if (nombre != null && !nombre.isEmpty()) {
					if (apellido1 != null && apellido1.length() > 0) {
						elementTitular.addFill(
								new ElementArbre(
										messageSource.getMessage(
												"dades.generiques.llinatge1",
												null,
												"Primer apellido",
												locale),
										apellido1));
					}
					if (apellido2 != null && apellido2.length() > 0) {
						elementTitular.addFill(
								new ElementArbre(
										messageSource.getMessage(
												"dades.generiques.llinatge2",
												null,
												"Segundo apellido",
												locale),
										apellido2));
					}
				}
				arrel.addFill(elementTitular);
			}
		}
		if (transmision.getDatosEspecificos() != null) {
			afegirDadesEspecifiques(
					arrel,
					(Element)transmision.getDatosEspecificos(),
					transmision.getDatosGenericos().getTransmision().getCodigoCertificado(),
					locale);
		}
		return arrel;
	}
	public void imprimirJustificantStdout(ElementArbre arrel) {
		printStdout(arrel, 0, true);
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}



	protected void afegirDadesEspecifiques(
			ElementArbre pare,
			Element doc,
			String codigoCertificado,
			Locale locale) throws ParserConfigurationException {
		if (doc.getElementsByTagNameNS("*", "DatosEspecificos") != null) {
			NodeList datosEspecificos = doc.getChildNodes();
			if (!teVarisNivells(doc)) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				org.w3c.dom.Document respuesta;
				respuesta = factory.newDocumentBuilder().newDocument();
				Element rootElement = respuesta.createElement("Respuesta");
				respuesta.appendChild(rootElement);
				NodeList hijos = doc.cloneNode(true).getChildNodes();
				if (hijos != null) {
					List<Node> nuevosHijos = new ArrayList<Node>();
					for (int i = 0; i < hijos.getLength(); i++) {
						nuevosHijos.add(hijos.item(i));
					}
					for (Node hijo : nuevosHijos) {
						respuesta.adoptNode(hijo);
						rootElement.appendChild(hijo);
					}
				}
				afegirNodeDadesEspecifiques(
						pare,
						respuesta.getDocumentElement(),
						codigoCertificado,
						locale);
				//almacenar(document, table, doc, 0, solicitud.getDatosGenericos().getTransmision().getCodigoCertificado());
			} else {
				for (int i = 0; i < datosEspecificos.getLength(); i++) {
					afegirNodeDadesEspecifiques(
							pare,
							datosEspecificos.item(i),
							codigoCertificado,
							locale);
					//almacenar(document, table, datosEspecificos.item(i), 0, solicitud.getDatosGenericos().getTransmision().getCodigoCertificado());
				}
			}
		} else {
			LOGGER.error("Les dades específiques no tenen l'estructura esperada");
		}
	}
	private void afegirNodeDadesEspecifiques(ElementArbre pare, Node node, String path, Locale locale) {
		if (!esDarrerNivellVisible(node))
			return;
		ElementArbre darrerElement = pare;
		boolean esText = node.getNodeName().equals("#text") || node.getNodeName().equals("#texts");
		String pathModificat = path + ".";
		if (!esText) {
			String nodeName = node.getNodeName();
			if (node.getPrefix() != null && !node.getPrefix().isEmpty())
				nodeName = nodeName.substring(node.getPrefix().length() + 1);
			String nodeValue = null;
			if (node.getNodeValue() != null)
				nodeValue = node.getNodeValue().trim();
			pathModificat = pathModificat + nodeName;
			String nodeNameTraduit = getNodeNameDadesEspecifiquesTraduit(
					pathModificat,
					nodeName,
					locale);
			String xpathDadaEspecifica;
			int indexPunt = pathModificat.indexOf(".");
			if (indexPunt != -1) {
				xpathDadaEspecifica = "DatosEspecificos" + pathModificat.substring(indexPunt);
			} else {
				xpathDadaEspecifica = "DatosEspecificos" + "." + pathModificat;
			}
			xpathDadaEspecifica = xpathDadaEspecifica.replace('.', '/');
			if (nodeValue == null || nodeValue.isEmpty()) {
				if (esNodeFillText(node)) {
					String fillText = node.getFirstChild().getNodeValue();
					if (fillText != null) {
						darrerElement = new ElementArbre(
								nodeNameTraduit,
								fillText.trim(),
								xpathDadaEspecifica);
					} else {
						darrerElement = new ElementArbre(nodeNameTraduit);
						darrerElement.setXpathDatoEspecifico(xpathDadaEspecifica);
					}
				} else {
					darrerElement = new ElementArbre(nodeNameTraduit);
					darrerElement.setXpathDatoEspecifico(xpathDadaEspecifica);
				}
			} else {
				darrerElement = new ElementArbre(
						nodeNameTraduit,
						nodeValue,
						xpathDadaEspecifica);
			}
			pare.addFill(darrerElement);
		}
		
		NodeList nodes = node.getChildNodes();
		if ((nodes != null) && (nodes.getLength() > 0)) {
			for (int i = 0; i < nodes.getLength(); i++)
				afegirNodeDadesEspecifiques(darrerElement, nodes.item(i), pathModificat, locale);
		}
	}

	private String getUnidadTramitadora(String nombreSolicitante, String unidad) {
		if (unidad != null)
			return unidad;
		try {
			int ini = nombreSolicitante.lastIndexOf("-");
			String unidadProcesada = nombreSolicitante.substring(ini + 1);
			return unidadProcesada;
		} catch (Exception e) {
			LOGGER.error("El nom del sol·licitant no té l'estructura esperada, no es pot recuperar la unidad tramitadora");
		}
		return " ";
	}
	private String getExpediente(String finalidad, String idexpediente) {
		if (idexpediente != null) return idexpediente;
		int fin = finalidad.lastIndexOf("#::#");
		int ini = finalidad.indexOf("#::#");
		try {
			String expedienteProcesado = finalidad.substring(ini + "#::#".length(), fin);
			return expedienteProcesado;
		} catch (Exception e) {
			LOGGER.error("El camp finalidad no té l'estructura esperada, no es pot recuperar l'expedient");
		}
		return finalidad;
	}
	private String getFinalidad(String finalidad) {
		if (finalidad.contains("#::#")) {
			int ini = finalidad.lastIndexOf("#::#");
			try {
				String finalidadProcesada = finalidad.substring(ini + "#::#".length());
				return finalidadProcesada;
			} catch (Exception e) {
				LOGGER.error("El camp finalidad no té l'estructura esperada");
				return finalidad;
			}
		}
		return finalidad;
	}
	private String getProcedimiento(String finalidad, Procedimiento procedimiento) {
		if (finalidad.contains("#::#")) {
			int ini = finalidad.indexOf("#::#");
			try {
				String procedimientoProcesado = finalidad.substring(0, ini);
				return procedimientoProcesado;
			} catch (Exception e) {
				LOGGER.error("El camp finalidad no té l'estructura esperada, no es pot recuperar el codigo de procedimiento");
				return finalidad;
			}
		}
		if (procedimiento == null) {
			LOGGER.error("No s'ha pogut recuperar el procedimiento de la resposta");
			return "";
		}
		return procedimiento.getNombreProcedimiento();
	}

	private boolean teVarisNivells(Node node) {
		NodeList fills = node.getChildNodes();
		if (fills == null || fills.getLength() == 0) return false;
		for (int i = 0; i < fills.getLength(); i++) {
			Node fillNivel1 = fills.item(i);
			if (fillNivel1 == null) return false;
			NodeList fillsNivell2 = fillNivel1.getChildNodes();
			if (fillsNivell2 == null || fillsNivell2.getLength() == 0) return false;
			for (int j = 0; j < fillsNivell2.getLength(); j++) {
				Node fillNivell2 = fillsNivell2.item(i);
				if (fillNivell2 == null) return false;
				NodeList fillsNivell3 = fillNivell2.getChildNodes();
				if (fillsNivell3 == null || fillsNivell3.getLength() == 0) return false;
			}
		}
		return true;
	}
	private boolean esDarrerNivellVisible(Node node) {
		if (node.getClass().getName().contains("TextImpl"))
			return true;
		if ((node.getChildNodes() != null) && (node.getChildNodes().getLength() > 0)) {
			if (node.getChildNodes().getLength() == 1) {
				return node.getFirstChild().getTextContent().trim().length() != 0;
			}
			return true;
		}
		return false;
	}
	private boolean esNodeFillText(Node node) {
		if (node.getFirstChild() == null) return false;
		if (node.getChildNodes().getLength() > 1) return false;
		return (node.getFirstChild().getClass().getName().contains("TextImpl"));
	}

	private String getNodeNameDadesEspecifiquesTraduit(
			String path,
			String nodeName,
			Locale locale) {
		String clau = "dades.especifiques." + path;
		if (path.contains("Estado.CodigoEstado"))
			clau = "dades.especifiques.estat.codi";
		else if (path.contains("Estado.LiteralError"))
			clau = "dades.especifiques.estat.literal";
		else if (path.contains("Estado"))
			clau = "dades.especifiques.estat";
		return messageSource.getMessage(
				clau,
				null,
				nodeName,
				locale);
	}

	private void printStdout(ElementArbre element, int nivell, boolean primer) {
		if (!primer) {
			System.out.print(">>>");
			for (int i = 0; i < nivell; i++)
				System.out.print("   ");
			if (element.getDescripcio() != null && !element.getDescripcio().isEmpty())
				System.out.println(element.getTitol() + ": " + element.getDescripcio());
			else
				System.out.println(element.getTitol());
		}
		if (element.teFills()) {
			for (ElementArbre fill: element.getFills())
				printStdout(fill, nivell + 1, false);
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ScspHelper.class);



	public class ElementArbre {
		private String titol;
		private String descripcio;
		private String xpathDatoEspecifico;
		private List<ElementArbre> fills;
		public ElementArbre(String titol) {
			super();
			this.titol = titol;
		}
		public ElementArbre(String titol, String descripcio) {
			super();
			this.titol = titol;
			this.descripcio = descripcio;
		}
		public ElementArbre(String titol, String descripcio, String xpathDatoEspecifico) {
			super();
			this.titol = titol;
			this.descripcio = descripcio;
			this.xpathDatoEspecifico = xpathDatoEspecifico;
		}
		public String getXpathDatoEspecifico() {
			return xpathDatoEspecifico;
		}
		public void setXpathDatoEspecifico(String xpathDatoEspecifico) {
			this.xpathDatoEspecifico = xpathDatoEspecifico;
		}
		public String getTitol() {
			return titol;
		}
		public void setTitol(String titol) {
			this.titol = titol;
		}
		public String getDescripcio() {
			return descripcio;
		}
		public void setDescripcio(String descripcio) {
			this.descripcio = descripcio;
		}
		public List<ElementArbre> getFills() {
			return fills;
		}
		public void setFills(List<ElementArbre> fills) {
			this.fills = fills;
		}
		public void addFill(ElementArbre fill) {
			if (fills == null)
				fills = new ArrayList<ElementArbre>();
			fills.add(fill);
		}
		public boolean teFills() {
			return fills != null && fills.size() > 0;
		}
	}

}
