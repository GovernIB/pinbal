/**
 * 
 */
package es.caib.pinbal.core.helper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateFactory;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

import es.caib.pinbal.core.model.ServeiJustificantCamp;
import es.caib.pinbal.scsp.JustificantArbreHelper.ElementArbre;
import freemarker.core.Environment;
import freemarker.core.NonStringException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModelException;

/**
 * Helper per a generar el justificant.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class JustificantPlantillaHelper implements MessageSourceAware {

	private static final String PLANTILLA_ODT_RESOURCE = "/es/caib/pinbal/core/template/justificant.odt";

	private MessageSource messageSource;



	public void generar(
			ElementArbre arbre,
			String serveiDescripcio,
			List<ServeiJustificantCamp> traduccions,
			Locale locale,
			OutputStream out) throws Exception {
		generarAmbPlantillaFreemarker(
				arbre,
				serveiDescripcio,
				traduccions,
				locale,
				out);
	}

	public void generarAmbPlantillaFreemarker(
			ElementArbre arbre,
			String serveiDescripcio,
			List<ServeiJustificantCamp> traduccions,
			Locale locale,
			OutputStream out) throws Exception {
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
		documentTemplateFactory.getFreemarkerConfiguration().setLocale(
				(locale != null) ? locale : new Locale("ca", "ES"));
		DocumentTemplate template = documentTemplateFactory.getTemplate(
				getClass().getResourceAsStream(PLANTILLA_ODT_RESOURCE));
		template.createDocument(
				generarModel(
						arbre,
						serveiDescripcio,
						traduccions,
						locale),
				out);
	}
	/*public void imprimirAmbPlantillaProva(ElementArbre arbre) throws Exception {
		Configuration cfg = new Configuration();
		cfg.setTemplateLoader(
				new FileTemplateLoader(new File(PLANTILLA_PATH)));  
		cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
		Template tpl = cfg.getTemplate("justificant.ftl");
		OutputStreamWriter output = new OutputStreamWriter(System.out);
		tpl.process(generarModel(arbre), output);
	}*/
	public String getNomArxiuGenerat(
			String peticionId,
			String solicitudId) {
		if (solicitudId != null)
			return "PBL_" + peticionId + "_" + solicitudId + ".odt";
		else
			return "PBL_" + peticionId + ".odt";
	}
	public String getExtensioArxiuGenerat(
			String peticionId,
			String solicitudId) {
		String nom = getNomArxiuGenerat(peticionId, solicitudId);
		if (nom.indexOf(".") == -1)
			return null;
		return nom.substring(nom.lastIndexOf(".") + 1);
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}



	private Map<String, Object> generarModel(
			ElementArbre arbre,
			String serveiDescripcio,
			List<ServeiJustificantCamp> traduccions,
			Locale locale) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(
				"text_titol_capsalera",
				messageSource.getMessage(
						"justificant.plantilla.titol.capsalera",
						null,
						locale));
		model.put(
				"text_titol_servei",
				serveiDescripcio);
		model.put(
				"text_titol_limitacio",
				messageSource.getMessage(
						"justificant.plantilla.titol.limitacio",
						null,
						locale));
		model.put(
				"text_legal",
				messageSource.getMessage(
						"justificant.plantilla.text.legal",
						null,
						locale));
		model.put(
				"text_data_eldia",
				messageSource.getMessage(
						"justificant.plantilla.data.eldia",
						null,
						locale));
		Date ara = new Date();
		model.put(
				"text_data_data",
				formatDate(ara, locale));
		model.put(
				"text_data_ales",
				messageSource.getMessage(
						"justificant.plantilla.data.ales",
						null,
						locale));
		model.put(
				"text_data_hora",
				(locale == null) ? 
						DateFormat.getTimeInstance(DateFormat.SHORT).format(ara) : 
						DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(ara));
		List<NodeInfo> nodes = new ArrayList<NodeInfo>();
		convertirArbreEnLlista(arbre, 0, nodes);
		if (traduccions != null) {
			for (NodeInfo node: nodes) {
				for (ServeiJustificantCamp traduccio: traduccions) {
					if (traduccio.getXpath().equals(node.getXpathDadaEspecifica())) {
						node.setTitol(traduccio.getTraduccio());
						break;
					}
				}
			}
		}
		model.put("nodes", nodes);
		return model;
	}
	private void convertirArbreEnLlista(ElementArbre element, int nivell, List<NodeInfo> nodes) {
		if (nivell > 0) {
			nodes.add(
					new NodeInfo(
							nivell - 1,
							element.getTitol(),
							element.getDescripcio(),
							element.getXpathDatoEspecifico()));
		}
		if (element.teFills()) {
			for (ElementArbre fill: element.getFills())
				convertirArbreEnLlista(
						fill, nivell + 1, nodes);
		}
	}

	private String formatDate(
			Date date,
			Locale locale) {
		if (locale == null || locale.getLanguage().equalsIgnoreCase("ca")) {
			String data = DateFormat.getDateInstance(DateFormat.LONG, new Locale("ca", "ES")).format(date);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int month = cal.get(Calendar.MONTH);
			if (month == Calendar.APRIL || month == Calendar.AUGUST || month == Calendar.OCTOBER) {
				return data.replaceFirst("/ ", "d'").replace("/", "de");
			} else {
				return data.replace("/", "de");
			}
		} else {
			return DateFormat.getDateInstance(DateFormat.LONG, locale).format(date);
		}
	}



	public class NodeInfo {
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
		public String getXpathDadaEspecifica() {
			return xpathDadaEspecifica;
		}
		public void setXpathDadaEspecifica(String xpathDadaEspecifica) {
			this.xpathDadaEspecifica = xpathDadaEspecifica;
		}
		public int getNivell() {
			return nivell;
		}
		public void setNivell(int nivell) {
			this.nivell = nivell;
		}
		public String getTitol() {
			return titol;
		}
		public void setTitol(String titol) {
			this.titol = titol;
		}
		public String getDescripcio() {
			return descripcio;
		}
		public void setDescripcio(String descripcio) {
			this.descripcio = descripcio;
		}
		public boolean isFinal() {
			return (descripcio != null);
		}
	}

}
