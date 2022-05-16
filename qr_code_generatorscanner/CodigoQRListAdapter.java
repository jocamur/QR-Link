package com.example.qr_code_generatorscanner;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

// Adaptador/Creador de elemento qr para mostrar en la lista
public class CodigoQRListAdapter extends ArrayAdapter<CodigoQR>
{
    Context contexto;

    // Referencia a la plantilla xml para cada codigo QR
    int resource;

    // Constructor
    public CodigoQRListAdapter(Context context, int resource, ArrayList<CodigoQR> objects)
    {
        super(context, resource, objects);
        this.contexto = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        // Obtenemos la informacion del qr actual
        CodigoQR codigo = getItem(position);

        // "Inflamos"(Cargamos) la vista
        LayoutInflater inflater = LayoutInflater.from(contexto);
        convertView = inflater.inflate(resource,parent,false);

        // Referencia a los elementos de la interfaz de la plantilla
        // Que son el numero del qr en la lista y su contenido
        TextView numero = convertView.findViewById(R.id.textViewNumero);
        TextView contenido = convertView.findViewById(R.id.textViewContenido);

        // Asigamos nombre y contenido
        numero.setText("#"+(position+1));
        contenido.setText(codigo.getContenido());

        // Regresamos el elemento listo para agregarse y mostrarse
        return  convertView;
    }
}
