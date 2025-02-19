package isi.dan.msclientes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "MS_CLI_USUARIO_HABILITADO")
@Data
public class UsuarioHabilitado {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "Apellido es obligatorio")
    private String apellido;

    @Column(name="DNI")
    @NotBlank(message = "DNI es obligatorio")
    private String dni;

    @Column(name="CORREO_ELECTRONICO")
    @NotNull(message = "Email es obligatorio")
    @Email(message = "Email debe ser valido")
    private String correoElectronico;
}
