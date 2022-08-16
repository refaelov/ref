package com.example.petcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class displayPets extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    DisplayAdapter displayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_pets);
        firebaseAuth=FirebaseAuth.getInstance();

        recyclerView=(RecyclerView)findViewById(R.id.rv);//display the pets in recylerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();


        FirebaseRecyclerOptions<DisplayModel> options=new FirebaseRecyclerOptions.Builder<DisplayModel>().setQuery(FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()),DisplayModel.class).build();
        displayAdapter=new DisplayAdapter(options);
        recyclerView.setAdapter(displayAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        displayAdapter.stopListening();
    }
    public void addNewPet(View view) {
        Intent intent=new Intent(this,addPet.class);
        startActivity(intent);
    }




}