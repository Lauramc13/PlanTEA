package com.example.plantea.persistencia

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
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

    /*Listar pictogramas de una categoria*/
    // fun listarPictogramas(categoria: Int): Cursor {
    //     return db!!.rawQuery("SELECT nombre,imagen,id_categoria from Pictograma Inner JOIN Categorias where Categorias.id = Pictograma.id_categoria AND Categorias.id = $categoria", null)
    // }

    /*Listar pictogramas de una categoria*/
   /* fun listarPictogramasOLD(categoria: Int, userId: String?): Cursor {
        return db!!.rawQuery("SELECT id, nombre, imagen, id_categoria, favorito FROM Pictograma WHERE id_categoria = $categoria AND (id_usuario IS NULL OR id_usuario = $userId)", null)
    }*/

    fun listarPictogramas(categoria: Int, userId: String?): Cursor {
        return db!!.rawQuery("SELECT id, nombre, imagen, id_categoria FROM Pictograma WHERE id_categoria = $categoria AND (id_usuario IS NULL OR id_usuario = $userId)", null)
    }

    fun listarPictogramasPrueba(categoria: Int, userId: String?): Cursor {
        return db!!.rawQuery(
            "SELECT Pictograma.id, Pictograma.nombre, Pictograma.imagen, Pictograma.id_categoria, " +
                    "CASE WHEN Favorito.id_usuario IS NOT NULL THEN 1 ELSE 0 END AS favorito " +
                    "FROM Pictograma " +
                    "LEFT JOIN Favorito ON Favorito.id_pictograma = Pictograma.id AND Favorito.id_usuario = ? " +
                    "WHERE Pictograma.id_categoria = ?",
            arrayOf(userId, categoria.toString())
        )
    }

    /*Insertar un pictograma nuevo*/
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



    /*Insertar pictogramas de una planificacion*/

   /* fun insertarPictogramaPlan(idPicto: Int, idPictoAPI: Int, historia: String?, id_plan: Int): Boolean {
        //Creamos el registro a insertar como objeto ContentValues
        val pictograma = ContentValues()
        pictograma.put("historia", historia)
        pictograma.put("id_plan", id_plan)
        pictograma.put("id_pictograma", idPicto)
        pictograma.put("id_pictogramaAPI", idPictoAPI)
        //Insertamos el registro en la base de datos
        val resultado = db!!.insert("RelacionPictogramaPlan", null, pictograma).toInt()
        return resultado != -1
    }*/

    /*Insertar una nueva planificacion*/
    fun insertarPlanificacion(idUsuario: String, titulo: String?): Int {
        var id = 0
        db!!.execSQL("INSERT INTO Planificacion (titulo, id_usuario) VALUES ('$titulo', '$idUsuario')")
        val c = db!!.rawQuery("SELECT last_insert_rowid()", null)
        if (c.moveToFirst()) {
            id = c.getInt(0)
        }
        return id
    }



    fun addPictogramasPlanificacion(idPlan: Int?, idPicto: String?, idPictoAPI: String?, historiaPicto: String?, duracionPicto: String?): Boolean {
        db!!.execSQL("INSERT INTO RelacionPictogramaPlan (id_plan, id_pictograma, id_pictogramaAPI, historia, duracion) VALUES ('$idPlan', '$idPicto', '$idPictoAPI', '$historiaPicto', '$duracionPicto')")
        return true
    }

    /*Listar planificaciones disponibles*/
    fun listarPlanificaciones(idUsuario: String): Cursor {
        return db!!.rawQuery("SELECT titulo, id from Planificacion WHERE id_usuario = '$idUsuario'", null)
    }

    /*Modificar la visibilidad de un evento para mostrar o no al niño un plan*/
    fun modificarVisibilidad(valor: Int, id: Int) {
        db!!.execSQL("UPDATE Evento SET visible ='$valor' WHERE id ='$id'")
    }

    /*Eliminar una planificacion*/
    fun borrarPlanificacion(id: Int) {
        db!!.execSQL("DELETE FROM Planificacion WHERE id='$id'")
        db!!.execSQL("DELETE FROM RelacionPictogramaPlan WHERE id_plan='$id'")
        db!!.execSQL("DELETE FROM Evento WHERE id_plan='$id'")
    }

    /*Listar pictogramas de una categoria*/
    /*fun listarPictogramasPlanificacionOLD(id: Int): Cursor {
        return db!!.rawQuery("SELECT nombre,imagen,categoria,historia from RelacionPictogramaPlan Inner JOIN Planificacion where Planificacion.id = RelacionPictogramaPlan.id_plan AND Planificacion.id = $id", null)
    }*/

    fun listarPictogramasPlanificacion(id: Int): Cursor {
        return db!!.rawQuery(
            "SELECT CombinedPictograms.id, CombinedPictograms.nombre, CombinedPictograms.imagen, CombinedPictograms.id_categoria, RelacionPictogramaPlan.historia, RelacionPictogramaPlan.duracion " +
                    "FROM (SELECT id, nombre, imagen, id_categoria FROM Pictograma UNION SELECT id, nombre, imagen, NULL AS id_categoria FROM PictogramaAPI) AS CombinedPictograms " +
                    "INNER JOIN RelacionPictogramaPlan ON RelacionPictogramaPlan.id_pictograma = CombinedPictograms.id OR RelacionPictogramaPlan.id_pictogramaAPI = CombinedPictograms.id " +
                    "INNER JOIN Planificacion ON Planificacion.id = RelacionPictogramaPlan.id_plan " +
                    "WHERE Planificacion.id = ?" +
                    "ORDER BY RelacionPictogramaPlan.id",
            arrayOf(id.toString())
        )
    }

    /*Actualizar una planificacion*/
    fun actualizarPlanificacion(id: Int, nombre: String?) {
        db!!.execSQL("UPDATE Planificacion SET titulo ='$nombre' WHERE id ='$id'")
        db!!.execSQL("DELETE FROM RelacionPictogramaPlan WHERE id_plan ='$id'")
    }

    /*Listar pictogramas de un plan a seguir*/

    fun obtenerPlanificacion(idUsuario: String, fecha: String): Cursor {
        return db!!.rawQuery(
            "SELECT CombinedPictograms.nombre, CombinedPictograms.imagen, CombinedPictograms.id_categoria, RelacionPictogramaPlan.historia, RelacionPictogramaPlan.duracion, RelacionPictogramaPlan.id_pictogramaAPI " +
                    "FROM (SELECT id, nombre, imagen, id_categoria FROM Pictograma UNION SELECT id, nombre, imagen, NULL AS id_categoria FROM PictogramaAPI) AS CombinedPictograms " +
                    "INNER JOIN RelacionPictogramaPlan ON RelacionPictogramaPlan.id_pictograma = CombinedPictograms.id OR RelacionPictogramaPlan.id_pictogramaAPI = CombinedPictograms.id " +
                    "INNER JOIN Evento ON RelacionPictogramaPlan.id_plan = Evento.id_plan " +
                    "WHERE Evento.fecha = ? AND Evento.visible = 1 AND Evento.id_usuario = ? " +
                    "ORDER BY RelacionPictogramaPlan.id",
            arrayOf(fecha, idUsuario)
        )
    }

    /*Obtener el numero de planficaciones visibles*/
    fun contarEventoVisible(idUsuario: String, fecha: String): Cursor {
        return db!!.rawQuery("SELECT id FROM Evento WHERE visible = 1 AND id_usuario = '$idUsuario' AND fecha = '$fecha'", null)
    }

    /*Obtener el titulo de la planificacion a seguir*/
    fun listarTituloPlan(idUsuario: String, fecha: String): Cursor {
        return db!!.rawQuery("SELECT titulo from Planificacion Inner JOIN Evento where Evento.id_plan = Planificacion.id AND Evento.visible = 1 AND Evento.id_usuario = '$idUsuario' AND Evento.fecha = '$fecha'", null)
    }

    fun listarTituloEvento(idUsuario: String, fecha: String): Cursor {
        return db!!.rawQuery("SELECT nombre from Evento where Evento.visible = 1 AND Evento.id_usuario = '$idUsuario' AND Evento.fecha = '$fecha'", null)
    }

    /*Insertar una nueva subcategoria*/
    /*fun insertarSubcategoria(nombre: String?, idUsuario: String) {
        db!!.execSQL("INSERT INTO Categoria (titulo) VALUES ('$nombre')")
    }*/

    fun insertarCategoria(nombre: String?, imagen: String?, principal: Int, color: String, idUsuario: String) {
        db!!.execSQL("INSERT INTO Categoria (titulo, imagen, principal, color, id_usuario) VALUES ('$nombre', '$imagen', '$principal', '$color', '$idUsuario')")
    }

    fun eliminarCategoria(idUsuario: String, idCategoria: Int) {
        db!!.execSQL("DELETE FROM Categoria WHERE id = '$idCategoria' AND id_usuario = '$idUsuario'")
    }

    /*Obtener identificador de una categoria*/
    fun obtenerIdCategoria(nombre: String?): Cursor {
        return db!!.rawQuery("SELECT id from Categoria where Categoria.titulo = '$nombre'", null)
    }

    /*Listar todas las categorias*/
    fun listarCategorias(): Cursor {
        return db!!.rawQuery("SELECT titulo from Categoria", null)
    }

    fun listarCategoriasPrincipales(idUsuario: String): Cursor {
        return db!!.rawQuery("SELECT id, titulo, imagen, color from Categoria WHERE principal is true AND (id_usuario = '$idUsuario' OR id_usuario IS NULL)", null)
    }


    /*Insertamos la contraseña del usuario*/
    /*fun insertarPass(pass: String?): Boolean {
        //Creamos el registro a insertar como objeto ContentValues
        val nuevaPass = ContentValues()
        nuevaPass.put("password", pass)
        //Insertamos el registro en la base de datos
        val resultado = db!!.insert("Usuario", null, nuevaPass).toInt()
        return resultado != -1
    }*/

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

    /*Verificar contraseña para login*/
   /* fun consultarPass(email:String?, pass: String?): Boolean {
        var resultado = false
        val c = db!!.rawQuery("SELECT password from Usuario where Usuario.email = '$email'", null)
        if (c.moveToFirst()) {
            resultado = c.getString(0) == pass
        }
        return resultado
    }*/
    /*Cambiar contraseña del usuario*/
    fun actualizarPass(idUsuario:String, passNueva: String): Boolean {
        db!!.execSQL("UPDATE Usuario SET password =? WHERE Usuario.id = ?", arrayOf(passNueva, idUsuario))
        return true
    }

    /*Listar pictogramas para el cuaderno*/
    /*fun listarPictogramasCuadernoOLD(identificador: Int): Cursor {
        return db!!.rawQuery("SELECT nombre, imagen, id_cuaderno from Pictograma where id_cuaderno = $identificador", null)
    }*/


    fun listarPictogramasCuaderno(idCuaderno: Int): Cursor {
        return db!!.rawQuery(
            "SELECT CombinedPictograms.id, CombinedPictograms.nombre, CombinedPictograms.imagen, RelacionPictogramaCuaderno.id_cuaderno, " +
                    "CASE WHEN CombinedPictograms.id IN (SELECT id FROM Pictograma) THEN 0 ELSE 1 END AS sourceAPI " +
                    "FROM (SELECT id, nombre, imagen FROM Pictograma UNION SELECT id, nombre, imagen FROM PictogramaAPI) AS CombinedPictograms " +
                    "INNER JOIN RelacionPictogramaCuaderno ON RelacionPictogramaCuaderno.id_pictograma = CombinedPictograms.id OR RelacionPictogramaCuaderno.id_pictogramaAPI = CombinedPictograms.id " +
                    "INNER JOIN Cuaderno ON Cuaderno.id = RelacionPictogramaCuaderno.id_cuaderno " +
                    "WHERE Cuaderno.id = ? " +
                    "ORDER BY RelacionPictogramaCuaderno.id",
            arrayOf(idCuaderno.toString())
        )
    }


    /*Insertar nueva cita en la tabla eventos*/
    fun insertarCita(idUsuario: String?, nombre: String?, fecha: String, hora: String?, idPlan: Int): Int {
        var id = 0
        db!!.execSQL("INSERT INTO Evento (id_usuario, nombre,fecha,hora,id_plan,visible) VALUES ('$idUsuario', '$nombre','$fecha','$hora', '$idPlan', 0)")
        val c = db!!.rawQuery("SELECT last_insert_rowid()", null)
        if (c.moveToFirst()) {
            id = c.getInt(0)
        }
        return id
    }

    /*Listar eventos*/
    // fun listarEventos(): Cursor {
    //     return db!!.rawQuery("SELECT id, id_usuario, nombre,fecha,hora, id_plan, imagen,visible from Evento", null)
    // }

    /*Listar eventos por usuario*/
    fun listarEventosPorUsuario(idUsuario: String): Cursor {
        val selectionArgs = arrayOf(idUsuario)
        return db!!.rawQuery(
            "SELECT id, id_usuario, nombre, fecha, hora, id_plan, visible " +
            "FROM Evento " +
            "WHERE id_usuario = ?",
            selectionArgs
        )
    }


    /*Eliminar evento*/
    fun eliminarEvento(id: Int) {
        db!!.execSQL("DELETE FROM Evento WHERE id='$id'")
    }

    /*Listar categorias de consulta*/
    fun listarConsulta(identificador: Int): Cursor {
        return db!!.rawQuery("SELECT nombre from Pictograma Inner JOIN Categoria where Categoria.id = Pictograma.id_categoria AND Categoria.id = $identificador", null)
    }

    /*Listar categorias de consulta*/
    /*fun obtenerRutaPictograma(identificador: Int): Cursor {
        return db!!.rawQuery("SELECT imagen from Pictograma WHERE Pictograma.id_categoria = '$identificador'", null)
    }*/

    /*Vemos si existe un usuario con el email y contraseña introducida*/
    /*fun consultarUsuario(email: String, password: String): Boolean {
        val cursor = db!!.rawQuery("SELECT COUNT(*) FROM Usuario WHERE email = ? AND password = ?", arrayOf(email, password))
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count > 0
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
        if (cursor.moveToFirst()) {
            val id = cursor.getString(cursor.getColumnIndex("id"))
            cursor.close()
            return id
        } else {
            cursor.close()
            return null
        }
    }



    /*fun insertarFavoritoOLD(idUsuario: String?, id:String?,  nombre: String?, imagen: String?, id_categoria:Int?) {
        db!!.execSQL("INSERT INTO Pictograma (id_usuario, id_pictoAPI, nombre, imagen, id_categoria, favorito)\n" +
            "VALUES ('$idUsuario', '$id', '$nombre', '$imagen', '$id_categoria', 1)\n" +
            "ON CONFLICT(id_pictoAPI) DO UPDATE SET\n" +
            "    nombre = excluded.nombre,\n" +
            "    imagen = excluded.imagen,\n" +
            "    id_categoria = excluded.id_categoria,\n" +
            "    favorito = 1;")
    }*/

    fun insertarFavorito(idUsuario: String?, id:String?, nombre: String?, imagen: String?, sourceApi: Boolean) {
        db!!.execSQL("INSERT INTO Favorito (id_usuario, id_pictograma) VALUES ('$idUsuario', '$id')")
        if(sourceApi){
            db!!.execSQL("INSERT OR REPLACE INTO PictogramaAPI (id, nombre, imagen) VALUES ('$id', '$nombre', '$imagen')")
        }
    }

    /*fun borrarFavoritoOLD(idUsuario: String?, idPicto: String?) {
        db!!.execSQL("UPDATE Favorito SET favorito = '0' WHERE id_usuario ='$idUsuario' AND id ='$idPicto'")
    }*/

    fun borrarFavorito(idUsuario: String?, idPicto: String?) {
        db!!.execSQL("DELETE FROM Favorito WHERE id_usuario = ? AND id_pictograma = ?", arrayOf(idUsuario, idPicto))
    }

    /*fun obtenerFavoritosOLD(idUsuario: String?): Cursor {
        return db!!.rawQuery("SELECT id, nombre, imagen, id_categoria FROM Pictograma where id_usuario = '$idUsuario' AND favorito ='1'", null)
    }*/

   /* fun obtenerFavoritos(idUsuario: String?): Cursor {
        return db!!.rawQuery(
            "SELECT PictogramaAPI.id, nombre, imagen " +
                    "FROM PictogramaAPI " +
                    "INNER JOIN Favorito ON Favorito.id_pictograma = PictogramaAPI.id " +
                    "WHERE Favorito.id_usuario = ?",
            arrayOf(idUsuario)
        )
    }*/

    fun obtenerFavoritos(idUsuario: String?): Cursor {
        return db!!.rawQuery(
            "SELECT CombinedPictograms.id, CombinedPictograms.nombre, CombinedPictograms.imagen " +
                    "FROM (SELECT id, nombre, imagen FROM Pictograma UNION SELECT id, nombre, imagen FROM PictogramaAPI) AS CombinedPictograms " +
                    "INNER JOIN Favorito ON Favorito.id_pictograma = CombinedPictograms.id " +
                    "WHERE Favorito.id_usuario = ?",
            arrayOf(idUsuario)
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


    /*@SuppressLint("Range")
    fun getFavoritoOLD(idPicto: String?, idUsuario: String?): Boolean {
        val cursor = db!!.rawQuery("SELECT id_pictoAPI FROM Pictograma WHERE id_pictoAPI = '$idPicto' AND id_usuario = '$idUsuario' AND favorito = '1'", null)
        val exists = if (cursor.moveToFirst()) {
            val id = cursor.getString(cursor.getColumnIndex("id_pictoAPI"))
            cursor.close()
            id != null
        } else {
            cursor.close()
            false
        }
        return exists
    }*/

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



    /*fun crearPassword(email: String, passCifrada: String) {
        val query = "UPDATE Usuario SET password = '$passCifrada' WHERE email = '$email'"
        db?.execSQL(query)
    }*/

    fun insertarCuaderno(idUsuario: String, titulo: String, imagen: String?, termometro: Int): Int {
        val statement = db!!.compileStatement("INSERT INTO Cuaderno (titulo, imagen, termometro, id_usuario) VALUES (?, ?, ?, ?)")
        statement.bindString(1, titulo)
        statement.bindString(2, imagen)
        statement.bindLong(3, termometro.toLong())
        statement.bindString(4, idUsuario)
        val insertedId = statement.executeInsert()
        statement.close()
        return insertedId.toInt()
    }

    //CAMBIAR ESTO
    fun insertarPictogramaCuadernoBusqueda(id: String?, titulo: String?, imagen: String?, idCuaderno: Int) {
        db!!.execSQL("INSERT OR REPLACE INTO PictogramaAPI (id, nombre, imagen) VALUES ('$id', '$titulo','$imagen')")
        db!!.execSQL("INSERT OR REPLACE INTO RelacionPictogramaCuaderno (id_pictogramaAPI, id_cuaderno) VALUES ('$id','$idCuaderno')")
    }

    fun insertarPictogramaCuaderno( titulo: String?, imagen: String?, idCuaderno: Int, idUsuario: String): Int {
        db!!.execSQL("INSERT INTO Pictograma (nombre, imagen, id_usuario) VALUES ('$titulo','$imagen', '$idUsuario')")
        val c = db!!.rawQuery("SELECT last_insert_rowid()", null)
        var id = 0
        if (c.moveToFirst()) {
            id = c.getInt(0)
        }
        c.close()

        db!!.execSQL("INSERT OR REPLACE INTO RelacionPictogramaCuaderno (id_pictograma, id_cuaderno) VALUES ('$id','$idCuaderno')")
        return id
    }

    fun borrarPictogramaCuadernoBusqueda(id: String?, idCuaderno: Int) {
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

    fun listarCuadernos(idUsuario: String): Cursor {
        return db!!.rawQuery("SELECT id, titulo, imagen, termometro from Cuaderno WHERE id_usuario = '$idUsuario' OR id_usuario IS NULL", null)
    }

    fun addPictogramaAPI(idPicto: String?, nombre: String?, imagen: String?) {
        db!!.execSQL("INSERT OR REPLACE INTO PictogramaAPI (id, nombre, imagen) VALUES ('$idPicto', '$nombre', '$imagen')")
    }

    fun checkCategoriaExiste(titulo: String, idUsuario: String): Cursor {
        return db!!.rawQuery("SELECT COUNT(*) FROM Categoria WHERE titulo = '$titulo' AND (id_usuario = '$idUsuario' OR id_usuario IS NULL)", null)
    }

    /*  fun obtenerTituloCategoria(idCategoria: Int): Any {
          val c = db!!.rawQuery("SELECT titulo from Categoria where Categoria.id = $idCategoria", null)
          var titulo = ""
          if (c.moveToFirst()) {
              titulo = c.getString(0)
          }
          c.close()
          return titulo
      }*/


    companion object {
        const val NOMBRE_BD = "PlanTEA"
    }
}