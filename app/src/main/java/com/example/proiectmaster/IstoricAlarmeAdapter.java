package com.example.proiectmaster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.proiectmaster.Models.Alarma;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IstoricAlarmeAdapter extends ArrayAdapter<Alarma> {
    private Context context;
    private List<Alarma> alarmeList = new ArrayList();

    public IstoricAlarmeAdapter(@NonNull Context context, ArrayList<Alarma> alarmeList) {
        super(context, 0, alarmeList);
        this.context = context;
        this.alarmeList = alarmeList;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.alarma_list_element, parent,false);
        }

        Alarma currentAlarma = alarmeList.get(position);

        TextView parametruTxt = (TextView) listItem.findViewById(R.id.parametruTxt);
        parametruTxt.setText(currentAlarma.getParametru());

        String valMinima = Double.toString(currentAlarma.getValMinima());
        String valMaxima = Double.toString(currentAlarma.getValMaxima());
        String interval = valMinima + " - " + valMaxima;
        TextView valoriNormaleTxt = (TextView) listItem.findViewById(R.id.intervalValoriTxt);
        valoriNormaleTxt.setText(interval);

        TextView valoriActualeTxt = (TextView) listItem.findViewById(R.id.valoriActualeValTxt);
        valoriActualeTxt.setText(Double.toString(currentAlarma.getValActuala()));

        TextView dataTxt = (TextView) listItem.findViewById(R.id.dataValTxt);
        dataTxt.setText(formatDate(currentAlarma.getData()));

        TextView unitateMasuraTxt1 = (TextView) listItem.findViewById(R.id.unitateMasuraTxt1);
        TextView unitateMasuraTxt2 = (TextView) listItem.findViewById(R.id.unitateMasuraTxt2);
        ImageView imageView = (ImageView) listItem.findViewById(R.id.alarmaImg);

        String parametru = currentAlarma.getParametru();
        switch(parametru) {
            case "Temperatura":
                imageView.setBackground(context.getResources().getDrawable(R.drawable.temperatura));
                unitateMasuraTxt1.setText("°C");
                unitateMasuraTxt2.setText("°C");
                break;
            case "ECG":
                imageView.setBackground(context.getResources().getDrawable(R.drawable.ecg));
                unitateMasuraTxt1.setText("");
                unitateMasuraTxt2.setText("");
                break;
            case "Puls":
                imageView.setBackground(context.getResources().getDrawable(R.drawable.puls));
                unitateMasuraTxt1.setText("bpm");
                unitateMasuraTxt2.setText("bpm");
                break;
            case "Umiditate":
                imageView.setBackground(context.getResources().getDrawable(R.drawable.umiditate));
                unitateMasuraTxt1.setText("%");
                unitateMasuraTxt2.setText("%");
                break;
            default:
                break;
        }

        return listItem;
    }

    private String formatDate(Date data)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(data);
    }
}