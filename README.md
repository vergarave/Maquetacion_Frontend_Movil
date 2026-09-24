# Amanecer

Aplicación Android de tipo prototipo para visualización de UX, enfocada en una experiencia de alarma y ajustes móviles.

La app presenta un flujo de navegación simulado para:
- pantalla principal de alarmas
- edición de alarmas
- configuración y perfil
- ajustes de notificaciones
- cambio de contraseña

Es un prototipo visual, no está conectada a backend ni a servicios reales de autenticación o almacenamiento.

## Proyecto

Este repositorio contiene la implementación de la interfaz en Android usando Kotlin y Android Studio.

- Proyecto base: Android
- Módulo principal: `app`
- Nombre de paquete: `com.uniandes.maquetacion_frontend_movil`
- App: `Amanecer`

## Requisitos

- Android Studio
- JDK 11 o superior
- Android SDK configurado
- Dispositivo emulado o físico compatible

## Cómo correrlo

### Opción 1: desde Android Studio
1. Abre la carpeta del proyecto en Android Studio.
2. Espera a que sincronice Gradle.
3. Selecciona un dispositivo disponible:
   - emulador Pixel 8
   - o un Xiaomi Redmi con modo desarrollador activado
4. Presiona el botón Run o "Run 'app'".

### Opción 2: desde terminal
En Windows:

```bash
gradlew.bat installDebug
```

O bien, si prefieres compilar directamente con Android Studio, usa la opción de ejecución normal.

## Dispositivos donde se probó

Este proyecto se validó en dos entornos reales de ejecución:
- emulador Pixel 8 en Android Studio
- Xiaomi Redmi con modo desarrollador activado, conectado a Android Studio para ejecución directa

