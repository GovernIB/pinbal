package es.caib.pinbal.api.interna.config;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.util.UrlPathHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WebMvcConfigTest {

    private final WebMvcConfig config = new WebMvcConfig();

    @Test
    public void testPageableResolver() {
        PageableHandlerMethodArgumentResolver resolver = config.pageableResolver();
        assertNotNull(resolver);
    }

    @Test
    public void testAddArgumentResolvers() {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
        config.addArgumentResolvers(resolvers);
        assertEquals(1, resolvers.size());
    }

    @Test
    public void testAddCorsMappings() {
        CorsRegistry registry = mock(CorsRegistry.class);
        config.addCorsMappings(registry);
        verify(registry).addMapping(eq("/**"));
    }

    @Test
    public void testConfigurePathMatch() {
        PathMatchConfigurer configurer = mock(PathMatchConfigurer.class);
        config.configurePathMatch(configurer);
        verify(configurer).setUrlPathHelper(org.mockito.ArgumentMatchers.any(UrlPathHelper.class));
    }

    @Test
    public void testUrlPathHelper() {
        UrlPathHelper urlPathHelper = config.urlPathHelper();
        assertNotNull(urlPathHelper);
    }

    @Test
    public void testJsonCustomizer() {
        assertNotNull(config.jsonCustomizer());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAddFormatters_ParsesLenientDateFormats() {
        FormatterRegistry registry = mock(FormatterRegistry.class);
        config.addFormatters(registry);

        ArgumentCaptor<Converter<String, Date>> captor = ArgumentCaptor.forClass(Converter.class);
        verify(registry).addConverter(eq(String.class), eq(Date.class), captor.capture());
        Converter<String, Date> converter = captor.getValue();

        assertNotNull(converter.convert("2024-01-15T10:30:00+02:00"));
        assertNotNull(converter.convert("2024-01-15T10:30:00+0200"));
        assertNotNull(converter.convert("2024-01-15T10:30:00%2B0200"));
        assertNotNull(converter.convert("2024-01-15T10:30:00%2b0200"));
        assertNotNull(converter.convert("2024-01-15T10:30:00 0200"));
    }
}
