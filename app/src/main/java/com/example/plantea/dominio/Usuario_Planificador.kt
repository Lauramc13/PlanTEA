package com.example.plantea.dominio

import android.app.Activity

class Usuario_Planificador {
    private var name: String? = null
    private var email: String? = null
    private var username:String? = null
    private var password: String? = null
    private var objeto: String?= null
    private var imagen: String?= null
    private var imagenObjeto: String?=null
    private var imagenTEA: String?=null
    private var nameTEA: String?=null
    private var gestorUsuario = GestionUsuarios()
    private var resultado: Boolean? = null

    constructor()
    constructor(pass: String?) {
        password = pass
    }

    constructor(nombre:String?, correo: String?, nombreUsuario:String?, pass: String, objet:String?, image:String?, nombreTEA:String?, imageTEA:String?, imageObjeto:String? ){
        name = nombre
        email = correo
        username = nombreUsuario
        password = pass
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

    fun getPassword(): String?{
        return password
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

    fun crearUsuario(name:String?, email: String?, username: String?, password: String?, objeto: String?, nameTEA:String?, actividad: Activity?): Boolean {
        resultado = gestorUsuario.crearUsuario(name, email, username, password, objeto, nameTEA, actividad)
        return resultado!!
    }

    fun comprobarPass(email: String, password: String, actividad: Activity?): Boolean {
        resultado = gestorUsuario.comprobarPassword(email, password, actividad)
        return resultado!!
    }

    fun confirmarPass(email: String, passwordVieja: String, passwordNueva: String, actividad: Activity?): Boolean {
        resultado = gestorUsuario.cambiarPassword(email, passwordVieja, passwordNueva, actividad)
        return resultado!!
    }

    fun comprobarUsuario(email: String, password: String, actividad: Activity?): Boolean? {
        return gestorUsuario.comprobarUsuario(email, password, actividad)
    }

    fun obtenerUsuario(email: String, actividad: Activity?): Usuario_Planificador{
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

    fun crearPassword(email: String, passCifrada: String, actividad: Activity?) {
        return gestorUsuario.crearPassword(email, passCifrada, actividad)

    }

}