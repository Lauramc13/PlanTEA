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
        dbHelper = BDSQLiteHelper(ctx, NOMBRE_BD, null, 1)
    }

    /*Abre la conexión con la base de datos*/
    @Throws(SQLException::class)
    fun abrir(): ConectorBD {
        db = dbHelper.writableDatabase
        return this
    }

    /*Cierra la conexión con la base de datos*/
    fun cerrar() {
        if (db != null) db!!.close()
    }

    /*Listar pictogramas de una categoria*/
    // fun listarPictogramas(categoria: Int): Cursor {
    //     return db!!.rawQuery("SELECT nombre,imagen,id_categoria from Pictograma Inner JOIN Categorias where Categorias.id = Pictograma.id_categoria AND Categorias.id = $categoria", null)
    // }

    /*Listar pictogramas de una categoria*/
    fun listarPictogramas(categoria: Int, userId: String?): Cursor {
        return db!!.rawQuery("SELECT Pictograma.id, Pictograma.nombre, Pictograma.imagen, Pictograma.id_categoria, \n" +
                "                   CASE WHEN Favorito.id IS NULL THEN 0 ELSE 1 END AS favorito\n" +
                "                   FROM Pictograma\n" +
                "                   LEFT JOIN Favorito ON Pictograma.id = Favorito.id_picto AND Favorito.id_usuario = '$userId'\n" +
                "                   WHERE Pictograma.id_categoria = $categoria", null)
    }

    /*Insertar un pictograma nuevo*/
    fun insertarPictograma(nombre: String?, imagen: String?, categoria: String?) {
        var categoria = categoria
        val c = db!!.rawQuery("SELECT id from Categorias where Categorias.titulo = '$categoria'", null)
        if (c.moveToFirst()) {
            categoria = c.getString(0)
        }
        c.close()
        db!!.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES ('$nombre', '$imagen','$categoria')")
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
    fun obtenerPlanficacion(id_usuario: String): Cursor {
        return db!!.rawQuery("SELECT Pictograma_Plan.nombre,Pictograma_Plan.imagen,categoria, Pictograma_Plan.historia from Pictograma_Plan Inner JOIN Evento where Evento.id_plan = Pictograma_Plan.id_plan AND Evento.visible = 1 AND Evento.id_usuario = '$id_usuario' ORDER BY Pictograma_Plan.id", null)
    }

    /*Obtener el numero de planficaciones visibles*/
    fun contarEventoVisible(id_usuario: String): Cursor {
        return db!!.rawQuery("SELECT count(visible) FROM Evento WHERE visible = 1 AND id_usuario = '$id_usuario'", null)
    }


    /*Obtener el titulo de la planificacion a seguir*/
    fun listarTituloPlan(id_usuario: String): Cursor {
        return db!!.rawQuery("SELECT titulo from Planificacion Inner JOIN Evento where Evento.id_plan = Planificacion.id AND Evento.visible = 1 AND Evento.id_usuario = '$id_usuario'", null)
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
    fun insertarUsuario(username:String?, name: String?, pass: String?, objeto:String?, nameTEA:String?): Boolean {
        //Creamos el registro a insertar como objeto ContentValues
        val nuevoUsuario = ContentValues()
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
    fun consultarPass(username:String?, pass: String?): Boolean {
        var resultado = false
        val c = db!!.rawQuery("SELECT password from Usuario_Planificador where Usuario_Planificador.username = '$username'", null)
        if (c.moveToFirst()) {
            resultado = c.getString(0) == pass
        }
        return resultado
    }
    /*Cambiar contraseña del usuario*/
    fun actualizarPass(username:String, passNueva: String, passVieja: String): Boolean {
        val actualizado: Boolean = if (consultarPass(username, passVieja)) {
            db!!.execSQL("UPDATE Usuario_Planificador SET password ='$passNueva' WHERE Usuario_Planificador.username = '$username'")
            true
        } else {
            false
        }
        return actualizado
    }

    /*Listar pictogramas para el cuaderno*/
    fun listarPictogramasCuaderno(identificador: Int): Cursor {
        return db!!.rawQuery("SELECT nombre,imagen,id_cuaderno from Pictograma Inner JOIN Cuaderno where Cuaderno.id = Pictograma.id_cuaderno AND Cuaderno.id = $identificador", null)
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

    fun consultarUsuario(username: String, password: String): Boolean {
        val cursor = db!!.rawQuery("SELECT COUNT(*) FROM Usuario_Planificador WHERE username = ? AND password = ?", arrayOf(username, password))
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count > 0
    }

    @SuppressLint("Range")
    fun obtenerUsuarioExistente(username: String): Usuario_Planificador {
        val cursor = db!!.rawQuery("SELECT * from Usuario_Planificador WHERE Usuario_Planificador.username = '$username'", null)
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
        usuario = Usuario_Planificador(name, username, password, objeto, imagen, nameTEA, imagenTEA, imagenObjeto)

        cursor.close()
        return usuario
    }

    fun addImagen(image: String, username: String ) {
        db!!.execSQL("UPDATE Usuario_Planificador SET imagen ='$image' WHERE Usuario_Planificador.username = '$username'")
    }

    fun addImagenTEA(image: String, username: String ) {
        db!!.execSQL("UPDATE Usuario_Planificador SET imagenTEA ='$image' WHERE Usuario_Planificador.username = '$username'")
    }


    fun addImagenObjeto(image: String, username: String ) {
        db!!.execSQL("UPDATE Usuario_Planificador SET imagenObjeto ='$image' WHERE Usuario_Planificador.username = '$username'")
    }

    @SuppressLint("Range")
    fun consultarId(username: String): String {
        val cursor = db!!.rawQuery("SELECT id FROM Usuario_Planificador WHERE username = ?", arrayOf(username))
        cursor.moveToFirst()
        val id = cursor.getString(cursor.getColumnIndex("id"))
        cursor.close()
        return id
    }

    fun nuevaHistoria(nombre: String?, historia: String?) {
        db!!.execSQL("UPDATE Pictograma_Plan SET historia ='$historia' WHERE Pictograma_Plan.nombre = '$nombre'")
    }

    fun insertarFavorito(id_usuario: String?, id_picto: String?) {
        db!!.execSQL("INSERT INTO Favorito (id_usuario, id_picto) VALUES ('$id_usuario', '$id_picto')")
    }


    fun borrarFavorito(id_usuario: String?, id_picto: String?) {
        db!!.execSQL("DELETE FROM Favorito WHERE id_usuario ='$id_usuario' AND id_picto ='$id_picto'")
    }

    fun obtenerFavoritos(id_usuario: String?): Cursor {
        return db!!.rawQuery("SELECT Pictograma.id,Pictograma.nombre,Pictograma.imagen,Pictograma.id_categoria from Pictograma Inner JOIN Favorito where Favorito.id_picto = Pictograma.id AND Favorito.id_usuario = '$id_usuario'", null)
    }

    companion object {
        const val NOMBRE_BD = "PlanTEA"
    }
}