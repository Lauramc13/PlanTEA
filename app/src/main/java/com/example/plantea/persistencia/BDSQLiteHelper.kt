package com.example.plantea.persistencia

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import com.example.plantea.R

class BDSQLiteHelper(contexto: Context?, nombreBD: String?, factory: CursorFactory?, versionBD: Int) : SQLiteOpenHelper(contexto, nombreBD, factory, versionBD) {
    /*Sentencia SQL para crear las tablas*/
    var sqlUsuario_Planificador = "CREATE TABLE Usuario_Planificador(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, name TEXT, password TEXT, objeto TEXT, imagen TEXT, imagenObjeto TEXT, nameTEA TEXT, imagenTEA TEXT)"
    var sqlCategorias = "CREATE TABLE Categorias(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT)"
    var sqlPictograma = "CREATE TABLE Pictograma(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, imagen TEXT, id_categoria INTEGER, id_cuaderno INTEGER, FOREIGN KEY (id_categoria) REFERENCES Cuaderno(id),FOREIGN KEY (id_cuaderno) REFERENCES Cuaderno(id))"
    var sqlPictograma_Plan = "CREATE TABLE Pictograma_Plan(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, imagen TEXT, categoria INTEGER, id_plan INTEGER, FOREIGN KEY (id_plan) REFERENCES Planificacion(id))"
    var sqlPlanificacion = "CREATE TABLE Planificacion(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, id_usuario INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario_Planificador(id))"
    var sqlCuaderno = "CREATE TABLE Cuaderno(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT)"
    var sqlEvento = "CREATE TABLE Evento(id INTEGER PRIMARY KEY AUTOINCREMENT, id_usuario INTEGER, nombre TEXT, fecha TEXT, hora TEXT, id_plan INTEGER, imagen TEXT,visible INTEGER, FOREIGN KEY (id_plan) REFERENCES Planificacion(id), FOREIGN KEY (id_usuario) REFERENCES Usuario_Planificador(id))"
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
            meterDatos(db)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, versionAnterior: Int, versionNueva: Int) {
        try {
            /*Se elimina la versión anterior de la table*/
            db.execSQL("DROP TABLE IF EXISTS Usuario_Planificador")
            db.execSQL("DROP TABLE IF EXISTS Categorias")
            db.execSQL("DROP TABLE IF EXISTS Pictograma")
            db.execSQL("DROP TABLE IF EXISTS Pictograma_Plan")
            db.execSQL("DROP TABLE IF EXISTS Planificacion")
            db.execSQL("DROP TABLE IF EXISTS Cuaderno")
            db.execSQL("DROP TABLE IF EXISTS Evento")
            /*Se crea la nueva versión de la table*/onCreate(db)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun meterDatos(db: SQLiteDatabase) {
        //Categorias
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('CONSULTAS')")
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('PROFESIONALES')")
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('LUGARES')")
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('DESPLAZAMIENTO')")
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('ACCION')")
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('ENTRETENIMIENTO')")
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('RECOMPENSA')")
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('REVISIÓN')")
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('VACUNACIÓN')")
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('PRUEBAS')")
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('OFTALMOLOGÍA')")
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('ODONTOLOGÍA')")
        db.execSQL("INSERT INTO Categorias (titulo) VALUES('LOGOPEDIA')")

        //Cuaderno
        db.execSQL("INSERT INTO Cuaderno (titulo) VALUES('TIEMPO')")
        db.execSQL("INSERT INTO Cuaderno (titulo) VALUES('SINTOMAS')")
        db.execSQL("INSERT INTO Cuaderno (titulo) VALUES('DOLOR')")
        db.execSQL("INSERT INTO Cuaderno (titulo) VALUES('ESCALA')")

        //Pictograma
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('REVISIÓN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_consultas + "', 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('VACUNACIÓN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categorias_consultas_vacunacion + "', 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PRUEBAS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categorias_consultas_pruebas + "', 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('OFTALMOLOGÍA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_consultas_oftalmologa + "', 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ODONTOLOGÍA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_consultas_dentista + "', 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LOGOPEDIA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_consultas_logopedia + "', 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MÉDICO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesionales_medico + "', 2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LOGOPEDA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesionales_logopeda + "', 2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ENFERMERA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesionales_enfermera + "', 2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('OFTALMÓLOGO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesionales_oculista + "', 2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('DENTISTA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesionales_dentista + "', 2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CELADOR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_profesionales_celador + "', 2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CENTRO DE SALUD', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_centrosalud + "', 3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('HOSPITAL', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_hospital + "', 3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CASA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_casa + "', 3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SALA DE ESPERA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_salaespera + "', 3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('RECEPCIÓN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_recepcion + "', 3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CONSULTA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_lugares_consulta + "', 3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('COCHE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_desplazamiento_coche + "', 4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('AUTOBÚS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_desplazamiento_autobus + "', 4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('AMBULANCIA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_desplazamiento_ambulancia + "', 4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TREN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_desplazamiento_tren + "', 4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('METRO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_desplazamiento_metro + "', 4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CAMINANDO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_desplazamiento_caminando + "', 4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('IR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_ir + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('VOLVER', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_volver + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ESPERAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_esperar + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ENTRAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_entrar + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SALUDAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_saludar + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('DESPEDIRSE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_despedirse + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('HABLAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_hablar + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ESCUCHAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_escuchar + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MIRAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_mirar + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SENTARSE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_accion_sentarse + "', 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('JUGAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_entretenimiento_jugar + "', 6)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LEER', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_entretenimiento_leer + "', 6)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('JUGAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_recompensa_jugar + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('IR AL PARQUE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_recompensa_parque + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('VER LA TELEVISIÓN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_recompensa_tele + "', 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('RECONOCIMIENTO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_revision_reconocimiento + "', 8)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('AUSCULTAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_revision_auscultar + "', 8)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CURAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_revision_curar + "', 8)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MIRAR GARGANTA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_revision_mirargarganta + "', 8)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MIRAR OÍDOS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_revision_miraroidos + "', 8)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PESAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_revision_pesar + "', 8)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MEDIR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_revision_medir + "', 8)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('DESINFECTAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_vacunacion_desinfectar + "', 9)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('VACUNAR', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_vacunacion_vacunar + "', 9)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PONER PARCHE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_vacunacion_parche + "', 9)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PRUEBA ALERGIA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_pruebas_alergia + "', 10)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PRUEBA DE SANGRE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_pruebas_sangre + "', 10)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FROTIS GARGANTA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_pruebas_frotisgarganta + "', 10)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FROTIS NASAL', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_pruebas_frotisnasal + "', 10)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('EXAMEN VISTA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_oculista_examenvista + "', 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('GRADUAR VISTA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_oculista_graduarvista + "', 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('OFTALMOSCOPIA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_oculista_oftalmoscopia + "', 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('OPTOMETRÍA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_oculista_optometria + "', 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PONER PARCHE', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_oculista_ponerparche + "', 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('REVISIÓN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_dentista_revisiondentista + "', 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('DIENTES', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_dentista_dientes + "', 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CARIES', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_dentista_caries + "', 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('BRACKETS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_dentista_brackets + "', 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ABRIR LA BOCA', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_dentista_boca + "', 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SILLÓN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_dentista_sillon + "', 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ARTICULACIÓN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_logopeda_articulacion + "', 13)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SOPLO', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_logopedia_soplo + "', 13)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PRAXIAS', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_logopeda_praxias + "', 13)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('RESPIRACIÓN', '" + "android.resource://com.example.plantea" + "/" + R.drawable.categoria_logopeda_respiracion + "', 13)")

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
    }
}