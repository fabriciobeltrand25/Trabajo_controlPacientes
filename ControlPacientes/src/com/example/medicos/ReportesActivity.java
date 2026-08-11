package com.example.medicos;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ReportesActivity extends Activity {
    
    private Button btnPacientesMas, btnMedicosMas, btnMora, btnRecaudacion, btnLimpiar;
    private ListView lvReportes;
    private TextView tvTotales;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listaReportes;
    private DatabaseHelper dbHelper;
    private DecimalFormat df = new DecimalFormat("#.00");
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);
        
        dbHelper = new DatabaseHelper(this);
        
        btnPacientesMas = (Button) findViewById(R.id.btnPacientesMas);
        btnMedicosMas = (Button) findViewById(R.id.btnMedicosMas);
        btnMora = (Button) findViewById(R.id.btnMora);
        btnRecaudacion = (Button) findViewById(R.id.btnRecaudacion);
        btnLimpiar = (Button) findViewById(R.id.btnLimpiar);
        lvReportes = (ListView) findViewById(R.id.lvReportes);
        tvTotales = (TextView) findViewById(R.id.tvTotales);
        
        listaReportes = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaReportes);
        lvReportes.setAdapter(adapter);
        
        btnPacientesMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportePacientesMas();
            }
        });
        
        btnMedicosMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reporteMedicosMas();
            }
        });
        
        btnMora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportePacientesMora();
            }
        });
        
        btnRecaudacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reporteRecaudacion();
            }
        });
        
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarReportes();
            }
        });
        
        reportePacientesMas();
    }
    
    private void reportePacientesMas() {
        listaReportes.clear();
        tvTotales.setText("");
        listaReportes.add("Pacientes con más consultas");
        //listaReportes.add("-------------------");
        
        ArrayList<HashMap<String, String>> data = dbHelper.reportePacientesMasConsultas();
        if (data.isEmpty()) {
            listaReportes.add("No hay datos");
        } else {
            int i = 1;
            for (HashMap<String, String> p : data) {
                listaReportes.add((i++) + ". " + p.get("nombre") + " - " + p.get("total_consultas") + " consultas");
            }
        }
        adapter.notifyDataSetChanged();
    }
    
    private void reporteMedicosMas() {
        listaReportes.clear();
        tvTotales.setText("");
        listaReportes.add("Médicos con más consultas");
        //listaReportes.add("-------------------");
        
        ArrayList<HashMap<String, String>> data = dbHelper.reporteMedicosMasConsultas();
        if (data.isEmpty()) {
            listaReportes.add("No hay datos");
        } else {
            int i = 1;
            for (HashMap<String, String> m : data) {
                listaReportes.add((i++) + ". " + m.get("nombre") + " (" + m.get("especialidad") + ") - " + 
                                 m.get("total_consultas") + " consultas");
            }
        }
        adapter.notifyDataSetChanged();
    }
    
    private void reportePacientesMora() {
        listaReportes.clear();
        tvTotales.setText("");
        listaReportes.add("Pacientes en mora");
       // listaReportes.add("-------------------");
        
        ArrayList<HashMap<String, String>> data = dbHelper.reportePacientesEnMora();
        if (data.isEmpty()) {
            listaReportes.add("No hay pacientes en mora");
        } else {
            double totalMora = 0;
            int i = 1;
            for (HashMap<String, String> p : data) {
                double mora = Double.parseDouble(p.get("mora"));
                totalMora += mora;
                listaReportes.add((i++) + ". " + p.get("paciente") + " - L " + df.format(mora) + " mora");
            }
            //listaReportes.add("-------------------");
            listaReportes.add("Total mora: L " + df.format(totalMora));
        }
        adapter.notifyDataSetChanged();
    }
    
    private void reporteRecaudacion() {
        listaReportes.clear();
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        tvTotales.setText("");
        listaReportes.add(" Recaudación del día: " + fecha);
        //listaReportes.add("-------------------");
        
        HashMap<String, Object> data = dbHelper.reporteRecaudacionDia(fecha);

        int totalCobros = 0;
        double totalRecaudado = 0.0;
        double totalMora = 0.0;
        double totalBase = 0.0;

        if (data != null) {
            if (data.get("total_cobros") != null) {
                totalCobros = ((Number) data.get("total_cobros")).intValue();
            }
            if (data.get("total_recaudado") != null) {
                totalRecaudado = ((Number) data.get("total_recaudado")).doubleValue();
            }
            if (data.get("total_mora") != null) {
                totalMora = ((Number) data.get("total_mora")).doubleValue();
            }
            if (data.get("total_base") != null) {
                totalBase = ((Number) data.get("total_base")).doubleValue();
            }
        }
        
        if (totalCobros == 0) {
            listaReportes.add("No hay cobros registrados hoy");
        } else {
            listaReportes.add("Total recaudado: L " + df.format(totalRecaudado));
            listaReportes.add(" Valor base total: L " + df.format(totalBase));
            listaReportes.add(" Mora total: L " + df.format(totalMora));
            listaReportes.add(" Total cobros: " + totalCobros);

            tvTotales.setText("Total Recaudado: L " + df.format(totalRecaudado));
        }
        adapter.notifyDataSetChanged();
    }
    
    private void limpiarReportes() {
        listaReportes.clear();
        tvTotales.setText("");
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Reporte limpiado", Toast.LENGTH_SHORT).show();
    }
}