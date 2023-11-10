package es.caib.pinbal.core.model;

import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public class ScspTokenId implements Serializable {

    public static final Integer PETICION = 0;
    public static final Integer CONFIRMACION_PETICION = 1;
    public static final Integer SOLICITUD_RESPUESTA = 2;
    public static final Integer RESPUESTA = 3;
    public static final Integer FAULT = 4;

    private String idPeticion;
    private Integer tipoMensaje;
}
