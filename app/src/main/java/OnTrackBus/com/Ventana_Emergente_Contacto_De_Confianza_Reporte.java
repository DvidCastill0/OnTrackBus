package OnTrackBus.com;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Ventana_Emergente_Contacto_De_Confianza_Reporte extends AppCompatActivity {

    private TextView reporte_completo;
    private TextView tv_fecha;
    private TextView tv_email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana__emergente__contacto__de__confianza__reporte);

        //cortar activity para hacer ventana emergente
        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);
        int ancho = medidasVentana.widthPixels;
        int alto = medidasVentana.heightPixels;
        getWindow().setLayout((int)(ancho + 0.85),(int)(alto * 0.40));


        reporte_completo = findViewById(R.id.campo_reporte);
        tv_fecha = findViewById(R.id.campo_fecha);
        tv_email = findViewById(R.id.campo_email);

        Bundle recibidor = getIntent().getExtras();



        String fecha = recibidor.getString("Fecha");
        String hora = recibidor.getString("Hora");
        String correo = recibidor.getString("Correo");
        String nombre = recibidor.getString("Nombre");
        String parada = recibidor.getString("Parada");
        String ruta = recibidor.getString("Ruta");

        SimpleDateFormat formateadorfecha = new SimpleDateFormat(
                "HH:mm:ss d-MM-yyyy");

        // SimpleDateFormat nuevoFormato = new SimpleDateFormat("hh:mm:ssa EEEE d 'de' MMMM 'de' yyyy");
        SimpleDateFormat nuevoFormatoHora = new SimpleDateFormat("hh:mm:ss a");
        SimpleDateFormat nuevoFormatoFecha = new SimpleDateFormat("EEEE d-MMM-yyyy");
        Date fechadate = null;
        String fechaantigua = hora+ " " + fecha;
        try {
            fechadate = formateadorfecha.parse(fechaantigua);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String fechadia = nuevoFormatoFecha.format(fechadate);
        String fechahora = nuevoFormatoHora.format(fechadate);
        String mensaje_confianza = "El usuario " + nombre +" abordó la ruta " + ruta + " a las " + fechahora + " en la parada " + parada +  ".\n" +
                "Ahora puedes estar tranquil@.\n Atte: On track Bus.";


        tv_fecha.setText(fechadia);
        tv_email.setText(correo);

        reporte_completo.setText(mensaje_confianza);










    }
}
