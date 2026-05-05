package es.caib.pinbal.logic.intf.base.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Informació d'una petició d'execució massiva.
 * 
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulkRequest<ID extends Serializable> {

	@NotNull
	@NotEmpty
	private ID[] ids;
	@NotNull
	private BulkActionType type;
	private String actionCode;
	private JsonNode params;

	public enum BulkActionType {
		PATCH,
		ACTION,
		DELETE
	}

}
