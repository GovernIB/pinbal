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

	RECURSOS_HUMANOS((short)2),
	TRIBUTARIO((short)3),
	SANCIONADOR((short)14),
	AFILIACION_COTIZACION_SS((short)19),
	AUTORIZ_LICEN_CONCES_HOMOLOG((short)21),
	AYUDAS_BECAS_SUBVEN((short)21),
	CERTIFICADOS((short)22),
	CONTRATACION_PUB((short)23),
	CONVENIOS_COMUNIC((short)24),
	GESTION_ECON_PATRIM((short)25),
	DECLARAC_COMUNIC_INTERESADOS((short)26),
	INSPECTORA((short)27),
	PREMIOS((short)28),
	PRESTACIONES((short)29),
	REGISTROS_CENSOS((short)30),
	RESP_PATRIM_INDEM((short)31),
	REVISION_ACTOS_ADM_RECURSOS((short)32),
	SUGEREN_QUEJAS_CIUDADANOS((short)33),
	ADUANERO((short)34);

	private final short shortValue;
	private ProcedimentClaseTramiteEnumDto(short shortValue) {
		this.shortValue = shortValue;
	}
	public short getShortValue() {
		return shortValue;
	}

};
