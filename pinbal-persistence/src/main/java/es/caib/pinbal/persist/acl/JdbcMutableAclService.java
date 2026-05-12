/* Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.caib.pinbal.persist.acl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


/**
 * Provides a base JDBC implementation of {@link MutableAclService}.
 * <p>
 * The default settings are for HSQLDB. If you are using a different database you
 * will probably need to set the {@link #setSidIdentityQuery(String) sidIdentityQuery} and
 * {@link #setClassIdentityQuery(String) classIdentityQuery} properties appropriately. The other queries,
 * SQL inserts and updates can also be customized to accomodate schema variations, but must produce results
 * consistent with those expected by the defaults.
 * <p>
 * See the appendix of the Spring Security reference manual for more information on the expected schema
 * and how it is used. Information on using PostgreSQL is also included.
 *
 * @author Ben Alex
 * @author Johannes Zlattinger
 */
@Slf4j
public class JdbcMutableAclService extends JdbcAclService implements PinbalMutableAclService {
	
	private static final String CLASS_IDENTITY_ORACLE = "SELECT " + TableNames.SEQUENCE_CLASS + ".CURRVAL FROM DUAL";
	private static final String SID_IDENTITY_ORACLE = "SELECT " + TableNames.SEQUENCE_SID + ".CURRVAL FROM DUAL";
	private static final String CLASS_IDENTITY_POSTGRES = "select currval(pg_get_serial_sequence('" + TableNames.TABLE_CLASS + "', 'id'))";
	private static final String SID_IDENTITY_POSTGRES = "select currval(pg_get_serial_sequence('" + TableNames.TABLE_SID + "', 'id'))";
	private static final String CLASS_IDENTITY_HSQL = "call identity()";
	private static final String SID_IDENTITY_HSQL = "call identity()";
	
    //~ Instance fields ================================================================================================

    private boolean foreignKeysInDatabase = true;
    private final AclCache aclCache;
    private String deleteEntryByObjectIdentityForeignKey = "delete from " + TableNames.TABLE_ENTRY + " where acl_object_identity=?";
    private String deleteEntryByObjectIdentityAndSid = "delete from " + TableNames.TABLE_ENTRY + " where acl_object_identity=? and sid=?";
    private String deleteObjectIdentityByPrimaryKey = "delete from " + TableNames.TABLE_OBJECT_IDENTITY + " where id=?";
    private String insertClass = "insert into " + TableNames.TABLE_CLASS + " (class) values (?)";
    private String insertEntry = "insert into " + TableNames.TABLE_ENTRY + " "
        + "(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)"
        + "values (?, ?, ?, ?, ?, ?, ?)";
    private String insertObjectIdentity = "insert into " + TableNames.TABLE_OBJECT_IDENTITY + " "
        + "(object_id_class, object_id_identity, owner_sid, entries_inheriting) " + "values (?, ?, ?, ?)";
    private String insertSid = "insert into " + TableNames.TABLE_SID + " (principal, sid) values (?, ?)";
    private String selectClassPrimaryKey = "select id from " + TableNames.TABLE_CLASS + " where class=?";
    private String selectObjectIdentityPrimaryKey = "select " + TableNames.TABLE_OBJECT_IDENTITY + ".id from " + TableNames.TABLE_OBJECT_IDENTITY + ", " + TableNames.TABLE_CLASS + " "
        + "where " + TableNames.TABLE_OBJECT_IDENTITY + ".object_id_class = " + TableNames.TABLE_CLASS + ".id and " + TableNames.TABLE_CLASS + ".class=? "
        + "and " + TableNames.TABLE_OBJECT_IDENTITY + ".object_id_identity = ?";
    private String selectSidPrimaryKey = "select id from " + TableNames.TABLE_SID + " where principal=? and sid=?";
    private String updateObjectIdentity = "update " + TableNames.TABLE_OBJECT_IDENTITY + " set "
        + "parent_object = ?, owner_sid = ?, entries_inheriting = ?" + " where id = ?";


    private String dialect = "oracle";

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    //~ Constructors ===================================================================================================

    public JdbcMutableAclService(DataSource dataSource, LookupStrategy lookupStrategy, AclCache aclCache) {

        super(dataSource, lookupStrategy);
        try (var conn = dataSource.getConnection()){
            dialect = conn.getMetaData().getDatabaseProductName().toLowerCase();
        } catch (Exception ex) {
            log.warn("JdbcMutableAclService: No ha estat possible obtenir les metadades de la connexió");

        }
        Assert.notNull(aclCache, "AclCache required");
        this.aclCache = aclCache;
    }

    //~ Methods ========================================================================================================

