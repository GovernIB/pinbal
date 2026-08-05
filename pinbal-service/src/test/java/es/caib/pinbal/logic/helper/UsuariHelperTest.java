package es.caib.pinbal.logic.helper;

import es.caib.pinbal.persist.entity.Usuari;
import es.caib.pinbal.persist.repository.ProcedimentServeiRepository;
import es.caib.pinbal.persist.repository.UsuariRepository;
import es.caib.pinbal.logic.intf.service.exception.NotFoundException;
import es.caib.pinbal.plugin.SistemaExternException;
import es.caib.pinbal.plugin.usuari.DadesUsuari;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class UsuariHelperTest {

    @Mock private UsuariRepository usuariRepository;
    @Mock private ProcedimentServeiRepository procedimentServeiRepository;
    @Mock private CacheHelper cacheHelper;
    @Mock private MutableAclService aclService;
    @Mock private PluginHelper pluginHelper;

    @InjectMocks
    private UsuariHelper usuariHelper;

    private Authentication auth;
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        usuariHelper.setPluginHelper(pluginHelper);
        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("usuari1");
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void getUsuariAutenticat_authNull_retornaNull() {
        when(securityContext.getAuthentication()).thenReturn(null);
        Usuari result = usuariHelper.getUsuariAutenticat();
        assertNull(result);
    }

    @Test
    public void getUsuariAutenticat_autentificat_retornaUsuari() throws Exception {
        Usuari usuari = mock(Usuari.class);
        when(usuari.isInicialitzat()).thenReturn(true);
        when(usuari.getNom()).thenReturn("Nom Existent");
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.of(usuari));

        Usuari result = usuariHelper.getUsuariAutenticat();

        assertNotNull(result);
        assertEquals(usuari, result);
    }

    @Test
    public void init_usuariJaExisteixIInicialitzat_retornaUsuari() {
        Usuari usuari = mock(Usuari.class);
        when(usuari.isInicialitzat()).thenReturn(true);
        when(usuari.getNom()).thenReturn("Nom Existent");
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.of(usuari));

        Usuari result = usuariHelper.init("usuari1");

        assertNotNull(result);
        verifyNoInteractions(pluginHelper);
    }

    @Test
    public void init_usuariInicialitzatPeroNomBuit_reintentaConsultaSistemaExtern() throws Exception {
        // Si el nom ha quedat buit (p.ex. una consulta anterior al sistema extern no el va poder
        // obtenir) cal reintentar-ho en el següent login encara que "inicialitzat" ja sigui true,
        // en lloc de deixar l'usuari sense nom per sempre.
        Usuari usuari = mock(Usuari.class);
        when(usuari.isInicialitzat()).thenReturn(true);
        when(usuari.getNom()).thenReturn(null);
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.of(usuari));
        DadesUsuari dades = new DadesUsuari();
        dades.setNom("Nom Recuperat");
        dades.setNif(null);
        when(pluginHelper.dadesUsuariConsultarAmbUsuariCodi("usuari1")).thenReturn(dades);

        Usuari result = usuariHelper.init("usuari1");

        assertNotNull(result);
        verify(usuari).update("Nom Recuperat", null);
    }

    @Test
    public void init_usuariExisteixNoInicialitzat_consultaSistemaExtern() throws Exception {
        Usuari usuari = mock(Usuari.class);
        when(usuari.isInicialitzat()).thenReturn(false);
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.of(usuari));
        DadesUsuari dades = new DadesUsuari();
        dades.setNom("Nom Test");
        dades.setNif("12345678A");
        when(pluginHelper.dadesUsuariConsultarAmbUsuariCodi("usuari1")).thenReturn(dades);

        Usuari result = usuariHelper.init("usuari1");

        assertNotNull(result);
        verify(usuari).update("Nom Test", "12345678A");
    }

    @Test
    public void init_usuariNoExisteix_creaUsuariNou() throws Exception {
        when(usuariRepository.findById("nouUsuari")).thenReturn(Optional.empty());
        DadesUsuari dades = new DadesUsuari();
        dades.setCodi("nouUsuari");
        dades.setNom("Nou Usuari");
        dades.setNif("87654321B");
        when(pluginHelper.dadesUsuariConsultarAmbUsuariCodi("nouUsuari")).thenReturn(dades);
        Usuari nouUsuari = mock(Usuari.class);
        when(usuariRepository.save(any(Usuari.class))).thenReturn(nouUsuari);

        Usuari result = usuariHelper.init("nouUsuari");

        assertNotNull(result);
        verify(usuariRepository).save(any(Usuari.class));
    }

    @Test
    public void init_pluginError_llancaNotFoundException() throws Exception {
        when(usuariRepository.findById("erroUsuari")).thenReturn(Optional.empty());
        when(pluginHelper.dadesUsuariConsultarAmbUsuariCodi("erroUsuari"))
                .thenThrow(new SistemaExternException("Plugin error"));

        assertThrows(NotFoundException.class, () -> usuariHelper.init("erroUsuari"));
    }
}
