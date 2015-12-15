/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Objecte DTO amb informació d'una consulta SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConsultaDto implements Serializable {

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
		NIE
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

	private Map<String, String> dadesEspecifiques;

	private String serveiDescripcio;
	private String procedimentNom;
	private Date creacioData;
	private UsuariDto creacioUsuari;
	private String estat;
	private Date terData;

	private boolean recobriment;
	private boolean multiple;

	private JustificantEstat justificantEstat;
	private boolean custodiat = false;
	private String custodiaUrl;
	private String justificantError;

	private boolean hiHaPeticio = false;
	private String peticioXml;

	private boolean hiHaResposta = false;
	private Date respostaData;
	private String respostaEstadoCodigo;
	private String respostaEstadoError;
	private String respostaXml;
	private RespostaAtributsDto respostaAtributs;

	private String error;



	public ConsultaDto() {
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getPareId() {
		return pareId;
	}

	public void setPareId(Long pareId) {
		this.pareId = pareId;
	}

	public Long getProcedimentId() {
		return procedimentId;
	}
	public void setProcedimentId(Long procedimentId) {
		this.procedimentId = procedimentId;
	}
	public String getServeiCodi() {
		return serveiCodi;
	}
	public void setServeiCodi(String serveiCodi) {
		this.serveiCodi = serveiCodi;
	}
	public String getScspPeticionId() {
		return scspPeticionId;
	}
	public void setScspPeticionId(String scspPeticionId) {
		this.scspPeticionId = scspPeticionId;
	}
	public String getScspSolicitudId() {
		return scspSolicitudId;
	}
	public void setScspSolicitudId(String scspSolicitudId) {
		this.scspSolicitudId = scspSolicitudId;
	}
	public String getFuncionariNom() {
		return funcionariNom;
	}
	public void setFuncionariNom(String funcionariNom) {
		this.funcionariNom = funcionariNom;
	}
	public String getFuncionariNif() {
		return funcionariNif;
	}
	public void setFuncionariNif(String funcionariNif) {
		this.funcionariNif = funcionariNif;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public String getEntitatNom() {
		return entitatNom;
	}
	public void setEntitatNom(String entitatNom) {
		this.entitatNom = entitatNom;
	}
	public String getEntitatCif() {
		return entitatCif;
	}
	public void setEntitatCif(String entitatCif) {
		this.entitatCif = entitatCif;
	}
	public String getDepartamentNom() {
		return departamentNom;
	}
	public void setDepartamentNom(String departamentNom) {
		this.departamentNom = departamentNom;
	}
	public Consentiment getConsentiment() {
		return consentiment;
	}
	public void setConsentiment(Consentiment consentiment) {
		this.consentiment = consentiment;
	}
	public String getFinalitat() {
		return finalitat;
	}
	public void setFinalitat(String finalitat) {
		this.finalitat = finalitat;
	}
	public DocumentTipus getTitularDocumentTipus() {
		return titularDocumentTipus;
	}
	public void setTitularDocumentTipus(DocumentTipus titularDocumentTipus) {
		this.titularDocumentTipus = titularDocumentTipus;
	}
	public String getTitularDocumentNum() {
		return titularDocumentNum;
	}
	public void setTitularDocumentNum(String titularDocumentNum) {
		this.titularDocumentNum = titularDocumentNum;
	}
	public String getTitularNom() {
		return titularNom;
	}
	public void setTitularNom(String titularNom) {
		this.titularNom = titularNom;
	}
	public String getTitularLlinatge1() {
		return titularLlinatge1;
	}
	public void setTitularLlinatge1(String titularLlinatge1) {
		this.titularLlinatge1 = titularLlinatge1;
	}
	public String getTitularLlinatge2() {
		return titularLlinatge2;
	}
	public void setTitularLlinatge2(String titularLlinatge2) {
		this.titularLlinatge2 = titularLlinatge2;
	}
	public String getTitularNomComplet() {
		return titularNomComplet;
	}
	public void setTitularNomComplet(String titularNomComplet) {
		this.titularNomComplet = titularNomComplet;
	}
	public String[] getCampsPeticioMultiple() {
		return campsPeticioMultiple;
	}
	public void setCampsPeticioMultiple(String[] campsPeticioMultiple) {
		this.campsPeticioMultiple = campsPeticioMultiple;
	}
	public String[][] getDadesPeticioMultiple() {
		return dadesPeticioMultiple;
	}
	public void setDadesPeticioMultiple(String[][] dadesPeticioMultiple) {
		this.dadesPeticioMultiple = dadesPeticioMultiple;
	}
	public String getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(String expedientId) {
		this.expedientId = expedientId;
	}
	public Map<String, String> getDadesEspecifiques() {
		return dadesEspecifiques;
	}
	public void setDadesEspecifiques(Map<String, String> dadesEspecifiques) {
		this.dadesEspecifiques = dadesEspecifiques;
	}
	public String getServeiDescripcio() {
		if (serveiDescripcio != null)
			return serveiDescripcio;
		else
			return serveiCodi;
	}
	public void setServeiDescripcio(String serveiDescripcio) {
		this.serveiDescripcio = serveiDescripcio;
	}
	public String getProcedimentNom() {
		return procedimentNom;
	}
	public void setProcedimentNom(String procedimentNom) {
		this.procedimentNom = procedimentNom;
	}
	public Date getCreacioData() {
		return creacioData;
	}
	public void setCreacioData(Date creacioData) {
		this.creacioData = creacioData;
	}
	public UsuariDto getCreacioUsuari() {
		return creacioUsuari;
	}
	public void setCreacioUsuari(UsuariDto creacioUsuari) {
		this.creacioUsuari = creacioUsuari;
	}
	public String getEstat() {
		return estat;
	}
	public void setEstat(String estat) {
		this.estat = estat;
	}
	public Date getTerData() {
		return terData;
	}
	public void setTerData(Date terData) {
		this.terData = terData;
	}
	public boolean isRecobriment() {
		return recobriment;
	}
	public void setRecobriment(boolean recobriment) {
		this.recobriment = recobriment;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
	public JustificantEstat getJustificantEstat() {
		return justificantEstat;
	}
	public void setJustificantEstat(JustificantEstat justificantEstat) {
		this.justificantEstat = justificantEstat;
	}
	public boolean isCustodiat() {
		return custodiat;
	}
	public void setCustodiat(boolean custodiat) {
		this.custodiat = custodiat;
	}
	public String getCustodiaUrl() {
		return custodiaUrl;
	}
	public void setCustodiaUrl(String custodiaUrl) {
		this.custodiaUrl = custodiaUrl;
	}
	public String getJustificantError() {
		return justificantError;
	}
	public void setJustificantError(String justificantError) {
		this.justificantError = justificantError;
	}
	public boolean isHiHaPeticio() {
		return hiHaPeticio;
	}
	public void setHiHaPeticio(boolean hiHaPeticio) {
		this.hiHaPeticio = hiHaPeticio;
	}
	public String getPeticioXml() {
		return peticioXml;
	}
	public void setPeticioXml(String peticioXml) {
		this.peticioXml = peticioXml;
	}
	public boolean isHiHaResposta() {
		return hiHaResposta;
	}
	public void setHiHaResposta(boolean hiHaResposta) {
		this.hiHaResposta = hiHaResposta;
	}
	public Date getRespostaData() {
		return respostaData;
	}
	public void setRespostaData(Date respostaData) {
		this.respostaData = respostaData;
	}
	public String getRespostaEstadoCodigo() {
		return respostaEstadoCodigo;
	}
	public void setRespostaEstadoCodigo(String respostaEstadoCodigo) {
		this.respostaEstadoCodigo = respostaEstadoCodigo;
	}
	public String getRespostaEstadoError() {
		return respostaEstadoError;
	}
	public void setRespostaEstadoError(String respostaEstadoError) {
		this.respostaEstadoError = respostaEstadoError;
	}
	public String getRespostaXml() {
		return respostaXml;
	}
	public void setRespostaXml(String respostaXml) {
		this.respostaXml = respostaXml;
	}
	public RespostaAtributsDto getRespostaAtributs() {
		return respostaAtributs;
	}
	public void setRespostaAtributs(RespostaAtributsDto respostaAtributs) {
		this.respostaAtributs = respostaAtributs;
	}

	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
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
		/*/ Retorna tots els valors excepte el primer (CIF)
		return Arrays.copyOfRange(
				DocumentTipus.values(),
				1,
				DocumentTipus.values().length);*/
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}



	private static final long serialVersionUID = 2147586341949166824L;

}
