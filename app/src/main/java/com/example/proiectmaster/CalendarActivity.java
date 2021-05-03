package com.example.proiectmaster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.proiectmaster.Models.Eveniment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    CalendarView mCalendarView;
    TextView myDate, txtData;
    ListView listView;
    Button btnAdaugaActivitate;
    String selectedDate;
    private EvenimenteAdapter evenimenteAdapter;
    private ArrayList<Eveniment> evenimenteList = new ArrayList<>();
    // private ArrayList<Date> highlightedDates = new ArrayList<>();
    private List<Calendar> calendars = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseUser user;
    private static final String TAG = "CalendarActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        myDate = findViewById(R.id.myDate);
        txtData = findViewById(R.id.txtData);
        myDate.setVisibility(View.INVISIBLE);
        txtData.setVisibility(View.INVISIBLE);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        listView = findViewById(R.id.evenimenteListView);
        btnAdaugaActivitate = findViewById(R.id.btnAdaugaActivitate);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        getDateProgramate(uid);

        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

                myDate.setVisibility(View.INVISIBLE);
                txtData.setVisibility(View.INVISIBLE);
                evenimenteList.clear();
                listView.setAdapter(null);

                Calendar calendar = eventDay.getCalendar();
                Date data = calendar.getTime();
                final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                selectedDate = sdf.format(data);
                myDate.setText(selectedDate);

                getEvenimente(data, user.getUid());
            }
        });

        btnAdaugaActivitate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, AdaugaActivitateActivity.class);
                if (selectedDate != "") {
                    intent.putExtra("SELECTED_DATE", selectedDate);
                }
                startActivity(intent);
            }
        });
    }

    private void displayList() {
        if (evenimenteList.isEmpty())
            txtData.setText("Nu exista activitati in ");
        else
            txtData.setText("Activitati in ");
        myDate.setVisibility(View.VISIBLE);
        txtData.setVisibility(View.VISIBLE);

        evenimenteAdapter = new EvenimenteAdapter(this, evenimenteList);
        listView.setAdapter(evenimenteAdapter);
    }

    private void connect() {
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Connected successfully!");
    }

    public void getDateProgramate(String uid) {
        connect();
        db.collection("pacienti").document(uid).collection("evenimente").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Date data = document.getDate("dataStart");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(data);
                        calendars.add(calendar);
                        // highlightedDates.add(data);
                    }
                    highlightDates();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void highlightDates()
    {
        mCalendarView.setHighlightedDays(calendars);
        for (Calendar calendar : calendars)
        {
            try { mCalendarView.setDate(calendar);
            } catch(Exception e) { }
        }

        try { mCalendarView.setDate(new Date());
        } catch(Exception e) { }
    }

    public void getEvenimente(Date date, String uid) {
        connect();
        Date startTime = getStartTime(date);
        Log.i(TAG, "startTime:" + startTime.toString());
        Date endTime = getEndTime(date);
        Log.i(TAG, "endTime:" + endTime.toString());

        db.collection("pacienti").document(uid).collection("evenimente").whereGreaterThanOrEqualTo("dataStart", startTime).whereLessThanOrEqualTo("dataStart", endTime).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Eveniment eveniment = document.toObject(Eveniment.class);
                        String ev = document.getString("eveniment");
                        Log.i(TAG, "eveniment:" + ev);
                        evenimenteList.add(eveniment);
                    }
                    displayList();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public Date getStartTime(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public Date getEndTime(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }
}
