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

	ADUANERO((short)1),
	AFILIACION_COTIZACION_SS((short)2),
	AUTORIZ_LICEN_CONCES_HOMOLOG((short)3),
	AYUDAS_BECAS_SUBVEN((short)4),
	CERTIFICADOS((short)5),
	CONTRATACION_PUB((short)6),
	CONVENIOS_COMUNIC((short)7),
	GESTION_ECON_PATRIM((short)8),
	DECLARAC_COMUNIC_INTERESADOS((short)9),
	INSPECTORA((short)10),
	PREMIOS((short)11),
	PRESTACIONES((short)12),
	RECURSOS_HUMANOS((short)13),
	REGISTROS_CENSOS((short)14),
	RESP_PATRIM_INDEM((short)15),
	REVISION_ACTOS_ADM_RECURSOS((short)16),
	SANCIONADOR((short)17),
	SUGEREN_QUEJAS_CIUDADANOS((short)18),
	TRIBUTARIO((short)19);

	private final short shortValue;
	private ProcedimentClaseTramiteEnumDto(short shortValue) {
		this.shortValue = shortValue;
	}
	public short getShortValue() {
		return shortValue;
	}

};
