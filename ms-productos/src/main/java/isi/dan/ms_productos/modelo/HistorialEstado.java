package isi.dan.ms_productos.modelo;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "MS_PRD_HISTORIAL_ESTADO")
@Data
public class HistorialEstado {
    private EstadoPedido estado;
    private Instant fechaEstado;
    private String userEstado;
    private String detalle;
}
