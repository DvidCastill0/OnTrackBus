package OnTrackBus.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Ventana_Emergente_EntrarACanal extends AppCompatActivity {

    private TextView tv_RutaCamal;
    private Button btn_Aceptar;
    private Button btn_Cancelar;
    private String nombreCanal;
    private int numeroCanal;

    private DatabaseReference OTBReference;
    private FirebaseAuth mAuth;

    //objeto para obtener hijos de canales de rutas
    private datosCanal oCanales;
    private ArrayList<datosCanal> array_reportesCanales = new ArrayList<datosCanal>();

    private int bandera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana__emergente__entrar_a_canal);

        //cortar activity para hacer ventana emergente
        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);
        int ancho = medidasVentana.widthPixels;
        int alto = medidasVentana.heightPixels;
        getWindow().setLayout((int)(ancho + 0.85),(int)(alto * 0.30));

        //instancear variables
        tv_RutaCamal = this.findViewById(R.id.tv_CanalDeRuta);

        //bundle para descargar datos
        Bundle BundleNombresDeCanales = new Bundle();
        BundleNombresDeCanales =  getIntent().getExtras();

        nombreCanal = BundleNombresDeCanales.getString("CanalNombre");
        numeroCanal = BundleNombresDeCanales.getInt("CanalNumero");
        tv_RutaCamal.setText("Canal de Ruta "+nombreCanal);


        //instanciar botones
        btn_Aceptar = this.findViewById(R.id.btn_entrarACanal);
        btn_Cancelar = this.findViewById(R.id.btn_CancelarCanal);

        btn_Cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //checar si el canal buscado ya esta en el list view
        OTBReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        OTBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                String iduser = mAuth.getCurrentUser().getUid();
                bandera=0;
                for (DataSnapshot snapshot : dataSnapshot.child("Users").child(iduser).child("CanalesSeleccionados").getChildren()){
                    oCanales = snapshot.getValue(datosCanal.class);
                    array_reportesCanales.add(oCanales);

                }

                for (int contadorcanales=0; contadorcanales<array_reportesCanales.size();contadorcanales++){
                    if (nombreCanal.equals(array_reportesCanales.get(contadorcanales).Nombre)){
                        bandera=1;
                        btn_Aceptar.setText("Abrir");
                    }
                }


                btn_Aceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        //abrir o unirse segun si ya esta el canal en el list view o no
                        if (bandera==1){
                            //abrir canal

                            Intent ver_Canal = new Intent(Ventana_Emergente_EntrarACanal.this, verInformacionDeCanal.class);

                            Bundle pasadorVentanaEmergenteCanal = new Bundle();
                            pasadorVentanaEmergenteCanal.putString("CanalNombre",nombreCanal);
                            pasadorVentanaEmergenteCanal.putInt("CanalNumero",numeroCanal);
                            ver_Canal.putExtras(pasadorVentanaEmergenteCanal);
                            startActivity(ver_Canal);
                            finish();



                        }else {
                            //unirse a canal

                                    String userid = mAuth.getCurrentUser().getUid();
                                    int contadorAgregados=1;
                                    for (int contadorInsertar=1; contadorInsertar<11;contadorInsertar++){

                                        if (dataSnapshot.child("Users").child(userid).child("CanalesSeleccionados").child("CanalSeleccionado"+contadorInsertar).exists()){
                                            contadorAgregados++;
                                        }else {
                                            Map<String, Object> AgregarCanal = new HashMap<>();
                                            AgregarCanal.put("Id_CanalSeleccionado",contadorInsertar);
                                            AgregarCanal.put("Nombre",nombreCanal);
                                            OTBReference.child("Users").child(userid).child("CanalesSeleccionados").child("CanalSeleccionado"+contadorInsertar).setValue(AgregarCanal).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    finish();
                                                }
                                            });
                                            break;
                                        }

                                    }

                                    if (contadorAgregados==10){
                                        Toast.makeText(getApplicationContext(),"Ya estas unido a 10 canales, porfavor elimina uno para agregar otro.",Toast.LENGTH_LONG);
                                        finish();
                                    }

                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //funcionalidad botones





    }
}
