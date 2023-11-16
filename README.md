# WeatherApp
**Asignatura:** Desarrollo de Aplicaciones para Ciencia de Datos
**Curso:** 2023
**Titulación:** Ciencia e Ingenería de Datos
**Escuela:** Escuela Técnica Superior de Ingeniería Informática y de Telecomunicaciones
**Universidad:** Universidad de las Palmas de Gran Canaria

## Resumen de la Funcionalidad
La aplicación WeatherApp proporciona información meteorológica para diferentes ubicaciones en las Islas Canarias. Utiliza datos en tiempo real de OpenWeatherMap y almacena la información localmente en una base de datos SQLite. La aplicación se ejecuta periódicamente para actualizar los datos meteorológicos y ofrece la posibilidad de consultar el pronóstico para varios días.

## Recursos Utilizados
- **Entornos de Desarrollo:** IntelliJ IDEA.
- **Herramientas de Control de Versiones:** Git, GitHub

## Diseño

### Patrones y Principios de Diseño

La aplicación utiliza el patrón de diseño MVC (Modelo-Vista-Controlador) para lograr una estructura modular y mantenible. La separación de responsabilidades facilita la extensibilidad y la prueba unitaria.

### Decisiones de Implementación

### Prevención de Inyecciones SQL
El uso de Prepared Statements en las consultas SQL en `SQLiteWeatherStore` minimiza el riesgo de inyecciones SQL, proporcionando una capa de seguridad adicional.

### Manejo de Excepciones
Se implementa un manejo adecuado de excepciones en varias partes del código, asegurando que los errores se registren y gestionen correctamente. Esto contribuye a la robustez del sistema y facilita la identificación de posibles problemas.

### Variable de Entorno para API Key
La gestión de la API key como variable de entorno mejora la seguridad al evitar la exposición de claves en el código fuente. Además, permite una configuración más sencilla y segura en diferentes entornos.

### Extracción de Datos Segura
El uso de la biblioteca Jsoup para extraer datos HTML de OpenWeatherMap se realiza de manera segura y controlada, minimizando posibles amenazas de seguridad.

### Utilización de Try-with-Resources
En el manejo de recursos, como conexiones JDBC, se emplea la declaración try-with-resources, garantizando la liberación adecuada de recursos y aumentando la fiabilidad del código.

### Uso de Tipos Inmutables
Los objetos de tipo `Weather` se diseñan como inmutables, lo que contribuye a la consistencia y predictibilidad del código al evitar modificaciones inesperadas.

### Validación de Datos de Entrada
Se realizan verificaciones de nulidad y validaciones de datos de entrada en puntos críticos del código, evitando posibles errores y mejorando la robustez del sistema.


### Diagrama de Clases

FALTA

## Relaciones de Dependencia

- La clase `WeatherController` depende de las clases `WeatherSupplier` y `WeatherStore` para obtener y almacenar datos meteorológicos.
- La clase `OpenWeatherMapSupplier` implementa la interfaz `WeatherSupplier` y utiliza la biblioteca Jsoup para obtener datos de OpenWeatherMap.
- La clase `SQLiteWeatherStore` implementa la interfaz `WeatherStore` y gestiona el almacenamiento de datos meteorológicos en una base de datos SQLite.
- La clase `WeatherTask` es una tarea programada que ejecuta la lógica de obtención de datos meteorológicos según un horario establecido.