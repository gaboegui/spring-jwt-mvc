package ec.pymeapps.jpa.app.view.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ec.pymeapps.jpa.app.model.entity.Cliente;

/**
 * esta clase sirve para hacer de elemento <root> del XML
 * 
 * @author Gabriel Eguiguren
 *
 */
@XmlRootElement(name = "clientes")
public class ClienteList {
	
	@XmlElement(name = "cliente")
	public List<Cliente> clientes;

	public ClienteList(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public ClienteList() {
	}

	public List<Cliente> getClientes() {
		return clientes;
	}
	
	
	
	

}
