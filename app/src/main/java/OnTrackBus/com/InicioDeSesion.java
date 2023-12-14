package OnTrackBus.com;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InicioDeSesion extends Fragment {


    //TextView que llevara a recuperar contraseña
    private TextView reestablecerContraseña;
    //variable para base de datos
    private DatabaseReference OTBReference;
    //variables para botones y cajas de texto
    private Button btnEntrar,btnRegistro;
    private EditText edtcorreo,edtcontrasena;
    private int contadorRMFDisponible=1;
    private int contadorRMFNumero=1;
    private Rutas or= new Rutas();
    //El objeto bundle permite pasar parametros entre activitis y fragments
    Bundle bundle = new Bundle();
    //instanciamos una barra de progreso mientras se inicia sesion
    private ProgressDialog progressDialog;
    //clase para loguear usuarios
    private FirebaseAuth mAuth;

    //contadores para ruta mas frecuentada
    private int contadorRMF1=1,contadorRutaNumero=1;
    //array para descargar las rutas que el usuario ha seleccionado previamente
    private ArrayList<String> rutasSeleccionadas= new ArrayList<>();

    public InicioDeSesion() {
        // Required empty public constructor
    }

    //metodo on create antes de crear Los elementos visuales
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }


    //Creacion de los elementos visuales aqui se trabajara
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vistaInicioDeSesion = inflater.inflate(R.layout.fragment_inicio_de_sesion, container, false);
       //Toast.makeText(getActivity(),"Seleccionaste ver la rutado: "+or.getNombreDeRuta(), Toast.LENGTH_SHORT).show();


        //union de las cajas de texto con java
        edtcorreo = vistaInicioDeSesion.findViewById(R.id.edt_correo);
        edtcontrasena =vistaInicioDeSesion.findViewById(R.id.edt_contrasena);

        //se instancia la barra de progreso
        progressDialog= new ProgressDialog(getActivity());

        //se referencia la clase auth
        mAuth = FirebaseAuth.getInstance();

        setOnClicksEvents(vistaInicioDeSesion);

        //Instancear variable de base de datos
        OTBReference = FirebaseDatabase.getInstance().getReference();
        //se obtiene de la base de datos el nombre de las rutas que se mostraran a elegir en el registrod e lo s usuarios
        OTBReference.child("Rutas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        //se obtiene losnombres de las rutas disponibles.zza
                        String rutaname=dataSnapshot.child("Ruta"+contadorRMFDisponible).child("NombreDeRuta").getValue().toString();
                        or.setNombreDeRuta(rutaname);
                        //se le agregan valores al bundle para pasarlos a otra interfaz
                        bundle.putString("ruta"+contadorRMFDisponible,or.getNombreDeRuta());
                        contadorRMFDisponible++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return vistaInicioDeSesion;
    }

    private void IniciarSesion(){
        String correo = edtcorreo.getText().toString().trim();
        String contraseña = edtcontrasena.getText().toString().trim();

        if (comprobacionCorreo()==false){
            //revisa que el correo sea valido y no este vacio
        }else if(contraseña.equals("")){
            edtcontrasena.setError("Dato Requerido");
            Toast.makeText(getActivity(),"Ingrese Una Contraseña", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.setMessage("Iniciando Sesion...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            //loguear usuario
            mAuth.signInWithEmailAndPassword(correo,contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //Variable para verificar si el usuario ha verificado su correo
                    FirebaseUser user  = mAuth.getCurrentUser();
                    //checar si se pudo entrar y si el correo o la contraseña son correctos
                    if (task.isSuccessful()){

                        if (!user.isEmailVerified()){
                            edtcorreo.setError("Correo Electronico no verificado.");
                            Toast.makeText(getActivity(),"Porfavor ve a tu Correo Electronico y da click en el corero de verificacion, para verificar tu cuenta.",Toast.LENGTH_LONG).show();
                        }else{



                            OTBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //se obtiene de la base de datos el nombre de las rutas seleccionadas por el usuario
                                    String id= mAuth.getCurrentUser().getUid();
                                    if (dataSnapshot.exists()){

                                        for (DataSnapshot snapshot: dataSnapshot.child("Users").child(id).child("RutaMasFrecuentada").getChildren()) {
                                            String istring = Integer.toString(contadorRMF1);
                                            String userkk = dataSnapshot.child("Users").child(id).child("RutaMasFrecuentada").child("RMF"+istring).getValue().toString();
                                            rutasSeleccionadas.add(userkk);
                                            contadorRMF1++;
                                        }

                                        //bundle para pasar las rutas seleccionadas por el usuario
                                        // Inicializas el Bundle
                                        Bundle bundleSesion = new Bundle();

                                        //agregamos el correo de confianza
                                        String correoConfianza = dataSnapshot.child("Users").child(id).child("contactoConfianza").getValue().toString();
                                        bundleSesion.putString("contactoConfianza",correoConfianza);

                                        //intent para iniciar la actividad siguiente
                                        Intent intentMenuUsuario = new Intent(getActivity(),MenuUsuario.class);
                                        for (int contadorarray=0;contadorarray<3;contadorarray++){
                                            if (rutasSeleccionadas.get(contadorarray)!=null){
                                                bundleSesion.putString("RMF"+contadorarray,rutasSeleccionadas.get(contadorarray));
                                            }
                                        }

                                        String rutaseleccionadaNumero=null;
                                        //se obtiene el numero de la primera ruta junto a el nombre de todas las rutas
                                        //se obtiene el numero de la primera ruta seleccionada
                                        for (DataSnapshot snapshot: dataSnapshot.child("Rutas").getChildren()) {
                                            //se obtiene losnombres de las rutas disponibles.zza
                                            String rutaname=dataSnapshot.child("Rutas").child("Ruta"+contadorRutaNumero).child("NombreDeRuta").getValue().toString();
                                            bundleSesion.putString("Ruta"+contadorRutaNumero,rutaname);
                                            if (rutaname.equals(bundleSesion.getString("RMF0"))){
                                                bundleSesion.putString("RutaSeleccionadaNumero","Ruta"+contadorRutaNumero);
                                                rutaseleccionadaNumero="Ruta"+contadorRutaNumero;
                                            }
                                            contadorRutaNumero++;
                                        }
                                        bundleSesion.putInt("CantidadDeRutas",contadorRutaNumero);

                                        int contadorParadas=1;
                                        //se descargan todaslas paradas de laprimera ruta seleccionada
                                        for (DataSnapshot snapshot: dataSnapshot.child("Rutas").child(rutaseleccionadaNumero).child("ida").getChildren()){
                                            String nombreParada = dataSnapshot.child("Rutas").child(rutaseleccionadaNumero).child("ida").child("Parada"+contadorParadas).child("tittle").getValue().toString();
                                            bundleSesion.putString("Parada"+contadorParadas,nombreParada);
                                            contadorParadas++;
                                        }
                                        bundleSesion.putInt("CantidadDeParadas",contadorParadas);

                                        // Agregas el Bundle al Intent e inicias ActivityB
                                        intentMenuUsuario.putExtras(bundleSesion);
                                        progressDialog.dismiss();
                                        startActivity(intentMenuUsuario);
                                        getActivity().finish();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                    }else{
                        //si el usuario no pudo iniciar sesion
                        if (task.getException() instanceof FirebaseAuthInvalidUserException){
                            edtcorreo.setError("Correo No Registrado");
                            Toast.makeText(getActivity(),"Este Correo no esta Asociado a ninguna cuenta, Por favor Registrate",Toast.LENGTH_SHORT).show();
                        }else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            edtcontrasena.setError("Contraseña Incorrecta");
                            Toast.makeText(getActivity(),"La Contraseña no coincide con el Correo Ingresado.",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getActivity(),"Algo Salio mal, favor de reportar",Toast.LENGTH_LONG).show();
                        }
                    }
                    progressDialog.dismiss();
                }
            });
        }
    }

    private boolean comprobacionCorreo(){
        // El email a validar
        String correo = edtcorreo.getText().toString().trim();
        // Patrón para validar el email
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        //busca nuestro email en base a la expresion que selecu¡cionamos del pattern en caso de que sea correcta nos retorna un valor true
        Matcher mather = pattern.matcher(correo);
        boolean bandera=false;
        if (correo.equals("")){
            edtcorreo.setError("Dato Requerido");
            Toast.makeText(getActivity(),"Porfavor Ingrese Un Correo Electronico ", Toast.LENGTH_SHORT).show();
        }else if (mather.find() == false){
            Toast.makeText(getActivity(),"Correo Electronico Invalido, Porfavor revise la estructura del Correo Electronico Ingresado.", Toast.LENGTH_LONG).show();
            edtcorreo.setError("Estructura de Correo electronico Invalida.");
        }else {
            bandera=true;
        }
        return bandera;
    }


    //Checar que si ya inicio sesion no nos muestre la pantala de login.
    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //revisar conexion a internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            if (currentUser != null){


                //bara de progreso en lo que se obtienen las direcciones
                progressDialog.setMessage("Iniciando Sesion...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                OTBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //se obtiene de la base de datos el nombre de las rutas seleccionadas por el usuario
                        String id= mAuth.getCurrentUser().getUid();

                        if (dataSnapshot.exists()){
                            for (DataSnapshot snapshot: dataSnapshot.child("Users").child(id).child("RutaMasFrecuentada").getChildren()) {
                                String istring = Integer.toString(contadorRMF1);
                                String userkk = dataSnapshot.child("Users").child(id).child("RutaMasFrecuentada").child("RMF"+istring).getValue().toString();
                                rutasSeleccionadas.add(userkk);
                                contadorRMF1++;
                            }

                            //bundle para pasar las rutas seleccionadas por el usuario
                            // Inicializas el Bundle
                            Bundle bundleSesion = new Bundle();

                            //agregamos el correo de confianza
                            String correoConfianza = dataSnapshot.child("Users").child(id).child("contactoConfianza").getValue().toString();
                            bundleSesion.putString("contactoConfianza",correoConfianza);



                            //intent para iniciar la actividad siguiente
                            Intent intentMenuUsuario = new Intent(getActivity(),MenuUsuario.class);
                            for (int contadorarray=0;contadorarray<3;contadorarray++){
                                if (rutasSeleccionadas.get(contadorarray)!=null){
                                    bundleSesion.putString("RMF"+contadorarray,rutasSeleccionadas.get(contadorarray));
                                }
                            }


                            String rutaseleccionadaNumero=null;
                            //se obtiene el numero de la primera ruta junto a el nombre de todas las rutas
                            //se obtiene el numero de la primera ruta seleccionada
                            for (DataSnapshot snapshot: dataSnapshot.child("Rutas").getChildren()) {
                                //se obtiene losnombres de las rutas disponibles.zza
                                String rutaname=dataSnapshot.child("Rutas").child("Ruta"+contadorRutaNumero).child("NombreDeRuta").getValue().toString();
                                bundleSesion.putString("Ruta"+contadorRutaNumero,rutaname);
                                if (rutaname.equals(bundleSesion.getString("RMF0"))){
                                    bundleSesion.putString("RutaSeleccionadaNumero","Ruta"+contadorRutaNumero);
                                    rutaseleccionadaNumero="Ruta"+contadorRutaNumero;
                                }
                                contadorRutaNumero++;
                            }
                            bundleSesion.putInt("CantidadDeRutas",contadorRutaNumero);

                            int contadorParadas=1;
                            //se descargan todaslas paradas de laprimera ruta seleccionada
                            for (DataSnapshot snapshot: dataSnapshot.child("Rutas").child(rutaseleccionadaNumero).child("ida").getChildren()){
                                String nombreParada = dataSnapshot.child("Rutas").child(rutaseleccionadaNumero).child("ida").child("Parada"+contadorParadas).child("tittle").getValue().toString();
                                bundleSesion.putString("Parada"+contadorParadas,nombreParada);
                                contadorParadas++;
                            }
                            bundleSesion.putInt("CantidadDeParadas",contadorParadas);


                            // Agregas el Bundle al Intent e inicias ActivityB
                            intentMenuUsuario.putExtras(bundleSesion);
                            progressDialog.dismiss();
                            startActivity(intentMenuUsuario);
                            getActivity().finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else {
                // Si hay conexión a Internet en este momento
                Toast.makeText(getActivity(),"Bienvenido, Porfavor Inicia Sesion", Toast.LENGTH_SHORT).show();
            }

        }else {
            // No hay conexión a Internet en este momento
            Toast.makeText(getActivity(),"No hay conexion a Internet, asegurate de estar conectado a una red para poder Iniciar Sesion o Registrarte.", Toast.LENGTH_LONG).show();
        }

}

    //region OnClicks Buttons
    private void setOnClicksEvents(View mainView){

        //region btnRegistro
        btnRegistro = (Button) mainView.findViewById(R.id.btn_registro);
        btnRegistro.setOnClickListener(v -> {
            //se llama a la clase a la cual cambiaremos y se crea un objetode  tipo bundle ya que desde aqui haremos la descarga de rutas a seleccionar al registrarse
            RegistroDeUsuarios fragmento = new RegistroDeUsuarios();
            //Se aactiva la clase para la transicion de fragmento
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            //se le agregan valores al bundle para pasarlos a otra interfaz
            bundle.putInt("cantidadDeRutas",contadorRMFDisponible-1);
            //se envia al bundle acompañado del objeto que abria el siguiente fragmento
            fragmento.setArguments(bundle);
            //se hace la trancisionde fragmento remplazando el fragmento en la actividad principal y se hace commit.
            transaction.replace(R.id.contenedor_main_activity,fragmento);
            transaction.commit();
        });
        //endregion

        //region btnEntrar
        //Declaracion de boton para entrar al Menu de usuarios en caso de que los datos puestos en las cajas de texto sean correctos
        btnEntrar = (Button) mainView.findViewById(R.id.btn_entrar);
        btnEntrar.setOnClickListener(v -> IniciarSesion());
        //endregion

        //region btnRestablecerContrasena
        //Boton para ir a fragmento donde se recupera contraseña.
        reestablecerContraseña = mainView.findViewById(R.id.btnRestablecerContrasena);
        //poner texto en subrayado
        reestablecerContraseña.setPaintFlags(reestablecerContraseña.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        reestablecerContraseña.setOnClickListener(v -> {
                    //se llama a la clase a la cual cambiaremos
                    RecuperarContrasena fragmento = new RecuperarContrasena();
                    //Se activa la clase para la transicion de fragmento
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    //se hace la trancision de fragmento remplazando el fragmento en la actividad principal y se hace commit.
                    transaction.replace(R.id.contenedor_main_activity, fragmento);
                    transaction.commit();
                });
        //endregion
    }
    //endregion

}
