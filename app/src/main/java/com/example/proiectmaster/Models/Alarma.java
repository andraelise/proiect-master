package com.example.proiectmaster.Models;

import java.io.Serializable;
import java.util.Date;

public class Alarma implements Serializable {

    private String parametru;
    private Date data = null;
    private int valMinima, valMaxima, valActuala;

    public Alarma() {

    }

    public Alarma(String parametru, Date data, int valMinima, int valMaxima, int valActuala) {
        this.parametru = parametru;
        this.data = data;
        this.valMinima = valMinima;
        this.valMaxima = valMaxima;
        this.valActuala = valActuala;
    }

    public void setParametru(String parametru) {
        this.parametru = parametru;
    }

    public String getParametru() {
        return parametru;
    }

    public void setData(Date data) { this.data = data; }

    public Date getData() { return data; }

    public void setValMinima(int valMinima) {
        this.valMinima = valMinima;
    }

    public int getValMinima() {
        return valMinima;
    }

    public void setValMaxima(int valMaxima) {
        this.valMaxima = valMaxima;
    }

    public int getValMaxima() {
        return valMaxima;
    }

    public void setValActuala(int valActuala) {
        this.valActuala = valActuala;
    }

    public int getValActuala() {
        return valActuala;
    }
}