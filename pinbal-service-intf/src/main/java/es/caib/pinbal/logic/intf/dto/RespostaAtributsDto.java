/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Atributs d'una resposta asíncrona.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString
public class RespostaAtributsDto implements Serializable {

	private String peticioId;
	private String numElements;
	private String timestamp;
	private String estatCodi;
	private String estatCodiSecundari;
	private Integer estatTempsEstimatResposta;
	private String estatErrorLiteral;

	private static final long serialVersionUID = -139254994389509932L;

}
