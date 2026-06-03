package es.caib.pinbal.back.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.pinbal.back.base.controller.MutableResourceController;
import es.caib.pinbal.back.base.controller.ReadonlyResourceController;
import es.caib.pinbal.back.base.util.HalFormsUtil;
import es.caib.pinbal.back.base.util.ResourceServiceLocator;
import es.caib.pinbal.logic.intf.base.annotation.ResourceArtifact;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.annotation.ResourceField;
import es.caib.pinbal.logic.intf.base.model.Resource;
import es.caib.pinbal.logic.intf.base.model.ResourceArtifactType;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.logic.intf.base.util.I18nUtil;
import es.caib.pinbal.logic.intf.base.util.StringUtil;
import es.caib.pinbal.logic.intf.base.util.TypeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Configuració de HAL-FORMS.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Configuration
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL_FORMS)
public class HalFormsConfig {

	@Autowired(required = false)
	private Set<ReadonlyResourceController> resourceControllers;
	@Autowired
	private ResourceServiceLocator resourceServiceLocator;
	@Autowired
	protected ObjectMapper objectMapper;

	@Bean
	HalFormsConfiguration halFormsConfiguration() {
		Set<Class<ReadonlyResourceController>> resourceControllerClasses = null;
		if (resourceControllers != null) {
			resourceControllerClasses = resourceControllers.stream().
					map(rc -> (Class<ReadonlyResourceController>) rc.getClass()).
					collect(Collectors.toSet());
		}
		return createHalFormsConfiguration(resourceControllerClasses);
	}

