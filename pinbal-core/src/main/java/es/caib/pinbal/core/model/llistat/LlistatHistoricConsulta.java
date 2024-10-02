package es.caib.pinbal.core.model.llistat;

import es.caib.pinbal.core.dto.EstatTipus;
import es.caib.pinbal.core.dto.JustificantEstat;
import es.caib.pinbal.core.model.Consulta;
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
@Table(name = "pbl_consulta_hist_list")
public class LlistatHistoricConsulta {

    @Id
    private Long id;

    @Column(name = "peticioId", length = 26, nullable = false)
    private String peticioId;

    @Column(name = "solicitudId", length = 64, nullable = false)
    private String solicitudId;

    @Column(name = "data", nullable = false)
    private Date data;

    @Column(name = "departament", length = 250, nullable = false)
    private String departamentNom;

    @Column(name = "recobriment")
    private boolean recobriment = false;

    @Column(name = "multiple")
    private boolean multiple = false;

    @Column(name = "usuariCodi", length = 64, nullable = false)
    private String usuariCodi;

    @Column(name = "usuariNom", length = 200, nullable = false)
    private String usuariNom;

    @Column(name = "funcionariNif", length = 16)
    private String funcionariNif;

    @Column(name = "funcionariNom", length = 128)
    private String funcionariNom;

    @Column(name = "titularNom", length = 122)
    private String titularNom;

    @Column(name = "titularDoctip", length = 16, nullable = false)
    private String titularDocumentTipus;

    @Column(name = "titularDocnum", length = 14, nullable = false)
    private String titularDocumentNum;

    @Column(name = "procedimentId", nullable = false)
    private Long procedimentId;

    @Column(name = "procedimentCodi", length = 20, nullable = false)
    private String procedimentCodi;

    @Column(name = "procedimentNom", length = 255, nullable = false)
    private String procedimentNom;

    @Column(name = "serveiCodi", length = 64, nullable = false)
    private String serveiCodi;

    @Column(name = "serveiNom", length = 512, nullable = false)
    private String serveiNom;

    @Column(name = "estat", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstatTipus estat;

    @Column(name = "error", length = Consulta.ERROR_SCSP_MAX_LENGTH)
    private String error;

    @Column(name = "justificantEstat", nullable = false)
    @Enumerated(EnumType.STRING)
    private JustificantEstat justificantEstat;

//    @Column(name = "justificant_error", length = Consulta.ERROR_JUSTIFICANT_MAX_LENGTH)
//    private String justificantError;

    @Column(name = "entitatId")
    private Long entitatId;

    @Column(name = "entitatCodi", length = 64, nullable = false)
    private String entitatCodi;

    @Column(name = "pareId")
    private Long pareId;

}
