package ec.pymeapps.jpa.app.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

// es muy importante que se defina como serializable para futuros intercambios

@Entity
@Table(name="clientes")
public class Cliente implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50) // bd
	@NotEmpty
	@Size(min=4 , max=49)
	private String nombre;
	
	@NotEmpty
	private String apellido;
	
	@NotEmpty
	@Email
	private String email;
	
	private String foto;
	
	
	@NotNull
	@Column(name="creado_en")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd") // HTML5 input date
	@JsonFormat(pattern = "yyyy-MM-dd")		// formateo JSON para este tipo de View
	private Date creadoEn;

	//@JsonIgnore hace que se ignore al momento de generar la Vista para el formato JSON para evitar el loop con Cliente
	//@JsonManagedReference su contraparte es @JsonBackReference de esta manera se serializa solo la primera relacion
	@JsonManagedReference  
	@OneToMany(mappedBy ="cliente", fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval = true )
	private List<Factura> facturas;
	
	
	// extra apara añadir de una en una
	public void addFactura(Factura factura) {
		facturas.add(factura);
	}
	
	//inicializo con el constructor la lista
	public Cliente() {
		facturas = new ArrayList<Factura>();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreadoEn() {
		return creadoEn;
	}

	public void setCreadoEn(Date creadoEn) {
		this.creadoEn = creadoEn;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<Factura> getFacturas() {
		return facturas;
	}

	public void setFacturas(List<Factura> facturas) {
		this.facturas = facturas;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	@Override
	public String toString() {
		return nombre + " " + apellido;
	}
	
	
	
	

}
