package es.caib.pinbal.plugin.unitat;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Informació d'una unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeDir3 implements Serializable, Comparable<NodeDir3> {

	@NonNull
	@JsonProperty("codigo")
	private String codi;
	@NonNull
	@JsonProperty("denominacion")
	private String denominacio;
	@JsonProperty("descripcionEstado")
	private String estat;
	@JsonProperty("raiz")
	private String arrel;
	@JsonProperty("superior")
	private String superior;
	@JsonProperty("localidad")
	private String localitat;
	@JsonProperty("idPadre")
	private String idPare;
	@JsonProperty("hijos")
	private List<NodeDir3> fills;

	@Override
	public int compareTo(NodeDir3 o) {
		return denominacio.compareToIgnoreCase(o.getDenominacio());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result + ((denominacio == null) ? 0 : denominacio.hashCode());
		result = prime * result + ((estat == null) ? 0 : estat.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeDir3 other = (NodeDir3)obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (denominacio == null) {
			if (other.denominacio != null)
				return false;
		} else if (!denominacio.equals(other.denominacio))
			return false;
		if (estat == null) {
			if (other.estat != null)
				return false;
		} else if (!estat.equals(other.estat))
			return false;
		return true;
	}

	private static final long serialVersionUID = -5602898182576627524L;

}
