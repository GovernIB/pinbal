package es.caib.pinbal.core.plugin;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.core.helper.ConfigHelper;
import es.caib.pinbal.core.helper.IntegracioHelper;
import es.caib.pinbal.core.helper.PluginHelper;
import es.caib.pinbal.plugins.FirmaServidorPlugin;
import es.caib.pinbal.plugins.SignaturaResposta;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Transactional
public class FirmaServidorTest {

    @Mock
    private ConfigHelper configHelper;
    @Mock
    private IntegracioHelper integracioHelper;
    @InjectMocks
    private PluginHelper pluginHelper = new PluginHelper();

    private static Map<String, String> propietats;
    static {
        propietats = new HashMap<>();
        propietats.put("", "");
        propietats.put("es.caib.pinbal.plugin.firmaservidor.portafib.endpoint", "https://dev.caib.es/portafib/common/rest/apifirmaenservidorsimple/v1/");
        propietats.put("es.caib.pinbal.plugin.firmaservidor.portafib.auth.username", "$notib_portafib");
        propietats.put("es.caib.pinbal.plugin.firmaservidor.portafib.auth.password", "notib_portafib");
        propietats.put("es.caib.pinbal.plugin.firmaservidor.portafib.perfil", "PADES");
        propietats.put("es.caib.pinbal.plugin.firmaservidor.portafib.username", "afirmades-firma");

    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(configHelper.getGroupProperties(Mockito.eq("FIRMA_SERVIDOR"))).thenReturn(propietats);
        when(configHelper.getConfig(Mockito.eq("es.caib.pinbal.plugin.firmaservidor.class"))).thenReturn("es.caib.pinbal.plugins.caib.FirmaSimpleServidorPluginPortafib");
        doNothing().when(integracioHelper).addAccioOk(anyString(), anyString(), Mockito.<Map<String, String>>any(), Mockito.<IntegracioAccioTipusEnumDto>any(), anyLong());
        doNothing().when(integracioHelper).addAccioError(anyString(), anyString(), Mockito.<Map<String, String>>any(), Mockito.<IntegracioAccioTipusEnumDto>any(), anyLong(), anyString());
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
                "ca");

        Assert.notNull(contingutFitxerFirmat.getContingut());

//        FileOutputStream outputStream = new FileOutputStream("fitxerfirmat.pdf");
//        outputStream.write(contingutFitxerFirmat);
//        File fitxerFirmat = new File("fitxerfirmat.pdf");
//        outputStream.close();

        PdfReader reader = new PdfReader(contingutFitxerFirmat.getContingut());
        AcroFields acroFields = reader.getAcroFields();
        List<String> signatureNames = acroFields.getSignatureNames();
        Assert.notNull(signatureNames);
        Assert.notEmpty(signatureNames);

    }
}
