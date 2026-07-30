/**
 *
 */
package es.caib.pinbal.persist.repository;

import es.caib.pinbal.logic.intf.dto.AvisNivellEnumDto;
import es.caib.pinbal.persist.config.PersistenceTestConfig;
import es.caib.pinbal.persist.entity.Avis;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test d'integració per al repositori d'avisos.
 *
 * Cobreix el bug on un avís amb data d'inici avui no es mostrava: la consulta
 * comparava la data d'inici (que pot tenir qualsevol hora del dia, no necessàriament
 * mitjanit) amb l'inici exacte del dia actual, fent que qualsevol hora posterior a
 * mitjanit el deixés fora fins l'endemà.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PersistenceTestConfig.class)
@Transactional
public class AvisRepositoryIntegrationTest {

	@Autowired
	private AvisRepository avisRepository;

	private Date atTime(int hourOfDay) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	private Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	private Date currentDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	@Test
	public void findActive_avisAmbDataIniciAvuiPeroNoAMitjanitEsTrobaActiu() {
		Avis avis = Avis.getBuilder("Assumpte", "Missatge", atTime(23), null, AvisNivellEnumDto.INFO).build();
		avisRepository.save(avis);
		avisRepository.flush();

		List<Avis> result = avisRepository.findActive(currentDate());

		assertEquals(1, result.size());
	}

	@Test
	public void findActive_avisAmbDataIniciDemaNoEsTrobaActiu() {
		Avis avis = Avis.getBuilder("Assumpte", "Missatge", addDays(atTime(0), 1), null, AvisNivellEnumDto.INFO).build();
		avisRepository.save(avis);
		avisRepository.flush();

		List<Avis> result = avisRepository.findActive(currentDate());

		assertTrue(result.isEmpty());
	}

	@Test
	public void findActive_avisAmbDataFinalAhirNoEsTrobaActiu() {
		Avis avis = Avis.getBuilder("Assumpte", "Missatge", addDays(atTime(0), -2), addDays(atTime(23), -1), AvisNivellEnumDto.INFO).build();
		avisRepository.save(avis);
		avisRepository.flush();

		List<Avis> result = avisRepository.findActive(currentDate());

		assertTrue(result.isEmpty());
	}

	@Test
	public void findActive_avisAmbDataFinalAvuiPeroNoAMitjanitEsTrobaActiu() {
		Avis avis = Avis.getBuilder("Assumpte", "Missatge", addDays(atTime(0), -1), atTime(1), AvisNivellEnumDto.INFO).build();
		avisRepository.save(avis);
		avisRepository.flush();

		List<Avis> result = avisRepository.findActive(currentDate());

		assertEquals(1, result.size());
	}

	@Test
	public void findActive_avisInactiuNoEsTrobaEncaraQueEstiguiDinsDelRang() {
		Avis avis = Avis.getBuilder("Assumpte", "Missatge", atTime(0), null, AvisNivellEnumDto.INFO).build();
		avis.updateActiva(false);
		avisRepository.save(avis);
		avisRepository.flush();

		List<Avis> result = avisRepository.findActive(currentDate());

		assertTrue(result.isEmpty());
	}

}
