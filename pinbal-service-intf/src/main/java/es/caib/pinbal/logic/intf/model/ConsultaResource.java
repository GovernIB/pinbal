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
 * Recurs per a la consulta de consultes SCSP recents.
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
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = "justificant",
                        requiresId = true
                ),
                // Només aplicable a consultes múltiples: zip amb tots els justificants.
                @ResourceArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = "justificantZip",
                        requiresId = true
                ),
                // Només per a administrador (restricció al servei de domini): zip amb tots els missatges XML.
                @ResourceArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = "xmlZip",
                        requiresId = true
                ),
                // Arbre amb les dades de la resposta (equivalent al "dadesResposta" del JSP).
                @ResourceArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = "respostaArbre",
                        requiresId = true
                ),
                // Reintent de generació/custòdia del justificant quan ha donat error.
                @ResourceArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = "justificantReintentar",
                        requiresId = true
                ),
                // Metadades dels camps del filtre de la graella (veure ConsultaFiltreParams).
                @ResourceArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = "FILTER_CONSULTA",
                        formClass = ConsultaFiltreParams.class
                )
        }
)
public class ConsultaResource extends BaseResource<Long> {

    // Camps de la graella
    private String scspPeticionId;
    private Date creacioData;
    private String creacioUsuariNomCodi;
    private String funcionariNomAmbDocument;
    private String procedimentCodiNom;
    private String serveiCodiNom;
    private String titularDocumentTipus;
    private String titularDocumentNum;
    private String titularNomComplet;
    private String estat;
    private Date dataEsperadaResposta;
    private String justificantEstat;
    private boolean recobriment;
    private boolean multiple;

    // Camp només de filtre (picker de procediment); mai es retorna emplenat.
    private ResourceReference<ProcedimentResource, Long> procediment;

    // Camps addicionals del detall
    private String scspSolicitudId;
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
    private String error;

    // Dades per a la visualització de l'XML de la petició/resposta (consulta.info.veure.xml)
    private boolean hiHaPeticio;
    private boolean peticioGenerada;
    private String peticioXml;
    private boolean hiHaResposta;
    private String respostaXml;

}
