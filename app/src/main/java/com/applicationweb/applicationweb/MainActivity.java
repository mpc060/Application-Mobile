package com.applicationweb.applicationweb;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.applicationweb.applicationweb.modelo.Pesssoa;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText editNome, editOrdem, editStatus;
    ListView listV_dados;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Pesssoa> listPessoa = new ArrayList<Pesssoa>();
    private ArrayAdapter<Pesssoa> arrayAdapterPesssoa;

    Pesssoa pessoaSelecionada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editNome = (EditText)findViewById(R.id.editNome);
        editOrdem = (EditText)findViewById(R.id.editOrdem);
        editStatus = (EditText)findViewById(R.id.editStatus);
        listV_dados = (ListView)findViewById(R.id.listV_dados);

        inicializarFirebase();
        eventoDatabase();

        listV_dados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pessoaSelecionada = (Pesssoa)parent.getItemAtPosition(position);
                editNome.setText(pessoaSelecionada.getNome());
                editOrdem.setText(pessoaSelecionada.getOrdemServico());
                editStatus.setText(pessoaSelecionada.getStatus());
            }
        });

    }

    private void eventoDatabase() {

        databaseReference.child("Pessoa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPessoa.clear();
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    Pesssoa p = objSnapshot.getValue(Pesssoa.class);
                    listPessoa.add(p);

                }
                arrayAdapterPesssoa = new ArrayAdapter<Pesssoa>(MainActivity.this,
                        android.R.layout.simple_list_item_1, listPessoa);

                listV_dados.setAdapter((arrayAdapterPesssoa));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {

        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.menu_novo){
            Pesssoa p = new Pesssoa();
            p.setId(UUID.randomUUID().toString());
            p.setNome(editNome.getText().toString());
            p.setOrdemServico(editOrdem.getText().toString());
            p.setStatus(editStatus.getText().toString());
            databaseReference.child("Pessoa").child(p.getId()).setValue(p);
            limparCampos();
        }else if(id == R.id.menu_atualizar){
            Pesssoa p = new Pesssoa();
            p.setId(pessoaSelecionada.getId());
            p.setNome(editNome.getText().toString().trim());
            p.setStatus(editStatus.getText().toString().trim());
            p.setOrdemServico(editOrdem.getText().toString().trim());
            databaseReference.child("Pessoa").child(p.getId()).setValue(p);
            limparCampos();
        }else if(id == R.id.menu_excluir){
            Pesssoa p = new Pesssoa();
            p.setId(pessoaSelecionada.getId());
            databaseReference.child("Pessoa").child(p.getId()).removeValue();
            limparCampos();

        }

        return true;
    }

    private void limparCampos() {

        editStatus.setText("");
        editOrdem.setText("");
        editNome.setText("");
    }
}
