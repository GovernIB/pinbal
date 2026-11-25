package es.caib.pinbal.scsp;

import es.scsp.common.dao.ServeiDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests de {@link ServeiDao#serveiFormatNamespacesScsp4(String)} amb la mateixa configuració que
 * {@link DaoTransaccioAnidadaTest} (current_session_context_class=thread): tant fora com dins d'una
 * transacció ja activa (cas del marshaller cridat des de ClienteUnico/PollingTask).
 */
public class ServeiDaoFormatNamespacesTest {

	private SessionFactory sessionFactory;
	private ServeiDao serveiDao;

	@Before
	public void configurar() throws Exception {
		Class.forName("org.h2.Driver");
		String url = "jdbc:h2:mem:serveiDaoFormatNs;DB_CLOSE_DELAY=-1;MODE=Oracle";
		try (Connection con = DriverManager.getConnection(url, "sa", "");
				Statement st = con.createStatement()) {
			st.execute("DROP TABLE IF EXISTS pbl_servei_config");
			st.execute("CREATE TABLE pbl_servei_config ("
					+ "servei_id VARCHAR(64) PRIMARY KEY, "
					+ "format_ns_scsp4 NUMBER(1) DEFAULT 0 NOT NULL)");
			st.execute("INSERT INTO pbl_servei_config (servei_id, format_ns_scsp4) VALUES ('SCDHPAJU', 1)");
			st.execute("INSERT INTO pbl_servei_config (servei_id) VALUES ('SCDCPAJU')");
		}
		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.applySetting("hibernate.connection.url", url)
				.applySetting("hibernate.connection.driver_class", "org.h2.Driver")
				.applySetting("hibernate.connection.username", "sa")
				.applySetting("hibernate.connection.password", "")
				.applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
				.applySetting("hibernate.hbm2ddl.auto", "none")
				.applySetting("hibernate.current_session_context_class", "thread")
				.build();
		sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		serveiDao = new ServeiDao();
		serveiDao.setSessionFactory(sessionFactory);
	}

	@After
	public void tancar() {
		sessionFactory.close();
	}

	@Test
	public void llegeixElValorDeLaConfiguracioDelServei() {
		assertTrue(serveiDao.serveiFormatNamespacesScsp4("SCDHPAJU"));
		assertFalse(serveiDao.serveiFormatNamespacesScsp4("SCDCPAJU"));
		assertFalse("Servei sense configuració", serveiDao.serveiFormatNamespacesScsp4("INEXISTENT"));
	}

	@Test
	public void funcionaDinsDUnaTransaccioJaActiva() {
		Session session = sessionFactory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		assertTrue(serveiDao.serveiFormatNamespacesScsp4("SCDHPAJU"));
		assertFalse(serveiDao.serveiFormatNamespacesScsp4("SCDCPAJU"));
		assertTrue("La transacció externa continua activa", tx.isActive());
		tx.commit();
	}

}
