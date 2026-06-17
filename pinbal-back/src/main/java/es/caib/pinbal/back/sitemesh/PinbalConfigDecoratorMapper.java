package es.caib.pinbal.back.sitemesh;

import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.mapper.ConfigDecoratorMapper;
import es.caib.pinbal.back.filter.OriginalServletPathFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Sitemesh mapper that uses the original request path (saved before any
 * RequestDispatcher.forward() calls) so that ModalHelper's internal path
 * rewriting doesn't prevent the modal decorator from being selected.
 */
public class PinbalConfigDecoratorMapper extends ConfigDecoratorMapper {

    @Override
    public Decorator getDecorator(HttpServletRequest request, Page page) {
        String savedPath = (String) request.getAttribute(OriginalServletPathFilter.ATTR);
        if (savedPath != null && !savedPath.equals(request.getServletPath())) {
            return super.getDecorator(new HttpServletRequestWrapper(request) {
                @Override
                public String getServletPath() {
                    return savedPath;
                }
            }, page);
        }
        return super.getDecorator(request, page);
    }

}
