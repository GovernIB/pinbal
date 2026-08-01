package es.caib.pinbal.plugin.firmaservidor;

import es.caib.pinbal.plugin.SistemaExternException;
import org.fundaciobit.pluginsib.core.v3.utils.AbstractPluginProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Implementació mock del plugin de firma en servidor: no contacta amb cap
 * servei extern (a diferència de {@link FirmaSimpleServidorPluginPortafib},
 * que crida el portafirmes PortaFIB), simplement retorna el mateix
 * contingut rebut com si ja estigués firmat. Útil per a desenvolupament i
 * entorns e2e on no cal validar la firma real del document, només que el
 * flux de generació del justificant es completi.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaServidorPluginMock extends AbstractPluginProperties implements FirmaServidorPlugin {

	public FirmaServidorPluginMock(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}

	@Override
	public SignaturaResposta signar(SignaturaDades dades) throws SistemaExternException {
		logger.debug("[MOCK] Firma en servidor simulada per al document '" + dades.getNom() + "' (sense contactar cap servei extern)");
		return SignaturaResposta.builder()
				.contingut(dades.getContingut())
				.nom(dades.getNom())
				.mime(dades.getContentType())
				.tipusFirma(dades.getTipusFirma() != null ? dades.getTipusFirma().name() : null)
				.build();
	}

	private static final Logger logger = LoggerFactory.getLogger(FirmaServidorPluginMock.class);

}
