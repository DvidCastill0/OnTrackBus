package com.android.ontrackbus.Models

class Usuarios {

    private var Nombre: String? = null
    private var Apellido: String? = null
    private var Correo: String? = null
    private var Contraseña: String? = null
    private var ContactoConfianza: String? = null
    private var RMF1: String? = null
    private var RMF2: String? = null
    private var RMF3: String? = null
    private var iduser: String? = null

    fun Usuarios() {}

    fun getNombre(): String? {
        return Nombre
    }

    fun setNombre(nombre: String?) {
        Nombre = nombre
    }

    fun getApellido(): String? {
        return Apellido
    }

    fun setApellido(apellido: String?) {
        Apellido = apellido
    }

    fun getCorreo(): String? {
        return Correo
    }

    fun setCorreo(correo: String?) {
        Correo = correo
    }

    fun getContraseña(): String? {
        return Contraseña
    }

    fun setContraseña(contraseña: String?) {
        Contraseña = contraseña
    }


    fun getContactoConfianza(): String? {
        return ContactoConfianza
    }

    fun setContactoConfianza(contactoConfianza: String?) {
        ContactoConfianza = contactoConfianza
    }


    fun getRMF1(): String? {
        return RMF1
    }

    fun setRMF1(RMF1: String?) {
        this.RMF1 = RMF1
    }

    fun getRMF2(): String? {
        return RMF2
    }

    fun setRMF2(RMF2: String) {
        this.RMF2 = RMF2
    }

    fun getRMF3(): String? {
        return RMF3
    }

    fun setRMF3(RMF3: String) {
        this.RMF3 = RMF3
    }


    fun getIduser(): String? {
        return iduser
    }

    fun setIduser(iduser: String?) {
        this.iduser = iduser
    }

}