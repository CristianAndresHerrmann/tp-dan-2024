package isi.dan.ms_productos.modelo;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "MS_PRD_ORDEN_COMPRA")
@Data
public class OrdenCompra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @NotNull
    private Instant fecha;

    @NotNull
    private Integer numeroPedido;

    @NotNull
    private String user;

    @NotNull
    private String observaciones;

    @NotNull
    private Cliente cliente;

    @NotNull
    private Obra obra;

    @NotNull
    private List<HistorialEstado> estados;
    
    @NotNull
    private EstadoPedido estado;

    @NotNull
    private Double total;
    
}
