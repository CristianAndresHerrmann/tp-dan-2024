package isi.dan.ms.pedidos.modelo;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Document(collection = "pedidos")
@Data
public class Pedido {
    @Id
    private String id;
    private Instant fecha;
    private Integer numeroPedido;
    private String usuario;
    private String observaciones;

    private Cliente cliente;
    private Obra obra;
    private List<HistorialEstado> estados = new ArrayList<>();
    private EstadoPedido estado;
    private BigDecimal total;

    @Field("detalle")
    private List<DetallePedido> detalle;

}

