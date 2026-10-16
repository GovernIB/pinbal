/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import es.caib.pinbal.logic.intf.helper.ToStringHelper;

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
	@ToString.Exclude
	private String[] enumDescripcions;
	private String dataFormat;
	@ToString.Exclude
	private ServeiCampDto campPare;
	private String valorPare;
	@ToString.Exclude
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
	@ToString.Exclude
	private ServeiCampDto validacioDataCmpCamp2;
	private Integer validacioDataCmpNombre;
	private ServeiCampDtoValidacioDataTipus validacioDataCmpTipus;

	private boolean campRegla = false;


	public String getCampNom() {
		int index = path.lastIndexOf("/");
		return index != -1 ? path.substring(index + 1) : path;
	}

	@ToString.Include(name = "enumDescripcions.length")
	private Integer enumDescripcionsLongitud() {
		return ToStringHelper.longitud(enumDescripcions);
	}

	@ToString.Include(name = "campPare.id")
	private Long campPareId() {
		return campPare != null ? campPare.getId() : null;
	}

	@ToString.Include(name = "grup.id")
	private Long grupId() {
		return grup != null ? grup.getId() : null;
	}

	@ToString.Include(name = "validacioDataCmpCamp2.id")
	private Long validacioDataCmpCamp2Id() {
		return validacioDataCmpCamp2 != null ? validacioDataCmpCamp2.getId() : null;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
