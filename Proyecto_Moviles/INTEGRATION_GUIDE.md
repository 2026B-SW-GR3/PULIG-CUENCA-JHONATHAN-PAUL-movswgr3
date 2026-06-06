# 📖 Guía de Integración y Uso - Red y Seguridad

## 🎯 Resumen General

Este proyecto implementa una aplicación Android completa con:
1. **Conectividad REST asincrónica** con JSONPlaceholder API
2. **Tres mecanismos de almacenamiento seguro** con diferentes niveles de encriptación

---

## 📦 Módulo 1: Conectividad REST

### Arquitectura

```
RestApiActivity (UI)
    ↓
RestApiViewModel (State Management)
    ↓
RetrofitClient (HTTP Client)
    ↓
JsonPlaceholderService (API Interface)
    ↓
https://jsonplaceholder.typicode.com/
```

### Ejemplo de Uso - Obtener un Post

```kotlin
// En RestApiActivity
binding.btnGetPost.setOnClickListener {
    val postId = binding.etPostId.text.toString().toIntOrNull() ?: 1
    viewModel.getPostById(postId)  // Corrutina en background
}

// En RestApiViewModel
fun getPostById(postId: Int) {
    viewModelScope.launch {
        try {
            _isLoading.value = true
            _error.value = null
            val result = apiService.getPost(postId)  // Suspend function
            _post.value = result
        } catch (e: Exception) {
            _error.value = "Error: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
}

// En RestApiActivity - Observar cambios
viewModel.post.observe(this) { post ->
    if (post != null) {
        binding.tvPostContent.text = 
            "ID: ${post.id}\nTítulo: ${post.title}\n\nCuerpo: ${post.body}"
    }
}
```

### Endpoints Disponibles (JSONPlaceholder)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/posts/{id}` | Obtener un post por ID (1-100) |
| GET | `/posts` | Obtener todos los posts (100 posts) |
| POST | `/posts` | Crear un nuevo post |
| PUT | `/posts/{id}` | Actualizar un post existente |
| DELETE | `/posts/{id}` | Eliminar un post |

### Configuración de Retrofit

```kotlin
// RetrofitClient.kt
private val retrofit: Retrofit by lazy {
    Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}

// Características:
// - Timeout: 30 segundos
// - Logging automático (BODY level)
// - Serialización/Deserialización JSON automática
// - SSL/TLS (HTTPS)
```

---

## 🔐 Módulo 3: Almacenamiento Seguro

### 1️⃣ SharedPreferences (Texto Plano)

**Uso:** Preferencias de aplicación, configuraciones no sensibles.

**Características:**
- Sincrónico
- Acceso rápido
- Sin encriptación
- Almacenamiento XML directo

**Ejemplo:**

```kotlin
// Guardar
sharedPreferencesManager.saveString("user_name", "Juan")
sharedPreferencesManager.saveInt("user_id", 123)

// Cargar
val name = sharedPreferencesManager.getString("user_name")
val id = sharedPreferencesManager.getInt("user_id")

// Guardar objeto
data class User(val name: String, val id: Int)
sharedPreferencesManager.saveObject("user", User("Juan", 123))

// Cargar objeto
val user = sharedPreferencesManager.getObject<User>("user")
```

### 2️⃣ Jetpack DataStore (Reactivo con Flows)

**Uso:** Almacenamiento moderno, reactivo, con manejo de preferencias complejas.

**Características:**
- Asincrónico
- Kotlin Flows (Reactivo)
- Transacciones ACID
- Evita bloqueos del hilo principal
- Protocolos Buffers

**Ejemplo:**

```kotlin
// Guardar (Suspend function)
viewModelScope.launch {
    dataStoreManager.saveString("auth_token", "eyJhbGc...")
}

// Observar cambios (Flow)
dataStoreManager.getStringFlow("auth_token")
    .collect { token ->
        println("Token actualizado: $token")
    }

// En UI con stateIn
val tokenFlow = dataStoreManager.getStringFlow("auth_token")
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = ""
    )
```

**Ventajas sobre SharedPreferences:**
- ✅ No bloquea el hilo UI
- ✅ Manejo seguro de concurrencia
- ✅ Actualización reactiva automática
- ✅ Soporte para transacciones

### 3️⃣ EncryptedSharedPreferences (Cifrado AES-256)

**Uso:** Almacenar datos sensibles (tokens JWT, credenciales, API keys).

**Características:**
- Sincrónico
- Cifrado automático AES-256 SIV (claves)
- Cifrado automático AES-128 GCM (valores)
- MasterKey gestionada por Android Keystore

**Ejemplo:**

