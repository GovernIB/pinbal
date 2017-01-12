package es.caib.pinbal.scsp;

import java.io.InputStream;

import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

/** Classe que implementa la interfície URIResolver per poder trobar els fitxers xsd que s'inclouen dins
 * d'un altre xsd. Per exemple, quan l'xsd de dades pròpies fa un import d'un altre xsd es crida a aquesta
 * classe que resol la ruta cap al mateix directori del servei per a que el trobi.
 * @author danielm
 *
 */
public class SchemaUriResolver implements URIResolver{

	/** Uri cap al directori on es troben els xsd del servei, s'informa amb el constructor.*/
	private String uri;
	
	/**
	 * Constrocutor amb el codi del servei i la versió de l'esquema per a construir la ruta URI cap
	 * al directori dels esquemes. Amb el codi del servei i la versió es crea el nom del directori
	 * de la següent forma: "/schemas/[codCertificado]v[versionEsquema]/".
	 * 
	 * @param codCertificado Codi del servei
	 * @param versionEsquema Versió de l'esquema. Si és null llavors no es tindrà en compte i no s'afegirà
	 * la versió al nom del directori.
	 */
	public SchemaUriResolver(String codCertificado, String versionEsquema) {		
		this.uri = "/schemas/" + codCertificado;
		if (versionEsquema != null) {
			int index = versionEsquema.lastIndexOf("V");
			if (index != -1) {
				this.uri = this.uri + "v" + versionEsquema.substring(index + 1) + "/";
			}
		}
	}

	/** Mètode que es crida quan hi ha un import o include que s'ha de resoldre cap al mateix directori.
	 * Retorna l'InputSource de l'xsd sol·licitat.
	 */
	@Override
	public InputSource resolveEntity(String targetNamespace, String schemaLocation, String baseUri) {

	    InputStream resourceAsStream = this.getClass()
	            .getResourceAsStream(this.uri + schemaLocation);
	    return new InputSource(resourceAsStream);
	}

}
