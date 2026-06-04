# 🏗️ Arquitectura Técnica - Red y Seguridad

## Diagrama de Capas (MVVM + Clean Architecture)

```
┌─────────────────────────────────────────────────────────────┐
│                       PRESENTACIÓN (UI Layer)               │
│  ┌──────────────────┐  ┌──────────────────────────────────┐ │
│  │ MainActivity     │  │ Activities                       │ │
│  │                  │  │ - RestApiActivity               │ │
│  │ [Navigation]     │  │ - SecurityStorageActivity       │ │
│  └──────────────────┘  └──────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────┘
           │                          │
           ▼                          ▼
┌──────────────────────┐  ┌──────────────────────────────────┐
│  PRESENTACIÓN        │  │  PRESENTACIÓN                    │
│  RestApiActivity     │  │  SecurityStorageActivity        │
│  (UI & Binding)      │  │  (UI & Binding)                 │
└──────────────────────┘  └──────────────────────────────────┘
           │                          │
           ▼                          ▼
┌──────────────────────┐  ┌──────────────────────────────────┐
│ DOMAIN (ViewModel)   │  │ DOMAIN (ViewModel)               │
│                      │  │                                  │
│ RestApiViewModel     │  │ SecurityStorageViewModel         │
│ - post: LiveData     │  │ - sharedPrefData: LiveData       │
│ - posts: LiveData    │  │ - dataStoreData: LiveData        │
│ - isLoading: LiveData│  │ - encryptedData: LiveData        │
│ - error: LiveData    │  │ - statusMessage: LiveData        │
└──────────────────────┘  └──────────────────────────────────┘
           │                          │
           ▼                          ▼
┌──────────────────────┐  ┌──────────────────────────────────┐
│ REPOSITORY (Service) │  │ REPOSITORY (Manager)             │
│                      │  │                                  │
│ RetrofitClient       │  │ - SharedPreferencesManager       │
│ JsonPlaceholder      │  │ - DataStoreManager               │
│ Service Interface    │  │ - EncryptedSharedPrefManager     │
└──────────────────────┘  └──────────────────────────────────┘
           │                          │
           ▼                          ▼
┌──────────────────────┐  ┌──────────────────────────────────┐
│ DATA SOURCE (Remote) │  │ DATA SOURCE (Local)              │
│                      │  │                                  │
│ JSONPlaceholder API  │  │ Android Shared Storage:          │
│ https://json...      │  │ - /data/data/app/shared_prefs/   │
│                      │  │ - datastore/                     │
│                      │  │ - security-crypto/               │
└──────────────────────┘  └──────────────────────────────────┘
           │                          │
           ▼                          ▼
┌──────────────────────┐  ┌──────────────────────────────────┐
│ EXTERNAL SERVICE     │  │ ANDROID FRAMEWORK                │
│                      │  │                                  │
│ REST API             │  │ SharedPreferences (XML)          │
│ (HTTPs/TLS)          │  │ DataStore (ProtoBuffer)          │
│                      │  │ Keystore (Encrypted)            │
└──────────────────────┘  └──────────────────────────────────┘
```

---

## Flujo de Datos: GET Post (REST API)

