package es.caib.pinbal.logic.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprova que la configuració SCSP (escaneig de components de {@link ScspConfig} +
 * application-context-scsp.xml) no defineix cap bean dues vegades amb el mateix nom.
 * <p>
 * Spring Boot desactiva la sobreescriptura de definicions de beans; si n'hi ha cap de
 * duplicada, l'aplicació no arrenca ("The bean 'parametroConfiguracionDao' ... could not be
 * registered ... overriding is disabled"). Només es registren les definicions: no s'instancia
 * cap bean, per tant no cal ni base de dades ni JNDI.
 */
class ScspConfigBeanDefinitionsTest {

	@Test
	void definicionsBeansScsp_senseSobreescriptura_noHiHaDuplicats() {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		// Mateix comportament que Spring Boot: llança BeanDefinitionOverrideException si hi ha duplicats
		beanFactory.setAllowBeanDefinitionOverriding(false);
		AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);
		beanFactory.registerBeanDefinition("scspConfig", new RootBeanDefinition(ScspConfig.class));

		ConfigurationClassPostProcessor postProcessor = new ConfigurationClassPostProcessor();
		postProcessor.setEnvironment(new StandardEnvironment());
		postProcessor.setResourceLoader(new DefaultResourceLoader());
		postProcessor.postProcessBeanDefinitionRegistry(beanFactory);

		assertDefinitXml(beanFactory, "parametroConfiguracionDao", "es.scsp.common.dao.ParametroConfiguracionDao");
		assertDefinitXml(beanFactory, "proxyManager", "es.scsp.common.utils.ProxyManager");
	}

	private static void assertDefinitXml(DefaultListableBeanFactory beanFactory, String nom, String classe) {
		assertTrue(beanFactory.containsBeanDefinition(nom), "No s'ha definit el bean " + nom);
		BeanDefinition definicio = beanFactory.getBeanDefinition(nom);
		assertEquals(classe, definicio.getBeanClassName());
		// Ha de ser la definició explícita del XML (la que ja s'aplicava a JBoss), no l'escanejada
		assertTrue(
				String.valueOf(definicio.getResourceDescription()).contains("application-context-scsp.xml"),
				"El bean " + nom + " no prové de application-context-scsp.xml: " + definicio.getResourceDescription());
	}

}
