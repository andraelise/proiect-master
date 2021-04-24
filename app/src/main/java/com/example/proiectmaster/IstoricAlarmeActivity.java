package com.example.proiectmaster;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proiectmaster.Models.Alarma;

import java.util.ArrayList;
import java.util.Date;

public class IstoricAlarmeActivity extends AppCompatActivity {
    private ListView listView;
    private IstoricAlarmeAdapter istoricAlarmeAdapter;
    private ArrayList<Alarma> alarmeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_istoric_alarme);
        getSupportActionBar().setTitle("Istoric alarme");

        listView = findViewById(R.id.alarmeListView);
        displayList();
    }

    private void displayList() {
        alarmeList.add(new Alarma("Temperatura", new Date(), 35, 36, 37));
        alarmeList.add(new Alarma("ECG", new Date(), 100, 120, 130));
        alarmeList.add(new Alarma("Puls", new Date(), 60, 90, 120));
        alarmeList.add(new Alarma("Umiditate", new Date(), 30, 50, 60));

        istoricAlarmeAdapter = new IstoricAlarmeAdapter(this, alarmeList);
        listView.setAdapter(istoricAlarmeAdapter);
    }
}
