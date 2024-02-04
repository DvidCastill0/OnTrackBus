package com.android.ontrackbus.User_Menu.ui.trusted_contact

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.ontrackbus.Models.TrustedContactReport
import com.android.ontrackbus.R
import com.android.ontrackbus.User_Menu.ui.trusted_contact.ModalWindow.detail_trusted_contact
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class TrustedContactFragment : Fragment() {


    //Instanciamiento de los componentes visuales
    private var lista_reportes: ListView? = null
    private var texto_superior: TextView? = null
    private var boton_ver: Button? = null
    private var boton_borrar: Button? = null
    private var boton_limpiar: Button? = null

    //Instanciamiento de un objeto de tipo reporte para mostrar datos detallados
    private var reporte: TrustedContactReport? = null

    //Referenciado al apartado de contacto de confianza de la BD
    private var OTBdatabase: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null

    //Creación de 2 ArrayList, uno sencillo para el apartado visual y otro para el lógico
    private val array_reportes: ArrayList<TrustedContactReport> =
        ArrayList<TrustedContactReport>()
    private val array_sencillo_reportes = ArrayList<String>()


    private var _binding: com.android.ontrackbus.databinding.FragmentTrustedContactBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val trustedContactViewModel =
            ViewModelProvider(this).get(TrustedContactViewModel::class.java)

        _binding = com.android.ontrackbus.databinding.FragmentTrustedContactBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.texttrustedContact
        trustedContactViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        mAuth = FirebaseAuth.getInstance()
        val idusuario = mAuth!!.getCurrentUser()!!.uid

        //Referenciado al apartado de contacto de confianza de la BD

        //Referenciado al apartado de contacto de confianza de la BD
        OTBdatabase = FirebaseDatabase.getInstance().reference.child("Users")
            .child(idusuario)
            .child("ContactoDeConfianza")


        //Inclusión de los componentes visuales


        //Inclusión de los componentes visuales
        texto_superior = root.findViewById<TextView>(R.id.tv_superior)
        lista_reportes =
            root.findViewById<ListView>(R.id.lv_reportesMiActividad)

        boton_ver = root.findViewById<Button>(R.id.btn_ver)
        boton_borrar = root.findViewById<Button>(R.id.btn_borrar)
        boton_limpiar = root.findViewById<Button>(R.id.btn_limpiar)


        /*A continuación se hace la descarga de los reportes de la base de datos
         una sola vez*/


        /*A continuación se hace la descarga de los reportes de la base de datos
         una sola vez*/
        OTBdatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                // 13 - 05 - 2020 // Limpiado de los ArrayList que almacenan los reportes lógica y visualmente
                array_reportes.clear()
                array_sencillo_reportes.clear()


                //Ciclo de descarga para los reportes de la BD
                var contadorreportes = 0
                for (snapshot in dataSnapshot.children) {
                    reporte = snapshot.getValue(TrustedContactReport::class.java)
                    array_reportes.add(reporte!!)
                    contadorreportes++
                }

                //Ciclo de asignación de la fecha y hora de los reportes
                for (numero_reporte in 0 until contadorreportes) {
                    val formateadorfecha = SimpleDateFormat(
                        "HH:mm:ss d-MM-yyyy"
                    )

                    // SimpleDateFormat nuevoFormato = new SimpleDateFormat("hh:mm:ssa EEEE d 'de' MMMM 'de' yyyy");
                    val nuevoFormato = SimpleDateFormat("hh:mm:ss a EEEE d-MMM-yyyy")
                    var fechadate: Date? = null
                    val fechaantigua: String =
                        array_reportes[numero_reporte].HoraAbordado + " " + array_reportes[numero_reporte].FechaAbordado
                    try {
                        fechadate = formateadorfecha.parse(fechaantigua)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    val fechaDia = nuevoFormato.format(fechadate)
                    array_sencillo_reportes.add(array_reportes[numero_reporte].NombreRemitente + "\n" + fechaDia)
                }

                //Ciclo de ordenamiento en base a las cadenas de caracteres
                var cambios = false
                var reporte_auxiliar = TrustedContactReport()
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
                            array_sencillo_reportes[i - 1] = string_auxiliar
                            cambios = true
                        }
                    }
                    if (cambios == false) {
                        break
                    }
                }
                val adaptador = ArrayAdapter<String>(
                    activity!!,
                    R.layout.list_view_tv_letras_style,
                    array_sencillo_reportes
                )
                lista_reportes!!.setAdapter(adaptador)
                lista_reportes!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
                    view.isSelected = true
                    boton_ver!!.setOnClickListener(View.OnClickListener {
                        val pasador = Bundle()
                        pasador.putString("Hora", array_reportes[position].HoraAbordado)
                        pasador.putString("Fecha", array_reportes[position].FechaAbordado)
                        pasador.putString("Correo", array_reportes[position].CorreoRemitente)
                        pasador.putString("Nombre", array_reportes[position].NombreRemitente)
                        pasador.putString("Parada", array_reportes[position].ParadaAbordada)
                        pasador.putString("Ruta", array_reportes[position].RutaAbordada)
                        val ver_reporte = Intent(
                            activity,
                            detail_trusted_contact::class.java
                        )
                        ver_reporte.putExtras(pasador)
                        startActivity(ver_reporte)
                    })


                    //Método onclick para eliminar un reporte de la lista en firebase
                    boton_borrar!!.setOnClickListener(View.OnClickListener {
                        val id_a_borrar: Int = array_reportes[position].Id_Reporte
                        OTBdatabase!!.child("Reporte$id_a_borrar").removeValue()
                    })
                })

                //Método onclick para limpiar completamente el nodo de reportes
                boton_limpiar!!.setOnClickListener(View.OnClickListener { OTBdatabase!!.removeValue() })

                //si no hay datos en la referencia no realizar nada para evitr crashear
                //Mientras no salgas de la siguiente llave ta bien
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}