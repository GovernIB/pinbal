/**
 * 
 */
package es.caib.pinbal.wso2.mediator;

import org.apache.axiom.soap.SOAPBody;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

/**
 * Mediator per a obtenir un codi d'entitat a partir d'una petició
 * SCSP.
 * Aquest mediator té dos paràmetres a configurar:
 * <ul>
 *   <li>classProperty: Propietat a on està emmagatzemada la classe per a resoldre el codi d'entitat.</li>
 *   <li>entitatProperty: Propietat a on es guardarà el codi d'entitat resolt.</li>
 * </ul>
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatResolverMediator extends AbstractMediator {

	private String classProperty;
	private String entitatProperty;

	public boolean mediate(MessageContext context) {
		SOAPBody body = context.getEnvelope().getBody();
		System.out.println("COS DEL MISSATGE " + body.toString());
		String entitatResolverClass = (String)context.getProperty(classProperty);
		if (entitatResolverClass == null || entitatResolverClass.isEmpty())
			handleException(
					"No s'ha especificat cap classe per a resoldre entitats",
					context);
		try {
			EntitatResolver entitatResolver = (EntitatResolver)Class.forName(entitatResolverClass).newInstance();
			String entitatCodi = entitatResolver.resolve(body.getFirstElement());
			if (entitatCodi == null || entitatCodi.isEmpty())
				handleException(
						"Codi d'entitat buit retornat per la classe (class=" + entitatResolverClass + ")",
						context);
			else
				context.setProperty(entitatProperty, entitatCodi);
		} catch (ClassNotFoundException ex) {
			handleException(
					"No s'ha trobat la classe (class=" + entitatResolverClass + ")",
					ex,
					context);
		} catch (InstantiationException ex) {
			handleException(
					"No s'ha pogut instanciar la classe (class=" + entitatResolverClass + ")",
					ex,
					context);
		} catch (IllegalAccessException ex) {
			handleException(
					"No s'ha pogut accedir a la classe o al seu constructor (class=" + entitatResolverClass + ")",
					ex,
					context);
		}
		return true;
	}

	public void setClassProperty(String classProperty) {
		this.classProperty = classProperty;
	}

	public void setEntitatProperty(String entitatProperty) {
		this.entitatProperty = entitatProperty;
	}

}
