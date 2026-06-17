package es.caib.pinbal.logic.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

//@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ReadDbPropertiesPostProcessor implements EnvironmentPostProcessor {

    private static final DeferredLog log = new DeferredLog();
    public static final String DBAPP_PROPERTIES = "es.caib.pinbal.db.properties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

//        Map<String, Object> propertySource = new HashMap<>();
//        try {
//            log.info("Obtenint dataSource per a carregar les propietats de la BBDD...");
//            var lookup = new JndiDataSourceLookup();
//            var datasourceJndi = environment.getProperty("spring.datasource.jndi-name", "java:jboss/datasources/pinbalDS");
//            var dataSource = lookup.getDataSource(datasourceJndi);
//            log.info("... Datasource carregat correctament.");
//
//            log.info("Carregant les propietats...");
//            try (var connection = dataSource.getConnection();
//                 var preparedStatement = connection.prepareStatement("SELECT key, value FROM not_config WHERE jboss_property = 0");
//                var resultSet = preparedStatement.executeQuery()) {
//                String clau;
//                String valor;
//                while (resultSet.next()) {
//                    clau = resultSet.getString("key");
//                    valor = resultSet.getString("value");
//                    propertySource.put(clau, valor);
//                    log.info("   ... carregada la propietat: " + clau + "=" + valor);
//                }
//            }pinbal
//            log.info("... Finalitzada la càrega de propietats");
//            log.info("Afegint les propietats carregades de base de dades al entorn...");
//            if (environment.getPropertySources().contains(DBAPP_PROPERTIES)) {
//                environment.getPropertySources().replace(DBAPP_PROPERTIES, new MapPropertySource(DBAPP_PROPERTIES, propertySource));
//            } else {
//                environment.getPropertySources().addFirst(new MapPropertySource(DBAPP_PROPERTIES, propertySource));
//            }
//            log.info("...Propietats afegides");
//        } catch (Exception ex) {
//            log.error("No s'han pogut carregar les propietats de la BBDD", ex);
//        }

    }
}
