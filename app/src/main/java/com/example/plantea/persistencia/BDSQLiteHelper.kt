package com.example.plantea.persistencia

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import com.example.plantea.R

class BDSQLiteHelper(contexto: Context?, nombreBD: String?, factory: CursorFactory?, versionBD: Int) : SQLiteOpenHelper(contexto, nombreBD, factory, versionBD) {
    /*Sentencia SQL para crear las tablas*/
    var sqlUsuario_Planificador = "CREATE TABLE Usuario_Planificador(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, username TEXT UNIQUE, name TEXT, password TEXT, objeto TEXT, imagen TEXT, imagenObjeto TEXT, nameTEA TEXT, imagenTEA TEXT)"
    var sqlCategorias = "CREATE TABLE Categorias(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT)"
    var sqlPictograma = "CREATE TABLE Pictograma(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, imagen TEXT, id_categoria INTEGER, id_cuaderno INTEGER, id_usuario INTEGER, FOREIGN KEY (id_categoria) REFERENCES Cuaderno(id),FOREIGN KEY (id_cuaderno) REFERENCES Cuaderno(id), FOREIGN KEY (id_usuario) REFERENCES Usuario_Planificador(id))"
    var sqlPictograma_Plan = "CREATE TABLE Pictograma_Plan(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, imagen TEXT, categoria INTEGER, historia TEXT, id_plan INTEGER, FOREIGN KEY (id_plan) REFERENCES Planificacion(id))"
    var sqlPlanificacion = "CREATE TABLE Planificacion(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, id_usuario INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario_Planificador(id))"
    var sqlCuaderno = "CREATE TABLE Cuaderno(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT)"
    var sqlEvento = "CREATE TABLE Evento(id INTEGER PRIMARY KEY AUTOINCREMENT, id_usuario INTEGER, nombre TEXT, fecha TEXT, hora TEXT, id_plan INTEGER,visible INTEGER, FOREIGN KEY (id_plan) REFERENCES Planificacion(id), FOREIGN KEY (id_usuario) REFERENCES Usuario_Planificador(id))"
    var sqlFavorito = "CREATE TABLE Favorito(id INTEGER PRIMARY KEY AUTOINCREMENT, id_usuario INTEGER, id_picto INTEGER, nombre TEXT, imagen TEXT, id_categoria INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario_Planificador(id))"
    override fun onCreate(db: SQLiteDatabase) {
        try {
            /*Se ejecuta la sentencia SQL de creación de la tabla*/
            db.execSQL(sqlUsuario_Planificador)
            db.execSQL(sqlCategorias)
            db.execSQL(sqlPictograma)
            db.execSQL(sqlPictograma_Plan)
            db.execSQL(sqlPlanificacion)
            db.execSQL(sqlCuaderno)
            db.execSQL(sqlEvento)
            db.execSQL(sqlFavorito)
            meterDatos(db)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, versionAnterior: Int, versionNueva: Int) {
        try {
            /*Se elimina la versión anterior de la tablet*/
            db.execSQL("DROP TABLE IF EXISTS Usuario_Planificador")
            db.execSQL("DROP TABLE IF EXISTS Categorias")
            db.execSQL("DROP TABLE IF EXISTS Pictograma")
            db.execSQL("DROP TABLE IF EXISTS Pictograma_Plan")
            db.execSQL("DROP TABLE IF EXISTS Planificacion")
            db.execSQL("DROP TABLE IF EXISTS Cuaderno")
            db.execSQL("DROP TABLE IF EXISTS Evento")
            db.execSQL("DROP TABLE IF EXISTS Favorito")

            /*Se crea la nueva versión de la table*/onCreate(db)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun meterDatos(db: SQLiteDatabase) {
        //Categorias
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('CONSULTAS')") // 1
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('PELUQUERIA')") // 2
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('COMPRA')") // 3
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('COLEGIO')") // 4
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('LUGARES')") // 5
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('DESPLAZAMIENTO')") // 6 
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('ACCION')") // 7
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('ENTRETENIMIENTO')") // 8
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('RECOMPENSA')") // 9 
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('FAVORITOS')") // 10 
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('REVISIÓN')") // 11 
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('PROFESIONALES')") // 12 
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('VACUNACIÓN')") // 13 
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('PRUEBAS')") // 14 
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('OFTALMOLOGÍA')") // 15 
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('ODONTOLOGÍA')") // 16
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('LOGOPEDIA')") // 17
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('KIT DE PELUQUERIA')") // 18
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('CORTES DE PELO')") // 19
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('TRABAJADORES')") // 20
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('ALIMENTOS')") // 21
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('OBJETOS')") // 22
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('EMPLEADOS')") // 23
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('UTILES')") // 24
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('PROFESORES')") // 25

        //Cuaderno
        db.execSQL("INSERT INTO Cuaderno (titulo) VALUES('TIEMPO')")
        db.execSQL("INSERT INTO Cuaderno (titulo) VALUES('SINTOMAS')")
        db.execSQL("INSERT INTO Cuaderno (titulo) VALUES('DOLOR')")
        db.execSQL("INSERT INTO Cuaderno (titulo) VALUES('ESCALA')")
        db.execSQL("INSERT INTO Cuaderno (titulo) VALUES('SENTIMIENTOS')")

        //Pictograma
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('REVISIÓN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_consultas + "', 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PROFESIONALES', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesionales + "', 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('VACUNACIÓN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categorias_consultas_vacunacion + "', 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PRUEBAS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categorias_consultas_pruebas + "', 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('OFTALMOLOGÍA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_consultas_oftalmologa + "', 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ODONTOLOGÍA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_consultas_dentista + "', 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LOGOPEDIA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_consultas_logopedia + "', 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('KIT DE PELUQUERIA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_kit_peine_tijeras + "', 2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CORTES DE PELO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_cortes_pelo + "', 2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TRABAJADORES', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_trabajadores + "', 2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ALIMENTOS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_alimentos + "', 3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('OBJETOS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_objetos_dinero + "', 3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('EMPLEADOS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_empleados_cajero + "', 3)")

        // COLEGIO = 4
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('UTILES', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_crayones + "', 4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PROFESORES', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesores_maestra + "', 4)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CASA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_casa + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CENTRO DE SALUD', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_centrosalud + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('HOSPITAL', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_hospital + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SALA DE ESPERA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_salaespera + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('RECEPCIÓN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_recepcion + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CONSULTA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_consulta + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('AULA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_aula + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('COLEGIO BILINGÜE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_colegio_bilingue + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('COLEGIO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_colegio + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CARNICERÍA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_carniceria + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FRUTERÍA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_fruteria + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PELUQUERÍA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_peluqueria + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PESCADERÍA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_pescaderia + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SUPERMERCADO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_supermercado + "', 5)")
        //Lugares
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('COCHE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_desplazamiento_coche + "', 6)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('AUTOBÚS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_desplazamiento_autobus + "', 6)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('AMBULANCIA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_desplazamiento_ambulancia + "', 6)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TREN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_desplazamiento_tren + "', 6)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('METRO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_desplazamiento_metro + "', 6)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CAMINANDO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_desplazamiento_caminando + "', 6)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('IR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_ir + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('VOLVER', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_volver + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ESPERAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_esperar + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ENTRAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_entrar + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SALUDAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_saludar + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('DESPEDIRSE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_despedirse + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('HABLAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_hablar + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ESCUCHAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_escuchar + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MIRAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_mirar + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SENTARSE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_sentarse + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('BAJAR ESCALERA MECÁNICA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_bajar_escalera_mecanica + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SUBIR ESCALERA MECÁNICA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_subir_escalera_mecanica + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('BAJAR RAMPA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_bajar_rampa + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SUBIR RAMPA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_subir_rampa + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CORTAR PELO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_cortar_pelo + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CORTAR PELO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_cortar_pelo1 + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LAVAR PELO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_lavar_pelo + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SECAR EL PELO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_secar_pelo + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PAGAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_pagar + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('COGER TICKET', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_coger_ticket + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('COGER TURNO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_coger_turno + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ESPERAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_esperar1 + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PREGUNTAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_preguntar + "', 7)")    
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PREGUNTAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_preguntar1 + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PREGUNTAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_preguntar2 + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CERRAR MOCHILA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_cerrar_mochila + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ABRIR MOCHILA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_abrir_mochila + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('APROBAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_aprobar + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SUSPENDER', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_suspender + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ENTRADA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_entrada + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ENTRADA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_entrada1 + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SALIDA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_salida + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('IR AL COLEGIO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_ir_colegio + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LECTURA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_lectura + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LEER', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_leer + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LEER', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_leer1 + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LLEVAR AL COLEGIO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_llevar_colegio + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('METER EL ALMUERZO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_meter_almuerzo + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PONERSE LA MOCHILA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_poner_mochila + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SACAR EL ALMUERZO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_sacar_almuerzo + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SALIDA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_salida + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SALIDA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_salida1 + "', 7)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('JUGAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_entretenimiento_jugar + "', 8)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LEER', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_entretenimiento_leer + "', 8)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('JUGAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_recompensa_jugar + "', 9)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('IR AL PARQUE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_recompensa_parque + "', 9)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('VER LA TELEVISIÓN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_recompensa_tele + "', 9)")
        // FAVORITOS = 10

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('RECONOCIMIENTO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_revision_reconocimiento + "', 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('AUSCULTAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_revision_auscultar + "', 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CURAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_revision_curar + "', 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MIRAR GARGANTA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_revision_mirargarganta + "', 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MIRAR OÍDOS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_revision_miraroidos + "', 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PESAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_revision_pesar + "', 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MEDIR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_revision_medir + "', 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MÉDICO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesionales_medico + "', 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LOGOPEDA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesionales_logopeda + "', 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ENFERMERA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesionales_enfermera + "', 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('OFTALMÓLOGO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesionales_oculista + "', 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('DENTISTA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesionales_dentista + "', 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CELADOR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesionales_celador + "', 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('DESINFECTAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_vacunacion_desinfectar + "', 13)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('VACUNAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_vacunacion_vacunar + "', 13)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PONER PARCHE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_vacunacion_parche + "', 13)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PRUEBA ALERGIA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_pruebas_alergia + "', 14)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PRUEBA DE SANGRE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_pruebas_sangre + "', 14)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FROTIS GARGANTA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_pruebas_frotisgarganta + "', 14)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FROTIS NASAL', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_pruebas_frotisnasal + "', 14)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('EXAMEN VISTA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_oculista_examenvista + "', 15)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('GRADUAR VISTA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_oculista_graduarvista + "', 15)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('OFTALMOSCOPIA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_oculista_oftalmoscopia + "', 15)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('OPTOMETRÍA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_oculista_optometria + "', 15)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PONER PARCHE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_oculista_ponerparche + "', 15)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('REVISIÓN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_dentista_revisiondentista + "', 16)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('DIENTES', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_dentista_dientes + "', 16)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CARIES', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_dentista_caries + "', 16)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('BRACKETS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_dentista_brackets + "', 16)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ABRIR LA BOCA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_dentista_boca + "', 16)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SILLÓN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_dentista_sillon + "', 16)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ARTICULACIÓN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_logopeda_articulacion + "', 17)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SOPLO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_logopedia_soplo + "', 17)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PRAXIAS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_logopeda_praxias + "', 17)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('RESPIRACIÓN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_logopeda_respiracion + "', 17)")

        //CATEGORIAS NUEVAS: 18(COMPRA), 19(PELUQUERIA), 20(COLEGIO), 21(FAOVORITOS)
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CEPILLO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_kit_cepillo_pelo + "', 18)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAQUINILLA DE CORTAR EL PELO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_kit_maquinilla_cortar_pelo + "', 18)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PEINE Y TIJERAS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_kit_peine_tijeras + "', 18)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PLANCHA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_kit_plancha_pelo + "', 18)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('RIZADOR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_kit_rizador + "', 18)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SECADOR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_kit_secador + "', 18)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TIJERAS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_kit_tijeras + "', 18)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FLEQUILLO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_cortes_flequillo + "', 19)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PELO LARGO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_cortes_pelo + "', 19)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PELO CORTO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_cortes_pelo_corto + "', 19)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PELO RIZADO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_cortes_pelo_rizado + "', 19)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PELO RIZADO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_cortes_pelo_rizado2 + "', 19)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PERILLA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_cortes_perilla + "', 19)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TEÑIR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_cortes_tenir + "', 19)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PELUQUERA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_trabajadores_peluquera + "', 20)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PELUQUERO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_trabajadores_peluquero + "', 20)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ACEITE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_aceite + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ADEREZO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_aderezo + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('BOLSA DE PATATAS FRITAS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_bolsa_fritos + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CARNE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_carne + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CHOCOLATE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_chocolate + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CHURROS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_churros + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('DONUT', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_donut + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FLAN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_flan + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('HORTALIZAS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_hortalizas + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MERMELADA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_mermelada + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MIEL', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_miel + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MOSTAZA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_mostaza + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('NATILLA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_natilla + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PAN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_pan + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PAN DE HAMBURGUESA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_pan_hamburguesa + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PAN DE MOLDE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_pan_molde + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PATATAS FRITAS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_patatas_fritas + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PESCADO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_pescado + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SALSA DE TOMATE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_salsa_ketchup + "', 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TOSTADA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_alimentos_tostada + "', 21)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('BOLSA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_objetos_bolsa + "', 22)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('BOLSA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_objetos_bolsa1 + "', 22)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CARRITO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_objetos_carrito + "', 22)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('DINERO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_objetos_dinero + "', 22)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TARJETA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_objetos_tarjeta + "', 22)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TICKET', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_objetos_ticket + "', 22)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CAJERO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_empleados_cajero + "', 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CAJERO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_empleados_cajero1 + "', 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CARNICERA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_empleados_carnicera + "', 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CARNICERO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_empleados_carnicero + "', 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FRUTERA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_empleados_frutera + "', 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FRUTERO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_empleados_frutero + "', 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MANIPULADOR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_empleados_manipulador_alimentos + "', 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PANADERA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_empleados_panadera + "', 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PANADERO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_empleados_panadero + "', 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PESCADERA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_empleados_pescadera + "', 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PESCADERO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_empleados_pescadero + "', 23)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LIBRO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_biblioteca + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('BOLIGRAFO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_boligrafo + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CELO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_celo + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CHINCHETAS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_chinchetas + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('COMPAS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_compas + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CORRECTOR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_corrector_liquido + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CRAYONES', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_crayones + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CUADERNO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_cuaderno + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ESCUADRA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_escuadra + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FOLIOS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_folios + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('GOMA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_goma + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('GRAPADORA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_grapadora + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LAPIZ Y PAPEL', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_lapiz_papel + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LIBRETA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_libreta + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MESA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_mesa_colegio + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MESA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_mesa_colegio1 + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PAPELERA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_papelera + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PEGAMENTO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_pegamento + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('REGLA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_regla + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ROTULADOR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_rotulador + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SACAPUNTAS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_sacapuntas + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SILLA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_silla_colegio + "', 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TIJERAS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_utiles_tijeras + "', 24)")
    
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAESTRA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesores_maestra + "', 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAESTRO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesores_maestro + "', 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAESTRA DE MATES', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesores_maestra_mates + "', 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAESTRO DE MATES', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesores_maestro_mates + "', 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAESTRO DE MUSICA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesores_profesor_musica + "', 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAESTRA DE MUSICA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesores_profesora_musica + "', 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAESTRO DE TALLER', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesores_profesor_taller + "', 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAESTRA DE TALLER', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesores_profesora_taller + "', 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('EDUCACION FISICA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesores_profesora_educacion_fisica + "', 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('EDUCACION FISICA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categorias_profesores_profesor_educacion_fisica + "', 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ORIENTACION', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesores_servicio_orientacion + "', 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ORIENTACION', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesores_servicio_orientacion1 + "', 25)")
        

        //PICTOGRAMAS CORRESPONDIENTES AL CUADERNO DE COMUNICACIÓN
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('IZQUIERDA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_escala_izquierda + "',1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('DERECHA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_escala_derecha + "',1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('SI', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_escala_bien + "',1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('NO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_escala_mal + "',1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('HORAS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_escala_hora + "',1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('DÍAS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_escala_dia + "',1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('SEMANAS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_escala_semana + "',1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('MESES', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_escala_mes + "',1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('AÑO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_escala_anio + "',1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('MAREO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sintomas_mareo + "',2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('CANSANCIO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sintomas_cansancio + "',2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('ESCALOFRIOS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sintomas_escalofrios + "',2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('TOS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sintomas_tos + "',2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('FIEBRE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sintomas_fiebre + "',2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('NO VEO BIEN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sintomas_noveobien + "',2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('VOMITOS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sintomas_vomitar + "',2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('ALERGIA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sintomas_alergia + "',2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('RESFRIADO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sintomas_resfriado + "',2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('SARPULLIDO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sintomas_sarpullido + "',2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('HERIDA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sintomas_herida + "',2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('QUEMADURA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sintomas_quemadura + "',2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('TAQUICARDIA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sintomas_taquicardia + "',2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('DIARREA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sintomas_diarrea + "',2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('ESTREÑIMIENTO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sintomas_estrenimiento + "',2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('CUELLO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_dolor_cuello + "',3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('ESPALDA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_dolor_espalda + "',3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('PECHO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_dolor_pecho + "',3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('CULO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_dolor_culo + "',3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('MUÑECA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_dolor_muneca + "',3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('RODILLA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_dolor_rodilla + "',3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('TOBILLO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_dolor_tobillo + "',3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('PIE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_dolor_pie + "',3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('MUELA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_dolor_muela + "',3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('OÍDO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_dolor_oido + "',3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('CABEZA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_dolor_cabeza + "',3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('BRAZO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_dolor_brazo + "',3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('GARGANTA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_dolor_garganta + "',3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('TRIPA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_dolor_estomago + "',3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('PIERNA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_dolor_pierna + "',3)")


        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('ABURRIDO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_aburrir + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('ALEGRE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_alegrar + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('AMADA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_amada + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('AMADO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_amado + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('ANSIOSO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_ansioso + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('ASQUEADO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_asco + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('ASUSTADO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_asustar + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('AUTOESTIMA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_autoestima + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('AVERGONZADO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_avergonzar + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('CANSADO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_cansar + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('CONDICION', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_condicion + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('CONFUNDIDO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_confundir + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('DISTRAIDO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_distraer + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('ENAMORADO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_enamorado + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('ENFADADO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_enfadar + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('ENTRISTECIDO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_entristecer + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('ENVIDIOSO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_envidia + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('NOSTALGICO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_nostalgico + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('REGOCIJAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_regocijar + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('SERIO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_serio + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('SORPRENDIDO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_sorprender + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('TENER MIEDO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_tener_miedo + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('TRANQUILO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_tranquilo + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('TRANQUILO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_tranquilo1 + "',4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_cuaderno) VALUES('VERGUENZA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.cuaderno_sentimientos_verguenza + "',4)")
    }
}