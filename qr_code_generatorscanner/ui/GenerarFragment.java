package com.example.qr_code_generatorscanner.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qr_code_generatorscanner.HomeActivity;
import com.example.qr_code_generatorscanner.databinding.FragmentGenerarBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Vista de generar QRs
public class GenerarFragment extends Fragment
{
    Context contexto;

    // Referencia a la vista para accesar a los elementos
    FragmentGenerarBinding binding;

    // Referencia a la base de datos firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    // Variable para guardar la imagen en que se genera a partir del ID del codigo QR para escanear
    Bitmap qrImagenBitmap;

    // Variable para guardar el contenido escrito
    String contenido;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Cargamos la vista inflando el xml y definimos el binding, root y contexto que usaremos en el script
        binding = FragmentGenerarBinding.inflate(inflater, container, false);
        contexto = binding.getRoot().getContext();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        // Evento al clickear el boton "Guardar QR"
        binding.buttonGuardarQR.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Si el contenido esta vacio mostramos mensaje
                if (contenido.isEmpty())
                    Toast.makeText(binding.getRoot().getContext(), "Introduzca algunos datos para generar el código QR", Toast.LENGTH_SHORT).show();
                else
                    HomeActivity.GuardarQR(contexto, contenido, qrImagenBitmap);
            }
        });

        // Evento al clickear el boton "Generar QR"
        binding.buttonGenerarQR.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                contenido = binding.editTextContenidoQR.getText().toString().trim();

                // Si el contenido esta vacio mostramos mensaje
                if (contenido.isEmpty())
                    Toast.makeText(binding.getRoot().getContext(), "Introduzca algunos datos para generar el código QR", Toast.LENGTH_SHORT).show();
                else
                {
                    // Generamos un ID para el qr a generar
                    String identificador = UUID.randomUUID().toString();

                    // Firebase requiere que le enviemos la informacion con el formato ID/Contenido
                    // Por lo que creamos un Hashmap para hacer exactamente eso
                    Map<String, Object> qr = new HashMap<>();
                    qr.put(identificador, contenido);

                    // Creamos la imagen QR a partir del ID y la mostramos en la vista
                    qrImagenBitmap = HomeActivity.CrearImagenQR(identificador);
                    binding.ivQR.setImageBitmap(qrImagenBitmap);

                    // InputManager para cerrar automaticamente el teclado y tener mejor vista
                    InputMethodManager inputManager = (InputMethodManager) binding.getRoot().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(binding.editTextContenidoQR.getApplicationWindowToken(), 0);
                    binding.textViewPlaceholder.setVisibility(View.GONE);

                    // Entramos a la coleccion Codigos y de ahi al documento con el nombre del correo del usuario actual
                    // Ahi ingresamos el nuevo codigo
                    firebaseFirestore.collection("Codigos").document(HomeActivity.email).set(qr, SetOptions.merge());

                    // Informamos
                    Toast.makeText(contexto, "Codigo creado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}