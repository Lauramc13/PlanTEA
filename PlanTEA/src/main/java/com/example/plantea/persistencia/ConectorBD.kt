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
import com.example.plantea.dominio.objetos.Actividad
import com.example.plantea.dominio.objetos.Usuario
import com.example.plantea.presentacion.actividades.CommonUtils

/*
* Clase que se encarga de la conexión con la base de datos y de realizar las operaciones necesarias.
 */

class ConectorBD(ctx: Context?) {
    private val dbHelper: BDSQLiteHelper = BDSQLiteHelper(ctx, NOMBRE_BD, null, 2)
    private var db: SQLiteDatabase? = null

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

    /**
     * Obtiene el siguiente ID global de la base de datos. Se utiliza para tener un id global entre los usuarios
     * planificadores y los usuarios TEA.
     *
     * @param db Base de datos
     */
    private fun getNextGlobalId(db: SQLiteDatabase): Int {
        val cursor = db.rawQuery("SELECT id FROM GlobalID", null)
        cursor.moveToFirst()
        val id = cursor.getInt(0)
        cursor.close()

        db.execSQL("UPDATE GlobalID SET id = id + 1")
        return id
    }

    /**
     * Obtiene los pictogramas de la categoria especificada.
     *
     * @param categoria ID de la categoria
     * @param userId ID del usuario
     * @param language Idioma actual en la aplicación
     */
    fun listarPictogramasCategoria(categoria: Int, userId: String?, language: String): Cursor {
        return db!!.rawQuery(
            """
        SELECT CombinedPictograms.id,
               COALESCE(Traduccion.translation, CombinedPictograms.nombre) AS nombre,
               CombinedPictograms.imagen,
               CombinedPictograms.id_API,
               COALESCE(CombinedPictograms.id_categoria_global, CombinedPictograms.id_categoria_local) AS id_categoria,
               CASE WHEN Favorito.id_usuario IS NOT NULL THEN 1 ELSE 0 END AS favorito
        FROM (
            SELECT Pictograma.id, Pictograma.nombre, PictogramaLocal.imagen, NULL as id_API,
                   Pictograma.id_categoria_global, Pictograma.id_categoria_local
            FROM Pictograma
            INNER JOIN PictogramaLocal ON Pictograma.id = PictogramaLocal.id
                AND (PictogramaLocal.id_usuario = ? OR PictogramaLocal.id_usuario IS NULL)
            UNION ALL
            SELECT Pictograma.id, Pictograma.nombre, NULL as imagen, PictogramaAPI.id_API,
                   Pictograma.id_categoria_global, Pictograma.id_categoria_local
            FROM Pictograma
            INNER JOIN PictogramaAPI ON Pictograma.id = PictogramaAPI.id
        ) AS CombinedPictograms
        LEFT JOIN Favorito ON Favorito.id_pictograma = CombinedPictograms.id AND Favorito.id_usuario = ?
        LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_pictograma = CombinedPictograms.id
        LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = ?
        LEFT JOIN UsuarioPictograma ON UsuarioPictograma.id_pictograma = CombinedPictograms.id AND UsuarioPictograma.id_usuario = ?
        WHERE (UsuarioPictograma.visible IS NULL OR UsuarioPictograma.visible != 0)
          AND (
              CombinedPictograms.id_categoria_local = ?
              OR (CombinedPictograms.id_categoria_global = ?
                  OR CombinedPictograms.id_categoria_local IN (
                      SELECT id FROM CategoriaUsuario WHERE id_categoria = ? AND id_usuario = ?)
              )
          )
        ORDER BY CombinedPictograms.nombre
        """.trimIndent(),
            arrayOf(userId, userId, language, userId, categoria.toString(), categoria.toString(), categoria.toString(), userId)
        )
    }



    fun listarPictogramasCategoriaOLD(categoria: Int, userId: String?, language: String): Cursor {
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

        db!!.execSQL("INSERT INTO UsuarioPictograma (id_usuario, id_pictograma, visible) VALUES ('$idUsuario', '$id', 1)")

        val values = ContentValues().apply {
            put("id", id)
            put("imagen", imagen)
            put("id_usuario", idUsuario)
        }
        db!!.insert("PictogramaLocal", null, values)

        return id
    }

