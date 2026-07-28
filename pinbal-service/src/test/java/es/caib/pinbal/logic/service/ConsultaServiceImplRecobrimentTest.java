package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.*;
import es.caib.pinbal.logic.helper.mock.JustificantHelperFactory;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.EstatTipus;
import es.caib.pinbal.logic.intf.dto.RecobrimentSolicitudDto;
import es.caib.pinbal.logic.intf.dto.arxiu.ArxiuDetallDto;
import es.caib.pinbal.logic.intf.service.exception.*;
import es.caib.pinbal.persist.entity.Consulta;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.Procediment;
import es.caib.pinbal.persist.entity.ProcedimentServei;
import es.caib.pinbal.persist.repository.*;
import es.caib.pinbal.persist.repository.dadesobertes.DadesObertesConsultaRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotConsultaDimensioRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotConsultaFetsRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotTempsRepository;
import es.caib.pinbal.persist.repository.llistat.LlistatConsultaRepository;
import es.caib.pinbal.plugin.SistemaExternException;
import es.caib.pinbal.scsp.ResultatEnviamentPeticio;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pinbal.scsp.Solicitud;
import es.caib.pluginsib.arxiu.api.Document;
import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ConsultaServiceImplRecobrimentTest {

	@Mock
	private ConsultaRepository consultaRepository;
	@Mock
	private DadesObertesConsultaRepository dadesObertesConsultaRepository;
	@Mock
	private EntitatRepository entitatRepository;
	@Mock
	private EntitatUsuariRepository entitatUsuariRepository;
	@Mock
	private ExplotConsultaDimensioRepository explotConsultaDimensioRepository;
	@Mock
	private ExplotConsultaFetsRepository explotConsultaFetsRepository;
	@Mock
	private ExplotTempsRepository explotTempsRepository;
	@Mock
	private LlistatConsultaRepository llistatConsultaRepository;
	@Mock
	private ProcedimentRepository procedimentRepository;
	@Mock
	private ProcedimentServeiRepository procedimentServeiRepository;
	@Mock
	private ServeiJustificantCampRepository serveiJustificantCampRepository;
	@Mock
	private ServeiRepository serveiRepository;
	@Mock
	private SuperConsultaRepository superConsultaRepository;
	@Mock
	private TokenRepository tokenRepository;
	@Mock
	private UsuariRepository usuariRepository;
	@Mock
	private ConfigHelper configHelper;
	@Mock
	private ConsultaHelper consultaHelper;
	@Mock
	private DtoMappingHelper dtoMappingHelper;
	@Mock
	private EmailReportEstatHelper emailReportEstatHelper;
	@Mock
	private ExcelHelper excelHelper;
	@Mock
	private IntegracioHelper integracioHelper;
	@Mock
	private JustificantHelperFactory justificantHelperFactory;
	@Mock
	private PeticioScspEstadistiquesHelper peticioScspEstadistiquesHelper;
	@Mock
	private PeticioScspHelper peticioScspHelper;
	@Mock
	private PluginHelper pluginHelper;
	@Mock
	private ServeiHelper serveiHelper;
	@Mock
	private UsuariHelper usuariHelper;
	@Mock
	private MutableAclService aclService;
	@Mock
	private PlatformTransactionManager transactionManager;
	@Mock
	private MapperFacade mapperFacade;
	@Mock
	private ScspHelper scspHelper;
	@Mock
	private LoggerHelper loggerHelper;

	@InjectMocks
	private ConsultaServiceImpl consultaService;

	private Authentication auth;
	private SecurityContext securityContext;

	@BeforeEach
	public void setUp() {
		when(dtoMappingHelper.getMapperFacade()).thenReturn(mapperFacade);
		ReflectionTestUtils.setField(consultaService, "scspHelper", scspHelper);
		ReflectionTestUtils.setField(LoggerHelper.class, "INSTANCE", loggerHelper);
		ReflectionTestUtils.setField(consultaService, "propertiesCopiades", true);
		auth = mock(Authentication.class);
		when(auth.getName()).thenReturn("usuari1");
		when(auth.getAuthorities()).thenReturn(Collections.emptyList());
		securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(auth);
		SecurityContextHolder.setContext(securityContext);
		when(mapperFacade.map(any(), eq(ConsultaDto.class))).thenAnswer(invocation -> new ConsultaDto());
	}

	@AfterEach
	public void tearDown() {
		System.clearProperty("es.caib.pinbal.arxiu.document.consultar.mock");
	}

	private Entitat crearEntitat(String cif) {
		return Entitat.getBuilder("AJTEST", "Ajuntament de Test", cif, Entitat.EntitatTipus.AJUNTAMENT).build();
	}

	private Procediment crearProcediment(Entitat entitat,
	                                     String codi) {
		return Procediment.getBuilder(entitat, codi, "Procediment de test", "Departament", null, null, null, null).build();
	}

	private ProcedimentServei crearProcedimentServeiActiu(Procediment procediment,
	                                                      String serveiCodi) {
		return ProcedimentServei.getBuilder(procediment, serveiCodi).build();
	}

	private RecobrimentSolicitudDto crearSolicitud(String entitatCif,
	                                               String procedimentCodi) {
		RecobrimentSolicitudDto solicitud = new RecobrimentSolicitudDto();
		solicitud.setEntitatCif(entitatCif);
		solicitud.setProcedimentCodi(procedimentCodi);
		solicitud.setFuncionariNom("Joan Funcionari");
		solicitud.setFuncionariNif("12345678A");
		solicitud.setDepartamentNom("Departament de Test");
		solicitud.setTitularDocumentNum("87654321B");
		solicitud.setTitularNom("Maria");
		solicitud.setTitularLlinatge1("Ferrer");
		solicitud.setFinalitat("Tramitació expedient");
		solicitud.setConsentiment(ConsultaDto.Consentiment.Si);
		solicitud.setExpedientId("EXP-1");
		return solicitud;
	}

	private ResultatEnviamentPeticio resultatOk() {
		ResultatEnviamentPeticio resultat = mock(ResultatEnviamentPeticio.class);
		when(resultat.isError()).thenReturn(false);
		when(resultat.getIdsSolicituds()).thenReturn(new String[]{"SOL-1"});
		when(resultat.getEstatCodi()).thenReturn("0003");
		when(resultat.getEstatDescripcio()).thenReturn("Tramitada");
		return resultat;
	}

	private ResultatEnviamentPeticio resultatError() {
		ResultatEnviamentPeticio resultat = mock(ResultatEnviamentPeticio.class);
		when(resultat.isError()).thenReturn(true);
		when(resultat.getErrorCodi()).thenReturn("0227");
		when(resultat.getErrorDescripcio()).thenReturn("Error de comunicació");
		when(resultat.getEstatCodi()).thenReturn("0227");
		when(resultat.getEstatDescripcio()).thenReturn("Error de comunicació");
		return resultat;
	}

	// ---------- novaConsultaRecobriment ----------

	@Test
	public void novaConsultaRecobriment_entitatNoTrobada_llancaException() {
		RecobrimentSolicitudDto solicitud = crearSolicitud("INEXISTENT", "PROC1");
		when(entitatRepository.findByCif("INEXISTENT")).thenReturn(null);

		assertThrows(EntitatNotFoundException.class, () -> consultaService.novaConsultaRecobriment("SV001", solicitud));
	}

	@Test
	public void novaConsultaRecobriment_procedimentNoTrobat_llancaException() {
		Entitat entitat = crearEntitat("Q0700001A");
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROCX");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROCX")).thenReturn(null);

		assertThrows(ProcedimentNotFoundException.class, () -> consultaService.novaConsultaRecobriment("SV001", solicitud));
	}

	@Test
	public void novaConsultaRecobriment_procedimentServeiNoTrobat_llancaException() {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROC1");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(null);

		assertThrows(ProcedimentServeiNotFoundException.class, () -> consultaService.novaConsultaRecobriment("SV001", solicitud));
	}

	@Test
	public void novaConsultaRecobriment_procedimentServeiNoActiu_llancaException() {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		ps.updateActiu(false);
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROC1");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(ps);

		assertThrows(ProcedimentServeiNotFoundException.class, () -> consultaService.novaConsultaRecobriment("SV001", solicitud));
	}

	@Test
	public void novaConsultaRecobriment_usuariSenseAcces_llancaException() {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROC1");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(ps);
		when(serveiHelper.isServeiPermesPerUsuari(entitat, procediment, "SV001")).thenReturn(false);

		assertThrows(ServeiNotAllowedException.class, () -> consultaService.novaConsultaRecobriment("SV001", solicitud));
	}

	@Test
	public void novaConsultaRecobriment_generacioIdPeticioFalla_propagaExcepcio() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROC1");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(ps);
		when(serveiHelper.isServeiPermesPerUsuari(entitat, procediment, "SV001")).thenReturn(true);
		when(scspHelper.generarIdPeticion("SV001")).thenThrow(new ConsultaScspGeneracioException("error generant"));

		assertThrows(ConsultaScspGeneracioException.class, () -> consultaService.novaConsultaRecobriment("SV001", solicitud));
		verify(consultaRepository, never()).save(any(Consulta.class));
	}

	@Test
	public void novaConsultaRecobriment_ok_senseError_desaConsultaIRetornaDto() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROC1");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(ps);
		when(serveiHelper.isServeiPermesPerUsuari(entitat, procediment, "SV001")).thenReturn(true);
		when(scspHelper.generarIdPeticion("SV001")).thenReturn("PET-1");
		Solicitud solicitudEnviar = new Solicitud();
		when(peticioScspHelper.convertirEnSolicitud(
			eq(entitat),
			eq(procediment),
			eq("SV001"),
			anyString(),
			anyString(),
			any(),
			anyString(),
			anyString(),
			anyString(),
			anyString(),
			anyString(),
			anyString(),
			any(),
			anyString(),
			anyString(),
			anyString(),
			any(),
			eq(ps),
			eq(scspHelper))
		).thenReturn(solicitudEnviar);
		ResultatEnviamentPeticio resultat = resultatOk();
		when(peticioScspHelper.enviarPeticioScsp(any(Consulta.class), anyList(), eq(true), eq(true), eq(scspHelper))).thenReturn(resultat);
		when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ConsultaDto resposta = consultaService.novaConsultaRecobriment("SV001", solicitud);

		assertNotNull(resposta);
		assertNull(resposta.getRespostaEstadoCodigo());
		verify(consultaRepository).save(any(Consulta.class));
		verify(consultaHelper).propagaCreacioConsulta(any(Consulta.class));
		verify(integracioHelper).addAccioOk(eq("PET-1"), anyString(), anyString(), anyMap(), any(), anyLong());
	}

	@Test
	public void novaConsultaRecobriment_ok_ambError_ompleCampsErrorEnResposta() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROC1");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(ps);
		when(serveiHelper.isServeiPermesPerUsuari(entitat, procediment, "SV001")).thenReturn(true);
		when(scspHelper.generarIdPeticion("SV001")).thenReturn("PET-2");
		when(peticioScspHelper.convertirEnSolicitud(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(new Solicitud());
		ResultatEnviamentPeticio resultat = resultatError();
		when(peticioScspHelper.enviarPeticioScsp(any(Consulta.class), anyList(), eq(true), eq(true), eq(scspHelper))).thenReturn(resultat);
		when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ConsultaDto resposta = consultaService.novaConsultaRecobriment("SV001", solicitud);

		assertEquals("0227", resposta.getRespostaEstadoCodigo());
		assertEquals("Error de comunicació", resposta.getRespostaEstadoError());
		verify(integracioHelper).addAccioError(eq("PET-2"), anyString(), anyString(), anyMap(), any(), anyLong(), anyString(), isNull());
	}

	@Test
	public void novaConsultaRecobriment_enviamentComunicacioException_generaConsultaAmbError() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROC1");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(ps);
		when(serveiHelper.isServeiPermesPerUsuari(entitat, procediment, "SV001")).thenReturn(true);
		when(scspHelper.generarIdPeticion("SV001")).thenReturn("PET-3");
		when(peticioScspHelper.convertirEnSolicitud(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(new Solicitud());
		when(peticioScspHelper.enviarPeticioScsp(any(Consulta.class), anyList(), eq(true), eq(true), eq(scspHelper))).thenThrow(new ConsultaScspComunicacioException("PET-3", "error de xarxa"));
		when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ConsultaDto resposta = consultaService.novaConsultaRecobriment("SV001", solicitud);

		assertEquals("ERROR", resposta.getRespostaEstadoCodigo());
		assertEquals("error de xarxa", resposta.getRespostaEstadoError());
		verify(peticioScspHelper).updateEstatConsultaError(any(Consulta.class), eq("error de xarxa"));
		verify(consultaHelper).propagaCanviConsulta(any(Consulta.class));
	}

	// ---------- novaConsultaRecobrimentInit ----------

	@Test
	public void novaConsultaRecobrimentInit_entitatNoTrobada_llancaException() {
		RecobrimentSolicitudDto solicitud = crearSolicitud("INEXISTENT", "PROC1");
		when(entitatRepository.findByCif("INEXISTENT")).thenReturn(null);

		assertThrows(EntitatNotFoundException.class, () -> consultaService.novaConsultaRecobrimentInit("SV001", solicitud));
	}

	@Test
	public void novaConsultaRecobrimentInit_procedimentNoTrobatPerCodiNiSia_llancaException() {
		Entitat entitat = crearEntitat("Q0700001A");
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROCX");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROCX")).thenReturn(null);
		when(procedimentRepository.findByEntitatAndCodiSia(entitat, "PROCX")).thenReturn(null);

		assertThrows(ProcedimentNotFoundException.class, () -> consultaService.novaConsultaRecobrimentInit("SV001", solicitud));
	}

	@Test
	public void novaConsultaRecobrimentInit_procedimentTrobatPerCodiSia_continua() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "SIA1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "SIA1");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "SIA1")).thenReturn(null);
		when(procedimentRepository.findByEntitatAndCodiSia(entitat, "SIA1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(ps);
		when(serveiHelper.isServeiPermesPerUsuari(entitat, procediment, "SV001")).thenReturn(true);
		when(scspHelper.generarIdPeticion("SV001")).thenReturn("PET-4");
		when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ConsultaDto resposta = consultaService.novaConsultaRecobrimentInit("SV001", solicitud);

		assertNotNull(resposta);
		verify(consultaHelper).propagaCreacioConsulta(any(Consulta.class));
	}

	@Test
	public void novaConsultaRecobrimentInit_procedimentServeiNoTrobat_llancaException() {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROC1");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(null);

		assertThrows(ProcedimentServeiNotFoundException.class, () -> consultaService.novaConsultaRecobrimentInit("SV001", solicitud));
	}

	@Test
	public void novaConsultaRecobrimentInit_usuariSenseAcces_llancaException() {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROC1");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(ps);
		when(serveiHelper.isServeiPermesPerUsuari(entitat, procediment, "SV001")).thenReturn(false);

		assertThrows(ServeiNotAllowedException.class, () -> consultaService.novaConsultaRecobrimentInit("SV001", solicitud));
	}

	@Test
	public void novaConsultaRecobrimentInit_ok_desaIPropaga() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROC1");
		solicitud.setAplicacioGuardaJustificantArxiu(true);
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(ps);
		when(serveiHelper.isServeiPermesPerUsuari(entitat, procediment, "SV001")).thenReturn(true);
		when(scspHelper.generarIdPeticion("SV001")).thenReturn("PET-5");
		when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ConsultaDto resposta = consultaService.novaConsultaRecobrimentInit("SV001", solicitud);

		assertNotNull(resposta);
		verify(consultaRepository).save(any(Consulta.class));
		verify(integracioHelper).addAccioOk(eq("PET-5"), anyString(), anyString(), anyMap(), any(), anyLong());
	}

	// ---------- novaConsultaRecobrimentEnviament ----------

	private Consulta crearConsultaRecobriment(ProcedimentServei ps,
	                                          String idPeticio) {
		Consulta consulta = Consulta.getBuilder(idPeticio, "Joan Funcionari", "12345678A", null, "87654321B", "Maria", "Ferrer", null, null, "Departament de Test", ps, "Tramitació", ConsultaDto.Consentiment.Si, "EXP-1", true, false, null).build();
		return consulta;
	}

	@Test
	public void novaConsultaRecobrimentEnviament_consultaNoTrobada_llancaException() {
		when(consultaRepository.findById(99L)).thenReturn(Optional.empty());
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROC1");

		assertThrows(ConsultaNotFoundException.class, () -> consultaService.novaConsultaRecobrimentEnviament(99L, solicitud));
	}

	@Test
	public void novaConsultaRecobrimentEnviament_ok_senseError_actualitzaEstat() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		Consulta consulta = crearConsultaRecobriment(ps, "PET-6");
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROC1");
		when(consultaRepository.findById(6L)).thenReturn(Optional.of(consulta));
		when(peticioScspHelper.convertirEnSolicitud(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(new Solicitud());
		ResultatEnviamentPeticio resultat = resultatOk();
		when(peticioScspHelper.enviarPeticioScsp(eq(consulta), anyList(), eq(true), eq(true), eq(scspHelper))).thenReturn(resultat);

		consultaService.novaConsultaRecobrimentEnviament(6L, solicitud);

		assertEquals(EstatTipus.Processant, consulta.getEstat());
		verify(consultaHelper).propagaCanviConsulta(consulta);
		verify(integracioHelper).addAccioOk(anyString(), anyString(), anyString(), anyMap(), any(), anyLong());
	}

	@Test
	public void novaConsultaRecobrimentEnviament_ok_ambError_registraAccioError() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		Consulta consulta = crearConsultaRecobriment(ps, "PET-7");
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROC1");
		when(consultaRepository.findById(7L)).thenReturn(Optional.of(consulta));
		when(peticioScspHelper.convertirEnSolicitud(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(new Solicitud());
		ResultatEnviamentPeticio resultat = resultatError();
		when(peticioScspHelper.enviarPeticioScsp(eq(consulta), anyList(), eq(true), eq(true), eq(scspHelper))).thenReturn(resultat);

		consultaService.novaConsultaRecobrimentEnviament(7L, solicitud);

		verify(integracioHelper).addAccioError(anyString(), anyString(), anyString(), anyMap(), any(), anyLong(), anyString(), isNull());
	}

	@Test
	public void novaConsultaRecobrimentEnviament_generacioSolicitudFalla_propagaExcepcio() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		Consulta consulta = crearConsultaRecobriment(ps, "PET-8");
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROC1");
		when(consultaRepository.findById(8L)).thenReturn(Optional.of(consulta));
		when(peticioScspHelper.convertirEnSolicitud(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenThrow(new ConsultaScspGeneracioException("no s'ha pogut generar"));

		assertThrows(ConsultaScspGeneracioException.class, () -> consultaService.novaConsultaRecobrimentEnviament(8L, solicitud));
	}

	@Test
	public void novaConsultaRecobrimentEnviament_enviamentComunicacioException_gestionaError() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		Consulta consulta = crearConsultaRecobriment(ps, "PET-9");
		RecobrimentSolicitudDto solicitud = crearSolicitud("Q0700001A", "PROC1");
		when(consultaRepository.findById(9L)).thenReturn(Optional.of(consulta));
		when(peticioScspHelper.convertirEnSolicitud(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(new Solicitud());
		when(peticioScspHelper.enviarPeticioScsp(eq(consulta), anyList(), eq(true), eq(true), eq(scspHelper))).thenThrow(new ConsultaScspComunicacioException("PET-9", "error de xarxa enviament"));
		when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocation -> invocation.getArgument(0));

		consultaService.novaConsultaRecobrimentEnviament(9L, solicitud);

		verify(peticioScspHelper).updateEstatConsultaError(eq(consulta), eq("error de xarxa enviament"));
	}

	// ---------- novaConsultaRecobrimentEstat ----------

	@Test
	public void novaConsultaRecobrimentEstat_consultaNoTrobada_llancaException() {
		when(consultaRepository.findById(50L)).thenReturn(Optional.empty());

		assertThrows(ConsultaNotFoundException.class, () -> consultaService.novaConsultaRecobrimentEstat(50L));
	}

	@Test
	public void novaConsultaRecobrimentEstat_ok_senseError_retornaDto() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		Consulta consulta = crearConsultaRecobriment(ps, "PET-10");
		when(consultaRepository.findById(10L)).thenReturn(Optional.of(consulta));
		ResultatEnviamentPeticio resultat = resultatOk();
		when(scspHelper.recuperarResultatEnviamentPeticio("PET-10")).thenReturn(resultat);

		ConsultaDto resposta = consultaService.novaConsultaRecobrimentEstat(10L);

		assertNotNull(resposta);
		assertNull(resposta.getRespostaEstadoCodigo());
		verify(peticioScspHelper).updateEstatConsulta(eq(consulta), eq(resultat), anyMap());
		verify(consultaHelper).propagaCanviConsulta(consulta);
		verify(integracioHelper).addAccioOk(eq("PET-10"), anyString(), anyString(), anyMap(), any(), anyLong());
	}

	@Test
	public void novaConsultaRecobrimentEstat_ok_ambError_ompleCampsError() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		Consulta consulta = crearConsultaRecobriment(ps, "PET-11");
		when(consultaRepository.findById(11L)).thenReturn(Optional.of(consulta));
		ResultatEnviamentPeticio resultat = resultatError();
		when(scspHelper.recuperarResultatEnviamentPeticio("PET-11")).thenReturn(resultat);

		ConsultaDto resposta = consultaService.novaConsultaRecobrimentEstat(11L);

		assertEquals("0227", resposta.getRespostaEstadoCodigo());
		assertEquals("Error de comunicació", resposta.getRespostaEstadoError());
	}

	@Test
	public void novaConsultaRecobrimentEstat_estatJaError_noActualitzaEstat() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		Consulta consulta = crearConsultaRecobriment(ps, "PET-12");
		consulta.updateEstat(EstatTipus.Error);
		when(consultaRepository.findById(12L)).thenReturn(Optional.of(consulta));
		ResultatEnviamentPeticio resultat = resultatOk();
		when(scspHelper.recuperarResultatEnviamentPeticio("PET-12")).thenReturn(resultat);

		ConsultaDto resposta = consultaService.novaConsultaRecobrimentEstat(12L);

		assertNotNull(resposta);
		verify(peticioScspHelper, never()).updateEstatConsulta(any(), any(), any());
	}

	@Test
	public void novaConsultaRecobrimentEstat_scspLlancaExcepcio_llancaConsultaScspEstatException() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		Consulta consulta = crearConsultaRecobriment(ps, "PET-13");
		when(consultaRepository.findById(13L)).thenReturn(Optional.of(consulta));
		when(scspHelper.recuperarResultatEnviamentPeticio("PET-13")).thenThrow(new RuntimeException("comunicació perduda"));

		assertThrows(ConsultaScspEstatException.class, () -> consultaService.novaConsultaRecobrimentEstat(13L));
		verify(integracioHelper).addAccioError(eq("PET-13"), anyString(), anyString(), anyMap(), any(), anyLong(), anyString(), any(Throwable.class));
	}

	// ---------- novaConsultaRecobrimentMultiple ----------

	private RecobrimentSolicitudDto crearSolicitudMultiple(String entitatCif,
	                                                       String procedimentCodi,
	                                                       String titular) {
		RecobrimentSolicitudDto solicitud = crearSolicitud(entitatCif, procedimentCodi);
		solicitud.setTitularNom(titular);
		return solicitud;
	}

	@Test
	public void novaConsultaRecobrimentMultiple_entitatNoTrobada_llancaException() {
		RecobrimentSolicitudDto solicitud = crearSolicitudMultiple("INEXISTENT", "PROC1", "Maria");
		when(entitatRepository.findByCif("INEXISTENT")).thenReturn(null);

		assertThrows(EntitatNotFoundException.class, () -> consultaService.novaConsultaRecobrimentMultiple("SV001", Collections.singletonList(solicitud)));
	}

	@Test
	public void novaConsultaRecobrimentMultiple_procedimentNoTrobat_llancaException() {
		Entitat entitat = crearEntitat("Q0700001A");
		RecobrimentSolicitudDto solicitud = crearSolicitudMultiple("Q0700001A", "PROCX", "Maria");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROCX")).thenReturn(null);

		assertThrows(ProcedimentNotFoundException.class, () -> consultaService.novaConsultaRecobrimentMultiple("SV001", Collections.singletonList(solicitud)));
	}

	@Test
	public void novaConsultaRecobrimentMultiple_procedimentServeiNoTrobat_llancaException() {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		RecobrimentSolicitudDto solicitud = crearSolicitudMultiple("Q0700001A", "PROC1", "Maria");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(null);

		assertThrows(ProcedimentServeiNotFoundException.class, () -> consultaService.novaConsultaRecobrimentMultiple("SV001", Collections.singletonList(solicitud)));
	}

	@Test
	public void novaConsultaRecobrimentMultiple_usuariSenseAcces_llancaException() {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		RecobrimentSolicitudDto solicitud = crearSolicitudMultiple("Q0700001A", "PROC1", "Maria");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(ps);
		when(serveiHelper.isServeiPermesPerUsuari(entitat, procediment, "SV001")).thenReturn(false);

		assertThrows(ServeiNotAllowedException.class, () -> consultaService.novaConsultaRecobrimentMultiple("SV001", Collections.singletonList(solicitud)));
	}

	@Test
	public void novaConsultaRecobrimentMultiple_ok_senseError_desaConsultesIndividuals() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		RecobrimentSolicitudDto s1 = crearSolicitudMultiple("Q0700001A", "PROC1", "Maria");
		RecobrimentSolicitudDto s2 = crearSolicitudMultiple("Q0700001A", "PROC1", "Pere");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(ps);
		when(serveiHelper.isServeiPermesPerUsuari(entitat, procediment, "SV001")).thenReturn(true);
		when(scspHelper.generarIdPeticion("SV001")).thenReturn("PET-M1");
		when(peticioScspHelper.convertirEnSolicitud(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(new Solicitud());
		ResultatEnviamentPeticio resultat = mock(ResultatEnviamentPeticio.class);
		when(resultat.isError()).thenReturn(false);
		when(resultat.getIdsSolicituds()).thenReturn(new String[]{"SOL-A", "SOL-B"});
		when(resultat.getEstatCodi()).thenReturn("0003");
		when(resultat.getEstatDescripcio()).thenReturn("Tramitada");
		when(resultat.getConfirmacionPeticion()).thenReturn(null);
		when(peticioScspHelper.enviarPeticioScsp(any(Consulta.class), anyList(), eq(false), eq(true), eq(scspHelper))).thenReturn(resultat);
		when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ConsultaDto resposta = consultaService.novaConsultaRecobrimentMultiple("SV001", Arrays.asList(s1, s2));

		assertNotNull(resposta);
		assertNull(resposta.getRespostaEstadoCodigo());
		verify(consultaRepository, times(3)).save(any(Consulta.class));
		verify(consultaHelper, times(3)).propagaCreacioConsulta(any(Consulta.class));
	}

	@Test
	public void novaConsultaRecobrimentMultiple_ok_ambConfirmacioPeticio_omplaRespostaAtributs() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		RecobrimentSolicitudDto s1 = crearSolicitudMultiple("Q0700001A", "PROC1", "Maria");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(ps);
		when(serveiHelper.isServeiPermesPerUsuari(entitat, procediment, "SV001")).thenReturn(true);
		when(scspHelper.generarIdPeticion("SV001")).thenReturn("PET-M2");
		when(peticioScspHelper.convertirEnSolicitud(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(new Solicitud());
		ConfirmacionPeticion confirmacio = new ConfirmacionPeticion();
		ResultatEnviamentPeticio resultat = mock(ResultatEnviamentPeticio.class);
		when(resultat.isError()).thenReturn(false);
		when(resultat.getIdsSolicituds()).thenReturn(new String[]{"SOL-C"});
		when(resultat.getEstatCodi()).thenReturn("0003");
		when(resultat.getEstatDescripcio()).thenReturn("Tramitada");
		when(resultat.getConfirmacionPeticion()).thenReturn(confirmacio);
		when(peticioScspHelper.enviarPeticioScsp(any(Consulta.class), anyList(), eq(false), eq(true), eq(scspHelper))).thenReturn(resultat);
		when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ConsultaDto resposta = consultaService.novaConsultaRecobrimentMultiple("SV001", Collections.singletonList(s1));

		assertNotNull(resposta);
		assertNotNull(resposta.getRespostaAtributs());
	}

	@Test
	public void novaConsultaRecobrimentMultiple_ambError_ompleCampsErrorEnResposta() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		RecobrimentSolicitudDto s1 = crearSolicitudMultiple("Q0700001A", "PROC1", "Maria");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(ps);
		when(serveiHelper.isServeiPermesPerUsuari(entitat, procediment, "SV001")).thenReturn(true);
		when(scspHelper.generarIdPeticion("SV001")).thenReturn("PET-M3");
		when(peticioScspHelper.convertirEnSolicitud(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(new Solicitud());
		ResultatEnviamentPeticio resultat = mock(ResultatEnviamentPeticio.class);
		when(resultat.isError()).thenReturn(true);
		when(resultat.getErrorCodi()).thenReturn("0242");
		when(resultat.getErrorDescripcio()).thenReturn("Error de generació");
		when(resultat.getEstatCodi()).thenReturn("0242");
		when(resultat.getEstatDescripcio()).thenReturn("Error de generació");
		when(resultat.getIdsSolicituds()).thenReturn(new String[]{"SOL-D"});
		when(peticioScspHelper.enviarPeticioScsp(any(Consulta.class), anyList(), eq(false), eq(true), eq(scspHelper))).thenReturn(resultat);
		when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ConsultaDto resposta = consultaService.novaConsultaRecobrimentMultiple("SV001", Collections.singletonList(s1));

		assertEquals("0242", resposta.getRespostaEstadoCodigo());
		assertEquals("Error de generació", resposta.getRespostaEstadoError());
		verify(integracioHelper).addAccioError(eq("PET-M3"), anyString(), anyString(), anyMap(), any(), anyLong(), anyString(), isNull());
	}

	@Test
	public void novaConsultaRecobrimentMultiple_generacioIdPeticioFalla_propagaExcepcio() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		RecobrimentSolicitudDto s1 = crearSolicitudMultiple("Q0700001A", "PROC1", "Maria");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(ps);
		when(serveiHelper.isServeiPermesPerUsuari(entitat, procediment, "SV001")).thenReturn(true);
		when(scspHelper.generarIdPeticion("SV001")).thenThrow(new ConsultaScspGeneracioException("error generant idpeticio"));

		assertThrows(ConsultaScspGeneracioException.class, () -> consultaService.novaConsultaRecobrimentMultiple("SV001", Collections.singletonList(s1)));
	}

	@Test
	public void novaConsultaRecobrimentMultiple_enviamentComunicacioException_generaRespostaAmbError() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		RecobrimentSolicitudDto s1 = crearSolicitudMultiple("Q0700001A", "PROC1", "Maria");
		when(entitatRepository.findByCif("Q0700001A")).thenReturn(entitat);
		when(procedimentRepository.findByEntitatAndCodi(entitat, "PROC1")).thenReturn(procediment);
		when(procedimentServeiRepository.findByProcedimentIdAndServei(procediment.getId(), "SV001")).thenReturn(ps);
		when(serveiHelper.isServeiPermesPerUsuari(entitat, procediment, "SV001")).thenReturn(true);
		when(scspHelper.generarIdPeticion("SV001")).thenReturn("PET-M4");
		when(peticioScspHelper.convertirEnSolicitud(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(new Solicitud());
		when(peticioScspHelper.enviarPeticioScsp(any(Consulta.class), anyList(), eq(false), eq(true), eq(scspHelper))).thenThrow(new ConsultaScspComunicacioException("PET-M4", "error comunicacio multiple"));
		when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ConsultaDto resposta = consultaService.novaConsultaRecobrimentMultiple("SV001", Collections.singletonList(s1));

		assertEquals("ERROR", resposta.getRespostaEstadoCodigo());
		assertEquals("error comunicacio multiple", resposta.getRespostaEstadoError());
	}

	// ---------- obtenirArxiuInfo ----------

	@Test
	public void obtenirArxiuInfo_consultaNoTrobada_retornaDtoBuit() {
		when(consultaRepository.findById(200L)).thenReturn(Optional.empty());

		ArxiuDetallDto resultat = consultaService.obtenirArxiuInfo(200L);

		assertNotNull(resultat);
		assertNull(resultat.getIdentificador());
	}

	@Test
	public void obtenirArxiuInfo_ok_senseMock_retornaDetallEmplenat() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		Consulta consulta = crearConsultaRecobriment(ps, "PET-ARX-1");
		when(consultaRepository.findById(201L)).thenReturn(Optional.of(consulta));
		Document document = new Document();
		document.setIdentificador("ARX-1");
		document.setNom("document.pdf");
		when(pluginHelper.arxiuDocumentConsultar(eq("PET-ARX-1"), any(), isNull(), eq(false), eq(false))).thenReturn(document);

		ArxiuDetallDto resultat = consultaService.obtenirArxiuInfo(201L);

		assertNotNull(resultat);
		assertEquals("ARX-1", resultat.getIdentificador());
		assertEquals("document.pdf", resultat.getNom());
	}

	@Test
	public void obtenirArxiuInfo_ambMockActivat_usaDocumentMock() throws Exception {
		System.setProperty("es.caib.pinbal.arxiu.document.consultar.mock", "true");
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		Consulta consulta = crearConsultaRecobriment(ps, "PET-ARX-2");
		when(consultaRepository.findById(202L)).thenReturn(Optional.of(consulta));
		Document document = new Document();
		document.setIdentificador("ARX-MOCK");
		when(pluginHelper.arxiuDocumentConsultarMock()).thenReturn(document);

		ArxiuDetallDto resultat = consultaService.obtenirArxiuInfo(202L);

		assertNotNull(resultat);
		assertEquals("ARX-MOCK", resultat.getIdentificador());
		verify(pluginHelper, never()).arxiuDocumentConsultar(any(), any(), any(), anyBoolean(), anyBoolean());
	}

	@Test
	public void obtenirArxiuInfo_pluginHelperLlancaExcepcio_retornaDtoBuit() throws Exception {
		Entitat entitat = crearEntitat("Q0700001A");
		Procediment procediment = crearProcediment(entitat, "PROC1");
		ProcedimentServei ps = crearProcedimentServeiActiu(procediment, "SV001");
		Consulta consulta = crearConsultaRecobriment(ps, "PET-ARX-3");
		when(consultaRepository.findById(203L)).thenReturn(Optional.of(consulta));
		when(pluginHelper.arxiuDocumentConsultar(any(), any(), any(), anyBoolean(), anyBoolean())).thenThrow(new SistemaExternException("error de connexió amb l'arxiu"));

		ArxiuDetallDto resultat = consultaService.obtenirArxiuInfo(203L);

		assertNotNull(resultat);
		assertNull(resultat.getIdentificador());
	}
}
