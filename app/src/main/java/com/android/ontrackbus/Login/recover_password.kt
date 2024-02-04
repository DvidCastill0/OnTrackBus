package com.android.ontrackbus.Login

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.android.ontrackbus.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import java.util.regex.Pattern

class recover_password : Fragment() {
    private var btn_Volver: Button? = null
    private var edt_RecuperarContrasena: EditText? = null
    private var btn_RecuperarContrasena: Button? = null

    private var mAuth: FirebaseAuth? = null

    //barra de progreso mientras se envia el correo
    private var mDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val vw_recover = inflater.inflate(R.layout.fragment_recover_password, container, false)

        //instancera mauth para usar funciones de autenticacion.

        //instancera mauth para usar funciones de autenticacion.
        mAuth = FirebaseAuth.getInstance()
        mDialog = ProgressDialog(context)

        edt_RecuperarContrasena =
            vw_recover.findViewById<EditText>(R.id.edtCorreoRestablecerContrasena)

        btn_RecuperarContrasena = vw_recover.findViewById<Button>(R.id.btn_Recuperar_Contrasena)
        btn_RecuperarContrasena!!.setOnClickListener(View.OnClickListener {
            if (comprobacionCorreo()) {
                mDialog!!.setMessage("Enviando Correo...")
                mDialog!!.setCanceledOnTouchOutside(false)
                mDialog!!.show()
                mAuth!!.setLanguageCode("es")
                mAuth!!.sendPasswordResetEmail(
                    edt_RecuperarContrasena!!.getText().toString().trim { it <= ' ' })
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            edt_RecuperarContrasena!!.setText("")
                            Toast.makeText(
                                activity,
                                "Se ha enviado un correo para Reestablecer tu contraseña a: " + edt_RecuperarContrasena!!.getText()
                                    .toString().trim { it <= ' ' },
                                Toast.LENGTH_SHORT
                            ).show()

                                this.requireActivity().supportFragmentManager.commit {
                                    replace<Login>(R.id.fl_Container)
                                    setReorderingAllowed(true)
                                    addToBackStack("Login")
                                }
                        } else {
                            if (task.exception is FirebaseAuthInvalidUserException) {
                                edt_RecuperarContrasena!!.setError("Correo No Registrado")
                                Toast.makeText(
                                    activity,
                                    "Este Correo no esta Asociado a ninguna cuenta, Por favor Registrate",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Algo Salio Mal porfavor reportalo.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        mDialog!!.dismiss()
                    }
            }
        })


        btn_Volver = vw_recover.findViewById<View>(R.id.btn_regresaraloginRC) as Button?
        btn_Volver!!.setOnClickListener(View.OnClickListener {
                this.parentFragmentManager.popBackStack()
        })

        return vw_recover
    }

    private fun comprobacionCorreo(): Boolean {
        // El email a validar
        val correo = edt_RecuperarContrasena!!.text.toString().trim { it <= ' ' }
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
            edt_RecuperarContrasena!!.error = "Dato Requerido"
        } else if (mather.find() == false) {
            edt_RecuperarContrasena!!.error = "Correo Invalido, Porfavor escribe un correo real."
        } else {
            bandera = true
        }
        return bandera
    }
}