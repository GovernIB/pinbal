package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.core.dto.ConfigDto;
import es.caib.pinbal.core.dto.ConfigGroupDto;
import es.caib.pinbal.core.service.ConfigService;
import es.caib.pinbal.webapp.command.ConfigCommand;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * Controlador per a la gestió de la configuració de l'aplicació.
 * Només accessible amb el rol de superusuari.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/config")
public class ConfigController extends BaseController{

    @Autowired
    private ConfigService configService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(
            HttpServletRequest request,
            Model model) {
        List<ConfigGroupDto> configGroups = configService.findAll();
        model.addAttribute("config_groups", configGroups);
        for (ConfigGroupDto cGroup: configGroups) {
            fillFormsModel(cGroup, model);
        }
        return "config";
    }

    @ResponseBody
    @RequestMapping(value="/update", method = RequestMethod.POST)
    public SimpleResponse updateConfig(
            HttpServletRequest request,
            Model model,
            @Valid ConfigCommand configCommand,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return SimpleResponse.builder()
                    .status(0)
                    .message(getMessage(request, "config.controller.edit.error"))
                    .build();
        }

        String msg = "config.controller.edit.ok";
        int status = 1;
        try {
            configService.updateProperty(configCommand.asDto());
        } catch (Exception e) {
            e.printStackTrace();
            msg = "config.controller.edit.error";
            status = 0;
        }
        return SimpleResponse.builder()
                .status(status)
                .message(getMessage(request, msg))
                .build();
    }

    @ResponseBody
    @RequestMapping(value="/sync", method = RequestMethod.GET)
    public SyncResponse sync(
            HttpServletRequest request,
            Model model) {
        try {
            List<String> editedProperties = configService.syncFromJBossProperties();
            return SyncResponse.builder()
                    .status(true)
                    .editedProperties(editedProperties)
                    .build();
        } catch (Exception e) {
            return SyncResponse.builder()
                    .status(false)
                    .build();
        }
    }

    private void fillFormsModel(ConfigGroupDto cGroup, Model model){
        for (ConfigDto config: cGroup.getConfigs()) {
            model.addAttribute("config_" + config.getKey().replace('.', '_'),
                    ConfigCommand.builder().key(config.getKey()).value(config.getValue()).build());
        }
        if (cGroup.getInnerConfigs() == null || cGroup.getInnerConfigs().isEmpty()){
            return;
        }
        for (ConfigGroupDto child : cGroup.getInnerConfigs()){
            fillFormsModel(child, model);
        }
    }

    @Builder @Getter
    public static class SyncResponse {
        private boolean status;
        private List<String> editedProperties;
    }

    @Builder @Getter
    public static class SimpleResponse {
        private int status;
        private String message;
    }
}
