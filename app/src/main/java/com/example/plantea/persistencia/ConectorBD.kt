package com.example.plantea.persistencia

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.plantea.dominio.Usuario_Planificador

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
    fun listarPictogramas(categoria: Int, userId: String?): Cursor {
        return db!!.rawQuery("SELECT id, nombre, imagen, id_categoria, favorito FROM Pictograma WHERE id_categoria = $categoria AND (id_usuario IS NULL OR id_usuario = $userId)", null)
    }

    /*Insertar un pictograma nuevo*/
    fun insertarPictograma(nombre: String?, imagen: String?, categoria: String?, idUsuario: String?) {
        var categoria = categoria
        val c = db!!.rawQuery("SELECT id from Categorias where Categorias.titulo = '$categoria'", null)
        if (c.moveToFirst()) {
            categoria = c.getString(0)
        }
        c.close()
        db!!.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria, id_usuario) VALUES ('$nombre', '$imagen','$categoria', '$idUsuario')")
    }

    /*Insertar pictogramas de una planificacion*/
    fun insertarPictogramaPlan(nombre: String?, imagen: String?, categoria: Int, historia: String?, id_plan: Int): Boolean {
        //Creamos el registro a insertar como objeto ContentValues
        val pictograma = ContentValues()
        pictograma.put("nombre", nombre)
        pictograma.put("imagen", imagen)
        pictograma.put("categoria", categoria)
        pictograma.put("historia", historia)
        pictograma.put("id_plan", id_plan)
        //Insertamos el registro en la base de datos
        val resultado = db!!.insert("Pictograma_Plan", null, pictograma).toInt()
        return resultado != -1
    }

    /*Insertar una nueva planificacion*/
    fun insertarPlanificacion(id_usuario: String, titulo: String?): Int {
        var id = 0
        db!!.execSQL("INSERT INTO Planificacion (titulo, id_usuario) VALUES ('$titulo', '$id_usuario')")
        val c = db!!.rawQuery("SELECT last_insert_rowid()", null)
        if (c.moveToFirst()) {
            id = c.getInt(0)
        }
        return id
    }

    /*Listar planificaciones disponibles*/
    fun listarPlanificaciones(id_usuario: String): Cursor {
        return db!!.rawQuery("SELECT titulo, id from Planificacion WHERE id_usuario = '$id_usuario'", null)
    }

    /*Modificar la visibilidad de un evento para mostrar o no al niño un plan*/
    fun modificarVisibilidad(valor: Int, id: Int) {
        db!!.execSQL("UPDATE Evento SET visible ='$valor' WHERE id ='$id'")
    }

    /*Eliminar una planificacion*/
    fun borrarPlanificacion(id: Int) {
        db!!.execSQL("DELETE FROM Planificacion WHERE id='$id'")
        db!!.execSQL("DELETE FROM Pictograma_Plan WHERE id_plan='$id'")
        db!!.execSQL("DELETE FROM Evento WHERE id_plan='$id'")
    }

    /*Listar pictogramas de una categoria*/
    fun listarPictogramasPlanificacion(id: Int): Cursor {
        return db!!.rawQuery("SELECT nombre,imagen,categoria,historia from Pictograma_Plan Inner JOIN Planificacion where Planificacion.id = Pictograma_Plan.id_plan AND Planificacion.id = $id", null)
    }

    /*Actualizar una planificacion*/
    fun actualizarPlanificacion(id: Int, nombre: String?) {
        db!!.execSQL("UPDATE Planificacion SET titulo ='$nombre' WHERE id ='$id'")
        db!!.execSQL("DELETE FROM Pictograma_Plan WHERE id_plan='$id'")
    }

    /*Listar pictogramas de un plan a seguir*/
    fun obtenerPlanficacion(id_usuario: String, fecha: String): Cursor {
        return db!!.rawQuery("SELECT Pictograma_Plan.nombre,Pictograma_Plan.imagen,categoria, Pictograma_Plan.historia from Pictograma_Plan Inner JOIN Evento where Evento.id_plan = Pictograma_Plan.id_plan AND Evento.fecha = '$fecha' AND Evento.visible = 1 AND Evento.id_usuario = '$id_usuario' ORDER BY Pictograma_Plan.id", null)
    }

    /*Obtener el numero de planficaciones visibles*/
    fun contarEventoVisible(id_usuario: String, fecha: String): Cursor {
        return db!!.rawQuery("SELECT count(visible) FROM Evento WHERE visible = 1 AND id_usuario = '$id_usuario' AND fecha = '$fecha'", null)
    }

    /*Obtener el titulo de la planificacion a seguir*/
    fun listarTituloPlan(id_usuario: String, fecha: String): Cursor {
        return db!!.rawQuery("SELECT titulo from Planificacion Inner JOIN Evento where Evento.id_plan = Planificacion.id AND Evento.visible = 1 AND Evento.id_usuario = '$id_usuario' AND Evento.fecha = '$fecha'", null)
    }

    /*Insertar una nueva subcategoria*/
    fun insertarSubcategoria(nombre: String?) {
        db!!.execSQL("INSERT INTO Categorias (titulo) VALUES ('$nombre')")
    }

    /*Obtener identificador de una categoria*/
    fun obtenerIdCategoria(nombre: String?): Cursor {
        return db!!.rawQuery("SELECT id from Categorias where Categorias.titulo = '$nombre'", null)
    }

    /*Listar todas las categorias*/
    fun listarCategorias(): Cursor {
        return db!!.rawQuery("SELECT titulo from Categorias", null)
    }

    /*Insertamos la contraseña del usuario*/
    fun insertarPass(pass: String?): Boolean {
        //Creamos el registro a insertar como objeto ContentValues
        val nuevaPass = ContentValues()
        nuevaPass.put("password", pass)
        //Insertamos el registro en la base de datos
        val resultado = db!!.insert("Usuario_Planificador", null, nuevaPass).toInt()
        return resultado != -1
    }

    /*Insertamos el usuario*/
    fun insertarUsuario(email: String?, username:String?, name: String?, pass: String?, objeto:String?, nameTEA:String?): Boolean {
        //Creamos el registro a insertar como objeto ContentValues
        val nuevoUsuario = ContentValues()
        nuevoUsuario.put("email", email)
        nuevoUsuario.put("username", username)
        nuevoUsuario.put("name", name)
        nuevoUsuario.put("password", pass)
        nuevoUsuario.put("objeto", objeto)
        nuevoUsuario.put("nameTEA", nameTEA)

        var resultado : Long? = null
        //Insertamos el registro en la base de datos
        try{
            resultado = db?.insert("Usuario_Planificador", null, nuevoUsuario) ?: -1
        }catch (e: SQLiteConstraintException){
            Log.d("TAG", "El usuario ya existe")
        }

        return resultado != -1L
    }

    /*Verificar contraseña para login*/
    fun consultarPass(email:String?, pass: String?): Boolean {
        var resultado = false
        val c = db!!.rawQuery("SELECT password from Usuario_Planificador where Usuario_Planificador.email = '$email'", null)
        if (c.moveToFirst()) {
            resultado = c.getString(0) == pass
        }
        return resultado
    }
    /*Cambiar contraseña del usuario*/
    fun actualizarPass(email:String, passVieja: String, passNueva: String): Boolean {
        val actualizado: Boolean = if (consultarPass(email, passVieja)) {
            db!!.execSQL("UPDATE Usuario_Planificador SET password ='$passNueva' WHERE Usuario_Planificador.email = '$email'")
            true
        } else {
            false
        }
        return actualizado
    }

    /*Listar pictogramas para el cuaderno*/
    fun listarPictogramasCuaderno(identificador: Int): Cursor {
        return db!!.rawQuery("SELECT nombre, imagen, id_cuaderno from Pictograma where id_cuaderno = $identificador", null)
    }

    /*Insertar nueva cita en la tabla eventos*/
    fun insertarCita(idUsuario: String?, nombre: String?, fecha: String, hora: String?, id_plan: Int): Int {
        var id = 0
        db!!.execSQL("INSERT INTO Evento (id_usuario, nombre,fecha,hora,id_plan,visible) VALUES ('$idUsuario', '$nombre','$fecha','$hora', '$id_plan',0)")
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
        return db!!.rawQuery("SELECT nombre from Pictograma Inner JOIN Categorias where Categorias.id = Pictograma.id_categoria AND Categorias.id = $identificador", null)
    }

    /*Listar categorias de consulta*/
    fun obtenerRutaPictograma(identificador: Int): Cursor {
        return db!!.rawQuery("SELECT imagen from Pictograma WHERE Pictograma.id_categoria = '$identificador'", null)
    }

    /*Vemos si existe un usuario con el email y contraseña introducida*/
    fun consultarUsuario(email: String, password: String): Boolean {
        val cursor = db!!.rawQuery("SELECT COUNT(*) FROM Usuario_Planificador WHERE email = ? AND password = ?", arrayOf(email, password))
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count > 0
    }

    @SuppressLint("Range")
    /*Obtenemos los usuarios existentes con el email que tenemos*/
    fun obtenerUsuarioExistente(email: String): Usuario_Planificador {
        val cursor = db!!.rawQuery("SELECT * from Usuario_Planificador WHERE Usuario_Planificador.email = '$email'", null)
        val usuario: Usuario_Planificador?
        cursor.moveToFirst()

        val username = cursor.getString(cursor.getColumnIndex("username"))
        val name = cursor.getString(cursor.getColumnIndex("name"))
        val password = cursor.getString(cursor.getColumnIndex("password"))
        val objeto = cursor.getString(cursor.getColumnIndex("objeto"))
        val imagen = cursor.getString(cursor.getColumnIndex("imagen"))
        val nameTEA = cursor.getString(cursor.getColumnIndex("nameTEA"))
        val imagenTEA = cursor.getString(cursor.getColumnIndex("imagenTEA"))
        val imagenObjeto = cursor.getString(cursor.getColumnIndex("imagenObjeto"))
        usuario = Usuario_Planificador(name, email, username, password, objeto, imagen, nameTEA, imagenTEA, imagenObjeto)

        cursor.close()
        return usuario
    }

    
    fun addImagen(image: String, idUsuario: String ) {
        db!!.execSQL("UPDATE Usuario_Planificador SET imagen ='$image' WHERE Usuario_Planificador.id = '$idUsuario'")
    }

    fun addImagenTEA(image: String, idUsuario: String ) {
        db!!.execSQL("UPDATE Usuario_Planificador SET imagenTEA ='$image' WHERE Usuario_Planificador.id = '$idUsuario'")
    }


    fun addImagenObjeto(image: String, idUsuario: String ) {
        db!!.execSQL("UPDATE Usuario_Planificador SET imagenObjeto ='$image' WHERE Usuario_Planificador.id = '$idUsuario'")
    }

    @SuppressLint("Range")
    fun consultarId(email: String): String? {
        val cursor = db!!.rawQuery("SELECT id FROM Usuario_Planificador WHERE email = ?", arrayOf(email))
        if (cursor.moveToFirst()) {
            val id = cursor.getString(cursor.getColumnIndex("id"))
            cursor.close()
            return id
        } else {
            cursor.close()
            return null
        }
    }

    //fun nuevaHistoria(nombre: String?, historia: String?) {
      //  db!!.execSQL("UPDATE Pictograma_Plan SET historia ='$historia' WHERE Pictograma_Plan.nombre = '$nombre'")
    //}

    fun insertarFavorito(id_usuario: String?, id:String?,  nombre: String?, imagen: String?, id_categoria:Int?) {
        db!!.execSQL("INSERT INTO Pictograma (id_usuario, id_pictoAPI, nombre, imagen, id_categoria, favorito)\n" +
            "VALUES ('$id_usuario', '$id', '$nombre', '$imagen', '$id_categoria', 1)\n" +
            "ON CONFLICT(id_pictoAPI) DO UPDATE SET\n" +
            "    nombre = excluded.nombre,\n" +
            "    imagen = excluded.imagen,\n" +
            "    id_categoria = excluded.id_categoria,\n" +
            "    favorito = 1;")
    }

    fun borrarFavorito(id_usuario: String?, idPicto: String?) {
        db!!.execSQL("UPDATE Pictograma SET favorito = '0' WHERE id_usuario ='$id_usuario' AND id ='$idPicto'")
    }

    fun obtenerFavoritos(id_usuario: String?): Cursor {
        return db!!.rawQuery("SELECT id, nombre, imagen, id_categoria FROM Pictograma where id_usuario = '$id_usuario' AND favorito ='1'", null)
    }

    fun guardarConfiguracion(nombreUsuarioPlanificador: String, username: String, nombreUsuarioTEA: String, nombreObjeto: String, rutaPlanificador: String, rutaUsuarioTEA: String, rutaObjeto: String, idUsuario: String?) {
        val query = "UPDATE Usuario_Planificador SET name = '$nombreUsuarioPlanificador', username = '$username', nameTEA = '$nombreUsuarioTEA', objeto = '$nombreObjeto', imagen = '$rutaPlanificador', imagenTEA = '$rutaUsuarioTEA', imagenObjeto = '$rutaObjeto' WHERE id = '$idUsuario'"
        db?.execSQL(query)
    }


    @SuppressLint("Range")
    fun getFavorito(idPicto: String?, idUsuario: String?): Boolean {
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
    }

    fun crearPassword(email: String, passCifrada: String) {
        val query = "UPDATE Usuario_Planificador SET password = '$passCifrada' WHERE email = '$email'"
        db?.execSQL(query)
    }

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

    fun insertarPictogramaCuaderno(id: String?, titulo: String?, imagen: String?, idUsuario: String?, idCuaderno: Int) {
        db!!.execSQL("INSERT OR REPLACE INTO Pictograma (id_pictoAPI, nombre, imagen, id_usuario, id_cuaderno) VALUES ('$id', '$titulo','$imagen','$idUsuario','$idCuaderno')")
    }

    fun borrarCuaderno(idCuaderno: Int) {
        //TODO
    }

    fun listarCuadernos(idUsuario: String): Cursor {
        return db!!.rawQuery("SELECT id, titulo, imagen, termometro from Cuaderno WHERE id_usuario = '$idUsuario' OR id_usuario IS NULL", null)
    }



    companion object {
        const val NOMBRE_BD = "PlanTEA"
    }
}