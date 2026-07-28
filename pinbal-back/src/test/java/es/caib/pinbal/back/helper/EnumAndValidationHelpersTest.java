package es.caib.pinbal.back.helper;

import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnumAndValidationHelpersTest {

    private enum ExempleEnum {
        UN, DOS, TRES
    }

    // ------------------------- HtmlSelectOptionHelper -------------------------

    @Test
    public void getOptionsForEnumRetornaUnaOpcioPerValor() {
        List<HtmlSelectOptionHelper.HtmlOption> opcions = HtmlSelectOptionHelper.getOptionsForEnum(ExempleEnum.class);

        assertEquals(3, opcions.size());
        assertEquals("UN", opcions.get(0).getValue());
        assertEquals("UN", opcions.get(0).getText());
    }

    @Test
    public void getOptionsForEnumAmbPrefixAfegeixElPrefixAlText() {
        List<HtmlSelectOptionHelper.HtmlOption> opcions = HtmlSelectOptionHelper.getOptionsForEnum(ExempleEnum.class, "enum.");

        assertEquals("enum.UN", opcions.get(0).getText());
    }

    @Test
    public void getOptionsForEnumAmbClasseNoEnumRetornaBuit() {
        assertTrue(HtmlSelectOptionHelper.getOptionsForEnum(String.class).isEmpty());
    }

    @Test
    public void getOptionsForArrayCombinaValorsITextos() {
        List<HtmlSelectOptionHelper.HtmlOption> opcions = HtmlSelectOptionHelper.getOptionsForArray(
                new String[]{"v1", "v2"}, new String[]{"t1", "t2"});

        assertEquals(2, opcions.size());
        assertEquals("v1", opcions.get(0).getValue());
        assertEquals("t2", opcions.get(1).getText());
    }

    @Test
    public void htmlOptionSettersFuncionen() {
        HtmlSelectOptionHelper.HtmlOption opcio = new HtmlSelectOptionHelper.HtmlOption("v", "t");

        opcio.setValue("v2");
        opcio.setText("t2");

        assertEquals("v2", opcio.getValue());
        assertEquals("t2", opcio.getText());
    }

    // ------------------------- EnumHelper -------------------------

    @Test
    public void enumHelperGetOptionsForEnumSenseIgnores() {
        List<EnumHelper.HtmlOption> opcions = EnumHelper.getOptionsForEnum(ExempleEnum.class);

        assertEquals(3, opcions.size());
    }

    @Test
    public void enumHelperGetOptionsForEnumAmbIgnoresElsExclou() {
        List<EnumHelper.HtmlOption> opcions = EnumHelper.getOptionsForEnum(
                ExempleEnum.class, null, new Enum<?>[]{ExempleEnum.DOS});

        assertEquals(2, opcions.size());
        assertTrue(opcions.stream().noneMatch(o -> o.getValue().equals("DOS")));
    }

    @Test
    public void enumHelperGetOptionsForEnumAmbClasseNoEnumRetornaBuit() {
        assertTrue(EnumHelper.getOptionsForEnum(String.class).isEmpty());
    }

    @Test
    public void enumHelperGetOneOptionForEnumTrobaElQueConteElNom() {
        EnumHelper.HtmlOption opcio = EnumHelper.getOneOptionForEnum(ExempleEnum.class, "prefix.DOS");

        assertEquals("DOS", opcio.getValue());
    }

    @Test
    public void enumHelperGetOneOptionForEnumSenseCoincidenciaRetornaNull() {
        assertEquals(null, EnumHelper.getOneOptionForEnum(ExempleEnum.class, "cap-coincidencia"));
    }

    @Test
    public void enumHelperGetOptionsForArray() {
        List<EnumHelper.HtmlOption> opcions = EnumHelper.getOptionsForArray(new String[]{"a"}, new String[]{"b"});

        assertEquals(1, opcions.size());
        assertEquals("a", opcions.get(0).getValue());
    }

    // ------------------------- ValidationHelper -------------------------

    @Test
    public void isValidSenseViolacionsRetornaTrue() {
        Validator validator = mock(Validator.class);
        when(validator.validate(any(), any())).thenReturn(Set.of());
        Errors errors = mock(Errors.class);

        ValidationHelper helper = new ValidationHelper(validator);

        assertTrue(helper.isValid(new Object(), errors));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void isValidAmbViolacioAfegeixError() {
        Validator validator = mock(Validator.class);
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        javax.validation.Path path = mock(javax.validation.Path.class);
        when(path.toString()).thenReturn("camp");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("error de validació");
        ConstraintDescriptor descriptor = mock(ConstraintDescriptor.class);
        Annotation annotation = mock(javax.validation.constraints.NotNull.class);
        when(annotation.annotationType()).thenReturn((Class) javax.validation.constraints.NotNull.class);
        when(descriptor.getAnnotation()).thenReturn(annotation);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("message", "msg");
        when(descriptor.getAttributes()).thenReturn(attributes);
        when(violation.getConstraintDescriptor()).thenReturn(descriptor);
        when(validator.validate(any(), any())).thenReturn(Set.of(violation));

        Errors errors = mock(Errors.class);
        when(errors.getFieldError("camp")).thenReturn(null);
        when(errors.getObjectName()).thenReturn("obj");
        when(errors.hasErrors()).thenReturn(true);

        ValidationHelper helper = new ValidationHelper(validator);

        assertFalse(helper.isValid(new Object(), errors));
        org.mockito.Mockito.verify(errors).rejectValue(
                anyString(), anyString(), any(Object[].class), anyString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void isValidAmbErrorDeBindingJaExistentNoAfegeixNouError() {
        Validator validator = mock(Validator.class);
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        javax.validation.Path path = mock(javax.validation.Path.class);
        when(path.toString()).thenReturn("camp");
        when(violation.getPropertyPath()).thenReturn(path);
        when(validator.validate(any(), any())).thenReturn(Set.of(violation));

        Errors errors = mock(Errors.class);
        FieldError bindingError = new FieldError("obj", "camp", null, true, null, null, "no és un número");
        when(errors.getFieldError("camp")).thenReturn(bindingError);
        when(errors.hasErrors()).thenReturn(true);

        ValidationHelper helper = new ValidationHelper(validator);

        assertFalse(helper.isValid(new Object(), errors));
        org.mockito.Mockito.verify(errors, org.mockito.Mockito.never()).rejectValue(
                anyString(), anyString(), any(Object[].class), anyString());
    }
}
