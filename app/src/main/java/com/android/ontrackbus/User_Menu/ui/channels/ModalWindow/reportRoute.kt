package com.android.ontrackbus.User_Menu.ui.channels.ModalWindow

import android.os.Bundle
import android.text.InputType
import android.util.DisplayMetrics
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.ontrackbus.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date

class reportRoute : AppCompatActivity() {

    private var OTBReference: DatabaseReference? = null

    //variables de xml
    private var spinner_tipos_reporte: Spinner? = null
    private var lista_subtipos: ListView? = null
    private var btn_enviarReporte: Button? = null
    private var btn_Cancelar: Button? = null

    //medidas de ventana emergente
    var ancho = 0
    var alto = 0

    //que item del spinner se selecciono
    private var reporte_seleccionado: String? = null
    private var NombreParada: String? = null
    private var CanalNumero: String? = null

    private val tipos_reporte =
        arrayOf("Desvio", "Estancanmiento", "No dan paradas", "Mal servicio", "Violencia")
    private val subtipos_reporte = ArrayList<String>()
    private val id_Reportes = ArrayList<String>()

    private var correoDelUsuario: String? = null

    private var bundleObtenerDatosReportar: Bundle? = null


    private var idReporteSeleccioanadoListView: String? = null
    private var fecha: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_route)


        OTBReference = FirebaseDatabase.getInstance().reference

        bundleObtenerDatosReportar = intent.extras
        NombreParada = bundleObtenerDatosReportar!!.getString("NombreParada")
        CanalNumero = bundleObtenerDatosReportar!!.getString("CanalNumero")
        correoDelUsuario = bundleObtenerDatosReportar!!.getString("correoDelUsuario")
        val medidasVentana = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(medidasVentana)
        ancho = medidasVentana.widthPixels
        alto = medidasVentana.heightPixels
        window.setLayout((ancho + 0.85).toInt(), (alto * 0.65).toInt())

        btn_Cancelar = findViewById<Button>(R.id.btn_CancelarEnviarReporte)
        btn_Cancelar!!.setOnClickListener(View.OnClickListener { finish() })

        spinner_tipos_reporte = findViewById<Spinner>(R.id.spinner_reportes)
        lista_subtipos = findViewById<ListView>(R.id.lv_de_tipos)
        btn_enviarReporte = findViewById<Button>(R.id.boton_envio_reporte)

        val adaptador_spinner = ArrayAdapter(this, R.layout.spinner_letras_style, tipos_reporte)
        spinner_tipos_reporte!!.setAdapter(adaptador_spinner)

        lista_subtipos!!.setVisibility(View.GONE)

        spinner_tipos_reporte!!.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                id_Reportes.clear()
                subtipos_reporte.clear()
                reporte_seleccionado = spinner_tipos_reporte!!.getSelectedItem().toString()
                if (reporte_seleccionado != tipos_reporte[1] && reporte_seleccionado != tipos_reporte[2]) {
                    window.setLayout((ancho + 0.85).toInt(), (alto * 0.65).toInt())
                    lista_subtipos!!.setVisibility(View.VISIBLE)
                    when (reporte_seleccionado) {
                        "Desvio" -> {
                            subtipos_reporte.add("Choque")
                            subtipos_reporte.add("Calle cerrada")
                            id_Reportes.add("RP-00-00")
                            id_Reportes.add("RP-00-01")
                        }

                        "Mal servicio" -> {
                            subtipos_reporte.add("Condición del vehículo")
                            subtipos_reporte.add("Aseo")
                            subtipos_reporte.add("Actitud del chofer")
                            id_Reportes.add("RP-03-00")
                            id_Reportes.add("RP-03-01")
                            id_Reportes.add("RP-03-02")
                        }

                        "Violencia" -> {
                            subtipos_reporte.add("Asalto")
                            subtipos_reporte.add("Pelea")
                            id_Reportes.add("RP-04-00")
                            id_Reportes.add("RP-04-01")
                        }
                    }
                    val adaptador_lista = ArrayAdapter<String>(
                        applicationContext,
                        R.layout.list_view_tv_letras_style_negro,
                        subtipos_reporte
                    )
                    lista_subtipos!!.setAdapter(adaptador_lista)
                    lista_subtipos!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
                        view.isSelected = true
                        idReporteSeleccioanadoListView = id_Reportes[position]
                        btn_enviarReporte!!.setOnClickListener(View.OnClickListener {
                            //objeto que busca el formato de la fecha a obtener
                            val formateadorfecha = SimpleDateFormat(
                                "HH:mm:ss d-MM-yyyy"
                            )

                            //se obtiene la fecha y hora actual
                            val fechaDate = Date()
                            fecha = formateadorfecha.format(fechaDate)
                            if (idReporteSeleccioanadoListView == "RP-03-00" || idReporteSeleccioanadoListView == "RP-03-01" || idReporteSeleccioanadoListView == "RP-03-02") {
                                val alertNumeroDeUnidad = AlertDialog.Builder(this@reportRoute)
                                alertNumeroDeUnidad.setTitle("Escriba el numero de la unidad")
                                val edt_numeroDeUnidad = EditText(this@reportRoute)
                                edt_numeroDeUnidad.inputType =
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD
                                alertNumeroDeUnidad.setView(edt_numeroDeUnidad)
                                alertNumeroDeUnidad.setPositiveButton(
                                    "Enviar"
                                ) { dialog, which ->
                                    val numeroDeUnidadCamion =
                                        edt_numeroDeUnidad.text.toString().trim { it <= ' ' }
                                    val ReporteCanal: MutableMap<String, Any?> =
                                        HashMap()
                                    ReporteCanal["Id_ReporteCanal"] =
                                        idReporteSeleccioanadoListView
                                    ReporteCanal["LugarDelReporte"] = NombreParada
                                    ReporteCanal["Hora_DeReporteCanal"] = fecha
                                    ReporteCanal["numeroDeUnidad"] = numeroDeUnidadCamion
                                    ReporteCanal["correoDelUsuario"] = correoDelUsuario
                                    // Generate a reference to a new location and add some data using push()
                                    val pushedPostRef = OTBReference!!.child("Canales").child(
                                        CanalNumero!!
                                    ).child("Reportes").push()
                                    val Id_Clave = pushedPostRef.key
                                    ReporteCanal["Id_Clave"] = Id_Clave
                                    OTBReference!!.child("Canales").child(CanalNumero!!)
                                        .child("Reportes").child(
                                            Id_Clave!!
                                        ).setValue(ReporteCanal)
                                    finish()
                                }
                                alertNumeroDeUnidad.setNegativeButton(
                                    "Cancelar"
                                ) { dialog, which -> dialog.cancel() }
                                alertNumeroDeUnidad.show()
                            } else {
                                val ReporteCanal: MutableMap<String, Any?> = HashMap()
                                ReporteCanal["Id_ReporteCanal"] = idReporteSeleccioanadoListView
                                ReporteCanal["LugarDelReporte"] = NombreParada
                                ReporteCanal["Hora_DeReporteCanal"] = fecha
                                // Generate a reference to a new location and add some data using push()
                                val pushedPostRef = OTBReference!!.child("Canales").child(
                                    CanalNumero!!
                                ).child("Reportes").push()
                                val Id_Clave = pushedPostRef.key
                                ReporteCanal["Id_Clave"] = Id_Clave
                                OTBReference!!.child("Canales").child(CanalNumero!!).child("Reportes")
                                    .child(
                                        Id_Clave!!
                                    ).setValue(ReporteCanal)
                                finish()
                            }
                        })
                    })
                } else {
                    window.setLayout((ancho + 0.85).toInt(), (alto * 0.35).toInt())
                    lista_subtipos!!.setVisibility(View.GONE)
                    btn_enviarReporte!!.setOnClickListener(View.OnClickListener {
                        var idReporteSinListView: String? = null
                        if (reporte_seleccionado == tipos_reporte[1]) {
                            idReporteSinListView = "RP-01"
                        }
                        if (reporte_seleccionado == tipos_reporte[2]) {
                            idReporteSinListView = "RP-02"
                        }

                        //objeto que busca el formato de la fecha a obtener
                        val formateadorfecha = SimpleDateFormat(
                            "HH:mm:ss d-MM-yyyy"
                        )

                        //se obtiene la fecha y hora actual
                        val fechaDate = Date()
                        val fecha = formateadorfecha.format(fechaDate)
                        val ReporteCanal: MutableMap<String, Any?> = HashMap()
                        ReporteCanal["Id_ReporteCanal"] = idReporteSinListView
                        ReporteCanal["LugarDelReporte"] = NombreParada
                        ReporteCanal["Hora_DeReporteCanal"] = fecha
                        // Generate a reference to a new location and add some data using push()
                        val pushedPostRef =
                            OTBReference!!.child("Canales").child(CanalNumero!!).child("Reportes")
                                .push()
                        val Id_Clave = pushedPostRef.key
                        ReporteCanal["Id_Clave"] = Id_Clave
                        OTBReference!!.child("Canales").child(CanalNumero!!).child("Reportes").push()
                            .setValue(ReporteCanal)
                        finish()
                    })
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })


    }
}