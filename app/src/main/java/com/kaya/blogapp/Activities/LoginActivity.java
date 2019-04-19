
package com.kaya.blogapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kaya.blogapp.R;

public class LoginActivity extends AppCompatActivity {
    private ImageView userFoto;
    private EditText editLoginEmail, editLoginPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Intent HomeActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        variaveis();

        progressBar.setVisibility(View.INVISIBLE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = editLoginEmail.getText().toString();
                final String password = editLoginPassword.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                if (email.isEmpty() || password.isEmpty()) {

                    showMessage("Preenche todos os campos");
                    btnLogin.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    signIn(email, password);
                }

            }
        });

        userFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });
    }

    private void variaveis() {
        userFoto = findViewById(R.id.imageViewLoginId);
        editLoginEmail = findViewById(R.id.loginEmailID);
        editLoginPassword = findViewById(R.id.loginPasswordID);
        btnLogin = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBarLogin);
        mAuth = FirebaseAuth.getInstance();
        HomeActivity = new Intent(this, com.kaya.blogapp.Activities.DrawerActivity.class);
    }

    private void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                       if (task.isSuccessful()) {
                           progressBar.setVisibility(View.INVISIBLE);
                           btnLogin.setVisibility(View.VISIBLE);
                           updateUi();
                       } else {
                           showMessage(task.getException().getMessage());
                           progressBar.setVisibility(View.INVISIBLE);
                           btnLogin.setVisibility(View.VISIBLE);
                       }
                    }
                });
    }

    private void updateUi() {

      startActivity(HomeActivity);
      finish();
    }

    private void showMessage(String message) {

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            updateUi();
        }
    }
}
