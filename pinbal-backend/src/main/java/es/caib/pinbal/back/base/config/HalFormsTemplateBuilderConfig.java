package es.caib.pinbal.back.base.config;

import es.caib.pinbal.back.base.util.ResourceServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.forms.CustomHalFormsTemplateBuilder;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;

/**
 * Configuració del HalFormsTemplateBuilder de spring-hateoas.
 *
 * @author Límit Tecnologies
 */
@Configuration
public class HalFormsTemplateBuilderConfig {

	@Autowired
	private ApplicationContext applicationContext;
	@Autowired(required = false)
	private HalFormsConfiguration halFormsConfiguration;
	@Autowired
	private ResourceServiceLocator resourceServiceLocator;

	@PostConstruct
	public void customizeHalFormsTemplateBuilder() {
		if (halFormsConfiguration != null) {
			Object halFormsTemplatePropertyWriter = applicationContext.getBean("halFormsTemplatePropertyWriter");
			Field builderField = ReflectionUtils.findField(halFormsTemplatePropertyWriter.getClass(), "builder");
			ReflectionUtils.makeAccessible(builderField);
			Object halFormsTemplateBuilder = ReflectionUtils.getField(builderField, halFormsTemplatePropertyWriter);
			Field builderResolverField = ReflectionUtils.findField(halFormsTemplateBuilder.getClass(), "resolver");
			ReflectionUtils.makeAccessible(builderResolverField);
			MessageResolver builderResolver = (MessageResolver) ReflectionUtils.getField(builderResolverField, halFormsTemplateBuilder);
			CustomHalFormsTemplateBuilder customHalFormsTemplateBuilder = new CustomHalFormsTemplateBuilder(
					halFormsConfiguration,
					builderResolver,
					resourceServiceLocator);
			ReflectionUtils.setField(builderField, halFormsTemplatePropertyWriter, customHalFormsTemplateBuilder);
		}
	}

}
