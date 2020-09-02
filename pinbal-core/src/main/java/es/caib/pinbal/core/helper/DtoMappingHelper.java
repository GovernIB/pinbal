/**
 * 
 */
package es.caib.pinbal.core.helper;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import es.caib.pinbal.core.dto.ClauPrivadaDto;
import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.InformeUsuariDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.model.ClauPrivada;
import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatUsuari;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ServeiCamp;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
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
						source.isAplicacio());
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
				ClassMapBuilder.map(Consulta.class, ConsultaDto.class).field(
						"procedimentServei.procediment.entitat.nom",
						"entitatNom").field("procedimentServei.procediment.entitat.cif", "entitatCif").
				// field("createdBy.codi", "funcionariNom").
				// field("createdBy.codi", "funcionariNif").
						field("procedimentServei.procediment.id", "procedimentId").field(
								"procedimentServei.procediment.nom",
								"procedimentNom").field("procedimentServei.servei", "serveiCodi").field(
										"createdBy",
										"creacioUsuari").field("createdDate", "creacioData").field(
												"funcionariDocumentNum",
												"funcionariNif").field("pare.id", "pareId").byDefault().toClassMap());
		// Mapeig d'informes d'usuaris
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(EntitatUsuari.class, InformeUsuariDto.class).field("usuari.codi", "codi").field(
						"usuari.nif",
						"nif").field("usuari.nom", "nom").field("departament", "departament").field(
								"entitat",
								"entitat").byDefault().toClassMap());
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
				ClassMapBuilder.map(ClauPrivada.class, ClauPrivadaDto.class).field(
						"organisme.id",
						"organisme").byDefault().toClassMap());
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
