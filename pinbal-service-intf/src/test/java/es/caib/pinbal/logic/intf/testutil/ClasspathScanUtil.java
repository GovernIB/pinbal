package es.caib.pinbal.logic.intf.testutil;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utilitat de test per escanejar totes les classes compilades d'un paquet base, emprada pels
 * tests genèrics de cobertura (excepcions, enums, DTOs) donat el gran nombre de classes senzilles
 * del mòdul.
 */
public final class ClasspathScanUtil {

    private static final String BASE_PACKAGE = "es.caib.pinbal.logic.intf";

    private ClasspathScanUtil() {
    }

    public static List<Class<?>> scanAll() {
        return scan(BASE_PACKAGE, c -> true);
    }

    public static List<Class<?>> scan(Predicate<Class<?>> filter) {
        return scan(BASE_PACKAGE, filter);
    }

    public static List<Class<?>> scan(String basePackage, Predicate<Class<?>> filter) {
        List<Class<?>> result = new ArrayList<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(resolver);
        String path = "classpath*:" + basePackage.replace('.', '/') + "/**/*.class";
        try {
            Resource[] resources = resolver.getResources(path);
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null || filename.contains("$") || filename.equals("package-info.class")
                        || filename.equals("module-info.class")) {
                    continue;
                }
                try {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    String className = metadataReader.getClassMetadata().getClassName();
                    // Exclou les classes de test (paquet testutil / tests que puguin quedar en el mateix classpath).
                    if (className.contains(".testutil.") || className.endsWith("Test")) {
                        continue;
                    }
                    Class<?> clazz = Class.forName(className, false, ClasspathScanUtil.class.getClassLoader());
                    if (filter.test(clazz)) {
                        result.add(clazz);
                    }
                } catch (Throwable ignored) {
                    // Classe no carregable (dependència no present, etc.): s'ignora per al recompte de cobertura.
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error escanejant el classpath sota " + basePackage, e);
        }
        return result;
    }
}
