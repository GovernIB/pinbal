package es.caib.pinbal.logic.helper;

import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.EntitatUsuari;
import es.caib.pinbal.persist.entity.Procediment;
import es.caib.pinbal.persist.repository.EntitatServeiRepository;
import es.caib.pinbal.persist.repository.EntitatUsuariRepository;
import es.caib.pinbal.persist.repository.ProcedimentServeiRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

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
}
