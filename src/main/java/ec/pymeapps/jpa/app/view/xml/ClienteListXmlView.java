package ec.pymeapps.jpa.app.view.xml;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.xml.MarshallingView;

import ec.pymeapps.jpa.app.model.entity.Cliente;

/**
 * corresponde al return "listar"; del metodo listar en ClienteController
 * 
 */
@Component("listar.xml")
public class ClienteListXmlView extends MarshallingView {

	/**
	 * Este constructor utiliza la clase: Jaxb2Marshaller definida en MvcConfig
	 * 
	 * @param marshaller
	 */
	@Autowired
	public ClienteListXmlView(Jaxb2Marshaller marshaller) {
		super(marshaller);
		
	}


	@SuppressWarnings("unchecked")
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		// obtengo los datos
		Page<Cliente> clientes = (Page<Cliente>) model.get("clientes");
		
		// luego remuevo los objetos que no van a estar en el XML dejándolo vacio
		model.remove("titulo");
		model.remove("page");
		model.remove("clientes");
		
		// si quiero todo el listado de clientes sin paginacion puedo llamar al servicio de Cliente
		// , y luego creo el ClienteList con los datos
		model.put("clienteList", new ClienteList(clientes.getContent()));
		
		super.renderMergedOutputModel(model, request, response);
	}
	
	

}
