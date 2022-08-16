package com.example.petcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.petcare.databinding.ActivityLoginBinding;
import com.example.petcare.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //view binding
    private ActivityMainBinding binding;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //---------------------------------/
        //--------googleLogin--start--------/
        //---------------------------------/
        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        checkUser();

        //handle click logout
        binding.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });
        //---------------------------------/
        //----------googleLoginEnds--------/
        //---------------------------------/
    }

    private void checkUser() {
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser==null){
            //user not logged in
            startActivity(new Intent(this,Login.class));
            finish();
        }
        else {
            //user logged in
            //get user info
            String name="שלום "+firebaseUser.getDisplayName();
//            set name
            if(name!=null)
                binding.userName.setText(name);
            //set profile picture
            String personImage = firebaseUser.getPhotoUrl().toString();
            ImageView userImage = findViewById(R.id.profilePic);
//            Glide.with(this).load(personImage).into(userImage);
            if(personImage!=null)
                Glide.with(this).load(personImage).into(userImage);
        }
    }

    public void petList(View view) {
        Intent intent=new Intent(this,addPet.class);
        startActivity(intent);
    }

    public void displayPets(View view) {

        Intent intent=new Intent(this,displayPets.class);
        startActivity(intent);
    }

    public void addNewPet(View view) {
        Intent intent=new Intent(this,addPet.class);
        startActivity(intent);
    }

    public void vetSearch(View view) {
        Intent intent=new Intent(this,VetNearby.class);
        startActivity(intent);
    }

    public void shop_list(View view) {
        Intent intent=new Intent(this,ShoppingList.class);
        startActivity(intent);
    }

    public void newReminder(View view) {
        Intent intent=new Intent(this,Reminders.class);
        startActivity(intent);
    }
}