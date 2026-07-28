package es.caib.pinbal.back.validation;

import es.caib.pinbal.logic.intf.service.exception.SistemaExternException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RestPreconditionsTest {

    @Test
    public void checkNotNullAmbReferenciaNoNullaLaRetorna() {
        String referencia = "valor";

        assertSame(referencia, RestPreconditions.checkNotNull(referencia));
    }

    @Test
    public void checkNotNullAmbReferenciaNullLlancaExcepcio() {
        assertThrows(SistemaExternException.class, () -> RestPreconditions.checkNotNull(null));
    }

    @Test
    public void checkNotNullAmbMissatgeIReferenciaNullLlancaExcepcio() {
        assertThrows(SistemaExternException.class, () -> RestPreconditions.checkNotNull(null, "error"));
    }

    @Test
    public void constructorPrivatLlancaAssertionError() throws Exception {
        java.lang.reflect.Constructor<RestPreconditions> constructor = RestPreconditions.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Exception ex = assertThrows(java.lang.reflect.InvocationTargetException.class, constructor::newInstance);
        assertEquals(AssertionError.class, ex.getCause().getClass());
    }
}
