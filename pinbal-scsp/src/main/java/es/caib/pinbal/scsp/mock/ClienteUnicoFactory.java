package es.caib.pinbal.scsp.mock;

import es.scsp.client.ClienteUnico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Factory per obtenir la instància correcta de ClienteUnico segons el perfil actiu.
 *
 * Aquesta classe resol el problema de selecció de bean en Spring 3.0 quan
 * s'utilitzen profiles i es busca un bean per nom específic.
 *
 * Ús:
 * <code>
 * ClienteUnico cliente = clienteUnicoFactory.getClienteUnico();
 * </code>
 */
@Component
public class ClienteUnicoFactory {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    /**
     * Obté la instància correcta de ClienteUnico segons el perfil actiu.
     *
     * - Si el perfil "mock-scsp-db" està actiu, retorna ClienteUnicoMockPersistent
     * - Si el perfil "mock-scsp" està actiu, retorna ClienteUnicoMock
     * - Altrament, retorna el ClienteUnico original
     *
     * @return La instància de ClienteUnico adequada
     */
    public ClienteUnico getClienteUnico() {
        String[] activeProfiles = environment.getActiveProfiles();

        // Comprovar perfils actius
        for (String profile : activeProfiles) {
            if ("mock-scsp-db".equals(profile)) {
                // Intentar obtenir el mock persistent
                try {
                    return applicationContext.getBean(ClienteUnicoMockPersistent.class);
                } catch (Exception e) {
                    // Si no es troba, continuar
                }
            }
            if ("mock-scsp".equals(profile)) {
                // Intentar obtenir el mock simple
                try {
                    return applicationContext.getBean(ClienteUnicoMock.class);
                } catch (Exception e) {
                    // Si no es troba, continuar
                }
            }
        }

        // Per defecte, retornar el ClienteUnico original
        return (ClienteUnico) applicationContext.getBean("clienteUnico");
    }
}
