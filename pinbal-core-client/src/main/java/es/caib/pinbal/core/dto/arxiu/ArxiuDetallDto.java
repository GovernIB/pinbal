/**
 * 
 */
package es.caib.pinbal.core.dto.arxiu;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Informació d'un contingut emmagatzemada a l'arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ArxiuDetallDto extends ArxiuContingutDto {

	private String eniVersio;
	private String eniIdentificador;
	private NtiOrigenEnumDto eniOrigen;
	private Date eniDataObertura;
	private String eniClassificacio;
	private ExpedientEstatEnumDto eniEstat;
	private List<String> eniOrgans;
	private List<String> eniInteressats;
	private Date eniDataCaptura;
	private DocumentNtiEstadoElaboracionEnumDto eniEstatElaboracio;
	private String eniTipusDocumental;
	private String eniTipusDocumentalAddicional;
	private String eniFormat;
	private String eniDocumentOrigenId;
	private String serieDocumental;

	private Map<String, Object> metadadesAddicionals;

	private String contingutTipusMime;
	private String contingutArxiuNom;
	
	private List<ArxiuFirmaDto> firmes;
	private List<ArxiuContingutDto> fills;

	private ArxiuEstatEnumDto arxiuEstat;

	private static final long serialVersionUID = -2124829280908976623L;

}
