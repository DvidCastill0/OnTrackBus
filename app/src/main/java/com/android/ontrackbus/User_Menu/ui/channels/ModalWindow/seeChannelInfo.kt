package com.android.ontrackbus.User_Menu.ui.channels.ModalWindow

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.ontrackbus.Models.Canales
import com.android.ontrackbus.Models.Rutas
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
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class seeChannelInfo : AppCompatActivity() {

    //variables firebase
    private var OTBReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null

    //bundle para recibir datos
    private var bundleCanalAbierto: Bundle? = null

    //instancear elementos graficos
    private var tv_Nombre: TextView? = null
    private var tv_Tarifa: TextView? = null
    private var tv_Reportes: TextView? = null
    private var tv_Sugerencias: TextView? = null

    private var btn_Aceptar: Button? = null

    private var lv_CanalAbierto: ListView? = null

    //datos importantes para pasar
    private var nombreCanal: String? = null
    private var numeroCanal = 0

    //variable para obtener la ultima localizacion conocida
    private var userFLPC: FusedLocationProviderClient? = null

    //variable apra ver la parada mas cercana a la que se hara el reporte
    //instanciar la clase rutas para poder mandar elnumero de la ruta en base al nombre
    private val oRutasAbordado: Rutas = Rutas()
    private var nombreParada: String? = null

    //arraylist de reportes
    private val arraylist_Choques: ArrayList<Canales> = ArrayList<Canales>()
    private val arraylist_CalleCerrada: ArrayList<Canales> = ArrayList<Canales>()
    private val arraylist_Estancamientos: ArrayList<Canales> = ArrayList<Canales>()
    private val arraylist_NoDaParadas: ArrayList<Canales> = ArrayList<Canales>()
    private val arraylist_MalaCondicionVehiculo: ArrayList<Canales> = ArrayList<Canales>()
    private val arraylist_MalAseo: ArrayList<Canales> = ArrayList<Canales>()
    private val arraylist_MalaActitudDelChofer: ArrayList<Canales> = ArrayList<Canales>()
    private val arraylist_Asaltos: ArrayList<Canales> = ArrayList<Canales>()
    private val arraylist_Peleas: ArrayList<Canales> = ArrayList<Canales>()

    private val listviewarray = ArrayList<String>()

    //private ArrayList<Canales> arraylist_EliminarBuscandoDiferentes= new ArrayList<>();
    private val DiferenciasParadaoFecha: ArrayList<Canales> = ArrayList<Canales>()
    private val ContadorDiferencias = ArrayList<Int>()

    //obejtos para descrgar datos de hijos
    //objeto para obtener hijos de canales de rutas
    private var oCanales: Canales? = null
    private val array_reportesCanales: ArrayList<Canales> = ArrayList<Canales>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_channel_info)
        val medidasVentana = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(medidasVentana)
        val ancho = medidasVentana.widthPixels
        val alto = medidasVentana.heightPixels
        window.setLayout((ancho + 0.85).toInt(), (alto * 0.85).toInt())

        //recibir vlores de intent

        //recibir vlores de intent
        bundleCanalAbierto = intent.extras
        nombreCanal = bundleCanalAbierto!!.getString("CanalNombre")
        numeroCanal = bundleCanalAbierto!!.getInt("CanalNumero")

        //instanciar variables graficas


        //instanciar variables graficas
        tv_Nombre = findViewById(R.id.tv_CanalAbierto)
        tv_Nombre!!.setText(nombreCanal)
        tv_Tarifa = findViewById(R.id.tv_TarifaCanalAbierto)
        tv_Reportes = findViewById(R.id.tv_Reportes)
        tv_Sugerencias = findViewById(R.id.tv_Sugerencias)

        tv_Sugerencias!!.setOnClickListener(View.OnClickListener {
            val intentSugerencias = Intent(
                this@seeChannelInfo,
                suggestions::class.java
            )
            //otra ruta bundle para pasar datos
            val BundleSugerencias = Bundle()
            BundleSugerencias.putString("CanalNumero", "Canal$numeroCanal")
            intentSugerencias.putExtras(BundleSugerencias)
            startActivity(intentSugerencias)
        })

        btn_Aceptar = findViewById(R.id.btn_AceptarCanalAbierto)
        funcionalidad_btn_aceptar()
        lv_CanalAbierto = findViewById(R.id.lv_ReportesCanalesAbiertos)


        //instancear variables de firebase


        //instancear variables de firebase
        OTBReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        funcionalidadbtn_Hacer_Reporte()


        OTBReference!!.child("Canales").child("Canal$numeroCanal")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val dardecimales = DecimalFormat("#.00")
                    val tarifaFloat = dataSnapshot.child("Tarifa").value.toString().toFloat()

                    //dejar limpios todos lso arrays
                    array_reportesCanales.clear()
                    arraylist_Choques.clear()
                    arraylist_CalleCerrada.clear()
                    arraylist_Estancamientos.clear()
                    arraylist_NoDaParadas.clear()
                    arraylist_MalaCondicionVehiculo.clear()
                    arraylist_MalAseo.clear()
                    arraylist_MalaActitudDelChofer.clear()
                    arraylist_Asaltos.clear()
                    arraylist_Peleas.clear()
                    tv_Tarifa?.setText("Tarifa: $" + dardecimales.format(tarifaFloat.toDouble()))
                    listviewarray.clear()
                    for (snapshotReportes in dataSnapshot.child("Reportes").children) {
                        oCanales = snapshotReportes.getValue(Canales::class.java)
                        array_reportesCanales.add(oCanales!!)
                    }
                    for (contadorReportesCanales in array_reportesCanales.indices) {
                        //aqui meter tod0 dentro de un if para que si tienen mas d eun dia se eliminen


                        //reportes choques
                        if (array_reportesCanales[contadorReportesCanales].Id_ReporteCanal.equals("RP-00-00")) {
                            //objeto que busca el formato de la fecha a obtener
                            val formateadorfecha = SimpleDateFormat(
                                "HH:mm:ss d-MM-yyyy"
                            )

                            //se obtiene la fecha y hora actual
                            val fechaDate = Date()
                            val fecha = formateadorfecha.format(fechaDate)
                            var fechaactual: Date? = null
                            var fechaMarcador: Date? = null
                            try {
                                fechaactual = formateadorfecha.parse(fecha)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            try {
                                fechaMarcador =
                                    formateadorfecha.parse(array_reportesCanales[contadorReportesCanales].Hora_DeReporteCanal)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            val diferenciaEn_ms = fechaactual!!.time - fechaMarcador!!.time
                            val Horas = diferenciaEn_ms / 3600000
                            if (Horas > 2) {
                                val idclave: String? =
                                    array_reportesCanales[contadorReportesCanales].Id_Clave
                                OTBReference!!.child("Canales").child("Canal$numeroCanal")
                                    .child("Reportes").child(idclave!!).removeValue()
                            } else {
                                arraylist_Choques.add(array_reportesCanales[contadorReportesCanales])
                            }
                        }
                        //reportes Calle cerrada
                        if (array_reportesCanales[contadorReportesCanales].Id_ReporteCanal.equals("RP-00-01")) {

                            //objeto que busca el formato de la fecha a obtener
                            val formateadorfecha = SimpleDateFormat(
                                "HH:mm:ss d-MM-yyyy"
                            )

                            //se obtiene la fecha y hora actual
                            val fechaDate = Date()
                            val fecha = formateadorfecha.format(fechaDate)
                            var fechaactual: Date? = null
                            var fechaMarcador: Date? = null
                            try {
                                fechaactual = formateadorfecha.parse(fecha)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            try {
                                fechaMarcador =
                                    formateadorfecha.parse(array_reportesCanales[contadorReportesCanales].Hora_DeReporteCanal)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            val diferenciaEn_ms = fechaactual!!.time - fechaMarcador!!.time
                            val Horas = diferenciaEn_ms / 3600000
                            if (Horas > 4) {
                                val idclave: String? =
                                    array_reportesCanales[contadorReportesCanales].Id_Clave
                                OTBReference!!.child("Canales").child("Canal$numeroCanal")
                                    .child("Reportes").child(idclave.toString()).removeValue()
                            } else {
                                arraylist_CalleCerrada.add(array_reportesCanales[contadorReportesCanales])
                            }
                        }
                        //reportes Estancamientos
                        if (array_reportesCanales[contadorReportesCanales].Id_ReporteCanal.equals("RP-01")) {

                            //objeto que busca el formato de la fecha a obtener
                            val formateadorfecha = SimpleDateFormat(
                                "HH:mm:ss d-MM-yyyy"
                            )

                            //se obtiene la fecha y hora actual
                            val fechaDate = Date()
                            val fecha = formateadorfecha.format(fechaDate)
                            var fechaactual: Date? = null
                            var fechaMarcador: Date? = null
                            try {
                                fechaactual = formateadorfecha.parse(fecha)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            try {
                                fechaMarcador =
                                    formateadorfecha.parse(array_reportesCanales[contadorReportesCanales].Hora_DeReporteCanal)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            val diferenciaEn_ms = fechaactual!!.time - fechaMarcador!!.time
                            val Horas = diferenciaEn_ms / 3600000
                            if (Horas > 2) {
                                val idclave: String? =
                                    array_reportesCanales[contadorReportesCanales].Id_Clave
                                OTBReference!!.child("Canales").child("Canal$numeroCanal")
                                    .child("Reportes").child(idclave.toString()).removeValue()
                            } else {
                                arraylist_Estancamientos.add(array_reportesCanales[contadorReportesCanales])
                            }
                        }
                        //reportes -no da Parada
                        if (array_reportesCanales[contadorReportesCanales].Id_ReporteCanal.equals("RP-02")) {

                            //objeto que busca el formato de la fecha a obtener
                            val formateadorfecha = SimpleDateFormat(
                                "HH:mm:ss d-MM-yyyy"
                            )

                            //se obtiene la fecha y hora actual
                            val fechaDate = Date()
                            val fecha = formateadorfecha.format(fechaDate)
                            var fechaactual: Date? = null
                            var fechaMarcador: Date? = null
                            try {
                                fechaactual = formateadorfecha.parse(fecha)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            try {
                                fechaMarcador =
                                    formateadorfecha.parse(array_reportesCanales[contadorReportesCanales].Hora_DeReporteCanal)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            val diferenciaEn_ms = fechaactual!!.time - fechaMarcador!!.time
                            val Horas = diferenciaEn_ms / 3600000
                            if (Horas > 1) {
                                val idclave: String? =
                                    array_reportesCanales[contadorReportesCanales].Id_Clave
                                OTBReference!!.child("Canales").child("Canal$numeroCanal")
                                    .child("Reportes").child(idclave!!).removeValue()
                            } else {
                                arraylist_NoDaParadas.add(array_reportesCanales[contadorReportesCanales])
                            }
                        }
                        //reportes Mala condicion del vehiculo
                        if (array_reportesCanales[contadorReportesCanales].Id_ReporteCanal.equals("RP-03-00")) {

                            //objeto que busca el formato de la fecha a obtener
                            val formateadorfecha = SimpleDateFormat(
                                "HH:mm:ss d-MM-yyyy"
                            )

                            //se obtiene la fecha y hora actual
                            val fechaDate = Date()
                            val fecha = formateadorfecha.format(fechaDate)
                            var fechaactual: Date? = null
                            var fechaMarcador: Date? = null
                            try {
                                fechaactual = formateadorfecha.parse(fecha)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            try {
                                fechaMarcador =
                                    formateadorfecha.parse(array_reportesCanales[contadorReportesCanales].Hora_DeReporteCanal)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            val diferenciaEn_ms = fechaactual!!.time - fechaMarcador!!.time
                            val minutos = diferenciaEn_ms / 60000
                            if (minutos > 30) {
                                val horas = minutos / 60
                                val dias = horas / 24
                                if (dias > 30) {
                                    val idclave: String? =
                                        array_reportesCanales[contadorReportesCanales].Id_Clave
                                    OTBReference!!.child("Canales").child("Canal$numeroCanal")
                                        .child("Reportes").child(idclave!!).removeValue()
                                }
                            } else {
                                arraylist_MalaCondicionVehiculo.add(array_reportesCanales[contadorReportesCanales])
                            }
                        }
                        //reportes Mal Aseo
                        if (array_reportesCanales[contadorReportesCanales].Id_ReporteCanal.equals("RP-03-01")) {

                            //objeto que busca el formato de la fecha a obtener
                            val formateadorfecha = SimpleDateFormat(
                                "HH:mm:ss d-MM-yyyy"
                            )

                            //se obtiene la fecha y hora actual
                            val fechaDate = Date()
                            val fecha = formateadorfecha.format(fechaDate)
                            var fechaactual: Date? = null
                            var fechaMarcador: Date? = null
                            try {
                                fechaactual = formateadorfecha.parse(fecha)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            try {
                                fechaMarcador =
                                    formateadorfecha.parse(array_reportesCanales[contadorReportesCanales].Hora_DeReporteCanal)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            val diferenciaEn_ms = fechaactual!!.time - fechaMarcador!!.time
                            val minutos = diferenciaEn_ms / 60000
                            if (minutos > 30) {
                                val horas = minutos / 60
                                val dias = horas / 24
                                if (dias > 30) {
                                    val idclave: String? =
                                        array_reportesCanales[contadorReportesCanales].Id_Clave
                                    OTBReference!!.child("Canales").child("Canal$numeroCanal")
                                        .child("Reportes").child(idclave!!).removeValue()
                                }
                            } else {
                                arraylist_MalAseo.add(array_reportesCanales[contadorReportesCanales])
                            }
                        }
                        //reportes Mal Actitud de chofer
                        if (array_reportesCanales[contadorReportesCanales].Id_ReporteCanal.equals("RP-03-02")) {

                            //objeto que busca el formato de la fecha a obtener
                            val formateadorfecha = SimpleDateFormat(
                                "HH:mm:ss d-MM-yyyy"
                            )

                            //se obtiene la fecha y hora actual
                            val fechaDate = Date()
                            val fecha = formateadorfecha.format(fechaDate)
                            var fechaactual: Date? = null
                            var fechaMarcador: Date? = null
                            try {
                                fechaactual = formateadorfecha.parse(fecha)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            try {
                                fechaMarcador =
                                    formateadorfecha.parse(array_reportesCanales[contadorReportesCanales].Hora_DeReporteCanal)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            val diferenciaEn_ms = fechaactual!!.time - fechaMarcador!!.time
                            val minutos = diferenciaEn_ms / 60000
                            if (minutos > 30) {
                                val horas = minutos / 60
                                val dias = horas / 24
                                if (dias > 30) {
                                    val idclave: String? =
                                        array_reportesCanales[contadorReportesCanales].Id_Clave
                                    OTBReference!!.child("Canales").child("Canal$numeroCanal")
                                        .child("Reportes").child(idclave!!).removeValue()
                                }
                            } else {
                                arraylist_MalaActitudDelChofer.add(array_reportesCanales[contadorReportesCanales])
                            }
                        }
                        //reportes asaltoo
                        if (array_reportesCanales[contadorReportesCanales].Id_ReporteCanal.equals("RP-04-00")) {

                            //objeto que busca el formato de la fecha a obtener
                            val formateadorfecha = SimpleDateFormat(
                                "HH:mm:ss d-MM-yyyy"
                            )

                            //se obtiene la fecha y hora actual
                            val fechaDate = Date()
                            val fecha = formateadorfecha.format(fechaDate)
                            var fechaactual: Date? = null
                            var fechaMarcador: Date? = null
                            try {
                                fechaactual = formateadorfecha.parse(fecha)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            try {
                                fechaMarcador =
                                    formateadorfecha.parse(array_reportesCanales[contadorReportesCanales].Hora_DeReporteCanal)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            val diferenciaEn_ms = fechaactual!!.time - fechaMarcador!!.time
                            val Horas = diferenciaEn_ms / 3600000
                            if (Horas > 2) {
                                val idclave: String? =
                                    array_reportesCanales[contadorReportesCanales].Id_Clave
                                OTBReference!!.child("Canales").child("Canal$numeroCanal")
                                    .child("Reportes").child(idclave!!).removeValue()
                            } else {
                                arraylist_Asaltos.add(array_reportesCanales[contadorReportesCanales])
                            }
                        }
                        //reportes pelea
                        if (array_reportesCanales[contadorReportesCanales].Id_ReporteCanal.equals("RP-04-01")) {

                            //objeto que busca el formato de la fecha a obtener
                            val formateadorfecha = SimpleDateFormat(
                                "HH:mm:ss d-MM-yyyy"
                            )

                            //se obtiene la fecha y hora actual
                            val fechaDate = Date()
                            val fecha = formateadorfecha.format(fechaDate)
                            var fechaactual: Date? = null
                            var fechaMarcador: Date? = null
                            try {
                                fechaactual = formateadorfecha.parse(fecha)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            try {
                                fechaMarcador =
                                    formateadorfecha.parse(array_reportesCanales[contadorReportesCanales].Hora_DeReporteCanal)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            val diferenciaEn_ms = fechaactual!!.time - fechaMarcador!!.time
                            val Horas = diferenciaEn_ms / 3600000
                            if (Horas > 2) {
                                val idclave: String? =
                                    array_reportesCanales[contadorReportesCanales].Id_Clave
                                OTBReference!!.child("Canales").child("Canal$numeroCanal")
                                    .child("Reportes").child(idclave!!).removeValue()
                            } else {
                                arraylist_Peleas.add(array_reportesCanales[contadorReportesCanales])
                            }
                        } //findeif
                    } //fin de for
                    DiferenciasParadaoFecha.clear()
                    ContadorDiferencias.clear()
                    //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                    if (arraylist_Choques.size >= 3) {
                        for (contaorParadasNombre in arraylist_Choques.indices) {
                            val paradaLugar: String? =
                                arraylist_Choques[contaorParadasNombre].LugarDelReporte
                            var banderayaseagrego = 0
                            for (lugaresAgregar in DiferenciasParadaoFecha.indices) {
                                if (paradaLugar == DiferenciasParadaoFecha[lugaresAgregar].LugarDelReporte) {
                                    banderayaseagrego = 1
                                }
                            }
                            if (banderayaseagrego == 0) {
                                DiferenciasParadaoFecha.add(arraylist_Choques[contaorParadasNombre])
                                ContadorDiferencias.add(0)
                            }
                        }

                        //for que busca cuantas paradas coinciden con cada diferencia
                        for (ParadasDiferencias in DiferenciasParadaoFecha.indices) {
                            val paradAcomparar: String? =
                                DiferenciasParadaoFecha[ParadasDiferencias].LugarDelReporte
                            for (buscarTodo in arraylist_Choques.indices) {
                                if (paradAcomparar == arraylist_Choques[buscarTodo].LugarDelReporte) {
                                    ContadorDiferencias[ParadasDiferencias] =
                                        ContadorDiferencias[ParadasDiferencias] + 1
                                }
                            }
                        }


                        //iterar entre los que tuvieron la misma parada 3 veces
                        for (contadorfech in DiferenciasParadaoFecha.indices) {
                            if (ContadorDiferencias[contadorfech] >= 3) {
                                //objeto que busca el formato de la fecha a obtener
                                val nuevoformato = SimpleDateFormat(
                                    "hh:mm:ss a"
                                )
                                val formateadorfecha = SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy"
                                )
                                var fechaMarcador: Date? = null
                                try {
                                    fechaMarcador =
                                        formateadorfecha.parse(DiferenciasParadaoFecha[contadorfech].Hora_DeReporteCanal)
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }
                                val hora = nuevoformato.format(fechaMarcador)
                                listviewarray.add("Se ha reportado un choque cerca de la parada " + DiferenciasParadaoFecha[contadorfech].LugarDelReporte + " a las " + hora + " toma tus precauciones.")
                            }
                        } //hasta aqui
                    }


                    //comprobcion Calle cerrada
                    DiferenciasParadaoFecha.clear()
                    ContadorDiferencias.clear()
                    //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                    if (arraylist_CalleCerrada.size >= 3) {
                        for (contaorParadasNombre in arraylist_CalleCerrada.indices) {
                            val paradaLugar: String? =
                                arraylist_CalleCerrada[contaorParadasNombre].LugarDelReporte
                            var banderayaseagrego = 0
                            for (lugaresAgregar in DiferenciasParadaoFecha.indices) {
                                if (paradaLugar == DiferenciasParadaoFecha[lugaresAgregar].LugarDelReporte) {
                                    banderayaseagrego = 1
                                }
                            }
                            if (banderayaseagrego == 0) {
                                DiferenciasParadaoFecha.add(arraylist_CalleCerrada[contaorParadasNombre])
                                ContadorDiferencias.add(0)
                            }
                        }

                        //for que busca cuantas paradas coinciden con cada diferencia
                        for (ParadasDiferencias in DiferenciasParadaoFecha.indices) {
                            val paradAcomparar: String? =
                                DiferenciasParadaoFecha[ParadasDiferencias].LugarDelReporte
                            for (buscarTodo in arraylist_CalleCerrada.indices) {
                                if (paradAcomparar == arraylist_CalleCerrada[buscarTodo].LugarDelReporte) {
                                    ContadorDiferencias[ParadasDiferencias] =
                                        ContadorDiferencias[ParadasDiferencias] + 1
                                }
                            }
                        }


                        //iterar entre los que tuvieron la misma parada 3 veces
                        for (contadorfech in DiferenciasParadaoFecha.indices) {
                            if (ContadorDiferencias[contadorfech] >= 3) {
                                //objeto que busca el formato de la fecha a obtener
                                val nuevoformato = SimpleDateFormat(
                                    "hh:mm:ss a"
                                )
                                val formateadorfecha = SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy"
                                )
                                var fechaMarcador: Date? = null
                                try {
                                    fechaMarcador =
                                        formateadorfecha.parse(DiferenciasParadaoFecha[contadorfech].Hora_DeReporteCanal)
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }
                                val hora = nuevoformato.format(fechaMarcador)
                                listviewarray.add("Se ha reportado que hay una calle cerrada cerca de la parada " + DiferenciasParadaoFecha[contadorfech].LugarDelReporte + " a las " + hora + " toma tus precauciones.")
                            }
                        } //hasta aqui
                    }


                    //comprobacion Estancamientos
                    DiferenciasParadaoFecha.clear()
                    ContadorDiferencias.clear()
                    //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                    if (arraylist_Estancamientos.size >= 3) {
                        for (contaorParadasNombre in arraylist_Estancamientos.indices) {
                            val paradaLugar: String? =
                                arraylist_Estancamientos[contaorParadasNombre].LugarDelReporte
                            var banderayaseagrego = 0
                            for (lugaresAgregar in DiferenciasParadaoFecha.indices) {
                                if (paradaLugar == DiferenciasParadaoFecha[lugaresAgregar].LugarDelReporte) {
                                    banderayaseagrego = 1
                                }
                            }
                            if (banderayaseagrego == 0) {
                                DiferenciasParadaoFecha.add(arraylist_Estancamientos[contaorParadasNombre])
                                ContadorDiferencias.add(0)
                            }
                        }

                        //for que busca cuantas paradas coinciden con cada diferencia
                        for (ParadasDiferencias in DiferenciasParadaoFecha.indices) {
                            val paradAcomparar: String? =
                                DiferenciasParadaoFecha[ParadasDiferencias].LugarDelReporte
                            for (buscarTodo in arraylist_Estancamientos.indices) {
                                if (paradAcomparar == arraylist_Estancamientos[buscarTodo].LugarDelReporte) {
                                    ContadorDiferencias[ParadasDiferencias] =
                                        ContadorDiferencias[ParadasDiferencias] + 1
                                }
                            }
                        }


                        //iterar entre los que tuvieron la misma parada 3 veces
                        for (contadorfech in DiferenciasParadaoFecha.indices) {
                            if (ContadorDiferencias[contadorfech] >= 3) {
                                //objeto que busca el formato de la fecha a obtener
                                val nuevoformato = SimpleDateFormat(
                                    "hh:mm:ss a"
                                )
                                val formateadorfecha = SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy"
                                )
                                var fechaMarcador: Date? = null
                                try {
                                    fechaMarcador =
                                        formateadorfecha.parse(DiferenciasParadaoFecha[contadorfech].Hora_DeReporteCanal)
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }
                                val hora = nuevoformato.format(fechaMarcador)
                                listviewarray.add("Se ha reportado un estancamiento cerca de la parada " + DiferenciasParadaoFecha[contadorfech].LugarDelReporte + " a las " + hora + " toma tus precauciones.")
                            }
                        }
                    } //hasta aqui


                    // comprobcion no estan dando paradas
                    DiferenciasParadaoFecha.clear()
                    ContadorDiferencias.clear()
                    //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                    if (arraylist_NoDaParadas.size >= 3) {
                        for (contaorParadasNombre in arraylist_NoDaParadas.indices) {
                            val paradaLugar: String? =
                                arraylist_NoDaParadas[contaorParadasNombre].LugarDelReporte
                            var banderayaseagrego = 0
                            for (lugaresAgregar in DiferenciasParadaoFecha.indices) {
                                if (paradaLugar == DiferenciasParadaoFecha[lugaresAgregar].LugarDelReporte) {
                                    banderayaseagrego = 1
                                }
                            }
                            if (banderayaseagrego == 0) {
                                DiferenciasParadaoFecha.add(arraylist_NoDaParadas[contaorParadasNombre])
                                ContadorDiferencias.add(0)
                            }
                        }

                        //for que busca cuantas paradas coinciden con cada diferencia
                        for (ParadasDiferencias in DiferenciasParadaoFecha.indices) {
                            val paradAcomparar: String? =
                                DiferenciasParadaoFecha[ParadasDiferencias].LugarDelReporte
                            for (buscarTodo in arraylist_NoDaParadas.indices) {
                                if (paradAcomparar == arraylist_NoDaParadas[buscarTodo].LugarDelReporte) {
                                    ContadorDiferencias[ParadasDiferencias] =
                                        ContadorDiferencias[ParadasDiferencias] + 1
                                }
                            }
                        }


                        //iterar entre los que tuvieron la misma parada 3 veces
                        for (contadorfech in DiferenciasParadaoFecha.indices) {
                            if (ContadorDiferencias[contadorfech] >= 3) {
                                //objeto que busca el formato de la fecha a obtener
                                val nuevoformato = SimpleDateFormat(
                                    "hh:mm:ss a"
                                )
                                val formateadorfecha = SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy"
                                )
                                var fechaMarcador: Date? = null
                                try {
                                    fechaMarcador =
                                        formateadorfecha.parse(DiferenciasParadaoFecha[contadorfech].Hora_DeReporteCanal)
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }
                                val hora = nuevoformato.format(fechaMarcador)
                                listviewarray.add("Se reporto a las " + hora + " que el camion no se esta parando en la parada  " + DiferenciasParadaoFecha[contadorfech].LugarDelReporte + " toma tus precauciones.")
                            }
                        }
                    } //hasta aqui


                    //comprobacion Mala condicion del vehiculo
                    DiferenciasParadaoFecha.clear()
                    ContadorDiferencias.clear()
                    //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                    if (arraylist_MalaCondicionVehiculo.size >= 3) {
                        for (contaorParadasNombre in arraylist_MalaCondicionVehiculo.indices) {
                            val paradaLugar: String? =
                                arraylist_MalaCondicionVehiculo[contaorParadasNombre].LugarDelReporte
                            var banderayaseagrego = 0
                            for (lugaresAgregar in DiferenciasParadaoFecha.indices) {
                                if (paradaLugar == DiferenciasParadaoFecha[lugaresAgregar].LugarDelReporte) {
                                    banderayaseagrego = 1
                                }
                            }
                            if (banderayaseagrego == 0) {
                                DiferenciasParadaoFecha.add(arraylist_MalaCondicionVehiculo[contaorParadasNombre])
                                ContadorDiferencias.add(0)
                            }
                        }

                        //for que busca cuantas paradas coinciden con cada diferencia
                        for (ParadasDiferencias in DiferenciasParadaoFecha.indices) {
                            val paradAcomparar: String? =
                                DiferenciasParadaoFecha[ParadasDiferencias].LugarDelReporte
                            for (buscarTodo in arraylist_MalaCondicionVehiculo.indices) {
                                if (paradAcomparar == arraylist_MalaCondicionVehiculo[buscarTodo].LugarDelReporte) {
                                    ContadorDiferencias[ParadasDiferencias] =
                                        ContadorDiferencias[ParadasDiferencias] + 1
                                }
                            }
                        }


                        //iterar entre los que tuvieron la misma parada 3 veces
                        for (contadorfech in DiferenciasParadaoFecha.indices) {
                            if (ContadorDiferencias[contadorfech] >= 3) {
                                //objeto que busca el formato de la fecha a obtener
                                val nuevoformato = SimpleDateFormat(
                                    "hh:mm:ss a"
                                )
                                val formateadorfecha = SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy"
                                )
                                var fechaMarcador: Date? = null
                                try {
                                    fechaMarcador =
                                        formateadorfecha.parse(DiferenciasParadaoFecha[contadorfech].Hora_DeReporteCanal)
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }
                                val hora = nuevoformato.format(fechaMarcador)
                                listviewarray.add("Se ha reportado una mala condicion de la unidad " + DiferenciasParadaoFecha[contadorfech].numeroDeUnidad + "cerca de la parada " + DiferenciasParadaoFecha[contadorfech].LugarDelReporte + " a las " + hora + " toma tus precauciones.")
                            }
                        }
                    } //hasta aqui


                    //comprobacion Vehiculo mal aseado
                    DiferenciasParadaoFecha.clear()
                    ContadorDiferencias.clear()
                    //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                    if (arraylist_MalAseo.size >= 3) {
                        for (contaorParadasNombre in arraylist_MalAseo.indices) {
                            val paradaLugar: String? =
                                arraylist_MalAseo[contaorParadasNombre].LugarDelReporte
                            var banderayaseagrego = 0
                            for (lugaresAgregar in DiferenciasParadaoFecha.indices) {
                                if (paradaLugar == DiferenciasParadaoFecha[lugaresAgregar].LugarDelReporte) {
                                    banderayaseagrego = 1
                                }
                            }
                            if (banderayaseagrego == 0) {
                                DiferenciasParadaoFecha.add(arraylist_MalAseo[contaorParadasNombre])
                                ContadorDiferencias.add(0)
                            }
                        }

                        //for que busca cuantas paradas coinciden con cada diferencia
                        for (ParadasDiferencias in DiferenciasParadaoFecha.indices) {
                            val paradAcomparar: String? =
                                DiferenciasParadaoFecha[ParadasDiferencias].LugarDelReporte
                            for (buscarTodo in arraylist_MalAseo.indices) {
                                if (paradAcomparar == arraylist_MalAseo[buscarTodo].LugarDelReporte) {
                                    ContadorDiferencias[ParadasDiferencias] =
                                        ContadorDiferencias[ParadasDiferencias] + 1
                                }
                            }
                        }


                        //iterar entre los que tuvieron la misma parada 3 veces
                        for (contadorfech in DiferenciasParadaoFecha.indices) {
                            if (ContadorDiferencias[contadorfech] >= 3) {
                                //objeto que busca el formato de la fecha a obtener
                                val nuevoformato = SimpleDateFormat(
                                    "hh:mm:ss a"
                                )
                                val formateadorfecha = SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy"
                                )
                                var fechaMarcador: Date? = null
                                try {
                                    fechaMarcador =
                                        formateadorfecha.parse(DiferenciasParadaoFecha[contadorfech].Hora_DeReporteCanal)
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }
                                val hora = nuevoformato.format(fechaMarcador)
                                listviewarray.add("Se ha reportado la unidad " + DiferenciasParadaoFecha[contadorfech].numeroDeUnidad + " con mal Aseo a las " + hora + " cerca de la parada " + DiferenciasParadaoFecha[contadorfech].LugarDelReporte + " Recuerda respetar y no tirar Basura en el Camion, ya que es de Todos.")
                            }
                        }
                    } //hasta aqui

                    //comprobacion mala actitud del chofer
                    DiferenciasParadaoFecha.clear()
                    ContadorDiferencias.clear()
                    //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                    if (arraylist_MalaActitudDelChofer.size >= 3) {
                        for (contaorParadasNombre in arraylist_MalaActitudDelChofer.indices) {
                            val paradaLugar: String? =
                                arraylist_MalaActitudDelChofer[contaorParadasNombre].LugarDelReporte
                            var banderayaseagrego = 0
                            for (lugaresAgregar in DiferenciasParadaoFecha.indices) {
                                if (paradaLugar == DiferenciasParadaoFecha[lugaresAgregar].LugarDelReporte) {
                                    banderayaseagrego = 1
                                }
                            }
                            if (banderayaseagrego == 0) {
                                DiferenciasParadaoFecha.add(arraylist_MalaActitudDelChofer[contaorParadasNombre])
                                ContadorDiferencias.add(0)
                            }
                        }

                        //for que busca cuantas paradas coinciden con cada diferencia
                        for (ParadasDiferencias in DiferenciasParadaoFecha.indices) {
                            val paradAcomparar: String =
                                DiferenciasParadaoFecha[ParadasDiferencias].LugarDelReporte.toString()
                            for (buscarTodo in arraylist_MalaActitudDelChofer.indices) {
                                if (paradAcomparar == arraylist_MalaActitudDelChofer[buscarTodo].LugarDelReporte) {
                                    ContadorDiferencias[ParadasDiferencias] =
                                        ContadorDiferencias[ParadasDiferencias] + 1
                                }
                            }
                        }


                        //iterar entre los que tuvieron la misma parada 3 veces
                        for (contadorfech in DiferenciasParadaoFecha.indices) {
                            if (ContadorDiferencias[contadorfech] >= 3) {
                                //objeto que busca el formato de la fecha a obtener
                                val nuevoformato = SimpleDateFormat(
                                    "hh:mm:ss a"
                                )
                                val formateadorfecha = SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy"
                                )
                                var fechaMarcador: Date? = null
                                try {
                                    fechaMarcador =
                                        formateadorfecha.parse(DiferenciasParadaoFecha[contadorfech].Hora_DeReporteCanal)
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }
                                val hora = nuevoformato.format(fechaMarcador)
                                listviewarray.add("Se ha reportado una mala actitud del chofer en la unidad " + DiferenciasParadaoFecha[contadorfech].numeroDeUnidad + " a las " + hora + " cerca de la parada " + DiferenciasParadaoFecha[contadorfech].LugarDelReporte + " toma tus precauciones y de ser necesario reporta al chofer en la linea 01 800 5238 699 de SEMOV, nos merecemos un buen servicio y trato digno. Mejoremos la Movilidad.")
                            }
                        }
                    } //hasta aqui


                    //comprobacion Asalto
                    DiferenciasParadaoFecha.clear()
                    ContadorDiferencias.clear()
                    //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                    if (arraylist_Asaltos.size >= 3) {
                        for (contaorParadasNombre in arraylist_Asaltos.indices) {
                            val paradaLugar: String? =
                                arraylist_Asaltos[contaorParadasNombre].LugarDelReporte
                            var banderayaseagrego = 0
                            for (lugaresAgregar in DiferenciasParadaoFecha.indices) {
                                if (paradaLugar == DiferenciasParadaoFecha[lugaresAgregar].LugarDelReporte) {
                                    banderayaseagrego = 1
                                }
                            }
                            if (banderayaseagrego == 0) {
                                DiferenciasParadaoFecha.add(arraylist_Asaltos[contaorParadasNombre])
                                ContadorDiferencias.add(0)
                            }
                        }

                        //for que busca cuantas paradas coinciden con cada diferencia
                        for (ParadasDiferencias in DiferenciasParadaoFecha.indices) {
                            val paradAcomparar: String? =
                                DiferenciasParadaoFecha[ParadasDiferencias].LugarDelReporte
                            for (buscarTodo in arraylist_Asaltos.indices) {
                                if (paradAcomparar == arraylist_Asaltos[buscarTodo].LugarDelReporte) {
                                    ContadorDiferencias[ParadasDiferencias] =
                                        ContadorDiferencias[ParadasDiferencias] + 1
                                }
                            }
                        }


                        //iterar entre los que tuvieron la misma parada 3 veces
                        for (contadorfech in DiferenciasParadaoFecha.indices) {
                            if (ContadorDiferencias[contadorfech] >= 3) {
                                //objeto que busca el formato de la fecha a obtener
                                val nuevoformato = SimpleDateFormat(
                                    "hh:mm:ss a"
                                )
                                val formateadorfecha = SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy"
                                )
                                var fechaMarcador: Date? = null
                                try {
                                    fechaMarcador =
                                        formateadorfecha.parse(DiferenciasParadaoFecha[contadorfech].Hora_DeReporteCanal)
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }
                                val hora = nuevoformato.format(fechaMarcador)
                                listviewarray.add("Se ha reportado un Asalto cerca de la parada " + DiferenciasParadaoFecha[contadorfech].LugarDelReporte + " a las " + hora + " Ten cuidado y toma tus precauciones. De ser necesario llama al 911 Recuerda que es gratuito. Mejoremos la Movilidad")
                            }
                        }
                    } //hasta aqui


                    //comprobacion peleas
                    DiferenciasParadaoFecha.clear()
                    ContadorDiferencias.clear()
                    //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                    if (arraylist_Peleas.size >= 3) {
                        for (contaorParadasNombre in arraylist_Peleas.indices) {
                            val paradaLugar: String? =
                                arraylist_Peleas[contaorParadasNombre].LugarDelReporte
                            var banderayaseagrego = 0
                            for (lugaresAgregar in DiferenciasParadaoFecha.indices) {
                                if (paradaLugar == DiferenciasParadaoFecha[lugaresAgregar].LugarDelReporte) {
                                    banderayaseagrego = 1
                                }
                            }
                            if (banderayaseagrego == 0) {
                                DiferenciasParadaoFecha.add(arraylist_Peleas[contaorParadasNombre])
                                ContadorDiferencias.add(0)
                            }
                        }

                        //for que busca cuantas paradas coinciden con cada diferencia
                        for (ParadasDiferencias in DiferenciasParadaoFecha.indices) {
                            val paradAcomparar: String? =
                                DiferenciasParadaoFecha[ParadasDiferencias].LugarDelReporte
                            for (buscarTodo in arraylist_Peleas.indices) {
                                if (paradAcomparar == arraylist_Peleas[buscarTodo].LugarDelReporte) {
                                    ContadorDiferencias[ParadasDiferencias] =
                                        ContadorDiferencias[ParadasDiferencias] + 1
                                }
                            }
                        }


                        //iterar entre los que tuvieron la misma parada 3 veces
                        for (contadorfech in DiferenciasParadaoFecha.indices) {
                            if (ContadorDiferencias[contadorfech] >= 3) {
                                //objeto que busca el formato de la fecha a obtener
                                val nuevoformato = SimpleDateFormat(
                                    "hh:mm:ss a"
                                )
                                val formateadorfecha = SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy"
                                )
                                var fechaMarcador: Date? = null
                                try {
                                    fechaMarcador =
                                        formateadorfecha.parse(DiferenciasParadaoFecha[contadorfech].Hora_DeReporteCanal)
                                } catch (e: ParseException) {
                                    e.printStackTrace()

                                }
                                val hora = nuevoformato.format(fechaMarcador)
                                listviewarray.add("Se ha reportado una pelea cerca de la parada " + DiferenciasParadaoFecha[contadorfech].LugarDelReporte + " a las " + hora + " toma tus precauciones y de ser necesario llama al 911. Mejoremos la Movilidad")
                            }
                        }
                    } //hasta aqui
                    val adaptadorCanalAbierto = ArrayAdapter<String>(
                        applicationContext, R.layout.list_view_azulcanales, listviewarray
                    )
                    lv_CanalAbierto?.setAdapter(adaptadorCanalAbierto)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })


    }


    private fun funcionalidad_btn_aceptar() {
        btn_Aceptar!!.setOnClickListener { finish() }
    }

    private fun funcionalidadbtn_Hacer_Reporte() {
        OTBReference!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("MissingPermission")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tv_Reportes!!.setOnClickListener {
                    userFLPC =
                        LocationServices.getFusedLocationProviderClient(this@seeChannelInfo)
                    userFLPC!!.lastLocation.addOnSuccessListener(this@seeChannelInfo,
                        OnSuccessListener<Location?> { location ->
                            var latuser = 0.0
                            var lnguser = 0.0
                            if (location != null) {
                                latuser = location.latitude
                                lnguser = location.longitude
                            }
                            var latuserresult = 0.0
                            var lnguserresult = 0.0
                            var latuserresult2: Double? = 0.0
                            var lnguserresult2: Double? = 0.0
                            var suma1 = 0.0
                            var suma2 = 0.0
                            oRutasAbordado.setLatuser(0.0)
                            oRutasAbordado.setLnguser(0.0)
                            for (contadoridaovuelta in 0..1) {
                                var orientcion: String? = null
                                //contador para paradas
                                var contadorParadas = 1
                                if (contadoridaovuelta == 0) {
                                    orientcion = "ida"
                                }
                                if (contadoridaovuelta == 1) {
                                    orientcion = "vuelta"
                                }
                                for (snapshot2 in dataSnapshot.child("Rutas").child(
                                    "Ruta$numeroCanal"
                                ).child(
                                    orientcion!!
                                ).children) {
                                    // se cargan marcadores nuevos
                                    val latitud1 =
                                        dataSnapshot.child("Rutas").child("Ruta$numeroCanal").child(
                                            orientcion
                                        ).child("Parada$contadorParadas")
                                            .child("latitud").value.toString()
                                    val longitud1 = dataSnapshot.child("Rutas").child(
                                        "Ruta$numeroCanal"
                                    ).child(
                                        orientcion
                                    ).child("Parada$contadorParadas")
                                        .child("longitud").value.toString()
                                    val latitud = latitud1.toDouble()
                                    val longitud = longitud1.toDouble()
                                    latuserresult = latuser - oRutasAbordado.getLatuser()!!
                                    lnguserresult = lnguser - oRutasAbordado.getLnguser()!!
                                    suma1 = Math.abs(latuserresult) + Math.abs(
                                        lnguserresult
                                    )
                                    latuserresult2 = latuser - latitud
                                    lnguserresult2 = lnguser - longitud
                                    suma2 = Math.abs(latuserresult2) + Math.abs(
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
                                        nombreParada =
                                            dataSnapshot.child("Rutas").child("Ruta$numeroCanal")
                                                .child(
                                                    orientcion
                                                ).child("Parada$contadorParadas")
                                                .child("tittle").value.toString()
                                    }
                                    contadorParadas++
                                }
                            } //contador para comparar tambein con vuelta
                            val intentHacerReporte = Intent(
                                this@seeChannelInfo,
                                reportRoute::class.java
                            )
                            val pasadorVentanaEmergenteReporte = Bundle()
                            val idusuario = mAuth!!.currentUser!!.uid
                            val correoDelUsuario = dataSnapshot.child("Users").child(idusuario)
                                .child("correo").value.toString()
                            pasadorVentanaEmergenteReporte.putString(
                                "correoDelUsuario",
                                correoDelUsuario
                            )
                            pasadorVentanaEmergenteReporte.putString(
                                "CanalNumero",
                                "Canal$numeroCanal"
                            )
                            pasadorVentanaEmergenteReporte.putString("NombreParada", nombreParada)
                            intentHacerReporte.putExtras(pasadorVentanaEmergenteReporte)
                            startActivity(intentHacerReporte)
                        })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}