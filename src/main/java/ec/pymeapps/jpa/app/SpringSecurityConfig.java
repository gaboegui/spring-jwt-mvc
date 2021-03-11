package ec.pymeapps.jpa.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ec.pymeapps.jpa.app.auth.handler.LoginSuccessHandler;
import ec.pymeapps.jpa.app.model.service.JpaUserDetailsService;


// esta anotacion permite que se puedan usar las anotaciones: @Secured("ROLE_ADMIN")
// en los controladores
@EnableGlobalMethodSecurity(securedEnabled = true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter  {
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Autowired
	JpaUserDetailsService jpaUserDetailsService;
	
	@Autowired
	LoginSuccessHandler successHandler;
	
	@Autowired
	public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception {
		
		// utiliza BCrypt
		PasswordEncoder encoder = passwordEncoder();
		// para que funcione con la base de datos
		builder.userDetailsService(jpaUserDetailsService).passwordEncoder(encoder);
		
		/*
		UserBuilder userbuilder = User.builder().passwordEncoder(password -> encoder.encode(password));
		builder.inMemoryAuthentication()
		.withUser(userbuilder.username("admin").password("123").roles("ADMIN", "USER"))
		.withUser(userbuilder.username("user").password("123").roles("USER"));
		*/
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests().antMatchers("/", "/css/**", "/js/**", "/images/**", "/listar", "/locale", "/api/listar").permitAll()
		.anyRequest().authenticated()
		.and()
			.formLogin()
				.successHandler(successHandler)
				.loginPage("/login").permitAll() //aqui mapeo al controller para que no muestre el FORM login por defecto 
//		.formLogin().permitAll() // este es el form standard
		.and()
		.logout().permitAll()
		.and()
		.exceptionHandling().accessDeniedPage("/error_403");
		
		
		
	}
	
	

}
