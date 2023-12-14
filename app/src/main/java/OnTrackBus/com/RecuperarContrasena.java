package OnTrackBus.com;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RecuperarContrasena extends Fragment {
    private Button btn_Volver;
    private EditText edt_RecuperarContrasena;
    private Button btn_RecuperarContrasena;

    private FirebaseAuth mAuth;

    //barra de progreso mientras se envia el correo
    private ProgressDialog mDialog;
    public RecuperarContrasena() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ResetPassword = inflater.inflate(R.layout.fragment_recuperar_contrasena, container, false);

        //instancera mauth para usar funciones de autenticacion.
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(getContext());

        edt_RecuperarContrasena = ResetPassword.findViewById(R.id.edtCorreoRestablecerContrasena);

        btn_RecuperarContrasena = ResetPassword.findViewById(R.id.btn_Recuperar_Contrasena);
        btn_RecuperarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comprobacionCorreo()==true){
                    mDialog.setMessage("Enviando Correo...");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    mAuth.setLanguageCode("es");
                    mAuth.sendPasswordResetEmail(edt_RecuperarContrasena.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                edt_RecuperarContrasena.setText("");
                                Toast.makeText(getActivity(),"Se ha enviado un correo para Reestablecer tu contraseña a: "+edt_RecuperarContrasena.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                InicioDeSesion fragment = new InicioDeSesion();
                                transaction.replace(R.id.contenedor_main_activity,fragment);
                                transaction.commit();
                            }else{
                                if (task.getException() instanceof FirebaseAuthInvalidUserException){
                                    edt_RecuperarContrasena.setError("Correo No Registrado");
                                    Toast.makeText(getActivity(),"Este Correo no esta Asociado a ninguna cuenta, Por favor Registrate",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getActivity(),"Algo Salio Mal porfavor reportalo.",Toast.LENGTH_SHORT).show();
                                }
                            }
                            mDialog.dismiss();
                        }
                    });
                }
            }
        });


        btn_Volver = (Button) ResetPassword.findViewById(R.id.btn_regresaraloginRC);
        btn_Volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                InicioDeSesion fragment = new InicioDeSesion();
                transaction.replace(R.id.contenedor_main_activity,fragment);
                transaction.commit();
            }
        });

        return ResetPassword;
    }

    private boolean comprobacionCorreo(){
        // El email a validar
        String correo = edt_RecuperarContrasena.getText().toString().trim();
        // Patrón para validar el email
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        //busca nuestro email en base a la expresion que selecu¡cionamos del pattern en caso de que sea correcta nos retorna un valor true
        Matcher mather = pattern.matcher(correo);
        boolean bandera=false;
        if (correo.equals("")){
            edt_RecuperarContrasena.setError("Dato Requerido");
        }else if (mather.find() == false){
            edt_RecuperarContrasena.setError("Correo Invalido, Porfavor escribe un correo real.");
        }else {
            bandera=true;
        }
        return bandera;
    }


}


