package OnTrackBus.com;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ventanaEmergenteSugerencias extends AppCompatActivity {

    //crear variable de firebase
    private DatabaseReference OTBReference;
    private FirebaseAuth mAuth;

    //variables xml
    private EditText edt_Sugerencias;
    private Button btn_CancelarSugerencia;
    private Button btn_EnviarSugerencias;

    private String numeroCanal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_emergente_sugerencias);

        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);
        int ancho = medidasVentana.widthPixels;
        int alto = medidasVentana.heightPixels;
        getWindow().setLayout((int)(ancho + 0.85),(int)(alto * 0.40));

        //recibir numero de cnal
        Bundle bundleSugerencias;
        bundleSugerencias = getIntent().getExtras();
        numeroCanal = bundleSugerencias.getString("CanalNumero");

        //instancemaos firebase
        OTBReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //instanciar elementos grficos
        edt_Sugerencias = this.findViewById(R.id.edt_EscribeSugerencia);
        btn_EnviarSugerencias = this.findViewById(R.id.btn_EnivarSugerencia);
        btn_CancelarSugerencia = this.findViewById(R.id.btn_CancelarSugerencia);

        //funcionalidad
        btn_CancelarSugerencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_EnviarSugerencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sugerenciaUsuario = edt_Sugerencias.getText().toString().trim();
                String correoUsuario = mAuth.getCurrentUser().getEmail();
                //objeto que busca el formato de la fecha a obtener
                SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                        "HH:mm:ss d-MM-yyyy");

                //se obtiene la fecha y hora actual
                Date fechaDate = new Date();
                String fecha = formateadorfecha.format(fechaDate);



                Map<String, Object> Sugerencia = new HashMap<>();
                Sugerencia.put("sugerencia_Contenido",sugerenciaUsuario);
                Sugerencia.put("CorreoRemitente",correoUsuario);
                Sugerencia.put("FechaDeEmision",fecha);
                // Generate a reference to a new location and add some data using push()
                DatabaseReference pushedPostRef = OTBReference.child("Canales").child(numeroCanal).child("Sugerencias").push();
                String Id_Clave = pushedPostRef.getKey();
                Sugerencia.put("Id_ClaveSugerencia",Id_Clave);
                OTBReference.child("Canales").child(numeroCanal).child("Sugerencias").push().setValue(Sugerencia);

                finish();
            }
        });

    }
}
