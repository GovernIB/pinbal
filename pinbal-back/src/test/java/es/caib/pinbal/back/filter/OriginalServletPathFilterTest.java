package es.caib.pinbal.back.filter;

import org.junit.jupiter.api.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OriginalServletPathFilterTest {

    @Test
    public void doFilterAmbHttpRequestGuardaElServletPathOriginal() throws Exception {
        OriginalServletPathFilter filter = new OriginalServletPathFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletResponse response = mock(ServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getServletPath()).thenReturn("/consulta/nova");

        filter.doFilter(request, response, chain);

        verify(request).setAttribute(eq(OriginalServletPathFilter.ATTR), eq("/consulta/nova"));
        verify(chain).doFilter(request, response);
    }

    @Test
    public void doFilterAmbRequestNoHttpNoGuardaAtribut() throws Exception {
        OriginalServletPathFilter filter = new OriginalServletPathFilter();
        ServletRequest request = mock(ServletRequest.class);
        ServletResponse response = mock(ServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    public void initIDestroyNoFanRes() {
        OriginalServletPathFilter filter = new OriginalServletPathFilter();

        filter.init(null);
        filter.destroy();
    }
}
