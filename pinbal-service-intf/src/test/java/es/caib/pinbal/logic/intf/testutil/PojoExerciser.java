package es.caib.pinbal.logic.intf.testutil;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Exercitador genèric de POJOs/DTOs via reflexió: intenta instanciar una classe (constructor buit
 * + setters, builder de Lombok, o el constructor públic amb més paràmetres), i després crida tots
 * els getters, {@code equals}, {@code hashCode} i {@code toString}.
 * <p>
 * Donat el gran nombre de DTOs senzills (getters/setters de Lombok) d'aquest mòdul, escriure un
 * test manual per cadascun no aporta valor addicional; aquest exercitador cobreix el mateix codi
 * (bytecode generat per Lombok) de manera genèrica.
 */
public final class PojoExerciser {

    private PojoExerciser() {
    }

    /**
     * Intenta instanciar i exercitar la classe indicada. Retorna cert si s'ha pogut obtenir
     * una instància (encara que alguna crida individual de getter/setter hagi fallat).
     */
    public static boolean exercise(Class<?> clazz) {
        if (clazz.isInterface() || clazz.isEnum() || Modifier.isAbstract(clazz.getModifiers())
                || clazz.isAnnotation() || clazz.isAnonymousClass() || clazz.isLocalClass()) {
            return false;
        }
        Object instance = tryInstantiate(clazz);
        if (instance == null) {
            return false;
        }
        exerciseGettersAndObjectMethods(instance, clazz);
        return true;
    }

    private static Object tryInstantiate(Class<?> clazz) {
        Object viaBuilder = tryBuilder(clazz);
        if (viaBuilder != null) {
            return viaBuilder;
        }
        Object viaNoArgAndSetters = tryNoArgConstructorAndSetters(clazz);
        if (viaNoArgAndSetters != null) {
            return viaNoArgAndSetters;
        }
        return tryLargestConstructor(clazz);
    }

    private static Object tryNoArgConstructorAndSetters(Class<?> clazz) {
        try {
            Constructor<?> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            Object instance = ctor.newInstance();
            for (Method method : clazz.getMethods()) {
                if (isSetter(method)) {
                    try {
                        method.invoke(instance, dummyValue(method.getParameterTypes()[0], 0));
                    } catch (Throwable ignored) {
                        // Un setter individual pot fallar (p.ex. validació): es continua amb la resta.
                    }
                }
            }
            return instance;
        } catch (Throwable e) {
            return null;
        }
    }

    private static Object tryLargestConstructor(Class<?> clazz) {
        Constructor<?> best = null;
        for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
            if (best == null || ctor.getParameterCount() > best.getParameterCount()) {
                best = ctor;
            }
        }
        if (best == null) {
            return null;
        }
        try {
            best.setAccessible(true);
            Class<?>[] paramTypes = best.getParameterTypes();
            Object[] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                args[i] = dummyValue(paramTypes[i], i);
            }
            return best.newInstance(args);
        } catch (Throwable e) {
            return null;
        }
    }

    private static Object tryBuilder(Class<?> clazz) {
        try {
            Method builderMethod = clazz.getMethod("builder");
            if (!Modifier.isStatic(builderMethod.getModifiers())) {
                return null;
            }
            Object builder = builderMethod.invoke(null);
            Class<?> builderClass = builder.getClass();
            for (Method method : builderClass.getMethods()) {
                if (method.getParameterCount() == 1 && method.getDeclaringClass() != Object.class
                        && builderClass.isAssignableFrom(method.getReturnType())) {
                    try {
                        method.invoke(builder, dummyValue(method.getParameterTypes()[0], 0));
                    } catch (Throwable ignored) {
                        // Es continua amb la resta de camps del builder.
                    }
                }
            }
            Method build = builderClass.getMethod("build");
            return build.invoke(builder);
        } catch (Throwable e) {
            return null;
        }
    }

    private static void exerciseGettersAndObjectMethods(Object instance, Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            if (isGetter(method)) {
                try {
                    method.invoke(instance);
                } catch (Throwable ignored) {
                    // Es continua amb la resta de getters.
                }
            }
        }
        try {
            instance.equals(instance);
            instance.equals(null);
            instance.equals(new Object());
            instance.hashCode();
            instance.toString();
        } catch (Throwable ignored) {
            // Alguns equals/hashCode/toString generats poden fallar amb valors buits: no és rellevant per la cobertura.
        }
    }

    private static boolean isSetter(Method method) {
        return method.getName().startsWith("set")
                && method.getName().length() > 3
                && method.getParameterCount() == 1
                && !Modifier.isStatic(method.getModifiers());
    }

    private static boolean isGetter(Method method) {
        String name = method.getName();
        boolean matchesName = (name.startsWith("get") && name.length() > 3) || (name.startsWith("is") && name.length() > 2);
        return matchesName
                && method.getParameterCount() == 0
                && !Modifier.isStatic(method.getModifiers())
                && method.getDeclaringClass() != Object.class;
    }

    @SuppressWarnings("unchecked")
    private static Object dummyValue(Class<?> type, int seed) {
        if (type == String.class) {
            return "valor-de-prova-" + seed;
        }
        if (type == int.class || type == Integer.class) {
            return seed + 1;
        }
        if (type == long.class || type == Long.class) {
            return (long) (seed + 1);
        }
        if (type == short.class || type == Short.class) {
            return (short) (seed + 1);
        }
        if (type == byte.class || type == Byte.class) {
            return (byte) (seed + 1);
        }
        if (type == double.class || type == Double.class) {
            return (double) (seed + 1);
        }
        if (type == float.class || type == Float.class) {
            return (float) (seed + 1);
        }
        if (type == boolean.class || type == Boolean.class) {
            return seed % 2 == 0;
        }
        if (type == char.class || type == Character.class) {
            return (char) ('a' + (seed % 26));
        }
        if (type == BigDecimal.class) {
            return BigDecimal.valueOf(seed + 1);
        }
        if (type == BigInteger.class) {
            return BigInteger.valueOf(seed + 1);
        }
        if (type == Date.class) {
            return new Date(0);
        }
        if (type == LocalDate.class) {
            return LocalDate.of(2024, 1, 1);
        }
        if (type == LocalDateTime.class) {
            return LocalDateTime.of(2024, 1, 1, 0, 0);
        }
        if (type == OffsetDateTime.class) {
            return OffsetDateTime.now();
        }
        if (type == Class.class) {
            return Object.class;
        }
        if (type == Comparator.class) {
            return Comparator.comparing(Object::toString);
        }
        if (type.isEnum()) {
            Object[] constants = type.getEnumConstants();
            return constants.length > 0 ? constants[0] : null;
        }
        if (type.isArray()) {
            return Array.newInstance(type.getComponentType(), 0);
        }
        if (type == List.class || type == ArrayList.class || type == Iterable.class || type == java.util.Collection.class) {
            return new ArrayList<>();
        }
        if (type == java.util.Set.class || type == HashSet.class) {
            return new HashSet<>();
        }
        if (type == java.util.Map.class || type == HashMap.class) {
            return new HashMap<>();
        }
        if (type == Object.class) {
            return new Object();
        }
        // Tipus complex desconegut (un altre DTO, per exemple): es deixa a null per evitar
        // recursivitat infinita amb estructures que es referencien entre elles.
        return null;
    }
}
