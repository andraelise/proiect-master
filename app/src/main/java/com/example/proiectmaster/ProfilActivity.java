package com.example.proiectmaster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfilActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnLogout, btnResetParola;
    TextView numePacient, numeMedic, emailPacient;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseFirestore db;
    private static final String TAG = "ProfilActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        getSupportActionBar().setTitle("Profilul meu");

        numePacient = findViewById(R.id.txtNumeUtilizator);
        numeMedic = findViewById(R.id.txtMedic);
        emailPacient = findViewById(R.id.txtEmail);

        btnLogout = findViewById(R.id.btnLogout);
        btnResetParola = findViewById(R.id.btnResetParola);
        btnLogout.setOnClickListener(this);
        btnResetParola.setOnClickListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            getDatePacient(uid);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfilActivity.this, RegisterActivity.class));
                break;
            case R.id.btnResetParola:
                String email = emailPacient.getText().toString();
                if (!email.isEmpty()) {
                    sendResetEmail(email);
                }
            default:
                break;
        }
    }

    private void connect() {
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Connected successfully!");
    }

    private void getDatePacient(String uid) {
        connect();
        DocumentReference pacientRef = db.collection("pacienti").document(uid);
        pacientRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String nume = document.getString("nume");
                    String prenume = document.getString("prenume");
                    String email = document.getString("email");
                    String uidMedic = document.getString("medic");
                    getMedic(uidMedic);
                    displayDatePacient(nume, prenume, email);
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void getMedic(String uid) {
        connect();
        DocumentReference medicRef = db.collection("medici").document(uid);
        medicRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String nume = document.getString("nume");
                    String prenume = document.getString("prenume");
                    displayNumeMedic(nume, prenume);
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void displayDatePacient(String nume, String prenume, String email)
    {
        numePacient.setText(prenume + " " + nume);
        emailPacient.setText(email);
    }

    private void displayNumeMedic(String nume, String prenume)
    {
        numeMedic.setText(prenume + " " + nume);
    }

    private void sendResetEmail(String emailAddress)
    {
        mFirebaseAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfilActivity.this, "Verifica-ti email-ul pentru a reseta parola!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfilActivity.this, "Eroare la trimiterea email-ului de resetare a parolei!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
