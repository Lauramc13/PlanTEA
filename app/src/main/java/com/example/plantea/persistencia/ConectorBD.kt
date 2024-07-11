package com.example.plantea.persistencia

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log
import com.example.plantea.dominio.Usuario

class ConectorBD(ctx: Context?) {
    private val dbHelper: BDSQLiteHelper
    private var db: SQLiteDatabase? = null

    /*Constructor*/
    init {
        dbHelper = BDSQLiteHelper(ctx, NOMBRE_BD, null, 2)
    }

    /*Abre la conexión con la base de datos*/
    @Throws(SQLException::class)
    fun abrir(): SQLiteDatabase {
        if (db == null || !db!!.isOpen) {
            db = dbHelper.writableDatabase
        }
        return db!!
    }
    /*Cierra la conexión con la base de datos*/
    fun cerrar() {
       // if (db != null) db!!.close()
    }

    /*************************************************************************************************************/
    /************************************ Funciones de la base de datos ******************************************/
    /*************************************************************************************************************/

    // Obtener todos los pictogramas de una categoria
    fun listarPictogramasPrueba(categoria: Int, userId: String?, language: String): Cursor {
        return db!!.rawQuery(
            "SELECT Pictograma.id, " +
                    "COALESCE(Traduccion.translation, Pictograma.nombre) AS nombre, " +
                    "Pictograma.imagen, Pictograma.id_categoria, " +
                    "CASE WHEN Favorito.id_usuario IS NOT NULL THEN 1 ELSE 0 END AS favorito " +
                    "FROM Pictograma " +
                    "LEFT JOIN Favorito ON Favorito.id_pictograma = Pictograma.id AND Favorito.id_usuario = ? " +
                    "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_pictograma = Pictograma.id " +
                    "LEFT JOIN Traduccion ON RelacionPictoTraduccion.id_traduccion = Traduccion.id " +
                    "AND Traduccion.language = ? " +
                    "WHERE Pictograma.id_categoria = ? AND (Pictograma.id_usuario IS NULL OR Pictograma.id_usuario = ?)",
            arrayOf(userId, language, categoria.toString(), userId)
        )
    }

    //Insertar un pictograma nuevo
    fun insertarPictograma(nombre: String?, imagen: String?, categoria: String?, idUsuario: String?): String {
        var categoria = categoria
        val c = db!!.rawQuery("SELECT id from Categoria where Categoria.titulo = '$categoria'", null)
        if (c.moveToFirst()) {
            categoria = c.getString(0)
        }
        c.close()
        db!!.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria, id_usuario) VALUES ('$nombre', '$imagen','$categoria', '$idUsuario')")

