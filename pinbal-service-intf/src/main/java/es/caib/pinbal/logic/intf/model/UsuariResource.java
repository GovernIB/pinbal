package es.caib.pinbal.logic.intf.model;

import es.caib.pinbal.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.model.BaseResource;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * Informació d'un usuari de l'aplicació.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = UsuariResource.Fields.codi,
	quickFilterFields = { UsuariResource.Fields.codi, UsuariResource.Fields.nom },
	accessConstraints = @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
		grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
	)
)
public class UsuariResource extends BaseResource<String> {

	@NotNull
	@Size(max = 64)
	private String codi;
	@Size(max = 15)
	private String nif;
	@Size(max = 200)
	private String nom;
	private boolean inicialitzat = false;
	@Size(max = 200)
	private String email;
	@Size(max = 2)
	private String idioma;
	private Set<EntitatUsuariResource> entitats;

	// Valors per defecte
	// ==============================================================
	protected ResourceReference<EntitatResource, Long> entitat;
	protected ResourceReference<ProcedimentResource, Long> procediment;
	protected ResourceReference<ServeiResource, Long> servei;

	@Size(max = 250)
	private String departament;
	@Size(max = 250)
	private String finalitat;
	private Integer numElementsPagina;

	public String getId() {
		return codi;
	}

	@Override
	public void setId(String id) {
		this.id = id;
		this.codi = id;
	}

}
