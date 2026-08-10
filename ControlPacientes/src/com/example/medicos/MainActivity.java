package com.example.medicos;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    
   
    private LinearLayout btnPacientes, btnMedicos, btnConsultas, btnCobros, btnReportes, btnSalir;
    private SharedPreferences prefs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        
        
        btnPacientes = (LinearLayout) findViewById(R.id.btnPacientes);
        btnMedicos = (LinearLayout) findViewById(R.id.btnMedicos);
        btnConsultas = (LinearLayout) findViewById(R.id.btnConsultas);
        btnCobros = (LinearLayout) findViewById(R.id.btnCobros);
        btnReportes = (LinearLayout) findViewById(R.id.btnReportes);
        btnSalir = (LinearLayout) findViewById(R.id.btnSalir);
        
        btnPacientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PacientesActivity.class));
            }
        });
        
        btnMedicos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MedicosActivity.class));
            }
        });
        
        btnConsultas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ConsultasActivity.class));
            }
        });
        
        btnCobros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CobrosActivity.class));
            }
        });
        
        btnReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ReportesActivity.class));
            }
        });
        
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putBoolean("logueado", false).apply();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}