```
┌─────────────────────────────────────────────────────────────┐
│ PASO 1: Usuario Interacción                                 │
└─────────────────────────────────────────────────────────────┘
    User Input: Ingresa ID "5" en EditText
    User Action: Presiona botón "Obtener Post"
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 2: UI Event Handler (RestApiActivity)                  │
└─────────────────────────────────────────────────────────────┘
    binding.btnGetPost.setOnClickListener {
        val postId = binding.etPostId.text.toString().toIntOrNull() ?: 1
        viewModel.getPostById(postId)  ◄── Llama ViewModel
    }
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 3: ViewModel (RestApiViewModel)                        │
└─────────────────────────────────────────────────────────────┘
    fun getPostById(postId: Int) {
        viewModelScope.launch {  ◄── Corrutina en Background
            _isLoading.value = true  ◄── Emit Loading State
            try {
                val result = apiService.getPost(postId)  ◄── Suspend Call
                _post.value = result  ◄── Emit Success
            } catch (e: Exception) {
                _error.value = e.message  ◄── Emit Error
            }
        }
    }
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 4: Repository (Retrofit Service)                       │
└─────────────────────────────────────────────────────────────┘
    @GET("posts/{id}")
    suspend fun getPost(@Path("id") postId: Int): Post
    
    ◄── Construye URL: posts/5
    ◄── Serializa parámetros
    ◄── Configura headers
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 5: HTTP Client (OkHttp + Interceptors)                 │
└─────────────────────────────────────────────────────────────┘
    HttpLoggingInterceptor.Level.BODY
    ◄── Loguea request completo
    ◄── Configura timeouts (30s)
    ◄── Configura SSL/TLS
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 6: Red (HTTPS Request)                                 │
└─────────────────────────────────────────────────────────────┘
    GET https://jsonplaceholder.typicode.com/posts/5 HTTP/1.1
    Host: jsonplaceholder.typicode.com
    Content-Type: application/json
    
    ◄── TLS/SSL Handshake
    ◄── Envía petición
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 7: Servidor Remoto (JSONPlaceholder)                   │
└─────────────────────────────────────────────────────────────┘
    Procesa GET /posts/5
    Devuelve JSON:
    {
      "userId": 1,
      "id": 5,
      "title": "nesciunt quas odio",
      "body": "repudiandae veniam quam..."
    }
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 8: Deserialización (Gson)                              │
└─────────────────────────────────────────────────────────────┘
    JSON String → Post data class
    
    Post(
      userId = 1,
      id = 5,
      title = "nesciunt quas odio",
      body = "repudiandae veniam..."
    )
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 9: LiveData Emission (ViewModel)                       │
└─────────────────────────────────────────────────────────────┘
    _post.value = result  ◄── Emite resultado
    _isLoading.value = false  ◄── Finaliza loading
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 10: UI Observation (Activity)                          │
└─────────────────────────────────────────────────────────────┘
    viewModel.post.observe(this) { post ->
        binding.tvPostContent.text = 
            "ID: ${post.id}\nTítulo: ${post.title}\n..."
    }
    
    ◄── TextView se actualiza automáticamente
    ◄── ProgressBar desaparece
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ RESULTADO: Usuario ve el Post en Pantalla                   │
└─────────────────────────────────────────────────────────────┘
    ID: 5
    Título: nesciunt quas odio
    Contenido: repudiandae veniam quam...
```

---

## Flujo de Datos: Guardar Secreto (EncryptedSharedPreferences)

```
┌─────────────────────────────────────────────────────────────┐
│ PASO 1: Usuario Ingresa Datos Sensibles                     │
└─────────────────────────────────────────────────────────────┘
    Clave: "jwt_token"
    Valor: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    Presiona: "Guardar Cifrado"
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 2: Activity Event Handler                              │
└─────────────────────────────────────────────────────────────┘
    binding.btnSaveEncrypted.setOnClickListener {
        val key = binding.etKeyEncrypted.text.toString()
        val value = binding.etValueEncrypted.text.toString()
        viewModel.saveToEncryptedPreferences(key, value)
    }
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 3: ViewModel (viewModelScope.launch)                   │
└─────────────────────────────────────────────────────────────┘
    viewModelScope.launch {
        encryptedManager.saveString(key, value)
        _statusMessage.value = "Guardado en EncryptedSharedPreferences"
        _encryptedData.value = value
    }
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 4: EncryptedSharedPreferencesManager                    │
└─────────────────────────────────────────────────────────────┘
    fun saveString(key: String, value: String) {
        encryptedSharedPreferences.edit()
            .putString(key, value)
            .apply()
    }
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 5: Android Keystore (MasterKey Generation)             │
└─────────────────────────────────────────────────────────────┘
    MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    ◄── Genera MasterKey en Keystore seguro
    ◄── No es exportable
    ◄── Protegido por Hardware si disponible
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 6a: Cifrado de Clave (AES-256-SIV)                     │
└─────────────────────────────────────────────────────────────┘
    Entrada: "jwt_token" (clave)
         │
         ▼ (AES-256-SIV)
    Salida: "x7f9a2e1d4b8c..." (clave cifrada)
    
    ◄── Determinístico (misma entrada = mismo output)
    ◄── Seguro contra ataques de diccionario
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 6b: Cifrado de Valor (AES-128-GCM)                     │
└─────────────────────────────────────────────────────────────┘
    Entrada: "eyJhbGciOi..." (valor/token)
         │
         ▼ (AES-128-GCM + IV aleatorio)
    Salida: "9k2m5p7q1r3s..." (valor cifrado)
    
    ◄── GCM proporciona autenticación
    ◄── IV aleatorio cada vez
    ◄── Protección contra modificación
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 7: Almacenamiento en Disco                             │
└─────────────────────────────────────────────────────────────┘
    Archivo: /data/data/com.example.redyseguridad/
             shared_prefs/encrypted_preferences.xml
    
    Contenido (ilegible sin MasterKey):
    <map>
      <string name="x7f9a2e1d...">9k2m5p7q1r3s...</string>
    </map>
    
    ◄── Sin MasterKey = No se puede descifrar
    ◄── Protegido contra extracción física
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 8: Respuesta del ViewModel                             │
└─────────────────────────────────────────────────────────────┘
    _statusMessage.value = 
        "Guardado en EncryptedSharedPreferences (Cifrado)"
    
    ◄── Emite estado
    ◄── Actualiza UI
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ PASO 9: Observación en Activity                             │
└─────────────────────────────────────────────────────────────┘
    viewModel.statusMessage.observe(this) { msg ->
        binding.tvStatus.text = msg
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
    
    viewModel.encryptedData.observe(this) { data ->
        binding.tvEncryptedResult.text = 
            "Valor: $data (CIFRADO) ✓"
    }
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ RESULTADO: Usuario ve confirmación de cifrado               │
└─────────────────────────────────────────────────────────────┘
    "Guardado en EncryptedSharedPreferences (Cifrado)"
    "Valor: eyJhbGciOi... (CIFRADO) ✓"
    
    Token guardado SEGURO en disco
    Sin acceso sin MasterKey del dispositivo
```

