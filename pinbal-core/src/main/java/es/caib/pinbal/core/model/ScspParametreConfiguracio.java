package es.caib.pinbal.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "core_parametro_configuracion")
public class ScspParametreConfiguracio {

    @Id
    @Column(name = "nombre", length = 64, nullable = false)
    private String nombre;
    @Column(name = "valor", length = 512, nullable = false)
    private String valor;
    @Column(name = "descripcion", length = 512)
    private String descripcion;

    public void update(
            String valor,
            String descripcion) {
        this.valor = valor;
        this.descripcion = descripcion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
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
        ScspParametreConfiguracio other = (ScspParametreConfiguracio)obj;
        if (nombre == null) {
            if (other.nombre != null)
                return false;
        } else if (!nombre.equals(other.nombre))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
