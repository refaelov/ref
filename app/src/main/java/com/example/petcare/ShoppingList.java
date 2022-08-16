package com.example.petcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.petcare.adapter.ListItemAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Model.ToDo;
import dmax.dialog.SpotsDialog;

public class ShoppingList extends AppCompatActivity {
    FirebaseUser firebaseUser;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference databaseUser;
    List<ToDo> toDoList=new ArrayList<>();
    FirebaseFirestore db;
    SpotsDialog dialog;
    RecyclerView listItem;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton floatingActionButton;

    public AppCompatEditText title;
    ListItemAdapter adapter;

    public boolean isUpdate=false; //flag to check if update title product or add a new product

    public String idUpdate = ""; //id of item we need to update


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        firebaseAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        db= FirebaseFirestore.getInstance();
        dialog=new SpotsDialog(this);
        title=(AppCompatEditText) findViewById(R.id.product);
        floatingActionButton=(FloatingActionButton) findViewById(R.id.add_product_btn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add a new product to todolist
                if (!isUpdate){
                    setData(title.getText().toString());
                }
                else {
                    updateData(title.getText().toString());
                }
            }
        });
        listItem=(RecyclerView) findViewById(R.id.list_todo);
        listItem.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        listItem.setLayoutManager(layoutManager);
        loadData();

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("מחק"))
            deleteItem(item.getOrder());
        return super.onContextItemSelected(item);
    }
//ToDoList
    private void deleteItem(int order) {
        db.collection(firebaseUser.getUid())
                .document(toDoList.get(order).getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        loadData();

                    }
                });
    }

    private void updateData(String title) {
        db.collection(firebaseUser.getUid()).document(idUpdate)
                .update("title",title)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ShoppingList.this,"עדכון התבצע",Toast.LENGTH_LONG).show();
                    }
                });

        //RealTime update refresh data
        db.collection(firebaseUser.getUid()).document(idUpdate)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        loadData();
                    }
                });
    }

    private void setData(String title) {
        //getting a new random id
        String id= UUID.randomUUID().toString();
        Map<String,Object>todo=new HashMap<>();
        todo.put("id",id);
        todo.put("title",title);

        db.collection(firebaseUser.getUid()).document(id)
                .set(todo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        loadData();
                    }
                });

    }

    private void loadData() {
        dialog.show();
        if(toDoList.size()>0)
            toDoList.clear();// remove the old values
        db.collection(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot documentSnapshot:task.getResult()){
                    ToDo toDo=new ToDo(documentSnapshot.getString("id"),
                            documentSnapshot.getString("title"));
                    toDoList.add(toDo);
                }
                adapter=new ListItemAdapter(ShoppingList.this,toDoList);
                listItem.setAdapter(adapter);
                dialog.dismiss();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShoppingList.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }
}