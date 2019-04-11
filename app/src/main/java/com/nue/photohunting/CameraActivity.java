package com.nue.photohunting;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.camerakit.CameraKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class CameraActivity extends AppCompatActivity {
    private ImageView mPriview;
    private Button btnambilGambar, btnUploadGambar;
    private CameraKitView cameraKitView;
    private TextView txtCameraQR,txtCameraSenyum;
    private ProgressDialog prolog;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference database;
    String senyuman = "kosong" ;
    String kodeQR = "kosong";
    Uri UploadGambar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);


        txtCameraSenyum = findViewById(R.id.txtSenyuman);
        txtCameraQR = findViewById(R.id.txtQr);
        btnambilGambar = findViewById(R.id.btnCameraAmbil);
        btnUploadGambar = findViewById(R.id.btnCameraUpload);
        cameraKitView = findViewById(R.id.camera);
        mPriview = findViewById(R.id.gbrCapture);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        prolog = new ProgressDialog(CameraActivity.this);

        btnambilGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cameraKitView.setFlash(CameraKit.FLASH_TORCH);



                cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {

                        File savedPhoto = new File(Environment.getExternalStorageDirectory(), "ph.jpg");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(capturedImage , 0, capturedImage.length);
                        setUploadGambar(Uri.fromFile(savedPhoto));

                        try {

                            FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                            outputStream.write(capturedImage);
                            outputStream.close();



                            ProsesBitmap(bitmap);
                            mPriview.setImageBitmap(bitmap);

                            btnambilGambar.setEnabled(false);

                            Timer buttonTimer = new Timer();
                            buttonTimer.schedule(new TimerTask() {

                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            btnambilGambar.setEnabled(true);
                                        }
                                    });
                                }
                            }, 10000);








                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }



                    }
                });


            }
        });

        btnUploadGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                prolog.setTitle("upload");
                prolog.setMessage("mengupload gambar,  harap tunggu!....");
                prolog.setCanceledOnTouchOutside(false);
                prolog.show();


                Uri file = UploadGambar;
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String Uid = currentUser.getUid();
                String Qr = txtCameraQR.getText().toString();

                StorageReference riversRef = mStorageRef.child("aktif_event/"+Uid+Qr+".jpg");

                riversRef.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                String downloadURL = taskSnapshot.getStorage().getDownloadUrl().toString();
                                String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                String Uid = currentUser.getUid();
                                String Qr = txtCameraQR.getText().toString();
                                String Senyuman = txtCameraSenyum.getText().toString();
                                database = FirebaseDatabase.getInstance().getReference().child("aktif_event").child(Uid+Qr);
                                String waktu = new SimpleDateFormat("yyyy-MM-dd:HH.mm.ss", Locale.getDefault()).format(new Date());
                                HashMap<String, String> foto = new HashMap<>();

                                foto.put("gambar" , downloadUrl);
                                foto.put("user_id", Uid);
                                foto.put("nilai",Senyuman );
                                foto.put("kode_qr", Qr);
                                foto.put("waktu", waktu);

                                database.setValue(foto);


                                prolog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads

                                prolog.dismiss();

                                // ...
                            }
                        });



            }
        });

    }

    private void setUploadGambar(Uri uploadGambar) {
        this.UploadGambar = uploadGambar;

    }

    private void ProsesBitmap(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        Bitmap bitmapQR = convert(bitmap, Bitmap.Config.ARGB_8888);
        FirebaseVisionImage QR = FirebaseVisionImage.fromBitmap(bitmapQR);



        FirebaseVisionBarcodeDetectorOptions opsiQR = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(
                        FirebaseVisionBarcode.FORMAT_QR_CODE,
                        FirebaseVisionBarcode.FORMAT_PDF417)
                .build();


        FirebaseVisionBarcodeDetector deteksiQR = FirebaseVision.getInstance().getVisionBarcodeDetector(opsiQR);

        deteksiQR.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                        prosesQR(firebaseVisionBarcodes);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CameraActivity.this, e.getMessage() , Toast.LENGTH_SHORT).show();
                    }
                });


        FirebaseVisionFaceDetectorOptions opsiWajah =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .setMinFaceSize(0.15f)
                        .enableTracking().build();

        FirebaseVisionFaceDetector detekWajah = FirebaseVision.getInstance()
                .getVisionFaceDetector(opsiWajah);

        detekWajah.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        prosesWajah(firebaseVisionFaces);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CameraActivity.this, e.getMessage() , Toast.LENGTH_SHORT).show();
                    }
                });



    }

    private void prosesWajah(List<FirebaseVisionFace> firebaseVisionFaces) {
        for (FirebaseVisionFace face : firebaseVisionFaces) {
            float smileProb = face.getSmilingProbability();
            String senyuman = String.valueOf(smileProb);


            txtCameraSenyum.setText(senyuman);
            setSenyuman(senyuman);

            if(txtCameraSenyum.getText().equals("senyuman")&&txtCameraQR.getText().equals("qr")){

                Toast.makeText(CameraActivity.this, "qr kode dan wajah tidak di temukan "
                        , Toast.LENGTH_SHORT).show();

            }else if(txtCameraSenyum.getText().equals("senyuman")){
                Toast.makeText(CameraActivity.this, "wajah tidak di temukan "
                        , Toast.LENGTH_SHORT).show();

            }else if(txtCameraQR.getText().equals("qr")){
                Toast.makeText(CameraActivity.this, "qr kode tidak di temukan "
                        , Toast.LENGTH_SHORT).show();

            }

            else{
                btnambilGambar.setVisibility(View.INVISIBLE);
                btnUploadGambar.setVisibility(View.VISIBLE);
                cameraKitView.setVisibility(View.INVISIBLE);
                mPriview.setVisibility(View.VISIBLE);

            }

        }


    }

    private void prosesQR(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {


        for (FirebaseVisionBarcode item : firebaseVisionBarcodes) {

            int value_type = item.getValueType();
            switch (value_type){

                case FirebaseVisionBarcode.TYPE_TEXT:{

                    String kodeQR = item.getRawValue();


                    txtCameraQR.setText(kodeQR);

                    setKodeQR(kodeQR);
                }
                break;


                default:
                    break;

            }



        }

    }

    private void setKodeQR(String kodeQR) {

        this.kodeQR = kodeQR;
    }

    private void setSenyuman(String senyuman) {

        this.senyuman = senyuman;
    }

    private Bitmap convert(Bitmap bitmap, Bitmap.Config config) {

        Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas canvas = new Canvas(convertedBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return convertedBitmap;

    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
