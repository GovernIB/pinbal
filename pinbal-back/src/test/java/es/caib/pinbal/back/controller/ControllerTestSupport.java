package es.caib.pinbal.back.controller;

import org.springframework.context.MessageSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Els controladors d'aquest paquet criden {@code new RequestContext(request)} (via
 * {@code BaseController.getMessage}) per obtenir el locale de la petició, cosa que llança
 * {@code IllegalStateException} si la petició no té associat un {@code WebApplicationContext}
 * (normalment el posa {@code DispatcherServlet}). Aquest helper prepara un mock de
 * {@link HttpServletRequest} amb sessió i principal ja configurats, i injecta camps
 * {@code @Autowired} (sense setter) per reflexió.
 */
final class ControllerTestSupport {

    private ControllerTestSupport() {
    }

    static HttpServletRequest mockRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
        when(request.getLocale()).thenReturn(Locale.forLanguageTag("ca"));
        when(request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE))
                .thenReturn(mock(WebApplicationContext.class));
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("usuari1");
        when(request.getUserPrincipal()).thenReturn(principal);
        return request;
    }

    /**
     * {@code ServerSideRequest.toPageable()} itera sobre {@code order} sense comprovar null: si
     * la petició no porta cap paràmetre {@code order[0][...]}, el camp queda {@code null} i
     * {@code toPageable()} llança {@code NullPointerException}. Cal cridar sempre aquest mètode
     * abans d'invocar un mètode {@code datatable(...)} d'un controlador en un test.
     */
    /**
     * Estableix els paràmetres mínims perquè {@code ServerSideRequest.toPageable()} no llanci
     * NullPointerException (draw/start/length/order) i, quan el controlador NO sobreescriu
     * {@code cols.get(i).setData(...)} abans de construir el {@code ServerSideResponse} (com fa
     * {@code AuditorUsuariController}/{@code RepresentantUsuariController}), les columnes
     * indicades han de ser noms de propietat REALS del DTO retornat, perquè
     * {@code ServerSideResponse} les llegeix per reflexió.
     */
    static void mockDatatableParams(HttpServletRequest request, String... columnData) {
        when(request.getParameter("draw")).thenReturn("1");
        when(request.getParameter("start")).thenReturn("0");
        when(request.getParameter("length")).thenReturn("10");
        when(request.getParameter("order[0][column]")).thenReturn("0");
        when(request.getParameter("order[0][dir]")).thenReturn("asc");
        String[] columnes = columnData.length > 0
                ? columnData
                : new String[] {"col0", "col1", "col2", "col3", "col4", "col5"};
        for (int i = 0; i < columnes.length; i++) {
            when(request.getParameter("columns[" + i + "][data]")).thenReturn(columnes[i]);
        }
    }

    static MessageSource mockMessageSourceEcoDeLaClau() {
        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage(any(), any(), any(), any())).thenAnswer(inv -> inv.getArgument(0));
        when(messageSource.getMessage(any(), any(), any())).thenAnswer(inv -> inv.getArgument(0));
        return messageSource;
    }

    static void setField(Object target, String name, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
