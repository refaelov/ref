package com.example.petcare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petcare.databinding.ActivityAddPetBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class addPet extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText petName;
    Button button;
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
    }

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
                Dog dog=new Dog(petType,petName.getText().toString(),myCalendar.getTime());
                //!!!!!!!!!!add code to check if pet is already exist
                databaseReference.setValue(dog);
                Toast.makeText(addPet.this," החיה נוספה בהצלחה",Toast.LENGTH_LONG).show();
                finish();
                break;
            case "חתול":
                Cat cat=new Cat(petType,petName.getText().toString(),myCalendar.getTime());
                databaseReference.setValue(cat);
                break;
            case "דג":
                Fish fish=new Fish(petType,petName.getText().toString(),myCalendar.getTime());
                databaseReference.setValue(fish);
                break;
            case "תוכי":
                Parrot parrot=new Parrot(petType,petName.getText().toString(),myCalendar.getTime());
                databaseReference.setValue(parrot);
                break;
            default:
                Toast.makeText(addPet.this,"חובה לבחור את סוג החיה",Toast.LENGTH_LONG).show();


        }

    }

}