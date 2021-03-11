package ec.pymeapps.jpa.app.model.repository;

import org.springframework.data.repository.CrudRepository;

import ec.pymeapps.jpa.app.model.entity.Usuario;

public interface IUsuarioRepository extends CrudRepository<Usuario, Long> {
	
	public Usuario findByUsername(String username);

}
