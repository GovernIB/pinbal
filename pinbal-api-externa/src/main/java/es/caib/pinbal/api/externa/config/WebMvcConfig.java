/**
 *
 */
package es.caib.pinbal.api.externa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * Configuració de Spring web MVC.
 *
 * @author Limit Tecnologies
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	// Accepta offsets amb i sense dos punts: +02:00 i +0200
	private static final DateTimeFormatter LENIENT_DATE_TIME = new DateTimeFormatterBuilder()
			.append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
			.optionalStart().appendOffsetId().optionalEnd()
			.optionalStart().appendOffset("+HHmm", "Z").optionalEnd()
			.toFormatter();

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(String.class, Date.class, source -> {
			// Normalitza les tres formes en que pot arribar el signe '+' de l'offset:
			//   %2B / %2b → no decodificat per JBoss/proxy
			//   ' '       → '+' no percent-encodat, decodificat com a espai
			String normalized = source.trim()
					.replace("%2B", "+")
					.replace("%2b", "+")
					.replace(" ", "+");
			try {
				OffsetDateTime odt = OffsetDateTime.parse(normalized, LENIENT_DATE_TIME);
				return Date.from(odt.toInstant());
			} catch (DateTimeParseException e) {
				// Format date-only (p.ex. 2024-09-19) → inici del dia en UTC
				LocalDate ld = LocalDate.parse(normalized, DateTimeFormatter.ISO_LOCAL_DATE);
				return Date.from(ld.atStartOfDay(ZoneOffset.UTC).toInstant());
			}
		});
	}

}
