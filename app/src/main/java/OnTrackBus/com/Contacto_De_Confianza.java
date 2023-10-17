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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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


public class Contacto_De_Confianza extends Fragment {


    //Instanciamiento de los componentes visuales
    private ListView lista_reportes;
    private TextView texto_superior;
    private Button boton_ver;
    private Button boton_borrar;
    private Button boton_limpiar;

    //Instanciamiento de un objeto de tipo reporte para mostrar datos detallados
    private Reporte_Contacto_De_Confianza reporte;

    //Referenciado al apartado de contacto de confianza de la BD
    private DatabaseReference OTBdatabase;
    private FirebaseAuth mAuth;

    //Creación de 2 ArrayList, uno sencillo para el apartado visual y otro para el lógico
    private ArrayList<Reporte_Contacto_De_Confianza> array_reportes = new ArrayList<Reporte_Contacto_De_Confianza>();
    private ArrayList<String> array_sencillo_reportes = new ArrayList<String>();


    public Contacto_De_Confianza() {
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
        View View_ContactoDeConfianza = inflater.inflate(R.layout.fragment_contacto__de__confianza, container, false);


        mAuth = FirebaseAuth.getInstance();
        String idusuario = mAuth.getCurrentUser().getUid();

        //Referenciado al apartado de contacto de confianza de la BD
        OTBdatabase = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(idusuario)
                .child("ContactoDeConfianza");


        //Inclusión de los componentes visuales
        texto_superior = View_ContactoDeConfianza.findViewById(R.id.tv_superior);
        lista_reportes = View_ContactoDeConfianza.findViewById(R.id.lv_reportesMiActividad);

        boton_ver = View_ContactoDeConfianza.findViewById(R.id.btn_ver);
        boton_borrar = View_ContactoDeConfianza.findViewById(R.id.btn_borrar);
        boton_limpiar = View_ContactoDeConfianza.findViewById(R.id.btn_limpiar);


        /*A continuación se hace la descarga de los reportes de la base de datos
         una sola vez*/

        OTBdatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {



                    // 13 - 05 - 2020 // Limpiado de los ArrayList que almacenan los reportes lógica y visualmente

                    array_reportes.clear();
                    array_sencillo_reportes.clear();


                //Ciclo de descarga para los reportes de la BD
                int contadorreportes=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    reporte = snapshot.getValue(Reporte_Contacto_De_Confianza.class);
                    array_reportes.add(reporte);
                    contadorreportes++;

                }

                //Ciclo de asignación de la fecha y hora de los reportes

                for (int numero_reporte = 0; numero_reporte < contadorreportes; numero_reporte++) {
                    SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                            "HH:mm:ss d-MM-yyyy");

                    // SimpleDateFormat nuevoFormato = new SimpleDateFormat("hh:mm:ssa EEEE d 'de' MMMM 'de' yyyy");
                    SimpleDateFormat nuevoFormato = new SimpleDateFormat("hh:mm:ss a EEEE d-MMM-yyyy");
                    Date fechadate = null;
                    String fechaantigua = array_reportes.get(numero_reporte).HoraAbordado + " " + array_reportes.get(numero_reporte).FechaAbordado;
                    try {
                        fechadate = formateadorfecha.parse(fechaantigua);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String fechaDia = nuevoFormato.format(fechadate);
                    array_sencillo_reportes.add(array_reportes.get(numero_reporte).NombreRemitente + "\n" + fechaDia);
                }

                //Ciclo de ordenamiento en base a las cadenas de caracteres

                boolean cambios = false;
                Reporte_Contacto_De_Confianza reporte_auxiliar = new Reporte_Contacto_De_Confianza();
                String string_auxiliar = null;

                while (true) {

                    cambios = false;

                    for (int i = 1; i < array_sencillo_reportes.size(); i++) {

                        if (array_sencillo_reportes.get(i).compareTo(array_sencillo_reportes.get(i - 1)) > 0) {

                            reporte_auxiliar = array_reportes.get(i);
                            string_auxiliar = array_sencillo_reportes.get(i);

                            array_reportes.set(i, array_reportes.get(i - 1));
                            array_sencillo_reportes.set(i, array_sencillo_reportes.get(i - 1));

                            array_reportes.set(i - 1, reporte_auxiliar);
                            array_sencillo_reportes.set(i - 1, string_auxiliar);

                            cambios = true;

                        }

                    }

                    if (cambios == false) {
                        break;
                    }
                }

                ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getActivity(), R.layout.list_view_tv_letras_style, array_sencillo_reportes);

                lista_reportes.setAdapter(adaptador);

                lista_reportes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        view.setSelected(true);

                        boton_ver.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Bundle pasador = new Bundle();

                                pasador.putString("Hora", array_reportes.get(position).HoraAbordado);
                                pasador.putString("Fecha", array_reportes.get(position).FechaAbordado);
                                pasador.putString("Correo", array_reportes.get(position).CorreoRemitente);
                                pasador.putString("Nombre", array_reportes.get(position).NombreRemitente);
                                pasador.putString("Parada", array_reportes.get(position).ParadaAbordada);
                                pasador.putString("Ruta", array_reportes.get(position).RutaAbordada);

                                Intent ver_reporte = new Intent(getActivity(), Ventana_Emergente_Contacto_De_Confianza_Reporte.class);
                                ver_reporte.putExtras(pasador);
                                startActivity(ver_reporte);

                            }
                        });


                        //Método onclick para eliminar un reporte de la lista en firebase
                        boton_borrar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                int id_a_borrar = array_reportes.get(position).Id_Reporte;

                                OTBdatabase.child("Reporte" + id_a_borrar).removeValue();

                            }
                        });


                    }
                });

                    //Método onclick para limpiar completamente el nodo de reportes
                    boton_limpiar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            OTBdatabase.removeValue();

                        }
                    });

                    //si no hay datos en la referencia no realizar nada para evitr crashear
                //Mientras no salgas de la siguiente llave ta bien
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });





        return View_ContactoDeConfianza;
    }
}
