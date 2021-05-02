package com.example.proiectmaster;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proiectmaster.Models.Alarma;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ParametriiActivity extends AppCompatActivity {
    Alarma alarma = null;
    Button btnAlarma;
    Dialog dialogAlarma;
    TextView txtAlarma, txtValNormale, txtValActuala;
    EditText comentarii;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser user;
    private FirebaseFirestore db;
    private static final String TAG = "ParametriiActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametrii);
        getSupportActionBar().setTitle("Parametrii");

        btnAlarma = findViewById(R.id.btnAlarma);
        dialogAlarma = new Dialog(this);

        alarma = new Alarma("Temperatura", new Date(), 35, 37, 38.5, "");

        btnAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alarma != null) {
                    openDialogAlarma(alarma);
                }
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void openDialogAlarma(Alarma alarma) {
        final Alarma alarm = alarma;
        dialogAlarma.setContentView(R.layout.alarma);
        dialogAlarma.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imageViewClose = dialogAlarma.findViewById(R.id.imageViewClose);
        Button btnOK = dialogAlarma.findViewById(R.id.btnOK);
        txtAlarma = dialogAlarma.findViewById(R.id.txtAlarma);
        txtValNormale = dialogAlarma.findViewById(R.id.txtValNormale);
        txtValActuala = dialogAlarma.findViewById(R.id.txtValActuala);
        comentarii = dialogAlarma.findViewById(R.id.editTextComentarii);
        setTextAlarma(alarma);

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAlarma.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.setText(comentarii.getText().toString());
                addAlarma(alarm, user.getUid());
                dialogAlarma.dismiss();
            }
        });

        dialogAlarma.show();
    }

    private void setTextAlarma(Alarma alarma) {
        String parametru = alarma.getParametru();
        Date data = alarma.getData();
        double valMinima = alarma.getValMinima();
        double valMaxima = alarma.getValMaxima();
        double valActuala = alarma.getValActuala();

        String unitateMasura = getUnitateMasura(parametru);

        String textAlarma = String.format("Valorile normale pentru %s au fost depasite la data %s.", parametru.toUpperCase(), formatDate(data));
        String textValNormale = String.format("Valori normale: %s - %s %s", Double.toString(valMinima), Double.toString(valMaxima), unitateMasura);
        String textValActuala = String.format("Valoare actuala: %s %s", Double.toString(valActuala), unitateMasura);

        txtAlarma.setText(textAlarma);
        txtValNormale.setText(textValNormale);
        txtValActuala.setText(textValActuala);
    }

    private String getUnitateMasura(String parametru) {
        switch (parametru) {
            case "Temperatura":
                return "°C";
            case "ECG":
                return "";
            case "Puls":
                return "bpm";
            case "Umiditate":
                return "%";
            default:
                return "";
        }
    }

    private String formatDate(Date data)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(data);
    }

    private void connect() {
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Connected successfully!");
    }

    private void addAlarma(Alarma alarma, String uid) {
        connect();
        DocumentReference pacientRef = db.collection("pacienti").document(uid);
        pacientRef.collection("alarme").add(alarma)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "Alarma adaugata in baza de date cu ID: " + documentReference.getId());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
                }
            });
    }
}
