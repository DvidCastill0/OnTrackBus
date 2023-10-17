package OnTrackBus.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuUsuario extends AppCompatActivity {
    private Toolbar toolbar;
    //boton y variable para cerrar sesion
    private FirebaseAuth mAuth;

    //bundle pra obtener datos
    private Bundle bundle;

    private int contadorRutaNumero=1;

    //variable para obtener la ultima localizacion conocida
    private FusedLocationProviderClient userFLPC;

    // Creamos un nuevo Bundle ahora para pasar los datos al fragmento de mapas
    private Bundle args = new Bundle();

    //creamos todos los fragmentos a usar
    //remplazar fragmento en contenedor(Poner fragmento inicial)
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private MapasEnTiempoReal fragmentoMapasEnTiempoReal= new MapasEnTiempoReal();
    private Contacto_De_Confianza fragmentoContacto_De_Confianza = new Contacto_De_Confianza();
    private CanalesDeRutas fragmentoCanalesDerutas = new CanalesDeRutas();
    private MiActividad fragmentoMiActividad = new MiActividad();
    private Mi_Perfil fragmentoMiPerfil = new Mi_Perfil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_usuario);


        // Obtienes el Bundle del Intent pasado
        bundle = getIntent().getExtras();

        if (bundle.getString("RecargarFragmento")==null){
            Toast.makeText(getApplicationContext(),"Bienvenido",Toast.LENGTH_LONG).show();
        }


        //se instancian las variables de autenticacion de firebase
        mAuth = FirebaseAuth.getInstance();


        //añadir toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarMenuUsuario);
        //agregar iconp de navegacion
        toolbar.setNavigationIcon(R.drawable.otbicono);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


                    //valores a obtener al recargar fragmento
                    String orientacionRuta=null,correoConfianza=null,rutaSeleccionada=null,rutaSeleccionadaNumero=null,nombreDeRutas=null;
                    int CantidadDeRutas;




                    //priemr parametro la orientacion este parametro no es obligatorio, pero puede recibir al escoger otra ruta
                    if (bundle.getString("OrientacionRuta") != null){
                        orientacionRuta=bundle.getString("OrientacionRuta");
                        args.putString("OrientacionRuta",orientacionRuta);
                    }

                    //segundo parametro Cantidad de rutas este parametro si es obligatorio y siempre recibe osea que es obligatorio
                    CantidadDeRutas  = bundle.getInt("CantidadDeRutas");
                    args.putInt("CantidadDeRutas",CantidadDeRutas);

                    //tercer parametro contactoConfianza este parametro siempre recibe por lo cual es obligatorio
                    correoConfianza = bundle.getString("contactoConfianza");
                    args.putString("contactoConfianza",correoConfianza);

                    //cuarto parametro RutaSeleccionada este parametro no es obligatorio pero alguna funcion si lo usa
                    if (bundle.getString("RutaSeleccionada") != null){
                        rutaSeleccionada=bundle.getString("RutaSeleccionada");
                        args.putString("RutaSeleccionada",rutaSeleccionada);
                    }

                    //quinto parametro es RutaSeleccionadaNumero este parametro si es obligatorio y lo usan todos los llamados a esta actividad
                    rutaSeleccionadaNumero = bundle.getString("RutaSeleccionadaNumero");
                    args.putString("RutaSeleccionadaNumero",rutaSeleccionadaNumero);

                    //sexto parametro array que contiene los nombres de las rutas Ruta+contador           obligatorio
                    for (int contadorDeRutas=1; contadorDeRutas<CantidadDeRutas;contadorDeRutas++){
                        nombreDeRutas=bundle.getString("Ruta"+contadorDeRutas);
                        args.putString("Ruta"+contadorDeRutas,nombreDeRutas);
                    }

                    //septimo parametro Rutas ams frecuentadas por el usuario RMF+contador   obligatorio
                    for (int contadorarray=0;contadorarray<3;contadorarray++){
                        if (bundle.getString("RMF"+contadorarray)!=null){
                            //si el bundle no eta vacio, se manda un String a la siguiente actividad para guardar las rutas seleccionadas por el usuario
                            args.putString("RMF"+contadorarray,bundle.getString("RMF"+contadorarray));
                        }
                    }

                    //octavo parametro La cantidad de Paradas de la ruta 1         obligatorio CantidadDeParadas
                    int CantidadDeParadas = bundle.getInt("CantidadDeParadas");
                    args.putInt("CantidadDeParadas",CantidadDeParadas);

                    //noveno parametro Los nombres de las rutas                     obligatorio "Parada"+contadorParadas
                    for (int contadorParadas=1;contadorParadas<CantidadDeParadas;contadorParadas++){
                        String nombreDeParada = bundle.getString("Parada"+contadorParadas);
                        args.putString("Parada"+contadorParadas,nombreDeParada);
                    }

                    //enviar la ultima ubicacion del usuario
                    userFLPC = LocationServices.getFusedLocationProviderClient(MenuUsuario.this);
                    userFLPC.getLastLocation()
                            .addOnSuccessListener(MenuUsuario.this, new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            // Got last known location. In some rare situations this can be null.
                                            if (location != null) {
                                                //se agregan estos parametros comoprimera localizacion del usuario
                                                args.putString("latuser",""+location.getLatitude());
                                                args.putString("lnguser",""+location.getLongitude());

                                                fragmentoMapasEnTiempoReal.setArguments(args);

                                                //remplazar fragmento en contenedor(Poner fragmento inicial)
                                                fragmentManager.beginTransaction().replace(R.id.contenedor_Menu_Usuario,fragmentoMapasEnTiempoReal,"Mapas_En_Tiempo_Real").commit();
                                            }
                                        }
                                    });


                }



    //Agregar funcioalidad a toolbar
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_menu_usuario,menu);


         return true;
     }

     public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){

            // boton de navegacion
            case android.R.id.home:

                if (fragmentManager.findFragmentByTag("Contacto_De_Confianza")!= null){
                    fragmentManager.beginTransaction().hide(fragmentoContacto_De_Confianza).commit();
                }
                if (fragmentManager.findFragmentByTag("Canales_De_Rutas")!= null){
                    fragmentManager.beginTransaction().hide(fragmentoCanalesDerutas).commit();
                }
                if (fragmentManager.findFragmentByTag("Mi_Actividad")!= null){
                    fragmentManager.beginTransaction().hide(fragmentoMiActividad).commit();
                }
                if (fragmentManager.findFragmentByTag("Mi_Perfil")!=null){
                    fragmentManager.beginTransaction().hide(fragmentoMiPerfil).commit();
                }

            break;
            case R.id.item_Canales_de_Rutas:

                int contadorRutasCanales = bundle.getInt("CantidadDeRutas");
                Bundle bundleEnviarCanales = new Bundle();

                for (int contadorEnviar=1;contadorEnviar<contadorRutasCanales;contadorEnviar++){
                    bundleEnviarCanales.putString("Ruta"+contadorEnviar,bundle.getString("Ruta"+contadorEnviar));
                }
                bundleEnviarCanales.putInt("CantidadDeRutas",contadorRutasCanales);

                if (fragmentManager.findFragmentByTag("Canales_De_Rutas")!=null){

                    if (fragmentManager.findFragmentByTag("Contacto_De_Confianza")!= null){
                        fragmentManager.beginTransaction().hide(fragmentoContacto_De_Confianza).commit();
                    }
                    if (fragmentManager.findFragmentByTag("Mi_Actividad")!=null){
                        fragmentManager.beginTransaction().hide(fragmentoMiActividad).commit();
                    }
                    if (fragmentManager.findFragmentByTag("Mi_Perfil")!=null){
                        fragmentManager.beginTransaction().hide(fragmentoMiPerfil).commit();
                    }
                    fragmentManager.beginTransaction().show(fragmentoCanalesDerutas).commit();
                }else {

                    fragmentoCanalesDerutas.setArguments(bundleEnviarCanales);
                    fragmentManager.beginTransaction().add(R.id.contenedor_Menu_Usuario,fragmentoCanalesDerutas,"Canales_De_Rutas").commit();
                }


                break;
                case R.id.item_Contacto_De_Confiaza:

                    if (fragmentManager.findFragmentByTag("Contacto_De_Confianza")!=null){

                        if (fragmentManager.findFragmentByTag("Canales_De_Rutas")!= null){
                            fragmentManager.beginTransaction().hide(fragmentoCanalesDerutas).commit();
                        }
                        if (fragmentManager.findFragmentByTag("Mi_Actividad")!= null){
                            fragmentManager.beginTransaction().hide(fragmentoMiActividad).commit();
                        }
                        if (fragmentManager.findFragmentByTag("Mi_Perfil")!=null){
                            fragmentManager.beginTransaction().hide(fragmentoMiPerfil).commit();
                        }
                        fragmentManager.beginTransaction().show(fragmentoContacto_De_Confianza).commit();
                    }else {
                        fragmentManager.beginTransaction().add(R.id.contenedor_Menu_Usuario,fragmentoContacto_De_Confianza,"Contacto_De_Confianza").commit();
                    }

                break;
                case R.id.item_Mi_Actividad_Diaria:

                    if (fragmentManager.findFragmentByTag("Mi_Actividad")!=null){

                        if (fragmentManager.findFragmentByTag("Canales_De_Rutas")!= null){
                            fragmentManager.beginTransaction().hide(fragmentoCanalesDerutas).commit();
                        }
                        if (fragmentManager.findFragmentByTag("Contacto_De_Confianza")!=null){
                            fragmentManager.beginTransaction().hide(fragmentoContacto_De_Confianza).commit();
                        }
                        if (fragmentManager.findFragmentByTag("Mi_Perfil")!=null){
                            fragmentManager.beginTransaction().hide(fragmentoMiPerfil).commit();
                        }

                        fragmentManager.beginTransaction().show(fragmentoMiActividad).commit();
                    }else {
                        fragmentManager.beginTransaction().add(R.id.contenedor_Menu_Usuario,fragmentoMiActividad,"Mi_Actividad").commit();
                    }
                    break;


            case R.id.item_Actualizar_Mis_Datos:

                 if (fragmentManager.findFragmentByTag("Mi_Perfil")!=null){

                if (fragmentManager.findFragmentByTag("Canales_De_Rutas")!= null){
                    fragmentManager.beginTransaction().hide(fragmentoCanalesDerutas).commit();
                }
                if (fragmentManager.findFragmentByTag("Contacto_De_Confianza")!=null){
                    fragmentManager.beginTransaction().hide(fragmentoContacto_De_Confianza).commit();
                }
                if (fragmentManager.findFragmentByTag("Mi_Actividad")!=null){
                    fragmentManager.beginTransaction().hide(fragmentoMiActividad).commit();
                }

                fragmentManager.beginTransaction().show(fragmentoMiPerfil).commit();
            }else {
                fragmentManager.beginTransaction().add(R.id.contenedor_Menu_Usuario,fragmentoMiPerfil,"Mi_Perfil").commit();
            }
                break;

            case R.id.item_Ayuda:
                // se finaliza la actividad para que no pueda volver hacia atras
                startActivity(new Intent(this,ventana_EmergenteAyuda.class));
                break;

            case R.id.item_Cerrar_Sesion:
                //se usa el metodo para cerrar sesion.
                mAuth.signOut();
                // se finaliza la actividad para que no pueda volver hacia atras
                startActivity(new Intent(this,MainActivity.class));
                finish();
                break;
        }
        return true;
     }

}
