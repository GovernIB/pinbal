package es.caib.pinbal.scsp;

import es.scsp.client.ScspSoapFaultMessageResolver;
import es.scsp.common.interceptors.InterceptorsFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.ws.FaultAwareWebServiceMessage;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.context.MessageContext;

import java.io.IOException;

/**
 * Substitueix l'InterceptorsFlow de la llibreria SCSP 5.x per tractar els SOAP Fault que
 * arriben amb un codi HTTP diferent de 500 (p.ex. versions d'EMISERV que retornen el Fault
 * amb HTTP 200).
 *
 * Spring-WS només considera Fault una resposta amb HTTP 500. Amb qualsevol altre codi la
 * tracta com una resposta normal i la llibreria en valida la signatura, que falla perquè
 * els Fault no van signats ("Firma no válida"). Aquí es detecta el Fault pel contingut
 * (com feia el client Axis2 de la llibreria 4.x) i es processa igual que ho fa la llibreria
 * amb un Fault rebut amb HTTP 500: s'executa el flux InFault i es resol amb
 * ScspSoapFaultMessageResolver, que retorna l'error SCSP (codi i literal) de l'emissor.
 *
 * La llibreria crea la ScspWebServiceTemplate a cada petició amb l'InterceptorsFlow injectat
 * per tipus a BasicServiceClientSpring, per això es declara com a bean @Primary.
 */
@Component
@Primary
public class PinbalInterceptorsFlow extends InterceptorsFlow {

	@Override
	public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
		WebServiceMessage resposta = messageContext.getResponse();
		if (resposta instanceof FaultAwareWebServiceMessage && ((FaultAwareWebServiceMessage)resposta).hasFault()) {
			LOGGER.warn("S'ha rebut un SOAP Fault amb un codi HTTP diferent de 500; es tracta com a Fault");
			handleFault(messageContext);
			try {
				new ScspSoapFaultMessageResolver().resolveFault(resposta);
			} catch (IOException ex) {
				// BasicServiceClientSpring extreu la ScspException de la ScspInnerException (causa)
				throw new WebServiceIOException("SOAP Fault rebut amb un codi HTTP diferent de 500", ex);
			}
		}
		return super.handleResponse(messageContext);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PinbalInterceptorsFlow.class);

}
