package es.caib.pinbal.api.interna.ws;

import es.caib.pinbal.client.recobriment.model.ScspAtributos;
import es.caib.pinbal.client.recobriment.model.ScspDatosGenericos;
import es.caib.pinbal.client.recobriment.model.ScspEmisor;
import es.caib.pinbal.client.recobriment.model.ScspEstado;
import es.caib.pinbal.client.recobriment.model.ScspPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.ScspSolicitante;
import es.caib.pinbal.client.recobriment.model.ScspSolicitud;
import es.caib.pinbal.client.recobriment.model.ScspTitular;
import es.caib.pinbal.client.recobriment.model.ScspTransmision;
import es.caib.pinbal.client.recobriment.model.ScspTransmisionDatos;
import es.scsp.bean.common.peticion.Atributos;
import es.scsp.bean.common.peticion.Consentimiento;
import es.scsp.bean.common.peticion.DatosGenericos;
import es.scsp.bean.common.peticion.Emisor;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.peticion.Solicitante;
import es.scsp.bean.common.peticion.SolicitudTransmision;
import es.scsp.bean.common.peticion.Solicitudes;
import es.scsp.bean.common.peticion.TipoDocumentacion;
import es.scsp.bean.common.peticion.Titular;
import es.scsp.bean.common.peticion.Transmision;
import es.scsp.bean.common.respuesta.Respuesta;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecobrimentSoapMapperTest {

	private final RecobrimentSoapMapper mapper = new RecobrimentSoapMapper();

	@Test
	void toScspPeticionMapsSoapRequest() throws Exception {
		Peticion peticion = new Peticion();
		Atributos atributos = new Atributos();
		atributos.setIdPeticion("PET-1");
		atributos.setNumElementos(1);
		atributos.setCodigoCertificado("SERVEI");
		peticion.setAtributos(atributos);

		SolicitudTransmision solicitudTransmision = new SolicitudTransmision();
		DatosGenericos datosGenericos = new DatosGenericos();
		Emisor emisor = new Emisor();
		emisor.setNifEmisor("S0711001H");
		datosGenericos.setEmisor(emisor);
		Solicitante solicitante = new Solicitante();
		solicitante.setIdentificadorSolicitante("APP");
		solicitante.setConsentimiento(Consentimiento.SI);
		datosGenericos.setSolicitante(solicitante);
		Titular titular = new Titular();
		titular.setTipoDocumentacion(TipoDocumentacion.DNI);
		titular.setDocumentacion("12345678Z");
		datosGenericos.setTitular(titular);
		Transmision transmision = new Transmision();
		transmision.setCodigoCertificado("SERVEI");
		transmision.setIdSolicitud("SOL-1");
		datosGenericos.setTransmision(transmision);
		solicitudTransmision.setDatosGenericos(datosGenericos);
		solicitudTransmision.setDatosEspecificos(element("<dades><valor>1</valor></dades>"));
		Solicitudes solicitudes = new Solicitudes();
		solicitudes.getSolicitudTransmision().add(solicitudTransmision);
		peticion.setSolicitudes(solicitudes);

		ScspPeticion result = mapper.toScspPeticion(peticion);

		assertEquals("PET-1", result.getAtributos().getIdPeticion());
		assertEquals("1", result.getAtributos().getNumElementos());
		ScspSolicitud solicitud = result.getSolicitudes().get(0);
		assertEquals("S0711001H", solicitud.getDatosGenericos().getEmisor().getNifEmisor());
		assertEquals(ScspSolicitante.ScspConsentimiento.Si,
				solicitud.getDatosGenericos().getSolicitante().getConsentimiento());
		assertEquals(ScspTitular.ScspTipoDocumentacion.DNI,
				solicitud.getDatosGenericos().getTitular().getTipoDocumentacion());
		assertTrue(solicitud.getDatosEspecificos().contains("<valor>1</valor>"));
	}

	@Test
	void toRespuestaMapsRestResponse() throws Exception {
		ScspRespuesta scspRespuesta = new ScspRespuesta();
		ScspAtributos atributos = new ScspAtributos();
		atributos.setIdPeticion("PET-2");
		atributos.setNumElementos("1");
		atributos.setCodigoCertificado("SERVEI");
		ScspEstado estado = new ScspEstado();
		estado.setCodigoEstado("0003");
		estado.setLiteralError("OK");
		atributos.setEstado(estado);
		scspRespuesta.setAtributos(atributos);

		ScspTransmisionDatos transmisionDatos = new ScspTransmisionDatos();
		ScspDatosGenericos datosGenericos = new ScspDatosGenericos();
		ScspEmisor emisor = new ScspEmisor();
		emisor.setNifEmisor("S0711001H");
		datosGenericos.setEmisor(emisor);
		ScspSolicitante solicitante = new ScspSolicitante();
		solicitante.setIdentificadorSolicitante("APP");
		solicitante.setConsentimiento(ScspSolicitante.ScspConsentimiento.Ley);
		datosGenericos.setSolicitante(solicitante);
		ScspTitular titular = new ScspTitular();
		titular.setTipoDocumentacion(ScspTitular.ScspTipoDocumentacion.NIE);
		titular.setDocumentacion("X1234567L");
		datosGenericos.setTitular(titular);
		ScspTransmision transmision = new ScspTransmision();
		transmision.setIdSolicitud("SOL-2");
		datosGenericos.setTransmision(transmision);
		transmisionDatos.setDatosGenericos(datosGenericos);
		transmisionDatos.setDatosEspecificos("<resposta><valor>2</valor></resposta>");
		scspRespuesta.setTransmisiones(Collections.singletonList(transmisionDatos));

		Respuesta result = mapper.toRespuesta(scspRespuesta);

		assertEquals("PET-2", result.getAtributos().getIdPeticion());
		assertEquals("0003", result.getAtributos().getEstado().getCodigoEstado());
		es.scsp.bean.common.respuesta.TransmisionDatos mapped =
				result.getTransmisiones().getTransmisionDatos().get(0);
		assertEquals("S0711001H", mapped.getDatosGenericos().getEmisor().getNifEmisor());
		assertEquals(es.scsp.bean.common.respuesta.Consentimiento.LEY,
				mapped.getDatosGenericos().getSolicitante().getConsentimiento());
		assertEquals(es.scsp.bean.common.respuesta.TipoDocumentacion.NIE,
				mapped.getDatosGenericos().getTitular().getTipoDocumentacion());
		assertNotNull(mapped.getDatosEspecificos());
	}

	private Element element(String xml) throws Exception {
		Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
		return document.getDocumentElement();
	}

}
