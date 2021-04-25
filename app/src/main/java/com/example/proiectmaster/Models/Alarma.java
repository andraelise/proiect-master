package com.example.proiectmaster.Models;

import java.io.Serializable;
import java.util.Date;

public class Alarma implements Serializable {

    private String parametru, text;
    private Date data = null;
    private double valMinima, valMaxima, valActuala;

    public Alarma() {

    }

    public Alarma(String parametru, Date data, double valMinima, double valMaxima, double valActuala, String text) {
        this.parametru = parametru;
        this.data = data;
        this.valMinima = valMinima;
        this.valMaxima = valMaxima;
        this.valActuala = valActuala;
        this.text = text;
    }

    public void setParametru(String parametru) {
        this.parametru = parametru;
    }

    public String getParametru() {
        return parametru;
    }

    public void setData(Date data) { this.data = data; }

    public Date getData() { return data; }

    public void setValMinima(double valMinima) {
        this.valMinima = valMinima;
    }

    public double getValMinima() {
        return valMinima;
    }

    public void setValMaxima(double valMaxima) {
        this.valMaxima = valMaxima;
    }

    public double getValMaxima() {
        return valMaxima;
    }

    public void setValActuala(double valActuala) {
        this.valActuala = valActuala;
    }

    public double getValActuala() {
        return valActuala;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}