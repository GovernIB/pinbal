package es.caib.pinbal.persist.entity.acl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Classe del model de dades que representa un SID d'una ACL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@EqualsAndHashCode( callSuper = false )
@Entity
@Table(name = "pbl_acl_sid")
public class AclSidEntity extends AbstractPersistable<Long> {

	@Column(name = "principal", nullable = false)
	private boolean principal;
	@Column(name = "sid", length = 100, nullable = false)
	private String sid;

	private static final long serialVersionUID = -2299453443943600172L;

}
