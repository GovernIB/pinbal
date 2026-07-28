package es.caib.pinbal.back.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.WebDataBinder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ConfigClassesTest {

    @Test
    public void binderControllerAdviceDenegaCampsDeClasse() {
        BinderControllerAdvice advice = new BinderControllerAdvice();
        WebDataBinder binder = mock(WebDataBinder.class);

        advice.setAllowedFields(binder);

        verify(binder).setDisallowedFields(new String[]{"class.", "Class.", ".class", ".Class"});
    }

    @Test
    public void hateoasMessageResolverConfigExposaBasenameILocale() throws Exception {
        HateoasMessageResolverConfig config = new HateoasMessageResolverConfig();

        var basenameMethod = HateoasMessageResolverConfig.class.getDeclaredMethod("getBasename");
        basenameMethod.setAccessible(true);
        var localeMethod = HateoasMessageResolverConfig.class.getDeclaredMethod("getDefaultLocale");
        localeMethod.setAccessible(true);

        assertEquals("pinbal-back-rest-messages", basenameMethod.invoke(config));
        assertEquals(Locale.forLanguageTag("ca"), localeMethod.invoke(config));
    }

    @Test
    public void messageSourceConfigExposaBasenamesILocale() throws Exception {
        MessageSourceConfig config = new MessageSourceConfig();

        var basenamesMethod = MessageSourceConfig.class.getDeclaredMethod("getBasenames");
        basenamesMethod.setAccessible(true);
        String[] basenames = (String[]) basenamesMethod.invoke(config);

        var localeMethod = MessageSourceConfig.class.getDeclaredMethod("getDefaultLocale");
        localeMethod.setAccessible(true);
        Locale locale = (Locale) localeMethod.invoke(config);

        assertEquals(2, basenames.length);
        assertEquals("messages", basenames[0]);
        assertEquals(Locale.forLanguageTag("ca"), locale);
    }
}
