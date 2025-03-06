package isi.dan.msclientes.servicios;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isi.dan.msclientes.dao.ClienteRepository;
import isi.dan.msclientes.exception.UsuarioHabilitadoNotFoundException;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.EstadoObra;
import isi.dan.msclientes.model.Obra;
import isi.dan.msclientes.model.UsuarioHabilitado;

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioHabilitadoService usuarioHabilitadoService;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Integer id) {
        return clienteRepository.findById(id);
    }

    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente update(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void deleteById(Integer id) {
        clienteRepository.deleteById(id);
    }

    public BigDecimal getDescubierto(Cliente cliente) {
        return cliente.getObras().stream()
                .filter(obra -> obra.getEstado() == EstadoObra.HABILITADA)
                .map(Obra::getPresupuesto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Cliente asociarUsuarioHabilitado(Integer idCliente, Integer idUsuarioHabilitado) throws UsuarioHabilitadoNotFoundException {
        Cliente cliente = clienteRepository.findById(idCliente).orElseThrow();
        UsuarioHabilitado usuarioHabilitado = usuarioHabilitadoService
                                            .findById(idUsuarioHabilitado)
                                            .orElseThrow(() -> new UsuarioHabilitadoNotFoundException("Usuario habilitado " + idUsuarioHabilitado + " no encontrado"));
        if(!cliente.getUsuariosHabilitados().contains(usuarioHabilitado)){
            cliente.getUsuariosHabilitados().add(usuarioHabilitado);
        }
        return clienteRepository.save(cliente);
    }
    
}
