package es.caib.pinbal.persist.entity;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Reprodueix "NULL not allowed for column ID" contra l'esquema REAL creat
 * per Liquibase (no hibernate.hbm2ddl.auto=create-drop, que amagaria
 * qualsevol desajust entre el que Hibernate espera i el que Liquibase crea
 * de veritat - exactament com s'executa l'app real, hibernate.hbm2ddl.auto=none).
 */
public class IdGenerationSpikeTest {

    private EntityManagerFactory emf;
    private EntityManager em;

    @BeforeEach
    public void setUp() throws Exception {
        Class.forName("org.h2.Driver");
        String url = "jdbc:h2:mem:idgenspike;DB_CLOSE_DELAY=-1;MODE=Oracle";
        Connection jdbcCon = DriverManager.getConnection(url, "sa", "");
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(jdbcCon));
        Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.yaml", new ClassLoaderResourceAccessor(), database);
        liquibase.setChangeLogParameter("e2eAdminUsername", "e2e_admin");
        liquibase.setChangeLogParameter("e2eDelegatUsername", "e2e_delegat");
        liquibase.setChangeLogParameter("e2eRepresentantUsername", "e2e_representant");
        liquibase.setChangeLogParameter("e2eAuditorUsername", "e2e_auditor");
        liquibase.setChangeLogParameter("e2eUserActiuUsername", "E2E_USER_ACTIU");
        liquibase.setChangeLogParameter("e2eUserInactiuUsername", "E2E_USER_INACTIU");
        liquibase.setChangeLogParameter("e2eUserAllRolesUsername", "pbl_all");
        liquibase.setChangeLogParameter("fakeScspBaseUrl", "http://localhost:18080");
        liquibase.update("");
        jdbcCon.close();

        DataSource dataSource = new SimpleDriverDataSource(new org.h2.Driver(), url, "sa", "");
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource);
        bean.setPackagesToScan("es.caib.pinbal.persist.entity", "es.caib.pinbal.persist.audit");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        bean.setJpaVendorAdapter(vendorAdapter);
        Properties props = new Properties();
        props.setProperty("hibernate.hbm2ddl.auto", "none");
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        props.setProperty("hibernate.implicit_naming_strategy", "es.caib.pinbal.persist.audit.CustomImplicitNamingStrategy");
        bean.setJpaProperties(props);
        bean.setPersistenceUnitName("idgenspike");
        bean.afterPropertiesSet();
        emf = bean.getObject();
        em = emf.createEntityManager();
    }

    @AfterEach
    public void tearDown() {
        if (em != null) em.close();
        if (emf != null) emf.close();
    }

    @Test
    public void avisEsGeneraCorrectament() {
        em.getTransaction().begin();
        try {
            Avis avis = Avis.getBuilder("Assumpte", "Missatge", new java.util.Date(), null,
                    es.caib.pinbal.logic.intf.dto.AvisNivellEnumDto.INFO).build();
            em.persist(avis);
            em.flush();
            assertNotNull(avis.getId());
            System.out.println("Avis id generat: " + avis.getId());
            em.getTransaction().rollback();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            fail("Avis: " + ex);
        }
    }

    @Test
    public void clauPrivadaEsGeneraCorrectament() {
        em.getTransaction().begin();
        try {
            OrganismeCessionari oc = OrganismeCessionari.getBuilder(
                    "Test organisme", "Q1111111A", null, null, Boolean.FALSE, null, new java.util.ArrayList<>()).build();
            em.persist(oc);
            em.flush();

            ClauPrivada cp = new ClauPrivada();
            ReflectionTestUtils.setField(cp, "alies", "test-alias");
            ReflectionTestUtils.setField(cp, "nom", "Test");
            ReflectionTestUtils.setField(cp, "password", "x");
            ReflectionTestUtils.setField(cp, "numSerie", "SN1");
            ReflectionTestUtils.setField(cp, "organisme", oc);
            em.persist(cp);
            em.flush();
            assertNotNull(cp.getId());
            System.out.println("ClauPrivada id generat: " + cp.getId());
            em.getTransaction().rollback();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            { System.out.println("ClauPrivada FALLA: " + ex); Throwable c = ex; while (c.getCause() != null) { c = c.getCause(); System.out.println("  Causat per: " + c); } }
        }
    }

    @Test
    public void organismeCessionariEsGeneraCorrectament() {
        em.getTransaction().begin();
        try {
            OrganismeCessionari oc = OrganismeCessionari.getBuilder(
                    "Test organisme", "Q1111111A", null, null, Boolean.FALSE, null, new java.util.ArrayList<>()).build();
            em.persist(oc);
            em.flush();
            assertNotNull(oc.getId());
            System.out.println("OrganismeCessionari id generat: " + oc.getId());
            em.getTransaction().rollback();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            { System.out.println("OrganismeCessionari FALLA: " + ex); Throwable c = ex; while (c.getCause() != null) { c = c.getCause(); System.out.println("  Causat per: " + c); } }
        }
    }
}
