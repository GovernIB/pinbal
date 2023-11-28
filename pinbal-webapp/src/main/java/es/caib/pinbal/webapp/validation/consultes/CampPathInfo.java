package es.caib.pinbal.webapp.validation.consultes;

import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.webapp.controller.ConsultaController;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampPathInfo {

    private String etiqueta;
    private String path;
    private ServeiCampDto.ServeiCampDtoTipus tipus;
    private List<String> valorsPermesos;
    private boolean validPath;

    private static SimpleDateFormat sdf = new SimpleDateFormat(ConsultaController.FORMAT_DATA_DADES_ESPECIFIQUES);

    public boolean isValidValue(String valor) {
        // Null és un valor vàlid
        if (valor == null) {
            return true;
        }

        switch (tipus) {
            case NUMERIC:
                return isInteger(formatNumeric(valor));
            case DATA:
                try {
                    Date dataDate = sdf.parse(valor);
                    if (sdf.format(dataDate).equals(valor))
                        return true;
                } catch (Exception ex) {}
                return false;
            case ENUM:
                return valorsPermesos.contains(valor);
            case PAIS:
                // String 3 caràcters
                return valor.length() == 3;
            case PROVINCIA:
                // String numèric 2 dígits
                String codiProvincia = formatNumeric(valor);
                return codiProvincia.length() == 2 && isInteger(codiProvincia);
            case MUNICIPI_3:
                // String numèric 3 dígits
                String codiMunicipi3 = formatNumeric(valor);
                return codiMunicipi3.length() == 3 && isInteger(codiMunicipi3);
            case MUNICIPI_5:
                // String numèric 5 dígits
                String codiMunicipi5 = formatNumeric(valor);
                return codiMunicipi5.length() == 5 && isInteger(codiMunicipi5);
            case BOOLEA:
                return "true".equalsIgnoreCase(valor) || "false".equalsIgnoreCase(valor);
            default:
                return true;
        }
    }

    public Object getValue(String valor) {
        if (valor == null) {
            return null;
        }

        switch (tipus) {
            case NUMERIC:
                Integer value = null;
                try {
                    value = Integer.valueOf(formatNumeric(valor));
                    return value;
                } catch (NumberFormatException nfe) {}
                return valor;
            case DATA:
                Date data = null;
                try {
                    data = sdf.parse(valor);
                    return data;
                } catch (ParseException pe) {}
                return valor;
            case BOOLEA:
                return new Boolean(valor);
            case PROVINCIA:
            case MUNICIPI_3:
            case MUNICIPI_5:
                return formatNumeric(valor);
            default:
                return valor;
        }
    }

    private String formatNumeric(String valor) {
        if (valor.endsWith(".0") || valor.endsWith(",0")) {
            return valor.substring(0, valor.length() - 2);
        }
        return valor;
    }

    public boolean isDadaEspecifica() {
        return this.path != null && this.path.toLowerCase().startsWith("datosespecificos");
    }

    private static boolean isInteger(String s) {
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i), 10) < 0) return false;
        }
        return true;
    }
}
