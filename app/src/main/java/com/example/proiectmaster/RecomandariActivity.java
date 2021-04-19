package com.example.proiectmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.proiectmaster.Models.Recomandare;
import com.example.proiectmaster.Utils.RecomandariAdapter;

import java.util.ArrayList;

public class RecomandariActivity extends AppCompatActivity {
//    private ListView listView;
//    private RecomandariAdapter recomandariAdapter;
//    private ArrayList<Recomandare> recomandariList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomandari);
        getSupportActionBar().setTitle("Recomandari");

//        listView = findViewById(R.id.recomandariListView);
//        displayList();
    }

//    private void displayList() {
//        recomandariList.add(new Recomandare("Bicicleta", 20, 3, "Pastrati pulsul sub 90."));
//        recomandariList.add(new Recomandare("Mers", 30, 4, "Plimbati-va 2 kilometri."));
//        recomandariList.add(new Recomandare("Alergat", 10, 1, "Alergati in ritm usor."));
//
//        RecomandariAdapter = new RecomandariAdapter(this, recomandariList);
//        listView.setAdapter((ListAdapter) recomandariList);
//    }
}
