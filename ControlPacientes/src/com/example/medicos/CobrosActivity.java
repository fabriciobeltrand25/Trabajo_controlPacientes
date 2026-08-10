package com.example.medicos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class CobrosActivity extends Activity {

    private EditText etIdentidad;
    private Button btnBuscar, btnPagar, btnLimpiar;
    private ListView lvCobros;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listaCobros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobros);

        etIdentidad = (EditText) findViewById(R.id.etIdentidad);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnPagar = (Button) findViewById(R.id.btnPagar);
        btnLimpiar = (Button) findViewById(R.id.btnLimpiar); // Botón para limpiar formulario
        lvCobros = (ListView) findViewById(R.id.lvCobros);

        listaCobros = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaCobros);
        lvCobros.setAdapter(adapter);

        // Acción de limpiar campos
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarCampos();
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaCobros.clear();
                listaCobros.add("No se encontraron cobros pendientes");
                adapter.notifyDataSetChanged();
            }
        });

        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CobrosActivity.this, "Seleccione un cobro activo para procesar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void limpiarCampos() {
        etIdentidad.setText("");
        listaCobros.clear();
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Campos limpiados", Toast.LENGTH_SHORT).show();
    }
}