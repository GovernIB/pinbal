package es.caib.pinbal.back.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import es.caib.pinbal.back.base.config.BaseWebMvcConfig;
import es.caib.pinbal.back.filter.OriginalServletPathFilter;
import es.caib.pinbal.back.interceptor.PinbalInterceptor;
import es.caib.pinbal.back.view.AuditorGenerarCsvView;
import es.caib.pinbal.back.view.AuditorGenerarExcelView;
import es.caib.pinbal.back.view.ConsultaAdminExcelView;
import es.caib.pinbal.back.view.ConsultaAuditorExcelView;
import es.caib.pinbal.back.view.ConsultaExcelView;
import es.caib.pinbal.back.view.ConsultaMultipleExcelView;
import es.caib.pinbal.back.view.ConsultaSuperauditorExcelView;
import es.caib.pinbal.back.view.EstadistiquesExcelView;
import es.caib.pinbal.back.view.InformeGeneralEstatExcelView;
import es.caib.pinbal.back.view.InformeProcedimentsExcelView;
import es.caib.pinbal.back.view.InformeServeisExcelView;
import es.caib.pinbal.back.view.InformeUsrEntOrgProcServExcelView;
import es.caib.pinbal.back.view.InformeUsuarisExcelView;
import es.caib.pinbal.back.view.PeticioMultiplePlantillaCsvView;
import es.caib.pinbal.back.view.PeticioMultiplePlantillaExcelView;
import es.caib.pinbal.back.view.PeticioMultiplePlantillaOdsView;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.base.util.ThreadLocalUtil;
import es.caib.pinbal.logic.intf.model.UserSession;
import es.caib.pinbal.logic.intf.resourceservice.UsuariResourceService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Configuració de Spring web MVC.
 *
 * @author Limit Tecnologies
 */
@RequiredArgsConstructor
@Configuration
@Order
public class WebMvcConfig extends BaseWebMvcConfig {

	@Value("${" + BaseConfig.PROP_USER_SESSION_HTTP_HEADER + ":X-App-Session}")
	private String userSessionHttpHeader;

	private final PinbalInterceptor pinbalInterceptor;
	private final ObjectMapper objectMapper;
	private final UsuariResourceService usuariResourceService;

	private static final long MAX_UPLOAD_SIZE = 52428800;