    public MutableAcl createAcl(ObjectIdentity objectIdentity) throws AlreadyExistsException {

        Assert.notNull(objectIdentity, "Object Identity required");
        // Check this object identity hasn't already been persisted
        if (retrieveObjectIdentityPrimaryKey(objectIdentity) != null) {
            throw new AlreadyExistsException("Object identity '" + objectIdentity + "' already exists");
        }

        // Need to retrieve the current principal, in order to know who "owns" this ACL (can be changed later on)
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var sid = new PrincipalSid(auth);

        // Create the acl_object_identity row
        createObjectIdentity(objectIdentity, sid);

        // Retrieve the ACL via superclass (ensures cache registration, proper retrieval etc)
        var acl = readAclById(objectIdentity);
        Assert.isInstanceOf(MutableAcl.class, acl, "MutableAcl should be been returned");

        return (MutableAcl) acl;
    }

    /**
     * Creates a new row in acl_entry for every ACE defined in the passed MutableAcl object.
     *
     * @param acl containing the ACEs to insert
     */
    protected void createEntries(final MutableAcl acl) {

        jdbcTemplate.batchUpdate(insertEntry,
            new BatchPreparedStatementSetter() {
                public int getBatchSize() {
                    return acl.getEntries().size();
                }

                public void setValues(PreparedStatement stmt, int i) throws SQLException {
                    AccessControlEntry entry_ = acl.getEntries().get(i);
                    Assert.isTrue(entry_ instanceof AccessControlEntryImpl, "Unknown ACE class");
                    AccessControlEntryImpl entry = (AccessControlEntryImpl) entry_;

                    stmt.setLong(1, ((Long) acl.getId()).longValue());
                    stmt.setInt(2, i);
                    stmt.setLong(3, createOrRetrieveSidPrimaryKey(entry.getSid(), true).longValue());
                    stmt.setInt(4, entry.getPermission().getMask());
                    stmt.setBoolean(5, entry.isGranting());
                    stmt.setBoolean(6, entry.isAuditSuccess());
                    stmt.setBoolean(7, entry.isAuditFailure());
                }
            });
    }

    /**
     * Creates an entry in the acl_object_identity table for the passed ObjectIdentity. The Sid is also
     * necessary, as acl_object_identity has defined the sid column as non-null.
     *
     * @param object to represent an acl_object_identity for
     * @param owner for the SID column (will be created if there is no acl_sid entry for this particular Sid already)
     */
    protected void createObjectIdentity(ObjectIdentity object, Sid owner) {

        var sidId = createOrRetrieveSidPrimaryKey(owner, true);
        var classId = createOrRetrieveClassPrimaryKey(object.getType(), true);
        jdbcTemplate.update(insertObjectIdentity, classId, object.getIdentifier(), sidId, Boolean.TRUE);
    }

    /**
     * Retrieves the primary key from {@code acl_class}, creating a new row if needed and the
     * {@code allowCreate} property is {@code true}.
     *
     * @param type to find or create an entry for (often the fully-qualified class name)
     * @param allowCreate true if creation is permitted if not found
     *
     * @return the primary key or null if not found
     */
	protected Long createOrRetrieveClassPrimaryKey(String type, boolean allowCreate) {

        var classIds = jdbcTemplate.queryForList(selectClassPrimaryKey, new Object[] {type}, Long.class);
        if (!classIds.isEmpty()) {
            return classIds.get(0);
        }
        if (!allowCreate) {
            return null;
        }
        jdbcTemplate.update(insertClass, type);
        Assert.isTrue(TransactionSynchronizationManager.isSynchronizationActive(), "Transaction must be running");
        return jdbcTemplate.queryForObject(getClassIdentityQuery(), Long.class);
    }

    /**
     * Retrieves the primary key from acl_sid, creating a new row if needed and the allowCreate property is
     * true.
     *
     * @param sid to find or create
     * @param allowCreate true if creation is permitted if not found
     *
     * @return the primary key or null if not found
     *
     * @throws IllegalArgumentException if the <tt>Sid</tt> is not a recognized implementation.
     */
    protected Long createOrRetrieveSidPrimaryKey(Sid sid, boolean allowCreate) {

        Assert.notNull(sid, "Sid required");
        String sidName;
        var sidIsPrincipal = true;
        if (sid instanceof PrincipalSid) {
            sidName = ((PrincipalSid) sid).getPrincipal();
        } else if (sid instanceof GrantedAuthoritySid) {
            sidName = ((GrantedAuthoritySid) sid).getGrantedAuthority();
            sidIsPrincipal = false;
        } else {
            throw new IllegalArgumentException("Unsupported implementation of Sid");
        }
        List<Long> sidIds = jdbcTemplate.queryForList(selectSidPrimaryKey, new Object[] {Boolean.valueOf(sidIsPrincipal), sidName},  Long.class);
        if (!sidIds.isEmpty()) {
            return sidIds.get(0);
        }
        if (!allowCreate) {
            return null;
        }
        jdbcTemplate.update(insertSid, Boolean.valueOf(sidIsPrincipal), sidName);
        Assert.isTrue(TransactionSynchronizationManager.isSynchronizationActive(), "Transaction must be running");
        return jdbcTemplate.queryForObject(getSidIdentityQuery(), Long.class);
    }

