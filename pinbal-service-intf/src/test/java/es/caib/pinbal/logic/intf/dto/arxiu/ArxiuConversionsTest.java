package es.caib.pinbal.logic.intf.dto.arxiu;

import es.caib.pluginsib.arxiu.api.ContingutOrigen;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentEstatElaboracio;
import es.caib.pluginsib.arxiu.api.DocumentMetadades;
import es.caib.pluginsib.arxiu.api.DocumentTipus;
import es.caib.pluginsib.arxiu.api.Firma;
import es.caib.pluginsib.arxiu.api.FirmaPerfil;
import es.caib.pluginsib.arxiu.api.FirmaTipus;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ArxiuConversions és una classe de conversions enum-a-enum mitjançant switch exhaustius: la
 * manera més eficient de cobrir totes les branques és recórrer tots els valors de l'enum d'origen
 * per cada mètode i verificar que la conversió és coherent i bidireccional on s'escaigui.
 */
class ArxiuConversionsTest {

    @Test
    void getEstatElaboracioCobreixTotsElsValors() {
        for (DocumentEstatElaboracio origen : DocumentEstatElaboracio.values()) {
            DocumentNtiEstadoElaboracionEnumDto resultat = ArxiuConversions.getEstatElaboracio(origen);
            assertThat(resultat).as("estatElaboracio per %s", origen).isNotNull();
            assertThat(ArxiuConversions.getDocumentEstatElaboracio(resultat)).isEqualTo(origen);
        }
    }

    @Test
    void getEstatElaboracioAmbDocumentDelegaEnMetadades() {
        Document document = new Document();
        DocumentMetadades metadades = new DocumentMetadades();
        metadades.setEstatElaboracio(DocumentEstatElaboracio.ORIGINAL);
        document.setMetadades(metadades);

        assertThat(ArxiuConversions.getEstatElaboracio(document)).isEqualTo(DocumentNtiEstadoElaboracionEnumDto.EE01);
    }

    @Test
    void getOrigenCobreixTotsElsValors() {
        for (ContingutOrigen origen : ContingutOrigen.values()) {
            NtiOrigenEnumDto resultat = ArxiuConversions.getOrigen(origen);
            assertThat(resultat).as("origen per %s", origen).isNotNull();
            assertThat(ArxiuConversions.getOrigen(resultat)).isEqualTo(origen);
        }
        assertThat(ArxiuConversions.getOrigen((NtiOrigenEnumDto) null)).isNull();
    }

    @Test
    void getOrigenAmbDocumentDelegaEnMetadades() {
        Document document = new Document();
        DocumentMetadades metadades = new DocumentMetadades();
        metadades.setOrigen(ContingutOrigen.CIUTADA);
        document.setMetadades(metadades);

        assertThat(ArxiuConversions.getOrigen(document)).isEqualTo(NtiOrigenEnumDto.O0);
    }

    @Test
    void getTipusDocumentalEnumCobreixTotsElsValorsIAdmetNull() {
        // El switch font és incomplet a propòsit (@SuppressWarnings("incomplete-switch")): no cal
        // que totes les constants de DocumentTipus tinguin una conversió; només s'exerceix el codi.
        for (DocumentTipus tipus : DocumentTipus.values()) {
            ArxiuConversions.getTipusDocumentalEnum(tipus);
        }
        assertThat(ArxiuConversions.getTipusDocumentalEnum(null)).isNull();
    }

    @Test
    void getTipusDocumentalAmbDocumentCobreixTotsElsValors() {
        for (DocumentTipus tipus : DocumentTipus.values()) {
            Document document = documentAmbTipus(tipus, null);
            assertThat(ArxiuConversions.getTipusDocumental(document)).as("tipus per %s", tipus).isNotNull();
        }
    }

    @Test
    void getTipusDocumentalAmbTipusAddicionalQuanNoHiHaTipusPrincipal() {
        Document document = documentAmbTipus(null, "TIPUS-ADDICIONAL");
        assertThat(ArxiuConversions.getTipusDocumental(document)).isEqualTo("TIPUS-ADDICIONAL");
    }

