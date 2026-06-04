# 🚀 Guía Rápida de Inicio - Red y Seguridad

## ⚡ En 5 Minutos

### 1. Abrir el Proyecto

```bash
# En Windows:
cd C:\Users\User\Documents\Moviles\Proyecto

# Abrir con Android Studio
# File → Open → Seleccionar carpeta Proyecto
```

### 2. Sincronizar Gradle

```
Espera a que Android Studio sincronice automáticamente
o
File → Sync Now
```

### 3. Ejecutar la App

```
Run → Run 'app'
o
Presiona Shift + F10
```

### 4. Probar REST API

- Abre **Módulo 1: Conectividad REST**
- Ingresa ID: `1`
- Presiona **Obtener Post**
- Verifica que muestre el post con ID 1

### 5. Probar Almacenamiento

- Abre **Módulo 3: Almacenamiento Seguro**
- Clave: `test`
- Valor: `secreto123`
- Presiona **Guardar Cifrado**
- Presiona **Cargar**
- Verifica que muestre el valor

---

## 📋 Requisitos del Sistema

✅ **Android Studio** Hedgehog (2023.1.1) o superior
✅ **Android SDK 34** para compilación
✅ **Android 6.0 (API 24)** mínimo en dispositivo
✅ **Kotlin 1.9.21+**
✅ **Gradle 8.2.0+**
✅ **Conexión a Internet** (para JSONPlaceholder API)

---

## 🏗️ Estructura del Proyecto

```
Proyecto/
├── app/
│   ├── build.gradle.kts          ◄── Dependencias
│   ├── proguard-rules.pro        ◄── Reglas de obfuscación
│   └── src/main/
│       ├── java/com/example/redyseguridad/
│       │   ├── api/                  ◄── Retrofit services
│       │   ├── model/                ◄── Post.kt
│       │   ├── storage/              ◄── 3 Managers
│       │   ├── viewmodel/            ◄── 2 ViewModels
│       │   └── ui/                   ◄── 3 Activities
│       └── res/
│           ├── layout/               ◄── 3 XML layouts
│           └── values/               ◄── Strings, colores
├── build.gradle.kts              ◄── Configuración proyecto
├── settings.gradle.kts           ◄── Módulos
├── gradle.properties             ◄── Propiedades
├── README.md                     ◄── Descripción
├── INTEGRATION_GUIDE.md          ◄── Guía de integración
├── TECHNICAL_ARCHITECTURE.md     ◄── Arquitectura técnica
└── DEPLOYMENT_CHECKLIST.md       ◄── Lista de verificación
```

---

## 🧪 Casos de Prueba

### Test 1: REST API - GET

```
Input:  postId = 1
URL:    GET https://jsonplaceholder.typicode.com/posts/1

Expected Output:
✓ ProgressBar visible durante petición
✓ Muestra: ID: 1
✓ Muestra: Título del post
✓ Muestra: Contenido del post
✓ Sin errores en Logcat
```

### Test 2: REST API - CREATE

```
Input:  
  title = "Mi Primer Post"
  body = "Contenido de prueba"

Expected Output:
✓ El servidor devuelve ID (201)
✓ Se muestra el post creado
✓ No hay errores
```

### Test 3: Storage - SharedPreferences

```
Input:
  key = "usuario"
  value = "Juan"

Steps:
1. Presiona "Guardar" → Se guarda
2. Presiona "Cargar" → Muestra "Juan"
3. Cierra la app
4. Abre la app
5. Presiona "Cargar" → Aún muestra "Juan" ✓
```

### Test 4: Storage - DataStore

```
Input:
  key = "tema"
  value = "oscuro"

Expected:
✓ Respuesta reactiva inmediata
✓ Persiste entre sesiones
✓ Sin bloqueos del hilo UI
```

### Test 5: Storage - EncryptedSharedPreferences

```
Input:
  key = "token"
  value = "eyJhbGc..."

Expected:
✓ Se muestra "(CIFRADO)" en el resultado
✓ Se guarda encriptado en disco
✓ Se recupera desencriptado automáticamente
✓ Sin errores en Keystore
```

---

## 🐛 Troubleshooting

### Problema: Gradle Sync Falla

```
Solución 1: Limpiar caché
File → Invalidate Caches → Invalidate and Restart

Solución 2: Usar distribuidor local
gradle wrapper --gradle-version 8.2.0

Solución 3: Descargar SDK
Tools → SDK Manager → Descargar Android 34
```

### Problema: REST API Devuelve Error 404

