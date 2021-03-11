package ec.pymeapps.jpa.app;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
	/**
	 * defino el locale por defecto
	 * 
	 * @return
	 */
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver(); 
		localeResolver.setDefaultLocale(new Locale("es", "ES"));
		return localeResolver;
	}
	
	/**
	 *  aqui defino el nombre de la variable del request que va a contener el idioma
	 * @return
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		 
		localeChangeInterceptor.setParamName("lang");
		
		return localeChangeInterceptor;
	}
	
	
	/**
	 * registro el interceptor en el aplicativo
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(localeChangeInterceptor());
	}
	
	
	/**
	 * Habilitar bean para realizar el marshal de XML y convertir Clases 
	 */
	@Bean
	public Jaxb2Marshaller jaxb2Marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(new Class[] {ec.pymeapps.jpa.app.view.xml.ClienteList.class});
		return marshaller;
	}
	

	// este metdo tiene que llamarse exactamente asi 
	public void addViewControllers(ViewControllerRegistry registry ) {
		
		//evita estar creando un controller solo para redireccionar a algun lado
		// para que funcione tambien hay que añadir el path en SpringSecurityConfig.class
		registry.addViewController("/error_403").setViewName("error_403");
		
	}
	
	
	

}
