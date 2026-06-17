/**
 * 
 */
package es.caib.pinbal.ejb;

import es.caib.pinbal.logic.intf.dto.IdiomaEnumDto;
import es.caib.pinbal.plugin.dadescomuns.Municipi;
import es.caib.pinbal.plugin.dadescomuns.Pais;
import es.caib.pinbal.plugin.dadescomuns.Provincia;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de DadesExternesService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class DadesExternesService extends AbstractService<es.caib.pinbal.logic.intf.service.DadesExternesService> implements es.caib.pinbal.logic.intf.service.DadesExternesService {


	@Override
	@RolesAllowed("**")
	public List<Provincia> findProvincies(IdiomaEnumDto idioma) {
		return getDelegateService().findProvincies(idioma);
	}

	@Override
	@RolesAllowed("**")
	public List<Municipi> findMunicipisPerProvincia(String provinciaCodi) {
		return getDelegateService().findMunicipisPerProvincia(provinciaCodi);
	}

	@Override
	@RolesAllowed("**")
	public List<Pais> findPaisos(IdiomaEnumDto idioma) {
		return getDelegateService().findPaisos(idioma);
	}

}
