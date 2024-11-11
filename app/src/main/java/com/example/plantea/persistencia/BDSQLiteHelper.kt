package com.example.plantea.persistencia

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.CommonUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BDSQLiteHelper(contexto: Context?, nombreBD: String?, factory: CursorFactory?, versionBD: Int) : SQLiteOpenHelper(contexto, nombreBD, factory, versionBD) {
    private var context = contexto
    private val sqlGlobalIdTable = "CREATE TABLE IF NOT EXISTS GlobalID (id INTEGER PRIMARY KEY AUTOINCREMENT)"
    private val sqlInsertInitialId = "INSERT INTO GlobalID (id) VALUES (1)"
    private var sqlUsuario = "CREATE TABLE Usuario(id INTEGER PRIMARY KEY DEFAULT (nextval('global_id_seq')), email TEXT UNIQUE, password TEXT, username TEXT, name TEXT, imagen TEXT)"
    private var sqlUsuarioTEA = "CREATE TABLE UsuarioTEA(id INTEGER PRIMARY KEY DEFAULT (nextval('global_id_seq')), name TEXT, imagen TEXT, configPictogramas TEXT, id_usuario INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlActividad = "CREATE TABLE Actividad(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, imagen TEXT, id_usuario INTEGER, FOREIGN KEY (id_usuario) REFERENCES UsuarioTEA(id))"
    private var sqlCategorias = "CREATE TABLE Categoria(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, imagen BLOB, color TEXT, id_usuario INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlCategoriasUsuario = "CREATE TABLE CategoriaUsuario(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, imagen BLOB, color TEXT, id_usuario INTEGER, id_categoria INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario(id), FOREIGN KEY (id_categoria) REFERENCES Categoria(id))"
    private var sqlCategoriaOculta = "CREATE TABLE CategoriaOculta(id INTEGER PRIMARY KEY AUTOINCREMENT, id_categoria INTEGER, id_usuario INTEGER, FOREIGN KEY (id_categoria) REFERENCES Categoria(id), FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlPictograma = "CREATE TABLE Pictograma(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, id_categoria_global INTEGER, id_categoria_local INTEGER, FOREIGN KEY (id_categoria_global) REFERENCES Categoria(id), FOREIGN KEY (id_categoria_local) REFERENCES CategoriaUsuario(id))"
    private var sqlPictogramaAPI = "CREATE TABLE PictogramaAPI(id INTEGER PRIMARY KEY, id_API INTEGER, FOREIGN KEY (id) REFERENCES Pictograma(id))"
    private var sqlPictogramaLocal = "CREATE TABLE PictogramaLocal(id INTEGER PRIMARY KEY, imagen BLOB, id_usuario INTEGER, FOREIGN KEY (id) REFERENCES Pictograma(id), FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlRelacionPictoAPI = "CREATE TABLE RelacionPictoAPI (id INTEGER PRIMARY KEY AUTOINCREMENT, id_usuario INTEGER, id_pictogramaAPI INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario(id), FOREIGN KEY (id_pictogramaAPI) REFERENCES PictogramaAPI(id))"
    private var sqlFavorito = "CREATE TABLE Favorito(id_pictograma INTEGER, id_usuario INTEGER, PRIMARY KEY (id_pictograma, id_usuario), FOREIGN KEY (id_usuario) REFERENCES Usuario(id), FOREIGN KEY (id_pictograma) REFERENCES PictogramaAPI(id))"
    private var sqpPlanificacion = "CREATE TABLE Planificacion(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, es_actual INTEGER, id_usuario INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlEvento = "CREATE TABLE Evento(id INTEGER PRIMARY KEY AUTOINCREMENT, id_usuario INTEGER, nombre TEXT, fecha TEXT, hora TEXT, visible INTEGER, reminder TEXT, change_visibility BOOLEAN, FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlRelacionPictogramaPlan = "CREATE TABLE RelacionPictogramaPlan (id INTEGER PRIMARY KEY AUTOINCREMENT, id_plan INTEGER, id_pictograma INTEGER, FOREIGN KEY (id_plan) REFERENCES Planificacion(id), FOREIGN KEY (id_pictograma) REFERENCES Pictograma(id))"
    private var sqlRelacionEventoPlan = "CREATE TABLE RelacionEventoPlan (id INTEGER PRIMARY KEY AUTOINCREMENT, historia TEXT, duracion TEXT, id_picto_entre, id_evento INTEGER, id_plan INTEGER, FOREIGN KEY (id_evento) REFERENCES Evento(id), FOREIGN KEY (id_plan) REFERENCES Planificacion(id))"
    private var sqlTraduccion = "CREATE TABLE Traduccion (id INTEGER PRIMARY KEY AUTOINCREMENT, language TEXT, translation TEXT)"
    private var sqlRelacionPictoTraduccion  = "CREATE TABLE RelacionPictoTraduccion (id INTEGER PRIMARY KEY AUTOINCREMENT, id_pictograma INTEGER, id_categoria INTEGER, id_traduccion INTEGER, FOREIGN KEY (id_pictograma) REFERENCES Pictograma(id), FOREIGN KEY (id_categoria) REFERENCES Categoria(id), FOREIGN KEY (id_traduccion) REFERENCES Traduccion(id))"
    private var sqlSemana  = "CREATE TABLE Semana (id INTEGER PRIMARY KEY AUTOINCREMENT, id_usuario INTEGER, configurationWeek INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlDiaSemana = "CREATE TABLE DiaSemana (id INTEGER PRIMARY KEY AUTOINCREMENT, semana_id INTEGER, pictograma_day BLOB, day_week TEXT, color TEXT, id_evento TEXT, FOREIGN KEY (semana_id) REFERENCES Semana(id))"

    override fun onCreate(db: SQLiteDatabase) {
        try {
            /*Se ejecuta la sentencia SQL de creación de la tabla*/
            db.execSQL(sqlGlobalIdTable)
            db.execSQL(sqlInsertInitialId)
            db.execSQL(sqlUsuario)
            db.execSQL(sqlUsuarioTEA)
            db.execSQL(sqlActividad)
            db.execSQL(sqlCategorias)
            db.execSQL(sqlCategoriasUsuario)
            db.execSQL(sqlCategoriaOculta)
            db.execSQL(sqlPictograma)
            db.execSQL(sqlPictogramaAPI)
            db.execSQL(sqlPictogramaLocal)
            db.execSQL(sqlRelacionPictoAPI)
            db.execSQL(sqlFavorito)
            db.execSQL(sqpPlanificacion)
            db.execSQL(sqlRelacionPictogramaPlan)
            db.execSQL(sqlEvento)
            db.execSQL(sqlRelacionEventoPlan)
            db.execSQL(sqlTraduccion)
            db.execSQL(sqlRelacionPictoTraduccion)
            db.execSQL(sqlSemana)
            db.execSQL(sqlDiaSemana)

            @OptIn(DelicateCoroutinesApi::class)
            GlobalScope.launch {
                meterDatos(db)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, versionAnterior: Int, versionNueva: Int) {
        try {
            /*Se elimina la versión anterior de la tablet*/
            db.execSQL("DROP TABLE IF EXISTS GlobalID")
            db.execSQL("DROP TABLE IF EXISTS Usuario")
            db.execSQL("DROP TABLE IF EXISTS UsuarioTEA")
            db.execSQL("DROP TABLE IF EXISTS Actividad")
            db.execSQL("DROP TABLE IF EXISTS Categorias")
            db.execSQL("DROP TABLE IF EXISTS CategoriaUsuario")
            db.execSQL("DROP TABLE IF EXISTS RelacionCategoriaUsuario")
            db.execSQL("DROP TABLE IF EXISTS Pictograma")
            db.execSQL("DROP TABLE IF EXISTS PictogramaAPI")
            db.execSQL("DROP TABLE IF EXISTS PictogramaLocal")
            db.execSQL("DROP TABLE IF EXISTS RelacionPictoAPI")
            db.execSQL("DROP TABLE IF EXISTS Favorito")
            db.execSQL("DROP TABLE IF EXISTS Planificacion")
            db.execSQL("DROP TABLE IF EXISTS RelacionPictogramaPlan")
            db.execSQL("DROP TABLE IF EXISTS Evento")
            db.execSQL("DROP TABLE IF EXISTS RelacionEventoPlan")
            db.execSQL("DROP TABLE IF EXISTS Traduccion")
            db.execSQL("DROP TABLE IF EXISTS RelacionPictoTraduccion")
            db.execSQL("DROP TABLE IF EXISTS Semana")
            db.execSQL("DROP TABLE IF EXISTS DiaSemana")

            /*Se crea la nueva versión de la table*/
            onCreate(db)

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun meterDatos(db: SQLiteDatabase) {
        insertCategoria(db, "LUGARES", R.drawable.categoria_lugares, "default", "en", "LOCATIONS") // 1
        insertCategoria(db, "DESPLAZAMIENTO", R.drawable.categoria_desplazamiento, "default", "en", "DISPLACEMENT") // 2
        insertCategoria(db, "ACCION", R.drawable.categoria_accion, "default","en",  "ACTION") // 3
        insertCategoria(db, "ENTRETENIMIENTO", R.drawable.categoria_entretenimiento, "blue", "en", "ENTERTAINMENT") // 4
        insertCategoria(db, "FAVORITOS", R.drawable.categoria_favorito, "yellow", "en", "FAVORITES") // 5

        insertPictogramaCategoria(db, "CASA", R.drawable.categoria_lugares_casa, 1, "en", "HOME")
        insertPictogramaCategoria(db,"CENTRO DE SALUD", R.drawable.categoria_lugares_centrosalud, 1,"en",  "HEALTH CENTER")
        insertPictogramaCategoria(db,"HOSPITAL", R.drawable.categoria_lugares_hospital, 1, "en", "HOSPITAL")
        insertPictogramaCategoria(db,"SALA DE ESPERA", R.drawable.categoria_lugares_salaespera, 1, "en", "WAITING ROOM")
        insertPictogramaCategoria(db,"RECEPCIÓN", R.drawable.categoria_lugares_recepcion, 1, "en", "RECEPTION")
        insertPictogramaCategoria(db,"CONSULTA", R.drawable.categoria_lugares_consulta, 1, "en", "CONSULTATION")
        insertPictogramaCategoria(db,"AULA", R.drawable.categoria_lugares_aula, 1, "en", "CLASSROOM")
        insertPictogramaCategoria(db,"COLEGIO BILINGÜE", R.drawable.categoria_lugares_colegio_bilingue, 1, "en", "BILINGUAL SCHOOL")
        insertPictogramaCategoria(db,"COLEGIO", R.drawable.categoria_lugares_colegio_bilingue, 1, "en", "SCHOOL")
        insertPictogramaCategoria(db,"CARNICERÍA", R.drawable.categoria_lugares_carniceria, 1, "en", "BUTCHER SHOP")
        insertPictogramaCategoria(db,"FRUTERÍA", R.drawable.categoria_lugares_fruteria, 1, "en", "FRUIT SHOP")
        insertPictogramaCategoria(db,"PELUQUERÍA", R.drawable.categoria_peluqueria, 1, "en", "HAIRDRESSER")
        insertPictogramaCategoria(db,"PESCADERÍA", R.drawable.categoria_lugares_pescaderia, 1, "en", "FISH SHOP")
        insertPictogramaCategoria(db,"SUPERMERCADO", R.drawable.categoria_lugares_supermercado, 1, "en", "SUPERMARKET")

        insertPictogramaCategoria(db,"COCHE", R.drawable.categoria_desplazamiento_coche, 2, "en", "CAR")
        insertPictogramaCategoria(db,"AUTOBÚS", R.drawable.categoria_desplazamiento_autobus, 2, "en", "BUS")
        insertPictogramaCategoria(db,"AMBULANCIA", R.drawable.categoria_desplazamiento_ambulancia, 2, "en", "AMBULANCE")
        insertPictogramaCategoria(db,"TREN", R.drawable.categoria_desplazamiento_tren, 2, "en", "TRAIN")
        insertPictogramaCategoria(db,"METRO", R.drawable.categoria_desplazamiento_metro, 2, "en", "METRO")
        insertPictogramaCategoria(db,"CAMINANDO", R.drawable.categoria_desplazamiento_caminando, 2, "en", "WALKING")

        insertPictogramaCategoria(db, "IR", R.drawable.categoria_accion_ir, 3, "en", "GO")
        insertPictogramaCategoria(db,"VOLVER", R.drawable.categoria_accion_volver, 3, "en", "RETURN")
        insertPictogramaCategoria(db,"ESPERAR", R.drawable.categoria_accion_esperar, 3, "en", "WAIT")
        insertPictogramaCategoria(db,"ENTRAR", R.drawable.categoria_accion_entrar, 3, "en", "ENTER")
        insertPictogramaCategoria(db,"SALUDAR", R.drawable.categoria_accion_saludar, 3, "en", "GREET")
        insertPictogramaCategoria(db,"DESPEDIRSE", R.drawable.categoria_accion_despedirse, 3, "en", "GOODBYE")
        insertPictogramaCategoria(db,"HABLAR", R.drawable.categoria_accion_hablar, 3, "en", "TALK")
        insertPictogramaCategoria(db,"ESCUCHAR", R.drawable.categoria_accion_escuchar, 3, "en", "LISTEN")
        insertPictogramaCategoria(db,"MIRAR", R.drawable.categoria_accion_mirar, 3, "en", "LOOK")
        insertPictogramaCategoria(db,"SENTARSE", R.drawable.categoria_accion_sentarse, 3, "en", "SIT DOWN")
        insertPictogramaCategoria(db,"BAJAR ESCALERA", R.drawable.categoria_accion_bajar_escalera_mecanica, 3, "en", "GO DOWN ESCALATOR")
        insertPictogramaCategoria(db,"SUBIR ESCALERA", R.drawable.categoria_accion_subir_escalera_mecanica, 3, "en", "GO UP ESCALATOR")
        insertPictogramaCategoria(db,"BAJAR RAMPA", R.drawable.categoria_accion_bajar_rampa, 3, "en", "GO DOWN RAMP")
        insertPictogramaCategoria(db,"SUBIR RAMPA", R.drawable.categoria_accion_subir_rampa, 3, "en", "GO UP RAMP")
        insertPictogramaCategoria(db,"CORTAR PELO", R.drawable.categoria_accion_cortar_pelo, 3, "en", "CUT HAIR")
        insertPictogramaCategoria(db,"CORTAR PELO", R.drawable.categoria_accion_cortar_pelo1, 3, "en", "CUT HAIR")
        insertPictogramaCategoria(db,"LAVAR PELO", R.drawable.categoria_accion_lavar_pelo, 3, "en", "WASH HAIR")
        insertPictogramaCategoria(db,"SECAR EL PELO", R.drawable.categoria_accion_secar_pelo, 3, "en", "DRY HAIR")
        insertPictogramaCategoria(db,"PAGAR", R.drawable.categoria_accion_pagar, 3, "en", "PAY")
        insertPictogramaCategoria(db,"COGER TICKET", R.drawable.categoria_accion_coger_ticket, 3, "en", "TAKE TICKET")
        insertPictogramaCategoria(db,"COGER TURNO", R.drawable.categoria_accion_coger_turno, 3, "en", "TAKE TURN")
        insertPictogramaCategoria(db,"ESPERAR", R.drawable.categoria_accion_esperar1, 3, "en", "WAIT")
        insertPictogramaCategoria(db,"PREGUNTAR", R.drawable.categoria_accion_preguntar, 3, "en", "ASK")
        insertPictogramaCategoria(db,"PREGUNTAR", R.drawable.categoria_accion_preguntar1, 3, "en", "ASK")
        insertPictogramaCategoria(db,"PREGUNTAR", R.drawable.categoria_accion_preguntar2, 3, "en", "ASK")
        insertPictogramaCategoria(db,"CERRAR MOCHILA", R.drawable.categoria_accion_cerrar_mochila, 3, "en", "CLOSE BACKPACK")
        insertPictogramaCategoria(db,"ABRIR MOCHILA", R.drawable.categoria_accion_abrir_mochila, 3, "en", "OPEN BACKPACK")
        insertPictogramaCategoria(db,"APROBAR", R.drawable.categoria_accion_aprobar, 3, "en", "APPROVE")
        insertPictogramaCategoria(db,"SUSPENDER", R.drawable.categoria_accion_suspender, 3, "en", "SUSPEND")
        insertPictogramaCategoria(db,"ENTRADA", R.drawable.categoria_accion_entrada, 3, "en", "ENTRANCE")
        insertPictogramaCategoria(db,"ENTRADA", R.drawable.categoria_accion_entrada1, 3, "en", "ENTRANCE")
        insertPictogramaCategoria(db,"SALIDA", R.drawable.categoria_accion_salida, 3, "en", "EXIT")
        insertPictogramaCategoria(db,"IR AL COLEGIO", R.drawable.categoria_accion_ir_colegio, 3, "en", "GO TO SCHOOL")
        insertPictogramaCategoria(db,"LEER", R.drawable.categoria_accion_lectura, 3, "en", "READING")
        insertPictogramaCategoria(db,"LEER", R.drawable.categoria_accion_leer, 3, "en", "READ")
        insertPictogramaCategoria(db,"LEER", R.drawable.categoria_accion_leer1, 3, "en", "READ")
        insertPictogramaCategoria(db,"LLEVAR AL COLEGIO", R.drawable.categoria_accion_llevar_colegio, 3, "en", "TAKE TO SCHOOL")
        insertPictogramaCategoria(db,"METER EL ALMUERZO", R.drawable.categoria_accion_meter_almuerzo, 3, "en", "PUT LUNCH")
        insertPictogramaCategoria(db,"PONERSE LA MOCHILA", R.drawable.categoria_accion_poner_mochila, 3, "en", "PUT ON BACKPACK")
        insertPictogramaCategoria(db,"SACAR EL ALMUERZO", R.drawable.categoria_accion_sacar_almuerzo, 3, "en", "TAKE OUT LUNCH")
        insertPictogramaCategoria(db,"SALIDA", R.drawable.categoria_accion_salida, 3, "en", "EXIT")
        insertPictogramaCategoria(db,"SALIDA", R.drawable.categoria_accion_salida1, 3, "en", "EXIT")

        insertPictogramaCategoria(db, "JUGAR", R.drawable.categoria_entretenimiento_jugar, 4, "en", "PLAY")
        insertPictogramaCategoria(db, "LEER", R.drawable.categoria_entretenimiento_leer, 4, "en", "READ")
    }

    private fun insertCategoria(db: SQLiteDatabase, titulo: String, imagen: Int?, color: String?, language: String, translation: String) {
        // Convert drawable to byte array
        val imageBlob = CommonUtils.drawableToByteArray(ContextCompat.getDrawable(context!!, imagen!!)!!)

        // Insert into Categoria table
        val categoriaValues = ContentValues().apply {
            put("titulo", titulo)
            put("imagen", imageBlob)
            put("color", color)
        }
        val idCategoria = db.insert("Categoria", null, categoriaValues)

        // Check if insertion was successful
        if (idCategoria == -1L) {
            Log.e("BDSQLiteHelper", "Error inserting into Categoria table")
            return
        }

        // Insert into Traduccion table
        val traduccionValues = ContentValues().apply {
            put("language", language)
            put("translation", translation)
        }
        val idTraduccion = db.insert("Traduccion", null, traduccionValues)

        // Check if insertion was successful
        if (idTraduccion == -1L) {
            Log.e("BDSQLiteHelper", "Error inserting into Traduccion table")
            return
        }

        // Insert into RelacionPictoTraduccion table
        val relacionValues = ContentValues().apply {
            put("id_categoria", idCategoria)
            put("id_traduccion", idTraduccion)
        }
        db.insert("RelacionPictoTraduccion", null, relacionValues)
    }

    private fun insertPictogramaCategoriaOLD(db: SQLiteDatabase, nombre: String, imagen: Int, idCategoria: Int, language: String, translation: String) {
        // Convert drawable to byte array
        val imagenBlob = CommonUtils.drawableToByteArray(ContextCompat.getDrawable(context!!, imagen)!!)

        // Convert drawable to byte array

        db.execSQL("INSERT INTO Pictograma (nombre, id_categoria) VALUES('$nombre', $idCategoria)")
        val cursor = db.rawQuery("SELECT last_insert_rowid()", null)
        var idPictograma: Long = -1 // Initialize id with a default value
        if (cursor != null && cursor.moveToFirst()) {
            idPictograma = cursor.getLong(0) // Get the value of the first column (which should be the id)
            cursor.close() // Close the cursor when done
        }

        db.execSQL("INSERT INTO PictogramaLocal (id, imagen) VALUES('$idPictograma', '$imagenBlob')")
        db.execSQL("INSERT INTO Traduccion (language, translation) VALUES('$language', '$translation')")
        db.execSQL("INSERT INTO RelacionPictoTraduccion (id_pictograma, id_traduccion) VALUES('$idPictograma', (SELECT last_insert_rowid()))")
    }

    private fun insertPictogramaCategoria(db: SQLiteDatabase, nombre: String, imagen: Int, idCategoria: Int, language: String, translation: String) {
        // Convert drawable to byte array
        val imagenBlob = CommonUtils.drawableToByteArray(ContextCompat.getDrawable(context!!, imagen)!!)

        // Insert into Pictograma table
        val pictogramaValues = ContentValues().apply {
            put("nombre", nombre)
            put("id_categoria_global", idCategoria)
        }
        val idPictograma = db.insert("Pictograma", null, pictogramaValues)

        // Check if insertion was successful
        if (idPictograma == -1L) {
            Log.e("BDSQLiteHelper", "Error inserting into Pictograma table")
            return
        }

        // Insert into PictogramaLocal table
        val pictogramaLocalValues = ContentValues().apply {
            put("id", idPictograma)
            put("imagen", imagenBlob)
        }
        val idPictogramaLocal = db.insert("PictogramaLocal", null, pictogramaLocalValues)

        // Check if insertion was successful
        if (idPictogramaLocal == -1L) {
            Log.e("BDSQLiteHelper", "Error inserting into PictogramaLocal table")
            return
        }

        // Insert into Traduccion table
        val traduccionValues = ContentValues().apply {
            put("language", language)
            put("translation", translation)
        }
        val idTraduccion = db.insert("Traduccion", null, traduccionValues)

        // Check if insertion was successful
        if (idTraduccion == -1L) {
            Log.e("BDSQLiteHelper", "Error inserting into Traduccion table")
            return
        }

        // Insert into RelacionPictoTraduccion table
        val relacionValues = ContentValues().apply {
            put("id_pictograma", idPictograma)
            put("id_traduccion", idTraduccion)
        }
        db.insert("RelacionPictoTraduccion", null, relacionValues)
    }

}