    @Test
    void getTipusDocumentalRetornaNullQuanNoHiHaCapTipus() {
        Document document = documentAmbTipus(null, null);
        assertThat(ArxiuConversions.getTipusDocumental(document)).isNull();
    }

    @Test
    void setTipusDocumentalCobreixTotsElsCodisIElPerDefecte() {
        for (DocumentNtiTipoDocumentalEnumDto codi : DocumentNtiTipoDocumentalEnumDto.values()) {
            DocumentMetadades metadades = new DocumentMetadades();
            ArxiuConversions.setTipusDocumental(metadades, codi.name());
            assertThat(metadades.getTipusDocumental()).as("codi %s", codi).isNotNull();
            assertThat(metadades.getTipusDocumentalAddicional()).isNull();
        }

        DocumentMetadades metadades = new DocumentMetadades();
        ArxiuConversions.setTipusDocumental(metadades, "CODI-DESCONEGUT");
        assertThat(metadades.getTipusDocumental()).isNull();
        assertThat(metadades.getTipusDocumentalAddicional()).isEqualTo("CODI-DESCONEGUT");
    }

    @Test
    void getNtiTipoFirmaCobreixTotsElsTipusIIgnoraCsv() {
        for (FirmaTipus tipus : FirmaTipus.values()) {
            Document document = new Document();
            document.setFirmes(List.of(firma(tipus)));
            DocumentNtiTipoFirmaEnumDto resultat = ArxiuConversions.getNtiTipoFirma(document);
            if (tipus == FirmaTipus.CSV) {
                // Amb només una firma CSV, el bucle no troba cap firma "no CSV" i el resultat és null.
                assertThat(resultat).isNull();
            } else {
                assertThat(resultat).as("firma per %s", tipus).isNotNull();
            }
        }
    }

    @Test
    void getNtiTipoFirmaAmbFirmaCsvISenseCsvTrobaLaNoCsv() {
        Document document = new Document();
        document.setFirmes(Arrays.asList(firma(FirmaTipus.CSV), firma(FirmaTipus.PADES)));
        assertThat(ArxiuConversions.getNtiTipoFirma(document)).isEqualTo(DocumentNtiTipoFirmaEnumDto.TF06);
    }

    @Test
    void getNtiTipoFirmaSenseFirmesRetornaNull() {
        Document document = new Document();
        assertThat(ArxiuConversions.getNtiTipoFirma(document)).isNull();
        document.setFirmes(List.of());
        assertThat(ArxiuConversions.getNtiTipoFirma(document)).isNull();
    }

    @Test
    void toArxiuFirmaTipusCobreixTotsElsCodisIElPerDefecte() {
        for (DocumentNtiTipoFirmaEnumDto codi : DocumentNtiTipoFirmaEnumDto.values()) {
            assertThat(ArxiuConversions.toArxiuFirmaTipus(codi.name())).as("codi %s", codi).isNotNull();
        }
        assertThat(ArxiuConversions.toArxiuFirmaTipus("DESCONEGUT")).isNull();
    }

    @Test
    void getFirmaTipusCobreixTotsElsValorsIAdmetNull() {
        for (ArxiuFirmaTipusEnumDto tipus : ArxiuFirmaTipusEnumDto.values()) {
            assertThat(ArxiuConversions.getFirmaTipus(tipus)).as("tipus %s", tipus).isNotNull();
        }
        assertThat(ArxiuConversions.getFirmaTipus(null)).isNull();
    }

    @Test
    void getFirmaPerfilCobreixTotsElsValorsIAdmetNull() {
        for (ArxiuFirmaPerfilEnumDto perfil : ArxiuFirmaPerfilEnumDto.values()) {
            assertThat(ArxiuConversions.getFirmaPerfil(perfil)).as("perfil %s", perfil).isNotNull();
        }
        assertThat(ArxiuConversions.getFirmaPerfil(null)).isNull();
    }

