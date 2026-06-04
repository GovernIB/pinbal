/**
 * 
 */
package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.ClauPrivadaCommand;
import es.caib.pinbal.back.datatables.ServerSideRequest;
import es.caib.pinbal.back.datatables.ServerSideResponse;
import es.caib.pinbal.back.helper.AlertHelper;
import es.caib.pinbal.logic.intf.dto.ClauPrivadaDto;
import es.caib.pinbal.logic.intf.dto.OrganismeCessionariDto;
import es.caib.pinbal.logic.intf.service.ScspService;
import es.caib.pinbal.logic.intf.service.exception.ClauPrivadaNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
	
	@GetMapping
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {
		return "clauPrivadaList";
	}
	
	@GetMapping(value = "/datatable", produces="application/json")
	@ResponseBody
	public ServerSideResponse<ClauPrivadaDto, Long> datatable(HttpServletRequest request, Model model)
	      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
	      SQLException, EntitatNotFoundException {

		ServerSideRequest serverSideRequest = new ServerSideRequest(request);		 
		Page<ClauPrivadaDto> page = scspService.findAllClauPrivada(serverSideRequest.toPageable());	
		return new ServerSideResponse<ClauPrivadaDto, Long>(serverSideRequest, page);
	}
	
	@GetMapping("/new")
	public String get(Model model) {
		
		model.addAttribute( new ClauPrivadaCommand() );
		model.addAttribute( "organismes", scspService.findAllOrganismeCessionariActiu());
		return "clauPrivadaForm";
	}
	
	@GetMapping("/{clauPrivadaId:.+}")
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

        List<OrganismeCessionariDto> organismesCessionarisActius = scspService.findAllOrganismeCessionariActiu();
        OrganismeCessionariDto organismeCessionariActual = scspService.findOrganismeCessionariById(dto.getOrganismeId());
        if (!organismesCessionarisActius.contains(organismeCessionariActual)) {
            organismesCessionarisActius.add(0, organismeCessionariActual);
        }
        model.addAttribute( "organismes", scspService.findAllOrganismeCessionari());
		
		return "clauPrivadaForm";
	}
	
	@PostMapping("/save")
	public String save(
			HttpServletRequest request,
			Model model,
			@Valid ClauPrivadaCommand command,
			BindingResult bindingResult) throws ClauPrivadaNotFoundException, EntitatNotFoundException {
		
		if (bindingResult.hasErrors()) {
			model.addAttribute( "organismes", scspService.findAllOrganismeCessionariActiu());
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
	
	@GetMapping("/{clauPrivadaId:.+}/delete")
	public String delete(
			HttpServletRequest request,
			@PathVariable Long clauPrivadaId) throws ClauPrivadaNotFoundException {
		
		scspService.deleteClauPrivada(clauPrivadaId);
		
		AlertHelper.success(
				request, 
				getMessage(
						request, 
						"clau.privada.controller.esborrat.ok"));
		
		return "redirect:../";
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
	
}
