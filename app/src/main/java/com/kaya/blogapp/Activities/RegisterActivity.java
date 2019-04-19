package com.kaya.blogapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaya.blogapp.R;

public class RegisterActivity extends AppCompatActivity {

    ImageView userFoto;
    static int PRequest = 1;
    static int REQUESTCODE = 1;
    Uri pickedImageUrl;

    private EditText editName, editEmail, editPassword, editConfirmPassword;
    private ProgressBar progressBar;
    private Button btnRegister;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        variaveis();

        progressBar.setVisibility(View.INVISIBLE);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRegister.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                String name = editName.getText().toString();
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                String confirmPassword = editConfirmPassword.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || !password.equals(confirmPassword)) {

                    showMessage("Porfavor verifique todos os campos");
                    btnRegister.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    createUserAccount(name, email, password);
                }

            }
        });

        userFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > 22){
                    checkPermissionFoto();
                } else {
                    openGallery();
                }
            }
        });
    }

    private void variaveis() {
        userFoto = findViewById(R.id.registerUserFoto);
        editName = findViewById(R.id.registerName);
        editEmail = findViewById(R.id.registerEmail);
        editPassword = findViewById(R.id.registerPassword);
        editConfirmPassword = findViewById(R.id.registerConfirmPassword);
        btnRegister = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
    }

    private void createUserAccount(final String name, String email, String password) {

       mAuth.createUserWithEmailAndPassword(email, password)
               .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()) {
                            showMessage("Conta Criada com Sucesso");
                            updateProfile(name, pickedImageUrl, mAuth.getCurrentUser());
                       } else {
                           showMessage("Falha ao criar a conta!" + task.getException().getMessage());
                           btnRegister.setVisibility(View.VISIBLE);
                           progressBar.setVisibility(View.INVISIBLE);
                       }
                   }
               });
    }

    private void updateProfile(final String name, Uri pickedImageUrl, final FirebaseUser currentUser) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImageUrl.getLastPathSegment());
        imageFilePath.putFile(pickedImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

               imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                       UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                               .setDisplayName(name)
                               .setPhotoUri(uri)
                               .build();

                       currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    showMessage("Registo completo");
                                    updateUi();
                                }
                           }
                       });
                   }
               });
            }
        });
    }

    private void updateUi() {
        Intent homeActivity = new Intent(getApplicationContext(), DrawerActivity.class);
        startActivity(homeActivity);
        finish();

    }


    private void showMessage(String message) {

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCODE);
    }

    private void checkPermissionFoto() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PRequest);
            }

        } else {
            openGallery();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {
          pickedImageUrl = data.getData();
          userFoto.setImageURI(pickedImageUrl);
        }
    }

}
