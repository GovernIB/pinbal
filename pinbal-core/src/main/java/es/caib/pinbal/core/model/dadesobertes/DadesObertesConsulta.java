package es.caib.pinbal.core.model.dadesobertes;

import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta.DadesObertesConsultaResultat;
import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta.DadesObertesConsultaTipus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pbl_consulta_do")
public class DadesObertesConsulta {

    @Id
    private Long id;

    @Column(name = "entitatCodi", length = 64, nullable = false)
    private String entitatCodi;

    @Column(name = "entitatNom", length = 255, nullable = false)
    private String entitatNom;

    @Column(name = "entitatNif", length = 255, nullable = false)
    private String entitatNif;

    @Column(name = "entitatTipus", length = 16, nullable = false)
    private String entitatTipus;

    @Column(name = "departamentCodi", length = 250)
    private String departamentCodi;

    @Column(name = "departamentNom", length = 250)
    private String departamentNom;

    @Column(name = "procedimentCodi", length = 20, nullable = false)
    private String procedimentCodi;

    @Column(name = "procedimentNom", length = 255, nullable = false)
    private String procedimentNom;

    @Column(name = "serveiCodi", length = 64, nullable = false)
    private String serveiCodi;

    @Column(name = "serveiNom", length = 512, nullable = false)
    private String serveiNom;

    @Column(name = "emissorNom", length = 50, nullable = false)
    private String emissorNom;

    @Column(name = "emissorNif", length = 16, nullable = false)
    private String emissorNif;

    @Column(name = "consentiment", length = 16)
    private String consentiment;

    @Column(name = "finalitat", length = 250)
    private String finalitat;

    @Column(name = "titularDoctip", length = 16, nullable = false)
    private String titularDocumentTipus;

    @Column(name = "solicitudId", length = 64, nullable = false)
    private String solicitudId;

    @Column(name = "data", nullable = false)
    private Date data;

    @Column(name = "tipus", length = 16, nullable = false)
    @Enumerated(EnumType.STRING)
    private DadesObertesConsultaTipus tipus;

    @Column(name = "resultat", length = 16)
    @Enumerated(EnumType.STRING)
    private DadesObertesConsultaResultat resultat;

    @Column(name = "multiple")
    private boolean multiple = false;


    public void update(DadesObertesConsultaResultat resultat,
                       String solicitudId) {
        this.resultat = resultat;
        this.solicitudId = solicitudId;
    }

}
