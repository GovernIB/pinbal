package es.caib.pinbal.back.base.config;

import es.caib.pinbal.logic.intf.base.model.UnpagedButSorted;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolverSupport;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;
import java.util.List;

/**
 * Configuració de Spring MVC.
 * 
 * @author Límit Tecnologies
 */
public abstract class BaseWebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		CustomPageableHandlerMethodArgumentResolver resolver = new CustomPageableHandlerMethodArgumentResolver();
		resolver.setFallbackPageable(Pageable.unpaged());
		resolvers.add(resolver);
		WebMvcConfigurer.super.addArgumentResolvers(resolvers);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (isJsAppResourceHandlerEnabled()) {
			// ResourceHandler per a que totes les peticions desconegudes passin per l'index.html
			registry.
					addResourceHandler(getJsAppStaticFolder() + "/**").
					addResourceLocations("classpath:/static" + getJsAppStaticFolder() + "/").
					resourceChain(true).
					addResolver(new PathResourceResolver() {
						@Override
						protected Resource getResource(String resourcePath, Resource location) throws IOException {
							Resource requestedResource = location.createRelative(resourcePath);
							if (requestedResource.exists() && requestedResource.isReadable()) {
								return requestedResource;
							} else {
								return new ClassPathResource("static" + getJsAppStaticFolder() + "/index.html");
							}
						}
					});
		}
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
	}

	protected boolean isJsAppResourceHandlerEnabled() {
		return true;
	}

	protected String getJsAppStaticFolder() {
		return "";
	}

	public static class CustomPageableHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolverSupport implements PageableArgumentResolver {
		private static final String UNPAGED_MARKER = "UNPAGED";
		private final SortArgumentResolver sortResolver = new SortHandlerMethodArgumentResolver();
		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return Pageable.class.equals(parameter.getParameterType());
		}
		@Override
		public Pageable resolveArgument(
				MethodParameter methodParameter,
				@Nullable ModelAndViewContainer mavContainer,
				NativeWebRequest webRequest,
				@Nullable WebDataBinderFactory binderFactory) {
			String page = webRequest.getParameter(getParameterNameToUse(getPageParameterName(), methodParameter));
			String pageSize = webRequest.getParameter(getParameterNameToUse(getSizeParameterName(), methodParameter));
			Sort sort = sortResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
			boolean withPageOrSort = page != null || pageSize != null || sort.isSorted();
			if (!withPageOrSort) {
				return null;
			} else if (UNPAGED_MARKER.equals(page)) {
				return new UnpagedButSorted(sort);
			} else {
				Pageable pageable = getPageable(
						methodParameter,
						page == null ? "0" : page,
						pageSize == null || "0".equals(pageSize) ? "10" : pageSize);
				if (sort.isSorted()) {
					return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
				}
				return pageable;
			}
		}
	}

}
