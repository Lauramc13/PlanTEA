package com.example.plantea.dominio

import android.app.Activity
import com.example.plantea.persistencia.ConectorBD

class GestionUsuarios {
    private var conectorBD: ConectorBD? = null
    private var resultado = false

    fun crearUsuario( name: String?, email: String?, username: String?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        resultado = conectorBD!!.insertarUsuario(email, username, name)
        conectorBD!!.cerrar()
        return resultado
    }

    fun crearUsuarioTEA(name:String?, imagen: String?, configPicto: String?, idUsuario: String?, actividad: Activity?): String {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.insertarUsuarioTEA(name, imagen, configPicto,  idUsuario)
        conectorBD!!.cerrar()
        return resultado
    }

    fun actualizarPass(idUsuario:String, passwordNueva: String, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        resultado = conectorBD!!.actualizarPass(idUsuario, passwordNueva)
        conectorBD!!.cerrar()
        return resultado
    }

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

    fun consultarId(email: String,  actividad: Activity?): String? {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val usuarioId = conectorBD!!.consultarId(email)
        conectorBD!!.cerrar()
        return usuarioId
    }

    fun guardarConfiguracion(nombreUsuarioPlanificador: String, username: String, ruta: String, idUsuario: String?, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        //conectorBD!!.guardarConfiguracion(nombreUsuarioPlanificador, username, nombreUsuarioTEA, nombreObjeto, rutaPlanificador, rutaUsuarioTEA, rutaObjeto, idUsuario)
        conectorBD!!.guardarConfiguracion(nombreUsuarioPlanificador, username, ruta, idUsuario)
        conectorBD!!.cerrar()
    }

    fun checkCredentials(email: String, password: String, actividad: Activity?): Boolean{
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.checkCredentials(email, password)
        conectorBD!!.cerrar()
        return resultado
    }

    fun cambiarConfiguracionPictogramas(config: String, idUsuario:String?, actividad: Activity?){
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.cambiarConfiguracionPictogramas(config, idUsuario)
        conectorBD!!.cerrar()
    }

    fun obtenerUsuariosTEA(idUsuario: String?, actividad: Activity): ArrayList<Usuario> {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val usuarios = ArrayList<Usuario>()
        val c = conectorBD!!.obtenerUsuariosTEA(idUsuario)
        if (c.moveToFirst()) {
            do {
                val usuario = Usuario()
                usuario.id = c.getString(0)
                usuario.name = c.getString(1)
                usuario.imagen = c.getString(2)
                usuario.configPictograma = c.getString(3)
                usuario.actividades?.addAll(conectorBD!!.getActividades(usuario.id)?:ArrayList())
                usuarios.add(usuario)

            } while (c.moveToNext())
        }

        conectorBD!!.cerrar()
        return usuarios
    }

    fun borrarUsuarioTEA(idUsuario: String?, idUsuarioTEA: String?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        resultado = conectorBD!!.borrarUsuarioTEA(idUsuario, idUsuarioTEA)
        conectorBD!!.cerrar()
        return resultado
    }

    fun guardarConfiguracionUsersTEA(users: ArrayList<Usuario>, idUsuario: String?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        for (user in users) {
            conectorBD!!.guardarConfiguracionUsersTEA(user, idUsuario)
        }
        conectorBD!!.cerrar()
        return true
    }

}