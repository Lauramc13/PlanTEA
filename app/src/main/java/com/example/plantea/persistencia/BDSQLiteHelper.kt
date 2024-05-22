package com.example.plantea.persistencia

import android.annotation.SuppressLint
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

class BDSQLiteHelper(contexto: Context?, nombreBD: String?, factory: CursorFactory?, versionBD: Int) : SQLiteOpenHelper(contexto, nombreBD, factory, versionBD) {
    private var sqlUsuario = "CREATE TABLE Usuario(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, password TEXT, username TEXT, name TEXT, imagen TEXT, objeto TEXT, imagenObjeto TEXT, nameTEA TEXT, imagenTEA TEXT)"
    private var sqlCategorias = "CREATE TABLE Categoria(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, imagen TEXT, principal BOOLEAN, color TEXT, id_usuario INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlPictograma = "CREATE TABLE Pictograma(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, imagen TEXT, id_categoria INTEGER, id_usuario INTEGER, FOREIGN KEY (id_categoria) REFERENCES Categoria(id), FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlPictogramaAPI = "CREATE TABLE PictogramaAPI(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, imagen TEXT)"
    private var sqlFavorito = "CREATE TABLE Favorito(id INTEGER PRIMARY KEY AUTOINCREMENT, id_pictograma INTEGER, id_usuario INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario(id), FOREIGN KEY (id_pictograma) REFERENCES PictogramaAPI(id))"
    private var sqlCuaderno = "CREATE TABLE Cuaderno(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, imagen TEXT, termometro BOOLEAN, id_usuario INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlRelacionPictogramaCuaderno = "CREATE TABLE RelacionPictogramaCuaderno(id INTEGER PRIMARY KEY AUTOINCREMENT, id_pictograma INTEGER, id_pictogramaAPI INTEGER, id_cuaderno INTEGER, FOREIGN KEY (id_pictograma) REFERENCES Pictograma(id), FOREIGN KEY (id_cuaderno) REFERENCES Cuaderno(id), FOREIGN KEY (id_pictogramaAPI) REFERENCES PictogramaAPI(id))"
    private var sqpPlanificacion = "CREATE TABLE Planificacion(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, es_actual INTEGER, id_usuario INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlEvento = "CREATE TABLE Evento(id INTEGER PRIMARY KEY AUTOINCREMENT, id_usuario INTEGER, nombre TEXT, fecha TEXT, hora TEXT, visible INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario(id))"
    private var sqlRelacionPictogramaPlan = "CREATE TABLE RelacionPictogramaPlan (id INTEGER PRIMARY KEY AUTOINCREMENT, historia TEXT, duracion TEXT, id_picto_entre, id_plan INTEGER, id_pictograma INTEGER, id_pictogramaAPI INTEGER, FOREIGN KEY (id_plan) REFERENCES Planificacion(id), FOREIGN KEY (id_pictograma) REFERENCES Pictograma(id), FOREIGN KEY (id_pictogramaAPI) REFERENCES PictogramaAPI(id))"
    private var sqlRelacionEventoPlan = "CREATE TABLE RelacionEventoPlan (id INTEGER PRIMARY KEY AUTOINCREMENT, id_evento INTEGER, id_plan INTEGER, FOREIGN KEY (id_evento) REFERENCES Evento(id), FOREIGN KEY (id_plan) REFERENCES Planificacion(id))"
    private var sqlTraduccion = "CREATE TABLE Traduccion (id INTEGER PRIMARY KEY AUTOINCREMENT, language TEXT, translation TEXT)"
    private var sqlRelacionPictoTraduccion  = "CREATE TABLE RelacionPictoTraduccion (id INTEGER PRIMARY KEY AUTOINCREMENT, id_pictograma INTEGER, id_cuaderno INTEGER, id_categoria INTEGER, id_traduccion INTEGER, FOREIGN KEY (id_pictograma) REFERENCES Pictograma(id), FOREIGN KEY (id_cuaderno) REFERENCES Cuaderno(id), FOREIGN KEY (id_categoria) REFERENCES Categoria(id), FOREIGN KEY (id_traduccion) REFERENCES Traduccion(id))"
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
            db.execSQL(sqlRelacionEventoPlan)
            db.execSQL(sqlTraduccion)
            db.execSQL(sqlRelacionPictoTraduccion)
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
            db.execSQL("DROP TABLE IF EXISTS RelacionEventoPlan")
            db.execSQL("DROP TABLE IF EXISTS Traduccion")
            db.execSQL("DROP TABLE IF EXISTS RelacionPictoTraduccion")

            /*Se crea la nueva versión de la table*/
            onCreate(db)

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun meterDatos(db: SQLiteDatabase) {

        //Categoria
        insertCategoria(db, "IR AL MEDICO", "categoria_consultas", true, "default", "en", "GO TO THE DOCTOR") // 1
        insertCategoria(db, "CORTARSE EL PELO", "categoria_peluqueria", true, "default","en", "CUT YOUR HAIR") // 2
        insertCategoria(db, "HACER LA COMPRA", "categoria_hacer_la_compra", true, "default", "en", "GO SHOPPING") // 3
        insertCategoria(db, "IR AL COLEGIO", "categoria_colegio", true, "default","en",  "GO TO SCHOOL") // 4
        insertCategoria(db, "LUGARES", "categoria_lugares", true, "default", "en", "LOCATIONS") // 5
        insertCategoria(db, "DESPLAZAMIENTO", "categoria_desplazamiento", true, "default", "en", "DISPLACEMENT") // 6
        insertCategoria(db, "ACCION", "categoria_accion", true, "default","en",  "ACTION") // 7
        insertCategoria(db, "ENTRETENIMIENTO", "categoria_entretenimiento", true, "blue", "en", "ENTERTAINMENT") // 8
        insertCategoria(db, "RECOMPENSA", "categoria_recompensa", true, "blue", "en", "REWARDS") // 9
        insertCategoria(db, "FAVORITOS", "categoria_favorito", true, "yellow", "en", "FAVORITES") // 10

        insertCategoria(db, "REVISIÓN", null, false, null, "en",  "REVISION") // 11
        insertCategoria(db, "PROFESIONALES", null, false, null, "en", "PROFESSIONALS") // 12
        insertCategoria(db, "VACUNACIÓN", null, false, null, "en", "VACCINATION") // 13
        insertCategoria(db, "PRUEBAS", null, false, null, "en", "TESTS") // 14
        insertCategoria(db, "OFTALMOLOGÍA", null, false, null, "en", "OPHTHALMOLOGY") // 15
        insertCategoria(db, "ODONTOLOGÍA", null, false, null, "en", "DENTISTRY") // 16
        insertCategoria(db, "LOGOPEDIA", null, false, null, "en", "SPEECH THERAPY") // 17
        insertCategoria(db, "KIT DE PELUQUERIA", null, false, null,"en",  "HAIRDRESSING KIT") // 18
        insertCategoria(db, "CORTES DE PELO", null, false, null,"en",  "HAIRCUTS") // 19
        insertCategoria(db, "TRABAJADORES", null, false, null,"en",  "WORKERS") // 20
        insertCategoria(db, "ALIMENTOS", null, false, null, "en", "FOOD") // 21
        insertCategoria(db, "OBJETOS", null, false, null, "en", "OBJECTS") // 22
        insertCategoria(db, "EMPLEADOS", null, false, null,"en", "EMPLOYEES") // 23
        insertCategoria(db, "UTILES", null, false, null, "en", "SUPPLIES") // 24
        insertCategoria(db, "PROFESORES", null, false, null, "en", "TEACHERS") // 25

        //Cuaderno
        //var sqlCuaderno = "CREATE TABLE Cuaderno(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, imagen TEXT, termometro BOOLEAN, id_usuario INTEGER, FOREIGN KEY (id_usuario) REFERENCES Usuario_Planificador(id))"

        insertCuaderno(db, "ESCALA", "cuaderno_sintomas", 0, "en", "SCALE")
        insertCuaderno(db, "¿QUÉ TE PASA?", "cuaderno_sintomas", 1, "en", "WHAT''S WRONG?")
        insertCuaderno(db, "¿DÓNDE TE DUELE?", "cuaderno_dolores", 1, "en", "WHERE DOES IT HURT?")
        insertCuaderno(db, "¿COMO TE SIENTES?", "card_sentimientos", 0,"en",  "HOW DO YOU FEEL?")

        //Pictograma
        insertPictograma(db, "REVISIÓN", "categoria_consultas", 1, "en", "REVISION")
        insertPictograma(db, "PROFESIONALES", "categoria_profesionales", 1, "en", "PROFESSIONALS")
        insertPictograma(db, "VACUNACIÓN", "categorias_consultas_vacunacion", 1,"en",  "VACCINATION")
        insertPictograma(db, "PRUEBAS", "categorias_consultas_pruebas", 1,"en",  "TESTS")
        insertPictograma(db, "OFTALMOLOGÍA", "categoria_consultas_oftalmologa", 1, "en", "OPHTHALMOLOGY")
        insertPictograma(db, "ODONTOLOGÍA", "categoria_consultas_dentista", 1, "en", "DENTISTRY")
        insertPictograma(db, "LOGOPEDIA", "categoria_consultas_logopedia", 1,"en",  "SPEECH THERAPY")

        insertPictograma(db, "KIT DE PELUQUERIA", "categoria_kit_peine_tijeras", 2, "en", "HAIRDRESSING KIT")
        insertPictograma(db, "CORTES DE PELO", "categoria_cortes_pelo", 2,"en",  "HAIRCUTS")
        insertPictograma(db, "TRABAJADORES", "categoria_trabajadores", 2, "en", "WORKERS")
        insertPictograma(db, "ALIMENTOS", "categoria_alimentos_alimentos", 3, "en", "FOOD")
        insertPictograma(db, "OBJETOS", "categoria_objetos_dinero", 3,"en",  "OBJECTS")
        insertPictograma(db, "EMPLEADOS", "categoria_empleados_cajero", 3, "en", "EMPLOYEES")
        insertPictograma(db, "UTILES", "categoria_utiles_crayones", 4, "en", "SUPPLIES")
        insertPictograma(db, "PROFESORES", "categoria_profesores_maestra", 4,"en",  "TEACHERS")
        insertPictograma(db, "CASA", "categoria_lugares_casa", 5, "en", "HOME")
        insertPictograma(db, "CENTRO DE SALUD", "categoria_lugares_centrosalud", 5,"en",  "HEALTH CENTER")
        insertPictograma(db, "HOSPITAL", "categoria_lugares_hospital", 5, "en", "HOSPITAL")
        insertPictograma(db, "SALA DE ESPERA", "categoria_lugares_salaespera", 5, "en", "WAITING ROOM")
        insertPictograma(db, "RECEPCIÓN", "categoria_lugares_recepcion", 5, "en", "RECEPTION")
        insertPictograma(db, "CONSULTA", "categoria_lugares_consulta", 5, "en", "CONSULTATION")
        insertPictograma(db, "AULA", "categoria_lugares_aula", 5, "en", "CLASSROOM")
        insertPictograma(db, "COLEGIO BILINGÜE", "categoria_lugares_colegio_bilingue", 5, "en", "BILINGUAL SCHOOL")
        insertPictograma(db, "COLEGIO", "categoria_lugares_colegio_bilingue", 5, "en", "SCHOOL")
        insertPictograma(db, "CARNICERÍA", "categoria_lugares_carniceria", 5, "en", "BUTCHER SHOP")
        insertPictograma(db, "FRUTERÍA", "categoria_lugares_fruteria", 5, "en", "FRUIT SHOP")
        insertPictograma(db, "PELUQUERÍA", "categoria_peluqueria", 5, "en", "HAIRDRESSER")
        insertPictograma(db, "PESCADERÍA", "categoria_lugares_pescaderia", 5, "en", "FISH SHOP")
        insertPictograma(db, "SUPERMERCADO", "categoria_lugares_supermercado", 5, "en", "SUPERMARKET")
        insertPictograma(db, "COCHE", "categoria_desplazamiento_coche", 6, "en", "CAR")
        insertPictograma(db, "AUTOBÚS", "categoria_desplazamiento_autobus", 6, "en", "BUS")
        insertPictograma(db, "AMBULANCIA", "categoria_desplazamiento_ambulancia", 6, "en", "AMBULANCE")
        insertPictograma(db, "TREN", "categoria_desplazamiento_tren", 6, "en", "TRAIN")
        insertPictograma(db, "METRO", "categoria_desplazamiento_metro", 6, "en", "METRO")
        insertPictograma(db, "CAMINANDO", "categoria_desplazamiento_caminando", 6, "en", "WALKING")
        insertPictograma(db, "IR", "categoria_accion_ir", 7,"en",  "GO")
        insertPictograma(db, "VOLVER", "categoria_accion_volver", 7, "en", "RETURN")
        insertPictograma(db, "ESPERAR", "categoria_accion_esperar", 7, "en", "WAIT")
        insertPictograma(db, "ENTRAR", "categoria_accion_entrar", 7,"en",  "ENTER")
        insertPictograma(db, "SALUDAR", "categoria_accion_saludar", 7, "en", "GREET")
        insertPictograma(db, "DESPEDIRSE", "categoria_accion_despedirse", 7,"en",  "GOODBYE")
        insertPictograma(db, "HABLAR", "categoria_accion_hablar", 7, "en", "TALK")
        insertPictograma(db, "ESCUCHAR", "categoria_accion_escuchar", 7, "en", "LISTEN")
        insertPictograma(db, "MIRAR", "categoria_accion_mirar", 7, "en", "LOOK")
        insertPictograma(db, "SENTARSE", "categoria_accion_sentarse", 7, "en", "SIT DOWN")
        insertPictograma(db, "BAJAR ESCALERA MECÁNICA", "categoria_accion_bajar_escalera_mecanica", 7, "en", "GO DOWN ESCALATOR")
        insertPictograma(db, "SUBIR ESCALERA MECÁNICA", "categoria_accion_subir_escalera_mecanica", 7, "en", "GO UP ESCALATOR")
        insertPictograma(db, "BAJAR RAMPA", "categoria_accion_bajar_rampa", 7,"en",  "GO DOWN RAMP")
        insertPictograma(db, "SUBIR RAMPA", "categoria_accion_subir_rampa", 7, "en", "GO UP RAMP")
        insertPictograma(db, "CORTAR PELO", "categoria_accion_cortar_pelo", 7, "en", "CUT HAIR")
        insertPictograma(db, "CORTAR PELO", "categoria_accion_cortar_pelo1", 7,"en",  "CUT HAIR")
        insertPictograma(db, "LAVAR PELO", "categoria_accion_lavar_pelo", 7, "en", "WASH HAIR")
        insertPictograma(db, "SECAR EL PELO", "categoria_accion_secar_pelo", 7, "en", "DRY HAIR")
        insertPictograma(db, "PAGAR", "categoria_accion_pagar", 7, "en", "PAY")
        insertPictograma(db, "COGER TICKET", "categoria_accion_coger_ticket", 7, "en", "TAKE TICKET")
        insertPictograma(db, "COGER TURNO", "categoria_accion_coger_turno", 7, "en", "TAKE TURN")
        insertPictograma(db, "ESPERAR", "categoria_accion_esperar1", 7, "en", "WAIT")
        insertPictograma(db, "PREGUNTAR", "categoria_accion_preguntar", 7, "en", "ASK")
        insertPictograma(db, "PREGUNTAR", "categoria_accion_preguntar1", 7, "en", "ASK")
        insertPictograma(db, "PREGUNTAR", "categoria_accion_preguntar2", 7, "en", "ASK")
        insertPictograma(db, "CERRAR MOCHILA", "categoria_accion_cerrar_mochila", 7, "en", "CLOSE BACKPACK")
        insertPictograma(db, "ABRIR MOCHILA", "categoria_accion_abrir_mochila", 7, "en", "OPEN BACKPACK")
        insertPictograma(db, "APROBAR", "categoria_accion_aprobar", 7, "en", "APPROVE")
        insertPictograma(db, "SUSPENDER", "categoria_accion_suspender", 7, "en", "SUSPEND")
        insertPictograma(db, "ENTRADA", "categoria_accion_entrada", 7, "en", "ENTRANCE")
        insertPictograma(db, "ENTRADA", "categoria_accion_entrada1", 7, "en", "ENTRANCE")
        insertPictograma(db, "SALIDA", "categoria_accion_salida", 7, "en", "EXIT")
        insertPictograma(db, "IR AL COLEGIO", "categoria_accion_ir_colegio", 7, "en", "GO TO SCHOOL")
        insertPictograma(db, "LECTURA", "categoria_accion_lectura", 7, "en", "READING")
        insertPictograma(db, "LEER", "categoria_accion_leer", 7, "en", "READ")
        insertPictograma(db, "LEER", "categoria_accion_leer1", 7, "en", "READ")
        insertPictograma(db, "LLEVAR AL COLEGIO", "categoria_accion_llevar_colegio", 7, "en", "TAKE TO SCHOOL")
        insertPictograma(db, "METER EL ALMUERZO", "categoria_accion_meter_almuerzo", 7, "en", "PUT LUNCH")
        insertPictograma(db, "PONERSE LA MOCHILA", "categoria_accion_poner_mochila", 7, "en", "PUT ON BACKPACK")
        insertPictograma(db, "SACAR EL ALMUERZO", "categoria_accion_sacar_almuerzo", 7, "en", "TAKE OUT LUNCH")
        insertPictograma(db, "SALIDA", "categoria_accion_salida", 7, "en", "EXIT")
        insertPictograma(db, "SALIDA", "categoria_accion_salida1", 7, "en", "EXIT")

        insertPictograma(db, "JUGAR", "categoria_entretenimiento_jugar", 8, "en", "PLAY")
        insertPictograma(db, "LEER", "categoria_entretenimiento_leer", 8, "en", "READ")
        insertPictograma(db, "JUGAR", "categoria_recompensa_jugar", 9, "en", "PLAY")
        insertPictograma(db, "IR AL PARQUE", "categoria_recompensa_parque", 9, "en", "GO TO THE PARK")
        insertPictograma(db, "VER LA TELEVISIÓN", "categoria_recompensa_tele", 9, "en", "WATCH TV")
        insertPictograma(db, "RECONOCIMIENTO", "categoria_revision_reconocimiento", 11, "en", "MEDICAL EXAMINATION")
        insertPictograma(db, "AUSCULTAR", "categoria_revision_auscultar", 11, "en", "AUSCULTATE")
        insertPictograma(db, "CURAR", "categoria_revision_curar", 11, "en", "HEAL")
        insertPictograma(db, "MIRAR GARGANTA", "categoria_revision_mirargarganta", 11, "en", "LOOK THROAT")
        insertPictograma(db, "MIRAR OÍDOS", "categoria_revision_miraroidos", 11, "en", "LOOK EARS")
        insertPictograma(db, "PESAR", "categoria_revision_pesar", 11, "en", "WEIGH")
        insertPictograma(db, "MEDIR", "categoria_revision_medir", 11, "en","MEASURE")
        insertPictograma(db, "MÉDICO", "categoria_profesionales_medico", 12, "en", "DOCTOR")
        insertPictograma(db, "LOGOPEDA", "categoria_profesionales_logopeda", 12, "en","SPEECH THERAPIST")
        insertPictograma(db, "ENFERMERA", "categoria_profesionales_enfermera", 12, "en","NURSE")
        insertPictograma(db, "OFTALMÓLOGO", "categoria_profesionales_oculista", 12, "en","OPHTHALMOLOGIST")
        insertPictograma(db, "DENTISTA", "categoria_profesionales_dentista", 12, "en","DENTIST")
        insertPictograma(db, "CELADOR", "categoria_profesionales_celador", 12, "en","ORDERLY")
        insertPictograma(db, "DESINFECTAR", "categoria_vacunacion_desinfectar", 13, "en","DISINFECT")
        insertPictograma(db, "VACUNAR", "categoria_vacunacion_vacunar", 13, "en","VACCINATE")
        insertPictograma(db, "PONER PARCHE", "categoria_vacunacion_parche", 13, "en","PUT PATCH")
        insertPictograma(db, "PRUEBA ALERGIA", "categoria_pruebas_alergia", 14, "en","ALLERGY TEST")
        insertPictograma(db, "PRUEBA DE SANGRE", "categoria_pruebas_sangre", 14, "en","BLOOD TEST")
        insertPictograma(db, "FROTIS GARGANTA", "categoria_pruebas_frotisgarganta", 14, "en","THROAT SWAB")
        insertPictograma(db, "ANÁLISIS ORINA", "categoria_pruebas_frotisnasal", 14, "en","NASAL SPRAY")

        insertPictograma(db, "EXAMEN VISTA", "categoria_oculista_examenvista", 15, "en","EYE EXAM")
        insertPictograma(db, "GRADUAR VISTA", "categoria_oculista_graduarvista", 15, "en","GRADUATE VIEW")
        insertPictograma(db, "OFTALMOSCOPIA", "categoria_oculista_oftalmoscopia", 15, "en","OPHTHALMOSCOPY")
        insertPictograma(db, "OPTOMETRÍA", "categoria_oculista_optometria", 15, "en","OPTOMETRY")
        insertPictograma(db, "PONER PARCHE", "categoria_oculista_ponerparche", 15, "en","PUT PATCH")

        insertPictograma(db, "REVISIÓN", "categoria_dentista_revisiondentista", 16, "en","REVISION")
        insertPictograma(db, "DIENTES", "categoria_dentista_dientes", 16, "en","TEETH")
        insertPictograma(db, "CARIES", "categoria_dentista_caries", 16, "en","DECAY")
        insertPictograma(db, "BRACKETS", "categoria_dentista_brackets", 16, "en","BRACKETS")
        insertPictograma(db, "ABRIR LA BOCA", "categoria_dentista_boca", 16, "en","OPEN MOUTH")
        insertPictograma(db, "SILLÓN", "categoria_dentista_sillon", 16, "en","CHAIR")

        insertPictograma(db, "ARTICULACIÓN", "categoria_logopeda_articulacion", 17, "en","JOINT")
        insertPictograma(db, "SOPLO", "categoria_logopedia_soplo", 17, "en","BLOW")
        insertPictograma(db, "PRAXIAS", "categoria_logopeda_praxias", 17, "en","PRAXIS")
        insertPictograma(db, "RESPIRACIÓN", "categoria_logopeda_respiracion", 17, "en","BREATHING")

        insertPictograma(db, "CEPILLO", "categoria_kit_cepillo_pelo", 18, "en","BRUSH")
        insertPictograma(db, "MAQUINILLA DE CORTAR EL PELO", "categoria_kit_maquinilla_cortar_pelo", 18, "en","HAIR CLIPPER")
        insertPictograma(db, "PEINE Y TIJERAS", "categoria_kit_peine_tijeras", 18, "en","COMB AND SCISSORS")
        insertPictograma(db, "PLANCHA", "categoria_kit_plancha_pelo", 18, "en","IRON")
        insertPictograma(db, "RIZADOR", "categoria_kit_rizador", 18, "en","CURLING IRON")
        insertPictograma(db, "SECADOR", "categoria_kit_secador", 18, "en","HAIR DRYER")
        insertPictograma(db, "TIJERAS", "categoria_kit_tijeras", 18, "en","SCISSORS")

        insertPictograma(db, "FLEQUILLO", "categoria_cortes_flequillo", 19, "en","BANGS")
        insertPictograma(db, "PELO LARGO", "categoria_cortes_pelo", 19, "en","LONG HAIR")
        insertPictograma(db, "PELO CORTO", "categoria_cortes_pelo_corto", 19, "en","SHORT HAIR")
        insertPictograma(db, "PELO RIZADO", "categoria_cortes_pelo_rizado", 19, "en","CURLY HAIR")
        insertPictograma(db, "PELO RIZADO", "categoria_cortes_pelo_rizado2", 19, "en","CURLY HAIR")
        insertPictograma(db, "PERILLA", "categoria_cortes_perilla", 19, "en","GOATEE")
        insertPictograma(db, "TEÑIR", "categoria_cortes_tenir", 19, "en","DYE")

        insertPictograma(db, "PELUQUERA", "categoria_trabajadores_peluquera", 20, "en","HAIRDRESSER")
        insertPictograma(db, "PELUQUERO", "categoria_trabajadores_peluquero", 20, "en","HAIRDRESSER")

        insertPictograma(db, "ACEITE", "categoria_alimentos_aceite", 21, "en","OIL")
        insertPictograma(db, "ADEREZO", "categoria_alimentos_aderezo", 21, "en","DRESSING")
        insertPictograma(db, "BOLSA DE PATATAS FRITAS", "categoria_alimentos_bolsa_fritos", 21, "en","BAG OF CHIPS")
        insertPictograma(db, "CARNE", "categoria_alimentos_carne", 21, "en","MEAT")
        insertPictograma(db, "CHOCOLATE", "categoria_alimentos_chocolate", 21, "en","CHOCOLATE")
        insertPictograma(db, "CHURROS", "categoria_alimentos_churros", 21, "en","CHURROS")
        insertPictograma(db, "DONUT", "categoria_alimentos_donut", 21, "en","DONUT")
        insertPictograma(db, "FLAN", "categoria_alimentos_flan", 21, "en","FLAN")
        insertPictograma(db, "HORTALIZAS", "categoria_alimentos_hortalizas", 21, "en","VEGETABLES")
        insertPictograma(db, "MERMELADA", "categoria_alimentos_mermelada", 21, "en","JAM")
        insertPictograma(db, "MIEL", "categoria_alimentos_miel", 21, "en","HONEY")
        insertPictograma(db, "MOSTAZA", "categoria_alimentos_mostaza", 21, "en","MUSTARD")
        insertPictograma(db, "NATILLA", "categoria_alimentos_natilla", 21, "en","CUSTARD")
        insertPictograma(db, "PAN", "categoria_alimentos_pan", 21, "en","BREAD")
        insertPictograma(db, "PAN DE HAMBURGUESA", "categoria_alimentos_pan_hamburguesa", 21, "en","HAMBURGER BREAD")
        insertPictograma(db, "PAN DE MOLDE", "categoria_alimentos_pan_molde", 21, "en","SANDWICH BREAD")
        insertPictograma(db, "PATATAS FRITAS", "categoria_alimentos_patatas_fritas", 21, "en","FRENCH FRIES")
        insertPictograma(db, "PESCADO", "categoria_alimentos_pescado", 21, "en","FISH")
        insertPictograma(db, "SALSA DE TOMATE", "categoria_alimentos_salsa_ketchup", 21, "en","TOMATO SAUCE")
        insertPictograma(db, "TOSTADA", "categoria_alimentos_tostada", 21, "en","TOAST")

        insertPictograma(db, "BOLSA", "categoria_objetos_bolsa", 22, "en","BAG")
        insertPictograma(db, "BOLSA", "categoria_objetos_bolsa1", 22, "en","BAG")
        insertPictograma(db, "CARRITO", "categoria_objetos_carrito", 22, "en","CART")
        insertPictograma(db, "DINERO", "categoria_objetos_dinero", 22, "en","MONEY")
        insertPictograma(db, "TARJETA", "categoria_objetos_tarjeta", 22, "en","CARD")
        insertPictograma(db, "TICKET", "categoria_objetos_ticket", 22, "en","TICKET")

        insertPictograma(db, "CAJERO", "categoria_empleados_cajero", 23, "en","CASHIER")
        insertPictograma(db, "CAJERO", "categoria_empleados_cajero1", 23, "en","CASHIER")
        insertPictograma(db, "CARNICERA", "categoria_empleados_carnicera", 23, "en","BUTCHER")
        insertPictograma(db, "CARNICERO", "categoria_empleados_carnicero", 23, "en","BUTCHER")
        insertPictograma(db, "FRUTERA", "categoria_empleados_frutera", 23, "en","FRUIT SELLER")
        insertPictograma(db, "FRUTERO", "categoria_empleados_frutero", 23, "en","FRUIT SELLER")
        insertPictograma(db, "MANIPULADOR", "categoria_empleados_manipulador_alimentos", 23, "en","FOOD HANDLER")
        insertPictograma(db, "PANADERA", "categoria_empleados_panadera", 23, "en","BAKER")
        insertPictograma(db, "PANADERO", "categoria_empleados_panadero", 23, "en","BAKER")
        insertPictograma(db, "PESCADERA", "categoria_empleados_pescadera", 23, "en","FISH SELLER")
        insertPictograma(db, "PESCADERO", "categoria_empleados_pescadero", 23, "en","FISH SELLER")

        insertPictograma(db, "LIBRO", "categoria_utiles_biblioteca", 24, "en","BOOK")
        insertPictograma(db, "BOLIGRAFO", "categoria_utiles_boligrafo", 24, "en","PEN")
        insertPictograma(db, "CELO", "categoria_utiles_celo", 24, "en","TAPE")
        insertPictograma(db, "CHINCHETAS", "categoria_utiles_chinchetas", 24, "en","PINS")
        insertPictograma(db, "COMPAS", "categoria_utiles_compas", 24, "en","COMPASS")
        insertPictograma(db, "CORRECTOR", "categoria_utiles_corrector_liquido", 24, "en","CORRECTOR")
        insertPictograma(db, "CRAYONES", "categoria_utiles_crayones", 24, "en","CRAYONS")
        insertPictograma(db, "CUADERNO", "categoria_utiles_cuaderno", 24, "en","NOTEBOOK")
        insertPictograma(db, "ESCUADRA", "categoria_utiles_escuadra", 24, "en","SQUARE")
        insertPictograma(db, "FOLIOS", "categoria_utiles_folios", 24, "en","SHEETS")
        insertPictograma(db, "GOMA", "categoria_utiles_goma", 24, "en","RUBBER")
        insertPictograma(db, "GRAPADORA", "categoria_utiles_grapadora", 24, "en","STAPLER")
        insertPictograma(db, "LAPIZ Y PAPEL", "categoria_utiles_lapiz_papel", 24, "en","PENCIL AND PAPER")
        insertPictograma(db, "LIBRETA", "categoria_utiles_libreta", 24, "en","NOTEBOOK")
        insertPictograma(db, "MESA", "categoria_utiles_mesa_colegio", 24, "en","TABLE")
        insertPictograma(db, "MESA", "categoria_utiles_mesa_colegio1", 24, "en","TABLE")
        insertPictograma(db, "PAPELERA", "categoria_utiles_papelera", 24, "en","BIN")
        insertPictograma(db, "PEGAMENTO", "categoria_utiles_pegamento", 24, "en","GLUE")
        insertPictograma(db, "REGLA", "categoria_utiles_regla", 24, "en","RULER")
        insertPictograma(db, "ROTULADOR", "categoria_utiles_rotulador", 24, "en","MARKER")
        insertPictograma(db, "SACAPUNTAS", "categoria_utiles_sacapuntas", 24, "en","SHARPENER")
        insertPictograma(db, "SILLA", "categoria_utiles_silla_colegio", 24, "en","CHAIR")
        insertPictograma(db, "TIJERAS", "categoria_utiles_tijeras", 24, "en","SCISSORS")

        insertPictograma(db, "MAESTRA", "categoria_profesores_maestra", 25, "en","TEACHER")
        insertPictograma(db, "MAESTRO", "categoria_profesores_maestro", 25, "en","TEACHER")
        insertPictograma(db, "MAESTRA DE MATEMÁTICAS", "categoria_profesores_maestra_mates", 25, "en","MATH TEACHER")
        insertPictograma(db, "MAESTRO DE MATEMÁTICAS", "categoria_profesores_maestro_mates", 25, "en","MATH TEACHER")
        insertPictograma(db, "MAESTRO DE MÚSICA", "categoria_profesores_profesor_musica", 25, "en","MUSIC TEACHER")
        insertPictograma(db, "MAESTRA DE MÚSICA", "categoria_profesores_profesora_musica", 25, "en","MUSIC TEACHER")
        insertPictograma(db, "MAESTRO DE TALLER", "categoria_profesores_profesor_taller", 25, "en","WORKSHOP TEACHER")
        insertPictograma(db, "MAESTRA DE TALLER", "categoria_profesores_profesora_taller", 25, "en","WORKSHOP TEACHER")
        insertPictograma(db, "EDUCACIÓN FÍSICA", "categoria_profesores_profesora_educacion_fisica", 25, "en","GYM TEACHER")
        insertPictograma(db, "EDUCACIÓN FÍSICA", "categorias_profesores_profesor_educacion_fisica", 25, "en","GYM TEACHER")
        insertPictograma(db, "ORIENTACIÓN", "categoria_profesores_servicio_orientacion", 25, "en","GUIDANCE")
        insertPictograma(db, "ORIENTACIÓN", "categoria_profesores_servicio_orientacion1", 25, "en","GUIDANCE")

        insertarPictoCuaderno(db, "IZQUIERDA", "cuaderno_escala_izquierda", 1, "en","LEFT")
        insertarPictoCuaderno(db, "DERECHA", "cuaderno_escala_derecha", 1, "en","RIGHT")
        insertarPictoCuaderno(db, "SI", "cuaderno_escala_bien", 1, "en","YES")
        insertarPictoCuaderno(db, "NO", "cuaderno_escala_mal", 1, "en","NO")
        insertarPictoCuaderno(db, "HORAS", "cuaderno_escala_hora", 1, "en","HOURS")
        insertarPictoCuaderno(db, "DÍAS", "cuaderno_escala_dia", 1, "en","DAYS")
        insertarPictoCuaderno(db, "SEMANAS", "cuaderno_escala_semana", 1, "en","WEEKS")
        insertarPictoCuaderno(db, "MESES", "cuaderno_escala_mes", 1, "en","MONTHS")
        insertarPictoCuaderno(db, "AÑO", "cuaderno_escala_anio", 1, "en","YEAR")

        insertarPictoCuaderno(db, "MAREO", "cuaderno_sintomas_mareo", 2, "en","DIZZINESS")
        insertarPictoCuaderno(db, "CANSANCIO", "cuaderno_sintomas_cansancio", 2, "en","TIREDNESS")
        insertarPictoCuaderno(db, "ESCALOFRIOS", "cuaderno_sintomas_escalofrios", 2, "en","CHILLS")
        insertarPictoCuaderno(db, "TOS", "cuaderno_sintomas_tos", 2, "en","COUGH")
        insertarPictoCuaderno(db, "FIEBRE", "cuaderno_sintomas_fiebre", 2, "en","FEVER")
        insertarPictoCuaderno(db, "NO VEO BIEN", "cuaderno_sintomas_noveobien", 2, "en","I CAN''T SEE WELL")
        insertarPictoCuaderno(db, "VOMITOS", "cuaderno_sintomas_vomitar", 2, "en","VOMIT")
        insertarPictoCuaderno(db, "ALERGIA", "cuaderno_sintomas_alergia", 2, "en","ALLERGY")
        insertarPictoCuaderno(db, "RESFRIADO", "cuaderno_sintomas_resfriado", 2, "en","COLD")
        insertarPictoCuaderno(db, "SARPULLIDO", "cuaderno_sintomas_sarpullido", 2, "en","RASH")
        insertarPictoCuaderno(db, "HERIDA", "cuaderno_sintomas_herida", 2, "en","WOUND")
        insertarPictoCuaderno(db, "QUEMADURA", "cuaderno_sintomas_quemadura", 2, "en","BURN")
        insertarPictoCuaderno(db, "TAQUICARDIA", "cuaderno_sintomas_taquicardia", 2, "en","TACHYCARDIA")
        insertarPictoCuaderno(db, "DIARREA", "cuaderno_sintomas_diarrea", 2, "en","DIARRHEA")
        insertarPictoCuaderno(db, "ESTREÑIMIENTO", "cuaderno_sintomas_estrenimiento", 2, "en","CONSTIPATION")

        insertarPictoCuaderno(db, "CUELLO", "cuaderno_dolor_cuello", 3, "en","NECK")
        insertarPictoCuaderno(db, "ESPALDA", "cuaderno_dolor_espalda", 3, "en","BACK")
        insertarPictoCuaderno(db, "PECHO", "cuaderno_dolor_pecho", 3, "en","CHEST")
        insertarPictoCuaderno(db, "CULO", "cuaderno_dolor_culo", 3, "en","BUTT")
        insertarPictoCuaderno(db, "MUÑECA", "cuaderno_dolor_muneca", 3, "en","WRIST")
        insertarPictoCuaderno(db, "RODILLA", "cuaderno_dolor_rodilla", 3, "en","KNEE")
        insertarPictoCuaderno(db, "TOBILLO", "cuaderno_dolor_tobillo", 3, "en","ANKLE")
        insertarPictoCuaderno(db, "PIE", "cuaderno_dolor_pie", 3, "en","FOOT")
        insertarPictoCuaderno(db, "MUELA", "cuaderno_dolor_muela", 3, "en","TOOTH")
        insertarPictoCuaderno(db, "OÍDO", "cuaderno_dolor_oido", 3, "en","EAR")
        insertarPictoCuaderno(db, "CABEZA", "cuaderno_dolor_cabeza", 3, "en","HEAD")
        insertarPictoCuaderno(db, "BRAZO", "cuaderno_dolor_brazo", 3, "en","ARM")
        insertarPictoCuaderno(db, "GARGANTA", "cuaderno_dolor_garganta", 3, "en","THROAT")
        insertarPictoCuaderno(db, "TRIPA", "cuaderno_dolor_estomago", 3, "en","STOMACH")
        insertarPictoCuaderno(db, "PIERNA", "cuaderno_dolor_pierna", 3, "en","LEG")

        insertarPictoCuaderno(db, "ABURRIDO", "cuaderno_sentimientos_aburrir", 4, "en","BORED")
        insertarPictoCuaderno(db, "ALEGRE", "cuaderno_sentimientos_alegrar", 4, "en","HAPPY")
        insertarPictoCuaderno(db, "AMADO", "cuaderno_sentimientos_amado", 4, "en","LOVED")
        insertarPictoCuaderno(db, "ANSIOSO", "cuaderno_sentimientos_ansioso", 4, "en","ANXIOUS")
        insertarPictoCuaderno(db, "ASQUEADO", "cuaderno_sentimientos_asco", 4, "en","DISGUSTED")
        insertarPictoCuaderno(db, "ASUSTADO", "cuaderno_sentimientos_asustar", 4, "en","SCARED")
        insertarPictoCuaderno(db, "AUTOESTIMA", "cuaderno_sentimientos_autoestima", 4, "en","SELF-ESTEEM")
        insertarPictoCuaderno(db, "AVERGONZADO", "cuaderno_sentimientos_avergonzar", 4, "en","EMBARRASSED")
        insertarPictoCuaderno(db, "CANSADO", "cuaderno_sentimientos_cansar", 4, "en","TIRED")
        insertarPictoCuaderno(db, "CONDICION", "cuaderno_sentimientos_condicion", 4, "en","CONDITION")
        insertarPictoCuaderno(db, "CONFUNDIDO", "cuaderno_sentimientos_confundir", 4, "en","CONFUSED")
        insertarPictoCuaderno(db, "DISTRAIDO", "cuaderno_sentimientos_distraer", 4, "en","DISTRACTED")
        insertarPictoCuaderno(db, "ENAMORADO", "cuaderno_sentimientos_enamorado", 4, "en","IN LOVE")
        insertarPictoCuaderno(db, "ENFADADO", "cuaderno_sentimientos_enfadar", 4, "en","ANGRY")
        insertarPictoCuaderno(db, "ENTRISTECIDO", "cuaderno_sentimientos_entristecer", 4, "en","SAD")
        insertarPictoCuaderno(db, "ENVIDIOSO", "cuaderno_sentimientos_envidia", 4, "en","ENVIOUS")
        insertarPictoCuaderno(db, "NOSTALGICO", "cuaderno_sentimientos_nostalgico", 4, "en","NOSTALGIC")
        insertarPictoCuaderno(db, "REGOCIJAR", "cuaderno_sentimientos_regocijar", 4, "en","REJOICE")
        insertarPictoCuaderno(db, "SERIO", "cuaderno_sentimientos_serio", 4, "en","SERIOUS")
        insertarPictoCuaderno(db, "SORPRENDIDO", "cuaderno_sentimientos_sorprender", 4, "en","SURPRISED")
        insertarPictoCuaderno(db, "TENER MIEDO", "cuaderno_sentimientos_tener_miedo", 4, "en","TO BE AFRAID")
        insertarPictoCuaderno(db, "TRANQUILO", "cuaderno_sentimientos_tranquilo", 4, "en","QUIET")
        insertarPictoCuaderno(db, "TRANQUILO", "cuaderno_sentimientos_tranquilo1", 4, "en","CALM")
        insertarPictoCuaderno(db, "VERGUENZA", "cuaderno_sentimientos_verguenza", 4, "en","SHAME")
    }

    @SuppressLint("Range")
    fun insertarPictoCuaderno(db: SQLiteDatabase, nombre: String, image: String, idCuaderno: Int, language: String, translation: String){
        db.execSQL("INSERT INTO Pictograma (nombre, imagen) VALUES('$nombre', '$image')")
        //get id here
        val cursor = db.rawQuery("SELECT last_insert_rowid()", null)
        var id: Long = -1
        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getLong(0)
            cursor.close()
        }
        db.execSQL("INSERT INTO RelacionPictogramaCuaderno (id_pictograma, id_cuaderno) VALUES('$id', '$idCuaderno')")
        db.execSQL("INSERT INTO Traduccion (language, translation) VALUES('$language', '$translation')")
        db.execSQL("INSERT INTO RelacionPictoTraduccion (id_pictograma, id_traduccion) VALUES('$id', (SELECT last_insert_rowid()))")
    }