    fun insertarPictogramaAPI(nombre: String?, idAPI: String?, idCategoria: String?, idUsuario: String?): String{
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
                db!!.execSQL("INSERT INTO UsuarioPictograma (id_usuario, id_pictograma, visible) VALUES ('$idUsuario', '$id', 1)")
                c.close()
                c2.close()
                return id
            }
        }

        c.close()
      return ""
    }

    fun borrarPictograma(id: String?, idUsuario: String?) {
        db!!.execSQL("INSERT INTO UsuarioPictograma (id_usuario, id_pictograma, visible) VALUES ('$idUsuario', '$id', 0)")
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
    fun addPictogramasPlanificacion(idPlan: Int?, idPicto: String?): Boolean {
        val categoriaValues = ContentValues().apply {
            put("id_plan", idPlan)
            put("id_pictograma", idPicto)
            /*put("historia", historiaPicto)
            put("duracion", duracionPicto)
            put("id_picto_entre", pictoEntretenimiento)*/

        }
        return db!!.insert("RelacionPictogramaPlan", null, categoriaValues) > 0
    }

    /*Listar planificaciones disponibles*/
    fun listarPlanificaciones(idUsuario: String): Cursor {
        return db!!.rawQuery("SELECT titulo, id from Planificacion WHERE id_usuario = '$idUsuario' AND (es_actual IS NULL OR es_actual = 0) ", null)
    }

    /*Modificar la visibilidad de un evento para mostrar o no al niño un plan*/
    fun modificarVisibilidad(valor: Int, id: Int?) {
        db!!.execSQL("UPDATE Evento SET visible ='$valor' WHERE id ='$id'")
    }

    @SuppressLint("Range")
    fun invisibiliarEvento(id: Int, idUsuario: String?) {
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

    fun listarPictogramasPlanificacionOLD(id: Int, language: String, idUsuario: String): Cursor {
        return db!!.rawQuery(
            "SELECT CombinedPictograms.id, " +
               "COALESCE(Traduccion.translation, CombinedPictograms.nombre) AS nombre,  " +
               "CombinedPictograms.imagen, CombinedPictograms.id_API, " +
                "CASE WHEN CombinedPictograms.id_categoria_global IS NOT NULL THEN CombinedPictograms.id_categoria_global ELSE CombinedPictograms.id_categoria_local " +
                "END AS id_categoria " +
              /* "RelacionPictogramaPlan.historia,  " +
               "RelacionPictogramaPlan.duracion,  " +
               "RelacionPictogramaPlan.id_picto_entre  " +*/
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

    fun listarPictogramasPlanificacion(idPlan: Int, language: String, idUsuario: String): Cursor {
        return db!!.rawQuery(
            """SELECT CombinedPictograms.id, 
                COALESCE(Traduccion.translation, CombinedPictograms.nombre) AS nombre,  
                CombinedPictograms.imagen, 
                CombinedPictograms.id_API, 
                CASE 
                    WHEN CombinedPictograms.id_categoria_global IS NOT NULL THEN CombinedPictograms.id_categoria_global 
                    ELSE CombinedPictograms.id_categoria_local 
                                END AS id_categoria
                FROM (
                    SELECT Pictograma.id, 
                           Pictograma.nombre, 
                           PictogramaLocal.imagen, 
                           NULL AS id_API, 
                           Pictograma.id_categoria_global, 
                           Pictograma.id_categoria_local
                    FROM Pictograma
                    INNER JOIN PictogramaLocal 
                        ON Pictograma.id = PictogramaLocal.id 
                        AND (PictogramaLocal.id_usuario = ? OR PictogramaLocal.id_usuario IS NULL)

                    UNION ALL
                    
                    SELECT Pictograma.id, 
                           Pictograma.nombre, 
                           NULL AS imagen, 
                           PictogramaAPI.id_API, 
                           Pictograma.id_categoria_global, 
                           Pictograma.id_categoria_local
                    FROM Pictograma
                    INNER JOIN PictogramaAPI 
                        ON Pictograma.id = PictogramaAPI.id
                ) AS CombinedPictograms
                INNER JOIN RelacionPictogramaPlan 
                    ON RelacionPictogramaPlan.id_pictograma = CombinedPictograms.id
                INNER JOIN Planificacion 
                    ON Planificacion.id = RelacionPictogramaPlan.id_plan
                LEFT JOIN RelacionPictoTraduccion 
                    ON RelacionPictoTraduccion.id_pictograma = CombinedPictograms.id
                LEFT JOIN Traduccion 
                    ON Traduccion.id = RelacionPictoTraduccion.id_traduccion 
                    AND Traduccion.language = ?
                WHERE Planificacion.id = ?
                ORDER BY RelacionPictogramaPlan.id""",
            arrayOf(idUsuario, language, idPlan.toString())
        )
    }

    fun listarPictogramasPlanificacionEventoOLD(idPlan: Int?, idEvento: Int?, language: String?, idUsuario: String?): Cursor {
        return db!!.rawQuery(
            """
                -- Get the pictograms of the planification and the event--
                SELECT CombinedPictograms.id, 
                  COALESCE(PictogramaEvento.titulo_alt, Traduccion.translation, CombinedPictograms.nombre) AS nombre,  
                  COALESCE(PictogramaEvento.imagen_alt, CombinedPictograms.imagen) AS imagen, 
                  CombinedPictograms.id_API, 
                  CASE 
                      WHEN CombinedPictograms.id_categoria_global IS NOT NULL THEN CombinedPictograms.id_categoria_global 
                      ELSE CombinedPictograms.id_categoria_local 
                  END AS id_categoria, 
                  PictogramaEvento.historia, 
                  PictogramaEvento.duracion, 
                  PictogramaEvento.is_imprevisto,
                  PictogramaEvento.id_picto_entre,
                  PictogramaEvento.posicion
           FROM (
               SELECT Pictograma.id, 
                      Pictograma.nombre, 
                      PictogramaLocal.imagen, 
                      NULL AS id_API, 
                      Pictograma.id_categoria_global, 
                      Pictograma.id_categoria_local
               FROM Pictograma
               INNER JOIN PictogramaLocal 
                   ON Pictograma.id = PictogramaLocal.id 
                   AND (PictogramaLocal.id_usuario = ? OR PictogramaLocal.id_usuario IS NULL)
               UNION ALL
                SELECT Pictograma.id, 
                      Pictograma.nombre, 
                      NULL AS imagen, 
                      PictogramaAPI.id_API, 
                      Pictograma.id_categoria_global, 
                      Pictograma.id_categoria_local
               FROM Pictograma
               INNER JOIN PictogramaAPI 
                   ON Pictograma.id = PictogramaAPI.id
           ) AS CombinedPictograms

            INNER JOIN RelacionPictogramaPlan ON RelacionPictogramaPlan.id_pictograma = CombinedPictograms.id
            INNER JOIN Planificacion ON Planificacion.id = RelacionPictogramaPlan.id_plan
            INNER JOIN RelacionEventoPlan ON RelacionEventoPlan.id_plan = Planificacion.id
            INNER JOIN PictogramaEvento ON PictogramaEvento.id_evento = RelacionEventoPlan.id_evento AND PictogramaEvento.id_pictograma = CombinedPictograms.id
            LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_pictograma = CombinedPictograms.id
            LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = ?

            WHERE Planificacion.id = ? AND RelacionEventoPlan.id_evento = ?
            GROUP BY  PictogramaEvento.posicion

           ORDER BY PictogramaEvento.posicion
           """,
            arrayOf(idUsuario, language, idPlan.toString(), idEvento.toString())
        )
    }

    fun listarPictogramasPlanificacionEvento(idPlan: Int?, idEvento: Int?, language: String?, idUsuario: String?): Cursor {
        return db!!.rawQuery(
            """
                SELECT CombinedPictograms.id, 
                  COALESCE(PictogramaEvento.titulo_alt, Traduccion.translation, CombinedPictograms.nombre) AS nombre,  
                  COALESCE(PictogramaEvento.imagen_alt, CombinedPictograms.imagen) AS imagen, 
                  CombinedPictograms.id_API, 
                  CASE 
                      WHEN CombinedPictograms.id_categoria_global IS NOT NULL THEN CombinedPictograms.id_categoria_global 
                      ELSE CombinedPictograms.id_categoria_local 
                  END AS id_categoria, 
                  PictogramaEvento.historia, 
                  PictogramaEvento.duracion, 
                  PictogramaEvento.is_imprevisto,
                  PictogramaEvento.id_picto_entre,
                  PictogramaEvento.posicion
           FROM (
               SELECT Pictograma.id, 
                      Pictograma.nombre, 
                      PictogramaLocal.imagen, 
                      NULL AS id_API, 
                      Pictograma.id_categoria_global, 
                      Pictograma.id_categoria_local
               FROM Pictograma
               INNER JOIN PictogramaLocal 
                   ON Pictograma.id = PictogramaLocal.id 
               UNION ALL
                SELECT Pictograma.id, 
                      Pictograma.nombre, 
                      NULL AS imagen, 
                      PictogramaAPI.id_API, 
                      Pictograma.id_categoria_global, 
                      Pictograma.id_categoria_local
               FROM Pictograma
               INNER JOIN PictogramaAPI 
                   ON Pictograma.id = PictogramaAPI.id
           ) AS CombinedPictograms

            LEFT JOIN PictogramaEvento ON PictogramaEvento.id_pictograma = CombinedPictograms.id
            LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_pictograma = CombinedPictograms.id
            LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = ?

            WHERE PictogramaEvento.id_evento = ?

           ORDER BY PictogramaEvento.posicion
           """,
            arrayOf(language,  idEvento.toString())
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

    fun listarEvento(idUsuario: String, fecha: String): Cursor {
        return db!!.rawQuery("SELECT id, nombre, fecha, hora_inicio, hora_fin, localizacion, notas from Evento where Evento.visible = 1 AND Evento.id_usuario = '$idUsuario' AND Evento.fecha = '$fecha'", null)
    }

    fun insertarCategoria(nombre: String?, imagen: ByteArray?, color: String, idUsuario: String): Long {
        val categoriaValues = ContentValues().apply {
            put("titulo", nombre)
            put("imagen", imagen)
            put("color", color)
            put("id_usuario", idUsuario)
        }
        return db!!.insert("Categoria", null, categoriaValues)
        //new categoria
    }

    fun eliminarCategoriaOLD(idUsuario: String, idCategoria: Int) {
        val cursor = db!!.rawQuery("SELECT id FROM Categoria WHERE id = '$idCategoria' and id_usuario IS NULL", null)
        if (cursor.moveToFirst()) {
            db!!.execSQL("INSERT INTO CategoriaOculta (id_usuario, id_categoria) VALUES ('$idUsuario', '$idCategoria')")
            db!!.execSQL("DELETE FROM CategoriaUsuario WHERE id = '$idCategoria' AND id_usuario = '$idUsuario'")
        }else{
            db!!.execSQL("DELETE FROM Categoria WHERE id = '$idCategoria' AND id_usuario = '$idUsuario'")
        }
        cursor.close()
    }

    fun eliminarCategoria(idUsuario: String, idCategoria: Int) {
        val cursor = db!!.rawQuery("SELECT id FROM Categoria WHERE id = '$idCategoria' and id_usuario IS NULL", null) // PARA VER SI ES UNA CATEGORIA PREDETERMINADA
        if (cursor.moveToFirst()) {
            db!!.execSQL("INSERT INTO CategoriaOculta (id_usuario, id_categoria) VALUES ('$idUsuario', '$idCategoria')")
            db!!.execSQL("DELETE FROM CategoriaUsuario WHERE id = '$idCategoria' AND id_usuario = '$idUsuario'")
        }else{
            db!!.execSQL("DELETE FROM Categoria WHERE id = '$idCategoria'")
        }
        cursor.close()
    }

    fun duplicateCategoria(idUsuario: String, idCategoria: Int): Cursor {
        //if the category is not duplicated yet we duplicate it, if it is duplicated we get the id of the duplicated category
        val cursor = db!!.rawQuery("SELECT id FROM CategoriaUsuario WHERE id_categoria = '$idCategoria' AND id_usuario = '$idUsuario'", null)
        return if (cursor.moveToFirst()) {
            cursor
        }else{
            db!!.execSQL("INSERT INTO CategoriaUsuario (id_usuario, id_categoria) VALUES ('$idUsuario', '$idCategoria')")
            val cursor2 = db!!.rawQuery("SELECT last_insert_rowid()", null)
            cursor2
        }
    }

    fun obtenerIdCategoriaOLD(nombre: String?, idUsuario: String?, language: String): Cursor {
        return db!!.rawQuery(
            "SELECT Categoria.id " +
                    "FROM Categoria " +
                    "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_categoria = Categoria.id " +
                    "LEFT JOIN Traduccion ON RelacionPictoTraduccion.id_traduccion = Traduccion.id AND Traduccion.language = ? " +
                    "WHERE COALESCE(Traduccion.translation, Categoria.titulo) = ?",
            arrayOf(language, nombre))
    }

    fun obtenerIdCategoria(nombre: String?, idUsuario: String?, language: String): Cursor {
        return db!!.rawQuery(
            """
        SELECT Categoria.id
        FROM Categoria
        LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_categoria = Categoria.id
        LEFT JOIN Traduccion ON RelacionPictoTraduccion.id_traduccion = Traduccion.id AND Traduccion.language = ?
        WHERE (COALESCE(Traduccion.translation, Categoria.titulo) = ?)
          AND (Categoria.id_usuario IS NULL OR Categoria.id_usuario = ?)
        """.trimIndent(),
            arrayOf(language, nombre, idUsuario)
        )
    }


    fun listarCategoriasPrincipalesOLD(idUsuario: String?, language: String): Cursor {
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

    fun listarCategoriasPrincipales(idUsuarioTEA: String?, idUsuario: String?, language: String): Cursor {
        return db!!.rawQuery(
            "SELECT Categoria.id, " +
                    "COALESCE(Traduccion.translation, Categoria.titulo) AS titulo, " +
                    "Categoria.imagen, Categoria.color " +
                    "FROM Categoria " +
                    "LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_categoria = Categoria.id " +
                    "LEFT JOIN Traduccion ON RelacionPictoTraduccion.id_traduccion = Traduccion.id " +
                    "AND Traduccion.language = ? " +
                    "WHERE Categoria.id NOT IN (SELECT id_categoria FROM CategoriaOculta WHERE id_usuario = ?) " +
                    "AND (Categoria.id_usuario IS NULL OR Categoria.id_usuario = ? OR Categoria.id_usuario = ?)",
            arrayOf(language, idUsuario, idUsuario, idUsuarioTEA)
        )
    }


    /*Insertamos el usuario*/
    fun insertarUsuario(email: String?, name: String?): Boolean {
        //Creamos el registro a insertar como objeto ContentValues
        val nuevoUsuario = ContentValues()
        val id = getNextGlobalId(db!!)
        nuevoUsuario.put("id", id)
        nuevoUsuario.put("email", email)
        nuevoUsuario.put("name", name)

        return try{
            (db?.insert("Usuario", null, nuevoUsuario) ?: -1) != -1L
        }catch (e: Exception){
            Log.d("TAG", "El usuario ya existe")
            false
        }
    }

    fun insertarUsuarioTEA(name: String?, imagen: ByteArray?, configPicto: String?, idUsuario: String?): String {
        val id = getNextGlobalId(db!!)
        val values = ContentValues().apply {
            put("id", id)
            put("name", name)
            put("imagen", imagen)
            put("id_usuario", idUsuario)
            put("configPictogramas", configPicto)
        }

        return try{
            db?.insert("UsuarioTEA", null, values)
            id.toString()

        }catch (e: Exception){
            Log.d("TAG", "El usuario ya existe")
            ""
        }

    }

    /*Insertar una actividad en la tabla de objetos*/
    fun insertarActividad(name: String?, imagen: ByteArray?, idUsuarioTEA: String?): String? {
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
            db!!.execSQL("DELETE FROM RelacionCategoriaActividad WHERE id_actividad='$idActividad'")
            true
        }catch (e: Exception){
            Log.d("TAG", "Error al borrar la actividad")
            false
        }
    }

    /*Actualizar una actividad*/
    fun actualizarActividad(idActividad: String?, name: String?, imagen: ByteArray?): Boolean {
        val values = ContentValues().apply {
            put("name", name)
            put("imagen", imagen)
        }
        return try{
            (db?.update("Actividad", values, "id = ?", arrayOf(idActividad)) ?: -1) != -1

            true
        }catch (e: Exception){
            Log.d("TAG", "Error al actualizar la actividad")
            false
        }
    }

    fun addCategoriaActividad(idActividad: String?, idCategoria: String?): Boolean {
        //update or insert into RelacionCategoriaActividad
        return try{
            db!!.execSQL("INSERT INTO RelacionCategoriaActividad (id_actividad, id_categoria) VALUES ('$idActividad', '$idCategoria')")
            true
        }catch (e: Exception){
            Log.d("TAG", "Error al añadir la categoria a la actividad")
            false
        }
    }

    fun removeCategoriaActividad(idActividad: String?, idCategoria: String?): Boolean {
        return try{
            db!!.execSQL("DELETE FROM RelacionCategoriaActividad WHERE id_actividad = '$idActividad' AND id_categoria = '$idCategoria'")
            true
        }catch (e: Exception){
            Log.d("TAG", "Error al borrar la categoria de la actividad")
            false
        }
    }

    /*Obtener las actividades de un usuario*/
    fun getActividades(idUsuario: String?, idCategoria: String?): Cursor {
        val actividades = ArrayList<Actividad>()
        val cursor = db!!.rawQuery("""
        SELECT a.id, a.name, a.imagen, a.id_usuario, GROUP_CONCAT(rca.id_categoria) AS categorias
        FROM actividad AS a
        INNER JOIN relacionCategoriaActividad AS rca 
        ON a.id = rca.id_actividad
        WHERE a.id IN (SELECT id_actividad FROM relacionCategoriaActividad WHERE id_categoria = ?)
        GROUP BY a.id """.trimIndent(), arrayOf(idCategoria))

        return cursor
    }

    fun getAllActividades(idUsuario: String?): Cursor {
        val cursor = db!!.rawQuery(   """
        SELECT a.id, a.name, a.imagen, a.id_usuario, GROUP_CONCAT(rca.id_categoria) AS categorias
        FROM actividad AS a
        LEFT JOIN relacionCategoriaActividad AS rca 
        ON a.id = rca.id_actividad
        WHERE a.id_usuario = ?
        GROUP BY  a.id
        """.trimIndent(), arrayOf(idUsuario))


        return cursor
    }

    fun getActividadById(idActividad: String?): Cursor {
        return db!!.rawQuery("SELECT * FROM Actividad WHERE id = '$idActividad'", null)
    }

    /*Cambiar contraseña del usuario*/
    fun actualizarPass(idUsuario:String, passNueva: String) {
        db!!.execSQL("UPDATE Usuario SET password =? WHERE Usuario.id = ?", arrayOf(passNueva, idUsuario))
    }


    /*Insertar nueva cita en la tabla eventos*/
    fun insertarEvento(idUsuario: String?, nombre: String?, fecha: String, horaInicio: String?, horaFin:String?, localizacion: String?, notas:String?, changeVisibility: Boolean?): Cursor {
        db!!.execSQL("INSERT INTO Evento (id_usuario, nombre, fecha, hora_inicio, hora_fin, localizacion, notas, visible, change_visibility) VALUES ('$idUsuario', '$nombre','$fecha','$horaInicio', '$horaFin', '$localizacion', '$notas', 0, '$changeVisibility')")
        return  db!!.rawQuery("SELECT last_insert_rowid()", null)
    }

    fun modificarEvento(id: Int?, nombre: String?, fecha: String, horaInicio: String?, horaFin:String?, localizacion: String?, notas:String?, changeVisibility: Boolean?, idPlan: Int?) {
        db!!.execSQL("UPDATE Evento SET nombre = '$nombre', fecha = '$fecha', hora_inicio = '$horaInicio', hora_fin = '$horaFin', localizacion = '$localizacion', notas = '$notas', change_visibility = '$changeVisibility' WHERE id = '$id'")
        db!!.execSQL("UPDATE RelacionEventoPlan SET id_plan = '$idPlan' WHERE id_evento = '$id'")
    }

    fun insertarCitaEvento(idEvento: Int?, idPlan: Int?){
        db!!.execSQL("INSERT INTO RelacionEventoPlan (id_evento, id_plan) VALUES ('$idEvento', '$idPlan')")
    }

    //poblar la tabla PictogramaEvento con la posicion de cada pictograma del idPlan
    fun insertarPictosEvento(idEvento: Int?, idPlan: Int?){
        val cursor = db!!.rawQuery("SELECT id_pictograma FROM RelacionPictogramaPlan WHERE id_plan = '$idPlan'", null)
        var posicion = 0
        if (cursor.moveToFirst()) {
            do {
                db!!.execSQL("INSERT INTO PictogramaEvento (id_evento, id_pictograma, posicion) VALUES ('$idEvento', '${cursor.getString(0)}', '$posicion')")
                posicion++
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    fun modificarTituloPictogramaEvento(posicion: Int?, titulo: String?, idEvento: String?, idPictograma: String?): Boolean {
        return try{
            db!!.execSQL("UPDATE PictogramaEvento SET titulo_alt = '$titulo' WHERE posicion = '$posicion' AND id_evento = '$idEvento' AND id_pictograma = '$idPictograma'")
            true
        }catch (e: Exception){
            Log.d("TAG", "Error al modificar el titulo del pictograma")
            false
        }
    }

    fun eliminarPictoEvento(posicion: Int?, idEvento: String?, idPictograma: String?): Boolean {
        return try{
            db!!.execSQL("DELETE FROM PictogramaEvento WHERE posicion = '$posicion' AND id_evento = '$idEvento' AND id_pictograma = '$idPictograma'")
            true
        }catch (e: Exception){
            Log.d("TAG", "Error al eliminar el pictograma del evento")
            false
        }
    }
    //posicion, picto.duracion, picto.historia, picto.isImprevisto, picto.pictoEntretenimiento, idEvento, picto.id
    fun aniadirImprevisto(posicion: Int?, duracion: String?, historia: String?, isImprevisto: Int, pictoEntretenimiento: Int?, idEvento: String?, idPictograma: String?): Boolean {
        return try{
            db!!.execSQL("UPDATE PictogramaEvento SET posicion = posicion + 1 WHERE posicion >= '$posicion' AND id_evento = '$idEvento'")
            db!!.execSQL("INSERT INTO PictogramaEvento (posicion, duracion, historia, is_imprevisto, id_picto_entre, id_evento, id_pictograma) VALUES ('$posicion', '$duracion', '$historia', '$isImprevisto', '$pictoEntretenimiento', '$idEvento', '$idPictograma')")
            val prePosition = posicion!! - 1
            db!!.execSQL("UPDATE PictogramaEvento SET duracion = NULL, historia = NULL, is_imprevisto = 0, id_picto_entre = NULL WHERE posicion = '$prePosition' AND id_evento = '$idEvento'")
            true
        }catch (e: Exception){
            Log.d("TAG", "Error al añadir el pictograma imprevisto")
            false
        }
    }

    fun actualizarImprevisto(posicion: Int?, idEvento: String?, idPictograma: String?): Boolean {
        return try{
            db!!.execSQL("UPDATE PictogramaEvento SET id_pictograma = '$idPictograma' WHERE posicion = '$posicion' AND id_evento = '$idEvento'")
            true
        }catch (e: Exception){
            Log.d("TAG", "Error al actualizar el pictograma imprevisto")
            false
        }
    }

    fun borrarTodosImprevistos(idEvento: String?): Boolean {
        return try{
            db!!.execSQL("DELETE FROM PictogramaEvento WHERE is_imprevisto = 1 AND id_evento = '$idEvento'")
            true
        }catch (e: Exception){
            Log.d("TAG", "Error al borrar los pictogramas imprevistos")
            false
        }
    }

    fun borrarImprevisto(idEvento: String?, idPictograma: String?, posicion: Int?): Boolean {
        return try{
            db!!.execSQL("DELETE FROM PictogramaEvento WHERE is_imprevisto = 1 AND id_evento = '$idEvento' AND id_pictograma = '$idPictograma' AND posicion = '$posicion'")
            //update la posicion de los pictogramas
            db!!.execSQL("UPDATE PictogramaEvento SET posicion = posicion - 1 WHERE posicion > '$posicion' AND id_evento = '$idEvento'")
            true
        }catch (e: Exception){
            Log.d("TAG", "Error al borrar el pictograma imprevisto")
            false
        }
    }

//    fun actualizarPosicionPictoEvento(posicionNew: Int?, posicionOld: Int?, idEvento: String?, idPictograma: String?): Boolean {
//        return try{
//            db!!.execSQL("UPDATE PictogramaEvento SET posicion = '$posicionNew' WHERE posicion = '$posicionOld' AND id_evento = '$idEvento' AND id_pictograma = '$idPictograma'")
//            true
//        }catch (e: Exception){
//            Log.d("TAG", "Error al actualizar la posicion del pictograma")
//            false
//        }
//    }

    fun listarEventosPorUsuario(idUsuario: String?): Cursor {
        val selectionArgs = arrayOf(idUsuario)
        return db!!.rawQuery(
            "SELECT Evento.id, Evento.id_usuario, Evento.nombre, Evento.fecha, Evento.hora_inicio, Evento.hora_fin, Evento.localizacion, Evento.notas, RelacionEventoPlan.id_plan, Evento.visible, Evento.change_visibility " +
                    "FROM Evento " +
                    "INNER JOIN RelacionEventoPlan ON Evento.id = RelacionEventoPlan.id_evento " +
                    "WHERE Evento.id_usuario = ?",
            selectionArgs
        )
    }

    fun obtenerInfoEvento(idEvento: Int?): Cursor { //CAMBIAR ESTO
        return db!!.rawQuery("SELECT Evento.*, RelacionEventoPlan.id_plan FROM Evento JOIN RelacionEventoPlan ON Evento.id = RelacionEventoPlan.id_evento WHERE Evento.id = ?", arrayOf(idEvento.toString()))
    }

    /*Eliminar evento*/
    fun eliminarEvento(id: Int?) {
        db!!.execSQL("DELETE FROM Evento WHERE id='$id'")
        db!!.execSQL("DELETE FROM RelacionEventoPlan WHERE id_evento='$id'")
        db!!.execSQL("DELETE FROM PictogramaEvento WHERE id_evento='$id'")
    }

    @SuppressLint("Range")
    /*Obtenemos los usuarios existentes con el email que tenemos*/
    fun obtenerUsuarioExistente(email: String): Cursor {
        return db!!.rawQuery("SELECT * from Usuario WHERE Usuario.email = '$email'", null)
    }

    fun addImagen(image: ByteArray?, idUsuario: String) {
        val values = ContentValues().apply {
            put("imagen", image)
        }
        db!!.update("Usuario", values, "id = ?", arrayOf(idUsuario))
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

    fun guardarConfiguracion(nombreUsuarioPlanificador: String, ruta: ByteArray?, idUsuario: String?) {
        val values = ContentValues().apply {
            put("name", nombreUsuarioPlanificador)
            put("imagen", ruta)
        }
        db!!.update("Usuario", values, "id = ?", arrayOf(idUsuario))
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
            put("name", user.name?.uppercase())
            put("imagen", CommonUtils.bitmapToByteArray(user.imagen))
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

    fun getFavorito(idPicto: String?, idUsuario: String?): Cursor {
        return db!!.rawQuery(
            "SELECT f.id_pictograma FROM Favorito f JOIN PictogramaAPI p ON f.id_pictograma = p.id WHERE p.id_api = ? AND f.id_usuario = ?",
            arrayOf(idPicto, idUsuario)
        )
    }

    /*fun addPictogramaAPI(idPicto: String?, idAPI: String?) {
        db!!.execSQL("INSERT OR REPLACE INTO PictogramaAPI (id, id_API) VALUES ('$idPicto', '$idAPI')")
    }*/

    fun checkCategoriaExiste(titulo: String, idUsuario: String, language: String): Cursor {
        return db!!.rawQuery("SELECT COUNT(*) AS count, " +
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

    fun listarPictogramasAleatoriosOLD(idUsuario: String?, language: String): Cursor {
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

    fun listarPictogramasAleatorios(idUsuario: String?, language: String): Cursor {
        return db!!.rawQuery(
            """
        SELECT CombinedPictograms.id,
               COALESCE(Traduccion.translation, CombinedPictograms.nombre) AS nombre,
               CombinedPictograms.imagen,
               CombinedPictograms.id_API
        FROM (
            SELECT Pictograma.id, Pictograma.nombre, PictogramaLocal.imagen, NULL as id_API
            FROM Pictograma
            INNER JOIN PictogramaLocal ON Pictograma.id = PictogramaLocal.id
                AND (PictogramaLocal.id_usuario = ? OR PictogramaLocal.id_usuario IS NULL)
            UNION ALL
            SELECT Pictograma.id, Pictograma.nombre, NULL as imagen, PictogramaAPI.id_API
            FROM Pictograma
            INNER JOIN PictogramaAPI ON Pictograma.id = PictogramaAPI.id
        ) AS CombinedPictograms
        LEFT JOIN RelacionPictoTraduccion ON RelacionPictoTraduccion.id_pictograma = CombinedPictograms.id
        LEFT JOIN Traduccion ON Traduccion.id = RelacionPictoTraduccion.id_traduccion AND Traduccion.language = ?
        LEFT JOIN UsuarioPictograma ON UsuarioPictograma.id_pictograma = CombinedPictograms.id AND UsuarioPictograma.id_usuario = ?
        WHERE UsuarioPictograma.visible IS NULL OR UsuarioPictograma.visible != 0
        ORDER BY RANDOM()
        LIMIT 6
        """.trimIndent(),
            arrayOf(idUsuario, language, idUsuario)
        )
    }


    fun checkPictoAPI(idPicto: Int?): Cursor {
        return db!!.rawQuery("SELECT id FROM PictogramaAPI WHERE id_API = ?", arrayOf(idPicto.toString()))
    }

    fun obtenerConfiguracionSemana(idUsuario: String): Cursor {
        return db!!.rawQuery("SELECT configurationWeek FROM Semana WHERE id_usuario = '$idUsuario'", null)
    }

    fun obtenerColoresHeader(idUsuario: String): Cursor {
        return db!!.rawQuery("SELECT colors FROM Semana WHERE id_usuario = '$idUsuario'", null)
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
        db!!.execSQL("UPDATE DiaSemana SET pictograma_day = NULL, id_evento = NULL WHERE semana_id = (SELECT id FROM Semana WHERE id_usuario = ?) AND day_week = ?", arrayOf(idUsuario, dayWeek))
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

    fun guardarColorsHeader(idUsuario: String, colors: ArrayList<String>){
        val cursor = db!!.rawQuery("SELECT id FROM Semana WHERE id_usuario = ?", arrayOf(idUsuario))
        if (cursor.moveToFirst()) {
            // Step 3: Update the pictograma_day if the entry exists
            db!!.execSQL("UPDATE Semana SET colors = ? WHERE id_usuario = ?", arrayOf(colors.joinToString(), idUsuario))
        } else {
            // Step 4: Insert a new entry if it does not exist
            db!!.execSQL("INSERT INTO Semana (colors, id_usuario) VALUES (?, ?)", arrayOf(colors.joinToString(), idUsuario))
        }
        cursor.close()
    }

    fun borrarEventoSemana(idEvento: String) {
        db!!.execSQL("UPDATE DiaSemana SET id_evento = NULL WHERE id_evento = ?", arrayOf(idEvento))
    }

    fun insertarDuracion(posicion: Int?, idEvento: String, duracion: String?) {
        db!!.execSQL("UPDATE PictogramaEvento SET duracion = ? WHERE id_evento = ? AND posicion = ?", arrayOf(duracion, idEvento, posicion))
    }

    fun insertarHistoria(posicion:Int?, idEvento: String, historia: String?) {
        db!!.execSQL("UPDATE PictogramaEvento SET historia = ? WHERE id_evento = ? AND posicion = ?", arrayOf(historia, idEvento, posicion))
    }

    fun insertarPictoEntretenimiento(posicion: Int?, idEvento: String, idPictoEntre: String?) {
        db!!.execSQL("UPDATE PictogramaEvento SET id_picto_entre = ? WHERE id_evento = ? AND posicion = ?", arrayOf(idPictoEntre, idEvento, posicion))
    }

    //QUERIES DE CALENDARIO MENSUAL
    fun obtenerDiaMes(idUsuario: String, fecha: String?): Cursor {
        return db!!.rawQuery("SELECT titulo, fecha, imagen, color FROM DiaMes WHERE id_usuario = '$idUsuario' AND fecha LIKE '$fecha' ORDER BY fecha", null)
    }

    fun guardarDia(idUsuario: String, titulo: String?,  imagen: ByteArray?, color: String?, fecha: String?){
        val cursor = db!!.rawQuery("SELECT id FROM DiaMes WHERE id_usuario = ? AND fecha = ?", arrayOf(idUsuario, fecha))
        if (cursor.moveToFirst()) {
            // Step 3: Update the pictograma_day if the entry exists
            db!!.execSQL("UPDATE DiaMes SET titulo = ?, imagen = ?, color = ? WHERE id_usuario = ? AND fecha = ?", arrayOf(titulo, imagen, color, idUsuario, fecha))
        } else {
            // Step 4: Insert a new entry if it does not exist
            db!!.execSQL("INSERT INTO DiaMes (id_usuario, fecha, titulo, imagen, color) VALUES (?, ?, ?, ?, ?)", arrayOf(idUsuario, fecha, titulo, imagen, color))
        }
        cursor.close()
    }

    fun borrarImagenDiaMes(idUsuario: String, fecha: String) {
        db!!.execSQL("UPDATE DiaMes SET imagen = NULL WHERE id_usuario = ? AND fecha = ?", arrayOf(idUsuario, fecha))
    }

    fun borrarColorDiaMes(idUsuario: String, fecha: String) {
        db!!.execSQL("UPDATE DiaMes SET color = NULL WHERE id_usuario = ? AND fecha = ?", arrayOf(idUsuario, fecha))
    }

    fun borrarDia(idUsuario: String, fecha: String) {
        db!!.execSQL("DELETE FROM DiaMes WHERE id_usuario = ? AND fecha = ?", arrayOf(idUsuario, fecha))
    }

    fun editarDia(idUsuario: String, titulo: String?, imagen: ByteArray?, color: String?, fechaNueva: String?, fecha: String?) {
        db!!.execSQL("UPDATE DiaMes SET titulo = ?, imagen = ?, color = ?, fecha = ? WHERE id_usuario = ? AND fecha = ?", arrayOf(titulo, imagen, color, fechaNueva, idUsuario, fecha))
    }

    fun crearCategoriaActividad(nombre: String?, idUsuario: String?): Int {
        val values = ContentValues().apply {
            put("titulo", nombre)
            put("id_usuario", idUsuario)
        }

        return try{
            val id = db?.insert("CategoriaActividad", null, values)?.toInt()
            id ?: -1
        }catch (e: Exception){
            Log.d("TAG", "La categoria ya existe")
            -1
        }
    }

    fun borrarCategoriaActividad(idCategoria: String?): Boolean {
        db!!.execSQL("DELETE FROM CategoriaActividad WHERE id = '$idCategoria'")
        db!!.execSQL("DELETE FROM RelacionCategoriaActividad WHERE id_categoria = '$idCategoria'")
        return true
    }

    fun editarCategoriaActividad(idCategoria: String?, nombre: String?): Boolean {
        db!!.execSQL("UPDATE CategoriaActividad SET titulo = '$nombre' WHERE id = '$idCategoria'")
        return true
    }

    fun listarCategoriasActividad(idUsuario: String?): Cursor {
        return db!!.rawQuery("SELECT * FROM CategoriaActividad WHERE id_usuario = '$idUsuario'", null)
    }

    fun obtenerIdCategoriaActividad(nombre: String?, idUsuario: String?): Cursor {
        return db!!.rawQuery("SELECT id FROM CategoriaActividad WHERE titulo = ? AND id_usuario = ?", arrayOf(nombre, idUsuario))
    }

    companion object {
        const val NOMBRE_BD = "PlanTEA"
    }
}