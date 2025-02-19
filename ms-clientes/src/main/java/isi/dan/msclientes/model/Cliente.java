package isi.dan.msclientes.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "MS_CLI_CLIENTE")
@Data
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(name="CORREO_ELECTRONICO")
    @Email(message = "Email debe ser valido")
    @NotBlank(message = "Email es obligatorio")
    private String correoElectronico;
    
    private String cuit;

    @Column(name="MAXIMO_DESCUBIERTO")
    @Min(value = 0, message = "El maximo descubierto debe ser mayor que cero")
    private BigDecimal maximoDescubierto;

    @Column(name="MAXIMO_CANTIDAD_OBRAS")
    @Min(value = 1, message = "La cantidad de obras activas no puede ser negativa")
    private Integer maximoCantidadObras;

    @OneToMany(mappedBy = "cliente")
    private List<Obra> obras;

    @OneToMany(mappedBy = "cliente")
    private List<UsuarioHabilitado> usuariosHabilitados;
    
}
