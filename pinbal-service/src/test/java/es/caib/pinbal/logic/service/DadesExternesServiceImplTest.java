package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.ConfigHelper;
import es.caib.pinbal.logic.helper.IntegracioHelper;
import es.caib.pinbal.logic.intf.dto.IdiomaEnumDto;
import es.caib.pinbal.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.plugin.dadescomuns.Municipi;
import es.caib.pinbal.plugin.dadescomuns.Pais;
import es.caib.pinbal.plugin.dadescomuns.Provincia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@Disabled("Es necessita connexió a un entorn funcional")
@ExtendWith(MockitoExtension.class)
public class DadesExternesServiceImplTest {

    @InjectMocks
    private DadesExternesServiceImpl dadesExternesService;

    @Mock
    private ConfigHelper configHelper;
    @Mock
    private IntegracioHelper integracioHelper;

    private final String baseUrl = "https://proves.caib.es/dadescomunsfront";

    @BeforeEach
    public void setUp() {
        when(configHelper.getConfig("es.caib.pinbal.dadescomunes.base.url", baseUrl)).thenReturn(baseUrl);
        lenient().doNothing().when(integracioHelper).addAccioOk(
                anyString(), anyString(), anyString(),
                Mockito.<Map<String, String>>any(),
                Mockito.<IntegracioAccioTipusEnumDto>any(),
                anyLong());
        lenient().doNothing().when(integracioHelper).addAccioError(
                anyString(), anyString(), anyString(),
                Mockito.<Map<String, String>>any(),
                Mockito.<IntegracioAccioTipusEnumDto>any(),
                anyLong(), anyString(),
                any(Throwable.class));
    }

    @Test
    public void testFindPaisos_Success() throws Exception {

        // Call method under test
        List<Pais> result = dadesExternesService.findPaisos(IdiomaEnumDto.CA);

        // Assertions
        assertEquals(244, result.size());
        assertEquals("Afganistan", result.get(0).getNom());
        assertEquals("Åland, illes; Aland, illes", result.get(1).getNom());
    }

    @Test
    public void testFindProvincies_Success() throws Exception {

        // Call method under test
        List<Provincia> result = dadesExternesService.findProvincies(IdiomaEnumDto.CA);

        // Assertions
        assertEquals(52, result.size());
        assertEquals("Àlaba", result.get(0).getNom());
        assertEquals("Alacant", result.get(1).getNom());
    }

    @Test
    public void testFindMunicipis_Success() throws Exception {

        // Call method under test
        List<Municipi> result = dadesExternesService.findMunicipisPerProvincia("07");

        // Assertions
        assertEquals(67, result.size());
        assertEquals("Alaior", result.get(0).getNom());
        assertEquals("Alaró", result.get(1).getNom());
    }

}