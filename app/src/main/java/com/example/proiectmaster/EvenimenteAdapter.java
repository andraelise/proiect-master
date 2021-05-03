package com.example.proiectmaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.proiectmaster.Models.Eveniment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EvenimenteAdapter extends ArrayAdapter<Eveniment> {
    private Context context;
    private List<Eveniment> evenimenteList = new ArrayList();

    public EvenimenteAdapter(@NonNull Context context, ArrayList<Eveniment> evenimenteList) {
        super(context, 0, evenimenteList);
        this.context = context;
        this.evenimenteList = evenimenteList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.eveniment_list_element, parent,false);
        }

        Eveniment currentEveniment= evenimenteList.get(position);

        TextView evenimentTxt = (TextView) listItem.findViewById(R.id.evenimentTxt);
        evenimentTxt.setText(currentEveniment.getEveniment());

        TextView oraStartTxt = (TextView) listItem.findViewById(R.id.oraStartTxt);
        oraStartTxt.setText(getHour(currentEveniment.getDataStart()));

        TextView oraEndTxt = (TextView) listItem.findViewById(R.id.oraEndTxt);
        oraEndTxt.setText(getHour(currentEveniment.getDataEnd()));

        TextView durataTxt = (TextView) listItem.findViewById(R.id.durataTxt);
        durataTxt.setText(Integer.toString(currentEveniment.getDurata()));

        ImageView imageView = (ImageView) listItem.findViewById(R.id.evenimentImg);
        String eveniment = currentEveniment.getEveniment();
        switch(eveniment) {
            case "Bicicleta":
                imageView.setBackground(context.getResources().getDrawable(R.drawable.bike));
                break;
            case "Mers":
                imageView.setBackground(context.getResources().getDrawable(R.drawable.walk));
                break;
            case "Alergat":
                imageView.setBackground(context.getResources().getDrawable(R.drawable.jogging));
                break;
            default:
                imageView.setBackground(context.getResources().getDrawable(R.drawable.activitate));
                break;
        }

        return listItem;
    }

    private String getHour(Date date)
    {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
//        return time;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }
}