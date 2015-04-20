/**
 * 
 */
package es.caib.pinbal.core.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import es.caib.pinbal.core.dto.OrdreDto;
import es.caib.pinbal.core.dto.OrdreDto.OrdreDireccio;
import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;

/**
 * Helper per a convertir les dades de paginació entre el DTO
 * i Spring-Data.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PaginacioHelper {

	public static Pageable toSpringDataPageable(
			PaginacioAmbOrdreDto dto,
			Map<String, String> mapeigPropietatsOrdenacio) {
		List<Order> orders = new ArrayList<Order>();
		if (dto.getOrdres() != null) {
			for (OrdreDto ordre: dto.getOrdres()) {
				Direction direccio = OrdreDireccio.DESCENDENT.equals(ordre.getDireccio()) ? Sort.Direction.DESC : Sort.Direction.ASC;
				String propietat = ordre.getCamp();
				if (mapeigPropietatsOrdenacio != null) {
					String mapeig = mapeigPropietatsOrdenacio.get(ordre.getCamp());
					if (mapeig != null)
						propietat = mapeig;
				}
				orders.add(new Order(
						direccio,
						propietat));
			}
		}
		return new PageRequest(
				dto.getPaginaNum(),
				dto.getPaginaTamany(),
				new Sort(orders));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static PaginaLlistatDto toPaginaLlistatDto(
			Page<?> page,
			DtoMappingHelper dtoMappingHelper,
			Class targetClass) {
		PaginaLlistatDto dto = new PaginaLlistatDto();
		dto.setNumero(page.getNumber());
		dto.setTamany(page.getSize());
		dto.setTotal(page.getTotalPages());
		dto.setElementsTotal(page.getTotalElements());
		dto.setAnteriors(page.hasPreviousPage());
		dto.setPrimera(page.isFirstPage());
		dto.setPosteriors(page.hasNextPage());
		dto.setDarrera(page.isLastPage());
		if (page.hasContent()) {
			if (targetClass != null)
				dto.setContingut(
						dtoMappingHelper.getMapperFacade().mapAsList(
								page.getContent(),
								targetClass));
			else
				dto.setContingut(page.getContent());
		}
		return dto;
	}

}
