package es.caib.pinbal.scsp;

import es.scsp.common.dao.ClavePrivadaDao;
import es.scsp.common.dao.OrganismoCesionarioDao;
import es.scsp.common.dao.PinbalDao;
import es.scsp.common.dao.ServicioDao;
import es.scsp.common.dao.ServicioOrganismoCesionarioDao;
import es.scsp.common.dao.ServeiDao;
import es.scsp.common.domain.core.ClavePrivada;
import es.scsp.common.domain.core.OrganismoCesionario;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.domain.req.ServicioOrganismoCesionario;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests dels mètodes de gestió de servicios/organismos cessionaris de {@code ScspHelper}: una gran
 * part del fitxer (~350 línies) que és pura delegació a DAOs, totalment testejable simulant
 * l'{@link ApplicationContext} amb Mockito.
 */
public class ScspHelperOrganismoCesionarioTest {

    private ApplicationContext applicationContext;
    private ScspHelper scspHelper;

    private ServicioDao servicioDao;
    private OrganismoCesionarioDao organismoCesionarioDao;
    private ServicioOrganismoCesionarioDao servicioOrganismoCesionarioDao;
    private ClavePrivadaDao clavePrivadaDao;
    private ServeiDao serveiDao;
    private PinbalDao pinbalDao;

    @Before
    public void configurar() {
        applicationContext = mock(ApplicationContext.class);
        MessageSource messageSource = mock(MessageSource.class);
        scspHelper = new ScspHelper(applicationContext, messageSource);

        servicioDao = mock(ServicioDao.class);
        organismoCesionarioDao = mock(OrganismoCesionarioDao.class);
        servicioOrganismoCesionarioDao = mock(ServicioOrganismoCesionarioDao.class);
        clavePrivadaDao = mock(ClavePrivadaDao.class);
        serveiDao = mock(ServeiDao.class);
        pinbalDao = mock(PinbalDao.class);

        when(applicationContext.getBean("servicioDao")).thenReturn(servicioDao);
        when(applicationContext.getBean("organismoCesionarioDao")).thenReturn(organismoCesionarioDao);
        when(applicationContext.getBean("servicioOrganismoCesionarioDao")).thenReturn(servicioOrganismoCesionarioDao);
        when(applicationContext.getBean("clavePrivadaDao")).thenReturn(clavePrivadaDao);
        when(applicationContext.getBean("serveiDao")).thenReturn(serveiDao);
        when(applicationContext.getBean("pinbalDao")).thenReturn(pinbalDao);
    }

    private OrganismoCesionario organisme(String cif) {
        OrganismoCesionario o = new OrganismoCesionario();
        o.setCif(cif);
        return o;
    }

    private Servicio servei(String codi) {
        Servicio s = new Servicio();
        s.setCodCertificado(codi);
        return s;
    }

    private void ambOrganismeExistent(OrganismoCesionario organisme) {
        when(organismoCesionarioDao.getAll()).thenReturn(new ArrayList<>(List.of(organisme)));
    }

    // ------------------------- actualitzarServiciosActivosOrganismoCesionario -------------------------

    @Test
    public void actualitzarServiciosActivosBloquejaElsServeisQueJaNoSonActius() {
        OrganismoCesionario organisme = organisme("B123");
        ambOrganismeExistent(organisme);

        ServicioOrganismoCesionario existent = new ServicioOrganismoCesionario();
        existent.setServicio(servei("SERV-INACTIU"));
        when(servicioOrganismoCesionarioDao.selectHistorico(organisme)).thenReturn(new ArrayList<>(List.of(existent)));

        scspHelper.actualitzarServiciosActivosOrganismoCesionario("B123", new HashSet<>(), null);

        assertTrue(existent.isBloqueado());
        verify(pinbalDao).delete(existent);
    }

