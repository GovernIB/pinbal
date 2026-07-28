package es.caib.pinbal.logic.intf;

import es.caib.pinbal.logic.intf.testutil.ClasspathScanUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.DynamicTest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Exercita tots els enums del mòdul: la simple referència a {@code values()}/{@code valueOf()}
 * força la càrrega de classe i l'execució de l'inicialitzador estàtic (que crida el constructor
 * de cada constant), cobrint la major part del bytecode generat per un enum.
 */
class EnumCoverageTest {

    @Test
    void hiHaEnumsPerExercitar() {
        List<Class<?>> enums = ClasspathScanUtil.scan(Class::isEnum);
        assertThat(enums).isNotEmpty();
    }

    @TestFactory
    Stream<DynamicTest> exercitaCadaEnum() {
        List<Class<?>> enums = ClasspathScanUtil.scan(Class::isEnum);
        return enums.stream().map(enumClass -> dynamicTest(enumClass.getName(), () -> {
            Object[] constants = enumClass.getEnumConstants();
            assertThat(constants).as("l'enum %s hauria de tenir constants", enumClass).isNotEmpty();
            for (Object constant : constants) {
                Enum<?> e = (Enum<?>) constant;
                assertThat(e.name()).isNotNull();
                assertThat(e.toString()).isNotNull();
                Object roundTrip = Enum.valueOf((Class<Enum>) enumClass, e.name());
                assertThat(roundTrip).isSameAs(constant);
            }
        }));
    }
}
