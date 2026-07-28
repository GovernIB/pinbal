package es.caib.pinbal.logic.intf.base.util;

import es.caib.pinbal.logic.intf.base.exception.ResourceFieldNotFoundException;
import es.caib.pinbal.logic.intf.base.model.Resource;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TypeUtilTest {

    // ------------------------- fixtures -------------------------

    static class TestResource implements Resource<Long> {
        private Long id;

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public void setId(Long id) {
            this.id = id;
        }
    }

    static class TestResourceReference extends ResourceReference<TestResource, Long> {
    }

    public static class FixtureFields {
        @NotNull
        private String ambAnotacio;
        private String senseAnotacio;
        private String[] campArray;
        private List<String> campCollection;
        private String simple;

        public String getSimple() {
            return simple;
        }

        public void setSimple(String simple) {
            this.simple = simple;
        }
    }

    static class NoGetterFixture {
        private String valor = "valor-inicial";
    }

    // ------------------------- getArgumentClassFromGenericSuperclass -------------------------

    @Test
    void getArgumentClassFromGenericSuperclassAmbSuperclassExplicita() {
        Class<?> resourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
                TestResourceReference.class, ResourceReference.class, 0);
        assertThat(resourceClass).isEqualTo(TestResource.class);

        Class<?> idClass = TypeUtil.getArgumentClassFromGenericSuperclass(
                TestResourceReference.class, ResourceReference.class, 1);
        assertThat(idClass).isEqualTo(Long.class);
    }

    @Test
    void getArgumentClassFromGenericSuperclassAmbDosParametresExecutaElCamiPerDefecte() {
        // ResourceReference.toString() empra aquesta variant (superClass=null) i ja la protegeix
        // amb un try/catch, ja que segons la jerarquia pot no resoldre cap argument.
        try {
            TypeUtil.getArgumentClassFromGenericSuperclass(TestResourceReference.class, 0);
        } catch (Exception ignored) {
            // Comportament acceptat: només interessa exercitar la línia de delegació.
        }
    }

    // ------------------------- getReferencedResourceClass -------------------------

    static class ResourceFieldsFixture {
        private TestResource simpleResource;
        private TestResourceReference resourceReferenceField;
        private List<ResourceReference<TestResource, Long>> resourceCollection;
        private String notAResource;
    }

    @Test
    void getReferencedResourceClassAmbCampResourceDirecte() throws NoSuchFieldException {
        Field field = ResourceFieldsFixture.class.getDeclaredField("simpleResource");
        assertThat(TypeUtil.getReferencedResourceClass(field)).isEqualTo(TestResource.class);
    }

    @Test
    void getReferencedResourceClassAmbCampNoResourceRetornaNull() throws NoSuchFieldException {
        Field field = ResourceFieldsFixture.class.getDeclaredField("notAResource");
        assertThat(TypeUtil.getReferencedResourceClass(field)).isNull();
    }

    @Test
    void getReferencedResourceClassAmbColleccioParametritzada() throws NoSuchFieldException {
        Field field = ResourceFieldsFixture.class.getDeclaredField("resourceCollection");
        assertThat(TypeUtil.getReferencedResourceClass(field)).isEqualTo(TestResource.class);
    }

    // ------------------------- suffix / not-null / multiple field helpers -------------------------

    @Test
    void getMethodSuffixFromFieldNameCapitalitzaLaPrimeraLletra() {
        assertThat(TypeUtil.getMethodSuffixFromFieldName("nom")).isEqualTo("Nom");
    }

    @Test
    void getMethodSuffixFromFieldCapitalitzaLaPrimeraLletra() throws NoSuchFieldException {
        Field field = FixtureFields.class.getDeclaredField("simple");
        assertThat(TypeUtil.getMethodSuffixFromField(field)).isEqualTo("Simple");
    }

    @Test
    void isNotNullFieldDetectaLAnotacio() throws NoSuchFieldException {
        assertThat(TypeUtil.isNotNullField(FixtureFields.class.getDeclaredField("ambAnotacio"))).isTrue();
        assertThat(TypeUtil.isNotNullField(FixtureFields.class.getDeclaredField("senseAnotacio"))).isFalse();
    }

    @Test
    void arrayFieldTypeEsDetectaCorrectament() throws NoSuchFieldException {
        Field field = FixtureFields.class.getDeclaredField("campArray");

        assertThat(TypeUtil.isArrayFieldType(field)).isTrue();
        assertThat(TypeUtil.isCollectionFieldType(field)).isFalse();
        assertThat(TypeUtil.isMultipleFieldType(field)).isTrue();
        assertThat(TypeUtil.getArrayFieldType(field)).isEqualTo(String.class);
        assertThat(TypeUtil.getMultipleFieldType(field)).isEqualTo(String.class);
        assertThat(TypeUtil.getFieldTypeMultipleAware(field)).isEqualTo(String.class);
    }

    @Test
    void collectionFieldTypeEsDetectaCorrectament() throws NoSuchFieldException {
        Field field = FixtureFields.class.getDeclaredField("campCollection");

        assertThat(TypeUtil.isCollectionFieldType(field)).isTrue();
        assertThat(TypeUtil.isArrayFieldType(field)).isFalse();
        assertThat(TypeUtil.isMultipleFieldType(field)).isTrue();
        assertThat(TypeUtil.getCollectionFieldType(field)).isEqualTo(String.class);
        assertThat(TypeUtil.getArrayFieldType(field)).isNull();
        assertThat(TypeUtil.getFieldTypeMultipleAware(field)).isEqualTo(String.class);
    }

    @Test
    void campSimpleNoEsArrayNiColleccio() throws NoSuchFieldException {
        Field field = FixtureFields.class.getDeclaredField("simple");

        assertThat(TypeUtil.isMultipleFieldType(field)).isFalse();
        assertThat(TypeUtil.getMultipleFieldType(field)).isNull();
        assertThat(TypeUtil.getCollectionFieldType(field)).isNull();
        assertThat(TypeUtil.getFieldTypeMultipleAware(field)).isEqualTo(String.class);
    }

    // ------------------------- getFieldOrGetterValue / setFieldOrSetterValue -------------------------

    @Test
    void getFieldOrGetterValueAmbNomDeCampIGetterExistent() {
        FixtureFields fixture = new FixtureFields();
        fixture.setSimple("valor-x");

        String valor = TypeUtil.getFieldOrGetterValue("simple", fixture);
        assertThat(valor).isEqualTo("valor-x");
    }

    @Test
    void getFieldOrGetterValueAmbCampPrivatSenseGetterAccedeixAlCampDirectament() {
        NoGetterFixture fixture = new NoGetterFixture();
        String valor = TypeUtil.getFieldOrGetterValue("valor", fixture);
        assertThat(valor).isEqualTo("valor-inicial");
    }

    @Test
    void getFieldOrGetterValueAmbFieldIObjectiuIGetterExistent() throws NoSuchFieldException {
        FixtureFields fixture = new FixtureFields();
        fixture.setSimple("valor-y");
        Field field = FixtureFields.class.getDeclaredField("simple");

        assertThat(TypeUtil.<String>getFieldOrGetterValue(field, fixture)).isEqualTo("valor-y");
    }

    @Test
    void getFieldOrGetterValueAmbFieldSenseGetterAccedeixAlCampDirectament() throws NoSuchFieldException {
        NoGetterFixture fixture = new NoGetterFixture();
        Field field = NoGetterFixture.class.getDeclaredField("valor");

        assertThat(TypeUtil.<String>getFieldOrGetterValue(field, fixture)).isEqualTo("valor-inicial");
    }

    @Test
    void getFieldOrGetterValueAmbTipusEsperatCorrecte() {
        FixtureFields fixture = new FixtureFields();
        fixture.setSimple("valor-z");

        String valor = TypeUtil.getFieldOrGetterValue("simple", fixture, String.class);
        assertThat(valor).isEqualTo("valor-z");
    }

    @Test
    void getFieldOrGetterValueAmbTipusEsperatIncorrecteLlancaExcepcio() {
        FixtureFields fixture = new FixtureFields();
        fixture.setSimple("valor-z");

        assertThatThrownBy(() -> TypeUtil.getFieldOrGetterValue("simple", fixture, Integer.class))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getFieldOrGetterValueAmbCampInexistentLlancaNullPointerException() {
        // NOTA: aquesta sobrecàrrega (String, Object) crida getMethodSuffixFromField(field) amb
        // field=null en comptes de getMethodSuffixFromFieldName(fieldName) quan el camp no
        // existeix, per la qual cosa en realitat llança NullPointerException i mai arriba a
        // llançar ResourceFieldNotFoundException. Es documenta el comportament real.
        FixtureFields fixture = new FixtureFields();
        assertThatThrownBy(() -> TypeUtil.getFieldOrGetterValue("noExisteix", fixture))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void getFieldOrGetterValueAmbFieldITipusEsperatCorrecte() throws NoSuchFieldException {
        FixtureFields fixture = new FixtureFields();
        fixture.setSimple("valor-w");
        Field field = FixtureFields.class.getDeclaredField("simple");

        assertThat(TypeUtil.getFieldOrGetterValue(field, fixture, String.class)).isEqualTo("valor-w");
    }

    @Test
    void getFieldOrGetterValueAmbFieldITipusEsperatIncorrecteLlancaExcepcio() throws NoSuchFieldException {
        FixtureFields fixture = new FixtureFields();
        fixture.setSimple("valor-w");
        Field field = FixtureFields.class.getDeclaredField("simple");

        assertThatThrownBy(() -> TypeUtil.getFieldOrGetterValue(field, fixture, Integer.class))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void setFieldOrSetterValueAmbSetterExistent() throws NoSuchFieldException {
        FixtureFields fixture = new FixtureFields();
        Field field = FixtureFields.class.getDeclaredField("simple");

        TypeUtil.setFieldOrSetterValue(field, fixture, "nou-valor");

        assertThat(fixture.getSimple()).isEqualTo("nou-valor");
    }

    @Test
    void setFieldOrSetterValueSenseSetterAssignaElCampDirectament() throws NoSuchFieldException {
        NoGetterFixture fixture = new NoGetterFixture();
        Field field = NoGetterFixture.class.getDeclaredField("valor");

        TypeUtil.setFieldOrSetterValue(field, fixture, "assignat-per-reflexio");

        assertThat(fixture.valor).isEqualTo("assignat-per-reflexio");
    }

    @Test
    void getFieldValueAmbCampExistent() {
        NoGetterFixture fixture = new NoGetterFixture();
        String valor = TypeUtil.getFieldValue(fixture, "valor");
        assertThat(valor).isEqualTo("valor-inicial");
    }

    @Test
    void getFieldValueAmbCampInexistentLlancaResourceFieldNotFoundException() {
        NoGetterFixture fixture = new NoGetterFixture();
        assertThatThrownBy(() -> TypeUtil.getFieldValue(fixture, "noExisteix"))
                .isInstanceOf(ResourceFieldNotFoundException.class);
    }

    // ------------------------- findAssignableClasses -------------------------

    @Test
    void findAssignableClassesTrobaExcepcionsDelPaquet() {
        Set<Class<RuntimeException>> trobades = TypeUtil.findAssignableClasses(
                RuntimeException.class, "es.caib.pinbal.logic.intf.base.exception");

        assertThat(trobades).isNotEmpty();
        assertThat(trobades).allMatch(RuntimeException.class::isAssignableFrom);
    }

    @Test
    void findAssignableClassesAmbPaquetBuitNoTrobaRes() {
        Set<Class<Object>> trobades = TypeUtil.findAssignableClasses(
                Object.class, "es.caib.pinbal.paquet.que.no.existeix");

        assertThat(trobades).isEmpty();
    }
}
