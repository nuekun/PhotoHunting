package com.nue.photohunting;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class ListEventActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_event);

        recyclerView = findViewById(R.id.recListEvent);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        fetch();

    }

    private void fetch() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("event");

        FirebaseRecyclerOptions<EventModel> options =
                new FirebaseRecyclerOptions.Builder<EventModel>()
                        .setQuery(query, new SnapshotParser<EventModel>() {
                            @NonNull
                            @Override
                            public EventModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new EventModel(snapshot.child("status").getValue().toString(),
                                        snapshot.child("nama").getValue().toString(),
                                        snapshot.child("mulai").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<EventModel, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.event, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, EventModel model) {




                    final String eventID = getRef(position).getKey();
                    final String status = model.getId();
                    holder.setTxtJudul(model.getNama());
                    holder.setTxtWaktu(model.getMulai());

                    holder.EventRoot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(status.equals("aktif")) {
                                Intent start = new Intent(ListEventActivity.this, ListWisataActivity.class);
                                start.putExtra("eventID", eventID);
                                startActivity(start);

                            }else{

                                Toast.makeText(ListEventActivity.this, "maaf event "+status,
                                        Toast.LENGTH_SHORT).show();
                            }

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
        adapter.stopListening();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
            public RelativeLayout EventRoot;
            public TextView txtJudul;
            public TextView txtWaktu;

            public ViewHolder(View itemView) {
                super(itemView);
                EventRoot = itemView.findViewById(R.id.EventRoot);
                txtJudul = itemView.findViewById(R.id.txtEventJudul);
                txtWaktu = itemView.findViewById(R.id.txtEventJam);

            }

            public void setTxtJudul(String string) {
                txtJudul.setText(string);
            }


            public void setTxtWaktu(String string) {
                txtWaktu.setText(string);
            }

        }

}
