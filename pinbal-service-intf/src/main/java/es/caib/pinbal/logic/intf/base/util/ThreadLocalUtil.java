package es.caib.pinbal.logic.intf.base.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper per a gestionar informació del TreadLocal. La llibreria base-boot
 * s'encarregarà de eliminar aquesta informació en cada perició al back.
 * 
 * @author Límit Tecnologies
 */
public class ThreadLocalUtil {

	public static final String SESSION_KEY = "BB_SESSION";
	public static final String HIBERNATE_INTERCEPTOR_PROPS_KEY = "BB_HIBERNATE_INTERCEPTOR_PROPS";

	private static final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

	public static void setAttribute(String key, Object value) {
		Map<String, Object> map = threadLocal.get();
		if (map == null) {
			map = new HashMap<String, Object>();
			threadLocal.set(map);
		}
		map.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public static <A> A getAttribute(String key, Class<A> clazz) {
		Map<String, Object> map = threadLocal.get();
		if (map != null) {
			return (A)map.get(key);
		} else {
			return null;
		}
	}

	public static void clear() {
		Map<String, Object> map = threadLocal.get();
		if (map != null) {
			map.clear();
		}
	}

}