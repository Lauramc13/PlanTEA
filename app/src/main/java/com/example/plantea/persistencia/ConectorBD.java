package com.example.plantea.persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.plantea.R;

public class ConectorBD {

    static final String NOMBRE_BD = "PlanTEA";
    private BDSQLiteHelper dbHelper;
    private SQLiteDatabase db;

    /*Constructor*/
    public ConectorBD (Context ctx)
    {
        dbHelper = new BDSQLiteHelper(ctx, NOMBRE_BD, null, 1);
    }

    /*Abre la conexión con la base de datos*/
    public ConectorBD abrir() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    /*Cierra la conexión con la base de datos*/
    public void cerrar()
    {
        if (db != null) db.close();
    }

    /*Listar pictogramas de una categoria*/
    public Cursor listarPictogramas(int categoria)
    {
         return db.rawQuery("SELECT nombre,imagen,id_categoria from Pictograma Inner JOIN Categorias where Categorias.id = Pictograma.id_categoria AND Categorias.id = " +categoria , null);
    }

    /*Insertar un pictograma nuevo*/
    public void insertarPictograma (String nombre, String imagen, String categoria){

        Cursor c = db.rawQuery("SELECT id from Categorias where Categorias.titulo = '"+categoria+"'" , null);
        if (c.moveToFirst()){
            categoria = c.getString(0);
        }
        c.close();
        db.execSQL("INSERT INTO Pictograma (nombre, imagen, id_categoria) VALUES ('"+nombre+"', '"+imagen+"','"+categoria+"')");
    }

    /*Insertar pictogramas de una planificacion*/
    public boolean insertarPictogramaPlan (String nombre, String imagen,int categoria, int id_plan){
        //Creamos el registro a insertar como objeto ContentValues
        ContentValues pictograma= new ContentValues();
        pictograma.put("nombre", nombre);
        pictograma.put("imagen", imagen);
        pictograma.put("categoria", categoria);
        pictograma.put("id_plan", id_plan);
        //Insertamos el registro en la base de datos
        int resultado = (int) db.insert("Pictograma_Plan", null, pictograma);
        if(resultado == -1){
            return false;
        }else {
            return true;
        }
    }

    /*Insertar una nueva planificacion*/
    public int insertarPlanificacion (String titulo) {
        int id=0;
        db.execSQL("INSERT INTO Planificacion (titulo) VALUES ('"+titulo+"')");
        Cursor c = db.rawQuery("SELECT last_insert_rowid()", null);
        if (c.moveToFirst()){
            id = c.getInt(0);
        }
        return id;
    }

    /*Listar planificaciones disponibles*/
    public Cursor listarPlanificaciones()
    {
        return db.rawQuery("SELECT titulo,id from Planificacion" , null);
    }

    /*Modificar la visibilidad de un evento para mostrar o no al niño un plan*/
    public void modificarVisibilidad(int valor, int id)
    {
        db.execSQL("UPDATE Evento SET visible ='"+valor+"' WHERE id ='"+id+"'");
    }

    /*Eliminar una planificacion*/
    public void borrarPlanificacion(int id)
    {
        db.execSQL("DELETE FROM Planificacion WHERE id='"+id+"'");
        db.execSQL("DELETE FROM Pictograma_Plan WHERE id_plan='"+id+"'");
    }

    /*Listar pictogramas de una categoria*/
    public Cursor listarPictogramasPlanificacion(int id)
    {
        return db.rawQuery("SELECT nombre,imagen,categoria from Pictograma_Plan Inner JOIN Planificacion where Planificacion.id = Pictograma_Plan.id_plan AND Planificacion.id = " +id , null);
    }

    /*Actualizar una planificacion*/
    public void actualizarPlanificacion(int id, String nombre)
    {
        db.execSQL("UPDATE Planificacion SET titulo ='"+nombre+"' WHERE id ='"+id+"'");
        db.execSQL("DELETE FROM Pictograma_Plan WHERE id_plan='"+id+"'");
    }

    /*Listar pictogramas de un plan a seguir*/
    public Cursor obtenerPlanficacion()
    {
        return db.rawQuery("SELECT Pictograma_Plan.nombre,Pictograma_Plan.imagen,categoria from Pictograma_Plan Inner JOIN Evento where Evento.id_plan = Pictograma_Plan.id_plan AND Evento.visible = 1 ORDER BY Pictograma_Plan.id"  , null);
    }

