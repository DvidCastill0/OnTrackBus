package OnTrackBus.com;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MapasEnTiempoReal extends Fragment implements OnMapReadyCallback {
    //google maps variables
    private GoogleMap mMap;


    //instanciamos una barra de progreso mientras se inicia sesion
    private ProgressDialog progressDialog;

    //instanciar la clase rutas para poder mandar elnumero de la ruta en base al nombre
    private Rutas oRutas = new Rutas();
    //adaptadores para obtener todos los marcadores que haya por ruta
    private ArrayList<Marker> tmpRealTimeMarker = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    //Button cambiar orientacion
    private Button btn_OrientacionRuta;

    //boton miubicacion
    private Button btn_mi_ubicacion;
    //boton paradamascercana
    private Button btn_ParadaCercana;
    //boton ya voy en camino
    private Button btn_voyEnCamino;
    //variables de firebase:
    private DatabaseReference OTBReference;
    //vista del fragmento
    private View vistaMapasEnTiempoReal;

    //spinner para seleccionar ruta
    private Spinner spnRuta;

    //array list para el adaptador del spinner.
    private ArrayList<String> rutas = new ArrayList<>();
    private ArrayList<String> rutasRecargar = new ArrayList<>();

    //arraylist rutas disponibles
    private ArrayList<String> rutasdisponibles = new ArrayList<>();

    //variables para pasar la seleccion de ruta al mapa
    private String rutaSeleccionada, rutaSeleccionadaNumero, rutaSeleccionadaSpn, orientacionRuta, correoConfianza;

    //contador para checar las rutas que hay en la base de datos.
    private int contadorRMFNumero = 1, contadorRutaNumeroAbordado;

    //variable para obtener la ultima localizacion conocida
    private FusedLocationProviderClient userFLPC;

    //variable para obtener la ultima localizacion conocida
    private FusedLocationProviderClient userFLPC2;

    //variables de latitud y longitud de la ultima localizacion del usuario
    private Double latuser = 0.0;
    private Double lnguser = 0.0;

    //variable boton buscar parada y autocompletar busqueda de parada
    private Button btn_buscar_parada;
    private AutoCompleteTextView actv_Buscar_Parada;
    private ArrayList<String> nombresDeParadasDisponibles = new ArrayList<>();
    private int CantidadDeParadas;
    private String numeroDeRutaParada;

    public MapasEnTiempoReal() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle datosrutas = getArguments();

        //obtenemos los valores de ubicacion desde el bundle para la primera vez que se abre la app latitud y longitud de usuario no obligatorio
        if (datosrutas.getString("latuser") != null && datosrutas.getString("lnguser") != null){
            latuser = Double.parseDouble(datosrutas.getString("latuser"));
            lnguser = Double.parseDouble(datosrutas.getString("lnguser"));
        }


        //obtener cantidad de Paradas                         CantidadDeParadas               obligatorio
        CantidadDeParadas = datosrutas.getInt("CantidadDeParadas");
        Toast.makeText(getActivity(), "cantidad de paradas"+CantidadDeParadas,Toast.LENGTH_SHORT).show();

        //obtener los nombres de las rutas y meterlos en un arraylist            "Parada"+contadorParadas                   obligatorio
        for (int contadorParadas=1;contadorParadas<CantidadDeParadas;contadorParadas++){
            String nombreDeParada = datosrutas.getString("Parada"+contadorParadas);
            nombresDeParadasDisponibles.add(nombreDeParada);
        }


        //obtener ubicacion del usuario para iniciar
        ultimaUbicacionDelUsuario();
        //obtener string que nos diga si la orientacion es de ida o vuelta                OrientacionRuta no obligatorio
        if (datosrutas.getString("OrientacionRuta") != null) {
            orientacionRuta = datosrutas.getString("OrientacionRuta");
        } else {
            orientacionRuta = "ida";
        }

        //se descarga el numero de rutas disponibles                           CantidadDeRutas obligatorio
        contadorRutaNumeroAbordado = datosrutas.getInt("CantidadDeRutas");
        //se descargan las srutas disponible  Rutas disponibles Obligatorio     Ruta" + contadorRD

        for (int contadorNombres=1; contadorNombres<contadorRutaNumeroAbordado;contadorNombres++){
            String nombreRuta = datosrutas.getString("Ruta"+contadorNombres);
            rutasdisponibles.add(nombreRuta);
        }



        //meter correo de confiana                                                  Correo electronico confianza  obligatorio   contactoConfianza
        correoConfianza = datosrutas.getString("contactoConfianza");

        //meter datos en array de RMF en casod eqeu se recargue el fragmento.                       RMF array obligatorio
        for (int contadorarray = 0; contadorarray < 3; contadorarray++) {
            if (datosrutas.getString("RMF" + contadorarray) != null) {
                //si el bundle no eta vacio, se asigna el string al array adapter
                rutasRecargar.add(getArguments().getString("RMF" + contadorarray));
            }
        }

        //meter datos en array a mostrar en este fragmento
        if (datosrutas != null) {
            //if para saber si se selecciono otra ruta
            if (datosrutas.getString("RutaSeleccionada") != null) {                     //Ruta seleccionada no obligatorio    RutaSeleccionada
                //poner datos en caso de que se seleccionara una ruta
                rutas.add(datosrutas.getString("RutaSeleccionada"));
                // Obtienes el texto del bundle
                for (int contadorarray = 0; contadorarray < 3; contadorarray++) {
                    if (datosrutas.getString("RMF" + contadorarray) != null) {
                        if (datosrutas.getString("RMF" + contadorarray).equals(datosrutas.getString("RutaSeleccionada"))) {

                        }else{
                            //si el bundle no eta vacio, se asigna el string al array adapter
                            rutas.add(getArguments().getString("RMF" + contadorarray));
                        }
                    }
                }
                rutas.add("Otra Ruta");
                rutaSeleccionada = datosrutas.getString("RutaSeleccionada");

                rutaSeleccionadaNumero = datosrutas.getString("RutaSeleccionadaNumero");                    //numero de ruta seleccionada obligatorio  RutaSeleccionadaNumero
            } else {
                // Poner datos en cacso de que no haya ninguna ruta seleccionada.
                for (int contadorarray = 0; contadorarray < 3; contadorarray++) {
                    if (datosrutas.getString("RMF" + contadorarray) != null) {
                        //si el bundle no eta vacio, se asigna el string al array adapter
                        rutas.add(getArguments().getString("RMF" + contadorarray));
                    }
                }
                rutaSeleccionada = datosrutas.getString("RMF0");
                rutaSeleccionadaNumero = datosrutas.getString("RutaSeleccionadaNumero");
                rutas.add("Otra Ruta");
            }


        } else {
            Toast.makeText(getActivity(), "no hay datos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vistaMapasEnTiempoReal = inflater.inflate(R.layout.fragment_mapas_en_tiempo_real, container, false);

        if (getActivity()==null){
            Toast.makeText(getActivity(),"no hay actividad",Toast.LENGTH_LONG).show();
        }else{

        //se instancia la barra de progreso
        progressDialog = new ProgressDialog(getActivity());

        //firebase instancias
        OTBReference = FirebaseDatabase.getInstance().getReference();


        //meter datos a spinner
        spnRuta = vistaMapasEnTiempoReal.findViewById(R.id.spn_Ruta_Visualizada);

        ArrayAdapter adp = new ArrayAdapter(getActivity(), R.layout.spinner_letras_style, rutas);
        spnRuta.setAdapter(adp);

        //funcionalidad en caso de que cambie el numero de la ruta
        spnRuta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rutaSeleccionadaSpn = (String) spnRuta.getAdapter().getItem(position);
                if (rutaSeleccionadaSpn == rutaSeleccionada) {

                } else if (rutaSeleccionadaSpn.equals("Otra Ruta")) {


                    // Creamos un nuevo Bundle ahora para pasar los datos ala ventana emergente
                    Bundle args3 = new Bundle();
                    int contadorRD2 = contadorRutaNumeroAbordado - 1;
                    int contadorRD0 = 1;
                    //se obtiene el numero de la primera ruta seleccionada
                    for (int contadorRD = 0; contadorRD < contadorRD2; contadorRD++) {
                        //se obtiene losnombres de las rutas disponibles.zza
                        args3.putString("Ruta" + contadorRD0, rutasdisponibles.get(contadorRD));
                        contadorRD0++;
                    }

                    for (int contadorArray2=0;contadorArray2<3;contadorArray2++){
                        if (rutasRecargar.get(contadorArray2)!=null){
                            int contadorrmf = contadorArray2+1;
                            args3.putString("RMF"+contadorrmf,rutasRecargar.get(contadorArray2));
                        }
                    }


                    //se pasa el correo de confianza
                    args3.putString("contactoConfianza", correoConfianza);

                    args3.putInt("CantidadDeRutas", contadorRutaNumeroAbordado);

                    args3.putString("orientacion",orientacionRuta);

                    args3.putString("rutaSeleccionadaNumero",rutaSeleccionadaNumero);
                    args3.putString("rutaSeleccionadaAbordado", rutaSeleccionada);


                    //intent para iniciar la actividad siguiente
                    Intent intentBuscarOtraruta = new Intent(getActivity(), BuscarOtraRutaVentanaEmergente.class);
                    // Agregas el Bundle al Intent e inicias ActivityB
                    intentBuscarOtraruta.putExtras(args3);
                    spnRuta.setSelection(0);
                    startActivity(intentBuscarOtraruta);


                } else {
                                Bundle bundleRecargar = new Bundle();

                                bundleRecargar.putString("OrientacionRuta", orientacionRuta);
                                bundleRecargar.putInt("CantidadDeRutas", contadorRutaNumeroAbordado);
                                //se pasa el correo de confianza
                                bundleRecargar.putString("contactoConfianza", correoConfianza);
                                bundleRecargar.putString("RutaSeleccionada", rutaSeleccionadaSpn);


                                //se envian las rutas predeterminadas del usuario junto a la ruta selccionada
                                for (int contadorarray = 0; contadorarray < 3; contadorarray++) {
                                    if (rutasRecargar.get(contadorarray) != null) {
                                        bundleRecargar.putString("RMF" + contadorarray, rutasRecargar.get(contadorarray));
                                    }

                                }

                                // se pasan las rutas seleccionadas disponibles junto a la cantidad de ellas
                                int contadorRD2 = contadorRutaNumeroAbordado - 1;
                                int contadorRD0 = 1;
                                //se obtiene el numero de la primera ruta seleccionada
                                for (int contadorRD = 0; contadorRD < contadorRD2; contadorRD++) {
                                    //se obtiene losnombres de las rutas disponibles.zza
                                    bundleRecargar.putString("Ruta" + contadorRD0, rutasdisponibles.get(contadorRD));
                                    contadorRD0++;
                                }


                                //intent para iniciar la actividad siguiente
                                Intent intentBuscarOtraruta = new Intent(getActivity(), pasadorSpinner.class);
                                // Agregas el Bundle al Intent e inicias ActivityB
                                intentBuscarOtraruta.putExtras(bundleRecargar);
                                 getActivity().finish();
                                startActivity(intentBuscarOtraruta);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //funcionalidad button cambiar orientacion.
        btn_OrientacionRuta = vistaMapasEnTiempoReal.findViewById(R.id.btn_ida_o_vuelta);
        if (orientacionRuta.equals("ida")) {
            btn_OrientacionRuta.setText("->");
        } else {
            btn_OrientacionRuta.setText("<-");
        }
        btn_OrientacionRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orientacionRuta.equals("ida")) {
                    orientacionRuta = "vuelta";
                } else {
                    orientacionRuta = "ida";
                }
                Bundle bundleRecargar = new Bundle();

                bundleRecargar.putString("OrientacionRuta", orientacionRuta);
                bundleRecargar.putInt("CantidadDeRutas", contadorRutaNumeroAbordado);
                //se pasa el correo de confianza
                bundleRecargar.putString("contactoConfianza", correoConfianza);
                bundleRecargar.putString("RutaSeleccionada", rutaSeleccionada);


                //se envian las rutas predeterminadas del usuario junto a la ruta selccionada
                for (int contadorarray = 0; contadorarray < 3; contadorarray++) {
                    if (rutasRecargar.get(contadorarray) != null) {
                        bundleRecargar.putString("RMF" + contadorarray, rutasRecargar.get(contadorarray));
                    }

                }

                // se pasan las rutas seleccionadas disponibles junto a la cantidad de ellas
                int contadorRD2 = contadorRutaNumeroAbordado - 1;
                int contadorRD0 = 1;
                //se obtiene el numero de la primera ruta seleccionada
                for (int contadorRD = 0; contadorRD < contadorRD2; contadorRD++) {
                    //se obtiene losnombres de las rutas disponibles.zza
                    bundleRecargar.putString("Ruta" + contadorRD0, rutasdisponibles.get(contadorRD));
                    contadorRD0++;
                }


                //intent para iniciar la actividad siguiente
                Intent intentBuscarOtraruta = new Intent(getActivity(), pasadorSpinner.class);
                // Agregas el Bundle al Intent e inicias ActivityB
                intentBuscarOtraruta.putExtras(bundleRecargar);
                getActivity().finish();
                startActivity(intentBuscarOtraruta);



            }
        });

        //funcionalidad ya voy en camino
        btn_voyEnCamino = vistaMapasEnTiempoReal.findViewById(R.id.btn_voy_en_camino);
        btn_voyEnCamino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Creamos un nuevo Bundle ahora para pasar los datos al fragmento de mapas
                Bundle args2 = new Bundle();
                int contadorRD2 = contadorRutaNumeroAbordado - 1;
                int contadorRD0 = 1;
                //se obtiene el numero de la primera ruta seleccionada
                for (int contadorRD = 0; contadorRD < contadorRD2; contadorRD++) {
                    //se obtiene losnombres de las rutas disponibles.zza
                    args2.putString("Ruta" + contadorRD0, rutasdisponibles.get(contadorRD));
                    contadorRD0++;
                }

                //se pasa el correo de confianza
                args2.putString("contactoConfianza", correoConfianza);

                args2.putInt("CantidadDeRutas", contadorRutaNumeroAbordado);

                args2.putString("orientacion",orientacionRuta);

                args2.putString("rutaSeleccionadaNumero",rutaSeleccionadaNumero);
                args2.putString("rutaSeleccionadaAbordado", rutaSeleccionada);

                //intent para iniciar la actividad siguiente
                Intent intentAbordado = new Intent(getActivity(), notificacionPantallaBloqueo.class);
                // Agregas el Bundle al Intent e inicias ActivityB
                intentAbordado.putExtras(args2);
                startActivity(intentAbordado);

            }
        });


        //instanciar boton para parada mas cercana
        btn_ParadaCercana = vistaMapasEnTiempoReal.findViewById(R.id.btn_ParadaCercana);

        //instancear boton mi ubbicacion
        btn_mi_ubicacion = vistaMapasEnTiempoReal.findViewById(R.id.btn_mi_ubicacion);

        //instanciar buscar parada autocomplete text view y boton
        btn_buscar_parada = vistaMapasEnTiempoReal.findViewById(R.id.btn_buscar_parada);
        actv_Buscar_Parada = vistaMapasEnTiempoReal.findViewById(R.id.actv_buscar_parada);
        //adaptador para buscar paradas
        ArrayAdapter<String> adapterParadasNombres = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,nombresDeParadasDisponibles);
        actv_Buscar_Parada.setAdapter(adapterParadasNombres);




            //implementar mapa en el fragmento
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapRutasEnTiempoReal);
            mapFragment.getMapAsync(this);
        }

        return vistaMapasEnTiempoReal;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Agregar marcadores en base a la ruta seleccionada.
        mMap = googleMap;

        //habilitr controles de localizacion y zoom
        mMap.setMyLocationEnabled(true);

        //desactivar boton mi ubicacion
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        //dar funcionalidad a boton mi ubicacion.
        btn_mi_ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFLPC2 = LocationServices.getFusedLocationProviderClient(getActivity());
                userFLPC2.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Double latuserubicacion = location.getLatitude();
                            Double lnguserubicacion = location.getLongitude();


                            //variables para actualizar camara a la paraa mas cercana
                            LatLng coordenadasmiubicacion = new LatLng(latuserubicacion,lnguserubicacion);
                            CameraUpdate miUbicacionboton = CameraUpdateFactory.newLatLngZoom(coordenadasmiubicacion, 18);
                            mMap.animateCamera(miUbicacionboton);
                        }
                    }
                });

            }
        });





        //barra de progreso en lo que carga el mapa
        progressDialog.setMessage("Cargando Mapa...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //funcionalidad para buscar parada
        btn_buscar_parada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ParadaABuscar= actv_Buscar_Parada.getText().toString().trim();
                //ver cual es la parada que se busco
                int contadorBuscarParadaMenosUno=CantidadDeParadas-1,bandera=0,numeroDeParadaABuscar=1;
                for (int contadorParadas=0;contadorParadas<contadorBuscarParadaMenosUno;contadorParadas++){
                    String nombreDeParada = nombresDeParadasDisponibles.get(contadorParadas);
                    if (ParadaABuscar.equals(nombreDeParada)){
                        bandera=1;
                        numeroDeParadaABuscar=contadorParadas+1;
                    }
                }

                if (ParadaABuscar.equals("")){
                    actv_Buscar_Parada.setError("Porfavor Ingrese un Nombre De parada a Buscar");
                    Toast.makeText(getActivity(),"Porfavor Ingrese un Nombre De parada a Buscar",Toast.LENGTH_SHORT).show();
                }else if (bandera==1){
                    //aqui se actualizara la camara en base a la parada

                    OTBReference.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).child("Parada"+numeroDeParadaABuscar).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String latParadastring = dataSnapshot.child("latitud").getValue().toString();
                            String lngParadastring = dataSnapshot.child("longitud").getValue().toString();

                            Double latParadaDouble = Double.parseDouble(latParadastring);
                            Double lngParadaDouble = Double.parseDouble(lngParadastring);

                            //variables para actualizar camara a la paraa mas cercana
                            LatLng coordenadas = new LatLng(latParadaDouble,lngParadaDouble);
                            CameraUpdate ParadaBuscada = CameraUpdateFactory.newLatLngZoom(coordenadas, 18);
                            mMap.animateCamera(ParadaBuscada);

                            //cerrar teclado al presionar boton
                            InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(actv_Buscar_Parada.getWindowToken(), 0);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }else{
                    actv_Buscar_Parada.setError("Porfavor Ingrese un Nombre De parada Valido a Buscar");
                    Toast.makeText(getActivity(),"Porfavor Ingrese un Nombre De parada Valido a Buscar",Toast.LENGTH_SHORT).show();
                }


            }
        });


        //boton para buscar la parada mas cercana
        btn_ParadaCercana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //variable para buscar la uktima ubicacion del usuario
                userFLPC = LocationServices.getFusedLocationProviderClient(getActivity());
                userFLPC.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    latuser=location.getLatitude();
                                    lnguser=location.getLongitude();


                                    OTBReference.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            //contador para paradas
                                            int contadorParadas=1;

                                            Double latuserresult=0.0,lnguserresult=0.0,latuserresult2=0.0,lnguserresult2=0.0,suma1=0.0,suma2=0.0;
                                            oRutas.setLatuser(0.0);
                                            oRutas.setLnguser(0.0);
                                            for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                                                // se cargan marcadores nuevos

                                                String latitud1 = dataSnapshot.child("Parada"+contadorParadas).child("latitud").getValue().toString();
                                                String longitud1 = dataSnapshot.child("Parada"+contadorParadas).child("longitud").getValue().toString();

                                                Double latitud = Double.parseDouble(latitud1);
                                                Double longitud = Double.parseDouble(longitud1);

                                                latuserresult=(latuser)-(oRutas.getLatuser());
                                                lnguserresult=(lnguser)-(oRutas.getLnguser());


                                                suma1=(Math.abs(latuserresult))+(Math.abs(lnguserresult));

                                                latuserresult2=(latuser)-(latitud);
                                                lnguserresult2=(lnguser)-(longitud);

                                                suma2=(Math.abs(latuserresult2))+(Math.abs(lnguserresult2));

                                                if (latuser==latitud){
                                                    suma2=suma2*2;
                                                }
                                                if (lnguser==longitud){
                                                    suma2=suma2*2;
                                                }

                                                if (suma2<suma1){
                                                    oRutas.setLatuser(latitud);
                                                    oRutas.setLnguser(longitud);
                                                }
                                                contadorParadas++;
                                            }

                                            //variables para actualizar camara a la paraa mas cercana
                                            LatLng coordenadas = new LatLng(oRutas.getLatuser(),oRutas.getLnguser());
                                            CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 18);
                                            mMap.animateCamera(miUbicacion);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });



                                }
                            }
                        });
            }
        });




        //se obtiene de la base de datos el nombre de las rutas que estan disponibles a ver en el fragmento y se compara con la que selecciono el usuario para ver si existe.
        OTBReference.child("Rutas").child(rutaSeleccionadaNumero).child(orientacionRuta).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {


                    for (Marker marker : realTimeMarkers) {
                        //se remueven marcadores viejos
                        marker.remove();
                    }
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // se cargan marcadores nuevos
                        oRutas = snapshot.getValue(Rutas.class);
                        Double latitud = oRutas.getLatitud();
                        Double longitud = oRutas.getLongitud();
                        String tittleu = oRutas.getTittle();
                        String snnipetu = oRutas.getSnnipet();

                        MarkerOptions markerOptions = new MarkerOptions();

                        //se obtiene la hora actual para compararse
                        //objeto que busca el formato de la fecha a obtener
                        SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                                "HH:mm:ss d-MM-yyyy");


                        //se obtiene la fecha y hora actual
                        Date fechaDate = new Date();
                        String fecha = formateadorfecha.format(fechaDate);

                        Date fechaactual = null;
                        Date fechaMarcador=null;

                        try {
                            fechaactual= formateadorfecha.parse(fecha);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        try {
                            fechaMarcador= formateadorfecha.parse(snnipetu);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long diferenciaEn_ms = fechaactual.getTime() - fechaMarcador.getTime();
                        long minutos = diferenciaEn_ms/60000;

                        //funcion para poner marcadores en base a el riempo que ha pasado desde que se marco
                        String iconu="sin_abordar";
                        if (minutos<1){
                            iconu="abordado_menos_min";
                        }else if (minutos>1 && minutos<10){
                            iconu="abordado_mas_min";
                        }else {
                            iconu="sin_abordar";
                        }
                        String snnipethaceuanto = "";
                        if (minutos<1){
                            long segundos = diferenciaEn_ms/6000;
                            if (segundos==1){
                                snnipethaceuanto="Ruta abordada hace: "+segundos+" Segundo.";
                            }else{
                                snnipethaceuanto="Ruta abordada hace: "+segundos+" Segundos.";
                            }

                        }
                        else if (minutos<60){
                            if (minutos==1){
                                snnipethaceuanto="Ruta abordada hace: "+minutos+" Minuto.";
                            }else{
                                snnipethaceuanto="Ruta abordada hace: "+minutos+" Minutos.";
                            }

                        }else if (minutos>=60&&minutos<1440){
                            long horas = minutos/60;
                            if (horas==1){
                                snnipethaceuanto="Ruta abordada hace: "+horas+" Hora.";
                            }else {
                                snnipethaceuanto="Ruta abordada hace: "+horas+" Horas.";
                            }

                        }else if (minutos>=1440){
                            long horas = minutos/60;
                            long dias = horas/24;
                            if (dias==1){
                                snnipethaceuanto="Ruta abordada hace: "+dias+" Dia.";
                            }else {
                                snnipethaceuanto="Ruta abordada hace: "+dias+" Dias.";
                            }

                        }


                        if (!isAdded()){
                            return;
                        }else {
                            Bitmap imageBitmap = BitmapFactory.decodeResource(getActivity().getResources(), getActivity().getResources().getIdentifier(iconu, "drawable", vistaMapasEnTiempoReal.getContext().getPackageName()));
                            markerOptions.position(new LatLng(latitud, longitud)).title(tittleu).snippet(snnipethaceuanto).icon(BitmapDescriptorFactory.fromBitmap(imageBitmap)).anchor(0.0f, 1.0f);
                            tmpRealTimeMarker.add(mMap.addMarker(markerOptions));
                        }





                    }


                    //se vacian los marcadores agregados en otro array de marcadores para cuando se necesiten borrar
                    realTimeMarkers.clear();
                    realTimeMarkers.addAll(tmpRealTimeMarker);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        LatLng usuario = new LatLng(latuser, lnguser);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usuario, 16));

    }

    private void ultimaUbicacionDelUsuario() {
        //barra de progreso en lo que carga el mapa
        userFLPC = LocationServices.getFusedLocationProviderClient(getActivity());
        userFLPC.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latuser=location.getLatitude();
                            lnguser=location.getLongitude();

                        }
                    }
                });


    }

}
