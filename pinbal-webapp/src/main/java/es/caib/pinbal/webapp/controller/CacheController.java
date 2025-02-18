/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.core.dto.CacheDto;
import es.caib.pinbal.core.dto.PaginaDto;
import es.caib.pinbal.core.service.AplicacioService;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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


	@RequestMapping(method = RequestMethod.GET)
	public String get( HttpServletRequest request ) {
		return "cacheList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public ServerSideResponse<CacheDto, String> datatable(HttpServletRequest request ) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);

		PaginaDto<CacheDto> allCaches = aplicacioService.getAllCaches();
		Page<CacheDto> page = new PageImpl<CacheDto>(allCaches.getContingut(), null, allCaches.getContingut().size());
		return new ServerSideResponse<>(serverSideRequest, page);
	}
	
	@RequestMapping(value = "/{cacheValue}/buidar", method = RequestMethod.GET)
	public String buidar(HttpServletRequest request, @PathVariable String cacheValue) {

		aplicacioService.removeCache(cacheValue);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", "cache.controller.esborrada.ok");
	}

	@RequestMapping(value = "/all/buidar", method = RequestMethod.GET)
	public String buidarTot(HttpServletRequest request) {

		aplicacioService.removeAllCaches();
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", "cache.controller.esborrada.ok");
	}
}
