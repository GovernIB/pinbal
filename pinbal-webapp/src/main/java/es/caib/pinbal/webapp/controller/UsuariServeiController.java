package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.webapp.command.ConsultaCommand;
import es.caib.pinbal.webapp.command.ServeiFiltreCommand;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.common.ServeiHelper;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Controlador per al manteniment de serveis per usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/usuari/{usuari}/serveis")
public class UsuariServeiController extends BaseController {
	
	private static final String USER_SESSION_ATTRIBUTE_FILTRE = "UsuariServeiController.session.filtre";

    @Autowired
    private UsuariService usuariService;
	@Autowired
	private ServeiService serveiService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ProcedimentService procedimentService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws Exception {
        UsuariDto usuari = usuariService.getUsuariActual();
        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request, entitatService);
        List<ServeiDto> serveis = serveiService.findPermesosAmbProcedimentPerDelegat(entitatActual.getId(), null);
        List<ServeiDto> procediments = new ArrayList<>();


        model.addAttribute("procediments", procediments);
        model.addAttribute("serveis", serveis);
        model.addAttribute(new ConsultaCommand(usuari.getProcedimentId(), usuari.getServeiCodi()));
		return "usuariServeiList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ServeiFiltreCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		if (bindingResult.hasErrors()) {
			omplirModelPerFiltreTaula(request, model);
			return "usuariServeiList";
		} else {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
                    USER_SESSION_ATTRIBUTE_FILTRE,
					command);
			return "redirect:usuariServeiList";
		}
	}

	@RequestMapping(value = "/datatable", produces="application/json", method = RequestMethod.GET)
	@ResponseBody
	public ServerSideResponse<ServeiDto, Long> datatable(HttpServletRequest request, Model model) throws Exception {
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		
		ServeiFiltreCommand filtre = (ServeiFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(
				request, 
				USER_SESSION_ATTRIBUTE_FILTRE);
		
		if (filtre == null) {
			filtre = new ServeiFiltreCommand();
			filtre.setActiu(true);
		}

        List<ServeiDto> serveis = ServeiHelper.getServeis(request);
        if (serveis == null) {
            String usuari = request.getUserPrincipal().getName();
            EntitatDto entitatActual = EntitatHelper.getEntitatActual(request, entitatService);
            serveis = serveiService.findPermesosAmbProcedimentPerDelegat(entitatActual.getId(), null);
        }

        // Filtra per codi i descripció i elimina no actius
        List<ServeiDto> filtrats = filtraServeis(serveis, filtre);

        // Ordena segons serverSideRequest.order
        ordenaServeis(filtrats, serverSideRequest);

        // Converteix a Page amb tots els elements
        Page<ServeiDto> page = new PageImpl<>(
                filtrats,
                serverSideRequest.toPageable(),
                filtrats.size()
        );

		return new ServerSideResponse<ServeiDto, Long>(serverSideRequest, page);
	}

    private static List<ServeiDto> filtraServeis(List<ServeiDto> serveis, ServeiFiltreCommand filtre) {
        List<ServeiDto> filtrats = new ArrayList<>();

        final String codiFiltre = filtre.getCodi() != null ? filtre.getCodi().toLowerCase() : null;
        final String descFiltre = filtre.getDescripcio() != null ? filtre.getDescripcio().toLowerCase() : null;

        if ((codiFiltre == null || codiFiltre.isEmpty()) && (descFiltre == null || descFiltre.isEmpty()))
            return serveis;

        for (ServeiDto s : serveis) {
            if (s == null) continue;
            // només actius
            if (s.getActiu() == null || !s.getActiu()) continue;

            boolean match = true;

            if (codiFiltre != null && !codiFiltre.isEmpty()) {
                String codi = s.getCodi();
                match &= (codi != null && codi.toLowerCase().contains(codiFiltre));
            }
            if (descFiltre != null && !descFiltre.isEmpty()) {
                String desc = s.getDescripcio();
                match &= (desc != null && desc.toLowerCase().contains(descFiltre));
            }
            if (match) filtrats.add(s);
        }

        return filtrats;
    }

    private static void ordenaServeis(List<ServeiDto> filtrats, ServerSideRequest serverSideRequest) {

        if (serverSideRequest == null || serverSideRequest.getOrder() == null || serverSideRequest.getColumns() == null) return;

        final List<String> fields = new ArrayList<>();
        final List<Boolean> descs = new ArrayList<>();
        for (es.caib.pinbal.webapp.datatables.ServerSideOrder ord : serverSideRequest.getOrder()) {
            int colIndex = ord.getColumn();
            if (colIndex < 0 || colIndex >= serverSideRequest.getColumns().size()) continue;
            String dataField = serverSideRequest.getColumns().get(colIndex).getData();
            // Només admetem "codi" i "descripcio"; per defecte mapejam a "codi"
            if (!"descripcio".equals(dataField)) {
                dataField = "codi";
            }
            fields.add(dataField);
            descs.add(Boolean.valueOf("desc".equalsIgnoreCase(ord.getDir())));
        }
        if (!fields.isEmpty()) {
            Collections.sort(filtrats, new Comparator<ServeiDto>() {
                public int compare(ServeiDto a, ServeiDto b) {
                    for (int i = 0; i < fields.size(); i++) {
                        String field = fields.get(i);
                        boolean desc = descs.get(i).booleanValue();
                        String va;
                        String vb;
                        if ("descripcio".equals(field)) {
                            va = a != null ? a.getDescripcio() : null;
                            vb = b != null ? b.getDescripcio() : null;
                        } else {
                            va = a != null ? a.getCodi() : null;
                            vb = b != null ? b.getCodi() : null;
                        }
                        String sa = (va == null) ? null : va.toLowerCase();
                        String sb = (vb == null) ? null : vb.toLowerCase();
                        int res;
                        if (sa == null && sb == null) res = 0;
                        else if (sa == null) res = -1;
                        else if (sb == null) res = 1;
                        else res = sa.compareTo(sb);
                        if (res != 0) return desc ? -res : res;
                    }
                    return 0;
                }
            });
        }
    }

    @InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}



	private void omplirModelPerFiltreTaula( 
			HttpServletRequest request, 
			Model model) throws Exception {
		ServeiFiltreCommand command = (ServeiFiltreCommand) RequestSessionHelper.obtenirObjecteSessio( 
				request, 
				USER_SESSION_ATTRIBUTE_FILTRE);
		if (command == null) {
			command = new ServeiFiltreCommand();
			command.setActiu(true);
		}
		command.eliminarEspaisCampsCerca();
		model.addAttribute(command);
		model.addAttribute("emisors", serveiService.findEmisorAll());
	} 

}
