/**
 * 
 */
package es.caib.pinbal.webapp.cxf;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.cxf.transport.servlet.CXFServlet;


/**
 * Modificació del CXFServlet per a poder accedir a l'instància d'aquest
 * servlet des d'un altre.
 * 
 * @author Limit Tecnologies
 */
public class PinbalCxfServlet extends CXFServlet {
	
	private static final long serialVersionUID = 1L;
	public static final String CONTEXT_ATTRIBUTE_NAME = "cxf-servlet";

	@Override
	public void loadBus(ServletConfig servletConfig) throws ServletException {
		super.loadBus(servletConfig);
		getServletContext().setAttribute(CONTEXT_ATTRIBUTE_NAME, this);
	}

}
