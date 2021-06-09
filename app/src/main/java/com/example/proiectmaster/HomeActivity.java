package com.example.proiectmaster;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.proiectmaster.Services.BluetoothService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imgProfil;
    TextView numePacient;
    CardView cardRecomandari, cardCalendar, cardParametrii, cardAlarme;
    private FirebaseFirestore db;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        numePacient = findViewById(R.id.txtNumePacient);
        imgProfil = findViewById(R.id.imgProfil);
        cardRecomandari = findViewById(R.id.cardRecomandari);
        cardCalendar = findViewById(R.id.cardCalendar);
        cardParametrii = findViewById(R.id.cardParametrii);
        cardAlarme = findViewById(R.id.cardAlarme);

        imgProfil.setOnClickListener(this);
        cardRecomandari.setOnClickListener(this);
        cardCalendar.setOnClickListener(this);
        cardParametrii.setOnClickListener(this);
        cardAlarme.setOnClickListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            getPatientName(uid);
        }

        if (BluetoothAdapter.getDefaultAdapter() != null) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                Intent btService = new Intent(this, BluetoothService.class);
                startService(btService);
                Log.d(TAG, "Bluetooth service started!");
            } else {
                Toast.makeText(this, "Please enable bluetooth.", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Log.d(TAG, "BluetoothAdapter is null!");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardRecomandari:
                startActivity(new Intent(HomeActivity.this, RecomandariActivity.class));
                break;
            case R.id.cardCalendar:
                startActivity(new Intent(HomeActivity.this, CalendarActivity.class));
                break;
            case R.id.cardParametrii:
                startActivity(new Intent(HomeActivity.this, ParametriiActivity.class));
                break;
            case R.id.cardAlarme:
                startActivity(new Intent(HomeActivity.this, IstoricAlarmeActivity.class));
                break;
            case R.id.imgProfil:
                startActivity(new Intent(HomeActivity.this, ProfilActivity.class));
                break;
            default:
                break;
        }
    }

    private void connect() {
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Connected successfully!");
    }

    private void getPatientName(String uid) {
        connect();
        DocumentReference pacientRef = db.collection("pacienti").document(uid);
        pacientRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String nume = document.getString("firstName");
                    String prenume = document.getString("lastName");
                    displayPatientName(nume, prenume);
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void displayPatientName(String nume, String prenume)
    {
        numePacient.setText(prenume + " " + nume);
    }
}