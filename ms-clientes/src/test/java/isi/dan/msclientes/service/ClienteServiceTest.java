package isi.dan.msclientes.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import isi.dan.msclientes.dao.ClienteRepository;
import isi.dan.msclientes.dao.ObraRepository;
import isi.dan.msclientes.exception.UsuarioHabilitadoNotFoundException;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.EstadoObra;
import isi.dan.msclientes.model.Obra;
import isi.dan.msclientes.model.UsuarioHabilitado;
import isi.dan.msclientes.servicios.ClienteService;
import isi.dan.msclientes.servicios.UsuarioHabilitadoService;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private UsuarioHabilitadoService usuarioHabilitadoService;

    @Mock
    private ObraRepository obraRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;
    private List<Obra> obras;


    @BeforeEach
    void iniciarDatos() {
        cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombre("Cliente Test");
        cliente.setCorreoElectronico("cliente@test.com");
        cliente.setMaximoDescubierto(BigDecimal.valueOf(100000));
        cliente.setMaximoCantidadObras(2);
        cliente.setObras(new ArrayList<>());
        cliente.setUsuariosHabilitados(new ArrayList<>());
    }

    @Test
    void testGetDescubierto_SinObras() {
        // Cuando no hay obras
        BigDecimal descubierto = clienteService.getDescubierto(cliente);
        
        assertEquals(BigDecimal.ZERO, descubierto);
    }

    @Test
    void testGetDescubierto_ConObrasHabilitadas() {
        // Preparar obras
        Obra obra1 = new Obra();
        obra1.setEstado(EstadoObra.HABILITADA);
        obra1.setPresupuesto(BigDecimal.valueOf(50000));

        Obra obra2 = new Obra();
        obra2.setEstado(EstadoObra.HABILITADA);
        obra2.setPresupuesto(BigDecimal.valueOf(30000));

        cliente.getObras().add(obra1);
        cliente.getObras().add(obra2);

        // Calcular descubierto
        BigDecimal descubierto = clienteService.getDescubierto(cliente);
        
        assertEquals(BigDecimal.valueOf(80000), descubierto);
    }

    @Test
    void testGetDescubierto_ConObrasPendientes() {
        // Preparar obras 
        Obra obra1 = new Obra();
        obra1.setEstado(EstadoObra.PENDIENTE);
        obra1.setPresupuesto(BigDecimal.valueOf(50000));

        Obra obra2 = new Obra();
        obra2.setEstado(EstadoObra.PENDIENTE);
        obra2.setPresupuesto(BigDecimal.valueOf(30000));

        cliente.getObras().add(obra1);
        cliente.getObras().add(obra2);

        BigDecimal descubierto = clienteService.getDescubierto(cliente);
        
        assertEquals(BigDecimal.ZERO, descubierto);
    }

    @Test
    void testAsociarUsuarioHabilitado_Exitoso() throws UsuarioHabilitadoNotFoundException {
        UsuarioHabilitado usuarioHabilitado = new UsuarioHabilitado();
        usuarioHabilitado.setId(1);

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(usuarioHabilitadoService.findById(1)).thenReturn(Optional.of(usuarioHabilitado));
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        // Ejecutar método
        Cliente resultado = clienteService.asociarUsuarioHabilitado(1, 1);

        // Verificaciones
        assertNotNull(resultado);
        assertTrue(resultado.getUsuariosHabilitados().contains(usuarioHabilitado));
        verify(clienteRepository).save(cliente);
    }

    @Test
    void testAsociarUsuarioHabilitado_UsuarioNoEncontrado() {
        // Configurar mocks
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(usuarioHabilitadoService.findById(1)).thenReturn(Optional.empty());

        // Verificar excepción
        assertThrows(UsuarioHabilitadoNotFoundException.class, () -> {
            clienteService.asociarUsuarioHabilitado(1, 1);
        });
    }

    @Test
    void testAsociarUsuarioHabilitado_UsuarioYaAsociado() throws UsuarioHabilitadoNotFoundException {
        // Preparar usuario habilitado
        UsuarioHabilitado usuarioHabilitado = new UsuarioHabilitado();
        usuarioHabilitado.setId(1);
        cliente.getUsuariosHabilitados().add(usuarioHabilitado);

        // Configurar mocks
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(usuarioHabilitadoService.findById(1)).thenReturn(Optional.of(usuarioHabilitado));
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        // Ejecutar método
        Cliente resultado = clienteService.asociarUsuarioHabilitado(1, 1);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(1, resultado.getUsuariosHabilitados().size());
        verify(clienteRepository).save(cliente);
    }

}
