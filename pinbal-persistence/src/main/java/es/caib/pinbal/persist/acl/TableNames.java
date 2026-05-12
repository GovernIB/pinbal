/**
 * 
 */
package es.caib.pinbal.persist.acl;

/**
 * Noms de les taules del mòdul ACL de spring-security.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TableNames {

	private TableNames() {
		throw new IllegalStateException("TableNames no es pot instanciar");
	}

	public static final String TABLE_PREFIX = ""; // ""pbl_";

	public static final String TABLE_OBJECT_IDENTITY = TABLE_PREFIX + "acl_object_identity";
	public static final String TABLE_SID = TABLE_PREFIX + "acl_sid";
	public static final String TABLE_CLASS = TABLE_PREFIX + "acl_class";
	public static final String TABLE_ENTRY = TABLE_PREFIX + "acl_entry";

	public static final String SEQUENCE_OBJECT_IDENTITY = TABLE_PREFIX + "acl_oid_seq"; //"acl_object_identity_seq";
	public static final String SEQUENCE_SID = TABLE_PREFIX + "acl_sid_seq";
	public static final String SEQUENCE_CLASS = TABLE_PREFIX + "acl_class_seq";
	public static final String SEQUENCE_ENTRY = TABLE_PREFIX + "acl_entry_seq";

}
