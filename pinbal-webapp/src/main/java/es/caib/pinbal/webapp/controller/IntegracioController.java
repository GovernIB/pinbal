/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.pinbal.core.dto.IntegracioAccioDto;
import es.caib.pinbal.core.dto.IntegracioAccioEstatEnumDto;
import es.caib.pinbal.core.dto.IntegracioDto;
import es.caib.pinbal.core.dto.IntegracioEnumDto;
import es.caib.pinbal.core.service.AplicacioService;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;
import es.caib.pinbal.webapp.helper.EnumHelper;

/**
 * Controlador per a la consulta d'accions de les integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/integracio")
public class IntegracioController extends BaseController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "IntegracioController.session.filtre";

	@Autowired
	private AplicacioService aplicacioService;



	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		return getAmbCodi(request, null, model);
	}
	@RequestMapping(value = "/{codi}", method = RequestMethod.GET)
	public String getAmbCodi(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		List<IntegracioDto> integracions = aplicacioService.integracioFindAll();
		for (IntegracioDto integracio: integracions) {
			for (IntegracioEnumDto integracioEnum: IntegracioEnumDto.values()) {
				if (integracio.getCodi() == integracioEnum.name()) {
					integracio.setNom(
							EnumHelper.getOneOptionForEnum(
									IntegracioEnumDto.class,
									"integracio.list.pipella." + integracio.getCodi()).getText());
				}
			}
			int nErrors = 0;
			List<IntegracioAccioDto> accions = aplicacioService.integracioFindDarreresAccionsByCodi(integracio.getCodi());
			for (IntegracioAccioDto integracioAccioDto : accions) {
				if (integracioAccioDto.getEstat() == IntegracioAccioEstatEnumDto.ERROR) {
					nErrors++;
				}
			}
			integracio.setNumErrors(nErrors);
		}
		
		model.addAttribute(
				"integracions",
				integracions);
		if (codi != null) {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					codi);
		} else if (integracions.size() > 0) {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					integracions.get(0).getCodi());
		}
		model.addAttribute(
				"codiActual",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE));
		return "integracioList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public ServerSideResponse<IntegracioAccioDto, Long> datatable(
			HttpServletRequest request) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException, SQLException {
		
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		
		String codi = (String)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		List<IntegracioAccioDto> accions = null;
		if (codi != null) {
			accions = aplicacioService.integracioFindDarreresAccionsByCodi(codi);
		} else {
			accions = new ArrayList<IntegracioAccioDto>();
		}
		
		return new ServerSideResponse<IntegracioAccioDto, Long>(serverSideRequest, accions);
	}

	@RequestMapping(value = "/{codi}/{id}", method = RequestMethod.GET)
	public String detall(
			HttpServletRequest request,
			@PathVariable String codi,
			@PathVariable Long id,
			Model model) {
		List<IntegracioAccioDto> accions = aplicacioService.integracioFindDarreresAccionsByCodi(codi);
		
		IntegracioAccioDto integracio = null;
		for (IntegracioAccioDto integracioAccioDto : accions) {
			if (integracioAccioDto.getId().equals(id)) {
				integracio = integracioAccioDto;
			}
		}
		if (integracio != null) {
			model.addAttribute(
					"integracio",
					integracio);
			model.addAttribute(
					"codiActual", 
					codi);
			return "integracioDetall";
			
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"integracio.list.no.existeix"));
			
			return "redirect:../../integracio/" + codi;

		}
	}

}
