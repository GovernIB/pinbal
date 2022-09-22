/**
 * 
 */
package es.caib.pinbal.core.helper;

import java.util.Date;
import java.util.List;
import java.util.Set;

import es.caib.pinbal.core.dto.*;
import es.caib.pinbal.core.model.*;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.Type;

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
				/*field("procedimentServei.procediment.entitat.nom", "entitatNom").
				field("procedimentServei.procediment.entitat.cif", "entitatCif").
				field("procedimentServei.procediment.id", "procedimentId").
				field("procedimentServei.procediment.nom", "procedimentNom").
				field("procedimentServei.servei", "serveiCodi").*/
				field("createdBy", "creacioUsuari").
				field("createdDate", "creacioData").
				field("funcionariDocumentNum", "funcionariNif").
				field("pare.id", "pareId").
				exclude("dadesEspecifiques").
				byDefault().toClassMap());
		// Mapeig de historic de consultes
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(HistoricConsulta.class, ConsultaDto.class).
						field("createdBy", "creacioUsuari").
						field("createdDate", "creacioData").
						field("funcionariDocumentNum", "funcionariNif").
						field("pare.id", "pareId").
						exclude("dadesEspecifiques").
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
