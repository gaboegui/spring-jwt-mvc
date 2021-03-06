package ec.pymeapps.jpa.app.auth.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import ec.pymeapps.jpa.app.auth.service.JWTService;
import ec.pymeapps.jpa.app.auth.service.JWTServiceImpl;

/**
 * Obtiene de todos los request el Header para obtener el JWT y ver si es valido
 * 
 * @author Gabriel Eguiguren
 *
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	JWTService jwtService;
	
	public JWTAuthorizationFilter(AuthenticationManager authenticationManager, JWTService jwtService) {
		super(authenticationManager);
		this.jwtService = jwtService;
		

	}

	/**
	 * Validar el token recibido
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String headerJwt = request.getHeader("Authorization");

		if (requiereAutenticacion(headerJwt) == false) {
			chain.doFilter(request, response);
			return;
		}
		
		UsernamePasswordAuthenticationToken authentication = null;
		
		// obtengo los datos del token y pueblo el authentication 
		if(jwtService.validarToken(headerJwt)) {
			
			String username = jwtService.getUsername(headerJwt);
			authentication = new UsernamePasswordAuthenticationToken(username, null 
					,jwtService.getAuthorities(headerJwt));
		}
		
		// autentico al usuario y lo almaceno en la peticion del request 
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(request, response);

	}

	protected boolean requiereAutenticacion(String header) {

		if (header == null || !header.startsWith(JWTServiceImpl.TOKEN_PREFIX)) {
			return false;
		}

		return true;

	}

}
