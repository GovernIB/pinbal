/**
 * 
 */
package es.caib.pinbal.core.dto;


import java.io.Serializable;

/**
 * Tipus de servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum ServeiTipusEnumDto implements Serializable {
	/** Per invocar el WS de backoffice configurat segons el codi de petició. */
	BACKOFFICE,
	/** Per distinguir la entitat destinatària a partir de la petició i escollir una  ruta. */
	ENRUTADOR,
	/** Per consultar totes les URL's i ajuntar una resposta. */
	ENRUTADOR_MULTIPLE	
}
