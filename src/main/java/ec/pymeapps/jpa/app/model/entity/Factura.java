package ec.pymeapps.jpa.app.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * el @XmlTransient en getCliente() es importante para evitar el loop infinito al momento de generar la vista
 * para exportar el listado de Clientes en XML
 * 
 * 
 * @author Gabriel Eguiguren
 *
 */

@Entity
@Table(name="facturas")
public class Factura implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty
	private String descripcion;
	
	private String observacion;
	
	@Temporal(TemporalType.DATE)
	private Date createAt;
	
	//muchas facturas pueden pertencer a un solo cliente
	@ManyToOne(fetch=FetchType.LAZY) 	// LAZY para evitar que cargue automaticamente la lista
	@JsonBackReference					// evita que se serialize la relacion inversa
	private Cliente cliente;

	// como la relacion es unidireccional con ItemFactura, 
	// entonces @JoinColumn crea la FK a nivel de BD
	@JoinColumn(name="factura_id") 
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval = true)
	private List<ItemFactura> items;
	
	
	public void addItemFactura(ItemFactura item) {
		this.items.add(item);
	}

	public Factura() {
		this.items = new ArrayList<ItemFactura>();
	}

	@PrePersist
	public void prePersist() {
		createAt = new Date();
	}
	
	public BigDecimal getTotal() {
		
		BigDecimal total = new BigDecimal(0);
		
		for(int i=0; i< items.size(); i++) {
			total = total.add(items.get(i).calcularSubTotal());
		}
		
		return total;
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	@XmlTransient
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<ItemFactura> getItems() {
		return items;
	}

	public void setItems(List<ItemFactura> items) {
		this.items = items;
	}
	
	
	
	
	

}
