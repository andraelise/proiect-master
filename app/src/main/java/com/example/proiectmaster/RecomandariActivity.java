package com.example.proiectmaster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    public void clickedRecomandare(View view) {

        RelativeLayout relativeLayout = (RelativeLayout) view.getParent();
        LinearLayout linearLayout1 = (LinearLayout) relativeLayout.getParent();
        LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getParent();
        // LinearLayout linearLayout1 = parent.findViewById(R.id.recomandare_info_row);
        TextView tipActivitateTxt = linearLayout2.findViewById(R.id.activitateTxt);
        TextView durataTxt = linearLayout2.findViewById(R.id.durataValTxt);

        String tipActivitate = tipActivitateTxt.getText().toString();
        String durata = durataTxt.getText().toString();

        Intent intent = new Intent(RecomandariActivity.this, AdaugaActivitateActivity.class);
        intent.putExtra("TIP_ACTIVITATE", tipActivitate);
        intent.putExtra("DURATA", durata);
        startActivity(intent);
    }
}
