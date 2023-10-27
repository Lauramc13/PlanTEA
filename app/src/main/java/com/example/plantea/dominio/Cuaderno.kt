package com.example.plantea.dominio

import android.app.Activity

class Cuaderno  {
    var id = 0
    var titulo: String? = null
    var imagen: String? = null
    var termometro: Boolean? = null
    var gestorCuadernos = GestionCuadernos()

    constructor()
    constructor(id: Int, titulo: String, imagen: String?,termometro: Boolean) {
        this.id = id
        this.titulo = titulo
        this.imagen = imagen
        this.termometro = termometro
    }

    fun consultarCuadernos(actividad: Activity?, idUsuario: String): ArrayList<Cuaderno> {
        return gestorCuadernos.consultarCuadernos(actividad, idUsuario)
    }

    fun crearCuaderno(actividad: Activity?, idUsuario: String, titulo: String, imagen: String?, termometro: Int): Int {
        return gestorCuadernos.insertarCuaderno(actividad, idUsuario, titulo, imagen, termometro)
    }

  /*  fun editarCuaderno(actividad: Activity?, id: Int) {
        gestorCuadernos.editarCuaderno(actividad, id)
    }*/

    fun eliminarCuaderno(actividad: Activity?, id: Int) {
        gestorCuadernos.eliminarCuaderno(actividad, id)
    }

}