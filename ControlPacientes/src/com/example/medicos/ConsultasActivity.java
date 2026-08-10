package com.example.medicos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;

public class ConsultasActivity extends Activity {

    private Spinner spPacientes, spMedicos;
    private EditText etFecha, etHora;
    private Button btnAsignar, btnListar, btnFinalizar, btnLimpiar;
    private ListView lvConsultas;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listaConsultas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultas);

        spPacientes = (Spinner) findViewById(R.id.spPacientes);
        spMedicos = (Spinner) findViewById(R.id.spMedicos);
        etFecha = (EditText) findViewById(R.id.etFecha);
        etHora = (EditText) findViewById(R.id.etHora);
        btnAsignar = (Button) findViewById(R.id.btnAsignar);
        btnListar = (Button) findViewById(R.id.btnListar);
        btnFinalizar = (Button) findViewById(R.id.btnFinalizar);
        btnLimpiar = (Button) findViewById(R.id.btnLimpiar); // Botón de limpiar activo
        lvConsultas = (ListView) findViewById(R.id.lvConsultas);

        listaConsultas = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaConsultas);
        lvConsultas.setAdapter(adapter);

        // Carga de opciones visuales por defecto
        cargarSpinnersDemostracion();

        // Botón Limpiar Campos
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarCampos();
            }
        });

        // Eventos neutros para el resto de botones
        btnAsignar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ConsultasActivity.this, "Módulo de asignación en revisión", Toast.LENGTH_SHORT).show();
            }
        });

        btnListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaConsultas.clear();
                listaConsultas.add("No hay consultas registradas");
                adapter.notifyDataSetChanged();
            }
        });

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ConsultasActivity.this, "Función de finalización no disponible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarSpinnersDemostracion() {
        ArrayList<String> pac = new ArrayList<String>();
        pac.add("Seleccione Paciente...");
        ArrayAdapter<String> adapterPac = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pac);
        spPacientes.setAdapter(adapterPac);

        ArrayList<String> med = new ArrayList<String>();
        med.add("Seleccione Médico...");
        ArrayAdapter<String> adapterMed = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, med);
        spMedicos.setAdapter(adapterMed);
    }

    private void limpiarCampos() {
        if (spPacientes != null && spPacientes.getAdapter() != null) spPacientes.setSelection(0);
        if (spMedicos != null && spMedicos.getAdapter() != null) spMedicos.setSelection(0);
        etFecha.setText("");
        etHora.setText("");
        listaConsultas.clear();
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Campos limpiados", Toast.LENGTH_SHORT).show();
    }
}