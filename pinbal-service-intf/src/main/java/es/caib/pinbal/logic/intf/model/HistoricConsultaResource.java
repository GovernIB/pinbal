package es.caib.pinbal.logic.intf.model;

import es.caib.pinbal.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.pinbal.logic.intf.base.annotation.ResourceArtifact;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.model.BaseResource;
import es.caib.pinbal.logic.intf.base.model.ResourceArtifactType;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * Recurs per a la consulta de consultes SCSP històriques (arxivades).
 * <p>
 * L'àmbit de dades (administrador, delegat, auditor o superauditor) el determina
 * el rol de l'usuari autenticat al servei, no una restricció d'accés del recurs.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        accessConstraints = @ResourceAccessConstraint(
                type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
                grantedPermissions = { PermissionEnum.READ }
        ),
        artifacts = @ResourceArtifact(
                type = ResourceArtifactType.REPORT,
                code = "justificant",
                requiresId = true
        )
)
public class HistoricConsultaResource extends BaseResource<Long> {

    // Camps de la graella
    private String scspPeticionId;
    private Date creacioData;
    private String creacioUsuariNomCodi;
    private String funcionariNomAmbDocument;
    private String procedimentCodiNom;
    private String serveiCodiNom;
    private String estat;
    private Date dataEsperadaResposta;
    private String justificantEstat;
    private boolean recobriment;
    private boolean multiple;
    private ResourceReference<EntitatResource, Long> entitat;

    // Camps addicionals del detall
    private String scspSolicitudId;
    private String titularDocumentTipus;
    private String titularDocumentNum;
    private String titularNomComplet;
    private String departamentNom;
    private String finalitat;
    private String consentiment;
    private String expedientId;
    private String entitatNom;
    private String entitatCif;
    private String procedimentCodi;
    private String procedimentNom;
    private Date respostaData;
    private boolean justificantEstatError;
    private String justificantError;
    private Long pareId;

}
