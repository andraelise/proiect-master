package com.example.proiectmaster.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.proiectmaster.Models.Recomandare;

import java.util.ArrayList;
import java.util.List;

public class RecomandariAdapter extends ArrayAdapter<Recomandare> {
    private Context context;
    private List<Recomandare> recomandariList = new ArrayList();

    public RecomandariAdapter(@NonNull Context context, ArrayList<Recomandare> recomandariList) {
        super(context, 0, recomandariList);
        this.context = context;
        this.recomandariList = recomandariList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
//        if(listItem == null) {
//            listItem = LayoutInflater.from(context).inflate(R.layout.recomandare_list_element, parent, false);
//        }
//
//        Recomandare currentRecomandare = recomandariList.get(position);
//
//        TextView tipActivitateTxt = (TextView) listItem.findViewById(R.id.activitateTxt);
//        tipActivitateTxt.setText(currentRecomandare.getTipActivitate());
//
//        TextView durataTxt = (TextView) listItem.findViewById(R.id.durataValTxt);
//        durataTxt.setText(currentRecomandare.getDurata());
//
//        TextView frecventaTxt = (TextView) listItem.findViewById(R.id.frecventaValTxt);
//        frecventaTxt.setText(currentRecomandare.getFrecventa());
//
//        TextView indicatiiTxt = (TextView) listItem.findViewById(R.id.indicatiiValTxt);
//        indicatiiTxt.setText(currentRecomandare.getIndicatii());
//
//        ImageView imageView = (ImageView) listItem.findViewById(R.id.recomandareImg);
//        String tipActivitate = currentRecomandare.getTipActivitate();
//        switch(tipActivitate) {
//            case "Bicicleta":
//                imageView.setBackground(context.getResources().getDrawable(R.drawable.bike));
//                break;
//            case "Mers":
//                imageView.setBackground(context.getResources().getDrawable(R.drawable.walk));
//                break;
//            case "Alergat":
//                imageView.setBackground(context.getResources().getDrawable(R.drawable.walk));
//                break;
//            default:
//                imageView.setBackground(context.getResources().getDrawable(R.drawable.bike));
//                break;
//        }

        return listItem;
    }
}
