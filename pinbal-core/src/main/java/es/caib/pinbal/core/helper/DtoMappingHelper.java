/**
 * 
 */
package es.caib.pinbal.core.helper;

import es.caib.pinbal.core.dto.ClauPrivadaDto;
import es.caib.pinbal.core.dto.ConfigDto;
import es.caib.pinbal.core.dto.ConfigGroupDto;
import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.InformeUsuariDto;
import es.caib.pinbal.core.dto.OrganGestorDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.core.dto.ServeiCampGrupDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.dto.regles.ServeiReglaDto;
import es.caib.pinbal.core.model.ClauPrivada;
import es.caib.pinbal.core.model.Config;
import es.caib.pinbal.core.model.ConfigGroup;
import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatUsuari;
import es.caib.pinbal.core.model.HistoricConsulta;
import es.caib.pinbal.core.model.OrganGestor;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ServeiCamp;
import es.caib.pinbal.core.model.ServeiCampGrup;
import es.caib.pinbal.core.model.ServeiRegla;
import es.caib.pinbal.core.model.llistat.LlistatConsulta;
import es.caib.pinbal.core.model.llistat.LlistatHistoricConsulta;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.Type;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper per al mapeig de classes del model a DTOs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DtoMappingHelper {

	private MapperFactory mapperFactory;

	@SuppressWarnings("deprecation")
	public DtoMappingHelper() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		// Mapeig d'entitats
		mapperFactory.registerClassMap(ClassMapBuilder.map(Entitat.class, EntitatDto.class).byDefault().toClassMap());
		mapperFactory.getConverterFactory().registerConverter(new CustomConverter<EntitatUsuari, EntitatUsuariDto>() {
			public EntitatUsuariDto convert(EntitatUsuari source, Type<? extends EntitatUsuariDto> destinationClass) {
				EntitatUsuariDto dto = new EntitatUsuariDto(
						new UsuariDto(
								source.getUsuari().getCodi(),
								source.getUsuari().getNif(),
								source.getUsuari().getNom(),
								source.getUsuari().isInicialitzat(),
								source.getUsuari().isNoInicialitzatNif(),
								source.getUsuari().isNoInicialitzatCodi()),
						source.getDepartament(),
						source.isPrincipal(),
						source.isRepresentant(),
						source.isDelegat(),
						source.isAuditor(),
						source.isAplicacio(),
						source.isActiu());
				return dto;
			}
		});
		// Mapeig de procediments
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(Procediment.class, ProcedimentDto.class).field("entitat.id", "entitatId").field(
						"entitat.nom",
						"entitatNom").byDefault().toClassMap());
		// Mapeig de consultes
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(Consulta.class, ConsultaDto.class).
						field("createdBy", "creacioUsuari").
						field("createdDate", "creacioData").
						field("funcionariDocumentNum", "funcionariNif").
						field("pare.id", "pareId").
						field("procediment.codi", "procedimentCodi").
						field("serveiScsp.descripcio", "serveiDescripcio").
						exclude("dadesEspecifiques").
						byDefault().toClassMap());
		// Mapeig de llistat de consultes
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(LlistatConsulta.class, ConsultaDto.class).
						field("peticioId", "scspPeticionId").
						field("solicitudId", "scspSolicitudId").
						field("serveiNom", "serveiDescripcio").
						field("data", "creacioData").
						field("usuariCodi", "creacioUsuari.codi").
						field("usuariNom", "creacioUsuari.nom").
						customize(new CustomMapper<LlistatConsulta, ConsultaDto>() {
							@Override
							public void mapAtoB(LlistatConsulta llistatConsulta, ConsultaDto consultaDto, MappingContext context) {
								super.mapAtoB(llistatConsulta, consultaDto, context);
								consultaDto.setEstat(llistatConsulta.getEstat().name());
							}
						}).
						byDefault().toClassMap());
		// Mapeig de historic de consultes
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(HistoricConsulta.class, ConsultaDto.class).
						field("createdBy", "creacioUsuari").
						field("createdDate", "creacioData").
						field("funcionariDocumentNum", "funcionariNif").
						field("pare.id", "pareId").
						field("procediment.codi", "procedimentCodi").
						field("serveiScsp.descripcio", "serveiDescripcio").
						exclude("dadesEspecifiques").
						byDefault().toClassMap());
		// Mapeig de historic de llistat de consultes
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(LlistatHistoricConsulta.class, ConsultaDto.class).
						field("peticioId", "scspPeticionId").
						field("solicitudId", "scspSolicitudId").
						field("serveiNom", "serveiDescripcio").
						field("data", "creacioData").
						field("usuariCodi", "creacioUsuari.codi").
						field("usuariNom", "creacioUsuari.nom").
						customize(new CustomMapper<LlistatHistoricConsulta, ConsultaDto>() {
							@Override
							public void mapAtoB(LlistatHistoricConsulta llistatConsulta, ConsultaDto consultaDto, MappingContext context) {
								super.mapAtoB(llistatConsulta, consultaDto, context);
								consultaDto.setEstat(llistatConsulta.getEstat().name());
							}
						}).
						byDefault().toClassMap());
		// Mapeig d'informes d'usuaris
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(EntitatUsuari.class, InformeUsuariDto.class).
						field("usuari.codi", "codi").
						field("usuari.nif", "nif").
						field("usuari.nom", "nom").
						field("departament", "departament").
						field("entitat", "entitat").byDefault().toClassMap());
		mapperFactory.getConverterFactory().registerConverter(new CustomConverter<DateTime, Date>() {
			public Date convert(DateTime source, Type<? extends Date> destinationClass) {
				return source.toDate();
			}
		});
		// Mapeig de camps de serveis
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(ServeiCamp.class, ServeiCampDto.class).byDefault().toClassMap());
		// Mapeig de camps de organismes cessionaris
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(ClauPrivada.class, ClauPrivadaDto.class).
						field("organisme.id", "organisme").
						byDefault().toClassMap());
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(ConfigGroup.class, ConfigGroupDto.class).byDefault().toClassMap());
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(Config.class, ConfigDto.class)
						.field("type.code", "typeCode")
						.customize(new CustomMapper<Config, ConfigDto>() {
							@Override
							public void mapAtoB(Config config, ConfigDto configDto, MappingContext context) {
								super.mapAtoB(config, configDto, context);
								configDto.setValidValues(config.getType().getValidValues());
							}
						})
						.byDefault().toClassMap());
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(OrganGestor.class, OrganGestorDto.class).
						field("pare.codi", "pareCodi").
						field("pare.nom", "pareNom").
						field("entitat.nom", "entitatNom").
						byDefault().toClassMap());
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(ServeiCampGrup.class, ServeiCampGrupDto.class).
						field("pare.id", "pareId").
						byDefault().toClassMap());
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(ServeiRegla.class, ServeiReglaDto.class).
						customize(new CustomMapper<ServeiRegla, ServeiReglaDto>() {
							@Override
							public void mapAtoB(ServeiRegla serveiRegla, ServeiReglaDto serveiReglaDto, MappingContext context) {
								serveiReglaDto.setId(serveiRegla.getId());
								serveiReglaDto.setNom(serveiRegla.getNom());
								serveiReglaDto.setServeiId(serveiRegla.getServei().getId());
								serveiReglaDto.setModificat(serveiRegla.getModificat());
								serveiReglaDto.setModificatValor(serveiRegla.getModificatValor() != null ? new LinkedHashSet<String>(serveiRegla.getModificatValor()) : new LinkedHashSet<String>());
								serveiReglaDto.setAfectatValor(serveiRegla.getAfectatValor() != null ? new LinkedHashSet<String>(serveiRegla.getAfectatValor()) : new LinkedHashSet<String>());
								serveiReglaDto.setAccio(serveiRegla.getAccio());
								serveiReglaDto.setOrdre(serveiRegla.getOrdre());
							}
						}).
//						byDefault().
						toClassMap());
		mapperFactory.build();
	}

	public MapperFacade getMapperFacade() {
		return mapperFactory.getMapperFacade();
	}

	public <T> T convertir(Object source, Class<T> targetType) {
		if (source == null)
			return null;
		return getMapperFacade().map(source, targetType);
	}

	public <T> List<T> convertirList(List<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsList(items, targetType);
	}

	public <T> Set<T> convertirSet(Set<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsSet(items, targetType);
	}

	public <S, D> Page<D> pageEntities2pageDto(Page<S> pageEntities, Class<D> destinationClass, Pageable pageable) {
		return new PageImpl<D>(
				this.getMapperFacade().mapAsList(pageEntities.getContent(), destinationClass),
				pageable,
				pageEntities.getTotalElements());
	}

}
