package com.example.qr_code_generatorscanner.ui;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.VIBRATE;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.qr_code_generatorscanner.databinding.FragmentEscanearBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import eu.livotov.labs.android.camview.ScannerLiveView;

// Vista de escanear QRs
public class EscanearFragment extends Fragment
{
    // Referencia a la vista para accesar a los elementos
    FragmentEscanearBinding binding;

    // Referencia a la base de datos firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    Context contexto;

    // Referencia al objeto que nos facilitara toda la parte de escanear el codigo QR
    ScannerLiveView scannerLiveView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Cargamos la vista inflando el xml y definimos el binding, root y contexto que usaremos en el script
        binding = FragmentEscanearBinding.inflate(inflater, container, false);
        contexto = binding.getRoot().getContext();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        
        scannerLiveView = binding.vistaCamara;

        // Si no tenemos permisos, los requerimos
        if (!RevisarPermisos())
            RequerirPermisos();

        // Iniciamos el escaner
        scannerLiveView.startScanner();

        // Una vez iniciado, comenzamos el listener para escuchar por resultado
        scannerLiveView.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener()
        {
            // Escaner iniciado
            @Override
            public void onScannerStarted(ScannerLiveView scanner)
            {
                Toast.makeText(contexto, "Escáner iniciado.", Toast.LENGTH_SHORT).show();
            }

            // Escaner detenido
            @Override
            public void onScannerStopped(ScannerLiveView scanner)
            {
                Toast.makeText(contexto, "Escáner detenido.", Toast.LENGTH_SHORT).show();
            }

            // Escaner encontro un error
            @Override
            public void onScannerError(Throwable err)
            {
                Toast.makeText(contexto, "Se ha producido un error en el escáner, por favor, inicie de nuevo.", Toast.LENGTH_SHORT).show();
            }

            // Se escaneo un codigo
            @Override
            public void onCodeScanned(String data)
            {
                // Cuando escaneamos un codigo necesitamos comprobar si esta registrado en la base de datos para esto
                // Cargamos todos los correos dentro de la coleccion Codigos
                firebaseFirestore.collection("Codigos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        // Una vez cargados, los metemos en una lista
                       List<DocumentSnapshot> correos = task.getResult().getDocuments();

                       // Variable para saber si encontramos el codigo
                       boolean encontrado = false;

                       // Para cada uno de los correos revisamos si contienen el codigo QR
                       for (int i = 0; i < correos.size(); i++)
                       {
                           // Comprobamos si el correo contiene el codigo escaneado, si escaneamos un codigo registado entonces
                           // deberia contener el ID
                           if (correos.get(i).get(data)!= null)
                           {
                               // Cuando encontramos el codigo mostramos el contenido
                               Toast.makeText(contexto, correos.get(i).get(data).toString(), Toast.LENGTH_LONG).show();
                               encontrado = true;
                               break;
                           }
                       }

                       // Si no encontramos el codigo significa que escaneamos un codigo comun y cualquiera asi que mostramos
                        // directamente la informacion escaneada
                       if (!encontrado)
                           Toast.makeText(contexto, data, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Revisamos si tenemos el permiso de camara y vibracion
    boolean RevisarPermisos()
    {
        int permisoCamara = ContextCompat.checkSelfPermission(contexto.getApplicationContext(), CAMERA);
        int permisoVibracion = ContextCompat.checkSelfPermission(contexto.getApplicationContext(), VIBRATE);

        // Si tenemos los dos permisos regresa true, si no, false
        return permisoCamara == PackageManager.PERMISSION_GRANTED && permisoVibracion == PackageManager.PERMISSION_GRANTED;
    }

    // Requerimos el permiso de vibracion
    void RequerirPermisos()
    {
        int PERMISSION_CODE = 200;
        ActivityCompat.requestPermissions(getActivity(), new String[]{CAMERA, VIBRATE}, PERMISSION_CODE);
    }

    // Se ejecuta cuando recibimos el resultado de los permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Si los permisos regresados con mayor a 0, es decir no recibimos una respuesta vacia
        if (grantResults.length > 0)
        {
            boolean camaraAceptada = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean vibracionAceptada = grantResults[1] == PackageManager.PERMISSION_GRANTED;

            if (!camaraAceptada || !vibracionAceptada)
                Toast.makeText(contexto, "Permiso denegado. No se puede utilizar la aplicación sin permisos", Toast.LENGTH_SHORT).show();
        }
    }
}