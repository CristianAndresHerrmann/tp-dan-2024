package isi.dan.ms_productos.modelo;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "MS_PRD_ORDEN_COMPRA_DETALLE")
@Data
public class OrdenCompraDelle {
    private Producto producto;
    private int cantidad;
    private double descuento;
    private double total;
}
