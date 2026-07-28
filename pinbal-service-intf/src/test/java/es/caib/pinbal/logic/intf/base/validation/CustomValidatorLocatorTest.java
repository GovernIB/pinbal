package es.caib.pinbal.logic.intf.base.validation;

import es.caib.pinbal.logic.intf.base.exception.ComponentNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomValidatorLocatorTest {

    static class FixtureValidator implements CustomValidator<String> {
        @Override
        public boolean validate(String value, ConstraintValidatorContext context) {
            return true;
        }
    }

    static class AltreValidator implements CustomValidator<String> {
        @Override
        public boolean validate(String value, ConstraintValidatorContext context) {
            return false;
        }
    }

    @Test
    void getCustomValidatorWithClassTrobaElValidadorPerClasse() {
        CustomValidatorLocator locator = new CustomValidatorLocator();
        FixtureValidator validator = new FixtureValidator();
        ReflectionTestUtils.setField(locator, "validators", List.of(validator, new AltreValidator()));

        CustomValidator<String> trobat = locator.getCustomValidatorWithClass(FixtureValidator.class);

        assertThat(trobat).isSameAs(validator);
    }

    @Test
    void getCustomValidatorWithClassSenseCoincidenciaLlancaComponentNotFoundException() {
        CustomValidatorLocator locator = new CustomValidatorLocator();
        ReflectionTestUtils.setField(locator, "validators", List.of(new AltreValidator()));

        assertThatThrownBy(() -> locator.getCustomValidatorWithClass(FixtureValidator.class))
                .isInstanceOf(ComponentNotFoundException.class);
    }

    @Test
    void getInstanceDelegaEnElContextDaplicacio() {
        CustomValidatorLocator locator = new CustomValidatorLocator();
        ApplicationContext context = Mockito.mock(ApplicationContext.class);
        Mockito.when(context.getBean(CustomValidatorLocator.class)).thenReturn(locator);

        locator.setApplicationContext(context);

        assertThat(CustomValidatorLocator.getInstance()).isSameAs(locator);
    }
}
