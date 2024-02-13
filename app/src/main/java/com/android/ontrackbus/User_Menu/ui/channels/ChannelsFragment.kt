package com.android.ontrackbus.User_Menu.ui.channels

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.ontrackbus.Models.Canales
import com.android.ontrackbus.R
import com.android.ontrackbus.User_Menu.MenuViewModel
import com.android.ontrackbus.User_Menu.ui.channels.ModalWindow.enterChannel
import com.android.ontrackbus.User_Menu.ui.channels.ModalWindow.seeChannelInfo
import com.android.ontrackbus.databinding.FragmentChannelsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.DecimalFormat


class ChannelsFragment : Fragment() {

    private var OTbReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null

    private var bundleRecuperarCanalesDeRutas: Bundle? = null

    //arraylist para autocomplete text view
    private var actv_Buscar_Canales_De_Ruta: AutoCompleteTextView? = null
    private var btn_BuscarCanal: Button? = null
    private val rutasNombresCanales = ArrayList<String>()

    //variables de botones
    private var btn_CanalesEntrar: Button? = null
    private var btn_CanalesBorrar: Button? = null

    //list view variables
    private var lv_CanalesDeRutas: ListView? = null
    private val CanalesSeleccionados = ArrayList<String>()
    private val ContadorCanalesSeleccionados = ArrayList<String>()

    private var nombresCanales = 0

    //objeto para obtener hijos de canales de rutas
    private var oCanales: Canales? = null
    private val array_reportesCanales: ArrayList<Canales> = ArrayList<Canales>()

    private var loginBundleSaved: MenuViewModel? = null

    private var _binding: FragmentChannelsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBundleSaved = ViewModelProvider(requireActivity())[MenuViewModel::class.java]

        bundleRecuperarCanalesDeRutas = loginBundleSaved!!.loginBundle
        nombresCanales = bundleRecuperarCanalesDeRutas!!.getInt("CantidadDeRutas")
        for (contadorCanales in 1 until nombresCanales) {
            rutasNombresCanales.add(bundleRecuperarCanalesDeRutas!!.getString("Ruta$contadorCanales")!!)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(ChannelsViewModel::class.java)

        _binding = FragmentChannelsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        OTbReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        //instancear variables

        //instancear variables
        actv_Buscar_Canales_De_Ruta =
            root.findViewById<AutoCompleteTextView>(R.id.actv_BuscarCanalDeRuta)
        btn_BuscarCanal = root.findViewById<Button>(R.id.btn_BuscarCanalDeRuta)
        btn_CanalesEntrar = root.findViewById<Button>(R.id.btn_AbrirCanal)
        btn_CanalesBorrar = root.findViewById<Button>(R.id.btn_BorrarCanal)
        lv_CanalesDeRutas = root.findViewById<ListView>(R.id.lv_CanalesDeRutas)

        //array adapter para autocompletetextview

        //array adapter para autocompletetextview
        val adapterrutasNombres = ArrayAdapter<String>(
            requireActivity(),
            R.layout.support_simple_spinner_dropdown_item,
            rutasNombresCanales
        )
        actv_Buscar_Canales_De_Ruta?.setAdapter(adapterrutasNombres)



        funcionBuscarCanal()




        OTbReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                CanalesSeleccionados.clear()
                array_reportesCanales.clear()
                ContadorCanalesSeleccionados.clear()
                val usuario = mAuth!!.getCurrentUser()!!.uid
                var contadorArray = 0
                for (snapshot in dataSnapshot.child("Users").child(usuario)
                    .child("CanalesSeleccionados").children) {
                    oCanales = snapshot.getValue(Canales::class.java)
                    array_reportesCanales.add(oCanales!!)
                    val nombreCanalSeleccionado = array_reportesCanales[contadorArray].Nombre
                    ContadorCanalesSeleccionados.add("CanalSeleccionado" + array_reportesCanales[contadorArray].Id_CanalSeleccionado)
                    var contadorCanales = 1
                    var TarifaDescarga: String? = null
                    for (snapshotTarifas in dataSnapshot.child("Canales").children) {
                        val NombreTarifaCanal =
                            dataSnapshot.child("Canales").child("Canal$contadorCanales")
                                .child("NombreCanal").value.toString()
                        if (NombreTarifaCanal == nombreCanalSeleccionado) {
                            TarifaDescarga =
                                dataSnapshot.child("Canales").child("Canal$contadorCanales")
                                    .child("Tarifa").value.toString()
                        }
                        contadorCanales++
                    }
                    val dardecimales = DecimalFormat("#.00")
                    val tarifaFloat = TarifaDescarga!!.toFloat()
                    CanalesSeleccionados.add(
                        array_reportesCanales[contadorArray].Nombre + "                      Tarifa: $" + dardecimales.format(
                            tarifaFloat.toDouble()
                        )
                    )
                    contadorArray++
                }
                val adaptadorCanalesSeleccionados = ArrayAdapter<String>(
                    activity!!, R.layout.list_view_tv_letras_style, CanalesSeleccionados
                )
                lv_CanalesDeRutas?.setAdapter(adaptadorCanalesSeleccionados)
                lv_CanalesDeRutas?.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
                    view.isSelected = true
                    btn_CanalesBorrar!!.setOnClickListener(View.OnClickListener {
                        val iduser = mAuth!!.getCurrentUser()!!.uid
                        OTbReference!!.child("Users").child(iduser).child("CanalesSeleccionados")
                            .child("CanalSeleccionado" + array_reportesCanales[position].Id_CanalSeleccionado)
                            .removeValue()
                    })
                    btn_CanalesEntrar!!.setOnClickListener(View.OnClickListener {
                        val ver_Canal = Intent(activity, seeChannelInfo::class.java)
                        val pasadorAbrirCanal = Bundle()
                        pasadorAbrirCanal.putString(
                            "CanalNombre",
                            array_reportesCanales[position].Nombre
                        )
                        val menosuno = nombresCanales - 1
                        for (contadornumeroCanal in 0 until menosuno) {
                            if (rutasNombresCanales[contadornumeroCanal] == array_reportesCanales[position].Nombre) {
                                val numero = contadornumeroCanal + 1
                                pasadorAbrirCanal.putInt("CanalNumero", numero)
                            }
                        }
                        ver_Canal.putExtras(pasadorAbrirCanal)
                        actv_Buscar_Canales_De_Ruta!!.setText("")
                        startActivity(ver_Canal)
                    })
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        return root
    }


