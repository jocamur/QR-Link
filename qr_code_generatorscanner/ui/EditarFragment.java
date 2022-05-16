package com.example.qr_code_generatorscanner.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qr_code_generatorscanner.HomeActivity;
import com.example.qr_code_generatorscanner.R;
import com.example.qr_code_generatorscanner.databinding.FragmentEditarBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

// Vista de editar el contenido de un QR
public class EditarFragment extends Fragment
{
    Context contexto;

    // Variable para guardar la imagen en que se genera a partir del ID del codigo QR para escanear
    Bitmap qrImagenBitmap;

    // Referencia a la vista para accesar a los elementos
    FragmentEditarBinding binding;
    // Referencia a la base de datos firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    // ID del codigo qr
    String qrID;
    // Contenido del codigo
    String qrContenido;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Cargamos la vista inflando el xml y definimos el binding, root y contexto que usaremos en el script
        binding = FragmentEditarBinding.inflate(inflater, container, false);
        contexto = binding.getRoot().getContext();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Obtenemos los argumentos de la pantalla anterior, en este caso el ID y contenido del QR seleccionado
        Bundle bundle = getArguments();
        qrID = bundle.getString("id");
        qrContenido = bundle.getString("contenido");

        // Actualizamos la vista con los elementos obtenidos
        binding.textViewID.setText("ID: " + qrID);
        binding.editTextContenidoQR.setText(qrContenido);

        // Creamos la imagen QR a partir del ID y la mostramos en la vista
        qrImagenBitmap = HomeActivity.CrearImagenQR(qrID);
        binding.imageViewQR.setImageBitmap(qrImagenBitmap);

        // Evento al clickear el boton "Regresar"
        binding.buttonRegresar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Cargamos el fragmento de la lista de qrs
                HomeActivity.CargarFragmento(R.id.navigation_lista,null);
            }
        });

        // Evento al clickear el boton "Guardar imagen"
        binding.buttonGuardarImagen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String contenido = binding.editTextContenidoQR.getText().toString();

                // Si el contenido esta vacio mostramos mensaje
                if (contenido.isEmpty())
                    Toast.makeText(binding.getRoot().getContext(), "Introduzca algunos datos para generar el código QR", Toast.LENGTH_SHORT).show();
                else
                    HomeActivity.GuardarQR(contexto, contenido,qrImagenBitmap);
            }
        });

        // Evento al clickear el boton "Actualizar"
        binding.buttonActualizar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String contenido = binding.editTextContenidoQR.getText().toString();

                // Si el contenido del QR esta vacio mostramos un mensaje
                if (contenido.isEmpty())
                    Toast.makeText(binding.getRoot().getContext(), "Introduzca algunos datos para generar el código QR", Toast.LENGTH_SHORT).show();
                else
                {
                    // Firebase requiere que le enviemos la informacion con el formato ID/Contenido
                    // Por lo que creamos un Hashmap para hacer exactamente eso
                    Map<String, Object> qr = new HashMap<>();
                    // Ponemos como ID el ID del codigo qr y como contenido el contenido que escribimos como String
                    qr.put(qrID, contenido);

                    // Entramos a la coleccion "Codigos" y ahi buscamos el documento con el nombre de nuestro email
                    // Una vez encontrado actualiza el contenido del QR con el ID dado
                    firebaseFirestore.collection("Codigos").document(HomeActivity.email).update(qr);

                    // Informamos que los cambios se guardaron
                    Toast.makeText(contexto, "Cambios guardados", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
