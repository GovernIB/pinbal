package es.caib.pinbal.back.view;

import org.springframework.context.MessageSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Moltes vistes d'aquest paquet criden {@code new RequestContext(request)} per obtenir el locale
 * de la petició via {@code MessageSource}. {@code RequestContext} llança
 * {@code IllegalStateException} si no troba un {@code WebApplicationContext} associat a la
 * petició (normalment el posa {@code DispatcherServlet}). Aquest helper prepara un mock de
 * {@link HttpServletRequest} amb l'atribut necessari perquè el constructor no falli en un test
 * unitari sense context real de Spring MVC.
 */
final class ViewTestSupport {

    private ViewTestSupport() {
    }

    static HttpServletRequest mockRequest(Locale locale) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getLocale()).thenReturn(locale);
        when(request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE))
                .thenReturn(mock(WebApplicationContext.class));
        return request;
    }

    /**
     * MessageSource que retorna la pròpia clau demanada (evita generar textos com "???clau???"
     * que POI rebutja com a nom de fulla o que dificulten les assercions).
     */
    static MessageSource mockMessageSourceEcoDeLaClau() {
        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage(any(), any(), any(), any())).thenAnswer(inv -> inv.getArgument(0));
        when(messageSource.getMessage(any(), any(), any())).thenAnswer(inv -> inv.getArgument(0));
        return messageSource;
    }
}
