package com.android.ontrackbus.User_Menu.ui.maps.ModalWindow

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.android.ontrackbus.R
import com.android.ontrackbus.User_Menu.MenuActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class pasadorSpinner : AppCompatActivity() {

    private val BundleOtraRuta = Bundle()
    private var OTBReference: DatabaseReference? = null

    //bundle para descargar datos
    private var BundleNombresDeRutas = Bundle()
    private var numeroDeRuta: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasador_spinner)

        val medidasVentana = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(medidasVentana)
        val ancho = medidasVentana.widthPixels
        val alto = medidasVentana.heightPixels
        window.setLayout((ancho + 0.85).toInt(), (alto * 0.3).toInt())


        BundleNombresDeRutas = intent.extras!!
        OTBReference = FirebaseDatabase.getInstance().reference

        OTBReference!!.child("Rutas").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                //este dato es opcional solo es para indicar que se esta recargando el fragmento y no muestre asi el mensaje de bienvenido
                BundleOtraRuta.putString("RecargarFragmento", "RecargarFragmento")
                BundleOtraRuta.putString(
                    "OrientacionRuta",
                    BundleNombresDeRutas.getString("OrientacionRuta")
                )
                BundleOtraRuta.putInt(
                    "CantidadDeRutas",
                    BundleNombresDeRutas.getInt("CantidadDeRutas")
                )
                BundleOtraRuta.putString(
                    "contactoConfianza",
                    BundleNombresDeRutas.getString("contactoConfianza")
                )
                BundleOtraRuta.putString(
                    "RutaSeleccionada",
                    BundleNombresDeRutas.getString("RutaSeleccionada")
                )
                var contadorRutasDisponibles = 1
                for (snapshot in dataSnapshot.children) {
                    //se obtiene losnombres de las rutas disponibles
                    val numeroDeRuta2 = "Ruta$contadorRutasDisponibles"
                    val rutaname = dataSnapshot.child("Ruta$contadorRutasDisponibles")
                        .child("NombreDeRuta").value.toString()
                    if (rutaname == BundleNombresDeRutas.getString("RutaSeleccionada")) {
                        BundleOtraRuta.putString("RutaSeleccionadaNumero", numeroDeRuta2)
                        numeroDeRuta = numeroDeRuta2
                    }
                    contadorRutasDisponibles++
                }

                //se obtienen los nombres de las Paradas
                var contadorParadas = 1
                for (snapshot in dataSnapshot.child(numeroDeRuta!!).child(
                    BundleNombresDeRutas.getString("OrientacionRuta")!!
                ).children) {
                    val nombreParada = dataSnapshot.child(numeroDeRuta!!)
                        .child(BundleNombresDeRutas.getString("OrientacionRuta")!!).child(
                            "Parada$contadorParadas"
                        ).child("tittle").value.toString()
                    BundleOtraRuta.putString("Parada$contadorParadas", nombreParada)
                    contadorParadas++
                }
                BundleOtraRuta.putInt("CantidadDeParadas", contadorParadas)
                for (RMFcontador in 0..2) {
                    if (BundleNombresDeRutas.getString("RMF$RMFcontador") != null) {
                        BundleOtraRuta.putString(
                            "RMF$RMFcontador",
                            BundleNombresDeRutas.getString("RMF$RMFcontador")
                        )
                    }
                }
                for (enviarrutas in 1 until BundleNombresDeRutas.getInt("CantidadDeRutas")) {
                    BundleOtraRuta.putString(
                        "Ruta$enviarrutas",
                        BundleNombresDeRutas.getString("Ruta$enviarrutas")
                    )
                }


                // Agregas el Bundle al Intent e inicias ActivityB
                val intentMenuUsuario = Intent(this@pasadorSpinner, MenuActivity::class.java)
                intentMenuUsuario.putExtras(BundleOtraRuta)
                startActivity(intentMenuUsuario)
                finish()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}