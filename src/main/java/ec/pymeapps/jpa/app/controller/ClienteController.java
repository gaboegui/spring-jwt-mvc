package ec.pymeapps.jpa.app.controller;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ec.pymeapps.jpa.app.controller.paginator.PageRender;
import ec.pymeapps.jpa.app.model.entity.Cliente;
import ec.pymeapps.jpa.app.model.service.IClienteService;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MessageSource messagesSource; 
	
	@Autowired
	private IClienteService clienteService;
	
	// con @ResponseBody especifico que el resultado no sera una vista y sera una repsuesta de un metodo REST en JSON
	// si creo un new ClientList(clienteService.findAll()) y lo retorno la respuesta sera en XML
	@GetMapping(value = "/api/listar")
	@ResponseBody
	public List<Cliente> listarRest() {
		return clienteService.findAll();
	}
	
	@GetMapping(value = {"/listar", "/"})
	public String listar(@RequestParam(name ="page", defaultValue = "0") int page,
			Model model, HttpServletRequest request, Locale locale) {

		
		//obtener el username logeado
		//otra forma de obtener el objeto Authentication sin tener que ponerlo como parametro de la funcion
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication !=  null) {
			log.info("El usuario: "+ authentication.getName()+ " ha iniciado sesión con éxito" );
		}
		
		if (hasRole("ROLE_ADMIN")) {
			log.info("Hola: "+ authentication.getName()+ " tienes Rol de Administrador" );
		}
		
		//Otras forma de comprobar el rol
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "");
		if(securityContext.isUserInRole("ROLE_ADMIN")) {
			log.info("USANDO SecurityContextHolderAwareRequestWrapper Hola: "+ authentication.getName()+ " tienes Rol de Administrador" );
		}

		if(request.isUserInRole("ROLE_ADMIN")) {
			log.info("USANDO HttpServletRequest Hola: "+ authentication.getName()+ " tienes Rol de Administrador" );
		}
		
		// el 4 indica el numero de registros a desplegar/leer por pagina
		Pageable pageRequest = PageRequest.of(page, 4);
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
		model.addAttribute("page", pageRender);

		
		// remplazo por consulta con paginación
		//model.addAttribute("clientes", clienteService.findAll());
		model.addAttribute("clientes", clientes);
		// obtengo los mensaje para internacionalizacion
		model.addAttribute("titulo", messagesSource.getMessage("text.cliente.listar.titulo", null, locale));
		
		return "listar";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/form/{id}")
	public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {

		Cliente cliente = null;

		if (id > 0) {
			cliente = clienteService.findOne(id);
			if(cliente == null) {
				flash.addFlashAttribute("error","El Cliente: "+id.toString()+" no existe!");
				return "redirect:/listar";
			}
			
		} else {
			flash.addFlashAttribute("error","El Id del Cliente no puede ser Cero!");
			return "redirect:/listar";
		}

		model.addAttribute("titulo", "Editar Cliente");
		model.addAttribute("cliente", cliente);
		flash.addFlashAttribute("success","Cliente editado con éxito!");
		return "form";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/eliminar/{id}")
	public String borrar(@PathVariable Long id, RedirectAttributes flash) {

		if (id > 0) {
			clienteService.delete(id);
			flash.addFlashAttribute("success","Cliente eliminado con éxito!");
		}

		return "redirect:/listar";

	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/form")
	public String crear(Model model) {

		Cliente cliente = new Cliente();

		model.addAttribute("titulo", "Formulario de Cliente");
		model.addAttribute("cliente", cliente);

		return "form";
	}
	
	@Secured("ROLE_ADMIN")
	@PostMapping("/form")
	public String guardar(@Valid Cliente cliente, BindingResult result, 
			Model model, RedirectAttributes flash, SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		}

		String mensaje = (cliente.getId()!= null) ? "Cliente editado con éxito!" : "Cliente creado con éxito!"; 
		
		clienteService.save(cliente);

		status.setComplete(); // esto elimina el atributo "cliente" de la session
		flash.addFlashAttribute("success", mensaje);
		return "redirect:listar";
	}
	
	@Secured("ROLE_USER")
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {

		// Cliente cliente = clienteService.findOne(id);
		// optimizando con la consulta fetch para que en una sola consulta traiga los datos del cliente y sus facturas
		Cliente cliente = clienteService.fetchByIdWithFacturas(id);
		
		
		if (cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/listar";
		}

		model.addAttribute("cliente", cliente);
		model.addAttribute("titulo", "Detalle cliente: " + cliente.getNombre());
		return "ver";
	}
	
	/**
	 * Metodo que ayuda a obtener el nombre del Rol del usuario authenticado
	 * de manera programatica
	 * 
	 * @param role
	 * @return
	 */
	private boolean hasRole(String role) {
		SecurityContext context = SecurityContextHolder.getContext();
		
		if (context == null ) {
			return false;
		}
		
		Authentication auth = context.getAuthentication();
		
		if (auth == null ) {
			return false;
		}
		
		// ? extends GrantedAuthority =  para cualquier clase que implemente GrantedAuthority
		Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
		
		for(GrantedAuthority item : roles ) {
			if (role.equals(item.getAuthority())) {
				return true;
				
			}
		}
		
		return false;
		
	}

}
