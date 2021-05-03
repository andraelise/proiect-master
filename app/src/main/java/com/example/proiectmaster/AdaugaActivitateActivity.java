package com.example.proiectmaster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proiectmaster.Models.Eveniment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AdaugaActivitateActivity extends AppCompatActivity {
    Button btnSalveazaActivitate;
    EditText dataEditText, durataEditText, activitateEditText, oraStartEditText;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser user;
    private FirebaseFirestore db;
    private static final String TAG = "AdaugaEvenimentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adauga_activitate);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        dataEditText = findViewById(R.id.dataEditText);
        String selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        dataEditText.setText(selectedDate);

        activitateEditText = findViewById(R.id.activitateEditText);
        String tipActivitate = getIntent().getStringExtra("TIP_ACTIVITATE");
        activitateEditText.setText(tipActivitate);

        durataEditText = findViewById(R.id.durataEditText);
        String durata = getIntent().getStringExtra("DURATA");
        durataEditText.setText(durata);

        oraStartEditText = findViewById(R.id.oraEditText);

        btnSalveazaActivitate = findViewById(R.id.btnSalveazaActivitate);
        btnSalveazaActivitate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addActivitate();
            }
        });
    }

    public void addActivitate()
    {
        if (!validateForm(activitateEditText.getText().toString(), durataEditText.getText().toString(), dataEditText.getText().toString(), oraStartEditText.getText().toString())) {
            return;
        }
        String activitate = activitateEditText.getText().toString();
        int durata = Integer.parseInt(durataEditText.getText().toString());
        Date dataStart = getDataFromDateAndHour(dataEditText.getText().toString(), oraStartEditText.getText().toString());
        Eveniment eveniment = new Eveniment(activitate, durata, dataStart);
        addEveniment(eveniment, user.getUid());
    }

    public Date getDataFromDateAndHour(String dataTxt, String oraTxt)
    {
        String hourString = oraTxt.substring(0,2);
        String minuteString = oraTxt.substring(3);
        if (hourString.startsWith("0"))
            hourString = hourString.substring(1);
        if (minuteString.startsWith("0"))
            minuteString = minuteString.substring(1);
        int hour = Integer.parseInt(hourString);
        int minute = Integer.parseInt(minuteString);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = sdf.parse(dataTxt);
        }
        catch(Exception e)
        {

        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        System.out.println(calendar.getTime());
        return calendar.getTime();
    }

    public boolean validateForm(String activitate, String durata, String data, String ora)
    {
        if (activitate.isEmpty() && durata.isEmpty() && data.isEmpty() && ora.isEmpty())
        {
            Toast.makeText(AdaugaActivitateActivity.this, "Datele evenimentului nu sunt completate!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (activitate.isEmpty())
        {
            activitateEditText.setError("Introdu numele activitatii!");
            activitateEditText.requestFocus();
            return false;
        }
        else if (durata.isEmpty())
        {
            durataEditText.setError("Introdu durata activitatii!");
            durataEditText.requestFocus();
            return false;
        }
        else if (data.isEmpty())
        {
            dataEditText.setError("Introdu durata activitatii!");
            dataEditText.requestFocus();
            return false;
        }
        else if (ora.isEmpty())
        {
            oraStartEditText.setError("Introdu durata activitatii!");
            oraStartEditText.requestFocus();
            return false;
        }
        return true;
    }

    private void connect() {
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Connected successfully!");
    }

    private void addEveniment(Eveniment eveniment, String uid) {
        connect();
        DocumentReference pacientRef = db.collection("pacienti").document(uid);
        pacientRef.collection("evenimente").add(eveniment)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "Evenimentul adaugat in baza de date cu ID: " + documentReference.getId());
                    Toast.makeText(AdaugaActivitateActivity.this, "Evenimentul a fost adaugat cu succes!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdaugaActivitateActivity.this, CalendarActivity.class);
                    startActivity(intent);
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
