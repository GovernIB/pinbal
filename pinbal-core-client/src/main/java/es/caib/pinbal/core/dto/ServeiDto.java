package es.caib.pinbal.core.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Objecte DTO amb informació d'un servei SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiDto implements Serializable {

	public enum EntitatTipusDto {
		GOVERN,
		CONSELL,
		AJUNTAMENT
	}
	public enum JustificantTipusDto {
		GENERAT,
		ADJUNT_PDF_BASE64
	}

	private String codi;
	private String descripcio;

	private EmisorDto scspEmisor;
	private Date scspFechaAlta;
	private Date scspFechaBaja;
	private int scspCaducidad;
	private String scspUrlSincrona;
	private String scspUrlAsincrona;
	private String scspActionSincrona;
	private String scspActionAsincrona;
	private String scspActionSolicitud;
	private String scspVersionEsquema;
	private String scspTipoSeguridad;
	private ClauPrivadaDto scspClaveFirma;
	private ClauPublicaDto scspClaveCifrado;
	private String scspXpathCifradoSincrono;
	private String scspXpathCifradoAsincrono;
	private String scspAlgoritmoCifrado;
	private String scspValidacionFirma;
	private String scspPrefijoPeticion;
	private String scspEsquemas;
	private int scspNumeroMaximoReenvios;
	private int scspMaxSolicitudesPeticion;
	private String scspPrefijoIdTransmision;
	private String scspXpathLiteralError;
	private String scspXpathCodigoError;
	private int scspTimeout;
	private String procedimentCodi;

	private String pinbalCustodiaCodi;
	private String pinbalRoleName;
	private String pinbalCondicioBusClass;
	private EntitatTipusDto pinbalEntitatTipus;
	private JustificantTipusDto pinbalJustificantTipus;
	private String pinbalJustificantXpath;
	private boolean pinbalPermesDocumentTipusDni;
	private boolean pinbalPermesDocumentTipusNif;
	private boolean pinbalPermesDocumentTipusCif;
	private boolean pinbalPermesDocumentTipusNie;
	private boolean pinbalPermesDocumentTipusPas;
	private boolean pinbalActiuCampNom;
	private boolean pinbalActiuCampLlinatge1;
	private boolean pinbalActiuCampLlinatge2;
	private boolean pinbalActiuCampNomComplet;
	private boolean pinbalActiuCampDocument;
	private boolean pinbalDocumentObligatori;
	private boolean pinbalComprovarDocument;
	
	private String ajuda;
	private String fitxerAjudaNom;
	private String fitxerAjudaMimeType;
	private byte[] fitxerAjudaContingut;
	
	private Long numeroProcedimentsAssociats;

	// Propietat per a mostrar el check actiu als llistats
	private Boolean actiu = false;

	public ServeiDto() {
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
	public EmisorDto getScspEmisor() {
		return scspEmisor;
	}
	public void setScspEmisor(EmisorDto scspEmisor) {
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
	public ClauPrivadaDto getScspClaveFirma() {
		return scspClaveFirma;
	}
	public void setScspClaveFirma(ClauPrivadaDto scspClaveFirma) {
		this.scspClaveFirma = scspClaveFirma;
	}
	public ClauPublicaDto getScspClaveCifrado() {
		return scspClaveCifrado;
	}
	public void setScspClaveCifrado(ClauPublicaDto scspClaveCifrado) {
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
	public String getProcedimentCodi() {
		return procedimentCodi;
	}

	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
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
	public EntitatTipusDto getPinbalEntitatTipus() {
		return pinbalEntitatTipus;
	}
	public void setPinbalEntitatTipus(EntitatTipusDto pinbalEntitatTipus) {
		this.pinbalEntitatTipus = pinbalEntitatTipus;
	}
	public JustificantTipusDto getPinbalJustificantTipus() {
		return pinbalJustificantTipus;
	}
	public void setPinbalJustificantTipus(JustificantTipusDto pinbalJustificantTipus) {
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
	public String getAjuda() {
		return ajuda;
	}
	public void setAjuda(String ajuda) {
		this.ajuda = ajuda;
	}
	public String getAjudaHtml() {
		String ajudaHtml = ajuda;
		if (ajudaHtml != null) {
			ajudaHtml = ajudaHtml.replace("\n", "<br/>");
			ajudaHtml = ajudaHtml.replace(" ", "&nbsp;");
			ajudaHtml = ajudaHtml.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		return ajudaHtml;
	}
	public String getFitxerAjudaNom() {
		return fitxerAjudaNom;
	}
	public void setFitxerAjudaNom(String fitxerAjudaNom) {
		this.fitxerAjudaNom = fitxerAjudaNom;
	}
	public String getFitxerAjudaMimeType() {
		return fitxerAjudaMimeType;
	}
	public void setFitxerAjudaMimeType(String fitxerAjudaMimeType) {
		this.fitxerAjudaMimeType = fitxerAjudaMimeType;
	}
	public byte[] getFitxerAjudaContingut() {
		return fitxerAjudaContingut;
	}
	public void setFitxerAjudaContingut(byte[] fitxerAjudaContingut) {
		this.fitxerAjudaContingut = fitxerAjudaContingut;
	}

	public Long getNumeroProcedimentsAssociats() {
		return numeroProcedimentsAssociats;
	}
	public void setNumeroProcedimentsAssociats(Long numeroProcedimentsAssociats) {
		this.numeroProcedimentsAssociats = numeroProcedimentsAssociats;
	}

	public String getScspEmisorNom() {
		if (scspEmisor == null)
			return null;
		return scspEmisor.getNom();
	}
	public String getScspEmisorCif() {
		if (scspEmisor == null)
			return null;
		return scspEmisor.getCif();
	}

	public boolean isConsultaSimplePermesa() {
		return (scspUrlSincrona != null && !scspUrlSincrona.isEmpty());
	}
	public boolean isConsultaMultiplePermesa() {
		return (scspUrlAsincrona != null && !scspUrlAsincrona.isEmpty());
	}

	public boolean getActiu() {
		return actiu;
	}
	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = 5684791166191332426L;

}
