/**
 * 
 */
package es.caib.pinbal.webapp.helper;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.webapp.command.EntitatCommand;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

import java.util.Map;

/**
 * Helper per al mapeig de DTOS a commands.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CommandMappingHelper {

	private static MapperFactory mapperFactory;

	public static MapperFacade getMapperFacade() {
		return getMapperFactory().getMapperFacade();
	}

	@SuppressWarnings("deprecation")
	private static MapperFactory getMapperFactory() {
		if (mapperFactory == null) {
			mapperFactory = new DefaultMapperFactory.Builder().build();
			mapperFactory.registerClassMap(
					ClassMapBuilder.map(
							EntitatDto.class,
							EntitatCommand.class).byDefault().toClassMap());
			mapperFactory.getConverterFactory().registerConverter(
					new PassThroughConverter(Map.class));
			mapperFactory.registerClassMap(
					ClassMapBuilder.map(
							Map.class,
							Map.class).byDefault().toClassMap());
			mapperFactory.build();
		}
		return mapperFactory;
	}

}
