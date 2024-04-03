package es.caib.pinbal.core.helper;

import es.caib.pinbal.core.dto.arxiu.ArxiuConversions;
import es.caib.pinbal.core.dto.arxiu.ArxiuDetallDto;
import es.caib.pinbal.core.dto.arxiu.ArxiuEstatEnumDto;
import es.caib.pinbal.core.dto.arxiu.ArxiuFirmaDto;
import es.caib.pinbal.core.dto.arxiu.ArxiuFirmaPerfilEnumDto;
import es.caib.pinbal.core.dto.arxiu.ArxiuFirmaTipusEnumDto;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaPerfil;
import es.caib.plugins.arxiu.api.FirmaTipus;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class ArxiuHelper {

    public static ArxiuDetallDto getArxiuDetall(Document arxiuDocument) {
        ArxiuDetallDto arxiuDetall = new ArxiuDetallDto();

        setCommonProperties(arxiuDocument, arxiuDetall);
        DocumentMetadades metadades = arxiuDocument.getMetadades();
        if (metadades != null) {
            setMetadataProperties(arxiuDocument, arxiuDetall, metadades);
        }
        if (arxiuDocument.getEstat() != null) {
            setDocumentStatus(arxiuDocument, arxiuDetall);
        }
        processFirmes(arxiuDocument, arxiuDetall);
        return arxiuDetall;
    }

    private static void setCommonProperties(Document arxiuDocument, ArxiuDetallDto arxiuDetall) {
        arxiuDetall.setIdentificador(arxiuDocument.getIdentificador());
        arxiuDetall.setNom(arxiuDocument.getNom());
    }

    private static void setMetadataProperties(Document arxiuDocument, ArxiuDetallDto arxiuDetall, DocumentMetadades metadades) {
        setEniProperties(arxiuDetall, metadades);
        setEniDateFormat(arxiuDetall, metadades);
        setContingutProperties(arxiuDocument, arxiuDetall);
    }

    private static void processFirmes(Document arxiuDocument, ArxiuDetallDto arxiuDetall) {
        List<Firma> firmes = arxiuDocument.getFirmes();
        if (firmes != null) {
            List<ArxiuFirmaDto> dtos = convertFirmesToFirmaDtos(firmes);
            arxiuDetall.setFirmes(dtos);
        }
    }

    private static void setEniProperties(ArxiuDetallDto arxiuDetall, DocumentMetadades metadades) {
        arxiuDetall.setEniVersio(metadades.getVersioNti());
        arxiuDetall.setEniIdentificador(metadades.getIdentificador());
        arxiuDetall.setSerieDocumental(metadades.getSerieDocumental());
        arxiuDetall.setEniDataCaptura(metadades.getDataCaptura());
        arxiuDetall.setEniOrigen(ArxiuConversions.getOrigen(metadades.getOrigen()));
        arxiuDetall.setEniEstatElaboracio(ArxiuConversions.getEstatElaboracio(metadades.getEstatElaboracio()));
        if (metadades.getFormat() != null) {
            arxiuDetall.setEniFormat(metadades.getFormat().toString());
        }
        arxiuDetall.setEniDocumentOrigenId(metadades.getIdentificadorOrigen());
    }

    private static void setEniDateFormat(ArxiuDetallDto arxiuDetall, DocumentMetadades metadades) {
        final String fechaSelladoKey = "eni:fecha_sellado";
        if (metadades.getMetadadesAddicionals() != null && metadades.getMetadadesAddicionals().containsKey(fechaSelladoKey)) {
            try {
                DateFormat dfIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                DateFormat dfOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date fechaSelladoValor = dfIn.parse(metadades.getMetadadesAddicionals().get(fechaSelladoKey).toString());
                String fechaSelladoValorStr = dfOut.format(fechaSelladoValor);
                metadades.getMetadadesAddicionals().put(fechaSelladoKey, fechaSelladoValorStr);
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
            }
        }
        arxiuDetall.setMetadadesAddicionals(metadades.getMetadadesAddicionals());
    }

    private static void setContingutProperties(Document arxiuDocument, ArxiuDetallDto arxiuDetall) {
        if (arxiuDocument.getContingut() != null) {
            arxiuDetall.setContingutArxiuNom(arxiuDocument.getContingut().getArxiuNom());
            arxiuDetall.setContingutTipusMime(arxiuDocument.getContingut().getTipusMime());
        }
    }

    private static void setDocumentStatus(Document arxiuDocument, ArxiuDetallDto arxiuDetall) {
        if (DocumentEstat.ESBORRANY.equals(arxiuDocument.getEstat())) {
            arxiuDetall.setArxiuEstat(ArxiuEstatEnumDto.ESBORRANY);
        } else if (DocumentEstat.DEFINITIU.equals(arxiuDocument.getEstat())) {
            arxiuDetall.setArxiuEstat(ArxiuEstatEnumDto.DEFINITIU);
        }
    }

    private static List<ArxiuFirmaDto> convertFirmesToFirmaDtos(List<Firma> firmes) {
        List<ArxiuFirmaDto> dtos = new ArrayList<>();
        for (Firma firma : firmes) {
            ArxiuFirmaDto dto = new ArxiuFirmaDto();
            dto.setTipus(convert(firma.getTipus()));
            dto.setPerfil(convert(firma.getPerfil()));
            dto.setFitxerNom(firma.getFitxerNom());
            if (ArxiuFirmaTipusEnumDto.CSV.equals(dto.getTipus())) {
                dto.setContingut(firma.getContingut());
            }
            dto.setTipusMime(firma.getTipusMime());
            dto.setCsvRegulacio(firma.getCsvRegulacio());
            dtos.add(dto);
        }
        return dtos;
    }

    private static ArxiuFirmaTipusEnumDto convert(FirmaTipus tipus) {
        if (tipus == null) {
            return null;
        }
        try {
            return ArxiuFirmaTipusEnumDto.valueOf(tipus.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static ArxiuFirmaPerfilEnumDto convert(FirmaPerfil perfil) {
        if (perfil == null) {
            return null;
        }
        try {
            return ArxiuFirmaPerfilEnumDto.valueOf(perfil.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
