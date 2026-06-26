/**
 * 
 */
package es.caib.pinbal.ejb;

import org.springframework.context.annotation.Primary;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * Implementació de PropertyService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Primary
@Stateless
public class PropertyService extends AbstractService<es.caib.pinbal.logic.intf.service.PropertyService> implements es.caib.pinbal.logic.intf.service.PropertyService {


	@Override
	public String get(String key) {
		return getDelegateService().get(key);
	}

    @Override
    public String get(String key, String defaultValue) {
        return getDelegateService().get(key, defaultValue);
    }

}
