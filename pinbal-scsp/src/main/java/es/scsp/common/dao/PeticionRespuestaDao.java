//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package es.scsp.common.dao;

import es.scsp.common.domain.core.ParametroConfiguracion;
import es.scsp.common.domain.core.PeticionRespuesta;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.domain.core.Transmision;
import es.scsp.common.exceptions.ScspException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class PeticionRespuestaDao extends BaseDao {
    private static final Log LOG = LogFactory.getLog(PeticionRespuestaDao.class);
    private static final String ID_PETICION = "idPeticion";
    private static final String SERVICIO = "servicio";

    public PeticionRespuestaDao() {
    }

    public PeticionRespuesta select(String idPeticion) throws ScspException {
        Session session = this.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        Criteria c = session.createCriteria(PeticionRespuesta.class);
        c.add(Restrictions.eq("idPeticion", idPeticion));
        PeticionRespuesta peticionRespuesta = (PeticionRespuesta)c.uniqueResult();
        tx.commit();
        return peticionRespuesta;
    }

    public List<PeticionRespuesta> select(String idpeticion, Servicio servicio) throws ScspException {
        Session session = this.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        Criteria c = session.createCriteria(PeticionRespuesta.class);
        if (servicio != null) {
            c.add(Restrictions.eq("servicio", servicio));
        }

        if (idpeticion != null && !"".equals(idpeticion)) {
            c.add(Restrictions.eq("idPeticion", idpeticion));
        }

        List<PeticionRespuesta> peticiones = c.list();
        tx.commit();
        return peticiones;
    }

    public List<PeticionRespuesta> selectlike(String idpeticion, Servicio servicio) throws ScspException {
        Session session = this.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        Criteria c = session.createCriteria(PeticionRespuesta.class);
        if (servicio != null) {
            c.add(Restrictions.like("servicio", servicio));
        }

        if (idpeticion != null && !"".equals(idpeticion)) {
            c.add(Restrictions.like("idPeticion", "%" + idpeticion + "%"));
        }

        c.add(Restrictions.not(Restrictions.eq("estado", "0002")));
        List<PeticionRespuesta> peticiones = c.list();
        tx.commit();
        return peticiones;
    }

    public void save(PeticionRespuesta peticionRespuesta) throws ScspException {
        Session session = this.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(peticionRespuesta);
        session.flush();
        tx.commit();
    }

    public List<PeticionRespuesta> selectByEstado(String estado, Date date) {
        Session session = this.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        Criteria c = session.createCriteria(PeticionRespuesta.class);
        c.add(Restrictions.like("estado", estado));
        c.add(Restrictions.lt("ter", date));
        c.addOrder(Order.asc("fechaUltimoSondeo"));
        List<PeticionRespuesta> list = c.list();
        tx.commit();
        return list;
    }

    public List<PeticionRespuesta> selectToPolling(String estado, Date date, int numeroPeticiones) throws ScspException {
        LOG.debug("Recuperando peticiones para procesamiento en polling");
        Session session = this.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        Criteria cmutex = session.createCriteria(ParametroConfiguracion.class);
        cmutex.add(Restrictions.eq("nombre", "task.polling.intervalo"));
        cmutex.setLockMode(LockMode.PESSIMISTIC_WRITE);
        cmutex.list();
        Criteria c = session.createCriteria(PeticionRespuesta.class);
        c.add(Restrictions.like("estado", estado));
        c.add(Restrictions.lt("ter", date));
        c.addOrder(Order.asc("fechaUltimoSondeo"));
        c.addOrder(Order.asc("fechaPeticion"));
        c.setMaxResults(numeroPeticiones);
        List<PeticionRespuesta> list = c.list();

        for(PeticionRespuesta pet : list) {
            pet.setEstado("0004");
            session.save(pet);
        }

        tx.commit();
        return list;
    }

    public List<PeticionRespuesta> selectByEstado(String estado, Date date, Servicio servicio) {
        Session session = this.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        Criteria c = session.createCriteria(PeticionRespuesta.class);
        c.add(Restrictions.eq("estado", estado));
        c.add(Restrictions.eq("servicio", servicio));
        c.add(Restrictions.lt("ter", date));
        c.addOrder(Order.asc("fechaUltimoSondeo"));
        List<PeticionRespuesta> list = c.list();
        tx.commit();
        return list;
    }

    public Long count(Servicio servicio) {
        Session session = this.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        String hql = String.format("select count(*) as n from PeticionRespuesta pr where servicio.id = '%s'", servicio.getId());
        Query query = session.createQuery(hql);
        Long count = (Long)query.uniqueResult();
        tx.commit();
        return count;
    }

    public int delete(Servicio servicio) {
        Session session = this.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        String hql = String.format("delete from PeticionRespuesta where servicio.id = '%s'", servicio.getId());
        Query query = session.createQuery(hql);
        int count = query.executeUpdate();
        tx.commit();
        return count;
    }

    public List<PeticionRespuesta> select(Servicio servicio, String docFuncionacio, String from, String to, String codigoProcedimiento, int primerRegistro, int numResultados) throws ScspException {
        Session session = this.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        Criteria criteria = this.getCriteriaParams(session, servicio, docFuncionacio, from, to, codigoProcedimiento);
        if (numResultados != 0) {
            criteria.setFirstResult(primerRegistro);
            criteria.setMaxResults(numResultados);
        }

        List<PeticionRespuesta> peticiones = criteria.list();
        tx.commit();
        return peticiones;
    }

    public long count(Servicio servicio, String docFuncionacio, String from, String to, String codigoProcedimiento) throws ScspException {
        Session session = this.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        Criteria criteria = this.getCriteriaParams(session, servicio, docFuncionacio, from, to, codigoProcedimiento);
        long numbers = (Long)criteria.setProjection(Projections.rowCount()).uniqueResult();
        tx.commit();
        return numbers;
    }

    private Criteria getCriteriaParams(Session session, Servicio servicio, String docFuncionacio, String from, String to, String codigoProcedimiento) throws ScspException {
        DetachedCriteria criteriaTrans = DetachedCriteria.forClass(Transmision.class).setProjection(Property.forName("peticion"));
        if (codigoProcedimiento != null && !"".equals(codigoProcedimiento)) {
            criteriaTrans.add(Restrictions.eq("codigoProcedimiento", codigoProcedimiento));
        }

        if (docFuncionacio != null && !"".equals(docFuncionacio)) {
            criteriaTrans.add(Restrictions.eq("docFuncionario", docFuncionacio));
        }

        DetachedCriteria criteriaServicio = DetachedCriteria.forClass(Servicio.class).setProjection(Property.forName("certificado"));
        if (servicio != null) {
            criteriaServicio.add(Restrictions.eq("codCertificado", servicio.getCodCertificado()));
        }

        Criteria criteria = session.createCriteria(PeticionRespuesta.class).add(Property.forName("idPeticion").in(criteriaTrans)).add(Property.forName("servicio").in(criteriaServicio));
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        try {
            if (from != null && !"".equals(from)) {
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                Date fromDate = format.parse(from);
                criteria.add(Restrictions.ge("fechaGeneracion", fromDate));
            }

            if (to != null && !"".equals(to)) {
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                Date toDate = format.parse(to);
                criteria.add(Restrictions.le("fechaGeneracion", toDate));
            }

            return criteria;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new ScspException("Error en el formato de las fechas establecidas para la consulta :" + e.getMessage(), "0342");
        }
    }
}
