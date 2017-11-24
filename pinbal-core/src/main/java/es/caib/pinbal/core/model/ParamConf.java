/**
 * 
 */
package es.caib.pinbal.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Classe de model de dades que conté la informació
 * d'un paràmetre de configuracio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "core_parametro_configuracion")
public class ParamConf implements Serializable {
	
	@Id
	@Column(name = "nombre", length = 64, nullable = false, unique = true)
	private String nom;

	@Column(name = "valor", length = 512, nullable = false)
	private String valor;

	@Column(name = "descripcion", length = 512)
	private String descripcio;
	
	
	/**
	 * Obté el Builder per a crear paràmetres de configuració.
	 * 
	 * @param nom
	 *            El nom del paràmetre de configuració.
	 * @param valor
	 *            El valor del paràmetre de configuració.
	 * @param descripcio
	 *            La descripció del paràmetre de configuració.
	 *            
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String nom,
			String valor,
			String descripcio) {
		return new Builder(
				nom,
				valor,
				descripcio);
	}
	
	public String getNom() {
		return nom;
	}
	
	public String getValor() {
		return valor;
	}
	
	public String getDescripcio() {
		return descripcio;
	}
	
	public void update(
			String valor,
			String descripcio) {
		this.valor = valor;;
		this.descripcio = descripcio;
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus ParamConf.
	 */
	public static class Builder {
		ParamConf built;

		/**
		 * Crea una nova instància del Builder.
		 * 
		 * @param nom
		 *            El nom del paràmetre de configuració.
		 * @param valor
		 *            El valor del paràmetre de configuració.
		 * @param descripcio
		 *            La descripció del paràmetre de configuració.
		 */
		Builder(String nom,
				String valor,
				String descripcio) {
			built = new ParamConf();
			built.nom = nom;
			built.valor = valor;
			built.descripcio = descripcio;
		}

		/**
		 * Construeix el nou objecte de tipus ParamConf.
		 * 
		 * @return L'objecte de tipus ParamConf creat.
		 */
		public ParamConf build() {
			return built;
		}
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
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
		ParamConf other = (ParamConf) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
	private static final long serialVersionUID = -6657066865382086237L;
	
}
