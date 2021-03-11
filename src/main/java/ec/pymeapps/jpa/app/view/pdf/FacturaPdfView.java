package ec.pymeapps.jpa.app.view.pdf;

import java.awt.Color;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import ec.pymeapps.jpa.app.model.entity.Factura;
import ec.pymeapps.jpa.app.model.entity.ItemFactura;

/**
 * 
 * el URL de retorno : "factura/ver-detalle" es el mismo definido en FacturaController 
 * para el metodo verDetalleFactura();
 * 
 * @author Gabriel Eguiguren
 *
 */
@Component("factura/ver-detalle")
public class FacturaPdfView extends AbstractPdfView {
	
	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//me sirve para acceder a los locales de los diferentes idiomas desde la superclase
		MessageSourceAccessor messages = getMessageSourceAccessor();
		
		Factura factura = (Factura) model.get("factura");
		
		PdfPCell celda = null;
		celda = new PdfPCell(new Phrase(messages.getMessage("text.cliente.datos.titulo")));
		celda.setBackgroundColor(new Color(184,218,255));
		celda.setPadding(8f);
		
		PdfPTable cabecera = new PdfPTable(1);
		cabecera.setSpacingAfter(20);
		cabecera.addCell(celda);
		cabecera.addCell(factura.getCliente().getNombre().concat(" ").concat(factura.getCliente().getApellido()) );
		cabecera.addCell(factura.getCliente().getEmail());
		
		PdfPTable tabla = new PdfPTable(1);
		tabla.setSpacingAfter(20);
		celda.setPhrase(new Phrase(messages.getMessage("text.factura.datos.titulo")));
		tabla.addCell(celda);
		tabla.addCell("Número: ".concat(factura.getId().toString()));
		tabla.addCell("Descripción: ".concat(factura.getDescripcion()));
		tabla.addCell("Fecha: ".concat(factura.getCreateAt().toString()));
		
		PdfPTable detalle = new PdfPTable(4);
		detalle.setWidths(new float [] {0.8f, 3.5f, 1, 1} ); //medidas de relacion
		detalle.addCell("Cantidad");
		detalle.addCell("Producto");
		detalle.addCell("Precio");
		detalle.addCell("Total");
		
		for (ItemFactura item : factura.getItems()) {
			
			celda = new PdfPCell(new Phrase(item.getCantidad().toString()));
			celda.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			detalle.addCell(celda);
			detalle.addCell(item.getProducto().getNombre());
			detalle.addCell(item.getProducto().getPrecio().toString());
			detalle.addCell(item.calcularSubTotal().toString());
			
		}
		
		PdfPCell footerTotal = new PdfPCell(new Phrase("Total: "));
		footerTotal.setColspan(3);
		footerTotal.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		detalle.addCell(footerTotal);
		detalle.addCell(factura.getTotal().toString());
		
		document.add(cabecera);
		document.add(tabla);
		document.add(detalle);
		
	}

}
