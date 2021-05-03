package com.example.proiectmaster;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AdaugaActivitateActivity extends AppCompatActivity {
    Button btnSalveazaActivitate;
    EditText dataEditText, durataEditText, activitateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adauga_activitate);

        dataEditText = findViewById(R.id.dataEditText);
        String selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        dataEditText.setText(selectedDate);

        activitateEditText = findViewById(R.id.activitateEditText);
        String tipActivitate = getIntent().getStringExtra("TIP_ACTIVITATE");
        activitateEditText.setText(tipActivitate);

        durataEditText = findViewById(R.id.durataEditText);
        String durata = getIntent().getStringExtra("DURATA");
        durataEditText.setText(durata);

        btnSalveazaActivitate = findViewById(R.id.btnSalveazaActivitate);
//        btnSalveazaActivitate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }
}
