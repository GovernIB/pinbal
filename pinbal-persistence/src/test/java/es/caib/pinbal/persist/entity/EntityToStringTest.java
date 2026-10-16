package es.caib.pinbal.persist.entity;

import es.caib.pinbal.logic.intf.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.persist.base.entity.EntityToStringHelper;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 * Tests de regressió del toString de les entitats: no han de recórrer les
 * relacions (OutOfMemoryError: Java heap space en crear una entitat amb el
 * log de Spring Data Auditing a nivell DEBUG).
 */
public class EntityToStringTest {

	private static final int NUM_ELEMENTS = 50;
	private static final int MAX_LONGITUD = 2000;

	@Test
	public void testToStringNoRecorreLesRelacions() {
		Usuari usuari = Usuari.getBuilderInicialitzat("USR1", "Usuari 1", "12345678A").build();
		Entitat entitat = Entitat.getBuilder("ENT1", "Entitat 1", "Q1234567A", Entitat.EntitatTipus.GOVERN).build();
		entitat.configurarIdPerTest(5L);
		entitat.setCreatedBy(usuari);
		EntitatUsuari entitatUsuari = null;
		for (int i = 0; i < NUM_ELEMENTS; i++) {
			Procediment procediment = Procediment.getBuilder(
					entitat, "PROC" + i, "Procediment " + i, "Departament", null, null, null, null).build();
			procediment.setCreatedBy(usuari);
			entitat.addProcediment(procediment);
			entitatUsuari = EntitatUsuari.getBuilder(
					entitat, usuari, "Departament", true, false, false, false, true).build();
			entitatUsuari.setCreatedBy(usuari);
			entitat.getUsuaris().add(entitatUsuari);
			usuari.getEntitats().add(entitatUsuari);
		}

		String usuariToString = usuari.toString();
		assertTrue(usuariToString.length() < MAX_LONGITUD, usuariToString);
		assertTrue(usuariToString.contains("codi=USR1"), usuariToString);
		assertTrue(usuariToString.contains("entitats.size=" + usuari.getEntitats().size()), usuariToString);
		assertFalse(usuariToString.contains("EntitatUsuari"), usuariToString);

		String entitatUsuariToString = entitatUsuari.toString();
		assertTrue(entitatUsuariToString.length() < MAX_LONGITUD, entitatUsuariToString);
		assertTrue(entitatUsuariToString.contains("entitat.id=5"), entitatUsuariToString);
		assertTrue(entitatUsuariToString.contains("usuari.codi=USR1"), entitatUsuariToString);
		assertFalse(entitatUsuariToString.contains("Entitat["), entitatUsuariToString);

		String entitatToString = entitat.toString();
		assertTrue(entitatToString.length() < MAX_LONGITUD, entitatToString);
		assertTrue(entitatToString.contains("procediments.size=" + NUM_ELEMENTS), entitatToString);
		assertTrue(entitatToString.contains("usuaris.size=" + NUM_ELEMENTS), entitatToString);
		assertFalse(entitatToString.contains("Procediment["), entitatToString);

		String procedimentToString = entitat.getProcediments().get(0).toString();
		assertTrue(procedimentToString.length() < MAX_LONGITUD, procedimentToString);
		assertTrue(procedimentToString.contains("entitat.id=5"), procedimentToString);
		assertFalse(procedimentToString.contains("Usuari["), procedimentToString);
	}

	@Test
	public void testToStringNoInicialitzaColleccionsLazy() {
		Entitat entitat = Entitat.getBuilder("ENT1", "Entitat 1", "Q1234567A", Entitat.EntitatTipus.GOVERN).build();
		entitat.setProcediments(new PersistentBag());

		String entitatToString = entitat.toString();
		assertTrue(entitatToString.contains("procediments.size=" + EntityToStringHelper.NO_INICIALITZADA), entitatToString);
	}

	@Test
	public void testToStringNoInicialitzaProxies() {
		LazyInitializer lazyInitializer = mock(LazyInitializer.class);
		when(lazyInitializer.getIdentifier()).thenReturn(7L);
		Entitat entitatProxy = mock(Entitat.class, withSettings().extraInterfaces(HibernateProxy.class));
		when(((HibernateProxy)entitatProxy).getHibernateLazyInitializer()).thenReturn(lazyInitializer);
		Procediment procediment = Procediment.getBuilder(
				entitatProxy, "PROC1", "Procediment 1", "Departament", null, null, null, null).build();

		String procedimentToString = procediment.toString();
		assertTrue(procedimentToString.contains("entitat.id=7"), procedimentToString);
		verify(entitatProxy, never()).getId();
	}

	@Test
	public void testToStringNoImprimeixContingutsGransNiPassword() {
		byte[] logo = new byte[1024 * 1024];
		OrganismeCessionari organisme = OrganismeCessionari.getBuilder(
				"Organisme", "Q1234567A", null, new Date(), Boolean.FALSE, logo, new ArrayList<>()).build();
		String organismeToString = organisme.toString();
		assertTrue(organismeToString.length() < MAX_LONGITUD, organismeToString);
		assertTrue(organismeToString.contains("logo.length=" + logo.length), organismeToString);

		ClauPrivada clauPrivada = ClauPrivada.getBuilder(
				"ALIES1", "Clau privada 1", "secret123", "NS12345", null, new Date(), true, organisme, false).build();
		String clauPrivadaToString = clauPrivada.toString();
		assertTrue(clauPrivadaToString.contains("alies=ALIES1"), clauPrivadaToString);
		assertFalse(clauPrivadaToString.contains("secret123"), clauPrivadaToString);
	}

	@Test
	public void testToStringConsulta() {
		Entitat entitat = Entitat.getBuilder("ENT1", "Entitat 1", "Q1234567A", Entitat.EntitatTipus.GOVERN).build();
		entitat.configurarIdPerTest(5L);
		Procediment procediment = Procediment.getBuilder(
				entitat, "PROC1", "Procediment 1", "Departament", null, null, null, null).build();
		ProcedimentServei procedimentServei = ProcedimentServei.getBuilder(procediment, "SERVEI1").build();
		Consulta consulta = Consulta.getBuilder(
				"PET1", "Joan Petit", "12345678A", "NIF", "87654321B", "Pere", "Gran", "Petit",
				"Pere Gran Petit", "Departament", procedimentServei, "Finalitat", Consentiment.Si,
				"EXP1", true, false, null).build();
		String dadesEspecifiques = new String(new char[100000]).replace('\0', 'x');
		consulta.updateDadesEspecifiques(dadesEspecifiques);

		String consultaToString = consulta.toString();
		assertTrue(consultaToString.length() < MAX_LONGITUD, consultaToString);
		assertTrue(consultaToString.contains("scspPeticionId=PET1"), consultaToString);
		assertTrue(consultaToString.contains("dadesEspecifiques.length=" + dadesEspecifiques.length()), consultaToString);
		assertTrue(consultaToString.contains("entitat.id=5"), consultaToString);
		assertTrue(consultaToString.contains("fills.size=0"), consultaToString);
	}

}
