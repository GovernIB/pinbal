package es.caib.pinbal.plugin.firmaservidor;

import es.caib.pinbal.plugin.SistemaExternException;
import org.fundaciobit.apisib.apifirmasimple.v1.ApiFirmaEnServidorSimple;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleAvailableProfile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignatureResult;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignedFileInfo;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FirmaSimpleServidorPluginPortafibTest {

    @Mock
    private ApiFirmaEnServidorSimple api;

    private FirmaSimpleServidorPluginPortafib crearPlugin(Properties properties) {
        return new FirmaSimpleServidorPluginPortafib("plugin.", properties);
    }

    private FirmaSimpleServidorPluginPortafib crearPlugin() {
        return crearPlugin(new Properties());
    }

    private FirmaSimpleFile fitxerAFirmar() {
        return new FirmaSimpleFile("document.pdf", "application/pdf", "contingut".getBytes());
    }

    // ------------------------- internalSignDocument -------------------------

    @Test
    void internalSignDocumentAmbStatusFinalOkRetornaResultat() throws Exception {
        FirmaSimpleServidorPluginPortafib plugin = crearPlugin();
        FirmaSimpleSignedFileInfo signedFileInfo = new FirmaSimpleSignedFileInfo();
        signedFileInfo.setEniTipoFirma("CADES");
        FirmaSimpleSignatureResult result = new FirmaSimpleSignatureResult(
                "999",
                new FirmaSimpleStatus(FirmaSimpleStatus.STATUS_FINAL_OK, null, null),
                fitxerAFirmar(),
                signedFileInfo);
        when(api.signDocument(any())).thenReturn(result);

        FirmaSimpleSignatureResult retornat = plugin.internalSignDocument(
                api, "PERFIL1", fitxerAFirmar(), "motiu", "DT42", "ca");

        assertThat(retornat).isSameAs(result);
        assertThat(retornat.getSignedFileInfo().getEniTipoFirma()).isEqualTo("CADES");
    }

    @Test
    void internalSignDocumentAmbTipusDocumentalNulNoEsParseja() throws Exception {
        FirmaSimpleServidorPluginPortafib plugin = crearPlugin();
        FirmaSimpleSignatureResult result = new FirmaSimpleSignatureResult(
                "1", new FirmaSimpleStatus(FirmaSimpleStatus.STATUS_FINAL_OK, null, null), null, new FirmaSimpleSignedFileInfo());
        when(api.signDocument(any())).thenReturn(result);

        FirmaSimpleSignatureResult retornat = plugin.internalSignDocument(
                api, "PERFIL1", fitxerAFirmar(), "motiu", null, null);

        assertThat(retornat).isSameAs(result);
    }

    @Test
    void internalSignDocumentAmbIdiomaNulFaServirCaPerDefecte() throws Exception {
        FirmaSimpleServidorPluginPortafib plugin = crearPlugin();
        FirmaSimpleSignatureResult result = new FirmaSimpleSignatureResult(
                "1", new FirmaSimpleStatus(FirmaSimpleStatus.STATUS_FINAL_OK, null, null), null, new FirmaSimpleSignedFileInfo());
        when(api.signDocument(any())).thenReturn(result);

        plugin.internalSignDocument(api, "PERFIL1", fitxerAFirmar(), "motiu", null, null);

        ArgumentCaptor<org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignDocumentRequest> captor =
                ArgumentCaptor.forClass(org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignDocumentRequest.class);
        verify(api).signDocument(captor.capture());
        assertThat(captor.getValue().getCommonInfo().getLanguageUI()).isEqualTo("ca");
    }

    @Test
    void internalSignDocumentAmbStatusInitializingLlancaExcepcio() throws Exception {
        FirmaSimpleServidorPluginPortafib plugin = crearPlugin();
        FirmaSimpleSignatureResult result = new FirmaSimpleSignatureResult(
                "1", new FirmaSimpleStatus(FirmaSimpleStatus.STATUS_INITIALIZING, null, null), null, null);
        when(api.signDocument(any())).thenReturn(result);

        assertThatThrownBy(() -> plugin.internalSignDocument(api, "P", fitxerAFirmar(), "m", null, "ca"))
                .isInstanceOf(SistemaExternException.class)
                .hasMessageContaining("Initializing");
    }

    @Test
    void internalSignDocumentAmbStatusInProgressLlancaExcepcio() throws Exception {
        FirmaSimpleServidorPluginPortafib plugin = crearPlugin();
        FirmaSimpleSignatureResult result = new FirmaSimpleSignatureResult(
                "1", new FirmaSimpleStatus(FirmaSimpleStatus.STATUS_IN_PROGRESS, null, null), null, null);
        when(api.signDocument(any())).thenReturn(result);

        assertThatThrownBy(() -> plugin.internalSignDocument(api, "P", fitxerAFirmar(), "m", null, "ca"))
                .isInstanceOf(SistemaExternException.class)
                .hasMessageContaining("In PROGRESS");
    }

    @Test
    void internalSignDocumentAmbStatusFinalErrorLlancaExcepcioAmbDetall() throws Exception {
        FirmaSimpleServidorPluginPortafib plugin = crearPlugin();
        FirmaSimpleSignatureResult result = new FirmaSimpleSignatureResult(
                "1", new FirmaSimpleStatus(FirmaSimpleStatus.STATUS_FINAL_ERROR, "missatge-error", "stack-trace"), null, null);
        when(api.signDocument(any())).thenReturn(result);

        assertThatThrownBy(() -> plugin.internalSignDocument(api, "P", fitxerAFirmar(), "m", null, "ca"))
                .isInstanceOf(SistemaExternException.class)
                .hasMessageContaining("missatge-error")
                .hasMessageContaining("stack-trace");
    }

    @Test
    void internalSignDocumentAmbStatusCancelledLlancaExcepcio() throws Exception {
        FirmaSimpleServidorPluginPortafib plugin = crearPlugin();
        FirmaSimpleSignatureResult result = new FirmaSimpleSignatureResult(
                "1", new FirmaSimpleStatus(FirmaSimpleStatus.STATUS_CANCELLED, null, null), null, null);
        when(api.signDocument(any())).thenReturn(result);

        assertThatThrownBy(() -> plugin.internalSignDocument(api, "P", fitxerAFirmar(), "m", null, "ca"))
                .isInstanceOf(SistemaExternException.class)
                .hasMessageContaining("cancel");
    }

    @Test
    void internalSignDocumentAmbStatusDesconegutLlancaExcepcio() throws Exception {
        FirmaSimpleServidorPluginPortafib plugin = crearPlugin();
        FirmaSimpleSignatureResult result = new FirmaSimpleSignatureResult(
                "1", new FirmaSimpleStatus(99, null, null), null, null);
        when(api.signDocument(any())).thenReturn(result);

        assertThatThrownBy(() -> plugin.internalSignDocument(api, "P", fitxerAFirmar(), "m", null, "ca"))
                .isInstanceOf(SistemaExternException.class)
                .hasMessageContaining("desconegut");
    }

    // ------------------------- getAvailableProfiles (privat, per reflexió) -------------------------

    @Test
    void getAvailableProfilesRegistraPerfilsIBuits() throws Exception {
        FirmaSimpleServidorPluginPortafib plugin = crearPlugin();
        when(api.getAvailableProfiles("ca")).thenReturn(Collections.emptyList());
        when(api.getAvailableProfiles("es")).thenReturn(
                List.of(new FirmaSimpleAvailableProfile("COD1", "Nom perfil", "Descripció")));

        Method method = FirmaSimpleServidorPluginPortafib.class.getDeclaredMethod(
                "getAvailableProfiles", ApiFirmaEnServidorSimple.class);
        method.setAccessible(true);
        method.invoke(plugin, api);

        verify(api, times(1)).getAvailableProfiles("ca");
        verify(api, times(1)).getAvailableProfiles("es");
    }

    // ------------------------- signar (mètode públic) -------------------------

    @Test
    void signarAmbEndpointNoConfiguratLlancaRuntimeException() {
        FirmaSimpleServidorPluginPortafib plugin = crearPlugin();
        SignaturaDades dades = SignaturaDades.builder()
                .nom("document.pdf")
                .contentType("application/pdf")
                .contingut("contingut".getBytes())
                .motiu("motiu")
                .idioma("ca")
                .build();

        assertThatThrownBy(() -> plugin.signar(dades))
                .isInstanceOf(RuntimeException.class);
    }
}
