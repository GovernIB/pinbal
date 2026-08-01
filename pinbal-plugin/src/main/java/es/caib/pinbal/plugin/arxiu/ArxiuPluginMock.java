package es.caib.pinbal.plugin.arxiu;

import es.caib.pluginsib.arxiu.api.ArxiuException;
import es.caib.pluginsib.arxiu.api.Carpeta;
import es.caib.pluginsib.arxiu.api.ConsultaFiltre;
import es.caib.pluginsib.arxiu.api.ConsultaResultat;
import es.caib.pluginsib.arxiu.api.ContingutArxiu;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentContingut;
import es.caib.pluginsib.arxiu.api.DocumentRepositori;
import es.caib.pluginsib.arxiu.api.DocumentTipusAddicional;
import es.caib.pluginsib.arxiu.api.Expedient;
import es.caib.pluginsib.arxiu.api.Firma;
import org.fundaciobit.pluginsib.core.v3.utils.AbstractPluginProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementació mock del plugin d'arxiu digital: no contacta amb cap servei
 * extern (a diferència d'{@link ArxiuPluginCaib}, que truca el servei
 * ConCSV/arxiu digital real del CAIB), simplement guarda els expedients i
 * documents en memòria. Útil per a desenvolupament i entorns e2e on no cal
 * un arxiu digital real, només que el flux de generació i desat del
 * justificant es completi (JustificantHelper.desarJustificantArxiu).
 * <p>
 * Només tenen lògica els mètodes realment exercitats per aquest flux:
 * {@link #expedientCrear}, {@link #expedientConsulta} (usat per cercar un
 * expedient existent pel seu nom), {@link #documentCrear},
 * {@link #documentDetalls} i {@link #documentImprimible} (usats en
 * recuperar un justificant ja desat a l'arxiu en descàrregues posteriors a
 * la primera). La resta de mètodes de la interfície no s'exerciten en
 * aquest flux i es deixen sense implementar.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuPluginMock extends AbstractPluginProperties implements ArxiuPlugin {

	public ArxiuPluginMock(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}

	private final Map<String, Expedient> expedients = new ConcurrentHashMap<>();
	private final Map<String, Document> documents = new ConcurrentHashMap<>();

	@Override
	public ContingutArxiu expedientCrear(Expedient expedient) throws ArxiuException {
		String uuid = UUID.randomUUID().toString();
		expedient.setIdentificador(uuid);
		expedients.put(uuid, expedient);
		logger.debug("[MOCK] Creat expedient a l'arxiu (uuid=" + uuid + ", nom=" + expedient.getNom() + ")");
		return expedient;
	}

	@Override
	public ConsultaResultat expedientConsulta(List<ConsultaFiltre> filtres, Integer pagina, Integer mida) throws ArxiuException {
		String nomCercat = null;
		if (filtres != null) {
			for (ConsultaFiltre filtre : filtres) {
				if ("name".equals(filtre.getMetadada())) {
					nomCercat = filtre.getValorOperacio1();
				}
			}
		}
		List<ContingutArxiu> resultats = new ArrayList<>();
		if (nomCercat != null) {
			for (Expedient expedient : expedients.values()) {
				if (nomCercat.equals(expedient.getNom())) {
					resultats.add(expedient);
				}
			}
		}
		logger.debug("[MOCK] Consulta d'expedients per nom '" + nomCercat + "': " + resultats.size() + " resultat(s)");
		return new ConsultaResultat(resultats.size(), resultats);
	}

	@Override
	public ContingutArxiu documentCrear(Document document, String expedientUuid) throws ArxiuException {
		if (expedientUuid == null || !expedients.containsKey(expedientUuid)) {
			throw new ArxiuException("[MOCK] No existeix cap expedient amb identificador '" + expedientUuid + "' a l'arxiu");
		}
		String uuid = UUID.randomUUID().toString();
		document.setIdentificador(uuid);
		documents.put(uuid, document);
		logger.debug("[MOCK] Creat document a l'arxiu (uuid=" + uuid + ", nom=" + document.getNom() + ", expedientUuid=" + expedientUuid + ")");
		return document;
	}

	@Override
	public Document documentDetalls(String identificador, String versio, boolean ambContingut) throws ArxiuException {
		Document document = documents.get(identificador);
		if (document == null) {
			throw new ArxiuException("[MOCK] No existeix cap document amb identificador '" + identificador + "' a l'arxiu");
		}
		// Un document amb firma adjunta (cas d'arxiuDocumentGuardarFirmaPades,
		// PluginHelper.toArxiuDocument amb fitxer==null) no té 'contingut'
		// propi: el PDF firmat viu dins la firma. PluginHelper.
		// arxiuDocumentConsultar espera poder llegir document.getContingut()
		// igualment quan ambContingut=true (NPE en cas contrari), així que cal
		// reconstruir-lo a partir de la primera firma.
		if (ambContingut && document.getContingut() == null && document.getFirmes() != null && !document.getFirmes().isEmpty()) {
			Firma firma = document.getFirmes().get(0);
			DocumentContingut contingut = new DocumentContingut();
			contingut.setContingut(firma.getContingut());
			contingut.setTipusMime(firma.getTipusMime());
			contingut.setArxiuNom(firma.getFitxerNom());
			document.setContingut(contingut);
		}
		return document;
	}

	@Override
	public DocumentContingut documentImprimible(String identificador) throws ArxiuException {
		Document document = documentDetalls(identificador, null, true);
		return document.getContingut();
	}

	@Override
	public ContingutArxiu expedientModificar(Expedient expedient) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] expedientModificar no implementat");
	}

	@Override
	public void expedientEsborrar(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] expedientEsborrar no implementat");
	}

	@Override
	public Expedient expedientDetalls(String identificador, String versio) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] expedientDetalls no implementat");
	}

	@Override
	public ContingutArxiu expedientCrearSubExpedient(Expedient expedient, String expedientArrelUuid) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] expedientCrearSubExpedient no implementat");
	}

	@Override
	public List<ContingutArxiu> expedientVersions(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] expedientVersions no implementat");
	}

	@Override
	public String expedientTancar(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] expedientTancar no implementat");
	}

	@Override
	public String expedientReobrir(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] expedientReobrir no implementat");
	}

	@Override
	public String expedientExportarEni(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] expedientExportarEni no implementat");
	}

	@Override
	public String expedientLligar(String identificador, String expedientUuidPare) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] expedientLligar no implementat");
	}

	@Override
	public void expedientDeslligar(String identificador, String expedientUuidPare) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] expedientDeslligar no implementat");
	}

	@Override
	public ContingutArxiu documentModificar(Document document) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] documentModificar no implementat");
	}

	@Override
	public void documentEsborrar(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] documentEsborrar no implementat");
	}

	@Override
	public ConsultaResultat documentConsulta(List<ConsultaFiltre> filtres, Integer pagina, Integer mida, DocumentRepositori repositori) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] documentConsulta no implementat");
	}

	@Override
	public List<ContingutArxiu> documentVersions(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] documentVersions no implementat");
	}

	@Override
	public ContingutArxiu documentCopiar(String identificador, String expedientUuidDesti) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] documentCopiar no implementat");
	}

	@Override
	public ContingutArxiu documentMoure(String identificador, String expedientUuidDesti) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] documentMoure no implementat");
	}

	@Override
	public ContingutArxiu documentMoure(String identificador, String expedientUuidOrigen, String expedientUuidDesti) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] documentMoure no implementat");
	}

	@Override
	public String documentExportarEni(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] documentExportarEni no implementat");
	}

	@Override
	public ContingutArxiu carpetaCrear(Carpeta carpeta, String expedientUuid) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] carpetaCrear no implementat");
	}

	@Override
	public ContingutArxiu carpetaModificar(Carpeta carpeta) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] carpetaModificar no implementat");
	}

	@Override
	public void carpetaEsborrar(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] carpetaEsborrar no implementat");
	}

	@Override
	public Carpeta carpetaDetalls(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] carpetaDetalls no implementat");
	}

	@Override
	public ContingutArxiu carpetaCopiar(String identificador, String carpetaUuidDesti) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] carpetaCopiar no implementat");
	}

	@Override
	public void carpetaMoure(String identificador, String carpetaUuidDesti) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] carpetaMoure no implementat");
	}

	@Override
	public List<DocumentTipusAddicional> documentTipusAddicionals() {
		return new ArrayList<>();
	}

	@Override
	public boolean suportaVersionatExpedient() {
		return false;
	}

	@Override
	public boolean suportaVersionatDocument() {
		return false;
	}

	@Override
	public boolean suportaVersionatCarpeta() {
		return false;
	}

	@Override
	public boolean suportaMetadadesNti() {
		return false;
	}

	@Override
	public boolean generaIdentificadorNti() {
		return false;
	}

	@Override
	public String getCsv(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] getCsv no implementat");
	}

	@Override
	public String getCsvValidationWeb(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] getCsvValidationWeb no implementat");
	}

	@Override
	public String getCsvGenerationDefinition(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] getCsvGenerationDefinition no implementat");
	}

	@Override
	public String getOriginalFileUrl(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] getOriginalFileUrl no implementat");
	}

	@Override
	public String getPrintableFileUrl(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] getPrintableFileUrl no implementat");
	}

	@Override
	public String getEniFileUrl(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] getEniFileUrl no implementat");
	}

	@Override
	public String getValidationFileUrl(String identificador) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] getValidationFileUrl no implementat");
	}

	@Override
	public String getOriginalFileUrl(ContingutArxiu contingut) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] getOriginalFileUrl no implementat");
	}

	@Override
	public String getPrintableFileUrl(ContingutArxiu contingut) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] getPrintableFileUrl no implementat");
	}

	@Override
	public String getEniFileUrl(ContingutArxiu contingut) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] getEniFileUrl no implementat");
	}

	@Override
	public String getValidationFileUrl(ContingutArxiu contingut) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] getValidationFileUrl no implementat");
	}

	@Override
	public String getCsvValidationWeb(ContingutArxiu contingut) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] getCsvValidationWeb no implementat");
	}

	@Override
	public String getCsvGenerationDefinition(ContingutArxiu contingut) throws ArxiuException {
		throw new UnsupportedOperationException("[MOCK] getCsvGenerationDefinition no implementat");
	}

	private static final Logger logger = LoggerFactory.getLogger(ArxiuPluginMock.class);

}
