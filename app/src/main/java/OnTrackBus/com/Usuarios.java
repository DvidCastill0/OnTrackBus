package OnTrackBus.com;

public class Usuarios {

    private String Nombre;
    private String Apellido;
    private String Correo;
    private String Contraseña;
    private String ContactoConfianza;
    private String RMF1=null,RMF2=null,RMF3=null;
    private String iduser;



    public Usuarios(){

    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public String getContraseña() {
        return Contraseña;
    }

    public void setContraseña(String contraseña) {
        Contraseña = contraseña;
    }


    public String getContactoConfianza() {
        return ContactoConfianza;
    }

    public void setContactoConfianza(String contactoConfianza) {
        ContactoConfianza = contactoConfianza;
    }


    public String getRMF1() {
        return RMF1;
    }

    public void setRMF1(String RMF1) {
        this.RMF1 = RMF1;
    }

    public String getRMF2() {
        return RMF2;
    }

    public void setRMF2(String RMF2) {
        this.RMF2 = RMF2;
    }

    public String getRMF3() {
        return RMF3;
    }

    public void setRMF3(String RMF3) {
        this.RMF3 = RMF3;
    }


    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

}
