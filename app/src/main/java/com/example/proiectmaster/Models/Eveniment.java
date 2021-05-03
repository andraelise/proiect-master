package com.example.proiectmaster.Models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Eveniment implements Serializable {

    private String eveniment;
    private int durata;
    private Date dataStart = null, dataEnd = null;

    public Eveniment() {

    }

    public Eveniment(String eveniment, int durata, Date dataStart) {
        this.eveniment = eveniment;
        this.durata = durata;
        this.dataStart = dataStart;
        dataEnd = calculateDataEnd(dataStart);
    }

    public void setEveniment(String eveniment) {
        this.eveniment = eveniment;
    }

    public String getEveniment() {
        return eveniment;
    }

    public void setDurata(int durata) {
        this.durata = durata;
    }

    public int getDurata() { return durata; }

    public void setDataStart(Date dataStart) {
        this.dataStart = dataStart;
    }

    public Date getDataStart() {
        return dataStart;
    }

    public void setDataEnd(Date dataEnd) {
        this.dataEnd = dataEnd;
    }

    public Date getDataEnd() {
        return dataEnd;
    }

    public Date calculateDataEnd(Date dataStart){
        if(dataStart != null){
            Date auxDate = addMinutesToJavaUtilDate(dataStart, durata);
            return auxDate;
        }
        return null;
    }

    private Date addMinutesToJavaUtilDate(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }
}