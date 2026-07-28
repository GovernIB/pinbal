package es.caib.pinbal.api.interna.controller;

import es.caib.pinbal.client.comu.EntitatInfo;
import es.caib.pinbal.logic.intf.service.EntitatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ApiRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EntitatService entitatService;

    @InjectMocks
    private ApiRestController apiRestController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(apiRestController).build();
    }

    @Test
    public void testTest_Success() throws Exception {
        List<EntitatInfo> entitats = new ArrayList<>();
        EntitatInfo entitat = new EntitatInfo();
        entitat.setCodi("ENT_001");
        entitats.add(entitat);
        when(entitatService.getEntitatsInfo()).thenReturn(entitats);

        mockMvc.perform(get("/api/entitats"))
                .andExpect(status().isOk());
    }
}
