package com.android.ontrackbus.User_Menu.ui.trusted_contact.ModalWindow

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.ontrackbus.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class detail_trusted_contact : AppCompatActivity() {

    private var reporte_completo: TextView? = null
    private var tv_fecha: TextView? = null
    private var tv_email: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_trusted_contact)


        //cortar activity para hacer ventana emergente

        //cortar activity para hacer ventana emergente
        val medidasVentana = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(medidasVentana)
        val ancho = medidasVentana.widthPixels
        val alto = medidasVentana.heightPixels
        window.setLayout((ancho + 0.85).toInt(), (alto * 0.40).toInt())


        reporte_completo = findViewById<TextView>(R.id.campo_reporte)
        tv_fecha = findViewById<TextView>(R.id.campo_fecha)
        tv_email = findViewById<TextView>(R.id.campo_email)

        val recibidor = intent.extras


        val fecha = recibidor!!.getString("Fecha")
        val hora = recibidor.getString("Hora")
        val correo = recibidor.getString("Correo")
        val nombre = recibidor.getString("Nombre")
        val parada = recibidor.getString("Parada")
        val ruta = recibidor.getString("Ruta")

        val formateadorfecha = SimpleDateFormat(
            "HH:mm:ss d-MM-yyyy"
        )

        // SimpleDateFormat nuevoFormato = new SimpleDateFormat("hh:mm:ssa EEEE d 'de' MMMM 'de' yyyy");

        // SimpleDateFormat nuevoFormato = new SimpleDateFormat("hh:mm:ssa EEEE d 'de' MMMM 'de' yyyy");
        val nuevoFormatoHora = SimpleDateFormat("hh:mm:ss a")
        val nuevoFormatoFecha = SimpleDateFormat("EEEE d-MMM-yyyy")
        var fechadate: Date? = null
        val fechaantigua = "$hora $fecha"
        try {
            fechadate = formateadorfecha.parse(fechaantigua)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val fechadia = nuevoFormatoFecha.format(fechadate)
        val fechahora = nuevoFormatoHora.format(fechadate)
        val mensaje_confianza =
            """El usuario $nombre abordó la ruta $ruta a las $fechahora en la parada $parada.
Ahora puedes estar tranquil@.
 Atte: On track Bus."""


        tv_fecha!!.setText(fechadia)
        tv_email!!.setText(correo)

        reporte_completo!!.setText(mensaje_confianza)


    }
}