package OnTrackBus.com;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistroDeUsuarios extends Fragment {
    //variable de firebase para sesion de  usuarios
    private FirebaseAuth mAuth;
    //Declaracion de variables  componentes de interfaz
    private Button btn_Volver,btn_Registrar,btn_AgregarRuta,btn_QuitarRuta1,btn_QuitarRuta2,btn_QuitarRuta3;
    private EditText et_Nombre,et_Apellido,et_Correo,et_Contraseña,et_ContraseñaConfirmacion,et_ContactoConfianza;
    private TextView tv_ruta1,tv_ruta2,tv_ruta3;
    private AutoCompleteTextView actv_RMF;
    //getter and setter para obtener informacion en el programa
    private Usuarios u = new Usuarios();
    //Reerencia para conectar con firebase.
    private DatabaseReference OTBReference;
    //Vista de este fragmento
    private View vistaRegistro;

    //arraylist para autocompletetextview
    private ArrayList<String> rutas= new ArrayList<>();
    //array que guardara las rutas que el usuario decida guardar
    private String [] array_rutas_seleccionadas= new String[3];
    //contador para descargar rutas y para revisar las rutas que aun quedan disponibles
    private int i,rutasagregadas=3;
    //instanciamos una barra de profreso mientras se registra el usuario
    private ProgressDialog progressDialog;

    public RegistroDeUsuarios() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //obtener cantidad de rutas actuales en la lista
            i = getArguments().getInt("cantidadDeRutas");

            for (int i2=1;i2<=i;i2++){
                //obtener nombres de las rutas para mostrar al usuario a la hora de escoger las 3 predeterminadas
                String ruta = getArguments().getString("ruta"+i2);
                rutas.add(ruta);
            }



        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vistaRegistro = inflater.inflate(R.layout.fragment_registro_de_usuarios, container, false);
        //instanceamos la referencia afirebase y  a  su autenticacion
        OTBReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //Instanceamos los elementos de xml con lso de java
        et_Nombre = vistaRegistro.findViewById(R.id.etNombre);
        et_Apellido = vistaRegistro.findViewById(R.id.etApellido);
        et_Correo = vistaRegistro.findViewById(R.id.etCorreoElectronicoRegistro);
        et_Contraseña = vistaRegistro.findViewById(R.id.etContraseñaRegistro);
        et_ContraseñaConfirmacion = vistaRegistro.findViewById(R.id.etContraseñaRegistroConfirmacion);
        et_ContactoConfianza = vistaRegistro.findViewById(R.id.etContactoConfianzaRegistro);

        actv_RMF = vistaRegistro.findViewById(R.id.actvRutaMasFrecuentadaRegistro);
        //array adapter para autocompletetextview
        ArrayAdapter<String> adapterrutas = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,rutas);
        actv_RMF.setAdapter(adapterrutas);

        tv_ruta1=vistaRegistro.findViewById(R.id.tvRuta1);
        tv_ruta1.setVisibility(View.INVISIBLE);
        tv_ruta2=vistaRegistro.findViewById(R.id.tvRuta2);
        tv_ruta2.setVisibility(View.INVISIBLE);
        tv_ruta3=vistaRegistro.findViewById(R.id.tvRuta3);
        tv_ruta3.setVisibility(View.INVISIBLE);

        btn_QuitarRuta1 = vistaRegistro.findViewById(R.id.btn_eliminar_ruta_1);
        btn_QuitarRuta1.setVisibility(View.INVISIBLE);
        btn_QuitarRuta2 = vistaRegistro.findViewById(R.id.btn_eliminar_ruta_2);
        btn_QuitarRuta2.setVisibility(View.INVISIBLE);
        btn_QuitarRuta3 = vistaRegistro.findViewById(R.id.btn_eliminar_ruta_3);
        btn_QuitarRuta3.setVisibility(View.INVISIBLE);

        progressDialog= new ProgressDialog(getActivity());

        //metodos de funcionalidad de los botones
        eliminarRuta();
        agregarRuta();
        BotonRegresar();
        BotonRegistrar();
        return vistaRegistro;
    }

    //metodo de accion oara regresar a el login.
    private void BotonRegresar(){
        btn_Volver = (Button) vistaRegistro.findViewById(R.id.btn_regresaralogin);
        btn_Volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                InicioDeSesion fragment = new InicioDeSesion();
                transaction.replace(R.id.contenedor_main_activity,fragment);
                transaction.commit();
            }
        });
    }

        //metodo de accion del boton registrar
    private void BotonRegistrar(){
        btn_Registrar = (Button) vistaRegistro.findViewById(R.id.btn_Registrarse);
        btn_Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validacion();
            }
        });
    }

    //metodo donde se agregan rutas seleccionada spor le usuario.
    private void agregarRuta(){
        btn_AgregarRuta = (Button) vistaRegistro.findViewById(R.id.btn_Agregar_Ruta);
        btn_AgregarRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bandera para saber si la ruta escrita esta dentrod e als rutas disponibles, para ver que no se repita una ruta
                int bandera=0,banderaRutaRepetida=0;
                //variable que obtiene el valor del autocompletetextview
                String rutase = actv_RMF.getText().toString().trim();

                //se revisa que no se hayan agregado mas de 3 rutas
                if (rutasagregadas!=0) {
                    //Se revisa que no sea una caja vacia
                    if (rutase.equals("")) {
                        Toast.makeText(getActivity(), "Porfavor Ingresa un numero de ruta", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int contador = 0; contador < i; contador++) {
                            //if que revisa la ruta pedida por el usuario entre las rutas disponibles a ver
                            if (rutase.equals(rutas.get(contador))) {
                                //bucle que busca que nos e este repitiendo ninguna ruta
                                for (int contadorRutaRepetida = 0; contadorRutaRepetida < 3; contadorRutaRepetida++) {
                                    //se revisa que el valor ingresado no haya sido repetido
                                    if (rutase.equals(array_rutas_seleccionadas[contadorRutaRepetida])) {
                                        Toast.makeText(getActivity(), "Ruta " + rutase + " Repetida favor de agregar un valor diferente.", Toast.LENGTH_SHORT).show();
                                        banderaRutaRepetida = 1;
                                        bandera = 1;
                                        break;
                                    }
                                }
                                if (banderaRutaRepetida == 0) {
                                    /*Inicia un ciclo for que busca el primer espacio en blanco en el array de
                                     * de las rutas seleccionadas*/
                                    for (int contadorarray = 0; contadorarray < 3; contadorarray++) {
                                        //Si se encuentra un elemento del array que esté vacío...
                                        if (array_rutas_seleccionadas[contadorarray] == null) {

                                            //... se establece su valor en un string de la ruta seleccionada en el spinner
                                            array_rutas_seleccionadas[contadorarray] = rutase;
                                            rutasagregadas--;
                                            //Y visualmente se establece dicha ruta en la pantalla
                                            switch (contadorarray) {
                                                case 0:
                                                    tv_ruta1.setText(rutase);
                                                    tv_ruta1.setVisibility(View.VISIBLE);
                                                    btn_QuitarRuta1.setVisibility(View.VISIBLE);
                                                    u.setRMF1(array_rutas_seleccionadas[0]);
                                                    break;
                                                case 1:
                                                    tv_ruta2.setText(rutase);
                                                    tv_ruta2.setVisibility(View.VISIBLE);
                                                    btn_QuitarRuta2.setVisibility(View.VISIBLE);
                                                    u.setRMF2(array_rutas_seleccionadas[1]);
                                                    break;
                                                case 2:
                                                    tv_ruta3.setText(rutase);
                                                    tv_ruta3.setVisibility(View.VISIBLE);
                                                    btn_QuitarRuta3.setVisibility(View.VISIBLE);
                                                    u.setRMF3(array_rutas_seleccionadas[2]);
                                                    break;
                                            }
                                            /*Si salió bien, se saldrá del ciclo después de haber agregado una ruta tanto
                                             * a la lógica de la app como a su parte visual*/
                                            //limpiamos el autocompletextview una vez agregada la ruta.
                                            actv_RMF.setText("");
                                            //le mostramos al usuario que gruta se ingreso
                                            Toast.makeText(getActivity(), "Ruta " + rutase + " Agregada, puedes agregar " + rutasagregadas + " rutas mas.", Toast.LENGTH_SHORT).show();
                                            bandera = 1;
                                            break;
                                        }

                                    }
                                }

                            }

                        }
                        //En caso de que no se haya agregado la ruta
                        if (bandera == 0) {
                            Toast.makeText(getActivity(), "Porfavor agregue una ruta de la lista de rutas disponibles", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    //avsiar al usuario que alcanzo las unicas 3 ruats disponibles a agregar
                    Toast.makeText(getActivity(), "Ya has agregado el maximo de Rutas Predeterminadas", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Aqui se llama a el metodo para eliminar rutas predeterminadas por el usuario y juntar visualmente hacia la izquierda las etiquetas.
    private void eliminarRuta(){
        btn_QuitarRuta1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                array_rutas_seleccionadas[0] = null;
                rutasagregadas++;
                acomodar();
            }
        });

        btn_QuitarRuta2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                array_rutas_seleccionadas[1] = null;
                rutasagregadas++;
                acomodar();
            }
        });

        btn_QuitarRuta3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                array_rutas_seleccionadas[2] = null;
                rutasagregadas++;
                acomodar();
            }
        });
    }
    //una vez eliminada la ruta juntar hacia la iquierda
    private void acomodar(){

        //array que guardara las rutas para acomodarlas en el nuevo codigo
        String [] array_acomodar_rutas= new String[3];
        for (int contador=0; contador<3;contador++){
            if (array_rutas_seleccionadas[contador]!=null){
                /*Inicia un ciclo for que busca el primer espacio en blanco en el array de
                 * de las rutas seleccionadas*/
                for(int i3 = 0 ; i3 < 3 ; i3++) {

                    //Si se encuentra un elemento del array que esté vacío...
                    if (array_acomodar_rutas[i3] == null) {
                        array_acomodar_rutas[i3] = array_rutas_seleccionadas[contador];
                        break;
                    }
                }
            }
        }
        tv_ruta1.setVisibility(View.INVISIBLE);
        btn_QuitarRuta1.setVisibility(View.INVISIBLE);
        tv_ruta2.setVisibility(View.INVISIBLE);
        btn_QuitarRuta2.setVisibility(View.INVISIBLE);
        tv_ruta3.setVisibility(View.INVISIBLE);
        btn_QuitarRuta3.setVisibility(View.INVISIBLE);
        for (int i3=0;i3<3;i3++){
            array_rutas_seleccionadas[i3]=array_acomodar_rutas[i3];
            if (i3==0 && array_rutas_seleccionadas[0]!=null){
                tv_ruta1.setVisibility(View.VISIBLE);
                tv_ruta1.setText(array_rutas_seleccionadas[i3]);
                btn_QuitarRuta1.setVisibility(View.VISIBLE);
                u.setRMF1(array_rutas_seleccionadas[0]);
            }
            if (i3==1 && array_rutas_seleccionadas[1]!=null){
                tv_ruta2.setVisibility(View.VISIBLE);
                tv_ruta2.setText(array_rutas_seleccionadas[i3]);
                btn_QuitarRuta2.setVisibility(View.VISIBLE);
                u.setRMF2(array_rutas_seleccionadas[1]);
            }
            if (i3==2 && array_rutas_seleccionadas[2]!=null){
                tv_ruta3.setVisibility(View.VISIBLE);
                tv_ruta3.setText(array_rutas_seleccionadas[i3]);
                btn_QuitarRuta3.setVisibility(View.VISIBLE);
                u.setRMF3(array_rutas_seleccionadas[2]);
            }

        }

    }


    private void validacion(){
        String RMF = array_rutas_seleccionadas[0];
        if (comprobacionNombre()==false){
            Toast.makeText(getActivity(), "Datos Erroneos, Verifique la casilla de Nombre", Toast.LENGTH_SHORT).show();
        }else if (comprobacionApellido()==false){
            Toast.makeText(getActivity(), "Datos Erroneos, Verifique la casilla de Apellidos", Toast.LENGTH_SHORT).show();
        }
        else if (comprobacionCorreo()==false){
            Toast.makeText(getActivity(), "Datos Erroneos, Verifique la casilla de Correo", Toast.LENGTH_SHORT).show();
        }
        else if (comprobacionContraseña()==false){
            Toast.makeText(getActivity(), "Datos Erroneos, Verifique la casilla de Contraseña", Toast.LENGTH_SHORT).show();
        }
        else if (comprobacionContraseñaConfirmacion()==false){
            Toast.makeText(getActivity(), "Datos Erroneos, Verifique la casilla de Confirmacion de Contraseña", Toast.LENGTH_SHORT).show();
        }
        else if (comprobacionContactoConfianza()==false){
            Toast.makeText(getActivity(), "Datos Erroneos, Verifique la casilla de Contacto de Confianza", Toast.LENGTH_SHORT).show();
        }
        else if (RMF==null){
            actv_RMF.setError("Se requiere aregues una ruta predeterminada.");
            Toast.makeText(getActivity(), "Datos Erroneos, Verifique la casilla de Ruta Predeterminada", Toast.LENGTH_SHORT).show();
        }else{
            //si las comprobaciones salieron bien se ejecutara esta parte del codigo y se registrara el usuario, pero primero
            // se revisara que no se este repitiendo el usuario.
            registrarUsuario();
        }
    }

    private boolean comprobacionNombre(){
        String nombre = et_Nombre.getText().toString().trim();
        boolean bandera=false;
        if (nombre.equals("")){
            et_Nombre.setError("Dato Requerido");
        }else if (nombre.length()>20){
            et_Nombre.setError("No puedes Poner mas de 20 caracteres.");
        }else {
            u.setNombre(nombre);
            bandera=true;
        }
        return bandera;
    }

    private boolean comprobacionApellido(){
        String apellido = et_Apellido.getText().toString().trim();
        boolean bandera=false;
        if (apellido.equals("")){
            et_Apellido.setError("Dato Requerido");
        }else if (apellido.length()>35){
            et_Apellido.setError("No puedes Poner mas de 35 caracteres.");
        }else {
            u.setApellido(apellido);
            bandera=true;
        }
        return bandera;
    }

    private boolean comprobacionCorreo(){
        // El email a validar
        String correo = et_Correo.getText().toString().trim();
        // Patrón para validar el email
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        //busca nuestro email en base a la expresion que selecu¡cionamos del pattern en caso de que sea correcta nos retorna un valor true
        Matcher mather = pattern.matcher(correo);
        boolean bandera=false;
        if (correo.equals("")){
            et_Correo.setError("Dato Requerido");
        }else if (mather.find() == false){
            et_Correo.setError("Correo Invalido, Porfavor escribe un correo real.");
        }else {
            u.setCorreo(correo);
            bandera=true;
        }
        return bandera;
    }

    private boolean comprobacionContraseña(){
        String contraseña = et_Contraseña.getText().toString().trim();
        boolean bandera=false;
        if (contraseña.equals("")){
            et_Contraseña.setError("Dato Requerido");
        }else {
            u.setContraseña(contraseña);
            bandera=true;
        }
        return bandera;
    }

    private boolean comprobacionContraseñaConfirmacion(){
        String contraseñaConfirmacion = et_ContraseñaConfirmacion.getText().toString().trim();
        String contraseña = et_Contraseña.getText().toString().trim();
        boolean bandera=false;
        if (contraseñaConfirmacion.equals("")){
            et_ContraseñaConfirmacion.setError("Dato Requerido");
        }else if (contraseñaConfirmacion.equals(contraseña)==false){
            et_ContraseñaConfirmacion.setError("Las contraseñas no coinciden.");
        }else {
            bandera=true;
        }
        return bandera;
    }

    private boolean comprobacionContactoConfianza(){
        String ContactoConfianza = et_ContactoConfianza.getText().toString().trim();
        // Patrón para validar el email
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        //busca nuestro email en base a la expresion que selecu¡cionamos del pattern en caso de que sea correcta nos retorna un valor true
        Matcher mather = pattern.matcher(ContactoConfianza);
        boolean bandera=false;
        if (ContactoConfianza.equals("")){
            et_ContactoConfianza.setError("Dato Requerido, A este Contacto se le avisara cuando aborde su ruta");
        }else if (mather.find() == false){
            et_ContactoConfianza.setError("Correo Invalido, Porfavor escribe un correo real.");
        }else {
            u.setContactoConfianza(ContactoConfianza);
            bandera=true;
        }
        return bandera;
    }

    private void registrarUsuario(){
        String correo = et_Correo.getText().toString().trim();
        String contraseña = et_Contraseña.getText().toString().trim();

        progressDialog.setMessage("Realizando registro en linea...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //creating a new user
        mAuth.createUserWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            String id= mAuth.getCurrentUser().getUid();
                            Map<String, Object> datosUsuario = new HashMap<>();
                            datosUsuario.put("nombre",u.getNombre());
                            datosUsuario.put("iduser",id);
                            datosUsuario.put("apellido",u.getApellido());
                            datosUsuario.put("correo",u.getCorreo());
                            datosUsuario.put("contraseña",u.getContraseña());
                            datosUsuario.put("contactoConfianza",u.getContactoConfianza());
                            //Mandar datos del map string a la base de datos
                            OTBReference.child("Users").child(id).setValue(datosUsuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task2) {
                                    if (task2.isSuccessful()){
                                        String id2= mAuth.getCurrentUser().getUid();
                                        Map<String, Object> RutasPredeterminadas = new HashMap<>();
                                        for (int i3=0;i3<3;i3++){
                                            if (i3==0 && u.getRMF1()!=null){
                                                RutasPredeterminadas.put("RMF1",u.getRMF1());
                                            }
                                            if (i3==1 && u.getRMF2()!=null){
                                                RutasPredeterminadas.put("RMF2",u.getRMF2());
                                            }
                                            if (i3==2 && u.getRMF3()!=null){
                                                RutasPredeterminadas.put("RMF3",u.getRMF3());
                                            }
                                                       }
                                        OTBReference.child("Users").child(id2).child("RutaMasFrecuentada").setValue(RutasPredeterminadas).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task3) {
                                                if (task3.isSuccessful()){
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    user.sendEmailVerification();
                                                    limpiarCaja();
                                                    Toast.makeText(getActivity(),"Se registro el Correo:  "+u.getCorreo()+" Ve a tu email, a Verificarlo e Inicia Sesion.",Toast.LENGTH_LONG).show();
                                                    //se cierra la sesion para que el usuario se loguee
                                                    mAuth.signOut();
                                                    //se llama a la clase a la cual cambiaremos y se crea un objetode  tipo bundle ya que desde aqui haremos la descarga de rutas a seleccionar al registrarse
                                                    InicioDeSesion fragmento = new InicioDeSesion();
                                                    //Se aactiva la clase para la transicion de fragmento
                                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                                    //se hace la trancisionde fragmento remplazando el fragmento en la actividad principal y se hace commit.
                                                    transaction.replace(R.id.contenedor_main_activity,fragmento);
                                                    transaction.commit();
                                                }else{
                                                    Toast.makeText(getActivity(),"No se pudieron agregar las rutas predeterminadas",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else {
                                        Toast.makeText(getActivity(),"No se pudieron crear los datos correctamente",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else{
                            //si el usuario ya esta rgistrado
                            if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                et_Correo.setError("Correo Ya registrado, ingresar uno diferente");
                                Toast.makeText(getActivity(),"Error, Este correo ya esta registrado, favor de Registrarse con un nuevo correo, o iniciar sesion.",Toast.LENGTH_LONG).show();
                            }else if (task.getException() instanceof FirebaseAuthWeakPasswordException){
                                et_Contraseña.setError("Contrasña Insegura, Porfavor Incluya Almenos 8 caracteres, con 3 Numeros como minimo.");
                                Toast.makeText(getActivity(),"Tu contraseña es demasiado insegura porfavor asegurese de que incluya almenos 8 caracteres, con 3 numeros como minimoo.",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getActivity(),"Algo Salio mal, verifique su conexion a Internet",Toast.LENGTH_LONG).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void limpiarCaja(){
        et_Nombre.setText("");
        et_Apellido.setText("");
        et_Correo.setText("");
        et_Contraseña.setText("");
        et_ContraseñaConfirmacion.setText("");
        et_ContactoConfianza.setText("");
        actv_RMF.setText("");
    }
}
