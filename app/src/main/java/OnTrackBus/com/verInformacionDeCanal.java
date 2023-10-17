package OnTrackBus.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class verInformacionDeCanal extends AppCompatActivity {

    //variables firebase
    private DatabaseReference OTBReference;
    private FirebaseAuth mAuth;

    //bundle para recibir datos
    private Bundle bundleCanalAbierto;

    //instancear elementos graficos
    private TextView tv_Nombre;
    private TextView tv_Tarifa;
    private TextView tv_Reportes;
    private TextView tv_Sugerencias;

    private Button btn_Aceptar;

    private ListView lv_CanalAbierto;

    //datos importantes para pasar
    private String nombreCanal;
    private int numeroCanal;

    //variable para obtener la ultima localizacion conocida
    private FusedLocationProviderClient userFLPC;
    //variable apra ver la parada mas cercana a la que se hara el reporte
    //instanciar la clase rutas para poder mandar elnumero de la ruta en base al nombre
    private Rutas oRutasAbordado = new Rutas();
    private String nombreParada;

    //arraylist de reportes
    private ArrayList<datosCanal> arraylist_Choques = new ArrayList<>();
    private ArrayList<datosCanal> arraylist_CalleCerrada = new ArrayList<>();
    private ArrayList<datosCanal> arraylist_Estancamientos = new ArrayList<>();
    private ArrayList<datosCanal> arraylist_NoDaParadas = new ArrayList<>();
    private ArrayList<datosCanal> arraylist_MalaCondicionVehiculo = new ArrayList<>();
    private ArrayList<datosCanal> arraylist_MalAseo = new ArrayList<>();
    private ArrayList<datosCanal> arraylist_MalaActitudDelChofer = new ArrayList<>();
    private ArrayList<datosCanal> arraylist_Asaltos = new ArrayList<>();
    private ArrayList<datosCanal> arraylist_Peleas = new ArrayList<>();

    private ArrayList<String> listviewarray = new ArrayList<>();

    //private ArrayList<datosCanal> arraylist_EliminarBuscandoDiferentes= new ArrayList<>();
    private ArrayList<datosCanal>       DiferenciasParadaoFecha = new ArrayList<>();
    private ArrayList<Integer> ContadorDiferencias = new ArrayList<>();

    //obejtos para descrgar datos de hijos
    //objeto para obtener hijos de canales de rutas
    private datosCanal oCanales;
    private ArrayList<datosCanal> array_reportesCanales = new ArrayList<datosCanal>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_informacion_de_canal);

        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);
        int ancho = medidasVentana.widthPixels;
        int alto = medidasVentana.heightPixels;
        getWindow().setLayout((int)(ancho + 0.85),(int)(alto * 0.85));

        //recibir vlores de intent
        bundleCanalAbierto = getIntent().getExtras();
        nombreCanal = bundleCanalAbierto.getString("CanalNombre");
        numeroCanal = bundleCanalAbierto.getInt("CanalNumero");

        //instanciar variables graficas

        tv_Nombre = this.findViewById(R.id.tv_CanalAbierto);
        tv_Nombre.setText(nombreCanal);
        tv_Tarifa = this.findViewById(R.id.tv_TarifaCanalAbierto);
        tv_Reportes = this.findViewById(R.id.tv_Reportes);
        tv_Sugerencias = this.findViewById(R.id.tv_Sugerencias);

        tv_Sugerencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSugerencias = new Intent(verInformacionDeCanal.this, ventanaEmergenteSugerencias.class);
                //otra ruta bundle para pasar datos
                Bundle BundleSugerencias = new Bundle();
                BundleSugerencias.putString("CanalNumero","Canal"+numeroCanal);
                intentSugerencias.putExtras(BundleSugerencias);
                startActivity(intentSugerencias);
            }
        });

        btn_Aceptar = this.findViewById(R.id.btn_AceptarCanalAbierto);
        funcionalidad_btn_aceptar();
        lv_CanalAbierto = this.findViewById(R.id.lv_ReportesCanalesAbiertos);


        //instancear variables de firebase
        OTBReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        funcionalidadbtn_Hacer_Reporte();


        OTBReference.child("Canales").child("Canal"+numeroCanal).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DecimalFormat dardecimales = new DecimalFormat("#.00");
                float tarifaFloat = Float.parseFloat(dataSnapshot.child("Tarifa").getValue().toString());

                //dejar limpios todos lso arrays
                array_reportesCanales.clear();
                arraylist_Choques.clear();
                arraylist_CalleCerrada.clear();
                arraylist_Estancamientos.clear();
                arraylist_NoDaParadas.clear();
                arraylist_MalaCondicionVehiculo.clear();
                arraylist_MalAseo.clear();
                arraylist_MalaActitudDelChofer.clear();
                arraylist_Asaltos.clear();
                arraylist_Peleas.clear();


                tv_Tarifa.setText("Tarifa: $"+dardecimales.format(tarifaFloat));
                listviewarray.clear();

                for(DataSnapshot snapshotReportes : dataSnapshot.child("Reportes").getChildren()){
                    oCanales = snapshotReportes.getValue(datosCanal.class);
                    array_reportesCanales.add(oCanales);

                }



                for(int contadorReportesCanales=0; contadorReportesCanales<array_reportesCanales.size();contadorReportesCanales++){
                    //aqui meter tod0 dentro de un if para que si tienen mas d eun dia se eliminen


                    //reportes choques
                    if (array_reportesCanales.get(contadorReportesCanales).Id_ReporteCanal.equals("RP-00-00")){
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
                            fechaMarcador= formateadorfecha.parse(array_reportesCanales.get(contadorReportesCanales).Hora_DeReporteCanal);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long diferenciaEn_ms = fechaactual.getTime() - fechaMarcador.getTime();
                        long Horas = diferenciaEn_ms/3600000;

                        if (Horas>2){
                            String idclave = array_reportesCanales.get(contadorReportesCanales).Id_Clave;
                            OTBReference.child("Canales").child("Canal"+numeroCanal).child("Reportes").child(idclave).removeValue();
                        }else {
                            arraylist_Choques.add(array_reportesCanales.get(contadorReportesCanales));
                        }



                    }
                    //reportes Calle cerrada
                    if (array_reportesCanales.get(contadorReportesCanales).Id_ReporteCanal.equals("RP-00-01")){

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
                            fechaMarcador= formateadorfecha.parse(array_reportesCanales.get(contadorReportesCanales).Hora_DeReporteCanal);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long diferenciaEn_ms = fechaactual.getTime() - fechaMarcador.getTime();
                        long Horas = diferenciaEn_ms/3600000;

                        if (Horas>4){
                            String idclave = array_reportesCanales.get(contadorReportesCanales).Id_Clave;
                            OTBReference.child("Canales").child("Canal"+numeroCanal).child("Reportes").child(idclave).removeValue();
                        }else {
                            arraylist_CalleCerrada.add(array_reportesCanales.get(contadorReportesCanales));
                        }

                    }
                    //reportes Estancamientos
                    if (array_reportesCanales.get(contadorReportesCanales).Id_ReporteCanal.equals("RP-01")){

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
                            fechaMarcador= formateadorfecha.parse(array_reportesCanales.get(contadorReportesCanales).Hora_DeReporteCanal);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long diferenciaEn_ms = fechaactual.getTime() - fechaMarcador.getTime();
                        long Horas = diferenciaEn_ms/3600000;

                        if (Horas>2){
                            String idclave = array_reportesCanales.get(contadorReportesCanales).Id_Clave;
                            OTBReference.child("Canales").child("Canal"+numeroCanal).child("Reportes").child(idclave).removeValue();
                        }else {
                            arraylist_Estancamientos.add(array_reportesCanales.get(contadorReportesCanales));
                        }

                    }
                    //reportes -no da Parada
                    if (array_reportesCanales.get(contadorReportesCanales).Id_ReporteCanal.equals("RP-02")){

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
                            fechaMarcador= formateadorfecha.parse(array_reportesCanales.get(contadorReportesCanales).Hora_DeReporteCanal);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long diferenciaEn_ms = fechaactual.getTime() - fechaMarcador.getTime();
                        long Horas = diferenciaEn_ms/3600000;

                        if (Horas>1){
                            String idclave = array_reportesCanales.get(contadorReportesCanales).Id_Clave;
                            OTBReference.child("Canales").child("Canal"+numeroCanal).child("Reportes").child(idclave).removeValue();
                        }else {
                            arraylist_NoDaParadas.add(array_reportesCanales.get(contadorReportesCanales));
                        }


                    }
                    //reportes Mala condicion del vehiculo
                    if (array_reportesCanales.get(contadorReportesCanales).Id_ReporteCanal.equals("RP-03-00")){

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
                            fechaMarcador= formateadorfecha.parse(array_reportesCanales.get(contadorReportesCanales).Hora_DeReporteCanal);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long diferenciaEn_ms = fechaactual.getTime() - fechaMarcador.getTime();
                        long minutos = diferenciaEn_ms/60000;

                        if (minutos>30){
                            long horas = minutos/60;
                            long dias = horas/24;
                            if (dias>30) {
                                String idclave = array_reportesCanales.get(contadorReportesCanales).Id_Clave;
                                OTBReference.child("Canales").child("Canal" + numeroCanal).child("Reportes").child(idclave).removeValue();
                            }
                        }else {
                            arraylist_MalaCondicionVehiculo.add(array_reportesCanales.get(contadorReportesCanales));
                        }


                    }
                    //reportes Mal Aseo
                    if (array_reportesCanales.get(contadorReportesCanales).Id_ReporteCanal.equals("RP-03-01")){

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
                            fechaMarcador= formateadorfecha.parse(array_reportesCanales.get(contadorReportesCanales).Hora_DeReporteCanal);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long diferenciaEn_ms = fechaactual.getTime() - fechaMarcador.getTime();
                        long minutos = diferenciaEn_ms/60000;

                        if (minutos>30){
                            long horas = minutos/60;
                            long dias = horas/24;

                            if (dias>30) {
                                String idclave = array_reportesCanales.get(contadorReportesCanales).Id_Clave;
                                OTBReference.child("Canales").child("Canal" + numeroCanal).child("Reportes").child(idclave).removeValue();
                            }
                        }else {
                            arraylist_MalAseo.add(array_reportesCanales.get(contadorReportesCanales));
                        }

                    }
                    //reportes Mal Actitud de chofer
                    if (array_reportesCanales.get(contadorReportesCanales).Id_ReporteCanal.equals("RP-03-02")){

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
                            fechaMarcador= formateadorfecha.parse(array_reportesCanales.get(contadorReportesCanales).Hora_DeReporteCanal);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long diferenciaEn_ms = fechaactual.getTime() - fechaMarcador.getTime();
                        long minutos = diferenciaEn_ms/60000;

                        if (minutos>30){
                            long horas = minutos/60;
                            long dias = horas/24;

                            if (dias>30){
                                String idclave = array_reportesCanales.get(contadorReportesCanales).Id_Clave;
                                OTBReference.child("Canales").child("Canal"+numeroCanal).child("Reportes").child(idclave).removeValue();
                            }

                        }else {
                            arraylist_MalaActitudDelChofer.add(array_reportesCanales.get(contadorReportesCanales));
                        }


                    }
                    //reportes asaltoo
                    if (array_reportesCanales.get(contadorReportesCanales).Id_ReporteCanal.equals("RP-04-00")){

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
                            fechaMarcador= formateadorfecha.parse(array_reportesCanales.get(contadorReportesCanales).Hora_DeReporteCanal);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long diferenciaEn_ms = fechaactual.getTime() - fechaMarcador.getTime();
                        long Horas = diferenciaEn_ms/3600000;

                        if (Horas>2){
                            String idclave = array_reportesCanales.get(contadorReportesCanales).Id_Clave;
                            OTBReference.child("Canales").child("Canal"+numeroCanal).child("Reportes").child(idclave).removeValue();
                        }else {
                            arraylist_Asaltos.add(array_reportesCanales.get(contadorReportesCanales));
                        }

                    }
                    //reportes pelea
                    if (array_reportesCanales.get(contadorReportesCanales).Id_ReporteCanal.equals("RP-04-01")){

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
                            fechaMarcador= formateadorfecha.parse(array_reportesCanales.get(contadorReportesCanales).Hora_DeReporteCanal);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long diferenciaEn_ms = fechaactual.getTime() - fechaMarcador.getTime();
                        long Horas = diferenciaEn_ms/3600000;

                        if (Horas>2){
                            String idclave = array_reportesCanales.get(contadorReportesCanales).Id_Clave;
                            OTBReference.child("Canales").child("Canal"+numeroCanal).child("Reportes").child(idclave).removeValue();
                        }else {
                            arraylist_Peleas.add(array_reportesCanales.get(contadorReportesCanales));
                        }
                    }//findeif
                }//fin de for


                DiferenciasParadaoFecha.clear();
                ContadorDiferencias.clear();
                //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                if (arraylist_Choques.size()>=3){

                    for (int contaorParadasNombre=0;contaorParadasNombre<arraylist_Choques.size();contaorParadasNombre++){
                       String paradaLugar = arraylist_Choques.get(contaorParadasNombre).LugarDelReporte;
                       int banderayaseagrego=0;
                       for (int lugaresAgregar=0;lugaresAgregar<DiferenciasParadaoFecha.size();lugaresAgregar++){
                           if (paradaLugar.equals(DiferenciasParadaoFecha.get(lugaresAgregar).LugarDelReporte)){
                                   banderayaseagrego=1;
                           }
                       }
                       if (banderayaseagrego==0){
                           DiferenciasParadaoFecha.add(arraylist_Choques.get(contaorParadasNombre));
                           ContadorDiferencias.add(0);
                       }
                    }

                    //for que busca cuantas paradas coinciden con cada diferencia
                    for (int ParadasDiferencias=0;ParadasDiferencias<DiferenciasParadaoFecha.size();ParadasDiferencias++){
                        String paradAcomparar = DiferenciasParadaoFecha.get(ParadasDiferencias).LugarDelReporte;
                        for (int buscarTodo=0;buscarTodo<arraylist_Choques.size();buscarTodo++){
                            if (paradAcomparar.equals(arraylist_Choques.get(buscarTodo).LugarDelReporte)){
                                ContadorDiferencias.set(ParadasDiferencias,ContadorDiferencias.get(ParadasDiferencias)+1);
                            }
                        }
                    }


                    //iterar entre los que tuvieron la misma parada 3 veces

                    for (int contadorfech=0;contadorfech<DiferenciasParadaoFecha.size();contadorfech++){
                        if (ContadorDiferencias.get(contadorfech)>=3){
                            //objeto que busca el formato de la fecha a obtener
                            SimpleDateFormat nuevoformato = new SimpleDateFormat(
                                    "hh:mm:ss a");
                            SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy");

                            Date fechaMarcador=null;
                            try {
                                fechaMarcador= formateadorfecha.parse(DiferenciasParadaoFecha.get(contadorfech).Hora_DeReporteCanal);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            String hora = nuevoformato.format(fechaMarcador);

                            listviewarray.add("Se ha reportado un choque cerca de la parada "+DiferenciasParadaoFecha.get(contadorfech).LugarDelReporte+" a las "+hora+" toma tus precauciones.");
                        }
                    }//hasta aqui


                }


                //comprobcion Calle cerrada
                DiferenciasParadaoFecha.clear();
                ContadorDiferencias.clear();
                //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                if (arraylist_CalleCerrada.size()>=3){
                    for (int contaorParadasNombre=0;contaorParadasNombre<arraylist_CalleCerrada.size();contaorParadasNombre++){
                        String paradaLugar = arraylist_CalleCerrada.get(contaorParadasNombre).LugarDelReporte;
                        int banderayaseagrego=0;
                        for (int lugaresAgregar=0;lugaresAgregar<DiferenciasParadaoFecha.size();lugaresAgregar++){
                            if (paradaLugar.equals(DiferenciasParadaoFecha.get(lugaresAgregar).LugarDelReporte)){
                                banderayaseagrego=1;
                            }
                        }
                        if (banderayaseagrego==0){
                            DiferenciasParadaoFecha.add(arraylist_CalleCerrada.get(contaorParadasNombre));
                            ContadorDiferencias.add(0);
                        }
                    }

                    //for que busca cuantas paradas coinciden con cada diferencia
                    for (int ParadasDiferencias=0;ParadasDiferencias<DiferenciasParadaoFecha.size();ParadasDiferencias++){
                        String paradAcomparar = DiferenciasParadaoFecha.get(ParadasDiferencias).LugarDelReporte;
                        for (int buscarTodo=0;buscarTodo<arraylist_CalleCerrada.size();buscarTodo++){
                            if (paradAcomparar.equals(arraylist_CalleCerrada.get(buscarTodo).LugarDelReporte)){
                                ContadorDiferencias.set(ParadasDiferencias,ContadorDiferencias.get(ParadasDiferencias)+1);
                            }
                        }
                    }


                    //iterar entre los que tuvieron la misma parada 3 veces

                    for (int contadorfech=0;contadorfech<DiferenciasParadaoFecha.size();contadorfech++){
                        if (ContadorDiferencias.get(contadorfech)>=3){
                            //objeto que busca el formato de la fecha a obtener
                            SimpleDateFormat nuevoformato = new SimpleDateFormat(
                                    "hh:mm:ss a");
                            SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy");

                            Date fechaMarcador=null;
                            try {
                                fechaMarcador= formateadorfecha.parse(DiferenciasParadaoFecha.get(contadorfech).Hora_DeReporteCanal);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            String hora = nuevoformato.format(fechaMarcador);

                            listviewarray.add("Se ha reportado que hay una calle cerrada cerca de la parada "+DiferenciasParadaoFecha.get(contadorfech).LugarDelReporte+" a las "+hora+" toma tus precauciones.");
                        }
                    }//hasta aqui


                }


                //comprobacion Estancamientos
                DiferenciasParadaoFecha.clear();
                ContadorDiferencias.clear();
                //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                if (arraylist_Estancamientos.size()>=3){
                    for (int contaorParadasNombre=0;contaorParadasNombre<arraylist_Estancamientos.size();contaorParadasNombre++){
                        String paradaLugar = arraylist_Estancamientos.get(contaorParadasNombre).LugarDelReporte;
                        int banderayaseagrego=0;
                        for (int lugaresAgregar=0;lugaresAgregar<DiferenciasParadaoFecha.size();lugaresAgregar++){
                            if (paradaLugar.equals(DiferenciasParadaoFecha.get(lugaresAgregar).LugarDelReporte)){
                                banderayaseagrego=1;
                            }
                        }
                        if (banderayaseagrego==0){
                            DiferenciasParadaoFecha.add(arraylist_Estancamientos.get(contaorParadasNombre));
                            ContadorDiferencias.add(0);
                        }
                    }

                    //for que busca cuantas paradas coinciden con cada diferencia
                    for (int ParadasDiferencias=0;ParadasDiferencias<DiferenciasParadaoFecha.size();ParadasDiferencias++){
                        String paradAcomparar = DiferenciasParadaoFecha.get(ParadasDiferencias).LugarDelReporte;
                        for (int buscarTodo=0;buscarTodo<arraylist_Estancamientos.size();buscarTodo++){
                            if (paradAcomparar.equals(arraylist_Estancamientos.get(buscarTodo).LugarDelReporte)){
                                ContadorDiferencias.set(ParadasDiferencias,ContadorDiferencias.get(ParadasDiferencias)+1);
                            }
                        }
                    }


                    //iterar entre los que tuvieron la misma parada 3 veces

                    for (int contadorfech=0;contadorfech<DiferenciasParadaoFecha.size();contadorfech++){
                        if (ContadorDiferencias.get(contadorfech)>=3){
                            //objeto que busca el formato de la fecha a obtener
                            SimpleDateFormat nuevoformato = new SimpleDateFormat(
                                    "hh:mm:ss a");
                            SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy");

                            Date fechaMarcador=null;
                            try {
                                fechaMarcador= formateadorfecha.parse(DiferenciasParadaoFecha.get(contadorfech).Hora_DeReporteCanal);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            String hora = nuevoformato.format(fechaMarcador);

                            listviewarray.add("Se ha reportado un estancamiento cerca de la parada "+DiferenciasParadaoFecha.get(contadorfech).LugarDelReporte+" a las "+hora+" toma tus precauciones.");
                        }
                    }


                }//hasta aqui


                // comprobcion no estan dando paradas
                DiferenciasParadaoFecha.clear();
                ContadorDiferencias.clear();
                //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                if (arraylist_NoDaParadas.size()>=3){
                    for (int contaorParadasNombre=0;contaorParadasNombre<arraylist_NoDaParadas.size();contaorParadasNombre++){
                        String paradaLugar = arraylist_NoDaParadas.get(contaorParadasNombre).LugarDelReporte;
                        int banderayaseagrego=0;
                        for (int lugaresAgregar=0;lugaresAgregar<DiferenciasParadaoFecha.size();lugaresAgregar++){
                            if (paradaLugar.equals(DiferenciasParadaoFecha.get(lugaresAgregar).LugarDelReporte)){
                                banderayaseagrego=1;
                            }
                        }
                        if (banderayaseagrego==0){
                            DiferenciasParadaoFecha.add(arraylist_NoDaParadas.get(contaorParadasNombre));
                            ContadorDiferencias.add(0);
                        }
                    }

                    //for que busca cuantas paradas coinciden con cada diferencia
                    for (int ParadasDiferencias=0;ParadasDiferencias<DiferenciasParadaoFecha.size();ParadasDiferencias++){
                        String paradAcomparar = DiferenciasParadaoFecha.get(ParadasDiferencias).LugarDelReporte;
                        for (int buscarTodo=0;buscarTodo<arraylist_NoDaParadas.size();buscarTodo++){
                            if (paradAcomparar.equals(arraylist_NoDaParadas.get(buscarTodo).LugarDelReporte)){
                                ContadorDiferencias.set(ParadasDiferencias,ContadorDiferencias.get(ParadasDiferencias)+1);
                            }
                        }
                    }


                    //iterar entre los que tuvieron la misma parada 3 veces

                    for (int contadorfech=0;contadorfech<DiferenciasParadaoFecha.size();contadorfech++){
                        if (ContadorDiferencias.get(contadorfech)>=3){
                            //objeto que busca el formato de la fecha a obtener
                            SimpleDateFormat nuevoformato = new SimpleDateFormat(
                                    "hh:mm:ss a");
                            SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy");

                            Date fechaMarcador=null;
                            try {
                                fechaMarcador= formateadorfecha.parse(DiferenciasParadaoFecha.get(contadorfech).Hora_DeReporteCanal);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            String hora = nuevoformato.format(fechaMarcador);

                            listviewarray.add("Se reporto a las "+hora+" que el camion no se esta parando en la parada  "+DiferenciasParadaoFecha.get(contadorfech).LugarDelReporte+" toma tus precauciones.");
                        }
                    }


                }//hasta aqui


                //comprobacion Mala condicion del vehiculo

                DiferenciasParadaoFecha.clear();
                ContadorDiferencias.clear();
                //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                if (arraylist_MalaCondicionVehiculo.size()>=3){
                    for (int contaorParadasNombre=0;contaorParadasNombre<arraylist_MalaCondicionVehiculo.size();contaorParadasNombre++){
                        String paradaLugar = arraylist_MalaCondicionVehiculo.get(contaorParadasNombre).LugarDelReporte;
                        int banderayaseagrego=0;
                        for (int lugaresAgregar=0;lugaresAgregar<DiferenciasParadaoFecha.size();lugaresAgregar++){
                            if (paradaLugar.equals(DiferenciasParadaoFecha.get(lugaresAgregar).LugarDelReporte)){
                                banderayaseagrego=1;
                            }
                        }
                        if (banderayaseagrego==0){
                            DiferenciasParadaoFecha.add(arraylist_MalaCondicionVehiculo.get(contaorParadasNombre));
                            ContadorDiferencias.add(0);
                        }
                    }

                    //for que busca cuantas paradas coinciden con cada diferencia
                    for (int ParadasDiferencias=0;ParadasDiferencias<DiferenciasParadaoFecha.size();ParadasDiferencias++){
                        String paradAcomparar = DiferenciasParadaoFecha.get(ParadasDiferencias).LugarDelReporte;
                        for (int buscarTodo=0;buscarTodo<arraylist_MalaCondicionVehiculo.size();buscarTodo++){
                            if (paradAcomparar.equals(arraylist_MalaCondicionVehiculo.get(buscarTodo).LugarDelReporte)){
                                ContadorDiferencias.set(ParadasDiferencias,ContadorDiferencias.get(ParadasDiferencias)+1);
                            }
                        }
                    }


                    //iterar entre los que tuvieron la misma parada 3 veces

                    for (int contadorfech=0;contadorfech<DiferenciasParadaoFecha.size();contadorfech++){
                        if (ContadorDiferencias.get(contadorfech)>=3){
                            //objeto que busca el formato de la fecha a obtener
                            SimpleDateFormat nuevoformato = new SimpleDateFormat(
                                    "hh:mm:ss a");
                            SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy");

                            Date fechaMarcador=null;
                            try {
                                fechaMarcador= formateadorfecha.parse(DiferenciasParadaoFecha.get(contadorfech).Hora_DeReporteCanal);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            String hora = nuevoformato.format(fechaMarcador);

                            listviewarray.add("Se ha reportado una mala condicion de la unidad "+DiferenciasParadaoFecha.get(contadorfech).numeroDeUnidad+ "cerca de la parada "+DiferenciasParadaoFecha.get(contadorfech).LugarDelReporte+" a las "+hora+" toma tus precauciones.");
                        }
                    }


                }//hasta aqui


                //comprobacion Vehiculo mal aseado
                DiferenciasParadaoFecha.clear();
                ContadorDiferencias.clear();
                //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                if (arraylist_MalAseo.size()>=3){
                    for (int contaorParadasNombre=0;contaorParadasNombre<arraylist_MalAseo.size();contaorParadasNombre++){
                        String paradaLugar = arraylist_MalAseo.get(contaorParadasNombre).LugarDelReporte;
                        int banderayaseagrego=0;
                        for (int lugaresAgregar=0;lugaresAgregar<DiferenciasParadaoFecha.size();lugaresAgregar++){
                            if (paradaLugar.equals(DiferenciasParadaoFecha.get(lugaresAgregar).LugarDelReporte)){
                                banderayaseagrego=1;
                            }
                        }
                        if (banderayaseagrego==0){
                            DiferenciasParadaoFecha.add(arraylist_MalAseo.get(contaorParadasNombre));
                            ContadorDiferencias.add(0);
                        }
                    }

                    //for que busca cuantas paradas coinciden con cada diferencia
                    for (int ParadasDiferencias=0;ParadasDiferencias<DiferenciasParadaoFecha.size();ParadasDiferencias++){
                        String paradAcomparar = DiferenciasParadaoFecha.get(ParadasDiferencias).LugarDelReporte;
                        for (int buscarTodo=0;buscarTodo<arraylist_MalAseo.size();buscarTodo++){
                            if (paradAcomparar.equals(arraylist_MalAseo.get(buscarTodo).LugarDelReporte)){
                                ContadorDiferencias.set(ParadasDiferencias,ContadorDiferencias.get(ParadasDiferencias)+1);
                            }
                        }
                    }


                    //iterar entre los que tuvieron la misma parada 3 veces

                    for (int contadorfech=0;contadorfech<DiferenciasParadaoFecha.size();contadorfech++){
                        if (ContadorDiferencias.get(contadorfech)>=3){
                            //objeto que busca el formato de la fecha a obtener
                            SimpleDateFormat nuevoformato = new SimpleDateFormat(
                                    "hh:mm:ss a");
                            SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy");

                            Date fechaMarcador=null;
                            try {
                                fechaMarcador= formateadorfecha.parse(DiferenciasParadaoFecha.get(contadorfech).Hora_DeReporteCanal);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            String hora = nuevoformato.format(fechaMarcador);

                            listviewarray.add("Se ha reportado la unidad "+DiferenciasParadaoFecha.get(contadorfech).numeroDeUnidad+" con mal Aseo a las "+hora+" cerca de la parada "+DiferenciasParadaoFecha.get(contadorfech).LugarDelReporte+" Recuerda respetar y no tirar Basura en el Camion, ya que es de Todos.");
                        }
                    }


                }//hasta aqui

                //comprobacion mala actitud del chofer

                DiferenciasParadaoFecha.clear();
                ContadorDiferencias.clear();
                //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                if (arraylist_MalaActitudDelChofer.size()>=3){
                    for (int contaorParadasNombre=0;contaorParadasNombre<arraylist_MalaActitudDelChofer.size();contaorParadasNombre++){
                        String paradaLugar = arraylist_MalaActitudDelChofer.get(contaorParadasNombre).LugarDelReporte;
                        int banderayaseagrego=0;
                        for (int lugaresAgregar=0;lugaresAgregar<DiferenciasParadaoFecha.size();lugaresAgregar++){
                            if (paradaLugar.equals(DiferenciasParadaoFecha.get(lugaresAgregar).LugarDelReporte)){
                                banderayaseagrego=1;
                            }
                        }
                        if (banderayaseagrego==0){
                            DiferenciasParadaoFecha.add(arraylist_MalaActitudDelChofer.get(contaorParadasNombre));
                            ContadorDiferencias.add(0);
                        }
                    }

                    //for que busca cuantas paradas coinciden con cada diferencia
                    for (int ParadasDiferencias=0;ParadasDiferencias<DiferenciasParadaoFecha.size();ParadasDiferencias++){
                        String paradAcomparar = DiferenciasParadaoFecha.get(ParadasDiferencias).LugarDelReporte;
                        for (int buscarTodo=0;buscarTodo<arraylist_MalaActitudDelChofer.size();buscarTodo++){
                            if (paradAcomparar.equals(arraylist_MalaActitudDelChofer.get(buscarTodo).LugarDelReporte)){
                                ContadorDiferencias.set(ParadasDiferencias,ContadorDiferencias.get(ParadasDiferencias)+1);
                            }
                        }
                    }


                    //iterar entre los que tuvieron la misma parada 3 veces

                    for (int contadorfech=0;contadorfech<DiferenciasParadaoFecha.size();contadorfech++){
                        if (ContadorDiferencias.get(contadorfech)>=3){
                            //objeto que busca el formato de la fecha a obtener
                            SimpleDateFormat nuevoformato = new SimpleDateFormat(
                                    "hh:mm:ss a");
                            SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy");

                            Date fechaMarcador=null;
                            try {
                                fechaMarcador= formateadorfecha.parse(DiferenciasParadaoFecha.get(contadorfech).Hora_DeReporteCanal);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            String hora = nuevoformato.format(fechaMarcador);

                            listviewarray.add("Se ha reportado una mala actitud del chofer en la unidad "+DiferenciasParadaoFecha.get(contadorfech).numeroDeUnidad+" a las "+hora+" cerca de la parada "+DiferenciasParadaoFecha.get(contadorfech).LugarDelReporte+" toma tus precauciones y de ser necesario reporta al chofer en la linea 01 800 5238 699 de SEMOV, nos merecemos un buen servicio y trato digno. Mejoremos la Movilidad.");
                        }
                    }
                }//hasta aqui


                    //comprobacion Asalto
                    DiferenciasParadaoFecha.clear();
                    ContadorDiferencias.clear();
                    //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                    if (arraylist_Asaltos.size()>=3){
                        for (int contaorParadasNombre=0;contaorParadasNombre<arraylist_Asaltos.size();contaorParadasNombre++){
                            String paradaLugar = arraylist_Asaltos.get(contaorParadasNombre).LugarDelReporte;
                            int banderayaseagrego=0;
                            for (int lugaresAgregar=0;lugaresAgregar<DiferenciasParadaoFecha.size();lugaresAgregar++){
                                if (paradaLugar.equals(DiferenciasParadaoFecha.get(lugaresAgregar).LugarDelReporte)){
                                    banderayaseagrego=1;
                                }
                            }
                            if (banderayaseagrego==0){
                                DiferenciasParadaoFecha.add(arraylist_Asaltos.get(contaorParadasNombre));
                                ContadorDiferencias.add(0);
                            }
                        }

                        //for que busca cuantas paradas coinciden con cada diferencia
                        for (int ParadasDiferencias=0;ParadasDiferencias<DiferenciasParadaoFecha.size();ParadasDiferencias++){
                            String paradAcomparar = DiferenciasParadaoFecha.get(ParadasDiferencias).LugarDelReporte;
                            for (int buscarTodo=0;buscarTodo<arraylist_Asaltos.size();buscarTodo++){
                                if (paradAcomparar.equals(arraylist_Asaltos.get(buscarTodo).LugarDelReporte)){
                                    ContadorDiferencias.set(ParadasDiferencias,ContadorDiferencias.get(ParadasDiferencias)+1);
                                }
                            }
                        }


                        //iterar entre los que tuvieron la misma parada 3 veces

                        for (int contadorfech=0;contadorfech<DiferenciasParadaoFecha.size();contadorfech++){
                            if (ContadorDiferencias.get(contadorfech)>=3){
                                //objeto que busca el formato de la fecha a obtener
                                SimpleDateFormat nuevoformato = new SimpleDateFormat(
                                        "hh:mm:ss a");
                                SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                                        "HH:mm:ss d-MM-yyyy");

                                Date fechaMarcador=null;
                                try {
                                    fechaMarcador= formateadorfecha.parse(DiferenciasParadaoFecha.get(contadorfech).Hora_DeReporteCanal);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                String hora = nuevoformato.format(fechaMarcador);

                                listviewarray.add("Se ha reportado un Asalto cerca de la parada "+DiferenciasParadaoFecha.get(contadorfech).LugarDelReporte+" a las "+hora+" Ten cuidado y toma tus precauciones. De ser necesario llama al 911 Recuerda que es gratuito. Mejoremos la Movilidad");
                            }
                        }


                    }//hasta aqui


                //comprobacion peleas
                DiferenciasParadaoFecha.clear();
                ContadorDiferencias.clear();
                //ya que se ordenaron los rpeortes en su respectivo array se comprueba en cual hay mas de 3 reportes
                if (arraylist_Peleas.size()>=3){
                    for (int contaorParadasNombre=0;contaorParadasNombre<arraylist_Peleas.size();contaorParadasNombre++){
                        String paradaLugar = arraylist_Peleas.get(contaorParadasNombre).LugarDelReporte;
                        int banderayaseagrego=0;
                        for (int lugaresAgregar=0;lugaresAgregar<DiferenciasParadaoFecha.size();lugaresAgregar++){
                            if (paradaLugar.equals(DiferenciasParadaoFecha.get(lugaresAgregar).LugarDelReporte)){
                                banderayaseagrego=1;
                            }
                        }
                        if (banderayaseagrego==0){
                            DiferenciasParadaoFecha.add(arraylist_Peleas.get(contaorParadasNombre));
                            ContadorDiferencias.add(0);
                        }
                    }

                    //for que busca cuantas paradas coinciden con cada diferencia
                    for (int ParadasDiferencias=0;ParadasDiferencias<DiferenciasParadaoFecha.size();ParadasDiferencias++){
                        String paradAcomparar = DiferenciasParadaoFecha.get(ParadasDiferencias).LugarDelReporte;
                        for (int buscarTodo=0;buscarTodo<arraylist_Peleas.size();buscarTodo++){
                            if (paradAcomparar.equals(arraylist_Peleas.get(buscarTodo).LugarDelReporte)){
                                ContadorDiferencias.set(ParadasDiferencias,ContadorDiferencias.get(ParadasDiferencias)+1);
                            }
                        }
                    }


                    //iterar entre los que tuvieron la misma parada 3 veces

                    for (int contadorfech=0;contadorfech<DiferenciasParadaoFecha.size();contadorfech++){
                        if (ContadorDiferencias.get(contadorfech)>=3){
                            //objeto que busca el formato de la fecha a obtener
                            SimpleDateFormat nuevoformato = new SimpleDateFormat(
                                    "hh:mm:ss a");
                            SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                                    "HH:mm:ss d-MM-yyyy");

                            Date fechaMarcador=null;
                            try {
                                fechaMarcador= formateadorfecha.parse(DiferenciasParadaoFecha.get(contadorfech).Hora_DeReporteCanal);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            String hora = nuevoformato.format(fechaMarcador);

                            listviewarray.add("Se ha reportado una pelea cerca de la parada "+DiferenciasParadaoFecha.get(contadorfech).LugarDelReporte+" a las "+hora+" toma tus precauciones y de ser necesario llama al 911. Mejoremos la Movilidad");
                        }
                    }


                }//hasta aqui



                ArrayAdapter<String> adaptadorCanalAbierto = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_view_azulcanales, listviewarray);
                lv_CanalAbierto.setAdapter(adaptadorCanalAbierto);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    private void funcionalidad_btn_aceptar(){
        btn_Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void funcionalidadbtn_Hacer_Reporte(){



        OTBReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {


            tv_Reportes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    userFLPC = LocationServices.getFusedLocationProviderClient(verInformacionDeCanal.this);
                    userFLPC.getLastLocation().addOnSuccessListener(verInformacionDeCanal.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Double latuser=0.0,lnguser=0.0;
                            if (location != null) {
                                latuser = location.getLatitude();
                                lnguser = location.getLongitude();
                            }



                            Double latuserresult = 0.0, lnguserresult = 0.0, latuserresult2 = 0.0, lnguserresult2 = 0.0, suma1 = 0.0, suma2 = 0.0;
                            oRutasAbordado.setLatuser(0.0);
                            oRutasAbordado.setLnguser(0.0);


                            for (int contadoridaovuelta=0;contadoridaovuelta<2;contadoridaovuelta++){
                             String orientcion=null;
                                //contador para paradas
                                int contadorParadas = 1;
                             if (contadoridaovuelta==0){
                                 orientcion="ida";
                             }
                             if (contadoridaovuelta==1){
                                 orientcion="vuelta";
                             }


                            for (DataSnapshot snapshot2 : dataSnapshot.child("Rutas").child("Ruta"+numeroCanal).child(orientcion).getChildren()) {
                                // se cargan marcadores nuevos

                                String latitud1 = dataSnapshot.child("Rutas").child("Ruta"+numeroCanal).child(orientcion).child("Parada" + contadorParadas).child("latitud").getValue().toString();
                                String longitud1 = dataSnapshot.child("Rutas").child("Ruta"+numeroCanal).child(orientcion).child("Parada" + contadorParadas).child("longitud").getValue().toString();

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

                                    nombreParada = dataSnapshot.child("Rutas").child("Ruta"+numeroCanal).child(orientcion).child("Parada" + contadorParadas).child("tittle").getValue().toString();
                                }
                                contadorParadas++;
                            }


                            }//contador para comparar tambein con vuelta




                            Intent intentHacerReporte = new Intent(verInformacionDeCanal.this, ReportarRuta.class);

                            Bundle pasadorVentanaEmergenteReporte = new Bundle();

                            String idusuario = mAuth.getCurrentUser().getUid();
                            String correoDelUsuario = dataSnapshot.child("Users").child(idusuario).child("correo").getValue().toString();
                            pasadorVentanaEmergenteReporte.putString("correoDelUsuario",correoDelUsuario);
                            pasadorVentanaEmergenteReporte.putString("CanalNumero","Canal"+numeroCanal);
                            pasadorVentanaEmergenteReporte.putString("NombreParada",nombreParada);
                            intentHacerReporte.putExtras(pasadorVentanaEmergenteReporte);
                            startActivity(intentHacerReporte);

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
}
