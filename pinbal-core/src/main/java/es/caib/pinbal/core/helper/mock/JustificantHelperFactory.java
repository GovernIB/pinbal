package es.caib.pinbal.core.helper.mock;

import es.caib.pinbal.core.helper.JustificantHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Factory per obtenir la instància correcta de JustificantHelper segons el perfil actiu.
 *
 * Aquesta classe resol el problema de selecció de bean en Spring 3.0 quan
 * s'utilitzen profiles per mocks.
 *
 * Ús:
 * <code>
 * JustificantHelper helper = justificantHelperFactory.getJustificantHelper();
 * </code>
 */
@Component
public class JustificantHelperFactory {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    /**
     * Obté la instància correcta de JustificantHelper segons el perfil actiu.
     *
     * - Si el perfil "mock-scsp-db" està actiu, retorna JustificantHelperMock
     * - Altrament, retorna el JustificantHelper original
     *
     * @return La instància de JustificantHelper adequada
     */
    public JustificantHelper getJustificantHelper() {
        String[] activeProfiles = environment.getActiveProfiles();

        // Comprovar si el perfil mock-scsp-db està actiu
        for (String profile : activeProfiles) {
            if ("mock-scsp-db".equals(profile) || "mock-scsp".equals(profile)) {
                // Intentar obtenir el mock
                try {
                    return applicationContext.getBean(JustificantHelperMock.class);
                } catch (Exception e) {
                    // Si no es troba, continuar amb el bean original
                }
            }
        }

        // Per defecte, retornar el JustificantHelper original
        return applicationContext.getBean(JustificantHelper.class);
    }
}
