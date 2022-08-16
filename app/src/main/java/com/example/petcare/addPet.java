package com.example.petcare;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.petcare.databinding.ActivityAddPetBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class addPet extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    ImageView petPic;
    EditText petName;
    Button button;
    final String randomKey= UUID.randomUUID().toString(); //random uuid for pet image
    FirebaseAuth firebaseAuth;
    private ActivityAddPetBinding binding;
    TextView petType;
    final Calendar myCalendar= Calendar.getInstance();
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);
        button=findViewById(R.id.button2);
        Spinner spinner=findViewById(R.id.petOptionSpinner);
        petType= (TextView)spinner.getSelectedView();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this,R.array.pets, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        petName=findViewById(R.id.petName);
        //---------------------------------/
        //--------googleLogin--start--------/
        //---------------------------------/
        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        editText=(EditText) findViewById(R.id.Birthday);
        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(addPet.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        petPic=findViewById(R.id.petPic);
        petPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            imageUri=data.getData();
            petPic.setImageURI(imageUri);
        }
    }
    //upload the selected picture to db
    private void uplodaPicture() {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle("מעלה תמונה...");
        pd.show();
        StorageReference riversRef=storageReference.child("/image"+randomKey);

        riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content),"התמונה הועלתה",Snackbar.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),"שגיאה",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent=(100.00*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        pd.setMessage((int)progressPercent+"%");
                    }
                });
    }
    //choose picture to upload
    public void choosePicture() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    //get date
    private void updateLabel(){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(dateFormat.format(myCalendar.getTime()));
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ((TextView) view).setTextColor(Color.BLACK);
        String petType= adapterView.getItemAtPosition(i).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
    public void createPet(View view) {
        //getting the pet type from spinner (user choice)
        Spinner spinner=findViewById(R.id.petOptionSpinner);
        String petType = spinner.getSelectedItem().toString();
        //making the pet object according the pet type
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=database.getReference(firebaseAuth.getCurrentUser().getUid()).push();
        switch (petType)
        {
            case "כלב":
                Dog dog=new Dog(petType,petName.getText().toString(),myCalendar.getTime(),randomKey);
                databaseReference.setValue(dog);
                Log.d("vvvvvv",dog.getVaccineRecord().toString());
                Toast.makeText(addPet.this," החיה נוספה בהצלחה",Toast.LENGTH_LONG).show();
                if(imageUri!=null)
                    uplodaPicture();
                finish();
                break;
            case "חתול":
                Cat cat=new Cat(petType,petName.getText().toString(),myCalendar.getTime(),randomKey);
                databaseReference.setValue(cat);
                if(imageUri!=null)
                    uplodaPicture();
                finish();
                break;
            case "דג":
                Fish fish=new Fish(petType,petName.getText().toString(),myCalendar.getTime(),randomKey);
                databaseReference.setValue(fish);
                if(imageUri!=null)
                    uplodaPicture();
                finish();
                break;
            case "תוכי":
                Parrot parrot=new Parrot(petType,petName.getText().toString(),myCalendar.getTime(),randomKey);
                databaseReference.setValue(parrot);
                if(imageUri!=null)
                    uplodaPicture();
                finish();
                break;
            default:
                Toast.makeText(addPet.this,"חובה לבחור את סוג החיה",Toast.LENGTH_LONG).show();
        }
    }
}