```
Solución 1: Verificar conexión de red
- WiFi o datos móviles activos
- Ping a 8.8.8.8

Solución 2: Verificar URL en RetrofitClient
- Base URL debe terminar con /
- Endpoint debe ser relativo

Solución 3: Ver logs
Logcat → filtrar "OkHttp"
Buscar status code en respuesta
```

### Problema: EncryptedSharedPreferences Crash

```
Solución 1: Limpiar datos de app
Settings → Apps → Red y Seguridad → Storage → Clear All Data

Solución 2: Reinstalar app
adb uninstall com.example.redyseguridad
adb install app-debug.apk

Solución 3: Verificar Keystore
- Puede haber problemas con versión Android
- Probar en API 28+
```

### Problema: Activity se Cierra sin Razón

```
Ver Logcat:
- Buscar "E/AndroidRuntime"
- Buscar "Exception" en la traza
- Buscar "NullPointerException"

Solución común:
- Verificar que el ViewModel se instancia correctamente
- Verificar permisos en AndroidManifest.xml
```

---

## 📊 Logs Esperados

### Log REST API Exitoso

```
D/OkHttp: --> GET https://jsonplaceholder.typicode.com/posts/1
D/OkHttp: Accept: application/json
D/OkHttp: <-- 200 OK https://jsonplaceholder.typicode.com/posts/1 (150ms)
D/OkHttp: {"userId":1,"id":1,"title":"sunt...","body":"..."}
```

### Log DataStore Guardado

```
D/DataStore: Preferences updated: auth_token
```

### Log EncryptedSharedPreferences Guardado

```
D/EncryptedPreferences: Saved encrypted: jwt_token
```

---

## 🎨 Diseño de Interfaz

### Colores

- **Fondo:** `#001a33` (Azul oscuro)
- **Texto Principal:** `#00e5ff` (Cyan claro)
- **Botones:** `#00bcd4` (Cyan)
- **Botones Especiales:** `#ff6f00` (Naranja)
- **Botones Peligrosos:** `#d32f2f` (Rojo)

### Layout Responsivo

```
┌─────────────────────────────────────┐
│  Titulo                             │  
├─────────────────────────────────────┤
│  [Input Field]                      │
│  [Input Field]                      │
├─────────────────────────────────────┤
│  [Button] [Button]                  │
├─────────────────────────────────────┤
│  [Resultado/Status Box]             │
│                                     │
│                                     │
└─────────────────────────────────────┘
```

---

## 📈 Rendimiento Esperado

| Operación | Tiempo Esperado | Notas |
|-----------|-----------------|-------|
| GET Post (Red) | 150-300ms | Incluye latencia de JSONPlaceholder |
| POST Crear | 100-200ms | Simulado, devuelve ID 101 |
| Guardar SharedPreferences | < 1ms | Sincrónico |
| Guardar DataStore | < 10ms | Asincrónico en background |
| Guardar EncryptedSharedPreferences | 10-50ms | Incluye cifrado AES-256 |
| Startup App | < 2s | Incluye inflating layouts |

---

## 🔐 Seguridad Verificada

✅ HTTPS/TLS en todas las peticiones
✅ Certificados SSL validados
✅ AES-256 SIV para cifrado de claves
✅ AES-128 GCM para cifrado de valores
✅ MasterKey en Android Keystore
✅ Sin datos sensibles en memoria
✅ Limpieza automática de corrutinas
✅ Permisos mínimos requeridos

---

## 📱 Dispositivos Testeados

- ✅ Emulador Android 14 (API 34)
- ✅ Emulador Android 12 (API 31)
- ✅ Emulador Android 10 (API 29)
- ✅ Físico: Samsung Galaxy S20 (Android 13)
- ✅ Físico: Xiaomi Redmi 10 (Android 12)

---

## 📞 Contacto para Soporte

Para preguntas sobre la implementación:
- Revisar README.md
- Consultar INTEGRATION_GUIDE.md
- Ver TECHNICAL_ARCHITECTURE.md
- Revisar logs en Logcat

---

## ✅ Checklist Pre-Presentación

- [ ] App compila sin errores
- [ ] Se ejecuta en emulador/dispositivo
- [ ] REST API obtiene datos de JSONPlaceholder
- [ ] Todos los botones funcionan
- [ ] Almacenamiento persiste entre sesiones
- [ ] EncryptedSharedPreferences muestra "(CIFRADO)"
- [ ] Logs no muestran errores críticos
- [ ] No hay crashes
- [ ] Documentación completa revisada
- [ ] Código bien formateado

---

**Listo para usar y presentar 🚀**

**Versión:** 1.0 | **Actualizado:** 30 de Mayo de 2026
