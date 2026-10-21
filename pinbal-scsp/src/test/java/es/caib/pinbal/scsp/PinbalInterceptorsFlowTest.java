package es.caib.pinbal.scsp;

import es.scsp.common.exceptions.ScspException;
import es.scsp.common.exceptions.ScspInnerException;
import es.scsp.common.interceptors.InterceptorsFlow;
import es.scsp.common.interceptors.ScspClientHandler;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.context.DefaultMessageContext;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.springframework.ws.transport.TransportInputStream;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class PinbalInterceptorsFlowTest {

	private static final String SOAP_FAULT_0301 =
			"<?xml version='1.0' encoding='UTF-8'?>" +
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body>" +
			"<soapenv:Fault><faultcode>0301</faultcode><faultstring>Organismo no autorizado</faultstring><detail>" +
			"<a:Atributos xmlns:a=\"http://intermediacion.redsara.es/scsp/esquemas/V3/soapfaultatributos\">" +
			"<a:IdPeticion>PINPRE00000000000000055468</a:IdPeticion><a:NumElementos>0</a:NumElementos>" +
			"<a:TimeStamp>2026-10-20T12:45:23.475+02:00</a:TimeStamp><a:CodigoCertificado>SCDHPAJU</a:CodigoCertificado>" +
			"<a:Estado><a:CodigoEstado>0301</a:CodigoEstado>" +
			"<a:LiteralError>Organismo no autorizado S0711001H SCDHPAJU</a:LiteralError></a:Estado>" +
			"</a:Atributos></detail></soapenv:Fault></soapenv:Body></soapenv:Envelope>";

	private static final String RESPOSTA_NORMAL =
			"<?xml version='1.0' encoding='UTF-8'?>" +
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body>" +
			"<Respuesta xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta\"/>" +
			"</soapenv:Body></soapenv:Envelope>";

	private AxiomSoapMessageFactory messageFactory;
	private PinbalInterceptorsFlow interceptorsFlow;

	@Before
	public void configurar() throws Exception {
		messageFactory = new AxiomSoapMessageFactory();
		messageFactory.afterPropertiesSet();
		interceptorsFlow = spy(new PinbalInterceptorsFlow());
		// Els handlers de la llibreria necessiten el context SCSP complet: es substitueixen per fluxos buits
		doReturn(true).when(interceptorsFlow).handleFault(any());
		doReturn(new ScspClientHandler[0]).when(interceptorsFlow).generateResponseFlow(any());
	}

	private MessageContext contextAmbResposta(String xml) throws Exception {
		MessageContext messageContext = new DefaultMessageContext(messageFactory);
		messageContext.setResponse(messageFactory.createWebServiceMessage(respostaHttp(xml)));
		return messageContext;
	}

	/** Resposta HTTP amb Content-Type SOAP 1.1, com la que rep el transport de Spring-WS. */
	private static TransportInputStream respostaHttp(String xml) {
		return new TransportInputStream() {
			@Override
			protected InputStream createInputStream() {
				return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
			}
			@Override
			public Iterator<String> getHeaderNames() {
				return Collections.singletonList("Content-Type").iterator();
			}
			@Override
			public Iterator<String> getHeaders(String name) {
				return "Content-Type".equalsIgnoreCase(name)
						? Collections.singletonList("text/xml;charset=UTF-8").iterator()
						: Collections.<String>emptyIterator();
			}
		};
	}

	@Test
	public void faultRebutComARespostaNormalEsResolComErrorScspDeLEmissor() throws Exception {
		MessageContext messageContext = contextAmbResposta(SOAP_FAULT_0301);

		try {
			interceptorsFlow.handleResponse(messageContext);
			fail("Hauria d'haver llançat WebServiceIOException");
		} catch (WebServiceIOException ex) {
			// BasicServiceClientSpring tracta les excepcions amb una ScspInnerException com a causa
			assertTrue(ex.getCause() instanceof ScspInnerException);
			ScspException scspException = ((ScspInnerException)ex.getCause()).getSe();
			assertEquals("0301", scspException.getScspCode());
			assertEquals("Organismo no autorizado S0711001H SCDHPAJU", scspException.getMessage());
		}
		verify(interceptorsFlow).handleFault(messageContext);
		verify(interceptorsFlow, never()).generateResponseFlow(any());
	}

	@Test
	public void respostaNormalDelegaEnElFluxDeLaLlibreria() throws Exception {
		MessageContext messageContext = contextAmbResposta(RESPOSTA_NORMAL);

		assertTrue(interceptorsFlow.handleResponse(messageContext));

		verify(interceptorsFlow, never()).handleFault(any());
		verify(interceptorsFlow).generateResponseFlow(any());
	}

	@Test
	public void sInjectaPerTipusEnLlocDeLInterceptorsFlowDeLaLlibreria() {
		try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
				InterceptorsFlow.class, PinbalInterceptorsFlow.class, ConsumidorInterceptorsFlow.class)) {
			assertTrue(ctx.getBean(ConsumidorInterceptorsFlow.class).interceptorsFlow instanceof PinbalInterceptorsFlow);
			assertSame(ctx.getBean(PinbalInterceptorsFlow.class), ctx.getBean(InterceptorsFlow.class));
		}
	}

	/** Reprodueix la injecció de BasicServiceClientSpring (@Autowired per tipus). */
	static class ConsumidorInterceptorsFlow {
		@Autowired
		InterceptorsFlow interceptorsFlow;
	}

}
