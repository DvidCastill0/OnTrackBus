package OnTrackBus.com;

import java.util.ArrayList;

public class Rutas {

    private String NombreDeRuta;


    //cercania de ruta
    private Double latuser;
    private Double lnguser;



    //elementos para marcadores
    private Double latitud;
    private Double longitud;
    private String tittle;
    private String snnipet;


    public Rutas(){

    }

    public String getNombreDeRuta() {
        return NombreDeRuta;
    }

    public void setNombreDeRuta(String nombreDeRuta) {
        NombreDeRuta = nombreDeRuta;
    }


    //elementos marcador

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }


    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getSnnipet() {
        return snnipet;
    }

    public void setSnnipet(String snnipet) {
        this.snnipet = snnipet;
    }


    //elementos cercania usuario
    public Double getLatuser() {
        return latuser;
    }

    public void setLatuser(Double latuser) {
        this.latuser = latuser;
    }

    public Double getLnguser() {
        return lnguser;
    }

    public void setLnguser(Double lnguser) {
        this.lnguser = lnguser;
    }
}
