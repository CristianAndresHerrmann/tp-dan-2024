package isi.dan.ms_productos.exception;

public class CategoriaNotFoundException extends Exception{
    public CategoriaNotFoundException(Long id) {
        super("Categoria con id " + id + " no encontrada");
    }

}
