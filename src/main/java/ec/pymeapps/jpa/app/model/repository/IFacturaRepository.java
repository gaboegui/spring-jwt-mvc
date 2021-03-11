package ec.pymeapps.jpa.app.model.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ec.pymeapps.jpa.app.model.entity.Factura;

public interface IFacturaRepository extends CrudRepository<Factura, Long> {
	
	// evita que se hagan 7 consultas con las oparaciones LAZY al momento de cargar una factura en la accion ver
	@Query("select f from Factura f join fetch f.cliente c join fetch f.items i join fetch i.producto where f.id =?1")
	public Factura fetchByIdWithClienteWithItemFacturaWithProducto(Long id);

}
