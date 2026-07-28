package es.caib.pinbal.api.interna.ws;

import es.caib.pinbal.client.recobriment.model.ScspAtributos;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.logic.intf.service.RecobrimentService;
import es.caib.pinbal.logic.intf.service.exception.RecobrimentScspException;
import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.bean.common.peticion.Atributos;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.common.exceptions.ScspException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.xml.ws.WebServiceContext;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecobrimentBeanTest {

	@Mock
	private RecobrimentService recobrimentService;
	@Mock
	private WebServiceContext webServiceContext;

	@InjectMocks
	private RecobrimentBean recobrimentBean;

	@BeforeEach
	void setUp() {
//		recobrimentBean = new RecobrimentBean(recobrimentService, );
//		recobrimentBean.setRecobrimentService(recobrimentService);
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

	@Test
	void peticionAsincronaDelegatesToRestRecobrimentService() throws Exception {
		when(recobrimentService.peticionAsincrona(any(ScspPeticion.class))).thenReturn(new ScspConfirmacionPeticion());

		ConfirmacionPeticion response = recobrimentBean.peticionAsincrona(peticion("PET-1", "SERVEI"));

		assertNotNull(response);
		verify(recobrimentService).peticionAsincrona(any(ScspPeticion.class));
	}

	@Test
	void getRespuestaDelegatesToRestRecobrimentService() throws Exception {
		when(recobrimentService.getRespuesta("PET-1")).thenReturn(new ScspRespuesta());

		Respuesta response = recobrimentBean.getRespuesta("PET-1");

		assertNotNull(response);
		verify(recobrimentService).getRespuesta("PET-1");
	}

	@Test
	void getJustificanteThrowsScspExceptionOnRecobrimentScspException() throws Exception {
		when(recobrimentService.getJustificante(anyString(), anyString()))
				.thenThrow(new RecobrimentScspException("no disponible"));

		assertThrows(ScspException.class, () -> recobrimentBean.getJustificante("PET-1", "SOL-1"));
	}

	@Test
	void peticionSincronaUnwrapsNestedScspException() throws Exception {
		ScspException causa = new ScspException("error scsp original", "9999");
		when(recobrimentService.peticionSincrona(any(ScspPeticion.class)))
				.thenThrow(new RuntimeException("error de servei", causa));

		ScspException resultat = assertThrows(ScspException.class,
				() -> recobrimentBean.peticionSincrona(peticion("PET-1", "SERVEI")));

		assertEquals("9999", resultat.getScspCode());
	}

	@Test
	void peticionSincronaWrapsPlainExceptionAsScspException() throws Exception {
		when(recobrimentService.peticionSincrona(any(ScspPeticion.class)))
				.thenThrow(new RuntimeException("error inesperat"));

		ScspException resultat = assertThrows(ScspException.class,
				() -> recobrimentBean.peticionSincrona(peticion("PET-1", "SERVEI")));

		assertEquals("error inesperat", resultat.getMessage());
	}

	@Test
	void checkPermissionThrowsWhenNoSpringSecurityNorWebServiceRole() {
		SecurityContextHolder.clearContext();

		ScspException resultat = assertThrows(ScspException.class,
				() -> recobrimentBean.getRespuesta("PET-1"));

		assertEquals("0227", resultat.getScspCode());
	}

	@Test
	void hasWebServiceRoleGrantsAccessWhenPrincipalHasRole() throws Exception {
		SecurityContextHolder.clearContext();
		Principal principal = () -> "usuariWs";
		when(webServiceContext.getUserPrincipal()).thenReturn(principal);
		when(webServiceContext.isUserInRole("PBL_WS")).thenReturn(true);
		when(recobrimentService.getRespuesta("PET-1")).thenReturn(new ScspRespuesta());

		Respuesta response = recobrimentBean.getRespuesta("PET-1");

		assertNotNull(response);
	}

	@Test
	void hasWebServiceRoleFallsBackWhenIllegalStateException() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
				"usuari",
				"N/A",
				AuthorityUtils.createAuthorityList("PBL_WS")));
		when(webServiceContext.getUserPrincipal()).thenThrow(new IllegalStateException("sense context"));
		when(recobrimentService.getRespuesta("PET-1")).thenReturn(new ScspRespuesta());

		Respuesta response = recobrimentBean.getRespuesta("PET-1");

		assertNotNull(response);
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
