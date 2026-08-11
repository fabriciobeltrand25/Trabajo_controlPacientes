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
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

public class ConsultasActivity extends Activity {
    
    private Spinner spPacientes, spMedicos;
    private EditText etFecha, etHora;
    private Button btnAsignar, btnListar, btnFinalizar;
    private ListView lvConsultas;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listaConsultas;
    private ArrayList<HashMap<String, String>> consultasData;
    private DatabaseHelper dbHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultas);
        
        dbHelper = new DatabaseHelper(this);
        
        spPacientes = (Spinner) findViewById(R.id.spPacientes);
        spMedicos = (Spinner) findViewById(R.id.spMedicos);
        etFecha = (EditText) findViewById(R.id.etFecha);
        etHora = (EditText) findViewById(R.id.etHora);
        btnAsignar = (Button) findViewById(R.id.btnAsignar);
        btnListar = (Button) findViewById(R.id.btnListar);
        btnFinalizar = (Button) findViewById(R.id.btnFinalizar);
        lvConsultas = (ListView) findViewById(R.id.lvConsultas);
        
        listaConsultas = new ArrayList<String>();
        consultasData = new ArrayList<HashMap<String, String>>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaConsultas);
        lvConsultas.setAdapter(adapter);
        
        // Fecha actual por defecto
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        etFecha.setText(sdf.format(new java.util.Date()));
        
        cargarPacientes();
        cargarMedicos();
        
        btnAsignar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asignarConsulta();
            }
        });
        
        btnListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listarConsultas("todas");
            }
        });
        
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listarConsultas("Activa");
            }
        });
        
        lvConsultas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final HashMap<String, String> consulta = consultasData.get(position);
                if (consulta.get("estado").equals("Activa")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ConsultasActivity.this);
                    builder.setTitle("Finalizar Consulta");
                    builder.setMessage("¿Finalizar consulta de " + consulta.get("paciente") + "?");
                    builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int idConsulta = Integer.parseInt(consulta.get("id"));
                            if (dbHelper.finalizarConsulta(idConsulta)) {
                                Toast.makeText(ConsultasActivity.this, "Consulta finalizada", Toast.LENGTH_SHORT).show();
                                listarConsultas("todas");
                            } else {
                                Toast.makeText(ConsultasActivity.this, "Error al finalizar", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("No", null);
                    builder.show();
                } else {
                    Toast.makeText(ConsultasActivity.this, "Esta consulta ya está finalizada", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        listarConsultas("todas");
    }
    
    private void cargarPacientes() {
        ArrayList<HashMap<String, String>> pacientes = dbHelper.listarPacientes();
        ArrayList<String> items = new ArrayList<String>();
        items.add("Seleccione Paciente...");
        for (HashMap<String, String> p : pacientes) {
            items.add(p.get("identidad") + " - " + p.get("nombre"));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPacientes.setAdapter(adapter);
    }
    
    private void cargarMedicos() {
        ArrayList<HashMap<String, String>> medicos = dbHelper.listarMedicos();
        ArrayList<String> items = new ArrayList<String>();
        items.add("Seleccione Médico...");
        for (HashMap<String, String> m : medicos) {
            items.add(m.get("nombre") + " - " + m.get("especialidad"));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMedicos.setAdapter(adapter);
    }
    
    private void asignarConsulta() {
        int posPaciente = spPacientes.getSelectedItemPosition();
        int posMedico = spMedicos.getSelectedItemPosition();
        
        if (posPaciente == 0 || posMedico == 0) {
            Toast.makeText(this, "Seleccione paciente y médico", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();
        
        if (fecha.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, "Ingrese fecha y hora", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Obtener IDs
        ArrayList<HashMap<String, String>> pacientes = dbHelper.listarPacientes();
        int idPaciente = Integer.parseInt(pacientes.get(posPaciente - 1).get("id"));
        
        ArrayList<HashMap<String, String>> medicos = dbHelper.listarMedicos();
        int idMedico = Integer.parseInt(medicos.get(posMedico - 1).get("id"));
        
        if (dbHelper.insertarConsulta(idPaciente, idMedico, fecha, hora)) {
            Toast.makeText(this, "Consulta asignada exitosamente", Toast.LENGTH_SHORT).show();
            listarConsultas("todas");
        } else {
            Toast.makeText(this, "El paciente tiene una consulta activa o un pago pendiente en Cobros", Toast.LENGTH_LONG).show();
        }
        
        
    }
    
    private void listarConsultas(String estado) {
        listaConsultas.clear();
        consultasData.clear();
        consultasData = dbHelper.listarConsultas(estado);
        
        if (consultasData.isEmpty()) {
            listaConsultas.add("No hay consultas");
        } else {
            for (HashMap<String, String> c : consultasData) {
                String icono = c.get("estado").equals("Activa") ? "🟢" : "🔴";
                listaConsultas.add(icono + " " + c.get("paciente") + " - " + 
                                 c.get("medico") + " (" + c.get("fecha_consulta") + ")");
            }
        }
        adapter.notifyDataSetChanged();
    }
}