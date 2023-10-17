package OnTrackBus.com;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class CanalesDeRutas extends Fragment {

    private DatabaseReference OTbReference;
    private FirebaseAuth mAuth;

    private Bundle bundleRecuperarCanalesDeRutas;

    //arraylist para autocomplete text view
    private AutoCompleteTextView actv_Buscar_Canales_De_Ruta;
    private Button btn_BuscarCanal;
    private ArrayList<String> rutasNombresCanales = new ArrayList<>();

    //variables de botones
    private Button btn_CanalesEntrar;
    private Button btn_CanalesBorrar;

    //list view variables
    private ListView lv_CanalesDeRutas;
    private ArrayList<String> CanalesSeleccionados = new ArrayList<>();
    private ArrayList<String> ContadorCanalesSeleccionados = new ArrayList<>();

    private int nombresCanales;

    //objeto para obtener hijos de canales de rutas
    private datosCanal oCanales;
    private ArrayList<datosCanal> array_reportesCanales = new ArrayList<datosCanal>();

    public CanalesDeRutas() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundleRecuperarCanalesDeRutas = getArguments();
        nombresCanales = bundleRecuperarCanalesDeRutas.getInt("CantidadDeRutas");
        for (int contadorCanales=1;contadorCanales<nombresCanales;contadorCanales++){
            rutasNombresCanales.add(bundleRecuperarCanalesDeRutas.getString("Ruta"+contadorCanales));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View Vista_CanalesDeRutas = inflater.inflate(R.layout.fragment_canales_de_rutas, container, false);

        OTbReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //instancear variables
        actv_Buscar_Canales_De_Ruta = Vista_CanalesDeRutas.findViewById(R.id.actv_BuscarCanalDeRuta);
        btn_BuscarCanal = Vista_CanalesDeRutas.findViewById(R.id.btn_BuscarCanalDeRuta);
        btn_CanalesEntrar = Vista_CanalesDeRutas.findViewById(R.id.btn_AbrirCanal);
        btn_CanalesBorrar = Vista_CanalesDeRutas.findViewById(R.id.btn_BorrarCanal);
        lv_CanalesDeRutas = Vista_CanalesDeRutas.findViewById(R.id.lv_CanalesDeRutas);

        //array adapter para autocompletetextview
        ArrayAdapter<String> adapterrutasNombres = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,rutasNombresCanales);
        actv_Buscar_Canales_De_Ruta.setAdapter(adapterrutasNombres);



        funcionBuscarCanal();




        OTbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     CanalesSeleccionados.clear();
                     array_reportesCanales.clear();
                     ContadorCanalesSeleccionados.clear();


                     String usuario = mAuth.getCurrentUser().getUid();

                     int contadorArray=0;
                     for (DataSnapshot snapshot : dataSnapshot.child("Users").child(usuario).child("CanalesSeleccionados").getChildren()){

                             oCanales = snapshot.getValue(datosCanal.class);
                             array_reportesCanales.add(oCanales);


                             String nombreCanalSeleccionado = array_reportesCanales.get(contadorArray).Nombre;
                             ContadorCanalesSeleccionados.add("CanalSeleccionado"+array_reportesCanales.get(contadorArray).Id_CanalSeleccionado);
                             int contadorCanales=1;
                             String TarifaDescarga = null;
                             for (DataSnapshot snapshotTarifas : dataSnapshot.child("Canales").getChildren()){
                                 String NombreTarifaCanal = dataSnapshot.child("Canales").child("Canal"+contadorCanales).child("NombreCanal").getValue().toString();
                                 if (NombreTarifaCanal.equals(nombreCanalSeleccionado)){
                                     TarifaDescarga = dataSnapshot.child("Canales").child("Canal"+contadorCanales).child("Tarifa").getValue().toString();
                                 }
                                 contadorCanales++;
                             }

                             DecimalFormat dardecimales = new DecimalFormat("#.00");
                             float tarifaFloat = Float.parseFloat(TarifaDescarga);
                             CanalesSeleccionados.add(array_reportesCanales.get(contadorArray).Nombre+"                      Tarifa: $"+dardecimales.format(tarifaFloat));
                             contadorArray++;

                     }




                ArrayAdapter<String> adaptadorCanalesSeleccionados = new ArrayAdapter<String>(getActivity(), R.layout.list_view_tv_letras_style, CanalesSeleccionados);
                lv_CanalesDeRutas.setAdapter(adaptadorCanalesSeleccionados);

                lv_CanalesDeRutas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        view.setSelected(true);

                        btn_CanalesBorrar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String iduser = mAuth.getCurrentUser().getUid();
                                OTbReference.child("Users").child(iduser).child("CanalesSeleccionados").child("CanalSeleccionado"+array_reportesCanales.get(position).Id_CanalSeleccionado).removeValue();
                            }
                        });


                        btn_CanalesEntrar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent ver_Canal = new Intent(getActivity(), verInformacionDeCanal.class);

                                Bundle pasadorAbrirCanal = new Bundle();
                                pasadorAbrirCanal.putString("CanalNombre",array_reportesCanales.get(position).Nombre);

                                int menosuno= nombresCanales-1;
                                for (int contadornumeroCanal=0;contadornumeroCanal<menosuno;contadornumeroCanal++){
                                    if (rutasNombresCanales.get(contadornumeroCanal).equals(array_reportesCanales.get(position).Nombre)){
                                        int numero = contadornumeroCanal+1;
                                        pasadorAbrirCanal.putInt("CanalNumero",numero);
                                    }
                                }
                                ver_Canal.putExtras(pasadorAbrirCanal);
                                actv_Buscar_Canales_De_Ruta.setText("");
                                startActivity(ver_Canal);
                            }
                        });

                    }

                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return Vista_CanalesDeRutas;
    }

    private void funcionBuscarCanal(){
        btn_BuscarCanal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String busquedaACTV = actv_Buscar_Canales_De_Ruta.getText().toString().trim();
                int bandera=0;
                int menosuno=nombresCanales-1,idcontador=0;
                for (int contadorBusqueda=0;contadorBusqueda<menosuno;contadorBusqueda++){
                    String  nombreBusqueda = rutasNombresCanales.get(contadorBusqueda);
                    if (nombreBusqueda.equals(busquedaACTV)){
                        bandera=1;
                        idcontador=contadorBusqueda+1;
                    }
                }

                if (bandera==1){

                    Intent ver_Canal = new Intent(getActivity(), Ventana_Emergente_EntrarACanal.class);

                    Bundle pasadorVentanaEmergenteCanal = new Bundle();
                    pasadorVentanaEmergenteCanal.putString("CanalNombre",busquedaACTV);
                    int menosuno2= nombresCanales-1;
                    for (int contadornumeroCanal=0;contadornumeroCanal<menosuno2;contadornumeroCanal++){
                        if (rutasNombresCanales.get(contadornumeroCanal).equals(busquedaACTV)){
                            int numero2 = contadornumeroCanal+1;
                            pasadorVentanaEmergenteCanal.putInt("CanalNumero",numero2);
                        }
                    }

                    ver_Canal.putExtras(pasadorVentanaEmergenteCanal);
                    actv_Buscar_Canales_De_Ruta.setText("");
                    startActivity(ver_Canal);
                }else if (busquedaACTV.equals("")){
                    actv_Buscar_Canales_De_Ruta.setError("Porfavor ingrese un Nombre de Ruta");
                    Toast.makeText(getActivity(),"Porfavor ingrese un Nombre de Ruta",Toast.LENGTH_SHORT).show();
                }else {
                    actv_Buscar_Canales_De_Ruta.setError("Porfavor ingrese un Nombre de Ruta Valido");
                    Toast.makeText(getActivity(),"Porfavor ingrese un Nombre de Ruta Valido",Toast.LENGTH_SHORT).show();
                }


            }
        });

    }




}
