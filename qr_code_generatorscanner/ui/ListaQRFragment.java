package com.example.qr_code_generatorscanner.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qr_code_generatorscanner.CodigoQR;
import com.example.qr_code_generatorscanner.CodigoQRListAdapter;
import com.example.qr_code_generatorscanner.HomeActivity;
import com.example.qr_code_generatorscanner.R;
import com.example.qr_code_generatorscanner.databinding.FragmentListaqrBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Vista de la lista de QRs del usuario
public class ListaQRFragment extends Fragment
{
    Context contexto;

    // Referencia a la vista para accesar a los elementos
    FragmentListaqrBinding binding;

    // Referencia a la base de datos firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Cargamos la vista inflando el xml y definimos el binding, root y contexto que usaremos en el script
        binding = FragmentListaqrBinding.inflate(inflater, container, false);
        contexto = binding.getRoot().getContext();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        // Referencia al elemento de lista de la interfaz
        ListView listView = binding.listaQR;

        // Lista de codigos QR registrados
        ArrayList<CodigoQR> listaQR = new ArrayList<>();

        // Entramos a la coleccion Codigos y de ahi al documento con el nombre del correo del usuario actual
        firebaseFirestore.collection("Codigos").document(HomeActivity.email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                // Obtenemos el documento de los codigos registrados por el usuario
                DocumentSnapshot documento = task.getResult();

                // Si existe el documento, o sea no esta vacio
                if (documento.exists())
                {
                    // Guardamos el contenido en un Hashmap, ID/Contenido
                    Map<String, Object> codigos = documento.getData();

                    // Si el hashmap existe y no esta vacio
                    if (codigos != null)
                    {
                        // Agregamos todos los codigos a la lista
                        // Usamos replaceAll para remover cualquier formato extra que firebase incluya y asi obtener el contenido original
                        for (Map.Entry<String, Object> codigo : codigos.entrySet())
                            listaQR.add(new CodigoQR(codigo.getKey(),codigo.getValue().toString().replaceAll("[\\[\\](){}]","")));
                    }
                }
                else
                    Toast.makeText(contexto, "Vacio", Toast.LENGTH_SHORT).show();

                CodigoQRListAdapter adapter = new CodigoQRListAdapter(contexto, R.layout.item_qr,listaQR);
                listView.setAdapter(adapter);

                // Evento que se ejecuta al hacer click en algun elemento de la lista
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                    {
                        // Creamos un nuevo contenedor para la informacion del QR seleccionado
                        CodigoQR codigoQR = listaQR.get(i);

                        // Agregamos los datos para la siguiente pantalla, el ID del qr y su contenido
                        Bundle bundle = new Bundle();
                        bundle.putString("id",codigoQR.getId());
                        bundle.putString("contenido",codigoQR.getContenido());

                        // Cargamos la pantalla
                        HomeActivity.CargarFragmento(R.id.navigation_editar,bundle);
                    }
                });
            }
        });
    }
}
