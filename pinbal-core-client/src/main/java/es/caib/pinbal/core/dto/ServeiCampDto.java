/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Dades d'un camp per al formulari d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
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
		if (index != -1)
			return path.substring(index + 1);
		else
			return path;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
