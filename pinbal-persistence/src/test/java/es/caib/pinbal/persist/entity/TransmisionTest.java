package es.caib.pinbal.persist.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class TransmisionTest {

    @Test
    public void testGettersReturnNullByDefault() {
        Transmision transmision = new Transmision();

        assertNull(transmision.getIdSolicitud());
        assertNull(transmision.getIdPeticion());
        assertNull(transmision.getIdTransmision());
        assertNull(transmision.getIdSolicitante());
        assertNull(transmision.getNombreSolicitante());
        assertNull(transmision.getDocTitular());
        assertNull(transmision.getNombreTitular());
        assertNull(transmision.getApellido1Titular());
        assertNull(transmision.getApellido2Titular());
        assertNull(transmision.getNombreCompletoTitular());
        assertNull(transmision.getDocFuncionario());
        assertNull(transmision.getNombreFuncionario());
        assertNull(transmision.getSeudonimoFuncionario());
        assertNull(transmision.getFechaGeneracion());
        assertNull(transmision.getUnidadTramitadora());
        assertNull(transmision.getCodigoUnidadTramitadora());
        assertNull(transmision.getCodigoProcedimiento());
        assertNull(transmision.getNombreProcedimiento());
        assertNull(transmision.getExpediente());
        assertNull(transmision.getFinalidad());
        assertNull(transmision.getConsentimiento());
        assertNull(transmision.getError());
        assertNull(transmision.getEstado());
        assertNull(transmision.getEstadoSecundario());
    }
}
