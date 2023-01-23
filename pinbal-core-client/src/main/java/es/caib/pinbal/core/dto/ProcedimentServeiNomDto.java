package es.caib.pinbal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcedimentServeiNomDto implements Serializable {
	
	private static final long serialVersionUID = 6085171827817934826L;
	
	private String serveiCodi;
	private String procedimentCodi;
	@EqualsAndHashCode.Exclude
	private String serveiDescripcio;
	@EqualsAndHashCode.Exclude
	private String procedimentNom;


}
