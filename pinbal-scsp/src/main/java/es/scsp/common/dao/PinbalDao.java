/**
 * 
 */
package es.scsp.common.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

/**
 * Dao per a esborrar registres de les taules SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PinbalDao extends BaseDao {

	public void save(Object o) {
		Session session = getSessionFactory().getCurrentSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(o);
		tx.commit();
	}

	public void delete(Object o) {
		Session session = getSessionFactory().getCurrentSession();
	    Transaction tx = session.beginTransaction();
	    session.delete(o);
	    tx.commit();
	}

}