	private HalFormsConfiguration createHalFormsConfiguration(
			Set<Class<ReadonlyResourceController>> resourceControllerClasses) {
		HalFormsConfiguration halFormsConfiguration = new HalFormsConfiguration();
		if (resourceControllerClasses != null) {
			for (Class<ReadonlyResourceController> rc: resourceControllerClasses) {
				Class<?> resourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
						rc,
						ReadonlyResourceController.class,
						0);
				halFormsConfiguration = withResourceClass(halFormsConfiguration, resourceClass, resourceControllerClasses);
			}
		}
		return halFormsConfiguration;
	}

	private HalFormsConfiguration withResourceClass(
			HalFormsConfiguration halFormsConfiguration,
			Class<?> resourceClass,
			Set<Class<ReadonlyResourceController>> resourceControllerClasses) {
		MutableHolder<HalFormsConfiguration> halFormsConfigurationHolder = new MutableHolder<>(halFormsConfiguration);
		ReflectionUtils.doWithFields(
				resourceClass,
				field -> configurationWithEnumOptions(
						halFormsConfigurationHolder,
						resourceClass,
						field),
				this::isEnumTypeMultipleAware);
		ReflectionUtils.doWithFields(
				resourceClass,
				field -> configurationWithResourceReferenceOptions(
						halFormsConfigurationHolder,
						resourceClass,
						null,
						field,
						resourceControllerClasses),
				this::isResourceReferenceTypeMultipleAware);
		ReflectionUtils.doWithFields(
				resourceClass,
				field -> configurationWithFieldEnumOptions(
						halFormsConfigurationHolder,
						resourceClass,
						null,
						field,
						resourceControllerClasses),
				this::isFieldEnumOptions);
		ResourceConfig resourceConfig = resourceClass.getAnnotation(ResourceConfig.class);
		if (resourceConfig != null) {
			for (ResourceArtifact artifact: resourceConfig.artifacts()) {
				if (!Serializable.class.equals(artifact.formClass())) {
					ReflectionUtils.doWithFields(
							artifact.formClass(),
							field -> configurationWithEnumOptions(
									halFormsConfigurationHolder,
									artifact.formClass(),
									field),
							this::isEnumTypeMultipleAware);
					ReflectionUtils.doWithFields(
							artifact.formClass(),
							field -> configurationWithResourceReferenceOptions(
									halFormsConfigurationHolder,
									resourceClass,
									artifact,
									field,
									resourceControllerClasses),
							this::isResourceReferenceTypeMultipleAware);
					ReflectionUtils.doWithFields(
							artifact.formClass(),
							field -> configurationWithFieldEnumOptions(
									halFormsConfigurationHolder,
									resourceClass,
									artifact,
									field,
									resourceControllerClasses),
							this::isFieldEnumOptions);
				}
			}
		}
		return halFormsConfigurationHolder.getValue();
	}

	private void configurationWithEnumOptions(
			MutableHolder<HalFormsConfiguration> halFormsConfigurationHolder,
			Class<?> resourceClass,
			Field resourceField) {
		log.debug("New HAL-FORMS enum options (class={}, field={})", resourceClass, resourceField.getName());
		halFormsConfigurationHolder.setValue(
				halFormsConfigurationHolder.getValue().withOptions(
						resourceClass,
						resourceField.getName(),
						metadata -> {
							Map<String, Object> newResourceValues = HalFormsUtil.getNewResourceValues(
									resourceClass,
									resourceServiceLocator);
							return HalFormsOptions.
									inline(getInlineOptionsEnumConstants(resourceField)).
									withValueField("id").
									withPromptField("description").
									withMinItems(TypeUtil.isNotNullField(resourceField) ? 1L : 0L).
									withMaxItems(TypeUtil.isMultipleFieldType(resourceField) ? null : 1L).
									withSelectedValue(newResourceValues.get(resourceField.getName()));
						}));
	}

	private void configurationWithResourceReferenceOptions(
			MutableHolder<HalFormsConfiguration> halFormsConfigurationHolder,
			Class<?> resourceClass,
			ResourceArtifact artifact,
			Field resourceField,
			Set<Class<ReadonlyResourceController>> resourceControllerClasses) {
		Class<?> optionsResourceClass = artifact != null ? artifact.formClass() : resourceClass;
		log.debug("New HAL-FORMS resource reference options (class={}, field={})", optionsResourceClass, resourceField.getName());
		halFormsConfigurationHolder.setValue(
				halFormsConfigurationHolder.getValue().withOptions(
						optionsResourceClass,
						resourceField.getName(),
						metadata -> {
							Link remoteOptionsLink = getRemoteOptionsLink(
									resourceClass,
									artifact,
									resourceField,
									resourceControllerClasses);
							Map<String, Object> newResourceValues = HalFormsUtil.getNewResourceValues(
									resourceClass,
									resourceServiceLocator);
							return HalFormsOptions.
									remote(remoteOptionsLink != null ? remoteOptionsLink : Link.of("_readonly_ref_")).
									withValueField("id").
									withPromptField(getRemoteOptionsPromptField(resourceField)).
									withMinItems(TypeUtil.isNotNullField(resourceField) ? 1L : 0L).
									withMaxItems(TypeUtil.isCollectionFieldType(resourceField) ? null : 1L).
									withSelectedValue(newResourceValues.get(resourceField.getName()));
						}));
	}

	private void configurationWithFieldEnumOptions(
			MutableHolder<HalFormsConfiguration> halFormsConfigurationHolder,
			Class<?> resourceClass,
			ResourceArtifact artifact,
			Field resourceField,
			Set<Class<ReadonlyResourceController>> resourceControllerClasses) {
		Class<?> optionsResourceClass = artifact != null ? artifact.formClass() : resourceClass;
		log.debug("New HAL-FORMS field enum options (class={}, field={})", resourceClass, resourceField.getName());
		halFormsConfigurationHolder.setValue(
				halFormsConfigurationHolder.getValue().withOptions(
						optionsResourceClass,
						resourceField.getName(),
						metadata -> {
							Link remoteOptionsLink = getRemoteFieldEnumOptionsLink(
									resourceClass,
									artifact,
									resourceField,
									resourceControllerClasses);
							Map<String, Object> newResourceValues = HalFormsUtil.getNewResourceValues(
									resourceClass,
									resourceServiceLocator);
							return HalFormsOptions.
									remote(remoteOptionsLink != null ? remoteOptionsLink : Link.of("_readonly_enum_")).
									withValueField("value").
									withPromptField("description").
									withMinItems(TypeUtil.isNotNullField(resourceField) ? 1L : 0L).
									withMaxItems(TypeUtil.isCollectionFieldType(resourceField) ? null : 1L).
									withSelectedValue(newResourceValues.get(resourceField.getName()));
						}));
	}

	private boolean isEnumTypeMultipleAware(Field field) {
		Class<?> fieldType = TypeUtil.getFieldTypeMultipleAware(field);
		return fieldType != null && fieldType.isEnum();
	}

	private boolean isResourceReferenceTypeMultipleAware(Field field) {
		Class<?> fieldType = TypeUtil.getFieldTypeMultipleAware(field);
		return fieldType != null && ResourceReference.class.isAssignableFrom(fieldType);
	}

	private boolean isFieldEnumOptions(Field field) {
		ResourceField resourceField = field.getAnnotation(ResourceField.class);
		return resourceField != null && resourceField.enumType();
	}

	private FieldOption[] getInlineOptionsEnumConstants(Field field) {
		Object[] enumConstants;
		if (TypeUtil.isMultipleFieldType(field)) {
			Class<?> multipleFieldType = TypeUtil.getMultipleFieldType(field);
			if (multipleFieldType != null) {
				enumConstants = multipleFieldType.getEnumConstants();
			} else {
				enumConstants = new Object[0];
			}
		} else {
			enumConstants = field.getType().getEnumConstants();
		}
		return Arrays.stream(enumConstants).
				map(e -> new FieldOption(
						getEnumFieldOptionId(e),
						I18nUtil.getInstance().getI18nEnumDescription(
								field,
								e.toString()))).
				toArray(FieldOption[]::new);
	}

	private Link getRemoteOptionsLink(
			Class<?> resourceClass,
			ResourceArtifact artifact,
			Field resourceField,
			Set<Class<ReadonlyResourceController>> resourceControllerClasses) {
		Optional<Class<ReadonlyResourceController>> resourceControllerClass = findResourceControllerClass(
			resourceClass,
			false,
			resourceControllerClasses);
		if (resourceControllerClass.isPresent()) {
			Link findLink = getFindLinkWithSelfRel(
					resourceControllerClass.get(),
					artifact,
					resourceField.getName());
			if (findLink != null) {
				// Al link generat li canviam les variables namedQuery i
				// perspective perquè no les posa com a múltiples.
				String findLinkHref = findLink.getHref().
						replace("namedQuery", "namedQuery*").
						replace("perspective", "perspective*");
				// I a més hi afegim les variables page, size i sort que no les
				// detecta a partir de la classe de tipus Pageable
				TemplateVariables findTemplateVariables = new TemplateVariables(
						new TemplateVariable("page", TemplateVariable.VariableType.REQUEST_PARAM),
						new TemplateVariable("size", TemplateVariable.VariableType.REQUEST_PARAM),
						new TemplateVariable("sort", TemplateVariable.VariableType.REQUEST_PARAM).composite());
				return Link.of(UriTemplate.of(findLinkHref).with(findTemplateVariables), findLink.getRel());
			} else {
				return null;
			}
		} else {
			Class<?> referencedResourceClass = TypeUtil.getReferencedResourceClass(resourceField);
			log.error("Couldn't find resource controller class from field (" +
					"resourceClass=" + resourceClass + "," +
					"fieldName=" + resourceField.getName() + "," +
					"referencedResourceClass=" + referencedResourceClass + ")");
			return null;
		}
	}

	private Link getRemoteFieldEnumOptionsLink(
			Class<?> resourceClass,
			ResourceArtifact artifact,
			Field resourceField,
			Set<Class<ReadonlyResourceController>> resourceControllerClasses) {
		Optional<Class<ReadonlyResourceController>> resourceControllerClass = findResourceControllerClass(
			resourceClass,
			artifact == null,
			resourceControllerClasses);
		if (resourceControllerClass.isPresent()) {
			if (artifact == null) {
				Class<MutableResourceController> mutableResourceControllerClass = (Class<MutableResourceController>)((Class<?>)resourceControllerClass.get());
				return linkTo(methodOn(mutableResourceControllerClass).fieldEnumOptionsFind(resourceField.getName())).
						withRel(IanaLinkRelations.SELF_VALUE);
			} else {
				return linkTo(methodOn(resourceControllerClass.get()).artifactFieldEnumOptionsFind(
						artifact.type(),
						artifact.code(),
						resourceField.getName())).
						withRel(IanaLinkRelations.SELF_VALUE);
			}
		} else {
			Class<?> referencedResourceClass = TypeUtil.getReferencedResourceClass(resourceField);
			log.error("Couldn't find resource controller class from field (" +
					"resourceClass=" + resourceClass + "," +
					"fieldName=" + resourceField.getName() + "," +
					"referencedResourceClass=" + referencedResourceClass + ")");
			return null;
		}
	}

	private Link getFindLinkWithSelfRel(
			Class<?> resourceControllerClass,
			ResourceArtifact artifact,
			String resourceFieldName) {
		Class<ReadonlyResourceController> readonlyResourceControllerClass = (Class<ReadonlyResourceController>)resourceControllerClass;
		boolean isMutableResourceController = MutableResourceController.class.isAssignableFrom(resourceControllerClass);
		if (artifact == null) {
			if (isMutableResourceController) {
				Class<MutableResourceController> mutableResourceControllerClass = (Class<MutableResourceController>)resourceControllerClass;
				return linkTo(methodOn(mutableResourceControllerClass).fieldOptionsFind(
						resourceFieldName,
						null,
						null,
						null,
						null,
						null)).withRel(IanaLinkRelations.SELF_VALUE);
			} else {
				return null;
			}
		} else if (artifact.type() == ResourceArtifactType.ACTION) {
			if (isMutableResourceController) {
				Class<MutableResourceController> mutableResourceControllerClass = (Class<MutableResourceController>)resourceControllerClass;
				return linkTo(methodOn(mutableResourceControllerClass).artifactActionFieldOptionsFind(
						artifact.code(),
						resourceFieldName,
						null,
						null,
						null,
						null,
						null)).withRel(IanaLinkRelations.SELF_VALUE);
			} else {
				return null;
			}
		} else if (artifact.type() == ResourceArtifactType.REPORT) {
			return linkTo(methodOn(readonlyResourceControllerClass).artifactReportFieldOptionsFind(
					artifact.code(),
					resourceFieldName,
					null,
					null,
					null,
					null,
					null)).withRel(IanaLinkRelations.SELF_VALUE);
		} else if (artifact.type() == ResourceArtifactType.FILTER) {
			return linkTo(methodOn(readonlyResourceControllerClass).artifactFilterFieldOptionsFind(
					artifact.code(),
					resourceFieldName,
					null,
					null,
					null,
					null,
					null)).withRel(IanaLinkRelations.SELF_VALUE);
		} else {
			return null;
		}
	}

	private Optional<Class<ReadonlyResourceController>> findResourceControllerClass(
		Class<?> resourceClass,
		boolean mutable,
		Set<Class<ReadonlyResourceController>> resourceControllerClasses) {
		return resourceControllerClasses.stream().
			filter(rc -> {
				boolean mutableCheck = !mutable || MutableResourceController.class.isAssignableFrom(rc);
				Class<?> controllerResourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
					rc,
					mutable ? MutableResourceController.class : ReadonlyResourceController.class,
					0);
				return mutableCheck && controllerResourceClass.equals(resourceClass);
			}).findFirst();
	}

	private String getRemoteOptionsPromptField(Field field) {
		String descriptionField = null;
		ResourceField fieldAnnotation = field.getAnnotation(ResourceField.class);
		if (fieldAnnotation != null && !fieldAnnotation.descriptionField().isEmpty()) {
			descriptionField = fieldAnnotation.descriptionField();
		} else {
			Class<? extends Resource<?>> referencedResourceClass = TypeUtil.getReferencedResourceClass(field);
			ResourceConfig configAnnotation = referencedResourceClass.getAnnotation(ResourceConfig.class);
			if (configAnnotation != null && !configAnnotation.descriptionField().isEmpty()) {
				descriptionField = configAnnotation.descriptionField();
			} else {
				descriptionField = "id";
			}
		}
		return descriptionField;
	}

	@SneakyThrows
	private String getEnumFieldOptionId(Object enumValue) {
		return StringUtil.removeLeadingAndTrailingChars(objectMapper.writeValueAsString(enumValue), 1);
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class MutableHolder<T> {
		private T value;
	}

	@Getter
	@AllArgsConstructor
	public static class FieldOption {
		private String id;
		private String description;
	}

}
