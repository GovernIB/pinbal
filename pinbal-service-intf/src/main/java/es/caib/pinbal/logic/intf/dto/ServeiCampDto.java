/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Dades d'un camp per al formulari d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@ToString
public class ServeiCampDto implements Serializable {

	public enum ServeiCampDtoTipus {
		TEXT,
		NUMERIC,
		DATA,
		ENUM,
		PROVINCIA,
		MUNICIPI_5,
		ETIQUETA,
		BOOLEA,
		DOC_IDENT,
		PAIS,
		MUNICIPI_3,
		ADJUNT_BINARI,
		ADJUNT_XML
	}

	public enum ServeiCampDtoValidacioOperacio {
		LT,
		LTE,
		GT,
		GTE,
		EQ,
		NEQ
	}
	public enum ServeiCampDtoValidacioDataTipus {
		DIES,
		MESOS,
		ANYS
	}

	private Long id;
	private String servei;
	private String path;
	private ServeiCampDtoTipus tipus;
	private String etiqueta;
	private String valorPerDefecte;
	private String comentari;
	private String[] enumDescripcions;
	private String dataFormat;
	private ServeiCampDto campPare;
	private String valorPare;
	private ServeiCampGrupDto grup;
	private boolean inicialitzar;
	private boolean obligatori;
	private boolean modificable;
	private boolean visible;
	private int ordre;
	private int mida;
	private String validacioRegexp;
	private Integer validacioMin;
	private Integer validacioMax;
	private ServeiCampDtoValidacioOperacio validacioDataCmpOperacio;
	private ServeiCampDto validacioDataCmpCamp2;
	private Integer validacioDataCmpNombre;
	private ServeiCampDtoValidacioDataTipus validacioDataCmpTipus;

	private boolean campRegla = false;


	public String getCampNom() {
		int index = path.lastIndexOf("/");
		return index != -1 ? path.substring(index + 1) : path;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
