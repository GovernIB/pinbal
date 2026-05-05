package es.caib.pinbal.logic.intf.base.config;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Propietats de configuració de l'aplicació.
 *
 * @author Límit Tecnologies
 */
public class PropertyConfig {

	public static final String PROPERTY_PREFIX = BaseConfig.BASE_PACKAGE + ".";
	public static final String PROPERTY_PREFIX_FRONT = PROPERTY_PREFIX + "front.";

	public static final String PROP_BACKEND_HTTP_HEADER_ANSWERS = PROPERTY_PREFIX + "http.header.answers";
	public static final String PROP_PERSIST_DEFAULT_AUDITOR = PROPERTY_PREFIX + "persist.default.auditor";
	public static final String PROP_PERSIST_CONTAINER_TRANSACTIONS_DISABLED = PROPERTY_PREFIX + "persist.container-transactions-disabled";
	public static final String PROP_PERSIST_TRANSACTION_MANAGER_ENABLED = PROPERTY_PREFIX + "persist.transaction-manager.enabled";

	public static final String PERSISTENCE_CONTAINER_TRANSACTIONS_DISABLED = PROPERTY_PREFIX + "persist.container-transactions-disabled";
	public static final String PERSISTENCE_TRANSACTION_MANAGER_ENABLED = PROPERTY_PREFIX + "persist.transaction-manager.enabled";

	public static final String PROP_FRONT_API_URL = PROPERTY_PREFIX_FRONT + "api.url";
	public static final String PROP_FRONT_AUTH_URL = PROPERTY_PREFIX_FRONT + "auth.url";
	public static final String PROP_FRONT_AUTH_REALM = PROPERTY_PREFIX_FRONT + "auth.realm";
	public static final String PROP_FRONT_AUTH_CLIENTID = PROPERTY_PREFIX_FRONT + "auth.clientid";

	public static final Map<String, String> REACT_APP_PROPS_MAP = Map.ofEntries(
		new AbstractMap.SimpleEntry<>(PROP_FRONT_API_URL, "REACT_APP_API_URL"),
		new AbstractMap.SimpleEntry<>(PROP_FRONT_AUTH_URL, "REACT_APP_AUTH_URL"),
		new AbstractMap.SimpleEntry<>(PROP_FRONT_AUTH_REALM, "REACT_APP_AUTH_REALM"),
		new AbstractMap.SimpleEntry<>(PROP_FRONT_AUTH_CLIENTID, "REACT_APP_AUTH_CLIENTID"));

	public static final Map<String, String> VITE_PROPS_MAP = Map.ofEntries(
		new AbstractMap.SimpleEntry<>(PROP_FRONT_API_URL, "VITE_API_URL"),
		new AbstractMap.SimpleEntry<>(PROP_FRONT_AUTH_URL, "VITE_AUTH_URL"),
		new AbstractMap.SimpleEntry<>(PROP_FRONT_AUTH_REALM, "VITE_AUTH_REALM"),
		new AbstractMap.SimpleEntry<>(PROP_FRONT_AUTH_CLIENTID, "VITE_AUTH_CLIENTID"));

	public static final String PROP_COMUNICACIONS_SIR_INTERNES = PROPERTY_PREFIX + "comunicacions.sir.internes";

}
