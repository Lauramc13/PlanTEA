package com.example.plantea.dominio

import android.app.Activity

class Usuario_Planificador {
    private var name: String? = null
    private var username:String? = null
    private var password: String? = null
    private var objeto: String?= null
    private var imagen: String?= null
    private var imagenObjeto: String?=null
    private var gestorUsuario = GestionUsuarios()
    private var resultado: Boolean? = null

    constructor()
    constructor(pass: String?) {
        password = pass
    }

    constructor(nombre:String?, nombreUsuario:String?, pass: String, objet:String?, image:String? ){
        name = nombre
        username = nombreUsuario
        password = pass
        imagen = image
        objeto = objet
    }

    // Getters
    fun getName(): String? {
        return name
    }

    fun getUsername(): String? {
        return username
    }

    fun getImagen(): String? {
        return imagen
    }

    fun getObjeto(): String? {
        return objeto
    }

    fun crearUsuario(name:String?, username: String?, password: String?, objeto: String?, actividad: Activity?): Boolean {
        resultado = gestorUsuario.crearUsuario(name, username, password, objeto, actividad)
        return resultado!!
    }

    fun comprobarPass(username: String, password: String, actividad: Activity?): Boolean {
        resultado = gestorUsuario.comprobarPassword(username, password, actividad)
        return resultado!!
    }

    fun confirmarPass(username: String, passwordVieja: String, passwordNueva: String, passwordConfirma: String, actividad: Activity?): Boolean {
        resultado = if (passwordNueva == passwordConfirma) {
            gestorUsuario.cambiarPassword(username, passwordNueva, passwordVieja, actividad)
        } else {
            false
        }
        return resultado!!
    }

    fun comprobarUsuario(username: String, password: String, actividad: Activity?): Boolean? {
        return gestorUsuario.comprobarUsuario(username, password, actividad)
    }

    fun obtenerUsuario(username: String, actividad: Activity?): Usuario_Planificador{
        return gestorUsuario.obtenerUsuario(username, actividad)
    }

    fun aniadirImagenPlanificador(imagen: String, username: String, actividad: Activity?){
        return gestorUsuario.addImagen(imagen, username, actividad)
    }

    fun aniadirImagenObjeto(imagen: String, username: String, actividad: Activity?){
        return gestorUsuario.addImagenObjeto(imagen, username, actividad)
    }

}