package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.persist.entity.ClauPrivada;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.EntitatServei;
import es.caib.pinbal.persist.entity.OrganismeCessionari;
import es.caib.pinbal.persist.entity.ServeiConfig;
import es.caib.pinbal.persist.repository.ClauPrivadaRepository;
import es.caib.pinbal.persist.repository.EntitatRepository;
import es.caib.pinbal.persist.repository.EntitatServeiRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import es.caib.pinbal.scsp.ScspHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class EntitatClauHelperTest {

    @Mock private ClauPrivadaRepository clauPrivadaRepository;
    @Mock private EntitatRepository entitatRepository;
    @Mock private EntitatServeiRepository entitatServeiRepository;
    @Mock private ServeiConfigRepository serveiConfigRepository;
    @Mock private ScspHelper scspHelper;

    @InjectMocks
    private EntitatClauHelper helper;

    @BeforeEach
    public void setUp() {
        // Evita que getScspHelper() intenti crear un ScspHelper real via new ScspHelper(...)
        ReflectionTestUtils.setField(helper, "scspHelper", scspHelper);
    }

    private Entitat buildEntitat(Long id, String cif) {
        Entitat entitat = new Entitat();
        ReflectionTestUtils.setField(entitat, "id", id);
        entitat.setCif(cif);
        return entitat;
    }

    @Test
    public void sincronitzarAmbServeis_entitatNoExisteix_llancaExcepcio() {
        when(entitatRepository.findByCif("Q0700000A")).thenReturn(null);

        assertThrows(EntitatNotFoundException.class,
                () -> helper.sincronitzarAmbServeis("alies1", "Q0700000A"));
    }

    @Test
    public void sincronitzarAmbServeis_sensServeisAmbCertificat_noFaRes() throws Exception {
        Entitat entitat = buildEntitat(1L, "Q0700000A");
        when(entitatRepository.findByCif("Q0700000A")).thenReturn(entitat);
        when(serveiConfigRepository.findByUseCertificatEntitatTrueAndEntitat(1L)).thenReturn(Collections.emptyList());

        helper.sincronitzarAmbServeis("alies1", "Q0700000A");

        verifyNoInteractions(scspHelper);
    }

    @Test
    public void sincronitzarAmbServeis_ambServeis_assignaCertificatACadaServei() throws Exception {
        Entitat entitat = buildEntitat(1L, "Q0700000A");
        ServeiConfig sc1 = new ServeiConfig();
        sc1.setServei("SV001");
        ServeiConfig sc2 = new ServeiConfig();
        sc2.setServei("SV002");

        when(entitatRepository.findByCif("Q0700000A")).thenReturn(entitat);
        when(serveiConfigRepository.findByUseCertificatEntitatTrueAndEntitat(1L))
                .thenReturn(List.of(sc1, sc2));

        helper.sincronitzarAmbServeis("alies1", "Q0700000A");

        verify(scspHelper).assignarCertificatAServei("Q0700000A", "SV001", "alies1");
        verify(scspHelper).assignarCertificatAServei("Q0700000A", "SV002", "alies1");
    }

    @Test
    public void sincronitzarAmbServeis_errorEnUnServei_continuaAmbLaResta() throws Exception {
        Entitat entitat = buildEntitat(1L, "Q0700000A");
        ServeiConfig sc1 = new ServeiConfig();
        sc1.setServei("SV001");
        ServeiConfig sc2 = new ServeiConfig();
        sc2.setServei("SV002");

        when(entitatRepository.findByCif("Q0700000A")).thenReturn(entitat);
        when(serveiConfigRepository.findByUseCertificatEntitatTrueAndEntitat(1L))
                .thenReturn(List.of(sc1, sc2));
        doThrow(new RuntimeException("error scsp")).when(scspHelper)
                .assignarCertificatAServei("Q0700000A", "SV001", "alies1");

        assertDoesNotThrow(() -> helper.sincronitzarAmbServeis("alies1", "Q0700000A"));

        verify(scspHelper).assignarCertificatAServei("Q0700000A", "SV002", "alies1");
    }

    @Test
    public void actualitzarServiciosOrganismos_actualitzaAmbAliesDeLaClauExistent() {
        Entitat entitat = buildEntitat(1L, "Q0700000A");
        EntitatServei es1 = mock(EntitatServei.class);
        when(es1.getServei()).thenReturn("SV001");
        EntitatServei es2 = mock(EntitatServei.class);
        when(es2.getServei()).thenReturn("SV002");

        when(entitatRepository.findByCif("Q0700000A")).thenReturn(entitat);
        when(entitatServeiRepository.findByEntitat(entitat)).thenReturn(List.of(es1, es2));

        ClauPrivada clauExistent = mock(ClauPrivada.class);
        when(clauExistent.getAlies()).thenReturn("alies-entitat");
        when(clauPrivadaRepository.findTopByOrganismeCifAndPerEntitatTrueOrderByDataAltaDesc("Q0700000A"))
                .thenReturn(clauExistent);

        helper.actualitzarServiciosOrganismos(mock(ClauPrivada.class), "Q0700000A");

        verify(scspHelper).actualitzarServiciosActivosOrganismoCesionario(
                eq("Q0700000A"),
                argThat(serveis -> serveis.contains("SV001") && serveis.contains("SV002") && serveis.size() == 2),
                eq("alies-entitat"));
    }

    @Test
    public void actualitzarServiciosOrganismos_senseClauPrivadaEntrada_noConsultaRepositori() {
        Entitat entitat = buildEntitat(1L, "Q0700000A");
        when(entitatRepository.findByCif("Q0700000A")).thenReturn(entitat);
        when(entitatServeiRepository.findByEntitat(entitat)).thenReturn(Collections.emptyList());

        helper.actualitzarServiciosOrganismos(null, "Q0700000A");

        verify(clauPrivadaRepository, never()).findTopByOrganismeCifAndPerEntitatTrueOrderByDataAltaDesc(anyString());
        verify(scspHelper).actualitzarServiciosActivosOrganismoCesionario(
                eq("Q0700000A"), eq(Collections.emptySet()), isNull());
    }

    @Test
    public void resetClauServiciosOrganismos_assignaCertificatDefectePerCadaServeiDeLEntitat() {
        OrganismeCessionari organisme = new OrganismeCessionari();
        organisme.setCif("Q0700000A");
        ClauPrivada clauPrivada = mock(ClauPrivada.class);
        when(clauPrivada.getOrganisme()).thenReturn(organisme);

        Entitat entitat = buildEntitat(1L, "Q0700000A");
        EntitatServei es1 = mock(EntitatServei.class);
        when(es1.getServei()).thenReturn("SV001");

        when(entitatRepository.findByCif("Q0700000A")).thenReturn(entitat);
        when(entitatServeiRepository.findByEntitat(entitat)).thenReturn(List.of(es1));

        helper.resetClauServiciosOrganismos(clauPrivada);

        verify(scspHelper).assignarDefaultCertificatAServei("Q0700000A", "SV001");
    }
}
