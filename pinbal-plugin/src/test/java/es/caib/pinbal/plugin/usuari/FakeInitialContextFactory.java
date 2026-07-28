package es.caib.pinbal.plugin.usuari;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.spi.InitialContextFactory;
import java.lang.reflect.Proxy;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factoria JNDI de proves: permet fer {@code new InitialContext().lookup(nom)} en tests sense
 * necessitar un servidor d'aplicacions. Els noms es registren a {@link #BINDINGS} abans d'invocar
 * el codi sota test i s'esborren al final amb {@link #clear()}.
 */
public class FakeInitialContextFactory implements InitialContextFactory {

    static final Map<String, Object> BINDINGS = new ConcurrentHashMap<>();

    static void bind(String name, Object value) {
        BINDINGS.put(name, value);
    }

    static void clear() {
        BINDINGS.clear();
    }

    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) {
        return (Context) Proxy.newProxyInstance(
                Context.class.getClassLoader(),
                new Class<?>[]{Context.class},
                (proxy, method, args) -> {
                    if ("lookup".equals(method.getName()) && args != null && args.length == 1 && args[0] instanceof String) {
                        Object bound = BINDINGS.get(args[0]);
                        if (bound == null) {
                            throw new NameNotFoundException("No hi ha cap binding per a " + args[0]);
                        }
                        return bound;
                    }
                    if ("close".equals(method.getName())) {
                        return null;
                    }
                    throw new UnsupportedOperationException("Mètode no suportat pel Context de proves: " + method.getName());
                });
    }
}
