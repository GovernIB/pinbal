/**
 * 
 */
package es.caib.pinbal.core.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Objecte DTO amb informació d'una consulta SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter @Getter
public class ConsultaDto extends AbstractIdentificable<Long> implements Serializable {

	public enum EstatTipus {
		Pendent,
		Processant,
		Tramitada,
		Error;
		public static EstatTipus[] sortedValues() {
			return new EstatTipus[] {
					EstatTipus.Error,
					EstatTipus.Pendent,
					EstatTipus.Processant,
					EstatTipus.Tramitada};
		}
	}
	public enum JustificantEstat {
		PENDENT, // Hi ha justificant però encara no s'ha pogut generar/custodiar
		OK, // Hi ha justificant i ja està generat i custodiat
		ERROR, // Hi ha justificant però hi ha hagut errors al generar o custodiar
		NO_DISPONIBLE, // Aquesta consulta no te justificant associat
		OK_NO_CUSTODIA // Hi ha justificant i la custòdia està deshabilitada
	}
	public enum Consentiment {
		Si,
		Llei
	}
	public enum DocumentTipus {
		CIF,
		NIF,
		DNI,
		Passaport,
		NIE,
		NombreIdentificacio,
		Altres
	}

	private Long id;
	private Long pareId;

	private Long procedimentId;
	private String serveiCodi;

	private String scspPeticionId;
	private String scspSolicitudId;
	private String funcionariNom;
	private String funcionariNif;
	private Long entitatId;
	private String entitatNom;
	private String entitatCif;
	private String departamentNom;
	private String unitatTramitadoraCodi;
	private Consentiment consentiment;
	private String finalitat;

	private String expedientId;

	private DocumentTipus titularDocumentTipus;
	private String titularDocumentNum;
	private String titularNom;
	private String titularLlinatge1;
	private String titularLlinatge2;
	private String titularNomComplet;

	private String[] campsPeticioMultiple;
	private String[][] dadesPeticioMultiple;

	private Map<String, Object> dadesEspecifiques;

	private String serveiDescripcio;
	private String procedimentNom;
	private String procedimentCodi;
	private Date creacioData;
	private UsuariDto creacioUsuari;
	private String estat;
	private Date terData;

	private boolean recobriment;
	private boolean multiple;

	private JustificantEstat justificantEstat;
	private boolean custodiat = false;
	private String custodiaUrl;
	private String arxiuDocumentUuid;
	private String justificantError;

	private boolean hiHaPeticio = false;
	private boolean peticioGenerada = false;
	private String peticioXml;

	private boolean hiHaResposta = false;
	private Date respostaData;
	private String respostaEstadoCodigo;
	private String respostaEstadoError;
	private String respostaXml;
	private RespostaAtributsDto respostaAtributs;

	private String error;

	private String procedimentCodiNom;
	private String serveiCodiNom;

	public String getTitularDocumentNum() {
		if (this.titularDocumentNum != null) {
			return this.titularDocumentNum.toUpperCase();
		}
		return null;
	}
	
	public String getFuncionariNif() {
		if (this.funcionariNif != null) {
			return funcionariNif.toUpperCase();
		}
		return null;
		
	}

	public String getServeiDescripcio() {
		if (serveiDescripcio != null)
			return serveiDescripcio;
		else
			return serveiCodi;
	}

	public String getTitularNomSencer() {
		StringBuilder sb = new StringBuilder();
		if (titularNom != null && titularNom.length() > 0) {
			sb.append(titularNom);
		}
		if (titularLlinatge1 != null && titularLlinatge1.length() > 0) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(titularLlinatge1);
		}
		if (titularLlinatge2 != null && titularLlinatge2.length() > 0) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(titularLlinatge2);
		}
		return sb.toString();
	}

	public String getTitularDocumentAmbTipus() {
		if (titularDocumentNum != null) {
			if (titularDocumentTipus != null)
				return titularDocumentTipus.toString() + ": " + titularDocumentNum;
			else
				return "???: " + titularDocumentNum;
		} else {
			return null;
		}
	}

	public String getScspPeticionSolicitudId() {
		if (scspPeticionId.equals(scspSolicitudId))
			return scspPeticionId;
		else
			return scspPeticionId + "/" + scspSolicitudId;
	}

	public String getFuncionariNomAmbDocument() {
		StringBuilder sb = new StringBuilder();
		if (funcionariNom != null) {
			sb.append(funcionariNom);
		}
		if (funcionariNif != null) {
			if (funcionariNom != null && !funcionariNom.isEmpty())
				sb.append(" ");
			sb.append("(" + funcionariNif + ")");
		}
		return sb.toString();
	}
	
	public boolean isEstatPendent() {
		return estat.equals(EstatTipus.Pendent.name());
	}
	public boolean isEstatProcessant() {
		return estat.equals(EstatTipus.Processant.name());
	}
	public boolean isEstatTramitada() {
		return estat.equals(EstatTipus.Tramitada.name());
	}
	public boolean isEstatError() {
		return estat.equals(EstatTipus.Error.name());
	}

	public boolean isJustificantEstatPendent() {
		return justificantEstat.equals(JustificantEstat.PENDENT);
	}
	public boolean isJustificantEstatOk() {
		return justificantEstat.equals(JustificantEstat.OK) || justificantEstat.equals(JustificantEstat.OK_NO_CUSTODIA);
	}
	public boolean isJustificantEstatError() {
		return justificantEstat.equals(JustificantEstat.ERROR);
	}
	public boolean isJustificantEstatNoDisponible() {
		return justificantEstat.equals(JustificantEstat.NO_DISPONIBLE);
	}

	public static DocumentTipus[] getDocumentTipusValorsPerFormulari() {
		return DocumentTipus.values();
	}

	public String getProcedimentCodiNom() {
		return procedimentCodi + " - " + procedimentNom;
	}
	public String getServeiCodiNom() {
		return serveiCodi + " - " + serveiDescripcio;
	}

	public Map<String, Object> getDadesEspecifiquesMap() {
		// removeLeadingSlashesFromKeys
		Map<String, Object> processedMap = new HashMap<>();

		for(Map.Entry<String, Object> entry : dadesEspecifiques.entrySet()) {
			String key = entry.getKey();
			while(key.startsWith("/")) {
				key = key.substring(1);
			}
			processedMap.put(key, entry.getValue());
		}

		return processedMap;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}



	private static final long serialVersionUID = 2147586341949166824L;

}
