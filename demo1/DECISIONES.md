# Documento de Decisiones del Proyecto

## 1. ¿Por qué se utilizó `@ConfigurationProperties` en lugar de `@Value`?
`@ConfigurationProperties` permite agrupar y mapear propiedades estructuradas y tipadas a través de un prefijo común (`app.info`), facilitando la validación conjunta con `@Validated` (p. ej., `@NotBlank`, `@Email`) y la inyección limpia del objeto completo de configuración como una única clase Java. Por el contrario, `@Value` requiere anotar cada campo individualmente con expresiones SpEL en texto plano, lo que resulta en duplicación de código, mayor propensión a errores de sintaxis y falta de soporte nativo directo para validación Jakarta Bean Validation sobre las propiedades agrupadas.

## 2. ¿Cómo se garantiza que la versión no se desincronice del `pom.xml`?
Se configura en `application.properties` la propiedad `app.info.version=@project.version@`. Además, en el archivo `pom.xml` se habilita la filtración de recursos mediante la sección `<resources><resource><directory>src/main/resources</directory><filtering>true</filtering></resource></resources>`. Durante la fase de construcción (*build*) con Maven, la variable `@project.version@` se reemplaza automáticamente con el valor definido en el nodo `<version>0.0.1-SNAPSHOT</version>` de Maven, asegurando que la versión nunca se mantenga ni duplique manualmente.

## 3. ¿Qué ocurre si se elimina `@Component` de `AppInfoProperties`?
Si se elimina `@Component` de `AppInfoProperties` (y no se registra alternativamente mediante `@EnableConfigurationProperties(AppInfoProperties.class)` en la configuración principal o mediante escaneo de ConfigurationProperties), Spring Boot no registrará `AppInfoProperties` como un bean administrado en el IoC Container. En consecuencia, al intentar iniciar la aplicación o resolver el constructor de `InfoController`, Spring fallará durante el arranque con una excepción `NoSuchBeanDefinitionException` / `No qualifying bean of type 'org.example.demo1.config.AppInfoProperties' available`.

## 4. ¿Qué diferencia existe entre `/api/info` y `/actuator/info`?
- `/api/info`: Es un endpoint REST propio y personalizado de la capa de dominio de la aplicación, expuesto según los requerimientos del negocio mediante un controlador (`InfoController`) y formateado a través de un DTO específico (`InfoResponse`).
- `/actuator/info`: Es un endpoint administrativo provisto por Spring Boot Actuator para observabilidad, monitoreo y gestión operativa. Expone información global del entorno de ejecución, datos de compilación (git, build) y métricas internas configuradas para operaciones o DevOps.

## 5. ¿Qué propiedades se escogieron para cada categoría y por qué?

1. **Servidor:**
   - `server.port=8080`: Define el puerto TCP explícito en el que el servidor web embebido (Tomcat) escucha las peticiones HTTP.
   - `server.servlet.context-path=/`: Establece la ruta raíz del contexto web de la aplicación.
2. **Logging:**
   - `logging.level.root=INFO`: Establece el nivel global por defecto para la emisión de logs de todo el sistema en INFO.
   - `logging.level.org.example.demo1=DEBUG`: Permite un nivel de trazabilidad y depuración detallado (DEBUG) para los paquetes propios del proyecto.
3. **Bases de datos / JSON:**
   - `spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/inventario}` (junto con username y password): Configura la conexión a PostgreSQL con valores por defecto seguros mediante variables de entorno para no bloquear la aplicación si la BD no está levantada.
   - `spring.jackson.serialization.indent-output=true`: Formatea e indenta automáticamente las respuestas JSON devueltas por los controladores REST para facilitar su lectura.
   - `spring.jackson.default-property-inclusion=non_null`: Evita serializar campos nulos en las respuestas JSON enviadas al cliente.
4. **Actuator:**
   - `management.endpoints.web.exposure.include=health,info`: Expone a través de la interfaz Web los endpoints administrativos `/actuator/health` y `/actuator/info`.
   - `management.endpoint.health.show-details=always`: Muestra detalles completos del estado de salud del sistema al consultar `/actuator/health`.
5. **Seguridad:**
   - `spring.security.user.name=admin`: Define el nombre de usuario predeterminado para la autenticación HTTP Basic.
   - `spring.security.user.password=admin123`: Establece la contraseña por defecto para el usuario administrativo.

## 6. ¿Qué cambió en el comportamiento de la aplicación?
Al incorporar las nuevas dependencias y configuraciones:
- La aplicación ahora valida las propiedades de `app.info` al arrancar.
- Se habilitó la seguridad con Spring Security, requiriendo autenticación por defecto en las rutas privadas pero permitiendo explícitamente el acceso público a `/api/info`, `/actuator/**` y `/home` mediante `SecurityFilterChain`.
- Los JSON producidos por la aplicación se entregan formateados e indentados (`indent-output`).
- Se expusieron los endpoints de observabilidad de Actuator.

## 7. ¿Cómo se verificó?
Se verificó mediante:
- Pruebas unitarias y de integración automatizadas ejecutando `.\mvnw.cmd clean test`.
- Verificación del endpoint GET `http://localhost:8080/api/info` en los perfiles `dev` y `prod`.
- Inspección manual y automatizada de `/actuator/health` y `/actuator/info`.

## 8. ¿Qué pasaría si se eliminaran las propiedades?
Si se eliminan las propiedades de `app.info` en `application.properties`, la validación de Bean Validation (`@NotBlank`, `@Email`) en `AppInfoProperties` fallará durante la fase de inicio del contexto de Spring Boot, impidiendo que la aplicación arranque y mostrando un error de tipo `BindValidationException`.

## 9. ¿Qué error produce `server.port=abc` y por qué?
Si se establece `server.port=abc`, Spring Boot arrojará una excepción durante el arranque (`Failed to bind properties under 'server.port' to java.lang.Integer` / `IllegalArgumentException` o `BindException`). Esto sucede porque el motor de binding de propiedades de Spring Boot intenta convertir la cadena de caracteres `"abc"` al tipo numérico requerido (`java.lang.Integer` o `int`) para el puerto de red. Al no ser un número entero válido, la conversión falla y detiene la inicialización del servidor Tomcat.

## 10. Declaración de uso de IA
Se utilizó inteligencia artificial como herramienta de asistencia para comprender conceptos de Spring Boot 4.1.1, comparar alternativas de diseño (como `@ConfigurationProperties` vs `@Value`), estructurar las configuraciones de seguridad y generar plantillas base de pruebas y DTOs (`record`). Todas las decisiones de implementación, código escrito y comportamientos resultantes fueron revisados, probados y verificados empíricamente en el entorno del proyecto.
