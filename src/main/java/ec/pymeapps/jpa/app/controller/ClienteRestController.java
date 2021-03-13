package ec.pymeapps.jpa.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.pymeapps.jpa.app.model.entity.Cliente;
import ec.pymeapps.jpa.app.model.service.IClienteService;

/**
 * alternativa tener un Controller dedicado para solo tipo de respuestas REST
 * 
 * @author Gabriel Eguiguren
 *
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteService;
	
	@GetMapping(value = "/listar")
	@Secured("ROLE_ADMIN")
	public List<Cliente> listarRest() {
		return clienteService.findAll();
	}


}
