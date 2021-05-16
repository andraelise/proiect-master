package com.example.proiectmaster;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proiectmaster.Models.Alarma;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class IstoricAlarmeActivity extends AppCompatActivity {
    private ListView listView;
    private IstoricAlarmeAdapter istoricAlarmeAdapter;
    private ArrayList<Alarma> alarmeList = new ArrayList<>();
    private FirebaseFirestore db;
    private static final String TAG = "IstoricAlarmeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_istoric_alarme);
        getSupportActionBar().setTitle("Istoric alarme");

        listView = findViewById(R.id.alarmeListView);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            getAlarme(uid);
        }
    }

    private void displayList() {
//        alarmeList.add(new Alarma("Temperatura", new Date(), 35, 36, 37, "Text alarma"));
//        alarmeList.add(new Alarma("ECG", new Date(), 100, 120, 130, "Text alarma"));
//        alarmeList.add(new Alarma("Puls", new Date(), 60, 90, 120, "Text alarma"));
//        alarmeList.add(new Alarma("Umiditate", new Date(), 30, 50, 60, "Text alarma"));
        istoricAlarmeAdapter = new IstoricAlarmeAdapter(this, alarmeList);
        listView.setAdapter(istoricAlarmeAdapter);
    }

    private void connect() {
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Connected successfully!");
    }

    public void getAlarme(String uid) {
        connect();
        db.collection("pacienti").document(uid).collection("alarme").orderBy("data", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Alarma alarma = document.toObject(Alarma.class);
                        alarmeList.add(alarma);
                    }
                    displayList();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