        val c2= db!!.rawQuery("SELECT last_insert_rowid()", null)
        var id = ""
        if (c2.moveToFirst()) {
            id = c2.getString(0)
        }
        c2.close()
        return id
    }

    // Insertar una planificacion nueva
    fun insertarPlanificacion(idUsuario: String, titulo: String?): Int {
        var id = 0
        val values = ContentValues()
        values.put("titulo", titulo)
        values.put("id_usuario", idUsuario)

        try {
            val insertedId = db?.insertOrThrow("Planificacion", null, values)
            if (insertedId != null) {
                id = insertedId.toInt()
            }
        } catch (e: SQLiteException) {
            // Handle any potential exceptions
            e.printStackTrace()
        }

        return id
    }

    // Insertar un pictograma en una planificacion
    fun addPictogramasPlanificacion(idPlan: Int?, idPicto: String?, idPictoAPI: String?, historiaPicto: String?, duracionPicto: String?, pictoEntretenimiento: Int?): Boolean {
        db!!.execSQL("INSERT INTO RelacionPictogramaPlan (id_plan, id_pictograma, id_pictogramaAPI, historia, duracion, id_picto_entre) VALUES ('$idPlan', '$idPicto', '$idPictoAPI', '$historiaPicto', '$duracionPicto', '$pictoEntretenimiento')")
        return true
    }

    /*Listar planificaciones disponibles*/
    fun listarPlanificaciones(idUsuario: String): Cursor {
        return db!!.rawQuery("SELECT titulo, id from Planificacion WHERE id_usuario = '$idUsuario' AND (es_actual IS NULL OR es_actual = 0) ", null)
    }

    /*Modificar la visibilidad de un evento para mostrar o no al niño un plan*/
    fun modificarVisibilidad(valor: Int, id: Int) {
        db!!.execSQL("UPDATE Evento SET visible ='$valor' WHERE id ='$id'")
    }

    @SuppressLint("Range")
    fun invisibiliarEvento(id: Int, idUsuario: String) {
        val cursor = db!!.rawQuery("SELECT fecha FROM Evento WHERE id = '$id'", null)
        if(cursor.moveToFirst()){
            val fechaIndex = cursor.getColumnIndex("fecha")
            if(fechaIndex == -1){
                return
            }else{
                val fecha = cursor.getString(cursor.getColumnIndex("fecha"))
                db!!.execSQL("UPDATE Evento SET visible = 0 WHERE fecha = '$fecha' AND id_usuario = '$idUsuario' AND id <> '$id'")
            }
        }
        cursor.close()
    }

    /*Eliminar una planificacion*/
    fun borrarPlanificacion(id: Int) {
        db!!.execSQL("DELETE FROM Planificacion WHERE id='$id'")
        db!!.execSQL("DELETE FROM RelacionPictogramaPlan WHERE id_plan='$id'")
    }

    fun borrarEventoFromPlanificacion(idEvento: Int, idPlan: Int) {
        db!!.execSQL("DELETE FROM RelacionEventoPlan WHERE id_plan='$idPlan' AND id_evento = '$idEvento'")
    }

    fun obtenerIdEvento(idPlan: Int): Cursor {
        return db!!.rawQuery("SELECT id_evento FROM RelacionEventoPlan WHERE id_plan='$idPlan'", null)
    }

    // Listar pictogramas de una planificacion
    fun listarPictogramasPlanificacion(id: Int, language: String): Cursor {
        return db!!.rawQuery(
            "SELECT CombinedPictograms.id, " +
                    "COALESCE(Traduccion.translation, CombinedPictograms.nombre) AS nombre, " +
                    "CombinedPictograms.imagen, CombinedPictograms.id_categoria, RelacionPictogramaPlan.historia, RelacionPictogramaPlan.duracion, RelacionPictogramaPlan.id_picto_entre " +
                    "FROM (SELECT id, nombre, imagen, id_categoria FROM Pictograma UNION SELECT id, nombre, imagen, NULL AS id_categoria FROM PictogramaAPI) AS CombinedPictograms " +
                    "INNER JOIN RelacionPictogramaPlan ON RelacionPictogramaPlan.id_pictograma = CombinedPictograms.id OR RelacionPictogramaPlan.id_pictogramaAPI = CombinedPictograms.id " +
                    "INNER JOIN Planificacion ON Planificacion.id = RelacionPictogramaPlan.id_plan " +
                    "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_pictograma = CombinedPictograms.id " +
                    "LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = ? " +
                    "WHERE Planificacion.id = ? " +
                    "ORDER BY RelacionPictogramaPlan.id",
            arrayOf(language, id.toString())
        )
    }

    /*Actualizar una planificacion*/
    fun actualizarPlanificacion(id: Int, actual: Int) {
        db!!.execSQL("UPDATE Planificacion SET es_actual ='$actual' WHERE id ='$id'")
    }

    fun obtenerPictograma(idPicto: String?, language: String): Cursor{
       return db!!.rawQuery("SELECT CombinedPictograms.id, CombinedPictograms.imagen, " +
               "COALESCE(Traduccion.translation, CombinedPictograms.nombre) AS nombre " +
               "FROM (SELECT id, nombre, imagen FROM Pictograma UNION SELECT id, nombre, imagen FROM PictogramaAPI) AS CombinedPictograms " +
               "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_pictograma = CombinedPictograms.id " +
                "LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = '$language' " +
                "WHERE CombinedPictograms.id = '$idPicto'", null)
    }

    fun obtenerPlanificacion(idUsuario: String, idEvento: String, language: String): Cursor {
        return db!!.rawQuery(
            "SELECT CombinedPictograms.id, " +
                    "COALESCE(Traduccion.translation, CombinedPictograms.nombre) AS nombre, " +
                    "CombinedPictograms.imagen, CombinedPictograms.id_categoria, RelacionPictogramaPlan.historia, RelacionPictogramaPlan.duracion, RelacionPictogramaPlan.id_picto_entre " +
                    "FROM (SELECT id, nombre, imagen, id_categoria FROM Pictograma UNION SELECT id, nombre, imagen, NULL AS id_categoria FROM PictogramaAPI) AS CombinedPictograms " +
                    "INNER JOIN RelacionPictogramaPlan ON RelacionPictogramaPlan.id_pictograma = CombinedPictograms.id OR RelacionPictogramaPlan.id_pictogramaAPI = CombinedPictograms.id " +
                    "INNER JOIN RelacionEventoPlan ON RelacionEventoPlan.id_plan = RelacionPictogramaPlan.id_plan " +
                    "INNER JOIN Evento ON RelacionEventoPlan.id_evento = Evento.id " +
                    "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_pictograma = CombinedPictograms.id " +
                    "LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = ? " +
                    "WHERE Evento.visible = 1 AND Evento.id_usuario = ? AND RelacionEventoPlan.id_evento = ? " +
                    "ORDER BY RelacionPictogramaPlan.id",
            arrayOf(language, idUsuario, idEvento)
        )
    }

    /*Obtener el numero de planficaciones visibles*/
    fun contarEventoVisible(idUsuario: String, fecha: String): Cursor {
        return db!!.rawQuery("SELECT id FROM Evento WHERE visible = 1 AND id_usuario = '$idUsuario' AND fecha = '$fecha'", null)
    }

    fun listarTituloEvento(idUsuario: String, fecha: String): Cursor {
        return db!!.rawQuery("SELECT id, nombre from Evento where Evento.visible = 1 AND Evento.id_usuario = '$idUsuario' AND Evento.fecha = '$fecha'", null)
    }

    fun insertarCategoria(nombre: String?, imagen: String?, color: String, idUsuario: String): Cursor {
        db!!.execSQL("INSERT INTO Categoria (titulo, imagen, color, id_usuario) VALUES ('$nombre', '$imagen', '$color', '$idUsuario')")
        return db!!.rawQuery("SELECT last_insert_rowid()", null)
    }

    fun eliminarCategoria(idUsuario: String, idCategoria: Int) {
        val cursor = db!!.rawQuery("SELECT id FROM Categoria WHERE id = '$idCategoria' and id_usuario IS NULL", null)

        if (cursor.moveToFirst()) {
            db!!.execSQL("INSERT INTO RelacionCategoriaUsuario (is_deleted, id_usuario, id_categoria) VALUES (1, '$idUsuario', '$idCategoria')")
        }else{
            db!!.execSQL("DELETE FROM Categoria WHERE id = '$idCategoria' AND id_usuario = '$idUsuario'")
        }

    }

    fun obtenerIdCategoria(nombre: String?, language: String): Cursor {
        return db!!.rawQuery(
            "SELECT Categoria.id " +
                    "FROM Categoria " +
                    "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_categoria = Categoria.id " +
                    "LEFT JOIN Traduccion ON RelacionPictoTraduccion.id_traduccion = Traduccion.id AND Traduccion.language = ? " +
                    "WHERE COALESCE(Traduccion.translation, Categoria.titulo) = ?",
            arrayOf(language, nombre))
    }

    /*Listar todas las categorias*/
    fun listarCategorias(language: String): Cursor {
        return db!!.rawQuery("SELECT Categoria.id, COALESCE(Traduccion.translation, Categoria.titulo) AS titulo from Categoria " +
                "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_categoria = Categoria.id " +
                "LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = '$language' " +
                "WHERE Categoria.id_usuario IS NULL", null)
    }

    fun listarCategoriasPrincipales(idUsuario: String, language: String): Cursor {
        return db!!.rawQuery(
            "SELECT Categoria.id, " +
                    "COALESCE(Traduccion.translation, Categoria.titulo) AS titulo, " +
                    "Categoria.imagen, Categoria.color " +
                    "FROM Categoria " +
                    "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_categoria = Categoria.id " +
                    "LEFT JOIN Traduccion ON RelacionPictoTraduccion.id_traduccion = Traduccion.id " +
                    "AND Traduccion.language = ? " +
                    "WHERE Categoria.id NOT IN (SELECT id_categoria FROM RelacionCategoriaUsuario WHERE id_usuario = ? AND is_deleted = 1) AND Categoria.id_usuario IS NULL OR Categoria.id_usuario = ?",
            arrayOf(language, idUsuario, idUsuario)
        )
    }

    /*Insertamos el usuario*/
    fun insertarUsuario(email: String?, password:String?, username:String?, name: String?, objeto:String?, nameTEA:String?): Boolean {
        //Creamos el registro a insertar como objeto ContentValues
        val nuevoUsuario = ContentValues()
        nuevoUsuario.put("email", email)
        nuevoUsuario.put("username", username)
        nuevoUsuario.put("password", password)
        nuevoUsuario.put("name", name)
        nuevoUsuario.put("objeto", objeto)
        nuevoUsuario.put("nameTEA", nameTEA)

        var resultado : Long? = null
        //Insertamos el registro en la base de datos
        try{
            resultado = db?.insert("Usuario", null, nuevoUsuario) ?: -1
        }catch (e: SQLiteConstraintException){
            Log.d("TAG", "El usuario ya existe")
        }

        return resultado != -1L
    }

    /*Cambiar contraseña del usuario*/
    fun actualizarPass(idUsuario:String, passNueva: String): Boolean {
        db!!.execSQL("UPDATE Usuario SET password =? WHERE Usuario.id = ?", arrayOf(passNueva, idUsuario))
        return true
    }

   /* fun listarPictogramasCuaderno(idCuaderno: Int, language: String): Cursor {
        return db!!.rawQuery(
            "SELECT CombinedPictograms.id, " +
                    "COALESCE(Traduccion.translation, CombinedPictograms.nombre) AS nombre, " +
                    "CombinedPictograms.imagen, RelacionPictogramaCuaderno.id_cuaderno, " +
                    "CASE WHEN CombinedPictograms.id IN (SELECT id FROM Pictograma) THEN 0 ELSE 1 END AS sourceAPI " +
                    "FROM (SELECT id, nombre, imagen FROM Pictograma UNION SELECT id, nombre, imagen FROM PictogramaAPI) AS CombinedPictograms " +
                    "INNER JOIN RelacionPictogramaCuaderno ON RelacionPictogramaCuaderno.id_pictograma = CombinedPictograms.id OR RelacionPictogramaCuaderno.id_pictogramaAPI = CombinedPictograms.id " +
                    "INNER JOIN Cuaderno ON Cuaderno.id = RelacionPictogramaCuaderno.id_cuaderno " +
                    "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_pictograma = CombinedPictograms.id " +
                    "LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = ? " +
                    "WHERE Cuaderno.id = ? " +
                    "ORDER BY RelacionPictogramaCuaderno.id",
            arrayOf(language, idCuaderno.toString())
        )
    }*/

    /*Insertar nueva cita en la tabla eventos*/
    fun insertarCita(idUsuario: String?, nombre: String?, fecha: String, hora: String?): Int {
        var id = 0
        db!!.execSQL("INSERT INTO Evento (id_usuario, nombre,fecha,hora,visible) VALUES ('$idUsuario', '$nombre','$fecha','$hora', 0)")
        val c = db!!.rawQuery("SELECT last_insert_rowid()", null)
        if (c.moveToFirst()) {
            id = c.getInt(0)
        }
        c.close()
        return id
    }

    fun insertarCitaEvento(idEvento: Int, idPlan: Int){
        db!!.execSQL("INSERT INTO RelacionEventoPlan (id_evento, id_plan) VALUES ('$idEvento', '$idPlan')")
    }

    fun listarEventosPorUsuario(idUsuario: String): Cursor {
        val selectionArgs = arrayOf(idUsuario)
        return db!!.rawQuery(
            "SELECT Evento.id, Evento.id_usuario, Evento.nombre, Evento.fecha, Evento.hora, RelacionEventoPlan.id_plan, Evento.visible " +
                    "FROM Evento " +
                    "INNER JOIN RelacionEventoPlan ON Evento.id = RelacionEventoPlan.id_evento " +
                    "WHERE Evento.id_usuario = ?",
            selectionArgs
        )
    }

    /*Eliminar evento*/
    fun eliminarEvento(id: Int) {
        db!!.execSQL("DELETE FROM Evento WHERE id='$id'")
        db!!.execSQL("DELETE FROM RelacionEventoPlan WHERE id_evento='$id'")
    }

    /*Listar categorias de consulta*/
    /*fun listarConsulta(identificador: Int): Cursor {
        return db!!.rawQuery("SELECT nombre from Pictograma Inner JOIN Categoria where Categoria.id = Pictograma.id_categoria AND Categoria.id = $identificador", null)
    }*/

    @SuppressLint("Range")
    /*Obtenemos los usuarios existentes con el email que tenemos*/
    fun obtenerUsuarioExistente(email: String): Usuario {
        val cursor = db!!.rawQuery("SELECT * from Usuario WHERE Usuario.email = '$email'", null)
        val usuario: Usuario?
        cursor.moveToFirst()

        val username = cursor.getString(cursor.getColumnIndex("username"))
        val name = cursor.getString(cursor.getColumnIndex("name"))
        val objeto = cursor.getString(cursor.getColumnIndex("objeto"))
        val imagen = cursor.getString(cursor.getColumnIndex("imagen"))
        val nameTEA = cursor.getString(cursor.getColumnIndex("nameTEA"))
        val imagenTEA = cursor.getString(cursor.getColumnIndex("imagenTEA"))
        val imagenObjeto = cursor.getString(cursor.getColumnIndex("imagenObjeto"))
        usuario = Usuario(name, email, username, objeto, imagen, nameTEA, imagenTEA, imagenObjeto)

        cursor.close()
        return usuario
    }

    
    fun addImagen(image: String, idUsuario: String) {
        db!!.execSQL("UPDATE Usuario SET imagen ='$image' WHERE Usuario.id = '$idUsuario'")
    }

    fun addImagenTEA(image: String, idUsuario: String) {
        db!!.execSQL("UPDATE Usuario SET imagenTEA ='$image' WHERE Usuario.id = '$idUsuario'")
    }


    fun addImagenObjeto(image: String, idUsuario: String) {
        db!!.execSQL("UPDATE Usuario SET imagenObjeto ='$image' WHERE Usuario.id = '$idUsuario'")
    }

    @SuppressLint("Range")
    fun consultarId(email: String): String? {
        val cursor = db!!.rawQuery("SELECT id FROM Usuario WHERE email = ?", arrayOf(email))
        return if (cursor.moveToFirst()) {
            val id = cursor.getString(cursor.getColumnIndex("id"))
            cursor.close()
            id
        } else {
            cursor.close()
            null
        }
    }

    fun insertarFavorito(idUsuario: String?, id:String?, nombre: String?, imagen: String?, sourceApi: Boolean) {
        db!!.execSQL("INSERT INTO Favorito (id_usuario, id_pictograma) VALUES ('$idUsuario', '$id')")
        if(sourceApi){
            db!!.execSQL("INSERT OR REPLACE INTO PictogramaAPI (id, nombre, imagen) VALUES ('$id', '$nombre', '$imagen')")
        }
    }

    fun borrarFavorito(idUsuario: String?, idPicto: String?) {
        db!!.execSQL("DELETE FROM Favorito WHERE id_usuario = ? AND id_pictograma = ?", arrayOf(idUsuario, idPicto))
    }

    fun obtenerFavoritos(idUsuario: String?, language: String): Cursor {
        return db!!.rawQuery(
            "SELECT CombinedPictograms.id, " +
                    "COALESCE(Traduccion.translation, CombinedPictograms.nombre) AS nombre, " +
                    "CombinedPictograms.imagen " +
                    "FROM (SELECT id, nombre, imagen FROM Pictograma UNION SELECT id, nombre, imagen FROM PictogramaAPI) AS CombinedPictograms " +
                    "INNER JOIN Favorito ON Favorito.id_pictograma = CombinedPictograms.id " +
                    "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_pictograma = CombinedPictograms.id " +
                    "LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = ? " +
                    "WHERE Favorito.id_usuario = ?",
            arrayOf(language, idUsuario)
        )
    }

    fun guardarConfiguracion(nombreUsuarioPlanificador: String, username: String, nombreUsuarioTEA: String, nombreObjeto: String, rutaPlanificador: String, rutaUsuarioTEA: String, rutaObjeto: String, idUsuario: String?) {
        val query = "UPDATE Usuario SET name = '$nombreUsuarioPlanificador', username = '$username', nameTEA = '$nombreUsuarioTEA', objeto = '$nombreObjeto', imagen = '$rutaPlanificador', imagenTEA = '$rutaUsuarioTEA', imagenObjeto = '$rutaObjeto' WHERE id = '$idUsuario'"
        db?.execSQL(query)
    }

    fun checkCredentials(email: String, password: String): Boolean {
        val cursor = db!!.rawQuery("SELECT COUNT(*) FROM Usuario WHERE email = ? AND password = ?", arrayOf(email, password))
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count > 0
    }

    fun getFavorito(idPicto: String?, idUsuario: String?): Boolean {
        val cursor = db!!.rawQuery(
            "SELECT CombinedPictograms.id " +
                    "FROM (SELECT id FROM Pictograma UNION SELECT id FROM PictogramaAPI) AS CombinedPictograms " +
                    "INNER JOIN Favorito ON Favorito.id_pictograma = CombinedPictograms.id " +
                    "WHERE CombinedPictograms.id = ? AND Favorito.id_usuario = ? ",
            arrayOf(idPicto, idUsuario)
        )

        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

   /* fun insertarCuaderno(idUsuario: String, titulo: String, imagen: String?, termometro: Int): Int {
        val statement = db!!.compileStatement("INSERT INTO Cuaderno (titulo, imagen, termometro, id_usuario) VALUES (?, ?, ?, ?)")
        statement.bindString(1, titulo)
        statement.bindString(2, imagen)
        statement.bindLong(3, termometro.toLong())
        statement.bindString(4, idUsuario)
        val insertedId = statement.executeInsert()
        statement.close()
        return insertedId.toInt()
    }*/

    //CAMBIAR ESTO
    /*fun insertarPictogramaCuadernoBusqueda(id: String?, titulo: String?, imagen: String?, idCuaderno: Int) {
        db!!.execSQL("INSERT OR REPLACE INTO PictogramaAPI (id, nombre, imagen) VALUES ('$id', '$titulo','$imagen')")
        db!!.execSQL("INSERT OR REPLACE INTO RelacionPictogramaCuaderno (id_pictogramaAPI, id_cuaderno) VALUES ('$id','$idCuaderno')")
    }*/

   /* fun insertarPictogramaCuaderno( titulo: String?, imagen: String?, idCuaderno: Int, idUsuario: String): Int {
        db!!.execSQL("INSERT INTO Pictograma (nombre, imagen, id_usuario) VALUES ('$titulo','$imagen', '$idUsuario')")
        val c = db!!.rawQuery("SELECT last_insert_rowid()", null)
        var id = 0
        if (c.moveToFirst()) {
            id = c.getInt(0)
        }
        c.close()

        db!!.execSQL("INSERT OR REPLACE INTO RelacionPictogramaCuaderno (id_pictograma, id_cuaderno) VALUES ('$id','$idCuaderno')")
        return id
    }*/

    /*fun borrarPictogramaCuadernoBusqueda(id: String?, idCuaderno: Int) {
        db!!.execSQL("DELETE FROM RelacionPictogramaCuaderno WHERE id_pictogramaAPI ='$id' AND id_cuaderno = '$idCuaderno'")
    }

    fun borrarPictogramaCuaderno(id: String?, idCuaderno: Int) {
        db!!.execSQL("DELETE FROM RelacionPictogramaCuaderno WHERE id_pictograma ='$id' AND id_cuaderno = '$idCuaderno'")
    }

    fun borrarCuaderno(idCuaderno: Int) {
        //Borrar fila en tabla cuaderno y borrar todas las filas que id_cuaderno = idCuaderno
        db!!.execSQL("DELETE FROM Cuaderno WHERE id = '$idCuaderno'")
        db!!.execSQL("DELETE FROM RelacionPictogramaCuaderno WHERE id_cuaderno = '$idCuaderno'")
    }

    fun editarCuaderno(idUsuario: String, idCuaderno: String, titulo: String, imagen: String?, termometro: Int) {
        db!!.execSQL("UPDATE Cuaderno SET titulo ='$titulo', imagen = '$imagen', termometro = '$termometro' WHERE id ='$idCuaderno' AND id_usuario = '$idUsuario'")
    }

    fun listarCuadernos(idUsuario: String, language: String): Cursor {
        return db!!.rawQuery("SELECT Cuaderno.id, " +
                "COALESCE(Traduccion.translation, titulo) AS titulo, " +
                "imagen, termometro from Cuaderno " +
                "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_cuaderno = Cuaderno.id " +
                "LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = '$language' " +
                " WHERE id_usuario = '$idUsuario' OR id_usuario IS NULL", null)
    }*/

    fun addPictogramaAPI(idPicto: String?, nombre: String?, imagen: String?) {
        db!!.execSQL("INSERT OR REPLACE INTO PictogramaAPI (id, nombre, imagen) VALUES ('$idPicto', '$nombre', '$imagen')")
    }

    fun checkCategoriaExiste(titulo: String, idUsuario: String, language: String): Cursor {
        return db!!.rawQuery("SELECT COUNT(*), " +
                "COALESCE(Traduccion.translation, titulo) AS translated_title " +
                "FROM Categoria " +
                "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_categoria = Categoria.id " +
                "LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = '$language' " +
                "WHERE translated_title = '$titulo' AND (Categoria.id_usuario = '$idUsuario' OR Categoria.id_usuario IS NULL)", null)
    }

    fun existsVisibleEventoDia(idUsuario: String, fecha: String): Cursor {
        return db!!.rawQuery(
            "SELECT RelacionEventoPlan.id_plan " +
                    "FROM Evento " +
                    "INNER JOIN RelacionEventoPlan ON Evento.id = RelacionEventoPlan.id_evento " +
                    "WHERE Evento.visible = 1 AND Evento.id_usuario = ? AND Evento.fecha = ?",
            arrayOf(idUsuario, fecha)
        )
    }

    fun listarPictogramasAleatorios(idUsuario: String?, language: String): Cursor {
        return db!!.rawQuery(
            "SELECT CombinedPictograms.id, " +
                    "COALESCE(Traduccion.translation, CombinedPictograms.nombre) AS nombre, " +
                    " CombinedPictograms.imagen " +
                    "FROM (SELECT id, nombre, imagen FROM Pictograma UNION SELECT id, nombre, imagen FROM PictogramaAPI) AS CombinedPictograms " +
                    "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_pictograma = CombinedPictograms.id " +
                    "LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = ? " +
                    "WHERE CombinedPictograms.id NOT IN (SELECT id_pictograma FROM Favorito WHERE id_usuario = ?) " +
                    "ORDER BY RANDOM() LIMIT 6",
            arrayOf(language, idUsuario)
        )
    }

    fun obtenerCategoriaById(idCategoria: Int, language: String): Cursor {
        return db!!.rawQuery(
            "SELECT COALESCE(Traduccion.translation, titulo) AS titulo " +
                    "FROM Categoria " +
                    "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_categoria = Categoria.id " +
                    "LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = ? " +
                    "WHERE Categoria.id = ?",
            arrayOf(language, idCategoria.toString())
        )

    }

    fun obtenerConfiguracionSemana(idUsuario: String): Cursor {
        return db!!.rawQuery("SELECT configurationWeek FROM Semana WHERE id_usuario = '$idUsuario'", null)
    }

    fun obtenerImagenDia(idUsuario: String, dayWeek: String): ByteArray? {
        val cursor = db!!.rawQuery("SELECT pictograma_day FROM Semana WHERE id_usuario = '$idUsuario' AND day_week = '$dayWeek'", null)
        var imagen : ByteArray? = null
        if (cursor.moveToFirst()) {
            imagen = cursor.getBlob(0)
        }
        cursor.close()
        return imagen
    }

    fun guardarImagenSemana(idUsuario: String, imagen: ByteArray?, dayWeek: String) {
        val cursor = db!!.rawQuery("SELECT id FROM Semana WHERE id_usuario = ? AND day_week = ?", arrayOf(idUsuario, dayWeek))
        if(cursor.moveToFirst()){
            db!!.execSQL("UPDATE Semana SET pictograma_day = ? WHERE id_usuario = ? AND day_week = ?", arrayOf(imagen, idUsuario, dayWeek))
        }else{
            db!!.execSQL("INSERT INTO Semana (id_usuario, day_week, pictograma_day) VALUES ('$idUsuario', '$dayWeek', ?)", arrayOf(imagen))
        }
    }

    companion object {
        const val NOMBRE_BD = "PlanTEA"
    }
}