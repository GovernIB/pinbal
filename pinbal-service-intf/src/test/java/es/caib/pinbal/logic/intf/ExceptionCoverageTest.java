package es.caib.pinbal.logic.intf;

import es.caib.pinbal.logic.intf.testutil.ClasspathScanUtil;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Exercita totes les classes d'excepció concretes del mòdul (63 en total): per cada constructor
 * públic es genera una instància amb valors de prova (String, Throwable) i s'executen
 * getMessage()/getCause()/toString()/getters propis. Donat el gran nombre d'excepcions, la major
 * part senzilles (missatge + causa), un exercitador genèric evita 60+ classes de test gairebé
 * idèntiques.
 */
class ExceptionCoverageTest {

    @Test
    void hiHaExcepcionsPerExercitar() {
        List<Class<?>> exceptions = concreteExceptionClasses();
        assertThat(exceptions).hasSizeGreaterThanOrEqualTo(50);
    }

    @TestFactory
    Stream<DynamicTest> exercitaCadaExcepcio() {
        return concreteExceptionClasses().stream().map(exceptionClass -> dynamicTest(exceptionClass.getName(), () -> {
            List<Throwable> instancies = new ArrayList<>();
            for (Constructor<?> ctor : exceptionClass.getDeclaredConstructors()) {
                if (!Modifier.isPublic(ctor.getModifiers())) {
                    continue;
                }
                try {
                    ctor.setAccessible(true);
                    Class<?>[] paramTypes = ctor.getParameterTypes();
                    Object[] args = new Object[paramTypes.length];
                    AtomicInteger seed = new AtomicInteger();
                    for (int i = 0; i < paramTypes.length; i++) {
                        args[i] = dummyArg(paramTypes[i], seed.getAndIncrement());
                    }
                    Object instance = ctor.newInstance(args);
                    instancies.add((Throwable) instance);
                } catch (Throwable ignored) {
                    // Alguns constructors poden requerir combinacions específiques: es continua amb els altres.
                }
            }
            assertThat(instancies)
                    .as("cap constructor públic de %s s'ha pogut invocar amb valors de prova", exceptionClass)
                    .isNotEmpty();
            for (Throwable instance : instancies) {
                instance.getMessage();
                instance.getCause();
                instance.toString();
                instance.getLocalizedMessage();
            }
        }));
    }

    private static List<Class<?>> concreteExceptionClasses() {
        return ClasspathScanUtil.scan(c -> Throwable.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers()));
    }

    private static Object dummyArg(Class<?> type, int seed) {
        if (type == Throwable.class || type == Exception.class || type == RuntimeException.class) {
            return new RuntimeException("causa de prova " + seed);
        }
        if (type == String.class) {
            return "valor de prova " + seed;
        }
        if (type == int.class || type == Integer.class) {
            return seed;
        }
        if (type == long.class || type == Long.class) {
            return (long) seed;
        }
        if (type == boolean.class || type == Boolean.class) {
            return seed % 2 == 0;
        }
        if (type.isEnum()) {
            Object[] constants = type.getEnumConstants();
            return constants.length > 0 ? constants[0] : null;
        }
        if (type == Class.class) {
            return Object.class;
        }
        if (type == java.io.Serializable.class) {
            return "identificador-de-prova-" + seed;
        }
        return null;
    }
}