```kotlin
// Guardar datos sensibles (CIFRADOS automáticamente)
encryptedManager.saveString("jwt_token", "eyJhbGciOiJIUzI1NiIs...")
encryptedManager.saveString("password", "miContraseñaSegura123")

// Cargar (Desencriptado automáticamente)
val token = encryptedManager.getString("jwt_token")
val password = encryptedManager.getString("password")

// Guardar objeto sensible
data class Credentials(val username: String, val password: String)
encryptedManager.saveObject("credentials", 
    Credentials("user@example.com", "P@ssw0rd!")
)

val creds = encryptedManager.getObject<Credentials>("credentials")
```

**Proceso de Cifrado:**

```
Dato Original → MasterKey (Keystore) → AES-256 SIV (clave)
                                      → AES-128 GCM (valor)
                                      → Almacenado Cifrado
                                      
Lectura:       Almacenado Cifrado → Keystore Desencripta MasterKey
                                  → AES-256 SIV (clave)
                                  → AES-128 GCM (valor)
                                  → Dato Original
```

---

## 🎨 Interfaz de Usuario

### Pantalla Principal (MainActivity)

```
┌─────────────────────────────────────────┐
│    Proyecto: Red y Seguridad            │
│                                         │
│  Conectividad asincrónica HTTP REST     │
│  con JSONPlaceholder...                 │
│                                         │
│  ┌─ Módulo 1: Conectividad REST ───┐   │
│  │                                 │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─ Módulo 3: Almacenamiento Seguro ┐  │
│  │                                  │  │
│  └──────────────────────────────────┘  │
│                                         │
│    FIS - Escuela Politécnica Nacional  │
└─────────────────────────────────────────┘
```

### Pantalla REST API (RestApiActivity)

```
┌─────────────────────────────────────────┐
│  Módulo 1: Conectividad REST            │
│                                         │
│  ID: [________] ◄─ Entrada Numérica    │
│                                         │
│  [Obtener POST] [Obtener Todos]        │
│                                         │
│  ┌─ Crear/Actualizar ────────────┐     │
│  │ Título: [_____________]       │     │
│  │ Contenido: [___________]      │     │
│  │ [Crear] [Actualizar] [Eliminar]│    │
│  └───────────────────────────────┘     │
│                                         │
│  ⏳ (ProgressBar si está cargando)     │
│                                         │
│  ┌─ Respuesta ────────────────────┐    │
│  │ ID: 1                          │    │
│  │ Título: sunt autem...          │    │
│  │ Contenido: quia voluptas...    │    │
│  └────────────────────────────────┘    │
└─────────────────────────────────────────┘
```

### Pantalla Almacenamiento Seguro (SecurityStorageActivity)

```
┌─────────────────────────────────────────┐
│  Módulo 3: Almacenamiento Seguro        │
│                                         │
│  📝 Status: Guardado en SharedPreferences│
│                                         │
│  ┌─ 1. SharedPreferences ─────────┐    │
│  │ Clave: [_____________]         │    │
│  │ Valor: [_____________]         │    │
│  │ [Guardar] [Cargar]             │    │
│  │ Resultado: miValor             │    │
│  └────────────────────────────────┘    │
│                                         │
│  ┌─ 2. Jetpack DataStore ────────┐     │
│  │ Clave: [_____________]         │    │
│  │ Valor: [_____________]         │    │
│  │ [Guardar] [Cargar]             │    │
│  │ Resultado: miDato (Reactivo)   │    │
│  └────────────────────────────────┘    │
│                                         │
│  ┌─ 3. EncryptedSharedPreferences ┐    │
│  │ Clave: [_____________]         │    │
│  │ Secreto: [*****] (Password)    │    │
│  │ [Guardar Cifrado] [Cargar]    │    │
│  │ Resultado: miToken (CIFRADO) ✓│    │
│  └────────────────────────────────┘    │
│                                         │
│  [Limpiar Todos los Datos]             │
└─────────────────────────────────────────┘
```

---

## 🔄 Flujo de Datos (MVVM)

### REST API Flow

```
UI Event (User Click)
    ↓
Activity.onClick()
    ↓
ViewModel.getPostById(id)
    ↓
viewModelScope.launch (Corrutine)
    ↓
apiService.getPost(id) [Suspend]
    ↓
Retrofit HTTP Request
    ↓
JSONPlaceholder Response
    ↓
_post.value = result (LiveData emit)
    ↓
Activity.observe() (Update UI)
```

### Storage Flow

```
UI Event (Save Button)
    ↓
Activity.saveToDataStore(key, value)
    ↓
ViewModel.saveToDataStore(key, value)
    ↓
viewModelScope.launch (Corrutine)
    ↓
dataStore.edit { preferences[key] = value }
    ↓
Storage Saved
    ↓
_dataStoreData.value = value (LiveData)
    ↓
Activity.observe() (Update UI)
    ↓
getStringFlow(key).collect() (Reactive Updates)
```

