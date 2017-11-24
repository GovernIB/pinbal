/**
 * 
 */
package es.scsp.common.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import es.caib.pinbal.scsp.PropertiesHelper;
import es.scsp.common.domain.core.ParametroConfiguracion;

/**
 * Dao per a obtenir els paràmetres de la configuració de PINBAL amb
 * prioritat a damunt els de la taula SCSP CORE_PARAMETRO_CONFIGURACION.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ParametroConfiguracionDao extends BaseDao {

	private static final String PROP_PREFIX = "es.caib.pinbal.scsp.";

	public ParametroConfiguracion select(String nombre) {
		ParametroConfiguracion param = getParametroConfiguracionProperties(
				PROP_PREFIX + nombre);
		if (param == null) {
			Session session = getSessionFactory().getCurrentSession();
			Transaction tx = session.beginTransaction();
			Criteria c = session.createCriteria(ParametroConfiguracion.class);
			c.add(Restrictions.like("nombre", nombre));
			param = (ParametroConfiguracion)c.uniqueResult();
			tx.commit();
		}
		return param;
	}

	@SuppressWarnings("unchecked")
	public List<ParametroConfiguracion> select() {
		Session session = getSessionFactory().getCurrentSession();
		Transaction tx = session.beginTransaction();
		Criteria c = session.createCriteria(ParametroConfiguracion.class);
		c.addOrder(Order.asc("nombre"));
		List<ParametroConfiguracion> params = c.list();
		for (Object key: PropertiesHelper.getProperties().keySet()) {
			String keyStr = (String)key;
			if (keyStr.startsWith(PROP_PREFIX)) {
				params.add(getParametroConfiguracionProperties(keyStr));
			}
		}
		tx.commit();
		return params;
	}

	public void save(ParametroConfiguracion value) {
		Session session = getSessionFactory().getCurrentSession();
		Transaction tx = session.beginTransaction();
		Criteria c = session.createCriteria(ParametroConfiguracion.class);
		c.add(Restrictions.like("nombre", value.getNombre()));
		ParametroConfiguracion current = (ParametroConfiguracion)c.uniqueResult();
		if (current == null) {
			session.save(value);
		} else {
			current.setDescripcion(value.getDescripcion());
			current.setValor(value.getValor());
			session.update(current);
		}
		tx.commit();
	}



	private ParametroConfiguracion getParametroConfiguracionProperties(String propKey) {
		ParametroConfiguracion param = new ParametroConfiguracion();
		param.setNombre(propKey.substring(PROP_PREFIX.length()));
		param.setValor(PropertiesHelper.getProperties().getProperty(propKey));
		return param;
	}

}
