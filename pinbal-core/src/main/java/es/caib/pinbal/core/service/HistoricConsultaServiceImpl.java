/**
 * 
 */
package es.caib.pinbal.core.service;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import es.caib.pinbal.client.dadesobertes.DadesObertesResposta;
import es.caib.pinbal.core.dto.ConsultaOpenDataDto;
import es.caib.pinbal.scsp.PropertiesHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;

import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.pinbal.core.dto.CarregaDto;
import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.core.dto.ConsultaFiltreDto;
import es.caib.pinbal.core.dto.EmisorDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EstadisticaDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto.EstadistiquesAgrupacioDto;
import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.InformeGeneralEstatDto;
import es.caib.pinbal.core.dto.InformeProcedimentServeiDto;
import es.caib.pinbal.core.dto.InformeRepresentantFiltreDto;
import es.caib.pinbal.core.dto.JustificantDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.helper.ConfigHelper;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.helper.JustificantHelper;
import es.caib.pinbal.core.helper.PermisosHelper;
import es.caib.pinbal.core.helper.PeticioScspEstadistiquesHelper;
import es.caib.pinbal.core.model.Consulta.EstatTipus;
import es.caib.pinbal.core.model.Consulta.JustificantEstat;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatUsuari;
import es.caib.pinbal.core.model.HistoricConsulta;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ProcedimentServei;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.EntitatUsuariRepository;
import es.caib.pinbal.core.repository.HistoricConsultaRepository;
import es.caib.pinbal.core.repository.ProcedimentRepository;
import es.caib.pinbal.core.repository.ProcedimentServeiRepository;
import es.caib.pinbal.core.repository.UsuariRepository;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.JustificantGeneracioException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;
import es.caib.pinbal.scsp.Resposta;
import es.caib.pinbal.scsp.ScspHelper;
import es.scsp.common.domain.core.EmisorCertificado;
import es.scsp.common.domain.core.Servicio;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Implementació dels mètodes per a gestionar les consultes al SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class HistoricConsultaServiceImpl implements HistoricConsultaService, ApplicationContextAware, MessageSourceAware {
	
	private static final String ROLE_ADMIN = "ROLE_ADMIN";
	private static final String ROLE_REPRES = "ROLE_REPRES";

	@Autowired
	private HistoricConsultaRepository historicConsultaRepository;
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private ProcedimentServeiRepository procedimentServeiRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private EntitatUsuariRepository entitatUsuariRepository;

	@Autowired
	private JustificantHelper justificantHelper;
	@Autowired
	private DtoMappingHelper dtoMappingHelper;
	@Autowired
	private PeticioScspEstadistiquesHelper peticioScspEstadistiquesHelper;
	@Autowired
	private ConfigHelper configHelper;

	@Autowired
	private MutableAclService aclService;
	
	@Autowired
	private ProcedimentService procedimentService;
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	private ApplicationContext applicationContext;
	private MessageSource messageSource;
	private ScspHelper scspHelper;

	private Map<Long, Object> justificantLocks = new HashMap<Long, Object>();


	@Override
	public JustificantDto obtenirJustificant(
			Long id,
			boolean isAdmin) throws ConsultaNotFoundException, JustificantGeneracioException {
		log.debug("Generant justificant per a la consulta (id=" + id + ")");
		HistoricConsulta consulta = historicConsultaRepository.findOne(id);
		if (consulta == null) {
			log.error("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		if (!isAdmin) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
				log.error("La consulta (id=" + id + ") no pertany a aquest usuari");
				throw new ConsultaNotFoundException();
			}
		}
		return obtenirJustificantComu(consulta, true);
	}

	@Override
	public JustificantDto obtenirJustificant(String idpeticion, String idsolicitud)
			throws ConsultaNotFoundException, JustificantGeneracioException {
		log.debug("Generant justificant per a la consulta (" +
				"idpeticion=" + idpeticion + ", " +
				"idsolicitud=" + idsolicitud + ")");
		HistoricConsulta consulta = historicConsultaRepository.findByScspPeticionIdAndScspSolicitudId(
				idpeticion,
				idsolicitud);
		if (consulta == null) {
			log.error("No s'ha trobat la consulta (idpeticion=" + idpeticion + ", idsolicitud=" + idsolicitud + ")");
			throw new ConsultaNotFoundException();
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
			log.error("La consulta (idpeticion=" + idpeticion + ", idsolicitud=" + idsolicitud + ") no pertany a aquest usuari");
			throw new ConsultaNotFoundException();
		}
		return obtenirJustificantComu(consulta, true);
	}

//	@Override
//	public JustificantDto reintentarGeneracioJustificant(
//			Long id,
//			boolean descarregar) throws ConsultaNotFoundException, JustificantGeneracioException {
//		log.debug("Reintentant generació del justificant per a la consulta (id=" + id + ")");
//		HistoricConsulta consulta = historicConsultaRepository.findOne(id);
//		if (consulta == null) {
//			log.error("No s'ha trobat la consulta (id=" + id + ")");
//			throw new ConsultaNotFoundException();
//		}
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
//			log.error("La consulta (id=" + id + ") no pertany a aquest usuari");
//			throw new ConsultaNotFoundException();
//		}
//		return obtenirJustificantComu(consulta, descarregar);
//	}

	@Transactional(rollbackFor = {ConsultaNotFoundException.class, JustificantGeneracioException.class})
	@Override
	public FitxerDto obtenirJustificantMultipleConcatenat(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Generant justificant concatenat per a la consulta múltiple (id=" + id + ")");
		copiarPropertiesToDb();
		HistoricConsulta consulta = historicConsultaRepository.findOne(id);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
			log.debug("La consulta (id=" + id + ") no pertany a aquest usuari");
			throw new ConsultaNotFoundException();
		}
		if (!consulta.isMultiple()) {
			log.debug("La consulta (id=" + id + ") no és múltiple");
			throw new ConsultaNotFoundException();
		}
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Document pdfConcatenat = new Document();
			PdfCopy copy = new PdfCopy(pdfConcatenat, baos);
			pdfConcatenat.open();
			for (HistoricConsulta solicitud: consulta.getFills()) {
				FitxerDto fitxerJustificantGenerat = justificantHelper.generar(
						solicitud,
						getScspHelper());
				PdfReader pdfReader = new PdfReader(fitxerJustificantGenerat.getContingut());
				for (int pagina = 1; pagina <= pdfReader.getNumberOfPages(); pagina++) {
					copy.addPage(
							copy.getImportedPage(pdfReader, pagina));
				}
			}
			pdfConcatenat.close();
			FitxerDto fitxer = new FitxerDto();
			fitxer.setNom("PBL_" + consulta.getScspPeticionId() + ".pdf");
			fitxer.setContingut(baos.toByteArray());
			return fitxer;
		} catch (Exception ex) {
			log.error("Error al generar justificant concatenat per a la consulta múltiple (id=" + id + ")", ex);
			throw new JustificantGeneracioException(ex.getMessage(), ex);
		}
	}

	@Transactional(rollbackFor = {ConsultaNotFoundException.class, JustificantGeneracioException.class})
	@Override
	public FitxerDto obtenirJustificantMultipleZip(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Generant justificant ZIP per a la consulta múltiple (id=" + id + ")");
		copiarPropertiesToDb();
		HistoricConsulta consulta = historicConsultaRepository.findOne(id);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
			log.debug("La consulta (id=" + id + ") no pertany a aquest usuari");
			throw new ConsultaNotFoundException();
		}
		if (!consulta.isMultiple()) {
			log.debug("La consulta (id=" + id + ") no és múltiple");
			throw new ConsultaNotFoundException();
		}
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);
			for (HistoricConsulta solicitud: consulta.getFills()) {
				FitxerDto fitxerJustificantGenerat = justificantHelper.generar(
						solicitud,
						getScspHelper());
				ZipEntry zipEntry = new ZipEntry(fitxerJustificantGenerat.getNom());
				zos.putNextEntry(zipEntry);
				zos.write(fitxerJustificantGenerat.getContingut());
				zos.closeEntry();
			}
			zos.close();
			FitxerDto fitxer = new FitxerDto();
			fitxer.setNom("PBL_" + consulta.getScspPeticionId() + ".zip");
			fitxer.setContingut(baos.toByteArray());
			return fitxer;
		} catch (Exception ex) {
			log.error("Error al generar justificant ZIP per a la consulta múltiple (id=" + id + ")", ex);
			throw new JustificantGeneracioException(ex.getMessage(), ex);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ConsultaDto> findSimplesByFiltrePaginatPerDelegat(
			Long entitatId,
			ConsultaFiltreDto filtre,
			Pageable pageable) throws EntitatNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Cercant les consultes de delegat simples per a l'entitat (id=" + entitatId + ") i l'usuari (codi=" + auth.getName() + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		copiarPropertiesToDb();
		return findByEntitatIUsuariFiltrePaginat(
				entitat,
				auth.getName(),
				filtre,
				pageable,
				false,
				true,
				false,
				false);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ConsultaDto> findMultiplesByFiltrePaginatPerDelegat(
			Long entitatId,
			ConsultaFiltreDto filtre,
			Pageable pageable) throws EntitatNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Cercant les consultes de delegat múltiples per a l'entitat (id=" + entitatId + ") i l'usuari (codi=" + auth.getName() + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		copiarPropertiesToDb();
		return findByEntitatIUsuariFiltrePaginat(
				entitat,
				auth.getName(),
				filtre,
				pageable,
				true,
				true,
				false,
				false);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ConsultaDto> findByFiltrePaginatPerAuditor(
			Long entitatId,
			ConsultaFiltreDto filtre,
			Pageable pageable) throws EntitatNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Cercant les consultes d'auditor per a l'entitat (id=" + entitatId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
				entitat.getId(),
				auth.getName());
		if (entitatUsuari == null || !entitatUsuari.isAuditor()) {
			log.debug("Aquest usuari no té permisos per auditar l'entitat (id=" + entitatId + ", usuariCodi=" + auth.getName() + ")");
			throw new EntitatNotFoundException();
		}
		copiarPropertiesToDb();
		return findByEntitatIUsuariFiltrePaginat(
				entitat,
				null,
				filtre,
				pageable,
				false,
				false,
				false,
				false);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ConsultaDto> findByFiltrePerAuditor(Long entitatId, ConsultaFiltreDto filtre) throws EntitatNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Cercant les consultes d'auditor per a l'entitat (id=" + entitatId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
				entitat.getId(),
				auth.getName());
		if (entitatUsuari == null || !entitatUsuari.isAuditor()) {
			log.debug("Aquest usuari no té permisos per auditar l'entitat (id=" + entitatId + ", usuariCodi=" + auth.getName() + ")");
			throw new EntitatNotFoundException();
		}
		copiarPropertiesToDb();
		Page<ConsultaDto> page =  findByEntitatIUsuariFiltrePaginat(
				entitat,
				null,
				filtre,
				new PageRequest(0, Integer.MAX_VALUE, new Sort(new Sort.Order(Sort.Direction.DESC, "scspPeticionId"))),
				false,
				false,
				false,
				false);
		return page.getContent();
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ConsultaDto> findByFiltrePaginatPerSuperauditor(
			Long entitatId,
			ConsultaFiltreDto filtre,
			Pageable pageable) throws EntitatNotFoundException {
		log.debug("Cercant les consultes de superauditor per a l'entitat (id=" + entitatId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		copiarPropertiesToDb();
		return findByEntitatIUsuariFiltrePaginat(
				entitat,
				null,
				filtre,
				pageable,
				false,
				false,
				false,
				false);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ConsultaDto> findByFiltrePaginatPerAdmin(
			ConsultaFiltreDto filtre,
			Pageable pageable) throws EntitatNotFoundException {
		log.debug("Cercant les consultes de administrador");
		return findByFiltrePaginatAdmin(
				filtre,
				pageable,
				false,
				false);
	}

	@Transactional(readOnly = true)
	@Override
	public List<DadesObertesRespostaConsulta> findByFiltrePerOpenData(
			String entitatCodi,
			Date dataInici,
			Date dataFi,
			String procedimentCodi,
			String serveiCodi) throws EntitatNotFoundException, ProcedimentNotFoundException {
		log.debug("Consultant informació per opendata (" +
				"entitatCodi=" + entitatCodi + ", " +
				"dataInici=" + dataInici + ", " +
				"dataFi" + dataFi + ", " +
				"procedimentCodi=" + procedimentCodi + ", " +
				"serveiCodi=" + serveiCodi + ")");
		Entitat entitat = null;
		Procediment procediment = null;
		if (entitatCodi != null) {
			entitat = getEntitat(entitatCodi);
			if (procedimentCodi != null) {
				procediment = getProcediment(procedimentCodi, entitat);
			}
		}
		List<DadesObertesRespostaConsulta> resposta = historicConsultaRepository.findByOpendata(
				entitat == null,
				entitat != null ? entitat.getId() : null,
				procediment == null,
				procediment != null ? procediment.getId() : null,
				serveiCodi == null,
				serveiCodi,
				dataInici == null,
				dataInici,
				dataFi == null,
				dataFi);
		return resposta;
	}

	@Override
	public DadesObertesResposta findByFiltrePerOpenDataV2(ConsultaOpenDataDto consultaOpenDataDto) throws EntitatNotFoundException, ProcedimentNotFoundException {
		log.debug("Consultant informació per opendata (" + consultaOpenDataDto + ")");
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Entitat entitat = null;
		Procediment procediment = null;
		if (consultaOpenDataDto.getEntitatCodi() != null) {
			entitat = getEntitat(consultaOpenDataDto.getEntitatCodi());
			if (consultaOpenDataDto.getProcedimentCodi() != null) {
				procediment = getProcediment(consultaOpenDataDto.getProcedimentCodi(), entitat);
			}
		}

		Integer numElements = historicConsultaRepository.countByOpendata(
				entitat == null,
				entitat != null ? entitat.getId() : null,
				procediment == null,
				procediment != null ? procediment.getId() : null,
				isBlank(consultaOpenDataDto.getServeiCodi()),
				!isBlank(consultaOpenDataDto.getServeiCodi()) ? consultaOpenDataDto.getServeiCodi() : null,
				consultaOpenDataDto.getDataInici() == null,
				consultaOpenDataDto.getDataInici(),
				consultaOpenDataDto.getDataFi() == null,
				consultaOpenDataDto.getDataFi());
		Page<DadesObertesRespostaConsulta> dades = historicConsultaRepository.findByOpendata(
				entitat == null,
				entitat != null ? entitat.getId() : null,
				procediment == null,
				procediment != null ? procediment.getId() : null,
				consultaOpenDataDto.getServeiCodi() == null,
				consultaOpenDataDto.getServeiCodi(),
				consultaOpenDataDto.getDataInici() == null,
				consultaOpenDataDto.getDataInici(),
				consultaOpenDataDto.getDataFi() == null,
				consultaOpenDataDto.getDataFi(),
				new PageRequest(consultaOpenDataDto.getPagina(), consultaOpenDataDto.getMida()));

		Integer totalPagines = (numElements.intValue() + consultaOpenDataDto.getMida() - 1)/consultaOpenDataDto.getMida();
		String nextUrl = null;
		if (totalPagines.intValue() > consultaOpenDataDto.getPagina().intValue() + 1) {
			nextUrl = consultaOpenDataDto.getAppPath() + "?historic=true";
			nextUrl += "&dataInici=" + sdf.format(consultaOpenDataDto.getDataInici());
			nextUrl += "&dataFi=" + sdf.format(consultaOpenDataDto.getDataFi());
			nextUrl += !isBlank(consultaOpenDataDto.getEntitatCodi()) ? "&entitatCodi=" + consultaOpenDataDto.getEntitatCodi() : "";
			nextUrl += !isBlank(consultaOpenDataDto.getProcedimentCodi()) ? "&procedimentCodi=" + consultaOpenDataDto.getProcedimentCodi() : "";
			nextUrl += !isBlank(consultaOpenDataDto.getServeiCodi()) ? "&serveiCodi=" + consultaOpenDataDto.getServeiCodi() : "";
			nextUrl += "&pagina=" + (consultaOpenDataDto.getPagina() + 1);
			nextUrl += "&mida=" + consultaOpenDataDto.getMida();
		}

		return DadesObertesResposta.builder()
				.totalElements(numElements)
				.paginaActual(consultaOpenDataDto.getPagina() + 1)
				.totalPagines(totalPagines)
				.properaPagina(nextUrl)
				.dades(dades.getContent())
				.build();
	}

	private Procediment getProcediment(String procedimentCodi, Entitat entitat) throws ProcedimentNotFoundException {
		Procediment procediment = procedimentRepository.findByEntitatAndCodi(entitat, procedimentCodi);
		if (procediment == null) {
			log.debug("No s'ha trobat el procediment (codi=" + procedimentCodi + ")");
			throw new ProcedimentNotFoundException();
		}
		return procediment;
	}

	private Entitat getEntitat(String entitatCodi) throws EntitatNotFoundException {
		Entitat entitat = entitatRepository.findByCodi(entitatCodi);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (codi=" + entitatCodi + ")");
			throw new EntitatNotFoundException();
		}
		return entitat;
	}

	@Transactional(readOnly = true)
	@Override
	public ConsultaDto findOneDelegat(Long id) throws ConsultaNotFoundException, ScspException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Cercant les dades de la consulta (id=" + id + ")");
		HistoricConsulta consulta = historicConsultaRepository.findOne(id);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
			log.debug("La consulta (id=" + id + ") no pertany a aquest usuari");
			throw new ConsultaNotFoundException();
		}
		return toConsultaDto(
				null,
				consulta);
	}

	@Transactional(readOnly = true)
	@Override
	public ConsultaDto findOneAuditor(Long id) throws ConsultaNotFoundException, ScspException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Cercant les dades de la consulta (id=" + id + ")");
		HistoricConsulta consulta = historicConsultaRepository.findOne(id);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		Entitat entitat = consulta.getProcedimentServei().getProcediment().getEntitat();
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
				entitat.getId(),
				auth.getName());
		if (entitatUsuari == null || !entitatUsuari.isAuditor()) {
			log.debug("Aquest usuari no té permisos per auditar la consulta (id=" + id + ", usuariCodi=" + auth.getName() + ")");
			throw new ConsultaNotFoundException();
		}
		return toConsultaDto(
				null,
				consulta);
	}

	@Transactional(readOnly = true)
	@Override
	public ConsultaDto findOneSuperauditor(Long id) throws ConsultaNotFoundException, ScspException {
		log.debug("Cercant les dades de la consulta (id=" + id + ")");
		HistoricConsulta consulta = historicConsultaRepository.findOne(id);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		return toConsultaDto(
				null,
				consulta);
	}

	@Transactional(readOnly = true)
	@Override
	public ConsultaDto findOneAdmin(Long id) throws ConsultaNotFoundException, ScspException {
		log.debug("Cercant les dades de la consulta (id=" + id + ")");
		HistoricConsulta consulta = historicConsultaRepository.findOne(id);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		return toConsultaDto(
				null,
				consulta);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ConsultaDto> findAmbPare(
			Long pareId) throws ConsultaNotFoundException, ScspException {
		log.debug("Cercant les consultes amb pare (pareId=" + pareId + ")");
		HistoricConsulta pare = historicConsultaRepository.findOne(pareId);
		if (pare == null) {
			log.debug("No s'ha trobat la consulta (id=" + pareId + ")");
			throw new ConsultaNotFoundException();
		}
		List<ConsultaDto> resposta = new ArrayList<ConsultaDto>();
		List<HistoricConsulta> filles = historicConsultaRepository.findByPareOrderByScspSolicitudIdAsc(pare);
		for (HistoricConsulta filla: filles) {
			resposta.add(
					toConsultaDto(
							null,
							filla));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<EstadisticaDto> findEstadistiquesByFiltre(EstadistiquesFiltreDto filtre) throws EntitatNotFoundException {
		log.debug("Consultant estadístiques per a l'entitat (id=" + filtre.getEntitatId() + ")");
		if (filtre.getEntitatId() != null) {
			Entitat entitat = entitatRepository.findOne(filtre.getEntitatId());
			if (entitat == null) {
				log.debug("No s'ha trobat l'entitat (id=" + filtre.getEntitatId() + ")");
				throw new EntitatNotFoundException();
			}
		}
		List<EstadisticaDto> resposta = new ArrayList<EstadisticaDto>();
		List<Object[]> resultats;
		if (EstadistiquesAgrupacioDto.PROCEDIMENT_SERVEI.equals(filtre.getAgrupacio())) {
			resultats = historicConsultaRepository.countByProcedimentServei(
					filtre.getEntitatId() == null,
					filtre.getEntitatId(),
					filtre.getUsuariCodi() == null,
					(filtre.getUsuariCodi() != null) ? usuariRepository.findOne(filtre.getUsuariCodi()) : null,
					filtre.getProcedimentId() == null,
					filtre.getProcedimentId(),
					filtre.getServeiCodi() == null || filtre.getServeiCodi().isEmpty(),
					filtre.getServeiCodi(),
					filtre.getEstat() == null,
					(filtre.getEstat() != null) ? EstatTipus.valueOf(filtre.getEstat().toString()) : null,
					filtre.getDataInici() == null,
					filtre.getDataInici(),
					filtre.getDataFi() == null,
					configurarDataFiPerFiltre(filtre.getDataFi()),
					EstatTipus.Tramitada,
					EstatTipus.Error);
		} else {
			resultats = historicConsultaRepository.countByServeiProcediment(
					filtre.getEntitatId() == null,
					filtre.getEntitatId(),
					filtre.getUsuariCodi() == null,
					(filtre.getUsuariCodi() != null) ? usuariRepository.findOne(filtre.getUsuariCodi()) : null,
					filtre.getProcedimentId() == null,
					filtre.getProcedimentId(),
					filtre.getServeiCodi() == null || filtre.getServeiCodi().isEmpty(),
					filtre.getServeiCodi(),
					filtre.getEstat() == null,
					(filtre.getEstat() != null) ? EstatTipus.valueOf(filtre.getEstat().toString()) : null,
					filtre.getDataInici() == null,
					filtre.getDataInici(),
					filtre.getDataFi() == null,
					configurarDataFiPerFiltre(filtre.getDataFi()),
					EstatTipus.Tramitada,
					EstatTipus.Error);
		}
		for (Object[] resultat: resultats) {
			Long procedimentServeiId = (Long)resultat[0];
			Long numRecobrimentOk = (Long)resultat[1];
			Long numRecobrimentError = (Long)resultat[2];
			Long numWebUIOk = (Long)resultat[4];
			Long numWebUIError = (Long)resultat[5];
			ProcedimentServei procedimentServei = procedimentServeiRepository.findOne(procedimentServeiId);
			EstadisticaDto dto = new EstadisticaDto();
			dto.setProcediment(
					dtoMappingHelper.getMapperFacade().map(
							procedimentServei.getProcediment(),
							ProcedimentDto.class));
			dto.setServeiCodi(procedimentServei.getServei());
			dto.setServeiNom(
					getScspHelper().getServicioDescripcion(
							procedimentServei.getServei()));
			dto.setNumRecobrimentOk(numRecobrimentOk);
			dto.setNumRecobrimentError(numRecobrimentError);
			dto.setNumWebUIOk(numWebUIOk);
			dto.setNumWebUIError(numWebUIError);
			resposta.add(dto);
		}
		EstadisticaDto estadisticaActual = null;
		for (EstadisticaDto estadistica: resposta) {
			boolean coincideixProcediment = estadisticaActual != null && estadisticaActual.getProcediment().getId().equals(estadistica.getProcediment().getId());
			boolean coincideixServei = estadisticaActual != null && estadisticaActual.getServeiCodi().equals(estadistica.getServeiCodi());
			if (	estadisticaActual == null || 
					(EstadistiquesAgrupacioDto.PROCEDIMENT_SERVEI.equals(filtre.getAgrupacio()) && !coincideixProcediment) ||
					(EstadistiquesAgrupacioDto.SERVEI_PROCEDIMENT.equals(filtre.getAgrupacio()) && !coincideixServei)) {
				estadisticaActual = estadistica;
				estadisticaActual.setConteSumatori(true);
			}
			estadisticaActual.setSumatoriNumRegistres(
					estadisticaActual.getSumatoriNumRegistres() + 1);
			estadisticaActual.setSumatoriNumRecobrimentOk(
					estadisticaActual.getSumatoriNumRecobrimentOk() + estadistica.getNumRecobrimentOk());
			estadisticaActual.setSumatoriNumRecobrimentError(
					estadisticaActual.getSumatoriNumRecobrimentError() + estadistica.getNumRecobrimentError());
			estadisticaActual.setSumatoriNumWebUIOk(
					estadisticaActual.getSumatoriNumWebUIOk() + estadistica.getNumWebUIOk());
			estadisticaActual.setSumatoriNumWebUIError(
					estadisticaActual.getSumatoriNumWebUIError() + estadistica.getNumWebUIError());
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public Map<EntitatDto, List<EstadisticaDto>> findEstadistiquesGlobalsByFiltre(
			EstadistiquesFiltreDto filtre) {
		log.debug("Consultant estadístiques globals");
		Map<EntitatDto, List<EstadisticaDto>> resposta = new HashMap<EntitatDto, List<EstadisticaDto>>();
		List<Object[]> resultats = historicConsultaRepository.countByEntitat(
				filtre.getUsuariCodi() == null,
				(filtre.getUsuariCodi() != null) ? usuariRepository.findOne(filtre.getUsuariCodi()) : null,
				filtre.getProcedimentId() == null,
				filtre.getProcedimentId(),
				filtre.getServeiCodi() == null || filtre.getServeiCodi().isEmpty(),
				filtre.getServeiCodi(),
				filtre.getEstat() == null,
				(filtre.getEstat() != null) ? EstatTipus.valueOf(filtre.getEstat().toString()) : null,
				filtre.getDataInici() == null,
				filtre.getDataInici(),
				filtre.getDataFi() == null,
				configurarDataFiPerFiltre(filtre.getDataFi()));
		// Omple les estadístiques per cada entitat
		for (Object[] resultat: resultats) {
			Long entitatId = (Long)resultat[0];
			Long numConsultes = (Long)resultat[1];
			if (numConsultes > 0) {
				filtre.setEntitatId(entitatId);
				try {
					resposta.put(
							dtoMappingHelper.getMapperFacade().map(
									entitatRepository.findOne(entitatId),
									EntitatDto.class),
							findEstadistiquesByFiltre(filtre));
				} catch (EntitatNotFoundException ex) {
					// És impossible però ho traurem pel log
					log.error("No s'ha trobat l'entitat (entitatId=" + entitatId + ")", ex);
				}
			}
		}
		// Omple l'estadística global
		filtre.setEntitatId(null);
		try {
			resposta.put(
					null,
					findEstadistiquesByFiltre(filtre));
		} catch (EntitatNotFoundException ex) {
			// És impossible però ho traurem pel log
			log.error("No s'ha trobat l'entitat (entitatId=null)", ex);
		}
		return resposta;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<CarregaDto> findEstadistiquesCarrega() {
		log.debug("Consultant estadístiques de càrrega");
		return peticioScspEstadistiquesHelper.consultaEstadistiques();
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> auditoriaGenerarAuditor(
			Long entitatId,
			Date dataInici,
			Date dataFi,
			int numConsultes) throws EntitatNotFoundException {
		log.debug("Generant auditoria per auditor (" +
				"entitatId=" + entitatId + "," +
				"dataInici=" + dataInici + "," +
				"dataFi=" + dataFi + "," +
				"numConsultes=" + numConsultes + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		List<HistoricConsulta> consultes = historicConsultaRepository.findByEntitatAndDataIniciAndDataFi(
				entitat,
				dataInici,
				dataFi);
		List<Long> resposta = new ArrayList<Long>();
		if (consultes.size() > numConsultes) {
			int[] indexos = getRandomIndexesFromList(
					consultes,
					numConsultes);
			for (int index: indexos) {
				resposta.add(consultes.get(index).getId());
			}
		} else {
			for (HistoricConsulta consulta: consultes)
				resposta.add(consulta.getId());
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ConsultaDto> auditoriaConsultarAuditor(
			Long entitatId,
			List<Long> consultaIds) throws EntitatNotFoundException, ScspException {
		log.debug("Consultant auditoria per auditor (" +
				"entitatId=" + entitatId + "," +
				"consultaIds=" + consultaIds + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		List<HistoricConsulta> consultes = historicConsultaRepository.findByEntitatAndIds(
				entitat,
				consultaIds);
		List<ConsultaDto> resposta = new ArrayList<ConsultaDto>();
		for (HistoricConsulta consulta: consultes)
			resposta.add(toConsultaDto(entitatId, consulta));
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> auditoriaGenerarSuperauditor(
			Date dataInici,
			Date dataFi,
			int numEntitats,
			int numConsultes) {
		log.debug("Generant auditoria per superauditor (" +
				"dataInici=" + dataInici + "," +
				"dataFi=" + dataFi + "," +
				"numConsultes=" + numConsultes + "," +
				"numEntitats=" + numEntitats + ")");
		List<Entitat> entitats = entitatRepository.findAll();
		// Filtra les entitats sense cap consulta
		List<Object[]> entitatCounts = historicConsultaRepository.countByEntitat(
				true,
				null,
				true,
				null,
				true,
				null,
				true,
				null,
				false,
				dataInici,
				false,
				dataFi);
		Iterator<Entitat> itEntitats = entitats.iterator();
		while (itEntitats.hasNext()) {
			Entitat entitat = itEntitats.next();
			boolean trobada = false;
			for (Object[] count: entitatCounts) {
				Long entitatId = (Long)count[0];
				if (entitat.getId().equals(entitatId)) {
					trobada = true;
					break;
				}
			}
			if (!trobada)
				itEntitats.remove();
		}
		// Selecciona numEntitats d'entre les disponibles
		List<Long> seleccioEntitats = new ArrayList<Long>();
		if (entitats.size() > numEntitats) {
			int[] indexos = getRandomIndexesFromList(
					entitats,
					numEntitats);
			for (int index: indexos) {
				seleccioEntitats.add(entitats.get(index).getId());
			}
			itEntitats = entitats.iterator();
			while (itEntitats.hasNext()) {
				Entitat entitat = itEntitats.next();
				if (!seleccioEntitats.contains(entitat.getId()))
					itEntitats.remove();
			}
		}
		// Genera l'auditoria per a cada entitat
		List<Long> resposta = new ArrayList<Long>();
		for (Entitat entitat: entitats) {
			try {
				List<Long> auditoria = auditoriaGenerarAuditor(
						entitat.getId(),
						dataInici,
						dataFi,
						numConsultes);
				resposta.addAll(auditoria);
			} catch (EntitatNotFoundException e) {
				log.error("No s'ha trobat l'entitat (id=" + entitat.getId() + ")");
			}
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public Map<EntitatDto, List<ConsultaDto>> auditoriaConsultarSuperauditor(
			List<Long> consultaIds) throws ScspException {
		log.debug("Consultant auditoria per superauditor (" +
				"consultaIds=" + consultaIds + ")");
		List<Object[]> consultes = historicConsultaRepository.findByIds(
				consultaIds);
		Map<EntitatDto, List<ConsultaDto>> resposta = new HashMap<EntitatDto, List<ConsultaDto>>();
		List<ConsultaDto> consultesEntitat = null;
		EntitatDto entitatActual = null;
		for (Object[] consulta: consultes) {
			HistoricConsulta c = (HistoricConsulta)consulta[0];
			Long entitatId = (Long)consulta[1];
			if (entitatActual == null || !entitatActual.getId().equals(entitatId)) {
				entitatActual = dtoMappingHelper.getMapperFacade().map(
						entitatRepository.findOne(entitatId),
						EntitatDto.class);
				consultesEntitat = new ArrayList<ConsultaDto>();
				resposta.put(entitatActual, consultesEntitat);
			}
			consultesEntitat.add(toConsultaDto(entitatId, c));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<InformeGeneralEstatDto> informeGeneralEstat(Date dataInici, Date dataFi) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataInici);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		dataInici = cal.getTime();
		cal.setTime(dataFi);
		cal.set(Calendar.HOUR_OF_DAY,23);
		cal.set(Calendar.MINUTE,59);
		cal.set(Calendar.SECOND,59);
		cal.set(Calendar.MILLISECOND,999);
		dataFi = cal.getTime();
		log.debug("Obtenint informe general d'estats");
		List<InformeGeneralEstatDto> resposta = new ArrayList<InformeGeneralEstatDto>();
		List<ProcedimentServei> serveis = procedimentServeiRepository.findAll(
				new Sort(Sort.Direction.ASC, "procediment.entitat.nom", "procediment.codi", "servei"));
		List<Object[]> consultes = historicConsultaRepository.countGroupByProcedimentServeiEstat(dataInici, dataFi);
		for (ProcedimentServei servei : serveis) {
			resposta.add(toInformeGeneralEstatDto(servei, consultes));
		}
		return resposta;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<InformeProcedimentServeiDto> informeUsuarisEntitatOrganProcedimentServei(Long entitatId, String rolActual, InformeRepresentantFiltreDto filtre) {
		log.debug("Obtenint informe usuaris agrupats per entitat, òrgan gestor, procediment i servei");
		List<InformeProcedimentServeiDto> resposta = new ArrayList<InformeProcedimentServeiDto>();
	
		List<ProcedimentServei> procedimentServeis = new ArrayList<ProcedimentServei>();
		if (ROLE_ADMIN.equals(rolActual)) {
			procedimentServeis = procedimentServeiRepository.findAllActius();
		} else if (ROLE_REPRES.equals(rolActual)) {
			procedimentServeis = procedimentServeiRepository.findAllActiusAmbFiltre(
					entitatId,
					filtre.getOrganGestorId() == null,
					filtre.getOrganGestorId(),
					filtre.getProcedimentId() == null,
					filtre.getProcedimentId(),
					filtre.getServeiCodi() == null,
					filtre.getServeiCodi());
		}
		for (ProcedimentServei procedimentServei : procedimentServeis) {
			List<String> usuarisAmbPermis = new ArrayList<String>();
			try {
				usuarisAmbPermis = procedimentService.findUsuarisAmbPermisPerServei(procedimentServei.getProcediment().getId(), procedimentServei.getServei());
			} catch (ProcedimentNotFoundException e) {
				log.error("No s'ha trobat el Procediment (id=" + procedimentServei.getProcediment().getId() + ")");
			} catch (ProcedimentServeiNotFoundException e) {
				log.error("No s'ha trobat el Servei (codi=" + procedimentServei.getServei() + ")");
			}
			
			for (String usuariAmbPermis: usuarisAmbPermis) {
				EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(procedimentServei.getProcediment().getEntitat().getId(), usuariAmbPermis);
				if (entitatUsuari != null) {
					resposta.add(toInformeProcedimentServeiDto(procedimentServei, entitatUsuari));	
				}
			}
		}
		
		return resposta;
	}
	
	private InformeProcedimentServeiDto toInformeProcedimentServeiDto(ProcedimentServei procedimentServei,
			EntitatUsuari entitatUsuari) {
		InformeProcedimentServeiDto informeProcedimentServei = new InformeProcedimentServeiDto();
		Servicio servicio = getScspHelper().getServicio(procedimentServei.getServei());
		informeProcedimentServei.setEntitatCodi(procedimentServei.getProcediment().getEntitat().getCodi());
		informeProcedimentServei.setEntitatNom(procedimentServei.getProcediment().getEntitat().getNom());
		informeProcedimentServei.setEntitatCif(procedimentServei.getProcediment().getEntitat().getCif());
		informeProcedimentServei.setOrganGestorCodi(procedimentServei.getProcediment().getOrganGestor() != null ? procedimentServei.getProcediment().getOrganGestor().getCodi() : null);
		informeProcedimentServei.setOrganGestorNom(procedimentServei.getProcediment().getOrganGestor() != null ? procedimentServei.getProcediment().getOrganGestor().getNom() : null);
		informeProcedimentServei.setProcedimentCodi(procedimentServei.getProcediment().getCodi());
		informeProcedimentServei.setProcedimentNom(procedimentServei.getProcediment().getNom());
		informeProcedimentServei.setServeiCodi(servicio != null ? servicio.getCodCertificado() : null);
		informeProcedimentServei.setServeiNom(servicio != null ? servicio.getDescripcion() : null);
		informeProcedimentServei.setUsuariCodi(entitatUsuari.getUsuari().getCodi());
		informeProcedimentServei.setUsuariNif(entitatUsuari.getUsuari().getNif());
		informeProcedimentServei.setUsuariNom(entitatUsuari.getUsuari().getNom());
		return informeProcedimentServei;
	}

	@Override
	public void setApplicationContext(
			ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Transactional
	@Override
	public void arxivarConsultesAntigues() {
		log.info("[ARXIU CONSULTES] Inici");
		String dialect = configHelper.getConfig("es.caib.pinbal.hibernate.dialect", "Oracle");
		int dies = configHelper.getAsInt("es.caib.pinbal.tasca.auto.arxivar.antiguetat.dies", 180);
		log.info("[ARXIU CONSULTES] Arxivar en {} les consultes amb una antiguitat superior a {} dies", dialect, dies);
		int consultesArxivades = 0;
		int consultesEliminades = 0;

		if (dialect != null && dialect.toLowerCase().contains("postgres")) {
			log.info("[ARXIU CONSULTES] Inici d'arxivat de les consultes dels últims {}dies - PostgreSql", dies);
			consultesArxivades = historicConsultaRepository.arxivaConsultesPostgres(dies);
		} else {
			log.info("[ARXIU CONSULTES] Inici d'arxivat de les consultes dels últims {}dies - Oracle", dies);
			consultesArxivades = historicConsultaRepository.arxivaConsultesOracle(dies);
		}
		log.info("[ARXIU CONSULTES] {} Consultes arxivades a la taula d'històric", consultesArxivades);
		if (consultesArxivades > 0) {
			consultesEliminades = historicConsultaRepository.purgaConsultes(dies);
			log.info("[ARXIU CONSULTES] {} Consultes eliminades de la taula de consultes", consultesEliminades);
		}

		if (consultesArxivades != consultesEliminades) {
			throw new RuntimeException("Error inesperat al arxivar les consultes antigues");
		}
		log.info("[ARXIU CONSULTES] Fi");
	}


	private InformeGeneralEstatDto toInformeGeneralEstatDto(ProcedimentServei servei, List<Object[]> consultes) {
		InformeGeneralEstatDto dto = new InformeGeneralEstatDto();
		Servicio servicio = getScspHelper().getServicio(servei.getServei());
		dto.setEntitatCodi(servei.getProcediment().getEntitat().getCodi());
		dto.setEntitatNom(servei.getProcediment().getEntitat().getNom());
		dto.setEntitatCif(servei.getProcediment().getEntitat().getCif());
		dto.setDepartament(servei.getProcediment().getDepartament());
		dto.setProcedimentCodi(servei.getProcediment().getCodi());
		dto.setProcedimentNom(servei.getProcediment().getNom());
		dto.setServeiCodi(servicio.getCodCertificado());
		dto.setServeiNom(servicio.getDescripcion());
		if (servicio.getEmisor() != null) {
			EmisorDto emisor = new EmisorDto();
			EmisorCertificado emisorCertificado = servicio.getEmisor();
			emisor.setCif(emisorCertificado.getCif());
			emisor.setNom(getScspHelper().getEmisorNombre(emisorCertificado.getCif()));
			dto.setServeiEmisor(emisor);
		}
		// Obtenim els usuaris que tenen permís sobre el procediment-servei
		List<AccessControlEntry> aces = PermisosHelper.getAclSids(
				ProcedimentServei.class,
				servei.getId(),
				aclService);
		Set<String> usuaris = new HashSet<String>();
		if (aces != null) {
			for (AccessControlEntry ace: aces) {
				if (ace.getSid() instanceof PrincipalSid)
					usuaris.add(((PrincipalSid)ace.getSid()).getPrincipal());
			}
		}
		dto.setServeiUsuaris(usuaris.size());
		// Calculam les peticions correctes i les errònees
		// consulta[0] --> Codi entitat
		// consulta[1] --> Codi procediemnt
		// consulta[2] --> Codi servei
		// consulta[3] --> Estat (Pendent, Processant, Tramitada o Error)
		// consulta[4] --> Número de peticions
		Long peticionsCorrectes = 0L;
		Long peticionsErronees = 0L;
		boolean etrobat = false;
		boolean strobat = false;
		Long entitatId = servei.getProcediment().getEntitat().getId();
		Long procedimentId = servei.getProcediment().getId();
		for (int i = 0; i < consultes.size(); i++) {
			Long entId = (Long)consultes.get(i)[0];
			Long procId = (Long)consultes.get(i)[1];
			if (entId.equals(entitatId) && procId.equals(procedimentId)) {
				etrobat = true;
				String serveiCodi = (String)consultes.get(i)[2];
				if (servei.getServei().equals(serveiCodi)) {
					strobat = true;
					EstatTipus estat = (EstatTipus)consultes.get(i)[3];
					Long count = (Long)consultes.get(i)[4];
					if (estat.equals(EstatTipus.Error))
						peticionsErronees += count;
					else
						peticionsCorrectes += count;
				} else if (strobat) {
					// Les consultes estan ordenades per servei. Per tant, al trobar un nou servei, ja no cal continuar
					strobat = false;
					break;
				}
			} else if(etrobat) {
				// Les consultes estan ordenades per entitat i procediment. Per tant, al trobar una nova entitat-procediment, ja no cal continuar
				etrobat = false;
				break;
			}
		}
		dto.setPeticionsCorrectes(peticionsCorrectes.intValue());
		dto.setPeticionsErronees(peticionsErronees.intValue());
		return dto;
	}

	private ConsultaDto toConsultaDto(
			Long entitatId,
			HistoricConsulta consulta) throws ScspException {
		ConsultaDto resposta = dtoMappingHelper.getMapperFacade().map(
				consulta,
				ConsultaDto.class);
		resposta.setEntitatId(entitatId);
		//resposta.setFuncionariNom(consulta.getCreatedBy().getNom());
		//resposta.setFuncionariNif(consulta.getCreatedBy().getNif());
		resposta.setFuncionariNom(consulta.getFuncionariNom());
		resposta.setFuncionariNif(consulta.getFuncionariDocumentNum());
		try {
			Resposta rpt = getScspHelper().recuperarResposta(
					consulta.getScspPeticionId(),
					consulta.getScspSolicitudId(),
					consulta.isMultiple());
			if (rpt != null) {
				resposta.setFinalitat(rpt.getFinalitat());
				if (rpt.getConsentiment() != null)
					resposta.setConsentiment(Consentiment.valueOf(rpt.getConsentiment().name()));
				resposta.setExpedientId(rpt.getExpedientId());
				resposta.setDepartamentNom(rpt.getUnitatTramitadora());
				resposta.setUnitatTramitadoraCodi(rpt.getUnitatTramitadoraCodi());
				if (rpt.getRespostaXml() != null) {
					resposta.setHiHaResposta(true);
					resposta.setRespostaData(rpt.getRespostaData());
					resposta.setRespostaXml(rpt.getRespostaXml());
				}
				if (rpt.getPeticioXml() != null) {
					resposta.setHiHaPeticio(true);
					resposta.setPeticioXml(rpt.getPeticioXml());
				}
				if (!consulta.isMultiple()) {
					resposta.setDadesEspecifiques(
							getScspHelper().getDadesEspecifiquesPeticio(
									consulta.getScspPeticionId(),
									consulta.getScspSolicitudId()));
				}
			}
		} catch (Exception ex) {
			log.error("No s'han pogut consultar les dades de la petició (peticionId=" + consulta.getScspPeticionId() + ", solicitudId=" + consulta.getScspSolicitudId() + ")", ex);
			throw new ScspException("No s'han pogut consultar les dades de la petició (peticionId=" + consulta.getScspPeticionId() + ", solicitudId=" + consulta.getScspSolicitudId() + ")", ex);
		}
		return resposta;
	}
 
	private Page<ConsultaDto> findByEntitatIUsuariFiltrePaginat(
			Entitat entitat,
			String usuariCodi,
			ConsultaFiltreDto filtre,
			Pageable pageable,
			boolean multiple,
			boolean nomesSensePare,
			boolean consultaHihaPeticio,
			boolean consultaTerData) throws EntitatNotFoundException {
		copiarPropertiesToDb();
		log.debug("HistoricConsulta de peticions findByEntitatIUsuariFiltrePaginat (" +
				"entitat=" + entitat.getCodi() + ", " +
				"usuariCodi=" + usuariCodi + ", " +
				((filtre != null) ? (
				"filtre.scspPeticionId=" + filtre.getScspPeticionId() + ", " +
				"filtre.procedimentId=" + filtre.getProcedimentId() + ", " +
				"filtre.serveiCodi=" + filtre.getServeiCodi() + ", " +
				"filtre.estat=" + filtre.getProcedimentId() + ", " +
				"filtre.dataInici=" + filtre.getProcedimentId() + ", " +
				"filtre.dataFi=" + filtre.getProcedimentId() + ", " +
				"filtre.titularNom=" + filtre.getTitularNom() + ", " +
				"filtre.titularDocument=" + filtre.getTitularDocument() + ", " +
				"filtre.funcionari=" + filtre.getFuncionari() + ", " +
				"filtre.usuari=" + filtre.getUsuari() + ", ") : "") +
				((pageable != null) ? (
				"paginacio.paginaNum=" + pageable.getPageNumber() + ", " +
				"paginacio.paginaTamany=" + pageable.getPageSize() + ", ") : "") +
				"multiple=" + multiple + ", " +
				"nomesSensePare=" + nomesSensePare + ")");
		long t0 = System.currentTimeMillis();
		Page<HistoricConsulta> paginaConsultes;
		if (filtre == null) {
			paginaConsultes = historicConsultaRepository.findByProcedimentServeiProcedimentEntitatIdAndCreatedBy(
					entitat.getId(),
					usuariCodi == null,
					(usuariCodi != null) ? usuariRepository.findOne(usuariCodi) : null,
					multiple,
					nomesSensePare,
					pageable);
		} else {
			historicConsultaRepository.setSessionOptimizerModeToRule();
			paginaConsultes = historicConsultaRepository.findByCreatedByAndFiltrePaginat(
					entitat.getId(),
					usuariCodi == null,
					(usuariCodi != null) ? usuariRepository.findOne(usuariCodi) : null,
					filtre.getScspPeticionId() == null || filtre.getScspPeticionId().isEmpty(),
					filtre.getScspPeticionId(),
					filtre.getProcedimentId() == null,
					filtre.getProcedimentId(),
					filtre.getServeiCodi() == null || filtre.getServeiCodi().isEmpty(),
					filtre.getServeiCodi(),
					filtre.getEstat() == null,
					(filtre.getEstat() != null) ? EstatTipus.valueOf(filtre.getEstat().toString()) : null,
					filtre.getDataInici() == null,
					filtre.getDataInici(),
					filtre.getDataFi() == null,
					configurarDataFiPerFiltre(filtre.getDataFi()),
					filtre.getTitularNom() == null || filtre.getTitularNom().isEmpty(),
					filtre.getTitularNom(),
					filtre.getTitularDocument() == null || filtre.getTitularDocument().isEmpty(),
					filtre.getTitularDocument(),
					filtre.getFuncionari() == null || filtre.getFuncionari().isEmpty(),
					filtre.getFuncionari(),
					filtre.getUsuari() == null || filtre.getUsuari().isEmpty(),
					filtre.getUsuari(),
					multiple,
					nomesSensePare,
					pageable);
		}
		log.debug("[S_CONS] HistoricConsulta a la base de dades (" + (System.currentTimeMillis() - t0) + " ms)");
		t0 = System.currentTimeMillis();
		Page<ConsultaDto> paginaConsultesDto = dtoMappingHelper.pageEntities2pageDto(paginaConsultes, ConsultaDto.class, pageable);
		log.debug("[S_CONS] Conversió a DTO (" + (System.currentTimeMillis() - t0) + " ms)");
		t0 = System.currentTimeMillis();
		if (consultaHihaPeticio) {
			for (ConsultaDto consulta: paginaConsultesDto.getContent()) {
				try {
					consulta.setHiHaPeticio(
							getScspHelper().isPeticionEnviada(
									consulta.getScspPeticionId()));
				} catch (es.scsp.common.exceptions.ScspException ex) {
					log.error("No s'han pogut consultar l'enviament de la petició (id=" + consulta.getScspPeticionId() + ")", ex);
					consulta.setHiHaPeticio(false);
				}
			}
		}
		if (consultaHihaPeticio) {
			for (ConsultaDto consulta: paginaConsultesDto.getContent()) {
				try {
					consulta.setTerData(getScspHelper().getTerPeticion(
										consulta.getScspPeticionId()));
				} catch (es.scsp.common.exceptions.ScspException ex) {
					log.error("No s'han pogut consultar el TER de la petició (id=" + consulta.getScspPeticionId() + ")", ex);
				}
			}
		}
		log.debug("[S_CONS] HistoricConsulta de peticions addicionals (" + (System.currentTimeMillis() - t0) + " ms)");
		return  paginaConsultesDto;
	}

	private Page<ConsultaDto> findByFiltrePaginatAdmin(
			ConsultaFiltreDto filtre,
			Pageable pageable,
			boolean consultaHihaPeticio,
			boolean consultaTerData) throws EntitatNotFoundException {
		copiarPropertiesToDb();
		log.debug("HistoricConsulta de peticions findByFiltrePaginat (" +
				((filtre != null) ? (
				"filtre.entitatId=" + filtre.getEntitatId() + ", " +
				"filtre.scspPeticionId=" + filtre.getScspPeticionId() + ", " +
				"filtre.procedimentId=" + filtre.getProcedimentId() + ", " +
				"filtre.serveiCodi=" + filtre.getServeiCodi() + ", " +
				"filtre.estat=" + filtre.getProcedimentId() + ", " +
				"filtre.dataInici=" + filtre.getProcedimentId() + ", " +
				"filtre.dataFi=" + filtre.getProcedimentId() + ", " +
				"filtre.titularNom=" + filtre.getTitularNom() + ", " +
				"filtre.titularDocument=" + filtre.getTitularDocument() + ", " +
				"filtre.usuari=" + filtre.getUsuari() + ", " +
				"filtre.funcionari=" + filtre.getFuncionari() + ", " +
				"filtre.recobriment=" + filtre.getRecobriment() + ", ") : "") +
				((pageable != null) ? (
				"paginacio.paginaNum=" + pageable.getPageNumber() + ", " +
				"paginacio.paginaTamany=" + pageable.getPageSize() + ", ") : "") + ")");
		long t0 = System.currentTimeMillis();
		Page<HistoricConsulta> paginaConsultes = historicConsultaRepository.findByFiltrePaginatAdmin(
				filtre.getEntitatId() == null,
				filtre.getEntitatId(),
				filtre.getScspPeticionId() == null || filtre.getScspPeticionId().isEmpty(),
				filtre.getScspPeticionId(),
				filtre.getProcedimentId() == null,
				filtre.getProcedimentId(),
				filtre.getServeiCodi() == null || filtre.getServeiCodi().isEmpty(),
				filtre.getServeiCodi(),
				filtre.getEstat() == null,
				(filtre.getEstat() != null) ? EstatTipus.valueOf(filtre.getEstat().toString()) : null,
				filtre.getDataInici() == null,
				filtre.getDataInici(),
				filtre.getDataFi() == null,
				configurarDataFiPerFiltre(filtre.getDataFi()),
				filtre.getTitularNom() == null || filtre.getTitularNom().isEmpty(),
				filtre.getTitularNom(),
				filtre.getTitularDocument() == null || filtre.getTitularDocument().isEmpty(),
				filtre.getTitularDocument(),
				filtre.getFuncionari() == null || filtre.getFuncionari().isEmpty(),
				filtre.getFuncionari(),
				filtre.getUsuari() == null || filtre.getUsuari().isEmpty(),
				filtre.getUsuari(),
				filtre.getRecobriment() == null,
				filtre.getRecobriment(),
				pageable);
		log.debug("[S_CONS] HistoricConsulta a la base de dades (" + (System.currentTimeMillis() - t0) + " ms)");
		t0 = System.currentTimeMillis();
		Page<ConsultaDto> paginaConsultesDto = dtoMappingHelper.pageEntities2pageDto(
				paginaConsultes,
				ConsultaDto.class,
				pageable);
		log.debug("[S_CONS] Conversió a DTO (" + (System.currentTimeMillis() - t0) + " ms)");
		t0 = System.currentTimeMillis();
		/*for (ConsultaDto consulta: paginaConsultesDto.getContent()) {
			consulta.setServeiDescripcio(
					getScspHelper().getServicioDescripcion(
							consulta.getServeiCodi()));
		}*/
		if (consultaHihaPeticio) {
			for (ConsultaDto consulta: paginaConsultesDto.getContent()) {
				try {
					consulta.setHiHaPeticio(
							getScspHelper().isPeticionEnviada(
									consulta.getScspPeticionId()));
				} catch (es.scsp.common.exceptions.ScspException ex) {
					log.error("No s'han pogut consultar l'enviament de la petició (id=" + consulta.getScspPeticionId() + ")", ex);
					consulta.setHiHaPeticio(false);
				}
			}
		}
		if (consultaTerData) {
			for (ConsultaDto consulta: paginaConsultesDto.getContent()) {
				try {
					consulta.setTerData(getScspHelper().getTerPeticion(
										consulta.getScspPeticionId()));
				} catch (es.scsp.common.exceptions.ScspException ex) {
					log.error("No s'han pogut consultar el TER de la petició (id=" + consulta.getScspPeticionId() + ")", ex);
				}
			}
		}
		log.debug("[S_CONS] HistoricConsulta de peticions addicionals (" + (System.currentTimeMillis() - t0) + " ms)");
		return paginaConsultesDto;
	}

	private JustificantDto obtenirJustificantComu(
			final HistoricConsulta consulta,
			final boolean descarregar) throws JustificantGeneracioException {
		// Abans de continuar es comprova si l'estat de la consulta és "Tramitada"
		if (EstatTipus.Tramitada.equals(consulta.getEstat())) {
			// Amb aquest bloc sincronitzat aconseguim que només hi hagi un thread a la vegada
			// generant el justificant per a una determinada consulta. Si un altre thread intenta
			// generar el mateix justificant es quedarà bloquejat esperant.
			// Això es fa per a evitar problemes derivats de l'accés concurrent a l'arxiu digital.
			try {
				synchronized (getJustificantLockForConsulta(consulta)) {
					TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
					transactionTemplate.execute(new TransactionCallback<Object>() {
						@Override
						public Object doInTransaction(TransactionStatus status) {
							HistoricConsulta consultaRefreshed = historicConsultaRepository.getOne(consulta.getId());
							// Si l'estat del justificant és PENDENT o ERROR intentam tornar a generar el justificant
							if (JustificantEstat.PENDENT.equals(consultaRefreshed.getJustificantEstat()) || JustificantEstat.ERROR.equals(consultaRefreshed.getJustificantEstat())) {
								justificantHelper.generarCustodiarJustificantPendent(
										consultaRefreshed,
										getScspHelper());
								historicConsultaRepository.saveAndFlush(consultaRefreshed);
							}
							return null;
						}
					});
				}
			} finally {
				releaseJustificantLockForConsulta(consulta);
			}
			if (descarregar) {
				TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
				return transactionTemplate.execute(new TransactionCallback<JustificantDto>() {
					@Override
					public JustificantDto doInTransaction(TransactionStatus status) {
						HistoricConsulta consultaRefreshed = historicConsultaRepository.getOne(consulta.getId());
						JustificantDto justificant = new JustificantDto();
						justificant.setEstat(
								ConsultaDto.JustificantEstat.valueOf(
										consultaRefreshed.getJustificantEstat().name()));
						if (JustificantEstat.ERROR.equals(consultaRefreshed.getJustificantEstat())) {
							justificant.setError(true);
							justificant.setErrorDescripcio("La generació del justificant ha produït errors");
						}
						if (JustificantEstat.NO_DISPONIBLE.equals(consultaRefreshed.getJustificantEstat())) {
							justificant.setError(true);
							justificant.setErrorDescripcio("L'estat del justificant de la consulta és NO_DISPONIBLE");
						}
						if (!justificant.isError()) {
							try {
								FitxerDto justificantFitxer = justificantHelper.descarregarFitxerGenerat(
										consultaRefreshed,
										getScspHelper());
								justificant.setNom(justificantFitxer.getNom());
								justificant.setContentType(justificantFitxer.getContentType());
								justificant.setContingut(justificantFitxer.getContingut());
							} catch (Exception ex) {
								log.error("La descàrrega del justificant ha produït errors (" +
										"id=" + consultaRefreshed.getScspPeticionId() + ", " +
										"scspPeticionId=" + consultaRefreshed.getScspPeticionId() + ", " +
										"scspSolicitudId=" + consultaRefreshed.getScspSolicitudId() + ")",
										ex);
								justificant.setError(true);
								justificant.setErrorDescripcio("La descàrrega del justificant ha produit errors");
							}
						}
						return justificant;
					}
				});
			} else {
				return null;
			}
		} else {
			throw new JustificantGeneracioException("La consulta no està en estat TRAMITADA");
		}
	}

	private Date configurarDataFiPerFiltre(Date dataFi) {
		if (dataFi == null)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataFi);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}

	private int[] getRandomIndexesFromList(
			List<?> list,
			int numIndexes) {
		int[] indexes = new int[numIndexes];
		for (int i = 0; i < numIndexes; i++) {
			int randomNum;
			boolean existeix;
			do {
				randomNum = new Random().nextInt(list.size());
				existeix = false;
				for (int j = 0; j < i; j++) {
					if (indexes[j] == randomNum)
						existeix = true;
				}
			} while (existeix);
			indexes[i] = randomNum;
		}
		Arrays.sort(indexes);
		return indexes;
	}

	private boolean propertiesCopiades = false;
	private void copiarPropertiesToDb() {
		if (!propertiesCopiades) {
			getScspHelper().copiarPropertiesToDb(
					PropertiesHelper.getProperties());
			propertiesCopiades = true;
		}
	}

	private ScspHelper getScspHelper() {
		if (scspHelper == null) {
			scspHelper = new ScspHelper(
					applicationContext,
					messageSource);
		}
		return scspHelper;
	}

	private Object getJustificantLockForConsulta(HistoricConsulta consulta) {
		Long id = consulta.getId();
		if (!justificantLocks.containsKey(id)) {
			justificantLocks.put(id, new Object());
		}
		return justificantLocks.get(id);
	}
	private void releaseJustificantLockForConsulta(HistoricConsulta consulta) {
		Long id = consulta.getId();
		if (justificantLocks.containsKey(id)) {
			justificantLocks.remove(id);
		}
	}

}
