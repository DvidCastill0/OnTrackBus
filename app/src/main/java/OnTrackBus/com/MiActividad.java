package OnTrackBus.com;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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


public class MiActividad extends Fragment {

    private Spinner spinner_dias;
    private ListView lista_reportes;
    private TextView texto_pruebas;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private DatabaseReference OTBdatabase = FirebaseDatabase.getInstance().getReference().child("Users")
            .child(mAuth.getCurrentUser().getUid())
            .child("MiActividad");


    datos_ReportesMiActividad reporte;

    ArrayList<datos_ReportesMiActividad> array_reportes = new ArrayList<datos_ReportesMiActividad>();
    ArrayList<String> array_sencillo_reportes = new ArrayList<String>();
    String[] dias_semana = {"lunes", "martes", "miercoles", "jueves", "viernes", "sabado", "domingo"};
    ArrayList<String> array_Dias = new ArrayList<String>();



    public MiActividad() {
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
        View vistaMiActividad = inflater.inflate(R.layout.fragment_mi_actividad, container, false);

        //objeto que busca el formato de la fecha a obtener
        SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                "EEEE");

        //se obtiene la fecha y hora actual
        Date fechaDate = new Date();
        final String ObtenerDia = formateadorfecha.format(fechaDate);

        array_Dias.add(ObtenerDia);
        for (int contadorDias=0; contadorDias<7;contadorDias++){
            if (dias_semana[contadorDias].equals(ObtenerDia)!=true){
                array_Dias.add(dias_semana[contadorDias]);
            }
        }



        spinner_dias = vistaMiActividad.findViewById(R.id.spinner_seleccion);
        lista_reportes = vistaMiActividad.findViewById(R.id.lv_reportesMiActividad);
        texto_pruebas = vistaMiActividad.findViewById(R.id.tv_pruebas);

        final ArrayAdapter<String> adaptador_dias = new ArrayAdapter<String>(getActivity(), R.layout.spinner_letras_style, array_Dias);
        spinner_dias.setAdapter(adaptador_dias);

        //Método que se ejecuta dependiendo de la selección del spinner
        spinner_dias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                final String dia_seleccionado = spinner_dias.getSelectedItem().toString();

                //Método de firebase
                OTBdatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        array_reportes.clear();
                        array_sencillo_reportes.clear();

                        for (DataSnapshot snapshot : dataSnapshot.child(dia_seleccionado).getChildren()){

                            reporte = snapshot.getValue(datos_ReportesMiActividad.class);
                            array_reportes.add(reporte);

                        }

                        //Asignación de los reportes al array visual
                        for (int numero_reporte = 0 ; numero_reporte < array_reportes.size() ; numero_reporte++){

                            SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy");

                            // SimpleDateFormat nuevoFormato = new SimpleDateFormat("hh:mm:ssa EEEE d 'de' MMMM 'de' yyyy");
                            SimpleDateFormat nuevoFormatoHora = new SimpleDateFormat("hh:mm:ss a");
                            SimpleDateFormat nuevoFormatoFecha = new SimpleDateFormat("EEEE d-MMM-yyyy");
                            Date fechadate = null;
                            String fechaantigua = array_reportes.get(numero_reporte).HoraAbordado+ " " +array_reportes.get(numero_reporte).FechaAbordado;
                            try {
                                fechadate = formateadorfecha.parse(fechaantigua);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String fechadia = nuevoFormatoFecha.format(fechadate);
                            String fechahora = nuevoFormatoHora.format(fechadate);

                            array_sencillo_reportes.add( fechadia+ "\n" +
                                    fechahora+ "\n" + "Parada: " +
                                    array_reportes.get(numero_reporte).ParadaAbordada + "       " +
                                    array_reportes.get(numero_reporte).RutaAbordada);

                        }



                        //Ordenamiento de los reportes para su muestra
                        boolean cambios = false;
                        datos_ReportesMiActividad reporte_auxiliar = new datos_ReportesMiActividad();
                        String string_auxiliar = null;

                        while(true){

                            cambios = false;

                            for(int i = 1; i < array_sencillo_reportes.size() ; i++) {

                                if(array_sencillo_reportes.get(i).compareTo(array_sencillo_reportes.get(i-1)) > 0 ){

                                    reporte_auxiliar = array_reportes.get(i);
                                    string_auxiliar = array_sencillo_reportes.get(i);

                                    array_reportes.set(i, array_reportes.get(i-1));
                                    array_sencillo_reportes.set(i, array_sencillo_reportes.get(i-1));

                                    array_reportes.set(i - 1, reporte_auxiliar);
                                    array_sencillo_reportes.set(i - 1, string_auxiliar);

                                    cambios = true;

                                }

                            }

                            if(cambios == false) {
                                break;
                            }
                        }


                        ArrayAdapter<String> adaptador_reportes = new ArrayAdapter<String>(getActivity(), R.layout.list_view_azulcanales, array_sencillo_reportes);
                        lista_reportes.setAdapter(adaptador_reportes);


                        //Aquí termina onDataChange
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                    //Aquí termina el método de firebase
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });







        return vistaMiActividad;
    }
}
