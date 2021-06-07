package com.example.proiectmaster.Services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class BluetoothService extends Service {
    private static String TAG = "BluetoothService";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final IBinder binder = new LocalBinder();
    // Bluetooth variables
    public static Handler handler;
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;
    BluetoothAdapter bluetoothAdapter;
    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update

    private String deviceAddress;
    private String hardwareValues;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser user;
    private FirebaseFirestore db;
    CollectionReference parametriRef;

    // hardware thresholds
    static int tempThreshold = 30;
    static int humidityThreshold = 30;
    static int pulseThreshold = 50;
    static int ECGThreshold = 200;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get List of Paired Bluetooth Device
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("HC-05")) {
                    deviceAddress = device.getAddress();
                    break;
                }
            }
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        parametriRef = db.collection("pacienti").document(user.getUid()).collection("parametri");
    }

    public String getSensorsValues()
    {
        if(hardwareValues != null)
        {
            return hardwareValues;
        }
        else
        {
            return "";
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        connectToDevice();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG,msg.toString());
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                Log.d(TAG,"Connected to " + deviceAddress);
                                break;
                            case -1:
                                Log.d(TAG,"Failed to connect!");
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
                        hardwareValues = verifyValues(arduinoMsg);

                        if (!hardwareValues.equals("")) {
                            writeValuesToFirestore(hardwareValues);
                        }
                        Log.d(TAG,"Arduino Message : " + arduinoMsg);
                        break;
                }
            }
        };
        return START_STICKY;
    }

    public synchronized void connectToDevice(){
        Log.d(TAG,"Connecting to device and starting service!");
        createConnectThread = new CreateConnectThread(bluetoothAdapter, deviceAddress);
        createConnectThread.start();
    }

    // this method shall check if the difference between new values and old onse are too big
    public String verifyValues(String rawValues){
        String[] oldHardValues;
        StringBuilder sb = new StringBuilder();
        try {
            String[] newHardValues = rawValues.split(";");
            if (hardwareValues != null)
            {
                oldHardValues = hardwareValues.split(";");
            }
            else
            {
                return rawValues;
            }

            if (!hardwareValues.equals("")) {
                if(Integer.parseInt(oldHardValues[0]) == 0)
                {
                    sb.append(newHardValues[0]).append(";");
                }
                else if (Math.abs(Integer.parseInt(oldHardValues[0]) - Integer.parseInt(newHardValues[0])) < humidityThreshold) {
                    sb.append(newHardValues[0]).append(";");
                } else {
                    Log.d(TAG, "Humidity was to high (spike caught): " + newHardValues[0]);
                    sb.append(oldHardValues[0]);
                }
                if(Integer.parseInt(oldHardValues[1]) == 0)
                {
                    sb.append(newHardValues[1]).append(";");
                }
                else if (Math.abs(Integer.parseInt(oldHardValues[1]) - Integer.parseInt(newHardValues[1])) < tempThreshold) {
                    sb.append(newHardValues[1]).append(";");
                } else {
                    Log.d(TAG, "Temp was to high (spike caught): " + newHardValues[1]);
                    sb.append(oldHardValues[1]);
                }

                if(Double.parseDouble(oldHardValues[2]) == 0)
                {
                    sb.append(newHardValues[2]).append(";");
                }
                else if (Math.abs(Double.parseDouble(oldHardValues[2]) - Double.parseDouble(newHardValues[2])) < pulseThreshold) {
                    sb.append(newHardValues[2]).append(";");
                } else {
                    Log.d(TAG, "Pulse was to high (spike caught): " + newHardValues[2]);
                    sb.append(oldHardValues[2]);
                }
                if(newHardValues.length == 4)
                {
                    if(oldHardValues.length < 4)
                    {
                        sb.append(newHardValues[3]).append(";");
                    }
                    else if (Math.abs(Double.parseDouble(oldHardValues[3]) - Double.parseDouble(newHardValues[3])) < ECGThreshold) {
                        sb.append(newHardValues[3]).append(";");
                    } else {
                        Log.d(TAG, "Pulse was to high (spike caught): " + newHardValues[3]);
                        sb.append("0");
                    }
                }
            }
        }catch (Exception ex)
        {
            Log.e(TAG,"Exception caught while parsing values!",ex);
        }
        return sb.toString();
    }

    @Override
    public void onDestroy() {
        connectedThread.cancel();
        createConnectThread.cancel();
    }

    class CreateConnectThread extends Thread {

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.e("Status", "Device connected");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS,-1,-1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.start();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    public void writeValuesToFirestore(String values)
    {
        String humidity, temp, pulse;
        String ECG = "0";
        String[] splitValues = values.split(";");
        // humidity; temp; pulse
        try {
            humidity = splitValues[0];
            temp = splitValues[1];
            pulse = splitValues[2];
            if(splitValues.length > 3)
            {
                ECG = splitValues[3];
            }

            if(humidity.equals("0") || temp.equals("0") || pulse.equals("0"))
            {
                return;
            }
            Date data = new Date();
            String umiditate = data.toString() + ";" + humidity;
            String temperatura = data.toString() + ";" + temp;
            String puls = data.toString() + ";" + pulse;
            String ecg = data.toString() + ";" + ECG;


            writeHumidity(umiditate);
            writeTemperature(temperatura);
            writePulse(puls);
            if(!ECG.equals("0"))
            {
                writeECG(ecg);
            }
        }
        catch (Exception ex) {
            Log.e(TAG, "Error caught while splitting sensor values", ex);
        }
    }

    public void writeECG(String ecg)
    {
        parametriRef.document("ECG").update("valori", FieldValue.arrayUnion(ecg))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Valoare adaugata cu succes: " + ecg);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Eroare la adaugarea valorii pentru ECG", e);
                    }
                });
    }

    public void writeHumidity(String humidity)
    {
        parametriRef.document("Umiditate").update("valori", FieldValue.arrayUnion(humidity))
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Valoare adaugata cu succes: " + humidity);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Eroare la adaugarea valorii pentru umiditate", e);
                }
            });
    }

    public void writeTemperature(String temp)
    {
        parametriRef.document("Temperatura").update("valori", FieldValue.arrayUnion(temp))
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Valoare adaugata cu succes: " + temp);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Eroare la adaugarea valorii pentru temperatura", e);
                }
            });
    }

    public void writePulse(String pulse)
    {
        parametriRef.document("Puls").update("valori", FieldValue.arrayUnion(pulse))
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Valoare adaugata cu succes: " + pulse);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Eroare la adaugarea valorii pentru puls", e);
                }
            });
    }

    /* Binding classs */
    public class LocalBinder extends Binder {
        public BluetoothService getService()
        {
            return BluetoothService.this;
        }
    }
    /* =============================== Thread for Data Transfer =========================================== */
    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.d(TAG,"Exception caught when getting communication streams!",e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            Log.d(TAG,"Sending start message to android");
            write("Give");
            while (true) {
                try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage;
                    if (buffer[bytes] == '\n') {
                        readMessage = new String(buffer, 0, bytes);
                        Log.e("Status", readMessage);
                        handler.obtainMessage(MESSAGE_READ, readMessage).sendToTarget();
                        bytes = 0;
                    } else {
                        bytes++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error", "Unable to send message", e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG,"Error closing socket!",e);
            }
        }
    }
}
