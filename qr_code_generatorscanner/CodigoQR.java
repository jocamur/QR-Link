package com.example.qr_code_generatorscanner;

// Clase para contener la informacion de los codigos QR a mostrar en la lista
// Unicamente tenemos 2 strings, uno para el id otro para el contenido
public class CodigoQR
{
    String id;
    String contenido;

    public CodigoQR(String id, String contenido)
    {
        this.id = id;
        this.contenido = contenido;
    }

    public String getId(){return id;}
    public String getContenido(){return contenido;}
}
