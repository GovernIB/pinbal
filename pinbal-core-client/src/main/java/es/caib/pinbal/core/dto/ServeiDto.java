package es.caib.pinbal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * Objecte DTO amb informació d'un servei SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServeiDto extends AbstractIdentificable<Long> implements Serializable {

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
	private Long id;

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
	private boolean pinbalUnitatDir3FromEntitat;
	private String pinbalUnitatDir3;
	private boolean pinbalDocumentObligatori;
	private boolean pinbalComprovarDocument;
	private boolean activaGestioXsd;
	private Integer maxPeticionsMinut;
	
	private String ajuda;
	private String fitxerAjudaNom;
	private String fitxerAjudaMimeType;
	private byte[] fitxerAjudaContingut;
	
	private boolean pinbalIniDadesEspecifiques;
	private boolean pinbalAddDadesEspecifiques;

	
	private List<ServeiXsdDto> fitxersXsd;
	
	private boolean pinbalIniDadesExpecifiques = false;
		
	private Long numeroProcedimentsAssociats;

	// Propietat per a mostrar el check actiu als llistats
	private Boolean actiu = false;

	private boolean useAutoClasse = true;
	private boolean enviarSolicitant = false;

	public String getAjudaHtml() {
		String ajudaHtml = ajuda;
		if (ajudaHtml != null) {
			ajudaHtml = ajudaHtml.replace("\n", "<br/>");
			ajudaHtml = ajudaHtml.replace(" ", "&nbsp;");
			ajudaHtml = ajudaHtml.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		return ajudaHtml;
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
	public String getCodiUrlEncoded() throws UnsupportedEncodingException {
		return URLEncoder.encode(this.codi, "UTF-8") ;
	}
	public boolean isConsultaSimplePermesa() {
		return (scspUrlSincrona != null && !scspUrlSincrona.isEmpty());
	}
	public boolean isConsultaMultiplePermesa() {
		return (scspUrlAsincrona != null && !scspUrlAsincrona.isEmpty());
	}

	public String getDescripcioAmbCodi() {
		return descripcio + " (" + codi + ")";
	}
	public String getCodiNom() {
		return codi + " - " + descripcio;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = 5684791166191332426L;

}
