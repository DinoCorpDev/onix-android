package com.company.Onix.Modelos;

public class mi_servicio_tres {

    String foto;
    String km;
    String minutos;
    String nombre;
    int tarifa_conductor;

    public mi_servicio_tres() {
    }

    public mi_servicio_tres(String foto, String km, String minutos, String nombre, int tarifa_conductor) {
        this.foto = foto;
        this.km = km;
        this.minutos = minutos;
        this.nombre = nombre;
        this.tarifa_conductor = tarifa_conductor;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getMinutos() {
        return minutos;
    }

    public void setMinutos(String minutos) {
        this.minutos = minutos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTarifa_conductor() {
        return tarifa_conductor;
    }

    public void setTarifa_conductor(int tarifa_conductor) {
        this.tarifa_conductor = tarifa_conductor;
    }
}
