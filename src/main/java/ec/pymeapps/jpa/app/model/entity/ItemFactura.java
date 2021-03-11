package ec.pymeapps.jpa.app.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="item_facturas")
public class ItemFactura implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer cantidad;
	
	
	//relacion en solo sentido
	@JoinColumn(name="producto_id") 
	@ManyToOne(fetch=FetchType.LAZY)
	// como la relacion es LAZY utiliza un objeto proxy que viene con estos atributos que se ignoran al generar el JSON
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})   
	private Producto producto;
	
	public BigDecimal calcularSubTotal() {
		return producto.getPrecio().multiply(new BigDecimal(cantidad));
	}
	
	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	
	public Long getId() {
		return id;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public ItemFactura(Integer cantidad, Producto producto) {
		this.cantidad = cantidad;
		this.producto = producto;
	}

	public ItemFactura() {
	}
	
	
	
	
	
}
