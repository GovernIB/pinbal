package es.caib.pinbal.logic.intf.base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.pinbal.logic.intf.base.exception.ResourceFieldNotFoundException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitats per a dades en format JSON.
 * 
 * @author Límit Tecnologies
 */
@Component
public class JsonUtil implements ApplicationContextAware {

	@Autowired
	protected ObjectMapper objectMapper;

	public <T> T fromJsonToObjectWithType(
			JsonNode jsonNode,
			Class<T> targetType) throws JsonProcessingException {
		return objectMapper.treeToValue(
				jsonNode != null ? jsonNode : objectMapper.createObjectNode(),
				targetType);
	}

	public <T> Map<String, Object> fromJsonToMap(
			JsonNode jsonNode,
			Class<T> targetType) throws JsonProcessingException {
		if (jsonNode != null) {
			Map<String, Object> jsonMap = objectMapper.convertValue(
					jsonNode,
					new TypeReference<>(){});
			T jsonAsObject = fromJsonToObjectWithType(jsonNode, targetType);
			Map<String, Object> map = new HashMap<>();
			jsonMap.keySet().forEach(k -> {
				Field field = ReflectionUtils.findField(targetType, k);
				if (field != null) {
					if (!k.equals("id")) {
						ReflectionUtils.makeAccessible(field);
						Object value = ReflectionUtils.getField(field, jsonAsObject);
						map.put(k, value);
					} else {
						// Feim això perquè el camp id no es copia (no sé per què)
						map.put(k, jsonMap.get(k));
					}
				}
			});
			return map;
		}
		return null;
	}

	@SneakyThrows
	public Object fillResourceWithFieldsMap(
			Object resource,
			Map<String, Object> fields,
			String fieldName,
			JsonNode fieldValue) throws ResourceFieldNotFoundException {
		if (fields != null) {
			fields.forEach((k, v) -> {
				Field field = ReflectionUtils.findField(resource.getClass(), k);
				if (field != null) {
					ReflectionUtils.makeAccessible(field);
					ReflectionUtils.setField(field, resource, v);
				}
			});
		}
		Object fieldValueObject = null;
		if (fieldName != null) {
			Field field = ReflectionUtils.findField(resource.getClass(), fieldName);
			if (field != null) {
				ReflectionUtils.makeAccessible(field);
				String fieldJson = objectMapper.writeValueAsString(fieldValue);
				JsonNode jsonNode = objectMapper.readTree("{\"" + field.getName() + "\": " + fieldJson + "}");
				Object jsonResource = objectMapper.treeToValue(jsonNode, resource.getClass());
				fieldValueObject = ReflectionUtils.getField(field, jsonResource);
			} else {
				throw new ResourceFieldNotFoundException(resource.getClass(), fieldName);
			}
		}
		return fieldValueObject;
	}

	private static ApplicationContext applicationContext;
	public static JsonUtil getInstance() {
		return applicationContext.getBean(JsonUtil.class);
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		JsonUtil.applicationContext = applicationContext;
	}

}
