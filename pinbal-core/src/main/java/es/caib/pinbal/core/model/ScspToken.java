package es.caib.pinbal.core.model;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Entity
@IdClass(ScspTokenId.class)
@Table(name = "core_token_data")
public class ScspToken implements Serializable {

    @Id
    @Column(length = 26)
    private String idPeticion;

    @Id
    private Integer tipoMensaje;

    @Lob
    @Column(nullable = false)
    private String datos;

    @Column(length = 32)
    private String modoEncriptacion;

    @Column(length = 32)
    private String algoritmoEncriptacion;

    @Column(length = 256)
    private String clave;
}
