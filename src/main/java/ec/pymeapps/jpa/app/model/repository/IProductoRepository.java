package ec.pymeapps.jpa.app.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ec.pymeapps.jpa.app.model.entity.Producto;

public interface IProductoRepository extends CrudRepository<Producto, Long> {
	
	@Query("select p from Producto p where p.nombre like %?1%")
	public List<Producto> findByNombre(String term);
	
	//es lo mismo que arriba
	public List<Producto> findByNombreLikeIgnoreCase(String term); 

}
