package com.example.plantea.dominio

import android.app.Activity
import com.example.plantea.persistencia.ConectorBD


class GestionUsuarios {
    private var conectorBD: ConectorBD? = null
    private var resultado = false


    fun crearUsuario( name: String?, email: String?, password: String?, username: String?, objeto:String?, nameTEA:String?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        resultado = conectorBD!!.insertarUsuario(email, password, username, name, objeto, nameTEA)
        conectorBD!!.cerrar()
        return resultado
    }

    /*fun comprobarPassword(email:String, password: String, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        resultado = conectorBD!!.consultarPass(email, password)
        conectorBD!!.cerrar()
        return resultado
    }*/

    fun actualizarPass(idUsuario:String, passwordNueva: String, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        resultado = conectorBD!!.actualizarPass(idUsuario, passwordNueva)
        conectorBD!!.cerrar()
        return resultado
    }

    /*fun comprobarUsuario(email: String, password: String, actividad: Activity?): Boolean? {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        resultado = conectorBD!!.consultarUsuario(email, password)
        conectorBD!!.cerrar()
        return resultado
    }*/

    fun obtenerUsuario(email: String, actividad: Activity?): Usuario {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val usuario = conectorBD!!.obtenerUsuarioExistente(email)
        conectorBD!!.cerrar()
        return usuario
    }

    fun addImagen(imagen: String, idUsuario: String, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val usuario = conectorBD!!.addImagen(imagen, idUsuario)
        conectorBD!!.cerrar()
        return usuario
    }

    fun addImagenTEA(imagen: String, idUsuario: String, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val usuario = conectorBD!!.addImagenTEA(imagen, idUsuario)
        conectorBD!!.cerrar()
        return usuario
    }

    fun addImagenObjeto(imagen: String, idUsuario: String, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val usuario = conectorBD!!.addImagenObjeto(imagen, idUsuario)
        conectorBD!!.cerrar()
        return usuario
    }

    fun consultarId(email: String,  actividad: Activity?): String? {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val usuarioId = conectorBD!!.consultarId(email)
        conectorBD!!.cerrar()
        return usuarioId
    }

    fun guardarConfiguracion(nombreUsuarioPlanificador: String, username: String, nombreUsuarioTEA: String, nombreObjeto: String, rutaPlanificador: String, rutaUsuarioTEA: String, rutaObjeto: String, idUsuario: String?, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.guardarConfiguracion(nombreUsuarioPlanificador, username, nombreUsuarioTEA, nombreObjeto, rutaPlanificador, rutaUsuarioTEA, rutaObjeto, idUsuario)
        conectorBD!!.cerrar()
    }

    fun checkCredentials(email: String, password: String, actividad: Activity?): Boolean{
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.checkCredentials(email, password)
        conectorBD!!.cerrar()
        return resultado
    }

   /* fun crearPassword(email: String, passCifrada: String, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.crearPassword(email, passCifrada)
        conectorBD!!.cerrar()
    }*/

}