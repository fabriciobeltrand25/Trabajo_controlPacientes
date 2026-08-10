package com.example.medicos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class ReportesActivity extends Activity {

    private Button btnPacientesMas, btnMedicosMas, btnMora, btnRecaudacion, btnLimpiar;
    private ListView lvReportes;
    private TextView tvTotales;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listaReportes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);

        btnPacientesMas = (Button) findViewById(R.id.btnPacientesMas);
        btnMedicosMas = (Button) findViewById(R.id.btnMedicosMas);
        btnMora = (Button) findViewById(R.id.btnMora);
        btnRecaudacion = (Button) findViewById(R.id.btnRecaudacion);
        btnLimpiar = (Button) findViewById(R.id.btnLimpiar); // Botón de limpiar
        lvReportes = (ListView) findViewById(R.id.lvReportes);
        tvTotales = (TextView) findViewById(R.id.tvTotales);

        listaReportes = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaReportes);
        lvReportes.setAdapter(adapter);

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarReportes();
            }
        });

        btnPacientesMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensajeVacio("Pacientes con más consultas");
            }
        });

        btnMedicosMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensajeVacio("Médicos con más consultas");
            }
        });

        btnMora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensajeVacio("Pacientes en mora");
            }
        });

        btnRecaudacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensajeVacio("Recaudación del día");
            }
        });
    }

    private void mostrarMensajeVacio(String titulo) {
        listaReportes.clear();
        tvTotales.setText("");
        listaReportes.add("📊 " + titulo);
        listaReportes.add("-------------------");
        listaReportes.add("Sin información registrada para este reporte");
        adapter.notifyDataSetChanged();
    }

    private void limpiarReportes() {
        listaReportes.clear();
        tvTotales.setText("");
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Reporte limpiado", Toast.LENGTH_SHORT).show();
    }
}