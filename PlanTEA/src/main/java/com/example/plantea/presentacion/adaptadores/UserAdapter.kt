package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.objetos.Actividad
import com.example.plantea.dominio.objetos.Usuario
import com.example.plantea.presentacion.actividades.CommonUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
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
            holder.card.visibility = View.GONE
        } else {
            holder.nombre.editText?.setText(users[position].name)
            if(users[position].imagen == null){
                holder.iconEdit.visibility = View.VISIBLE
            }else{
                holder.iconEdit.visibility = View.GONE
                holder.imagen.setImageBitmap(users[position].imagen)
            }
            holder.newUser.visibility = View.GONE
            holder.user.visibility = View.VISIBLE

            holder.buttonConfig.text = buttonConfig(holder.itemView.context, users[position].configPictograma)

            if(users[position].actividades?.isEmpty() == true || (users[position].actividades?.last()?.nombre != null && users[position].actividades?.last()?.nombre != "")){
                users[position].actividades?.add(Actividad("", "",null, null, users[position].id))
            }
            holder.recyclerActividades?.layoutManager = GridLayoutManager(holder.recyclerActividades?.context, 3, GridLayoutManager.VERTICAL, false)
            holder.recyclerActividades?.adapter = ActividadAdapter(position, users[position].actividades, listenerActividad)

        }
    }

    private fun buttonConfig(context: Context, string: String?): String{
       return  when(string){
           //get string imagen_y_texto from strings.xml
            "default" -> getString(context, R.string.imagen_y_texto)
            "imagen" -> getString(context, R.string.imagen)
            "texto" -> getString(context, R.string.texto)
            else -> getString(context, R.string.imagen_y_texto)
        }
    }

    override fun getItemCount(): Int {
        return users!!.size
    }

    inner class ViewHolderUserTEA(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var card: MaterialCardView = itemView.findViewById(R.id.id_card)
        var nombre: TextInputLayout = itemView.findViewById(R.id.nombre)
        var imagen : ShapeableImageView = itemView.findViewById(R.id.imagen)
        var iconEdit : ImageView = itemView.findViewById(R.id.id_editIcon)
        var user : LinearLayout = itemView.findViewById(R.id.user)
        var newUser : View
        val buttonConfig : MaterialButton = itemView.findViewById(R.id.configPicto)
        val borrar: Button = itemView.findViewById(R.id.borrar)
        var recyclerActividades: RecyclerView? = null

        init {
            recyclerActividades = itemView.findViewById(R.id.recycler_view_actividades)

            newUser = if(CommonUtils.isMobile(itemView.context)){
                 itemView.findViewById<MaterialButton>(R.id.newUser)
            }else{
                 itemView.findViewById<LinearLayout>(R.id.newUser)
            }

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