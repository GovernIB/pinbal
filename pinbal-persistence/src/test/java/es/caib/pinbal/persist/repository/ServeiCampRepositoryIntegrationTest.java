package es.caib.pinbal.persist.repository;

import es.caib.pinbal.persist.config.PersistenceTestConfig;
import es.caib.pinbal.persist.entity.Servei;
import es.caib.pinbal.persist.entity.ServeiCamp;
import es.caib.pinbal.persist.entity.ServeiCampGrup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test d'integració per a comprovar que findByServeiOrderByGrupOrdreAsc
 * retorna també els camps sense grup (grup null), ja que la navegació
 * implícita "sc.grup.nom" a l'ORDER BY es pot compilar com a inner join
 * segons la versió d'Hibernate i descartar aquests camps silenciosament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PersistenceTestConfig.class)
@Transactional
public class ServeiCampRepositoryIntegrationTest {

	private static final String SERVEI_CODI = "SVCDATOS";

	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private ServeiCampRepository serveiCampRepository;
	@Autowired
	private ServeiCampGrupRepository serveiCampGrupRepository;

	@BeforeEach
	public void setUp() {
		Servei servei = new Servei();
		servei.setCodi(SERVEI_CODI);
		servei.setDescripcio("Servei de dades");
		serveiRepository.save(servei);
	}

	@Test
	public void findByServeiOrderByGrupOrdreAscRetornaElsCampsSenseGrup() {
		serveiCampRepository.save(
				ServeiCamp.getBuilder(SERVEI_CODI, "camp.sense.grup.1", 0, 64).build());
		serveiCampRepository.save(
				ServeiCamp.getBuilder(SERVEI_CODI, "camp.sense.grup.2", 1, 64).build());

		List<ServeiCamp> camps = serveiCampRepository.findByServeiOrderByGrupOrdreAsc(SERVEI_CODI);

		assertEquals(2, camps.size());
	}

	@Test
	public void findByServeiOrderByGrupOrdreAscRetornaCampsAmbISenseGrup() {
		ServeiCampGrup grup = serveiCampGrupRepository.save(
				ServeiCampGrup.getBuilder(SERVEI_CODI, null, "Grup 1", null, 0).build());
		ServeiCamp campAmbGrup = ServeiCamp.getBuilder(SERVEI_CODI, "camp.amb.grup", 0, 64).build();
		campAmbGrup.updateGrup(grup);
		serveiCampRepository.save(campAmbGrup);
		serveiCampRepository.save(
				ServeiCamp.getBuilder(SERVEI_CODI, "camp.sense.grup", 1, 64).build());

		List<ServeiCamp> camps = serveiCampRepository.findByServeiOrderByGrupOrdreAsc(SERVEI_CODI);

		assertEquals(2, camps.size());
		assertTrue(camps.stream().anyMatch(c -> "camp.amb.grup".equals(c.getPath())));
		assertTrue(camps.stream().anyMatch(c -> "camp.sense.grup".equals(c.getPath())));
	}

}
