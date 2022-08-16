package com.example.petcare;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class DisplayAdapter extends FirebaseRecyclerAdapter<DisplayModel,DisplayAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     */
    public DisplayAdapter(@NonNull FirebaseRecyclerOptions<DisplayModel> options) {
        super(options);
    }
    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull DisplayModel model) {
        //display pet//
        //name,age and picture
        holder.name.setText(model.getPetName());//getting pet name
        holder.petAge.setText("גיל :"+getPetYear(model.getBornDate())+"."+getPetMonth(model.getBornDate()));//getting pet age
        final StorageReference mStorageRef= FirebaseStorage.getInstance().getReference(model.getPetImgUrl());   //get the pet imgURL
        try {
            final File localFile= File.createTempFile("temp","png");
            mStorageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                Glide.with(holder.img.getContext());
                holder.img.setImageBitmap(bitmap); //set img after successful creation file
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("cant load",model.getPetImgUrl());
                }
            });

        }catch (IOException e){
            Log.d("cant load",model.getPetImgUrl());

        }
        //edit Button
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus=DialogPlus.newDialog(holder.img.getContext()).setContentHolder(new ViewHolder(R.layout.update_popup)).setExpanded(true,1200).create();
                View view1=dialogPlus.getHolderView();

                EditText name=view1.findViewById(R.id.txt_name);
                EditText type=view1.findViewById(R.id.txt_type);

                Button btnUpdate=view1.findViewById(R.id.btn_update);

                name.setText(model.getPetName());
                type.setText(model.getPetType());
                dialogPlus.show();

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> map=new HashMap<>();
                        map.put("petName",name.getText().toString());
                        map.put("petType",type.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child(getRef(position).getKey()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(holder.name.getContext(),"עדכון התבצע",Toast.LENGTH_LONG).show();
                                dialogPlus.dismiss();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(holder.name.getContext(),"אירעה שגיאה במהלך העדכון",Toast.LENGTH_LONG).show();
                                        dialogPlus.dismiss();
                                    }
                                });
                    }
                });
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(holder.name.getContext());
                builder.setTitle("האם אתה בטוח?");
                builder.setMessage("מידע שנמחק לא יהיה ניתן להחזיר בעתיד.");
                builder.setPositiveButton("מחק", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child(getRef(position).getKey()).removeValue();

                    }
                });
                builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.name.getContext(),"התבטל",Toast.LENGTH_LONG).show();
                    }
                });
                builder.show();
            }
        });
        holder.btnInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus=DialogPlus.newDialog(holder.img.getContext()).setContentHolder(new ViewHolder(R.layout.display_pet_info_popup)).setExpanded(true,1200).create();
                final Calendar myCalendar= Calendar.getInstance();

                View view1=dialogPlus.getHolderView();
                TextView name=view1.findViewById(R.id.pet_name);
                TextView age=view1.findViewById(R.id.pet_age);
                TextView type=view1.findViewById(R.id.pet_type);
                TextView vaccines=view1.findViewById(R.id.pet_vaccines);
                EditText cal=view1.findViewById(R.id.vaccine_date);
                EditText vaccineName=view1.findViewById(R.id.vaccine_name);
                Button buttonAddNewVaccine=view1.findViewById(R.id.add_vaccine_btn);


                name.setText("שם: "+ model.getPetName());
                age.setText("גיל "+ getPetYear(model.getBornDate())+"."+getPetMonth(model.getBornDate()));
                type.setText("סוג החיה: "+model.getPetType());

                if(model.getPetType().equals("כלב")){
                    vaccines.setText("רשימת חיסונים: "+model.getVaccineRecord());
                }
                else
                    vaccines.setText("");
                dialogPlus.show();

                buttonAddNewVaccine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> map=new HashMap<>();
                        Vaccine vaccine=new Vaccine(new Date(),vaccineName.toString());
                        map.put("petVaccineRecord",vaccine);

                        FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child(getRef(position).getKey()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.name.getContext(),"עדכון התבצע",Toast.LENGTH_LONG).show();
                                        dialogPlus.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(holder.name.getContext(),"אירעה שגיאה במהלך העדכון",Toast.LENGTH_LONG).show();
                                        dialogPlus.dismiss();
                                    }
                                });
                    }
                });


            }


        });
    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        CircleImageView img;
        TextView name,petAge;
        Button btnEdit,btnDelete,btnChangeImg,btnInfo;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            //
            img=(CircleImageView) itemView.findViewById(R.id.img1);
            petAge=(TextView) itemView.findViewById(R.id.pet_age);
            name=(TextView) itemView.findViewById(R.id.pet_name);
            btnEdit=(Button) itemView.findViewById(R.id.btn_edit);
            btnDelete=(Button) itemView.findViewById(R.id.btn_delete);
            btnChangeImg=(Button) itemView.findViewById(R.id.img_change);
            btnInfo = (Button) itemView.findViewById(R.id.btn_info);
        }
        public void downloadImage(String imgUrl){
            final StorageReference mStorageRef= FirebaseStorage.getInstance().getReference(imgUrl);
            try {
                final File localFile= File.createTempFile("temp","png");
                mStorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        img.setImageBitmap(bitmap);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }catch (IOException e){

            }
        }
    }
    //calculate the pet age ,month
    public static int getPetMonth(Date bornDate){
        Date today = new Date();
        if(bornDate.getMonth()==today.getMonth())
            return 0;
        return  (12-(Math.abs(bornDate.getMonth()-today.getMonth())));
    }
    //calculate the pet age ,year
    public static int getPetYear(Date bornDate){
        Date today = new Date();
        if(today.getYear()-1==bornDate.getYear())
            return 0;
        return  today.getYear()-bornDate.getYear();
    }



}
