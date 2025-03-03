package es.caib.pinbal.core.service;

import es.caib.pinbal.core.dto.IdiomaEnumDto;
import es.caib.pinbal.core.dto.dadesexternes.Municipi;
import es.caib.pinbal.core.dto.dadesexternes.Pais;
import es.caib.pinbal.core.dto.dadesexternes.Provincia;
import es.caib.pinbal.core.helper.ConfigHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DadesExternesServiceImplTest {

    @InjectMocks
    private DadesExternesService dadesExternesService = new es.caib.pinbal.core.service.DadesExternesServiceImpl();

    @Mock
    private ConfigHelper configHelper;

    private final String baseUrl = "https://proves.caib.es/dadescomunsfront";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(configHelper.getConfig("es.caib.pinbal.dadescomunes.base.url", baseUrl)).thenReturn(baseUrl);
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