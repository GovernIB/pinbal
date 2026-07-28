package es.caib.pinbal.api.interna;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class PinbalApiInternaAppTest {

    @Test
    public void testConfigure() {
        PinbalApiInternaApp app = new PinbalApiInternaApp();
        SpringApplicationBuilder builder = new SpringApplicationBuilder();

        SpringApplicationBuilder result = app.configure(builder);

        assertNotNull(result);
        assertSame(builder, result);
    }
}
