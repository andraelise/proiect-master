package com.example.proiectmaster;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proiectmaster.Models.Alarma;
import com.example.proiectmaster.Services.BluetoothService;
import com.example.proiectmaster.adapters.ECGAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robinhood.spark.SparkView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    BluetoothAdapter bluetoothAdapter;

    private double minTemp, maxTemp, minUmid, maxUmid, minPuls, maxPuls;
    private static final String TAG = "ParametriiActivity";

    // variables used for binding bluetooth service to this activity
    BluetoothService mBtService;
    boolean mBound = false;
    private Thread hardwareThread;

    // UI text views for sensor values
    TextView txtParamTemp;
    TextView txtParamHumidity;
    TextView txtParamPulse;
    TextView txtParamECG;
    SparkView ecgSparkView;

    // ECG array from firebase
    ArrayList<Float> _receivedValues;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG,"Bind connected!");
            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) iBinder;
            mBtService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG,"Bind disconnected!");
            mBound = false;
        }
    };

    /* Thread class for receiving hardware values frm service */
    public class HardwareThread extends Thread {
        @Override
        public synchronized void start() {
            super.start();
            Log.d(TAG, "Started getting values...");
        }

        @Override
        public void run() {
            while (true) {
                if (bluetoothAdapter != null) {
                    if (mBtService != null && bluetoothAdapter.isEnabled()) {
                        String sensorValues = mBtService.getSensorsValues();
                        Log.d(TAG, "Sensor values: " + sensorValues);
                        if (!sensorValues.equals("")) {
                            String[] splitValues = sensorValues.split(";");
                            // humidity; temp; pulse; ECG
                            try {
                                txtParamHumidity.setText(splitValues[0]);
                                txtParamTemp.setText(splitValues[1]);
                                txtParamPulse.setText(splitValues[2]);
                                txtParamECG.setText(splitValues[3]);
                                // TODO: ECG value must be added when tested together

                                // Check parameters and create alarm if limits are exceeded
                                checkParams(splitValues);
                            } catch (Exception ex) {
                                Log.e(TAG, "Error caught while splitting sensor values", ex);
                            }
                        }
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "BluetoothAdapter is null!");
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametrii);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        readParamLimits(user.getUid());

        // initialize db and auth
        db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        _receivedValues = new ArrayList();
        //txtParamECG = findViewById(R.id.txt_params_ecg);
        txtParamHumidity = findViewById(R.id.txt_params_humidity);
        txtParamPulse = findViewById(R.id.txt_params_pulse);
        txtParamTemp = findViewById(R.id.txt_params_temp);

        btnAlarma = findViewById(R.id.btnAlarma);
        dialogAlarma = new Dialog(this);

        alarma = new Alarma("Temperatura", new Date(), 35, 37, 38.5, "");

        ecgSparkView = findViewById(R.id.sv_ecg_values);
        try {
            getECGValues();
        } catch (Exception ex)
        {
            Log.d(TAG,"Exception caught",ex);
        }
        btnAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alarma != null) {
                    openDialogAlarma(alarma);
                }
            }
        });

        if (BluetoothAdapter.getDefaultAdapter() != null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            // get values from service on separate thread
            if (bluetoothAdapter.isEnabled()) {
                HardwareThread getHardwareValuesThread = new HardwareThread();
                getHardwareValuesThread.start();
            } else {
                Toast.makeText(this, "Porneste bluetooth pentru monitorizare!", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Log.d(TAG, "BluetoothAdapter is null!");
        }
    }

    private void getECGValues()
    {
        db.collection("pacienti").document(mFirebaseAuth.getUid()).collection("parametri")
                .document("ECG").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> receivedValues = (ArrayList)documentSnapshot.get("valori");

                        for(String item : receivedValues)
                        {
                            _receivedValues.add(Float.parseFloat(item.split(";")[1]));
                        }
                        ecgSparkView.setAdapter(new ECGAdapter(_receivedValues));
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failed to retrieve ECG values",e);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // bind to bluetooth service
        if (bluetoothAdapter != null) {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                Intent bindIntent = new Intent(this, BluetoothService.class);
                bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);
            }
        }
        else
        {
            Log.d(TAG, "BluetoothAdapter is null!");
        }
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent serviceIntent = new Intent(this,BluetoothService.class);
        stopService(serviceIntent);
  
    private void readParamLimits(String uid)
    {
        connect();
        CollectionReference parametriRef = db.collection("pacienti").document(uid).collection("parametri");
        // Puls
        parametriRef.document("Puls").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    minPuls = document.getDouble("valMinima");
                    maxPuls = document.getDouble("valMaxima");
                    Log.d(TAG, "Puls: " + minPuls + " " + maxPuls);
                } else {
                    Log.d(TAG, "Error getting limits for Puls: ", task.getException());
                }
            }
        });

        // Temperatura
        parametriRef.document("Temperatura").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    minTemp = document.getDouble("valMinima");
                    maxTemp = document.getDouble("valMaxima");
                } else {
                    Log.d(TAG, "Error getting limits for Temperatura: ", task.getException());
                }
                Log.d(TAG, "Temperatura: " + minTemp + " " + maxTemp);
            }
        });

        // Umiditate
        parametriRef.document("Umiditate").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    minUmid = document.getDouble("valMinima");
                    maxUmid = document.getDouble("valMaxima");
                } else {
                    Log.d(TAG, "Error getting limits for Umiditate: ", task.getException());
                }
                String[] values = {"27", "50", "80"};
                checkParams(values);
                Log.d(TAG, "Umiditate: " + minUmid + " " + maxUmid);
            }
        });
    }

    private void checkParams(String[] values)
    {
        double temp = Double.parseDouble(values[0]);
        double umid = Double.parseDouble(values[1]);
        double puls = Double.parseDouble(values[2]);

        // Check puls
        if (puls > maxPuls || puls < minPuls)
        {
            Alarma alarmPuls = new Alarma("Puls", new Date(), minPuls, maxPuls, puls, "");
            openDialogAlarma(alarmPuls);
        }

        // Check temperatura
        if (temp > maxTemp || temp < minTemp)
        {
            Alarma alarmTemp = new Alarma("Temperatura", new Date(), minTemp, maxTemp, temp, "");
            openDialogAlarma(alarmTemp);
        }

        // Check umiditate
        if (umid > maxUmid || umid < minUmid)
        {
            Alarma alarmUmid = new Alarma("Umiditate", new Date(), minUmid, maxUmid, umid, "");
            openDialogAlarma(alarmUmid);
        }
    }
}
