/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Dades d'un camp per al formulari d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
		ADJUNT
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
	private boolean obligatori;
	private boolean modificable;
	private boolean visible;
	private int ordre;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getServei() {
		return servei;
	}
	public void setServei(String servei) {
		this.servei = servei;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public ServeiCampDtoTipus getTipus() {
		return tipus;
	}
	public void setTipus(ServeiCampDtoTipus tipus) {
		this.tipus = tipus;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public String getValorPerDefecte() {
		return valorPerDefecte;
	}
	public void setValorPerDefecte(String valorPerDefecte) {
		this.valorPerDefecte = valorPerDefecte;
	}
	public String getComentari() {
		return comentari;
	}
	public void setComentari(String comentari) {
		this.comentari = comentari;
	}
	public String[] getEnumDescripcions() {
		return enumDescripcions;
	}
	public void setEnumDescripcions(String[] enumDescripcions) {
		this.enumDescripcions = enumDescripcions;
	}
	public String getDataFormat() {
		return dataFormat;
	}
	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}
	public ServeiCampDto getCampPare() {
		return campPare;
	}
	public void setCampPare(ServeiCampDto campPare) {
		this.campPare = campPare;
	}
	public String getValorPare() {
		return valorPare;
	}
	public void setValorPare(String valorPare) {
		this.valorPare = valorPare;
	}
	public ServeiCampGrupDto getGrup() {
		return grup;
	}
	public void setGrup(ServeiCampGrupDto grup) {
		this.grup = grup;
	}
	public boolean isObligatori() {
		return obligatori;
	}
	public void setObligatori(boolean obligatori) {
		this.obligatori = obligatori;
	}
	public boolean isModificable() {
		return modificable;
	}
	public void setModificable(boolean modificable) {
		this.modificable = modificable;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

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
