package OnTrackBus.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BuscarOtraRutaVentanaEmergente extends AppCompatActivity {

    // variable autocomplete otra ruta
    private AutoCompleteTextView actv_BuscarOtraRuta;

    //variabble botones otra ruta
    private Button btn_OtraRutaAceptar,btn_OtraRutaCancelar;

    //variables para recuperar datos.
    //arraylist para autocompletetextview
    private ArrayList<String> rutasNombres = new ArrayList<>();
    private ArrayList<String> RMFNombres = new ArrayList<>();
    private int contadorNombresRutas;
    private String correoConfianza,orientacionRutas;

    //otra ruta bundle para pasar datos
    private Bundle BundleOtraRuta = new Bundle();

    private DatabaseReference OTBReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_otra_ruta_ventana_emergente);

        OTBReference = FirebaseDatabase.getInstance().getReference();

        //instancear variables
        actv_BuscarOtraRuta = this.findViewById(R.id.actv_SeleccioneOtraRutaAMirar);
        btn_OtraRutaAceptar = this.findViewById(R.id.btn_BuscarOtraRuta);
        btn_OtraRutaCancelar = this.findViewById(R.id.btn_CancelarOtraRutaMirar);

        //bundle para descargar datos
        Bundle BundleNombresDeRutas = new Bundle();
        BundleNombresDeRutas =  getIntent().getExtras();
        contadorNombresRutas = BundleNombresDeRutas.getInt("CantidadDeRutas");
        correoConfianza = BundleNombresDeRutas.getString("contactoConfianza");
        orientacionRutas = BundleNombresDeRutas.getString("orientacion");


        for (int contadorarrarRMF=1;contadorarrarRMF<4;contadorarrarRMF++){
            RMFNombres.add(BundleNombresDeRutas.getString("RMF"+contadorarrarRMF));
        }
        for (int contadorNombresRutas2=1; contadorNombresRutas2<contadorNombresRutas; contadorNombresRutas2++){
            rutasNombres.add(BundleNombresDeRutas.getString("Ruta"+contadorNombresRutas2));
        }


        //array adapter para autocompletetextview
        ArrayAdapter<String> adapterrutasNombres = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,rutasNombres);
        actv_BuscarOtraRuta.setAdapter(adapterrutasNombres);




        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);
        int ancho = medidasVentana.widthPixels;
        int alto = medidasVentana.heightPixels;
        getWindow().setLayout((int)(ancho + 0.85),(int)(alto * 0.3));

        funcion_btn_Aceptar();
        funcion_btn_Cancelar();
    }

    private void funcion_btn_Aceptar(){
        btn_OtraRutaAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                OTBReference.child("Rutas").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String rutaSeleccionadaNombre = actv_BuscarOtraRuta.getText().toString().trim();
                        String rutaSeleccionadaNumero=null;
                        int bandera=0;
                        for (int contadorarraynombres=1;contadorarraynombres<contadorNombresRutas;contadorarraynombres++){
                            int contadormenosuno=contadorarraynombres-1;
                            if (rutaSeleccionadaNombre.equals(rutasNombres.get(contadormenosuno))){
                                rutaSeleccionadaNumero="Ruta"+contadorarraynombres;
                                bandera=1;
                            }
                        }

                        if (bandera==1){
                            //este dato es opcional solo es para indicar que se esta recargando el fragmento y no muestre asi el mensaje de bienvenido
                            BundleOtraRuta.putString("RecargarFragmento","RecargarFragmento");


                            BundleOtraRuta.putString("OrientacionRuta",orientacionRutas);
                            BundleOtraRuta.putInt("CantidadDeRutas",contadorNombresRutas);
                            BundleOtraRuta.putString("contactoConfianza",correoConfianza);
                            BundleOtraRuta.putString("RutaSeleccionada",rutaSeleccionadaNombre);
                            BundleOtraRuta.putString("RutaSeleccionadaNumero",rutaSeleccionadaNumero);
                            for (int enviarrutas=1;enviarrutas<contadorNombresRutas;enviarrutas++){
                                int menosuno=enviarrutas-1;
                                BundleOtraRuta.putString("Ruta"+enviarrutas,rutasNombres.get(menosuno));
                            }
                            for (int RMFcontador=0;RMFcontador<3;RMFcontador++){
                                if (RMFNombres.get(RMFcontador) != null){
                                    BundleOtraRuta.putString("RMF"+RMFcontador,RMFNombres.get(RMFcontador));
                                }
                            }

                            //se obtienen los nombres de las rutas
                            int contadorParadas=1;

                            for (DataSnapshot snapshot: dataSnapshot.child(rutaSeleccionadaNumero).child(orientacionRutas).getChildren()){
                                String nombreParada = dataSnapshot.child(rutaSeleccionadaNumero).child(orientacionRutas).child("Parada"+contadorParadas).child("tittle").getValue().toString();
                                BundleOtraRuta.putString("Parada"+contadorParadas,nombreParada);
                                contadorParadas++;
                            }
                            BundleOtraRuta.putInt("CantidadDeParadas",contadorParadas);



                            // Agregas el Bundle al Intent e inicias ActivityB
                            Intent intentMenuUsuario = new Intent(BuscarOtraRutaVentanaEmergente.this,MenuUsuario.class);
                            intentMenuUsuario.putExtras(BundleOtraRuta);
                            startActivity(intentMenuUsuario);
                            finish();
                        }
                        else if (rutaSeleccionadaNombre.equals("")){
                            Toast.makeText(getApplicationContext(),"Porfavor Ingrese una Ruta",Toast.LENGTH_SHORT).show();
                            actv_BuscarOtraRuta.setError("Porfavor Ingrese una Ruta");

                        }else{
                            Toast.makeText(getApplicationContext(),"Porfavor Ingrese una Ruta Valida De la lista de Rutas Disponibles.",Toast.LENGTH_SHORT).show();
                            actv_BuscarOtraRuta.setError("Porfavor Ingrese una Ruta Valida De la lista de Rutas Disponibles.");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }


    private void funcion_btn_Cancelar(){
        btn_OtraRutaCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
