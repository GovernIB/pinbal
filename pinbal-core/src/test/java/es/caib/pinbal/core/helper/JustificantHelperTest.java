package es.caib.pinbal.core.helper;

import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.JustificantEstat;
import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.OrganGestor;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ProcedimentServei;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.repository.ConsultaRepository;
import es.caib.pinbal.core.repository.HistoricConsultaRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.repository.ServeiJustificantCampRepository;
import es.caib.pinbal.plugins.FirmaServidorPlugin;
import es.caib.pinbal.plugins.SignaturaResposta;
import es.caib.pinbal.scsp.ResultatEnviamentPeticio;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.DocumentTipus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JustificantHelperTest {

    @Mock
    private ServeiJustificantCampRepository serveiJustificantCampRepository;
    @Mock
    private ServeiConfigRepository serveiConfigRepository;
    @Mock
    private ConversioTipusDocumentHelper conversioTipusDocumentHelper;
    @Mock
    private PluginHelper pluginHelper;
    @Mock
    private IntegracioHelper integracioHelper;
    @Mock
    private ConfigHelper configHelper;
    @Mock
    private ConsultaRepository consultaRepository;
    @Mock
    private HistoricConsultaRepository historicConsultaRepository;
    @Mock
    private ScspHelper scspHelper;
    @Mock
    private ServeiConfig serveiConfig;
    @Spy
    @InjectMocks
    private JustificantHelper justificantHelper = new JustificantHelper();

    private ProcedimentServei procedimentServei;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        OrganGestor organGestor = new OrganGestor();
        organGestor.setCodi("ORG");

        Procediment procediment = new Procediment();
        procediment.setCodi("PROC");
        procediment.setCodiSia("SIA");
        procediment.setOrganGestor(organGestor);

        procedimentServei = ProcedimentServei.getBuilder(procediment, "SERVEI").build();

        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
        when(scspHelper.recuperarResultatEnviamentPeticio(anyString())).thenReturn(resultat);
        when(serveiConfigRepository.findByServei("SERVEI")).thenReturn(serveiConfig);
        when(configHelper.getAsBoolean("es.caib.pinbal.justificant.signar.i.custodiar", false)).thenReturn(true);
        when(configHelper.getConfig("es.caib.pinbal.plugin.arxiu.serie.documental")).thenReturn("SERIE");
        when(conversioTipusDocumentHelper.nomArxiuConvertit(anyString(), anyString())).thenReturn("justificant.pdf");
        when(pluginHelper.isPluginArxiuActiu()).thenReturn(true);

        FitxerDto fitxerDto = new FitxerDto();
        fitxerDto.setNom("justificant.pdf");
        fitxerDto.setContingut(new byte[]{1, 2, 3});
        doReturn(fitxerDto).when(justificantHelper).generar(any(Consulta.class), eq(scspHelper));

        SignaturaResposta signaturaResposta = SignaturaResposta.builder()
                .contingut(new byte[]{9, 8, 7})
                .nom("justificant.pdf")
                .mime("application/pdf")
                .build();
        when(pluginHelper.firmaServidorFirmar(any(FitxerDto.class),
                eq(FirmaServidorPlugin.TipusFirma.PADES),
                anyString(),
                anyString(),
                anyString())).thenReturn(signaturaResposta);
        when(pluginHelper.arxiuDocumentGuardarFirmaPades(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(FitxerDto.class),
                Mockito.<ContingutOrigen>any(),
                Mockito.<DocumentEstatElaboracio>any(),
                Mockito.<DocumentTipus>any())).thenReturn("DOC-1");
    }

    @Test
    public void whenCreateReturnsDuplicateThenReusesExistingExpedient() throws Exception {
        Consulta consulta = mockConsulta("PETICIO-1", "000001");

        ContingutArxiu expedientExistent = Mockito.mock(ContingutArxiu.class);
        when(expedientExistent.getIdentificador()).thenReturn("EXP-RECUPERAT");
        when(pluginHelper.arxiuExpedientCercarAmbNom("PETICIO-1"))
                .thenReturn(null)
                .thenReturn(expedientExistent);
        when(pluginHelper.arxiuExpedientCrear(
                eq("PETICIO-1"),
                eq("12345678A"),
                eq("ORG"),
                eq("SIA"),
                eq("PROC"),
                eq("SERIE")))
                .thenThrow(new RuntimeException("Duplicate child name not allowed: PETICIO-1 ORA-00001"));
        when(consultaRepository.findByScspPeticionId("PETICIO-1")).thenReturn(Collections.<Consulta>emptyList());

        justificantHelper.generarCustodiarJustificantPendent(consulta, scspHelper);

        verify(pluginHelper).arxiuDocumentGuardarFirmaPades(
                eq("PETICIO-1"),
                eq("EXP-RECUPERAT"),
                eq("000001"),
                eq("ORG"),
                eq("SERIE"),
                any(FitxerDto.class),
                Mockito.<ContingutOrigen>any(),
                Mockito.<DocumentEstatElaboracio>any(),
                Mockito.<DocumentTipus>any());
        verify(consulta).updateJustificantEstat(
                eq(JustificantEstat.OK),
                eq(true),
                eq((String) null),
                eq((String) null),
                eq((String) null),
                eq("EXP-RECUPERAT"),
                eq("DOC-1"));
    }

    @Test
    public void whenExpedientIsResolvedThenPropagatesUuidToSiblingConsultes() throws Exception {
        Consulta consulta = mockConsulta("PETICIO-2", "000001");
        Consulta consultaGermana = Mockito.mock(Consulta.class);
        when(consultaGermana.getArxiuExpedientUuid()).thenReturn(null);

        List<Consulta> consultes = Arrays.asList(consulta, consultaGermana);
        when(pluginHelper.arxiuExpedientCercarAmbNom("PETICIO-2")).thenReturn(null);
        when(pluginHelper.arxiuExpedientCrear(
                eq("PETICIO-2"),
                eq("12345678A"),
                eq("ORG"),
                eq("SIA"),
                eq("PROC"),
                eq("SERIE")))
                .thenReturn("EXP-CREAT");
        when(consultaRepository.findByScspPeticionId("PETICIO-2")).thenReturn(consultes);

        justificantHelper.generarCustodiarJustificantPendent(consulta, scspHelper);

        verify(consulta).updateArxiuExpedientUuid("EXP-CREAT");
        verify(consultaGermana).updateArxiuExpedientUuid("EXP-CREAT");
        verify(consultaRepository).save(consultes);
        verify(historicConsultaRepository, never()).findByScspPeticionId(anyString());
    }

    private Consulta mockConsulta(String scspPeticionId, String scspSolicitudId) {
        Consulta consulta = Mockito.mock(Consulta.class);
        when(consulta.getId()).thenReturn(1L);
        when(consulta.getScspPeticionId()).thenReturn(scspPeticionId);
        when(consulta.getScspSolicitudId()).thenReturn(scspSolicitudId);
        when(consulta.getProcedimentServei()).thenReturn(procedimentServei);
        when(consulta.getArxiuExpedientUuid()).thenReturn(null);
        when(consulta.getArxiuDocumentUuid()).thenReturn(null);
        when(consulta.getTitularDocumentNum()).thenReturn("12345678A");
        when(consulta.isAplicacioGuardaJustificantArxiu()).thenReturn(false);
        when(consulta.isCustodiat()).thenReturn(false);
        when(consulta.getCustodiaUrl()).thenReturn(null);
        when(consulta.getCustodiaId()).thenReturn(null);
        return consulta;
    }
}
