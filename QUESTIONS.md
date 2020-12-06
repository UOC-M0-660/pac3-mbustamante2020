# PARTE TEORICA

### Lifecycle

#### Explica el ciclo de vida de una Activity.

##### ¿Por qué vinculamos las tareas de red a los componentes UI de la aplicación?
Se vinculan para poder controlar las tareas de red, esto es iniciarlas, pausarlas, finalizarlas o ver  en que estado se encuentra la tarea, esto mediante el uso de la UI de la aplicación. Al ejecutar una petición a un servidor externo, no se obtendrá una respuesta inmediata, mientras se espera la respuesta, en la UI se podría mostrar el icono de actualización a la espera de dicha respuesta y por ejemplo también se podría poner un botón para cancelar la petición, si el tiempo de respuesta es muy largo. Esta vinculación entre las tareas de red y los componentes de UI se realiza con la ayuda del multitasking.

##### ¿Qué pasaría si intentamos actualizar la recyclerview con nuevos streams después de que el usuario haya cerrado la aplicación?
Según lo leído, en el caso que un hilo siguiese ejecutándose luego que se haya cerrado la aplicación, este hilo seguiría estando en memoria, pero Android monitorea el consumo de memoria de las aplicaciones y puede finalizar procesos para liberarla. En el momento en que Android mata un proceso, también lo hace con los subprocesos asociados, liberando la memoria.

##### Describe brevemente los principales estados del ciclo de vida de una Activity.

| Estado del proceso  | Estado del Activity |
| ------------- | ------------- |
| En el plano principal, la actividad esta corriendo (en foco)      | <li> OnCreate(): Se ejecuta cuando se crea la actividad. Aquí se realizan las inicializaciones de todo tipo. </li><li> OnStart(): Se ejecuta cuando la actividad esta a punto de iniciar y ser mostrada en pantalla. </li><li> OnResumed(): Se ejecuta cuando la actividad esta lista para que el usuario interactué con ella. </li>    |
| En pausa, se ha perdido el foco, la actividad esta en segundo plano (sigue siendo visible)      | OnPause(): Se ejecuta cuando la actividad esta a punto de ser enviada a segundo plano, ya que otra actividad es puesta en primer plano    |
| Parada, se ha sustituido la actividad por otra, y queda en segundo plano (no visible)     | OnStop(): Se ejecuta en el momento en que la actividad no será visible al usuario     |
| Ha finalizado la actividad    | OnDestroy(): Se llama antes que finalice la actividad     |

---

### Paginación 

#### Explica el uso de paginación en la API de Twitch.

##### ¿Qué ventajas ofrece la paginación a la aplicación?
* Permite mostrar contenido sin interrupción, el contenido se irá cargando de forma automática a medida que se hace scroll.
* Reduce el tiempo de carga, ya que se irán mostrando una cierta cantidad de elementos, a la vez.
* En Smartphones y tablets, ayuda a visualizar mejor el contenido, esto es por ir mostrando una cierta cantidad de elementos a la vez.
* Permite hacer scroll infinitos.

##### ¿Qué problemas puede tener la aplicación si no se utiliza paginación?
Según lo investigado, si no se realiza paginación con cursor, pueden aparecer 2 problemas, los cuales son:
1. Cuando se agrega un ítem, esto hace que los ítems se desplacen un espacio hacia abajo, lo cual provoca la repetición de un ítem en la paginación. Se muestra la primera página, se agrega un nuevo ítem y luego al mostrar la siguiente página, el último ítem de la primera página se repetiría en la siguiente página como primer ítem.
1. Cuando se elimina un ítem, esto hace que los ítems posteriores al ítem que se elimina, se desplacen un espacio hacia arriba, lo cual provoca que un ítem no se mostrará en las siguientes páginas. Se muestra la primera página, se elimina un ítem y luego se muestra la siguiente página, hay un ítem que no se muestra, este ítem es el primer ítem de la siguiente página antes de la eliminación.

Esto se soluciona con el uso de la paginación con cursor, el cual marca el punto exacto, desde donde se seguirán obteniendo los siguientes resultados.

##### Lista algunos ejemplos de aplicaciones que usan paginación.
Como ejemplos de aplicaciones que usan paginación, tenemos a:
* Twitter
* Instagram
* Facebook
* Amazon
* Pinterest