package com.example.plantea.persistencia

import android.annotation.SuppressLint
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

class BDSQLiteHelper(contexto: Context?, nombreBD: String?, factory: CursorFactory?, versionBD: Int) : SQLiteOpenHelper(contexto, nombreBD, factory, versionBD) {
    /*Sentencia SQL para crear las tablas*/

    private var sqlUsuario = "CREATE TABLE Usuario(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, password TEXT, username TEXT, name TEXT, imagen TEXT, objeto TEXT, imagenObjeto TEXT, nameTEA TEXT, imagenTEA TEXT)"
    private var sqlCategorias = "CREATE TABLE Categoria(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, imagen TEXT, principal BOOLEAN, color TEXT, id_usuario INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlPictograma = "CREATE TABLE Pictograma(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, imagen TEXT, id_categoria INTEGER, id_usuario INTEGER, FOREIGN KEY (id_categoria) REFERENCES Categoria(id), FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlPictogramaAPI = "CREATE TABLE PictogramaAPI(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, imagen TEXT)"
    private var sqlFavorito = "CREATE TABLE Favorito(id INTEGER PRIMARY KEY AUTOINCREMENT, id_pictograma INTEGER, id_usuario INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario(id), FOREIGN KEY (id_pictograma) REFERENCES PictogramaAPI(id))"
    private var sqlCuaderno = "CREATE TABLE Cuaderno(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, imagen TEXT, termometro BOOLEAN, id_usuario INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlRelacionPictogramaCuaderno = "CREATE TABLE RelacionPictogramaCuaderno(id INTEGER PRIMARY KEY AUTOINCREMENT, id_pictograma INTEGER, id_pictogramaAPI INTEGER, id_cuaderno INTEGER, FOREIGN KEY (id_pictograma) REFERENCES Pictograma(id), FOREIGN KEY (id_cuaderno) REFERENCES Cuaderno(id), FOREIGN KEY (id_pictogramaAPI) REFERENCES PictogramaAPI(id))"
    private var sqpPlanificacion = "CREATE TABLE Planificacion(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, id_usuario INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlEvento = "CREATE TABLE Evento(id INTEGER PRIMARY KEY AUTOINCREMENT, id_usuario INTEGER, nombre TEXT, fecha TEXT, hora TEXT, visible INTEGER, id_plan INTEGER, FOREIGN KEY (id_plan) REFERENCES Planificacion(id), FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlRelacionPictogramaPlan = "CREATE TABLE RelacionPictogramaPlan (id INTEGER PRIMARY KEY AUTOINCREMENT, historia TEXT, duracion TEXT, id_plan INTEGER, id_pictograma INTEGER, id_pictogramaAPI INTEGER, FOREIGN KEY (id_plan) REFERENCES Planificacion(id), FOREIGN KEY (id_pictograma) REFERENCES Pictograma(id), FOREIGN KEY (id_pictogramaAPI) REFERENCES PictogramaAPI(id))"

    override fun onCreate(db: SQLiteDatabase) {
        try {
            /*Se ejecuta la sentencia SQL de creación de la tabla*/
            db.execSQL(sqlUsuario)
            db.execSQL(sqlCategorias)
            db.execSQL(sqlPictograma)
            db.execSQL(sqlPictogramaAPI)
            db.execSQL(sqlFavorito)
            db.execSQL(sqlCuaderno)
            db.execSQL(sqlRelacionPictogramaCuaderno)
            db.execSQL(sqpPlanificacion)
            db.execSQL(sqlRelacionPictogramaPlan)
            db.execSQL(sqlEvento)
            meterDatos(db)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, versionAnterior: Int, versionNueva: Int) {
        try {
            /*Se elimina la versión anterior de la tablet*/
            db.execSQL("DROP TABLE IF EXISTS Usuario")
            db.execSQL("DROP TABLE IF EXISTS Categorias")
            db.execSQL("DROP TABLE IF EXISTS Pictograma")
            db.execSQL("DROP TABLE IF EXISTS PictogramaAPI")
            db.execSQL("DROP TABLE IF EXISTS Favorito")
            db.execSQL("DROP TABLE IF EXISTS Planificacion")
            db.execSQL("DROP TABLE IF EXISTS Cuaderno")
            db.execSQL("DROP TABLE IF EXISTS RelacionPictogramaCuaderno")
            db.execSQL("DROP TABLE IF EXISTS RelacionPictogramaPlan")
            db.execSQL("DROP TABLE IF EXISTS Evento")

            /*Se crea la nueva versión de la table*/
            onCreate(db)

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun meterDatos(db: SQLiteDatabase) {
        //Categoria
        db.execSQL("INSERT INTO Categoria (titulo, imagen, principal, color) VALUES('IR AL MEDICO', 'categoria_consultas', true, 'default')") // 1
        db.execSQL("INSERT INTO Categoria (titulo, imagen, principal, color) VALUES('CORTARSE EL PELO', 'categoria_peluqueria', true, 'default')") // 2
        db.execSQL("INSERT INTO Categoria (titulo, imagen, principal, color) VALUES('HACER LA COMPRA', 'categoria_hacer_la_compra', true, 'default')") // 3
        db.execSQL("INSERT INTO Categoria (titulo, imagen, principal, color) VALUES('IR AL COLEGIO', 'categoria_colegio', true, 'default')") // 4
        db.execSQL("INSERT INTO Categoria (titulo, imagen, principal, color) VALUES('LUGARES', 'categoria_lugares', true, 'default')") // 5
        db.execSQL("INSERT INTO Categoria (titulo, imagen, principal, color) VALUES('DESPLAZAMIENTO', 'categoria_desplazamiento', true, 'default')") // 6
        db.execSQL("INSERT INTO Categoria (titulo, imagen, principal, color) VALUES('ACCION', 'categoria_accion', true, 'default')") // 7
        db.execSQL("INSERT INTO Categoria (titulo, imagen, principal, color) VALUES('ENTRETENIMIENTO', 'categoria_entretenimiento', true, 'blue')") // 8
        db.execSQL("INSERT INTO Categoria (titulo, imagen, principal, color) VALUES('RECOMPENSA', 'categoria_recompensa', true, 'blue')") // 9
        db.execSQL("INSERT INTO Categoria (titulo, imagen, principal, color) VALUES('FAVORITOS', 'categoria_favorito', true, 'yellow')") // 10

        db.execSQL("INSERT INTO Categoria (titulo) VALUES('REVISIÓN')") // 11 
        db.execSQL("INSERT INTO Categoria (titulo) VALUES('PROFESIONALES')") // 12 
        db.execSQL("INSERT INTO Categoria (titulo) VALUES('VACUNACIÓN')") // 13 
        db.execSQL("INSERT INTO Categoria (titulo) VALUES('PRUEBAS')") // 14 
        db.execSQL("INSERT INTO Categoria (titulo) VALUES('OFTALMOLOGÍA')") // 15 
        db.execSQL("INSERT INTO Categoria (titulo) VALUES('ODONTOLOGÍA')") // 16
        db.execSQL("INSERT INTO Categoria (titulo) VALUES('LOGOPEDIA')") // 17
        db.execSQL("INSERT INTO Categoria (titulo) VALUES('KIT DE PELUQUERIA')") // 18
        db.execSQL("INSERT INTO Categoria (titulo) VALUES('CORTES DE PELO')") // 19
        db.execSQL("INSERT INTO Categoria (titulo) VALUES('TRABAJADORES')") // 20
        db.execSQL("INSERT INTO Categoria (titulo) VALUES('ALIMENTOS')") // 21
        db.execSQL("INSERT INTO Categoria (titulo) VALUES('OBJETOS')") // 22
        db.execSQL("INSERT INTO Categoria (titulo) VALUES('EMPLEADOS')") // 23
        db.execSQL("INSERT INTO Categoria (titulo) VALUES('UTILES')") // 24
        db.execSQL("INSERT INTO Categoria (titulo) VALUES('PROFESORES')") // 25

        //Cuaderno
        //var sqlCuaderno = "CREATE TABLE Cuaderno(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, imagen TEXT, termometro BOOLEAN, id_usuario INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario_Planificador(id))"


        db.execSQL("INSERT INTO Cuaderno (titulo, imagen, termometro) VALUES('ESCALA', 'cuaderno_sintomas', 0)")
        db.execSQL("INSERT INTO Cuaderno (titulo, imagen, termometro) VALUES('¿QUÉ TE PASA?',  'cuaderno_sintomas'  , 1)")
        db.execSQL("INSERT INTO Cuaderno (titulo, imagen, termometro) VALUES('¿DÓNDE TE DUELE?',  'cuaderno_dolores'  , 1)")
        db.execSQL("INSERT INTO Cuaderno (titulo, imagen, termometro) VALUES('¿COMO TE SIENTES?',  'card_sentimientos'  , 0)")

        //Pictograma
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('REVISIÓN',  'categoria_consultas'  , 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PROFESIONALES',  'categoria_profesionales'  , 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('VACUNACIÓN',  'categorias_consultas_vacunacion'  , 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PRUEBAS',  'categorias_consultas_pruebas'  , 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('OFTALMOLOGÍA',  'categoria_consultas_oftalmologa'  , 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ODONTOLOGÍA',  'categoria_consultas_dentista'  , 1)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LOGOPEDIA',  'categoria_consultas_logopedia'  , 1)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('KIT DE PELUQUERIA',  'categoria_kit_peine_tijeras'  , 2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CORTES DE PELO',  'categoria_cortes_pelo'  , 2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TRABAJADORES',  'categoria_trabajadores'  , 2)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ALIMENTOS',  'categoria_alimentos_alimentos'  , 3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('OBJETOS',  'categoria_objetos_dinero'  , 3)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('EMPLEADOS',  'categoria_empleados_cajero'  , 3)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('UTILES',  'categoria_utiles_crayones'  , 4)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PROFESORES',  'categoria_profesores_maestra'  , 4)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CASA',  'categoria_lugares_casa'  , 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CENTRO DE SALUD',  'categoria_lugares_centrosalud'  , 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('HOSPITAL',  'categoria_lugares_hospital'  , 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SALA DE ESPERA',  'categoria_lugares_salaespera'  , 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('RECEPCIÓN',  'categoria_lugares_recepcion'  , 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CONSULTA',  'categoria_lugares_consulta'  , 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('AULA',  'categoria_lugares_aula'  , 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('COLEGIO BILINGÜE',  'categoria_lugares_colegio_bilingue'  , 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('COLEGIO',  'categoria_lugares_colegio'  , 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CARNICERÍA',  'categoria_lugares_carniceria'  , 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FRUTERÍA',  'categoria_lugares_fruteria'  , 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PELUQUERÍA',  'categoria_peluqueria'  , 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PESCADERÍA',  'categoria_lugares_pescaderia'  , 5)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SUPERMERCADO',  'categoria_lugares_supermercado'  , 5)")
        //Lugares
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('COCHE',  'categoria_desplazamiento_coche'  , 6)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('AUTOBÚS',  'categoria_desplazamiento_autobus'  , 6)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('AMBULANCIA',  'categoria_desplazamiento_ambulancia'  , 6)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TREN',  'categoria_desplazamiento_tren'  , 6)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('METRO',  'categoria_desplazamiento_metro'  , 6)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CAMINANDO',  'categoria_desplazamiento_caminando'  , 6)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('IR',  'categoria_accion_ir'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('VOLVER',  'categoria_accion_volver'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ESPERAR',  'categoria_accion_esperar'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ENTRAR',  'categoria_accion_entrar'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SALUDAR',  'categoria_accion_saludar'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('DESPEDIRSE',  'categoria_accion_despedirse'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('HABLAR',  'categoria_accion_hablar'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ESCUCHAR',  'categoria_accion_escuchar'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MIRAR',  'categoria_accion_mirar'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SENTARSE',  'categoria_accion_sentarse'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('BAJAR ESCALERA MECÁNICA',  'categoria_accion_bajar_escalera_mecanica'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SUBIR ESCALERA MECÁNICA',  'categoria_accion_subir_escalera_mecanica'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('BAJAR RAMPA',  'categoria_accion_bajar_rampa'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SUBIR RAMPA',  'categoria_accion_subir_rampa'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CORTAR PELO',  'categoria_accion_cortar_pelo'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CORTAR PELO',  'categoria_accion_cortar_pelo1'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LAVAR PELO',  'categoria_accion_lavar_pelo'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SECAR EL PELO',  'categoria_accion_secar_pelo'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PAGAR',  'categoria_accion_pagar'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('COGER TICKET',  'categoria_accion_coger_ticket'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('COGER TURNO',  'categoria_accion_coger_turno'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ESPERAR',  'categoria_accion_esperar1'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PREGUNTAR',  'categoria_accion_preguntar'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PREGUNTAR',  'categoria_accion_preguntar1'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PREGUNTAR',  'categoria_accion_preguntar2'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CERRAR MOCHILA',  'categoria_accion_cerrar_mochila'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ABRIR MOCHILA',  'categoria_accion_abrir_mochila'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('APROBAR',  'categoria_accion_aprobar'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SUSPENDER',  'categoria_accion_suspender'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ENTRADA',  'categoria_accion_entrada'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ENTRADA',  'categoria_accion_entrada1'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SALIDA',  'categoria_accion_salida'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('IR AL COLEGIO',  'categoria_accion_ir_colegio'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LECTURA',  'categoria_accion_lectura'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LEER',  'categoria_accion_leer'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LEER',  'categoria_accion_leer1'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LLEVAR AL COLEGIO',  'categoria_accion_llevar_colegio'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('METER EL ALMUERZO',  'categoria_accion_meter_almuerzo'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PONERSE LA MOCHILA',  'categoria_accion_poner_mochila'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SACAR EL ALMUERZO',  'categoria_accion_sacar_almuerzo'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SALIDA',  'categoria_accion_salida'  , 7)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SALIDA',  'categoria_accion_salida1'  , 7)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('JUGAR',  'categoria_entretenimiento_jugar'  , 8)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LEER',  'categoria_entretenimiento_leer'  , 8)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('JUGAR',  'categoria_recompensa_jugar'  , 9)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('IR AL PARQUE',  'categoria_recompensa_parque'  , 9)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('VER LA TELEVISIÓN',  'categoria_recompensa_tele'  , 9)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('RECONOCIMIENTO',  'categoria_revision_reconocimiento'  , 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('AUSCULTAR',  'categoria_revision_auscultar'  , 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CURAR',  'categoria_revision_curar'  , 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MIRAR GARGANTA',  'categoria_revision_mirargarganta'  , 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MIRAR OÍDOS',  'categoria_revision_miraroidos'  , 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PESAR',  'categoria_revision_pesar'  , 11)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MEDIR',  'categoria_revision_medir'  , 11)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MÉDICO',  'categoria_profesionales_medico'  , 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LOGOPEDA',  'categoria_profesionales_logopeda'  , 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ENFERMERA',  'categoria_profesionales_enfermera'  , 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('OFTALMÓLOGO',  'categoria_profesionales_oculista'  , 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('DENTISTA',  'categoria_profesionales_dentista'  , 12)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CELADOR',  'categoria_profesionales_celador'  , 12)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('DESINFECTAR',  'categoria_vacunacion_desinfectar'  , 13)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('VACUNAR',  'categoria_vacunacion_vacunar'  , 13)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PONER PARCHE',  'categoria_vacunacion_parche'  , 13)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PRUEBA ALERGIA',  'categoria_pruebas_alergia'  , 14)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PRUEBA DE SANGRE',  'categoria_pruebas_sangre'  , 14)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FROTIS GARGANTA',  'categoria_pruebas_frotisgarganta'  , 14)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FROTIS NASAL',  'categoria_pruebas_frotisnasal'  , 14)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('EXAMEN VISTA',  'categoria_oculista_examenvista'  , 15)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('GRADUAR VISTA',  'categoria_oculista_graduarvista'  , 15)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('OFTALMOSCOPIA',  'categoria_oculista_oftalmoscopia'  , 15)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('OPTOMETRÍA',  'categoria_oculista_optometria'  , 15)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PONER PARCHE',  'categoria_oculista_ponerparche'  , 15)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('REVISIÓN',  'categoria_dentista_revisiondentista'  , 16)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('DIENTES',  'categoria_dentista_dientes'  , 16)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CARIES',  'categoria_dentista_caries'  , 16)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('BRACKETS',  'categoria_dentista_brackets'  , 16)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ABRIR LA BOCA',  'categoria_dentista_boca'  , 16)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SILLÓN',  'categoria_dentista_sillon'  , 16)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ARTICULACIÓN',  'categoria_logopeda_articulacion'  , 17)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SOPLO',  'categoria_logopedia_soplo'  , 17)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PRAXIAS',  'categoria_logopeda_praxias'  , 17)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('RESPIRACIÓN',  'categoria_logopeda_respiracion'  , 17)")

        //CATEGORIAS NUEVAS: 18(COMPRA), 19(PELUQUERIA), 20(COLEGIO), 21(FAOVORITOS)
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CEPILLO',  'categoria_kit_cepillo_pelo'  , 18)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAQUINILLA DE CORTAR EL PELO',  'categoria_kit_maquinilla_cortar_pelo'  , 18)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PEINE Y TIJERAS',  'categoria_kit_peine_tijeras'  , 18)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PLANCHA',  'categoria_kit_plancha_pelo'  , 18)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('RIZADOR',  'categoria_kit_rizador'  , 18)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SECADOR',  'categoria_kit_secador'  , 18)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TIJERAS',  'categoria_kit_tijeras'  , 18)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FLEQUILLO',  'categoria_cortes_flequillo'  , 19)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PELO LARGO',  'categoria_cortes_pelo'  , 19)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PELO CORTO',  'categoria_cortes_pelo_corto'  , 19)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PELO RIZADO',  'categoria_cortes_pelo_rizado'  , 19)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PELO RIZADO',  'categoria_cortes_pelo_rizado2'  , 19)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PERILLA',  'categoria_cortes_perilla'  , 19)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TEÑIR',  'categoria_cortes_tenir'  , 19)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PELUQUERA',  'categoria_trabajadores_peluquera'  , 20)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PELUQUERO',  'categoria_trabajadores_peluquero'  , 20)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ACEITE',  'categoria_alimentos_aceite'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ADEREZO',  'categoria_alimentos_aderezo'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('BOLSA DE PATATAS FRITAS',  'categoria_alimentos_bolsa_fritos'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CARNE',  'categoria_alimentos_carne'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CHOCOLATE',  'categoria_alimentos_chocolate'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CHURROS',  'categoria_alimentos_churros'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('DONUT',  'categoria_alimentos_donut'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FLAN',  'categoria_alimentos_flan'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('HORTALIZAS',  'categoria_alimentos_hortalizas'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MERMELADA',  'categoria_alimentos_mermelada'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MIEL',  'categoria_alimentos_miel'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MOSTAZA',  'categoria_alimentos_mostaza'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('NATILLA',  'categoria_alimentos_natilla'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PAN',  'categoria_alimentos_pan'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PAN DE HAMBURGUESA',  'categoria_alimentos_pan_hamburguesa'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PAN DE MOLDE',  'categoria_alimentos_pan_molde'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PATATAS FRITAS',  'categoria_alimentos_patatas_fritas'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PESCADO',  'categoria_alimentos_pescado'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SALSA DE TOMATE',  'categoria_alimentos_salsa_ketchup'  , 21)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TOSTADA',  'categoria_alimentos_tostada'  , 21)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('BOLSA',  'categoria_objetos_bolsa'  , 22)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('BOLSA',  'categoria_objetos_bolsa1'  , 22)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CARRITO',  'categoria_objetos_carrito'  , 22)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('DINERO',  'categoria_objetos_dinero'  , 22)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TARJETA',  'categoria_objetos_tarjeta'  , 22)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TICKET',  'categoria_objetos_ticket'  , 22)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CAJERO',  'categoria_empleados_cajero'  , 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CAJERO',  'categoria_empleados_cajero1'  , 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CARNICERA',  'categoria_empleados_carnicera'  , 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CARNICERO',  'categoria_empleados_carnicero'  , 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FRUTERA',  'categoria_empleados_frutera'  , 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FRUTERO',  'categoria_empleados_frutero'  , 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MANIPULADOR',  'categoria_empleados_manipulador_alimentos'  , 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PANADERA',  'categoria_empleados_panadera'  , 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PANADERO',  'categoria_empleados_panadero'  , 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PESCADERA',  'categoria_empleados_pescadera'  , 23)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PESCADERO',  'categoria_empleados_pescadero'  , 23)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LIBRO',  'categoria_utiles_biblioteca'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('BOLIGRAFO',  'categoria_utiles_boligrafo'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CELO',  'categoria_utiles_celo'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CHINCHETAS',  'categoria_utiles_chinchetas'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('COMPAS',  'categoria_utiles_compas'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CORRECTOR',  'categoria_utiles_corrector_liquido'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CRAYONES',  'categoria_utiles_crayones'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('CUADERNO',  'categoria_utiles_cuaderno'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ESCUADRA',  'categoria_utiles_escuadra'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('FOLIOS',  'categoria_utiles_folios'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('GOMA',  'categoria_utiles_goma'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('GRAPADORA',  'categoria_utiles_grapadora'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LAPIZ Y PAPEL',  'categoria_utiles_lapiz_papel'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('LIBRETA',  'categoria_utiles_libreta'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MESA',  'categoria_utiles_mesa_colegio'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MESA',  'categoria_utiles_mesa_colegio1'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PAPELERA',  'categoria_utiles_papelera'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('PEGAMENTO',  'categoria_utiles_pegamento'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('REGLA',  'categoria_utiles_regla'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ROTULADOR',  'categoria_utiles_rotulador'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SACAPUNTAS',  'categoria_utiles_sacapuntas'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('SILLA',  'categoria_utiles_silla_colegio'  , 24)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('TIJERAS',  'categoria_utiles_tijeras'  , 24)")

        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAESTRA', 'categoria_profesores_maestra'  , 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAESTRO',  'categoria_profesores_maestro'  , 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAESTRA DE MATES',  'categoria_profesores_maestra_mates'  , 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAESTRO DE MATES',  'categoria_profesores_maestro_mates'  , 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAESTRO DE MUSICA',  'categoria_profesores_profesor_musica'  , 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAESTRA DE MUSICA',  'categoria_profesores_profesora_musica'  , 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAESTRO DE TALLER',  'categoria_profesores_profesor_taller'  , 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('MAESTRA DE TALLER',  'categoria_profesores_profesora_taller'  , 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('EDUCACION FISICA',  'categoria_profesores_profesora_educacion_fisica'  , 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('EDUCACION FISICA',  'categorias_profesores_profesor_educacion_fisica'  , 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ORIENTACION',  'categoria_profesores_servicio_orientacion'  , 25)")
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('ORIENTACION',  'categoria_profesores_servicio_orientacion1'  , 25)")

        insertarPictoCuaderno(db, "IZQUIERDA", "cuaderno_escala_izquierda", 1)
        insertarPictoCuaderno(db, "DERECHA", "cuaderno_escala_derecha", 1)
        insertarPictoCuaderno(db, "SI", "cuaderno_escala_bien", 1)
        insertarPictoCuaderno(db, "NO", "cuaderno_escala_mal", 1)
        insertarPictoCuaderno(db, "HORAS", "cuaderno_escala_hora", 1)
        insertarPictoCuaderno(db, "DÍAS", "cuaderno_escala_dia", 1)
        insertarPictoCuaderno(db, "SEMANAS", "cuaderno_escala_semana", 1)
        insertarPictoCuaderno(db, "MESES", "cuaderno_escala_mes", 1)
        insertarPictoCuaderno(db, "AÑO", "cuaderno_escala_anio", 1)

        insertarPictoCuaderno(db, "MAREO", "cuaderno_sintomas_mareo", 2)
        insertarPictoCuaderno(db, "CANSANCIO", "cuaderno_sintomas_cansancio", 2)
        insertarPictoCuaderno(db, "ESCALOFRIOS", "cuaderno_sintomas_escalofrios", 2)
        insertarPictoCuaderno(db, "TOS", "cuaderno_sintomas_tos", 2)
        insertarPictoCuaderno(db, "FIEBRE", "cuaderno_sintomas_fiebre", 2)
        insertarPictoCuaderno(db, "NO VEO BIEN", "cuaderno_sintomas_noveobien", 2)
        insertarPictoCuaderno(db, "VOMITOS", "cuaderno_sintomas_vomitar", 2)
        insertarPictoCuaderno(db, "ALERGIA", "cuaderno_sintomas_alergia", 2)
        insertarPictoCuaderno(db, "RESFRIADO", "cuaderno_sintomas_resfriado", 2)
        insertarPictoCuaderno(db, "SARPULLIDO", "cuaderno_sintomas_sarpullido", 2)
        insertarPictoCuaderno(db, "HERIDA", "cuaderno_sintomas_herida", 2)
        insertarPictoCuaderno(db, "QUEMADURA", "cuaderno_sintomas_quemadura", 2)
        insertarPictoCuaderno(db, "TAQUICARDIA", "cuaderno_sintomas_taquicardia", 2)
        insertarPictoCuaderno(db, "DIARREA", "cuaderno_sintomas_diarrea", 2)
        insertarPictoCuaderno(db, "ESTREÑIMIENTO", "cuaderno_sintomas_estrenimiento", 2)

        insertarPictoCuaderno(db, "CUELLO", "cuaderno_dolor_cuello", 3)
        insertarPictoCuaderno(db, "ESPALDA", "cuaderno_dolor_espalda", 3)
        insertarPictoCuaderno(db, "PECHO", "cuaderno_dolor_pecho", 3)
        insertarPictoCuaderno(db, "CULO", "cuaderno_dolor_culo", 3)
        insertarPictoCuaderno(db, "MUÑECA", "cuaderno_dolor_muneca", 3)
        insertarPictoCuaderno(db, "RODILLA", "cuaderno_dolor_rodilla", 3)
        insertarPictoCuaderno(db, "TOBILLO", "cuaderno_dolor_tobillo", 3)
        insertarPictoCuaderno(db, "PIE", "cuaderno_dolor_pie", 3)
        insertarPictoCuaderno(db, "MUELA", "cuaderno_dolor_muela", 3)
        insertarPictoCuaderno(db, "OÍDO", "cuaderno_dolor_oido", 3)
        insertarPictoCuaderno(db, "CABEZA", "cuaderno_dolor_cabeza", 3)
        insertarPictoCuaderno(db, "BRAZO", "cuaderno_dolor_brazo", 3)
        insertarPictoCuaderno(db, "GARGANTA", "cuaderno_dolor_garganta", 3)
        insertarPictoCuaderno(db, "TRIPA", "cuaderno_dolor_estomago", 3)
        insertarPictoCuaderno(db, "PIERNA", "cuaderno_dolor_pierna", 3)

        insertarPictoCuaderno(db, "ABURRIDO", "cuaderno_sentimientos_aburrir", 4)
        insertarPictoCuaderno(db, "ALEGRE", "cuaderno_sentimientos_alegrar", 4)
        insertarPictoCuaderno(db, "AMADA", "cuaderno_sentimientos_amada", 4)
        insertarPictoCuaderno(db, "AMADO", "cuaderno_sentimientos_amado", 4)
        insertarPictoCuaderno(db, "ANSIOSO", "cuaderno_sentimientos_ansioso", 4)
        insertarPictoCuaderno(db, "ASQUEADO", "cuaderno_sentimientos_asco", 4)
        insertarPictoCuaderno(db, "ASUSTADO", "cuaderno_sentimientos_asustar", 4)
        insertarPictoCuaderno(db, "AUTOESTIMA", "cuaderno_sentimientos_autoestima", 4)
        insertarPictoCuaderno(db, "AVERGONZADO", "cuaderno_sentimientos_avergonzar", 4)
        insertarPictoCuaderno(db, "CANSADO", "cuaderno_sentimientos_cansar", 4)
        insertarPictoCuaderno(db, "CONDICION", "cuaderno_sentimientos_condicion", 4)
        insertarPictoCuaderno(db, "CONFUNDIDO", "cuaderno_sentimientos_confundir", 4)
        insertarPictoCuaderno(db, "DISTRAIDO", "cuaderno_sentimientos_distraer", 4)
        insertarPictoCuaderno(db, "ENAMORADO", "cuaderno_sentimientos_enamorado", 4)
        insertarPictoCuaderno(db, "ENFADADO", "cuaderno_sentimientos_enfadar", 4)
        insertarPictoCuaderno(db, "ENTRISTECIDO", "cuaderno_sentimientos_entristecer", 4)
        insertarPictoCuaderno(db, "ENVIDIOSO", "cuaderno_sentimientos_envidia", 4)
        insertarPictoCuaderno(db, "NOSTALGICO", "cuaderno_sentimientos_nostalgico", 4)
        insertarPictoCuaderno(db, "REGOCIJAR", "cuaderno_sentimientos_regocijar", 4)
        insertarPictoCuaderno(db, "SERIO", "cuaderno_sentimientos_serio", 4)
        insertarPictoCuaderno(db, "SORPRENDIDO", "cuaderno_sentimientos_sorprender", 4)
        insertarPictoCuaderno(db, "TENER MIEDO", "cuaderno_sentimientos_tener_miedo", 4)
        insertarPictoCuaderno(db, "TRANQUILO", "cuaderno_sentimientos_tranquilo", 4)
        insertarPictoCuaderno(db, "TRANQUILO", "cuaderno_sentimientos_tranquilo1", 4)
        insertarPictoCuaderno(db, "VERGUENZA", "cuaderno_sentimientos_verguenza", 4)
     }


    @SuppressLint("Range")
    fun insertarPictoCuaderno(db: SQLiteDatabase, nombre: String, image: String, idCuaderno: Int){
        db.execSQL("INSERT INTO Pictograma (nombre, imagen) VALUES('$nombre', '$image')")

        var id = "0"
        val cursor = db.rawQuery("SELECT id FROM Pictograma WHERE nombre = '$nombre'", null)
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex("id"))
            cursor.close()
        }

        db.execSQL("INSERT INTO RelacionPictogramaCuaderno (id_pictograma, id_cuaderno) VALUES('$id', '$idCuaderno')")
    }

}