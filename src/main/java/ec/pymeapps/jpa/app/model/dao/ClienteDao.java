package ec.pymeapps.jpa.app.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import ec.pymeapps.jpa.app.model.entity.Cliente;

@Repository("clienteDaoJPA")
public class ClienteDao implements IClienteDao {

	@PersistenceContext
	EntityManager em;

	@SuppressWarnings("unchecked")
	@Override
	public List<Cliente> findAll() {
		return em.createQuery("from Cliente").getResultList();
	}

	@Override
	public Cliente findOne(Long id) {
		
		return em.find(Cliente.class, id);
	}

	@Override
	public void save(Cliente cliente) {
		if (cliente.getId() != null && cliente.getId() > 0) {
			// UPDATE
			em.merge(cliente);
		} else {
			// INSERT
			em.persist(cliente);
		}
	}


	@Override
	public void delete(Long id) {
		em.remove(findOne(id));

	}

}
