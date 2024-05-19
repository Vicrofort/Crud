package com.example.crud;

import android.app.Person;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crud.model.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private List<Persona> listPerson = new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;


    EditText nomP, userN, dirP, correoP, passP;
    ListView listV_personas;


    FirebaseDatabase  firebaseDatabase;
    DatabaseReference   databaseReference;
    Persona personaSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        nomP = findViewById(R.id.txt_nombrePersona);
        userN =findViewById(R.id.txt_userName);
        dirP = findViewById(R.id.txt_dirPersona);
        correoP =findViewById(R.id.txt_correoPersona);
        passP =findViewById(R.id.txt_Password);

        listV_personas = findViewById(R.id.lv_datosPersonas);
        inicializarFirebase();

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        inicializarFirebase();
        listarDatos();

        listV_personas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            personaSelected = (Persona) parent.getItemAtPosition(position);
                nomP.setText(personaSelected.getNombres());
                userN.setText(personaSelected.getUsername());
                dirP.setText(personaSelected.getDireccion());
                correoP.setText(personaSelected.getCorreo());
                passP.setText(personaSelected.getPassword());

            }
        });

    }

    public void toshop(View view) {
        Intent intent = new Intent(this, Productos.class);
        startActivity(intent);
    }
    public void aMapas(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
        public void toLogin(View view) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);

    }
    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPerson.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Persona p = objSnapshot.getValue(Persona.class);
                    listPerson.add(p);

                    arrayAdapterPersona = new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1, listPerson);
                    listV_personas.setAdapter(arrayAdapterPersona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }


     public static final int MENU_ADD_ID = R.id.icon_add; // String resource for "Add"
    public static final int MENU_DELETE_ID = R.id.icon_del; // String resource for "Delete"
    public static final int MENU_SAVE_ID = R.id.icon_save; // String resource for "Save"

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

String nombre =nomP.getText().toString();
String correo =correoP.getText().toString();
String userName =userN.getText().toString();
String pass =passP.getText().toString();
String dir =dirP.getText().toString();

        int itemId = item.getItemId();
        if (nombre.isEmpty()|| correo.isEmpty()||userName.isEmpty()||pass.isEmpty() ){
            validacion();
        } else {



            if (itemId == MENU_ADD_ID) {

                Persona p = new Persona();
                p.setUid(UUID.randomUUID().toString());
                p.setNombres(nombre);
                p.setUsername(userName);
                p.setCorreo(correo);
                p.setPassword(pass);
                p.setDireccion(dir);
                databaseReference.child("Persona").child(p.getUid()).setValue(p);
                Toast.makeText(this, "Usuario Agregado", Toast.LENGTH_SHORT).show();
             limpiarCajas();

            } else if (itemId == MENU_DELETE_ID) {
                Persona p = new Persona();
                p.setUid(personaSelected.getUid());
                databaseReference.child("Persona").child(p.getUid()).removeValue();
                Toast.makeText(this, "Eliminar", Toast.LENGTH_SHORT).show();
            } if (itemId == MENU_SAVE_ID) {

               Persona p = new Persona();
                p.setUid(personaSelected.getUid());
                p.setNombres(nomP.getText().toString().trim());
                p.setUsername(userN.getText().toString().trim());
                p.setCorreo(correoP.getText().toString().trim());
                p.setPassword(passP.getText().toString().trim());
                p.setDireccion(dirP.getText().toString().trim());
                databaseReference.child("Persona").child(p.getUid()).setValue(p);
                Toast.makeText(this, "Usuario Actualizado", Toast.LENGTH_SHORT).show();
                limpiarCajas();
            }
        }
            return super.onOptionsItemSelected(item);
        }

    private void limpiarCajas() {
        nomP.setText("");
        dirP.setText("");
        userN.setText("");
        correoP.setText("");
        passP.setText("");


    }


    private void validacion() {
        String nombre =nomP.getText().toString();
        String correo =correoP.getText().toString();
        String userName =userN.getText().toString();
        String pass =passP.getText().toString();

        if (nombre.isEmpty()){
            nomP.setError("Required");
        }
        else if  (userName.isEmpty()){
            userN.setError("Required");
        }
        else if (correo.isEmpty()){
            correoP.setError("Required");
        }

        else if  (pass.isEmpty()){
            passP.setError("Required");
        }


    }
}