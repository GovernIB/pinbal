package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.InformeRepresentantFiltreCommand;
import es.caib.pinbal.back.datatables.ServerSideRequest;
import es.caib.pinbal.back.datatables.ServerSideResponse;
import es.caib.pinbal.back.helper.EntitatHelper;
import es.caib.pinbal.back.helper.RequestSessionHelper;
import es.caib.pinbal.back.helper.RolHelper;
import es.caib.pinbal.logic.intf.dto.InformeProcedimentServeiDto;
import es.caib.pinbal.logic.intf.dto.InformeRepresentantFiltreDto;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador per a l'informe del representant.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/informeRepresentant")
public class InformeRepresentantController extends BaseController {
	
	public static final String SESSION_ATTRIBUTE_FILTRE = "InformeRepresentantController.session.filtre";
	
	@Autowired
	private ConsultaService consultaService;
	
	@GetMapping
	public String get(
			HttpServletRequest request,
			Model model) {
		omplirModelFiltreDataTable(request, model);
		return "informeRepresentant";
	}
	
	@PostMapping
	public String post(
			HttpServletRequest request,
			@Valid InformeRepresentantFiltreCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		if (bindingResult.hasErrors()) {
			omplirModelFiltreDataTable(request, model);
			return "informeRepresentant";
		} else {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					command);
			return "redirect:informeRepresentant";
		}
	}
	@GetMapping(value = "/datatable", produces="application/json")
	@ResponseBody
	public ServerSideResponse<InformeProcedimentServeiDto, Long> datatable(HttpServletRequest request, Model model)
	      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
	      SQLException, EntitatNotFoundException {
		
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		
		InformeRepresentantFiltreCommand command = (InformeRepresentantFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null)
			command = new InformeRepresentantFiltreCommand();

		InformeRepresentantFiltreDto filtre = InformeRepresentantFiltreCommand.asDto(command);
		
		List<InformeProcedimentServeiDto> listUsuarisInforme = consultaService.informeUsuarisEntitatOrganProcedimentServei(
				EntitatHelper.getEntitatActual(request).getId(), 
				RolHelper.getRolActual(request), filtre);
		
		Page<InformeProcedimentServeiDto> page = new PageImpl<InformeProcedimentServeiDto>(listUsuarisInforme, null, listUsuarisInforme.size());
		
		return new ServerSideResponse<InformeProcedimentServeiDto, Long>(serverSideRequest, page);
	}
	
	@GetMapping("/excelInformePerRepresentant")
	public String usuarisOrganProcedimentServeiPerRepresentant(
			HttpServletRequest request,
			Model model) {
		 
		InformeRepresentantFiltreCommand command = (InformeRepresentantFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null)
			command = new InformeRepresentantFiltreCommand();

		InformeRepresentantFiltreDto filtre = InformeRepresentantFiltreCommand.asDto(command);
		model.addAttribute(
				"informeDades",
				consultaService.informeUsuarisEntitatOrganProcedimentServei(EntitatHelper.getEntitatActual(request).getId(), RolHelper.getRolActual(request), filtre));
		model.addAttribute("isAdministrador", RolHelper.isRolActualAdministrador(request));
		
		return "informeUsrEntOrgProcServExcelView";
	}
	
	private void omplirModelFiltreDataTable(
			HttpServletRequest request,
			Model model) {
		InformeRepresentantFiltreCommand command = (InformeRepresentantFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null)
			command = new InformeRepresentantFiltreCommand();
		model.addAttribute(command);
		
	}
	
}
