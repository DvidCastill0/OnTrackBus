package com.android.ontrackbus.Login

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.android.ontrackbus.Models.Rutas
import com.android.ontrackbus.R
import com.android.ontrackbus.User_Menu.MenuActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.regex.Pattern

class Login : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //TextView que llevara a recuperar contraseña
    private var reestablecerContraseña: TextView? = null

    //variable para base de datos
    private var OTBReference: DatabaseReference? = null

    //variables para botones y cajas de texto
    private var btnEntrar: Button? = null
    //variables para botones y cajas de texto
    private var btnRegistro: Button? = null
    private var edtcorreo: EditText? = null
    private var edtcontrasena:EditText? = null
    private var contadorRMFDisponible = 1
    private var contadorRMFNumero = 1
    private val or: Rutas = Rutas()

    //El objeto bundle permite pasar parametros entre activitis y fragments
    var bundle = Bundle()

    //instanciamos una barra de progreso mientras se inicia sesion
    private var progressDialog: ProgressDialog? = null

    //clase para loguear usuarios
    private var mAuth: FirebaseAuth? = null

    //contadores para ruta mas frecuentada
    private var contadorRMF1 = 1
    //contadores para ruta mas frecuentada
    private var contadorRutaNumero = 1

    //array para descargar las rutas que el usuario ha seleccionado previamente
    private val rutasSeleccionadas = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //param1 = it.getString(ARG_PARAM1)
            //param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var vw_login = inflater.inflate(R.layout.fragment_login, container, false)



        //union de las cajas de texto con java
        edtcorreo = vw_login.findViewById<EditText>(R.id.edt_correo)
        edtcontrasena = vw_login.findViewById<EditText>(R.id.edt_contrasena)

        //se instancia la barra de progreso

        //se instancia la barra de progreso
        progressDialog = ProgressDialog(activity)

        //se referencia la clase auth

        //se referencia la clase auth
        mAuth = FirebaseAuth.getInstance()

        setOnClicksEvents(vw_login)

        //Instancear variable de base de datos

        //Instancear variable de base de datos
        OTBReference = FirebaseDatabase.getInstance().reference

        //se obtiene de la base de datos el nombre de las rutas que se mostraran a elegir en el registrod e lo s usuarios
        OTBReference!!.child("Rutas").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        //se obtiene losnombres de las rutas disponibles.zza
                        val rutaname = dataSnapshot.child("Ruta$contadorRMFDisponible")
                            .child("NombreDeRuta").value.toString()
                        or.setNombreDeRuta(rutaname)
                        //se le agregan valores al bundle para pasarlos a otra interfaz
                        bundle.putString("ruta$contadorRMFDisponible", or.getNombreDeRuta())
                        contadorRMFDisponible++
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })


        // Inflate the layout for this fragment
        return vw_login;
    }

    //region OnClicks Buttons
    private fun setOnClicksEvents(mainView: View) {

        //region btnRegistro
        btnRegistro = mainView.findViewById<View>(R.id.btn_registro) as Button
        btnRegistro!!.setOnClickListener(View.OnClickListener { v: View? ->

            //se le agregan valores al bundle para pasarlos a otra interfaz
            bundle.putInt("cantidadDeRutas", contadorRMFDisponible - 1)

            // Crear una instancia del fragmento y asignarle el Bundle
            val loginFragment = register_user()
            loginFragment.arguments = bundle

            // Realizar la transacción de fragmento
            this.requireActivity().supportFragmentManager.commit {
                replace(R.id.fl_Container, loginFragment)
                setReorderingAllowed(true)
                addToBackStack("register_user")
            }
        })
        //endregion

        //region btnEntrar
        //Declaracion de boton para entrar al Menu de usuarios en caso de que los datos puestos en las cajas de texto sean correctos
        btnEntrar = mainView.findViewById<View>(R.id.btn_entrar) as Button
        btnEntrar!!.setOnClickListener(View.OnClickListener { v: View? -> IniciarSesion() })
        //endregion

        //region btnRestablecerContrasena
        //Boton para ir a fragmento donde se recupera contraseña.
        reestablecerContraseña = mainView.findViewById(R.id.btnRestablecerContrasena)
        //poner texto en subrayado
        reestablecerContraseña!!.setPaintFlags(reestablecerContraseña!!.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        reestablecerContraseña!!.setOnClickListener(View.OnClickListener { v: View? ->
            this.requireActivity().supportFragmentManager.commit {
                replace<recover_password>(R.id.fl_Container)
                setReorderingAllowed(true)
                addToBackStack("recover_password")
            }
        })
        //endregion
    }

    private fun IniciarSesion() {
        val correo = edtcorreo!!.text.toString().trim { it <= ' ' }
        val contraseña = edtcontrasena!!.text.toString().trim { it <= ' ' }
        if (comprobacionCorreo() == false) {
            //revisa que el correo sea valido y no este vacio
        } else if (contraseña == "") {
            edtcontrasena!!.error = "Dato Requerido"
            Toast.makeText(activity, "Ingrese Una Contraseña", Toast.LENGTH_SHORT).show()
        } else {
            progressDialog!!.setMessage("Iniciando Sesion...")
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()

            //loguear usuario
            mAuth!!.signInWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener { task -> //Variable para verificar si el usuario ha verificado su correo
                    val user = mAuth!!.currentUser
                    //checar si se pudo entrar y si el correo o la contraseña son correctos
                    if (task.isSuccessful) {
                        if (!user!!.isEmailVerified) {
                            edtcorreo!!.error = "Correo Electronico no verificado."
                            Toast.makeText(
                                activity,
                                "Porfavor ve a tu Correo Electronico y da click en el corero de verificacion, para verificar tu cuenta.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            OTBReference!!.addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    //se obtiene de la base de datos el nombre de las rutas seleccionadas por el usuario
                                    val id = mAuth!!.currentUser!!.uid
                                    if (dataSnapshot.exists()) {
                                        for (snapshot in dataSnapshot.child("Users")
                                            .child(id).child("RutaMasFrecuentada").children) {
                                            val istring = Integer.toString(contadorRMF1)
                                            val userkk = dataSnapshot.child("Users").child(id)
                                                .child("RutaMasFrecuentada").child(
                                                    "RMF$istring"
                                                ).value
                                            if(userkk!=null)
                                                rutasSeleccionadas.add(userkk.toString())
                                            contadorRMF1++
                                        }

                                        //bundle para pasar las rutas seleccionadas por el usuario
                                        // Inicializas el Bundle
                                        val bundleSesion = Bundle()

                                        //agregamos el correo de confianza
                                        val correoConfianza = dataSnapshot.child("Users").child(id)
                                            .child("contactoConfianza").value.toString()
                                        bundleSesion.putString("contactoConfianza", correoConfianza)

                                        //intent para iniciar la actividad siguiente
                                        val intentMenuUsuario = Intent(
                                            activity,
                                            MenuActivity::class.java
                                        )
                                        for (contadorarray in 0..rutasSeleccionadas.count()) {
                                            if (contadorarray < rutasSeleccionadas.count() && rutasSeleccionadas[contadorarray] != null) {
                                                bundleSesion.putString(
                                                    "RMF$contadorarray",
                                                    rutasSeleccionadas[contadorarray]
                                                )
                                            }
                                        }
                                        var rutaseleccionadaNumero: String? = null
                                        //se obtiene el numero de la primera ruta junto a el nombre de todas las rutas
                                        //se obtiene el numero de la primera ruta seleccionada
                                        for (snapshot in dataSnapshot.child("Rutas").children) {
                                            //se obtiene losnombres de las rutas disponibles.zza
                                            val rutaname = dataSnapshot.child("Rutas").child(
                                                "Ruta$contadorRutaNumero"
                                            ).child("NombreDeRuta").value.toString()
                                            bundleSesion.putString(
                                                "Ruta$contadorRutaNumero",
                                                rutaname
                                            )
                                            if (rutaname == bundleSesion.getString("RMF0")) {
                                                bundleSesion.putString(
                                                    "RutaSeleccionadaNumero",
                                                    "Ruta$contadorRutaNumero"
                                                )
                                                rutaseleccionadaNumero = "Ruta$contadorRutaNumero"
                                            }
                                            contadorRutaNumero++
                                        }
                                        bundleSesion.putInt("CantidadDeRutas", contadorRutaNumero)
                                        var contadorParadas = 1
                                        //se descargan todaslas paradas de laprimera ruta seleccionada
                                        for (snapshot in dataSnapshot.child("Rutas").child(
                                            rutaseleccionadaNumero!!
                                        ).child("ida").children) {
                                            val nombreParada = dataSnapshot.child("Rutas").child(
                                                rutaseleccionadaNumero
                                            ).child("ida").child("Parada$contadorParadas")
                                                .child("tittle").value.toString()
                                            bundleSesion.putString(
                                                "Parada$contadorParadas",
                                                nombreParada
                                            )
                                            contadorParadas++
                                        }
                                        bundleSesion.putInt("CantidadDeParadas", contadorParadas)

                                        // Agregas el Bundle al Intent e inicias ActivityB
                                        intentMenuUsuario.putExtras(bundleSesion)
                                        progressDialog!!.dismiss()
                                        startActivity(intentMenuUsuario)
                                        activity!!.finish()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {}
                            })
                        }
                    } else {
                        //si el usuario no pudo iniciar sesion
                        if (task.exception is FirebaseAuthInvalidUserException) {
                            edtcorreo!!.error = "Correo No Registrado"
                            Toast.makeText(
                                activity,
                                "Este Correo no esta Asociado a ninguna cuenta, Por favor Registrate",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            edtcontrasena!!.error = "Contraseña Incorrecta"
                            Toast.makeText(
                                activity,
                                "La Contraseña no coincide con el Correo Ingresado.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                activity,
                                "Algo Salio mal, favor de reportar",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                    progressDialog!!.dismiss()
                }
        }
    }


    private fun comprobacionCorreo(): Boolean {
        // El email a validar
        val correo = edtcorreo!!.text.toString().trim { it <= ' ' }
        // Patrón para validar el email
        val pattern = Pattern
            .compile(
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            )
        //busca nuestro email en base a la expresion que selecu¡cionamos del pattern en caso de que sea correcta nos retorna un valor true
        val mather = pattern.matcher(correo)
        var bandera = false
        if ((correo == "")) {
            edtcorreo!!.error = "Dato Requerido"
            Toast.makeText(activity, "Porfavor Ingrese Un Correo Electronico ", Toast.LENGTH_SHORT)
                .show()
        } else if (mather.find() == false) {
            Toast.makeText(
                activity,
                "Correo Electronico Invalido, Porfavor revise la estructura del Correo Electronico Ingresado.",
                Toast.LENGTH_LONG
            ).show()
            edtcorreo!!.error = "Estructura de Correo electronico Invalida."
        } else {
            bandera = true
        }
        return bandera
    }
    //endregion

    //region OnStart
    //Checar que si ya inicio sesion no nos muestre la pantala de login.
    override fun onStart() {
        super.onStart()
        val currentUser = mAuth!!.currentUser
        //revisar conexion a internet
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
            if (currentUser != null) {


                //bara de progreso en lo que se obtienen las direcciones
                progressDialog!!.setMessage("Iniciando Sesion...")
                progressDialog!!.setCanceledOnTouchOutside(false)
                progressDialog!!.show()
                OTBReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        //se obtiene de la base de datos el nombre de las rutas seleccionadas por el usuario
                        val id = mAuth!!.currentUser!!.uid
                        if (dataSnapshot.exists()) {
                            for (snapshot in dataSnapshot.child("Users").child(id)
                                .child("RutaMasFrecuentada").children) {
                                val istring = Integer.toString(contadorRMF1)
                                val userkk = dataSnapshot.child("Users").child(id)
                                    .child("RutaMasFrecuentada").child(
                                        "RMF$istring"
                                    ).value
                                if(userkk != null)
                                    rutasSeleccionadas.add(userkk.toString())
                                contadorRMF1++
                            }

                            //bundle para pasar las rutas seleccionadas por el usuario
                            // Inicializas el Bundle
                            val bundleSesion = Bundle()

                            //agregamos el correo de confianza
                            val correoConfianza = dataSnapshot.child("Users").child(id)
                                .child("contactoConfianza").value.toString()
                            bundleSesion.putString("contactoConfianza", correoConfianza)


                            //intent para iniciar la actividad siguiente
                            val intentMenuUsuario = Intent(activity, MenuActivity::class.java)
                            for (contadorarray in 0..rutasSeleccionadas.count()) {
                                if (contadorarray < rutasSeleccionadas.count() && rutasSeleccionadas[contadorarray] != null) {
                                    bundleSesion.putString(
                                        "RMF$contadorarray",
                                        rutasSeleccionadas[contadorarray]
                                    )
                                }
                            }
                            var rutaseleccionadaNumero: String? = null
                            //se obtiene el numero de la primera ruta junto a el nombre de todas las rutas
                            //se obtiene el numero de la primera ruta seleccionada
                            for (snapshot in dataSnapshot.child("Rutas").children) {
                                //se obtiene losnombres de las rutas disponibles.zza
                                val rutaname =
                                    dataSnapshot.child("Rutas").child("Ruta$contadorRutaNumero")
                                        .child("NombreDeRuta").value.toString()
                                bundleSesion.putString("Ruta$contadorRutaNumero", rutaname)
                                if (rutaname == bundleSesion.getString("RMF0")) {
                                    bundleSesion.putString(
                                        "RutaSeleccionadaNumero",
                                        "Ruta$contadorRutaNumero"
                                    )
                                    rutaseleccionadaNumero = "Ruta$contadorRutaNumero"
                                }
                                contadorRutaNumero++
                            }
                            bundleSesion.putInt("CantidadDeRutas", contadorRutaNumero)
                            var contadorParadas = 1
                            //se descargan todaslas paradas de laprimera ruta seleccionada
                            for (snapshot in dataSnapshot.child("Rutas").child(
                                rutaseleccionadaNumero!!
                            ).child("ida").children) {
                                val nombreParada = dataSnapshot.child("Rutas").child(
                                    rutaseleccionadaNumero!!
                                ).child("ida").child(
                                    "Parada$contadorParadas"
                                ).child("tittle").value.toString()
                                bundleSesion.putString("Parada$contadorParadas", nombreParada)
                                contadorParadas++
                            }
                            bundleSesion.putInt("CantidadDeParadas", contadorParadas)


                            // Agregas el Bundle al Intent e inicias ActivityB
                            intentMenuUsuario.putExtras(bundleSesion)
                            progressDialog!!.dismiss()
                            startActivity(intentMenuUsuario)
                            activity!!.finish()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            } else {
                // Si hay conexión a Internet en este momento
                Toast.makeText(activity, "Bienvenido, Porfavor Inicia Sesion", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            // No hay conexión a Internet en este momento
            Toast.makeText(
                activity,
                "No hay conexion a Internet, asegurate de estar conectado a una red para poder Iniciar Sesion o Registrarte.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    //endregion
}