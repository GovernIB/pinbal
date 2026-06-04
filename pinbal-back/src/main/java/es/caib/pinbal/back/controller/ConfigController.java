package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.ConfigCommand;
import es.caib.pinbal.logic.intf.dto.ConfigDto;
import es.caib.pinbal.logic.intf.dto.ConfigGroupDto;
import es.caib.pinbal.logic.intf.service.ConfigService;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@Controller
@RequestMapping("/config")
public class ConfigController extends BaseController{

    @Autowired
    private ConfigService configService;

    @GetMapping
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
    @PostMapping("/update")
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
    @GetMapping("/sync")
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

    @ResponseBody
    @GetMapping("/reiniciarTasques")
    public String reiniciarTasques(
            HttpServletRequest request,
            Model model) {
        configService.reiniciarTasques();
        return "OK";
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
