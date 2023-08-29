/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.core.dto.ClauPrivadaDto;
import es.caib.pinbal.core.service.ScspService;
import es.caib.pinbal.core.service.exception.ClauPrivadaNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.webapp.command.ClauPrivadaCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controlador per al manteniment de claus privades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/scsp/clauprivada")
public class ClauPrivadaController extends BaseController {
	
	@Autowired
	private ScspService scspService;
//	@Autowired
//	private ClauPrivadaMapper clauPrivadaMapper;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {
		return "clauPrivadaList";
	}
	
	@RequestMapping(value = "/datatable", produces="application/json", method = RequestMethod.GET)
	@ResponseBody
	public ServerSideResponse<ClauPrivadaDto, Long> datatable(HttpServletRequest request, Model model)
	      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
	      SQLException, EntitatNotFoundException {

		ServerSideRequest serverSideRequest = new ServerSideRequest(request);		 
		Page<ClauPrivadaDto> page = scspService.findAllClauPrivada(serverSideRequest.toPageable());	
		return new ServerSideResponse<ClauPrivadaDto, Long>(serverSideRequest, page);
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String get(Model model) {
		
		model.addAttribute( new ClauPrivadaCommand() );
		model.addAttribute( "organismes", scspService.findAllOrganismeCessionari());
		return "clauPrivadaForm";
	}
	
	@RequestMapping(value = "/{clauPrivadaId:.+}", method = RequestMethod.GET)
	public String get(
			@PathVariable Long clauPrivadaId,
			Model model) {
		
		ClauPrivadaDto dto = null;
		if (clauPrivadaId != null)
			dto = scspService.findClauPrivadaById(clauPrivadaId);
		
		if (dto != null)
			model.addAttribute( ClauPrivadaCommand.asCommand(dto) );
//			model.addAttribute( clauPrivadaMapper.dtoToCommand(dto) );
		else
			model.addAttribute( new ClauPrivadaCommand() );
		
		model.addAttribute( "organismes", scspService.findAllOrganismeCessionari());
		
		return "clauPrivadaForm";
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			Model model,
			@Valid ClauPrivadaCommand command,
			BindingResult bindingResult) throws ClauPrivadaNotFoundException {
		
		if (bindingResult.hasErrors()) {
			model.addAttribute( "organismes", scspService.findAllOrganismeCessionari());
			return "clauPrivadaForm";
		}
		
		if (command.getId() == null) {
			scspService.createClauPrivada(ClauPrivadaCommand.asDto(command));
//			scspService.createClauPrivada(clauPrivadaMapper.commandToDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:./",
					"clau.privada.controller.creat.ok");
		} else {
			scspService.updateClauPrivada(ClauPrivadaCommand.asDto(command));
//			scspService.updateClauPrivada(clauPrivadaMapper.commandToDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:./",
					"clau.privada.controller.modificat.ok");
		}
	}
	
	@RequestMapping(value = "/{clauPrivadaId:.+}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long clauPrivadaId) throws ClauPrivadaNotFoundException {
		
		scspService.deleteClauPrivada(clauPrivadaId);
		
		AlertHelper.success(
				request, 
				getMessage(
						request, 
						"paramconf.controller.esborrat.ok"));
		
		return "redirect:../";
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
	
}
