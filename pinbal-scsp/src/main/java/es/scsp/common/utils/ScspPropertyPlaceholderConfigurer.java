/**
 * 
 */
package es.scsp.common.utils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import es.caib.pinbal.scsp.PropertiesHelper;
import es.scsp.common.domain.core.ParametroConfiguracion;

/**
 * PlaceholderConfigurer per a que les els beans de l'application
 * context de SCSP agafin els properties de la configuració de PINBAL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

	@Autowired
	@Qualifier("sessionFactory")
	public SessionFactory sessionFactoryManager;

	public String getProperty(String property) {
		String valor = PropertiesHelper.getProperties().getProperty(
				"es.caib.pinbal.scsp." + property);
		if (valor != null) {
			return valor;
		} else {
			Session session = this.sessionFactoryManager.openSession();
			Transaction t = session.beginTransaction();
			Criteria criteria = session.createCriteria(ParametroConfiguracion.class);
			criteria.add(Restrictions.like("nombre", property));
			ParametroConfiguracion result = (ParametroConfiguracion)criteria.uniqueResult();
			t.commit();
			session.close();
			if (result != null) {
				return result.getValor().trim();
			} else {
				return null;
			}
		}
	}

}
