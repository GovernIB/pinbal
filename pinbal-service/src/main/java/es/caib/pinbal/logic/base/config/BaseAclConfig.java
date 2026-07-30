package es.caib.pinbal.logic.base.config;

import es.caib.pinbal.logic.intf.base.permission.ExtendedPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.sql.DataSource;
import java.util.List;

/**
 * Configuració de les ACLs de Spring Security.
 * 
 * @author Límit Tecnologies
 */
public abstract class BaseAclConfig {

	private static final boolean CLASS_ID_SUPPORTED = true;

	@Value("${spring.jpa.properties.hibernate.dialect}")
	private String hibernateDialect;

	@Autowired
	private DataSource dataSource;

	@Bean
	public AclAuthorizationStrategy aclAuthorizationStrategy() {
		return new AclAuthorizationStrategy() {
			@Override
			public void securityCheck(Acl acl, int changeType) {
				if ((SecurityContextHolder.getContext() == null)
						|| (SecurityContextHolder.getContext().getAuthentication() == null)
						|| !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
					throw new AccessDeniedException(
							"Authenticated principal required to operate with ACLs");
				}
			}
		};
	}

	@Bean
	public AclCache aclCache(CacheManager springCacheManager) {
		return new SpringCacheBasedAclCache(
				springCacheManager.getCache(getAclCacheName()),
				new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger()),
				aclAuthorizationStrategy());
	}

	@Bean
	public LookupStrategy lookupStrategy(CacheManager springCacheManager) {
		BasicLookupStrategy lookupStrategy = new BasicLookupStrategy(
				dataSource,
				aclCache(springCacheManager),
				aclAuthorizationStrategy(),
				new ConsoleAuditLogger());
		String tableClass = getDbTablePrefix() + "acl_class";
		String tableSid = getDbTablePrefix() + "acl_sid";
		String tableOid = getDbTablePrefix() + "acl_object_identity";
		String tableEntry = getDbTablePrefix() + "acl_entry";
		String selectClause = "select " + tableOid + ".object_id_identity, "
				+ tableEntry + ".ace_order,  " + tableOid + ".id as acl_id, " +  tableOid + ".parent_object, "
				+ tableOid + ".entries_inheriting, " + tableEntry + ".id as ace_id, " + tableEntry + ".mask,  "
				+ tableEntry + ".granting,  " + tableEntry + ".audit_success, " + tableEntry + ".audit_failure,  "
				+ tableSid + ".principal as ace_principal, " + tableSid + ".sid as ace_sid,  "
				+ "acli_sid.principal as acl_principal, acli_sid.sid as acl_sid, " + tableClass + ".class ";
		String selectIdClause = ", " + tableClass + ".class_id_type  ";
		String fromClause = "from " + tableOid + " "
				+ "left join " + tableSid + " acli_sid on acli_sid.id = " +  tableOid + ".owner_sid "
				+ "left join " + tableClass + " on " + tableClass + ".id = " +  tableOid + ".object_id_class   "
				+ "left join " + tableEntry + " on " +  tableOid + ".id = " +  tableEntry + ".acl_object_identity "
				+ "left join " + tableSid + " on " + tableEntry + ".sid = " + tableSid + ".id  " + "where ( ";
		lookupStrategy.setPermissionFactory(new ExtendedPermissionFactory());
		lookupStrategy.setAclClassIdSupported(CLASS_ID_SUPPORTED);
		lookupStrategy.setSelectClause(CLASS_ID_SUPPORTED ? selectClause + selectIdClause + fromClause : selectClause + fromClause);
		lookupStrategy.setLookupPrimaryKeysWhereClause("(" +  tableOid + ".id = ?)");
		lookupStrategy.setLookupObjectIdentitiesWhereClause("(" +  tableOid + ".object_id_identity = ? and " + tableClass + ".class = ?)");
		lookupStrategy.setOrderByClause(") order by " +  tableOid + ".object_id_identity asc, " +  tableEntry + ".ace_order asc");
		return lookupStrategy;
	}

	@Bean
	public MutableAclService aclService(CacheManager cacheManager) {
		BaseMutableAclService mutableAclService = createMutableAclServiceInstance(dataSource, cacheManager);
		String tableClass = getDbTablePrefix() + "acl_class";
		String tableSid = getDbTablePrefix() + "acl_sid";
		String tableOid = getDbTablePrefix() + "acl_object_identity";
		String tableEntry = getDbTablePrefix() + "acl_entry";
		mutableAclService.setAclClassIdSupported(CLASS_ID_SUPPORTED);
		if (hibernateDialect.toLowerCase().contains("oracle") && isOracleSequenceLegacy()) {
			mutableAclService.setClassIdentityQuery("select " + tableClass.toUpperCase() + getTableSequenceSuffix() + ".currval from dual");
			mutableAclService.setSidIdentityQuery("select " + tableSid.toUpperCase() + getTableSequenceSuffix() + ".currval from dual");
		} else if (hibernateDialect.toLowerCase().contains("oracle") && !isOracleSequenceLegacy()) {
			mutableAclService.setClassIdentityQuery("select current_value('" + tableClass.toUpperCase() + "') from dual");
			mutableAclService.setSidIdentityQuery("select current_value('" + tableSid.toUpperCase() + "') from dual");
		} else if (hibernateDialect.toLowerCase().contains("postgres")) {
			mutableAclService.setClassIdentityQuery("select currval(pg_get_serial_sequence('" + tableClass + "', 'id'))");
			mutableAclService.setSidIdentityQuery("select currval(pg_get_serial_sequence('" + tableSid + "', 'id'))");
		} else if (hibernateDialect.toLowerCase().contains("h2")) {
			// A H2 (usat per als entorns e2e), l'id es genera amb "default next value for" en lloc del
			// trigger d'Oracle (veure db/changelog/init/04_initial_schema_sequence.yaml), i es recupera
			// amb la mateixa sintaxi "seq.currval from dual" que ja funciona a Oracle.
			mutableAclService.setClassIdentityQuery("select " + tableClass.toUpperCase() + getTableSequenceSuffix() + ".currval from dual");
			mutableAclService.setSidIdentityQuery("select " + tableSid.toUpperCase() + getTableSequenceSuffix() + ".currval from dual");
		} else if (hibernateDialect.toLowerCase().contains("hsql")) {
			mutableAclService.setClassIdentityQuery("call identity()");
			mutableAclService.setSidIdentityQuery("call identity()");
		}
		mutableAclService.setFindChildrenQuery("select obj.object_id_identity as obj_id, class.class as class" +
				(CLASS_ID_SUPPORTED ? ", class.class_id_type as class_id_type" : "") +
				" from " + tableOid + " obj, " + tableOid + " parent, " + tableClass + " class " +
				"where obj.parent_object = parent.id and obj.object_id_class = class.id " +
				"and parent.object_id_identity = ? and parent.object_id_class = (" +
				"select id FROM " + tableClass + " where " + tableClass + ".class = ?)");
		mutableAclService.setDeleteEntryByObjectIdentityForeignKeySql(
				"delete from " + tableEntry + " where acl_object_identity=?");
		mutableAclService.setDeleteObjectIdentityByPrimaryKeySql(
				"delete from " + tableOid + " where id=?");
		mutableAclService.setInsertClassSql(
				CLASS_ID_SUPPORTED ? "insert into " + tableClass + " (class, class_id_type) values (?, ?)" : "insert into " + tableClass + " (class) values (?)");
		mutableAclService.setInsertEntrySql(
				"insert into " + tableEntry + " " +
						"(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)" +
						"values (?, ?, ?, ?, ?, ?, ?)");
		mutableAclService.setInsertObjectIdentitySql(
				"insert into " + tableOid + " " +
						"(object_id_class, object_id_identity, owner_sid, entries_inheriting) " +
						"values (?, ?, ?, ?)");
		mutableAclService.setInsertSidSql(
				"insert into " + tableSid + " (principal, sid) values (?, ?)");
		mutableAclService.setClassPrimaryKeyQuery(
				"select id from " + tableClass + " where class=?");
		mutableAclService.setObjectIdentityPrimaryKeyQuery(
				"select " + tableOid + ".id from " + tableOid + ", " + tableClass + " " +
						"where " + tableOid + ".object_id_class = " + tableClass + ".id and " + tableClass + ".class=? " +
						"and " + tableOid + ".object_id_identity = ?");
		mutableAclService.setSidPrimaryKeyQuery(
				"select id from " + tableSid + " where principal=? and sid=?");
		mutableAclService.setUpdateObjectIdentity(
				"update " + tableOid + " set " +
						"parent_object = ?, owner_sid = ?, entries_inheriting = ?" + " where id = ?");
		return mutableAclService;
	}

	public String getIdsWithPermissionQuery(
			boolean anyPermission,
			boolean anyPrincipalSid,
			boolean anyGrantedAuthoritiesSids) {
		String tableClass = getDbTablePrefix() + "acl_class";
		String tableSid = getDbTablePrefix() + "acl_sid";
		String tableOid = getDbTablePrefix() + "acl_object_identity";
		String tableEntry = getDbTablePrefix() + "acl_entry";
		String sidsCondition;
		if (anyPrincipalSid && anyGrantedAuthoritiesSids) {
			sidsCondition = "and ( " +
					"    " + tableEntry + ".sid in (select " + tableSid + ".id from " + tableSid + " where " + tableSid + ".principal = :isTrue and " + tableSid + ".sid = :principal) " +
					"    or " + tableEntry + ".sid in (select " + tableSid + ".id from " + tableSid + " where " + tableSid + ".principal = :isFalse and " + tableSid + ".sid in (:grantedAuthorities))) ";
		} else if (anyPrincipalSid) {
			sidsCondition = "and " + tableEntry + ".sid in (select " + tableSid + ".id from " + tableSid + " where " + tableSid + ".principal = :isTrue and " + tableSid + ".sid = :principal)";
		} else if (anyGrantedAuthoritiesSids) {
			sidsCondition = "and " + tableEntry + ".sid in (select " + tableSid + ".id from " + tableSid + " where " + tableSid + ".principal = :isFalse and " + tableSid + ".sid in (:grantedAuthorities)) ";;
		} else {
			sidsCondition = "";
		}
		return "select " +
				"    distinct " + tableOid + ".object_id_identity id " +
				"from " +
				"    " + tableEntry + " " +
				"    left join " + tableOid + " on " + tableOid + ".id = " + tableEntry + ".acl_object_identity " +
				"where " +
				"    " + tableEntry + ".granting = :isTrue " +
				(anyPermission ? "and " + tableEntry + ".mask in (:masks) " : "") +
				"and " + tableOid + ".object_id_class = (select id from " + tableClass + " where class = :className) " +
				sidsCondition;
	}

	protected abstract String getDbTablePrefix();

	protected String getTableSequenceSuffix() {
		return "_sq";
	}

	protected String getAclCacheName() {
		return "aclCache";
	}

	protected boolean isOracleSequenceLegacy() {
		return false;
	}

	protected BaseMutableAclService createMutableAclServiceInstance(
			DataSource dataSource,
			CacheManager cacheManager) {
		// S'han hagut de modificar els mètodes retrieveObjectIdentityPrimaryKey i findChildren per a
		// solucionar errors en les consultes quan el tipus de base de dades és PostgreSQL. Si forçam
		// que l'identificador del ObjectIdentity sigui un String dona error al executar la consulta
		// dient que no es pot convertir un bigint al tipus varchar.
		return new BaseMutableAclService(
				dataSource,
				lookupStrategy(cacheManager),
				aclCache(cacheManager));
	}

	public static class BaseMutableAclService extends JdbcMutableAclService {
		public BaseMutableAclService(DataSource dataSource, LookupStrategy lookupStrategy, AclCache aclCache) {
			super(dataSource, lookupStrategy, aclCache);
		}
		public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
			return super.findChildren(
					new ObjectIdentityImpl(
							parentIdentity.getType(),
							parentIdentity.getIdentifier().toString()));
		}
		protected Long retrieveObjectIdentityPrimaryKey(ObjectIdentity oid) {
			return super.retrieveObjectIdentityPrimaryKey(
					new ObjectIdentityImpl(
							oid.getType(),
							oid.getIdentifier().toString()));
		}
	}

	private static class ExtendedPermissionFactory extends DefaultPermissionFactory {
		private ExtendedPermissionFactory() {
			super();
			registerPublicPermissions(ExtendedPermission.class);
		}
	}

}
