package com.example.proiectmaster.Models;

import java.io.Serializable;

public class Recomandare implements Serializable {

    private String tipActivitate, indicatii;
    private int durata, frecventa;

    public Recomandare() {

    }

    public Recomandare(String tipActivitate, int durata, int frecventa, String indicatii) {
        this.tipActivitate = tipActivitate;
        this.durata = durata;
        this.frecventa = frecventa;
        this.indicatii = indicatii;
    }

    public void setTipActivitate(String tipActivitate) {
        this.tipActivitate = tipActivitate;
    }

    public String getTipActivitate() {
        return tipActivitate;
    }

    public void setDurata(int durata) {
        this.durata = durata;
    }

    public int getDurata() {
        return durata;
    }

    public void setFrecventa(int frecventa) {
        this.frecventa = frecventa;
    }

    public int getFrecventa() {
        return frecventa;
    }

    public void setIndicatii(String indicatii) {
        this.indicatii = indicatii;
    }

    public String getIndicatii() {
        return indicatii;
    }
}
