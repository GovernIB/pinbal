package es.caib.pinbal.scsp.binding;

import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.solicitud.SolicitudRespuesta;
import es.scsp.common.dao.ServeiDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link Jaxb2Marshaller} per al bean {@code getMarshaller} de la llibreria SCSP (ClienteUnico,
 * MessageRecover, JustificanteMaker, LoadContextReceiver).
 * <p>
 * Amb SCSP 5 les peticions es generen amb un únic JAXBContext que conté Peticion, Respuesta,
 * SolicitudRespuesta i ConfirmacionPeticion. JAXB declara a l'arrel tots els namespaces del context
 * (ns2..ns5) i escriu {@code <ns2:DatosEspecificos>} amb prefix. Algun emissor construeix la
 * resposta copiant aquests nodes i després no valida, per això es recupera la forma que tenien les
 * peticions amb SCSP 4:
 * <ul>
 *   <li>El marshalling de {@link Peticion} i {@link SolicitudRespuesta} es fa amb un JAXBContext que
 *   només conté la classe de l'objecte (sense els namespaces de la resta de missatges).</li>
 *   <li>El DOM resultant es normalitza amb {@link ScspNamespaceNormalizer} ({@code DatosEspecificos}
 *   amb xmlns per defecte, sense declaracions sobrants).</li>
 * </ul>
 * Tot es fa abans que la llibreria posi la petició dins el missatge SOAP i la signi. La resta
 * d'objectes i tot l'unmarshalling continuen amb el context complet, sense cap canvi.
 * <p>
 * El format de SCSP 4 només s'aplica als serveis que el tenen activat a la seva configuració
 * ({@code PBL_SERVEI_CONFIG.FORMAT_NS_SCSP4}). Per a la resta, o si no es pot consultar la
 * configuració, la petició es genera exactament igual que amb {@link Jaxb2Marshaller}.
 */
public class ScspJaxb2Marshaller extends Jaxb2Marshaller {

	private static final Set<Class<?>> CLASSES_NORMALITZADES = Collections.unmodifiableSet(
			new HashSet<>(Arrays.<Class<?>>asList(Peticion.class, SolicitudRespuesta.class)));

	private static final Log LOG = LogFactory.getLog(ScspJaxb2Marshaller.class);

	private final ConcurrentMap<Class<?>, JAXBContext> contextsPerClasse = new ConcurrentHashMap<>();

	private ServeiDao serveiDao;

	public void setServeiDao(ServeiDao serveiDao) {
		this.serveiDao = serveiDao;
	}

	@Override
	public Marshaller createMarshaller() {
		return new NormalitzadorMarshaller(super.createMarshaller());
	}

	boolean isNormalitzable(Object objecte) {
		if (objecte == null || !CLASSES_NORMALITZADES.contains(objecte.getClass())) {
			return false;
		}
		String serveiCodi = getCodigoCertificado(objecte);
		if (serveiCodi == null || serveiDao == null) {
			return false;
		}
		try {
			return serveiDao.serveiFormatNamespacesScsp4(serveiCodi);
		} catch (Exception ex) {
			LOG.warn("No s'ha pogut consultar el format de namespaces del servei " + serveiCodi
					+ ". La petició es genera amb el format per defecte.", ex);
			return false;
		}
	}

	private static String getCodigoCertificado(Object objecte) {
		if (objecte instanceof Peticion) {
			Peticion peticion = (Peticion) objecte;
			return peticion.getAtributos() != null ? peticion.getAtributos().getCodigoCertificado() : null;
		}
		if (objecte instanceof SolicitudRespuesta) {
			SolicitudRespuesta solicitud = (SolicitudRespuesta) objecte;
			return solicitud.getAtributos() != null ? solicitud.getAtributos().getCodigoCertificado() : null;
		}
		return null;
	}

	private Marshaller createMarshallerClasse(Class<?> classe) throws JAXBException {
		JAXBContext context = contextsPerClasse.get(classe);
		if (context == null) {
			context = JAXBContext.newInstance(classe);
			JAXBContext existent = contextsPerClasse.putIfAbsent(classe, context);
			if (existent != null) {
				context = existent;
			}
		}
		Marshaller marshaller = context.createMarshaller();
		initJaxbMarshaller(marshaller);
		return marshaller;
	}

	private interface Configuracio {
		void aplicar(Marshaller marshaller) throws JAXBException;
	}

	/**
	 * Marshaller retornat per {@link #createMarshaller()}. Delega en el marshaller del context
	 * complet; per a Peticion i SolicitudRespuesta usa el context específic i normalitza el DOM.
	 * La configuració que rep (propietats, adapters, etc.) s'aplica també al marshaller específic.
	 */
	private class NormalitzadorMarshaller implements Marshaller {

		private final Marshaller delegat;
		private final List<Configuracio> configuracions = new ArrayList<>();

		NormalitzadorMarshaller(Marshaller delegat) {
			this.delegat = delegat;
		}

		private Document marshalNormalitzat(Object objecte) throws JAXBException {
			Marshaller marshaller = createMarshallerClasse(objecte.getClass());
			for (Configuracio configuracio : configuracions) {
				configuracio.aplicar(marshaller);
			}
			Document document = newDocument();
			marshaller.marshal(objecte, new DOMResult(document));
			ScspNamespaceNormalizer.normalize(document.getDocumentElement());
			return document;
		}

