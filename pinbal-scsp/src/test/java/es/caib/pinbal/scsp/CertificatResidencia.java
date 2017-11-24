/**
 * 
 */
package es.caib.pinbal.scsp;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.Consentimiento;
import es.scsp.bean.common.DatosGenericos;
import es.scsp.bean.common.Emisor;
import es.scsp.bean.common.Funcionario;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Respuesta;
import es.scsp.bean.common.Solicitante;
import es.scsp.bean.common.SolicitudTransmision;
import es.scsp.bean.common.Solicitudes;
import es.scsp.bean.common.TipoDocumentacion;
import es.scsp.bean.common.Titular;
import es.scsp.bean.common.Transmision;
import es.scsp.client.ClienteUnico;
import es.scsp.common.dao.EmisorCertificadoDao;
import es.scsp.common.dao.ServicioDao;
import es.scsp.common.domain.core.EmisorCertificado;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.utils.DateUtils;
import es.scsp.common.utils.StaticContextSupport;

/**
 * @author josepg
 *
 */
public class CertificatResidencia {

	private static final String XMLNS_DATOS_ESPECIFICOS_V2 = "http://www.map.es/scsp/esquemas/datosespecificos";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ClienteUnico clienteUnico = getClienteUnico();
			String certificado = "pruebaPMI";
			String solicitanteCif = "G07896004";
			String solicitanteNombre = "FUNDACIO IBIT";
			String procedimentCodi = "IBIT_20101223_PRUEBA";
			String procedimentNom = "PROCEDIMIENTO DE PRUEBA FUNDACION IBIT";
			String funcionarioNombre = "MPR TESTER";
			String funcionarioNif = "12345678Z";

			String titularDocumentacion = "02634348C";
			String titularTipoDocumentacion = "DNI";

			String idPeticion = clienteUnico.getIDPeticion(certificado);

			Peticion peticion = new Peticion();
			peticion.setAtributos(new Atributos());
			peticion.setSolicitudes(new Solicitudes());
			peticion.getAtributos().setCodigoCertificado(certificado);
			peticion.getAtributos().setIdPeticion(idPeticion);
			String timeStamp = DateUtils.parseISO8601(new Date());
			peticion.getAtributos().setTimeStamp(timeStamp);
			peticion.setSolicitudes(new Solicitudes());
			peticion.getSolicitudes().setSolicitudTransmision(
					new ArrayList<SolicitudTransmision>());
			SolicitudTransmision st = new SolicitudTransmision();
			DatosGenericos datosGenericos = new DatosGenericos();
			st.setDatosGenericos(datosGenericos);
			//st.setDatosEspecificos(createDatosEspecificos());
			Emisor beanEmisor = new Emisor();
			beanEmisor.setNifEmisor(getCifEmisor(certificado));
			beanEmisor.setNombreEmisor(getNombreEmisor(certificado));
			datosGenericos.setEmisor(beanEmisor);
			Solicitante solicitante = new Solicitante();
			datosGenericos.setSolicitante(solicitante);
			solicitante.setIdentificadorSolicitante(solicitanteCif);
			solicitante.setNombreSolicitante(solicitanteNombre);
			solicitante.setFinalidad(procedimentCodi + "#::##::#" + procedimentNom);
			solicitante.setConsentimiento(Consentimiento.Si);
			solicitante.setFuncionario(new Funcionario());
			solicitante.getFuncionario().setNombreCompletoFuncionario(funcionarioNombre);
			solicitante.getFuncionario().setNifFuncionario(funcionarioNif);
			datosGenericos.setTitular(new Titular());
			datosGenericos.getTitular().setTipoDocumentacion(
					TipoDocumentacion.valueOf(titularTipoDocumentacion));
			datosGenericos.getTitular().setDocumentacion(titularDocumentacion);
			datosGenericos.setTransmision(new Transmision());
			datosGenericos.getTransmision().setCodigoCertificado(certificado);
			datosGenericos.getTransmision().setFechaGeneracion(timeStamp);
			datosGenericos.getTransmision().setIdSolicitud(idPeticion);
			datosGenericos.getTransmision().setIdTransmision(idPeticion);
			peticion.getSolicitudes().getSolicitudTransmision().add(st);
			peticion.getAtributos().setNumElementos(
					String.valueOf(
							peticion.getSolicitudes().getSolicitudTransmision().size()));
			Respuesta respuesta = clienteUnico.realizaPeticionSincrona(peticion);
			ByteArrayOutputStream baos = clienteUnico.generaJustificanteTransmision(
					respuesta.getTransmisiones().getTransmisionDatos().get(0),
					respuesta.getAtributos().getIdPeticion());
			OutputStream out = new FileOutputStream("/home/likewise-open/LIMIT_CECOMASA/josepg/Escriptori/temp/certificat.pdf");
			out.write(baos.toByteArray());
			out.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static ClienteUnico getClienteUnico() {
		ApplicationContext ctx = StaticContextSupport.getContextInstance();
		return (ClienteUnico)ctx.getBean("clienteUnicoProvisional");
	}

	private static String getCifEmisor(String certificado) {
		ServicioDao servicioDao = (ServicioDao)StaticContextSupport.getContextInstance().getBean("servicioDao");
		Servicio servicio = servicioDao.select(certificado);
		return servicio.getEmisor().getCif();
	}
	private static String getNombreEmisor(String certificado) {
		String cifEmisor = getCifEmisor(certificado);
		EmisorCertificadoDao emisorCertificadoDao = (EmisorCertificadoDao)StaticContextSupport.getContextInstance().getBean("emisorCertificadoDao");
		EmisorCertificado emisorCertificado = emisorCertificadoDao.selectByCif(cifEmisor);
		return emisorCertificado.getNombre();
	}

	@SuppressWarnings("unused")
	private static Element createDatosEspecificos() throws Exception {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		Document doc = fac.newDocumentBuilder().newDocument();
		Element datosEspecificos = doc.createElement("DatosEspecificos");
		datosEspecificos.setAttribute("xmlns", XMLNS_DATOS_ESPECIFICOS_V2);

		Element solicitanteDatos = doc.createElement("SolicitanteDatos");
		Element tipo = doc.createElement("Tipo");
		tipo.setTextContent("app");
		solicitanteDatos.appendChild(tipo);
		datosEspecificos.appendChild(solicitanteDatos);

		Element solicitud = doc.createElement("Solicitud");
		Element espanol = doc.createElement("Espanol");
		espanol.setTextContent("s");
		solicitud.appendChild(espanol);
		datosEspecificos.appendChild(solicitud);

		doc.appendChild(datosEspecificos);
		return doc.getDocumentElement();
	}

}
