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
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class ConsultasActivity extends Activity {
    
    private Spinner spPacientes, spMedicos;
    private EditText etFecha, etHora;
    private Button btnAsignar, btnActivas, btnFinalizadas;
    private ListView lvConsultas;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listaConsultas;
    private ArrayList<HashMap<String, String>> consultasData;
    private DatabaseHelper dbHelper;
    private String estadoActual = "Activa";
    
    private static final Pattern PATTERN_HORA = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    
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
        btnActivas = (Button) findViewById(R.id.btnActivas);
        btnFinalizadas = (Button) findViewById(R.id.btnFinalizadas);
        lvConsultas = (ListView) findViewById(R.id.lvConsultas);
        
        listaConsultas = new ArrayList<String>();
        consultasData = new ArrayList<HashMap<String, String>>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaConsultas);
        lvConsultas.setAdapter(adapter);
        
        // Fecha actual por defecto
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        etFecha.setText(sdf.format(new java.util.Date()));
        
        // ============ MÁSCARA PARA FECHA (YYYY-MM-DD) ============
        etFecha.addTextChangedListener(new TextWatcher() {
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
                
                // Formato: YYYY-MM-DD (8 dígitos)
                if (input.length() > 4) {
                    String anio = input.substring(0, 4);
                    String resto = input.substring(4);
                    if (resto.length() > 2) {
                        String mes = resto.substring(0, 2);
                        String dia = resto.substring(2, Math.min(resto.length(), 4));
                        input = anio + "-" + mes + "-" + dia;
                    } else {
                        input = anio + "-" + resto;
                    }
                }
                
                // Limitar a 8 dígitos (YYYYMMDD)
                String soloNumeros = input.replaceAll("[^0-9]", "");
                if (soloNumeros.length() > 8) {
                    soloNumeros = soloNumeros.substring(0, 8);
                    if (soloNumeros.length() > 4) {
                        String a = soloNumeros.substring(0, 4);
                        String resto = soloNumeros.substring(4);
                        if (resto.length() > 2) {
                            String m = resto.substring(0, 2);
                            String d = resto.substring(2, Math.min(resto.length(), 4));
                            input = a + "-" + m + "-" + d;
                        } else {
                            input = a + "-" + resto;
                        }
                    } else {
                        input = soloNumeros;
                    }
                }
                
                if (!input.equals(s.toString())) {
                    etFecha.setText(input);
                    etFecha.setSelection(input.length());
                }
                
                isUpdating = false;
            }
        });
        
        // ============ MÁSCARA PARA HORA ============
        etHora.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;
                isUpdating = true;
                
                String input = s.toString().replaceAll("[^0-9]", "");
                
                if (input.length() >= 4) {
                    String horas = input.substring(0, Math.min(2, input.length() - 2));
                    String minutos = input.substring(Math.max(0, input.length() - 2));
                    
                    // Validar que horas sea 00-23 y minutos 00-59
                    try {
                        int h = Integer.parseInt(horas);
                        int m = Integer.parseInt(minutos);
                        if (h >= 0 && h <= 23 && m >= 0 && m <= 59) {
                            input = horas + ":" + minutos;
                        }
                    } catch (Exception e) {}
                }
                
                if (!input.equals(s.toString())) {
                    etHora.setText(input);
                    etHora.setSelection(input.length());
                }
                
                isUpdating = false;
            }
        });
        
        cargarPacientes();
        cargarMedicos();
        
        btnAsignar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asignarConsulta();
            }
        });
        
        btnActivas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estadoActual = "Activa";
                listarConsultas("Activa");
            }
        });
        
        btnFinalizadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estadoActual = "Finalizada";
                listarConsultas("Finalizada");
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
                                Toast.makeText(ConsultasActivity.this, "✅ Consulta finalizada", Toast.LENGTH_SHORT).show();
                                listarConsultas(estadoActual.equals("todas") ? "todas" : estadoActual);
                            } else {
                                Toast.makeText(ConsultasActivity.this, "❌ Error al finalizar", Toast.LENGTH_SHORT).show();
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
        
        // Cargar consultas activas por defecto
        estadoActual = "Activa";
        listarConsultas("Activa");
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
    
    // ============ VALIDAR FORMATO DE FECHA ============
    private boolean validarFecha(String fecha) {
        if (!fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        String[] partes = fecha.split("-");
        int anio = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        int dia = Integer.parseInt(partes[2]);
        return anio >= 1900 && anio <= 2100 && mes >= 1 && mes <= 12 && dia >= 1 && dia <= 31;
    }
    
    // ============ VALIDAR FORMATO DE HORA ============
    private boolean validarHora(String hora) {
        if (!PATTERN_HORA.matcher(hora).matches()) {
            return false;
        }
        String[] partes = hora.split(":");
        int horas = Integer.parseInt(partes[0]);
        int minutos = Integer.parseInt(partes[1]);
        return horas >= 0 && horas <= 23 && minutos >= 0 && minutos <= 59;
    }
    
    // ============ FORMATEAR HORA ============
    private String formatearHora(String hora) {
        hora = hora.trim();
        
        if (PATTERN_HORA.matcher(hora).matches()) {
            return hora;
        }
        
        if (hora.length() == 3 || hora.length() == 4) {
            String horas = hora.substring(0, hora.length() - 2);
            String minutos = hora.substring(hora.length() - 2);
            
            if (horas.length() == 1) {
                horas = "0" + horas;
            }
            
            try {
                int h = Integer.parseInt(horas);
                int m = Integer.parseInt(minutos);
                if (h >= 0 && h <= 23 && m >= 0 && m <= 59) {
                    return horas + ":" + minutos;
                }
            } catch (Exception e) {}
        }
        return null;
    }
    
    // ============ ASIGNAR CONSULTA ============
    private void asignarConsulta() {
        int posPaciente = spPacientes.getSelectedItemPosition();
        int posMedico = spMedicos.getSelectedItemPosition();
        
        if (posPaciente == 0 || posMedico == 0) {
            Toast.makeText(this, "⚠️ Seleccione paciente y médico", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();
        
        if (fecha.isEmpty()) {
            Toast.makeText(this, "⚠️ Ingrese la fecha", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (hora.isEmpty()) {
            Toast.makeText(this, "⚠️ Ingrese la hora", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // ============ VALIDACIÓN DE FORMATO DE FECHA ============
        if (!validarFecha(fecha)) {
            Toast.makeText(this, "⚠️ Fecha inválida. Use formato YYYY-MM-DD (ej: 2024-12-07)", Toast.LENGTH_LONG).show();
            return;
        }
        
        // ============ VALIDACIÓN DE FORMATO DE HORA ============
        String horaFormateada = formatearHora(hora);
        
        if (horaFormateada == null) {
            Toast.makeText(this, "⚠️ Formato de hora inválido. Use HH:MM (Ej: 14:30)", Toast.LENGTH_LONG).show();
            return;
        }
        
        if (!validarHora(horaFormateada)) {
            Toast.makeText(this, "⚠️ Hora inválida. Use formato 00:00 a 23:59", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Actualizar el campo con el formato correcto
        etHora.setText(horaFormateada);
        
        // ============ OBTENER IDs ============
        ArrayList<HashMap<String, String>> pacientes = dbHelper.listarPacientes();
        int idPaciente = Integer.parseInt(pacientes.get(posPaciente - 1).get("id"));
        
        ArrayList<HashMap<String, String>> medicos = dbHelper.listarMedicos();
        int idMedico = Integer.parseInt(medicos.get(posMedico - 1).get("id"));
        
        // ============ VALIDACIONES ============
        
        // 1. Verificar si el paciente tiene consulta activa
        if (dbHelper.tieneConsultaActiva(idPaciente)) {
            Toast.makeText(this, "⚠️ El paciente ya tiene una consulta activa", Toast.LENGTH_LONG).show();
            return;
        }

        // 2. Verificar si tiene una consulta finalizada que aún no ha cancelado
        if (dbHelper.tienePagoPendiente(idPaciente)) {
            Toast.makeText(this, "⚠️ El paciente tiene una consulta pendiente de pago. Debe cancelarla en Cobros antes de asignarle otra.", Toast.LENGTH_LONG).show();
            return;
        }
        
        // 3. VERIFICAR DISPONIBILIDAD DEL MÉDICO
        if (!dbHelper.medicoDisponible(idMedico, fecha, horaFormateada)) {
            String nombreMedico = medicos.get(posMedico - 1).get("nombre");
            Toast.makeText(this, "⚠️ El Dr(a). " + nombreMedico + " ya tiene una consulta asignada en esa fecha y hora", Toast.LENGTH_LONG).show();
            return;
        }
        
        // ============ INSERTAR CONSULTA ============
        if (dbHelper.insertarConsulta(idPaciente, idMedico, fecha, horaFormateada)) {
            Toast.makeText(this, "✅ Consulta asignada exitosamente", Toast.LENGTH_SHORT).show();
            listarConsultas(estadoActual.equals("todas") ? "todas" : estadoActual);
            limpiarCampos();
        } else {
            Toast.makeText(this, "❌ Error al asignar consulta", Toast.LENGTH_LONG).show();
        }
    }
    
    private void limpiarCampos() {
        spPacientes.setSelection(0);
        spMedicos.setSelection(0);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        etFecha.setText(sdf.format(new java.util.Date()));
        etHora.setText("");
    }
    
    private void listarConsultas(String estado) {
        listaConsultas.clear();
        consultasData.clear();
        consultasData = dbHelper.listarConsultas(estado);
        
        if (consultasData.isEmpty()) {
            String mensaje = estado.equals("Activa") ? "📭 No hay consultas activas" : 
                            estado.equals("Finalizada") ? "📭 No hay consultas finalizadas" : 
                            "📭 No hay consultas";
            listaConsultas.add(mensaje);
        } else {
            for (HashMap<String, String> c : consultasData) {
                String icono = c.get("estado").equals("Activa") ? "🟢" : "🔴";
                listaConsultas.add(icono + " " + c.get("paciente") + " - " + 
                                   c.get("medico") + " (" + c.get("fecha_consulta") + " " + c.get("hora_consulta") + ")");
            }
        }
        adapter.notifyDataSetChanged();
    }
}