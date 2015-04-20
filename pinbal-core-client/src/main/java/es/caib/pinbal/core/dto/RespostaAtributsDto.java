/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Atributs d'una resposta asíncrona.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaAtributsDto implements Serializable {

	private String peticioId;
	private String numElements;
	private String timestamp;
	private String estatCodi;
	private String estatCodiSecundari;
	private Integer estatTempsEstimatResposta;
	private String estatErrorLiteral;

	public String getPeticioId() {
		return peticioId;
	}
	public void setPeticioId(String peticioId) {
		this.peticioId = peticioId;
	}
	public String getNumElements() {
		return numElements;
	}
	public void setNumElements(String numElements) {
		this.numElements = numElements;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getEstatCodi() {
		return estatCodi;
	}
	public void setEstatCodi(String estatCodi) {
		this.estatCodi = estatCodi;
	}
	public String getEstatCodiSecundari() {
		return estatCodiSecundari;
	}
	public void setEstatCodiSecundari(String estatCodiSecundari) {
		this.estatCodiSecundari = estatCodiSecundari;
	}
	public Integer getEstatTempsEstimatResposta() {
		return estatTempsEstimatResposta;
	}
	public void setEstatTempsEstimatResposta(Integer estatTempsEstimatResposta) {
		this.estatTempsEstimatResposta = estatTempsEstimatResposta;
	}
	public String getEstatErrorLiteral() {
		return estatErrorLiteral;
	}
	public void setEstatErrorLiteral(String estatErrorLiteral) {
		this.estatErrorLiteral = estatErrorLiteral;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
