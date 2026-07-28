package es.caib.pinbal.persist.entity;

import es.caib.pinbal.persist.entity.ServeiConfig.EntitatTipus;
import es.caib.pinbal.persist.entity.ServeiConfig.JustificantTipus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServeiConfigTest {

    private ServeiConfig buildServeiConfig() {
        return ServeiConfig.getBuilder(
                "SERVEI1",
                "CUSTODIA1",
                "ROLE_SERVEI1",
                "es.caib.pinbal.CondicioBusImpl",
                EntitatTipus.GOVERN,
                JustificantTipus.GENERAT,
                "/justificant",
                "Ajuda del servei",
                true,
                60,
                "ajuda.pdf",
                "application/pdf",
                new byte[]{1, 2, 3},
                true,
                true,
                true,
                true,
                true,
                true).build();
    }

    @Test
    public void testGetBuilder() {
        ServeiConfig config = buildServeiConfig();

        assertEquals("SERVEI1", config.getServei());
        assertEquals("CUSTODIA1", config.getCustodiaCodi());
        assertEquals("ROLE_SERVEI1", config.getRoleName());
        assertEquals("es.caib.pinbal.CondicioBusImpl", config.getCondicioBusClass());
        assertEquals(EntitatTipus.GOVERN, config.getEntitatTipus());
        assertEquals(JustificantTipus.GENERAT, config.getJustificantTipus());
        assertEquals("/justificant", config.getJustificantXpath());
        assertEquals("Ajuda del servei", config.getAjuda());
        assertTrue(config.isActivaGestioXsd());
        assertEquals(60, config.getMaxPeticionsMinut());
        assertEquals("ajuda.pdf", config.getFitxerAjudaNom());
        assertEquals("application/pdf", config.getFitxerAjudaMimeType());
        assertArrayEquals(new byte[]{1, 2, 3}, config.getFitxerAjudaContingut());
        assertTrue(config.isIniDadesEspecifiques());
        assertTrue(config.isAddDadesEspecifiques());
        assertTrue(config.isUseAutoClasse());
        assertTrue(config.isEnviarSolicitant());
        assertTrue(config.isUseCertificatEntitat());
        assertTrue(config.isActiu());
        assertEquals(0L, config.getVersion());
        assertTrue(config.isPermesDocumentTipusDni());
        assertTrue(config.isPermesDocumentTipusNif());
        assertTrue(config.isPermesDocumentTipusCif());
        assertTrue(config.isPermesDocumentTipusNie());
        assertTrue(config.isPermesDocumentTipusPas());
        assertTrue(config.isActiuCampNom());
        assertTrue(config.isActiuCampLlinatge1());
        assertTrue(config.isActiuCampLlinatge2());
        assertTrue(config.isActiuCampNomComplet());
        assertTrue(config.isActiuCampDocument());
        assertFalse(config.isDocumentObligatori());
        assertTrue(config.isComprovarDocument());
    }

    @Test
    public void testUpdateActiu() {
        ServeiConfig config = buildServeiConfig();

        config.updateActiu(false);

        assertFalse(config.isActiu());
    }

    @Test
    public void testUpdate() {
        ServeiConfig config = buildServeiConfig();

        config.update(
                "CUSTODIA2",
                "ROLE_SERVEI2",
                "es.caib.pinbal.AltraCondicio",
                EntitatTipus.AJUNTAMENT,
                JustificantTipus.ADJUNT_PDF_BASE64,
                "/altre/justificant",
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                "A01234567",
                true,
                false,
                false,
                30,
                "Nova ajuda",
                false,
                false,
                false,
                false,
                false);

        assertEquals("CUSTODIA2", config.getCustodiaCodi());
        assertEquals("ROLE_SERVEI2", config.getRoleName());
        assertEquals("es.caib.pinbal.AltraCondicio", config.getCondicioBusClass());
        assertEquals(EntitatTipus.AJUNTAMENT, config.getEntitatTipus());
        assertEquals(JustificantTipus.ADJUNT_PDF_BASE64, config.getJustificantTipus());
        assertEquals("/altre/justificant", config.getJustificantXpath());
        assertFalse(config.isPermesDocumentTipusDni());
        assertFalse(config.isPermesDocumentTipusNif());
        assertFalse(config.isPermesDocumentTipusCif());
        assertFalse(config.isPermesDocumentTipusNie());
        assertFalse(config.isPermesDocumentTipusPas());
        assertFalse(config.isActiuCampNom());
        assertFalse(config.isActiuCampLlinatge1());
        assertFalse(config.isActiuCampLlinatge2());
        assertFalse(config.isActiuCampNomComplet());
        assertFalse(config.isActiuCampDocument());
        assertEquals("A01234567", config.getPinbalUnitatDir3());
        assertTrue(config.isDocumentObligatori());
        assertFalse(config.isComprovarDocument());
        assertFalse(config.isActivaGestioXsd());
        assertEquals(30, config.getMaxPeticionsMinut());
        assertEquals("Nova ajuda", config.getAjuda());
        assertFalse(config.isIniDadesEspecifiques());
        assertFalse(config.isAddDadesEspecifiques());
        assertFalse(config.isUseAutoClasse());
        assertFalse(config.isEnviarSolicitant());
        assertFalse(config.isUseCertificatEntitat());
    }

    @Test
    public void testUpdateFitxerAjuda() {
        ServeiConfig config = buildServeiConfig();

        config.updateFitxerAjuda("nou.pdf", "application/octet-stream", new byte[]{9, 8});

        assertEquals("nou.pdf", config.getFitxerAjudaNom());
        assertEquals("application/octet-stream", config.getFitxerAjudaMimeType());
        assertArrayEquals(new byte[]{9, 8}, config.getFitxerAjudaContingut());
    }

    @Test
    public void testSettersLombokAddicionals() {
        ServeiConfig config = buildServeiConfig();

        config.setPinbalUnitatDir3FromEntitat(true);
        config.setArrelRespostaPath("/arrel/resposta");
        config.setVersion(3L);

        assertTrue(config.isPinbalUnitatDir3FromEntitat());
        assertEquals("/arrel/resposta", config.getArrelRespostaPath());
        assertEquals(3L, config.getVersion());
    }

    @Test
    public void testEqualsAndHashCode() {
        // ServeiConfig hereta equals() d'AbstractPersistable, basat en id: com que l'id és
        // null a instàncies noves, mai són iguals encara que coincideixin el servei.
        ServeiConfig config1 = buildServeiConfig();
        ServeiConfig config2 = buildServeiConfig();

        assertEquals(config1, config1);
        assertNotEquals(config1, config2);
        assertNotEquals(config1, null);
        assertNotEquals(config1, new Object());
        assertEquals(config1.hashCode(), config2.hashCode());
    }

    @Test
    public void testToString() {
        ServeiConfig config = buildServeiConfig();

        assertNotNull(config.toString());
    }
}
