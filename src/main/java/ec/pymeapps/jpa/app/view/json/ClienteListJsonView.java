package ec.pymeapps.jpa.app.view.json;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import ec.pymeapps.jpa.app.model.entity.Cliente;

/**
 * corresponde al return "listar"; del metodo listar() en ClienteController
 * 
 * @author Gabriel Eguiguren
 *
 */
@Component("listar.json")
public class ClienteListJsonView extends MappingJackson2JsonView {

	@SuppressWarnings("unchecked")
	@Override
	protected Object filterModel(Map<String, Object> model) {

		//quito los atributos que vienen del controller que no quiero incluir en el JSON de respuesta
		model.remove("titulo");
		model.remove("page");
		
		// obtengo los datos
		Page<Cliente> clientes = (Page<Cliente>) model.get("clientes");
		model.remove("clientes");
		
		// si quiero todo el listado de clientes sin paginacion puedo llamar al servicio de Cliente
		model.put("clientes", clientes.getContent());

		
		return super.filterModel(model);
	}
	
	

}
