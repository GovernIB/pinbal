package es.caib.pinbal.scsp;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Embolcalla un SessionFactory perque getCurrentSession().beginTransaction()
 * sigui segur quan ja hi ha una transaccio activa (per exemple la de Spring
 * @Transactional del servei que crida una DAO d'es.scsp.common.dao a traves
 * de ScspHelper). Sense aixo, aquestes DAO (patro begin/commit sense
 * comprovar l'estat previ) llencen IllegalStateException("Transaction
 * already active"), deixant la sessio Hibernate d'aquell fil de JBoss
 * corrupta per a qualsevol peticio posterior no relacionada atesa pel mateix
 * fil (confirmat amb dues traces reals independents: ScspHelper.getServicio
 * i ScspHelper.findServicioAll, totes dues cridades des de metodes
 * @Transactional de ServeiServiceImpl).
 *
 * En lloc de modificar cada metode de cada DAO d'es.scsp.common.dao, la
 * seguretat s'aplica un sol cop aqui, a la sessio que BaseDao obte de
 * getSessionFactory().getCurrentSession(): quan ja hi ha una transaccio
 * activa, beginTransaction() en retorna una que "participa" en l'existent en
 * lloc d'obrir-ne una de nova; en delega tots els metodes excepte
 * commit()/rollback(), que fan no-op, ja que la transaccio real nomes l'ha
 * de tancar qui la va obrir originalment (l'@Transactional de Spring, o la
 * primera DAO de la cadena de crides).
 */
public final class NestedTransactionSessionFactory {

    private NestedTransactionSessionFactory() {
    }

    public static SessionFactory wrap(SessionFactory target) {
        return (SessionFactory) Proxy.newProxyInstance(
                SessionFactory.class.getClassLoader(),
                new Class<?>[]{SessionFactory.class},
                new SessionFactoryInvocationHandler(target));
    }

    private static Session wrapSession(Session target) {
        return (Session) Proxy.newProxyInstance(
                Session.class.getClassLoader(),
                new Class<?>[]{Session.class},
                new SessionInvocationHandler(target));
    }

    private static Transaction wrapParticipantTransaction(Transaction target) {
        return (Transaction) Proxy.newProxyInstance(
                Transaction.class.getClassLoader(),
                new Class<?>[]{Transaction.class},
                new ParticipantTransactionInvocationHandler(target));
    }

    private static Object invokeDelegate(Object target, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private static class SessionFactoryInvocationHandler implements InvocationHandler {
        private final SessionFactory target;

        SessionFactoryInvocationHandler(SessionFactory target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = invokeDelegate(target, method, args);
            if ("getCurrentSession".equals(method.getName()) && result instanceof Session) {
                return wrapSession((Session) result);
            }
            return result;
        }
    }

    private static class SessionInvocationHandler implements InvocationHandler {
        private final Session target;

        SessionInvocationHandler(Session target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("beginTransaction".equals(method.getName()) && (args == null || args.length == 0)) {
                Transaction current = target.getTransaction();
                if (current != null && current.isActive()) {
                    return wrapParticipantTransaction(current);
                }
                return target.beginTransaction();
            }
            return invokeDelegate(target, method, args);
        }
    }

    private static class ParticipantTransactionInvocationHandler implements InvocationHandler {
        private final Transaction target;

        ParticipantTransactionInvocationHandler(Transaction target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if ("commit".equals(name) || "rollback".equals(name)) {
                return null;
            }
            return invokeDelegate(target, method, args);
        }
    }
}
