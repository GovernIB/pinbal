package es.caib.pinbal.api.interna.ws;

import es.caib.pinbal.client.recobriment.model.ScspAtributos;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.logic.intf.service.RecobrimentService;
import es.scsp.bean.common.peticion.Atributos;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.respuesta.Respuesta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecobrimentBeanTest {

	@Mock
	private RecobrimentService recobrimentService;

	private RecobrimentBean recobrimentBean;

	@BeforeEach
	void setUp() {
		recobrimentBean = new RecobrimentBean();
		recobrimentBean.setSpringRecobrimentService(recobrimentService);
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
				"usuari",
				"N/A",
				AuthorityUtils.createAuthorityList("PBL_WS")));
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void peticionSincronaDelegatesToRestRecobrimentService() throws Exception {
		ScspRespuesta serviceResponse = new ScspRespuesta();
		ScspAtributos responseAttributes = new ScspAtributos();
		responseAttributes.setIdPeticion("PET-1");
		responseAttributes.setNumElementos("1");
		responseAttributes.setCodigoCertificado("SERVEI");
		serviceResponse.setAtributos(responseAttributes);
		when(recobrimentService.peticionSincrona(any(ScspPeticion.class))).thenReturn(serviceResponse);

		Respuesta response = recobrimentBean.peticionSincrona(peticion("PET-1", "SERVEI"));

		ArgumentCaptor<ScspPeticion> captor = ArgumentCaptor.forClass(ScspPeticion.class);
		verify(recobrimentService).peticionSincrona(captor.capture());
		assertEquals("SERVEI", captor.getValue().getAtributos().getCodigoCertificado());
		assertEquals("PET-1", response.getAtributos().getIdPeticion());
	}

	@Test
	void getJustificanteReturnsServiceContent() throws Exception {
		ScspJustificante justificante = new ScspJustificante();
		justificante.setContingut(new byte[] {1, 2, 3});
		when(recobrimentService.getJustificante("PET-1", "SOL-1")).thenReturn(justificante);

		byte[] result = recobrimentBean.getJustificante("PET-1", "SOL-1");

		assertArrayEquals(new byte[] {1, 2, 3}, result);
		verify(recobrimentService).getJustificante("PET-1", "SOL-1");
	}

	private Peticion peticion(String idPeticion, String codigoCertificado) {
		Peticion peticion = new Peticion();
		Atributos atributos = new Atributos();
		atributos.setIdPeticion(idPeticion);
		atributos.setNumElementos(1);
		atributos.setCodigoCertificado(codigoCertificado);
		peticion.setAtributos(atributos);
		return peticion;
	}

}
