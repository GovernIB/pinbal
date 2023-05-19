package es.caib.pinbal.plugins;

/**
 * Plugin permetre la signatura de documents en servidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface FirmaServidorPlugin {

	public SignaturaResposta signar(SignaturaDades signaturaDades) throws SistemaExternException;

	public static enum TipusFirma {
		PADES,
		CADES,
		XADES
	}

}