    /*Obtener el numero de planficaciones visibles*/
    public Cursor contarEventoVisible()
    {
        return db.rawQuery("SELECT count(visible) from Evento where visible = 1", null);
    }

    /*Obtener el titulo de la planificacion a seguir*/
    public Cursor listarTituloPlan()
    {
        return db.rawQuery("SELECT titulo from Planificacion Inner JOIN Evento where Evento.id_plan = Planificacion.id AND Evento.visible = 1", null);
    }

    /*Insertar una nueva subcategoria*/
    public void insertarSubcategoria (String nombre){
        db.execSQL("INSERT INTO Categorias (titulo) VALUES ('"+nombre+"')");
    }

    /*Obtener identificador de una categoria*/
    public Cursor obtenerIdCategoria(String nombre)
    {
        return db.rawQuery("SELECT id from Categorias where Categorias.titulo = '"+nombre+"'" , null);
    }

    /*Listar todas las categorias*/
    public Cursor listarCategorias()
    {
        return db.rawQuery("SELECT titulo from Categorias", null);
    }


    /*Insertamos la contraseña del usuario*/
    public boolean insertarPass (String pass){
        //Creamos el registro a insertar como objeto ContentValues
        ContentValues nuevaPass= new ContentValues();
        nuevaPass.put("password", pass);
        //Insertamos el registro en la base de datos
        int resultado = (int) db.insert("Usuario_Planificador", null, nuevaPass);
        if(resultado == -1){
            return false;
        }else {
            return true;
        }
    }

    /*Verificar contraseña para login*/
    public boolean consultarPass (String pass){
        boolean resultado = false;
        Cursor c = db.rawQuery("SELECT password from Usuario_Planificador where Usuario_Planificador.id = 1", null);
        if (c.moveToFirst()){
            if(c.getString(0).equals(pass)){
                resultado = true;
            }else{
                resultado = false;
            }
        }
        return resultado;
    }

    /*Cambiar contraseña del usuario*/
    public boolean actualizarPass(String passNueva, String passVieja){
        boolean actualizado;
        if(consultarPass(passVieja)){
            db.execSQL("UPDATE Usuario_Planificador SET password ='"+passNueva+"' WHERE Usuario_Planificador.id = 1");
            actualizado = true;
        }else{
            actualizado = false;
        }
        return actualizado;
    }

    /*Listar pictogramas para el cuaderno*/
    public Cursor listarPictogramasCuaderno(int identificador)
    {
        return db.rawQuery("SELECT nombre,imagen,id_cuaderno from Pictograma Inner JOIN Cuaderno where Cuaderno.id = Pictograma.id_cuaderno AND Cuaderno.id = " +identificador , null);
    }

    /*Insertar nueva cita en la tabla eventos*/
    public int insertarCita (String nombre, String fecha, String hora, int id_plan, String imagen){
        int id=0;
        db.execSQL("INSERT INTO Evento (nombre,fecha,hora,id_plan,imagen,visible) VALUES ('"+nombre+"','"+fecha+"','"+hora+"', '"+id_plan+"', '"+imagen+"',0)");
        Cursor c = db.rawQuery("SELECT last_insert_rowid()", null);
        if (c.moveToFirst()){
            id = c.getInt(0);
        }
        return id;
    }

    /*Listar eventos*/
    public Cursor listarEventos()
    {
        return db.rawQuery("SELECT id,nombre,fecha,hora, id_plan, imagen,visible from Evento", null);
    }

    /*Eliminar evento*/
    public void eliminarEvento(int id)
    {
        db.execSQL("DELETE FROM Evento WHERE id='"+id+"'");
    }

    /*Listar categorias de consulta*/
    public Cursor listarConsulta(int identificador)
    {
        return db.rawQuery("SELECT nombre from Pictograma Inner JOIN Categorias where Categorias.id = Pictograma.id_categoria AND Categorias.id = "+identificador , null);
    }

    /*Listar categorias de consulta*/
    public Cursor obtenerRutaPictograma(String consulta, int identificador)
    {
        return db.rawQuery("SELECT imagen from Pictograma WHERE Pictograma.nombre = '"+consulta+"' AND Pictograma.id_categoria = '"+identificador+"'" , null);
    }
}
