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

	public void delete(Object o) {
		Session session = this.sessionFactory.getCurrentSession();
	    Transaction tx = session.beginTransaction();
	    session.delete(o);
	    tx.commit();
	}

}