---

## Comparación: SharedPreferences vs DataStore vs EncryptedSharedPreferences

```
┌─────────────────┬──────────────────┬──────────────┬──────────────────┐
│ CARACTERÍSTICA  │ SharedPreferences │ DataStore    │ Encrypted        │
├─────────────────┼──────────────────┼──────────────┼──────────────────┤
│ SINCRÓNICO      │ ✅ Sí            │ ❌ No        │ ✅ Sí            │
│ ASINCRÓNICO     │ ❌ No            │ ✅ Sí        │ ❌ No            │
│ REACTIVO        │ ❌ No            │ ✅ Flows     │ ❌ No            │
│ CIFRADO         │ ❌ No            │ ❌ No        │ ✅ AES-256       │
│ BLOQUEA HILO    │ ⚠️ Sí (puede)    │ ❌ No        │ ⚠️ Sí (puede)    │
│ TRANSACCIONES   │ ❌ No            │ ✅ ACID      │ ❌ No            │
│ MODERNO         │ ⚠️ Antiguo       │ ✅ Moderno   │ ✅ Moderno       │
│ GOOGLE RECOMEND │ ❌ Deprecado     │ ✅ Sí        │ ✅ Sí            │
│ PARA TOKENS     │ ❌ NO!           │ ⚠️ Posible   │ ✅ SÍ (Ideal)    │
│ PARA PREFS      │ ✅ Sí            │ ✅ Sí        │ ❌ No (too much) │
│ PARA DATOS LIVE │ ❌ No            │ ✅ Sí        │ ❌ No            │
├─────────────────┼──────────────────┼──────────────┼──────────────────┤
│ FORMATO DISCO   │ XML              │ ProtoBuffer  │ XML              │
│ LOCALIZACIÓN    │ shared_prefs/    │ datastore/   │ security-crypto/ │
└─────────────────┴──────────────────┴──────────────┴──────────────────┘

MATRIZ DE DECISIÓN:

¿Datos sensibles (tokens, API keys, credenciales)?
    → EncryptedSharedPreferences

¿Datos que cambian frecuentemente y necesitan observación?
    → Jetpack DataStore

¿Preferencias simples de configuración?
    → SharedPreferences

¿Necesita ser reactivo y moderno?
    → Jetpack DataStore

¿Requiere acceso rápido síncrono?
    → SharedPreferences o EncryptedSharedPreferences
```

---

## Seguridad: Cifrado AES-256

```
INFORMACIÓN SENSIBLE:
┌──────────────────────────────────────────┐
│ Token JWT: eyJhbGciOiJIUzI1NiIsInR5...  │
│ API Key: sk_live_51HfLCBEzGGH...         │
│ Password: MySecurePass123!               │
└──────────────────────────────────────────┘
              │
              ▼
┌──────────────────────────────────────────┐
│ PASO 1: MasterKey (Keystore Android)     │
│                                          │
│ Generado una sola vez                    │
│ Almacenado en Keystore del dispositivo  │
│ Protegido por hardware si disponible     │
└──────────────────────────────────────────┘
              │
              ▼
┌──────────────────────────────────────────┐
│ PASO 2: Cifrado de Clave (AES-256-SIV)   │
│                                          │
│ Entrada: "api_key"                       │
│ Algoritmo: AES-256 SIV                   │
│ Salida: "8f4a9e2d7b1c..."                │
│ Ventaja: Determinístico y seguro         │
└──────────────────────────────────────────┘
              │
              ▼
┌──────────────────────────────────────────┐
│ PASO 3: Cifrado de Valor (AES-128-GCM)   │
│                                          │
│ Entrada: "sk_live_51HfLCB..."            │
│ Algoritmo: AES-128 GCM + IV aleatorio   │
│ Salida: "x7y8z9a0b1c2d3e..."             │
│ Ventaja: Autenticación incluida          │
└──────────────────────────────────────────┘
              │
              ▼
┌──────────────────────────────────────────┐
│ ALMACENAMIENTO EN DISCO                  │
│                                          │
│ /data/data/app/shared_prefs/             │
│     encrypted_preferences.xml            │
│                                          │
│ Sin MasterKey = IRRECUPERABLE            │
│ Incluso con root = Inútil sin Keystore   │
└──────────────────────────────────────────┘
              │
              ▼
┌──────────────────────────────────────────┐
│ LECTURA: Desencriptación automática      │
│                                          │
│ 1. Recuperar MasterKey del Keystore      │
│ 2. Desencriptar clave (AES-256-SIV)      │
│ 3. Desencriptar valor (AES-128-GCM)      │
│ 4. Retornar valor original               │
│ 5. Todo transparente para la app         │
└──────────────────────────────────────────┘

PROTECCIÓN CONTRA:
✅ Robo físico del dispositivo
✅ Análisis de memoria
✅ Extracción de datos
✅ Man-in-the-Middle (localizado)
✅ Ataques de diccionario (SIV)
✅ Modificación de datos (GCM)

NO PROTEGE CONTRA:
❌ App con permisos en el dispositivo
❌ Process memory dump mientras se usa
❌ Keylogger o malware instalado
```

