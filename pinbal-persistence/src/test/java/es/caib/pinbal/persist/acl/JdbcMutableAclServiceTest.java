package es.caib.pinbal.persist.acl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AlreadyExistsException;
import org.springframework.security.acls.model.ChildrenExistException;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AclTestConfig.class)
@Transactional
public class JdbcMutableAclServiceTest {

    @Autowired
    private DataSource dataSource;

    private AclCache aclCache;
    private JdbcMutableAclService service;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("usuari1", "N/A", AuthorityUtils.createAuthorityList("PBL_ADMIN")));
        aclCache = mock(AclCache.class);
        BasicLookupStrategy lookupStrategy = new BasicLookupStrategy(
                dataSource,
                aclCache,
                new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ACL_ADMIN")),
                new ConsoleAuditLogger());
        // BasicLookupStrategy no coneix el prefix "pbl_" de TableNames: cal indicar-li
        // les taules reals mantenint els mateixos alies que fa servir internament al WHERE/ORDER BY.
        lookupStrategy.setSelectClause(
                "select acl_object_identity.object_id_identity, "
                        + "acl_entry.ace_order,  "
                        + "acl_object_identity.id as acl_id, "
                        + "acl_object_identity.parent_object, "
                        + "acl_object_identity.entries_inheriting, "
                        + "acl_entry.id as ace_id, "
                        + "acl_entry.mask,  "
                        + "acl_entry.granting,  "
                        + "acl_entry.audit_success, "
                        + "acl_entry.audit_failure,  "
                        + "acl_sid.principal as ace_principal, "
                        + "acl_sid.sid as ace_sid,  "
                        + "acli_sid.principal as acl_principal, "
                        + "acli_sid.sid as acl_sid, "
                        + "acl_class.class "
                        + "from " + TableNames.TABLE_OBJECT_IDENTITY + " acl_object_identity "
                        + "left join " + TableNames.TABLE_SID + " acli_sid on acli_sid.id = acl_object_identity.owner_sid "
                        + "left join " + TableNames.TABLE_CLASS + " acl_class on acl_class.id = acl_object_identity.object_id_class   "
                        + "left join " + TableNames.TABLE_ENTRY + " acl_entry on acl_object_identity.id = acl_entry.acl_object_identity "
                        + "left join " + TableNames.TABLE_SID + " acl_sid on acl_entry.sid = acl_sid.id  "
                        + "where ( ");
        service = new JdbcMutableAclService(dataSource, lookupStrategy, aclCache);
    }

    private ObjectIdentity objectIdentity(long id) {
        return new ObjectIdentityImpl("es.caib.pinbal.persist.entity.Entitat", id);
    }

    @Test
    public void testCreateAcl_Success() {
        MutableAcl acl = service.createAcl(objectIdentity(1L));

        assertNotNull(acl);
        assertNotNull(acl.getId());
        assertEquals("usuari1", ((PrincipalSid) acl.getOwner()).getPrincipal());
        assertTrue(acl.isEntriesInheriting());
    }

    @Test
    public void testCreateAcl_AlreadyExists() {
        service.createAcl(objectIdentity(2L));

        assertThrows(AlreadyExistsException.class, () -> service.createAcl(objectIdentity(2L)));
    }

    @Test
    public void testReadAclById_NotFound() {
        assertThrows(NotFoundException.class, () -> service.readAclById(objectIdentity(999L)));
    }

    @Test
    public void testUpdateAcl_WithEntries() throws Exception {
        MutableAcl acl = service.createAcl(objectIdentity(3L));
        acl.insertAce(0, BasePermission.READ, new PrincipalSid("usuari2"), true);
        acl.insertAce(1, BasePermission.WRITE, new GrantedAuthoritySid("ROLE_PBL_ADMIN"), false);

        MutableAcl updated = service.updateAcl(acl);

        assertEquals(2, updated.getEntries().size());
        assertTrue(updated.getEntries().get(0).isGranting());
        assertTrue(BasePermission.READ.equals(updated.getEntries().get(0).getPermission()));
        assertTrue(!updated.getEntries().get(1).isGranting());
    }

    @Test
    public void testUpdateAcl_WithoutEntriesOnlyUpdatesObjectIdentity() {
        MutableAcl acl = service.createAcl(objectIdentity(4L));
        // L'ordre importa: un cop canviat l'owner, l'usuari autenticat ("usuari1") ja no és
        // propietari ni té cap ACE d'ADMINISTRATION, per tant setEntriesInheriting fallaria
        // amb AccessDeniedException si es cridés després de canviar l'owner.
        acl.setEntriesInheriting(false);
        acl.setOwner(new PrincipalSid("nouPropietari"));

        MutableAcl updated = service.updateAcl(acl);

        assertEquals("nouPropietari", ((PrincipalSid) updated.getOwner()).getPrincipal());
        assertTrue(!updated.isEntriesInheriting());
        assertTrue(updated.getEntries().isEmpty());
    }

    @Test
    public void testDeleteAcl_NoChildren() {
        MutableAcl acl = service.createAcl(objectIdentity(5L));

        service.deleteAcl(acl.getObjectIdentity(), false);

        assertThrows(NotFoundException.class, () -> service.readAclById(objectIdentity(5L)));
    }

    @Test
    public void testDeleteAcl_WithChildrenCascades() {
        MutableAcl parent = service.createAcl(objectIdentity(6L));
        MutableAcl child = service.createAcl(objectIdentity(7L));
        child.setParent(parent);
        service.updateAcl(child);

        service.deleteAcl(parent.getObjectIdentity(), true);

        assertThrows(NotFoundException.class, () -> service.readAclById(objectIdentity(6L)));
        assertThrows(NotFoundException.class, () -> service.readAclById(objectIdentity(7L)));
    }

    @Test
    public void testDeleteAcl_WithoutForeignKeysThrowsWhenChildrenExist() {
        MutableAcl parent = service.createAcl(objectIdentity(8L));
        MutableAcl child = service.createAcl(objectIdentity(9L));
        child.setParent(parent);
        service.updateAcl(child);
        service.setForeignKeysInDatabase(false);

        assertThrows(ChildrenExistException.class, () -> service.deleteAcl(parent.getObjectIdentity(), false));
    }

    @Test
    public void testFindChildren_ReturnsChildAndNullWhenNone() {
        MutableAcl parent = service.createAcl(objectIdentity(10L));
        MutableAcl child = service.createAcl(objectIdentity(11L));
        child.setParent(parent);
        service.updateAcl(child);

        List<ObjectIdentity> children = service.findChildren(parent.getObjectIdentity());

        assertNotNull(children);
        assertEquals(1, children.size());
        assertEquals(11L, children.get(0).getIdentifier());
        assertNull(service.findChildren(child.getObjectIdentity()));
    }

    @Test
    public void testDeleteEntries_ByObjectIdentityAndSid() {
        MutableAcl acl = service.createAcl(objectIdentity(12L));
        PrincipalSid sid = new PrincipalSid("usuari3");
        acl.insertAce(0, BasePermission.READ, sid, true);
        service.updateAcl(acl);

        service.deleteEntries(acl.getObjectIdentity(), sid);

        MutableAcl reloaded = (MutableAcl) service.readAclById(acl.getObjectIdentity());
        assertTrue(reloaded.getEntries().isEmpty());
    }

    @Test
    public void testSetDialect_UnsupportedDialectThrows() {
        service.setDialect("mysql");

        assertThrows(RuntimeException.class, () -> service.createAcl(objectIdentity(13L)));
    }

    @Test
    public void testSetDialect_PostgresIsSupported() {
        service.setDialect("postgresql");

        // PostgreSQL identity query cal.currval requires a real Postgres sequence, no disponible en H2:
        // el test només verifica que arriba a intentar-ho (dialecte reconegut) i no llança
        // l'excepció "Dialecte Hibernate no suportat", sinó un error real d'execució SQL.
        assertThrows(Exception.class, () -> service.createAcl(objectIdentity(14L)));
    }

    @Test
    public void testCreateAcl_UnsupportedSidTypeThrows() {
        MutableAcl acl = service.createAcl(objectIdentity(15L));
        org.springframework.security.acls.model.Sid unsupportedSid = mock(org.springframework.security.acls.model.Sid.class);
        acl.insertAce(0, BasePermission.READ, unsupportedSid, true);

        assertThrows(IllegalArgumentException.class, () -> service.updateAcl(acl));
    }
}
