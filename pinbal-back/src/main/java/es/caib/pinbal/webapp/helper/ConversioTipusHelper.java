package es.caib.pinbal.webapp.helper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.JavassistCompilerStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Helper per a convertir entre diferents formats de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ConversioTipusHelper {

	private static MapperFactory mapperFactory;

	public static <T> T convertir(Object source, Class<T> targetType) {
		if (source == null)
			return null;
		return getMapperFacade().map(source, targetType);
	}
	public static <T> List<T> convertirList(List<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsList(items, targetType);
	}
	public static <T> Set<T> convertirSet(Set<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsSet(items, targetType);
	}



	private static MapperFacade getMapperFacade() {
		if (mapperFactory == null)
			mapperFactory = new DefaultMapperFactory.Builder().compilerStrategy(new JavassistCompilerStrategy()).build();
		/*mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<MultipartFile, byte[]>() {
					public byte[] convert(MultipartFile source, Type<? extends byte[]> destinationClass) {
						byte[] target = new byte[0];
						return target;
					}
				});*/
		return mapperFactory.getMapperFacade();
	}

}
