package ec.pymeapps.jpa.app.auth.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ec.pymeapps.jpa.app.auth.service.JWTService;
import ec.pymeapps.jpa.app.auth.service.JWTServiceImpl;
import ec.pymeapps.jpa.app.model.entity.Usuario;

/**
 * Esta es la primera clase necesaria para JWT, 
 * por debajo invoca a JpaUserDetailsService
 * 
 * maneja el login a traves del body del metodo POST
 * 
 * @author Gabriel Eguiguren
 *
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	
	private JWTService jwtService;
	
	private AuthenticationManager authenticationManager;
	
	/**
	 * Aplica el filtro solo para el URI "/api/login" tipo POST
	 * 
	 * @param authenticationManager
	 */
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTService jwtService ) {
		
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		
		setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login", "POST"));
	}

	
	/**
	 * Crea un token interno de Spring con el usuario logeado 
	 * 
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		String username = obtainUsername(request);
		String password = obtainPassword(request);
		
		if(username != null && password != null) {
			
			logger.info("Username recibido desde request parameter (form-data): " + username);
			
		} else {
			//los datos vienen en un objeto JSON en el body tipo RAW

			Usuario usuario = null;
			
			try {
				// convierto el objeto RAW json en un objeto Usuario
				usuario = new ObjectMapper().readValue(request.getInputStream(),Usuario.class);
				username = usuario.getUsername();
				password = usuario.getPassword();
				
				logger.info("Username recibido desde request(raw): " + username);
				
			} catch (JsonParseException e) {
				logger.error(e);
			} catch (JsonMappingException e) {
				logger.error(e);
			} catch (IOException e) {
				logger.error(e);
			}
			
		}
		
		//token interno de Spring con el usuario logeado
		UsernamePasswordAuthenticationToken authToken =  new UsernamePasswordAuthenticationToken(username, password);
		
		// si el login es exitoso retorna el objeto Authentication con los roles inclusive
		return authenticationManager.authenticate(authToken);
	}

	/**
	 * Si la autentificacion fue exitosa, crea el JWT, 
	 * aqui se define la clave secreta, y el contenido del JWT
	 *  
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		String jwtToken = jwtService.createToken(authResult);
		
		//Pasar el JWT creado al response, "Authorization" y "Bearer " son obligatorios
		response.addHeader("Authorization", JWTServiceImpl.TOKEN_PREFIX + jwtToken);
		
		//aparte del response Header voy a enviar un json en el Body Http
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("token", jwtToken);
		body.put("user", (User) authResult.getPrincipal());
		body.put("mensaje", String.format("%s, has iniciado la sesión con éxito", authResult.getName()));
		
		// añado el map al response, con ObjectMapper convierto cualquier objeto java a JSON
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		
		response.setStatus(200);	// respuesta OK
		response.setContentType("application/json");
	}

	/**
	 * Define la respuesta en el body si la autenticacion falla
	 * 
	 */
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("mensaje", "Error de Autenticación: username o password incorrectos");
		body.put("error", failed.getMessage());
		
		// añado el map al response, con ObjectMapper convierto cualquier objeto java a JSON
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(401);	// respuesta No Autorizado
		response.setContentType("application/json");

		
		
	}
	
	
	
	
	

}
