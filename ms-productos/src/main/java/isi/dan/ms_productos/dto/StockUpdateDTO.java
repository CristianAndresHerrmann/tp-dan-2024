package isi.dan.ms_productos.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockUpdateDTO {

    @NotNull(message = "El id del producto no puede ser nulo")
    private Long idProducto;

    @NotNull(message = "La cantidad no puede ser nula")
    @Min(value = 0, message = "La cantidad no puede ser menor a 0")
    private Integer cantidad;

    @NotNull(message = "El precio no puede ser nulo")
    @Min(value = 0, message = "El precio no puede ser menor a 0")
    private BigDecimal precio;
}
