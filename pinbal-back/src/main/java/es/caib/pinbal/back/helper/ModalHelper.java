/**
 * 
 */
package es.caib.pinbal.back.helper;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Utilitat per a finestres modals.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ModalHelper {

	private static final String PREFIX_MODAL = "/modal";
	private static final String REQUEST_ATTRIBUTE_MODAL = "ModalHelper.Modal";

	public static final String ACCIO_MODAL_TANCAR = PREFIX_MODAL + "/tancar";

	public static boolean isModal(HttpServletRequest request) {
		return request.getAttribute(REQUEST_ATTRIBUTE_MODAL) != null;
	}
	public static boolean comprovarModalInterceptor(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (isRequestPathModal(request)) {
			String pathSensePrefix = getPathSensePrefix(request);
			// Es marca ABANS de fer el forward: l'atribut de request sobreviu al forward (mateix objecte
			// HttpServletRequest), a diferència del mecanisme anterior (marcar-ho a la "segona passada" quan
			// l'interceptor es torna a cridar per al path sense prefix), que depenia de PinbalInterceptor
			// s'executés una segona vegada per al forward intern — cosa que NO passa (el forward arriba
			// directament al @Controller destí sense re-invocar la cadena d'interceptors de Spring), fent que
			// ModalHelper.isModal() sempre retornés false per a qualsevol petició (GET o POST) rebuda via
			// forward, i que el formulari mai fes servir la ruta de tancament de la modal en desar.
			marcarModal(request);
			RequestDispatcher dispatcher = request.getRequestDispatcher(pathSensePrefix);
			dispatcher.forward(request, response);
			return false;
		}
		return true;
	}

	public static boolean isRequestPathModal(
			HttpServletRequest request) {
		String servletPath = request.getServletPath();
		return
				servletPath.startsWith(PREFIX_MODAL) &&
				!servletPath.startsWith(ACCIO_MODAL_TANCAR);
	}

	private static String getPathSensePrefix(
			HttpServletRequest request) {
		return request.getServletPath().substring(PREFIX_MODAL.length());
	}

	private static void marcarModal(HttpServletRequest request) {
		request.setAttribute(
				REQUEST_ATTRIBUTE_MODAL,
				Boolean.TRUE);
	}

}
