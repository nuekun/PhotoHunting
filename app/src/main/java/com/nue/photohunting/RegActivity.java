package com.nue.photohunting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegActivity extends AppCompatActivity {
    private Button btnSimpan;
    private EditText txtNama;
    private EditText txtAlamat;
    private EditText txtTelepon;
    private EditText txtEmail;
    private EditText txtPassword;
    private DatabaseReference database;
    private FirebaseAuth mAuth;
    private Toolbar RegToolbar;
    private ProgressDialog logProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);


        btnSimpan = findViewById(R.id.btnRegSimpan);
        txtNama = findViewById(R.id.txtRegNama);
        txtAlamat = findViewById(R.id.txtRegAlamat);
        txtTelepon = findViewById(R.id.txtRegHP);
        txtEmail = findViewById(R.id.txtRegEmail);
        txtPassword = findViewById(R.id.txtRegPassword);

        mAuth = FirebaseAuth.getInstance();



        logProgress = new ProgressDialog(this);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                String nama = txtNama.getText().toString();
                String alamat = txtAlamat.getText().toString();
                String telepon = txtTelepon.getText().toString();
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                if(!TextUtils.isEmpty(nama)||



                !TextUtils.isEmpty(alamat)||
                        !TextUtils.isEmpty(telepon)||
                        !TextUtils.isEmpty(email)||
                        !TextUtils.isEmpty(password) ) {


                    logProgress.setTitle("Login");
                    logProgress.setMessage("menghubungkan ke server");
                    logProgress.setCanceledOnTouchOutside(false);
                    logProgress.show();
                    daftarkanUser(nama,alamat,telepon,email,password);

                }

            }
        });



    }

    private void daftarkanUser(final String nama, final String alamat, final String telepon, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    String TAG = null;
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();
                            database = FirebaseDatabase.getInstance().getReference().child("Pengguna_android").child(uid);

                            String device_token = FirebaseInstanceId.getInstance().getToken();

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("nama", nama);
                            userMap.put("alamat", alamat);
                            userMap.put("gambar_profil", "default");
                            userMap.put("telepon", telepon);
                            userMap.put("token", device_token);
                            userMap.put("poin" ,"0");
                            userMap.put("id",uid);

                            database.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    logProgress.dismiss();
                                    if(task.isSuccessful()){

                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);

                                    }

                                }
                            });






                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            logProgress.dismiss();
                            updateUI(null);
                        }

                        // ...
                    }
                });


    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            Toast.makeText(RegActivity.this, "error" , Toast.LENGTH_SHORT).show();

        }else{
            goMain();
        }

    }

    private void goMain() {

        Intent main = new Intent(RegActivity.this,MainActivity.class);
        startActivity(main);
        finish();
    }
}
