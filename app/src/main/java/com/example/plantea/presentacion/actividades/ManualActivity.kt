package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.example.plantea.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ManualActivity : AppCompatActivity() {

    lateinit var vista: ScrollView
    private lateinit var indice: View
    private lateinit var btn_subir: FloatingActionButton

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Comprobamos la orientacion de la pantalla
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Horizontal", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "Vertical", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual)

        val backButton : Button = findViewById(R.id.goBackButton)
        val tutorialButton : Button = findViewById(R.id.tutorialButton)

        backButton.setOnClickListener{
            finish()
        }

        tutorialButton.setOnClickListener {
            val intent = Intent(applicationContext, TutorialActivity::class.java)
            intent.putExtra("isFromManual", true)
            startActivity(intent)
        }



        indice = findViewById(R.id.layout_indice)
        vista = findViewById(R.id.scrollview)
        btn_subir = findViewById(R.id.floatingActionButton)
        val parrafo1 : TextView = findViewById(R.id.lbl_parrafo1)
        val parrafo2 : TextView = findViewById(R.id.lbl_parrafo2)
        val parrafo3 : TextView = findViewById(R.id.lbl_parrafo3)
        //val parrafo4 : TextView = findViewById(R.id.lbl_parrafo4)
        val parrafo5 : TextView = findViewById(R.id.lbl_parrafo5)
        val parrafo6 : TextView = findViewById(R.id.lbl_parrafo6)
        val parrafo7 : TextView = findViewById(R.id.lbl_parrafo7)
        val parrafo8 : TextView = findViewById(R.id.lbl_parrafo8)
        val parrafo9 : TextView = findViewById(R.id.lbl_parrafo9)
        val parrafo10 : TextView = findViewById(R.id.lbl_parrafo10)
        val parrafo11 : TextView = findViewById(R.id.lbl_parrafo11)
        val parrafo12 : TextView = findViewById(R.id.lbl_parrafo12)
        val parrafo13 : TextView = findViewById(R.id.lbl_parrafo13)
        val parrafo14 : TextView = findViewById(R.id.lbl_parrafo14)
        val parrafo15 : TextView = findViewById(R.id.lbl_parrafo15)
        val parrafo16 : TextView = findViewById(R.id.lbl_parrafo16)
       // val parrafo17 : TextView = findViewById(R.id.lbl_parrafo17)
        val parrafo18 : TextView = findViewById(R.id.lbl_parrafo18)
        val parrafo19 : TextView = findViewById(R.id.lbl_parrafo19)
        val parrafo20 : TextView = findViewById(R.id.lbl_parrafo20)
        val parrafo21 : TextView = findViewById(R.id.lbl_parrafo21)
        val parrafo22 : TextView = findViewById(R.id.lbl_parrafo22)
        val parrafo23 : TextView = findViewById(R.id.lbl_parrafo23)
        val parrafo24 : TextView = findViewById(R.id.lbl_parrafo24)
        val parrafo241 : TextView = findViewById(R.id.lbl_parrafo241)
        val parrafo25 : TextView = findViewById(R.id.lbl_parrafo25)
        val parrafo26 : TextView = findViewById(R.id.lbl_parrafo26)
        val parrafo27 : TextView = findViewById(R.id.lbl_parrafo27)
        val parrafo28 : TextView = findViewById(R.id.lbl_parrafo28)
        val parrafo29 : TextView = findViewById(R.id.lbl_parrafo29)
        val parrafo30 : TextView = findViewById(R.id.lbl_parrafo30)
        val parrafo31 : TextView = findViewById(R.id.lbl_parrafo31)
        val parrafo32 : TextView = findViewById(R.id.lbl_parrafo32)
        val parrafo33 : TextView = findViewById(R.id.lbl_parrafo33)
        val parrafo34 : TextView = findViewById(R.id.lbl_parrafo34)
        val parrafo341 : TextView = findViewById(R.id.lbl_parrafo341)
        val parrafo35 : TextView = findViewById(R.id.lbl_parrafo35)
        val parrafo36 : TextView = findViewById(R.id.lbl_parrafo36)
        val parrafo37 : TextView = findViewById(R.id.lbl_parrafo37)
        val parrafo38 : TextView = findViewById(R.id.lbl_parrafo38)
        val parrafo39 : TextView = findViewById(R.id.lbl_parrafo39)
        val parrafo40 : TextView = findViewById(R.id.lbl_parrafo40)
        val parrafo41 : TextView = findViewById(R.id.lbl_parrafo41)
        val parrafo42 : TextView = findViewById(R.id.lbl_parrafo42)
        val parrafo43 : TextView = findViewById(R.id.lbl_parrafo43)


        val indice1 : TextView = findViewById(R.id.lbl_indice1) 
        // val indice2 : TextView = findViewById(R.id.lbl_indice2)
        // val indice21 : TextView = findViewById(R.id.lbl_indice2_1)
        // val indice22 : TextView = findViewById(R.id.lbl_indice2_2)
        // val indice23 : TextView = findViewById(R.id.lbl_indice2_3)
        val indice3 : TextView = findViewById(R.id.lbl_indice3)
        val indice4 : TextView = findViewById(R.id.lbl_indice4)
        //  val indice41 : TextView = findViewById(R.id.lbl_indice4_1)
        val indice42 : TextView = findViewById(R.id.lbl_indice4_2)
        val indice43 : TextView = findViewById(R.id.lbl_indice4_3)
        val indice44 : TextView = findViewById(R.id.lbl_indice4_4)
        val indice45 : TextView = findViewById(R.id.lbl_indice4_5)
        val indice46 : TextView = findViewById(R.id.lbl_indice4_6)
        val indice47 : TextView = findViewById(R.id.lbl_indice4_7)
        val indice48 : TextView = findViewById(R.id.lbl_indice4_8)
        val indice49 : TextView = findViewById(R.id.lbl_indice4_9)
        val indice410 : TextView = findViewById(R.id.lbl_indice4_10)
        val indice411 : TextView = findViewById(R.id.lbl_indice4_11)
        val indice5 : TextView = findViewById(R.id.lbl_indice5)
        val indice51 : TextView = findViewById(R.id.lbl_indice5_1)
        val indice52 : TextView = findViewById(R.id.lbl_indice5_2)
        val indice53 : TextView = findViewById(R.id.lbl_indice5_3)
        val indice54 : TextView = findViewById(R.id.lbl_indice5_4)
        val indice6 : TextView = findViewById(R.id.lbl_indice6)



        indice1.setOnClickListener {
            vista.scrollTo(0, parrafo1.top)
        }
/*
        indice2.setOnClickListener {
            vista.scrollTo(0, parrafo8.top)
        }

        indice21.setOnClickListener {
            vista.scrollTo(0, parrafo3.top)
        }

        indice22.setOnClickListener {
            vista.scrollTo(0, parrafo5.top)
        }

        indice23.setOnClickListener {
            vista.scrollTo(0, parrafo6.top)
        }

        indice41.setOnClickListener {
            vista.scrollTo(0, parrafo11.top)
        }
*/
        indice3.setOnClickListener {
            vista.scrollTo(0, parrafo8.top)
        }

        indice4.setOnClickListener {
            vista.scrollTo(0, parrafo10.top)
        }

        indice42.setOnClickListener {
            vista.scrollTo(0, parrafo11.top)
        }

        indice43.setOnClickListener {
            vista.scrollTo(0, parrafo12.top)
        }

        indice44.setOnClickListener {
            vista.scrollTo(0, parrafo13.top)
        }

        indice45.setOnClickListener {
            vista.scrollTo(0, parrafo16.top)
        }

        indice46.setOnClickListener {
            vista.scrollTo(0, parrafo21.top)
        }

        indice47.setOnClickListener {
            vista.scrollTo(0, parrafo22.top)
        }

        indice48.setOnClickListener {
            vista.scrollTo(0, parrafo23.top)
        }

        indice49.setOnClickListener {
            vista.scrollTo(0, parrafo26.top)
        }

        indice410.setOnClickListener {
            vista.scrollTo(0, parrafo28.top)
        }

        indice411.setOnClickListener {
            vista.scrollTo(0, parrafo30.top)
        }

        indice5.setOnClickListener {
            vista.scrollTo(0, parrafo31.top)
        }

        indice51.setOnClickListener {
            vista.scrollTo(0, parrafo31.top)
        }

        indice52.setOnClickListener {
            vista.scrollTo(0, parrafo33.top)
        }

        indice53.setOnClickListener {
            vista.scrollTo(0, parrafo37.top)
        }

        indice54.setOnClickListener {
            vista.scrollTo(0, parrafo40.top)
        }

        indice6.setOnClickListener {
            vista.scrollTo(0, parrafo2.top)
        }

        /*1. Descripción de la aplicación PlanTEA*/parrafo1.text = HtmlCompat.fromHtml("<h1>1. Descripción de la aplicación PlanTEA</h1>  PlanTEA es una aplicación móvil que permite planificar y anticipar la asistencia a niños o adultos con Trastorno del Espectro Autista (TEA) a las consultas médicas, así como facilitar la comunicación con los especialistas mediante un cuaderno de comunicación aumentativa y alternativa. <br> <br> La aplicación da soporte a dos roles:" +
                "<ul><li><b>Planificador:</b> Usuario dirigido a familiares, personal de apoyo o persona con altas capacidades. Este usuario podrá crear planificaciones y gestionarlas mediante un calendario con la posibilidad de recibir notificaciones.<br><br>" +
                "<li><b>Usuario:</b> Usuario dirigido a niños o adultos que sólo podrá seguir la planificación relacionada con el evento elegido por el planificador y hacer uso del cuaderno de comunicación.</li></ul>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        /*3. Descripción de la Pantalla inicial*/parrafo8.text = HtmlCompat.fromHtml("<h1>2. Descripción de la Pantalla inicial</h1> Al acceder a la aplicación aparecerá la pantalla principal. Desde esta pantalla se podrá acceder a dos usuarios diferenciados por la imagen y nombre elegido en la pantalla de preferencias.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo9.text = HtmlCompat.fromHtml("<br> Los dos usuarios disponibles son:<br><br> " +
                "<ul> <li><b>Planificador: </b>Corresponde al usuario de la izquierda. Podrá crear y gestionar planificaciones de las citas médicas. </li> " +
                "<li> <b> Usuario: </b>Corresponde al usuario de la derecha. Sólo podrá seguir una planificación creada por el planificador y acceder al cuaderno de comunicación. </li> </ul>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo9.text =
            mostrarIcono(R.drawable.ic_baseline_help_outline_64, parrafo9.text, 370, 371, true)

        /*4.Usuario Planificador*/

        parrafo10.text = HtmlCompat.fromHtml("<h1>3. Usuario Planificador</h1>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        /*4.1 Uso seguro de la aplicación: Autenticación*/parrafo11.text = HtmlCompat.fromHtml("<h2>3.1 Uso seguro de la aplicación: Autenticación</h2> Para acceder a las funcionalidades correspondientes al usuario planificador se solicitará la contraseña creada.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        /*4.2 Cambio de contraseña*/parrafo12.text = HtmlCompat.fromHtml("<h2>3.2 Cambio de contraseña</h2> Para realizar el cambio de contraseña, se solicitará la contraseña actual, la nueva contraseña, la cual se deberá introducir dos veces, para su verificación. Al tocar el botón “Guardar”, la contraseña será actualizada.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        /*4.3 Descripción de la Pantalla inicial*/parrafo13.text = HtmlCompat.fromHtml("<h2>3.3 Descripción de la pantalla inicial</h2> En la parte izquierda de esta pantalla se encuentra el calendario por el cual se podrá navegar y seleccionar cualquier día. En la parte derecha se mostrarán las citas médicas/eventos del día seleccionado en el calendario.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo14.text = HtmlCompat.fromHtml("<br>Se podrá crear un evento para el día seleccionado, tocando el botón “Evento”.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo15.text = HtmlCompat.fromHtml("<br>Desde esta pantalla también se puede acceder a las opciones:<br> " +
                "<ul> <li><b>Ajuste de las preferencias: </b> Tocando el icono  se podrá cambiar la imagen y nombre de los usuarios y del objeto para tranquilizar. También se podrá activar o desactivar las notificaciones de las citas médicas y modificar cuando quieres recibir el aviso.</li>" +
                "<li><b>Ayuda: </b>Tocando el icono se podrá acceder a este manual de usuario.</li></ul>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo15.text = mostrarIcono(R.drawable.user_circle, parrafo15.text, 107, 108, true)
        parrafo15.text =
            mostrarIcono(R.drawable.ic_baseline_help_outline_64, parrafo15.text, 418, 419, true)

        /*4.4 Creación de un evento*/parrafo16.text = HtmlCompat.fromHtml("<h2>3.4 Creación de un evento</h2>" +
                "Para crear un nuevo evento y añadirla al calendario se tendrá que completar los siguientes campos: " +
                "<ul> <li> <b>Hora:</b> Tocando el botón “Hora” se podrá seleccionar la hora de inicio de la cita. </li>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo18.text = HtmlCompat.fromHtml("<li><b>Planificación: </b> Después de seleccionar la hora se mostrarán todas las planificaciones disponibles. Si no hay ninguna planificación o si se necesita crear otra, se podrá crear tocando en el botón “Nueva planificación”.</li> </ul>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo19.text = HtmlCompat.fromHtml("<br>Una vez rellenados todos los campos y seleccionada la planificación, se creará el evento tocando el botón “Guardar”.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo20.text = HtmlCompat.fromHtml("<br>El nuevo evento se mostrará en el calendario en el día correspondiente.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        /*Opciones de los eventos/citas médicas*/parrafo21.text = HtmlCompat.fromHtml("<h2>- Opciones de los eventos</h2>" +
                "En los eventos creados se pueden encontrar las siguientes opciones: <br>" +
                "<ul> <li>Cuando un evento tiene el icono     la planificación no será visible para el niño o adulto. Pero si tiene el icono la planificación si será visible para el usuario y podrá seguirla. Solo una cita médica/evento podrá estar visible.</li>" +
                "<li>Tocando el icono   se podrá eliminar el evento. Antes pedirá confirmar la acción. </li> </ul>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo21.text = mostrarIcono(R.drawable.ic_baseline_visibility_off_40, parrafo21.text, 156, 157, true)
        parrafo21.text =
            mostrarIcono(R.drawable.ic_baseline_visibility_40, parrafo21.text, 235, 236, true)
        parrafo21.text = mostrarIcono(R.drawable.svg_trash, parrafo21.text, 486, 487, true)

        /*4.5 Creación de una planificación*/parrafo22.text = HtmlCompat.fromHtml("<br><h2>3.5 Creación de una planificación</h2>En esta pantalla se encuentran diez categorías (Ir al médico, Cortarse el pelo, Hacer la compra, Ir al colegio, Lugares, Desplazamiento, Acción, Entretenimiento y Recompensa) que agrupan los pictogramas disponibles para crear una planificación.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        /*Pictogramas*/parrafo23.text = HtmlCompat.fromHtml("<br><h2>Pictogramas</h2>Al seleccionar una de estas categorías, se mostrará la lista de pictogramas relacionados. Para volver atrás o salir de una categoría se tocará el icono X. Los pictogramas tambien podrán seleccionarse como favoritos y se guardarán en la categoría Favoritos.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo23.text = mostrarIcono(R.drawable.ic_baseline_close_24, parrafo23.text, 166, 167, false)
        parrafo24.text = HtmlCompat.fromHtml("<br>En el caso de la categoría <i>Consultas</i>, se mostrará un conjunto de consultas predefinidas y habituales " +
                "que ofrece la aplicación que, a su vez, cuentan con pictogramas específicos para cada una de ellas. Para ver esos pictogramas se seleccionará el tipo de consulta deseado.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo241.text =  HtmlCompat.fromHtml("<br>En los pictogramas también tendremos la posibilidad de crear una historia social haciendo clic sobre el icono de bocadillo en el pictograma deseado. Se abrirá un nuevo diálogo en el que podremos introducir la historia social que queramos. <br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo25.text = HtmlCompat.fromHtml("<br>La aplicación ofrece la posibilidad de incluir en las planificaciones actividades de entretenimiento para los tiempos de espera y recompensas para motivar o premiar la realización de un plan. " +
                "Estos pictogramas se encuentran en las categorías de <i>Entretenimiento</i> y <i>Recompensa</i>.<br><br> Se diferencian en la planificación por el color del borde del pictograma (azul para las actividades de entretenimiento y amarillo para las " +
                "recompensas), así como por un icono representativo en la esquina superior derecha del pictograma.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        /*Creación de un nuevo pictograma*/parrafo26.text = HtmlCompat.fromHtml("<br><h2>Creación de un nuevo pictograma</h2>En cada categoría se puede añadir nuevos pictogramas. Para ello se tocará el icono  . Se mostrará una ventana para la creación del nuevo pictograma. " +
                " Se deberá introducir un título, la categoría en la cual se guardará y la imagen seleccionada desde la galería tocando sobre   .<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo26.text =
            mostrarIcono(R.drawable.ic_baseline_add_circle_64, parrafo26.text, 117, 118, false)
        parrafo26.text = mostrarIcono(R.drawable.ic_baseline_add_photo_alternate_128, parrafo26.text, 306, 307, true)
        parrafo27.text = HtmlCompat.fromHtml("<br>Cuando los campos estén completados se seleccionará el botón “Guardar”. El nuevo pictograma se mostrará junto al resto de pictogramas en la categoría seleccionada. <br>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        /*Pasos para crear planificación*/parrafo28.text = HtmlCompat.fromHtml("<br><h2>Pasos para crear planificación</h2>A la hora de crear una planificación será necesario seguir los siguientes pasos: <br><br>" +
                "1. Introducir un título para la planificación.<br>" +
                "2. Seleccionar una de las categorías.<br>" +
                "3. Hacer clic en el pictograma deseado.<br>" +
                "4. Repetir el paso anteriores hasta formar la secuencia de acciones.<br>" +
                "5. Cualquier pictograma añadido al plan se podrá cambiar de posición desplazándolo hacia la posición deseada. " +
                "También se podrá eliminar un pictograma añadido al plan deslizándolo hacia arriba o haciendo clic en el icono de la papelera. <br>" +
                "6. Guardar la planificación tocando el botón “Guardar”.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo29.text = HtmlCompat.fromHtml("<br>La planificación creada se mostrará junto a las demás y podrá ser seleccionada para crear un evento. Al seleccionar el plan cambiará a color azul claro.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        /*Opciones de las planificaciones*/parrafo30.text = HtmlCompat.fromHtml("<br><h2>Opciones de las planificaciones</h2>En las planificaciones guardadas se pueden encontrar tres opciones:<br>" +
                "<b>-Duplicar planificación: </b>Se podrá crear una copia de una planificación existente tocando el icono    .<br>" +
                "<b>-Editar planificación: </b>Se podrá modificar el título o los pictogramas que forman cualquier plan tocando el icono    .<br>" +
                "<b>-Eliminar planificación: </b>Se podrá eliminar cualquier planificación tocando el icono    .<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo30.text =
            mostrarIcono(R.drawable.svg_files, parrafo30.text, 200, 201, true)
        parrafo30.text = mostrarIcono(R.drawable.svg_edit, parrafo30.text, 315, 316, true)
        parrafo30.text = mostrarIcono(R.drawable.svg_trash, parrafo30.text, 401, 402, true)

        /*5. Usuario TEA y 5.1 Descripción de la Pantalla inicial*/parrafo31.text = HtmlCompat.fromHtml("<br><h1>4.Usuario TEA</h1> Este usuario solo podrá seguir una planificación seleccionada por el planificador y acceder al cuaderno de comunicación. Al acceder, no se requiere contraseña y se mostrará la pantalla inicial para su usuario." +
                "<h2>4.1 Descripción de la Pantalla inicial</h2>" +
                "En esta pantalla se mostrará la planificación correspondiente a los eventos visibles seleccionados por el planificador. Se puede navegar entre los diferentes días para ver las planificaciones visibles.<br>" +
                "<br>Además, en la parte de abajo se puede encontrar dos botones. El primero será para acceder al cuaderno de comunicación y el segundo llevará a la pantalla Actividades en la que se podrá mostrar la actividad tranquilizadora o abrir Youtube.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        //parrafo32.text = HtmlCompat.fromHtml("<br>Desde esta pantalla el usuario también tendrá acceso al cuaderno de comunicación al tocar el icono    y tocando en el icono    se tendrá acceso a este manual de usuario.", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo32.text = mostrarIcono(R.drawable.icono_cuaderno, parrafo32.text, 99, 100, false)
        parrafo32.text = mostrarIcono(R.drawable.ic_baseline_help_outline_64, parrafo32.text, 121, 122, false)

        /*5.2 Seguimiento de una planificación*/parrafo33.text = HtmlCompat.fromHtml("<br><h2>4.2 Seguimiento de una planificación</h2> Si hay una planificación visible, se mostrará para poder seguirla.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo34.text = HtmlCompat.fromHtml("<br>Al seleccionar los pictogramas que forman la planificación se mostrarán en grande para poder seguir el plan paso a paso.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo341.text = HtmlCompat.fromHtml("<br>Si seleccionamos la opción de reproduccir una planificación, los pictogramas en la lista se irán mostrando uno por uno con una animación.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        parrafo35.text = HtmlCompat.fromHtml("<br>A medida que se avanza en la planificación, los pictogramas que representan los pasos completados cambiarán a color gris. Para volver a pasos anteriores, ya completados, se tocará el icono Deshacer.<br> ", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo35.text = mostrarIcono(R.drawable.ic_baseline_undo_64, parrafo35.text, 189, 190, true)
        parrafo36.text = HtmlCompat.fromHtml("<br>Las actividades de entretenimiento y recompensa incluidas por el planificador en una planificación son mostradas mediante una animación.<br> ", HtmlCompat.FROM_HTML_MODE_LEGACY)

        /*5.3 Cuaderno de comunicación*/parrafo37.text = HtmlCompat.fromHtml("<br><h2>4.3 Cuaderno de comunicación</h2>" +
                "En la pantalla del cuaderno de comunicación se encuentran dos categorías para que el usuario niño o adulto pueda indicar la zona de dolor (<i>¿Dónde te duele?</i>) o el síntoma que tiene (<i>¿Qué te pasa?</i>).<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo38.text = HtmlCompat.fromHtml("<br>También cuenta con pictogramas relacionados con el tiempo para indicar desde cuando se padece el dolor o síntoma, y pictogramas para responder al médico o indicar la zona. Estos pictogramas se pueden ver en grande al seleccionar sobre ellos.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo39.text = HtmlCompat.fromHtml("<br>Además, tocando en el icono     podrás acceder a este manual de usuario.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo39.text =
            mostrarIcono(R.drawable.ic_baseline_help_outline_64, parrafo39.text, 28, 29, true)

        /*Categorías del cuaderno de comunicación*/parrafo40.text = HtmlCompat.fromHtml("<h2>Categorías del cuaderno de comunicación</h2>" +
                "Para ver los pictogramas relacionados con síntomas se tocará sobre la categoría de la izquierda <i>¿Qué te pasa?</i>.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo41.text = HtmlCompat.fromHtml("<br>Para ver los pictogramas relacionados con dolores se tocará sobre la categoría de la derecha <i>¿Qué te duele?</i>.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo42.text = HtmlCompat.fromHtml("<br>Al seleccionar un pictograma disponible en una de las categorías, se mostrará en grande. <br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo43.text = HtmlCompat.fromHtml("<br>El usuario podrá indicar la intensidad de la dolencia deslizando la barra hacia la imagen que represente su estado. <br>", HtmlCompat.FROM_HTML_MODE_LEGACY)

         /*2. Ajuste de las preferencias*/parrafo2.text = HtmlCompat.fromHtml("<h1> 5. Ajuste de las preferencias </h1> La primera vez que se inicie la aplicación se mostrará la pantalla de preferencias. En ella se podrán personalizar algunos aspectos que utilizará la aplicación. También se puede acceder a dicha pantalla tocando el icono    .<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo2.text = mostrarIcono(R.drawable.user_circle, parrafo2.text, 251, 252, true)

        /*Información de los usuarios*/parrafo3.text = HtmlCompat.fromHtml("<br>Las opciones para configurar son: <br><br> <b>-Información de los usuarios:</b> Se podrá seleccionar una imagen y nombre para los dos usuarios. La imagen será seleccionada desde la galería del dispositivo para ello se tocará la imagen   .<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo3.text = mostrarIcono(R.drawable.ic_baseline_add_photo_alternate_128, parrafo3.text, 221, 222, true)
        /*parrafo4.text = HtmlCompat.fromHtml("<br>La primera vez que se acceda a la galería pedirá permiso.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY) */

        /*Objeto tranquilizador*/parrafo5.text = HtmlCompat.fromHtml("<br><b>-Objeto Tranquilizador: </b>También se podrá seleccionar una imagen y nombre que represente el objeto que calme a la persona para el caso de que la planificación no salga bien.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        /*Notificaciones*/parrafo6.text = HtmlCompat.fromHtml("<br><b>-Notificaciones: </b>Se podrá activar o desactivar la notificación de las citas médicas planificadas y seleccionar la antelación para recibir el aviso.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        parrafo7.text = HtmlCompat.fromHtml("<br>Una vez completados los campos se seleccionará el botón “Guardar”.<br> ", HtmlCompat.FROM_HTML_MODE_LEGACY)


        /*Botón para subir al indice*/btn_subir.setOnClickListener {
            vista.scrollTo(
                0,
                indice.top
            )
        }
    }

    private fun mostrarIcono(icono: Int, texto: CharSequence?, start: Int, end: Int, tintarIcono: Boolean): SpannableString {
        val textoIcono = SpannableString(texto)
        val drawable = ContextCompat.getDrawable(this, icono)
        drawable!!.setBounds(0, 0, 60, 60) // Tamaño de imagen personalizado
        //Tintar icono a negro en el caso de los iconos de color gris
       // if (tintarIcono) {
           // drawable.setTint(Color.BLACK)
        //}
        val span = ImageSpan(drawable)
        //textoIcono.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return textoIcono
    }
/*
    fun onclick_punto_1() {
        vista.scrollTo(0, parrafo1.top)
    }

    fun onclick_punto_2() {
        vista.scrollTo(0, parrafo2.top)
    }

    fun onclick_punto_2_1() {
        vista.scrollTo(0, parrafo3.top)
    }

    fun onclick_punto_2_2() {
        vista.scrollTo(0, parrafo5.top)
    }

    fun onclick_punto_2_3() {
        vista.scrollTo(0, parrafo6.top)
    }

    fun onclick_punto_3() {
        vista.scrollTo(0, parrafo8.top)
    }

    fun onclick_punto_4() {
        vista.scrollTo(0, parrafo10.top)
    }

    fun onclick_punto_4_1() {
        vista.scrollTo(0, parrafo10.top)
    }

    fun onclick_punto_4_2() {
        vista.scrollTo(0, parrafo11.top)
    }

    fun onclick_punto_4_3() {
        vista.scrollTo(0, parrafo12.top)
    }

    fun onclick_punto_4_4() {
        vista.scrollTo(0, parrafo13.top)
    }

    fun onclick_punto_4_5() {
        vista.scrollTo(0, parrafo16.top)
    }

    fun onclick_punto_4_6() {
        vista.scrollTo(0, parrafo21.top)
    }

    fun onclick_punto_4_7() {
        vista.scrollTo(0, parrafo22.top)
    }

    fun onclick_punto_4_8() {
        vista.scrollTo(0, parrafo23.top)
    }

    fun onclick_punto_4_9() {
        vista.scrollTo(0, parrafo26.top)
    }

    fun onclick_punto_4_10() {
        vista.scrollTo(0, parrafo28.top)
    }

    fun onclick_punto_4_11() {
        vista.scrollTo(0, parrafo30.top)
    }

    fun onclick_punto_5() {
        vista.scrollTo(0, parrafo31.top)
    }

    fun onclick_punto_5_1() {
        vista.scrollTo(0, parrafo31.top)
    }

    fun onclick_punto_5_2() {
        vista.scrollTo(0, parrafo33.top)
    }

    fun onclick_punto_5_3() {
        vista.scrollTo(0, parrafo37.top)
    }

    fun onclick_punto_5_4() {
        vista.scrollTo(0, parrafo40.top)
    }

   */

}