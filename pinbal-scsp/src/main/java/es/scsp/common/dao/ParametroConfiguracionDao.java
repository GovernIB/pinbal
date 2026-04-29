/**
 * 
 */
package es.scsp.common.dao;

import es.caib.pinbal.scsp.PropertiesHelper;
import es.scsp.common.domain.core.ParametroConfiguracion;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Dao per a obtenir els paràmetres de la configuració de PINBAL amb
 * prioritat a damunt els de la taula SCSP CORE_PARAMETRO_CONFIGURACION.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ParametroConfiguracionDao extends BaseDao<ParametroConfiguracion> {

    /* MOD PBL */ private static final String PROP_PREFIX = "es.caib.pinbal.scsp.";

    @PostConstruct
    public void initme() {
        this.clazz = ParametroConfiguracion.class;
    }

	public ParametroConfiguracion select(String nombre) {
        /* MOD PBL */ ParametroConfiguracion param = getParametroConfiguracionProperties(PROP_PREFIX + nombre);
        /* MOD PBL */ if (param != null) return param;
        return (ParametroConfiguracion)this.selectEquals("nombre", nombre);
	}

	public List<ParametroConfiguracion> select() {
        List<ParametroConfiguracion> params = this.selectAll();
		/* MOD PBL */ for (Object key: PropertiesHelper.getProperties().keySet()) {
		/* MOD PBL */   String keyStr = (String)key;
		/* MOD PBL */ 	if (keyStr.startsWith(PROP_PREFIX)) {
		/* MOD PBL */ 		params.add(getParametroConfiguracionProperties(keyStr));
		/* MOD PBL */ 	}
		/* MOD PBL */ }
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


	/* MOD PBL */ private ParametroConfiguracion getParametroConfiguracionProperties(String propKey) {
	/* MOD PBL */ 	String propValor = PropertiesHelper.getProperties().getProperty(propKey);
	/* MOD PBL */ 	if (propValor != null) {
	/* MOD PBL */ 		ParametroConfiguracion param = new ParametroConfiguracion();
	/* MOD PBL */ 		param.setNombre(propKey.substring(PROP_PREFIX.length()));
	/* MOD PBL */ 		param.setValor(PropertiesHelper.getProperties().getProperty(propKey));
	/* MOD PBL */ 		return param;
	/* MOD PBL */ 	} else {
	/* MOD PBL */ 		return null;
	/* MOD PBL */ 	}
	/* MOD PBL */ }

}
