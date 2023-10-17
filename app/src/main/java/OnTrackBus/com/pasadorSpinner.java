package OnTrackBus.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class pasadorSpinner extends AppCompatActivity {

    private Bundle BundleOtraRuta = new Bundle();
    private DatabaseReference OTBReference;
    //bundle para descargar datos
    private Bundle BundleNombresDeRutas = new Bundle();
    private String numeroDeRuta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasador_spinner);

        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);
        int ancho = medidasVentana.widthPixels;
        int alto = medidasVentana.heightPixels;
        getWindow().setLayout((int)(ancho + 0.85),(int)(alto * 0.3));


        BundleNombresDeRutas =  getIntent().getExtras();
        OTBReference = FirebaseDatabase.getInstance().getReference();

        OTBReference.child("Rutas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                //este dato es opcional solo es para indicar que se esta recargando el fragmento y no muestre asi el mensaje de bienvenido
                BundleOtraRuta.putString("RecargarFragmento","RecargarFragmento");


                BundleOtraRuta.putString("OrientacionRuta",BundleNombresDeRutas.getString("OrientacionRuta"));
                BundleOtraRuta.putInt("CantidadDeRutas",BundleNombresDeRutas.getInt("CantidadDeRutas"));
                BundleOtraRuta.putString("contactoConfianza",BundleNombresDeRutas.getString("contactoConfianza"));
                BundleOtraRuta.putString("RutaSeleccionada",BundleNombresDeRutas.getString("RutaSeleccionada"));
                int contadorRutasDisponibles=1;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //se obtiene losnombres de las rutas disponibles
                    String numeroDeRuta2 = "Ruta" + contadorRutasDisponibles;
                    String rutaname = dataSnapshot.child("Ruta" + contadorRutasDisponibles).child("NombreDeRuta").getValue().toString();
                    if (rutaname.equals(BundleNombresDeRutas.getString("RutaSeleccionada"))) {
                        BundleOtraRuta.putString("RutaSeleccionadaNumero",numeroDeRuta2);
                        numeroDeRuta=numeroDeRuta2;
                    }
                    contadorRutasDisponibles++;
                }

                //se obtienen los nombres de las Paradas
                int contadorParadas=1;
                for (DataSnapshot snapshot: dataSnapshot.child(numeroDeRuta).child(BundleNombresDeRutas.getString("OrientacionRuta")).getChildren()){
                    String nombreParada = dataSnapshot.child(numeroDeRuta).child(BundleNombresDeRutas.getString("OrientacionRuta")).child("Parada"+contadorParadas).child("tittle").getValue().toString();
                    BundleOtraRuta.putString("Parada"+contadorParadas,nombreParada);
                    contadorParadas++;
                }
                BundleOtraRuta.putInt("CantidadDeParadas",contadorParadas);

                for (int RMFcontador=0;RMFcontador<3;RMFcontador++){
                    if (BundleNombresDeRutas.getString("RMF"+RMFcontador) != null){
                        BundleOtraRuta.putString("RMF"+RMFcontador,BundleNombresDeRutas.getString("RMF"+RMFcontador));
                    }
                }

                for (int enviarrutas=1;enviarrutas<BundleNombresDeRutas.getInt("CantidadDeRutas");enviarrutas++){
                    BundleOtraRuta.putString("Ruta"+enviarrutas,BundleNombresDeRutas.getString("Ruta"+enviarrutas));
                }


                // Agregas el Bundle al Intent e inicias ActivityB
                Intent intentMenuUsuario = new Intent(pasadorSpinner.this,MenuUsuario.class);
                intentMenuUsuario.putExtras(BundleOtraRuta);
                startActivity(intentMenuUsuario);
                finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
