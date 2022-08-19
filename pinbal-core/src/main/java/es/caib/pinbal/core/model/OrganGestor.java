package es.caib.pinbal.core.model;

import java.util.List;

import javax.persistence.*;

import es.caib.pinbal.core.dto.OrganGestorEstatEnum;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.pinbal.core.audit.PinbalAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe de model de dades que conté la informació dels organs gestors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "pbl_organ_gestor",
		uniqueConstraints = @UniqueConstraint(name = "pbl_organ_gestor_uk", columnNames = { "codi", "entitat_id" }))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class OrganGestor extends PinbalAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;

	@Column(name = "nom", length = 1000)
	private String nom;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	private Entitat entitat;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "pare_id")
	private OrganGestor pare;

	@Column(name = "actiu")
	private boolean actiu;

	@Column(name = "estat")
	@Enumerated(EnumType.STRING)
	private OrganGestorEstatEnum estat;

	@OneToMany(mappedBy = "organGestor", fetch = FetchType.LAZY)
	private List<Procediment> procediments;

	@OneToMany(mappedBy = "pare", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	private List<OrganGestor> fills;

	@PreRemove
	private void preRemove() {
		for (OrganGestor fill : this.getFills()) {
			fill.setPare(null);
		}
	}

	private static final long serialVersionUID = 458331024861203562L;

}
