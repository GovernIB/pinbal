package es.caib.pinbal.back.command;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.caib.pinbal.logic.intf.dto.ServeiXsdDto;
import es.caib.pinbal.logic.intf.dto.XsdTipusEnumDto;
import es.caib.pinbal.back.helper.ConversioTipusHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;


/**
 * Informació d'un fitxer XSD d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServeiXsdCommand {

	private String codi;
	@NotNull
	private XsdTipusEnumDto tipus;
	@NotNull
	private String nomArxiu;
	@JsonIgnore
	@NotNull
	private MultipartFile contingut;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public static ServeiXsdCommand asCommand(ServeiXsdDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				ServeiXsdCommand.class);
	}
	public static ServeiXsdDto asDto(ServeiXsdCommand command) throws IOException {
		return ConversioTipusHelper.convertir(
				command,
				ServeiXsdDto.class);
	}
}
