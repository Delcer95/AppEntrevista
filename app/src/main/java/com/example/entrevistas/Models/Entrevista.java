package com.example.entrevistas.Models;


public class Entrevista {
    private String idOrden;
    private String Descripcion;
    private String Periodista;
    private String Fecha;
    private String fotoURL;
    Double vaccine_price;



    public Entrevista() {
    }

    public Entrevista(String idOrden, String descripcion, String periodista, String fecha, String fotoURL, Double vaccine_price) {
        this.idOrden = idOrden;
        Descripcion = descripcion;
        Periodista = periodista;
        Fecha = fecha;
        this.fotoURL = fotoURL;
        this.vaccine_price = vaccine_price;
    }

    public String getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(String idOrden) {
        this.idOrden = idOrden;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getPeriodista() {
        return Periodista;
    }

    public void setPeriodista(String periodista) {
        Periodista = periodista;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getFotoURL() {
        return fotoURL;
    }

    public void setFotoURL(String fotoURL) {
        this.fotoURL = fotoURL;
    }

    public Double getVaccine_price() {
        return vaccine_price;
    }

    public void setVaccine_price(Double vaccine_price) {
        this.vaccine_price = vaccine_price;
    }
}
