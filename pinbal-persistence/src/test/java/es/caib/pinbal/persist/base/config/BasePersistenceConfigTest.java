package es.caib.pinbal.persist.base.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BasePersistenceConfigTest {

    private static class TestPersistenceConfig extends BasePersistenceConfig {
        private final DataSourceProperties dataSourceProperties = new DataSourceProperties();
        private final JpaHibernateProperties jpaHibernateProperties = new JpaHibernateProperties();

        @Override
        protected String[] getEntityPackages() {
            return new String[] {"es.caib.pinbal.persist.entity", "es.caib.pinbal.persist.audit"};
        }

        @Override
        public DataSourceProperties mainDataSourceProperties() {
            return dataSourceProperties;
        }

        @Override
        public JpaHibernateProperties jpaHibernateProperties() {
            return jpaHibernateProperties;
        }
    }

    private EntityManagerFactoryBuilder newBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
    }

    @Test
    public void testJpaHibernateProperties() {
        BasePersistenceConfig config = new TestPersistenceConfig();

        BasePersistenceConfig.JpaHibernateProperties properties = config.jpaHibernateProperties();

        assertNotNull(properties);
        properties.setHibernate(new HashMap<>());
        assertNotNull(properties.getHibernate());
    }

    @Test
    public void testMainDataSourceProperties() {
        BasePersistenceConfig config = new TestPersistenceConfig();

        DataSourceProperties properties = config.mainDataSourceProperties();

        assertNotNull(properties);
    }

    @Test
    public void testMainDataSource_UrlBased() {
        BasePersistenceConfig config = new TestPersistenceConfig();
        DataSourceProperties properties = config.mainDataSourceProperties();
        properties.setUrl("jdbc:h2:mem:basepersistenceconfigtest;DB_CLOSE_DELAY=-1");
        properties.setDriverClassName("org.h2.Driver");
        properties.setUsername("sa");
        properties.setPassword("");

        DataSource dataSource = config.mainDataSource();

        assertNotNull(dataSource);
    }

    @Test
    public void testMainDataSource_JndiLookupFails() {
        BasePersistenceConfig config = new TestPersistenceConfig();
        config.mainDataSourceProperties().setJndiName("java:comp/env/jdbc/inexistent");

        assertThrows(DataSourceLookupFailureException.class, config::mainDataSource);
    }

    @Test
    public void testMainEntityManager_UsesConfiguredHibernateProperties() {
        TestPersistenceConfig config = new TestPersistenceConfig();
        DataSourceProperties properties = config.mainDataSourceProperties();
        properties.setUrl("jdbc:h2:mem:basepersistenceconfigtest2;DB_CLOSE_DELAY=-1");
        properties.setDriverClassName("org.h2.Driver");
        properties.setUsername("sa");
        HashMap<String, String> hibernateProps = new HashMap<>();
        hibernateProps.put("hbm2ddl.auto", "create-drop");
        hibernateProps.put("dialect", "org.hibernate.dialect.H2Dialect");
        config.jpaHibernateProperties().setHibernate(hibernateProps);

        LocalContainerEntityManagerFactoryBean entityManager = config.mainEntityManager(newBuilder());

        assertNotNull(entityManager);
    }

    @Test
    public void testMainEntityManager_DefaultsHbm2ddlAutoWhenMissing() {
        TestPersistenceConfig config = new TestPersistenceConfig();
        DataSourceProperties properties = config.mainDataSourceProperties();
        properties.setUrl("jdbc:h2:mem:basepersistenceconfigtest3;DB_CLOSE_DELAY=-1");
        properties.setDriverClassName("org.h2.Driver");
        properties.setUsername("sa");

        LocalContainerEntityManagerFactoryBean entityManager = config.mainEntityManager(newBuilder());

        assertNotNull(entityManager);
    }

    @Test
    public void testMainTransactionManager_Jpa() {
        TestPersistenceConfig config = new TestPersistenceConfig();
        DataSourceProperties properties = config.mainDataSourceProperties();
        properties.setUrl("jdbc:h2:mem:basepersistenceconfigtest4;DB_CLOSE_DELAY=-1");
        properties.setDriverClassName("org.h2.Driver");
        properties.setUsername("sa");
        HashMap<String, String> hibernateProps = new HashMap<>();
        hibernateProps.put("hbm2ddl.auto", "create-drop");
        hibernateProps.put("dialect", "org.hibernate.dialect.H2Dialect");
        config.jpaHibernateProperties().setHibernate(hibernateProps);

        PlatformTransactionManager transactionManager = config.mainTransactionManager(newBuilder());

        assertTrue(transactionManager instanceof JpaTransactionManager);
    }

    @Test
    public void testMainTransactionManager_JtaWhenOnJboss() {
        TestPersistenceConfig config = new TestPersistenceConfig();
        ReflectionTestUtils.setField(config, "jbossHomeDir", "/opt/jboss");
        ReflectionTestUtils.setField(config, "containerTransactionsDisabled", false);

        PlatformTransactionManager transactionManager = config.mainTransactionManager(newBuilder());

        assertTrue(transactionManager instanceof JtaTransactionManager);
        assertEquals("java:/TransactionManager",
                ReflectionTestUtils.getField(transactionManager, "transactionManagerName"));
    }

    @Test
    public void testMainTransactionManager_JpaWhenContainerTransactionsDisabledEvenOnJboss() {
        TestPersistenceConfig config = new TestPersistenceConfig();
        DataSourceProperties properties = config.mainDataSourceProperties();
        properties.setUrl("jdbc:h2:mem:basepersistenceconfigtest5;DB_CLOSE_DELAY=-1");
        properties.setDriverClassName("org.h2.Driver");
        properties.setUsername("sa");
        HashMap<String, String> hibernateProps = new HashMap<>();
        hibernateProps.put("hbm2ddl.auto", "create-drop");
        hibernateProps.put("dialect", "org.hibernate.dialect.H2Dialect");
        config.jpaHibernateProperties().setHibernate(hibernateProps);
        ReflectionTestUtils.setField(config, "jbossHomeDir", "/opt/jboss");
        ReflectionTestUtils.setField(config, "containerTransactionsDisabled", true);

        PlatformTransactionManager transactionManager = config.mainTransactionManager(newBuilder());

        assertTrue(transactionManager instanceof JpaTransactionManager);
    }

    @Test
    public void testGetPersistenceUnitName() {
        TestPersistenceConfig config = new TestPersistenceConfig();

        assertEquals("main", ReflectionTestUtils.invokeMethod(config, "getPersistenceUnitName"));
    }
}
