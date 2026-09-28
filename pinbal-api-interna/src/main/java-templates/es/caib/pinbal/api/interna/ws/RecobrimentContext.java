package es.caib.pinbal.api.interna.ws;

/**
 * Context path del servei SOAP de recobriment. El valor el genera Maven a partir
 * de la propietat pinbal.context.prefix (per defecte /pinbal/ws).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public final class RecobrimentContext {

	public static final String CONTEXT_ROOT = "${pinbal.context.ws}";

	private RecobrimentContext() {
	}

}
