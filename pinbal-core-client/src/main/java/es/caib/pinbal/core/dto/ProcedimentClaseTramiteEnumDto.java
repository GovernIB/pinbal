/**
 * 
 */
package es.caib.pinbal.core.dto;

/**
 * Enumerat amb possibles valors del camp SCSP de procediment ClaseTramite.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum ProcedimentClaseTramiteEnumDto {

	ADUANERO(1),
	AFILIACION_COTIZACION_SS(2),
	AUTORIZ_LICEN_CONCES_HOMOLOG(3),
	AYUDAS_BECAS_SUBVEN(4),
	CERTIFICADOS(5),
	CONTRATACION_PUB(6),
	CONVENIOS_COMUNIC(7),
	GESTION_ECON_PATRIM(8),
	DECLARAC_COMUNIC_INTERESADOS(9),
	INSPECTORA(10),
	PREMIOS(11),
	PRESTACIONES(12),
	RECURSOS_HUMANOS(13),
	REGISTROS_CENSOS(14),
	RESP_PATRIM_INDEM(15),
	REVISION_ACTOS_ADM_RECURSOS(16),
	SANCIONADOR(17),
	SUGEREN_QUEJAS_CIUDADANOS(18),
	TRIBUTARIO(19);

	private final int intValue;
	private ProcedimentClaseTramiteEnumDto(int intValue) {
		this.intValue = intValue;
	}
	public int getIntValue() {
		return intValue;
	}

};
