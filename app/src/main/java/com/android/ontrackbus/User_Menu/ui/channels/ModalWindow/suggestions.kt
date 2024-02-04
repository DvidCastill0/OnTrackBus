package com.android.ontrackbus.User_Menu.ui.channels.ModalWindow

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.ontrackbus.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date

class suggestions : AppCompatActivity() {

    //crear variable de firebase
    private var OTBReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null

    //variables xml
    private var edt_Sugerencias: EditText? = null
    private var btn_CancelarSugerencia: Button? = null
    private var btn_EnviarSugerencias: Button? = null

    private var numeroCanal: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestions)

        val medidasVentana = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(medidasVentana)
        val ancho = medidasVentana.widthPixels
        val alto = medidasVentana.heightPixels
        window.setLayout((ancho + 0.85).toInt(), (alto * 0.40).toInt())

        //recibir numero de cnal

        //recibir numero de cnal
        val bundleSugerencias: Bundle?
        bundleSugerencias = intent.extras
        numeroCanal = bundleSugerencias!!.getString("CanalNumero")

        //instancemaos firebase

        //instancemaos firebase
        OTBReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        //instanciar elementos grficos

        //instanciar elementos grficos
        edt_Sugerencias = findViewById<EditText>(R.id.edt_EscribeSugerencia)
        btn_EnviarSugerencias = findViewById<Button>(R.id.btn_EnivarSugerencia)
        btn_CancelarSugerencia = findViewById<Button>(R.id.btn_CancelarSugerencia)

        //funcionalidad

        //funcionalidad
        btn_CancelarSugerencia!!.setOnClickListener(View.OnClickListener { finish() })

        btn_EnviarSugerencias!!.setOnClickListener(View.OnClickListener {
            val sugerenciaUsuario = edt_Sugerencias!!.getText().toString().trim { it <= ' ' }
            val correoUsuario = mAuth!!.getCurrentUser()!!.email
            //objeto que busca el formato de la fecha a obtener
            val formateadorfecha = SimpleDateFormat(
                "HH:mm:ss d-MM-yyyy"
            )

            //se obtiene la fecha y hora actual
            val fechaDate = Date()
            val fecha = formateadorfecha.format(fechaDate)
            val Sugerencia: MutableMap<String, Any?> = HashMap()
            Sugerencia["sugerencia_Contenido"] = sugerenciaUsuario
            Sugerencia["CorreoRemitente"] = correoUsuario
            Sugerencia["FechaDeEmision"] = fecha
            // Generate a reference to a new location and add some data using push()
            val pushedPostRef =
                OTBReference!!.child("Canales").child(numeroCanal!!).child("Sugerencias").push()
            val Id_Clave = pushedPostRef.key
            Sugerencia["Id_ClaveSugerencia"] = Id_Clave
            OTBReference!!.child("Canales").child(numeroCanal!!).child("Sugerencias").push()
                .setValue(Sugerencia)
            finish()
        })

    }
}