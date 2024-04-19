package com.android.ontrackbus.Models

public class Rutas {

    private var NombreDeRuta: String? = null
    private var FrecuenciaPasada: Int? = null
    private var HoraFin: String? = null
    private var HoraInicio: String? = null
    private var Id_IA: Int? = null
    private var PromedioParadas: Int? = null


    //cercania de ruta
    private var latuser: Double? = null
    private var lnguser: Double? = null


    //elementos para marcadores
    private var latitud: Double? = null
    private var longitud: Double? = null
    private var tittle: String? = null
    private var snnipet: String? = null


    fun Rutas() {}

    fun getNombreDeRuta(): String? {
        return NombreDeRuta
    }

    fun setNombreDeRuta(nombreDeRuta: String?) {
        NombreDeRuta = nombreDeRuta
    }


    //elementos marcador

    //elementos marcador
    fun getLatitud(): Double? {
        return latitud
    }

    fun setLatitud(latitud: Double?) {
        this.latitud = latitud
    }

    fun getLongitud(): Double? {
        return longitud
    }

    fun setLongitud(longitud: Double?) {
        this.longitud = longitud
    }


    fun getTittle(): String? {
        return tittle
    }

    fun setTittle(tittle: String?) {
        this.tittle = tittle
    }

    fun getSnnipet(): String? {
        return snnipet
    }

    fun setSnnipet(snnipet: String?) {
        this.snnipet = snnipet
    }


    //elementos cercania usuario
    fun getLatuser(): Double? {
        return latuser
    }

    fun setLatuser(latuser: Double?) {
        this.latuser = latuser
    }

    fun getLnguser(): Double? {
        return lnguser
    }

    fun setLnguser(lnguser: Double?) {
        this.lnguser = lnguser
    }


    fun getFrecuenciaPasada(): Int? {
        return this.FrecuenciaPasada
    }

    fun setFrecuenciaPasada(frecuenciaPasada: Int?) {
        this.FrecuenciaPasada = frecuenciaPasada
    }

    fun getHoraFin(): String? {
        return this.HoraFin
    }

    fun setHoraFin(horaFin: String?) {
        this.HoraFin = horaFin
    }

    fun getHoraInicio(): String? {
        return this.HoraInicio
    }

    fun setHoraInicio(horaInicio: String?) {
        this.HoraInicio = horaInicio
    }

    fun getId_IA(): Int? {
        return this.Id_IA
    }

    fun setId_IA(id_IA: Int?) {
        this.Id_IA = id_IA
    }
    fun getPromedioParadas(): Int? {
        return this.PromedioParadas
    }

    fun setPromedioParadas(promedioParadas: Int?) {
        this.PromedioParadas = promedioParadas
    }
}