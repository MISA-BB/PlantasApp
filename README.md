# 🌱 PlantaApp — Gestión Inteligente de Jardín con IA

---

## 👥 Integrantes del Equipo

| Nombre Completo                     | Matrícula    |
|:------------------------------------|:-------------|
| *Lucero Bahena Santana*             | *20253ds087* |
| *Melisa Casandra Sanchez Vazquez*   | *20253ds083* |
| *Arantza Saddai Hernandez Martinez* | *20253ds071* |


---

## 📝 Descripción de la Solución

**PlantaApp** es una solución móvil desarrollada en **Kotlin (Jetpack Compose)** conectada a una API REST en **Spring Boot (Java)** y **MySQL**, diseñada para facilitar el cuidado y seguimiento de plantas domésticas mediante visión por computadora e inteligencia artificial.

Al tomar o seleccionar una fotografía de cualquier planta, el sistema la envía a la API de **Google Gemini**, la cual identifica la especie (nombre común y científico) y genera una ficha personalizada de cuidados (frecuencia de riego, cantidad de agua, iluminación, temperatura, humedad y tipo de suelo). Antes de registrar la planta en el jardín del usuario, la aplicación presenta una vista previa editable para confirmar o descartar los datos y automatiza posteriormente el cálculo de próximos riegos y su historial.

---

## 🛠️ Tecnologías Utilizadas

* **Frontend Móvil (`PlantaApp`):** Kotlin, Jetpack Compose (Material 3), Arquitectura MVVM, Retrofit 2 + OkHttp.
* **Backend (`plantasBackend`):** Java 17+, Spring Boot (Spring Web, Spring Data JPA), Maven.
* **Base de Datos:** MySQL 8.0 (`plantasdb`).
* **Inteligencia Artificial:** Google Gemini API (`gemini-3.8-flash`).

---

## 🚀 Instrucciones Básicas de Ejecución

### 1. Requisitos Previos
* **Java JDK 17** (o superior) e **IntelliJ IDEA**.
* **MySQL Server 8.0** activo en `localhost:3306`.
* **Android Studio** y un dispositivo físico Android (o emulador) conectado en la misma red Wi-Fi.
* Una clave válida de **Google AI Studio (Gemini API)**.

### 2. Levantar el Backend (`plantasBackend`)
1. Abre el proyecto en **IntelliJ IDEA**.
2. Configura la variable de entorno con tu API Key en **Run > Edit Configurations > Environment variables**:
   ```text
   GEMINI_API_KEY=tu_clave_de_gemini
---

## 📡 Documentación de Endpoints (API REST)

**URL Base:** `http://<IP_SERVIDOR>:8080`

### Autenticación (`/api/auth`)

| Método | Endpoint | Descripción | Cuerpo / Parámetros |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Registra un nuevo usuario en el sistema. | **JSON:** `{ "fullName": "...", "correo": "...", "password": "..." }` |
| `POST` | `/api/auth/login` | Valida credenciales y retorna los datos del usuario. | **JSON:** `{ "correo": "...", "password": "..." }` |

### Gestión de Plantas (`/api/plantas`)

| Método | Endpoint | Descripción | Cuerpo / Parámetros |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/plantas/analizar` | **Paso 1:** Envía la imagen a Gemini para identificar especie y cuidados sin guardar en MySQL. | **Multipart (`form-data`):** `usuarioId` (text), `imagen` (file JPEG) |
| `POST` | `/api/plantas/guardar` | **Paso 2:** Guarda definitivamente en MySQL la planta confirmada por el usuario. | **JSON (`PlantaDto`):** Objeto completo de la planta analizada |
| `GET` | `/api/plantas/usuario/{usuarioId}` | Lista todas las plantas registradas por un usuario (ordenadas por fecha de registro). | **Path:** `usuarioId` (`Long`) |
| `GET` | `/api/plantas/{id}` | Obtiene el detalle completo de una planta por su ID. | **Path:** `id` (`Long`) |
| `PUT` | `/api/plantas/{id}` | Actualiza el nombre común de una planta existente. | **Path:** `id` (`Long`) • **JSON:** `{ "nombreComun": "..." }` |
| `POST` | `/api/plantas/{id}/regar` | Registra un riego hoy, recalcula el próximo riego y crea un registro en el historial. | **Path:** `id` (`Long`) |
| `DELETE` | `/api/plantas/{id}` | Elimina una planta del jardín del usuario. | **Path:** `id` (`Long`) |

### Historial de Riegos (`/api/riegos`)

| Método | Endpoint | Descripción | Cuerpo / Parámetros |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/riegos/usuario/{usuarioId}` | Devuelve el historial cronológico de todos los riegos realizados por el usuario. | **Path:** `usuarioId` (`Long`) |