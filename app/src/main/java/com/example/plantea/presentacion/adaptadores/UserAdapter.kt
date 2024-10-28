package com.example.plantea.presentacion.adaptadores

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Actividad
import com.example.plantea.dominio.Usuario
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout

class UserAdapter(private val users: ArrayList<Usuario>?, private val listener: OnItemSelectedListener?, private val listenerActividad: ActividadAdapter.OnItemSelectedListenerActividad) : RecyclerView.Adapter<UserAdapter.ViewHolderUserTEA>() {

    interface OnItemSelectedListener {
        fun onNuevoUser()
        fun changeConfigPicto(position: Int)
        fun onBorrarUser(position: Int)

        fun onEditImage(position: Int)

        fun onEditName(position: Int, name: String)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderUserTEA {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_tea, parent, false)
        return ViewHolderUserTEA(view)
    }

    override fun onBindViewHolder(holder: ViewHolderUserTEA, position: Int) {
        if (position == users!!.size - 1 && users[position].name.isNullOrEmpty()) {
            holder.newUser.visibility = View.VISIBLE
            holder.user.visibility = View.GONE
            holder.borrar.visibility = View.GONE
        } else {
            holder.nombre.editText?.setText(users[position].name)
            if(users[position].imagen.isNullOrEmpty()){
                holder.iconEdit.visibility = View.VISIBLE
            }else{
                holder.iconEdit.visibility = View.GONE
                holder.imagen.setImageURI(Uri.parse(users[position].imagen))
            }
            holder.newUser.visibility = View.GONE
            holder.user.visibility = View.VISIBLE

            holder.buttonConfig.text = buttonConfig(users[position].configPictograma)

            if(users[position].actividades?.isEmpty() == true || (users[position].actividades?.last()?.name != null && users[position].actividades?.last()?.name != "")){
                users[position].actividades?.add(Actividad("", "","", users[position].id))
            }
            holder.recyclerActividades?.layoutManager = GridLayoutManager(holder.recyclerActividades?.context, 3, GridLayoutManager.VERTICAL, false)
            holder.recyclerActividades?.adapter = ActividadAdapter(position, users[position].actividades, listenerActividad)

        }
    }


    private fun buttonConfig(string: String?): String{
       return  when(string){
            "default" -> "Imagen y texto"
            "imagen" -> "Imagen"
            "texto" -> "Texto"
            else -> "Imagen y texto"
        }
    }

    override fun getItemCount(): Int {
        return users!!.size
    }

    inner class ViewHolderUserTEA(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var card: RelativeLayout
        var imagen : ShapeableImageView
        var iconEdit : ImageView
        var nombre: TextInputLayout
        var user : LinearLayout
        val newUser : LinearLayout
        val buttonConfig : Button
        val borrar: Button
        var recyclerActividades: RecyclerView? = null

        init {
            card = itemView.findViewById(R.id.id_card)
            nombre = itemView.findViewById(R.id.nombre)
            imagen = itemView.findViewById(R.id.imagen)
            iconEdit = itemView.findViewById(R.id.id_editIcon)
            user = itemView.findViewById(R.id.user)
            newUser = itemView.findViewById(R.id.newUser)
            buttonConfig = itemView.findViewById(R.id.configPicto)
            borrar = itemView.findViewById(R.id.borrar)
            recyclerActividades = itemView.findViewById(R.id.recycler_view_actividades)

            card.setOnClickListener {
                listener?.onEditImage(bindingAdapterPosition)
            }

            newUser.setOnClickListener{
                listener?.onNuevoUser()
            }

            buttonConfig.setOnClickListener {
                listener?.changeConfigPicto(bindingAdapterPosition)
            }

            borrar.setOnClickListener {
                listener?.onBorrarUser(bindingAdapterPosition)
            }

            //when nombre is changed the user is updated
            nombre.editText?.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val nombreUser = nombre.editText?.text.toString()
                    if (nombreUser.isNotEmpty()) {
                        listener?.onEditName(bindingAdapterPosition, nombreUser)
                    }
                }

            }

        }

    }
}