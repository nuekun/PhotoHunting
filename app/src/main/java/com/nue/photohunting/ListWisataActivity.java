package com.nue.photohunting;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ListWisataActivity extends AppCompatActivity {
    private ImageView mImageEvent;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private TextView mtxtjudul,mTxtDeskripsi,mTxtBerakhir ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_wisata);


        Intent intent = getIntent();
        String eventID = intent.getStringExtra("eventID");
        mImageEvent = findViewById(R.id.gbrListWisata);
        mtxtjudul = findViewById(R.id.txtListWisataJudulEvent);
        mTxtDeskripsi = findViewById(R.id.txtListWisataDeskripsi);
        mTxtBerakhir = findViewById(R.id.txtlistWisataBerakhir);
        recyclerView = findViewById(R.id.recListWisata);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        FirebaseDatabase dataEvent = FirebaseDatabase.getInstance();
        DatabaseReference event = dataEvent.getReference
                ("event/" + eventID);

        event.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String judul = dataSnapshot.child("nama").getValue().toString();
                String deskripsi = dataSnapshot.child("deskripsi").getValue().toString();
                String berakhir = dataSnapshot.child("berakhir").getValue().toString();
                String gambar = dataSnapshot.child("imgEvent").getValue().toString();

                Picasso.with(ListWisataActivity.this).load(gambar).into(mImageEvent);

                mtxtjudul.setText(judul);
                mTxtDeskripsi.setText(deskripsi);
                mTxtBerakhir.setText(berakhir);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        set(eventID);
        fetch(eventID);



    }

    private void set(String eventID) {


    }

    private void fetch(String eventID) {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("event/"+eventID+"/wisata");

        FirebaseRecyclerOptions<WisataModel> options =
                new FirebaseRecyclerOptions.Builder<WisataModel>()
                        .setQuery(query, new SnapshotParser<WisataModel>() {
                            @NonNull
                            @Override
                            public WisataModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new WisataModel(snapshot.child("nama").getValue().toString(),
                                        snapshot.child("id").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<WisataModel, ListWisataActivity.ViewHolder>(options) {
            @Override
            public ListWisataActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.wisata, parent, false);

                return new ListWisataActivity.ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ListWisataActivity.ViewHolder holder, final int position, WisataModel model) {


                final String WisataID = model.getId();

                holder.setTxtJudul(model.getnama());

                holder.WisataRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent wisata = new Intent(ListWisataActivity.this,WisataActivity.class);
                        wisata.putExtra("WisataID",WisataID);
                        startActivity(wisata);
                    }
                });
            }


        };
        recyclerView.setAdapter(adapter);

    }



    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout WisataRoot;
        public TextView txtJudul;
        public TextView txtWaktu;

        public ViewHolder(View itemView) {
            super(itemView);
            WisataRoot = itemView.findViewById(R.id.WisataRoot);
            txtJudul = itemView.findViewById(R.id.txtWisataJudul);

        }

        public void setTxtJudul(String string) {
            txtJudul.setText(string);
        }



    }




    }