---

## 🛠️ Configuración de Dependencias

### Retrofit & Network

```gradle
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.11.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
```

### Storage

```gradle
implementation("androidx.datastore:datastore-preferences:1.0.0")
implementation("androidx.security:security-crypto:1.1.0-alpha06")
```

### Async & Lifecycle

```gradle
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

---

## 📝 Ejemplo Completo: Guardar y Recuperar Token JWT

```kotlin
// En Activity
val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

// 1. Guardar en EncryptedSharedPreferences (Seguro)
viewModel.saveToEncryptedPreferences("jwt_token", token)

// 2. Observar resultado
viewModel.statusMessage.observe(this) { msg ->
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    // "Guardado en EncryptedSharedPreferences (Cifrado)"
}

// 3. El token se cifra automáticamente:
// Token Original: eyJhbGciOi...
// ↓ Cifrado AES-256
// Token en Disco: 8f4a9e2d...x1f (Ilegible)

// 4. Recuperar (Desencriptar automático)
viewModel.loadFromEncryptedPreferences("jwt_token")

// 5. Ver resultado
viewModel.encryptedData.observe(this) { token ->
    println("Token recuperado y desencriptado: $token")
    // Automáticamente disponible para usar en headers
}

// 6. Usar en peticiones (Retrofit)
val request = HttpLoggingInterceptor()
// Headers con token recuperado
api.getSecureData(
    authorization = "Bearer $token"
)
```

---

## ⚠️ Buenas Prácticas

### ✅ HACER

- ✅ Usar EncryptedSharedPreferences para tokens, API keys, credenciales
- ✅ Usar DataStore para datos reactivos que cambian frecuentemente
- ✅ Usar SharedPreferences para preferencias simples (idioma, tema)
- ✅ Siempre usar corrutinas para operaciones de red
- ✅ Observar LiveData/Flow para actualizaciones de UI
- ✅ Manejar excepciones en try-catch
- ✅ Mostrar estados de carga (ProgressBar)

### ❌ EVITAR

- ❌ No guardar datos sensibles en SharedPreferences sin cifrar
- ❌ No hacer llamadas de red en el hilo principal
- ❌ No usar `.get()` blocking en Flow (usar `.collect()`)
- ❌ No ignorar excepciones de red
- ❌ No guardar contraseñas en variables globales
- ❌ No hacer peticiones HTTP sin timeout

---

## 🧪 Testing Rápido

### Probar REST API

1. Ingresa `5` en el campo ID
2. Presiona "Obtener Post"
3. Debe mostrar el post con ID 5 de JSONPlaceholder

**Esperado:**
```
ID: 5
Título: nesciunt quas odio
Contenido: repudiandae veniam...
```

### Probar Almacenamiento

1. **SharedPreferences:**
   - Clave: `mi_clave`
   - Valor: `texto_simple`
   - Resultado: `texto_simple` (sin cambios)

2. **DataStore:**
   - Clave: `datos_reactivos`
   - Valor: `dato_moderno`
   - Resultado: actualización reactiva automática

3. **EncryptedSharedPreferences:**
   - Clave: `token_secreto`
   - Valor: `abc123xyz`
   - Resultado: `abc123xyz (CIFRADO)` ✓ (El símbolo ✓ indica que fue encriptado)

---

## 🚀 Despliegue

### Generar APK

```bash
# Debug APK
./gradlew assembleDebug
# Resultado: app/build/outputs/apk/debug/app-debug.apk

# Release APK (Requiere firma)
./gradlew assembleRelease
# Resultado: app/build/outputs/apk/release/app-release.apk
```

### Instalar en dispositivo

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 📞 Soporte y Debugging

### Ver logs de red

Los logs de Retrofit se muestran automáticamente en Logcat:

```
D/OkHttp: --> GET https://jsonplaceholder.typicode.com/posts/1
D/OkHttp: <-- 200 OK https://jsonplaceholder.typicode.com/posts/1 (234ms)
D/OkHttp: {"userId":1,"id":1,"title":"...","body":"..."}
```

### Ver logs de almacenamiento

```kotlin
val value = dataStoreManager.getStringFlow("key").collect { value ->
    Log.d("DataStore", "Valor actualizado: $value")
}
```

---

## 📚 Referencias Completas

- [JSONPlaceholder API](https://jsonplaceholder.typicode.com)
- [Retrofit Docs](https://square.github.io/retrofit)
- [Android DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Security Crypto](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [MVVM Architecture](https://developer.android.com/topic/libraries/architecture)

---

**Versión:** 1.0 | **Actualizado:** Mayo 2026
