package com.example.proiectmaster;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proiectmaster.Models.Recomandare;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RecomandariActivity extends AppCompatActivity {
    private ListView listView;
    private RecomandariAdapter recomandariAdapter;
    private ArrayList<Recomandare> recomandariList = new ArrayList<>();
    private FirebaseFirestore db;
    private static final String TAG = "RecomandariActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomandari);
        getSupportActionBar().setTitle("Recomandari");

        listView = findViewById(R.id.recomandariListView);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            getRecomandari(uid);
        }
    }

    private void displayList() {
        recomandariAdapter = new RecomandariAdapter(this, recomandariList);
        listView.setAdapter(recomandariAdapter);
    }

    private void connect() {
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Connected successfully!");
    }

    public void getRecomandari(String uid) {
        connect();
        db.collection("pacienti").document(uid).collection("recomandari").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recomandare recomandare = document.toObject(Recomandare.class);
                        recomandariList.add(recomandare);
                    }
                    displayList();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
