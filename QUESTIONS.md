# PARTE TEORICA

### Lifecycle

#### Explica el ciclo de vida de una Activity.

##### ¿Por qué vinculamos las tareas de red a los componentes UI de la aplicación?
Se vinculan para poder controlar las tareas de red, esto es iniciarlas, pausarlas o finalizarlas, haciendo uso de la UI de la aplicación. Y con el multitasking, estas tareas de red, como lo puede ser una petición a un servidor externo, se pueden ejecutar en un Thread diferente al principal, con lo cual la aplicación seguirá funcionado sin bloquearse, mientras se espera la respuesta del servidor.

##### ¿Qué pasaría si intentamos actualizar la recyclerview con nuevos streams después de que el usuario haya cerrado la aplicación?
Según lo leído, en este caso que este hilo siguiese ejecutándose luego que se haya cerrado la aplicación, este hilo se seguiría ejecutándose, permaneciendo en memoria, pero Android monitorea el consumo de memoria de las aplicaciones y puede finalizar procesos para liberar memoria. En el momento en que Android mata una aplicación, son esto se matan todos los procesos, y subprocesos de la aplicación y se libera la memoria utilizada por estos.

##### Describe brevemente los principales estados del ciclo de vida de una Activity.
Escribe aquí tu respuesta

---

### Paginación 

#### Explica el uso de paginación en la API de Twitch.

##### ¿Qué ventajas ofrece la paginación a la aplicación?
Escribe aquí tu respuesta

##### ¿Qué problemas puede tener la aplicación si no se utiliza paginación?
Escribe aquí tu respuesta

##### Lista algunos ejemplos de aplicaciones que usan paginación.
Escribe aquí tu respuesta
