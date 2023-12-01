package es.caib.pinbal.core.helper;

import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.DocumentExtensio;
import es.caib.plugins.arxiu.api.DocumentFormat;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.DocumentTipus;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaPerfil;
import es.caib.plugins.arxiu.api.FirmaTipus;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ArxiuDocumentMock {


   private Document document;


    public ArxiuDocumentMock() {

        document = new Document();
        initDocument();
    }

    private void initDocument() {

        document.setIdentificador("documentIdentificador-test");
        document.setNom("nomDocument-test");
        document.setDescripcio("descripcioDocument-test");
        document.setVersio("versio-test");
        document.setEstat(DocumentEstat.DEFINITIU);
        initFirmes();
        initExpedientMetadades();
        initDocumentMetadades();
    }

    private void initFirmes() {

        List<Firma> firmes = new ArrayList<>();
        Firma firma;
        for (int foo=0;foo<2;foo++) {
            firma = new Firma();
            firma.setTipus(FirmaTipus.CSV);
            firma.setPerfil(FirmaPerfil.A);
            firma.setFitxerNom("nomFitxer-test");
            firma.setContingut("contingut-test".getBytes());
            firma.setTamany(100);
            firma.setTipusMime("tipusMime");
            firma.setCsvRegulacio("csvRegulacio-test");
            firmes.add(firma);
        }
        document.setFirmes(firmes);
    }

    private void initExpedientMetadades() {

        ExpedientMetadades metadades = new ExpedientMetadades();
        metadades.setIdentificador("identificador-test");
        metadades.setVersioNti("versionti-Test");
        metadades.setOrgans(Arrays.asList("organ1", "organ2"));
        metadades.setDataObertura(new Date());
        metadades.setClassificacio("classificacio");
        metadades.setEstat(ExpedientEstat.OBERT);
        metadades.setInteressats(Arrays.asList("interessat1", "interessat2"));
        metadades.setSerieDocumental("serieDocumental-test");
        Map<String, Object> addicionals = new HashMap<>();
        addicionals.put("addicional1", "addicional1");
        addicionals.put("addicional2", "addicional2");
        metadades.setMetadadesAddicionals(addicionals);
        document.setExpedientMetadades(metadades);
    }

    private void initDocumentMetadades() {

        DocumentMetadades metadades = new DocumentMetadades();
        metadades.setIdentificador("identificador-test");
        metadades.setVersioNti("versioNti-test");
        metadades.setOrigen(ContingutOrigen.ADMINISTRACIO);
        metadades.setOrgans(Arrays.asList("organ1", "organ2"));
        metadades.setDataCaptura(new Date());
        metadades.setEstatElaboracio(DocumentEstatElaboracio.ORIGINAL);
        metadades.setTipusDocumental(DocumentTipus.COMUNICACIO);
        metadades.setFormat(DocumentFormat.MP3);
        metadades.setExtensio(DocumentExtensio.MP3);
        metadades.setIdentificadorOrigen("identificadorOrigen-test");
        metadades.setMetadadesAddicionals(null);
        metadades.setSerieDocumental("serieDocumental-test");
        metadades.setCsv("csv-test");
        metadades.setCsvDef("csvDef-test");
        metadades.setTipusDocumentalAddicional("tipusDocumentalAdicional-test");
        Map<String, Object> addicionals = new HashMap<>();
        addicionals.put("addicional11", "addicional11");
        addicionals.put("addicional22", "addicional22");
        metadades.setMetadadesAddicionals(addicionals);
        document.setMetadades(metadades);
    }
}
