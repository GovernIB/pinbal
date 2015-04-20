/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.pinbal.core.dto.ClauPrivadaDto;
import es.caib.pinbal.core.dto.ClauPublicaDto;
import es.caib.pinbal.core.dto.EmisorDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.webapp.validation.CodiServeiNoRepetit;

/**
 * Command que representa la informació d'un servei SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@CodiServeiNoRepetit(campCodi = "codi", campCreacio="creacio")
public class ServeiCommand {

	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=512)
	private String descripcio;
	@NotEmpty @Size(max=16)
	private String scspEmisor;
	@NotNull
	private Date scspFechaAlta;
	private Date scspFechaBaja;
	private int scspCaducidad = 0;
	@Size(max=256)
	private String scspUrlSincrona;
	@Size(max=256)
	private String scspUrlAsincrona;
	@Size(max=256)
	private String scspActionSincrona;
	@Size(max=256)
	private String scspActionAsincrona;
	@Size(max=256)
	private String scspActionSolicitud;
	@NotEmpty @Size(max=32)
	private String scspVersionEsquema;
	@NotEmpty @Size(max=16)
	private String scspTipoSeguridad;
	@NotEmpty @Size(max=256)
	private String scspClaveFirma;
	@Size(max=256)
	private String scspClaveCifrado;
	@Size(max=256)
	private String scspXpathCifradoSincrono;
	@Size(max=256)
	private String scspXpathCifradoAsincrono;
	@Size(max=32)
	private String scspAlgoritmoCifrado;
	@Size(max=32)
	private String scspValidacionFirma;
	@Size(max=8)
	private String scspPrefijoPeticion;
	@Size(max=256)
	private String scspEsquemas;
	@NotNull
	private int scspNumeroMaximoReenvios = 0;
	@NotNull
	private int scspMaxSolicitudesPeticion = 0;
	@Size(max=8)
	private String scspPrefijoIdTransmision;
	@Size(max=256)
	private String scspXpathCodigoError;
	@Size(max=256)
	private String scspXpathLiteralError;
	@NotNull
	private int scspTimeout;

	@Size(max=64)
	private String pinbalCustodiaCodi;
	@Size(max=64)
	private String pinbalRoleName;
	@Size(max=255)
	private String pinbalCondicioBusClass;
	private String pinbalEntitatTipus;
	private String pinbalJustificantTipus;
	private String pinbalJustificantXpath;
	private boolean pinbalPermesDocumentTipusDni = true;
	private boolean pinbalPermesDocumentTipusNif = true;
	private boolean pinbalPermesDocumentTipusCif = true;
	private boolean pinbalPermesDocumentTipusNie = true;
	private boolean pinbalPermesDocumentTipusPas = true;
	private boolean pinbalActiuCampNom = true;
	private boolean pinbalActiuCampLlinatge1 = true;
	private boolean pinbalActiuCampLlinatge2 = true;
	private boolean pinbalActiuCampNomComplet = true;
	private boolean pinbalActiuCampDocument = true;
	private boolean pinbalDocumentObligatori = true;
	private boolean pinbalComprovarDocument = true;

	private boolean creacio;



	public ServeiCommand() {
		scspFechaAlta = new Date();
		creacio = false;
	}
	public ServeiCommand(boolean creacio) {
		scspFechaAlta = new Date();
		this.creacio = creacio;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getScspEmisor() {
		return scspEmisor;
	}
	public void setScspEmisor(String scspEmisor) {
		this.scspEmisor = scspEmisor;
	}
	public Date getScspFechaAlta() {
		return scspFechaAlta;
	}
	public void setScspFechaAlta(Date scspFechaAlta) {
		this.scspFechaAlta = scspFechaAlta;
	}
	public Date getScspFechaBaja() {
		return scspFechaBaja;
	}
	public void setScspFechaBaja(Date scspFechaBaja) {
		this.scspFechaBaja = scspFechaBaja;
	}
	public int getScspCaducidad() {
		return scspCaducidad;
	}
	public void setScspCaducidad(int scspCaducidad) {
		this.scspCaducidad = scspCaducidad;
	}
	public String getScspUrlSincrona() {
		return scspUrlSincrona;
	}
	public void setScspUrlSincrona(String scspUrlSincrona) {
		this.scspUrlSincrona = scspUrlSincrona;
	}
	public String getScspUrlAsincrona() {
		return scspUrlAsincrona;
	}
	public void setScspUrlAsincrona(String scspUrlAsincrona) {
		this.scspUrlAsincrona = scspUrlAsincrona;
	}
	public String getScspActionSincrona() {
		return scspActionSincrona;
	}
	public void setScspActionSincrona(String scspActionSincrona) {
		this.scspActionSincrona = scspActionSincrona;
	}
	public String getScspActionAsincrona() {
		return scspActionAsincrona;
	}
	public void setScspActionAsincrona(String scspActionAsincrona) {
		this.scspActionAsincrona = scspActionAsincrona;
	}
	public String getScspActionSolicitud() {
		return scspActionSolicitud;
	}
	public void setScspActionSolicitud(String scspActionSolicitud) {
		this.scspActionSolicitud = scspActionSolicitud;
	}
	public String getScspVersionEsquema() {
		return scspVersionEsquema;
	}
	public void setScspVersionEsquema(String scspVersionEsquema) {
		this.scspVersionEsquema = scspVersionEsquema;
	}
	public String getScspTipoSeguridad() {
		return scspTipoSeguridad;
	}
	public void setScspTipoSeguridad(String scspTipoSeguridad) {
		this.scspTipoSeguridad = scspTipoSeguridad;
	}
	public String getScspClaveFirma() {
		return scspClaveFirma;
	}
	public void setScspClaveFirma(String scspClaveFirma) {
		this.scspClaveFirma = scspClaveFirma;
	}
	public String getScspClaveCifrado() {
		return scspClaveCifrado;
	}
	public void setScspClaveCifrado(String scspClaveCifrado) {
		this.scspClaveCifrado = scspClaveCifrado;
	}
	public String getScspXpathCifradoSincrono() {
		return scspXpathCifradoSincrono;
	}
	public void setScspXpathCifradoSincrono(String scspXpathCifradoSincrono) {
		this.scspXpathCifradoSincrono = scspXpathCifradoSincrono;
	}
	public String getScspXpathCifradoAsincrono() {
		return scspXpathCifradoAsincrono;
	}
	public void setScspXpathCifradoAsincrono(String scspXpathCifradoAsincrono) {
		this.scspXpathCifradoAsincrono = scspXpathCifradoAsincrono;
	}
	public String getScspAlgoritmoCifrado() {
		return scspAlgoritmoCifrado;
	}
	public void setScspAlgoritmoCifrado(String scspAlgoritmoCifrado) {
		this.scspAlgoritmoCifrado = scspAlgoritmoCifrado;
	}
	public String getScspValidacionFirma() {
		return scspValidacionFirma;
	}
	public void setScspValidacionFirma(String scspValidacionFirma) {
		this.scspValidacionFirma = scspValidacionFirma;
	}
	public String getScspPrefijoPeticion() {
		return scspPrefijoPeticion;
	}
	public void setScspPrefijoPeticion(String scspPrefijoPeticion) {
		this.scspPrefijoPeticion = scspPrefijoPeticion;
	}
	public String getScspEsquemas() {
		return scspEsquemas;
	}
	public void setScspEsquemas(String scspEsquemas) {
		this.scspEsquemas = scspEsquemas;
	}
	public int getScspNumeroMaximoReenvios() {
		return scspNumeroMaximoReenvios;
	}
	public void setScspNumeroMaximoReenvios(int scspNumeroMaximoReenvios) {
		this.scspNumeroMaximoReenvios = scspNumeroMaximoReenvios;
	}
	public int getScspMaxSolicitudesPeticion() {
		return scspMaxSolicitudesPeticion;
	}
	public void setScspMaxSolicitudesPeticion(int scspMaxSolicitudesPeticion) {
		this.scspMaxSolicitudesPeticion = scspMaxSolicitudesPeticion;
	}
	public String getScspPrefijoIdTransmision() {
		return scspPrefijoIdTransmision;
	}
	public void setScspPrefijoIdTransmision(String scspPrefijoIdTransmision) {
		this.scspPrefijoIdTransmision = scspPrefijoIdTransmision;
	}
	public String getScspXpathLiteralError() {
		return scspXpathLiteralError;
	}
	public void setScspXpathLiteralError(String scspXpathLiteralError) {
		this.scspXpathLiteralError = scspXpathLiteralError;
	}
	public String getScspXpathCodigoError() {
		return scspXpathCodigoError;
	}
	public void setScspXpathCodigoError(String scspXpathCodigoError) {
		this.scspXpathCodigoError = scspXpathCodigoError;
	}
	public int getScspTimeout() {
		return scspTimeout;
	}
	public void setScspTimeout(int scspTimeout) {
		this.scspTimeout = scspTimeout;
	}
	public String getPinbalCustodiaCodi() {
		return pinbalCustodiaCodi;
	}
	public void setPinbalCustodiaCodi(String pinbalCustodiaCodi) {
		this.pinbalCustodiaCodi = pinbalCustodiaCodi;
	}
	public String getPinbalRoleName() {
		return pinbalRoleName;
	}
	public void setPinbalRoleName(String pinbalRoleName) {
		this.pinbalRoleName = pinbalRoleName;
	}
	public String getPinbalCondicioBusClass() {
		return pinbalCondicioBusClass;
	}
	public void setPinbalCondicioBusClass(String pinbalCondicioBusClass) {
		this.pinbalCondicioBusClass = pinbalCondicioBusClass;
	}
	public String getPinbalEntitatTipus() {
		return pinbalEntitatTipus;
	}
	public void setPinbalEntitatTipus(String pinbalEntitatTipus) {
		this.pinbalEntitatTipus = pinbalEntitatTipus;
	}
	public String getPinbalJustificantTipus() {
		return pinbalJustificantTipus;
	}
	public void setPinbalJustificantTipus(String pinbalJustificantTipus) {
		this.pinbalJustificantTipus = pinbalJustificantTipus;
	}
	public String getPinbalJustificantXpath() {
		return pinbalJustificantXpath;
	}
	public void setPinbalJustificantXpath(String pinbalJustificantXpath) {
		this.pinbalJustificantXpath = pinbalJustificantXpath;
	}
	public boolean isPinbalPermesDocumentTipusDni() {
		return pinbalPermesDocumentTipusDni;
	}
	public void setPinbalPermesDocumentTipusDni(boolean pinbalPermesDocumentTipusDni) {
		this.pinbalPermesDocumentTipusDni = pinbalPermesDocumentTipusDni;
	}
	public boolean isPinbalPermesDocumentTipusNif() {
		return pinbalPermesDocumentTipusNif;
	}
	public void setPinbalPermesDocumentTipusNif(boolean pinbalPermesDocumentTipusNif) {
		this.pinbalPermesDocumentTipusNif = pinbalPermesDocumentTipusNif;
	}
	public boolean isPinbalPermesDocumentTipusCif() {
		return pinbalPermesDocumentTipusCif;
	}
	public void setPinbalPermesDocumentTipusCif(boolean pinbalPermesDocumentTipusCif) {
		this.pinbalPermesDocumentTipusCif = pinbalPermesDocumentTipusCif;
	}
	public boolean isPinbalPermesDocumentTipusNie() {
		return pinbalPermesDocumentTipusNie;
	}
	public void setPinbalPermesDocumentTipusNie(boolean pinbalPermesDocumentTipusNie) {
		this.pinbalPermesDocumentTipusNie = pinbalPermesDocumentTipusNie;
	}
	public boolean isPinbalPermesDocumentTipusPas() {
		return pinbalPermesDocumentTipusPas;
	}
	public void setPinbalPermesDocumentTipusPas(boolean pinbalPermesDocumentTipusPas) {
		this.pinbalPermesDocumentTipusPas = pinbalPermesDocumentTipusPas;
	}
	public boolean isPinbalActiuCampNom() {
		return pinbalActiuCampNom;
	}
	public void setPinbalActiuCampNom(boolean pinbalActiuCampNom) {
		this.pinbalActiuCampNom = pinbalActiuCampNom;
	}
	public boolean isPinbalActiuCampLlinatge1() {
		return pinbalActiuCampLlinatge1;
	}
	public void setPinbalActiuCampLlinatge1(boolean pinbalActiuCampLlinatge1) {
		this.pinbalActiuCampLlinatge1 = pinbalActiuCampLlinatge1;
	}
	public boolean isPinbalActiuCampLlinatge2() {
		return pinbalActiuCampLlinatge2;
	}
	public void setPinbalActiuCampLlinatge2(boolean pinbalActiuCampLlinatge2) {
		this.pinbalActiuCampLlinatge2 = pinbalActiuCampLlinatge2;
	}
	public boolean isPinbalActiuCampNomComplet() {
		return pinbalActiuCampNomComplet;
	}
	public void setPinbalActiuCampNomComplet(boolean pinbalActiuCampNomComplet) {
		this.pinbalActiuCampNomComplet = pinbalActiuCampNomComplet;
	}
	public boolean isPinbalActiuCampDocument() {
		return pinbalActiuCampDocument;
	}
	public void setPinbalActiuCampDocument(boolean pinbalActiuCampDocument) {
		this.pinbalActiuCampDocument = pinbalActiuCampDocument;
	}
	public boolean isPinbalDocumentObligatori() {
		return pinbalDocumentObligatori;
	}
	public void setPinbalDocumentObligatori(boolean pinbalDocumentObligatori) {
		this.pinbalDocumentObligatori = pinbalDocumentObligatori;
	}
	public boolean isPinbalComprovarDocument() {
		return pinbalComprovarDocument;
	}
	public void setPinbalComprovarDocument(boolean pinbalComprovarDocument) {
		this.pinbalComprovarDocument = pinbalComprovarDocument;
	}
	public boolean isCreacio() {
		return creacio;
	}
	public void setCreacio(boolean creacio) {
		this.creacio = creacio;
	}



	public static ServeiCommand asCommand(ServeiDto dto) {
		ServeiCommand command = CommandMappingHelper.getMapperFacade().map(
				dto,
				ServeiCommand.class);
		if (dto.getScspEmisor() != null)
			command.setScspEmisor(dto.getScspEmisor().getCif());
		if (dto.getScspClaveFirma() != null)
			command.setScspClaveFirma(dto.getScspClaveFirma().getAlies());
		if (dto.getScspClaveCifrado() != null)
			command.setScspClaveCifrado(dto.getScspClaveCifrado().getAlies());
		return command;
	}
	public static ServeiDto asDto(ServeiCommand command) {
		if (command.getPinbalEntitatTipus() != null && command.getPinbalEntitatTipus().isEmpty())
			command.setPinbalEntitatTipus(null);
		ServeiDto dto = CommandMappingHelper.getMapperFacade().map(
				command,
				ServeiDto.class);
		if (command.getScspEmisor() != null && !command.getScspEmisor().isEmpty()) {
			EmisorDto emisor = new EmisorDto();
			emisor.setCif(command.getScspEmisor());
			dto.setScspEmisor(emisor);
		} else {
			dto.setScspEmisor(null);
		}
		if (command.getScspClaveFirma() != null && !command.getScspClaveFirma().isEmpty()) {
			ClauPrivadaDto clauPrivada = new ClauPrivadaDto();
			clauPrivada.setAlies(command.getScspClaveFirma());
			dto.setScspClaveFirma(clauPrivada);
		} else {
			dto.setScspClaveFirma(null);
		}
		if (command.getScspClaveCifrado() != null && !command.getScspClaveCifrado().isEmpty()) {
			ClauPublicaDto clauPublica = new ClauPublicaDto();
			clauPublica.setAlies(command.getScspClaveCifrado());
			dto.setScspClaveCifrado(clauPublica);
		} else {
			dto.setScspClaveCifrado(null);
		}
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
