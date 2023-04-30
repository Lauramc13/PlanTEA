package com.example.plantea.dominio

import android.app.Activity
import com.example.plantea.persistencia.ConectorBD


class GestionUsuarios {
    private var conectorBD: ConectorBD? = null
    private var resultado = false
    fun crearPassword(password: String?, actividad: Activity?): Boolean { //borrar esto TODO
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        resultado = conectorBD!!.insertarPass(password)
        conectorBD!!.cerrar()
        return resultado
    }

    fun crearUsuario( name: String?, username: String?,password: String?, objeto:String?, nameTEA:String?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        resultado = conectorBD!!.insertarUsuario(username, name, password, objeto, nameTEA)
        conectorBD!!.cerrar()
        return resultado
    }

    fun comprobarPassword(username:String, password: String, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        resultado = conectorBD!!.consultarPass(username, password)
        conectorBD!!.cerrar()
        return resultado
    }

    fun cambiarPassword(username:String, passwordNueva: String, passwordVieja: String, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        resultado = conectorBD!!.actualizarPass(username, passwordNueva, passwordVieja)
        conectorBD!!.cerrar()
        return resultado
    }

    fun comprobarUsuario(username: String, password: String, actividad: Activity?): Boolean? {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        resultado = conectorBD!!.consultarUsuario(username, password)
        conectorBD!!.cerrar()
        return resultado
    }

    fun obtenerUsuario(username: String, actividad: Activity?): Usuario_Planificador {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        var usuario = conectorBD!!.obtenerUsuarioExistente(username)
        conectorBD!!.cerrar()
        return usuario
    }

    fun addImagen(imagen: String, username: String, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        var usuario = conectorBD!!.addImagen(imagen, username)
        conectorBD!!.cerrar()
        return usuario
    }

    fun addImagenTEA(imagen: String, username: String, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        var usuario = conectorBD!!.addImagenTEA(imagen, username)
        conectorBD!!.cerrar()
        return usuario
    }

    fun addImagenObjeto(imagen: String, username: String, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        var usuario = conectorBD!!.addImagenObjeto(imagen, username)
        conectorBD!!.cerrar()
        return usuario
    }





}