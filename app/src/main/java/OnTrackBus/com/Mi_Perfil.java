package OnTrackBus.com;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Mi_Perfil extends Fragment {

    //instancear base de datos y autenticacion
    private DatabaseReference OTBReference;
    private FirebaseAuth mAuth;

    //variables de xml
    private EditText edt_ActualizarNombre;
    private EditText edt_ActualizarApellido;
    private EditText edt_ActualizarContactoConfianza;

    private AutoCompleteTextView actv_ActualizarRMF;


    //getter and setter para obtener informacion en el programa
    private Usuarios u = new Usuarios();


    public Mi_Perfil() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vistaMiPerfil = inflater.inflate(R.layout.fragment_mi__perfil, container, false);

        //instanear variables de firebase
        OTBReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        //instanceear variables de firebase
        edt_ActualizarNombre = vistaMiPerfil.findViewById(R.id.edt_ActualizarNombre);
        edt_ActualizarApellido = vistaMiPerfil.findViewById(R.id.edt_ActualizzarApellido);
        edt_ActualizarContactoConfianza = vistaMiPerfil.findViewById(R.id.edt_ActualizarContactoConfianza);

        actv_ActualizarRMF = vistaMiPerfil.findViewById(R.id.actv_ActualizarRutasPredeterminadas);

        return vistaMiPerfil;
    }


    //metodo donde se agregan rutas seleccionada spor le usuario.
    /*private void agregarRuta(){
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
                                  /*  for (int contadorarray = 0; contadorarray < 3; contadorarray++) {
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
                                          /*  actv_RMF.setText("");
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
    /*private void eliminarRuta(){
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
    }*/
    //una vez eliminada la ruta juntar hacia la iquierda
   /* private void acomodar(){

        //array que guardara las rutas para acomodarlas en el nuevo codigo
        String [] array_acomodar_rutas= new String[3];
        for (int contador=0; contador<3;contador++){
            if (array_rutas_seleccionadas[contador]!=null){
                /*Inicia un ciclo for que busca el primer espacio en blanco en el array de
                 * de las rutas seleccionadas*/
               /* for(int i3 = 0 ; i3 < 3 ; i3++) {

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
        String nombre = edt_ActualizarNombre.getText().toString().trim();
        boolean bandera=false;
        if (nombre.equals("")){
            edt_ActualizarNombre.setError("Dato Requerido");
        }else if (nombre.length()>20){
            edt_ActualizarNombre.setError("No puedes Poner mas de 20 caracteres.");
        }else {
            u.setNombre(nombre);
            bandera=true;
        }
        return bandera;
    }

    private boolean comprobacionApellido(){
        String apellido = edt_ActualizarApellido.getText().toString().trim();
        boolean bandera=false;
        if (apellido.equals("")){
            edt_ActualizarApellido.setError("Dato Requerido");
        }else if (apellido.length()>35){
            edt_ActualizarApellido.setError("No puedes Poner mas de 35 caracteres.");
        }else {
            u.setApellido(apellido);
            bandera=true;
        }
        return bandera;
    }


    private boolean comprobacionContactoConfianza(){
        String ContactoConfianza = edt_ActualizarContactoConfianza.getText().toString().trim();
        // Patrón para validar el email
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        //busca nuestro email en base a la expresion que selecu¡cionamos del pattern en caso de que sea correcta nos retorna un valor true
        Matcher mather = pattern.matcher(ContactoConfianza);
        boolean bandera=false;
        if (ContactoConfianza.equals("")){
            edt_ActualizarContactoConfianza.setError("Dato Requerido, A este Contacto se le avisara cuando aborde su ruta");
        }else if (mather.find() == false){
            edt_ActualizarContactoConfianza.setError("Correo Invalido, Porfavor escribe un correo real.");
        }else {
            u.setContactoConfianza(ContactoConfianza);
            bandera=true;
        }
        return bandera;
    }
*/



}
