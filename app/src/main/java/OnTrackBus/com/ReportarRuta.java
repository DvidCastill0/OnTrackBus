package OnTrackBus.com;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Repo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReportarRuta extends AppCompatActivity {

    private DatabaseReference OTBReference;

    //variables de xml
    private Spinner spinner_tipos_reporte;
    private ListView lista_subtipos;
    private Button btn_enviarReporte;
    private Button btn_Cancelar;

    //medidas de ventana emergente
    int ancho;
    int alto;

    //que item del spinner se selecciono
    private String reporte_seleccionado;
    private String NombreParada=null;
    private String CanalNumero=null;

    private String [] tipos_reporte =  {"Desvio", "Estancanmiento", "No dan paradas", "Mal servicio", "Violencia" };
    private ArrayList<String> subtipos_reporte = new ArrayList<String>();
    private ArrayList<String> id_Reportes = new ArrayList<>();

    private String correoDelUsuario=null;

    private Bundle bundleObtenerDatosReportar;


    private String idReporteSeleccioanadoListView;
    private String fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportar_ruta);

        OTBReference = FirebaseDatabase.getInstance().getReference();

        bundleObtenerDatosReportar = getIntent().getExtras();
        NombreParada = bundleObtenerDatosReportar.getString("NombreParada");
        CanalNumero = bundleObtenerDatosReportar.getString("CanalNumero");
        correoDelUsuario = bundleObtenerDatosReportar.getString("correoDelUsuario");
        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);
        ancho = medidasVentana.widthPixels;
        alto = medidasVentana.heightPixels;
        getWindow().setLayout((int)(ancho + 0.85),(int)(alto * 0.65));

        btn_Cancelar = this.findViewById(R.id.btn_CancelarEnviarReporte);
        btn_Cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        spinner_tipos_reporte = findViewById(R.id.spinner_reportes);
        lista_subtipos = findViewById(R.id.lv_de_tipos);
        btn_enviarReporte = findViewById(R.id.boton_envio_reporte);

        ArrayAdapter<String> adaptador_spinner = new ArrayAdapter<String>(this, R.layout.spinner_letras_style, tipos_reporte);
        spinner_tipos_reporte.setAdapter(adaptador_spinner);

        lista_subtipos.setVisibility(View.GONE);

        spinner_tipos_reporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                id_Reportes.clear();
                subtipos_reporte.clear();

                reporte_seleccionado = spinner_tipos_reporte.getSelectedItem().toString();

                if( !( reporte_seleccionado.equals(tipos_reporte[1]) ) && !( reporte_seleccionado.equals(tipos_reporte[2]) ) ) {
                    getWindow().setLayout((int)(ancho + 0.85),(int)(alto * 0.65));

                    lista_subtipos.setVisibility(View.VISIBLE);

                    switch (reporte_seleccionado) {

                        case "Desvio":

                            subtipos_reporte.add("Choque");
                            subtipos_reporte.add("Calle cerrada");

                            id_Reportes.add("RP-00-00");
                            id_Reportes.add("RP-00-01");
                            break;

                        case "Mal servicio":

                            subtipos_reporte.add("Condición del vehículo");
                            subtipos_reporte.add("Aseo");
                            subtipos_reporte.add("Actitud del chofer");

                            id_Reportes.add("RP-03-00");
                            id_Reportes.add("RP-03-01");
                            id_Reportes.add("RP-03-02");
                            break;

                        case "Violencia":

                            subtipos_reporte.add("Asalto");
                            subtipos_reporte.add("Pelea");

                            id_Reportes.add("RP-04-00");
                            id_Reportes.add("RP-04-01");

                            break;
                    }

                    ArrayAdapter<String> adaptador_lista = new ArrayAdapter<>(getApplicationContext(), R.layout.list_view_tv_letras_style_negro, subtipos_reporte);
                    lista_subtipos.setAdapter(adaptador_lista);

                    lista_subtipos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                            view.setSelected(true);
                            idReporteSeleccioanadoListView = id_Reportes.get(position);

                            btn_enviarReporte.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    //objeto que busca el formato de la fecha a obtener
                                    SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                                            "HH:mm:ss d-MM-yyyy");

                                    //se obtiene la fecha y hora actual
                                    Date fechaDate = new Date();
                                    fecha = formateadorfecha.format(fechaDate);

                                    if (idReporteSeleccioanadoListView.equals("RP-03-00")||idReporteSeleccioanadoListView.equals("RP-03-01")||idReporteSeleccioanadoListView.equals("RP-03-02")){
                                        final AlertDialog.Builder alertNumeroDeUnidad = new AlertDialog.Builder(ReportarRuta.this);
                                        alertNumeroDeUnidad.setTitle("Escriba el numero de la unidad");

                                        final EditText edt_numeroDeUnidad = new EditText(ReportarRuta.this);
                                        edt_numeroDeUnidad.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                        alertNumeroDeUnidad.setView(edt_numeroDeUnidad);

                                        alertNumeroDeUnidad.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String  numeroDeUnidadCamion = edt_numeroDeUnidad.getText().toString().trim();
                                                Map<String, Object> ReporteCanal = new HashMap<>();
                                                ReporteCanal.put("Id_ReporteCanal",idReporteSeleccioanadoListView);
                                                ReporteCanal.put("LugarDelReporte",NombreParada);
                                                ReporteCanal.put("Hora_DeReporteCanal",fecha);
                                                ReporteCanal.put("numeroDeUnidad",numeroDeUnidadCamion);
                                                ReporteCanal.put("correoDelUsuario",correoDelUsuario);
                                                // Generate a reference to a new location and add some data using push()
                                                DatabaseReference pushedPostRef = OTBReference.child("Canales").child(CanalNumero).child("Reportes").push();
                                                String Id_Clave = pushedPostRef.getKey();
                                                ReporteCanal.put("Id_Clave",Id_Clave);
                                                OTBReference.child("Canales").child(CanalNumero).child("Reportes").child(Id_Clave).setValue(ReporteCanal);
                                                finish();

                                            }
                                        });

                                        alertNumeroDeUnidad.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        alertNumeroDeUnidad.show();


                                    }else {
                                        Map<String, Object> ReporteCanal = new HashMap<>();
                                        ReporteCanal.put("Id_ReporteCanal",idReporteSeleccioanadoListView);
                                        ReporteCanal.put("LugarDelReporte",NombreParada);
                                        ReporteCanal.put("Hora_DeReporteCanal",fecha);
                                        // Generate a reference to a new location and add some data using push()
                                        DatabaseReference pushedPostRef = OTBReference.child("Canales").child(CanalNumero).child("Reportes").push();
                                        String Id_Clave = pushedPostRef.getKey();
                                        ReporteCanal.put("Id_Clave",Id_Clave);
                                        OTBReference.child("Canales").child(CanalNumero).child("Reportes").child(Id_Clave).setValue(ReporteCanal);
                                        finish();
                                    }



                                }
                            });

                        }
                    });

                } else {
                    getWindow().setLayout((int)(ancho + 0.85),(int)(alto * 0.35));
                    lista_subtipos.setVisibility(View.GONE);

                    btn_enviarReporte.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String idReporteSinListView =null;

                            if (reporte_seleccionado.equals(tipos_reporte[1])){
                                idReporteSinListView = "RP-01";
                            }
                            if (reporte_seleccionado.equals(tipos_reporte[2])){
                                idReporteSinListView = "RP-02";
                            }

                            //objeto que busca el formato de la fecha a obtener
                            SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy");

                            //se obtiene la fecha y hora actual
                            Date fechaDate = new Date();
                            String fecha = formateadorfecha.format(fechaDate);

                            Map<String, Object> ReporteCanal = new HashMap<>();
                            ReporteCanal.put("Id_ReporteCanal",idReporteSinListView);
                            ReporteCanal.put("LugarDelReporte",NombreParada);
                            ReporteCanal.put("Hora_DeReporteCanal",fecha);
                            // Generate a reference to a new location and add some data using push()
                            DatabaseReference pushedPostRef = OTBReference.child("Canales").child(CanalNumero).child("Reportes").push();
                            String Id_Clave = pushedPostRef.getKey();
                            ReporteCanal.put("Id_Clave",Id_Clave);
                            OTBReference.child("Canales").child(CanalNumero).child("Reportes").push().setValue(ReporteCanal);
                            finish();



                        }
                    });

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }
}
