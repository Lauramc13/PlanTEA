package com.example.plantea.dominio

import android.app.Activity
import android.graphics.Bitmap

class Usuario {
     var id : String? = null
     var name: String? = null
     var email: String? = null
     var username:String? = null
     var imagen: Bitmap? = null
     var actividades: ArrayList<Actividad>? = ArrayList()
     var configPictograma : String?=null

    private var gestorUsuario = GestionUsuarios()

    constructor()

    constructor(nombre:String?, correo: String?, nombreUsuario:String?, image:Bitmap?){
        name = nombre
        email = correo
        username = nombreUsuario
        imagen = image
    }

    constructor(nombre:String?, image:Bitmap?, actividades: ArrayList<Actividad>, configPicto:String?){
        name = nombre
        imagen = image
        configPictograma = configPicto
        actividades.let { this.actividades?.addAll(it) }
    }

    fun crearUsuario(name:String?, email: String?, username: String?, actividad: Activity?): Boolean {
       return gestorUsuario.crearUsuario(name, email, username, actividad)
    }

    fun crearUsuarioTEA(name:String?, imagen: ByteArray?, configPicto: String?, idUsuario: String?, actividad: Activity?): String {
        return gestorUsuario.crearUsuarioTEA(name, imagen, configPicto,  idUsuario, actividad)
    }

    fun actualizarPass(idUsuario: String, passwordNueva: String, actividad: Activity?): Boolean {
        return gestorUsuario.actualizarPass(idUsuario, passwordNueva, actividad)
    }
    
    fun obtenerUsuario(email: String, actividad: Activity?): Usuario{
        return gestorUsuario.obtenerUsuario(email, actividad)
    }

    fun aniadirImagenPlanificador(imagen: ByteArray?, idUsuario: String, actividad: Activity?){
        return gestorUsuario.addImagen(imagen, idUsuario, actividad)
    }

    fun consultarId(email: String, actividad: Activity?): String? {
        return gestorUsuario.consultarId(email, actividad)
    }

    fun guardarConfiguracion(nombreUsuarioPlanificador: String, username: String, rutaImagen: ByteArray?, idUsuario:String?, actividad: Activity?) {
        return gestorUsuario.guardarConfiguracion(nombreUsuarioPlanificador, username, rutaImagen, idUsuario, actividad)
    }

    fun checkCredentials(email: String, password: String, actividad: Activity?): Boolean {
        return gestorUsuario.checkCredentials(email, password, actividad)
    }

    fun cambiarConfiguracionPictogramas(config: String, idUsuario: String?, actividad: Activity?){
        return gestorUsuario.cambiarConfiguracionPictogramas(config, idUsuario, actividad)
    }

    fun obtenerUsuariosTEA(idUsuario: String?, actividad: Activity): ArrayList<Usuario> {
        return gestorUsuario.obtenerUsuariosTEA(idUsuario, actividad)
    }

    fun borrarUsuarioTEA(idUsuario: String?, idUsuarioTEA: String?, actividad: Activity?): Boolean {
        return gestorUsuario.borrarUsuarioTEA(idUsuario, idUsuarioTEA, actividad)
    }

    fun guardarConfiguracionUsersTEA(users: ArrayList<Usuario>, idUsuario: String?, actividad: Activity?): Boolean {
        return gestorUsuario.guardarConfiguracionUsersTEA(users, idUsuario, actividad)
    }

}