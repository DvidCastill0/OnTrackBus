package com.android.ontrackbus.User_Menu.ui.profile

import android.app.ProgressDialog
import android.os.Bundle
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.android.ontrackbus.Login.Login
import com.android.ontrackbus.Models.Usuarios
import com.android.ontrackbus.R
import com.android.ontrackbus.User_Menu.MenuViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.regex.Pattern
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProfileFragment : Fragment() {


    //instancear base de datos y autenticacion
    private var OTBReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null

    //variables de xml
    private var edt_ActualizarNombre: EditText? = null
    private var edt_ActualizarApellido: EditText? = null
    private var edt_ActualizarContactoConfianza: EditText? = null

    private var actv_ActualizarRMF: AutoCompleteTextView? = null


    //getter and setter para obtener informacion en el programa
    private val userData: Usuarios = Usuarios()

    private  var vistaMiPerfil: View? = null


    //Declaracion de variables  componentes de interfaz
    private var btn_ActualizarContrasena: Button? = null
    private var btn_Actualizar: Button? = null
    private var btn_QuitarRuta1: Button? = null
    private var btn_QuitarRuta2: Button? = null
    private var btn_QuitarRuta3: Button? = null

    private var tv_ruta1: TextView? = null
    private var tv_ruta2: TextView? = null
    private var tv_ruta3: TextView? = null


    //arraylist para autocompletetextview
    private var arrl_Rutas = ArrayList<String>()

    //array que guardara las rutas que el usuario decida guardar
    private val array_rutas_seleccionadas = arrayOfNulls<String>(3)

    //contador para descargar rutas y para revisar las rutas que aun quedan disponibles
    private var i = 0

    //contador para descargar rutas y para revisar las rutas que aun quedan disponibles
    private var rutasagregadas = 3

    //instanciamos una barra de profreso mientras se registra el usuario
    private var progressDialog: ProgressDialog? = null

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel

    private var loginBundleSaved: MenuViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBundleSaved = ViewModelProvider(requireActivity())[MenuViewModel::class.java]
        var loginBundle = loginBundleSaved!!.loginBundle

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        vistaMiPerfil = inflater.inflate(R.layout.fragment_profile, container, false)
        //instanear variables de firebase

        //instanear variables de firebase
        OTBReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()


        //Instanceamos los elementos de xml con los de java
        edt_ActualizarNombre = vistaMiPerfil!!.findViewById(R.id.edt_ActualizarNombre)
        edt_ActualizarApellido = vistaMiPerfil!!.findViewById(R.id.edt_ActualizzarApellido)
        edt_ActualizarContactoConfianza = vistaMiPerfil!!.findViewById(R.id.edt_ActualizarContactoConfianza)
        actv_ActualizarRMF = vistaMiPerfil!!.findViewById<AutoCompleteTextView>(R.id.actv_ActualizarRutasPredeterminadas)
        btn_ActualizarContrasena = vistaMiPerfil!!.findViewById(R.id.btn_ActualizarContrasena)
        btn_Actualizar = vistaMiPerfil!!.findViewById(R.id.btn_Actualizar)

        lifecycleScope.launch {
            try {
                arrl_Rutas = obtenerNombresDeRutas()
            } catch (e: Exception) {
                // Manejar excepciones si es necesario
            }
        }

        //array adapter para autocompletetextview
        //array adapter para autocompletetextview
        val adapterrutas = ArrayAdapter<String>(
            requireActivity(),
            R.layout.support_simple_spinner_dropdown_item,
            arrl_Rutas
        )
        actv_ActualizarRMF!!.setAdapter(adapterrutas)

        tv_ruta1 = vistaMiPerfil!!.findViewById(R.id.tvRutaActualizar1)
        tv_ruta1!!.setVisibility(View.INVISIBLE)
        tv_ruta2 = vistaMiPerfil!!.findViewById(R.id.tvRutaActualizar2)
        tv_ruta2!!.setVisibility(View.INVISIBLE)
        tv_ruta3 = vistaMiPerfil!!.findViewById(R.id.tvRutaActualizar3)
        tv_ruta3!!.setVisibility(View.INVISIBLE)

        btn_QuitarRuta1 = vistaMiPerfil!!.findViewById(R.id.btn_eliminar_ruta_Actualizar1)
        btn_QuitarRuta1!!.setVisibility(View.INVISIBLE)
        btn_QuitarRuta2 = vistaMiPerfil!!.findViewById(R.id.btn_eliminar_ruta_Actualizar2)
        btn_QuitarRuta2!!.setVisibility(View.INVISIBLE)
        btn_QuitarRuta3 = vistaMiPerfil!!.findViewById(R.id.btn_eliminar_ruta_Actualizar3)
        btn_QuitarRuta3!!.setVisibility(View.INVISIBLE)

        progressDialog = ProgressDialog(activity)

        //metodos de funcionalidad de los botones

        //metodos de funcionalidad de los botones
        eliminarRuta()
        agregarRuta()



        return vistaMiPerfil
    }

    suspend fun obtenerNombresDeRutas(): ArrayList<String> {
        var nombresDeRutas = ArrayList<String>()

        try {
            var snapshot = OTBReference!!.child("Rutas").get().await()

            for (rutaSnapshot in snapshot.children) {
                var nombreRuta = rutaSnapshot.child("NombreDeRuta").getValue(String::class.java)
                if (nombreRuta != null) {
                    nombresDeRutas.add(nombreRuta)
                }
            }
        } catch (e: Exception) {
            // Manejar excepciones si es necesario
        }

        return nombresDeRutas
    }
    //metodo donde se agregan rutas seleccionada spor le usuario.
    private fun agregarRuta() {
        var btn_AgregarRuta = vistaMiPerfil!!.findViewById(R.id.btn_AgregarRutaActualizacion) as Button
        btn_AgregarRuta.setOnClickListener(View.OnClickListener {
            //bandera para saber si la ruta escrita esta dentrod e als rutas disponibles, para ver que no se repita una ruta
            var bandera = 0
            var banderaRutaRepetida = 0
            //variable que obtiene el valor del autocompletetextview
            val rutase: String = actv_ActualizarRMF!!.getText().toString().trim()

            //se revisa que no se hayan agregado mas de 3 rutas
            if (rutasagregadas !== 0) {
                //Se revisa que no sea una caja vacia
                if ((rutase == "")) {
                    Toast.makeText(
                        activity,
                        "Porfavor Ingresa un numero de ruta",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    for (contador in 0 until i) {
                        //if que revisa la ruta pedida por el usuario entre las rutas disponibles a ver
                        if ((rutase == arrl_Rutas.get(contador))) {
                            //bucle que busca que nos e este repitiendo ninguna ruta
                            for (contadorRutaRepetida in 0..2) {
                                //se revisa que el valor ingresado no haya sido repetido
                                if ((rutase == array_rutas_seleccionadas.get(contadorRutaRepetida))) {
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
                                    if (array_rutas_seleccionadas.get(contadorarray) == null) {

                                        //... se establece su valor en un string de la ruta seleccionada en el spinner
                                        array_rutas_seleccionadas[contadorarray] = rutase
                                        rutasagregadas--
                                        when (contadorarray) {
                                            0 -> {
                                                tv_ruta1!!.setText(rutase)
                                                tv_ruta1!!.setVisibility(View.VISIBLE)
                                                btn_QuitarRuta1!!.setVisibility(View.VISIBLE)
                                                userData.setRMF1(array_rutas_seleccionadas.get(0))
                                            }

                                            1 -> {
                                                tv_ruta2!!.setText(rutase)
                                                tv_ruta2!!.setVisibility(View.VISIBLE)
                                                btn_QuitarRuta2!!.setVisibility(View.VISIBLE)
                                                userData.setRMF2(array_rutas_seleccionadas.get(1).toString())
                                            }

                                            2 -> {
                                                tv_ruta3!!.setText(rutase)
                                                tv_ruta3!!.setVisibility(View.VISIBLE)
                                                btn_QuitarRuta3!!.setVisibility(View.VISIBLE)
                                                userData.setRMF3(array_rutas_seleccionadas.get(2).toString())
                                            }
                                        }
                                        /*Si salió bien, se saldrá del ciclo después de haber agregado una ruta tanto
                                             * a la lógica de la app como a su parte visual*/
                                        //limpiamos el autocompletextview una vez agregada la ruta.
                                        actv_ActualizarRMF!!.setText("")
                                        //le mostramos al usuario que gruta se ingreso
                                        Toast.makeText(
                                            activity,
                                            ("Ruta $rutase Agregada, puedes agregar $rutasagregadas").toString() + " rutas mas.",
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
        btn_QuitarRuta1!!.setOnClickListener(View.OnClickListener {
            array_rutas_seleccionadas[0] = null
            rutasagregadas++
            acomodar()
        })
        btn_QuitarRuta2!!.setOnClickListener(View.OnClickListener {
            array_rutas_seleccionadas[1] = null
            rutasagregadas++
            acomodar()
        })
        btn_QuitarRuta3!!.setOnClickListener(View.OnClickListener {
            array_rutas_seleccionadas[2] = null
            rutasagregadas++
            acomodar()
        })
    }

    //una vez eliminada la ruta juntar hacia la iquierda
    private fun acomodar() {

        //array que guardara las rutas para acomodarlas en el nuevo codigo
        val array_acomodar_rutas = arrayOfNulls<String>(3)
        for (contador in 0..2) {
            if (array_rutas_seleccionadas.get(contador) != null) {
                /*Inicia un ciclo for que busca el primer espacio en blanco en el array de
                 * de las rutas seleccionadas*/
                for (i3 in 0..2) {

                    //Si se encuentra un elemento del array que esté vacío...
                    if (array_acomodar_rutas[i3] == null) {
                        array_acomodar_rutas[i3] = array_rutas_seleccionadas.get(contador)
                        break
                    }
                }
            }
        }
        tv_ruta1?.setVisibility(View.INVISIBLE)
        btn_QuitarRuta1?.setVisibility(View.INVISIBLE)
        tv_ruta2?.setVisibility(View.INVISIBLE)
        btn_QuitarRuta2?.setVisibility(View.INVISIBLE)
        tv_ruta3?.setVisibility(View.INVISIBLE)
        btn_QuitarRuta3?.setVisibility(View.INVISIBLE)
        for (i3 in 0..2) {
            array_rutas_seleccionadas[i3] = array_acomodar_rutas[i3]
            if (i3 == 0 && array_rutas_seleccionadas.get(0) != null) {
                tv_ruta1!!.setVisibility(View.VISIBLE)
                tv_ruta1!!.setText(array_rutas_seleccionadas.get(i3))
                btn_QuitarRuta1!!.setVisibility(View.VISIBLE)
                userData.setRMF1(array_rutas_seleccionadas.get(0))
            }
            if (i3 == 1 && array_rutas_seleccionadas.get(1) != null) {
                tv_ruta2!!.setVisibility(View.VISIBLE)
                tv_ruta2!!.setText(array_rutas_seleccionadas.get(i3))
                btn_QuitarRuta2!!.setVisibility(View.VISIBLE)
                userData.setRMF2(array_rutas_seleccionadas!!.get(1).toString())
            }
            if (i3 == 2 && array_rutas_seleccionadas.get(2) != null) {
                tv_ruta3!!.setVisibility(View.VISIBLE)
                tv_ruta3!!.setText(array_rutas_seleccionadas.get(i3))
                btn_QuitarRuta3!!.setVisibility(View.VISIBLE)
                userData.setRMF3(array_rutas_seleccionadas.get(2)!!)
            }
        }
    }


    private fun validacion() {
        val RMF: String = array_rutas_seleccionadas.get(0).toString()
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
        } else if (comprobacionContactoConfianza() == false) {
            Toast.makeText(
                activity,
                "Datos Erroneos, Verifique la casilla de Contacto de Confianza",
                Toast.LENGTH_SHORT
            ).show()
        } else if (RMF == null) {
            actv_ActualizarRMF?.setError("Se requiere aregues una ruta predeterminada.")
            Toast.makeText(
                activity,
                "Datos Erroneos, Verifique la casilla de Ruta Predeterminada",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            //si las comprobaciones salieron bien se ejecutara esta parte del codigo y se registrara el usuario, pero primero
            // se revisara que no se este repitiendo el usuario.
            actualizarUsuario()
        }
    }

    private fun actualizarUsuario() {
        progressDialog!!.setMessage("Realizando actualización en línea...")
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()

        // Obtener el ID del usuario actualmente autenticado
        val id = mAuth!!.currentUser!!.uid

        // Crear un mapa con los campos que deseas actualizar
        val datosUsuario: MutableMap<String, Any?> = HashMap()
        datosUsuario["nombre"] = userData.getNombre()
        datosUsuario["apellido"] = userData.getApellido()
        datosUsuario["contactoConfianza"] = userData.getContactoConfianza()

        // Actualizar los datos del usuario en Firebase Realtime Database
        OTBReference!!.child("Users").child(id).updateChildren(datosUsuario)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Actualización exitosa
                    Toast.makeText(
                        activity,
                        "Datos del usuario actualizados correctamente.",
                        Toast.LENGTH_SHORT
                    ).show()
                    limpiarCaja() // Limpia los campos de entrada si es necesario

                    // Puedes agregar aquí más lógica según sea necesario

                } else {
                    // Manejar errores en la actualización
                    Toast.makeText(
                        activity,
                        "Error al actualizar los datos del usuario.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                progressDialog!!.dismiss()
            }
    }


    private fun limpiarCaja() {
        edt_ActualizarNombre!!.setText("")
        edt_ActualizarApellido!!.setText("")
        edt_ActualizarContactoConfianza!!.setText("")
        actv_ActualizarRMF!!.setText("")
    }

    private fun comprobacionNombre(): Boolean {
        val nombre = edt_ActualizarNombre!!.text.toString().trim { it <= ' ' }
        var bandera = false
        if (nombre == "") {
            edt_ActualizarNombre!!.error = "Dato Requerido"
        } else if (nombre.length > 20) {
            edt_ActualizarNombre!!.error = "No puedes Poner mas de 20 caracteres."
        } else {
            userData.setNombre(nombre)
            bandera = true
        }
        return bandera
    }

    private fun comprobacionApellido(): Boolean {
        val apellido = edt_ActualizarApellido!!.text.toString().trim { it <= ' ' }
        var bandera = false
        if (apellido == "") {
            edt_ActualizarApellido!!.error = "Dato Requerido"
        } else if (apellido.length > 35) {
            edt_ActualizarApellido!!.error = "No puedes Poner mas de 35 caracteres."
        } else {
            userData.setApellido(apellido)
            bandera = true
        }
        return bandera
    }


    private fun comprobacionContactoConfianza(): Boolean {
        val ContactoConfianza = edt_ActualizarContactoConfianza!!.text.toString().trim { it <= ' ' }
        // Patrón para validar el email
        val pattern = Pattern
            .compile(
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            )
        //busca nuestro email en base a la expresion que selecu¡cionamos del pattern en caso de que sea correcta nos retorna un valor true
        val mather = pattern.matcher(ContactoConfianza)
        var bandera = false
        if ((ContactoConfianza == "")) {
            edt_ActualizarContactoConfianza!!.error =
                "Dato Requerido, A este Contacto se le avisara cuando aborde su ruta"
        } else if (mather.find() == false) {
            edt_ActualizarContactoConfianza!!.error =
                "Correo Invalido, Porfavor escribe un correo real."
        } else {
            userData.setContactoConfianza(ContactoConfianza)
            bandera = true
        }
        return bandera
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

}