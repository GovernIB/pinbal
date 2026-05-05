package es.caib.pinbal.logic.intf.base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.pinbal.logic.intf.base.exception.CompositePkParsingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Base64;
import java.util.UUID;

/**
 * Utilitats per a serialitzar i deserialitzar claus primàries compostes.
 *
 * @author Límit Tecnologies
 */
@Component
public class CompositePkUtil implements ApplicationContextAware {

	@Autowired
	protected ObjectMapper objectMapper;

	public <CPK extends Serializable> CPK getCompositePkFromSerializedId(
			String id,
			Class<?> pkClass) {
		if (id != null) {
			try {
				byte[] idBase64 = id.getBytes();
				return (CPK)objectMapper.readValue(
						new String(Base64.getDecoder().decode(idBase64)),
						pkClass);
			} catch (JsonProcessingException ex) {
				throw new CompositePkParsingException(id, null, pkClass, ex);
			}
		} else {
			return null;
		}
	}

	public String getSerializedIdFromCompositePk(Serializable pk) {
		if (pk != null) {
			try {
				byte[] idBase64 = Base64.getEncoder().encode(objectMapper.writeValueAsString(pk).getBytes());
				return new String(idBase64);
			} catch (JsonProcessingException ex) {
				throw new CompositePkParsingException(null, pk, pk.getClass(), ex);
			}
		} else {
			return null;
		}
	}

	public boolean isCompositePkClass(Class<?> clazz) {
		// TODO potser seria millor obligar a que totes les claus primàries estenguessin d'una interfície comuna
		// TODO per a fer aquesta comprovació
		return !(
				Serializable.class.isAssignableFrom(clazz) ||
						String.class.isAssignableFrom(clazz) ||
						Number.class.isAssignableFrom(clazz) ||
						UUID.class.isAssignableFrom(clazz));
	}

	private static ApplicationContext applicationContext;
	public static CompositePkUtil getInstance() {
		return applicationContext.getBean(CompositePkUtil.class);
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		CompositePkUtil.applicationContext = applicationContext;
	}

}
