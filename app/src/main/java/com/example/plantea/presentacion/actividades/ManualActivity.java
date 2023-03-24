package com.example.plantea.presentacion.actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.example.plantea.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ManualActivity extends AppCompatActivity {

    TextView parrafo1, parrafo2, parrafo3, parrafo4, parrafo5, parrafo6, parrafo7, parrafo8, parrafo9, parrafo10, parrafo11, parrafo12, parrafo13, parrafo14, parrafo15, parrafo16, parrafo17,
            parrafo18, parrafo19, parrafo20, parrafo21, parrafo22, parrafo23, parrafo24, parrafo25, parrafo26, parrafo27, parrafo28, parrafo29, parrafo30, parrafo31, parrafo32, parrafo33, parrafo34, parrafo35,
            parrafo36, parrafo37, parrafo38, parrafo39, parrafo40, parrafo41, parrafo42, parrafo43;
    ScrollView vista;
    View indice;
    FloatingActionButton btn_subir;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Horizontal", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "Vertical", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        //Activamos icono volver atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        indice = findViewById(R.id.layout_indice);
        vista = findViewById(R.id.scrollview);
        btn_subir = findViewById(R.id.floatingActionButton);
        parrafo1 = findViewById(R.id.lbl_parrafo1);
        parrafo2 = findViewById(R.id.lbl_parrafo2);
        parrafo3 = findViewById(R.id.lbl_parrafo3);
        parrafo4 = findViewById(R.id.lbl_parrafo4);
        parrafo5 = findViewById(R.id.lbl_parrafo5);
        parrafo6 = findViewById(R.id.lbl_parrafo6);
        parrafo7 = findViewById(R.id.lbl_parrafo7);
        parrafo8 = findViewById(R.id.lbl_parrafo8);
        parrafo9 = findViewById(R.id.lbl_parrafo9);
        parrafo10 = findViewById(R.id.lbl_parrafo10);
        parrafo11 = findViewById(R.id.lbl_parrafo11);
        parrafo12 = findViewById(R.id.lbl_parrafo12);
        parrafo13 = findViewById(R.id.lbl_parrafo13);
        parrafo14 = findViewById(R.id.lbl_parrafo14);
        parrafo15 = findViewById(R.id.lbl_parrafo15);
        parrafo16 = findViewById(R.id.lbl_parrafo16);
        parrafo17 = findViewById(R.id.lbl_parrafo17);
        parrafo18 = findViewById(R.id.lbl_parrafo18);
        parrafo19 = findViewById(R.id.lbl_parrafo19);
        parrafo20 = findViewById(R.id.lbl_parrafo20);
        parrafo21 = findViewById(R.id.lbl_parrafo21);
        parrafo22 = findViewById(R.id.lbl_parrafo22);
        parrafo23 = findViewById(R.id.lbl_parrafo23);
        parrafo24 = findViewById(R.id.lbl_parrafo24);
        parrafo25 = findViewById(R.id.lbl_parrafo25);
        parrafo26 = findViewById(R.id.lbl_parrafo26);
        parrafo27 = findViewById(R.id.lbl_parrafo27);
        parrafo28 = findViewById(R.id.lbl_parrafo28);
        parrafo29 = findViewById(R.id.lbl_parrafo29);
        parrafo30 = findViewById(R.id.lbl_parrafo30);
        parrafo31 = findViewById(R.id.lbl_parrafo31);
        parrafo32 = findViewById(R.id.lbl_parrafo32);
        parrafo33 = findViewById(R.id.lbl_parrafo33);
        parrafo34 = findViewById(R.id.lbl_parrafo34);
        parrafo35 = findViewById(R.id.lbl_parrafo35);
        parrafo36 = findViewById(R.id.lbl_parrafo36);
        parrafo37 = findViewById(R.id.lbl_parrafo37);
        parrafo38 = findViewById(R.id.lbl_parrafo38);
        parrafo39 = findViewById(R.id.lbl_parrafo39);
        parrafo40 = findViewById(R.id.lbl_parrafo40);
        parrafo41 = findViewById(R.id.lbl_parrafo41);
        parrafo42 = findViewById(R.id.lbl_parrafo42);
        parrafo43 = findViewById(R.id.lbl_parrafo43);


        /*1. Descripción de la aplicación PlanTEA*/
        parrafo1.setText(HtmlCompat.fromHtml("<h1>1. Descripción de la aplicación PlanTEA</h1>  PlanTEA es una aplicación móvil que permite planificar y anticipar la asistencia a niños o adultos con Trastorno del Espectro Autista (TEA) a las consultas médicas, así como facilitar la comunicación con los especialistas mediante un cuaderno de comunicación aumentativa y alternativa. <br><br> La aplicación da soporte a dos roles:<br><br>" +
                "<b>-Planificador:</b> Usuario dirigido a familiares, personal de apoyo o persona con altas capacidades. Este usuario podrá crear planificaciones y gestionarlas mediante un calendario con la posibilidad de recibir notificaciones.<br><br>" +
                "<b>-Usuario:</b> Usuario dirigido a niños o adultos que sólo podrá seguir la planificación relacionada con la cita médica elegida por el planificador y hacer uso del cuaderno de comunicación.<br>", HtmlCompat.FROM_HTML_MODE_LEGACY ));


        /*2. Ajuste de las preferencias*/
        parrafo2.setText(HtmlCompat.fromHtml("<h1> 2. Ajuste de las preferencias </h1> La primera vez que se inicie la aplicación se mostrará la pantalla de preferencias. En ella se podrán personalizar algunos aspectos que utilizará la aplicación. También se puede acceder a dicha pantalla tocando el icono    .<br>", HtmlCompat.FROM_HTML_MODE_LEGACY));
        parrafo2.setText(mostrarIcono(R.drawable.user_circle, parrafo2.getText(), 251,252,true));

            /*Información de los usuarios*/
        parrafo3.setText(HtmlCompat.fromHtml("<br>Las opciones para configurar son: <br><br> <b>-Información de los usuarios:</b> Se podrá seleccionar una imagen y nombre para los dos usuarios. La imagen será seleccionada desde la galería del dispositivo para ello se tocará la imagen   .<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo3.setText(mostrarIcono(R.drawable.ic_baseline_add_photo_alternate_128, parrafo3.getText(), 221,222,true));
        parrafo4.setText(HtmlCompat.fromHtml("<br>La primera vez que se acceda a la galería pedirá permiso.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));

            /*Objeto tranquilizador*/
        parrafo5.setText(HtmlCompat.fromHtml("<br><b>-Objeto Tranquilizador: </b>También se podrá seleccionar una imagen y nombre que represente el objeto que calme a la persona para el caso de que la planificación no salga bien.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));

            /*Notificaciones*/
        parrafo6.setText(HtmlCompat.fromHtml("<br><b>-Notificaciones: </b>Se podrá activar o desactivar la notificación de las citas médicas planificadas y seleccionar la antelación para recibir el aviso.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo7.setText(HtmlCompat.fromHtml("<br>Una vez completados los campos se seleccionará el botón “Guardar”.<br> ",HtmlCompat.FROM_HTML_MODE_LEGACY ));

        /*3. Descripción de la Pantalla inicial*/
        parrafo8.setText(HtmlCompat.fromHtml("<br><h1>3. Descripción de la Pantalla inicial</h1> Al acceder a la aplicación aparecerá la pantalla principal. Desde esta pantalla se podrá acceder a dos usuarios diferenciados por la imagen y nombre elegido en la pantalla de preferencias.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY));
        parrafo9.setText(HtmlCompat.fromHtml("<br> Los dos usuarios disponibles son:<br><br> " +
                "<b>-Planificador: </b>Corresponde al usuario de la izquierda. Podrá crear y gestionar planificaciones de las citas médicas.<br><br> " +
                "<b>-Usuario: </b>Corresponde al usuario de la derecha. Sólo podrá seguir una planificación creada por el planificador y acceder al cuaderno de comunicación.<br> También se tendrá acceso a este manual de usuario tocando el icono     .",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo9.setText(mostrarIcono(R.drawable.ic_baseline_help_outline_64, parrafo9.getText(), 370,371,true));

        /*4.Usuario Planificador*/

            /*4.1 Inicio de la aplicación por primera vez: Creación de contraseña*/
        parrafo10.setText(HtmlCompat.fromHtml("<br><h1>4. Usuario Planificador</h1> Este usuario será el encargado de crear planificaciones de las citas médicas haciendo uso de pictogramas y gestionarlas mediante un calendario. Para acceder se requiere contraseña. <h2>4.1 Inicio de la aplicación por primera vez: Creación de contraseña</h2>" +
                "La primera vez que se accede como usuario planificador, se solicitará la creación de una contraseña. Para crearla se deberá introducir dos veces para realizar su confirmación.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));

            /*4.2 Uso seguro de la aplicación: Autenticación*/
        parrafo11.setText(HtmlCompat.fromHtml("<br><h2>4.2 Uso seguro de la aplicación: Autenticación</h2> Para acceder a las funcionalidades correspondientes al usuario planificador se solicitará la contraseña creada.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));

        /*4.3 Cambio de contraseña*/
        parrafo12.setText(HtmlCompat.fromHtml("<br><h2>4.3 Cambio de contraseña</h2> Para realizar el cambio de contraseña, se solicitará la contraseña actual, la nueva contraseña, la cual se deberá introducir dos veces, para su verificación. Al tocar el botón “Guardar”, la contraseña será actualizada.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));

            /*4.4 Descripción de la Pantalla inicial*/
        parrafo13.setText(HtmlCompat.fromHtml("<br><h2>4.4 Descripción de la Pantalla inicial</h2> En la parte izquierda de esta pantalla se encuentra el calendario por el cual se podrá navegar y seleccionar cualquier día. En la parte derecha se mostrarán las citas médicas/eventos del día seleccionado en el calendario.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo14.setText(HtmlCompat.fromHtml("<br>Se podrá crear un evento para el día seleccionado, tocando el botón “Evento”.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo15.setText(HtmlCompat.fromHtml("<br>Desde esta pantalla también se puede acceder a las opciones:<br> " +
                "<b>-Ajuste de las preferencias: </b>Tocando el icono  se podrá cambiar la imagen y nombre de los usuarios y del objeto para tranquilizar. También se podrá activar o desactivar las notificaciones de las citas médicas y modificar cuando quieres recibir el aviso.<br>" +
                "<b>-Cambio de contraseña: </b>Tocando el icono se podrá cambiar la contraseña actual.<br>" +
                "<b>-Ayuda: </b>Tocando el icono se podrá acceder a este manual de usuario.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo15.setText(mostrarIcono(R.drawable.user_circle, parrafo15.getText(), 107,108,true));
        parrafo15.setText(mostrarIcono(R.drawable.ic_baseline_lock_reset_64, parrafo15.getText(), 354,355,true));
        parrafo15.setText(mostrarIcono(R.drawable.ic_baseline_help_outline_64, parrafo15.getText(), 418,419,true));

         /*4.5 Creación de un evento/cita médica*/
        parrafo16.setText(HtmlCompat.fromHtml("<br><h2>4.5 Creación de un evento/cita médica</h2>" +
                "Para crear una nueva cita médica y añadirla al calendario se tendrá que completar los siguientes campos: <br>" +
                "<br><b>-Hora:</b> Tocando el botón “Hora” se podrá seleccionar la hora de inicio de la cita. Cuando se seleccione una hora se habilitará las opciones relacionadas con la planificación.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo17.setText(HtmlCompat.fromHtml("<br><b>-Tipo de consulta: </b>Se podrá seleccionar el tipo de cita médica.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo18.setText(HtmlCompat.fromHtml("<br><b>-Planificación: </b>Tocando el botón “Planificar” se mostrarán todas las planificaciones disponibles. Si no hay ninguna planificación o si se necesita crear otra, se podrá crear tocando en el botón “Nueva planificación”.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo19.setText(HtmlCompat.fromHtml("<br>Una vez rellenados todos los campos y seleccionada la planificación, se creará el evento tocando el botón “Guardar”.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo20.setText(HtmlCompat.fromHtml("<br>El nuevo evento se mostrará en el calendario en el día correspondiente.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));

        /*Opciones de los eventos/citas médicas*/
        parrafo21.setText(HtmlCompat.fromHtml("<br><h2>Opciones de los eventos/citas médicas</h2>" +
                "En los eventos creados se pueden encontrar las siguientes opciones: <br>" +
                "<br>-Cuando una cita médica/evento tiene el icono     la planificación no será visible para el niño o adulto. Pero si tiene el icono la planificación si será visible para el usuario y podrá seguirla. Solo una cita médica/evento podrá estar visible.<br>" +
                "<br>-Tocando el icono   se podrá visualizar todas las planificaciones desde el punto de vista del usuario niño o adulto.<br>" +
                "<br>-Tocando el icono   se podrá eliminar el evento. Antes pedirá confirmar la acción. <br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));

        parrafo21.setText(mostrarIcono(R.drawable.ic_baseline_visibility_off_40, parrafo21.getText(), 156,157,true));
        parrafo21.setText(mostrarIcono(R.drawable.ic_baseline_visibility_40, parrafo21.getText(), 235,236,true));
        parrafo21.setText(mostrarIcono(R.drawable.ic_baseline_preview_40, parrafo21.getText(), 370,371,true));
        parrafo21.setText(mostrarIcono(R.drawable.ic_baseline_delete_forever_40, parrafo21.getText(), 486,487,true));

        /*4.6 Creación de una planificación*/
        parrafo22.setText(HtmlCompat.fromHtml("<br><h2>4.6 Creación de una planificación</h2>En esta pantalla se encuentran siete categorías (Consultas, Profesionales, Lugares, Desplazamiento, Acción, Entretenimiento y Recompensa) que agrupan los pictogramas disponibles para crear una planificación.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));

        /*Pictogramas*/

        parrafo23.setText(HtmlCompat.fromHtml("<br><h2>Pictogramas</h2>Al seleccionar una de estas categorías, se mostrará la lista de pictogramas relacionados. Para volver atrás o salir de una categoría se tocará el icono     .<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo23.setText(mostrarIcono(R.drawable.ic_baseline_arrow_circle_left_64, parrafo23.getText(), 166,167,false));
        parrafo24.setText(HtmlCompat.fromHtml("<br>En el caso de la categoría <i>Consultas</i>, se mostrará un conjunto de consultas predefinidas y habituales " +
                "que ofrece la aplicación que, a su vez, cuentan con pictogramas específicos para cada una de ellas. Para ver esos pictogramas se mantendrá seleccionado el tipo de consulta deseado.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo25.setText(HtmlCompat.fromHtml("<br>La aplicación ofrece la posibilidad de incluir en las planificaciones actividades de entretenimiento para los tiempos de espera y recompensas para motivar o premiar la realización de un plan. " +
                "Estos pictogramas se encuentran en las categorías de <i>Entretenimiento</i> y <i>Recompensa</i>.<br><br> Se diferencian en la planificación por el color del borde del pictograma (rojo para las actividades de entretenimiento y amarillo para las " +
                "recompensas), así como por un icono representativo en la esquina superior derecha del pictograma.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));

        /*Creación de un nuevo pictograma*/
        parrafo26.setText(HtmlCompat.fromHtml("<br><h2>Creación de un nuevo pictograma</h2>En cada categoría se puede añadir nuevos pictogramas. Para ello se tocará el icono  . Se mostrará una ventana para la creación del nuevo pictograma. " +
                " Se deberá introducir un título, la categoría en la cual se guardará y la imagen seleccionada desde la galería tocando sobre   .<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo26.setText(mostrarIcono(R.drawable.ic_baseline_add_circle_64, parrafo26.getText(), 117,118,false));
        parrafo26.setText(mostrarIcono(R.drawable.ic_baseline_add_photo_alternate_128, parrafo26.getText(), 306,307,true));
        parrafo27.setText(HtmlCompat.fromHtml("<br>Cuando los campos estén completados se seleccionará el botón “Guardar”. El nuevo pictograma se mostrará junto al resto de pictogramas en la categoría seleccionada. <br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));

        /*Pasos para crear planificación*/
        parrafo28.setText(HtmlCompat.fromHtml("<br><h2>Pasos para crear planificación</h2>A la hora de crear una planificación será necesario seguir los siguientes pasos: <br><br>" +
                "1. Introducir un título para la planificación.<br>" +
                "2. Seleccionar una de las categorías.<br>" +
                "3. Mantener seleccionado el pictograma deseado y arrastrarlo hacia la pizarra.<br>" +
                "4. Soltar el pictograma en la pizarra.<br>" +
                "5. Repetir los pasos anteriores hasta formar la secuencia de acciones.<br>" +
                "6. Cualquier pictograma añadido al plan se podrá cambiar de posición desplazándolo hacia la posición deseada. " +
                "También se podrá eliminar un pictograma añadido al plan deslizándolo hacia arriba. <br>" +
                "7. Guardar la planificación tocando el botón “Guardar”.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo29.setText(HtmlCompat.fromHtml("<br>La planificación creada se mostrará junto a las demás y podrá ser seleccionada para crear una cita médica/evento. Al seleccionar el plan cambiará a color gris.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));

        /*Opciones de las planificaciones*/
        parrafo30.setText(HtmlCompat.fromHtml("<br><h2>Opciones de las planificaciones</h2>En las planificaciones guardadas se pueden encontrar tres opciones:<br>" +
                "<b>-Duplicar planificación: </b>Se podrá crear una copia de una planificación existente tocando el icono    .<br>" +
                "<b>-Editar planificación: </b>Se podrá modificar el título o los pictogramas que forman cualquier plan tocando el icono    .<br>" +
                "<b>-Eliminar planificación: </b>Se podrá eliminar cualquier planificación tocando el icono    .<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo30.setText(mostrarIcono(R.drawable.ic_baseline_file_copy_40, parrafo30.getText(), 200,201,true));
        parrafo30.setText(mostrarIcono(R.drawable.ic_baseline_edit_40, parrafo30.getText(), 315,316,true));
        parrafo30.setText(mostrarIcono(R.drawable.ic_baseline_delete_forever_40, parrafo30.getText(), 401,402,true));

        /*5. Usuario TEA y 5.1 Descripción de la Pantalla inicial*/
        parrafo31.setText(HtmlCompat.fromHtml("<br><h1>5.Usuario TEA</h1> Este usuario solo podrá seguir una planificación seleccionada por el planificador y acceder al cuaderno de comunicación. Al acceder, no se requiere contraseña y se mostrará la pantalla inicial para su usuario." +
                "<h2>5.1 Descripción de la Pantalla inicial</h2>" +
                "En esta pantalla se mostrará la planificación correspondiente a una cita médica seleccionada como visible por el planificador. <br>" +
                "<br>Además, en la parte superior derecha se puede encontrar el objeto tranquilizador creado en la pantalla de preferencias. Al tocar sobre el objeto se mostrará en grande.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo32.setText(HtmlCompat.fromHtml("<br>Desde esta pantalla el usuario también tendrá acceso al cuaderno de comunicación al tocar el icono    y tocando en el icono    se tendrá acceso a este manual de usuario.",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo32.setText(mostrarIcono(R.drawable.icono_cuaderno, parrafo32.getText(), 99,100,false));
        parrafo32.setText(mostrarIcono(R.drawable.ic_baseline_help_outline_64, parrafo32.getText(), 121,122,false));

        /*5.2 Seguimiento de una planificación*/
        parrafo33.setText(HtmlCompat.fromHtml("<br><h2>5.2 Seguimiento de una planificación</h2> Si hay una planificación visible, se mostrará para poder seguirla.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo34.setText(HtmlCompat.fromHtml("<br>Al seleccionar los pictogramas que forman la planificación se mostrarán en grande para poder seguir el plan paso a paso.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo35.setText(HtmlCompat.fromHtml("<br>A medida que se avanza en la planificación, los pictogramas que representan los pasos completados cambiarán a color gris. Para volver a pasos anteriores, ya completados, se tocará el icono    .<br> ",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo35.setText(mostrarIcono(R.drawable.ic_baseline_undo_64, parrafo35.getText(), 189,190,true));
        parrafo36.setText(HtmlCompat.fromHtml("<br>Las actividades de entretenimiento y recompensa incluidas por el planificador en una planificación son mostradas mediante una animación.<br> ",HtmlCompat.FROM_HTML_MODE_LEGACY ));

        /*5.3 Cuaderno de comunicación*/
        parrafo37.setText(HtmlCompat.fromHtml("<br><h2>5.3 Cuaderno de comunicación</h2>" +
                "En la pantalla del cuaderno de comunicación se encuentran dos categorías para que el usuario niño o adulto pueda indicar la zona de dolor (<i>¿Dónde te duele?</i>) o el síntoma que tiene (<i>¿Qué te pasa?</i>).<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo38.setText(HtmlCompat.fromHtml("<br>También cuenta con pictogramas relacionados con el tiempo para indicar desde cuando se padece el dolor o síntoma, y pictogramas para responder al médico o indicar la zona. Estos pictogramas se pueden ver en grande al seleccionar sobre ellos.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo39.setText(HtmlCompat.fromHtml("<br>Además, tocando en el icono     podrás acceder a este manual de usuario.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo39.setText(mostrarIcono(R.drawable.ic_baseline_help_outline_64, parrafo39.getText(), 28,29,true));

        /*Categorías del cuaderno de comunicación*/
        parrafo40.setText(HtmlCompat.fromHtml("<h2>Categorías del cuaderno de comunicación</h2>" +
                "Para ver los pictogramas relacionados con síntomas se tocará sobre la categoría de la izquierda <i>¿Qué te pasa?</i>.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));

        parrafo41.setText(HtmlCompat.fromHtml("<br>Para ver los pictogramas relacionados con dolores se tocará sobre la categoría de la derecha <i>¿Qué te duele?</i>.<br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo42.setText(HtmlCompat.fromHtml("<br>Al seleccionar un pictograma disponible en una de las categorías, se mostrará en grande. <br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));
        parrafo43.setText(HtmlCompat.fromHtml("<br>El usuario podrá indicar la intensidad de la dolencia deslizando la barra hacia la imagen que represente su estado. <br>",HtmlCompat.FROM_HTML_MODE_LEGACY ));

        /*Botón para subir al indice*/
        btn_subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vista.scrollTo(0, indice.getTop());
            }
        });
    }

    public SpannableString mostrarIcono (int icono, CharSequence texto, int start, int end, boolean tintarIcono){
        SpannableString textoIcono = new SpannableString(texto);
        Drawable drawable = ContextCompat.getDrawable(this, icono);
        drawable.setBounds(0, 0, 60, 60); // Tamaño de imagen personalizado
        //Tintar icono a negro en el caso de los iconos de color gris
        if(tintarIcono){
            drawable.setTint(Color.BLACK);
        }
        ImageSpan span = new ImageSpan(drawable);
        textoIcono.setSpan(span,start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return textoIcono;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void onclick_punto_1(View v){
        vista.scrollTo(0,parrafo1.getTop());
    }
    public void onclick_punto_2(View v){
        vista.scrollTo(0,parrafo2.getTop());
    }
    public void onclick_punto_2_1(View v){
        vista.scrollTo(0,parrafo3.getTop());
    }
    public void onclick_punto_2_2(View v){
        vista.scrollTo(0,parrafo5.getTop());
    }
    public void onclick_punto_2_3(View v){
        vista.scrollTo(0,parrafo6.getTop());
    }
    public void onclick_punto_3(View v){
        vista.scrollTo(0,parrafo8.getTop());
    }
    public void onclick_punto_4(View v){
        vista.scrollTo(0,parrafo10.getTop());
    }
    public void onclick_punto_4_1(View v){
        vista.scrollTo(0,parrafo10.getTop());
    }
    public void onclick_punto_4_2(View v){
        vista.scrollTo(0,parrafo11.getTop());
    }
    public void onclick_punto_4_3(View v){
        vista.scrollTo(0,parrafo12.getTop());
    }
    public void onclick_punto_4_4(View v){
        vista.scrollTo(0,parrafo13.getTop());
    }
    public void onclick_punto_4_5(View v){
        vista.scrollTo(0,parrafo16.getTop());
    }
    public void onclick_punto_4_6(View v){
        vista.scrollTo(0,parrafo21.getTop());
    }
    public void onclick_punto_4_7(View v){
        vista.scrollTo(0,parrafo22.getTop());
    }
    public void onclick_punto_4_8(View v){
        vista.scrollTo(0,parrafo23.getTop());
    }
    public void onclick_punto_4_9(View v){
        vista.scrollTo(0,parrafo26.getTop());
    }
    public void onclick_punto_4_10(View v){
        vista.scrollTo(0,parrafo28.getTop());
    }
    public void onclick_punto_4_11(View v){
        vista.scrollTo(0,parrafo30.getTop());
    }
    public void onclick_punto_5(View v){
        vista.scrollTo(0,parrafo31.getTop());
    }
    public void onclick_punto_5_1(View v){
        vista.scrollTo(0,parrafo31.getTop());
    }
    public void onclick_punto_5_2(View v){
        vista.scrollTo(0,parrafo33.getTop());
    }
    public void onclick_punto_5_3(View v){
        vista.scrollTo(0,parrafo37.getTop());
    }
    public void onclick_punto_5_4(View v){
        vista.scrollTo(0,parrafo40.getTop());
    }
}
