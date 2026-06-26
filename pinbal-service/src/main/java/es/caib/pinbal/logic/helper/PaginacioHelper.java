/**
 * 
 */
package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.PaginaDto;
import es.caib.pinbal.logic.intf.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.logic.intf.dto.PaginacioAmbOrdreDto.OrdreDireccioDto;
import es.caib.pinbal.logic.intf.dto.PaginacioAmbOrdreDto.OrdreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helper per a convertir les dades de paginació entre el DTO
 * i Spring-Data.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@Component
public class PaginacioHelper {

	private final DtoMappingHelper dtoMappingHelper;
	
	public boolean esPaginacioActivada(PaginacioAmbOrdreDto dto) {
		return dto.getPaginaTamany() > 0;
	}

	public <T> Pageable toSpringDataPageable(PaginacioAmbOrdreDto dto, Map<String, String[]> mapeigPropietatsOrdenacio) {
		Sort sort = toSpringDataSort(dto.getOrdres(), mapeigPropietatsOrdenacio);
		return PageRequest.of(
				dto.getPaginaNum(),
				dto.getPaginaTamany(),
				sort != null ? sort : Sort.unsorted());
	}

	public <T> Pageable toSpringDataPageable(PaginacioAmbOrdreDto dto) {
		return toSpringDataPageable(dto, null);
	}

	public <T> Sort toSpringDataSort(PaginacioAmbOrdreDto dto) {
		return toSpringDataSort(dto.getOrdres(), null);
	}

	public Sort toSpringDataSort(List<OrdreDto> ordres, Map<String, String[]> mapeigPropietatsOrdenacio) {
		List<Order> orders = new ArrayList<Order>();
		if (ordres != null) {
			for (OrdreDto ordre : ordres) {
				Direction direccio = OrdreDireccioDto.DESCENDENT.equals(
						ordre.getDireccio()) ? Sort.Direction.DESC : Sort.Direction.ASC;
				if (mapeigPropietatsOrdenacio != null) {
					String[] mapeig = mapeigPropietatsOrdenacio.get(ordre.getCamp());
					if (mapeig != null) {
						for (String prop : mapeig) {
							orders.add(new Order(direccio, prop));
						}
					} else {
						orders.add(new Order(direccio, ordre.getCamp()));
					}
				} else {
					orders.add(new Order(direccio, ordre.getCamp()));
				}
			}
		}
		if (!orders.isEmpty())
			return Sort.by(orders);
		else
			return null;
	}

	public <S, T> PaginaDto<T> toPaginaDto(Page<S> page, Class<T> targetType) {
		return toPaginaDto(page, targetType, null);
	}

	public <S, T> PaginaDto<T> toPaginaDto(Page<S> page, Class<T> targetType, Converter<S, T> converter) {
		PaginaDto<T> dto = new PaginaDto<T>();
		dto.setNumero(page.getNumber());
		dto.setTamany(page.getSize());
		dto.setTotal(page.getTotalPages());
		dto.setElementsTotal(page.getTotalElements());
		dto.setAnteriors(page.hasPrevious());
		dto.setPrimera(page.isFirst());
		dto.setPosteriors(page.hasNext());
		dto.setDarrera(page.isLast());
		if (page.hasContent()) {
			if (converter == null) {
				dto.setContingut(dtoMappingHelper.convertirList(page.getContent(), targetType));
			} else {
				List<T> contingut = new ArrayList<T>();
				for (S element : page.getContent()) {
					contingut.add(converter.convert(element));
				}
				dto.setContingut(contingut);
			}
		}
		return dto;
	}
	
	
	public <S, T> PaginaDto<T> toPaginaDto(Page<S> page, Class<T> targetType, String param, ConverterParam<S, T> converter) {
		PaginaDto<T> dto = new PaginaDto<T>();
		dto.setNumero(page.getNumber());
		dto.setTamany(page.getSize());
		dto.setTotal(page.getTotalPages());
		dto.setElementsTotal(page.getTotalElements());
		dto.setAnteriors(page.hasPrevious());
		dto.setPrimera(page.isFirst());
		dto.setPosteriors(page.hasNext());
		dto.setDarrera(page.isLast());
		if (page.hasContent()) {
			if (converter == null) {
				dto.setContingut(dtoMappingHelper.convertirList(page.getContent(), targetType));
			} else {
				List<T> contingut = new ArrayList<T>();
				for (S element : page.getContent()) {
					contingut.add(converter.convert(element, param));
				}
				dto.setContingut(contingut);
			}
		}
		return dto;
	}

