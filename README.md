# 🏃 EnduranceCoach AI

![Angular](https://img.shields.io/badge/Angular-17+-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=java&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![OpenAI](https://img.shields.io/badge/OpenAI-412991?style=for-the-badge&logo=openai&logoColor=white)

**EnduranceCoach AI** es una plataforma web integral, diseñada para revolucionar la gestión deportiva en disciplinas de resistencia (running, triatlón, ciclismo). Esta aplicación combina la potencia de un backend robusto con la inteligencia de modelos **generativos (OpenAI)** para crear de forma automática planes de entrenamiento y nutrición hiper-personalizados, presentados en una interfaz moderna y premium.

---

## ✨ Características Principales

- 🤖 **Entrenamiento Impulsado por IA**: Generación de planes estructurados con métricas avanzadas (zonas de intensidad, FTP/RPE, calentamiento, bloques principales y enfriamiento) basándose en los objetivos del atleta.
- 🍏 **Planes de Nutrición Inteligentes**: Creación de estrategias nutricionales adaptadas al perfil del usuario, incluyendo pautas previas, hidratación y estrategias de día de carrera.
- 📊 **Seguimiento de Métricas**: Dashboard completo para que los atletas registren y visualicen sus métricas clave (peso, frecuencia cardíaca máxima, FTP).
- 🔒 **Seguridad y Autenticación**: Sistema completo de login/registro utilizando **JWT (JSON Web Tokens)** y contraseñas encriptadas, garantizando la privacidad de los datos.
- 🎨 **Diseño UX/UI Premium**: Interfaz moderna estilo Glassmorphism sobre fondos oscuros (Deep Slate) y acentos en Azul Premium (#38bdf8), ofreciendo un look and feel envolvente y 100% responsivo.

---

## 🛠 Arquitectura y Tecnologías

El sistema sigue una arquitectura monolítica modular completamente desacoplada cliente-servidor:

### Backend (Servidor)
- **Lenguaje:** Java 21
- **Framework Core:** Spring Boot 3.2
- **Seguridad:** Spring Security + JWT
- **Persistencia:** Spring Data JPA + Hibernate
- **Base de Datos:** PostgreSQL
- **IA Integration:** Spring AI / Cliente HTTP (Conexión a la API de OpenAI)

### Frontend (Cliente)
- **Framework:** Angular 17+ (Basado en *Standalone Components*)
- **Estilos:** CSS3 nativo avanzado (Flexbox, Grid, Glassmorphism, CSS Variables)
- **Formularios:** Angular Reactive Forms con validaciones asíncronas
- **Peticiones HTTP:** `HttpClient` con interceptores JWT para proteger rutas.

---

## 📂 Estructura del Proyecto

- `/backend`: Contiene la API RESTful (controladores, servicios, repositorios, configuraciones de seguridad).
- `/frontend`: Contiene la SPA (Single Page Application) dividida en páginas (Login, Register, Dashboard, Entrenamientos, Nutrición) y servicios.
- `/docker` (Opcional): Scripts y configuración para despliegue de PostgreSQL y la aplicación en contenedores.

---

## 🚀 Instalación y Despliegue Local

### Prerrequisitos
- **Java 21** o superior.
- **Node.js** (v18+) y NPM.
- **PostgreSQL** instalado y ejecutándose localmente (o vía Docker).
- Una **API Key de OpenAI**.

### 1. Configuración de la Base de Datos (PostgreSQL)
Crea una base de datos local llamada `endurancecoach`:
```sql
CREATE DATABASE endurancecoach;
```
Actualiza las credenciales en `/backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/endurancecoach
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
```

### 2. Configuración del Backend
En el archivo `application.properties`, añade tu API Key de OpenAI y el secreto para generar los tokens JWT:
```properties
jwt.secret=TU_SECRETO_SUPER_SEGURO_PARA_LOS_TOKENS
openai.api.key=TU_API_KEY_DE_OPENAI
```
Para ejecutar el servidor (se levantará en el puerto `:8080`):
```bash
cd backend
./mvnw spring-boot:run
```

### 3. Configuración del Frontend
Abre una nueva terminal e instala las dependencias de Angular:
```bash
cd frontend
npm install
```
Levanta el servidor de desarrollo (se levantará en el puerto `:4200`):
```bash
npm run start
# O bien: ng serve
```
Abre tu navegador en `http://localhost:4200` para empezar a usar la aplicación.

---

## 🔌 Principales Endpoints de la API

| Método | Endpoint | Descripción | Requiere Auth |
|---|---|---|---|
| `POST` | `/api/auth/register` | Registro de nuevo atleta | ❌ No |
| `POST` | `/api/auth/login` | Login y retorno del token JWT | ❌ No |
| `GET`  | `/api/metrics` | Obtiene las métricas del atleta | ✅ Sí |
| `POST` | `/api/metrics/save` | Guarda/Actualiza las métricas | ✅ Sí |
| `POST` | `/api/training/generate` | Genera un plan de IA (depende del objetivo) | ✅ Sí |
| `POST` | `/api/training/save` | Guarda un plan en el historial del usuario | ✅ Sí |
| `GET`  | `/api/training/user` | Obtiene el historial de planes de entrenamiento | ✅ Sí |
| `POST` | `/api/nutrition/generate` | Genera un plan nutricional completo con IA | ✅ Sí |
| `GET`  | `/api/nutrition` | Obtiene el historial de planes de nutrición | ✅ Sí |
| `DELETE`| `/api/nutrition/{id}` | Elimina un plan nutricional específico | ✅ Sí |

---

## 👨‍💻 Acerca del TFM

Este proyecto ha sido desarrollado como Trabajo de Fin de Máster (TFM) focalizado en Aplicaciones y Sistemas Inteligentes. Se ha diseñado cuidando meticulosamente tanto la lógica del negocio, el acoplamiento con grandes modelos de lenguaje (LLM), y las correctas prácticas de la Ingeniería de Software modernas, ofreciendo un producto final que une analítica e impacto visual.
