package com.company.Onix.Modelos;

public class mi_servicio_dos {

    String nombre_cliente;
    String destino;
    String direccion;
    String id_cliente;
    String pago;


    public mi_servicio_dos(){

    }

    public mi_servicio_dos(String nombre_cliente, String destino, String direccion, String id_cliente, String pago) {
        this.nombre_cliente = nombre_cliente;
        this.destino = destino;
        this.direccion = direccion;
        this.id_cliente = id_cliente;
        this.pago = pago;
    }

    public String getNombre_cliente() {
        return nombre_cliente;
    }

    public void setNombre_cliente(String nombre_cliente) {
        this.nombre_cliente = nombre_cliente;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getPago() {
        return pago;
    }

    public void setPago(String pago) {
        this.pago = pago;
    }
}
