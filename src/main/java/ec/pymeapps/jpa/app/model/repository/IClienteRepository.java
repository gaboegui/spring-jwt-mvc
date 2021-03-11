package ec.pymeapps.jpa.app.model.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import ec.pymeapps.jpa.app.model.entity.Cliente;

// no se lo declara como componente ya que el CrudRepository ya lo es 
// PagingAndSortingRepository extiende CrudRepository  y se lo cambia para hacer paginación
public interface IClienteRepository extends PagingAndSortingRepository<Cliente, Long> {
	
	// left join permite obtener un resultado asi el cliente no tenga ninguna factura
	@Query("select c from Cliente c left join fetch c.facturas f where c.id=?1")
	public Cliente fetchByIdWithFacturas(Long id);
}
