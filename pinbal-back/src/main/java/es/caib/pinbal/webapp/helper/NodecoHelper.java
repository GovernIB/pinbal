/**
 * 
 */
package es.caib.pinbal.webapp.helper;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Utilitat per a finestres sense decoració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NodecoHelper {

	private static final String PREFIX_NODECO = "/nodeco";
	private static final String REQUEST_ATTRIBUTE_NODECO = "NodecoHelper.Nodeco";
	private static final String SESSION_ATTRIBUTE_REQUESTPATHSMAP = "NodecoHelper.RequestPathsMap";

	public static boolean isNodeco(HttpServletRequest request) {
		return request.getAttribute(REQUEST_ATTRIBUTE_NODECO) != null;
	}
	public static boolean comprovarNodecoInterceptor(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (isRequestPathNodeco(request)) {
			String uriSensePrefix = getUriSensePrefix(request);
			Set<String> requestPathsMap = getRequestPathsMap(request);
			requestPathsMap.add(uriSensePrefix);
			RequestDispatcher dispatcher = request.getRequestDispatcher(uriSensePrefix);
			dispatcher.forward(request, response);
			return false;
		} else {
			Set<String> requestPathsMap = getRequestPathsMap(request);
			String pathComprovacio = request.getServletPath();
			if (requestPathsMap.contains(pathComprovacio)) {
				requestPathsMap.remove(pathComprovacio);
				marcarNodeco(request);
			}
			return true;
		}
	}

	private static boolean isRequestPathNodeco(
			HttpServletRequest request) {
		String servletPath = request.getServletPath();
		return servletPath.startsWith(PREFIX_NODECO);
	}
	private static String getUriSensePrefix(
			HttpServletRequest request) {
		return request.getServletPath().substring(PREFIX_NODECO.length());
	}
	private static Set<String> getRequestPathsMap(
			HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		Set<String> requestPathsMap = (Set<String>)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_REQUESTPATHSMAP);
		if (requestPathsMap == null) {
			requestPathsMap = new HashSet<String>();
			request.getSession().setAttribute(
					SESSION_ATTRIBUTE_REQUESTPATHSMAP,
					requestPathsMap);
		}
		return requestPathsMap;
	}
	private static void marcarNodeco(HttpServletRequest request) {
		request.setAttribute(
				REQUEST_ATTRIBUTE_NODECO,
				new Boolean(true));
	}

}
