package com.example.plantea.persistencia

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log
import com.example.plantea.dominio.Actividad
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
       //if (db != null) db!!.close()
    }

    /*************************************************************************************************************/
    /************************************ Funciones de la base de datos ******************************************/
    /*************************************************************************************************************/

    fun listarPictogramasCategoria(categoria: Int, userId: String?, language: String): Cursor {
        return db!!.rawQuery(
            "SELECT CombinedPictograms.id, " +
                    "COALESCE(Traduccion.translation, CombinedPictograms.nombre) AS nombre, " +
                    "CombinedPictograms.imagen, CombinedPictograms.id_API, " +
                    "COALESCE(CombinedPictograms.id_categoria_global, CombinedPictograms.id_categoria_local) AS id_categoria, " +
                    "CASE WHEN Favorito.id_usuario IS NOT NULL THEN 1 ELSE 0 END AS favorito " +
                    "FROM (" +
                    "SELECT Pictograma.id, Pictograma.nombre, PictogramaLocal.imagen, NULL as id_API, Pictograma.id_categoria_global, Pictograma.id_categoria_local " +
                    "FROM Pictograma " +
                    "INNER JOIN PictogramaLocal ON Pictograma.id = PictogramaLocal.id AND (PictogramaLocal.id_usuario = ? OR PictogramaLocal.id_usuario IS NULL) " +
                    "UNION ALL " +
                    "SELECT Pictograma.id, Pictograma.nombre, NULL as imagen, PictogramaAPI.id_API, Pictograma.id_categoria_global, Pictograma.id_categoria_local " +
                    "FROM Pictograma " +
                    "INNER JOIN PictogramaAPI ON Pictograma.id = PictogramaAPI.id " +
                    ") AS CombinedPictograms " +
                    "LEFT JOIN Favorito ON Favorito.id_pictograma = CombinedPictograms.id AND Favorito.id_usuario = ? " +
                    "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_pictograma = CombinedPictograms.id " +
                    "LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = ? " +
                    "WHERE (" +
                    // Case 1: Pictogramas in a category created by the user (with no global reference)
                    "CombinedPictograms.id_categoria_local = ? " +
                    // Case 2: Pictogramas of a global category or local category that points to a global category
                    "OR (CombinedPictograms.id_categoria_global = ? " +
                    "OR CombinedPictograms.id_categoria_local IN (" +
                    "SELECT id FROM CategoriaUsuario WHERE id_categoria = ? AND id_usuario = ?))" +
                    ") " +
                    "ORDER BY CombinedPictograms.nombre",
            arrayOf(userId, userId, language, categoria.toString(), categoria.toString(), categoria.toString(), userId)
        )
    }

    fun insertarPictogramaLocal(nombre: String?, imagen: ByteArray?, categoria: String?, idUsuario: String?): String {
            db!!.execSQL("INSERT INTO Pictograma (nombre, id_categoria_local) VALUES ('$nombre','$categoria')")

            val c2= db!!.rawQuery("SELECT last_insert_rowid()", null)
            var id = ""
            if (c2.moveToFirst()) {
                id = c2.getString(0)
            }
            c2.close()

            val values = ContentValues().apply {
                put("id", id)
                put("imagen", imagen)
                put("id_usuario", idUsuario)
            }
            db!!.insert("PictogramaLocal", null, values)

            return id
    }

    fun insertarPictogramaAPI(nombre: String?, idAPI: String?, idCategoria: String?): String{
        //check if there is a pictogram with the same idAPI
        val c = db!!.rawQuery("SELECT id from PictogramaAPI where id_API = '$idAPI'", null)
        if (c.moveToFirst()) {
            return c.getString(0)
        }else{
            db!!.execSQL("INSERT INTO Pictograma (nombre, id_categoria_local) VALUES ('$nombre', '$idCategoria')")
            val c2 = db!!.rawQuery("SELECT last_insert_rowid()", null)
            if (c2.moveToFirst()) {
                val id = c2.getString(0)
                db!!.execSQL("INSERT INTO PictogramaAPI (id, id_API) VALUES ('$id','$idAPI')")
                c.close()
                c2.close()
                return id
            }
        }

        c.close()
      return ""
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
    fun addPictogramasPlanificacion(idPlan: Int?, idPicto: String?, historiaPicto: String?, duracionPicto: String?, pictoEntretenimiento: Int?): Boolean {
        val categoriaValues = ContentValues().apply {
            put("id_plan", idPlan)
            put("id_pictograma", idPicto)
            put("historia", historiaPicto)
            put("duracion", duracionPicto)
            put("id_picto_entre", pictoEntretenimiento)

        }
        return db!!.insert("RelacionPictogramaPlan", null, categoriaValues) > 0
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

    fun listarPictogramasPlanificacion(id: Int, language: String, idUsuario: String): Cursor {
        return db!!.rawQuery(
            "SELECT CombinedPictograms.id, " +
               "COALESCE(Traduccion.translation, CombinedPictograms.nombre) AS nombre,  " +
               "CombinedPictograms.imagen, CombinedPictograms.id_API, " +
                "CASE WHEN CombinedPictograms.id_categoria_global IS NOT NULL THEN CombinedPictograms.id_categoria_global ELSE CombinedPictograms.id_categoria_local " +
                "END AS id_categoria, " +
               "RelacionPictogramaPlan.historia,  " +
               "RelacionPictogramaPlan.duracion,  " +
               "RelacionPictogramaPlan.id_picto_entre  " +
            "FROM ( " +
                    "SELECT Pictograma.id, Pictograma.nombre, PictogramaLocal.imagen, NULL as id_API, Pictograma.id_categoria_global, Pictograma.id_categoria_local " +
                      "FROM Pictograma " +
                        "INNER JOIN PictogramaLocal ON Pictograma.id = PictogramaLocal.id AND (PictogramaLocal.id_usuario = ? OR PictogramaLocal.id_usuario IS NULL) " +
                        "UNION ALL  " +
                        "SELECT Pictograma.id, Pictograma.nombre, NULL as imagen, PictogramaAPI.id_API, Pictograma.id_categoria_global, Pictograma.id_categoria_local  " +
                        "FROM Pictograma  " +
                        "INNER JOIN PictogramaAPI ON Pictograma.id = PictogramaAPI.id " +
                        ") AS CombinedPictograms   " +
        "INNER JOIN RelacionPictogramaPlan ON RelacionPictogramaPlan.id_pictograma = CombinedPictograms.id " +
        "INNER JOIN Planificacion ON Planificacion.id = RelacionPictogramaPlan.id_plan " +
        "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_pictograma = CombinedPictograms.id " +
        "LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = ? " +
        "WHERE Planificacion.id = ? " +
        "ORDER BY RelacionPictogramaPlan.id",
            arrayOf(idUsuario, language, id.toString())
        )
    }

    /*Actualizar una planificacion*/
    fun actualizarPlanificacion(id: Int, actual: Int) {
        db!!.execSQL("UPDATE Planificacion SET es_actual ='$actual' WHERE id ='$id'")
    }

    fun obtenerPictograma(idPicto: String?, language: String): Cursor{
       return db!!.rawQuery("SELECT CombinedPictograms.id, CombinedPictograms.imagen, CombinedPictograms.id_API, " +
               "COALESCE(CombinedPictograms.id_categoria_global, CombinedPictograms.id_categoria_local) AS id_categoria, " +
               "COALESCE(Traduccion.translation, CombinedPictograms.nombre) AS nombre " +
               " FROM (" +
                    "SELECT Pictograma.id, Pictograma.nombre, PictogramaLocal.imagen, NULL as id_API, Pictograma.id_categoria_global, Pictograma.id_categoria_local " +
                "  FROM Pictograma " +
                    "  LEFT JOIN PictogramaLocal ON Pictograma.id = PictogramaLocal.id " +
                    "   UNION ALL " +
                    "   SELECT Pictograma.id, Pictograma.nombre, NULL as imagen, PictogramaAPI.id_API, Pictograma.id_categoria_global, Pictograma.id_categoria_local " +
                    "    FROM Pictograma " +
                    "     INNER JOIN PictogramaAPI ON Pictograma.id = PictogramaAPI.id " +
                    "   ) AS CombinedPictograms " +
                "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_pictograma = CombinedPictograms.id " +
                "LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = '$language' " +
                "WHERE CombinedPictograms.id = '$idPicto'", null)
    }

    fun obtenerPlanificacion(idEvento: String, idUsuario: String): Cursor {
        return db!!.rawQuery("SELECT id_plan FROM Evento " +
                "INNER JOIN RelacionEventoPlan ON RelacionEventoPlan.id_evento = Evento.id " +
                "INNER JOIN Planificacion ON Planificacion.id = RelacionEventoPlan.id_plan " +
                "WHERE Evento.id = '$idEvento' AND Evento.id_usuario = '$idUsuario' AND Evento.visible = 1", null)
    }

    /*Obtener el numero de planficaciones visibles*/
    fun contarEventoVisible(idUsuario: String, fecha: String): Cursor {
        return db!!.rawQuery("SELECT id FROM Evento WHERE visible = 1 AND id_usuario = '$idUsuario' AND fecha = '$fecha'", null)
    }

    fun listarTituloEvento(idUsuario: String, fecha: String): Cursor {
        return db!!.rawQuery("SELECT id, nombre from Evento where Evento.visible = 1 AND Evento.id_usuario = '$idUsuario' AND Evento.fecha = '$fecha'", null)
    }

    fun insertarCategoria(nombre: String?, imagen: ByteArray?, color: String, idUsuario: String): Long {
        val categoriaValues = ContentValues().apply {
            put("titulo", nombre)
            put("imagen", imagen)
            put("color", color)
            put("id_usuario", idUsuario)
        }
        return db!!.insert("Categoria", null, categoriaValues)
    }

    fun eliminarCategoria(idUsuario: String, idCategoria: Int) {
        val cursor = db!!.rawQuery("SELECT id FROM Categoria WHERE id = '$idCategoria' and id_usuario IS NULL", null)
        if (cursor.moveToFirst()) {
            db!!.execSQL("INSERT INTO CategoriaOculta (id_usuario, id_categoria) VALUES ('$idUsuario', '$idCategoria')")
            db!!.execSQL("DELETE FROM CategoriaUsuario WHERE id = '$idCategoria' AND id_usuario = '$idUsuario'")
        }else{
            db!!.execSQL("DELETE FROM Categoria WHERE id = '$idCategoria' AND id_usuario = '$idUsuario'")
        }
        cursor.close()
    }

    fun duplicateCategoria(idUsuario: String, idCategoria: Int): Int {
        //if the category is not duplicated yet we duplicate it, if it is duplicated we get the id of the duplicated category
        val cursor = db!!.rawQuery("SELECT id FROM CategoriaUsuario WHERE id_categoria = '$idCategoria' AND id_usuario = '$idUsuario'", null)
        return if (cursor.moveToFirst()) {
            cursor.getInt(0)
        }else{
            db!!.execSQL("INSERT INTO CategoriaUsuario (id_usuario, id_categoria) VALUES ('$idUsuario', '$idCategoria')")
            val c = db!!.rawQuery("SELECT last_insert_rowid()", null)
            var id = 0
            if (c.moveToFirst()) {
                id = c.getInt(0)
            }
            cursor.close()
            c.close()
            id
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

    fun listarCategoriasPrincipales(idUsuario: String?, language: String): Cursor {
        return db!!.rawQuery(
            "SELECT Categoria.id, " +
                    "COALESCE(Traduccion.translation, Categoria.titulo) AS titulo, " +
                    "Categoria.imagen, Categoria.color " +
                    "FROM Categoria " +
                    "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_categoria = Categoria.id " +
                    "LEFT JOIN Traduccion ON RelacionPictoTraduccion.id_traduccion = Traduccion.id " +
                    "AND Traduccion.language = ? " +
                    "WHERE Categoria.id NOT IN (SELECT id_categoria FROM CategoriaOculta WHERE id_usuario = ?) AND Categoria.id_usuario IS NULL OR Categoria.id_usuario = ?",
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
        //nuevoUsuario.put("objeto", objeto)
        //nuevoUsuario.put("nameTEA", nameTEA)

        return try{
            val resultado = db?.insert("Usuario", null, nuevoUsuario) ?: -1
            resultado != -1L
        }catch (e: Exception){
            Log.d("TAG", "El usuario ya existe")
            false
        }
    }

    fun insertarUsuarioTEA(name: String?, imagen: String?, configPicto: String?, idUsuario: String?): String {
        val values = ContentValues().apply {
            put("name", name)
            put("imagen", imagen)
            put("id_usuario", idUsuario)
            /*put("objeto", nameObjeto)
            put("imagenObjeto", imagenObjeto)*/
            put("configPictogramas", configPicto)
        }

        return try{
            db?.insert("UsuarioTEA", null, values)
            val c = db!!.rawQuery("SELECT last_insert_rowid()", null)
            var id = 0
            if (c.moveToFirst()) {
                id = c.getInt(0)
            }
            c.close()
            id.toString()
        }catch (e: Exception){
            Log.d("TAG", "El usuario ya existe")
            ""
        }


    }

    /*Insertar una actividad en la tabla de objetos*/
    fun insertarActividad(name: String?, imagen: String?, idUsuarioTEA: String?): String? {
        val values = ContentValues().apply {
            put("name", name)
            put("imagen", imagen)
            put("id_usuario", idUsuarioTEA)
        }
        return try{
            (db?.insert("Actividad", null, values) ?: -1).toInt()
            val c = db!!.rawQuery("SELECT last_insert_rowid()", null)
            var id = ""
            if (c.moveToFirst()) {
                id = c.getString(0)
            }
            c.close()
            id
        }catch (e: Exception){
            Log.d("TAG", "La actividad ya existe")
            null
        }
    }

    /*Borrar una actividad*/
    fun borrarActividad(idActividad: String?): Boolean {
        return try{
            db!!.execSQL("DELETE FROM Actividad WHERE id='$idActividad'")
            true
        }catch (e: Exception){
            Log.d("TAG", "Error al borrar la actividad")
            false
        }
    }

    /*Actualizar una actividad*/
    fun actualizarActividad(idActividad: String?, name: String?, imagen: String?): Boolean {
        return try{
            db!!.execSQL("UPDATE Actividad SET name = '$name', imagen = '$imagen' WHERE id = '$idActividad'")
            true
        }catch (e: Exception){
            Log.d("TAG", "Error al actualizar la actividad")
            false
        }
    }

    /*Obtener las actividades de un usuario*/
    fun getActividades(idUsuario: String?): ArrayList<Actividad>? {
        val actividades = ArrayList<Actividad>()
        val cursor = db!!.rawQuery("SELECT * FROM Actividad WHERE id_usuario = '$idUsuario'", null)
        if (cursor.moveToFirst()) {
            do {
                val actividad = Actividad()
                actividad.id = cursor.getString(0)
                actividad.name = cursor.getString(1)
                actividad.imagen = cursor.getString(2)
                actividad.idUsuario = cursor.getString(3)
                actividades.add(actividad)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return actividades
    }

    /*Cambiar contraseña del usuario*/
    fun actualizarPass(idUsuario:String, passNueva: String): Boolean {
        db!!.execSQL("UPDATE Usuario SET password =? WHERE Usuario.id = ?", arrayOf(passNueva, idUsuario))
        return true
    }

    /*Insertar nueva cita en la tabla eventos*/
    fun insertarEvento(idUsuario: String?, nombre: String?, fecha: String, hora: String?, reminder: String?, changeVisibility: Boolean): Int {
        var id = 0
        db!!.execSQL("INSERT INTO Evento (id_usuario, nombre, fecha, hora, visible, reminder, change_visibility) VALUES ('$idUsuario', '$nombre','$fecha','$hora', 0, '$reminder', '$changeVisibility')")
        val c = db!!.rawQuery("SELECT last_insert_rowid()", null)
        if (c.moveToFirst()) {
            id = c.getInt(0)
        }
        c.close()
        return id
    }

    fun modificarEvento(id: Int, nombre: String?, fecha: String, hora: String?, changeVisibility: Boolean, reminder: String?, idPlan: Int?): Int {
        db!!.execSQL("UPDATE Evento SET nombre = '$nombre', fecha = '$fecha', hora = '$hora', change_visibility = '$changeVisibility', reminder = '$reminder' WHERE id = '$id'")
        db!!.execSQL("UPDATE RelacionEventoPlan SET id_plan = '$idPlan' WHERE id_evento = '$id'")

        return id
    }

    fun insertarCitaEvento(idEvento: Int, idPlan: Int){
        db!!.execSQL("INSERT INTO RelacionEventoPlan (id_evento, id_plan) VALUES ('$idEvento', '$idPlan')")
    }

    fun listarEventosPorUsuario(idUsuario: String): Cursor {
        val selectionArgs = arrayOf(idUsuario)
        return db!!.rawQuery(
            "SELECT Evento.id, Evento.id_usuario, Evento.nombre, Evento.fecha, Evento.hora, RelacionEventoPlan.id_plan, Evento.visible, Evento.reminder, Evento.change_visibility " +
                    "FROM Evento " +
                    "INNER JOIN RelacionEventoPlan ON Evento.id = RelacionEventoPlan.id_evento " +
                    "WHERE Evento.id_usuario = ?",
            selectionArgs
        )
    }

    fun obtenerInfoEvento(idEvento: Int): Cursor {
        return db!!.rawQuery("SELECT Evento.*, RelacionEventoPlan.id_plan FROM Evento JOIN RelacionEventoPlan ON Evento.id = RelacionEventoPlan.id_evento WHERE Evento.id = ?", arrayOf(idEvento.toString()))
    }

    /*Eliminar evento*/
    fun eliminarEvento(id: Int) {
        db!!.execSQL("DELETE FROM Evento WHERE id='$id'")
        db!!.execSQL("DELETE FROM RelacionEventoPlan WHERE id_evento='$id'")
    }

    @SuppressLint("Range")
    /*Obtenemos los usuarios existentes con el email que tenemos*/
    fun obtenerUsuarioExistente(email: String): Usuario {
        val cursor = db!!.rawQuery("SELECT * from Usuario WHERE Usuario.email = '$email'", null)
        val usuario: Usuario?
        cursor.moveToFirst()

        val username = cursor.getString(cursor.getColumnIndex("username"))
        val name = cursor.getString(cursor.getColumnIndex("name"))
       // val objeto = cursor.getString(cursor.getColumnIndex("objeto"))
        val imagen = cursor.getString(cursor.getColumnIndex("imagen"))
        //val nameTEA = cursor.getString(cursor.getColumnIndex("nameTEA"))
        // val imagenTEA = cursor.getString(cursor.getColumnIndex("imagenTEA"))
        //val imagenObjeto = cursor.getString(cursor.getColumnIndex("imagenObjeto"))
       // val configPicto = cursor.getString(cursor.getColumnIndex("configPictogramas"))
        //usuario = Usuario(name, email, username, objeto, imagen, nameTEA, imagenTEA, imagenObjeto, configPicto)
        usuario = Usuario(name, email, username, imagen)

        cursor.close()
        return usuario
    }

    fun addImagen(image: String, idUsuario: String) {
        db!!.execSQL("UPDATE Usuario SET imagen ='$image' WHERE Usuario.id = '$idUsuario'")
    }

    /*fun addImagenTEA(image: String, idUsuario: String) {
        db!!.execSQL("UPDATE Usuario SET imagenTEA ='$image' WHERE Usuario.id = '$idUsuario'")
    }*/


    /*fun addImagenObjeto(image: String, idUsuario: String) {
        db!!.execSQL("UPDATE Usuario SET imagenObjeto ='$image' WHERE Usuario.id = '$idUsuario'")
    }*/

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

    fun insertarFavorito(idUsuario: String?, id: String?, nombre: String?, idAPI: Int) {
        //Comprbamos si es un pictograma de la API o no. Si es de la API, vemos si ya lo tenemos guardado. Si no esta guardado lo guardamos

        if (idAPI != 0) {
            val cursor = db!!.rawQuery("SELECT id FROM PictogramaAPI WHERE id_API = ?", arrayOf(idAPI.toString()))
            if (cursor.moveToFirst()) {
                val idPicto = cursor.getString(0)
                db!!.execSQL("INSERT OR REPLACE INTO Favorito (id_usuario, id_pictograma) VALUES (?, ?)", arrayOf(idUsuario, idPicto))
                cursor.close()
                return
            }else{
                db!!.execSQL("INSERT OR REPLACE INTO Pictograma (nombre) VALUES (?)", arrayOf(nombre))

                // Retrieve the last inserted row ID from Pictograma table
                val cursor2 = db!!.rawQuery("SELECT last_insert_rowid()", null)
                var lastInsertedId: Long = -1
                if (cursor2.moveToFirst()) {
                    lastInsertedId = cursor2.getLong(0)
                }
                cursor2.close()

                db!!.execSQL("INSERT OR REPLACE INTO PictogramaAPI (id, id_API) VALUES (?, ?)", arrayOf(lastInsertedId, idAPI))
                db!!.execSQL("INSERT INTO Favorito (id_usuario, id_pictograma) VALUES (?, ?)", arrayOf(idUsuario, lastInsertedId))
            }
        }else{
            db!!.execSQL("INSERT INTO Favorito (id_usuario, id_pictograma) VALUES (?, ?)", arrayOf(idUsuario, id))
        }
    }


    fun borrarFavorito(idUsuario: String?, idPicto: String?) {
        db!!.execSQL("DELETE FROM Favorito WHERE id_usuario = ? AND id_pictograma = ?", arrayOf(idUsuario, idPicto))
    }

    fun obtenerFavoritos(idUsuario: String?, language: String): Cursor {
        return db!!.rawQuery(
            "SELECT CombinedPictograms.id, " +
                    "COALESCE(Traduccion.translation, CombinedPictograms.nombre) AS nombre, " +
                    "CombinedPictograms.imagen, CombinedPictograms.id_API " +
                    "FROM (" +
                    "    SELECT Pictograma.id, Pictograma.nombre, PictogramaLocal.imagen, NULL as id_API " +
                    "    FROM Pictograma " +
                    "    INNER JOIN PictogramaLocal ON Pictograma.id = PictogramaLocal.id " +
                    "    UNION " +
                    "    SELECT Pictograma.id, Pictograma.nombre, NULL as imagen, PictogramaAPI.id_API " +
                    "    FROM Pictograma " +
                    "    INNER JOIN PictogramaAPI ON Pictograma.id = PictogramaAPI.id " +
                    ") AS CombinedPictograms " +
                    "INNER JOIN Favorito ON Favorito.id_pictograma = CombinedPictograms.id " +
                    "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_pictograma = CombinedPictograms.id " +
                    "LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = ? " +
                    "WHERE Favorito.id_usuario = ?",
            arrayOf(language, idUsuario)
        )
    }

    fun guardarConfiguracionOLD(nombreUsuarioPlanificador: String, username: String, nombreUsuarioTEA: String, nombreObjeto: String, rutaPlanificador: String, rutaUsuarioTEA: String, rutaObjeto: String, idUsuario: String?) {
        val query = "UPDATE Usuario SET name = '$nombreUsuarioPlanificador', username = '$username', nameTEA = '$nombreUsuarioTEA', objeto = '$nombreObjeto', imagen = '$rutaPlanificador', imagenTEA = '$rutaUsuarioTEA', imagenObjeto = '$rutaObjeto' WHERE id = '$idUsuario'"
        db?.execSQL(query)
    }

    fun guardarConfiguracion(nombreUsuarioPlanificador: String, username: String, ruta: String, idUsuario: String?) {
        val query = "UPDATE Usuario SET name = '$nombreUsuarioPlanificador', username = '$username', imagen = '$ruta' WHERE id = '$idUsuario'"
        db?.execSQL(query)
    }

    fun checkCredentials(email: String, password: String): Boolean {
        val cursor = db!!.rawQuery("SELECT COUNT(*) FROM Usuario WHERE email = ? AND password = ?", arrayOf(email, password))
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count > 0
    }

    fun cambiarConfiguracionPictogramas(configuration: String, idUsuario: String?) {
        db!!.execSQL("UPDATE UsuarioTEA SET configPictogramas = '$configuration' WHERE id = '$idUsuario'")
    }

    fun obtenerUsuariosTEA(idUsuario: String?): Cursor {
        return db!!.rawQuery("SELECT * FROM UsuarioTEA WHERE id_usuario = '$idUsuario'", null)
    }

    fun borrarUsuarioTEA(idUsuario: String?, idUsuarioTEA: String?): Boolean {
        try {
            db!!.execSQL("DELETE FROM UsuarioTEA WHERE id = '$idUsuarioTEA' AND id_usuario = '$idUsuario'")
            db!!.execSQL("DELETE FROM Actividad WHERE id_usuario = '$idUsuarioTEA'")
        } catch (e: SQLiteConstraintException) {
            return false
        }
        return true
    }

    fun guardarConfiguracionUsersTEA(user: Usuario, idUsuario: String?): Boolean {
        val values = ContentValues().apply {
            put("name", user.name)
            put("imagen", user.imagen)
           /* put("objeto", user.objeto)
            put("imagenObjeto", user.imagenObjeto)*/
            put("configPictogramas", user.configPictograma)
            put("id_usuario", idUsuario)
        }

        return try{
            //update
            db?.update("UsuarioTEA", values, "id_usuario = ? AND id = ?", arrayOf(idUsuario, user.id))
            true
        }catch (e: Exception){
            Log.d("TAG", "Algo ha fallado")
            false
        }
    }

    fun getFavorito(idPicto: String?, idUsuario: String?): Boolean {
        val cursor = db!!.rawQuery(
            "SELECT f.id_pictograma FROM Favorito f JOIN PictogramaAPI p ON f.id_pictograma = p.id WHERE p.id_api = ? AND f.id_usuario = ?",
            arrayOf(idPicto, idUsuario)
        )

        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    /*fun addPictogramaAPI(idPicto: String?, idAPI: String?) {
        db!!.execSQL("INSERT OR REPLACE INTO PictogramaAPI (id, id_API) VALUES ('$idPicto', '$idAPI')")
    }*/

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
            "SELECT CombinedPictograms.id, COALESCE(Traduccion.translation, CombinedPictograms.nombre) AS nombre, CombinedPictograms.imagen, CombinedPictograms.id_API " +
                    "FROM (" +
                        "SELECT Pictograma.id, Pictograma.nombre, PictogramaLocal.imagen, NULL as id_API " +
                        "FROM Pictograma " +
                        "INNER JOIN PictogramaLocal ON Pictograma.id = PictogramaLocal.id AND (PictogramaLocal.id_usuario = ? OR PictogramaLocal.id_usuario IS NULL) " +
                        "UNION ALL " +
                        "SELECT Pictograma.id, Pictograma.nombre, NULL as imagen, PictogramaAPI.id_API " +
                        "FROM Pictograma " +
                        "INNER JOIN PictogramaAPI ON Pictograma.id = PictogramaAPI.id) " +
                    "AS CombinedPictograms " +
                        "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_pictograma = CombinedPictograms.id " +
                        "LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = ? " +
                    "ORDER BY RANDOM() LIMIT 6",
            arrayOf(idUsuario, language)
        )
    }

    fun obtenerConfiguracionSemana(idUsuario: String): Cursor {
        return db!!.rawQuery("SELECT configurationWeek FROM Semana WHERE id_usuario = '$idUsuario'", null)
    }

    fun obtenerConfigDias(idUsuario: String, dayWeek: String): Cursor {
        return db!!.rawQuery("SELECT pictograma_day, color, id_evento FROM DiaSemana JOIN Semana ON DiaSemana.semana_id = Semana.id WHERE id_usuario = '$idUsuario' AND day_week = '$dayWeek'", null)
    }


    @SuppressLint("Range")
    fun guardarSemana(idUsuario: String, imagen: ByteArray?, color: String?, dayWeek: String?, idEvento: String?) {
        var semanaId: Int? = null
        val cursorSemana = db!!.rawQuery("SELECT id FROM Semana WHERE id_usuario = ?", arrayOf(idUsuario))
        if (cursorSemana.moveToFirst()) {
            semanaId = cursorSemana.getInt(cursorSemana.getColumnIndex("id"))
        } else {
            db!!.execSQL("INSERT INTO Semana (id_usuario, configurationWeek) VALUES (?, ?)", arrayOf(idUsuario, 1))
            val cursorNewSemana = db!!.rawQuery("SELECT last_insert_rowid() as id", null)
            if (cursorNewSemana.moveToFirst()) {
                semanaId = cursorNewSemana.getInt(cursorNewSemana.getColumnIndex("id"))
            }
            cursorNewSemana.close()
        }
        cursorSemana.close()
        if (semanaId != null) {

            // Step 2: Check if an entry for the given day_week already exists in DiaSemana
            val cursorDiaSemana = db!!.rawQuery("SELECT id FROM DiaSemana WHERE semana_id = ? AND day_week = ?", arrayOf(semanaId.toString(), dayWeek))
            if (cursorDiaSemana.moveToFirst()) {
                db!!.execSQL("UPDATE DiaSemana SET pictograma_day = ?, color = ?, id_evento = ? WHERE semana_id = ? AND day_week = ?", arrayOf(imagen, color, idEvento, semanaId.toString(), dayWeek))
            } else {
                db!!.execSQL("INSERT INTO DiaSemana (semana_id, day_week, pictograma_day, color, id_evento) VALUES (?, ?, ?, ?, ?)", arrayOf(semanaId.toString(), dayWeek, imagen, color, idEvento))
            }
            cursorDiaSemana.close()
        }
        cursorSemana.close()
    }

    fun borrarImagenSemana(idUsuario: String, dayWeek: String) {
        db!!.execSQL("UPDATE DiaSemana SET pictograma_day = NULL WHERE semana_id = (SELECT id FROM Semana WHERE id_usuario = ?) AND day_week = ?", arrayOf(idUsuario, dayWeek))
    }

    fun borrarColorSemana(idUsuario: String, dayWeek: String) {
        db!!.execSQL("UPDATE DiaSemana SET color = NULL WHERE semana_id = (SELECT id FROM Semana WHERE id_usuario = ?) AND day_week = ?", arrayOf(idUsuario, dayWeek))
    }

    fun guardarConfiguracionWeek(idUsuario: String, configurationWeek: Int){
        val cursor = db!!.rawQuery("SELECT id FROM Semana WHERE id_usuario = ?", arrayOf(idUsuario))
        if (cursor.moveToFirst()) {
            // Step 3: Update the pictograma_day if the entry exists
            db!!.execSQL("UPDATE Semana SET configurationWeek = ? WHERE id_usuario = ?", arrayOf(configurationWeek, idUsuario))
        } else {
            // Step 4: Insert a new entry if it does not exist
            db!!.execSQL("INSERT INTO Semana (configurationWeek, id_usuario) VALUES (?, ?)", arrayOf(configurationWeek, idUsuario))
        }
        cursor.close()
    }

    companion object {
        const val NOMBRE_BD = "PlanTEA"
    }
}