package es.caib.pinbal.client.recobriment;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Servidor HTTP mínim que simula l'API REST de PINBAL per als tests unitaris
 * dels clients. Retorna la resposta configurada per a cada mètode i path, i
 * guarda la darrera petició rebuda per poder-la comprovar.
 * <p>
 * Només empra l'API del JDK (disponible des de Java 6) per a poder executar
 * els tests del client legacy amb Java 7.
 */
public class ServidorPinbalSimulat {

    public static final String CONTEXT = "/pinbalapi";
    public static final String BASE_RECOBRIMENT = CONTEXT + "/interna/recobriment/v2/";

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final HttpServer server;
    private final Map<String, Resposta> respostes = new HashMap<String, Resposta>();

    private String darrerMetode;
    private String darrerPath;
    private String darreraQuery;
    private String darreraAutoritzacio;
    private String darrerContentType;
    private String darrerCos;

    public ServidorPinbalSimulat() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                gestionar(exchange);
            }
        });
        server.start();
    }

    public String getUrlBase() {
        return "http://127.0.0.1:" + server.getAddress().getPort() + CONTEXT;
    }

    public void atura() {
        server.stop(0);
    }

    /**
     * Configura la resposta per a una petició al recobriment v2.
     *
     * @param metode mètode HTTP (GET, POST)
     * @param path path relatiu a /interna/recobriment/v2/
     */
    public void respon(String metode, String path, int status, String contentType, String cos) {
        respostes.put(metode + " " + BASE_RECOBRIMENT + path, new Resposta(status, contentType, cos));
    }

    public void responJson(String metode, String path, String json) {
        respon(metode, path, 200, "application/json", json);
    }

    private synchronized void gestionar(HttpExchange exchange) throws IOException {
        darrerMetode = exchange.getRequestMethod();
        darrerPath = exchange.getRequestURI().getRawPath();
        darreraQuery = exchange.getRequestURI().getRawQuery();
        darreraAutoritzacio = exchange.getRequestHeaders().getFirst("Authorization");
        darrerContentType = exchange.getRequestHeaders().getFirst("Content-Type");
        darrerCos = llegir(exchange.getRequestBody());

        Resposta resposta = respostes.get(darrerMetode + " " + darrerPath);
        if (resposta == null) {
            resposta = new Resposta(404, "application/json", "{\"errorMessage\":\"No configurat: " + darrerPath + "\"}");
        }
        byte[] cos = resposta.cos != null ? resposta.cos.getBytes(UTF8) : new byte[0];
        if (resposta.contentType != null) {
            exchange.getResponseHeaders().set("Content-Type", resposta.contentType);
        }
        if (cos.length == 0) {
            exchange.sendResponseHeaders(resposta.status, -1);
        } else {
            exchange.sendResponseHeaders(resposta.status, cos.length);
            OutputStream os = exchange.getResponseBody();
            os.write(cos);
            os.close();
        }
        exchange.close();
    }

    private static String llegir(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while ((n = is.read(buffer)) != -1) {
            baos.write(buffer, 0, n);
        }
        return new String(baos.toByteArray(), UTF8);
    }

    public synchronized String getDarrerMetode() {
        return darrerMetode;
    }
    public synchronized String getDarrerPath() {
        return darrerPath;
    }
    public synchronized String getDarreraQuery() {
        return darreraQuery;
    }
    public synchronized String getDarreraAutoritzacio() {
        return darreraAutoritzacio;
    }
    public synchronized String getDarrerContentType() {
        return darrerContentType;
    }
    public synchronized String getDarrerCos() {
        return darrerCos;
    }

    private static class Resposta {
        final int status;
        final String contentType;
        final String cos;

        Resposta(int status, String contentType, String cos) {
            this.status = status;
            this.contentType = contentType;
            this.cos = cos;
        }
    }

}
