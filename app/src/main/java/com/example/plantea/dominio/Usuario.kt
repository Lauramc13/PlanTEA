package com.example.plantea.dominio

import android.app.Activity

class Usuario {
    private var name: String? = null
    private var email: String? = null
    private var username:String? = null
    private var objeto: String?= null
    private var imagen: String?= null
    private var imagenObjeto: String?=null
    private var imagenTEA: String?=null
    private var nameTEA: String?=null
    private var gestorUsuario = GestionUsuarios()
    private var resultado: Boolean? = null

    constructor()
    constructor(nombre:String?, correo: String?, nombreUsuario:String?, objet:String?, image:String?, nombreTEA:String?, imageTEA:String?, imageObjeto:String? ){
        name = nombre
        email = correo
        username = nombreUsuario
        imagen = image
        objeto = objet
        nameTEA = nombreTEA
        imagenTEA = imageTEA
        imagenObjeto = imageObjeto
    }

    // Getters
    fun getName(): String? {
        return name
    }

    fun getEmail(): String? {
        return email
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

    fun getNameTEA(): String? {
        return nameTEA
    }

    fun getImagenTEA(): String?{
        return imagenTEA
    }

    fun getImagenObjeto(): String?{
        return imagenObjeto
    }

    fun crearUsuario(name:String?, email: String?, password: String?, username: String?, objeto: String?, nameTEA:String?, actividad: Activity?): Boolean {
        resultado = gestorUsuario.crearUsuario(name, email, password, username, objeto, nameTEA, actividad)
        return resultado!!
    }

   /* fun comprobarPass(email: String, password: String, actividad: Activity?): Boolean {
        resultado = gestorUsuario.comprobarPassword(email, password, actividad)
        return resultado!!
    }*/

    fun actualizarPass(idUsuario: String, passwordNueva: String, actividad: Activity?): Boolean {
        resultado = gestorUsuario.actualizarPass(idUsuario, passwordNueva, actividad)
        return resultado!!
    }
    
    fun obtenerUsuario(email: String, actividad: Activity?): Usuario{
        return gestorUsuario.obtenerUsuario(email, actividad)
    }

    fun aniadirImagenPlanificador(imagen: String, idUsuario: String, actividad: Activity?){
        return gestorUsuario.addImagen(imagen, idUsuario, actividad)
    }

    fun aniadirImagenPlanificado(imagen: String, idUsuario: String, actividad: Activity?){
        return gestorUsuario.addImagenTEA(imagen, idUsuario, actividad)
    }

    fun aniadirImagenObjeto(imagen: String, idUsuario: String, actividad: Activity?){
        return gestorUsuario.addImagenObjeto(imagen, idUsuario, actividad)
    }

    fun consultarId(email: String, actividad: Activity?): String? {
        return gestorUsuario.consultarId(email, actividad)

    }

    fun guardarConfiguracion(nombreUsuarioPlanificador: String, username: String, nombreUsuarioTEA: String, nombreObjeto: String, rutaPlanificador: String, rutaUsuarioTEA: String, rutaObjeto: String, idUsuario:String?, actividad: Activity?) {
        return gestorUsuario.guardarConfiguracion(nombreUsuarioPlanificador, username, nombreUsuarioTEA, nombreObjeto, rutaPlanificador, rutaUsuarioTEA, rutaObjeto, idUsuario, actividad)
    }

    fun checkCredentials(email: String, password: String, actividad: Activity?): Boolean {
        return gestorUsuario.checkCredentials(email, password, actividad)
    }

}