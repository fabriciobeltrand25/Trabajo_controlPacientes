package com.example.medicos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

public class MedicosActivity extends Activity {
    
    private EditText etCodigo, etNombre, etEspecialidad, etTelefono, etEmail, etBuscar;
    private Button btnGuardar, btnListar, btnBuscar, btnLimpiar;
    private ListView lvMedicos;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listaMedicos;
    private ArrayList<HashMap<String, String>> medicosData;
    private DatabaseHelper dbHelper;
    private boolean isEditing = false;
    private int idEditando = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicos);
        
        dbHelper = new DatabaseHelper(this);
        
        etCodigo = (EditText) findViewById(R.id.etCodigo);
        etNombre = (EditText) findViewById(R.id.etNombre);
        etEspecialidad = (EditText) findViewById(R.id.etEspecialidad);
        etTelefono = (EditText) findViewById(R.id.etTelefono);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etBuscar = (EditText) findViewById(R.id.etBuscar);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnListar = (Button) findViewById(R.id.btnListar);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnLimpiar = (Button) findViewById(R.id.btnLimpiar);
        lvMedicos = (ListView) findViewById(R.id.lvMedicos);
        
        listaMedicos = new ArrayList<String>();
        medicosData = new ArrayList<HashMap<String, String>>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaMedicos);
        lvMedicos.setAdapter(adapter);
        
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditing) {
                    actualizarMedico();
                } else {
                    guardarMedico();
                }
            }
        });
        
        btnListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listarMedicos();
            }
        });
        
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarMedico();
            }
        });
        
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarCampos();
            }
        });
        
        lvMedicos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mostrarOpciones(position);
            }
        });
        
        listarMedicos();
    }
    
    private void guardarMedico() {
        String codigo = etCodigo.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String especialidad = etEspecialidad.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        
        if (codigo.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(this, "Código y Nombre son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (dbHelper.insertarMedico(codigo, nombre, especialidad, telefono, email)) {
            Toast.makeText(this, "Médico guardado exitosamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            listarMedicos();
        } else {
            Toast.makeText(this, "Error: El código ya existe", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void listarMedicos() {
        listaMedicos.clear();
        medicosData.clear();
        medicosData = dbHelper.listarMedicos();
        
        if (medicosData.isEmpty()) {
            listaMedicos.add("No hay médicos registrados");
        } else {
            for (HashMap<String, String> m : medicosData) {
                listaMedicos.add(m.get("codigo") + " - " + m.get("nombre") + " (" + m.get("especialidad") + ")");
            }
        }
        adapter.notifyDataSetChanged();
    }
    
    private void buscarMedico() {
        String busqueda = etBuscar.getText().toString().trim();
        if (busqueda.isEmpty()) {
            listarMedicos();
            return;
        }
        
        listaMedicos.clear();
        medicosData.clear();
        medicosData = dbHelper.buscarMedicos(busqueda);
        
        if (medicosData.isEmpty()) {
            listaMedicos.add("Médico no encontrado");
        } else {
            for (HashMap<String, String> m : medicosData) {
                listaMedicos.add(m.get("codigo") + " - " + m.get("nombre") + " (" + m.get("especialidad") + ")");
            }
        }
        adapter.notifyDataSetChanged();
    }
    
    private void limpiarCampos() {
        etCodigo.setText("");
        etNombre.setText("");
        etEspecialidad.setText("");
        etTelefono.setText("");
        etEmail.setText("");
        etBuscar.setText("");
        isEditing = false;
        idEditando = 0;
        btnGuardar.setText("Guardar Médico");
        etCodigo.requestFocus();
    }
    
    private void mostrarOpciones(final int position) {
        final HashMap<String, String> medico = medicosData.get(position);
        String[] opciones = {"Editar", "Eliminar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opciones");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    editarMedico(medico);
                } else {
                    eliminarMedico(medico);
                }
            }
        });
        builder.show();
    }
    
    private void editarMedico(HashMap<String, String> medico) {
        isEditing = true;
        idEditando = Integer.parseInt(medico.get("id"));
        etCodigo.setText(medico.get("codigo"));
        etNombre.setText(medico.get("nombre"));
        etEspecialidad.setText(medico.get("especialidad"));
        etTelefono.setText(medico.get("telefono"));
        etEmail.setText(medico.get("email"));
        btnGuardar.setText("Actualizar Médico");
        etCodigo.requestFocus();
    }
    
    private void actualizarMedico() {
        String codigo = etCodigo.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String especialidad = etEspecialidad.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        
        if (dbHelper.actualizarMedico(idEditando, codigo, nombre, especialidad, telefono, email)) {
            Toast.makeText(this, "Médico actualizado", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            listarMedicos();
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void eliminarMedico(final HashMap<String, String> medico) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Médico");
        builder.setMessage("¿Está seguro de eliminar a " + medico.get("nombre") + "?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int id = Integer.parseInt(medico.get("id"));
                if (dbHelper.eliminarMedico(id)) {
                    Toast.makeText(MedicosActivity.this, "Médico eliminado", Toast.LENGTH_SHORT).show();
                    listarMedicos();
                } else {
                    Toast.makeText(MedicosActivity.this, "No se puede eliminar: tiene consultas asociadas", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
}