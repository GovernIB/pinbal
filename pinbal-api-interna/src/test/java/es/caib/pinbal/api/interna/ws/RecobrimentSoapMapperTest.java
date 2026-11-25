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
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspFuncionario;
import es.caib.pinbal.client.recobriment.model.ScspProcedimiento;
import es.caib.pinbal.logic.intf.ws.model.WsAtributos;
import es.caib.pinbal.logic.intf.ws.model.WsConfirmacionPeticion;
import es.caib.pinbal.logic.intf.ws.model.WsConsentimiento;
import es.caib.pinbal.logic.intf.ws.model.WsDatosGenericos;
import es.caib.pinbal.logic.intf.ws.model.WsEmisor;
import es.caib.pinbal.logic.intf.ws.model.WsEstado;
import es.caib.pinbal.logic.intf.ws.model.WsFuncionario;
import es.caib.pinbal.logic.intf.ws.model.WsPeticion;
import es.caib.pinbal.logic.intf.ws.model.WsProcedimiento;
import es.caib.pinbal.logic.intf.ws.model.WsRespuesta;
import es.caib.pinbal.logic.intf.ws.model.WsSolicitante;
import es.caib.pinbal.logic.intf.ws.model.WsSolicitudTransmision;
import es.caib.pinbal.logic.intf.ws.model.WsSolicitudes;
import es.caib.pinbal.logic.intf.ws.model.WsTipoDocumentacion;
import es.caib.pinbal.logic.intf.ws.model.WsTitular;
import es.caib.pinbal.logic.intf.ws.model.WsTransmision;
import es.caib.pinbal.logic.intf.ws.model.WsTransmisionDatos;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecobrimentSoapMapperTest {

	private final RecobrimentSoapMapper mapper = new RecobrimentSoapMapper();

	@Test
	void toScspPeticionMapsSoapRequest() throws Exception {
		WsPeticion peticion = new WsPeticion();
		WsAtributos atributos = new WsAtributos();
		atributos.setIdPeticion("PET-1");
		atributos.setNumElementos("1");
		atributos.setCodigoCertificado("SERVEI");
		atributos.setTimeStamp("2026-11-25T10:00:00");
		WsEstado estadoPeticio = new WsEstado();
		estadoPeticio.setLiteralErrorSec("SEC");
		atributos.setEstado(estadoPeticio);
		peticion.setAtributos(atributos);

		WsSolicitudTransmision solicitudTransmision = new WsSolicitudTransmision();
		solicitudTransmision.setId("ID-1");
		WsDatosGenericos datosGenericos = new WsDatosGenericos();
		WsEmisor emisor = new WsEmisor();
		emisor.setNifEmisor("S0711001H");
		emisor.setNombreEmisor("Emissor");
		datosGenericos.setEmisor(emisor);
		WsSolicitante solicitante = new WsSolicitante();
		solicitante.setIdentificadorSolicitante("APP");
		solicitante.setNombreSolicitante("Aplicació");
		solicitante.setUnidadTramitadora("Unitat");
		solicitante.setCodigoUnidadTramitadora("A04000000");
		solicitante.setIdExpediente("EXP-1");
		solicitante.setFinalidad("Finalitat");
		solicitante.setConsentimiento(WsConsentimiento.Si);
		WsProcedimiento procedimiento = new WsProcedimiento();
		procedimiento.setCodProcedimiento("P1");
		procedimiento.setNombreProcedimiento("Procediment");
		solicitante.setProcedimiento(procedimiento);
		WsFuncionario funcionario = new WsFuncionario();
		funcionario.setNifFuncionario("12345678Z");
		funcionario.setNombreCompletoFuncionario("Funcionari");
		funcionario.setSeudonimo("PSEUDO");
		solicitante.setFuncionario(funcionario);
		datosGenericos.setSolicitante(solicitante);
		WsTitular titular = new WsTitular();
		titular.setTipoDocumentacion(WsTipoDocumentacion.DNI);
		titular.setDocumentacion("12345678Z");
		titular.setNombre("Nom");
		titular.setApellido1("Llinatge1");
		titular.setApellido2("Llinatge2");
		titular.setNombreCompleto("Nom Llinatge1 Llinatge2");
		datosGenericos.setTitular(titular);
		WsTransmision transmision = new WsTransmision();
		transmision.setCodigoCertificado("SERVEI");
		transmision.setIdSolicitud("SOL-1");
		transmision.setIdTransmision("T-1");
		transmision.setFechaGeneracion("2026-11-25");
		datosGenericos.setTransmision(transmision);
		solicitudTransmision.setDatosGenericos(datosGenericos);
		solicitudTransmision.setDatosEspecificos(element("<datosEspecificos><Consulta><valor>1</valor></Consulta></datosEspecificos>"));
		WsSolicitudes solicitudes = new WsSolicitudes();
		solicitudes.getSolicitudTransmision().add(solicitudTransmision);
		solicitudes.getSolicitudTransmision().add(null);
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
		// Camps que el mapper anterior (beans SCSP 5) no traspassava
		assertEquals(1, result.getSolicitudes().size());
		assertEquals("ID-1", solicitud.getId());
		assertEquals("SEC", result.getAtributos().getEstado().getLiteralErrorSec());
		assertEquals("A04000000", solicitud.getDatosGenericos().getSolicitante().getCodigoUnidadTramitadora());
		assertEquals("PSEUDO", solicitud.getDatosGenericos().getSolicitante().getFuncionario().getSeudonimo());
		assertEquals("P1", solicitud.getDatosGenericos().getSolicitante().getProcedimiento().getCodProcedimiento());
		assertEquals("T-1", solicitud.getDatosGenericos().getTransmision().getIdTransmision());
		assertEquals("Llinatge2", solicitud.getDatosGenericos().getTitular().getApellido2());
	}

	@Test
	void toScspPeticionNumElementosEsPassaTalQual() throws Exception {
		WsPeticion peticion = new WsPeticion();
		WsAtributos atributos = new WsAtributos();
		atributos.setNumElementos("");
		peticion.setAtributos(atributos);
		// El servei ja tracta numElementos buit o no numèric (RecobrimentHelper.parseNumElements)
		assertEquals("", mapper.toScspPeticion(peticion).getAtributos().getNumElementos());
		assertNull(mapper.toScspPeticion(null));
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

		estado.setLiteralErrorSec("SEC");
		estado.setTiempoEstimadoRespuesta(5);
		solicitante.setCodigoUnidadTramitadora("A04000000");
		ScspProcedimiento procedimiento = new ScspProcedimiento();
		procedimiento.setCodProcedimiento("P2");
		solicitante.setProcedimiento(procedimiento);
		ScspFuncionario funcionario = new ScspFuncionario();
		funcionario.setSeudonimo("PSEUDO");
		solicitante.setFuncionario(funcionario);
		transmisionDatos.setId("ID-2");

		WsRespuesta result = mapper.toRespuesta(scspRespuesta);

		assertEquals("PET-2", result.getAtributos().getIdPeticion());
		assertEquals("0003", result.getAtributos().getEstado().getCodigoEstado());
		assertEquals("1", result.getAtributos().getNumElementos());
		assertEquals("SEC", result.getAtributos().getEstado().getLiteralErrorSec());
		assertEquals(5, result.getAtributos().getEstado().getTiempoEstimadoRespuesta());
		WsTransmisionDatos mapped = result.getTransmisiones().getTransmisionDatos().get(0);
		assertEquals("ID-2", mapped.getId());
		assertEquals("S0711001H", mapped.getDatosGenericos().getEmisor().getNifEmisor());
		assertEquals(WsConsentimiento.Ley, mapped.getDatosGenericos().getSolicitante().getConsentimiento());
		assertEquals("A04000000", mapped.getDatosGenericos().getSolicitante().getCodigoUnidadTramitadora());
		assertEquals("P2", mapped.getDatosGenericos().getSolicitante().getProcedimiento().getCodProcedimiento());
		assertEquals("PSEUDO", mapped.getDatosGenericos().getSolicitante().getFuncionario().getSeudonimo());
		assertEquals(WsTipoDocumentacion.NIE, mapped.getDatosGenericos().getTitular().getTipoDocumentacion());
		assertTrue(mapped.getDatosEspecificos() instanceof Element);
		assertEquals("resposta", ((Element) mapped.getDatosEspecificos()).getLocalName());
	}

	@Test
	void toConfirmacionPeticionMapsAtributos() {
		ScspConfirmacionPeticion confirmacion = new ScspConfirmacionPeticion();
		ScspAtributos atributos = new ScspAtributos();
		atributos.setIdPeticion("PET-3");
		atributos.setNumElementos("3");
		ScspEstado estado = new ScspEstado();
		estado.setCodigoEstado("0002");
		atributos.setEstado(estado);
		confirmacion.setAtributos(atributos);

		WsConfirmacionPeticion result = mapper.toConfirmacionPeticion(confirmacion);

		assertEquals("PET-3", result.getAtributos().getIdPeticion());
		assertEquals("3", result.getAtributos().getNumElementos());
		assertEquals("0002", result.getAtributos().getEstado().getCodigoEstado());
		assertNull(mapper.toConfirmacionPeticion(null));
	}

	@Test
	void totsElsTipusDeDocumentIConsentimentsTenenEquivalent() {
		for (WsTipoDocumentacion tipus : WsTipoDocumentacion.values()) {
			assertEquals(tipus.name(), ScspTitular.ScspTipoDocumentacion.valueOf(tipus.name()).name());
		}
		for (ScspTitular.ScspTipoDocumentacion tipus : ScspTitular.ScspTipoDocumentacion.values()) {
			assertEquals(tipus.name(), WsTipoDocumentacion.valueOf(tipus.name()).name());
		}
		for (WsConsentimiento consentimiento : WsConsentimiento.values()) {
			assertEquals(consentimiento.name(), ScspSolicitante.ScspConsentimiento.valueOf(consentimiento.name()).name());
		}
	}

	private Element element(String xml) throws Exception {
		Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
		return document.getDocumentElement();
	}

}