---

## Integración Corrutinas + Lifecycle

```
ViewModel Scope Lifecycle:
┌─────────────────────────────────────────┐
│ Activity/Fragment Created                │
│         │                               │
│         ▼                               │
│ ViewModel Created                       │
│ viewModelScope Inicializado             │
│         │                               │
│         ▼                               │
│ Activity/Fragment Activo                │
│ (Corrutinas pueden ejecutarse)          │
│         │                               │
│         ▼                               │
│ Activity/Fragment Destroyed             │
│ viewModelScope.cancel() automático      │
│ Todas las corrutinas se cancelan        │
│ Evita memory leaks                      │
└─────────────────────────────────────────┘

EJEMPLO:

fun getPostById(postId: Int) {
    viewModelScope.launch {  ◄── Vinculado al scope del VM
        try {
            val result = apiService.getPost(postId)
            _post.value = result
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
}

Si Activity es destroyed mientras se ejecuta:
├─ viewModelScope.cancel() se llama automáticamente
├─ La corrutina se cancela
├─ No se emite resultado
├─ No se hace setState en UI destruida
└─ ✅ Sin crash, sin memory leak
```

---

## Patrón Observador: LiveData

```
┌──────────────────────────────────────────┐
│ MutableLiveData (Productor)              │
│                                          │
│ private val _post = MutableLiveData()   │
│ val post: LiveData = _post              │
│                                          │
│ _post.value = newValue                  │
│         │ (Notifica a observadores)     │
│         ▼                               │
├──────────────────────────────────────────┤
│ Activity (Consumidor)                    │
│                                          │
│ viewModel.post.observe(this) { post ->  │
│     updateUI(post)                      │
│ }                                        │
│         ▲                               │
│         │ (Observador se registra)      │
│         │ (Automáticament desuscribe    │
│         │  cuando Activity es destroyed)│
└──────────────────────────────────────────┘

VENTAJAS:
✅ Lifecycle aware (no necesita manual unsubscribe)
✅ Evita memory leaks
✅ Actualización automática si Activity está activa
✅ Thread-safe
✅ Funciona con rotación de pantalla
```

---

## Resumen Arquitectónico

### Patrones Implementados

1. **MVVM (Model-View-ViewModel)**
   - Separación de responsabilidades
   - ViewModel gestiona estado
   - Activity/Fragment solo muestra UI

2. **Repository Pattern**
   - ApiService (Remote)
   - Storage Managers (Local)
   - ViewModel consume ambos

3. **Coroutines + Async**
   - viewModelScope para lifecycle safety
   - Suspend functions para operaciones largas
   - Flows para datos reactivos

4. **Reactive Programming**
   - LiveData para estados
   - Flow para cambios continuos
   - Observers automáticos

5. **Dependency Injection (Manual)**
   - RetrofitClient singleton
   - Storage managers en ViewModel
   - Factory pattern en ViewModelProvider

### Seguridad en Capas

```
┌──────────────────┐
│ HTTPS/TLS        │ ◄── Comunicación
├──────────────────┤
│ Validación JSON  │ ◄── Validación
├──────────────────┤
│ ViewModel Check  │ ◄── Lógica
├──────────────────┤
│ AES-256 Cifrado  │ ◄── Almacenamiento
├──────────────────┤
│ Android Keystore │ ◄── Infraestructura
└──────────────────┘
```

---

**Documentación Técnica Completa**
**Versión:** 1.0 | **Fecha:** 30 de Mayo de 2026
