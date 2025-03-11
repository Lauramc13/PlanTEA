# Module PlanTEA
PlanTEA es una aplicación para tablets y móviles que permite a las personas con TEA planificar y 
anticipar eventos y tareas de su vida diaria.

La aplicación está dividida en tres paquetes:

- **`Dominio`**: Contiene las clases que representan los objetos del dominio de la aplicación.
- **`Persistencia`**: Contiene las clases que permiten la persistencia de los datos de la aplicación.
- **`Presentación`**: Contiene las clases que representan las actividades de la aplicación y contiene la lógica de presentación.

# Package com.example.plantea.presentacion.actividades
Las actividades representan las pantallas individuales de la aplicación. Cada actividad tiene su
propio ciclo de vida y se encarga de mostrar la información al usuario.

# Package com.example.plantea.presentacion.viewModels
Los viewModel son clases diseñadas para almacenar y gestionar los datos relacionados con la UI.

- **El ViewModel conserva los datos** cuando la pantalla rota. Esto significa que si el usuario gira el dispositivo, la actividad (Activity) puede recrearse, pero los datos almacenados en el ViewModel no se pierden.
    - **MutableLiveData y SingleLiveEvent** son herramientas utilizadas en **Android** (con **ViewModel** y **LiveData**) para gestionar datos y eventos en la interfaz de usuario de manera reactiva.
        - **MutableLiveData** es un tipo de LiveData que permite actualizar y observar datos. Cuando su valor cambia, **todas las vistas (Activity, Fragment) que lo observan reciben la actualización y pueden reaccionar** (por ejemplo, mostrando información nueva en la pantalla).
        - **SingleLiveEvent**, en cambio, está diseñado para manejar eventos que solo deben ejecutarse una vez, como mostrar un mensaje o navegar a otra pantalla. A diferencia de MutableLiveData, **SingleLiveEvent no vuelve a emitir el evento cuando la pantalla se rota**, evitando que se dispare accidentalmente otra vez.

# Package com.example.plantea.presentacion.adaptadores
Los adaptadores son clases que actúan como puente entre una fuente de datos y una vista (RecyclerView, ListView o Spinner). Coge los datos y los convierte en elementos visuales.

# Package com.example.plantea.presentacion.fragmentos
Los Fragments son componentes de la interfaz de usuario que representa una parte de una pantalla. Estos tienen sus propios ciclos de vida como las actividades.

