package com.nue.photohunting;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class WisataActivity extends AppCompatActivity {
private TextView txtWisataKeteranganJudul,txtWisataKeteranganHin , txtWisataKeteranganLokasi;
    private ImageView mgbrWisata , mgbrBerburu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wisata);
        Intent intent = getIntent();
        String wisataID = intent.getStringExtra("WisataID");

        txtWisataKeteranganHin = findViewById(R.id.txtWisataKeteranganHin);
        txtWisataKeteranganJudul = findViewById(R.id.txtWisataKeteranganJudul);
        txtWisataKeteranganLokasi = findViewById(R.id.txtWisataKeteranganLokasi);
        mgbrWisata = findViewById(R.id.gbrWisataKeteranganGambar);
        mgbrBerburu = findViewById(R.id.gbrWisataBerburu);
        mgbrBerburu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kamera = new Intent(WisataActivity.this, CameraActivity.class);
                startActivity(kamera);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference wisata = database.getReference
                ("wisata/" + wisataID);

        wisata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String nama = dataSnapshot.child("nama").getValue().toString();
                String hint = dataSnapshot.child("hint").getValue().toString();
                String lokasi = dataSnapshot.child("lokasi").getValue().toString();
                String gambar = dataSnapshot.child("urlWisata").getValue().toString();

                txtWisataKeteranganHin.setText(hint);
                txtWisataKeteranganJudul.setText(nama);
                txtWisataKeteranganLokasi.setText(lokasi);
                Picasso.with(WisataActivity.this).load(gambar).into(mgbrWisata);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
