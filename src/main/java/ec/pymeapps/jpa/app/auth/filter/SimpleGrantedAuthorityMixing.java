package ec.pymeapps.jpa.app.auth.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class SimpleGrantedAuthorityMixing {

	
	/**
	 *  @JsonCreator es para designar a este como el constructor por defecto, 
	 *  cuando se creen objetos SimpleGrantedAuthority
	 *  
	 *  @JsonProperty("authority") mapea el campo del JSON hacia la propiedad del objeto 
	 *  
	 */
	@JsonCreator
	public SimpleGrantedAuthorityMixing( @JsonProperty("authority") String role) {
		
		
	}
	
	

}
