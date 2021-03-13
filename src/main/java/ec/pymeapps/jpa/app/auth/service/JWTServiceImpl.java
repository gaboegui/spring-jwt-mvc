package ec.pymeapps.jpa.app.auth.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ec.pymeapps.jpa.app.auth.filter.SimpleGrantedAuthorityMixing;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTServiceImpl implements JWTService {

	public static final String SECRET_KEY = Base64Utils.encodeToString("ALGUNA-CLAAV3-SECRETA-454864564".getBytes());
	
	public static final String TOKEN_PREFIX = "Bearer ";
	
	public static final long TOKEN_EXPIRATION_TIME_MILISEG = 3600000L; // una hora
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	

	@Override
	public String createToken(Authentication authentication) throws JsonProcessingException {

		Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();

		// tambien se puede obtener directamente con authResult.getName();
		String username = ((User) authentication.getPrincipal()).getUsername();

		Claims claims = Jwts.claims();
		// convierto roles a JSON y los pongo en claims para incluirlos en el jwtToken
		claims.put("authorities", new ObjectMapper().writeValueAsString(roles));
		
		// creo el token
		String jwtToken = Jwts.builder().setClaims(claims).setSubject(username)
				.signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME_MILISEG)).compact();

		return jwtToken;
	}

	@Override
	public boolean validarToken(String token) {
		
		
		// validar el token y obtengo el body
		try {
			 
			@SuppressWarnings("unused")
			Claims claims = getClaims(token);
			
			return true;

		} catch (JwtException | IllegalArgumentException e) {
			log.error("Error al validar el token JWT: ", e);
			return false;
			
		} catch (Exception e) {
			log.error("Error al validar el token JWT Exception: ", e);
			return false;
		}
	}

	/**
	 * decodifica el token y devuelve el Claims
	 */
	@Override
	public Claims getClaims(String token) {

		Claims claims = Jwts.parser()
				.setSigningKey(SECRET_KEY.getBytes())
				.parseClaimsJws(limpiarToken(token)) 		// valida el token
				.getBody();
		
		return claims;
	}
	
	@Override
	public String getUsername(String token) {

		return getClaims(token).getSubject();
	}


	/**
	 * Obtiene los roles incluidos en los claims del token JWT
	 * haciendo una conversion de JSON a GrantedAuthority
	 * 
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities(String token) throws JsonParseException, JsonMappingException, IOException {

		Object roles = getClaims(token).get("authorities");
		
		// convierto el JSON a una lista de GrantedAuthority
		Collection<? extends GrantedAuthority> authorities = Arrays.asList(
				new ObjectMapper()
				// defino la clase mixin que hace la conversion del objeto
				.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixing.class) 
				.readValue(roles.toString().getBytes(),SimpleGrantedAuthority[].class));
		
		return authorities;
	}

	/**
	 * Elimina la palabra Bearer que viene en el token
	 * 
	 */
	@Override
	public String limpiarToken(String token) {

		if (token!=null && token.startsWith(TOKEN_PREFIX)) {
			return  token.replace(TOKEN_PREFIX, "");
		}
		return null;
		
	}

}
