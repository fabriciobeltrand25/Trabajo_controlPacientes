package com.example.medicos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "control_medicos.db";
	private static final int DATABASE_VERSION = 3;

	private static final String TABLE_MEDICOS = "medicos";
	private static final String TABLE_PACIENTES = "pacientes";
	private static final String TABLE_CONSULTAS = "consultas";
	private static final String TABLE_COBROS = "cobros";

	// Campos comunes
	private static final String KEY_ID = "id";

	// Campos Médicos
	private static final String KEY_CODIGO = "codigo";
	private static final String KEY_NOMBRE = "nombre";
	private static final String KEY_ESPECIALIDAD = "especialidad";
	private static final String KEY_TELEFONO = "telefono";
	private static final String KEY_EMAIL = "email";

	// Campos Pacientes
	private static final String KEY_IDENTIDAD = "identidad";
	private static final String KEY_DIRECCION = "direccion";
	private static final String KEY_FECHA_NAC = "fecha_nacimiento";

	// Campos Consultas
	private static final String KEY_ID_PACIENTE = "id_paciente";
	private static final String KEY_ID_MEDICO = "id_medico";
	private static final String KEY_FECHA_CONSULTA = "fecha_consulta";
	private static final String KEY_HORA_CONSULTA = "hora_consulta";
	private static final String KEY_VALOR_CONSULTA = "valor_consulta";
	private static final String KEY_ESTADO = "estado";

	// Campos Cobros
	private static final String KEY_ID_CONSULTA = "id_consulta";
	private static final String KEY_VALOR_BASE = "valor_base";
	private static final String KEY_MORA = "mora";
	private static final String KEY_TOTAL_PAGADO = "total_pagado";
	private static final String KEY_CAMBIO = "cambio";
	private static final String KEY_FECHA_PAGO = "fecha_pago";

	public static final double VALOR_CONSULTA = 500.00;
	public static final double VALOR_MORA_DIA = 20.00;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String CREATE_TABLE_MEDICOS = "CREATE TABLE " + TABLE_MEDICOS + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CODIGO + " TEXT UNIQUE," + KEY_NOMBRE + " TEXT,"
				+ KEY_ESPECIALIDAD + " TEXT," + KEY_TELEFONO + " TEXT," + KEY_EMAIL + " TEXT" + ")";
		db.execSQL(CREATE_TABLE_MEDICOS);

		String CREATE_TABLE_PACIENTES = "CREATE TABLE " + TABLE_PACIENTES + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_IDENTIDAD + " TEXT UNIQUE," + KEY_NOMBRE + " TEXT,"
				+ KEY_DIRECCION + " TEXT," + KEY_TELEFONO + " TEXT," + KEY_FECHA_NAC + " TEXT" + ")";
		db.execSQL(CREATE_TABLE_PACIENTES);

		String CREATE_TABLE_CONSULTAS = "CREATE TABLE " + TABLE_CONSULTAS + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ID_PACIENTE + " INTEGER," + KEY_ID_MEDICO + " INTEGER,"
				+ KEY_FECHA_CONSULTA + " TEXT," + KEY_HORA_CONSULTA + " TEXT," + KEY_VALOR_CONSULTA + " REAL,"
				+ KEY_ESTADO + " TEXT," + "FOREIGN KEY(" + KEY_ID_PACIENTE + ") REFERENCES " + TABLE_PACIENTES + "("
				+ KEY_ID + ")," + "FOREIGN KEY(" + KEY_ID_MEDICO + ") REFERENCES " + TABLE_MEDICOS + "(" + KEY_ID + ")"
				+ ")";
		db.execSQL(CREATE_TABLE_CONSULTAS);

		String CREATE_TABLE_COBROS = "CREATE TABLE " + TABLE_COBROS + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ID_CONSULTA + " INTEGER," + KEY_VALOR_BASE + " REAL,"
				+ KEY_MORA + " REAL," + KEY_TOTAL_PAGADO + " REAL," + KEY_CAMBIO + " REAL," + KEY_FECHA_PAGO + " TEXT,"
				+ "FOREIGN KEY(" + KEY_ID_CONSULTA + ") REFERENCES " + TABLE_CONSULTAS + "(" + KEY_ID + ")" + ")";
		db.execSQL(CREATE_TABLE_COBROS);

		insertarDatosPrueba(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COBROS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONSULTAS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PACIENTES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICOS);
		onCreate(db);
	}

	private void insertarDatosPrueba(SQLiteDatabase db) {

		db.execSQL("INSERT INTO " + TABLE_MEDICOS + "(" + KEY_CODIGO + "," + KEY_NOMBRE + "," + KEY_ESPECIALIDAD + ","
				+ KEY_TELEFONO + "," + KEY_EMAIL + ") VALUES "
				+ "('MED001','Dr. Carlos García','Cardiología','9999-1111','carlos.garcia@hospital.com'),"
				+ "('MED002','Dra. María López','Pediatría','9999-2222','maria.lopez@hospital.com'),"
				+ "('MED003','Dr. Juan Pérez','Traumatología','9999-3333','juan.perez@hospital.com'),"
				+ "('MED004','Dra. Ana Martínez','Ginecología','9999-4444','ana.martinez@hospital.com'),"
				+ "('MED005','Dr. Roberto Sánchez','Neurología','9999-5555','roberto.sanchez@hospital.com')");

		db.execSQL("INSERT INTO " + TABLE_PACIENTES + "(" + KEY_IDENTIDAD + "," + KEY_NOMBRE + "," + KEY_DIRECCION + ","
				+ KEY_TELEFONO + "," + KEY_FECHA_NAC + ") VALUES "
				+ "('0801-1985-12345','Pedro González','Colonia Miramontes, Casa #123','9999-6666','1985-03-15'),"
				+ "('0801-1990-67890','Laura Fernández','Residencial Los Pinos, Casa #45','9999-7777','1990-07-22'),"
				+ "('0801-1978-23456','Miguel Rodríguez','Barrio El Centro, #78','9999-8888','1978-11-30'),"
				+ "('0801-1995-78901','Carmen Torres','Colonia Kennedy, Casa #67','9999-9999','1995-05-10'),"
				+ "('0801-1982-34567','José Ramírez','Residencial Las Lomas, #90','8888-1111','1982-09-25')");

		
		String hoy = fechaRelativa(0);
		String ayer = fechaRelativa(1);
		String haceDosDias = fechaRelativa(2);
		String haceTresDias = fechaRelativa(3);
		
		db.execSQL("INSERT INTO " + TABLE_CONSULTAS + "(" + KEY_ID_PACIENTE + "," + KEY_ID_MEDICO + ","
	            + KEY_FECHA_CONSULTA + "," + KEY_HORA_CONSULTA + "," + KEY_VALOR_CONSULTA + "," + KEY_ESTADO + ") VALUES " +
	            "(1,1,'" + hoy + "','09:00',500.00,'Activa')," +
	            "(2,2,'" + haceDosDias + "','10:30',500.00,'Finalizada')," +
	            "(3,3,'" + haceTresDias + "','14:00',500.00,'Finalizada')," +
	            "(4,4,'" + hoy + "','11:00',500.00,'Activa')");

	    // Insertar Cobros (registrados el día de hoy)
	    db.execSQL("INSERT INTO " + TABLE_COBROS + "(" + KEY_ID_CONSULTA + "," + KEY_VALOR_BASE + ","
	            + KEY_MORA + "," + KEY_TOTAL_PAGADO + "," + KEY_CAMBIO + "," + KEY_FECHA_PAGO + ") VALUES " +
	            "(2,500.00,20.00,520.00,0.00,'" + haceDosDias + "')," +
	            "(3,500.00,40.00,600.00,60.00,'" + haceTresDias + "')");
	}


	private String fechaRelativa(int diasAtras) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.add(java.util.Calendar.DAY_OF_MONTH, -diasAtras);
		return new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(c.getTime());
	}

	// -- MÉTODOS CRUD PARA MÉDICOS 
	public boolean insertarMedico(String codigo, String nombre, String especialidad, String telefono, String email) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CODIGO, codigo);
		values.put(KEY_NOMBRE, nombre);
		values.put(KEY_ESPECIALIDAD, especialidad);
		values.put(KEY_TELEFONO, telefono);
		values.put(KEY_EMAIL, email);

		long result = db.insert(TABLE_MEDICOS, null, values);
		db.close();
		return result != -1;
	}

	public ArrayList<HashMap<String, String>> listarMedicos() {
		ArrayList<HashMap<String, String>> lista = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_MEDICOS, null, null, null, null, null, KEY_NOMBRE);

		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(KEY_ID, cursor.getString(cursor.getColumnIndex(KEY_ID)));
				map.put(KEY_CODIGO, cursor.getString(cursor.getColumnIndex(KEY_CODIGO)));
				map.put(KEY_NOMBRE, cursor.getString(cursor.getColumnIndex(KEY_NOMBRE)));
				map.put(KEY_ESPECIALIDAD, cursor.getString(cursor.getColumnIndex(KEY_ESPECIALIDAD)));
				map.put(KEY_TELEFONO, cursor.getString(cursor.getColumnIndex(KEY_TELEFONO)));
				map.put(KEY_EMAIL, cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
				lista.add(map);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return lista;
	}

	public ArrayList<HashMap<String, String>> buscarMedicos(String busqueda) {
		ArrayList<HashMap<String, String>> lista = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_MEDICOS, null, KEY_NOMBRE + " LIKE ? OR " + KEY_ESPECIALIDAD + " LIKE ?",
				new String[] { "%" + busqueda + "%", "%" + busqueda + "%" }, null, null, KEY_NOMBRE);

		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(KEY_ID, cursor.getString(cursor.getColumnIndex(KEY_ID)));
				map.put(KEY_CODIGO, cursor.getString(cursor.getColumnIndex(KEY_CODIGO)));
				map.put(KEY_NOMBRE, cursor.getString(cursor.getColumnIndex(KEY_NOMBRE)));
				map.put(KEY_ESPECIALIDAD, cursor.getString(cursor.getColumnIndex(KEY_ESPECIALIDAD)));
				map.put(KEY_TELEFONO, cursor.getString(cursor.getColumnIndex(KEY_TELEFONO)));
				map.put(KEY_EMAIL, cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
				lista.add(map);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return lista;
	}

	public boolean actualizarMedico(int id, String codigo, String nombre, String especialidad, String telefono,
			String email) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CODIGO, codigo);
		values.put(KEY_NOMBRE, nombre);
		values.put(KEY_ESPECIALIDAD, especialidad);
		values.put(KEY_TELEFONO, telefono);
		values.put(KEY_EMAIL, email);

		int result = db.update(TABLE_MEDICOS, values, KEY_ID + "=?", new String[] { String.valueOf(id) });
		db.close();
		return result > 0;
	}

	public boolean eliminarMedico(int id) {
		if (tieneConsultasMedico(id)) {
			return false;
		}
		SQLiteDatabase db = this.getWritableDatabase();
		int result = db.delete(TABLE_MEDICOS, KEY_ID + "=?", new String[] { String.valueOf(id) });
		db.close();
		return result > 0;
	}

	private boolean tieneConsultasMedico(int idMedico) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CONSULTAS, null, KEY_ID_MEDICO + "=?", new String[] { String.valueOf(idMedico) },
				null, null, null);
		boolean tiene = cursor.getCount() > 0;
		cursor.close();
		db.close();
		return tiene;
	}

	
	public boolean verificarCodigoMedico(String codigo) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_MEDICOS, new String[] { "id" }, "codigo = ?", new String[] { codigo }, null,
				null, null);
		boolean exists = cursor.getCount() > 0;
		cursor.close();
		db.close();
		return exists;
	}

	
	public boolean verificarCodigoMedico(String codigo, int idExcluir) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_MEDICOS, new String[] { "id" }, "codigo = ? AND id != ?",
				new String[] { codigo, String.valueOf(idExcluir) }, null, null, null);
		boolean exists = cursor.getCount() > 0;
		cursor.close();
		db.close();
		return exists;
	}

	
	// --- MÉTODOS CRUD PARA PACIENTES 
	public boolean insertarPaciente(String identidad, String nombre, String direccion, String telefono,
			String fechaNac) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_IDENTIDAD, identidad);
		values.put(KEY_NOMBRE, nombre);
		values.put(KEY_DIRECCION, direccion);
		values.put(KEY_TELEFONO, telefono);
		values.put(KEY_FECHA_NAC, fechaNac);

		long result = db.insert(TABLE_PACIENTES, null, values);
		db.close();
		return result != -1;
	}

	public ArrayList<HashMap<String, String>> listarPacientes() {
		ArrayList<HashMap<String, String>> lista = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PACIENTES, null, null, null, null, null, KEY_NOMBRE);

		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(KEY_ID, cursor.getString(cursor.getColumnIndex(KEY_ID)));
				map.put(KEY_IDENTIDAD, cursor.getString(cursor.getColumnIndex(KEY_IDENTIDAD)));
				map.put(KEY_NOMBRE, cursor.getString(cursor.getColumnIndex(KEY_NOMBRE)));
				map.put(KEY_DIRECCION, cursor.getString(cursor.getColumnIndex(KEY_DIRECCION)));
				map.put(KEY_TELEFONO, cursor.getString(cursor.getColumnIndex(KEY_TELEFONO)));
				map.put(KEY_FECHA_NAC, cursor.getString(cursor.getColumnIndex(KEY_FECHA_NAC)));
				lista.add(map);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return lista;
	}

	public ArrayList<HashMap<String, String>> buscarPacientes(String busqueda) {
		ArrayList<HashMap<String, String>> lista = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PACIENTES, null, KEY_IDENTIDAD + " LIKE ? OR " + KEY_NOMBRE + " LIKE ?",
				new String[] { "%" + busqueda + "%", "%" + busqueda + "%" }, null, null, KEY_NOMBRE);

		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(KEY_ID, cursor.getString(cursor.getColumnIndex(KEY_ID)));
				map.put(KEY_IDENTIDAD, cursor.getString(cursor.getColumnIndex(KEY_IDENTIDAD)));
				map.put(KEY_NOMBRE, cursor.getString(cursor.getColumnIndex(KEY_NOMBRE)));
				map.put(KEY_DIRECCION, cursor.getString(cursor.getColumnIndex(KEY_DIRECCION)));
				map.put(KEY_TELEFONO, cursor.getString(cursor.getColumnIndex(KEY_TELEFONO)));
				map.put(KEY_FECHA_NAC, cursor.getString(cursor.getColumnIndex(KEY_FECHA_NAC)));
				lista.add(map);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return lista;
	}

	public ArrayList<HashMap<String, String>> obtenerTodosPacientes() {
		ArrayList<HashMap<String, String>> lista = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = this.getReadableDatabase();

		String[] columnas = new String[] { KEY_IDENTIDAD, KEY_NOMBRE };
		Cursor cursor = db.query(TABLE_PACIENTES, columnas, null, null, null, null, KEY_NOMBRE);

		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(KEY_IDENTIDAD, cursor.getString(cursor.getColumnIndex(KEY_IDENTIDAD)));
				map.put(KEY_NOMBRE, cursor.getString(cursor.getColumnIndex(KEY_NOMBRE)));
				lista.add(map);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return lista;
	}

	public boolean actualizarPaciente(int id, String identidad, String nombre, String direccion, String telefono,
			String fechaNac) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_IDENTIDAD, identidad);
		values.put(KEY_NOMBRE, nombre);
		values.put(KEY_DIRECCION, direccion);
		values.put(KEY_TELEFONO, telefono);
		values.put(KEY_FECHA_NAC, fechaNac);

		int result = db.update(TABLE_PACIENTES, values, KEY_ID + "=?", new String[] { String.valueOf(id) });
		db.close();
		return result > 0;
	}

	public boolean eliminarPaciente(int id) {
		if (tieneConsultasPaciente(id)) {
			return false;
		}
		SQLiteDatabase db = this.getWritableDatabase();
		int result = db.delete(TABLE_PACIENTES, KEY_ID + "=?", new String[] { String.valueOf(id) });
		db.close();
		return result > 0;
	}

	private boolean tieneConsultasPaciente(int idPaciente) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CONSULTAS, null, KEY_ID_PACIENTE + "=?",
				new String[] { String.valueOf(idPaciente) }, null, null, null);
		boolean tiene = cursor.getCount() > 0;
		cursor.close();
		db.close();
		return tiene;
	}

	// --- MÉTODOS PARA CONSULTAS 
	public boolean tieneConsultaActiva(int idPaciente) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CONSULTAS, new String[] { KEY_ID },
				KEY_ID_PACIENTE + "=? AND " + KEY_ESTADO + "=?", new String[] { String.valueOf(idPaciente), "Activa" },
				null, null, null);
		boolean tiene = cursor.getCount() > 0;
		cursor.close();
		db.close();
		return tiene;
	}

	public boolean tienePagoPendiente(int idPaciente) {
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT c.id FROM consultas c " + "WHERE c.id_paciente = ? " + "AND c.estado = 'Finalizada' "
				+ "AND c.id NOT IN (SELECT id_consulta FROM cobros)";

		Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(idPaciente) });
		boolean tienePendiente = cursor.getCount() > 0;
		cursor.close();
		db.close();
		return tienePendiente;
	}

	public boolean tieneConsultaPendienteOPago(int idPaciente) {
		return tieneConsultaActiva(idPaciente) || tienePagoPendiente(idPaciente);
	}

	
	//  VERIFICAR DISPONIBILIDAD DEL MÉDICO
	public boolean medicoDisponible(int idMedico, String fecha, String hora) {
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT COUNT(*) as total FROM " + TABLE_CONSULTAS + " WHERE " + KEY_ID_MEDICO + "=? AND "
				+ KEY_FECHA_CONSULTA + "=? AND " + KEY_HORA_CONSULTA + "=? AND " + KEY_ESTADO + "='Activa'";

		Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(idMedico), fecha, hora });

		int total = 0;
		if (cursor.moveToFirst()) {
			total = cursor.getInt(cursor.getColumnIndex("total"));
		}
		cursor.close();
		db.close();

		return total == 0;
	}

	public boolean insertarConsulta(int idPaciente, int idMedico, String fecha, String hora) {
		if (tieneConsultaPendienteOPago(idPaciente)) {
			return false;
		}

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID_PACIENTE, idPaciente);
		values.put(KEY_ID_MEDICO, idMedico);
		values.put(KEY_FECHA_CONSULTA, fecha);
		values.put(KEY_HORA_CONSULTA, hora);
		values.put(KEY_VALOR_CONSULTA, VALOR_CONSULTA);
		values.put(KEY_ESTADO, "Activa");

		long resultado = db.insert(TABLE_CONSULTAS, null, values);
		db.close();
		return resultado != -1;
	}

	public ArrayList<HashMap<String, String>> listarConsultas(String estado) {
		ArrayList<HashMap<String, String>> lista = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT c.*, p." + KEY_NOMBRE + " as paciente, m." + KEY_NOMBRE + " as medico " + "FROM "
				+ TABLE_CONSULTAS + " c " + "JOIN " + TABLE_PACIENTES + " p ON c." + KEY_ID_PACIENTE + "=p." + KEY_ID
				+ " " + "JOIN " + TABLE_MEDICOS + " m ON c." + KEY_ID_MEDICO + "=m." + KEY_ID;

		if (!estado.equals("todas")) {
			query += " WHERE c." + KEY_ESTADO + "='" + estado + "'";
		}
		query += " ORDER BY c." + KEY_FECHA_CONSULTA + " DESC, c." + KEY_HORA_CONSULTA + " DESC";

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(KEY_ID, cursor.getString(cursor.getColumnIndex(KEY_ID)));
				map.put("paciente", cursor.getString(cursor.getColumnIndex("paciente")));
				map.put("medico", cursor.getString(cursor.getColumnIndex("medico")));
				map.put(KEY_ID_PACIENTE, cursor.getString(cursor.getColumnIndex(KEY_ID_PACIENTE)));
				map.put(KEY_ID_MEDICO, cursor.getString(cursor.getColumnIndex(KEY_ID_MEDICO)));
				map.put(KEY_FECHA_CONSULTA, cursor.getString(cursor.getColumnIndex(KEY_FECHA_CONSULTA)));
				map.put(KEY_HORA_CONSULTA, cursor.getString(cursor.getColumnIndex(KEY_HORA_CONSULTA)));
				map.put(KEY_VALOR_CONSULTA, cursor.getString(cursor.getColumnIndex(KEY_VALOR_CONSULTA)));
				map.put(KEY_ESTADO, cursor.getString(cursor.getColumnIndex(KEY_ESTADO)));
				lista.add(map);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return lista;
	}

	public boolean finalizarConsulta(int idConsulta) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ESTADO, "Finalizada");
		int result = db.update(TABLE_CONSULTAS, values, KEY_ID + "=?", new String[] { String.valueOf(idConsulta) });
		db.close();
		return result > 0;
	}

	//  MÉTODOS PARA COBROS 
	public ArrayList<HashMap<String, String>> buscarCobrosPendientes(String identidad) {
		ArrayList<HashMap<String, String>> lista = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT c.*, p." + KEY_NOMBRE + " as paciente, p." + KEY_IDENTIDAD + ", m." + KEY_NOMBRE
				+ " as medico " + "FROM " + TABLE_CONSULTAS + " c " + "JOIN " + TABLE_PACIENTES + " p ON c."
				+ KEY_ID_PACIENTE + "=p." + KEY_ID + " " + "JOIN " + TABLE_MEDICOS + " m ON c." + KEY_ID_MEDICO + "=m."
				+ KEY_ID + " " + "WHERE p." + KEY_IDENTIDAD + "=? AND c." + KEY_ESTADO + "='Finalizada' "
				+ "AND NOT EXISTS (SELECT 1 FROM " + TABLE_COBROS + " co WHERE co." + KEY_ID_CONSULTA + "=c." + KEY_ID
				+ ")";

		Cursor cursor = db.rawQuery(query, new String[] { identidad });

		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(KEY_ID, cursor.getString(cursor.getColumnIndex(KEY_ID)));
				map.put("paciente", cursor.getString(cursor.getColumnIndex("paciente")));
				map.put("medico", cursor.getString(cursor.getColumnIndex("medico")));
				map.put(KEY_ID_PACIENTE, cursor.getString(cursor.getColumnIndex(KEY_ID_PACIENTE)));
				map.put(KEY_ID_MEDICO, cursor.getString(cursor.getColumnIndex(KEY_ID_MEDICO)));
				map.put(KEY_FECHA_CONSULTA, cursor.getString(cursor.getColumnIndex(KEY_FECHA_CONSULTA)));
				map.put(KEY_VALOR_CONSULTA, cursor.getString(cursor.getColumnIndex(KEY_VALOR_CONSULTA)));
				map.put(KEY_ESTADO, cursor.getString(cursor.getColumnIndex(KEY_ESTADO)));

				String fechaConsulta = cursor.getString(cursor.getColumnIndex(KEY_FECHA_CONSULTA));
				double valorBase = cursor.getDouble(cursor.getColumnIndex(KEY_VALOR_CONSULTA));
				double mora = calcularMora(fechaConsulta);
				double total = valorBase + mora;

				map.put("mora", String.valueOf(mora));
				map.put("total", String.valueOf(total));
				lista.add(map);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return lista;
	}

	public double calcularMora(String fechaConsulta) {
		try {
			String[] partes = fechaConsulta.split("-");
			int anio = Integer.parseInt(partes[0]);
			int mes = Integer.parseInt(partes[1]) - 1;
			int dia = Integer.parseInt(partes[2]);

			java.util.Calendar fechaCons = java.util.Calendar.getInstance();
			fechaCons.set(anio, mes, dia, 0, 0, 0);
			fechaCons.set(java.util.Calendar.MILLISECOND, 0);

			java.util.Calendar hoy = java.util.Calendar.getInstance();
			hoy.set(java.util.Calendar.HOUR_OF_DAY, 0);
			hoy.set(java.util.Calendar.MINUTE, 0);
			hoy.set(java.util.Calendar.SECOND, 0);
			hoy.set(java.util.Calendar.MILLISECOND, 0);

			long diff = hoy.getTimeInMillis() - fechaCons.getTimeInMillis();
			int dias = (int) Math.round(diff / (24.0 * 60 * 60 * 1000));

			if (dias > 0) {
				return dias * VALOR_MORA_DIA;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean registrarCobro(int idConsulta, double valorBase, double mora, double totalPagado, double cambio) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID_CONSULTA, idConsulta);
		values.put(KEY_VALOR_BASE, valorBase);
		values.put(KEY_MORA, mora);
		values.put(KEY_TOTAL_PAGADO, totalPagado);
		values.put(KEY_CAMBIO, cambio);
		values.put(KEY_FECHA_PAGO, new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

		long result = db.insert(TABLE_COBROS, null, values);
		db.close();
		return result != -1;
	}

	//  OBTENER PACIENTES CON DEUDA 
	public ArrayList<HashMap<String, String>> obtenerPacientesConDeuda() {
		ArrayList<HashMap<String, String>> lista = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT DISTINCT p." + KEY_ID + ", p." + KEY_IDENTIDAD + ", p." + KEY_NOMBRE + " FROM "
				+ TABLE_PACIENTES + " p " + " INNER JOIN " + TABLE_CONSULTAS + " c ON p." + KEY_ID + "=c."
				+ KEY_ID_PACIENTE + " WHERE c." + KEY_ESTADO + "='Finalizada'" + " AND NOT EXISTS (SELECT 1 FROM "
				+ TABLE_COBROS + " co WHERE co." + KEY_ID_CONSULTA + "=c." + KEY_ID + ")" + " ORDER BY p." + KEY_NOMBRE;

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(KEY_ID, cursor.getString(cursor.getColumnIndex(KEY_ID)));
				map.put(KEY_IDENTIDAD, cursor.getString(cursor.getColumnIndex(KEY_IDENTIDAD)));
				map.put(KEY_NOMBRE, cursor.getString(cursor.getColumnIndex(KEY_NOMBRE)));
				lista.add(map);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return lista;
	}

	//  MÉTODOS PARA REPORTES 

	public ArrayList<HashMap<String, String>> reportePacientesMasConsultas() {
		ArrayList<HashMap<String, String>> lista = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT p.*, COUNT(c." + KEY_ID + ") as total_consultas " + "FROM " + TABLE_PACIENTES + " p "
				+ "LEFT JOIN " + TABLE_CONSULTAS + " c ON p." + KEY_ID + "=c." + KEY_ID_PACIENTE + " " + "GROUP BY p."
				+ KEY_ID + " " + "HAVING total_consultas > 0 " + "ORDER BY total_consultas DESC";

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(KEY_IDENTIDAD, cursor.getString(cursor.getColumnIndex(KEY_IDENTIDAD)));
				map.put(KEY_NOMBRE, cursor.getString(cursor.getColumnIndex(KEY_NOMBRE)));
				map.put(KEY_TELEFONO, cursor.getString(cursor.getColumnIndex(KEY_TELEFONO)));
				map.put("total_consultas", cursor.getString(cursor.getColumnIndex("total_consultas")));
				lista.add(map);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return lista;
	}

	public ArrayList<HashMap<String, String>> reporteMedicosMasConsultas() {
		ArrayList<HashMap<String, String>> lista = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT m.*, COUNT(c." + KEY_ID + ") as total_consultas " + "FROM " + TABLE_MEDICOS + " m "
				+ "LEFT JOIN " + TABLE_CONSULTAS + " c ON m." + KEY_ID + "=c." + KEY_ID_MEDICO + " " + "GROUP BY m."
				+ KEY_ID + " " + "HAVING total_consultas > 0 " + "ORDER BY total_consultas DESC";

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(KEY_CODIGO, cursor.getString(cursor.getColumnIndex(KEY_CODIGO)));
				map.put(KEY_NOMBRE, cursor.getString(cursor.getColumnIndex(KEY_NOMBRE)));
				map.put(KEY_ESPECIALIDAD, cursor.getString(cursor.getColumnIndex(KEY_ESPECIALIDAD)));
				map.put("total_consultas", cursor.getString(cursor.getColumnIndex("total_consultas")));
				lista.add(map);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return lista;
	}

	public ArrayList<HashMap<String, String>> reportePacientesEnMora() {
		ArrayList<HashMap<String, String>> lista = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT c.*, p." + KEY_NOMBRE + " as paciente, m." + KEY_NOMBRE + " as medico " + "FROM "
				+ TABLE_CONSULTAS + " c " + "JOIN " + TABLE_PACIENTES + " p ON c." + KEY_ID_PACIENTE + "=p." + KEY_ID
				+ " " + "JOIN " + TABLE_MEDICOS + " m ON c." + KEY_ID_MEDICO + "=m." + KEY_ID + " " + "WHERE c."
				+ KEY_ESTADO + "='Finalizada' " + "AND NOT EXISTS (SELECT 1 FROM " + TABLE_COBROS + " co WHERE co."
				+ KEY_ID_CONSULTA + "=c." + KEY_ID + ")";

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				String fechaConsulta = cursor.getString(cursor.getColumnIndex(KEY_FECHA_CONSULTA));
				double valorBase = cursor.getDouble(cursor.getColumnIndex(KEY_VALOR_CONSULTA));
				double mora = calcularMora(fechaConsulta);

				if (mora > 0) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("paciente", cursor.getString(cursor.getColumnIndex("paciente")));
					map.put("medico", cursor.getString(cursor.getColumnIndex("medico")));
					map.put(KEY_FECHA_CONSULTA, fechaConsulta);
					map.put(KEY_VALOR_CONSULTA, String.valueOf(valorBase));
					map.put("mora", String.valueOf(mora));
					map.put("total", String.valueOf(valorBase + mora));
					lista.add(map);
				}
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return lista;
	}

	public HashMap<String, Object> reporteRecaudacionDia(String fecha) {
		HashMap<String, Object> resultado = new HashMap<String, Object>();
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT COUNT(*) as total_cobros, " + "SUM(" + KEY_TOTAL_PAGADO + " - " + KEY_CAMBIO
				+ ") as total_recaudado, " + "SUM(" + KEY_MORA + ") as total_mora, " + "SUM(" + KEY_VALOR_BASE
				+ ") as total_base " + "FROM " + TABLE_COBROS + " WHERE " + KEY_FECHA_PAGO + "=?";

		Cursor cursor = db.rawQuery(query, new String[] { fecha });

		if (cursor.moveToFirst()) {
			resultado.put("total_cobros", cursor.getInt(cursor.getColumnIndex("total_cobros")));
			resultado.put("total_recaudado", cursor.getDouble(cursor.getColumnIndex("total_recaudado")));
			resultado.put("total_mora", cursor.getDouble(cursor.getColumnIndex("total_mora")));
			resultado.put("total_base", cursor.getDouble(cursor.getColumnIndex("total_base")));
		} else {
			resultado.put("total_cobros", 0);
			resultado.put("total_recaudado", 0.0);
			resultado.put("total_mora", 0.0);
			resultado.put("total_base", 0.0);
		}
		cursor.close();
		db.close();
		return resultado;
	}
}