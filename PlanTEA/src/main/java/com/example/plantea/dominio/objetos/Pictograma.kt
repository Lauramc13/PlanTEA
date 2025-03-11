package com.example.plantea.dominio.objetos

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class Pictograma(
    var id: String? = null,
    var titulo: String? = null,
    @Transient
    var imagen: Bitmap? = null, // Bitmap is not Parcelable, so it will be ignored
    var idAPI: Int = 0,
    var categoria: Int = 0,
    var favorito: Boolean = false,
    var historia: String? = null,
    var duracion: String? = null,
    var isImprevisto: Boolean = false,
    var pictoEntretenimiento: Int = 0,
    var listaPictogramas: ArrayList<Pictograma>? = null,
    var posicion: Int? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString(),
        titulo = parcel.readString(),
        idAPI = parcel.readInt(),
        categoria = parcel.readInt(),
        favorito = parcel.readByte() != 0.toByte(),
        historia = parcel.readString(),
        duracion = parcel.readString(),
        isImprevisto = parcel.readByte() != 0.toByte(),
        pictoEntretenimiento = parcel.readInt(),
        listaPictogramas = parcel.createTypedArrayList(CREATOR),
        posicion = parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(titulo)
        parcel.writeInt(idAPI)
        parcel.writeInt(categoria)
        parcel.writeByte(if (favorito) 1 else 0)
        parcel.writeString(historia)
        parcel.writeString(duracion)
        parcel.writeByte(if (isImprevisto) 1 else 0)
        parcel.writeInt(pictoEntretenimiento)
        parcel.writeTypedList(listaPictogramas)
        parcel.writeValue(posicion)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Pictograma> {
        override fun createFromParcel(parcel: Parcel): Pictograma {
            return Pictograma(parcel)
        }

        override fun newArray(size: Int): Array<Pictograma?> {
            return arrayOfNulls(size)
        }
    }
}
