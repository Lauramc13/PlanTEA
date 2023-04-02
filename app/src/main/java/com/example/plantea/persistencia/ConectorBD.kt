package com.example.plantea.persistencia

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

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
    fun listarPictogramas(categoria: Int): Cursor {
        return db!!.rawQuery("SELECT nombre,imagen,id_categoria from Pictograma Inner JOIN Categorias where Categorias.id = Pictograma.id_categoria AND Categorias.id = $categoria", null)
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
    fun insertarPictogramaPlan(nombre: String?, imagen: String?, categoria: Int, id_plan: Int): Boolean {
        //Creamos el registro a insertar como objeto ContentValues
        val pictograma = ContentValues()
        pictograma.put("nombre", nombre)
        pictograma.put("imagen", imagen)
        pictograma.put("categoria", categoria)
        pictograma.put("id_plan", id_plan)
        //Insertamos el registro en la base de datos
        val resultado = db!!.insert("Pictograma_Plan", null, pictograma).toInt()
        return if (resultado == -1) {
            false
        } else {
            true
        }
    }

    /*Insertar una nueva planificacion*/
    fun insertarPlanificacion(titulo: String?): Int {
        var id = 0
        db!!.execSQL("INSERT INTO Planificacion (titulo) VALUES ('$titulo')")
        val c = db!!.rawQuery("SELECT last_insert_rowid()", null)
        if (c.moveToFirst()) {
            id = c.getInt(0)
        }
        return id
    }

    /*Listar planificaciones disponibles*/
    fun listarPlanificaciones(): Cursor {
        return db!!.rawQuery("SELECT titulo,id from Planificacion", null)
    }

    /*Modificar la visibilidad de un evento para mostrar o no al niño un plan*/
    fun modificarVisibilidad(valor: Int, id: Int) {
        db!!.execSQL("UPDATE Evento SET visible ='$valor' WHERE id ='$id'")
    }

    /*Eliminar una planificacion*/
    fun borrarPlanificacion(id: Int) {
        db!!.execSQL("DELETE FROM Planificacion WHERE id='$id'")
        db!!.execSQL("DELETE FROM Pictograma_Plan WHERE id_plan='$id'")
    }

    /*Listar pictogramas de una categoria*/
    fun listarPictogramasPlanificacion(id: Int): Cursor {
        return db!!.rawQuery("SELECT nombre,imagen,categoria from Pictograma_Plan Inner JOIN Planificacion where Planificacion.id = Pictograma_Plan.id_plan AND Planificacion.id = $id", null)
    }

    /*Actualizar una planificacion*/
    fun actualizarPlanificacion(id: Int, nombre: String?) {
        db!!.execSQL("UPDATE Planificacion SET titulo ='$nombre' WHERE id ='$id'")
        db!!.execSQL("DELETE FROM Pictograma_Plan WHERE id_plan='$id'")
    }

    /*Listar pictogramas de un plan a seguir*/
    fun obtenerPlanficacion(): Cursor {
        return db!!.rawQuery("SELECT Pictograma_Plan.nombre,Pictograma_Plan.imagen,categoria from Pictograma_Plan Inner JOIN Evento where Evento.id_plan = Pictograma_Plan.id_plan AND Evento.visible = 1 ORDER BY Pictograma_Plan.id", null)
    }

    /*Obtener el numero de planficaciones visibles*/
    fun contarEventoVisible(): Cursor {
        return db!!.rawQuery("SELECT count(visible) from Evento where visible = 1", null)
    }

    /*Obtener el titulo de la planificacion a seguir*/
    fun listarTituloPlan(): Cursor {
        return db!!.rawQuery("SELECT titulo from Planificacion Inner JOIN Evento where Evento.id_plan = Planificacion.id AND Evento.visible = 1", null)
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
        return if (resultado == -1) {
            false
        } else {
            true
        }
    }

    /*Insertamos el usuario*/
    fun insertarUsuario(username:String?, name: String?, pass: String?): Boolean {
        //Creamos el registro a insertar como objeto ContentValues
        val nuevoUsuario = ContentValues()
        nuevoUsuario.put("username", username)
        nuevoUsuario.put("name", name)
        nuevoUsuario.put("password", pass)

        //Insertamos el registro en la base de datos
        val resultado = db?.insert("Usuario_Planificador", null, nuevoUsuario) ?: -1

        return resultado != -1L
    }

    /*Verificar contraseña para login*/
    fun consultarPass(pass: String?): Boolean {
        var resultado = false
        val c = db!!.rawQuery("SELECT password from Usuario_Planificador where Usuario_Planificador.id = 1", null)
        if (c.moveToFirst()) {
            resultado = if (c.getString(0) == pass) {
                true
            } else {
                false
            }
        }
        return resultado
    }

    /*Cambiar contraseña del usuario*/
    fun actualizarPass(passNueva: String, passVieja: String): Boolean {
        val actualizado: Boolean
        actualizado = if (consultarPass(passVieja)) {
            db!!.execSQL("UPDATE Usuario_Planificador SET password ='$passNueva' WHERE Usuario_Planificador.id = 1")
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
    fun insertarCita(nombre: String?, fecha: String, hora: String?, id_plan: Int, imagen: String?): Int {
        var id = 0
        db!!.execSQL("INSERT INTO Evento (nombre,fecha,hora,id_plan,imagen,visible) VALUES ('$nombre','$fecha','$hora', '$id_plan', '$imagen',0)")
        val c = db!!.rawQuery("SELECT last_insert_rowid()", null)
        if (c.moveToFirst()) {
            id = c.getInt(0)
        }
        return id
    }

    /*Listar eventos*/
    fun listarEventos(): Cursor {
        return db!!.rawQuery("SELECT id,nombre,fecha,hora, id_plan, imagen,visible from Evento", null)
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
    fun obtenerRutaPictograma(consulta: String?, identificador: Int): Cursor {
        return db!!.rawQuery("SELECT imagen from Pictograma WHERE Pictograma.nombre = '$consulta' AND Pictograma.id_categoria = '$identificador'", null)
    }

    companion object {
        const val NOMBRE_BD = "PlanTEA"
    }
}