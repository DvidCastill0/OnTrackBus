package com.android.ontrackbus.User_Menu.ui.maps.ModalWindow

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.ontrackbus.R
import com.android.ontrackbus.User_Menu.MenuActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class search_route : AppCompatActivity() {
    // variable autocomplete otra ruta
    private var actv_BuscarOtraRuta: AutoCompleteTextView? = null

    //variabble botones otra ruta
    private var btn_OtraRutaAceptar: Button? = null
    //variabble botones otra ruta
    private var btn_OtraRutaCancelar: Button? = null

    //variables para recuperar datos.
    //arraylist para autocompletetextview
    private val rutasNombres = ArrayList<String>()
    private val RMFNombres = ArrayList<String>()
    private var contadorNombresRutas = 0
    private var correoConfianza: String? = null
    private  var orientacionRutas:kotlin.String? = null

    //otra ruta bundle para pasar datos
    private val BundleOtraRuta = Bundle()

    private var OTBReference: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_route)

        OTBReference = FirebaseDatabase.getInstance().reference

        //instancear variables

        //instancear variables
        actv_BuscarOtraRuta = findViewById<AutoCompleteTextView>(R.id.actv_SeleccioneOtraRutaAMirar)
        btn_OtraRutaAceptar = findViewById<Button>(R.id.btn_BuscarOtraRuta)
        btn_OtraRutaCancelar = findViewById<Button>(R.id.btn_CancelarOtraRutaMirar)

        //bundle para descargar datos

        //bundle para descargar datos
        var BundleNombresDeRutas: Bundle? = Bundle()
        BundleNombresDeRutas = intent.extras
        contadorNombresRutas = BundleNombresDeRutas!!.getInt("CantidadDeRutas")
        correoConfianza = BundleNombresDeRutas.getString("contactoConfianza")
        orientacionRutas = BundleNombresDeRutas.getString("orientacion")


        for (contadorarrarRMF in 1..3) {
            RMFNombres.add(BundleNombresDeRutas.getString("RMF$contadorarrarRMF")!!)
        }
        for (contadorNombresRutas2 in 1 until contadorNombresRutas) {
            rutasNombres.add(BundleNombresDeRutas.getString("Ruta$contadorNombresRutas2")!!)
        }


        //array adapter para autocompletetextview


        //array adapter para autocompletetextview
        val adapterrutasNombres =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, rutasNombres)
        actv_BuscarOtraRuta!!.setAdapter(adapterrutasNombres)


        val medidasVentana = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(medidasVentana)
        val ancho = medidasVentana.widthPixels
        val alto = medidasVentana.heightPixels
        window.setLayout((ancho + 0.85).toInt(), (alto * 0.3).toInt())

        funcion_btn_Aceptar()
        funcion_btn_Cancelar()
    }


    private fun funcion_btn_Aceptar() {
        btn_OtraRutaAceptar!!.setOnClickListener {
            OTBReference!!.child("Rutas")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val rutaSeleccionadaNombre =
                            actv_BuscarOtraRuta!!.text.toString().trim { it <= ' ' }
                        var rutaSeleccionadaNumero: String? = null
                        var bandera = 0
                        for (contadorarraynombres in 1 until contadorNombresRutas) {
                            val contadormenosuno = contadorarraynombres - 1
                            if (rutaSeleccionadaNombre == rutasNombres[contadormenosuno]) {
                                rutaSeleccionadaNumero = "Ruta$contadorarraynombres"
                                bandera = 1
                            }
                        }
                        if (bandera == 1) {
                            //este dato es opcional solo es para indicar que se esta recargando el fragmento y no muestre asi el mensaje de bienvenido
                            BundleOtraRuta.putString("RecargarFragmento", "RecargarFragmento")
                            BundleOtraRuta.putString("OrientacionRuta", orientacionRutas)
                            BundleOtraRuta.putInt("CantidadDeRutas", contadorNombresRutas)
                            BundleOtraRuta.putString("contactoConfianza", correoConfianza)
                            BundleOtraRuta.putString("RutaSeleccionada", rutaSeleccionadaNombre)
                            BundleOtraRuta.putString(
                                "RutaSeleccionadaNumero",
                                rutaSeleccionadaNumero
                            )
                            for (enviarrutas in 1 until contadorNombresRutas) {
                                val menosuno = enviarrutas - 1
                                BundleOtraRuta.putString("Ruta$enviarrutas", rutasNombres[menosuno])
                            }
                            for (RMFcontador in 0..2) {
                                if (RMFNombres[RMFcontador] != null) {
                                    BundleOtraRuta.putString(
                                        "RMF$RMFcontador",
                                        RMFNombres[RMFcontador]
                                    )
                                }
                            }

                            //se obtienen los nombres de las rutas
                            var contadorParadas = 1
                            for (snapshot in dataSnapshot.child(rutaSeleccionadaNumero!!)
                                .child(
                                    orientacionRutas!!
                                ).children) {
                                val nombreParada = dataSnapshot.child(rutaSeleccionadaNumero).child(
                                    orientacionRutas!!
                                ).child("Parada$contadorParadas").child("tittle").value.toString()
                                BundleOtraRuta.putString("Parada$contadorParadas", nombreParada)
                                contadorParadas++
                            }
                            BundleOtraRuta.putInt("CantidadDeParadas", contadorParadas)


                            // Agregas el Bundle al Intent e inicias ActivityB
                            val intentMenuUsuario = Intent(
                                this@search_route,
                                MenuActivity::class.java
                            )
                            intentMenuUsuario.putExtras(BundleOtraRuta)
                            startActivity(intentMenuUsuario)
                            finish()
                        } else if (rutaSeleccionadaNombre == "") {
                            Toast.makeText(
                                applicationContext,
                                "Porfavor Ingrese una Ruta",
                                Toast.LENGTH_SHORT
                            ).show()
                            actv_BuscarOtraRuta!!.error = "Porfavor Ingrese una Ruta"
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Porfavor Ingrese una Ruta Valida De la lista de Rutas Disponibles.",
                                Toast.LENGTH_SHORT
                            ).show()
                            actv_BuscarOtraRuta!!.error =
                                "Porfavor Ingrese una Ruta Valida De la lista de Rutas Disponibles."
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        }
    }


    private fun funcion_btn_Cancelar() {
        btn_OtraRutaCancelar!!.setOnClickListener { finish() }
    }

}