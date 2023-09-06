/**
 * 
 */
package es.caib.pinbal.client.recobriment.svdrrccmatrimoniows01;

import es.caib.pinbal.client.recobriment.ClientBase;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.SolicitudBaseSvdrrcc;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.util.List;

/**
 * Client del recobriment per a fer peticions al servei SCSP SVDRRCCMATRIMONIOWS01:
 * "Consulta de Matrimonio"
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientSvdrrccmatrimoniows01 extends ClientBase {

	private static final String SERVEI_CODI = "SVDRRCCMATRIMONIOWS01";

	public ClientSvdrrccmatrimoniows01(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase, usuari, contrasenya);
	}

	public ClientSvdrrccmatrimoniows01(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public ScspRespuesta peticionSincrona(
			List<SolicitudSvdrrccmatrimoniows01> solicituds) throws IOException {
		return basePeticionSincrona(SERVEI_CODI, solicituds);
	}

	public ScspConfirmacionPeticion peticionAsincrona(
			List<SolicitudSvdrrccmatrimoniows01> solicituds) throws IOException {
		return basePeticionAsincrona(SERVEI_CODI, solicituds);
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class SolicitudSvdrrccmatrimoniows01 extends SolicitudBaseSvdrrcc {

		@Override
		public String getDatosEspecificos() { // xml

			StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			xmlBuilder.append("<DatosEspecificos>");
			xmlBuilder.append("<Consulta>");

			if (titularDadesAdicionals != null) {
				xmlBuilder.append("<DatosAdicionalesTitular>");
				FetRegistral fetRegistral = titularDadesAdicionals.getFetregistral();
				if (fetRegistral != null && !fetRegistral.isEmpty()) {
					xmlBuilder.append("<HechoRegistral>");
					xmlBuilder.append(xmlOptionalDateParameter(fetRegistral.getData(), "Fecha"));
					if (fetRegistral.getMunicipi() != null && !fetRegistral.getMunicipi().isEmpty()) {
						xmlBuilder.append("<Municipio>");
						if (fetRegistral.getMunicipi().getCodi() != null && !fetRegistral.getMunicipi().getCodi().isEmpty()) {
							validFormat("Consulta.DatosAdicionalesTitular.HechoRegistral.Municipio.Codigo", fetRegistral.getMunicipi().getCodi(), "\\d{5}");
							xmlBuilder.append(xmlOptionalStringParameter(fetRegistral.getMunicipi().getCodi(), "Codigo"));
						} else {
							validLength("Consulta.DatosAdicionalesTitular.HechoRegistral.Municipio.Descripcion", fetRegistral.getMunicipi().getDescripcio(), 1, 200);
							validFormat("Consulta.DatosAdicionalesTitular.HechoRegistral.Municipio.Descripcion", fetRegistral.getMunicipi().getDescripcio(), "[:_0-9A-Za-zÁáÉéÍíÓóÚúÑñÀàÈèÌìÒòÙùÄäËëÏïÖöÜüÂâÊêÎîÔôÛûÃãÕõÅåØøÆÐÝýÞþÇç·'\\-\\s,.()/\\\\]{1,200}");
							xmlBuilder.append(xmlOptionalStringParameter(fetRegistral.getMunicipi().getDescripcio(), "Descripcion"));
						}
						xmlBuilder.append("</Municipio>");
					}
					xmlBuilder.append("</HechoRegistral>");
				}
				Naixement naixement = titularDadesAdicionals.getNaixement();
				if (naixement != null && !naixement.isEmpty()) {
					xmlBuilder.append("<Nacimiento>");
					xmlBuilder.append(xmlOptionalDateParameter(naixement.getData(), "Fecha"));
					validFormat("Consulta.DatosAdicionalesTitular.Nacimiento.Pais", naixement.getPaisCodi(), "\\d{3}");
					xmlBuilder.append(xmlOptionalStringParameter(naixement.getPaisCodi(), "Pais"));
					if (naixement.getProvincia() != null && !naixement.getProvincia().isEmpty()) {
						xmlBuilder.append("<Provincia>");
						if (naixement.getProvincia().getCodi() != null && !naixement.getProvincia().getCodi().isEmpty()) {
							validFormat("Consulta.DatosAdicionalesTitular.Nacimiento.Provincia.Codigo", naixement.getProvincia().getCodi(), "\\d{2}");
							xmlBuilder.append(xmlOptionalStringParameter(naixement.getProvincia().getCodi(), "Codigo"));
						} else {
							validLength("Consulta.DatosAdicionalesTitular.Nacimiento.Provincia.Descripcion", naixement.getProvincia().getDescripcio(), 1, 200);
							validFormat("Consulta.DatosAdicionalesTitular.Nacimiento.Provincia.Descripcion", naixement.getProvincia().getDescripcio(), "[:_0-9A-Za-zÁáÉéÍíÓóÚúÑñÀàÈèÌìÒòÙùÄäËëÏïÖöÜüÂâÊêÎîÔôÛûÃãÕõÅåØøÆÐÝýÞþÇç·'\\-\\s,.()/\\\\]{1,200}");
							xmlBuilder.append(xmlOptionalStringParameter(naixement.getProvincia().getDescripcio(), "Descripcion"));
						}
						xmlBuilder.append("</Provincia>");
					}
					if (naixement.getMunicipi() != null && !naixement.getMunicipi().isEmpty()) {
						xmlBuilder.append("<Municipio>");
						if (naixement.getMunicipi().getCodi() != null && !naixement.getMunicipi().getCodi().isEmpty()) {
							validFormat("Consulta.DatosAdicionalesTitular.Nacimiento.Municipio.Codigo", naixement.getMunicipi().getCodi(), "\\d{5}");
							xmlBuilder.append(xmlOptionalStringParameter(naixement.getMunicipi().getCodi(), "Codigo"));
						} else {
							validLength("Consulta.DatosAdicionalesTitular.Nacimiento.Municipio.Descripcion", naixement.getMunicipi().getDescripcio(), 1, 200);
							validFormat("Consulta.DatosAdicionalesTitular.Nacimiento.Municipio.Descripcion", naixement.getMunicipi().getDescripcio(), "[:_0-9A-Za-zÁáÉéÍíÓóÚúÑñÀàÈèÌìÒòÙùÄäËëÏïÖöÜüÂâÊêÎîÔôÛûÃãÕõÅåØøÆÐÝýÞþÇç·'\\-\\s,.()/\\\\]{1,200}");
							xmlBuilder.append(xmlOptionalStringParameter(naixement.getMunicipi().getDescripcio(), "Descripcion"));
						}
						xmlBuilder.append("</Municipio>");
					}
					xmlBuilder.append("</Nacimiento>");
				}
				xmlBuilder.append(xmlOptionalBooleanParameter(titularDadesAdicionals.isAusenciaSegonLlinatge(), "AusenciaSegundoApellido"));
				if (titularDadesAdicionals.getSexe() != null)
					xmlBuilder.append(xmlOptionalStringParameter(titularDadesAdicionals.getSexe().getValor(), "Sexo"));
				if (titularDadesAdicionals.getNomPare() != null) {
					validLength("Consulta.DatosAdicionalesTitular.NombrePadre", titularDadesAdicionals.getNomPare(), 1, 50);
					validFormat("Consulta.DatosAdicionalesTitular.NombrePadre", titularDadesAdicionals.getNomPare().toUpperCase(), "[A-ZÁÉÍÓÚÑÀÈÌÒÙÄËÏÖÜÂÊÎÔÛÃÕÅØÆÐÝÞÇ·'\\-\\s,.()/\\\\]{1,50}");
					xmlBuilder.append(xmlOptionalStringParameter(titularDadesAdicionals.getNomPare().toUpperCase(), "NombrePadre"));
				}
				if (titularDadesAdicionals.getNomMare() != null) {
					validLength("Consulta.DatosAdicionalesTitular.NombreMadre", titularDadesAdicionals.getNomMare(), 1, 50);
					validFormat("Consulta.DatosAdicionalesTitular.NombreMadre", titularDadesAdicionals.getNomMare().toUpperCase(), "[A-ZÁÉÍÓÚÑÀÈÌÒÙÄËÏÖÜÂÊÎÔÛÃÕÅØÆÐÝÞÇ·'\\-\\s,.()/\\\\]{1,50}");
					xmlBuilder.append(xmlOptionalStringParameter(titularDadesAdicionals.getNomMare().toUpperCase(), "NombreMadre"));
				}
				xmlBuilder.append("</DatosAdicionalesTitular>");
			}

			if (dadesRegistrals != null && !dadesRegistrals.isEmpty()) {
				xmlBuilder.append("<DatosRegistrales>");
				validLength("Consulta.DatosRegistrales.RegistroCivil", dadesRegistrals.getRegistreCivil(), 12, 12);
				validFormat("Consulta.DatosRegistrales.RegistroCivil", dadesRegistrals.getRegistreCivil(), "\\d*");
				xmlBuilder.append(xmlOptionalStringParameter(dadesRegistrals.getRegistreCivil(), "RegistroCivil"));
				validLength("Consulta.DatosRegistrales.Tomo", dadesRegistrals.getTom(), 1, 10);
				validFormat("Consulta.DatosRegistrales.Tomo", dadesRegistrals.getTom(), "([0-9]{5})([ ]|[_]|\\w)*");
				xmlBuilder.append(xmlOptionalStringParameter(dadesRegistrals.getTom(), "Tomo"));
				validLength("Consulta.DatosRegistrales.Pagina", dadesRegistrals.getPagina(), 1, 10);
				xmlBuilder.append(xmlOptionalStringParameter(dadesRegistrals.getPagina(), "Pagina"));
				xmlBuilder.append("</DatosRegistrales>");
			}

			xmlBuilder.append("</Consulta>");
			xmlBuilder.append("</DatosEspecificos>");
			return xmlBuilder.toString();
		}

	}

}