    @Test
    public void actualitzarServiciosActivosMantePeroNoEsborraElsQueContinuenActius() {
        OrganismoCesionario organisme = organisme("B123");
        ambOrganismeExistent(organisme);

        ServicioOrganismoCesionario existent = new ServicioOrganismoCesionario();
        existent.setServicio(servei("SERV-ACTIU"));
        when(servicioOrganismoCesionarioDao.selectHistorico(organisme)).thenReturn(new ArrayList<>(List.of(existent)));
        when(servicioDao.select("SERV-ACTIU")).thenReturn(servei("SERV-ACTIU"));

        Set<String> actius = new HashSet<>();
        actius.add("SERV-ACTIU");
        scspHelper.actualitzarServiciosActivosOrganismoCesionario("B123", actius, null);

        assertFalse(existent.isBloqueado());
        verify(pinbalDao, never()).delete(existent);
    }

    @Test
    public void actualitzarServiciosActivosCreaUnNouServicioOrganismoPerCadaServeiActiuNou() {
        OrganismoCesionario organisme = organisme("B123");
        ambOrganismeExistent(organisme);
        when(servicioOrganismoCesionarioDao.selectHistorico(organisme)).thenReturn(new ArrayList<>());
        when(servicioDao.select("SERV-NOU")).thenReturn(servei("SERV-NOU"));

        Set<String> actius = new HashSet<>();
        actius.add("SERV-NOU");
        actius.add(null); // s'ha d'ignorar
        scspHelper.actualitzarServiciosActivosOrganismoCesionario("B123", actius, null);

        org.mockito.ArgumentCaptor<ServicioOrganismoCesionario> captor =
                org.mockito.ArgumentCaptor.forClass(ServicioOrganismoCesionario.class);
        verify(pinbalDao).save(captor.capture());
        assertEquals("SERV-NOU", captor.getValue().getServicio().getCodCertificado());
        assertFalse(captor.getValue().isBloqueado());
    }

    @Test
    public void actualitzarServiciosActivosIgnoraServeisQueNoExisteixen() {
        OrganismoCesionario organisme = organisme("B123");
        ambOrganismeExistent(organisme);
        when(servicioOrganismoCesionarioDao.selectHistorico(organisme)).thenReturn(new ArrayList<>());
        when(servicioDao.select("SERV-NO-EXISTEIX")).thenReturn(null);

        Set<String> actius = new HashSet<>();
        actius.add("SERV-NO-EXISTEIX");
        scspHelper.actualitzarServiciosActivosOrganismoCesionario("B123", actius, null);

        verify(pinbalDao, never()).save(any());
    }

    // ------------------------- actualitzarServeiOrganismoCesionario -------------------------

    @Test
    public void actualitzarServeiOrganismoCesionarioAmbServeiNullNoFaRes() {
        scspHelper.actualitzarServeiOrganismoCesionario("B123", null, null);
        verify(pinbalDao, never()).save(any());
    }

    @Test
    public void actualitzarServeiOrganismoCesionarioAmbServeiInexistentNoFaRes() {
        when(servicioDao.select("NO-EXISTEIX")).thenReturn(null);
        scspHelper.actualitzarServeiOrganismoCesionario("B123", "NO-EXISTEIX", null);
        verify(pinbalDao, never()).save(any());
    }

    @Test
    public void actualitzarServeiOrganismoCesionarioCreaUnNouRegistre() {
        Servicio servei = servei("SERV1");
        when(servicioDao.select("SERV1")).thenReturn(servei);
        OrganismoCesionario organisme = organisme("B123");
        ambOrganismeExistent(organisme);
        when(servicioOrganismoCesionarioDao.select(servei, organisme)).thenReturn(new ArrayList<>());

        scspHelper.actualitzarServeiOrganismoCesionario("B123", "SERV1", null);

        org.mockito.ArgumentCaptor<ServicioOrganismoCesionario> captor =
                org.mockito.ArgumentCaptor.forClass(ServicioOrganismoCesionario.class);
        verify(pinbalDao).save(captor.capture());
        assertEquals("SERV1", captor.getValue().getServicio().getCodCertificado());
    }

