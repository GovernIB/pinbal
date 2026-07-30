package es.caib.pinbal.scsp;

import es.scsp.common.dao.EstadoDao;
import es.scsp.common.domain.core.EstadoPeticion;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Reprodueix el bug real (javax.ejb.EJBException: java.lang.IllegalStateException:
 * "Transaction already active") i verifica el fix aplicat a totes les DAO de
 * es.scsp.common.dao (vegeu el comentari a BaseDao): una crida a una DAO des
 * de dins d'una transaccio ja activa (com la de Spring @Transactional d'un
 * servei que crida ScspHelper) no ha de tornar a obrir transaccio, sino
 * participar en l'existent.
 *
 * Fa servir hibernate.current_session_context_class=thread perque es
 * exactament la configuracio real (getSessionFactory().getCurrentSession()
 * es la crida que fan totes les DAO), i EstadoDao/EstadoPeticion perque son
 * l'entitat mes simple (sense associacions) que exercita tant el path amb
 * save() com el de selectEquals-com-Criteria heretat de BaseDao.
 */
public class DaoTransaccioAnidadaTest {

    private SessionFactory buildSessionFactory(String dbName) throws Exception {
        Class.forName("org.h2.Driver");
        String url = "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1;MODE=Oracle";
        Connection jdbcCon = DriverManager.getConnection(url, "sa", "");
        try (Statement st = jdbcCon.createStatement()) {
            st.execute("CREATE TABLE core_estado_peticion (" +
                    "codigo VARCHAR(4) PRIMARY KEY, " +
                    "mensaje VARCHAR(256) NOT NULL)");
        }
        jdbcCon.close();

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.url", url)
                .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
                .applySetting("hibernate.connection.username", "sa")
                .applySetting("hibernate.connection.password", "")
                .applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .applySetting("hibernate.hbm2ddl.auto", "none")
                .applySetting("hibernate.current_session_context_class", "thread")
                .build();
        return new MetadataSources(registry)
                .addAnnotatedClass(EstadoPeticion.class)
                .buildMetadata()
                .buildSessionFactory();
    }

    @Test
    public void daoStandaloneObreITancaLaSevaPropiaTransaccio() throws Exception {
        SessionFactory sessionFactory = buildSessionFactory("daoStandalone");
        try {
            EstadoDao dao = new EstadoDao();
            dao.setSessionFactory(sessionFactory);

            EstadoPeticion e = new EstadoPeticion();
            e.setCodigo("T1");
            e.setMensaje("standalone");
            dao.save(e);

            try (Session verify = sessionFactory.openSession()) {
                EstadoPeticion loaded = verify.get(EstadoPeticion.class, "T1");
                assertNotNull("l'entitat s'hauria d'haver persistit amb la transaccio propia de la DAO", loaded);
                assertEquals("standalone", loaded.getMensaje());
            }
        } finally {
            sessionFactory.close();
        }
    }

    @Test
    public void daoDinsTransaccioJaActivaNoLlencaTransactionAlreadyActive() throws Exception {
        SessionFactory sessionFactory = buildSessionFactory("daoAnidada");
        try {
            EstadoDao dao = new EstadoDao();
            dao.setSessionFactory(sessionFactory);

            // Simula el context real: un servei Spring @Transactional obre la
            // transaccio abans de cridar la DAO (via ScspHelper).
            Session outerSession = sessionFactory.getCurrentSession();
            Transaction outerTx = outerSession.beginTransaction();

            EstadoPeticion e = new EstadoPeticion();
            e.setCodigo("T2");
            e.setMensaje("anidada");
            // Sense el fix, aquesta crida faria session.beginTransaction() amb
            // una transaccio ja activa i llencaria IllegalStateException.
            dao.save(e);

            // Segona crida a la mateixa DAO dins la mateixa transaccio activa,
            // reproduint el patró real de ScspHelper (múltiples crides a DAO
            // dins un sol metode @Transactional).
            EstadoPeticion e2 = new EstadoPeticion();
            e2.setCodigo("T3");
            e2.setMensaje("anidada2");
            dao.save(e2);

            outerTx.commit();

            try (Session verify = sessionFactory.openSession()) {
                EstadoPeticion loaded = verify.get(EstadoPeticion.class, "T2");
                assertNotNull("l'entitat s'hauria d'haver persistit quan es fa commit de la transaccio externa", loaded);
                assertEquals("anidada", loaded.getMensaje());

                EstadoPeticion loaded2 = verify.get(EstadoPeticion.class, "T3");
                assertNotNull("la segona crida dins la mateixa transaccio tambe s'ha de persistir", loaded2);
                assertEquals("anidada2", loaded2.getMensaje());
            }
        } finally {
            sessionFactory.close();
        }
    }
}
