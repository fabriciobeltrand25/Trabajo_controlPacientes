package com.example.medicos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

public class PacientesActivity extends Activity {
    
    private EditText etIdentidad, etNombre, etDireccion, etTelefono, etFechaNac, etBuscar;
    private Button btnGuardar, btnListar, btnBuscar, btnLimpiar;
    private ListView lvPacientes;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listaPacientes;
    private ArrayList<HashMap<String, String>> pacientesData;
    private DatabaseHelper dbHelper;
    private boolean isEditing = false;
    private int idEditando = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes);
        
        dbHelper = new DatabaseHelper(this);
        
        etIdentidad = (EditText) findViewById(R.id.etIdentidad);
        etNombre = (EditText) findViewById(R.id.etNombre);
        etDireccion = (EditText) findViewById(R.id.etDireccion);
        etTelefono = (EditText) findViewById(R.id.etTelefono);
        etFechaNac = (EditText) findViewById(R.id.etFechaNac);
        etBuscar = (EditText) findViewById(R.id.etBuscar);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnListar = (Button) findViewById(R.id.btnListar);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnLimpiar = (Button) findViewById(R.id.btnLimpiar);
        lvPacientes = (ListView) findViewById(R.id.lvPacientes);
        
        listaPacientes = new ArrayList<String>();
        pacientesData = new ArrayList<HashMap<String, String>>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaPacientes);
        lvPacientes.setAdapter(adapter);
        
        // ============ MÁSCARA PARA TELÉFONO HONDURAS ============
        etTelefono.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;
                isUpdating = true;
                
                // Eliminar todo excepto números
                String input = s.toString().replaceAll("[^0-9]", "");
                
                // Formato: 1234-5678 (8 dígitos)
                if (input.length() > 4) {
                    String parte1 = input.substring(0, 4);
                    String parte2 = input.substring(4, Math.min(input.length(), 8));
                    input = parte1 + "-" + parte2;
                }
                
                // Si excede 8 dígitos, truncar
                if (input.replaceAll("[^0-9]", "").length() > 8) {
                    input = input.substring(0, 9); // 4 dígitos + guión + 4 dígitos = 9
                }
                
                if (!input.equals(s.toString())) {
                    etTelefono.setText(input);
                    etTelefono.setSelection(input.length());
                }
                
                isUpdating = false;
            }
        });
        
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditing) {
                    actualizarPaciente();
                } else {
                    guardarPaciente();
                }
            }
        });
        
        btnListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listarPacientes();
            }
        });
        
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarPaciente();
            }
        });
        
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarCampos();
            }
        });
        
        lvPacientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mostrarOpciones(position);
            }
        });
        
        listarPacientes();
    }
    
    private void guardarPaciente() {
        String identidad = etIdentidad.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String fechaNac = etFechaNac.getText().toString().trim();
        
        if (identidad.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(this, "⚠️ Identidad y Nombre son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (dbHelper.insertarPaciente(identidad, nombre, direccion, telefono, fechaNac)) {
            Toast.makeText(this, " Paciente guardado exitosamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            listarPacientes();
        } else {
            Toast.makeText(this, " Error: La identidad ya existe", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void listarPacientes() {
        listaPacientes.clear();
        pacientesData.clear();
        pacientesData = dbHelper.listarPacientes();
        
        if (pacientesData.isEmpty()) {
            listaPacientes.add("No hay pacientes registrados");
        } else {
            for (HashMap<String, String> p : pacientesData) {
                listaPacientes.add(p.get("identidad") + " - " + p.get("nombre"));
            }
        }
        adapter.notifyDataSetChanged();
    }
    
    private void buscarPaciente() {
        String busqueda = etBuscar.getText().toString().trim();
        if (busqueda.isEmpty()) {
            listarPacientes();
            return;
        }
        
        listaPacientes.clear();
        pacientesData.clear();
        pacientesData = dbHelper.buscarPacientes(busqueda);
        
        if (pacientesData.isEmpty()) {
            listaPacientes.add("Paciente no encontrado");
        } else {
            for (HashMap<String, String> p : pacientesData) {
                listaPacientes.add(p.get("identidad") + " - " + p.get("nombre"));
            }
        }
        adapter.notifyDataSetChanged();
    }
    
    private void limpiarCampos() {
        etIdentidad.setText("");
        etNombre.setText("");
        etDireccion.setText("");
        etTelefono.setText("");
        etFechaNac.setText("");
        etBuscar.setText("");
        isEditing = false;
        idEditando = 0;
        btnGuardar.setText("Guardar Paciente");
        etIdentidad.requestFocus();
    }
    
    private void mostrarOpciones(final int position) {
        final HashMap<String, String> paciente = pacientesData.get(position);
        String[] opciones = {"Editar", "Eliminar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opciones");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    editarPaciente(paciente);
                } else {
                    eliminarPaciente(paciente);
                }
            }
        });
        builder.show();
    }
    
    private void editarPaciente(HashMap<String, String> paciente) {
        isEditing = true;
        idEditando = Integer.parseInt(paciente.get("id"));
        etIdentidad.setText(paciente.get("identidad"));
        etNombre.setText(paciente.get("nombre"));
        etDireccion.setText(paciente.get("direccion"));
        etTelefono.setText(paciente.get("telefono"));
        etFechaNac.setText(paciente.get("fecha_nacimiento"));
        btnGuardar.setText("Actualizar Paciente");
        etIdentidad.requestFocus();
    }
    
    private void actualizarPaciente() {
        String identidad = etIdentidad.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String fechaNac = etFechaNac.getText().toString().trim();
        
        if (dbHelper.actualizarPaciente(idEditando, identidad, nombre, direccion, telefono, fechaNac)) {
            Toast.makeText(this, "Paciente actualizado", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            listarPacientes();
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void eliminarPaciente(final HashMap<String, String> paciente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Paciente");
        builder.setMessage("¿Está seguro de eliminar a " + paciente.get("nombre") + "?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int id = Integer.parseInt(paciente.get("id"));
                if (dbHelper.eliminarPaciente(id)) {
                    Toast.makeText(PacientesActivity.this, "Paciente eliminado", Toast.LENGTH_SHORT).show();
                    listarPacientes();
                } else {
                    Toast.makeText(PacientesActivity.this, " No se puede eliminar: tiene consultas asociadas", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
}