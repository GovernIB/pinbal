package es.caib.pinbal.persist.entity.acl;

import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.List;

/**
 * Classe del model de dades que representa un SID d'una ACL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode( callSuper = false )
@Entity
@Table(name = "pbl_acl_object_identity")
public class AclObjectIdentityEntity extends AbstractPersistable<Long> {

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "object_id_class", nullable = false)
	private AclClassEntity classname;
	@Column(name = "object_id_identity", nullable = false)
	private Long objectId;
	@EqualsAndHashCode.Exclude
	@Column(name = "parent_object", nullable = true)
	private Long parentObject;
	@EqualsAndHashCode.Exclude
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "owner_sid", nullable = false)
	private AclSidEntity ownerSid;
	@EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "aclObjectIdentity", fetch = FetchType.EAGER)
	private List<AclEntryEntity> entries;
	@EqualsAndHashCode.Exclude
	@Builder.Default
	@Column(name = "entries_inheriting", nullable = false)
	private boolean entriesInheriting = true;
	
	private static final long serialVersionUID = -2299453443943600172L;

}
