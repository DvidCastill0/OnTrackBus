package com.android.ontrackbus.User_Menu.ui.maps.ModalWindow

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.ontrackbus.Models.Rutas
import com.android.ontrackbus.Models.Usuarios
import com.android.ontrackbus.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class notify_LockScreen : AppCompatActivity() {

    //clase para loguear usuarios
    private var mAuth: FirebaseAuth? = null

    //variables de botones
    private var btn_abordadosi: Button? = null //variables de botones
    private var btn_abordadono: Button? = null //variables de botones
    private var btn_OtraRutaAbordado: Button? = null

    //variable para el autocomplete textview
    private var actv_OtraRutaAbordado: AutoCompleteTextView? = null

    //variables para actv
    private var CantidadDeRutas = 0

    //arraylist para la parte voy en cmaino
    private val alAbordado = ArrayList<String>()

    //variable que recibe la ruta que esta reviamente seleccionada
    private var rutaSeleccionadaAbordado: String? = null
    //variable que recibe la ruta que esta reviamente seleccionada
    private var rutaSeleccionadaNumero: String? = null

    //text view abordado
    private var tv_Abordado: TextView? = null

    //variables de firebase:
    private var OTBReference: DatabaseReference? = null

    //variable para obtener la ultima localizacion conocida
    private var userFLPC: FusedLocationProviderClient? = null

    //variables de latitud y longitud de la ultima localizacion del usuario
    private var latuser = 0.0
    private var lnguser = 0.0

    //instanciar la clase rutas para poder mandar elnumero de la ruta en base al nombre
    private val oRutasAbordado: Rutas = Rutas()
    private var oUsuariossDatos: Usuarios = Usuarios()

    private var orientacionRuta: String? = null
    private var contactoConfianza:kotlin.String? = null
    private var nombreParada:kotlin.String? = null
    private  var userid:kotlin.String? = null
    private  var numeroParada:kotlin.String? = null
    private  var rutaAbordada:kotlin.String? = null
    private  var rutaAbordadaNumero:kotlin.String? = null

    //bandera para ver si el correo de confianza existe
    private var banderaCorreoConfianzaExistente = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notify_lock_screen)


        val medidasVentana = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(medidasVentana)
        val ancho = medidasVentana.widthPixels
        val alto = medidasVentana.heightPixels
        window.setLayout((ancho + 0.85).toInt(), (alto * 0.3).toInt())

        // Obtienes el Bundle del Intent pasado

        // Obtienes el Bundle del Intent pasado
        val bundle = intent.extras
        CantidadDeRutas = bundle!!.getInt("CantidadDeRutas")
        for (contadorAbordado in 1 until CantidadDeRutas) {
            alAbordado.add(bundle.getString("Ruta$contadorAbordado")!!)
        }

        //descargar orientacion de rut

        //descargar orientacion de rut
        orientacionRuta = bundle.getString("orientacion")

        contactoConfianza = bundle.getString("contactoConfianza")
        //se referencia la clase auth
        //se referencia la clase auth
        mAuth = FirebaseAuth.getInstance()

        //se descarga la ruta seleccionada y se manda la pregunta

        //se descarga la ruta seleccionada y se manda la pregunta
        rutaSeleccionadaAbordado = bundle.getString("rutaSeleccionadaAbordado")
        rutaSeleccionadaNumero = bundle.getString("rutaSeleccionadaNumero")
        tv_Abordado = findViewById(R.id.tv_Abordado)
        tv_Abordado!!.setText("¿Ya abordaste la Ruta $rutaSeleccionadaAbordado?")

        //instanciamos los botones

        //instanciamos los botones
        btn_abordadosi = findViewById(R.id.btn_abordadoSi)
        btn_abordadono = findViewById(R.id.btn_abordadoNo)
        btn_OtraRutaAbordado = findViewById(R.id.btn_OtraRutaAbordado)

        //instanciar actv

        //instanciar actv
        actv_OtraRutaAbordado = findViewById(R.id.actv_OtraRutaAbordado)
        val adapterrutasAbordado =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, alAbordado)
        actv_OtraRutaAbordado!!.setAdapter(adapterrutasAbordado)

        //instancear base de datos

        //instancear base de datos
        OTBReference = FirebaseDatabase.getInstance().reference

        //funcionalidad de los botones

        //funcionalidad de los botones
        funcion_btnAbordadoSI()
        funcion_btnAbordadoNo()
        funcion_btnOtraRutaAbordado()
    }


    //funcionalidad boton si
    private fun funcion_btnAbordadoSI() {


        //se obtiene de la base de datos el nombre de las rutas que estan disponibles a ver en el fragmento y se compara con la que selecciono el usuario para ver si existe.
        OTBReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                btn_abordadosi!!.setOnClickListener {
                    //variable para buscar la uktima ubicacion del usuario
                    userFLPC =
                        LocationServices.getFusedLocationProviderClient(this@notify_LockScreen)
                    if (ActivityCompat.checkSelfPermission(
                            this@notify_LockScreen,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this@notify_LockScreen,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                    }
                    userFLPC!!.getLastLocation()
                        .addOnSuccessListener(this@notify_LockScreen,
                            OnSuccessListener<Location?> { location ->
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    latuser = location.latitude
                                    lnguser = location.longitude

                                    //contador para paradas
                                    var contadorParadas = 1
                                    var latuserresult: Double? = 0.0
                                    var lnguserresult: Double? = 0.0
                                    var latuserresult2: Double? = 0.0
                                    var lnguserresult2: Double? = 0.0
                                    var suma1 = 0.0
                                    var suma2 = 0.0
                                    oRutasAbordado.setLatuser(0.0)
                                    oRutasAbordado.setLnguser(0.0)
                                    for (snapshot2 in dataSnapshot.child("Rutas").child(
                                        rutaSeleccionadaNumero!!
                                    ).child(orientacionRuta!!).children) {
                                        // se cargan marcadores nuevos
                                        val latitud1 = dataSnapshot.child("Rutas")
                                            .child(rutaSeleccionadaNumero!!).child(
                                                orientacionRuta!!
                                            ).child("Parada$contadorParadas")
                                            .child("latitud").value.toString()
                                        val longitud1 = dataSnapshot.child("Rutas").child(
                                            rutaSeleccionadaNumero!!
                                        ).child(orientacionRuta!!).child("Parada$contadorParadas")
                                            .child("longitud").value.toString()
                                        val latitud = latitud1.toDouble()
                                        val longitud = longitud1.toDouble()
                                        latuserresult = latuser - oRutasAbordado!!.getLatuser()!!
                                        lnguserresult = lnguser - oRutasAbordado!!.getLnguser()!!
                                        suma1 =
                                            Math.abs(latuserresult!!) + Math.abs(
                                                lnguserresult!!
                                            )
                                        latuserresult2 = latuser - latitud
                                        lnguserresult2 = lnguser - longitud
                                        suma2 =
                                            Math.abs(latuserresult2) + Math.abs(
                                                lnguserresult2
                                            )
                                        if (latuser === latitud) {
                                            suma2 = suma2 * 2
                                        }
                                        if (lnguser === longitud) {
                                            suma2 = suma2 * 2
                                        }
                                        if (suma2 < suma1) {
                                            oRutasAbordado.setLatuser(latitud)
                                            oRutasAbordado.setLnguser(longitud)
                                            nombreParada = dataSnapshot.child("Rutas")
                                                .child(rutaSeleccionadaNumero!!).child(
                                                    orientacionRuta!!
                                                ).child("Parada$contadorParadas")
                                                .child("tittle").value.toString()
                                            numeroParada = "Parada$contadorParadas"
                                        }
                                        contadorParadas++
                                    }

                                    //se obtiene el id del usuario al que se le mandaaran los datos
                                    for (snapshot3 in dataSnapshot.child("Users").children) {
                                        oUsuariossDatos = snapshot3.getValue(Usuarios::class.java)!!
                                        val correoContactoConfianza: String? =
                                            oUsuariossDatos.getCorreo()
                                        if (contactoConfianza == correoContactoConfianza) {
                                            userid = oUsuariossDatos.getIduser()
                                            banderaCorreoConfianzaExistente = 1
                                        }
                                    }
                                    val nombreCurrentUser = dataSnapshot.child("Users").child(
                                        mAuth!!.currentUser!!.uid
                                    ).child("nombre").value.toString()
                                    val correoCurrentUser = dataSnapshot.child("Users").child(
                                        mAuth!!.currentUser!!.uid
                                    ).child("correo").value.toString()
                                    //objeto que busca el formato de la fecha a obtener
                                    val formateadorfecha = SimpleDateFormat(
                                        " d-MM-yyyy"
                                    )

                                    //esto significa  a jueves 7 de mayo de 2020. "'a' EEEE d 'de' MMMM 'de' yyyy
                                    val formateadorhora = SimpleDateFormat(
                                        "HH:mm:ss"
                                    )
                                    //se obtiene la fecha y hora actual
                                    val fechaDate = Date()
                                    val fecha = formateadorfecha.format(fechaDate)
                                    val Hora = formateadorhora.format(fechaDate)


                                    //funcion para saber si el usuario esta cerca de 200 metros de la parada d elo contrario no podra indicar que esta arriba
                                    val latminimo: Double
                                    val latmaximo: Double
                                    val lngminimo: Double
                                    val lngmaximo: Double
                                    latminimo = oRutasAbordado.getLatuser()!! - 0.002
                                    latmaximo = oRutasAbordado.getLatuser()!! + 0.002
                                    lngminimo = oRutasAbordado.getLnguser()!! - 0.002
                                    lngmaximo = oRutasAbordado.getLnguser()!! + 0.002
                                    if (latuser <= latmaximo && latuser >= latminimo && lnguser <= lngmaximo && lnguser >= lngminimo) {
                                        if (banderaCorreoConfianzaExistente == 1) {
                                            for (contadorReportes in 1..10) {
                                                if (dataSnapshot.child("Users").child(userid.toString())
                                                        .child("ContactoDeConfianza").child(
                                                            "Reporte$contadorReportes"
                                                        ).exists() != true
                                                ) {
                                                    val ReporteContactoConfianza: MutableMap<String, Any> =
                                                        HashMap()
                                                    ReporteContactoConfianza["CorreoRemitente"] =
                                                        correoCurrentUser
                                                    ReporteContactoConfianza["NombreRemitente"] =
                                                        nombreCurrentUser
                                                    ReporteContactoConfianza["FechaAbordado"] =
                                                        fecha
                                                    ReporteContactoConfianza["HoraAbordado"] = Hora
                                                    ReporteContactoConfianza["ParadaAbordada"] =
                                                        this@notify_LockScreen.nombreParada.toString()
                                                    ReporteContactoConfianza["RutaAbordada"] =
                                                        rutaSeleccionadaAbordado!!
                                                    ReporteContactoConfianza["Id_Reporte"] =
                                                        contadorReportes
                                                    //Mandar datos del map string a la base de datos
                                                    OTBReference!!.child("Users").child(userid.toString())
                                                        .child("ContactoDeConfianza").child(
                                                            "Reporte$contadorReportes"
                                                        ).setValue(ReporteContactoConfianza)
                                                        .addOnCompleteListener {
                                                            Toast.makeText(
                                                                applicationContext,
                                                                "Gracias",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    OTBReference!!.child("Rutas")
                                                        .child(rutaSeleccionadaNumero!!).child(
                                                            orientacionRuta!!
                                                        ).child(numeroParada.toString()).child("snnipet")
                                                        .setValue("$Hora $fecha")

                                                    //Enviar info a mi Actividad
                                                    funcion_EnviarInfoMiActiviad(
                                                        fecha, Hora, nombreParada.toString(),
                                                        rutaSeleccionadaAbordado!!
                                                    )
                                                    finish()
                                                    break
                                                } else {
                                                    if (contadorReportes == 10) {
                                                        for (contadorReportesViejos in 2..10) {
                                                            //String reporteviejo = dataSnapshot.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte"+contadorReportesViejos).child("FechaAbordado").getValue().toString();
                                                            val fechaAbordado =
                                                                dataSnapshot.child("Users")
                                                                    .child(userid.toString())
                                                                    .child("ContactoDeConfianza")
                                                                    .child(
                                                                        "Reporte$contadorReportesViejos"
                                                                    )
                                                                    .child("FechaAbordado").value.toString()
                                                            val horaAbordado =
                                                                dataSnapshot.child("Users")
                                                                    .child(userid.toString())
                                                                    .child("ContactoDeConfianza")
                                                                    .child(
                                                                        "Reporte$contadorReportesViejos"
                                                                    )
                                                                    .child("HoraAbordado").value.toString()
                                                            val fechahora =
                                                                fechaAbordado + horaAbordado
                                                            val contadormenosuno =
                                                                contadorReportesViejos - 1
                                                            val fechavieja =
                                                                dataSnapshot.child("Users")
                                                                    .child(userid.toString())
                                                                    .child("ContactoDeConfianza")
                                                                    .child(
                                                                        "Reporte$contadormenosuno"
                                                                    )
                                                                    .child("FechaAbordado").value.toString()
                                                            val horavieja =
                                                                dataSnapshot.child("Users")
                                                                    .child(userid.toString())
                                                                    .child("ContactoDeConfianza")
                                                                    .child(
                                                                        "Reporte$contadormenosuno"
                                                                    )
                                                                    .child("HoraAbordado").value.toString()
                                                            val fechahoravieja =
                                                                fechavieja + horavieja
                                                            if (fechahora.compareTo(fechahoravieja) > 0) {
                                                                val ReporteContactoConfianza: MutableMap<String, Any> =
                                                                    HashMap()
                                                                ReporteContactoConfianza["CorreoRemitente"] =
                                                                    correoCurrentUser
                                                                ReporteContactoConfianza["NombreRemitente"] =
                                                                    nombreCurrentUser
                                                                ReporteContactoConfianza["FechaAbordado"] =
                                                                    fecha
                                                                ReporteContactoConfianza["HoraAbordado"] =
                                                                    Hora
                                                                ReporteContactoConfianza["ParadaAbordada"] =
                                                                    nombreParada.toString()
                                                                ReporteContactoConfianza["RutaAbordada"] =
                                                                    rutaSeleccionadaAbordado!!
                                                                ReporteContactoConfianza["Id_Reporte"] =
                                                                    contadormenosuno
                                                                //Mandar datos del map string a la base de datos
                                                                OTBReference!!.child("Users")
                                                                    .child(userid.toString())
                                                                    .child("ContactoDeConfianza")
                                                                    .child(
                                                                        "Reporte$contadormenosuno"
                                                                    ).setValue(
                                                                        ReporteContactoConfianza
                                                                    )
                                                                    .addOnCompleteListener {
                                                                        Toast.makeText(
                                                                            applicationContext,
                                                                            "Gracias",
                                                                            Toast.LENGTH_LONG
                                                                        ).show()
                                                                    }
                                                                OTBReference!!.child("Rutas")
                                                                    .child(rutaSeleccionadaNumero!!)
                                                                    .child(
                                                                        orientacionRuta!!
                                                                    ).child(numeroParada.toString())
                                                                    .child("snnipet").setValue(
                                                                        "$Hora $fecha"
                                                                    )

                                                                //Enviar info a mi Actividad
                                                                funcion_EnviarInfoMiActiviad(
                                                                    fecha, Hora,
                                                                    nombreParada.toString(),
                                                                    rutaSeleccionadaAbordado!!
                                                                )
                                                                finish()
                                                                break
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            //no hacer nada si no existe el correo e contacto
                                            Toast.makeText(
                                                applicationContext,
                                                "Tu contacto de confianza no tiene una ceunta en OnTrackBus",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            //aqui acaba el verificar si el correo existe
                                            OTBReference!!.child("Rutas")
                                                .child(rutaSeleccionadaNumero!!).child(
                                                    orientacionRuta!!
                                                ).child(numeroParada.toString()).child("snnipet")
                                                .setValue("$Hora $fecha")
                                            //Enviar info a mi Actividad
                                            funcion_EnviarInfoMiActiviad(
                                                fecha, Hora, nombreParada.toString(),
                                                rutaSeleccionadaAbordado!!
                                            )
                                            finish()
                                        }
                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            "Estas demasiado lejos de una parada, porfavor espera acercarte a almenos 200 metros para presionar este boton.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun funcion_btnAbordadoNo() {
        btn_abordadono!!.setOnClickListener {
            Toast.makeText(
                applicationContext,
                "Porfavor selecciona la Ruta que abordaste",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun funcion_btnOtraRutaAbordado() {
        //se obtiene de la base de datos el nombre de las rutas que estan disponibles a ver en el fragmento y se compara con la que selecciono el usuario para ver si existe.
        OTBReference!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("MissingPermission")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                btn_OtraRutaAbordado!!.setOnClickListener {
                    rutaAbordada = actv_OtraRutaAbordado!!.text.toString()
                    val CantidadDeRutasVerificacion = CantidadDeRutas - 1
                    var bandera = 0
                    for (contadorverificacion in 0 until CantidadDeRutasVerificacion) {
                        if (rutaAbordada == alAbordado[contadorverificacion]) {
                            val contadorxd = contadorverificacion + 1
                            rutaSeleccionadaNumero = "Ruta$contadorxd"
                            rutaSeleccionadaAbordado = rutaAbordada
                            bandera = 1
                        }
                    }
                    if (bandera == 1) {
                        //variable para buscar la uktima ubicacion del usuario
                        userFLPC =
                            LocationServices.getFusedLocationProviderClient(this@notify_LockScreen)
                        userFLPC!!.getLastLocation()
                            .addOnSuccessListener(this@notify_LockScreen,
                                OnSuccessListener<Location?> { location ->
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        latuser = location.latitude
                                        lnguser = location.longitude

                                        //contador para paradas
                                        var contadorParadas = 1
                                        var latuserresult: Double? = 0.0
                                        var lnguserresult: Double? = 0.0
                                        var latuserresult2: Double? = 0.0
                                        var lnguserresult2: Double? = 0.0
                                        var suma1 = 0.0
                                        var suma2 = 0.0
                                        oRutasAbordado.setLatuser(0.0)
                                        oRutasAbordado.setLnguser(0.0)
                                        for (snapshot2 in dataSnapshot.child("Rutas")
                                            .child(
                                                rutaSeleccionadaNumero!!
                                            ).child(orientacionRuta!!).children) {
                                            // se cargan marcadores nuevos
                                            val latitud1 = dataSnapshot.child("Rutas").child(
                                                rutaSeleccionadaNumero!!
                                            ).child(orientacionRuta!!)
                                                .child("Parada$contadorParadas")
                                                .child("latitud").value.toString()
                                            val longitud1 = dataSnapshot.child("Rutas").child(
                                                rutaSeleccionadaNumero!!
                                            ).child(orientacionRuta!!)
                                                .child("Parada$contadorParadas")
                                                .child("longitud").value.toString()
                                            val latitud = latitud1.toDouble()
                                            val longitud = longitud1.toDouble()
                                            latuserresult = latuser - oRutasAbordado.getLatuser()!!
                                            lnguserresult = lnguser - oRutasAbordado.getLnguser()!!
                                            suma1 =
                                                Math.abs(latuserresult!!) + Math.abs(
                                                    lnguserresult!!
                                                )
                                            latuserresult2 = latuser - latitud
                                            lnguserresult2 = lnguser - longitud
                                            suma2 =
                                                Math.abs(latuserresult2) + Math.abs(
                                                    lnguserresult2
                                                )
                                            if (latuser === latitud) {
                                                suma2 = suma2 * 2
                                            }
                                            if (lnguser === longitud) {
                                                suma2 = suma2 * 2
                                            }
                                            if (suma2 < suma1) {
                                                oRutasAbordado.setLatuser(latitud)
                                                oRutasAbordado.setLnguser(longitud)
                                                nombreParada = dataSnapshot.child("Rutas")
                                                    .child(rutaSeleccionadaNumero!!).child(
                                                        orientacionRuta!!
                                                    ).child("Parada$contadorParadas")
                                                    .child("tittle").value.toString()
                                                numeroParada = "Parada$contadorParadas"
                                            }
                                            contadorParadas++
                                        }

                                        //se obtiene el id del usuario al que se le mandaaran los datos
                                        for (snapshot3 in dataSnapshot.child("Users").children) {
                                            oUsuariossDatos =
                                                snapshot3.getValue(Usuarios::class.java)!!
                                            val correoContactoConfianza: String =
                                                oUsuariossDatos.getCorreo().toString()
                                            if (contactoConfianza == correoContactoConfianza) {
                                                userid = oUsuariossDatos.getIduser()
                                                banderaCorreoConfianzaExistente = 1
                                            }
                                        }
                                        val nombreCurrentUser = dataSnapshot.child("Users").child(
                                            mAuth!!.currentUser!!.uid
                                        ).child("nombre").value.toString()
                                        val correoCurrentUser = dataSnapshot.child("Users").child(
                                            mAuth!!.currentUser!!.uid
                                        ).child("correo").value.toString()
                                        //objeto que busca el formato de la fecha a obtener
                                        val formateadorfecha = SimpleDateFormat(
                                            " d-MM-yyyy"
                                        )

                                        //esto significa  a jueves 7 de mayo de 2020. "'a' EEEE d 'de' MMMM 'de' yyyy
                                        val formateadorhora = SimpleDateFormat(
                                            "HH:mm:ss"
                                        )
                                        //se obtiene la fecha y hora actual
                                        val fechaDate = Date()
                                        val fecha = formateadorfecha.format(fechaDate)
                                        val Hora = formateadorhora.format(fechaDate)


                                        //funcion para saber si el usuario esta cerca de 200 metros de la parada d elo contrario no podra indicar que esta arriba
                                        val latminimo: Double
                                        val latmaximo: Double
                                        val lngminimo: Double
                                        val lngmaximo: Double
                                        latminimo = oRutasAbordado.getLatuser()!! - 0.002
                                        latmaximo = oRutasAbordado.getLatuser()!! + 0.002
                                        lngminimo = oRutasAbordado.getLnguser()!! - 0.002
                                        lngmaximo = oRutasAbordado.getLnguser()!! + 0.002
                                        if (latuser <= latmaximo && latuser >= latminimo && lnguser <= lngmaximo && lnguser >= lngminimo) {
                                            if (banderaCorreoConfianzaExistente == 1) {
                                                for (contadorReportes in 1..10) {
                                                    if (dataSnapshot.child("Users").child(userid.toString())
                                                            .child("ContactoDeConfianza").child(
                                                                "Reporte$contadorReportes"
                                                            ).exists() != true
                                                    ) {
                                                        val ReporteContactoConfianza: MutableMap<String, Any> =
                                                            HashMap()
                                                        ReporteContactoConfianza["CorreoRemitente"] =
                                                            correoCurrentUser
                                                        ReporteContactoConfianza["NombreRemitente"] =
                                                            nombreCurrentUser
                                                        ReporteContactoConfianza["FechaAbordado"] =
                                                            fecha
                                                        ReporteContactoConfianza["HoraAbordado"] =
                                                            Hora
                                                        ReporteContactoConfianza["ParadaAbordada"] =
                                                            nombreParada.toString()
                                                        ReporteContactoConfianza["RutaAbordada"] =
                                                            rutaSeleccionadaAbordado!!
                                                        ReporteContactoConfianza["Id_Reporte"] =
                                                            contadorReportes
                                                        //Mandar datos del map string a la base de datos
                                                        OTBReference!!.child("Users").child(userid.toString())
                                                            .child("ContactoDeConfianza").child(
                                                                "Reporte$contadorReportes"
                                                            ).setValue(ReporteContactoConfianza)
                                                            .addOnCompleteListener {
                                                                Toast.makeText(
                                                                    applicationContext,
                                                                    "Gracias",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                            }
                                                        OTBReference!!.child("Rutas")
                                                            .child(rutaSeleccionadaNumero!!).child(
                                                                orientacionRuta!!
                                                            ).child(numeroParada.toString()).child("snnipet")
                                                            .setValue(
                                                                "$Hora $fecha"
                                                            )

                                                        //Enviar info a mi Actividad
                                                        funcion_EnviarInfoMiActiviad(
                                                            fecha, Hora, nombreParada.toString(),
                                                            rutaSeleccionadaAbordado!!
                                                        )
                                                        finish()
                                                        break
                                                    } else {
                                                        if (contadorReportes == 10) {
                                                            for (contadorReportesViejos in 2..10) {
                                                                //String reporteviejo = dataSnapshot.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte"+contadorReportesViejos).child("FechaAbordado").getValue().toString();
                                                                val fechaAbordado =
                                                                    dataSnapshot.child("Users")
                                                                        .child(userid.toString())
                                                                        .child("ContactoDeConfianza")
                                                                        .child(
                                                                            "Reporte$contadorReportesViejos"
                                                                        )
                                                                        .child("FechaAbordado").value.toString()
                                                                val horaAbordado =
                                                                    dataSnapshot.child("Users")
                                                                        .child(userid.toString())
                                                                        .child("ContactoDeConfianza")
                                                                        .child(
                                                                            "Reporte$contadorReportesViejos"
                                                                        )
                                                                        .child("HoraAbordado").value.toString()
                                                                val fechahora =
                                                                    fechaAbordado + horaAbordado
                                                                val contadormenosuno =
                                                                    contadorReportesViejos - 1
                                                                val fechavieja =
                                                                    dataSnapshot.child("Users")
                                                                        .child(userid.toString())
                                                                        .child("ContactoDeConfianza")
                                                                        .child(
                                                                            "Reporte$contadormenosuno"
                                                                        )
                                                                        .child("FechaAbordado").value.toString()
                                                                val horavieja =
                                                                    dataSnapshot.child("Users")
                                                                        .child(userid.toString())
                                                                        .child("ContactoDeConfianza")
                                                                        .child(
                                                                            "Reporte$contadormenosuno"
                                                                        )
                                                                        .child("HoraAbordado").value.toString()
                                                                val fechahoravieja =
                                                                    fechavieja + horavieja
                                                                if (fechahora.compareTo(
                                                                        fechahoravieja
                                                                    ) > 0
                                                                ) {
                                                                    val ReporteContactoConfianza: MutableMap<String, Any> =
                                                                        HashMap()
                                                                    ReporteContactoConfianza["CorreoRemitente"] =
                                                                        correoCurrentUser
                                                                    ReporteContactoConfianza["NombreRemitente"] =
                                                                        nombreCurrentUser
                                                                    ReporteContactoConfianza["FechaAbordado"] =
                                                                        fecha
                                                                    ReporteContactoConfianza["HoraAbordado"] =
                                                                        Hora
                                                                    ReporteContactoConfianza["ParadaAbordada"] =
                                                                        nombreParada.toString()
                                                                    ReporteContactoConfianza["RutaAbordada"] =
                                                                        rutaSeleccionadaAbordado!!
                                                                    ReporteContactoConfianza["Id_Reporte"] =
                                                                        contadormenosuno
                                                                    //Mandar datos del map string a la base de datos
                                                                    OTBReference!!.child("Users")
                                                                        .child(userid.toString())
                                                                        .child("ContactoDeConfianza")
                                                                        .child(
                                                                            "Reporte$contadormenosuno"
                                                                        ).setValue(
                                                                            ReporteContactoConfianza
                                                                        ).addOnCompleteListener {
                                                                            Toast.makeText(
                                                                                applicationContext,
                                                                                "Gracias",
                                                                                Toast.LENGTH_LONG
                                                                            ).show()
                                                                        }
                                                                    OTBReference!!.child("Rutas")
                                                                        .child(
                                                                            rutaSeleccionadaNumero!!
                                                                        ).child(orientacionRuta!!)
                                                                        .child(numeroParada.toString())
                                                                        .child("snnipet").setValue(
                                                                            "$Hora $fecha"
                                                                        )

                                                                    //Enviar info a mi Actividad
                                                                    funcion_EnviarInfoMiActiviad(
                                                                        fecha, Hora,
                                                                        nombreParada.toString(),
                                                                        rutaSeleccionadaAbordado!!
                                                                    )
                                                                    finish()
                                                                    break
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                //no hacer nada si no existe el correo e contacto
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Tu contacto de confianza no tiene una ceunta en OnTrackBus",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                //aqui acaba el verificar si el correo existe
                                                OTBReference!!.child("Rutas")
                                                    .child(rutaSeleccionadaNumero!!).child(
                                                        orientacionRuta!!
                                                    ).child(numeroParada.toString()).child("snnipet")
                                                    .setValue("$Hora $fecha")
                                                //Enviar info a mi Actividad
                                                funcion_EnviarInfoMiActiviad(
                                                    fecha, Hora, nombreParada.toString(),
                                                    rutaSeleccionadaAbordado!!
                                                )
                                                finish()
                                            }
                                        } else {
                                            Toast.makeText(
                                                applicationContext,
                                                "Estas demasiado lejos de una parada, porfavor espera acercarte a almenos 200 metros para presionar este boton.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                })
                    } else if (rutaAbordada == "") {
                        actv_OtraRutaAbordado!!.error = "Porfavor Ingrese una Ruta"
                        Toast.makeText(
                            applicationContext,
                            "Porfavor Ingrese una Ruta",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        actv_OtraRutaAbordado!!.error = "Porfavor Ingrese una Ruta Valida"
                        Toast.makeText(
                            applicationContext,
                            "Porfavor Ingrese una Ruta Valida",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } //aqui acaba el onclick listener
            } //aqui acaba el on data change

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun funcion_EnviarInfoMiActiviad(
        fecha: String,
        Hora: String,
        nombreParada: String,
        rutaSeleccionadaAbordado: String
    ) {
        val idusuario = mAuth!!.currentUser!!.uid

        //objeto que busca el formato de la fecha a obtener
        val formateadorfecha = SimpleDateFormat(
            "EEEE"
        )

        //se obtiene la fecha y hora actual
        val fechaDate = Date()
        val ObtenerDia = formateadorfecha.format(fechaDate)
        OTBReference!!.child("Users").child(idusuario).child("MiActividad").child(ObtenerDia)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (contadorReportes in 1..10) {
                        if (dataSnapshot.child("Id_ReporteMA$contadorReportes").exists() != true) {
                            val ReporteMiActividad: MutableMap<String, Any> = HashMap()
                            ReporteMiActividad["FechaAbordado"] = fecha
                            ReporteMiActividad["HoraAbordado"] = Hora
                            ReporteMiActividad["Id_ReporteMA"] = contadorReportes
                            ReporteMiActividad["ParadaAbordada"] = nombreParada
                            ReporteMiActividad["RutaAbordada"] = rutaSeleccionadaAbordado
                            //Mandar datos del map string a la base de datos
                            OTBReference!!.child("Users").child(idusuario).child("MiActividad")
                                .child(ObtenerDia).child(
                                    "Id_ReporteMA$contadorReportes"
                                ).setValue(ReporteMiActividad).addOnCompleteListener { }
                            break
                        } else {
                            if (contadorReportes == 10) {
                                for (contadorReportesViejos in 2..10) {
                                    val fechaAbordado =
                                        dataSnapshot.child("Id_ReporteMA$contadorReportesViejos")
                                            .child("FechaAbordado").value.toString()
                                    val horaAbordado =
                                        dataSnapshot.child("Id_ReporteMA$contadorReportesViejos")
                                            .child("HoraAbordado").value.toString()
                                    val fechahora = fechaAbordado + horaAbordado
                                    val contadormenosuno = contadorReportesViejos - 1
                                    val fechavieja =
                                        dataSnapshot.child("Id_ReporteMA$contadormenosuno")
                                            .child("FechaAbordado").value.toString()
                                    val horavieja =
                                        dataSnapshot.child("Id_ReporteMA$contadormenosuno")
                                            .child("HoraAbordado").value.toString()
                                    val fechahoravieja = fechavieja + horavieja
                                    if (fechahora.compareTo(fechahoravieja) > 0) {
                                        val ReporteMiActividad: MutableMap<String, Any> = HashMap()
                                        ReporteMiActividad["FechaAbordado"] = fecha
                                        ReporteMiActividad["HoraAbordado"] = Hora
                                        ReporteMiActividad["Id_ReporteMA"] = contadormenosuno
                                        ReporteMiActividad["ParadaAbordada"] = nombreParada
                                        ReporteMiActividad["RutaAbordada"] =
                                            rutaSeleccionadaAbordado
                                        //Mandar datos del map string a la base de datos
                                        OTBReference!!.child("Users").child(idusuario)
                                            .child("MiActividad").child(ObtenerDia).child(
                                                "Id_ReporteMA$contadormenosuno"
                                            ).setValue(ReporteMiActividad).addOnCompleteListener { }
                                        break
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }


}