package com.company.Onix.Modelos;

public class mi_servicio {

    String direccion;
    String nombre;
    String nota;
    double lat;
    double lng;





    public mi_servicio() {
    }

    public mi_servicio(String direccion, String nombre, String nota, double lat, double lng) {
        this.direccion = direccion;
        this.nombre = nombre;
        this.nota = nota;
        this.lat = lat;
        this.lng = lng;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}