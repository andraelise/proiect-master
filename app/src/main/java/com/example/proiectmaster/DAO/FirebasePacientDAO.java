package com.example.proiectmaster.DAO;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebasePacientDAO implements PacientDAO {
    private static final String TAG = "FirebasePacientDAO";
    private FirebaseFirestore db;
    private CollectionReference pacientiRef;

    private void connect() {
        db = FirebaseFirestore.getInstance();
        pacientiRef = db.collection("pacienti");
        Log.d(TAG, "Connected successfully!");
    }
}
