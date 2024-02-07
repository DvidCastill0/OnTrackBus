package com.android.ontrackbus.User_Menu.ui.maps

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.ontrackbus.Models.Rutas
import com.android.ontrackbus.R
import com.android.ontrackbus.User_Menu.MenuViewModel
import com.android.ontrackbus.User_Menu.ui.maps.ModalWindow.notify_LockScreen
import com.android.ontrackbus.User_Menu.ui.maps.ModalWindow.pasadorSpinner
import com.android.ontrackbus.User_Menu.ui.maps.ModalWindow.search_route
import com.android.ontrackbus.databinding.FragmentMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    // Obtienes el Bundle del Intent pasado
    var nombreDeRutas: String? = null
    var CantidadDeRutas: Int = 0

    // Creamos un nuevo Bundle ahora para pasar los datos al fragmento de mapas
    private val args = Bundle()

    //region Variables MAps
    //google maps variables
    private var mMap: GoogleMap? = null


    //instanciamos una barra de progreso mientras se inicia sesion
    private var progressDialog: ProgressDialog? = null

    //instanciar la clase rutas para poder mandar elnumero de la ruta en base al nombre
    private var oRutas: Rutas = Rutas()

    //adaptadores para obtener todos los marcadores que haya por ruta
    private val tmpRealTimeMarker = ArrayList<Marker>()
    private val realTimeMarkers = ArrayList<Marker>()

    //Button cambiar orientacion
    private var btn_OrientacionRuta: Button? = null

    //boton miubicacion
    private var btn_mi_ubicacion: Button? = null

    //boton paradamascercana
    private var btn_ParadaCercana: Button? = null

    //boton ya voy en camino
    private var btn_voyEnCamino: Button? = null

    //variables de firebase:
    private var OTBReference: DatabaseReference? = null

    //vista del fragmento
    private var vistaMapasEnTiempoReal: View? = null

    //spinner para seleccionar ruta
    private lateinit var spnRuta: Spinner

    //array list para el adaptador del spinner.
    private val rutas = ArrayList<String>()
    private val rutasRecargar = ArrayList<String>()

    //arraylist rutas disponibles
    private val rutasdisponibles = ArrayList<String>()

    //variables para pasar la seleccion de ruta al mapa
    private var rutaSeleccionada: String? = null  //variables para pasar la seleccion de ruta al mapa
    private var rutaSeleccionadaNumero: String? = null  //variables para pasar la seleccion de ruta al mapa
    private var rutaSeleccionadaSpn: String? = null  //variables para pasar la seleccion de ruta al mapa
    private var orientacionRuta: String? = null  //variables para pasar la seleccion de ruta al mapa
    private var correoConfianza: String? = null

    //contador para checar las rutas que hay en la base de datos.
    private val contadorRMFNumero = 1  //contador para checar las rutas que hay en la base de datos.
    private var contadorRutaNumeroAbordado = 0

    //variable para obtener la ultima localizacion conocida
    private var userFLPC: FusedLocationProviderClient? = null

    //variable para obtener la ultima localizacion conocida
    private var userFLPC2: FusedLocationProviderClient? = null

    //variables de latitud y longitud de la ultima localizacion del usuario
    private var latuser = 0.0
    private var lnguser = 0.0

    //variable boton buscar parada y autocompletar busqueda de parada
    private var btn_buscar_parada: Button? = null
    private var actv_Buscar_Parada: AutoCompleteTextView? = null
    private val nombresDeParadasDisponibles = ArrayList<String>()
    private var CantidadDeParadas = 0

    private lateinit var loginBundle: Bundle
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            var loginBundleSaved = ViewModelProvider(requireActivity())[MenuViewModel::class.java]
            loginBundle = loginBundleSaved!!.loginBundle;
            //segundo parametro Cantidad de rutas este parametro si es obligatorio y siempre recibe osea que es obligatorio
            CantidadDeRutas = loginBundle.getInt("CantidadDeRutas")


            //obtener cantidad de Paradas                         CantidadDeParadas               obligatorio
            CantidadDeParadas = loginBundle.getInt("CantidadDeParadas")
            Toast.makeText(activity, "cantidad de paradas$CantidadDeParadas", Toast.LENGTH_SHORT)
                .show()

            //obtener los nombres de las rutas y meterlos en un arraylist            "Parada"+contadorParadas                   obligatorio

            //obtener los nombres de las rutas y meterlos en un arraylist            "Parada"+contadorParadas                   obligatorio
            for (contadorParadas in 1 until CantidadDeParadas) {
                val nombreDeParada: String? = loginBundle.getString("Parada$contadorParadas")
                if (nombreDeParada != null) {
                    nombresDeParadasDisponibles.add(nombreDeParada)
                }
            }

            //obtener ubicacion del usuario para iniciar


            //obtener ubicacion del usuario para iniciar
            ultimaUbicacionDelUsuario()
            //obtener string que nos diga si la orientacion es de ida o vuelta                OrientacionRuta no obligatorio
            //obtener string que nos diga si la orientacion es de ida o vuelta                OrientacionRuta no obligatorio
            orientacionRuta = if (loginBundle.getString("OrientacionRuta") != null) {
                loginBundle.getString("OrientacionRuta")
            } else {
                "ida"
            }

            //se descarga el numero de rutas disponibles                           CantidadDeRutas obligatorio

            //se descarga el numero de rutas disponibles                           CantidadDeRutas obligatorio
            contadorRutaNumeroAbordado = loginBundle.getInt("CantidadDeRutas")
            //se descargan las srutas disponible  Rutas disponibles Obligatorio     Ruta" + contadorRD

            //se descargan las srutas disponible  Rutas disponibles Obligatorio     Ruta" + contadorRD
            for (contadorNombres in 1 until contadorRutaNumeroAbordado) {
                val nombreRuta: String? = loginBundle.getString("Ruta$contadorNombres")
                if (nombreRuta != null) {
                    rutasdisponibles.add(nombreRuta)
                }
            }


            //meter correo de confiana                                                  Correo electronico confianza  obligatorio   contactoConfianza


            //meter correo de confiana                                                  Correo electronico confianza  obligatorio   contactoConfianza
            correoConfianza = loginBundle.getString("contactoConfianza")

            //meter datos en array de RMF en casod eqeu se recargue el fragmento.                       RMF array obligatorio

            //meter datos en array de RMF en casod eqeu se recargue el fragmento.                       RMF array obligatorio
            for (contadorarray in 0..2) {
                if (loginBundle.getString("RMF$contadorarray") != null) {
                    //si el bundle no eta vacio, se asigna el string al array adapter
                    rutasRecargar.add(loginBundle.getString("RMF$contadorarray")!!)
                }
            }

            //meter datos en array a mostrar en este fragmento

            //meter datos en array a mostrar en este fragmento
            if (loginBundle != null) {
                //if para saber si se selecciono otra ruta
                if (loginBundle.getString("RutaSeleccionada") != null) {                     //Ruta seleccionada no obligatorio    RutaSeleccionada
                    //poner datos en caso de que se seleccionara una ruta
                    rutas.add(loginBundle.getString("RutaSeleccionada")!!)
                    // Obtienes el texto del bundle
                    for (contadorarray in 0..2) {
                        if (loginBundle.getString("RMF$contadorarray") != null) {
                            if (loginBundle.getString("RMF$contadorarray") == loginBundle.getString("RutaSeleccionada")) {
                            } else {
                                //si el bundle no eta vacio, se asigna el string al array adapter
                                rutas.add(loginBundle.getString("RMF$contadorarray")!!)
                            }
                        }
                    }
                    rutas.add("Otra Ruta")
                    rutaSeleccionada = loginBundle.getString("RutaSeleccionada")
                    rutaSeleccionadaNumero =
                        loginBundle.getString("RutaSeleccionadaNumero") //numero de ruta seleccionada obligatorio  RutaSeleccionadaNumero
                } else {
                    // Poner datos en cacso de que no haya ninguna ruta seleccionada.
                    for (contadorarray in 0..2) {
                        if (loginBundle.getString("RMF$contadorarray") != null) {
                            //si el bundle no eta vacio, se asigna el string al array adapter
                            rutas.add(loginBundle.getString("RMF$contadorarray")!!)
                        }
                    }
                    rutaSeleccionada = loginBundle.getString("RMF0")
                    rutaSeleccionadaNumero = loginBundle.getString("RutaSeleccionadaNumero")
                    rutas.add("Otra Ruta")
                }
            } else {
                Toast.makeText(activity, "no hay datos", Toast.LENGTH_SHORT).show()
            }

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mapsViewModel =
            ViewModelProvider(this).get(MapsViewModel::class.java)

        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        vistaMapasEnTiempoReal = root;
        //val textView: TextView = binding.textGallery
        /*mapsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/


        if (activity == null) {
            Toast.makeText(activity, "no hay actividad", Toast.LENGTH_LONG).show()
        } else {

            //se instancia la barra de progreso
            progressDialog = ProgressDialog(activity)

            //firebase instancias
            OTBReference = FirebaseDatabase.getInstance().reference


            //meter datos a spinner
            spnRuta = vistaMapasEnTiempoReal!!.findViewById<Spinner>(R.id.spn_Ruta_Visualizada)
            val adp: ArrayAdapter<*> =
                ArrayAdapter<Any?>(requireActivity(), R.layout.spinner_letras_style,
                    rutas as List<Any?>
                )
            spnRuta.setAdapter(adp)

            //funcionalidad en caso de que cambie el numero de la ruta
            spnRuta.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    rutaSeleccionadaSpn = spnRuta.getAdapter().getItem(position) as String
                    if (rutaSeleccionadaSpn === rutaSeleccionada) {
                    } else if (rutaSeleccionadaSpn == "Otra Ruta") {


                        // Creamos un nuevo Bundle ahora para pasar los datos ala ventana emergente
                        val args3 = Bundle()
                        val contadorRD2 = contadorRutaNumeroAbordado - 1
                        var contadorRD0 = 1
                        //se obtiene el numero de la primera ruta seleccionada
                        for (contadorRD in 0 until contadorRD2) {
                            //se obtiene losnombres de las rutas disponibles.zza
                            args3.putString("Ruta$contadorRD0", rutasdisponibles[contadorRD])
                            contadorRD0++
                        }
                        for (contadorArray2 in 0..2) {
                            if (rutasRecargar[contadorArray2] != null) {
                                val contadorrmf = contadorArray2 + 1
                                args3.putString("RMF$contadorrmf", rutasRecargar[contadorArray2])
                            }
                        }


                        //se pasa el correo de confianza
                        args3.putString("contactoConfianza", correoConfianza)
                        args3.putInt("CantidadDeRutas", contadorRutaNumeroAbordado)
                        args3.putString("orientacion", orientacionRuta)
                        args3.putString("rutaSeleccionadaNumero", rutaSeleccionadaNumero)
                        args3.putString("rutaSeleccionadaAbordado", rutaSeleccionada)


                        //intent para iniciar la actividad siguiente
                        val intentBuscarOtraruta = Intent(
                            activity,
                            search_route::class.java
                        )
                        // Agregas el Bundle al Intent e inicias ActivityB
                        intentBuscarOtraruta.putExtras(args3)
                        spnRuta.setSelection(0)
                        startActivity(intentBuscarOtraruta)


                    } else {
                        val bundleRecargar = Bundle()
                        bundleRecargar.putString("OrientacionRuta", orientacionRuta)
                        bundleRecargar.putInt("CantidadDeRutas", contadorRutaNumeroAbordado)
                        //se pasa el correo de confianza
                        bundleRecargar.putString("contactoConfianza", correoConfianza)
                        bundleRecargar.putString("RutaSeleccionada", rutaSeleccionadaSpn)


                        //se envian las rutas predeterminadas del usuario junto a la ruta selccionada
                        for (contadorarray in 0..rutasRecargar.count()) {
                            if (contadorarray < rutasRecargar.count() && rutasRecargar[contadorarray] != null) {
                                bundleRecargar.putString(
                                    "RMF$contadorarray",
                                    rutasRecargar[contadorarray]
                                )
                            }
                        }

                        // se pasan las rutas seleccionadas disponibles junto a la cantidad de ellas
                        val contadorRD2 = contadorRutaNumeroAbordado - 1
                        var contadorRD0 = 1
                        //se obtiene el numero de la primera ruta seleccionada
                        for (contadorRD in 0 until contadorRD2) {
                            //se obtiene losnombres de las rutas disponibles.zza
                            bundleRecargar.putString(
                                "Ruta$contadorRD0",
                                rutasdisponibles[contadorRD]
                            )
                            contadorRD0++
                        }


                        //intent para iniciar la actividad siguiente
                        val intentBuscarOtraruta = Intent(activity, pasadorSpinner::class.java)
                        // Agregas el Bundle al Intent e inicias ActivityB
                        intentBuscarOtraruta.putExtras(bundleRecargar)
                        activity!!.finish()
                        startActivity(intentBuscarOtraruta)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            })

            //funcionalidad button cambiar orientacion.
            btn_OrientacionRuta = vistaMapasEnTiempoReal!!.findViewById<Button>(R.id.btn_ida_o_vuelta)
            if (orientacionRuta == "ida") {
                btn_OrientacionRuta!!.setText("->")
            } else {
                btn_OrientacionRuta!!.setText("<-")
            }
            btn_OrientacionRuta!!.setOnClickListener(View.OnClickListener { v: View? ->
                orientacionRuta = if (orientacionRuta == "ida") {
                    "vuelta"
                } else {
                    "ida"
                }
                val bundleRecargar = Bundle()
                bundleRecargar.putString("OrientacionRuta", orientacionRuta)
                bundleRecargar.putInt("CantidadDeRutas", contadorRutaNumeroAbordado)
                //se pasa el correo de confianza
                bundleRecargar.putString("contactoConfianza", correoConfianza)
                bundleRecargar.putString("RutaSeleccionada", rutaSeleccionada)


                //se envian las rutas predeterminadas del usuario junto a la ruta selccionada
                for (contadorarray in 0..2) {
                    if (rutasRecargar[contadorarray] != null) {
                        bundleRecargar.putString("RMF$contadorarray", rutasRecargar[contadorarray])
                    }
                }

                // se pasan las rutas seleccionadas disponibles junto a la cantidad de ellas
                val contadorRD2 = contadorRutaNumeroAbordado - 1
                var contadorRD0 = 1
                //se obtiene el numero de la primera ruta seleccionada
                for (contadorRD in 0 until contadorRD2) {
                    //se obtiene losnombres de las rutas disponibles.zza
                    bundleRecargar.putString("Ruta$contadorRD0", rutasdisponibles[contadorRD])
                    contadorRD0++
                }


                //intent para iniciar la actividad siguiente
                val intentBuscarOtraruta = Intent(activity, pasadorSpinner::class.java)
                // Agregas el Bundle al Intent e inicias ActivityB
                intentBuscarOtraruta.putExtras(bundleRecargar)
                requireActivity().finish()
                startActivity(intentBuscarOtraruta)
            })

            //funcionalidad ya voy en camino
            btn_voyEnCamino = vistaMapasEnTiempoReal!!.findViewById<Button>(R.id.btn_voy_en_camino)
            btn_voyEnCamino!!.setOnClickListener(View.OnClickListener { v: View? ->

                // Creamos un nuevo Bundle ahora para pasar los datos al fragmento de mapas
                val args2 = Bundle()
                val contadorRD2 = contadorRutaNumeroAbordado - 1
                var contadorRD0 = 1
                //se obtiene el numero de la primera ruta seleccionada
                for (contadorRD in 0 until contadorRD2) {
                    //se obtiene losnombres de las rutas disponibles.zza
                    args2.putString("Ruta$contadorRD0", rutasdisponibles[contadorRD])
                    contadorRD0++
                }

                //se pasa el correo de confianza
                args2.putString("contactoConfianza", correoConfianza)
                args2.putInt("CantidadDeRutas", contadorRutaNumeroAbordado)
                args2.putString("orientacion", orientacionRuta)
                args2.putString("rutaSeleccionadaNumero", rutaSeleccionadaNumero)
                args2.putString("rutaSeleccionadaAbordado", rutaSeleccionada)

                //intent para iniciar la actividad siguiente
                val intentAbordado = Intent(activity, notify_LockScreen::class.java)
                // Agregas el Bundle al Intent e inicias ActivityB
                intentAbordado.putExtras(args2)
                startActivity(intentAbordado)
            })


            //instanciar boton para parada mas cercana
            btn_ParadaCercana = vistaMapasEnTiempoReal!!.findViewById<Button>(R.id.btn_ParadaCercana)

            //instancear boton mi ubbicacion
            btn_mi_ubicacion = vistaMapasEnTiempoReal!!.findViewById<Button>(R.id.btn_mi_ubicacion)

            //instanciar buscar parada autocomplete text view y boton
            btn_buscar_parada = vistaMapasEnTiempoReal!!.findViewById<Button>(R.id.btn_buscar_parada)
            actv_Buscar_Parada =
                vistaMapasEnTiempoReal!!.findViewById<AutoCompleteTextView>(R.id.actv_buscar_parada)
            //adaptador para buscar paradas
            val adapterParadasNombres = ArrayAdapter<String>(
                requireActivity(),
                R.layout.support_simple_spinner_dropdown_item,
                nombresDeParadasDisponibles
            )
            actv_Buscar_Parada!!.setAdapter(adapterParadasNombres)


            //implementar mapa en el fragmento
            val mapFragment =
                this.childFragmentManager.findFragmentById(R.id.mapRutasEnTiempoReal) as SupportMapFragment?
            mapFragment!!.getMapAsync(this)
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {

        //Agregar marcadores en base a la ruta seleccionada.
        mMap = googleMap

        //habilitr controles de localizacion y zoom
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
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
            return
        }
        mMap!!.isMyLocationEnabled = true

        //desactivar boton mi ubicacion
        mMap!!.uiSettings.isMyLocationButtonEnabled = false

        //dar funcionalidad a boton mi ubicacion.
        btn_mi_ubicacion!!.setOnClickListener { v: View? ->
            userFLPC2 = LocationServices.getFusedLocationProviderClient(requireActivity())
            userFLPC2!!.getLastLocation().addOnSuccessListener(
                requireActivity()
            ) { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    val latuserubicacion = location.latitude
                    val lnguserubicacion = location.longitude


                    //variables para actualizar camara a la paraa mas cercana
                    val coordenadasmiubicacion =
                        LatLng(latuserubicacion, lnguserubicacion)
                    val miUbicacionboton =
                        CameraUpdateFactory.newLatLngZoom(coordenadasmiubicacion, 18f)
                    mMap!!.animateCamera(miUbicacionboton)
                }
            }
        }


        //barra de progreso en lo que carga el mapa
        progressDialog!!.setMessage("Cargando Mapa...")
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()

        //funcionalidad para buscar parada
        btn_buscar_parada!!.setOnClickListener {
            val ParadaABuscar = actv_Buscar_Parada!!.text.toString().trim { it <= ' ' }
            //ver cual es la parada que se busco
            val contadorBuscarParadaMenosUno = CantidadDeParadas - 1
            var bandera = 0
            var numeroDeParadaABuscar = 1
            for (contadorParadas in 0 until contadorBuscarParadaMenosUno) {
                val nombreDeParada = nombresDeParadasDisponibles[contadorParadas]
                if (ParadaABuscar == nombreDeParada) {
                    bandera = 1
                    numeroDeParadaABuscar = contadorParadas + 1
                }
            }
            if (ParadaABuscar == "") {
                actv_Buscar_Parada!!.error = "Porfavor Ingrese un Nombre De parada a Buscar"
                Toast.makeText(
                    activity,
                    "Porfavor Ingrese un Nombre De parada a Buscar",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (bandera == 1) {
                //aqui se actualizara la camara en base a la parada
                OTBReference!!.child("Rutas").child(rutaSeleccionadaNumero!!)
                    .child(orientacionRuta!!).child(
                        "Parada$numeroDeParadaABuscar"
                    ).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val latParadastring = dataSnapshot.child("latitud").value.toString()
                            val lngParadastring = dataSnapshot.child("longitud").value.toString()
                            val latParadaDouble = latParadastring.toDouble()
                            val lngParadaDouble = lngParadastring.toDouble()

                            //variables para actualizar camara a la paraa mas cercana
                            val coordenadas =
                                LatLng(latParadaDouble, lngParadaDouble)
                            val ParadaBuscada = CameraUpdateFactory.newLatLngZoom(coordenadas, 18f)
                            mMap!!.animateCamera(ParadaBuscada)

                            //cerrar teclado al presionar boton
                            val inputMethodManager =
                                activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(
                                actv_Buscar_Parada!!.windowToken,
                                0
                            )
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
            } else {
                actv_Buscar_Parada!!.error = "Porfavor Ingrese un Nombre De parada Valido a Buscar"
                Toast.makeText(
                    activity,
                    "Porfavor Ingrese un Nombre De parada Valido a Buscar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        //boton para buscar la parada mas cercana
        btn_ParadaCercana!!.setOnClickListener {
            //variable para buscar la uktima ubicacion del usuario
            userFLPC = LocationServices.getFusedLocationProviderClient(requireActivity())
            userFLPC!!.lastLocation
                .addOnSuccessListener(
                    requireActivity()
                ) { location ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        latuser = location.latitude
                        lnguser = location.longitude
                        OTBReference!!.child("Rutas").child(rutaSeleccionadaNumero!!)
                            .child(orientacionRuta!!)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    //contador para paradas
                                    var contadorParadas = 1
                                    var latuserresult = 0.0
                                    var lnguserresult = 0.0
                                    var latuserresult2: Double? = 0.0
                                    var lnguserresult2: Double? = 0.0
                                    var suma1 = 0.0
                                    var suma2 = 0.0
                                    oRutas.setLatuser(0.0)
                                    oRutas.setLnguser(0.0)
                                    for (snapshot2 in dataSnapshot.children) {
                                        // se cargan marcadores nuevos
                                        val latitud1 = dataSnapshot.child("Parada$contadorParadas")
                                            .child("latitud").value.toString()
                                        val longitud1 = dataSnapshot.child("Parada$contadorParadas")
                                            .child("longitud").value.toString()
                                        val latitud = latitud1.toDouble()
                                        val longitud = longitud1.toDouble()
                                        latuserresult = latuser - oRutas.getLatuser()!!
                                        lnguserresult = lnguser - oRutas.getLnguser()!!
                                        suma1 =
                                            Math.abs(latuserresult) + Math.abs(
                                                lnguserresult
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
                                            oRutas.setLatuser(latitud)
                                            oRutas.setLnguser(longitud)
                                        }
                                        contadorParadas++
                                    }

                                    //variables para actualizar camara a la paraa mas cercana
                                    val coordenadas = LatLng(
                                        oRutas.getLatuser()!!, oRutas.getLnguser()!!
                                    )
                                    val miUbicacion =
                                        CameraUpdateFactory.newLatLngZoom(coordenadas, 18f)
                                    mMap!!.animateCamera(miUbicacion)
                                }

                                override fun onCancelled(databaseError: DatabaseError) {}
                            })
                    }
                }
        }


        //se obtiene de la base de datos el nombre de las rutas que estan disponibles a ver en el fragmento y se compara con la que selecciono el usuario para ver si existe.
        OTBReference!!.child("Rutas").child(rutaSeleccionadaNumero!!).child(orientacionRuta!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (marker in realTimeMarkers) {
                            //se remueven marcadores viejos
                            marker.remove()
                        }
                        for (snapshot in dataSnapshot.children) {
                            // se cargan marcadores nuevos
                            oRutas = snapshot.getValue(Rutas::class.java)!!
                            val latitud = oRutas.getLatitud()
                            val longitud = oRutas.getLongitud()
                            val tittleu = oRutas.getTittle()
                            val snnipetu = oRutas.getSnnipet()
                            val markerOptions = MarkerOptions()

                            //se obtiene la hora actual para compararse
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
                                fechaMarcador = formateadorfecha.parse(snnipetu)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            val diferenciaEn_ms = fechaactual!!.time - fechaMarcador!!.time
                            val minutos = diferenciaEn_ms / 60000

                            //funcion para poner marcadores en base a el riempo que ha pasado desde que se marco
                            var iconu = "sin_abordar"
                            iconu = if (minutos < 1) {
                                "abordado_menos_min"
                            } else if (minutos > 1 && minutos < 10) {
                                "abordado_mas_min"
                            } else {
                                "sin_abordar"
                            }
                            var snnipethaceuanto = ""
                            if (minutos < 1) {
                                val segundos = diferenciaEn_ms / 6000
                                snnipethaceuanto = if (segundos == 1L) {
                                    "Ruta abordada hace: $segundos Segundo."
                                } else {
                                    "Ruta abordada hace: $segundos Segundos."
                                }
                            } else if (minutos < 60) {
                                snnipethaceuanto = if (minutos == 1L) {
                                    "Ruta abordada hace: $minutos Minuto."
                                } else {
                                    "Ruta abordada hace: $minutos Minutos."
                                }
                            } else if (minutos >= 60 && minutos < 1440) {
                                val horas = minutos / 60
                                snnipethaceuanto = if (horas == 1L) {
                                    "Ruta abordada hace: $horas Hora."
                                } else {
                                    "Ruta abordada hace: $horas Horas."
                                }
                            } else if (minutos >= 1440) {
                                val horas = minutos / 60
                                val dias = horas / 24
                                snnipethaceuanto = if (dias == 1L) {
                                    "Ruta abordada hace: $dias Dia."
                                } else {
                                    "Ruta abordada hace: $dias Dias."
                                }
                            }
                            if (!isAdded) {
                                return
                            } else {
                                val imageBitmap = BitmapFactory.decodeResource(
                                    activity!!.resources,
                                    activity!!.resources.getIdentifier(
                                        iconu,
                                        "drawable",
                                        vistaMapasEnTiempoReal!!.context.packageName
                                    )
                                )
                                markerOptions.position(
                                    LatLng(
                                        latitud!!,
                                        longitud!!
                                    )
                                ).title(tittleu).snippet(snnipethaceuanto)
                                    .icon(BitmapDescriptorFactory.fromBitmap(imageBitmap))
                                    .anchor(0.0f, 1.0f)
                                tmpRealTimeMarker.add(mMap!!.addMarker(markerOptions)!!)
                            }
                        }


                        //se vacian los marcadores agregados en otro array de marcadores para cuando se necesiten borrar
                        realTimeMarkers.clear()
                        realTimeMarkers.addAll(tmpRealTimeMarker)
                        progressDialog!!.dismiss()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        val usuario = LatLng(latuser, lnguser)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(usuario, 16f))
    }


    private fun ultimaUbicacionDelUsuario() {
        //barra de progreso en lo que carga el mapa
        userFLPC = LocationServices.getFusedLocationProviderClient(requireActivity())
        if ((ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        userFLPC!!.lastLocation
            .addOnSuccessListener(
                requireActivity()
            ) { location -> // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    latuser = location.latitude
                    lnguser = location.longitude
                }
            }
    }
}