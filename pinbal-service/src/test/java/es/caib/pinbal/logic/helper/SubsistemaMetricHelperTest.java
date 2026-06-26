package es.caib.pinbal.logic.helper;

import es.caib.comanda.model.server.monitoring.EstatSalutEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubsistemaMetricHelperTest {

    @Test
    public void getSubsistemesInfo_retornaSubsistemes() {
        SubsistemaMetricHelper.SubsistemesInfo info = SubsistemaMetricHelper.getSubsistemesInfo();
        assertNotNull(info);
        assertNotNull(info.getSubsistemesSalut());
        assertNotNull(info.getEstatGlobal());
    }

    @Test
    public void getSubsistemesInfo_conteTotsElsSubsistemes() {
        SubsistemaMetricHelper.SubsistemesInfo info = SubsistemaMetricHelper.getSubsistemesInfo();
        assertEquals(SubsistemaMetricHelper.SubsistemesEnum.values().length,
                info.getSubsistemesSalut().size());
    }

    @Test
    public void addSuccessOperation_noLlancaException() {
        assertDoesNotThrow(() ->
                SubsistemaMetricHelper.addSuccessOperation(
                        SubsistemaMetricHelper.SubsistemesEnum.CWS.name(), 100L));
    }

    @Test
    public void addErrorOperation_noLlancaException() {
        assertDoesNotThrow(() ->
                SubsistemaMetricHelper.addErrorOperation(
                        SubsistemaMetricHelper.SubsistemesEnum.CWS.name()));
    }

    @Test
    public void addSuccessOperationAmbServei_noLlancaException() {
        assertDoesNotThrow(() ->
                SubsistemaMetricHelper.addSuccessOperation(
                        SubsistemaMetricHelper.SubsistemesEnum.CRS.name(), "SV001", 50L));
    }

    @Test
    public void addErrorOperationAmbServei_noLlancaException() {
        assertDoesNotThrow(() ->
                SubsistemaMetricHelper.addErrorOperation(
                        SubsistemaMetricHelper.SubsistemesEnum.CRS.name(), "SV001"));
    }

    @Test
    public void addSuccessAmbServeiNull_noLlancaException() {
        assertDoesNotThrow(() ->
                SubsistemaMetricHelper.addSuccessOperation(
                        SubsistemaMetricHelper.SubsistemesEnum.CWA.name(), null, 100L));
    }

    @Test
    public void getMetrica_crearNovaMetrica_retornaMetrica() {
        assertNotNull(SubsistemaMetricHelper.getMetrica("TEST_SUBSISTEMA"));
    }

    @Test
    public void getIntegracionsSalut_retornaLlista() {
        assertDoesNotThrow(() -> {
            var result = SubsistemaMetricHelper.getIntegracionsSalut();
            assertNotNull(result);
        });
    }

    @Test
    public void subsistemesEnum_nomNoNull() {
        for (SubsistemaMetricHelper.SubsistemesEnum s : SubsistemaMetricHelper.SubsistemesEnum.values()) {
            assertNotNull(s.getNom());
        }
    }

    @Test
    public void subsistemesEnum_valueOfCodi_trobat() {
        SubsistemaMetricHelper.SubsistemesEnum result =
                SubsistemaMetricHelper.SubsistemesEnum.valueOfCodi("CWS");
        assertEquals(SubsistemaMetricHelper.SubsistemesEnum.CWS, result);
    }

    @Test
    public void subsistemesEnum_valueOfCodi_noTrobat_retornaNull() {
        assertNull(SubsistemaMetricHelper.SubsistemesEnum.valueOfCodi("INEXISTENT"));
    }

    @Test
    public void sistemesCritics_cwsICrsSOnsistemaCritic() {
        assertTrue(SubsistemaMetricHelper.SubsistemesEnum.CWS.isSistemaCritic());
        assertTrue(SubsistemaMetricHelper.SubsistemesEnum.CRS.isSistemaCritic());
        assertFalse(SubsistemaMetricHelper.SubsistemesEnum.CWA.isSistemaCritic());
        assertFalse(SubsistemaMetricHelper.SubsistemesEnum.CRA.isSistemaCritic());
        assertFalse(SubsistemaMetricHelper.SubsistemesEnum.JUS.isSistemaCritic());
    }
}
