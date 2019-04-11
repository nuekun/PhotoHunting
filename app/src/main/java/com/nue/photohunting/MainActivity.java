package com.nue.photohunting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;

public class MainActivity extends AppCompatActivity {
    private ImageView mGbrMainPermainan , mGbrMainLogout , mGbrMainCamera , mgbrMainTutorial;
    private FirebaseAuth mAuth;
    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mGbrMainPermainan = findViewById(R.id.gbrMainPermainan);
        mGbrMainLogout = findViewById(R.id.gbrMainLogout);
        mGbrMainCamera = findViewById(R.id.gbrMainCamera);
        mgbrMainTutorial = findViewById(R.id.gbrMainCara);
        mFunctions = FirebaseFunctions.getInstance();
        mFunctions.getHttpsCallable("updateStatusEvent").call();

        mgbrMainTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cara = new Intent(MainActivity.this, TutorialActivity.class);
                startActivity(cara);
            }
        });

        mGbrMainCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent,0);
                Intent kamera = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(kamera);



            }
        });

        mGbrMainPermainan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent permainan = new Intent(MainActivity.this,ListEventActivity.class);
                startActivity(permainan);
            }
        });

        mGbrMainLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                updateUI(null);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);


    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser==null){

            Intent start = new Intent(MainActivity.this,StartActivity.class);
            startActivity(start);
            finish();

        }



    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//    if(data !=null ) {
//        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//        Intent kamera = new Intent(MainActivity.this, CameraActivity.class);
//        kamera.putExtra("BitmapImage", bitmap);
//        startActivity(kamera);
//    }else{
//
//
//    }
//
//    }

}
