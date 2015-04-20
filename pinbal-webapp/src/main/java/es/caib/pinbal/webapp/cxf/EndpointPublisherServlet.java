/**
 * 
 */
package es.caib.pinbal.webapp.cxf;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import es.caib.pinbal.core.ws.Recobriment;


/**
 * Servlet per a publicar tots els serveis que proporciona PINBAL
 * mitjançant apache-cxf.
 * 
 * @author Limit Tecnologies
 */
public class EndpointPublisherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		CXFServlet cxfServlet = (CXFServlet)servletConfig.getServletContext().getAttribute(PinbalCxfServlet.CONTEXT_ATTRIBUTE_NAME);
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
		Bus bus = cxfServlet.getBus();
		BusFactory.setDefaultBus(bus);
		WsServerUtils.publish(
				"/recobriment",
				context.getBean(Recobriment.class),
				false,
				true);
	}

}
