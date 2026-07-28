package es.caib.pinbal.plugin;

import es.caib.comanda.model.server.monitoring.EstatSalutEnum;
import es.caib.comanda.model.server.monitoring.IntegracioSalut;
import es.caib.comanda.ms.salut.helper.IntegracioApp;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cada test utilitza un {@link IntegracioApp} exclusiu i no emprat per cap altra classe del
 * mòdul, ja que l'estat de {@link PluginMetricHelper} és estàtic i compartit dins la mateixa JVM
 * de tests (incloent-hi altres classes de test que sí que empren USR/PFI/DIR/ARX/DCM).
 */
class PluginMetricHelperTest {

    @Test
    void constructorIsUsable() {
        assertThat(new PluginMetricHelper()).isNotNull();
    }

    @Test
    void getMetricaReturnsSameInstanceForSamePlugin() {
        Object first = PluginMetricHelper.getMetrica(IntegracioApp.ACH);
        Object second = PluginMetricHelper.getMetrica(IntegracioApp.ACH);
        assertThat(first).isSameAs(second);
    }

    @Test
    void addEndpointIsReflectedInSalutInfo() {
        PluginMetricHelper.addEndpoint(IntegracioApp.AFI, "https://endpoint-de-prova");
        IntegracioSalut salut = findSalut(IntegracioApp.AFI);
        assertThat(salut.getPeticions().getEndpoint()).isEqualTo("https://endpoint-de-prova");
    }

    @Test
    void withAtLeast20RequestsAndNoErrorsGivesUp() {
        for (int i = 0; i < 25; i++) {
            PluginMetricHelper.addSuccessOperation(IntegracioApp.CAR, 5L);
        }
        IntegracioSalut salut = findSalut(IntegracioApp.CAR);
        assertThat(salut.getEstat()).isEqualTo(EstatSalutEnum.UP);
        assertThat(salut.getPeticions().getPeticionsOkUltimPeriode()).isEqualTo(25L);
        assertThat(salut.getPeticions().getPeticionsErrorUltimPeriode()).isZero();
        assertThat(salut.getPeticions().getTotalOk()).isGreaterThanOrEqualTo(25L);
    }

    @Test
    void withAtLeast20RequestsAndMajorityErrorsGivesError() {
        for (int i = 0; i < 5; i++) {
            PluginMetricHelper.addSuccessOperation(IntegracioApp.COM, 3L);
        }
        for (int i = 0; i < 15; i++) {
            PluginMetricHelper.addErrorOperation(IntegracioApp.COM);
        }
        IntegracioSalut salut = findSalut(IntegracioApp.COM);
        // 15/20 = 75% d'errors -> ERROR
        assertThat(salut.getEstat()).isEqualTo(EstatSalutEnum.ERROR);
        assertThat(salut.getPeticions().getPeticionsErrorUltimPeriode()).isEqualTo(15L);
    }

    @Test
    void withFewerThan20RequestsUsesFifoWindow() {
        PluginMetricHelper.addSuccessOperation(IntegracioApp.CSV, 1L);
        PluginMetricHelper.addSuccessOperation(IntegracioApp.CSV, 1L);
        PluginMetricHelper.addErrorOperation(IntegracioApp.CSV);
        // 1 error d'un total de 3 (via FIFO) = 33% -> DEGRADED
        IntegracioSalut salut = findSalut(IntegracioApp.CSV);
        assertThat(salut.getEstat()).isEqualTo(EstatSalutEnum.DEGRADED);
    }

    @Test
    void withNoRequestsAtAllReturnsUnknown() {
        // Registram el plugin (branca "encara no existeix" de getMetrica) sense afegir cap petició.
        PluginMetricHelper.getMetrica(IntegracioApp.CDO);
        IntegracioSalut salut = findSalut(IntegracioApp.CDO);
        assertThat(salut.getEstat()).isEqualTo(EstatSalutEnum.UNKNOWN);
    }

    @Test
    void getIntegracionsSalutResetsLocalCountersBetweenCalls() {
        PluginMetricHelper.addSuccessOperation(IntegracioApp.CIE, 1L);
        IntegracioSalut abans = findSalut(IntegracioApp.CIE);
        assertThat(abans.getPeticions().getPeticionsOkUltimPeriode()).isGreaterThanOrEqualTo(1L);

        IntegracioSalut despres = findSalut(IntegracioApp.CIE);
        assertThat(despres.getPeticions().getPeticionsOkUltimPeriode()).isZero();
    }

    private IntegracioSalut findSalut(IntegracioApp plugin) {
        List<IntegracioSalut> salut = PluginMetricHelper.getIntegracionsSalut();
        Optional<IntegracioSalut> trobat = salut.stream()
                .filter(s -> s.getCodi().equals(plugin.name()))
                .findFirst();
        assertThat(trobat).as("no s'ha trobat informació de salut per %s", plugin).isPresent();
        return trobat.get();
    }
}
