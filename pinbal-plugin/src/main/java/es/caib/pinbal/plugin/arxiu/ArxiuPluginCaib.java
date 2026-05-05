package es.caib.pinbal.plugin.arxiu;

import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.pinbal.plugin.PluginMetricHelper;
import es.caib.plugins.arxiu.api.*;

import java.util.List;
import java.util.Properties;

public class ArxiuPluginCaib extends es.caib.plugins.arxiu.caib.ArxiuPluginCaib implements ArxiuPlugin {

    @FunctionalInterface
    private interface ArxiuOp<T> {
        T call() throws ArxiuException;
    }

    public ArxiuPluginCaib() {
        throw new RuntimeException("No es possible instanciar la clase ArxiuCaibPluginImpl sense paràmetres");
    }

    public ArxiuPluginCaib(String propertyKeyBase) {
        throw new RuntimeException("No es possible instanciar la clase ArxiuCaibPluginImpl sense propietats");
    }

    public ArxiuPluginCaib(String propertyKeyBase, Properties properties) {
        super(propertyKeyBase, properties);
        PluginMetricHelper.addEndpoint(IntegracioApp.ARX, this.getProperty("plugin.arxiu.caib.base.url"));
    }

    private <T> T tracked(ArxiuOp<T> op) throws ArxiuException {
        try {
            long start = System.currentTimeMillis();
            T result = op.call();
            PluginMetricHelper.addSuccessOperation(IntegracioApp.ARX, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable ex) {
            PluginMetricHelper.addErrorOperation(IntegracioApp.ARX);
            throw ex;
        }
    }

    @Override
    public ContingutArxiu expedientCrear(Expedient expedient) throws ArxiuException {
        return tracked(() -> super.expedientCrear(expedient));
    }

    @Override
    public Expedient expedientDetalls(String identificador, String versio) throws ArxiuException {
        return tracked(() -> super.expedientDetalls(identificador, versio));
    }

    @Override
    public ConsultaResultat expedientConsulta(List<ConsultaFiltre> filtres, Integer pagina, Integer itemsPerPagina) throws ArxiuException {
        return tracked(() -> super.expedientConsulta(filtres, pagina, itemsPerPagina));
    }

    @Override
    public String expedientTancar(String identificador) throws ArxiuException {
        return tracked(() -> super.expedientTancar(identificador));
    }

    @Override
    public void expedientEsborrar(String identificador) throws ArxiuException {
        tracked(() -> { super.expedientEsborrar(identificador); return null; });
    }

    @Override
    public ContingutArxiu documentCrear(Document document, String identificadorPare) throws ArxiuException {
        return tracked(() -> super.documentCrear(document, identificadorPare));
    }

    @Override
    public Document documentDetalls(String identificador, String versio, boolean ambContingut) throws ArxiuException {
        return tracked(() -> super.documentDetalls(identificador, versio, ambContingut));
    }

    @Override
    public DocumentContingut documentImprimible(String identificador) throws ArxiuException {
        return tracked(() -> super.documentImprimible(identificador));
    }

}
