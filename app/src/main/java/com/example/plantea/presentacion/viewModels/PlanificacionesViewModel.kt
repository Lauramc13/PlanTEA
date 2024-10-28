package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Button
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.Actividad
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorListaPlanes
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Locale

class PlanificacionesViewModel : ViewModel(){

    val plan = Planificacion()
    lateinit var idUsuario : String
    lateinit var planes : ArrayList<Planificacion>
    var adaptador: AdaptadorListaPlanes? = null
    var posicionPlan = 0
    val planificacion = Planificacion()

    fun configureUser(prefs : android.content.SharedPreferences, context: Context){
        val userId = prefs.getString("idUsuario", "")
        idUsuario = userId.toString()
    }


    @OptIn(DelicateCoroutinesApi::class)
    fun dialogoTraduccion(activity: Activity, idPlan : Int){
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialogo_historia_traduccion)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val titulo : TextInputLayout = dialog.findViewById(R.id.txt_title)
        val iconoCerrar : ImageView = dialog.findViewById(R.id.icono_CerrarDialogo)
        val btnCrear : Button = dialog.findViewById(R.id.btn_create)

        val listaPictogramas = planificacion.obtenerPictogramasPlanificacion(activity, idPlan, Locale.getDefault().language, idUsuario)

        val imagesLoadedJob = GlobalScope.launch(Dispatchers.Default) {
            listaPictogramas.forEach { pictogram ->
                if (pictogram.idAPI != 0) {
                    pictogram.imagen = withContext(Dispatchers.IO) {
                        CommonUtils.getImagenAPI(pictogram.idAPI)
                    }
                }
            }
        }

        btnCrear.setOnClickListener{
            var tituloString = titulo.editText?.text.toString()

            //if tituloString is empty -> error
            if(tituloString.isEmpty()){
                tituloString = ""
            }

            runBlocking {
                imagesLoadedJob.join()
                CommonUtils.guardarPDF(activity.applicationContext, tituloString.uppercase(), listaPictogramas)
            }

            CommonUtils.hideKeyboard(activity.applicationContext, titulo)
            dialog.dismiss()
        }

        iconoCerrar.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

}
