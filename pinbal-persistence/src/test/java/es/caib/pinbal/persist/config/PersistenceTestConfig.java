package es.caib.pinbal.persist.config;

import es.caib.pinbal.persist.base.repository.BaseRepositoryImpl;
import es.caib.pinbal.persist.entity.Usuari;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "es.caib.pinbal.persist.repository",
        entityManagerFactoryRef = "mainEntityManager",
        transactionManagerRef = "mainTransactionManager",
        repositoryBaseClass = BaseRepositoryImpl.class
)
@EnableJpaAuditing
public class PersistenceTestConfig {

    @Bean
    public DataSource mainDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean mainEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(mainDataSource());
        em.setPackagesToScan(
                "es.caib.pinbal.persist.entity",
                "es.caib.pinbal.persist.audit"
        );
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        em.setJpaVendorAdapter(vendorAdapter);
        Properties jpaProperties = new Properties();
        jpaProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        jpaProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        jpaProperties.setProperty("hibernate.implicit_naming_strategy",
                "es.caib.pinbal.persist.audit.CustomImplicitNamingStrategy");
        em.setJpaProperties(jpaProperties);
        em.setPersistenceUnitName("main");
        return em;
    }

    @Bean
    public PlatformTransactionManager mainTransactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(mainEntityManager().getObject());
        return tm;
    }

    @Bean
    public AuditorAware<Usuari> auditorProvider() {
        return Optional::empty;
    }
}
