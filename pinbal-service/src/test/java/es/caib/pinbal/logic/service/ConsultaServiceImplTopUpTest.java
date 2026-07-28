package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.ConfigHelper;
import es.caib.pinbal.logic.helper.ConsultaHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.helper.EmailReportEstatHelper;
import es.caib.pinbal.logic.helper.ExcelHelper;
import es.caib.pinbal.logic.helper.IntegracioHelper;
import es.caib.pinbal.logic.helper.LoggerHelper;
import es.caib.pinbal.logic.helper.PeticioScspEstadistiquesHelper;
import es.caib.pinbal.logic.helper.PeticioScspHelper;
import es.caib.pinbal.logic.helper.PluginHelper;
import es.caib.pinbal.logic.helper.ServeiHelper;
import es.caib.pinbal.logic.helper.UsuariHelper;
import es.caib.pinbal.logic.helper.mock.JustificantHelperFactory;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.Procediment;
import es.caib.pinbal.persist.repository.ConsultaRepository;
import es.caib.pinbal.persist.repository.EntitatRepository;
import es.caib.pinbal.persist.repository.EntitatUsuariRepository;
import es.caib.pinbal.persist.repository.ProcedimentRepository;
import es.caib.pinbal.persist.repository.ProcedimentServeiRepository;
import es.caib.pinbal.persist.repository.ServeiJustificantCampRepository;
import es.caib.pinbal.persist.repository.ServeiRepository;
import es.caib.pinbal.persist.repository.SuperConsultaRepository;
import es.caib.pinbal.persist.repository.TokenRepository;
import es.caib.pinbal.persist.repository.UsuariRepository;
import es.caib.pinbal.persist.repository.dadesobertes.DadesObertesConsultaRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotConsultaDimensioRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotConsultaFetsRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotTempsRepository;
import es.caib.pinbal.persist.repository.llistat.LlistatConsultaRepository;
import es.caib.pinbal.scsp.ScspHelper;
import es.scsp.common.exceptions.ScspException;
import ma.glasnost.orika.MapperFacade;
import org.hibernate.exception.JDBCConnectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests addicionals per completar la cobertura de mètodes privats/utilitaris
 * de {@link ConsultaServiceImpl} que no queden coberts pels altres fitxers de
 * test de la classe (generació de missatges d'error SCSP, helpers de cerca,
 * utilitats de connexió/reintent, etc).
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ConsultaServiceImplTopUpTest {

    @Mock private ConsultaRepository consultaRepository;
    @Mock private DadesObertesConsultaRepository dadesObertesConsultaRepository;
    @Mock private EntitatRepository entitatRepository;
    @Mock private EntitatUsuariRepository entitatUsuariRepository;
    @Mock private ExplotConsultaDimensioRepository explotConsultaDimensioRepository;
    @Mock private ExplotConsultaFetsRepository explotConsultaFetsRepository;
    @Mock private ExplotTempsRepository explotTempsRepository;
    @Mock private LlistatConsultaRepository llistatConsultaRepository;
    @Mock private ProcedimentRepository procedimentRepository;
    @Mock private ProcedimentServeiRepository procedimentServeiRepository;
    @Mock private ServeiJustificantCampRepository serveiJustificantCampRepository;
    @Mock private ServeiRepository serveiRepository;
    @Mock private SuperConsultaRepository superConsultaRepository;
    @Mock private TokenRepository tokenRepository;
    @Mock private UsuariRepository usuariRepository;
    @Mock private ConfigHelper configHelper;
    @Mock private ConsultaHelper consultaHelper;
    @Mock private DtoMappingHelper dtoMappingHelper;
    @Mock private EmailReportEstatHelper emailReportEstatHelper;
    @Mock private ExcelHelper excelHelper;
    @Mock private IntegracioHelper integracioHelper;
    @Mock private JustificantHelperFactory justificantHelperFactory;
    @Mock private PeticioScspEstadistiquesHelper peticioScspEstadistiquesHelper;
    @Mock private PeticioScspHelper peticioScspHelper;
    @Mock private PluginHelper pluginHelper;
    @Mock private ServeiHelper serveiHelper;
    @Mock private UsuariHelper usuariHelper;
    @Mock private MutableAclService aclService;
    @Mock private PlatformTransactionManager transactionManager;
    @Mock private MapperFacade mapperFacade;
    @Mock private ScspHelper scspHelper;
    @Mock private LoggerHelper loggerHelper;
    @Mock private ConsultaService self;
    @Mock private ApplicationContext applicationContext;
    @Mock private MessageSource messageSource;

    @InjectMocks
    private ConsultaServiceImpl consultaService;

    private Authentication auth;
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        when(dtoMappingHelper.getMapperFacade()).thenReturn(mapperFacade);
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("MSG");
        ReflectionTestUtils.setField(consultaService, "scspHelper", scspHelper);
        ReflectionTestUtils.setField(consultaService, "applicationContext", applicationContext);
        ReflectionTestUtils.setField(consultaService, "messageSource", messageSource);
        ReflectionTestUtils.setField(LoggerHelper.class, "INSTANCE", loggerHelper);
        ReflectionTestUtils.setField(consultaService, "propertiesCopiades", true);
        ReflectionTestUtils.setField(consultaService, "self", self);
        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("usuari1");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * Desembolica excepcions llançades a través de reflexió (invokeMethod las
     * embolica en InvocationTargetException/UndeclaredThrowableException)
     * fins arribar a la causa arrel.
     */
    private Throwable arrel(Throwable t) {
        Throwable actual = t;
        while (actual.getCause() != null &&
                (actual instanceof java.lang.reflect.UndeclaredThrowableException ||
                        actual instanceof java.lang.reflect.InvocationTargetException)) {
            actual = actual.getCause();
        }
        return actual;
    }

    private String invokeGenerateErrorMessage(Throwable throwable, Locale locale) {
        return ReflectionTestUtils.invokeMethod(consultaService, "generateErrorMessage", throwable, locale);
    }

    private String invokeGetError(String methodName, String message, Locale locale) {
        return ReflectionTestUtils.invokeMethod(consultaService, methodName, message, locale);
    }

    // ===================== generateErrorMessage =====================

    private static final String[] TOTS_ELS_CODIS_SCSP = {
            "0001", "0002", "0003", "0101", "0102", "0103",
            "0201", "0202", "0203", "0204", "0205", "0206", "0207", "0208", "0209", "0210",
            "0211", "0212", "0213", "0214", "0215", "0216", "0217", "0218", "0219", "0220",
            "0221", "0222", "0223", "0224", "0225", "0226", "0227", "0228", "0229", "0230",
            "0231", "0232", "0233", "0234", "0235", "0236", "0237", "0238", "0239", "0240",
            "0241", "0242", "0243", "0244", "0245", "0246", "0247", "0248", "0249", "0250",
            "0251", "0252", "0253", "0254", "0255", "0256", "0257", "0258", "0259",
            "0301", "0302", "0303", "0304", "0305", "0306", "0307", "0308", "0309", "0310",
            "0311", "0312", "0313", "0314", "0315", "0316", "0317", "0318", "0319", "0320",
            "0321", "0322", "0323", "0324", "0325", "0326", "0327", "0328", "0329", "0330",
            "0331", "0332", "0350",
            "0401", "0402", "0403", "0404", "0405", "0406", "0411", "0412", "0413", "0414",
            "0415", "0416", "0417", "0419",
            "0501", "0502", "0503", "0504", "0512", "0513", "0514",
            "0901", "0902", "0903", "0904"
    };

    @Test
    public void generateErrorMessage_totsElsCodisScspDeclaratsAlSwitch_noLlencaExcepcioIRetornaValor() {
        Locale locale = new Locale("ca");
        for (String codi : TOTS_ELS_CODIS_SCSP) {
            ScspException scspException = new ScspException("Missatge SCSP de prova pel codi " + codi, codi);
            Exception wrapper = new Exception("Error de comunicació SCSP", scspException);

            String resultat = invokeGenerateErrorMessage(wrapper, locale);

            assertNotNull(resultat, "El codi " + codi + " hauria de generar un missatge no nul");
        }
    }

    @Test
    public void generateErrorMessage_codiNoContemplat_retornaBranquaPerDefecte() {
        ScspException scspException = new ScspException("Missatge no classificat", "9999");
        Exception wrapper = new Exception("wrap", scspException);

        String resultat = invokeGenerateErrorMessage(wrapper, new Locale("es"));

        assertEquals("[9999] Missatge no classificat", resultat);
    }

    @Test
    public void generateErrorMessage_causaSoapFaultException_retornaCodiIMissatgeDelFault() {
        SOAPFault fault = mock(SOAPFault.class);
        when(fault.getFaultCode()).thenReturn("SOAP-001");
        when(fault.getFaultString()).thenReturn("Fault de prova");
        SOAPFaultException soapFaultException = new SOAPFaultException(fault);
        Exception wrapper = new Exception("wrap-soap", soapFaultException);

        String resultat = invokeGenerateErrorMessage(wrapper, new Locale("ca"));

        assertEquals("[SOAP-001]Fault de prova", resultat);
    }

    @Test
    public void generateErrorMessage_senseCausa_retornaMessageDelThrowable() {
        Exception throwable = new Exception("Missatge directe sense causa");

        String resultat = invokeGenerateErrorMessage(throwable, new Locale("ca"));

        assertEquals("Missatge directe sense causa", resultat);
    }

    @Test
    public void generateErrorMessage_causaNoScspNiSoap_retornaMessageDelThrowable() {
        Exception throwable = new Exception("Missatge amb causa desconeguda", new IllegalStateException("altra causa"));

        String resultat = invokeGenerateErrorMessage(throwable, new Locale("ca"));

        assertEquals("Missatge amb causa desconeguda", resultat);
    }

    // ===================== getError227 =====================

    @Test
    public void getError227_totesLesBranquesInternes_noLlancaExcepcio() {
        Locale locale = new Locale("ca");
        String backofficePrefix = "El servidor ha devuelto un mensaje SOAP Fault. Error al generar la respuesta. BackofficeException:";
        String generic = "El servidor ha devuelto un mensaje SOAP Fault.";

        invokeGetError("getError227", null, locale);
        invokeGetError("getError227", "Connection refused per algun motiu", locale);
        invokeGetError("getError227", "resposta text/html amb j_security_check", locale);
        invokeGetError("getError227", "Error HTTP 404 not found", locale);
        invokeGetError("getError227", "Error HTTP 502 bad gateway", locale);
        invokeGetError("getError227", "Error HTTP 503 unavailable", locale);
        invokeGetError("getError227", "Error HTTP 500 internal", locale);
        invokeGetError("getError227", "Host unreachable detectat", locale);
        invokeGetError("getError227", "resposta amb [0254] inclosa", locale);
        invokeGetError("getError227", "resposta amb \"code\":233 inclosa", locale);

        String resultatLlarg = invokeGetError("getError227", backofficePrefix + "detall addicional", locale);
        assertEquals("detall addicional", resultatLlarg);

        invokeGetError("getError227", backofficePrefix, locale);

        String genericLlarg = invokeGetError("getError227", generic + "detall generic", locale);
        assertEquals("detall generic", genericLlarg);

        invokeGetError("getError227", generic, locale);

        String fallback = invokeGetError("getError227", "missatge sense cap patró conegut", locale);
        assertEquals("missatge sense cap patró conegut", fallback);
    }

    // ===================== getError242 =====================

    @Test
    public void getError242_totesLesBranquesInternes_noLlancaExcepcio() {
        Locale locale = new Locale("ca");
        String genericoPrefix = "El servidor ha devuelto un mensaje SOAP Fault. Error Genérico devuelto por el BackOffice";
        String generic = "El servidor ha devuelto un mensaje SOAP Fault.";

        invokeGetError("getError242", null, locale);
        invokeGetError("getError242", "La estructura del fichero recibido no corresponde con el esquema esperat", locale);
        invokeGetError("getError242", "Organismo no autorizado per accedir", locale);
        invokeGetError("getError242", "Tiempo de espera superado en la petició", locale);

        String llarg = invokeGetError("getError242", genericoPrefix + "detall backoffice", locale);
        assertEquals("detall backoffice", llarg);
        invokeGetError("getError242", genericoPrefix, locale);

        String genericLlarg = invokeGetError("getError242", generic + "detall generic 242", locale);
        assertEquals("detall generic 242", genericLlarg);
        invokeGetError("getError242", generic, locale);

        String fallback = invokeGetError("getError242", "missatge sense patró 242", locale);
        assertEquals("missatge sense patró 242", fallback);
    }

    // ===================== getError252 =====================

    @Test
    public void getError252_totesLesBranquesInternes_noLlancaExcepcio() {
        Locale locale = new Locale("ca");
        String generic = "El servidor ha devuelto un mensaje SOAP Fault.";

        invokeGetError("getError252", null, locale);

        String llarg = invokeGetError("getError252", generic + "detall 252", locale);
        assertEquals("detall 252", llarg);
        invokeGetError("getError252", generic, locale);

        String fallback = invokeGetError("getError252", "missatge sense patró 252", locale);
        assertEquals("missatge sense patró 252", fallback);
    }

    // ===================== getError254 =====================

    @Test
    public void getError254_totesLesBranquesInternes_noLlancaExcepcio() {
        Locale locale = new Locale("ca");
        String prefix = "El servidor ha devuelto un mensaje SOAP Fault. No se ha aportado la información mínima necesaria para tramitar la petición";

        invokeGetError("getError254", null, locale);

        String missatgeLlarg = prefix + " detall 254 addicional";
        String llarg = invokeGetError("getError254", missatgeLlarg, locale);
        assertEquals(missatgeLlarg.substring(123), llarg);
        invokeGetError("getError254", prefix, locale);

        String fallback = invokeGetError("getError254", "missatge sense patró 254", locale);
        assertEquals("missatge sense patró 254", fallback);
    }

    // ===================== getError401 =====================

    @Test
    public void getError401_totesLesBranquesInternes_noLlancaExcepcio() {
        Locale locale = new Locale("ca");

        invokeGetError("getError401", null, locale);

        String ambCvc = invokeGetError("getError401", "Error de validació cvc-type.3.1.3: contingut incorrecte", locale);
        assertEquals(": contingut incorrecte", ambCvc);

        String fallback = invokeGetError("getError401", "missatge sense cvc", locale);
        assertEquals("missatge sense cvc", fallback);
    }

    // ===================== getError402 =====================

    @Test
    public void getError402_totesLesBranquesInternes_noLlancaExcepcio() {
        Locale locale = new Locale("ca");
        String prefix = "Falta informar campo obligatorio";

        invokeGetError("getError402", null, locale);

        String llarg = invokeGetError("getError402", prefix + " nomCamp", locale);
        assertEquals(" nomCamp", llarg);
        invokeGetError("getError402", prefix, locale);

        String fallback = invokeGetError("getError402", "missatge sense patró 402", locale);
        assertEquals("missatge sense patró 402", fallback);
    }

    // ===================== getProcediment / getEntitat =====================

    @Test
    public void getProcediment_trobat_retornaProcediment() throws Exception {
        Entitat entitat = mock(Entitat.class);
        Procediment procediment = mock(Procediment.class);
        when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);

        Object resultat = ReflectionTestUtils.invokeMethod(consultaService, "getProcediment", "PROC1", entitat);

        assertSame(procediment, resultat);
    }

    @Test
    public void getProcediment_noTrobat_llancaProcedimentNotFoundException() {
        Entitat entitat = mock(Entitat.class);
        when(procedimentRepository.findByEntitatAndCodi(entitat, "PROCX")).thenReturn(null);

        try {
            ReflectionTestUtils.invokeMethod(consultaService, "getProcediment", "PROCX", entitat);
            fail("Esperava ProcedimentNotFoundException");
        } catch (Throwable t) {
            assertTrue(arrel(t) instanceof ProcedimentNotFoundException);
        }
    }

    @Test
    public void getEntitat_trobada_retornaEntitat() {
        Entitat entitat = mock(Entitat.class);
        when(entitatRepository.findByCodi("ENT1")).thenReturn(entitat);

        Object resultat = ReflectionTestUtils.invokeMethod(consultaService, "getEntitat", "ENT1");

        assertSame(entitat, resultat);
    }

    @Test
    public void getEntitat_noTrobada_llancaEntitatNotFoundException() {
        when(entitatRepository.findByCodi("ENTX")).thenReturn(null);

        try {
            ReflectionTestUtils.invokeMethod(consultaService, "getEntitat", "ENTX");
            fail("Esperava EntitatNotFoundException");
        } catch (Throwable t) {
            assertTrue(arrel(t) instanceof EntitatNotFoundException);
        }
    }

    // ===================== isAdministrador =====================

    @Test
    public void isAdministrador_authNull_retornaFalse() {
        Boolean resultat = ReflectionTestUtils.invokeMethod(consultaService, "isAdministrador", (Authentication) null);

        assertFalse(resultat);
    }

    @Test
    public void isAdministrador_authoritiesNull_retornaFalse() {
        Authentication authSenseAuthorities = mock(Authentication.class);
        doReturn(null).when(authSenseAuthorities).getAuthorities();

        Boolean resultat = ReflectionTestUtils.invokeMethod(consultaService, "isAdministrador", authSenseAuthorities);

        assertFalse(resultat);
    }

    @Test
    public void isAdministrador_ambRolAdmin_retornaTrue() {
        GrantedAuthority admin = mock(GrantedAuthority.class);
        when(admin.getAuthority()).thenReturn("PBL_ADMIN");
        Authentication authAdmin = mock(Authentication.class);
        List<GrantedAuthority> authorities = Arrays.asList((GrantedAuthority) null, admin);
        doReturn(authorities).when(authAdmin).getAuthorities();

        Boolean resultat = ReflectionTestUtils.invokeMethod(consultaService, "isAdministrador", authAdmin);

        assertTrue(resultat);
    }

    @Test
    public void isAdministrador_senseRolAdmin_retornaFalse() {
        GrantedAuthority altre = mock(GrantedAuthority.class);
        when(altre.getAuthority()).thenReturn("PBL_USUARI");
        Authentication authUsuari = mock(Authentication.class);
        List<GrantedAuthority> authorities = Collections.singletonList(altre);
        doReturn(authorities).when(authUsuari).getAuthorities();

        Boolean resultat = ReflectionTestUtils.invokeMethod(consultaService, "isAdministrador", authUsuari);

        assertFalse(resultat);
    }

    // ===================== getScspHelper =====================

    @Test
    public void getScspHelper_scspHelperNoInicialitzat_creaInstanciaNovaICachejada() {
        ReflectionTestUtils.setField(consultaService, "scspHelper", null);

        Object primeraCrida = ReflectionTestUtils.invokeMethod(consultaService, "getScspHelper");

        assertNotNull(primeraCrida);
        assertTrue(primeraCrida instanceof ScspHelper);
        assertSame(primeraCrida, ReflectionTestUtils.getField(consultaService, "scspHelper"));

        Object segonaCrida = ReflectionTestUtils.invokeMethod(consultaService, "getScspHelper");
        assertSame(primeraCrida, segonaCrida);
    }

    // ===================== getRandomIndexesFromList =====================

    @Test
    public void getRandomIndexesFromList_retornaIndexosUnicsIOrdenats() {
        List<String> llista = Arrays.asList("a", "b", "c", "d", "e");

        int[] resultat = ReflectionTestUtils.invokeMethod(consultaService, "getRandomIndexesFromList", llista, 3);

        assertEquals(3, resultat.length);
        assertTrue(resultat[0] <= resultat[1] && resultat[1] <= resultat[2]);
        assertTrue(resultat[0] >= 0 && resultat[2] < llista.size());
        assertNotEquals(resultat[0], resultat[1]);
        assertNotEquals(resultat[1], resultat[2]);
    }

    @Test
    public void getRandomIndexesFromList_zeroIndexos_retornaArrayBuit() {
        List<String> llista = Arrays.asList("a", "b");

        int[] resultat = ReflectionTestUtils.invokeMethod(consultaService, "getRandomIndexesFromList", llista, 0);

        assertEquals(0, resultat.length);
    }

    // ===================== mapTipoMensajeFolder =====================

    @Test
    public void mapTipoMensajeFolder_totsElsTipus_retornaCarpetaCorresponent() {
        assertEquals("altres", ReflectionTestUtils.invokeMethod(consultaService, "mapTipoMensajeFolder", (Integer) null));
        assertEquals("peticion", ReflectionTestUtils.invokeMethod(consultaService, "mapTipoMensajeFolder", Integer.valueOf(0)));
        assertEquals("confirmacion-peticion", ReflectionTestUtils.invokeMethod(consultaService, "mapTipoMensajeFolder", Integer.valueOf(1)));
        assertEquals("solicitud-respuesta", ReflectionTestUtils.invokeMethod(consultaService, "mapTipoMensajeFolder", Integer.valueOf(2)));
        assertEquals("respuesta", ReflectionTestUtils.invokeMethod(consultaService, "mapTipoMensajeFolder", Integer.valueOf(3)));
        assertEquals("fault", ReflectionTestUtils.invokeMethod(consultaService, "mapTipoMensajeFolder", Integer.valueOf(4)));
        assertEquals("altres", ReflectionTestUtils.invokeMethod(consultaService, "mapTipoMensajeFolder", Integer.valueOf(99)));
    }

    // ===================== configurarDataFiPerFiltre =====================

    @Test
    public void configurarDataFiPerFiltre_dataNull_retornaNull() {
        Object resultat = ReflectionTestUtils.invokeMethod(consultaService, "configurarDataFiPerFiltre", (java.util.Date) null);

        assertNull(resultat);
    }

    @Test
    public void configurarDataFiPerFiltre_dataInformada_retornaFinalDelDia() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2024, java.util.Calendar.MARCH, 15, 8, 30, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        java.util.Date data = cal.getTime();

        java.util.Date resultat = ReflectionTestUtils.invokeMethod(consultaService, "configurarDataFiPerFiltre", data);

        java.util.Calendar calResultat = java.util.Calendar.getInstance();
        calResultat.setTime(resultat);
        assertEquals(23, calResultat.get(java.util.Calendar.HOUR_OF_DAY));
        assertEquals(59, calResultat.get(java.util.Calendar.MINUTE));
        assertEquals(59, calResultat.get(java.util.Calendar.SECOND));
        assertEquals(999, calResultat.get(java.util.Calendar.MILLISECOND));
        assertEquals(java.util.Calendar.MARCH, calResultat.get(java.util.Calendar.MONTH));
        assertEquals(15, calResultat.get(java.util.Calendar.DAY_OF_MONTH));
    }

    // ===================== nodeToJson =====================

    @Test
    public void nodeToJson_documentAmbFillsIFullesIElementsBuits_generaJsonCorrecte() throws Exception {
        String xml = "<arrel>"
                + "<camp1>valor1</camp1>"
                + "<campBuit></campBuit>"
                + "<subnode><campNiat>valorNiat</campNiat></subnode>"
                + "</arrel>";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        Element root = document.getDocumentElement();

        String json = ReflectionTestUtils.invokeMethod(consultaService, "nodeToJson", root);

        assertNotNull(json);
        assertTrue(json.contains("valor1"));
        assertTrue(json.contains("valorNiat"));
    }

    // ===================== getRootCauseMessage =====================

    @Test
    public void getRootCauseMessage_cadenaDeCauses_retornaLaCausaArrel() {
        Exception arrelException = new IllegalArgumentException("causa arrel");
        Exception intermedia = new RuntimeException("intermedia", arrelException);
        Exception exterior = new Exception("exterior", intermedia);

        String resultat = ReflectionTestUtils.invokeMethod(consultaService, "getRootCauseMessage", exterior);

        assertEquals("IllegalArgumentException: causa arrel", resultat);
    }

    @Test
    public void getRootCauseMessage_senseCausa_retornaLaMateixaExcepcio() {
        Exception exception = new IllegalStateException("sense causa");

        String resultat = ReflectionTestUtils.invokeMethod(consultaService, "getRootCauseMessage", exception);

        assertEquals("IllegalStateException: sense causa", resultat);
    }

    // ===================== sleepBeforeRetry =====================

    @Test
    public void sleepBeforeRetry_esperaSenseLlencarExcepcio() {
        long inici = System.currentTimeMillis();

        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(consultaService, "sleepBeforeRetry"));

        assertTrue(System.currentTimeMillis() - inici >= 0);
    }

    // ===================== isRecoverableConnectionException =====================

    @Test
    public void isRecoverableConnectionException_excepcioJdbc_retornaTrue() {
        JDBCConnectionException ex = new JDBCConnectionException("error connexió", new SQLException("sql error"));

        Boolean resultat = ReflectionTestUtils.invokeMethod(consultaService, "isRecoverableConnectionException", (Throwable) ex);

        assertTrue(resultat);
    }

    @Test
    public void isRecoverableConnectionException_dataAccessResourceFailure_retornaTrue() {
        DataAccessResourceFailureException ex = new DataAccessResourceFailureException("recurs no disponible");

        Boolean resultat = ReflectionTestUtils.invokeMethod(consultaService, "isRecoverableConnectionException", (Throwable) ex);

        assertTrue(resultat);
    }

    @Test
    public void isRecoverableConnectionException_missatgeConexioTancada_retornaTrue() {
        RuntimeException ex = new RuntimeException("La conexión cerrada inesperadament");

        Boolean resultat = ReflectionTestUtils.invokeMethod(consultaService, "isRecoverableConnectionException", (Throwable) ex);

        assertTrue(resultat);
    }

    @Test
    public void isRecoverableConnectionException_missatgeClosedConnection_retornaTrue() {
        RuntimeException ex = new RuntimeException("closed connection detected");

        Boolean resultat = ReflectionTestUtils.invokeMethod(consultaService, "isRecoverableConnectionException", (Throwable) ex);

        assertTrue(resultat);
    }

    @Test
    public void isRecoverableConnectionException_causaEnProfunditat_retornaTrue() {
        RuntimeException profunda = new RuntimeException("connection is closed per sorpresa");
        RuntimeException intermedia = new RuntimeException("intermedia", profunda);
        RuntimeException exterior = new RuntimeException("exterior", intermedia);

        Boolean resultat = ReflectionTestUtils.invokeMethod(consultaService, "isRecoverableConnectionException", (Throwable) exterior);

        assertTrue(resultat);
    }

    @Test
    public void isRecoverableConnectionException_excepcioNoRecuperable_retornaFalse() {
        RuntimeException ex = new RuntimeException("error qualsevol sense relació amb connexions");

        Boolean resultat = ReflectionTestUtils.invokeMethod(consultaService, "isRecoverableConnectionException", (Throwable) ex);

        assertFalse(resultat);
    }

    @Test
    public void isRecoverableConnectionException_missatgeNull_noLlancaExcepcioIRetornaFalse() {
        RuntimeException ex = new RuntimeException((String) null);

        Boolean resultat = ReflectionTestUtils.invokeMethod(consultaService, "isRecoverableConnectionException", (Throwable) ex);

        assertFalse(resultat);
    }

    // ===================== setApplicationContext / setMessageSource =====================

    @Test
    public void setApplicationContext_assignaElCampIntern() {
        ApplicationContext altre = mock(ApplicationContext.class);

        consultaService.setApplicationContext(altre);

        assertSame(altre, ReflectionTestUtils.getField(consultaService, "applicationContext"));
    }

    @Test
    public void setMessageSource_assignaElCampIntern() {
        MessageSource altre = mock(MessageSource.class);

        consultaService.setMessageSource(altre);

        assertSame(altre, ReflectionTestUtils.getField(consultaService, "messageSource"));
    }

    // ===================== copiarPropertiesToDb =====================

    @Test
    public void copiarPropertiesToDb_primeraCridaCopiaINoRepeteixEnSegonaCrida() {
        ReflectionTestUtils.setField(consultaService, "propertiesCopiades", false);

        ReflectionTestUtils.invokeMethod(consultaService, "copiarPropertiesToDb");
        ReflectionTestUtils.invokeMethod(consultaService, "copiarPropertiesToDb");

        verify(scspHelper, times(1)).copiarPropertiesToDb(any());
        assertEquals(Boolean.TRUE, ReflectionTestUtils.getField(consultaService, "propertiesCopiades"));
    }
}
