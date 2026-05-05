package es.caib.pinbal.logic.intf.base.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Informació d'una resposta d'execució massiva.
 * 
 * @author Límit Tecnologies
 */
@Getter
@Setter
@AllArgsConstructor
public class BulkResponse<ID extends Serializable> {

	private long successCount;
	private long errorCount;
	private BulkResponseItem<ID>[] items;

	@Getter
	@AllArgsConstructor
	public static class BulkResponseItem<ID extends Serializable> {
		private ID id;
		private Serializable actionResult;
		private boolean error;
		private String errorMessage;
	}

}
