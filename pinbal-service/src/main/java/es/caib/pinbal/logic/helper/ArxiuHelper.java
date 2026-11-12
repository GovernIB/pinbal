package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.arxiu.*;
import es.caib.pluginsib.arxiu.api.*;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
            completarFirmes(dtos, arxiuDocument, arxiuDetall);
            arxiuDetall.setFirmes(dtos);
        }
    }

    private static final String METADADA_PERFIL_FIRMA = "eni:perfil_firma";

    /**
     * La informació del document es consulta a l'arxiu sense contingut, i per a les firmes
     * integrades el plugin només retorna el tipus (i el perfil si el pot convertir a enumerat).
     * Es completen les dades que falten amb la informació que ja tenim del document:
     * <ul>
     *     <li>el perfil, a partir de la metadada {@code eni:perfil_firma};</li>
     *     <li>per a les firmes integrades al document, el nom del fitxer i el tipus MIME del
     *     propi document (en una firma PAdES el contingut és sempre un PDF).</li>
     * </ul>
     */
    private static void completarFirmes(List<ArxiuFirmaDto> firmes, Document arxiuDocument, ArxiuDetallDto arxiuDetall) {
        String perfilMetadada = getMetadadaPerfilFirma(arxiuDetall.getMetadadesAddicionals());
        for (ArxiuFirmaDto firma : firmes) {
            if (firma.getPerfil() == null && perfilMetadada != null) {
                firma.setPerfil(toPerfil(perfilMetadada));
            }
            firma.setPerfilDescripcio(firma.getPerfil() != null ? firma.getPerfil().name() : perfilMetadada);

            boolean integrada = isFirmaIntegrada(firma.getTipus());
            firma.setIntegrada(integrada);
            if (integrada) {
                if (isBuit(firma.getFitxerNom())) {
                    firma.setFitxerNom(!isBuit(arxiuDetall.getContingutArxiuNom())
                            ? arxiuDetall.getContingutArxiuNom()
                            : arxiuDocument.getNom());
                }
                if (isBuit(firma.getTipusMime())) {
                    if (!isBuit(arxiuDetall.getContingutTipusMime())) {
                        firma.setTipusMime(arxiuDetall.getContingutTipusMime());
                    } else if (ArxiuFirmaTipusEnumDto.PADES.equals(firma.getTipus())) {
                        firma.setTipusMime("application/pdf");
                    }
                }
            }
        }
    }

    private static String getMetadadaPerfilFirma(Map<String, Object> metadadesAddicionals) {
        if (metadadesAddicionals == null) {
            return null;
        }
        Object valor = metadadesAddicionals.get(METADADA_PERFIL_FIRMA);
        if (valor == null || isBuit(valor.toString())) {
            return null;
        }
        return valor.toString().trim();
    }

    /**
     * Converteix el perfil informat a l'arxiu a l'enumerat, acceptant variants habituals
     * ("EPES", "AdES-EPES", "BASELINE B-Level"...). Retorna null si no es reconeix.
     */
    static ArxiuFirmaPerfilEnumDto toPerfil(String valor) {
        if (isBuit(valor)) {
            return null;
        }
        String normalitzat = valor.trim().toUpperCase(Locale.ROOT).replaceAll("[\\s\\-]+", "_");
        if (normalitzat.startsWith("ADES_")) {
            normalitzat = normalitzat.substring("ADES_".length());
        }
        for (ArxiuFirmaPerfilEnumDto perfil : ArxiuFirmaPerfilEnumDto.values()) {
            if (perfil.name().equalsIgnoreCase(normalitzat)) {
                return perfil;
            }
        }
        return null;
    }

    private static boolean isFirmaIntegrada(ArxiuFirmaTipusEnumDto tipus) {
        return ArxiuFirmaTipusEnumDto.PADES.equals(tipus)
                || ArxiuFirmaTipusEnumDto.CADES_ATT.equals(tipus)
                || ArxiuFirmaTipusEnumDto.XADES_ENV.equals(tipus);
    }

    private static boolean isBuit(String valor) {
        return valor == null || valor.trim().isEmpty();
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
