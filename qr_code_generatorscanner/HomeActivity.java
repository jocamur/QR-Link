package com.example.qr_code_generatorscanner;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.qr_code_generatorscanner.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

// Actividad del menu princiapl del usuario
public class HomeActivity extends AppCompatActivity
{
    ActivityHomeBinding binding;
    static NavController navController;

    // Referencia al email con el que ingreso el usuario
    // Nos servira a la hora de guardar los QR generados
    public static String email;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

     binding = ActivityHomeBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

     // Obtenemos el email de la pantalla anterior de ingreso
     email = getIntent().getExtras().getString("email");

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Pasa cada menu ID como un conjunto de IDs porque
        // cada menu debe ser considerado como destino top level
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_escanear, R.id.navigation_generar, R.id.navigation_lista, R.id.navigation_acerca)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_home);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navView, navController);
    }

    // Metodo para cargar nuevos fragmentos
    public static void CargarFragmento(int fragmento, Bundle bundle)
    {
        navController.navigate(fragmento,bundle);
    }

    // Metodo para guardar QR localmente
    public static void GuardarQR(Context contexto, String qrID, Bitmap qrImagenBitmap)
    {
        //  Variable para guardar el estado de si la imagen se guardo o no
        boolean guardada;
        // OutputStream para escribir/guardar la imagen
        OutputStream escritor;

        try
        {
            // Si el dispositivo es android 9 o superior
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                ContentResolver resolver = contexto.getContentResolver();
                ContentValues contentValues = new ContentValues();

                // Guardamos la imagen en el destino DCIM/QR, imagen de tipo .png con el nombre de "QR - " + las 5 primeras letras del QR
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "QR - "+ qrID.toString().substring(0,5));
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "QR");

                // Insertamos la imagen en los archivos a escribir
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                // Guardamos la imagen
                escritor = resolver.openOutputStream(imageUri);
            }
            else
            {
                // El proceso a seguir es el mismo pero de diferente manera por la version de android
                String directorioImagen = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM).toString() + File.separator + "QR";

                File archivoImagen = new File(directorioImagen);

                if (!archivoImagen.exists())
                    archivoImagen.mkdir();

                File imagen = new File(directorioImagen, "QR - "+qrID.toString().substring(0,5) + ".jpg");
                escritor = new FileOutputStream(imagen);
            }
            guardada = qrImagenBitmap.compress(Bitmap.CompressFormat.PNG, 100, escritor);

            // Al finalizar limpiamos y cerramos el stream
            escritor.flush();
            escritor.close();

            // Informamos que se guardo la imagen correctamente
            Toast.makeText(contexto, "Imagen guardada", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            // Si tenemos algun error lo mostramos
            Toast.makeText(contexto, "Error al guardar: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    // Metodo para crear un bitmap(bits) de un String
    // Lo usaremos para generar las imagenes de los codigos qr
    public static Bitmap CrearImagenQR(String contenido)
    {
        MultiFormatWriter escritor = new MultiFormatWriter();

        BitMatrix matriz = null;

        try
        {
            // Convertimos la cadena de texto del ID a una matriz, especificamos que se trata de un codigo QR
            matriz = escritor.encode(contenido, BarcodeFormat.QR_CODE, 280, 280);
        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }

        BarcodeEncoder codificadorQR = new BarcodeEncoder();

        // Con el codificador QR convertimos la matriz a un conjunto de bits para crear la imagen
        Bitmap bitmap = codificadorQR.createBitmap(matriz);

        // Devolvemos el conjunto de bits para crear la iamgen
        return bitmap;
    }
}