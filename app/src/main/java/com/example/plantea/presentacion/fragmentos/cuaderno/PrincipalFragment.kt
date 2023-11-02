package com.example.plantea.presentacion.fragmentos.cuaderno

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Cuaderno
import com.example.plantea.presentacion.CuadernoInterface
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorCategoriasCuaderno
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import java.util.UUID

class PrincipalFragment : Fragment(), AdaptadorCategoriasCuaderno.OnItemSelectedListener {
    lateinit var vista: View
    lateinit var actividad: Activity
    lateinit var listaPictoCuaderno: ArrayList<Cuaderno>
    lateinit var recycler_Pictogramas: RecyclerView
    lateinit var interfaceCuaderno: CuadernoInterface
    lateinit var image: ShapeableImageView
    lateinit var adaptador : AdaptadorCategoriasCuaderno
    private var isPlanificador = false

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val orientation = newConfig.orientation
        val gridValueManager: Int = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            3
        } else {
            4
        }
        recycler_Pictogramas.layoutManager = GridLayoutManager(context, gridValueManager)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_cuaderno_principal, container, false)
        val bundle = this.arguments
        listaPictoCuaderno = (bundle!!["key"] as ArrayList<Cuaderno>?)!!
        isPlanificador = (bundle["isPlan"] as Boolean)
        recycler_Pictogramas = vista.findViewById(R.id.lst_cuaderno_pictogramas)

        val gridValue = context?.let { CommonUtils.cambioOrientacion(it) }
        recycler_Pictogramas.layoutManager = gridValue?.let { GridLayoutManager(context, it) }
        val context = requireContext()
        adaptador = AdaptadorCategoriasCuaderno(listaPictoCuaderno, isPlanificador, this, context, this)
        recycler_Pictogramas.adapter = adaptador
        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
            interfaceCuaderno = (actividad as CuadernoInterface?)!!
        }
    }

    override fun pictogramaCuaderno(posicion: Int, idCuaderno: Int){
        if(posicion == listaPictoCuaderno.lastIndex && isPlanificador){
            mostrarDialogo()
        }else {
            listaPictoCuaderno[posicion].titulo?.let { interfaceCuaderno.mostrarPictogramas(idCuaderno, listaPictoCuaderno[posicion].termometro, it)
            }
        }
    }

    fun eliminarCuaderno(cuaderno: Cuaderno) {
        cuaderno.eliminarCuaderno(actividad, cuaderno.id)
        listaPictoCuaderno.remove(cuaderno)
        adaptador.notifyDataSetChanged()

    }

    private fun mostrarDialogo(){
        val dialogo = context?.let { Dialog(it) }
        dialogo!!.setContentView(R.layout.dialogo_crear_categoria_cuaderno)
        dialogo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val title : TextInputLayout = dialogo.findViewById(R.id.txt_title)
        val termometro : SwitchCompat =  dialogo.findViewById(R.id.switch_termometro)
        image = dialogo.findViewById(R.id.img)

        val btnCrear : Button = dialogo.findViewById(R.id.btn_create)
        val iconoCerrarLogin : ImageView = dialogo.findViewById(R.id.icono_CerrarDialogo)

        image.setOnClickListener {
            abrirGaleria()
        }

        //el termometro por ahora no hace nada
        btnCrear.setOnClickListener{
            title.error = null
            if (title.editText?.text.toString().isEmpty() || image.drawable == null) {
                title.error = "Obligatorio"
                Toast.makeText(context, "Tienes que rellenar todos los campos", Toast.LENGTH_LONG).show()
            }else{
                val cuaderno = Cuaderno()
                val prefs = context?.getSharedPreferences("Preferencias", MODE_PRIVATE)
                val idUsuario = prefs?.getString("idUsuario", "")
                val numero = UUID.randomUUID()
                val imagen = context?.let { it1 -> CommonUtils.crearRuta(it1, image, "ImgCuaderno$numero"
                ) }

                var isTermometro = 0
                if(termometro.isChecked){
                    isTermometro = 1
                }

                if (idUsuario != null) {
                    val id = cuaderno.crearCuaderno(activity, idUsuario, title.editText?.text.toString(), imagen, isTermometro)
                    cuaderno.id = id
                    cuaderno.titulo = title.editText?.text.toString()
                    cuaderno.imagen = imagen
                    cuaderno.termometro = termometro.isChecked
                    val lastIndex = listaPictoCuaderno.size - 1
                    listaPictoCuaderno.add(lastIndex, cuaderno)
                }
                adaptador.notifyDataSetChanged()
                dialogo.dismiss()
            }
        }
        iconoCerrarLogin.setOnClickListener { dialogo.dismiss() }
        dialogo.show()
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        // Manejar la URI devuelta aquí
        if (uri != null) {
            // Load the selected image from the URI
            val inputStream = context?.contentResolver?.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Guardar la imagen en rutaUsuarioTEA
            val ruta = context?.let { CommonUtils.getPathFromUri(it, uri)}

            context?.let {
                if (ruta != null) {
                    CommonUtils.guardarImagen(it, ruta, bitmap)
                    image.background = null
                    image.setImageURI(uri)
                }
            }

        } else {
            Toast.makeText(context, "No se ha seleccionado una imagen", Toast.LENGTH_SHORT).show()
        }
    }




}