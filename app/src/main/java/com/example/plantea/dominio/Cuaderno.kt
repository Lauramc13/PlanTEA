package com.example.plantea.dominio

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable

class Cuaderno() : Parcelable {
    var id = 0
    var titulo: String? = null
    var imagen: String? = null
    var termometro: Boolean? = null
    private var gestorCuadernos = GestionCuadernos()

    constructor(id: Int, titulo: String, imagen: String?, termometro: Boolean) : this() {
        this.id = id
        this.titulo = titulo
        this.imagen = imagen
        this.termometro = termometro
    }

    // Implement the Parcelable interface methods
    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        titulo = parcel.readString()
        imagen = parcel.readString()
        termometro = parcel.readValue(Boolean::class.java.classLoader) as Boolean?
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(titulo)
        dest.writeString(imagen)
        dest.writeValue(termometro)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Cuaderno> {
        override fun createFromParcel(parcel: Parcel): Cuaderno {
            return Cuaderno(parcel)
        }

        override fun newArray(size: Int): Array<Cuaderno?> {
            return arrayOfNulls(size)
        }
    }
    fun consultarCuadernos(actividad: Activity?, idUsuario: String): ArrayList<Cuaderno> {
        return gestorCuadernos.consultarCuadernos(actividad, idUsuario)
    }

    fun crearCuaderno(actividad: Activity?, idUsuario: String, titulo: String, imagen: String?, termometro: Int): Int {
        return gestorCuadernos.insertarCuaderno(actividad, idUsuario, titulo, imagen, termometro)
    }

    fun editarCuaderno(actividad: Activity?, idUsuario: String, idCuaderno: String, titulo: String, imagen: String?, termometro: Int) {
        gestorCuadernos.editarCuaderno(actividad, idUsuario, idCuaderno, titulo, imagen, termometro)
    }

    fun eliminarCuaderno(actividad: Activity?, idCuaderno: Int) {
        gestorCuadernos.eliminarCuaderno(actividad, idCuaderno)
    }

}