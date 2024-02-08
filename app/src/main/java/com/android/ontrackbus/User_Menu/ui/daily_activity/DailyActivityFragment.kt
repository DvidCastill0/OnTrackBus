package com.android.ontrackbus.User_Menu.ui.daily_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.ontrackbus.Models.DailyActivityReport
import com.android.ontrackbus.R
import com.android.ontrackbus.User_Menu.MenuViewModel
import com.android.ontrackbus.User_Menu.ui.channels.ChannelsViewModel
import com.android.ontrackbus.databinding.FragmentChannelsBinding
import com.android.ontrackbus.databinding.FragmentDailyActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class DailyActivityFragment : Fragment() {

    private var _binding: FragmentDailyActivityBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private var spinner_dias: Spinner? = null
    private var lista_reportes: ListView? = null
    private var texto_pruebas: TextView? = null
    private val mAuth = FirebaseAuth.getInstance()


    private val OTBdatabase = FirebaseDatabase.getInstance().reference.child("Users")
        .child(mAuth.currentUser!!.uid)
        .child("MiActividad")


    var reporte: DailyActivityReport? = null

    var array_reportes: ArrayList<DailyActivityReport> =
        ArrayList<DailyActivityReport>()
    var array_sencillo_reportes = ArrayList<String>()
    var dias_semana =
        arrayOf("lunes", "martes", "miercoles", "jueves", "viernes", "sabado", "domingo")
    var array_Dias = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dailyViewModel =
            ViewModelProvider(this).get(DailyActivityViewModel::class.java)


        _binding = FragmentDailyActivityBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //objeto que busca el formato de la fecha a obtener

        //objeto que busca el formato de la fecha a obtener
        val formateadorfecha = SimpleDateFormat(
            "EEEE"
        )

        //se obtiene la fecha y hora actual

        //se obtiene la fecha y hora actual
        val fechaDate = Date()
        val ObtenerDia = formateadorfecha.format(fechaDate)

        array_Dias.add(ObtenerDia)
        for (contadorDias in 0..6) {
            if (dias_semana[contadorDias] == ObtenerDia != true) {
                array_Dias.add(dias_semana[contadorDias])
            }
        }

        var vistaMiActividad = inflater.inflate(R.layout.fragment_daily_activity, container, false)

        spinner_dias = vistaMiActividad.findViewById<Spinner>(R.id.spinner_seleccion)
        lista_reportes = vistaMiActividad.findViewById<ListView>(R.id.lv_reportesMiActividad)
        texto_pruebas = vistaMiActividad.findViewById<TextView>(R.id.tv_pruebas)

        val adaptador_dias = ArrayAdapter(requireActivity(), R.layout.spinner_letras_style, array_Dias)
        spinner_dias!!.setAdapter(adaptador_dias)

        //Método que se ejecuta dependiendo de la selección del spinner

        //Método que se ejecuta dependiendo de la selección del spinner
        spinner_dias!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (view == null)
                    return;
                val dia_seleccionado = spinner_dias!!.getSelectedItem().toString()

                //Método de firebase
                OTBdatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        array_reportes.clear()
                        array_sencillo_reportes.clear()
                        for (snapshot: DataSnapshot in dataSnapshot.child(dia_seleccionado).children) {
                            reporte = snapshot.getValue(DailyActivityReport::class.java)
                            array_reportes.add(reporte!!)
                        }

                        //Asignación de los reportes al array visual
                        for (numero_reporte in array_reportes.indices) {
                            val formateadorfecha = SimpleDateFormat(
                                "HH:mm:ss d-MM-yyyy"
                            )

                            // SimpleDateFormat nuevoFormato = new SimpleDateFormat("hh:mm:ssa EEEE d 'de' MMMM 'de' yyyy");
                            val nuevoFormatoHora = SimpleDateFormat("hh:mm:ss a")
                            val nuevoFormatoFecha = SimpleDateFormat("EEEE d-MMM-yyyy")
                            var fechadate: Date? = null
                            val fechaantigua: String =
                                array_reportes[numero_reporte].HoraAbordado + " " + array_reportes[numero_reporte].FechaAbordado
                            try {
                                fechadate = formateadorfecha.parse(fechaantigua)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            val fechadia = nuevoFormatoFecha.format(fechadate)
                            val fechahora = nuevoFormatoHora.format(fechadate)
                            array_sencillo_reportes.add(
                                (fechadia + "\n" +
                                        fechahora + "\n" + "Parada: " +
                                        array_reportes[numero_reporte].ParadaAbordada).toString() + "       " +
                                        array_reportes[numero_reporte].RutaAbordada
                            )
                        }


                        //Ordenamiento de los reportes para su muestra
                        var cambios = false
                        var reporte_auxiliar: DailyActivityReport =
                            DailyActivityReport()
                        var string_auxiliar: String? = null
                        while (true) {
                            cambios = false
                            for (i in 1 until array_sencillo_reportes.size) {
                                if (array_sencillo_reportes[i].compareTo(array_sencillo_reportes[i - 1]) > 0) {
                                    reporte_auxiliar = array_reportes[i]
                                    string_auxiliar = array_sencillo_reportes[i]
                                    array_reportes[i] = array_reportes[i - 1]
                                    array_sencillo_reportes[i] = array_sencillo_reportes[i - 1]
                                    array_reportes[i - 1] = reporte_auxiliar
                                    array_sencillo_reportes.set(i - 1, string_auxiliar)
                                    cambios = true
                                }
                            }
                            if (cambios == false) {
                                break
                            }
                        }
                        val adaptador_reportes = ArrayAdapter(
                            (activity)!!,
                            R.layout.list_view_azulcanales,
                            array_sencillo_reportes
                        )
                        lista_reportes!!.setAdapter(adaptador_reportes)


                        //Aquí termina onDataChange
                    }

                    override fun onCancelled(databaseError: DatabaseError) {} //Aquí termina el método de firebase
                })
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })


        return vistaMiActividad
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(DailyActivityViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}