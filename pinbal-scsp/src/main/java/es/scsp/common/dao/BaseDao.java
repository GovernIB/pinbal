package es.scsp.common.dao;

import es.caib.pinbal.scsp.NestedTransactionSessionFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Override local (pinbal-scsp) de es.scsp.common.dao.BaseDao.
 *
 * L'unic canvi respecte l'original es que el SessionFactory que Spring
 * injecta a setSessionFactory() es embolcalla amb
 * NestedTransactionSessionFactory (vegeu el seu Javadoc per al detall del
 * problema que soluciona: totes les DAO d'es.scsp.common.dao criden sempre
 * session.beginTransaction() sense comprovar si ja n'hi ha una activa,
 * p.ex. la de Spring @Transactional del servei que crida ScspHelper, i
 * llencen IllegalStateException("Transaction already active")). Amb el
 * wrapper aplicat aqui, TOTES les DAO (aquesta i qualsevol subclasse,
 * sobreescrita o no per pinbal-scsp) queden protegides sense haver de tocar
 * el seu codi, ja que totes obtenen la sessio via
 * this.getSessionFactory().getCurrentSession().
 */
@Component
public abstract class BaseDao<T extends Serializable> {
    private static final Log LOG = LogFactory.getLog(BaseDao.class);
    private SessionFactory sessionFactory;
    protected Class<T> clazz;

    @Autowired
    @Qualifier("sessionFactory")
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = NestedTransactionSessionFactory.wrap(sessionFactory);
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public void closeSession() {
        this.sessionFactory.getCurrentSession().close();
    }

    public T selectEquals(String attribute, Object argument) {
        Transaction tx = null;
        Serializable o = null;
        try {
            Session session = this.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery criteria = builder.createQuery(this.clazz);
            Root root = criteria.from(this.clazz);
            criteria.select((Selection) root).where((Expression) builder.equal((Expression) root.get(attribute), argument));
            Query query = session.createQuery(criteria);
            o = (Serializable) query.getSingleResult();
        } catch (NoResultException no) {
            LOG.warn((Object) ("No existe " + attribute + "=" + argument));
        } catch (NonUniqueResultException nuq) {
            LOG.error((Object) ("Duplicado registro " + attribute + "=" + argument), (Throwable) nuq);
        } catch (Exception e) {
            LOG.error((Object) "Error basedao", (Throwable) e);
        } finally {
            tx.commit();
        }
        return (T) o;
    }

    public T selectEqualsActive(String attribute, Object argument, String fechaBaja) {
        Transaction tx = null;
        Serializable o = null;
        try {
            Session session = this.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery criteria = builder.createQuery(this.clazz);
            Root root = criteria.from(this.clazz);
            criteria.select((Selection) root).where(new Predicate[]{builder.equal((Expression) root.get(attribute), argument), builder.isNull((Expression) root.get(fechaBaja))});
            Query query = session.createQuery(criteria);
            o = (Serializable) query.getSingleResult();
        } catch (NoResultException no) {
            LOG.warn((Object) ("No existe " + attribute + "=" + argument + " con " + fechaBaja + " a null"));
        } catch (NonUniqueResultException nuq) {
            LOG.error((Object) ("Duplicado registro " + attribute + "=" + argument + " con " + fechaBaja + " a null"));
        } catch (Exception e) {
            LOG.error((Object) "Error basedao", (Throwable) e);
        } finally {
            tx.commit();
        }
        return (T) o;
    }

    public List<T> selectAll() {
        Transaction tx = null;
        List o = null;
        try {
            Session session = this.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery criteria = builder.createQuery(this.clazz);
            criteria.from(this.clazz);
            Query query = session.createQuery(criteria);
            o = query.getResultList();
        } catch (NoResultException no) {
            LOG.warn((Object) "Tabla vacía: NO RESULTADOS!");
        } catch (Exception e) {
            LOG.error((Object) "Error basedao", (Throwable) e);
        } finally {
            tx.commit();
        }
        return o;
    }

    public void delete(T cp) {
        Session session = this.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        session.delete(cp);
        tx.commit();
    }

    public void save(T o) {
        Session session = this.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        session.save(o);
        tx.commit();
    }

