package es.caib.pinbal.logic.intf.dto;

import es.caib.pinbal.logic.intf.helper.ToStringHelper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests de regressió del toString dels DTOs: no han d'imprimir les
 * col·leccions, els objectes relacionats ni els continguts grans.
 */
public class DtoToStringTest {

	private static final int NUM_ELEMENTS = 50;
	private static final int MAX_LONGITUD = 3000;

	@Test
	public void testEntitatDtoNoImprimeixUsuaris() {
		EntitatDto entitat = new EntitatDto();
		entitat.setId(5L);
		entitat.setCodi("ENT1");
		List<EntitatUsuariDto> usuaris = new ArrayList<>();
		for (int i = 0; i < NUM_ELEMENTS; i++) {
			UsuariDto usuari = new UsuariDto();
			usuari.setCodi("USR" + i);
			usuaris.add(new EntitatUsuariDto(usuari, "Departament", false, true, false, false, false, true));
		}
		entitat.setUsuaris(usuaris);

		String entitatToString = entitat.toString();
		assertTrue(entitatToString.length() < MAX_LONGITUD, entitatToString);
		assertTrue(entitatToString.contains("codi=ENT1"), entitatToString);
		assertTrue(entitatToString.contains("usuaris.size=" + NUM_ELEMENTS), entitatToString);
		assertFalse(entitatToString.contains("UsuariDto("), entitatToString);

		ServeiBusDto serveiBus = new ServeiBusDto();
		serveiBus.setEntitat(entitat);
		String serveiBusToString = serveiBus.toString();
		assertTrue(serveiBusToString.contains("entitat.id=5"), serveiBusToString);
		assertFalse(serveiBusToString.contains("EntitatDto("), serveiBusToString);
	}

	@Test
	public void testConsultaDtoNoImprimeixContingutsGrans() {
		String xml = new String(new char[100000]).replace('\0', 'x');
		UsuariDto usuari = new UsuariDto();
		usuari.setCodi("USR1");
		ConsultaDto consulta = new ConsultaDto();
		consulta.setScspPeticionId("PET1");
		consulta.setPeticioXml(xml);
		consulta.setRespostaXml(xml);
		consulta.setCreacioUsuari(usuari);
		consulta.setDadesPeticioMultiple(new String[NUM_ELEMENTS][10]);

		String consultaToString = consulta.toString();
		assertTrue(consultaToString.length() < MAX_LONGITUD, consultaToString);
		assertTrue(consultaToString.contains("scspPeticionId=PET1"), consultaToString);
		assertTrue(consultaToString.contains("peticioXml.length=" + xml.length()), consultaToString);
		assertTrue(consultaToString.contains("respostaXml.length=" + xml.length()), consultaToString);
		assertTrue(consultaToString.contains("creacioUsuari.codi=USR1"), consultaToString);
		assertTrue(consultaToString.contains("dadesPeticioMultiple.length=" + NUM_ELEMENTS), consultaToString);
	}

	@Test
	public void testFitxerDtoNoImprimeixContingut() {
		byte[] contingut = new byte[1024 * 1024];
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom("document.pdf");
		fitxer.setContingut(contingut);

		String fitxerToString = fitxer.toString();
		assertTrue(fitxerToString.length() < MAX_LONGITUD, fitxerToString);
		assertTrue(fitxerToString.contains("nom=document.pdf"), fitxerToString);
		assertTrue(fitxerToString.contains("contingut.length=" + contingut.length), fitxerToString);
	}

	@Test
	public void testPaginaDtoNoImprimeixContingut() {
		PaginaDto<ConsultaDto> pagina = new PaginaDto<>();
		List<ConsultaDto> contingut = new ArrayList<>();
		for (int i = 0; i < NUM_ELEMENTS; i++) {
			contingut.add(new ConsultaDto());
		}
		pagina.setContingut(contingut);

		String paginaToString = pagina.toString();
		assertTrue(paginaToString.contains("contingut.size=" + NUM_ELEMENTS), paginaToString);
		assertFalse(paginaToString.contains("ConsultaDto("), paginaToString);
	}

	@Test
	public void testToStringHelper() {
		assertNull(ToStringHelper.mida((List<?>)null));
		assertNull(ToStringHelper.mida((Map<?, ?>)null));
		assertNull(ToStringHelper.longitud((Object[])null));
		assertNull(ToStringHelper.longitud((byte[])null));
		assertNull(ToStringHelper.longitud((String)null));
		assertEquals(Integer.valueOf(1), ToStringHelper.mida(Collections.singletonList("a")));
		assertEquals(Integer.valueOf(1), ToStringHelper.mida(Collections.singletonMap("a", "b")));
		assertEquals(Integer.valueOf(2), ToStringHelper.longitud(new String[2]));
		assertEquals(Integer.valueOf(3), ToStringHelper.longitud(new byte[3]));
		assertEquals(Integer.valueOf(4), ToStringHelper.longitud("abcd"));
	}

}
