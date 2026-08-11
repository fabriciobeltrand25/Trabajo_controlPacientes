package com.example.medicos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class CobrosActivity extends Activity {
    
    private Spinner spPacientes;
    private Button btnBuscar, btnPagar, btnLimpiar;
    private ListView lvCobros;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listaCobros;
    private ArrayList<HashMap<String, String>> cobrosData;
    private ArrayList<HashMap<String, String>> pacientesData;
    private DatabaseHelper dbHelper;
    private DecimalFormat df = new DecimalFormat("#.00");
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobros);
        
        dbHelper = new DatabaseHelper(this);
        
        spPacientes = (Spinner) findViewById(R.id.spPacientes);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnPagar = (Button) findViewById(R.id.btnPagar);
        btnLimpiar = (Button) findViewById(R.id.btnLimpiar);
        lvCobros = (ListView) findViewById(R.id.lvCobros);
        
        listaCobros = new ArrayList<String>();
        cobrosData = new ArrayList<HashMap<String, String>>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaCobros);
        lvCobros.setAdapter(adapter);
        
        // Cargar pacientes en el Spinner al iniciar
        cargarPacientesSpinner();
        
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarCobros();
            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spPacientes.getAdapter() != null && spPacientes.getAdapter().getCount() > 0) {
                    spPacientes.setSelection(0);
                }
                listaCobros.clear();
                cobrosData.clear();
                adapter.notifyDataSetChanged();
            }
        });
        
        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cobrosData.isEmpty()) {
                    Toast.makeText(CobrosActivity.this, "No hay cobros pendientes", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String[] opciones = new String[cobrosData.size()];
                for (int i = 0; i < cobrosData.size(); i++) {
                    HashMap<String, String> cobro = cobrosData.get(i);
                    opciones[i] = cobro.get("medico") + " - L " + cobro.get("total");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(CobrosActivity.this);
                builder.setTitle("Seleccione cobro a pagar");
                builder.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mostrarPago(cobrosData.get(which));
                    }
                });
                builder.show();
            }
        });
        
        lvCobros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!cobrosData.isEmpty()) {
                    mostrarPago(cobrosData.get(position));
                }
            }
        });
    }

    private void cargarPacientesSpinner() {
        pacientesData = dbHelper.obtenerTodosPacientes(); 
        ArrayList<String> nombresPacientes = new ArrayList<String>();
        nombresPacientes.add("Seleccione un paciente...");
        
        if (pacientesData != null) {
            for (HashMap<String, String> p : pacientesData) {
                // Formato de presentación: "1001 - Juan Perez"
                nombresPacientes.add(p.get("identidad") + " - " + p.get("nombre"));
            }
        }
        
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nombresPacientes);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPacientes.setAdapter(adapterSpinner);
    }
    
    private void buscarCobros() {
        int posicion = spPacientes.getSelectedItemPosition();
        if (posicion <= 0 || pacientesData == null || pacientesData.isEmpty()) {
            Toast.makeText(this, "Seleccione un paciente válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtiene la identidad directamente del objeto cargado en la posición exacta
        String identidad = pacientesData.get(posicion - 1).get("identidad");
        
        listaCobros.clear();
        cobrosData.clear();
        cobrosData = dbHelper.buscarCobrosPendientes(identidad);
        
        if (cobrosData.isEmpty()) {
            listaCobros.add("No hay cobros pendientes para este paciente");
        } else {
            for (HashMap<String, String> c : cobrosData) {
                listaCobros.add("" + c.get("paciente") + " - " + c.get("medico") + 
                               " (L " + c.get("total") + ")");
            }
        }
        adapter.notifyDataSetChanged();
    }
    
    private void mostrarPago(final HashMap<String, String> cobro) {
        final double total = Double.parseDouble(cobro.get("total"));
        final int idConsulta = Integer.parseInt(cobro.get("id"));
        final double valorBase = Double.parseDouble(cobro.get("valor_consulta"));
        final double mora = Double.parseDouble(cobro.get("mora"));
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pagar Consulta");
        builder.setMessage("Paciente: " + cobro.get("paciente") + 
                          "\nMédico: " + cobro.get("medico") +
                          "\nValor: L " + df.format(valorBase) +
                          "\nMora: L " + df.format(mora) +
                          "\n\nTotal a pagar: L " + df.format(total));
        
        final EditText input = new EditText(this);
        input.setHint("Ingrese monto a pagar");
        builder.setView(input);
        
        builder.setPositiveButton("Pagar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String montoStr = input.getText().toString().trim();
                if (montoStr.isEmpty()) {
                    Toast.makeText(CobrosActivity.this, "Ingrese el monto", Toast.LENGTH_SHORT).show();
                    return;
                }
                double monto = Double.parseDouble(montoStr);
                if (monto < total) {
                    Toast.makeText(CobrosActivity.this, "El pago debe ser mayor o igual a L " + df.format(total), Toast.LENGTH_SHORT).show();
                    return;
                }
                double cambio = monto - total;
                if (dbHelper.registrarCobro(idConsulta, valorBase, mora, monto, cambio)) {
                    String mensaje = "Pago registrado exitosamente";
                    if (cambio > 0) {
                        mensaje += "\nCambio: L " + df.format(cambio);
                    }
                    Toast.makeText(CobrosActivity.this, mensaje, Toast.LENGTH_LONG).show();
                    buscarCobros();
                } else {
                    Toast.makeText(CobrosActivity.this, "Error al registrar pago", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}