    @Test
    void toArxiuFirmaPerfilEnumCobreixValorsConeguts() {
        String[] codisPerfil = {
                "AdES-BES", "AdES-EPES", "AdES-T", "AdES-C", "AdES-X",
                "AdES-XL", "AdES-A", "PAdES-LTV", "PAdES-Basic"
        };
        for (String codi : codisPerfil) {
            assertThat(ArxiuConversions.toArxiuFirmaPerfilEnum(codi)).as("codi %s", codi).isNotNull();
        }
        assertThat(ArxiuConversions.toArxiuFirmaPerfilEnum("DESCONEGUT")).isNull();
    }

    @Test
    void toArxiuFirmaTipusEnumCobreixTotesLesCombinacions() {
        assertThat(ArxiuConversions.toArxiuFirmaTipusEnum("PAdES", "qualsevol")).isEqualTo(ArxiuFirmaTipusEnumDto.PADES);
        assertThat(ArxiuConversions.toArxiuFirmaTipusEnum("qualsevol", "implicit_enveloped/attached")).isEqualTo(ArxiuFirmaTipusEnumDto.PADES);
        assertThat(ArxiuConversions.toArxiuFirmaTipusEnum("XAdES", "explicit/detached")).isEqualTo(ArxiuFirmaTipusEnumDto.XADES_DET);
        assertThat(ArxiuConversions.toArxiuFirmaTipusEnum("XAdES", "implicit_enveloping/attached")).isEqualTo(ArxiuFirmaTipusEnumDto.XADES_ENV);
        assertThat(ArxiuConversions.toArxiuFirmaTipusEnum("CAdES", "explicit/detached")).isEqualTo(ArxiuFirmaTipusEnumDto.CADES_DET);
        assertThat(ArxiuConversions.toArxiuFirmaTipusEnum("CAdES", "implicit_enveloping/attached")).isEqualTo(ArxiuFirmaTipusEnumDto.CADES_ATT);
        assertThat(ArxiuConversions.toArxiuFirmaTipusEnum("desconegut", "desconegut")).isNull();
    }

    @Test
    void getNtiCsvAmbFirmaCsvRetornaDadesDeCsv() {
        Document document = new Document();
        Firma firmaCsv = firma(FirmaTipus.CSV);
        firmaCsv.setCsvRegulacio("REGULACIO-CSV");
        firmaCsv.setContingut("contingut-csv".getBytes());
        document.setFirmes(List.of(firmaCsv));

        String[] ntiCsv = ArxiuConversions.getNtiCsv(document);

        assertThat(ntiCsv[0]).isEqualTo("REGULACIO-CSV");
        assertThat(ntiCsv[1]).isEqualTo("contingut-csv");
    }

    @Test
    void getNtiCsvSenseFirmesRetornaArrayBuit() {
        Document document = new Document();
        String[] ntiCsv = ArxiuConversions.getNtiCsv(document);
        assertThat(ntiCsv).hasSize(2);
        assertThat(ntiCsv[0]).isNull();
        assertThat(ntiCsv[1]).isNull();
    }

    @Test
    void getNtiCsvAmbFirmaCsvSenseContingutDeixaContingutNull() {
        Document document = new Document();
        Firma firmaCsv = firma(FirmaTipus.CSV);
        firmaCsv.setCsvRegulacio("REGULACIO-CSV");
        document.setFirmes(List.of(firmaCsv));

        String[] ntiCsv = ArxiuConversions.getNtiCsv(document);

        assertThat(ntiCsv[0]).isEqualTo("REGULACIO-CSV");
        assertThat(ntiCsv[1]).isNull();
    }

    private static Document documentAmbTipus(DocumentTipus tipus, String tipusAddicional) {
        Document document = new Document();
        DocumentMetadades metadades = new DocumentMetadades();
        metadades.setTipusDocumental(tipus);
        metadades.setTipusDocumentalAddicional(tipusAddicional);
        document.setMetadades(metadades);
        return document;
    }

    private static Firma firma(FirmaTipus tipus) {
        Firma firma = new Firma();
        firma.setTipus(tipus);
        firma.setPerfil(FirmaPerfil.BES);
        return firma;
    }
}
