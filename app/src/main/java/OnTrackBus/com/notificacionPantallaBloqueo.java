package OnTrackBus.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class notificacionPantallaBloqueo extends AppCompatActivity {

    //clase para loguear usuarios
    private FirebaseAuth mAuth;

    //variables de botones
    private Button btn_abordadosi,btn_abordadono,btn_OtraRutaAbordado;

    //variable para el autocomplete textview
    private AutoCompleteTextView actv_OtraRutaAbordado;

    //variables para actv
    private int CantidadDeRutas;
    //arraylist para la parte voy en cmaino
    private ArrayList<String> alAbordado = new ArrayList<>();
    //variable que recibe la ruta que esta reviamente seleccionada
    private String rutaSeleccionadaAbordado,rutaSeleccionadaNumero;

    //text view abordado
    private TextView tv_Abordado;

    //variables de firebase:
    private DatabaseReference OTBReference;

    //variable para obtener la ultima localizacion conocida
    private FusedLocationProviderClient userFLPC;
    //variables de latitud y longitud de la ultima localizacion del usuario
    private Double latuser = 0.0;
    private Double lnguser = 0.0;

    //instanciar la clase rutas para poder mandar elnumero de la ruta en base al nombre
    private Rutas oRutasAbordado = new Rutas();
    private Usuarios oUsuariossDatos = new Usuarios();

    private String orientacionRuta,contactoConfianza,nombreParada,userid, numeroParada, rutaAbordada,rutaAbordadaNumero;

    //bandera para ver si el correo de confianza existe
    private int banderaCorreoConfianzaExistente=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion_pantalla_bloqueo);

        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);
        int ancho = medidasVentana.widthPixels;
        int alto = medidasVentana.heightPixels;
        getWindow().setLayout((int)(ancho + 0.85),(int)(alto * 0.3));

        // Obtienes el Bundle del Intent pasado
        Bundle bundle = getIntent().getExtras();
        CantidadDeRutas = bundle.getInt("CantidadDeRutas");
        for (int contadorAbordado=1;contadorAbordado<CantidadDeRutas;contadorAbordado++){
            alAbordado.add(bundle.getString("Ruta"+contadorAbordado));
        }

        //descargar orientacion de rut
        orientacionRuta = bundle.getString("orientacion");

        contactoConfianza = bundle.getString("contactoConfianza");
        //se referencia la clase auth
        mAuth = FirebaseAuth.getInstance();

        //se descarga la ruta seleccionada y se manda la pregunta
        rutaSeleccionadaAbordado = bundle.getString("rutaSeleccionadaAbordado");
        rutaSeleccionadaNumero = bundle.getString("rutaSeleccionadaNumero");
        tv_Abordado = this.findViewById(R.id.tv_Abordado);
        tv_Abordado.setText("¿Ya abordaste la Ruta "+rutaSeleccionadaAbordado+"?");

        //instanciamos los botones
        btn_abordadosi = this.findViewById(R.id.btn_abordadoSi);
        btn_abordadono = this.findViewById(R.id.btn_abordadoNo);
        btn_OtraRutaAbordado = this.findViewById(R.id.btn_OtraRutaAbordado);

        //instanciar actv
        actv_OtraRutaAbordado = this.findViewById(R.id.actv_OtraRutaAbordado);
        ArrayAdapter<String> adapterrutasAbordado = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,alAbordado);
        actv_OtraRutaAbordado.setAdapter(adapterrutasAbordado);

        //instancear base de datos
        OTBReference = FirebaseDatabase.getInstance().getReference();

        //funcionalidad de los botones
        funcion_btnAbordadoSI();
        funcion_btnAbordadoNo();
        funcion_btnOtraRutaAbordado();
    }

    //funcionalidad boton si
    private void funcion_btnAbordadoSI(){


                //se obtiene de la base de datos el nombre de las rutas que estan disponibles a ver en el fragmento y se compara con la que selecciono el usuario para ver si existe.
                OTBReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        btn_abordadosi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            //variable para buscar la uktima ubicacion del usuario
                            userFLPC = LocationServices.getFusedLocationProviderClient(notificacionPantallaBloqueo.this);
                            userFLPC.getLastLocation()
                                    .addOnSuccessListener(notificacionPantallaBloqueo.this, new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            // Got last known location. In some rare situations this can be null.
                                                if (location != null) {
                                                    latuser = location.getLatitude();
                                                    lnguser = location.getLongitude();

                                                    //contador para paradas
                                                    int contadorParadas = 1;

                                                    Double latuserresult = 0.0, lnguserresult = 0.0, latuserresult2 = 0.0, lnguserresult2 = 0.0, suma1 = 0.0, suma2 = 0.0;
                                                    oRutasAbordado.setLatuser(0.0);
                                                    oRutasAbordado.setLnguser(0.0);
                                                    for (DataSnapshot snapshot2 : dataSnapshot.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).getChildren()) {
                                                        // se cargan marcadores nuevos

                                                        String latitud1 = dataSnapshot.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).child("Parada" + contadorParadas).child("latitud").getValue().toString();
                                                        String longitud1 = dataSnapshot.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).child("Parada" + contadorParadas).child("longitud").getValue().toString();

                                                        Double latitud = Double.parseDouble(latitud1);
                                                        Double longitud = Double.parseDouble(longitud1);

                                                        latuserresult = (latuser) - (oRutasAbordado.getLatuser());
                                                        lnguserresult = (lnguser) - (oRutasAbordado.getLnguser());


                                                        suma1 = (Math.abs(latuserresult)) + (Math.abs(lnguserresult));

                                                        latuserresult2 = (latuser) - (latitud);
                                                        lnguserresult2 = (lnguser) - (longitud);

                                                        suma2 = (Math.abs(latuserresult2)) + (Math.abs(lnguserresult2));

                                                        if (latuser == latitud) {
                                                            suma2 = suma2 * 2;
                                                        }
                                                        if (lnguser == longitud) {
                                                            suma2 = suma2 * 2;
                                                        }

                                                        if (suma2 < suma1) {
                                                            oRutasAbordado.setLatuser(latitud);
                                                            oRutasAbordado.setLnguser(longitud);

                                                            nombreParada = dataSnapshot.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).child("Parada" + contadorParadas).child("tittle").getValue().toString();
                                                            numeroParada = "Parada"+contadorParadas;
                                                        }
                                                        contadorParadas++;
                                                    }

                                                    //se obtiene el id del usuario al que se le mandaaran los datos
                                                    for (DataSnapshot snapshot3 : dataSnapshot.child("Users").getChildren()) {
                                                        oUsuariossDatos = snapshot3.getValue(Usuarios.class);

                                                        String correoContactoConfianza = oUsuariossDatos.getCorreo();
                                                        if (contactoConfianza.equals(correoContactoConfianza)){
                                                             userid = oUsuariossDatos.getIduser();
                                                             banderaCorreoConfianzaExistente=1;
                                                        }


                                                    }
                                                    String nombreCurrentUser = dataSnapshot.child("Users").child(mAuth.getCurrentUser().getUid()).child("nombre").getValue().toString();
                                                    String correoCurrentUser = dataSnapshot.child("Users").child(mAuth.getCurrentUser().getUid()).child("correo").getValue().toString();
                                                    //objeto que busca el formato de la fecha a obtener
                                                    SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                                                            " d-MM-yyyy");

                                                    //esto significa  a jueves 7 de mayo de 2020. "'a' EEEE d 'de' MMMM 'de' yyyy
                                                    SimpleDateFormat formateadorhora = new SimpleDateFormat(
                                                            "HH:mm:ss");
                                                    //se obtiene la fecha y hora actual
                                                    Date fechaDate = new Date();
                                                    String fecha = formateadorfecha.format(fechaDate);
                                                    String Hora = formateadorhora.format(fechaDate);



                                                    //funcion para saber si el usuario esta cerca de 200 metros de la parada d elo contrario no podra indicar que esta arriba
                                                    Double latminimo,latmaximo,lngminimo,lngmaximo;
                                                    latminimo=oRutasAbordado.getLatuser()-0.002;
                                                    latmaximo=oRutasAbordado.getLatuser()+0.002;
                                                    lngminimo=oRutasAbordado.getLnguser()-0.002;
                                                    lngmaximo=oRutasAbordado.getLnguser()+0.002;

                                                    if (latuser<=latmaximo&&latuser>=latminimo&&lnguser<=lngmaximo&&lnguser>=lngminimo){

                                                        if (banderaCorreoConfianzaExistente==1) {

                                                        for (int contadorReportes=1; contadorReportes<11;contadorReportes++){





                                                                if (dataSnapshot.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte" + contadorReportes).exists() != true) {
                                                                    Map<String, Object> ReporteContactoConfianza = new HashMap<>();
                                                                    ReporteContactoConfianza.put("CorreoRemitente", correoCurrentUser);
                                                                    ReporteContactoConfianza.put("NombreRemitente", nombreCurrentUser);
                                                                    ReporteContactoConfianza.put("FechaAbordado", fecha);
                                                                    ReporteContactoConfianza.put("HoraAbordado", Hora);
                                                                    ReporteContactoConfianza.put("ParadaAbordada", nombreParada);
                                                                    ReporteContactoConfianza.put("RutaAbordada", rutaSeleccionadaAbordado);
                                                                    ReporteContactoConfianza.put("Id_Reporte", contadorReportes);
                                                                    //Mandar datos del map string a la base de datos
                                                                    OTBReference.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte" + contadorReportes).setValue(ReporteContactoConfianza).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Toast.makeText(getApplicationContext(), "Gracias", Toast.LENGTH_LONG).show();

                                                                        }
                                                                    });

                                                                    OTBReference.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).child(numeroParada).child("snnipet").setValue(Hora + " " + fecha);

                                                                    //Enviar info a mi Actividad
                                                                    funcion_EnviarInfoMiActiviad(fecha, Hora, nombreParada, rutaSeleccionadaAbordado);


                                                                    finish();
                                                                    break;
                                                                } else {
                                                                    if (contadorReportes == 10) {
                                                                        for (int contadorReportesViejos = 2; contadorReportesViejos < 11; contadorReportesViejos++) {
                                                                            //String reporteviejo = dataSnapshot.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte"+contadorReportesViejos).child("FechaAbordado").getValue().toString();
                                                                            String fechaAbordado = dataSnapshot.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte" + contadorReportesViejos).child("FechaAbordado").getValue().toString();
                                                                            String horaAbordado = dataSnapshot.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte" + contadorReportesViejos).child("HoraAbordado").getValue().toString();
                                                                            String fechahora = fechaAbordado + horaAbordado;

                                                                            int contadormenosuno = contadorReportesViejos - 1;
                                                                            String fechavieja = dataSnapshot.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte" + contadormenosuno).child("FechaAbordado").getValue().toString();
                                                                            String horavieja = dataSnapshot.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte" + contadormenosuno).child("HoraAbordado").getValue().toString();
                                                                            String fechahoravieja = fechavieja + horavieja;
                                                                            if (fechahora.compareTo(fechahoravieja) > 0) {
                                                                                Map<String, Object> ReporteContactoConfianza = new HashMap<>();
                                                                                ReporteContactoConfianza.put("CorreoRemitente", correoCurrentUser);
                                                                                ReporteContactoConfianza.put("NombreRemitente", nombreCurrentUser);
                                                                                ReporteContactoConfianza.put("FechaAbordado", fecha);
                                                                                ReporteContactoConfianza.put("HoraAbordado", Hora);
                                                                                ReporteContactoConfianza.put("ParadaAbordada", nombreParada);
                                                                                ReporteContactoConfianza.put("RutaAbordada", rutaSeleccionadaAbordado);
                                                                                ReporteContactoConfianza.put("Id_Reporte", contadormenosuno);
                                                                                //Mandar datos del map string a la base de datos
                                                                                OTBReference.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte" + contadormenosuno).setValue(ReporteContactoConfianza).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        Toast.makeText(getApplicationContext(), "Gracias", Toast.LENGTH_LONG).show();

                                                                                    }
                                                                                });

                                                                                OTBReference.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).child(numeroParada).child("snnipet").setValue(Hora + " " + fecha);

                                                                                //Enviar info a mi Actividad
                                                                                funcion_EnviarInfoMiActiviad(fecha, Hora, nombreParada, rutaSeleccionadaAbordado);


                                                                                finish();
                                                                                break;
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                              }
                                                            }else{
                                                                //no hacer nada si no existe el correo e contacto
                                                                Toast.makeText(getApplicationContext(),"Tu contacto de confianza no tiene una ceunta en OnTrackBus",Toast.LENGTH_LONG).show();
                                                                //aqui acaba el verificar si el correo existe
                                                                OTBReference.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).child(numeroParada).child("snnipet").setValue(Hora + " " + fecha);
                                                                //Enviar info a mi Actividad
                                                                funcion_EnviarInfoMiActiviad(fecha, Hora, nombreParada, rutaSeleccionadaAbordado);
                                                                finish();
                                                            }



                                                    }else{
                                                        Toast.makeText(getApplicationContext(),"Estas demasiado lejos de una parada, porfavor espera acercarte a almenos 200 metros para presionar este boton.",Toast.LENGTH_LONG).show();
                                                    }


                                                }
                                            }


                                    });
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





    }

    private void funcion_btnAbordadoNo(){
        btn_abordadono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Porfavor selecciona la Ruta que abordaste",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void funcion_btnOtraRutaAbordado(){
                    //se obtiene de la base de datos el nombre de las rutas que estan disponibles a ver en el fragmento y se compara con la que selecciono el usuario para ver si existe.
                    OTBReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                            btn_OtraRutaAbordado.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rutaAbordada = actv_OtraRutaAbordado.getText().toString();



                                    int CantidadDeRutasVerificacion=CantidadDeRutas-1;
                                    int bandera=0;
                                    for (int contadorverificacion=0;contadorverificacion<CantidadDeRutasVerificacion;contadorverificacion++){
                                        if (rutaAbordada.equals(alAbordado.get(contadorverificacion))){
                                            int contadorxd = contadorverificacion+1;
                                            rutaSeleccionadaNumero="Ruta"+contadorxd;
                                            rutaSeleccionadaAbordado = rutaAbordada;
                                            bandera=1;
                                        }
                                    }


                                    if (bandera==1){
                                        //variable para buscar la uktima ubicacion del usuario
                                        userFLPC = LocationServices.getFusedLocationProviderClient(notificacionPantallaBloqueo.this);
                                        userFLPC.getLastLocation()
                                                .addOnSuccessListener(notificacionPantallaBloqueo.this, new OnSuccessListener<Location>() {
                                                    @Override
                                                    public void onSuccess(Location location) {
                                                        // Got last known location. In some rare situations this can be null.
                                                        if (location != null) {
                                                            latuser = location.getLatitude();
                                                            lnguser = location.getLongitude();

                                                            //contador para paradas
                                                            int contadorParadas = 1;

                                                            Double latuserresult = 0.0, lnguserresult = 0.0, latuserresult2 = 0.0, lnguserresult2 = 0.0, suma1 = 0.0, suma2 = 0.0;
                                                            oRutasAbordado.setLatuser(0.0);
                                                            oRutasAbordado.setLnguser(0.0);
                                                            for (DataSnapshot snapshot2 : dataSnapshot.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).getChildren()) {
                                                                // se cargan marcadores nuevos

                                                                String latitud1 = dataSnapshot.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).child("Parada" + contadorParadas).child("latitud").getValue().toString();
                                                                String longitud1 = dataSnapshot.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).child("Parada" + contadorParadas).child("longitud").getValue().toString();

                                                                Double latitud = Double.parseDouble(latitud1);
                                                                Double longitud = Double.parseDouble(longitud1);

                                                                latuserresult = (latuser) - (oRutasAbordado.getLatuser());
                                                                lnguserresult = (lnguser) - (oRutasAbordado.getLnguser());


                                                                suma1 = (Math.abs(latuserresult)) + (Math.abs(lnguserresult));

                                                                latuserresult2 = (latuser) - (latitud);
                                                                lnguserresult2 = (lnguser) - (longitud);

                                                                suma2 = (Math.abs(latuserresult2)) + (Math.abs(lnguserresult2));

                                                                if (latuser == latitud) {
                                                                    suma2 = suma2 * 2;
                                                                }
                                                                if (lnguser == longitud) {
                                                                    suma2 = suma2 * 2;
                                                                }

                                                                if (suma2 < suma1) {
                                                                    oRutasAbordado.setLatuser(latitud);
                                                                    oRutasAbordado.setLnguser(longitud);

                                                                    nombreParada = dataSnapshot.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).child("Parada" + contadorParadas).child("tittle").getValue().toString();
                                                                    numeroParada = "Parada"+contadorParadas;
                                                                }
                                                                contadorParadas++;
                                                            }

                                                            //se obtiene el id del usuario al que se le mandaaran los datos
                                                            for (DataSnapshot snapshot3 : dataSnapshot.child("Users").getChildren()) {
                                                                oUsuariossDatos = snapshot3.getValue(Usuarios.class);

                                                                String correoContactoConfianza = oUsuariossDatos.getCorreo();
                                                                if (contactoConfianza.equals(correoContactoConfianza)){
                                                                    userid = oUsuariossDatos.getIduser();
                                                                    banderaCorreoConfianzaExistente=1;
                                                                }


                                                            }
                                                            String nombreCurrentUser = dataSnapshot.child("Users").child(mAuth.getCurrentUser().getUid()).child("nombre").getValue().toString();
                                                            String correoCurrentUser = dataSnapshot.child("Users").child(mAuth.getCurrentUser().getUid()).child("correo").getValue().toString();
                                                            //objeto que busca el formato de la fecha a obtener
                                                            SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                                                                    " d-MM-yyyy");

                                                            //esto significa  a jueves 7 de mayo de 2020. "'a' EEEE d 'de' MMMM 'de' yyyy
                                                            SimpleDateFormat formateadorhora = new SimpleDateFormat(
                                                                    "HH:mm:ss");
                                                            //se obtiene la fecha y hora actual
                                                            Date fechaDate = new Date();
                                                            String fecha = formateadorfecha.format(fechaDate);
                                                            String Hora = formateadorhora.format(fechaDate);



                                                            //funcion para saber si el usuario esta cerca de 200 metros de la parada d elo contrario no podra indicar que esta arriba
                                                            Double latminimo,latmaximo,lngminimo,lngmaximo;
                                                            latminimo=oRutasAbordado.getLatuser()-0.002;
                                                            latmaximo=oRutasAbordado.getLatuser()+0.002;
                                                            lngminimo=oRutasAbordado.getLnguser()-0.002;
                                                            lngmaximo=oRutasAbordado.getLnguser()+0.002;

                                                            if (latuser<=latmaximo&&latuser>=latminimo&&lnguser<=lngmaximo&&lnguser>=lngminimo){

                                                                if (banderaCorreoConfianzaExistente==1) {

                                                                    for (int contadorReportes=1; contadorReportes<11;contadorReportes++){





                                                                        if (dataSnapshot.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte" + contadorReportes).exists() != true) {
                                                                            Map<String, Object> ReporteContactoConfianza = new HashMap<>();
                                                                            ReporteContactoConfianza.put("CorreoRemitente", correoCurrentUser);
                                                                            ReporteContactoConfianza.put("NombreRemitente", nombreCurrentUser);
                                                                            ReporteContactoConfianza.put("FechaAbordado", fecha);
                                                                            ReporteContactoConfianza.put("HoraAbordado", Hora);
                                                                            ReporteContactoConfianza.put("ParadaAbordada", nombreParada);
                                                                            ReporteContactoConfianza.put("RutaAbordada", rutaSeleccionadaAbordado);
                                                                            ReporteContactoConfianza.put("Id_Reporte", contadorReportes);
                                                                            //Mandar datos del map string a la base de datos
                                                                            OTBReference.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte" + contadorReportes).setValue(ReporteContactoConfianza).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    Toast.makeText(getApplicationContext(), "Gracias", Toast.LENGTH_LONG).show();

                                                                                }
                                                                            });

                                                                            OTBReference.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).child(numeroParada).child("snnipet").setValue(Hora + " " + fecha);

                                                                            //Enviar info a mi Actividad
                                                                            funcion_EnviarInfoMiActiviad(fecha, Hora, nombreParada, rutaSeleccionadaAbordado);


                                                                            finish();
                                                                            break;
                                                                        } else {
                                                                            if (contadorReportes == 10) {
                                                                                for (int contadorReportesViejos = 2; contadorReportesViejos < 11; contadorReportesViejos++) {
                                                                                    //String reporteviejo = dataSnapshot.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte"+contadorReportesViejos).child("FechaAbordado").getValue().toString();
                                                                                    String fechaAbordado = dataSnapshot.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte" + contadorReportesViejos).child("FechaAbordado").getValue().toString();
                                                                                    String horaAbordado = dataSnapshot.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte" + contadorReportesViejos).child("HoraAbordado").getValue().toString();
                                                                                    String fechahora = fechaAbordado + horaAbordado;

                                                                                    int contadormenosuno = contadorReportesViejos - 1;
                                                                                    String fechavieja = dataSnapshot.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte" + contadormenosuno).child("FechaAbordado").getValue().toString();
                                                                                    String horavieja = dataSnapshot.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte" + contadormenosuno).child("HoraAbordado").getValue().toString();
                                                                                    String fechahoravieja = fechavieja + horavieja;
                                                                                    if (fechahora.compareTo(fechahoravieja) > 0) {
                                                                                        Map<String, Object> ReporteContactoConfianza = new HashMap<>();
                                                                                        ReporteContactoConfianza.put("CorreoRemitente", correoCurrentUser);
                                                                                        ReporteContactoConfianza.put("NombreRemitente", nombreCurrentUser);
                                                                                        ReporteContactoConfianza.put("FechaAbordado", fecha);
                                                                                        ReporteContactoConfianza.put("HoraAbordado", Hora);
                                                                                        ReporteContactoConfianza.put("ParadaAbordada", nombreParada);
                                                                                        ReporteContactoConfianza.put("RutaAbordada", rutaSeleccionadaAbordado);
                                                                                        ReporteContactoConfianza.put("Id_Reporte", contadormenosuno);
                                                                                        //Mandar datos del map string a la base de datos
                                                                                        OTBReference.child("Users").child(userid).child("ContactoDeConfianza").child("Reporte" + contadormenosuno).setValue(ReporteContactoConfianza).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                Toast.makeText(getApplicationContext(), "Gracias", Toast.LENGTH_LONG).show();

                                                                                            }
                                                                                        });

                                                                                        OTBReference.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).child(numeroParada).child("snnipet").setValue(Hora + " " + fecha);

                                                                                        //Enviar info a mi Actividad
                                                                                        funcion_EnviarInfoMiActiviad(fecha, Hora, nombreParada, rutaSeleccionadaAbordado);


                                                                                        finish();
                                                                                        break;
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }else{
                                                                    //no hacer nada si no existe el correo e contacto
                                                                    Toast.makeText(getApplicationContext(),"Tu contacto de confianza no tiene una ceunta en OnTrackBus",Toast.LENGTH_LONG).show();
                                                                    //aqui acaba el verificar si el correo existe
                                                                    OTBReference.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).child(numeroParada).child("snnipet").setValue(Hora + " " + fecha);
                                                                    //Enviar info a mi Actividad
                                                                    funcion_EnviarInfoMiActiviad(fecha, Hora, nombreParada, rutaSeleccionadaAbordado);
                                                                    finish();
                                                                }



                                                            }else{
                                                                Toast.makeText(getApplicationContext(),"Estas demasiado lejos de una parada, porfavor espera acercarte a almenos 200 metros para presionar este boton.",Toast.LENGTH_LONG).show();
                                                            }


                                                        }
                                                    }


                                                });

                                        }
                                else if(rutaAbordada.equals("")){
                                    actv_OtraRutaAbordado.setError("Porfavor Ingrese una Ruta");
                                    Toast.makeText(getApplicationContext(),"Porfavor Ingrese una Ruta",Toast.LENGTH_SHORT).show();
                                }else{
                                    actv_OtraRutaAbordado.setError("Porfavor Ingrese una Ruta Valida");
                                    Toast.makeText(getApplicationContext(),"Porfavor Ingrese una Ruta Valida",Toast.LENGTH_SHORT).show();
                                }

                                }
                            });//aqui acaba el onclick listener

                        }//aqui acaba el on data change

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

        private void funcion_EnviarInfoMiActiviad(final String fecha, final String Hora, final String nombreParada, final String rutaSeleccionadaAbordado){

            final String idusuario = mAuth.getCurrentUser().getUid();

            //objeto que busca el formato de la fecha a obtener
            SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                    "EEEE");

            //se obtiene la fecha y hora actual
            Date fechaDate = new Date();
            final String ObtenerDia = formateadorfecha.format(fechaDate);

            OTBReference.child("Users").child(idusuario).child("MiActividad").child(ObtenerDia).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (int contadorReportes=1; contadorReportes<11;contadorReportes++){
                        if (dataSnapshot.child("Id_ReporteMA"+contadorReportes).exists()!=true){
                            Map<String, Object> ReporteMiActividad = new HashMap<>();
                            ReporteMiActividad.put("FechaAbordado",fecha);
                            ReporteMiActividad.put("HoraAbordado",Hora);
                            ReporteMiActividad.put("Id_ReporteMA",contadorReportes);
                            ReporteMiActividad.put("ParadaAbordada",nombreParada);
                            ReporteMiActividad.put("RutaAbordada",rutaSeleccionadaAbordado);
                            //Mandar datos del map string a la base de datos
                            OTBReference.child("Users").child(idusuario).child("MiActividad").child(ObtenerDia).child("Id_ReporteMA"+contadorReportes).setValue(ReporteMiActividad).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                            break;
                        }else{


                            if(contadorReportes==10){
                                for (int contadorReportesViejos=2;contadorReportesViejos<11;contadorReportesViejos++){
                                    String fechaAbordado = dataSnapshot.child("Id_ReporteMA"+contadorReportesViejos).child("FechaAbordado").getValue().toString();
                                    String horaAbordado = dataSnapshot.child("Id_ReporteMA"+contadorReportesViejos).child("HoraAbordado").getValue().toString();
                                    String fechahora=fechaAbordado+horaAbordado;

                                    int contadormenosuno=contadorReportesViejos-1;
                                    String fechavieja =dataSnapshot.child("Id_ReporteMA"+contadormenosuno).child("FechaAbordado").getValue().toString();
                                    String horavieja = dataSnapshot.child("Id_ReporteMA"+contadormenosuno).child("HoraAbordado").getValue().toString();
                                    String fechahoravieja=fechavieja+horavieja;
                                    if (fechahora.compareTo(fechahoravieja)>0){
                                        Map<String, Object> ReporteMiActividad = new HashMap<>();
                                        ReporteMiActividad.put("FechaAbordado",fecha);
                                        ReporteMiActividad.put("HoraAbordado",Hora);
                                        ReporteMiActividad.put("Id_ReporteMA",contadormenosuno);
                                        ReporteMiActividad.put("ParadaAbordada",nombreParada);
                                        ReporteMiActividad.put("RutaAbordada",rutaSeleccionadaAbordado);
                                        //Mandar datos del map string a la base de datos
                                        OTBReference.child("Users").child(idusuario).child("MiActividad").child(ObtenerDia).child("Id_ReporteMA"+contadormenosuno).setValue(ReporteMiActividad).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {


                                            }
                                        });
                                        break;
                                    }
                                }
                            }






                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


}
