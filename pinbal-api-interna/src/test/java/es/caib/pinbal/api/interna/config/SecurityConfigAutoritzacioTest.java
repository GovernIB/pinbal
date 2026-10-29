package es.caib.pinbal.api.interna.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Comprova les regles d'autorització per URL de {@link SecurityConfig}. L'usuari i els rols
 * s'informen a la petició com ho fa el contenidor (autenticació J2EE preautenticada).
 */
@SpringJUnitWebConfig(SecurityConfigAutoritzacioTest.TestConfig.class)
public class SecurityConfigAutoritzacioTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        DelegatingFilterProxy springSecurityFilterChain = new DelegatingFilterProxy("springSecurityFilterChain", context);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    public void recobrimentV2_ambRolWs_permes() throws Exception {
        mockMvc.perform(get("/recobriment/v2/entitats").with(usuari("app", "PBL_WS")))
                .andExpect(status().isOk());
    }

    @Test
    public void recobrimentV2_senseRolWs_prohibit() throws Exception {
        mockMvc.perform(get("/recobriment/v2/entitats").with(usuari("persona", "PBL_USER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/recobriment/v2/consultes/P1/solicitud/S1/justificant").with(usuari("persona", "PBL_USER", "PBL_ADMIN")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void recobrimentV2_senseAutenticar_prohibit() throws Exception {
        mockMvc.perform(get("/recobriment/v2/entitats"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void recobrimentTest_esPublic() throws Exception {
        mockMvc.perform(get("/recobriment/test"))
                .andExpect(status().isOk());
    }

    @Test
    public void altresApis_nomesRequereixenAutenticacio() throws Exception {
        mockMvc.perform(get("/procediments").with(usuari("persona", "PBL_USER")))
                .andExpect(status().isOk());
    }

    private static RequestPostProcessor usuari(String nom, String... rols) {
        return request -> {
            request.setUserPrincipal(() -> nom);
            for (String rol: rols) {
                request.addUserRole(rol);
            }
            return request;
        };
    }

    @Configuration
    @EnableWebMvc
    @Import({SecurityConfig.class, TestConfig.TestController.class})
    static class TestConfig {
        @RestController
        static class TestController {
            @GetMapping({"/recobriment/v2/entitats", "/recobriment/v2/consultes/P1/solicitud/S1/justificant", "/recobriment/test", "/procediments"})
            public String ok() {
                return "ok";
            }
        }
    }
}
