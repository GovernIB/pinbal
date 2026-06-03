package es.caib.pinbal.logic.plugin;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import es.caib.pinbal.logic.helper.ConfigHelper;
import es.caib.pinbal.logic.helper.IntegracioHelper;
import es.caib.pinbal.logic.helper.PluginHelper;
import es.caib.pinbal.logic.intf.dto.FitxerDto;
import es.caib.pinbal.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.plugin.firmaservidor.FirmaServidorPlugin;
import es.caib.pinbal.plugin.firmaservidor.SignaturaResposta;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@Disabled("Es necessita connexió a un entorn funcional")
@ExtendWith(MockitoExtension.class)
public class FirmaServidorTest {

    private static final String PROPERTIES_RESOURCE = "es/caib/pinbal/logic/plugin/firma-servei-test.properties";

    @Mock
    private ConfigHelper configHelper;
    @Mock
    private IntegracioHelper integracioHelper;
    @InjectMocks
    private PluginHelper pluginHelper;

    private Properties propietats;

    @BeforeEach
    public void setup() throws IOException {
        InputStream propsStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_RESOURCE);
        assumeTrue(propsStream != null,
                "Fitxer de configuració no trobat: " + PROPERTIES_RESOURCE +
                " (copia el .template i omple les credencials)");
        propietats = new Properties();
        propietats.load(propsStream);

        when(configHelper.getEnvironmentProperties()).thenReturn(propietats);
        when(configHelper.getConfig(Mockito.eq("es.caib.pinbal.plugin.firmaservidor.class")))
                .thenReturn("es.caib.pinbal.plugin.firmaservidor.FirmaSimpleServidorPluginPortafib");
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
    public void whenFirmaThenOk() throws Exception {

        FitxerDto fitxer = new FitxerDto();
        fitxer.setNom("document.pdf");
        fitxer.setContentType("application/pdf");
        fitxer.setContingut(IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("buit.pdf")));

        SignaturaResposta contingutFitxerFirmat = pluginHelper.firmaServidorFirmar(
                fitxer,
                FirmaServidorPlugin.TipusFirma.PADES,
                "Firma justificant PINBAL",
                "ca",
                "idConsulta");

        Assert.notNull(contingutFitxerFirmat.getContingut());

        PdfReader reader = new PdfReader(contingutFitxerFirmat.getContingut());
        AcroFields acroFields = reader.getAcroFields();
        List<String> signatureNames = acroFields.getSignatureNames();
        Assert.notNull(signatureNames);
        Assert.notEmpty(signatureNames);
    }
}
