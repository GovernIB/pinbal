/**
 * 
 */
package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.datatables.ServerSideRequest;
import es.caib.pinbal.back.datatables.ServerSideResponse;
import es.caib.pinbal.logic.intf.dto.CacheDto;
import es.caib.pinbal.logic.intf.dto.PaginaDto;
import es.caib.pinbal.logic.intf.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;

/**
 * Controlador per a la gestió de cache
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/cache")
public class CacheController extends BaseController {
		
	@Autowired
	private AplicacioService aplicacioService;


	@GetMapping
	public String get( HttpServletRequest request ) {
		return "cacheList";
	}

	@GetMapping("/datatable")
	@ResponseBody
	public ServerSideResponse<CacheDto, String> datatable(HttpServletRequest request ) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);

		PaginaDto<CacheDto> allCaches = aplicacioService.getAllCaches();
		Page<CacheDto> page = new PageImpl<CacheDto>(allCaches.getContingut(), Pageable.unpaged(), allCaches.getContingut().size());
		return new ServerSideResponse<>(serverSideRequest, page);
	}
	
	@GetMapping("/{cacheValue}/buidar")
	public String buidar(HttpServletRequest request, @PathVariable String cacheValue) {

		aplicacioService.removeCache(cacheValue);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", "cache.controller.esborrada.ok");
	}

	@GetMapping("/all/buidar")
	public String buidarTot(HttpServletRequest request) {

		aplicacioService.removeAllCaches();
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", "cache.controller.esborrada.ok");
	}
}
