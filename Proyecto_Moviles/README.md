# Proyecto: Red y Seguridad

Aplicación Android desarrollada en **Kotlin** para la asignatura **FIS - Programación de Aplicaciones Móviles** de la Escuela Politécnica Nacional.

## 📋 Descripción del Proyecto

Este proyecto implementa dos módulos principales:

### **Módulo 1: Conectividad REST**
- **Objetivo:** Implementar peticiones HTTP asíncronas con JSONPlaceholder
- **Tecnologías:**
  - Retrofit 2.9.0 para cliente HTTP
  - OkHttp 4.11.0 para configuración de red
  - Coroutines para operaciones asincrónicas
  - Gson para serialización de datos

**Funcionalidades:**
- ✅ GET: Obtener un post por ID
- ✅ GET: Obtener todos los posts
- ✅ POST: Crear nuevo post
- ✅ PUT: Actualizar post existente
- ✅ DELETE: Eliminar post
- ✅ Manejo de estados de carga (loading)
- ✅ Captura de errores

### **Módulo 3: Almacenamiento Seguro**
- **Objetivo:** Implementar tres mecanismos de persistencia de datos con diferentes niveles de seguridad
- **Tecnologías:**
  - SharedPreferences (Texto plano)
  - Jetpack DataStore (Reactivo con Flows)
  - EncryptedSharedPreferences (Cifrado AES-256)

**Mecanismos Implementados:**

1. **SharedPreferences** (Sincrónico/Texto Plano)
   - Almacenamiento clave-valor directo
   - Ideal para preferencias no sensibles

2. **Jetpack DataStore** (Asincrónico/Reactivo)
   - Alternativa moderna y reactiva a SharedPreferences
   - Integración con Kotlin Flows
   - Evita bloqueos del hilo principal

3. **EncryptedSharedPreferences** (Sincrónico/Cifrado)
   - Cifrado automático con AES-256 SIV y AES-128 GCM
   - Ideal para tokens JWT y credenciales
   - Gestión automática de claves con MasterKey

## 🏗️ Estructura del Proyecto

```
com.example.redyseguridad/
├── api/
│   ├── JsonPlaceholderService.kt       # Interfaz Retrofit
│   └── RetrofitClient.kt               # Cliente HTTP configurado
├── model/
│   └── Post.kt                         # Modelo de datos
├── storage/
│   ├── SharedPreferencesManager.kt     # Gestor SharedPreferences
│   ├── DataStoreManager.kt             # Gestor DataStore
│   └── EncryptedSharedPreferencesManager.kt # Gestor Encrypted
├── viewmodel/
│   ├── RestApiViewModel.kt             # ViewModel REST
│   └── SecurityStorageViewModel.kt     # ViewModel Almacenamiento
├── ui/
│   ├── MainActivity.kt                 # Pantalla principal
│   ├── RestApiActivity.kt              # Actividad REST
│   └── SecurityStorageActivity.kt      # Actividad Almacenamiento
└── res/
    ├── layout/
    │   ├── activity_main.xml
    │   ├── activity_rest_api.xml
    │   └── activity_security_storage.xml
    └── values/
        ├── strings.xml
        └── themes.xml
```

## 🚀 Dependencias Principales

```gradle
// Lifecycle & MVVM
androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0
androidx.lifecycle:lifecycle-livedata-ktx:2.7.0

// Network
com.squareup.retrofit2:retrofit:2.9.0
com.squareup.retrofit2:converter-gson:2.9.0
com.squareup.okhttp3:okhttp:4.11.0
com.squareup.okhttp3:logging-interceptor:4.11.0

// Storage
androidx.datastore:datastore-preferences:1.0.0
androidx.security:security-crypto:1.1.0-alpha06

// Async
org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3
```

## 🎯 Casos de Uso

### REST API
1. Usuario ingresa ID de post (1-100)
2. Presiona "Obtener Post"
3. Aplicación realiza GET a `https://jsonplaceholder.typicode.com/posts/{id}`
4. Se muestra título y contenido del post
5. Usuario puede crear, actualizar o eliminar posts

### Almacenamiento Seguro
1. Usuario ingresa clave y valor
2. Elige mecanismo: SharedPreferences, DataStore o EncryptedSharedPreferences
3. Presiona "Guardar"
4. Presiona "Cargar" para recuperar el valor
5. EncryptedSharedPreferences muestra "(CIFRADO)" para tokens sensibles

## 📊 Rúbrica de Evaluación (60%)

- **30%** - Módulo 1 (REST API)
  - Petición GET/PUT con JSONPlaceholder
  - Control de estados de carga en widgets

- **30%** - Módulo 3 (Seguridad)
  - Integración correcta: SharedPreferences, DataStore, EncryptedSharedPreferences
  - Uso correcto de Kotlin Flows con DataStore

- **20%** - Gestión de Estado
  - Reactividad de datos instantánea sin caídas de proceso

- **20%** - Sustentación
  - Explicación técnica de la correspondencia entre el framework móvil y capacidades de Android

## 🔒 Características de Seguridad

- ✅ HTTPS en todas las peticiones
- ✅ Cifrado automático AES-256 en EncryptedSharedPreferences
- ✅ MasterKey gestionada automáticamente por Android
- ✅ Seguimiento del principio de "conocimiento previo de llave"
- ✅ Manejo seguro de tokens JWT

## 📝 Requisitos

- Android Studio Arctic Fox o superior
- Android SDK 34 (Compilación)
- Android 6.0 (API 24) o superior (Ejecución)
- Kotlin 1.9.21+
- Gradle 8.2.0+

## 🏃 Cómo Ejecutar

1. Clonar/Descargar el proyecto
2. Abrir en Android Studio
3. Sincronizar Gradle
4. Ejecutar en emulador o dispositivo físico
5. Permitir permisos de red e internet

## ✨ Características Implementadas

- [x] Conectividad asincrónica HTTP REST
- [x] Peticiones GET, POST, PUT, DELETE
- [x] Manejo de errores y excepciones
- [x] Estados de carga (ProgressBar)
- [x] SharedPreferences para datos simples
- [x] Jetpack DataStore con Kotlin Flows
- [x] EncryptedSharedPreferences con cifrado
- [x] MVVM Architecture
- [x] LiveData para observación reactiva
- [x] ViewModels para persistencia de estado
- [x] Coroutines para operaciones asincrónicas

## 📚 Referencias

- [JSONPlaceholder API](https://jsonplaceholder.typicode.com/)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Android DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [EncryptedSharedPreferences](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

## 👨‍🎓 Autor

Proyecto realizado para **FIS - Programación de Aplicaciones Móviles**
Escuela Politécnica Nacional (EPN)

---

**Versión:** 1.0 | **Estado:** Completado ✅
