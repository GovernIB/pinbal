package es.caib.pinbal.logic.intf;

import es.caib.pinbal.logic.intf.testutil.ClasspathScanUtil;
import es.caib.pinbal.logic.intf.testutil.PojoExerciser;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercita genèricament totes les classes concretes del mòdul que no són excepcions (DTOs, models,
 * utilitats, helpers, ...): per cadascuna s'intenta instanciar (constructor buit + setters,
 * builder de Lombok, o el constructor amb més paràmetres) i després es criden tots els getters,
 * equals/hashCode/toString.
 * <p>
 * Donat que el mòdul té més de 150 classes d'aquest tipus (majoritàriament DTOs amb getters/setters
 * generats per Lombok), aquest exercitador cobreix aquell bytecode de manera genèrica; les classes
 * amb lògica pròpia rellevant (utilitats, helpers, validadors, Keycloak) tenen a més tests
 * específics en altres classes de test d'aquest mòdul.
 */
class DtoCoverageTest {

    @Test
    void exercitaTotesLesClassesNoExcepcio() {
        List<Class<?>> classes = ClasspathScanUtil.scan(c ->
                !c.isEnum()
                        && !c.isInterface()
                        && !Throwable.class.isAssignableFrom(c)
                        && !Modifier.isAbstract(c.getModifiers()));

        assertThat(classes).as("s'esperava trobar classes DTO/model sota el paquet base").hasSizeGreaterThanOrEqualTo(80);

        int exercitades = 0;
        List<Class<?>> noInstanciables = new java.util.ArrayList<>();
        for (Class<?> clazz : classes) {
            if (PojoExerciser.exercise(clazz)) {
                exercitades++;
            } else {
                noInstanciables.add(clazz);
            }
        }

        // No totes les classes són instanciables genèricament (constructors amb paràmetres
        // obligatoris de tipus complexos, etc.); es verifica que la gran majoria sí que ho són.
        double ratio = (double) exercitades / classes.size();
        assertThat(ratio)
                .as("classes no instanciables genèricament: %s", noInstanciables)
                .isGreaterThanOrEqualTo(0.6);
    }
}
