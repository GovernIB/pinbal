package es.caib.pinbal.logic.intf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Objecte DTO amb informació d'un emisor certificat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class EmissorCertDto extends AbstractIdentificable<Long> implements Serializable {
	
	private Long id;
	
	private String nom;
	private String cif;
	private Date dataBaixa;

	private static final long serialVersionUID = -2822106398117415005L;
	
}