    public T selectOne(InnerCriteria innerCriteria) {
        Serializable r = null;
        Transaction tx = null;
        try {
            Session session = this.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();
            CriteriaBuilder builder = this.getSessionFactory().getCurrentSession().getCriteriaBuilder();
            CriteriaQuery criteria = builder.createQuery(this.clazz);
            Root root = criteria.from(this.clazz);
            criteria = criteria.select((Selection) root);
            if (!innerCriteria.parameters.isEmpty()) {
                Predicate[] predicates = new Predicate[innerCriteria.parameters.size()];
                int index = 0;
                for (String atr : innerCriteria.parameters.keySet()) {
                    Object o = innerCriteria.parameters.get(atr);
                    Operator op = innerCriteria.operators.get(index);
                    if (op.equals(Operator.NOTNULL)) {
                        Assert.isNull(o, "Para operator not null no debe pasarse un objeto para el criteria");
                        predicates[index] = builder.isNotNull((Expression) root.get(atr));
                    }
                    if (op.equals(Operator.NULL)) {
                        Assert.isNull(o, "Para operator not null no debe pasarse un objeto para el criteria");
                        predicates[index] = builder.isNotNull((Expression) root.get(atr));
                    }
                    if (op.equals(Operator.EQ)) {
                        Assert.isTrue(o != null, "Para operator EQUALS debe pasarse un objeto para la comparacion en el criteria");
                        predicates[index] = builder.equal((Expression) root.get(atr), o);
                    }
                    if (op.equals(Operator.LIKE)) {
                        Assert.isTrue(o != null, "Para operator LIKE debe pasarse un objeto para la comparacion en el criteria");
                        Assert.isTrue(o instanceof String, "Para operator LIKE debe pasarse un objeto de tipo String");
                        predicates[index] = builder.like((Expression) root.get(atr), "%" + (String) o + "%");
                    }
                    ++index;
                }
                criteria = criteria.where(predicates);
            }
            if (innerCriteria.getOrderField() != null) {
                criteria = innerCriteria.isAsc() ? criteria.orderBy(new Order[]{builder.asc((Expression) root.get(innerCriteria.getOrderField()))}) : criteria.orderBy(new Order[]{builder.desc((Expression) root.get(innerCriteria.getOrderField()))});
            }
            Query query = session.createQuery(criteria);
            r = (Serializable) query.getSingleResult();
        } catch (NoResultException no) {
            LOG.warn((Object) ("No existe " + innerCriteria.toString()));
        } catch (NonUniqueResultException nuq) {
            LOG.error((Object) ("Duplicado registro " + innerCriteria.toString()), (Throwable) nuq);
        } catch (Exception e) {
            LOG.error((Object) "Error basedao", (Throwable) e);
        } finally {
            tx.commit();
        }
        return (T) r;
    }

    public static InnerCriteria makeCriteria() {
        return new InnerCriteria();
    }

    public String toString() {
        return "BaseDao [sessionFactory=" + this.sessionFactory + ", clazz=" + this.clazz + "]";
    }

    public enum Operator {
        EQ,
        GT,
        LT,
        LIKE,
        NOTNULL,
        NULL
    }

    public static class InnerCriteria {
        private Map<String, Object> parameters = new HashMap<String, Object>();
        private String orderField;
        private boolean asc;
        private List<Operator> operators = new ArrayList<Operator>();

        InnerCriteria add(String attribute, Object value, Operator operator) {
            this.parameters.put(attribute, value);
            this.operators.add(operator);
            return this;
        }

        InnerCriteria addOrder(String field, boolean asc) {
            this.asc = asc;
            this.orderField = field;
            return this;
        }

        public Map<String, Object> getParameters() {
            return this.parameters;
        }

        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }

        public String getOrderField() {
            return this.orderField;
        }

        public void setOrderField(String orderField) {
            this.orderField = orderField;
        }

        public boolean isAsc() {
            return this.asc;
        }

        public void setAsc(boolean asc) {
            this.asc = asc;
        }

        public List<Operator> getOperators() {
            return this.operators;
        }

        public void setOperators(List<Operator> operators) {
            this.operators = operators;
        }
    }
}
