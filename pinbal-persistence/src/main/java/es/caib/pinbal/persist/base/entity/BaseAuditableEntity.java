package es.caib.pinbal.persist.base.entity;

import es.caib.pinbal.logic.intf.base.model.Resource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base per a definir les demés entitats de l'aplicació.
 *
 * @param <R> classe del recurs associat.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditableEntity<R extends Resource<?>, PK extends Serializable>
		extends BaseResourceEntity<R, PK>
		implements AuditableEntity {

	@CreatedBy
	@Column(name = "createdby_codi", length = 64, nullable = false)
	private String createdBy;
	@CreatedDate
	@Column(name = "createddate", nullable = false)
	private LocalDateTime createdDate;
	@LastModifiedBy
	@Column(name = "lastmodifiedby_codi", length = 64)
	private String lastModifiedBy;
	@LastModifiedDate
	@Column(name = "lastmodifieddate")
	private LocalDateTime lastModifiedDate;

	@Override
	public void updateCreated(
			String createdBy,
			LocalDateTime createdDate) {
		this.createdBy = createdBy;
		this.createdDate = (createdDate != null) ? createdDate : LocalDateTime.now();
	}

	@Override
	public void updateLastModified(
			String lastModifiedBy,
			LocalDateTime lastModifiedDate) {
		this.lastModifiedBy = lastModifiedBy;
		this.lastModifiedDate = (lastModifiedDate != null) ? lastModifiedDate : LocalDateTime.now();
	}

}