    private fun insertCategoria(db: SQLiteDatabase, titulo: String, imagen: String?, principal: Boolean, color: String?, language: String, translation: String){
        db.execSQL("INSERT INTO Categoria (titulo, imagen, principal, color) VALUES('$titulo', '$imagen', $principal, '$color')")
        val cursor = db.rawQuery("SELECT last_insert_rowid()", null)
        var idCategoria: Long = -1 // Initialize id with a default value
        if (cursor != null && cursor.moveToFirst()) {
            idCategoria = cursor.getLong(0) // Get the value of the first column (which should be the id)
            cursor.close() // Close the cursor when done
        }
        db.execSQL("INSERT INTO Traduccion (language, translation) VALUES('$language', '$translation')")
        db.execSQL("INSERT INTO RelacionPictoTraduccion (id_categoria, id_traduccion) VALUES('$idCategoria', (SELECT last_insert_rowid()))")
    }

    private fun insertCuaderno(db: SQLiteDatabase, titulo: String, imagen: String, termometro: Int, language: String, translation: String) {
        db.execSQL("INSERT INTO Cuaderno (titulo, imagen, termometro) VALUES('$titulo', '$imagen', $termometro)")
        val cursor = db.rawQuery("SELECT last_insert_rowid()", null)
        var idCuaderno: Long = -1 // Initialize id with a default value
        if (cursor != null && cursor.moveToFirst()) {
            idCuaderno = cursor.getLong(0) // Get the value of the first column (which should be the id)
            cursor.close() // Close the cursor when done
        }
        db.execSQL("INSERT INTO Traduccion (language, translation) VALUES('$language', '$translation')")
        db.execSQL("INSERT INTO RelacionPictoTraduccion (id_cuaderno, id_traduccion) VALUES('$idCuaderno', (SELECT last_insert_rowid()))")
    }

    private fun insertPictograma(db: SQLiteDatabase, nombre: String, imagen: String, idCategoria: Int, language: String, translation: String) {
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES('$nombre', '$imagen', $idCategoria)")
        val cursor = db.rawQuery("SELECT last_insert_rowid()", null)
        var idPictograma: Long = -1 // Initialize id with a default value
        if (cursor != null && cursor.moveToFirst()) {
            idPictograma = cursor.getLong(0) // Get the value of the first column (which should be the id)
            cursor.close() // Close the cursor when done
        }
        db.execSQL("INSERT INTO Traduccion (language, translation) VALUES('$language', '$translation')")
        db.execSQL("INSERT INTO RelacionPictoTraduccion (id_pictograma, id_traduccion) VALUES('$idPictograma', (SELECT last_insert_rowid()))")
    }

}