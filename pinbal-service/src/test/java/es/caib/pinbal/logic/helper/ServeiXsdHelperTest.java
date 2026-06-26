package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.FitxerDto;
import es.caib.pinbal.logic.intf.dto.ServeiXsdDto;
import es.caib.pinbal.logic.intf.dto.XsdTipusEnumDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ServeiXsdHelperTest {

    private final ServeiXsdHelper serveiXsdHelper = new ServeiXsdHelper();

    @TempDir
    Path tempDir;

    private ConfigHelper configHelperMock;

    @BeforeEach
    public void setUp() throws Exception {
        configHelperMock = mock(ConfigHelper.class);
        doReturn(tempDir.toString()).when(configHelperMock).getConfig(anyString());
        doReturn(tempDir.toString()).when(configHelperMock).getConfig(anyString(), anyString());
        ReflectionTestUtils.setField(ConfigHelper.class, "instance", configHelperMock);
    }

    @AfterEach
    public void tearDown() {
        ReflectionTestUtils.setField(ConfigHelper.class, "instance", null);
    }

    @Test
    public void modificarXsd_creaFitxer() {
        byte[] contingut = "<xsd/>".getBytes();
        ServeiXsdDto xsd = new ServeiXsdDto();
        xsd.setTipus(XsdTipusEnumDto.PETICIO);

        assertDoesNotThrow(() -> serveiXsdHelper.modificarXsd("SV_TEST", xsd, contingut));

        File expectedFile = new File(tempDir.toString(), "SV_TEST/peticion.xsd");
        assertTrue(expectedFile.exists());
    }

    @Test
    public void esborrarXsd_fitxerNoExisteix_noLlancaException() {
        assertDoesNotThrow(() -> serveiXsdHelper.esborrarXsd("SV_INEXISTENT", XsdTipusEnumDto.PETICIO));
    }

    @Test
    public void findAll_dirNoExisteix_retornaLlistaVuida() {
        List<ServeiXsdDto> result = serveiXsdHelper.findAll("SERVEI_SENSE_DIRECTORI");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void descarregarXsd_fitxerNoExisteix_llancaException() {
        assertThrows(IOException.class, () ->
                serveiXsdHelper.descarregarXsd("SV_INEXISTENT", XsdTipusEnumDto.RESPOSTA));
    }
}
