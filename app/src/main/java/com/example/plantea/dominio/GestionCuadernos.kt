package com.example.plantea.dominio

import android.app.Activity
import com.example.plantea.persistencia.ConectorBD

class GestionCuadernos {
    private lateinit var listaCuadernos: ArrayList<Cuaderno>
    private lateinit var conectorBD: ConectorBD

    fun consultarCuadernos(actividad: Activity?, idUsuario: String): ArrayList<Cuaderno> {
        listaCuadernos = ArrayList()
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        val c = conectorBD.listarCuadernos(idUsuario)
        if (c.moveToFirst()) {
            do {
                val cuaderno = Cuaderno()
                cuaderno.id = c.getInt(0)
                cuaderno.titulo = c.getString(1)
                cuaderno.imagen = c.getString(2)
                cuaderno.termometro = c.getInt(3) != 0
                listaCuadernos.add(cuaderno)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD.cerrar()
        return listaCuadernos
    }

    fun insertarCuaderno(actividad: Activity?, idUsuario: String, titulo: String, imagen: String?, termometro: Int): Int{
        //var resultado = false
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        val id = conectorBD.insertarCuaderno(idUsuario, titulo, imagen, termometro)
       /* for (i in pictogramas.indices) {
            resultado = conectorBD.insertarPictogramaCuaderno(pictogramas[i].titulo, pictogramas[i].imagen, pictogramas[i].categoria, pictogramas[i].historia, id_plan)
        }*/
        conectorBD.cerrar()
        return id
    }

    fun eliminarCuaderno(actividad: Activity?, id_cuaderno: Int) {
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        conectorBD.borrarCuaderno(id_cuaderno)
        conectorBD.cerrar()
    }

}