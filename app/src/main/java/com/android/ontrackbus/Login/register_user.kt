package com.android.ontrackbus.Login

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.android.ontrackbus.Models.Usuarios
import com.android.ontrackbus.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class register_user() : Fragment(), Parcelable {
    //variable de firebase para sesion de  usuarios
    private var mAuth: FirebaseAuth? = null

    //Declaracion de variables  componentes de interfaz
    private var btn_Volver: Button? = null
    private var btn_Registrar: Button? = null
    private var btn_AgregarRuta: Button? = null
    private var btn_QuitarRuta1: Button? = null
    private var btn_QuitarRuta2: Button? = null
    private var btn_QuitarRuta3: Button? = null
    private var et_Nombre: EditText? = null
    private var et_Apellido: EditText? = null
    private var et_Correo: EditText? = null
    private var et_Contraseña: EditText? = null
    private var et_ContraseñaConfirmacion: EditText? = null
    private var et_ContactoConfianza: EditText? = null
    private var tv_ruta1: TextView? = null
    private var tv_ruta2: TextView? = null
    private var tv_ruta3: TextView? = null
    private var actv_RMF: AutoCompleteTextView? = null

    //getter and setter para obtener informacion en el programa
    private val u: Usuarios = Usuarios()

    //Reerencia para conectar con firebase.
    private var OTBReference: DatabaseReference? = null

    //Vista de este fragmento
    private var vistaRegistro: View? = null

    //arraylist para autocompletetextview
    private val rutas = ArrayList<String>()

    //array que guardara las rutas que el usuario decida guardar
    private val array_rutas_seleccionadas = arrayOfNulls<String>(3)

    //contador para descargar rutas y para revisar las rutas que aun quedan disponibles
    private var i = 0

    //contador para descargar rutas y para revisar las rutas que aun quedan disponibles
    private var rutasagregadas = 3

    //instanciamos una barra de profreso mientras se registra el usuario
    private var progressDialog: ProgressDialog? = null

    constructor(parcel: Parcel) : this() {
        i = parcel.readInt()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //obtener cantidad de rutas actuales en la lista

            //obtener cantidad de rutas actuales en la lista
            i = requireArguments().getInt("cantidadDeRutas")

            for (i2 in 1..i) {
                //obtener nombres de las rutas para mostrar al usuario a la hora de escoger las 3 predeterminadas
                val ruta = requireArguments().getString("ruta$i2")
                rutas.add(ruta!!)
            }

        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(i)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<register_user> {
        override fun createFromParcel(parcel: Parcel): register_user {
            return register_user(parcel)
        }

        override fun newArray(size: Int): Array<register_user?> {
            return arrayOfNulls(size)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment
        vistaRegistro = inflater.inflate(R.layout.fragment_register_user, container, false)

        //instanceamos la referencia afirebase y  a  su autenticacion
        //instanceamos la referencia afirebase y  a  su autenticacion
        OTBReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        //Instanceamos los elementos de xml con los de java

        //Instanceamos los elementos de xml con los de java
        et_Nombre = vistaRegistro!!.findViewById(R.id.etNombre)
        et_Apellido = vistaRegistro!!.findViewById(R.id.etApellido)
        et_Correo = vistaRegistro!!.findViewById(R.id.etCorreoElectronicoRegistro)
        et_Contraseña = vistaRegistro!!.findViewById(R.id.etContrasenaRegistro)
        et_ContraseñaConfirmacion =
            vistaRegistro!!.findViewById(R.id.etContrasenaRegistroConfirmacion)
        et_ContactoConfianza = vistaRegistro!!.findViewById(R.id.etContactoConfianzaRegistro)

        actv_RMF = vistaRegistro!!.findViewById(R.id.actvRutaMasFrecuentadaRegistro)
        //array adapter para autocompletetextview
        //array adapter para autocompletetextview
        val adapterrutas = ArrayAdapter<String>(
            requireActivity(),
            R.layout.support_simple_spinner_dropdown_item,
            rutas
        )
        actv_RMF!!.setAdapter(adapterrutas)

        tv_ruta1 = vistaRegistro!!.findViewById(R.id.tvRuta1)
        tv_ruta1!!.setVisibility(View.INVISIBLE)
        tv_ruta2 = vistaRegistro!!.findViewById(R.id.tvRuta2)
        tv_ruta2!!.setVisibility(View.INVISIBLE)
        tv_ruta3 = vistaRegistro!!.findViewById(R.id.tvRuta3)
        tv_ruta3!!.setVisibility(View.INVISIBLE)

        btn_QuitarRuta1 = vistaRegistro!!.findViewById(R.id.btn_eliminar_ruta_1)
        btn_QuitarRuta1!!.setVisibility(View.INVISIBLE)
        btn_QuitarRuta2 = vistaRegistro!!.findViewById(R.id.btn_eliminar_ruta_2)
        btn_QuitarRuta2!!.setVisibility(View.INVISIBLE)
        btn_QuitarRuta3 = vistaRegistro!!.findViewById(R.id.btn_eliminar_ruta_3)
        btn_QuitarRuta3!!.setVisibility(View.INVISIBLE)

        progressDialog = ProgressDialog(activity)

        //metodos de funcionalidad de los botones

        //metodos de funcionalidad de los botones
        eliminarRuta()
        agregarRuta()
        BotonRegresar()
        BotonRegistrar()

        return vistaRegistro
    }


    //metodo de accion oara regresar a el login.
    private fun BotonRegresar() {
        btn_Volver = vistaRegistro!!.findViewById<View>(R.id.btn_regresaralogin) as Button
        btn_Volver!!.setOnClickListener(View.OnClickListener {
            // Realizar la transacción de fragmento
            this.requireActivity().supportFragmentManager.commit {
                replace<Login>(R.id.fl_Container)
                setReorderingAllowed(true)
                addToBackStack("register_user")
            }
        })
    }

    //metodo de accion del boton registrar
    private fun BotonRegistrar() {
        btn_Registrar = vistaRegistro!!.findViewById<View>(R.id.btn_Registrarse) as Button
        btn_Registrar!!.setOnClickListener(View.OnClickListener { validacion() })
    }

    //metodo donde se agregan rutas seleccionada spor le usuario.
    private fun agregarRuta() {
        btn_AgregarRuta = vistaRegistro!!.findViewById<View>(R.id.btn_Agregar_Ruta) as Button
        btn_AgregarRuta!!.setOnClickListener(View.OnClickListener {
            //bandera para saber si la ruta escrita esta dentrod e als rutas disponibles, para ver que no se repita una ruta
            var bandera = 0
            var banderaRutaRepetida = 0
            //variable que obtiene el valor del autocompletetextview
            val rutase = actv_RMF!!.text.toString().trim { it <= ' ' }

            //se revisa que no se hayan agregado mas de 3 rutas
            if (rutasagregadas != 0) {
                //Se revisa que no sea una caja vacia
                if (rutase == "") {
                    Toast.makeText(
                        activity,
                        "Porfavor Ingresa un numero de ruta",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    for (contador in 0 until i) {
                        //if que revisa la ruta pedida por el usuario entre las rutas disponibles a ver
                        if (rutase == rutas[contador]) {
                            //bucle que busca que nos e este repitiendo ninguna ruta
                            for (contadorRutaRepetida in 0..2) {
                                //se revisa que el valor ingresado no haya sido repetido
                                if (rutase == array_rutas_seleccionadas[contadorRutaRepetida]) {
                                    Toast.makeText(
                                        activity,
                                        "Ruta $rutase Repetida favor de agregar un valor diferente.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    banderaRutaRepetida = 1
                                    bandera = 1
                                    break
                                }
                            }
                            if (banderaRutaRepetida == 0) {
                                /*Inicia un ciclo for que busca el primer espacio en blanco en el array de
                                     * de las rutas seleccionadas*/
                                for (contadorarray in 0..2) {
                                    //Si se encuentra un elemento del array que esté vacío...
                                    if (array_rutas_seleccionadas[contadorarray] == null) {

                                        //... se establece su valor en un string de la ruta seleccionada en el spinner
                                        array_rutas_seleccionadas[contadorarray] = rutase
                                        rutasagregadas--
                                        when (contadorarray) {
                                            0 -> {
                                                tv_ruta1!!.text = rutase
                                                tv_ruta1!!.visibility = View.VISIBLE
                                                btn_QuitarRuta1!!.visibility = View.VISIBLE
                                                u.setRMF1(array_rutas_seleccionadas[0])
                                            }

                                            1 -> {
                                                tv_ruta2!!.text = rutase
                                                tv_ruta2!!.visibility = View.VISIBLE
                                                btn_QuitarRuta2!!.visibility = View.VISIBLE
                                                u.setRMF2(array_rutas_seleccionadas[1]!!)
                                            }

                                            2 -> {
                                                tv_ruta3!!.text = rutase
                                                tv_ruta3!!.visibility = View.VISIBLE
                                                btn_QuitarRuta3!!.visibility = View.VISIBLE
                                                u.setRMF3(array_rutas_seleccionadas[2]!!)
                                            }
                                        }
                                        /*Si salió bien, se saldrá del ciclo después de haber agregado una ruta tanto
                                             * a la lógica de la app como a su parte visual*/
                                        //limpiamos el autocompletextview una vez agregada la ruta.
                                        actv_RMF!!.setText("")
                                        //le mostramos al usuario que gruta se ingreso
                                        Toast.makeText(
                                            activity,
                                            "Ruta $rutase Agregada, puedes agregar $rutasagregadas rutas mas.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        bandera = 1
                                        break
                                    }
                                }
                            }
                        }
                    }
                    //En caso de que no se haya agregado la ruta
                    if (bandera == 0) {
                        Toast.makeText(
                            activity,
                            "Porfavor agregue una ruta de la lista de rutas disponibles",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                //avsiar al usuario que alcanzo las unicas 3 ruats disponibles a agregar
                Toast.makeText(
                    activity,
                    "Ya has agregado el maximo de Rutas Predeterminadas",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    //Aqui se llama a el metodo para eliminar rutas predeterminadas por el usuario y juntar visualmente hacia la izquierda las etiquetas.
    private fun eliminarRuta() {
        btn_QuitarRuta1!!.setOnClickListener {
            array_rutas_seleccionadas[0] = null
            rutasagregadas++
            acomodar()
        }
        btn_QuitarRuta2!!.setOnClickListener {
            array_rutas_seleccionadas[1] = null
            rutasagregadas++
            acomodar()
        }
        btn_QuitarRuta3!!.setOnClickListener {
            array_rutas_seleccionadas[2] = null
            rutasagregadas++
            acomodar()
        }
    }

    //una vez eliminada la ruta juntar hacia la iquierda
    private fun acomodar() {

        //array que guardara las rutas para acomodarlas en el nuevo codigo
        val array_acomodar_rutas = arrayOfNulls<String>(3)
        for (contador in 0..2) {
            if (array_rutas_seleccionadas[contador] != null) {
                /*Inicia un ciclo for que busca el primer espacio en blanco en el array de
                 * de las rutas seleccionadas*/
                for (i3 in 0..2) {

                    //Si se encuentra un elemento del array que esté vacío...
                    if (array_acomodar_rutas[i3] == null) {
                        array_acomodar_rutas[i3] = array_rutas_seleccionadas[contador]
                        break
                    }
                }
            }
        }
        tv_ruta1!!.visibility = View.INVISIBLE
        btn_QuitarRuta1!!.visibility = View.INVISIBLE
        tv_ruta2!!.visibility = View.INVISIBLE
        btn_QuitarRuta2!!.visibility = View.INVISIBLE
        tv_ruta3!!.visibility = View.INVISIBLE
        btn_QuitarRuta3!!.visibility = View.INVISIBLE
        for (i3 in 0..2) {
            array_rutas_seleccionadas[i3] = array_acomodar_rutas[i3]
            if (i3 == 0 && array_rutas_seleccionadas[0] != null) {
                tv_ruta1!!.visibility = View.VISIBLE
                tv_ruta1!!.text = array_rutas_seleccionadas[i3]
                btn_QuitarRuta1!!.visibility = View.VISIBLE
                u.setRMF1(array_rutas_seleccionadas[0])
            }
            if (i3 == 1 && array_rutas_seleccionadas[1] != null) {
                tv_ruta2!!.visibility = View.VISIBLE
                tv_ruta2!!.text = array_rutas_seleccionadas[i3]
                btn_QuitarRuta2!!.visibility = View.VISIBLE
                u.setRMF2(array_rutas_seleccionadas[1]!!)
            }
            if (i3 == 2 && array_rutas_seleccionadas[2] != null) {
                tv_ruta3!!.visibility = View.VISIBLE
                tv_ruta3!!.text = array_rutas_seleccionadas[i3]
                btn_QuitarRuta3!!.visibility = View.VISIBLE
                u.setRMF3(array_rutas_seleccionadas[2]!!)
            }
        }
    }

    private fun validacion() {
        val RMF = array_rutas_seleccionadas[0]
        if (comprobacionNombre() == false) {
            Toast.makeText(
                activity,
                "Datos Erroneos, Verifique la casilla de Nombre",
                Toast.LENGTH_SHORT
            ).show()
        } else if (comprobacionApellido() == false) {
            Toast.makeText(
                activity,
                "Datos Erroneos, Verifique la casilla de Apellidos",
                Toast.LENGTH_SHORT
            ).show()
        } else if (comprobacionCorreo() == false) {
            Toast.makeText(
                activity,
                "Datos Erroneos, Verifique la casilla de Correo",
                Toast.LENGTH_SHORT
            ).show()
        } else if (comprobacionContraseña() == false) {
            Toast.makeText(
                activity,
                "Datos Erroneos, Verifique la casilla de Contraseña",
                Toast.LENGTH_SHORT
            ).show()
        } else if (comprobacionContraseñaConfirmacion() == false) {
            Toast.makeText(
                activity,
                "Datos Erroneos, Verifique la casilla de Confirmacion de Contraseña",
                Toast.LENGTH_SHORT
            ).show()
        } else if (comprobacionContactoConfianza() == false) {
            Toast.makeText(
                activity,
                "Datos Erroneos, Verifique la casilla de Contacto de Confianza",
                Toast.LENGTH_SHORT
            ).show()
        } else if (RMF == null) {
            actv_RMF!!.error = "Se requiere aregues una ruta predeterminada."
            Toast.makeText(
                activity,
                "Datos Erroneos, Verifique la casilla de Ruta Predeterminada",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            //si las comprobaciones salieron bien se ejecutara esta parte del codigo y se registrara el usuario, pero primero
            // se revisara que no se este repitiendo el usuario.
            registrarUsuario()
        }
    }

    private fun comprobacionNombre(): Boolean {
        val nombre = et_Nombre!!.text.toString().trim { it <= ' ' }
        var bandera = false
        if (nombre == "") {
            et_Nombre!!.error = "Dato Requerido"
        } else if (nombre.length > 20) {
            et_Nombre!!.error = "No puedes Poner mas de 20 caracteres."
        } else {
            u.setNombre(nombre)
            bandera = true
        }
        return bandera
    }

    private fun comprobacionApellido(): Boolean {
        val apellido = et_Apellido!!.text.toString().trim { it <= ' ' }
        var bandera = false
        if (apellido == "") {
            et_Apellido!!.error = "Dato Requerido"
        } else if (apellido.length > 35) {
            et_Apellido!!.error = "No puedes Poner mas de 35 caracteres."
        } else {
            u.setApellido(apellido)
            bandera = true
        }
        return bandera
    }

    private fun comprobacionCorreo(): Boolean {
        // El email a validar
        val correo = et_Correo!!.text.toString().trim { it <= ' ' }
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
            et_Correo!!.error = "Dato Requerido"
        } else if (mather.find() == false) {
            et_Correo!!.error = "Correo Invalido, Porfavor escribe un correo real."
        } else {
            u.setCorreo(correo)
            bandera = true
        }
        return bandera
    }

    private fun comprobacionContraseña(): Boolean {
        val contraseña = et_Contraseña!!.text.toString().trim { it <= ' ' }
        var bandera = false
        if ((contraseña == "")) {
            et_Contraseña!!.error = "Dato Requerido"
        } else {
            u.setContraseña(contraseña)
            bandera = true
        }
        return bandera
    }

    private fun comprobacionContraseñaConfirmacion(): Boolean {
        val contraseñaConfirmacion = et_ContraseñaConfirmacion!!.text.toString().trim { it <= ' ' }
        val contraseña = et_Contraseña!!.text.toString().trim { it <= ' ' }
        var bandera = false
        if ((contraseñaConfirmacion == "")) {
            et_ContraseñaConfirmacion!!.error = "Dato Requerido"
        } else if ((contraseñaConfirmacion == contraseña) == false) {
            et_ContraseñaConfirmacion!!.error = "Las contraseñas no coinciden."
        } else {
            bandera = true
        }
        return bandera
    }

    private fun comprobacionContactoConfianza(): Boolean {
        val ContactoConfianza = et_ContactoConfianza!!.text.toString().trim { it <= ' ' }
        // Patrón para validar el email
        val pattern = Pattern
            .compile(
                ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
            )
        //busca nuestro email en base a la expresion que selecu¡cionamos del pattern en caso de que sea correcta nos retorna un valor true
        val mather = pattern.matcher(ContactoConfianza)
        var bandera = false
        if ((ContactoConfianza == "")) {
            et_ContactoConfianza!!.error =
                "Dato Requerido, A este Contacto se le avisara cuando aborde su ruta"
        } else if (mather.find() == false) {
            et_ContactoConfianza!!.error = "Correo Invalido, Porfavor escribe un correo real."
        } else {
            u.setContactoConfianza(ContactoConfianza)
            bandera = true
        }
        return bandera
    }

    private fun registrarUsuario() {
        val correo = et_Correo!!.text.toString().trim { it <= ' ' }
        val contraseña = et_Contraseña!!.text.toString().trim { it <= ' ' }
        progressDialog!!.setMessage("Realizando registro en linea...")
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()

        //creating a new user
        mAuth!!.createUserWithEmailAndPassword(correo, contraseña)
            .addOnCompleteListener { task -> //checking if success
                if (task.isSuccessful) {
                    val id = mAuth!!.currentUser!!.uid
                    val datosUsuario: MutableMap<String, Any?> =
                        HashMap()
                    datosUsuario["nombre"] = u.getNombre()
                    datosUsuario["iduser"] = id
                    datosUsuario["apellido"] = u.getApellido()
                    datosUsuario["correo"] = u.getCorreo()
                    datosUsuario["contraseña"] = u.getContraseña()
                    datosUsuario["contactoConfianza"] = u.getContactoConfianza()
                    //Mandar datos del map string a la base de datos
                    OTBReference!!.child("Users").child(id).setValue(datosUsuario)
                        .addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                val id2 = mAuth!!.currentUser!!.uid
                                val RutasPredeterminadas: MutableMap<String, Any?> =
                                    HashMap()
                                for (i3 in 0..2) {
                                    if (i3 == 0 && u.getRMF1() != null) {
                                        RutasPredeterminadas["RMF1"] = u.getRMF1()
                                    }
                                    if (i3 == 1 && u.getRMF2() != null) {
                                        RutasPredeterminadas["RMF2"] = u.getRMF2()
                                    }
                                    if (i3 == 2 && u.getRMF3() != null) {
                                        RutasPredeterminadas["RMF3"] = u.getRMF3()
                                    }
                                }
                                OTBReference!!.child("Users").child(id2).child("RutaMasFrecuentada")
                                    .setValue(RutasPredeterminadas).addOnCompleteListener { task3 ->
                                        if (task3.isSuccessful) {
                                            val user = mAuth!!.currentUser
                                            user!!.sendEmailVerification()
                                            limpiarCaja()
                                            Toast.makeText(
                                                activity,
                                                ("Se registro el Correo:  " + u.getCorreo()).toString() + " Ve a tu email, a Verificarlo e Inicia Sesion.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            //se cierra la sesion para que el usuario se loguee
                                            mAuth!!.signOut()

                                            // Realizar la transacción de fragmento
                                            this.requireActivity().supportFragmentManager.commit {
                                                replace<Login>(R.id.fl_Container)
                                                setReorderingAllowed(true)
                                                addToBackStack("register_user")
                                            }
                                        } else {
                                            Toast.makeText(
                                                activity,
                                                "No se pudieron agregar las rutas predeterminadas",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "No se pudieron crear los datos correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    //si el usuario ya esta rgistrado
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        et_Correo!!.error = "Correo Ya registrado, ingresar uno diferente"
                        Toast.makeText(
                            activity,
                            "Error, Este correo ya esta registrado, favor de Registrarse con un nuevo correo, o iniciar sesion.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (task.exception is FirebaseAuthWeakPasswordException) {
                        et_Contraseña!!.error =
                            "Contrasña Insegura, Porfavor Incluya Almenos 8 caracteres, con 3 Numeros como minimo."
                        Toast.makeText(
                            activity,
                            "Tu contraseña es demasiado insegura porfavor asegurese de que incluya almenos 8 caracteres, con 3 numeros como minimoo.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            activity,
                            "Algo Salio mal, verifique su conexion a Internet",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                progressDialog!!.dismiss()
            }
    }

    private fun limpiarCaja() {
        et_Nombre!!.setText("")
        et_Apellido!!.setText("")
        et_Correo!!.setText("")
        et_Contraseña!!.setText("")
        et_ContraseñaConfirmacion!!.setText("")
        et_ContactoConfianza!!.setText("")
        actv_RMF!!.setText("")
    }

    //region OnClicks Buttons

    //endregion
}