    @Test
    public void actualitzarServeiOrganismoCesionarioAmbAliesClauEntitatActualitzaLaClauExistent() {
        Servicio servei = servei("SERV1");
        when(servicioDao.select("SERV1")).thenReturn(servei);
        OrganismoCesionario organisme = organisme("B123");
        ambOrganismeExistent(organisme);

        ServicioOrganismoCesionario existent = new ServicioOrganismoCesionario();
        existent.setServicio(servei);
        when(servicioOrganismoCesionarioDao.select(servei, organisme)).thenReturn(new ArrayList<>(List.of(existent)));

        ClavePrivada clau = new ClavePrivada();
        clau.setAlias("aliesEntitat");
        when(clavePrivadaDao.selectByAlias("aliesEntitat")).thenReturn(clau);

        scspHelper.actualitzarServeiOrganismoCesionario("B123", "SERV1", "aliesEntitat");

        verify(pinbalDao).save(existent);
        assertSame(clau, existent.getClavePrivada());
    }

    // ------------------------- assignarDefaultCertificatAServei -------------------------

    @Test
    public void assignarDefaultCertificatAServeiAmbOrganismeInexistentLlancaExcepcio() {
        when(organismoCesionarioDao.getAll()).thenReturn(new ArrayList<>());
        try {
            scspHelper.assignarDefaultCertificatAServei("NO-EXISTEIX", "SERV1");
            fail("Hauria d'haver llançat IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // esperat
        }
    }

    @Test
    public void assignarDefaultCertificatAServeiAmbServeiInexistentLlancaExcepcio() {
        ambOrganismeExistent(organisme("B123"));
        when(servicioDao.select("NO-EXISTEIX")).thenReturn(null);
        try {
            scspHelper.assignarDefaultCertificatAServei("B123", "NO-EXISTEIX");
            fail("Hauria d'haver llançat IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // esperat
        }
    }

    @Test
    public void assignarDefaultCertificatAServeiSenseClauDeFirmaLlancaExcepcio() {
        ambOrganismeExistent(organisme("B123"));
        Servicio servei = servei("SERV1");
        when(servicioDao.select("SERV1")).thenReturn(servei);
        try {
            scspHelper.assignarDefaultCertificatAServei("B123", "SERV1");
            fail("Hauria d'haver llançat IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // esperat
        }
    }

    @Test
    public void assignarDefaultCertificatAServeiCreaUnNouRegistreAmbLaClauPerDefecte() {
        OrganismoCesionario organisme = organisme("B123");
        ambOrganismeExistent(organisme);
        Servicio servei = servei("SERV1");
        ClavePrivada claveFirma = new ClavePrivada();
        servei.setClaveFirma(claveFirma);
        when(servicioDao.select("SERV1")).thenReturn(servei);
        when(servicioOrganismoCesionarioDao.select(servei, organisme)).thenReturn(new ArrayList<>());

        scspHelper.assignarDefaultCertificatAServei("B123", "SERV1");

        org.mockito.ArgumentCaptor<ServicioOrganismoCesionario> captor =
                org.mockito.ArgumentCaptor.forClass(ServicioOrganismoCesionario.class);
        verify(pinbalDao).save(captor.capture());
        assertSame(claveFirma, captor.getValue().getClavePrivada());
    }

    @Test
    public void assignarDefaultCertificatAServeiActualitzaUnRegistreExistent() {
        OrganismoCesionario organisme = organisme("B123");
        ambOrganismeExistent(organisme);
        Servicio servei = servei("SERV1");
        ClavePrivada claveFirma = new ClavePrivada();
        servei.setClaveFirma(claveFirma);
        when(servicioDao.select("SERV1")).thenReturn(servei);

        ServicioOrganismoCesionario existent = new ServicioOrganismoCesionario();
        existent.setServicio(servei);
        when(servicioOrganismoCesionarioDao.select(servei, organisme)).thenReturn(new ArrayList<>(List.of(existent)));

        scspHelper.assignarDefaultCertificatAServei("B123", "SERV1");

        verify(pinbalDao).save(existent);
        assertSame(claveFirma, existent.getClavePrivada());
    }

    // ------------------------- assignarCertificatAServei -------------------------

    @Test
    public void assignarCertificatAServeiAmbOrganismeInexistentLlancaExcepcio() {
        when(organismoCesionarioDao.getAll()).thenReturn(new ArrayList<>());
        try {
            scspHelper.assignarCertificatAServei("NO-EXISTEIX", "SERV1", "alies1");
            fail("Hauria d'haver llançat IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // esperat
        }
    }

    @Test
    public void assignarCertificatAServeiAmbAliesInexistentLlancaExcepcio() {
        ambOrganismeExistent(organisme("B123"));
        when(servicioDao.select("SERV1")).thenReturn(servei("SERV1"));
        when(clavePrivadaDao.select()).thenReturn(new ArrayList<>());

        try {
            scspHelper.assignarCertificatAServei("B123", "SERV1", "no-existeix");
            fail("Hauria d'haver llançat IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // esperat
        }
    }

    @Test
    public void assignarCertificatAServeiCreaUnNouRegistreAmbLaClauIndicada() {
        OrganismoCesionario organisme = organisme("B123");
        ambOrganismeExistent(organisme);
        Servicio servei = servei("SERV1");
        when(servicioDao.select("SERV1")).thenReturn(servei);

        ClavePrivada clau = new ClavePrivada();
        clau.setAlias("alies1");
        when(clavePrivadaDao.select()).thenReturn(new ArrayList<>(List.of(clau)));
        when(servicioOrganismoCesionarioDao.select(servei, organisme)).thenReturn(new ArrayList<>());

        scspHelper.assignarCertificatAServei("B123", "SERV1", "alies1");

        org.mockito.ArgumentCaptor<ServicioOrganismoCesionario> captor =
                org.mockito.ArgumentCaptor.forClass(ServicioOrganismoCesionario.class);
        verify(pinbalDao).save(captor.capture());
        assertSame(clau, captor.getValue().getClavePrivada());
    }

    // ------------------------- eliminarSeviciosOrganismoServei -------------------------

    @Test
    public void eliminarSeviciosOrganismoServeiAmbServeiInexistentNoFaRes() {
        when(servicioDao.select("NO-EXISTEIX")).thenReturn(null);
        scspHelper.eliminarSeviciosOrganismoServei("NO-EXISTEIX");
        verify(pinbalDao, never()).delete(any());
    }

    @Test
    public void eliminarSeviciosOrganismoServeiEsborraTotsElsRegistresDelServei() {
        Servicio servei = servei("SERV1");
        when(servicioDao.select("SERV1")).thenReturn(servei);
        OrganismoCesionario organisme = organisme("B123");
        when(organismoCesionarioDao.getAll()).thenReturn(new ArrayList<>(List.of(organisme)));

        ServicioOrganismoCesionario coincident = new ServicioOrganismoCesionario();
        coincident.setId(1L);
        coincident.setServicio(servei);
        ServicioOrganismoCesionario altre = new ServicioOrganismoCesionario();
        altre.setId(2L);
        altre.setServicio(servei("ALTRE-SERVEI"));
        when(servicioOrganismoCesionarioDao.select(organisme)).thenReturn(new ArrayList<>(List.of(coincident, altre)));

        scspHelper.eliminarSeviciosOrganismoServei("SERV1");

        verify(pinbalDao, times(1)).delete(coincident);
        verify(pinbalDao, never()).delete(altre);
    }

    // ------------------------- eliminarSeviciosOrganismoOrganisme -------------------------

    @Test
    public void eliminarSeviciosOrganismoOrganismeAmbOrganismeInexistentNoFaRes() {
        when(organismoCesionarioDao.select("NO-EXISTEIX")).thenReturn(null);
        scspHelper.eliminarSeviciosOrganismoOrganisme("NO-EXISTEIX");
        verify(pinbalDao, never()).delete(any());
    }

    @Test
    public void eliminarSeviciosOrganismoOrganismeEsborraTotsElsRegistres() {
        OrganismoCesionario organisme = organisme("B123");
        when(organismoCesionarioDao.select("B123")).thenReturn(organisme);
        ServicioOrganismoCesionario r1 = new ServicioOrganismoCesionario();
        ServicioOrganismoCesionario r2 = new ServicioOrganismoCesionario();
        when(servicioOrganismoCesionarioDao.select(organisme)).thenReturn(new ArrayList<>(List.of(r1, r2)));

        scspHelper.eliminarSeviciosOrganismoOrganisme("B123");

        verify(pinbalDao, times(2)).delete(any());
    }

    // ------------------------- eliminarServicioOrganismo -------------------------

    @Test
    public void eliminarServicioOrganismoAmbServeiInexistentNoFaRes() {
        when(servicioDao.select("NO-EXISTEIX")).thenReturn(null);
        scspHelper.eliminarServicioOrganismo("B123", "NO-EXISTEIX");
        verify(pinbalDao, never()).delete(any());
    }

    @Test
    public void eliminarServicioOrganismoEsborraNomesElsCoincidents() {
        Servicio servei = servei("SERV1");
        when(servicioDao.select("SERV1")).thenReturn(servei);
        OrganismoCesionario organisme = organisme("B123");
        when(organismoCesionarioDao.select("B123")).thenReturn(organisme);

        ServicioOrganismoCesionario coincident = new ServicioOrganismoCesionario();
        coincident.setId(1L);
        coincident.setServicio(servei);
        ServicioOrganismoCesionario altre = new ServicioOrganismoCesionario();
        altre.setId(2L);
        altre.setServicio(servei("ALTRE"));
        when(servicioOrganismoCesionarioDao.select(organisme)).thenReturn(new ArrayList<>(List.of(coincident, altre)));

        scspHelper.eliminarServicioOrganismo("B123", "SERV1");

        verify(pinbalDao, times(1)).delete(coincident);
        verify(pinbalDao, never()).delete(altre);
    }

    // ------------------------- getServiciosOrganismoPorServei -------------------------

    @Test
    public void getServiciosOrganismoPorServeiAmbServeiInexistentRetornaListaBuida() {
        when(servicioDao.select("NO-EXISTEIX")).thenReturn(null);
        assertTrue(scspHelper.getServiciosOrganismoPorServei("NO-EXISTEIX").isEmpty());
    }

    @Test
    public void getServiciosOrganismoPorServeiRetornaNomesElsCoincidents() {
        Servicio servei = servei("SERV1");
        when(servicioDao.select("SERV1")).thenReturn(servei);
        OrganismoCesionario organisme = organisme("B123");
        when(organismoCesionarioDao.getAll()).thenReturn(new ArrayList<>(List.of(organisme)));

        ServicioOrganismoCesionario coincident = new ServicioOrganismoCesionario();
        coincident.setServicio(servei);
        ServicioOrganismoCesionario altre = new ServicioOrganismoCesionario();
        altre.setServicio(servei("ALTRE"));
        when(servicioOrganismoCesionarioDao.select(organisme)).thenReturn(new ArrayList<>(List.of(coincident, altre)));

        List<ServicioOrganismoCesionario> resultat = scspHelper.getServiciosOrganismoPorServei("SERV1");

        assertEquals(1, resultat.size());
        assertSame(coincident, resultat.get(0));
    }
}
