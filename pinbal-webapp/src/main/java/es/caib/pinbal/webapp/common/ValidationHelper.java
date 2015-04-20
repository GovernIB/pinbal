/**
 * 
 */
package es.caib.pinbal.webapp.common;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.ConstraintDescriptor;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

/**
 * Utilitat per a mostrar missatges d'alerta o informació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidationHelper {

	private Validator validator;

	public ValidationHelper(Validator validator) {
		this.validator = validator;
	}

	public boolean isValid(
			Object target,
			Errors errors,
			Class<?>... groups) {
		Set<ConstraintViolation<Object>> result = validator.validate(target, groups);
		for (ConstraintViolation<Object> violation : result) {
			String field = violation.getPropertyPath().toString();
			FieldError fieldError = errors.getFieldError(field);
			if (fieldError == null || !fieldError.isBindingFailure()) {
				ConstraintDescriptor<?> constraintDescriptor = violation.getConstraintDescriptor();
				errors.rejectValue(
						field,
						constraintDescriptor.getAnnotation().annotationType().getSimpleName(),
						getArgumentsForConstraint(
								errors.getObjectName(),
								field,
								constraintDescriptor),
						violation.getMessage());
			}
		}
		return !errors.hasErrors();
	}

	private Object[] getArgumentsForConstraint(String objectName, String field, ConstraintDescriptor<?> descriptor) {
		List<Object> arguments = new LinkedList<Object>();
		String[] codes = new String[] { objectName + Errors.NESTED_PATH_SEPARATOR + field, field };
		arguments.add(new DefaultMessageSourceResolvable(codes, field));
		Map<String, Object> attributesToExpose = new TreeMap<String, Object>();
		for (Map.Entry<String, Object> entry : descriptor.getAttributes().entrySet()) {
			String attributeName = entry.getKey();
			Object attributeValue = entry.getValue();
			if ("message".equals(attributeName) || "groups".equals(attributeName) || "payload".equals(attributeName)) {
				attributesToExpose.put(attributeName, attributeValue);
			}
		}
		arguments.addAll(attributesToExpose.values());
		return arguments.toArray(new Object[arguments.size()]);
	}

}
