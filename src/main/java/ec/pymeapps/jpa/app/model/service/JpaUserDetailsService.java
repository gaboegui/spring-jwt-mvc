package ec.pymeapps.jpa.app.model.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.pymeapps.jpa.app.model.entity.Role;
import ec.pymeapps.jpa.app.model.entity.Usuario;
import ec.pymeapps.jpa.app.model.repository.IUsuarioRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {

	@Autowired
	private IUsuarioRepository usuarioRepository;
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	@Transactional(readOnly = true) // con esto me aseguro que el LAZY usuario.getRoles() se ejecute en la misma transaccion
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario = usuarioRepository.findByUsername(username);
		
		if (usuario == null) {
			log.error("Usuario no encontrado: " + username);
			throw new UsernameNotFoundException("Usuario no encontrado: " + username);
		}
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		for (Role role : usuario.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getNombre()) );
		}
		
		if (authorities.isEmpty()) {
			log.error("Usuario: " + username + " no tiene roles asignados");
			throw new UsernameNotFoundException("Usuario: " + username + " no tiene roles asignados");
		}

		return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(), true, true, true, authorities);
	}

}
