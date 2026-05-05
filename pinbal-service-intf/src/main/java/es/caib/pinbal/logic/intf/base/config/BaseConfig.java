package es.caib.pinbal.logic.intf.base.config;

/**
 * Propietats de l'aplicació.
 *
 * @author Límit Tecnologies
 */
public class BaseConfig {

	public static final String APP_NAME = "pinbal";
	public static final String DB_PREFIX = "pbl_";
	public static final String ROLE_PREFIX = "PBL_";
	public static final String DEFAULT_LOCALE = "ca";

	public static final String BASE_PACKAGE = "es.caib." + APP_NAME;
	public static final String PROPERTY_PREFIX = BASE_PACKAGE + ".";

	public static final String APP_PROPERTIES = BASE_PACKAGE + ".properties";
	public static final String APP_SYSTEM_PROPERTIES = BASE_PACKAGE + ".system.properties";

	public static final String ROLE_ADMIN = ROLE_PREFIX + "ADMIN";
	public static final String ROLE_REPRES = ROLE_PREFIX + "REPRES";
	public static final String ROLE_WS = ROLE_PREFIX + "WS";
	public static final String ROLE_REPORT = ROLE_PREFIX + "REPORT";
	public static final String ROLE_COM = ROLE_PREFIX + "COM";
	public static final String ROLE_AUDIT = ROLE_PREFIX + "AUDIT";
	public static final String ROLE_SUPERAUDIT = ROLE_PREFIX + "SUPERAUD";
	public static final String ROLE_USER = "tothom";

	public static final String API_PATH = "/apinew";
	public static final String PING_PATH = "/ping";
	public static final String SYSENV_PATH = "/sysenv";
	public static final String MANIFEST_PATH = "/manifest";
	public static final String AUTH_TOKEN_PATH = "/authToken";
	public static final String AUTH_ROLES_PATH = "/authRoles";
	public static final String REACT_APP_PATH="/reactapp";

	public static final String PROP_FILES = PROPERTY_PREFIX + "fitxers";
	public static final String PROP_SECURITY_MAPPABLE_ROLES = PROPERTY_PREFIX + "security.mappableRoles";
	public static final String PROP_SECURITY_ROLE_HTTP_HEADER = PROPERTY_PREFIX + "security.selected.role.http.header";
	public static final String PROP_SECURITY_NAME_ATTRIBUTE_KEY = PROPERTY_PREFIX + "security.nameAttributeKey";
	public static final String PROP_USER_SESSION_HTTP_HEADER = PROPERTY_PREFIX + "user.session.http.header";

}
