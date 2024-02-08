package com.android.ontrackbus.User_Menu.ui.channels.ModalWindow

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.ontrackbus.Models.Canales
import com.android.ontrackbus.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class enterChannel : AppCompatActivity() {

    private var tv_RutaCamal: TextView? = null
    private var btn_Aceptar: Button? = null
    private var btn_Cancelar: Button? = null
    private var nombreCanal: String? = null
    private var numeroCanal = 0

    private var OTBReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null

    //objeto para obtener hijos de canales de rutas
    private var oCanales: Canales? = null
    private val array_reportesCanales: ArrayList<Canales> = ArrayList<Canales>()

    private var bandera = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_channel)


        //cortar activity para hacer ventana emergente

        //cortar activity para hacer ventana emergente
        val medidasVentana = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(medidasVentana)
        val ancho = medidasVentana.widthPixels
        val alto = medidasVentana.heightPixels
        window.setLayout((ancho + 0.85).toInt(), (alto * 0.30).toInt())

        //instancear variables

        //instancear variables
        tv_RutaCamal = findViewById(R.id.tv_CanalDeRuta)

        //bundle para descargar datos

        //bundle para descargar datos
        var BundleNombresDeCanales: Bundle? = Bundle()
        BundleNombresDeCanales = intent.extras

        nombreCanal = BundleNombresDeCanales!!.getString("CanalNombre")
        numeroCanal = BundleNombresDeCanales.getInt("CanalNumero")
        tv_RutaCamal!!.text = "Canal de Ruta $nombreCanal"


        //instanciar botones


        //instanciar botones
        btn_Aceptar = findViewById(R.id.btn_entrarACanal)
        btn_Cancelar = findViewById(R.id.btn_CancelarCanal)

        btn_Cancelar?.setOnClickListener(View.OnClickListener { finish() })


        //checar si el canal buscado ya esta en el list view


        //checar si el canal buscado ya esta en el list view
        OTBReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        OTBReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val iduser = mAuth!!.getCurrentUser()!!.uid
                bandera = 0
                for (snapshot in dataSnapshot.child("Users").child(iduser)
                    .child("CanalesSeleccionados").children) {
                    oCanales = snapshot.getValue(Canales::class.java)
                    array_reportesCanales.add(oCanales!!)
                }
                for (contadorcanales in array_reportesCanales.indices) {
                    if (nombreCanal == array_reportesCanales[contadorcanales].Nombre) {
                        bandera = 1
                        btn_Aceptar!!.text = "Abrir"
                    }
                }
                btn_Aceptar!!.setOnClickListener(View.OnClickListener {
                    //abrir o unirse segun si ya esta el canal en el list view o no
                    if (bandera == 1) {
                        //abrir canal
                        val ver_Canal = Intent(
                            this@enterChannel,
                            seeChannelInfo::class.java
                        )
                        val pasadorVentanaEmergenteCanal = Bundle()
                        pasadorVentanaEmergenteCanal.putString("CanalNombre", nombreCanal)
                        pasadorVentanaEmergenteCanal.putInt("CanalNumero", numeroCanal)
                        ver_Canal.putExtras(pasadorVentanaEmergenteCanal)
                        startActivity(ver_Canal)
                        finish()
                    } else {
                        //unirse a canal
                        val userid = mAuth!!.getCurrentUser()!!.uid
                        var contadorAgregados = 1
                        for (contadorInsertar in 1..10) {
                            if (dataSnapshot.child("Users").child(userid)
                                    .child("CanalesSeleccionados").child(
                                        "CanalSeleccionado$contadorInsertar"
                                    ).exists()
                            ) {
                                contadorAgregados++
                            } else {
                                val AgregarCanal: MutableMap<String, Any> = HashMap()
                                AgregarCanal["Id_CanalSeleccionado"] = contadorInsertar
                                AgregarCanal["Nombre"] = nombreCanal!!
                                OTBReference!!.child("Users").child(userid)
                                    .child("CanalesSeleccionados").child(
                                        "CanalSeleccionado$contadorInsertar"
                                    ).setValue(AgregarCanal).addOnCompleteListener { finish() }
                                break
                            }
                        }
                        if (contadorAgregados == 10) {
                            Toast.makeText(
                                applicationContext,
                                "Ya estas unido a 10 canales, porfavor elimina uno para agregar otro.",
                                Toast.LENGTH_LONG
                            )
                            finish()
                        }
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        //funcionalidad botones


    }
}