package es.caib.pinbal.logic.helper;

import es.caib.pinbal.client.serveis.ServeiBasic;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.EntitatUsuari;
import es.caib.pinbal.persist.entity.EntitatServei;
import es.caib.pinbal.persist.entity.Procediment;
import es.caib.pinbal.persist.entity.ProcedimentServei;
import es.caib.pinbal.persist.entity.ServeiConfig;
import es.caib.pinbal.persist.repository.EntitatServeiRepository;
import es.caib.pinbal.persist.repository.EntitatUsuariRepository;
import es.caib.pinbal.persist.repository.ProcedimentServeiRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import es.caib.pinbal.persist.repository.ServeiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ServeiHelperTest {

    @Mock private ServeiConfigRepository serveiConfigRepository;
    @Mock private ProcedimentServeiRepository procedimentServeiRepository;
    @Mock private EntitatServeiRepository entitatServeiRepository;
    @Mock private EntitatUsuariRepository entitatUsuariRepository;
    @Mock private ServeiRepository serveiRepository;
    @Mock private MutableAclService aclService;

    @InjectMocks
    private ServeiHelper serveiHelper;

    private Authentication auth;
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("usuari1");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void findServeisClient_delegaAlRepositori() {
        List<ServeiBasic> serveis = Collections.singletonList(new ServeiBasic("SERV001", "Servei 1", true));
        when(serveiRepository.findAllServeisClient()).thenReturn(serveis);

        assertSame(serveis, serveiHelper.findServeisClient());
    }

    @Test
    public void findServeisClientPerEntitat_delegaAlRepositori() {
        List<ServeiBasic> serveis = Collections.singletonList(new ServeiBasic("SERV001", "Servei 1", true));
        when(serveiRepository.findServeisClientByEntitatCodi("ENT001")).thenReturn(serveis);

        assertSame(serveis, serveiHelper.findServeisClientPerEntitat("ENT001"));
    }

    @Test
    public void findServeisClientPerProcediment_delegaAlRepositori() {
        List<ServeiBasic> serveis = Collections.singletonList(new ServeiBasic("SERV001", "Servei 1", true));
        when(serveiRepository.findServeisClientByProcedimentCodi("PROC001")).thenReturn(serveis);

        assertSame(serveis, serveiHelper.findServeisClientPerProcediment("ENT001", "PROC001"));
    }

    @Test
    public void findServeisPermesosPerUsuari_entitatUsuariNull_retornaLlistaVuida() {
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "usuari1")).thenReturn(null);

        List<String> result = serveiHelper.findServeisPermesosPerUsuari(1L, null, auth);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findServeisPermesosPerUsuari_usuariNoActiu_retornaLlistaVuida() {
        EntitatUsuari eu = mock(EntitatUsuari.class);
        when(eu.isDelegat()).thenReturn(true);
        when(eu.isAplicacio()).thenReturn(false);
        when(eu.isActiu()).thenReturn(false);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "usuari1")).thenReturn(eu);

        List<String> result = serveiHelper.findServeisPermesosPerUsuari(1L, null, auth);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findServeisPermesosPerUsuari_usuariDelegatActiu_retornaServeis() {
        EntitatUsuari eu = mock(EntitatUsuari.class);
        when(eu.isDelegat()).thenReturn(true);
        when(eu.isAplicacio()).thenReturn(false);
        when(eu.isActiu()).thenReturn(true);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "usuari1")).thenReturn(eu);
        when(procedimentServeiRepository.findActiusByEntitatId(1L)).thenReturn(Collections.emptyList());
        when(entitatServeiRepository.findByEntitatId(1L)).thenReturn(Collections.emptyList());

        List<String> result = serveiHelper.findServeisPermesosPerUsuari(1L, null, auth);

        assertNotNull(result);
        // No services available → empty
        assertTrue(result.isEmpty());
    }

    @Test
    public void isServeiPermesPerUsuari_serveiNoPermes_retornaFalse() {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(1L);
        Procediment procediment = mock(Procediment.class);
        when(procediment.getCodi()).thenReturn("PROC01");
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "usuari1")).thenReturn(null);

        boolean result = serveiHelper.isServeiPermesPerUsuari(entitat, procediment, "SV001");

        assertFalse(result);
    }

    @Test
    public void findCodisSiaPermesosPerServei_usuariNoDelegat_retornaMapaBuit() {
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "usuari1")).thenReturn(null);

        Map<String, List<String>> result = serveiHelper.findCodisSiaPermesosPerServei(1L, auth);

        assertTrue(result.isEmpty());
        verify(procedimentServeiRepository, never()).findActiusByEntitatId(anyLong());
    }

    @Test
    public void findCodisSiaPermesosPerServei_retornaCodisSiaDelsProcedimentsPermesos() {
        EntitatUsuari eu = mock(EntitatUsuari.class);
        when(eu.isAplicacio()).thenReturn(true);
        when(eu.isActiu()).thenReturn(true);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "usuari1")).thenReturn(eu);
        List<ProcedimentServei> pss = new ArrayList<>(Arrays.asList(
                procedimentServei(1L, "SV1", "200002"),
                procedimentServei(2L, "SV1", "100001"),
                procedimentServei(3L, "SV1", null),          // procediment sense codi SIA
                procedimentServei(4L, "SV1", " "),           // procediment amb codi SIA buit
                procedimentServei(5L, "SV1", "100001"),      // codi SIA repetit (dos procediments)
                procedimentServei(6L, "SV2", "300003"),      // sense permís sobre el procediment-servei
                procedimentServei(7L, "SV3", "400004"),      // servei no disponible per a l'entitat
                procedimentServei(8L, "SV4", null)));        // servei permès sense cap codi SIA
        when(procedimentServeiRepository.findActiusByEntitatId(1L)).thenReturn(pss);
        List<EntitatServei> serveisEntitat = Arrays.asList(entitatServei("SV1"), entitatServei("SV2"), entitatServei("SV4"));
        when(entitatServeiRepository.findByEntitatId(1L)).thenReturn(serveisEntitat);
        ServeiConfig serveiConfig1 = serveiConfig(10L, "SV1");
        ServeiConfig serveiConfig4 = serveiConfig(11L, "SV4");
        when(serveiConfigRepository.findByServei("SV1")).thenReturn(serveiConfig1);
        when(serveiConfigRepository.findByServei("SV4")).thenReturn(serveiConfig4);
        permetreProcedimentServeis(1L, 2L, 3L, 4L, 5L, 7L, 8L);

        Map<String, List<String>> result = serveiHelper.findCodisSiaPermesosPerServei(1L, auth);

        // Les claus són els serveis permesos (igual que findServeisPermesosPerUsuari)
        assertEquals(new HashSet<>(Arrays.asList("SV1", "SV4")), result.keySet());
        assertEquals(Arrays.asList("100001", "200002"), result.get("SV1"));
        assertTrue(result.get("SV4").isEmpty());
    }

    private static ServeiConfig serveiConfig(Long id, String servei) {
        ServeiConfig serveiConfig = mock(ServeiConfig.class);
        when(serveiConfig.getId()).thenReturn(id);
        when(serveiConfig.getServei()).thenReturn(servei);
        when(serveiConfig.isActiu()).thenReturn(true);
        return serveiConfig;
    }

    private static ProcedimentServei procedimentServei(Long id, String servei, String codiSia) {
        Procediment procediment = mock(Procediment.class);
        when(procediment.getCodiSia()).thenReturn(codiSia);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getId()).thenReturn(id);
        when(ps.getServei()).thenReturn(servei);
        when(ps.getProcediment()).thenReturn(procediment);
        return ps;
    }

    private static EntitatServei entitatServei(String servei) {
        EntitatServei entitatServei = mock(EntitatServei.class);
        when(entitatServei.getServei()).thenReturn(servei);
        return entitatServei;
    }

    /** Dona permís de lectura sobre els procediment-servei indicats; la resta queden sense permís. */
    private void permetreProcedimentServeis(Long... ids) {
        Set<Long> permesos = new HashSet<>(Arrays.asList(ids));
        when(aclService.readAclById(any(ObjectIdentity.class))).thenAnswer(invocation -> {
            ObjectIdentity oid = invocation.getArgument(0);
            boolean granted = !ProcedimentServei.class.getName().equals(oid.getType())
                    || permesos.contains(((Number) oid.getIdentifier()).longValue());
            Acl acl = mock(Acl.class);
            when(acl.isGranted(anyList(), anyList(), anyBoolean())).thenReturn(granted);
            return acl;
        });
    }
}