    private fun funcionBuscarCanal() {
        btn_BuscarCanal!!.setOnClickListener {
            val busquedaACTV = actv_Buscar_Canales_De_Ruta!!.text.toString().trim { it <= ' ' }
            var bandera = 0
            val menosuno = nombresCanales - 1
            var idcontador = 0
            for (contadorBusqueda in 0 until menosuno) {
                val nombreBusqueda = rutasNombresCanales[contadorBusqueda]
                if (nombreBusqueda == busquedaACTV) {
                    bandera = 1
                    idcontador = contadorBusqueda + 1
                }
            }
            if (bandera == 1) {
                val ver_Canal = Intent(activity, enterChannel::class.java)
                val pasadorVentanaEmergenteCanal = Bundle()
                pasadorVentanaEmergenteCanal.putString("CanalNombre", busquedaACTV)
                val menosuno2 = nombresCanales - 1
                for (contadornumeroCanal in 0 until menosuno2) {
                    if (rutasNombresCanales[contadornumeroCanal] == busquedaACTV) {
                        val numero2 = contadornumeroCanal + 1
                        pasadorVentanaEmergenteCanal.putInt("CanalNumero", numero2)
                    }
                }
                ver_Canal.putExtras(pasadorVentanaEmergenteCanal)
                actv_Buscar_Canales_De_Ruta!!.setText("")
                startActivity(ver_Canal)
            } else if (busquedaACTV == "") {
                actv_Buscar_Canales_De_Ruta!!.error = "Porfavor ingrese un Nombre de Ruta"
                Toast.makeText(activity, "Porfavor ingrese un Nombre de Ruta", Toast.LENGTH_SHORT)
                    .show()
            } else {
                actv_Buscar_Canales_De_Ruta!!.error = "Porfavor ingrese un Nombre de Ruta Valido"
                Toast.makeText(
                    activity,
                    "Porfavor ingrese un Nombre de Ruta Valido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}