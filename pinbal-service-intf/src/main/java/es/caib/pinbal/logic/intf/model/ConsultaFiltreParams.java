package es.caib.pinbal.logic.intf.model;

import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Camps del filtre de {@link ConsultaResource}/{@link HistoricConsultaResource} (artefacte
 * FILTER "FILTER_CONSULTA"). Aquesta classe no es fa servir mai per rebre dades reals: només
 * serveix perquè el mecanisme genèric de metadades de camps (HAL-FORMS) sàpiga quin tipus/format
 * ha de renderitzar cada camp del filtre al frontend (select per a l'estat, picker per al
 * procediment, dates, etc.).
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
public class ConsultaFiltreParams implements Serializable {

    private String scspPeticionId;
    private ResourceReference<ProcedimentResource, Long> procediment;
    private String serveiCodiNom;
    private ConsultaDto.EstatTipus estat;
    private Date dataInici;
    private Date dataFi;
    private String titularNomComplet;
    private String titularDocumentNum;
    private ResourceReference<EntitatResource, Long> entitat;
    private String funcionariNomAmbDocument;
    private Boolean recobriment;
    private Boolean multiple;

}
