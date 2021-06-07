package com.example.proiectmaster.adapters;

import com.robinhood.spark.SparkAdapter;

import java.util.ArrayList;

public class ECGAdapter extends SparkAdapter {
    private ArrayList _ecgValues;

    public ECGAdapter(ArrayList ecgValues)
    {
        _ecgValues = ecgValues;
    }

    @Override
    public int getCount() {
        return _ecgValues.size();
    }

    @Override
    public Object getItem(int index) {
        return _ecgValues.get(index);
    }

    @Override
    public float getY(int index) {
        return (float)_ecgValues.get(index);
    }
}
