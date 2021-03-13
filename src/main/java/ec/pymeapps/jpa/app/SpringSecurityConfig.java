package ec.pymeapps.jpa.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ec.pymeapps.jpa.app.auth.filter.JWTAuthenticationFilter;
import ec.pymeapps.jpa.app.auth.filter.JWTAuthorizationFilter;
import ec.pymeapps.jpa.app.auth.handler.LoginSuccessHandler;
import ec.pymeapps.jpa.app.auth.service.JWTService;
import ec.pymeapps.jpa.app.model.service.JpaUserDetailsService;

/**
 * @EnableGlobalMethodSecurity permite que se puedan usar las anotaciones: @Secured("ROLE_ADMIN") en los controladores
 * 
 * 
 * @author Gabriel Eguiguren
 *
 */
@EnableGlobalMethodSecurity(securedEnabled = true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter  {
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Autowired
	JWTService jwtService;
	
	@Autowired
	JpaUserDetailsService jpaUserDetailsService;
	
	@Autowired
	LoginSuccessHandler successHandler;
	
	/**
	 * Aqui defino el authentication manager de todo el aplicativo
	 * 
	 * @param builder
	 * @throws Exception
	 */
	@Autowired
	public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception {
		
		// utiliza BCrypt
		PasswordEncoder encoder = passwordEncoder();
		// para que funcione con la base de datos
		builder.userDetailsService(jpaUserDetailsService).passwordEncoder(encoder);
		
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests().antMatchers("/", "/css/**", "/js/**", "/images/**", "/listar", "/locale", "/api/listar").permitAll()
		.anyRequest().authenticated()
	/*	.and()
			.formLogin()
				.successHandler(successHandler)
				.loginPage("/login").permitAll() //aqui mapeo al controller para que no muestre el FORM login por defecto 
		.and()
		.logout().permitAll()
		.and()
		.exceptionHandling().accessDeniedPage("/error_403") */
		.and()
		.addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtService)) //aqui añado el filtro para que maneje el login a traves del body del metodo POST
		.addFilter(new JWTAuthorizationFilter(authenticationManager(),  jwtService)) //añado el filtro que revisa si viene el beare JWT en el header Authorization
		.csrf().disable() // primer cambio para JWT
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);  //elimino la sesion
		
	}
	
	

}
