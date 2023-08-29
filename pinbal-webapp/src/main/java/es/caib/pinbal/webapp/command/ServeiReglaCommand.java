package es.caib.pinbal.webapp.command;

import es.caib.pinbal.core.dto.regles.AccioEnum;
import es.caib.pinbal.core.dto.regles.ModificatEnum;
import es.caib.pinbal.core.dto.regles.ServeiReglaDto;
import es.caib.pinbal.webapp.validation.ServeiRegla;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ServeiRegla
public class ServeiReglaCommand {
    private Long id;
    @Size(max = 255)
    @NotNull
    private String nom;
    @NotNull
    private Long serveiId;
    @NotNull
    private ModificatEnum modificat;
    @NotEmpty
    private List<String> modificatValor;
    @NotEmpty
    private List<String> afectatValor;
    @NotNull
    private AccioEnum accio;

    public static ServeiReglaCommand asCommand(ServeiReglaDto dto) {
        return ServeiReglaCommand.builder()
                .id(dto.getId())
                .nom(dto.getNom())
                .serveiId(dto.getServeiId())
                .modificat(dto.getModificat())
                .modificatValor(dto.getModificatValor() != null ? new ArrayList<String>(dto.getModificatValor()) : new ArrayList<String>())
                .afectatValor(dto.getAfectatValor() != null ? new ArrayList<String>(dto.getAfectatValor()) : new ArrayList<String>())
                .accio(dto.getAccio())
                .build();
    }

    public static ServeiReglaDto asDto(ServeiReglaCommand command) {
        return ServeiReglaDto.builder()
                .id(command.getId())
                .nom(command.getNom())
                .serveiId(command.getServeiId())
                .modificat(command.getModificat())
                .modificatValor(command.getModificatValor() != null ? new LinkedHashSet<String>(command.getModificatValor()) : new LinkedHashSet<String>())
                .afectatValor(command.getAfectatValor() != null ? new LinkedHashSet<String>(command.getAfectatValor()) : new LinkedHashSet<String>())
                .accio(command.getAccio())
                .build();
    }
}
