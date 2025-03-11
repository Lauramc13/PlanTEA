package com.example.plantea.dominio.gestores

import android.app.Activity
import com.example.plantea.dominio.objetos.Usuario
import com.example.plantea.persistencia.ConectorBD
import com.example.plantea.presentacion.actividades.CommonUtils

class GestionUsuarios {
    private var conectorBD: ConectorBD? = null

    fun crearUsuario( name: String?, email: String?, username: String?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.insertarUsuario(email, username, name)
        conectorBD!!.cerrar()
        return resultado
    }

    fun crearUsuarioTEA(name:String?, imagen: ByteArray?, configPicto: String?, idUsuario: String?, actividad: Activity?): String {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.insertarUsuarioTEA(name, imagen, configPicto,  idUsuario)
        conectorBD!!.cerrar()
        return resultado
    }

    fun actualizarPass(idUsuario:String, passwordNueva: String, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.actualizarPass(idUsuario, passwordNueva)
        conectorBD!!.cerrar()
    }

    fun obtenerUsuario(email: String, actividad: Activity?): Usuario {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val usuario = Usuario()
        val c = conectorBD!!.obtenerUsuarioExistente(email)
        if (c.moveToFirst()) {
            do {
                usuario.id = c.getString(0)
                usuario.email = c.getString(1)
                usuario.username =c.getString(3)
                usuario.name = c.getString(4)
                usuario.imagen = CommonUtils.byteArrayToBitmap(c.getBlob(5))
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()

        return usuario
    }

    fun addImagen(imagen: ByteArray?, idUsuario: String, actividad: Activity?) {
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

    fun guardarConfiguracion(nombreUsuarioPlanificador: String, username: String, ruta: ByteArray?, idUsuario: String?, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
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
                usuario.imagen = CommonUtils.byteArrayToBitmap(c.getBlob(2))
                usuario.configPictograma = c.getString(3)
                val gActividades = GestionActividades()
                usuario.actividades?.addAll(gActividades.getAllActividades(usuario.id, actividad))
                usuarios.add(usuario)

            } while (c.moveToNext())
        }

        conectorBD!!.cerrar()
        return usuarios
    }

    fun borrarUsuarioTEA(idUsuario: String?, idUsuarioTEA: String?, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.borrarUsuarioTEA(idUsuario, idUsuarioTEA)
        conectorBD!!.cerrar()
    }

    fun guardarConfiguracionUsersTEA(users: ArrayList<Usuario>, idUsuario: String?, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        for (user in users) {
            conectorBD!!.guardarConfiguracionUsersTEA(user, idUsuario)
        }
        conectorBD!!.cerrar()
    }

}