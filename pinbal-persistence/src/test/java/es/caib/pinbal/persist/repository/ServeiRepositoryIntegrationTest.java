package es.caib.pinbal.persist.repository;

import es.caib.pinbal.client.recobriment.v2.Titular;
import es.caib.pinbal.client.serveis.ServeiBasic;
import es.caib.pinbal.persist.config.PersistenceTestConfig;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.EntitatServei;
import es.caib.pinbal.persist.entity.Procediment;
import es.caib.pinbal.persist.entity.ProcedimentServei;
import es.caib.pinbal.persist.entity.Servei;
import es.caib.pinbal.persist.entity.ServeiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test d'integració de les consultes de serveis emprades pel recobriment, que
 * projecten la configuració dels tipus de document admesos sobre ServeiBasic.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PersistenceTestConfig.class)
@Transactional
public class ServeiRepositoryIntegrationTest {

	private static final String SERVEI_CODI = "SVCDATOS";
	private static final String ENTITAT_CODI = "ENT001";
	private static final String PROCEDIMENT_CODI = "PROC001";

	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private ServeiConfigRepository serveiConfigRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private EntitatServeiRepository entitatServeiRepository;
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private ProcedimentServeiRepository procedimentServeiRepository;

	@BeforeEach
	public void setUp() {
		Servei servei = new Servei();
		servei.setCodi(SERVEI_CODI);
		servei.setDescripcio("Servei de dades");
		serveiRepository.save(servei);

		// Per defecte tots els tipus de document estan permesos; es desactiven
		// el CIF i el NIE per a comprovar que la projecció els té en compte.
		ServeiConfig serveiConfig = ServeiConfig.getBuilder(
				SERVEI_CODI,
				null,
				null,
				null,
				null,
				ServeiConfig.JustificantTipus.GENERAT,
				null,
				null,
				false,
				null,
				null,
				null,
				null,
				false,
				true,
				true,
				false,
				false,
				true).build();
		serveiConfig.setPermesDocumentTipusCif(false);
		serveiConfig.setPermesDocumentTipusNie(false);
		serveiConfigRepository.save(serveiConfig);

		Entitat entitat = Entitat.getBuilder(
				ENTITAT_CODI,
				"Entitat de proves",
				"00000000T",
				Entitat.EntitatTipus.ALTRES).build();
		entitat.setUnitatArrel("unknown");
		entitatRepository.save(entitat);
		entitatServeiRepository.save(EntitatServei.getBuilder(entitat, SERVEI_CODI).build());

		Procediment procediment = Procediment.getBuilder(
				entitat,
				PROCEDIMENT_CODI,
				"Procediment de proves",
				null,
				null,
				null,
				null,
				null).build();
		procedimentRepository.save(procediment);
		procedimentServeiRepository.save(ProcedimentServei.getBuilder(procediment, SERVEI_CODI).build());
	}

	@Test
	public void findAllServeisClientProjectaElsTipusDeDocumentPermesos() {
		List<ServeiBasic> serveis = serveiRepository.findAllServeisClient();

		assertServeiDeProves(serveis);
	}

	@Test
	public void findServeisClientByEntitatCodiProjectaElsTipusDeDocumentPermesos() {
		List<ServeiBasic> serveis = serveiRepository.findServeisClientByEntitatCodi(ENTITAT_CODI);

		assertServeiDeProves(serveis);
	}

	@Test
	public void findServeisClientByProcedimentCodiProjectaElsTipusDeDocumentPermesos() {
		List<ServeiBasic> serveis = serveiRepository.findServeisClientByProcedimentCodi(PROCEDIMENT_CODI);

		assertServeiDeProves(serveis);
	}

	private void assertServeiDeProves(List<ServeiBasic> serveis) {
		assertEquals(1, serveis.size());
		ServeiBasic servei = serveis.get(0);
		assertEquals(SERVEI_CODI, servei.getCodi());
		assertEquals("Servei de dades", servei.getDescripcio());
		assertTrue(servei.getActiu());
		assertEquals(
				Arrays.asList(
						Titular.DocumentTipus.DNI,
						Titular.DocumentTipus.NIF,
						Titular.DocumentTipus.Pasaporte),
				servei.getDocumentsTipusPermesos());
	}

}
