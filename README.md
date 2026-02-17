# EnduranceCoach
# Plataforma de Gestión Deportiva con IA - TFM

## 1. Descripción del Proyecto
Este proyecto es una aplicación web integral para la gestión de entrenamiento y nutrición orientada a deportes de resistencia (running, triatlón, ciclismo). El valor diferencial reside en la integración de **Inteligencia Artificial Generativa** para la creación automática y personalizada de planes de entrenamiento.

## 2. Arquitectura del Sistema
El proyecto sigue una arquitectura monolítica modular desacoplada (Frontend y Backend separados):

### Backend (Servidor)
- **Lenguaje:** Java 21
- **Framework:** Spring Boot 3.2
- **Seguridad:** Spring Security + JWT (JSON Web Tokens)
- **Base de Datos:** PostgreSQL
- **IA Integration:** Spring AI (conectado a OpenAI API)

### Frontend (Cliente)
- **Framework:** Angular 17+ (Standalone Components)
- **Estilos:** Tailwind CSS (recomendado por facilidad) o Bootstrap
- **Gráficas:** Ngx-Charts

## 3. Estructura del Proyecto
- `/backend`: API RESTful con Spring Boot.
- `/frontend`: SPA (Single Page Application) con Angular.
- `/docker`: Configuración para despliegue en contenedores.
