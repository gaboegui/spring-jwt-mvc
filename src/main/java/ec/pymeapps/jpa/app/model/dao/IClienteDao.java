package ec.pymeapps.jpa.app.model.dao;

import java.util.List;


import ec.pymeapps.jpa.app.model.entity.Cliente;

public interface IClienteDao {
	
	public List<Cliente> findAll();
	
	public Cliente findOne(Long id);
	
	public void save(Cliente cliente);
	
	public void delete(Long id);
	
	

}