	@Bean
	public FilterRegistrationBean<OriginalServletPathFilter> originalServletPathFilter() {
		FilterRegistrationBean<OriginalServletPathFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new OriginalServletPathFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(1);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<SiteMeshFilter> sitemeshFilter() {
		FilterRegistrationBean<SiteMeshFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new SiteMeshFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(2);
		return registrationBean;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/webjars/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.jsp("/WEB-INF/jsp/", ".jsp");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.
				addMapping("/**").
				allowedOrigins("http://localhost:5173", "http://localhost:8080").
				allowCredentials(true).
				allowedHeaders("*").
				allowedMethods("*");
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(MAX_UPLOAD_SIZE);
		return multipartResolver;
	}

	@Bean
	public LocaleResolver localeResolver() {
		var localeResolver = new CustomLocaleResolver(Arrays.asList(Locale.forLanguageTag("ca"), Locale.forLanguageTag("es")));
		localeResolver.setDefaultLocale(Locale.forLanguageTag("ca"));
		return localeResolver;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		var lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	@Bean
	public HandlerInterceptor userInterceptor() {
		return new AsyncHandlerInterceptor() {
			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
				usuariResourceService.refresh();
				return true;
			}
		};
	}

	@Bean
	public HandlerInterceptor userSessionInterceptor() {
		return new AsyncHandlerInterceptor() {
			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws JsonProcessingException {
				UserSession userSession = null;
				String json = request.getHeader(userSessionHttpHeader);
				if (json != null) {
					var parsedJson = objectMapper.readValue(json, java.util.Map.class);
					Integer entitatId = (Integer)parsedJson.get("e");
					Integer organGestorId = (Integer)parsedJson.get("o");
					userSession = new UserSession(
						entitatId != null ? entitatId.longValue() : null);
				}
				if (userSession != null) {
					ThreadLocalUtil.setAttribute(ThreadLocalUtil.SESSION_KEY, userSession);
				}
				return true;
			}
		};
	}

	private static final String[] INTERCEPTOR_EXCLUSIONS = 	{
			BaseConfig.API_PATH + "/**",
			BaseConfig.PING_PATH,
			BaseConfig.SYSENV_PATH,
			BaseConfig.MANIFEST_PATH,
			BaseConfig.AUTH_TOKEN_PATH,
			BaseConfig.REACT_APP_PATH + "/**",
			"/js/**",
			"/css/**",
			"/fonts/**",
			"/img/**",
			"/images/**",
			"/extensions/**",
			"/webjars/**",
			"/**/datatable/**",
			"/**/selection/**",
			"/api/rest/**",
			"/api/apidoc**",
			"/api-docs/**",
			"/**/api-docs/",
			"/api/consulta/**",
			"/api/services/**",
			"/error",
			"/**/monitor/tasques",
			"/usuari/configuracio/**"
	};

	// - BeanNameViewResolver (ordre 0) per resoldre noms de vista als beans
	//  - 16 @Bean de vistes (Excel, CSV, ODS) amb els noms exactes que retornen els controladors
	//  - configureDefaultServletHandling() habilitat
	//  - Handler de recursos per /webjars/**
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean
	public BeanNameViewResolver beanNameViewResolver() {
		BeanNameViewResolver resolver = new BeanNameViewResolver();
		resolver.setOrder(0);
		return resolver;
	}

	// Sobreescriu el ContentNegotiatingViewResolver d'Spring Boot (que recull candidats de TOTS els
	// resolvers incloent InternalResourceViewResolver). Amb InternalResourceViewResolver al pool,
	// per a Accept: text/html la JSP guanya sempre sobre la vista Excel (text/html q=1.0 > */* q=0.8),
	// i el forward a la JSP inexistent retorna el path com a error. Delegant UNICAMENT al
	// BeanNameViewResolver: Excel views es resolen via */* i les JSP normals cauen al resolver inferior.
	@Bean
	public ContentNegotiatingViewResolver contentNegotiatingViewResolver() {
		ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
		resolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
		resolver.setViewResolvers(Collections.singletonList(beanNameViewResolver()));
		return resolver;
	}

	@Bean(name = "consultaExcelView")
	public ConsultaExcelView consultaExcelView() { return new ConsultaExcelView(); }

	@Bean(name = "consultaMultipleExcelView")
	public ConsultaMultipleExcelView consultaMultipleExcelView() { return new ConsultaMultipleExcelView(); }

	@Bean(name = "consultaAdminExcelView")
	public ConsultaAdminExcelView consultaAdminExcelView() { return new ConsultaAdminExcelView(); }

	@Bean(name = "consultaAuditorExcelView")
	public ConsultaAuditorExcelView consultaAuditorExcelView() { return new ConsultaAuditorExcelView(); }

	@Bean(name = "consultaSuperauditorExcelView")
	public ConsultaSuperauditorExcelView consultaSuperauditorExcelView() { return new ConsultaSuperauditorExcelView(); }

	@Bean(name = "estadistiquesExcelView")
	public EstadistiquesExcelView estadistiquesExcelView() { return new EstadistiquesExcelView(); }

	@Bean(name = "auditorGenerarExcelView")
	public AuditorGenerarExcelView auditorGenerarExcelView() { return new AuditorGenerarExcelView(); }

	@Bean(name = "auditorGenerarCsvView")
	public AuditorGenerarCsvView auditorGenerarCsvView() { return new AuditorGenerarCsvView(); }

	@Bean(name = "peticioMultiplePlantillaExcelView")
	public PeticioMultiplePlantillaExcelView peticioMultiplePlantillaExcelView() { return new PeticioMultiplePlantillaExcelView(); }

	@Bean(name = "peticioMultiplePlantillaCsvView")
	public PeticioMultiplePlantillaCsvView peticioMultiplePlantillaCsvView() { return new PeticioMultiplePlantillaCsvView(); }

	@Bean(name = "peticioMultiplePlantillaOdsView")
	public PeticioMultiplePlantillaOdsView peticioMultiplePlantillaOdsView() { return new PeticioMultiplePlantillaOdsView(); }

	@Bean(name = "informeProcedimentsExcelView")
	public InformeProcedimentsExcelView informeProcedimentsExcelView() { return new InformeProcedimentsExcelView(); }

	@Bean(name = "informeUsuarisExcelView")
	public InformeUsuarisExcelView informeUsuarisExcelView() { return new InformeUsuarisExcelView(); }

	@Bean(name = "informeServeisExcelView")
	public InformeServeisExcelView informeServeisExcelView() { return new InformeServeisExcelView(); }

	@Bean(name = "informeGeneralEstatExcelView")
	public InformeGeneralEstatExcelView informeGeneralEstatExcelView() { return new InformeGeneralEstatExcelView(); }

	@Bean(name = "informeUsrEntOrgProcServExcelView")
	public InformeUsrEntOrgProcServExcelView informeUsrEntOrgProcServExcelView() { return new InformeUsrEntOrgProcServExcelView(); }
	// Fi


	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(userInterceptor());
		registry.addInterceptor(userSessionInterceptor());
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(pinbalInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS).order(0);
//		registry.addInterceptor(new CsrfTokenInterceptor());
	}

	public static class CustomLocaleResolver extends SessionLocaleResolver {
		private final AcceptHeaderLocaleResolver acceptHeaderLocaleResolver;
		public CustomLocaleResolver(List<Locale> supportedLocales) {
			acceptHeaderLocaleResolver = new AcceptHeaderLocaleResolver();
			acceptHeaderLocaleResolver.setSupportedLocales(supportedLocales);
		}
		@Override
		@NotNull
		protected Locale determineDefaultLocale(@NotNull HttpServletRequest request) {
			var acceptHeaderLocale = acceptHeaderLocaleResolver.resolveLocale(request);
			if (acceptHeaderLocale != null) {
				return acceptHeaderLocale;
			}
			Locale defaultLocale = getDefaultLocale();
			if (defaultLocale == null) {
				defaultLocale = request.getLocale();
			}
			return defaultLocale;
		}
	}

}
