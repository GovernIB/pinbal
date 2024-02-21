package es.caib.pinbal.core.justificant;

import freemarker.core.Environment;
import freemarker.core.NonStringException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModelException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateException;
import net.sf.jooreports.templates.DocumentTemplateFactory;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GenerarJustificantTest {


    private static final String PLANTILLA_ODT_RESOURCE = "/es/caib/pinbal/core/template/justificant.odt";

    @Ignore
    @Test
    public void generarJustificant() throws IOException, DocumentTemplateException {


        DocumentTemplateFactory dtf = getDocTemplateFactory();
        Locale locale = new Locale("ca", "ES");
        dtf.getFreemarkerConfiguration().setLocale(locale);
        DocumentTemplate template = dtf.getTemplate(getClass().getResourceAsStream(PLANTILLA_ODT_RESOURCE));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        template.createDocument(generarModel("[SCDCPAJU] Servei de consulta de padró de convivència"), out);
        try(OutputStream outputStream = Files.newOutputStream(Paths.get("/var/tmp/justificant-test.odt"))) {
            out.writeTo(outputStream);
        }
    }

    private DocumentTemplateFactory getDocTemplateFactory() {

        DocumentTemplateFactory documentTemplateFactory = new DocumentTemplateFactory();
        documentTemplateFactory.getFreemarkerConfiguration().setTemplateExceptionHandler(new TemplateExceptionHandler() {
            public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
                try {
                    if (te instanceof TemplateModelException || te instanceof NonStringException) {
                        out.write("[exception]");
                    } else {
                        out.write("[???]");
                    }
                    out.write("<office:annotation><dc:creator>Pinbal</dc:creator><dc:date>" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()) + "</dc:date><text:p><![CDATA[" + te.getFTLInstructionStack() + "\n" + te.getMessage() + "]]></text:p></office:annotation>");
                } catch (IOException e) {
                    throw new TemplateException("Failed to print error message. Cause: " + e, env);
                }
            }
        });
        return documentTemplateFactory;
    }

    private Map<String, Object> generarModel(String serveiDescripcio) {

        Map<String, Object> model = new HashMap<>();
        model.put("text_titol_capsalera", "Justificant de transmissió de dades");
        model.put("text_titol_servei", serveiDescripcio);
        model.put("text_titol_limitacio", "Sol·licitud");
        String textLegal = "El present justificant de transmissió de dades no originarà drets ni expectatives de drets a favor del sol·licitant o de tercers, " +
                "ni podrà ser invocat a l'efecte d'interrupció o paralització de terminis de caducitat o prescripció, ni servirà de mitjà de notificació dels expedients " +
                "al fet que pogués fer referència, no afectant al que pogués resultar d'actuacions posteriors de comprovació o recerca sobre aquest tema.";
        model.put("text_legal", textLegal);
        Date ara = new Date();
        model.put("text_data_eldia", "Generat el dia");
        model.put("text_data_data", formatDate(ara));
        List<NodeInfo> nodes = generarNodes();
        model.put("nodes", nodes);
        return model;
    }

    public List<NodeInfo> generarNodes() {

        List<NodeInfo> nodes = new ArrayList<>();
        String solicitud = "Sol·licitud";
        NodeInfo n = new NodeInfo(0, solicitud, null, "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(0, "Identificador de la petició", "PINPRE00000000000000003073", "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(0, "Identificador de la transmissió", "TEMISCSP000000000000000000456", "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(0, "Data de la sol·licitud", "2023-10-26T14:45:12.943+02:00", "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(0, "Emisor", "S0711001H - Govern de les Illes Balears", "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(0, "Sol·licitant", "S0711001H - Govern de les Illes Balears", "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(0, "Funcionari", "43088195H - Nom Llinatge1 Llinatge2", "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(0, "Procediment", "Test Justificant", "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(0, "Finalitat", "Test", "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(0, "Consentiment", "Si", "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(0, "Expedient", "Test", "node1.xpathDadaEspecifica");
        nodes.add(n);

        n = new NodeInfo(0, "Dades personals", null, "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(0, "Estat", null, "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(0, "Codi", "0003", "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(0, "Descripció", "TRAMITADA", "node1.xpathDadaEspecifica");
        nodes.add(n);

        n = new NodeInfo(0, "Solicitud", null, "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(0, "ProvinciaSolicitud", "07", "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(0, "MunicipioSolicitud", "033", "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(1, "Titular", null, "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(2, "Documentacion", null, "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(3, "Tipo", "NIF", "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(3, "Valor", "12345678Z", "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(3, "Test", "test", "node1.xpathDadaEspecifica");
        nodes.add(n);

        n = new NodeInfo(0, "Resultado", null, "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(1, "ClaveHojaPadronal", null, "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(2, "Distrito", "1", "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(2, "Seccion", "2", "node1.xpathDadaEspecifica");
        nodes.add(n);
        n = new NodeInfo(2, "Hoja", "185", "node1.xpathDadaEspecifica");
        nodes.add(n);

        return nodes;
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
    }

    @Builder
    @Getter
    @Setter
    public static class NodeInfo {

        private int nivell;
        private String xpathDadaEspecifica;
        private String titol;
        private String descripcio;
        public NodeInfo(int nivell, String titol, String descripcio, String xpathDadaEspecifica) {
            super();
            this.nivell = nivell;
            this.xpathDadaEspecifica = xpathDadaEspecifica;
            this.titol = titol;
            this.descripcio = descripcio;

        }
        public boolean isFinal() {
            return descripcio != null;
        }
    }
 }