    public void deleteAcl(ObjectIdentity objectIdentity, boolean deleteChildren) throws ChildrenExistException {

        Assert.notNull(objectIdentity, "Object Identity required");
        Assert.notNull(objectIdentity.getIdentifier(), "Object Identity doesn't provide an identifier");
        if (deleteChildren) {
            var children = findChildren(objectIdentity);
            if (children != null) {
                for (ObjectIdentity child : children) {
                    deleteAcl(child, true);
                }
            }
        } else {
            if (!foreignKeysInDatabase) {
                // We need to perform a manual verification for what a FK would normally do
                // We generally don't do this, in the interests of deadlock management
                var children = findChildren(objectIdentity);
                if (children != null) {
                    throw new ChildrenExistException("Cannot delete '" + objectIdentity + "' (has " + children.size() + " children)");
                }
            }
        }
        Long oidPrimaryKey = retrieveObjectIdentityPrimaryKey(objectIdentity);
        // Delete this ACL's ACEs in the acl_entry table
        deleteEntries(oidPrimaryKey);
        // Delete this ACL's acl_object_identity row
        deleteObjectIdentity(oidPrimaryKey);
        // Clear the cache
        aclCache.evictFromCache(objectIdentity);
    }

    /**
     * Deletes all ACEs defined in the acl_entry table belonging to the presented ObjectIdentity primary key.
     *
     * @param oidPrimaryKey the rows in acl_entry to delete
     */
    protected void deleteEntries(Long oidPrimaryKey) {
        jdbcTemplate.update(deleteEntryByObjectIdentityForeignKey, oidPrimaryKey);
    }

    public void deleteEntries(ObjectIdentity oid, Sid sid) {

    	var oidPrimaryKey = retrieveObjectIdentityPrimaryKey(oid);
    	var sidId = createOrRetrieveSidPrimaryKey(sid, true);
    	jdbcTemplate.update(deleteEntryByObjectIdentityAndSid, oidPrimaryKey, sidId);
    }
    /**
     * Deletes a single row from acl_object_identity that is associated with the presented ObjectIdentity primary key.
     * <p>
     * We do not delete any entries from acl_class, even if no classes are using that class any longer. This is a
     * deadlock avoidance approach.
     *
     * @param oidPrimaryKey to delete the acl_object_identity
     */
    protected void deleteObjectIdentity(Long oidPrimaryKey) {
        // Delete the acl_object_identity row
        jdbcTemplate.update(deleteObjectIdentityByPrimaryKey, oidPrimaryKey);
    }

    /**
     * Retrieves the primary key from the acl_object_identity table for the passed ObjectIdentity. Unlike some
     * other methods in this implementation, this method will NOT create a row (use {@link
     * #createObjectIdentity(ObjectIdentity, Sid)} instead).
     *
     * @param oid to find
     *
     * @return the object identity or null if not found
     */
    protected Long retrieveObjectIdentityPrimaryKey(ObjectIdentity oid) {
        try {
            return jdbcTemplate.queryForObject(selectObjectIdentityPrimaryKey, Long.class, oid.getType(), oid.getIdentifier());
        } catch (DataAccessException notFound) {
            return null;
        }
    }

    /**
     * This implementation will simply delete all ACEs in the database and recreate them on each invocation of
     * this method. A more comprehensive implementation might use dirty state checking, or more likely use ORM
     * capabilities for create, update and delete operations of {@link MutableAcl}.
     */
    public MutableAcl updateAcl(MutableAcl acl) throws NotFoundException {
    	
    	Assert.notNull(acl.getId(), "Object Identity doesn't provide an identifier");
    	if (acl.getEntries().isEmpty()) {
            // Change the mutable columns in acl_object_identity
            updateObjectIdentity(acl);
            // Retrieve the ACL via superclass (ensures cache registration, proper retrieval etc)
            return (MutableAcl)super.readAclById(acl.getObjectIdentity());
        }
        // Delete this ACL's ACEs in the acl_entry table
        deleteEntries(retrieveObjectIdentityPrimaryKey(acl.getObjectIdentity()));
        // Create this ACL's ACEs in the acl_entry table
        createEntries(acl);
        // Change the mutable columns in acl_object_identity
        updateObjectIdentity(acl);
        // Clear the cache, including children
        clearCacheIncludingChildren(acl.getObjectIdentity());
        // Retrieve the ACL via superclass (ensures cache registration, proper retrieval etc)
        return (MutableAcl) super.readAclById(acl.getObjectIdentity());
    }