		private void escriure(Document document, Result result) throws JAXBException {
			if (result instanceof DOMResult) {
				DOMResult domResult = (DOMResult) result;
				Node node = domResult.getNode();
				if (node == null) {
					domResult.setNode(document);
					return;
				}
				Document owner = node instanceof Document ? (Document) node : node.getOwnerDocument();
				Node importat = owner.importNode(document.getDocumentElement(), true);
				Node seguent = domResult.getNextSibling();
				if (seguent != null) {
					node.insertBefore(importat, seguent);
				} else {
					node.appendChild(importat);
				}
				return;
			}
			try {
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				Object encoding = delegat.getProperty(JAXB_ENCODING);
				transformer.setOutputProperty(OutputKeys.ENCODING, encoding != null ? encoding.toString() : "UTF-8");
				if (Boolean.TRUE.equals(delegat.getProperty(JAXB_FORMATTED_OUTPUT))) {
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				}
				if (Boolean.TRUE.equals(delegat.getProperty(JAXB_FRAGMENT))) {
					transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				}
				transformer.transform(new DOMSource(document), result);
			} catch (TransformerException ex) {
				throw new JAXBException(ex);
			}
		}

		private void marshalResult(Object objecte, Result result) throws JAXBException {
			if (isNormalitzable(objecte)) {
				escriure(marshalNormalitzat(objecte), result);
			} else {
				delegat.marshal(objecte, result);
			}
		}

		@Override
		public void marshal(Object jaxbElement, Result result) throws JAXBException {
			marshalResult(jaxbElement, result);
		}

		@Override
		public void marshal(Object jaxbElement, Node node) throws JAXBException {
			if (isNormalitzable(jaxbElement)) {
				escriure(marshalNormalitzat(jaxbElement), new DOMResult(node));
			} else {
				delegat.marshal(jaxbElement, node);
			}
		}

		@Override
		public void marshal(Object jaxbElement, OutputStream os) throws JAXBException {
			if (isNormalitzable(jaxbElement)) {
				escriure(marshalNormalitzat(jaxbElement), new StreamResult(os));
			} else {
				delegat.marshal(jaxbElement, os);
			}
		}

		@Override
		public void marshal(Object jaxbElement, File output) throws JAXBException {
			if (isNormalitzable(jaxbElement)) {
				escriure(marshalNormalitzat(jaxbElement), new StreamResult(output));
			} else {
				delegat.marshal(jaxbElement, output);
			}
		}

		@Override
		public void marshal(Object jaxbElement, Writer writer) throws JAXBException {
			if (isNormalitzable(jaxbElement)) {
				escriure(marshalNormalitzat(jaxbElement), new StreamResult(writer));
			} else {
				delegat.marshal(jaxbElement, writer);
			}
		}

		@Override
		public void marshal(Object jaxbElement, ContentHandler handler) throws JAXBException {
			if (isNormalitzable(jaxbElement)) {
				escriure(marshalNormalitzat(jaxbElement), new SAXResult(handler));
			} else {
				delegat.marshal(jaxbElement, handler);
			}
		}

		@Override
		public void marshal(Object jaxbElement, XMLStreamWriter writer) throws JAXBException {
			if (isNormalitzable(jaxbElement)) {
				escriure(marshalNormalitzat(jaxbElement), new StAXResult(writer));
			} else {
				delegat.marshal(jaxbElement, writer);
			}
		}

		@Override
		public void marshal(Object jaxbElement, XMLEventWriter writer) throws JAXBException {
			if (isNormalitzable(jaxbElement)) {
				escriure(marshalNormalitzat(jaxbElement), new StAXResult(writer));
			} else {
				delegat.marshal(jaxbElement, writer);
			}
		}

		@Override
		public Node getNode(Object contentTree) throws JAXBException {
			return delegat.getNode(contentTree);
		}

		@Override
		public void setProperty(final String name, final Object value) throws PropertyException {
			delegat.setProperty(name, value);
			configuracions.add(m -> m.setProperty(name, value));
		}

		@Override
		public Object getProperty(String name) throws PropertyException {
			return delegat.getProperty(name);
		}

		@Override
		public void setEventHandler(final ValidationEventHandler handler) throws JAXBException {
			delegat.setEventHandler(handler);
			configuracions.add(m -> m.setEventHandler(handler));
		}

		@Override
		public ValidationEventHandler getEventHandler() throws JAXBException {
			return delegat.getEventHandler();
		}

		@Override
		@SuppressWarnings("rawtypes")
		public void setAdapter(final XmlAdapter adapter) {
			delegat.setAdapter(adapter);
			configuracions.add(m -> m.setAdapter(adapter));
		}

		@Override
		@SuppressWarnings("rawtypes")
		public <A extends XmlAdapter> void setAdapter(final Class<A> type, final A adapter) {
			delegat.setAdapter(type, adapter);
			configuracions.add(m -> m.setAdapter(type, adapter));
		}

		@Override
		@SuppressWarnings("rawtypes")
		public <A extends XmlAdapter> A getAdapter(Class<A> type) {
			return delegat.getAdapter(type);
		}

		@Override
		public void setAttachmentMarshaller(final AttachmentMarshaller am) {
			delegat.setAttachmentMarshaller(am);
			configuracions.add(m -> m.setAttachmentMarshaller(am));
		}

		@Override
		public AttachmentMarshaller getAttachmentMarshaller() {
			return delegat.getAttachmentMarshaller();
		}

		@Override
		public void setSchema(final Schema schema) {
			delegat.setSchema(schema);
			configuracions.add(m -> m.setSchema(schema));
		}

		@Override
		public Schema getSchema() {
			return delegat.getSchema();
		}

		@Override
		public void setListener(final Listener listener) {
			delegat.setListener(listener);
			configuracions.add(m -> m.setListener(listener));
		}

		@Override
		public Listener getListener() {
			return delegat.getListener();
		}

	}

	private static Document newDocument() throws JAXBException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			return dbf.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException ex) {
			throw new JAXBException(ex);
		}
	}

}
