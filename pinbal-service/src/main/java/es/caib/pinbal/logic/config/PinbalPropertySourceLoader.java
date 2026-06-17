package es.caib.pinbal.logic.config;

import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

import static es.caib.pinbal.logic.config.ReadDbPropertiesPostProcessor.DBAPP_PROPERTIES;

/**
 * BeanFactoryPostProcessor que carrega propietats de PBL_CONFIG a l'arrencada.
 *
 * Llegeix les files amb sourceProperty=DATABASE de PBL_CONFIG via JNDI+JDBC i les
 * afegeix a l'Environment de Spring com a MapPropertySource d'alta prioritat, perquè
 * estiguin disponibles per a @Value i ConfigHelper.getConfig() des del primer moment.
 *
 * S'executa amb ordre LOWEST_PRECEDENCE-100, just abans de ScspPropertyPlaceholderConfigurer
 * (ordre LOWEST_PRECEDENCE), i després de que ConfigurationClassPostProcessor hagi
 * processat els @PropertySource (fitxers externs ja carregats a l'Environment).
 */
@Slf4j
public class PinbalPropertySourceLoader implements BeanFactoryPostProcessor, EnvironmentAware, PriorityOrdered {

    private static final String SQL_LOAD_DB_PROPS =
            "SELECT key, value FROM PBL_CONFIG WHERE source_property = 'DATABASE'";

    private ConfigurableEnvironment environment;

    @Override
    public void setEnvironment(Environment env) {
        this.environment = (ConfigurableEnvironment) env;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        loadDbProperties();
    }

    private void loadDbProperties() {
        String jndiName = environment.getProperty("spring.datasource.jndi-name");
        if (jndiName == null || jndiName.isBlank()) {
            log.debug("spring.datasource.jndi-name no definit; propietats de PBL_CONFIG no es carregaran a l'inici");
            return;
        }
        try {
            DataSource ds = new JndiDataSourceLookup().getDataSource(jndiName);
            Map<String, Object> props = new LinkedHashMap<>();
            try (Connection conn = ds.getConnection();
                 PreparedStatement ps = conn.prepareStatement(SQL_LOAD_DB_PROPS);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String key = rs.getString("key");
                    String value = rs.getString("value");
                    if (key != null && value != null) {
                        props.put(key, value);
                    }
                }
            }
            if (!props.isEmpty()) {
                if (environment.getPropertySources().contains(DBAPP_PROPERTIES)) {
                    environment.getPropertySources().replace(
                            DBAPP_PROPERTIES, new MapPropertySource(DBAPP_PROPERTIES, props));
                } else {
                    environment.getPropertySources().addFirst(
                            new MapPropertySource(DBAPP_PROPERTIES, props));
                }
                log.info("Carregades {} propietats de PBL_CONFIG a l'entorn Spring", props.size());
            }
        } catch (Exception ex) {
            log.warn("No s'han pogut carregar les propietats de PBL_CONFIG a l'inici: {}", ex.getMessage());
        }
    }
}