    private void clearCacheIncludingChildren(ObjectIdentity objectIdentity) {

        Assert.notNull(objectIdentity, "ObjectIdentity required");
        var children = findChildren(objectIdentity);
        if (children != null) {
            for (ObjectIdentity child : children) {
                clearCacheIncludingChildren(child);
            }
        }
        aclCache.evictFromCache(objectIdentity);
    }

    /**
     * Updates an existing acl_object_identity row, with new information presented in the passed MutableAcl
     * object. Also will create an acl_sid entry if needed for the Sid that owns the MutableAcl.
     *
     * @param acl to modify (a row must already exist in acl_object_identity)
     *
     * @throws NotFoundException if the ACL could not be found to update.
     */
    protected void updateObjectIdentity(MutableAcl acl) {

        Long parentId = null;
        if (acl.getParentAcl() != null) {
            Assert.isInstanceOf(ObjectIdentityImpl.class, acl.getParentAcl().getObjectIdentity(), "Implementation only supports ObjectIdentityImpl");
            var oii = (ObjectIdentityImpl) acl.getParentAcl().getObjectIdentity();
            parentId = retrieveObjectIdentityPrimaryKey(oii);
        }
        Assert.notNull(acl.getOwner(), "Owner is required in this implementation");
        var ownerSid = createOrRetrieveSidPrimaryKey(acl.getOwner(), true);
        var count = jdbcTemplate.update(updateObjectIdentity, parentId, ownerSid, Boolean.valueOf(acl.isEntriesInheriting()), acl.getId());
        if (count != 1) {
            throw new NotFoundException("Unable to locate ACL to update");
        }
    }

    public void setDeleteEntryByObjectIdentityForeignKeySql(String deleteEntryByObjectIdentityForeignKey) {
        this.deleteEntryByObjectIdentityForeignKey = deleteEntryByObjectIdentityForeignKey;
    }

    public void setDeleteObjectIdentityByPrimaryKeySql(String deleteObjectIdentityByPrimaryKey) {
        this.deleteObjectIdentityByPrimaryKey = deleteObjectIdentityByPrimaryKey;
    }

    public void setInsertClassSql(String insertClass) {
        this.insertClass = insertClass;
    }

    public void setInsertEntrySql(String insertEntry) {
        this.insertEntry = insertEntry;
    }

    public void setInsertObjectIdentitySql(String insertObjectIdentity) {
        this.insertObjectIdentity = insertObjectIdentity;
    }

    public void setInsertSidSql(String insertSid) {
        this.insertSid = insertSid;
    }

    public void setClassPrimaryKeyQuery(String selectClassPrimaryKey) {
        this.selectClassPrimaryKey = selectClassPrimaryKey;
    }

    public void setObjectIdentityPrimaryKeyQuery(String selectObjectIdentityPrimaryKey) {
        this.selectObjectIdentityPrimaryKey = selectObjectIdentityPrimaryKey;
    }

    public void setSidPrimaryKeyQuery(String selectSidPrimaryKey) {
        this.selectSidPrimaryKey = selectSidPrimaryKey;
    }

    public void setUpdateObjectIdentity(String updateObjectIdentity) {
        this.updateObjectIdentity = updateObjectIdentity;
    }

    /**
     * @param foreignKeysInDatabase if false this class will perform additional FK constrain checking, which may
     * cause deadlocks (the default is true, so deadlocks are avoided but the database is expected to enforce FKs)
     */
    public void setForeignKeysInDatabase(boolean foreignKeysInDatabase) {
        this.foreignKeysInDatabase = foreignKeysInDatabase;
    }
    
    private String getClassIdentityQuery() {

        if (dialect.contains("oracle")) {
			return CLASS_IDENTITY_ORACLE;
		}
        if (dialect.contains("postgres")) {
			return CLASS_IDENTITY_POSTGRES;
		}
        if (dialect.contains("hsql") || dialect.contains("h2")) {
			return CLASS_IDENTITY_HSQL;
		}
        throw new RuntimeException("Dialecte Hibernate no suportat pel mòdul ACL");
	}

	private String getSidIdentityQuery() {
        if (dialect.contains("oracle")) {
            return SID_IDENTITY_ORACLE;
        }
        if (dialect.contains("postgres")) {
            return SID_IDENTITY_POSTGRES;
        }
        if (dialect.contains("hsql") || dialect.contains("h2")) {
            return SID_IDENTITY_HSQL;
        }
        throw new RuntimeException("Dialecte Hibernate no suportat pel mòdul ACL");
	}

}
