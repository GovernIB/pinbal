package es.caib.pinbal.plugins;

/**
 * Plugin permetre la signatura de documents en servidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface FirmaServidorPlugin {

	public byte[] firmar(
			String nom,
			String motiu,
			byte[] contingut,
			TipusFirma tipusFirma,
			String idioma) throws SistemaExternException;

	public static enum TipusFirma {
		PADES,
		CADES,
		XADES
	}

}