	public <T> PaginaDto<T> toPaginaDto(List<?> llista, Class<T> targetType) {
		PaginaDto<T> dto = new PaginaDto<T>();
		dto.setNumero(0);
		dto.setTamany(llista.size());
		dto.setTotal(1);
		dto.setElementsTotal(llista.size());
		dto.setAnteriors(false);
		dto.setPrimera(true);
		dto.setPosteriors(false);
		dto.setDarrera(true);
		if (targetType != null) {
			dto.setContingut(dtoMappingHelper.convertirList(llista, targetType));
		}
		return dto;
	}

	public <T> PaginaDto<T> getPaginaDtoBuida(Class<T> targetType) {
		PaginaDto<T> dto = new PaginaDto<T>();
		dto.setNumero(0);
		dto.setTamany(0);
		dto.setTotal(1);
		dto.setElementsTotal(0);
		dto.setAnteriors(false);
		dto.setPrimera(true);
		dto.setPosteriors(false);
		dto.setDarrera(true);
		return dto;
	}

	public interface Converter<S, T> {

		T convert(S source);

	}
	
	public interface ConverterParam<S, T> {

		T convert(S source, String param);

	}

	
	
//	public static Pageable toSpringDataPageable(
//			PaginacioAmbOrdreDto dto,
//			Map<String, String> mapeigPropietatsOrdenacio) {
//		List<Order> orders = new ArrayList<Order>();
//		if (dto.getOrdres() != null && !dto.getOrdres().isEmpty()) {
//			for (OrdreDto ordre: dto.getOrdres()) {
//				Direction direccio = OrdreDireccio.DESCENDENT.equals(ordre.getDireccio()) ? Sort.Direction.DESC : Sort.Direction.ASC;
//				String propietat = ordre.getCamp();
//				if (mapeigPropietatsOrdenacio != null) {
//					String mapeig = mapeigPropietatsOrdenacio.get(ordre.getCamp());
//					if (mapeig != null)
//						propietat = mapeig;
//				}
//				orders.add(new Order(
//						direccio,
//						propietat));
//			}
//			orders.add(new Order(
//					Sort.Direction.ASC,
//					"id"));
//		}
//		return new PageRequest(
//				dto.getPaginaNum(),
//				dto.getPaginaTamany(),
//				new Sort(orders));
//	}
//	
//	public static Pageable toSpringDataPageableWithoutId(
//			PaginacioAmbOrdreDto dto,
//			Map<String, String> mapeigPropietatsOrdenacio) {
//		List<Order> orders = new ArrayList<Order>();
//		if (dto.getOrdres() != null && !dto.getOrdres().isEmpty()) {
//			for (OrdreDto ordre: dto.getOrdres()) {
//				Direction direccio = OrdreDireccio.DESCENDENT.equals(ordre.getDireccio()) ? Sort.Direction.DESC : Sort.Direction.ASC;
//				String propietat = ordre.getCamp();
//				if (mapeigPropietatsOrdenacio != null) {
//					String mapeig = mapeigPropietatsOrdenacio.get(ordre.getCamp());
//					if (mapeig != null)
//						propietat = mapeig;
//				}
//				orders.add(new Order(
//						direccio,
//						propietat));
//			}
//		}
//		return new PageRequest(
//				dto.getPaginaNum(),
//				dto.getPaginaTamany(),
//				new Sort(orders));
//	}
//
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public static PaginaLlistatDto toPaginaLlistatDto(
//			Page<?> page,
//			DtoMappingHelper dtoMappingHelper,
//			Class targetClass) {
//		PaginaLlistatDto dto = new PaginaLlistatDto();
//		dto.setNumero(page.getNumber());
//		dto.setTamany(page.getSize());
//		dto.setTotal(page.getTotalPages());
//		dto.setElementsTotal(page.getTotalElements());
//		dto.setAnteriors(page.hasPrevious());
//		dto.setPrimera(page.isFirst());
//		dto.setPosteriors(page.hasNext());
//		dto.setDarrera(page.isLast());
//		if (page.hasContent()) {
//			if (targetClass != null)
//				dto.setContingut(
//						dtoMappingHelper.getMapperFacade().mapAsList(
//								page.getContent(),
//								targetClass));
//			else
//				dto.setContingut(page.getContent());
//		}
//		return dto;
//	}

}
