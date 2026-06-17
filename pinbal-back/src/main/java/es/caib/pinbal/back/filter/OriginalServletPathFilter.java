package es.caib.pinbal.back.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Saves the original getServletPath() as a request attribute so that the Sitemesh
 * decorator mapper can use it after RequestDispatcher.forward() calls (e.g. ModalHelper)
 * have mutated the underlying path in Undertow/JBoss.
 *
 * Must run before SiteMeshFilter (order 1 vs order 2).
 */
public class OriginalServletPathFilter implements Filter {

    public static final String ATTR = OriginalServletPathFilter.class.getName() + ".originalServletPath";

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            httpRequest.setAttribute(ATTR, httpRequest.getServletPath());
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
