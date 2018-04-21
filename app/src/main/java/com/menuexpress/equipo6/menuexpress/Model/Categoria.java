package com.menuexpress.equipo6.menuexpress.Model;

public class Categoria {
    private String id;
    private String nombre;
    private String imagen;

    public Categoria() {
    }

    public Categoria(String nombre, String imagen) {
        this.nombre = nombre;
        this.imagen = imagen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
