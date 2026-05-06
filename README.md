# CrossWarr 🔥

**CrossWarr** es una aplicación nativa de Android diseñada para entusiastas del fitness. Su objetivo es hacer accesible el entrenamiento diario ofreciendo retos dinámicos que se adaptan al material disponible del usuario, gestionando todo de forma reactiva a través de la nube.


## ✨ Características Principales

### 👤 Perfil del usuario
- Acceso a retos diarios filtrándolos dependiendo de si el usuario dispone o no de equipamiento auxiliar.
- La app oculta automáticamente ejercicios que requieren equipamiento si el usuario no dispone de él.
- Timer integrado con alertas sonoras y gestión de hardware para mantener la pantalla encendida durante la actividad.
- Registro automático de tiempos y marcas personales.

### 🔐 Perfil Administrador
- **Gestión de Catálogo:** Panel completo para Crear, Leer, Actualizar y Borrar (CRUD) ejercicios y retos.
- **Integridad de Datos:** Lógica de borrado seguro (no permite eliminar ejercicios en uso y/o desafíos ya publicados).
- **Gestión de Usuarios:** Control de roles y administración de la base de datos de usuarios.

## 🛠️ Tecnologías utilizadas

| **Lenguaje** | Java |
| **Arquitectura** | Repository Pattern + LiveData + Observers |
| **Base de Datos** | Firebase Cloud Firestore (NoSQL) |
| **Autenticación** | Firebase Auth |
| **UI/UX** | View Binding, Navigation Component, Shimmer (Skeletons) |
| **Multimedia** | Picasso (Imágenes) y Lottie (Animaciones) |
| **Notificaciones** | Firebase Cloud Messaging (FCM) |

## 🧪 Testing

CrossWarr cuenta con una robusta batería de pruebas para garantizar la estabilidad del sistema:

### Pruebas de Unidad (Unit Tests)
Ubicadas en `src/test/java`, utilizan **JUnit 4** y **Mockito** para validar:
- Lógica de filtrado de ejercicios (con/sin material).
- Generación de identificadores únicos para retos.
- Persistencia de preferencias de usuario.
- Validación de formularios de entrada.

### Pruebas de Integración (UI Tests)
Ubicadas en `src/androidTest/java`, utilizan **Espresso** para verificar flujos completos:
- Flujo de Login y navegación exitosa a la pantalla principal.
- Interacción y limpieza de formularios en la creación de ejercicios.

## ⚙️ Instalación y Configuración

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/AdriBelSer/CrossWarr.git
