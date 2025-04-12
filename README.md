# 🧩 Challenge - Gestión de Clientes
Este proyecto es una API RESTful para la gestión de clientes. 
Integra funcionalidades modernas como autenticación JWT, envío de correos, eventos asíncronos con RabbitMQ, métricas de monitoreo con Prometheus, documentación OpenAPI, y persistencia con PostgreSQL. 
Todo el entorno puede ejecutarse con Docker Compose.

---

## 🛠️ Tecnologías y herramientas utilizadas

| Herramienta                 | Descripción                                   |
|-----------------------------|-----------------------------------------------|
| **Java 17**                 | Lenguaje principal                            |
| **Spring Boot 3.4.4**       | Framework de aplicación                       |
| **Spring Data JPA**         | Persistencia con Hibernate                    |
| **Spring Security + JWT**   | Seguridad y autenticación                     |
| **MapStruct**               | Mapeo entre DTO y entidades                   |
| **Liquibase**               | Control de versiones de la base de datos      |
| **PostgreSQL / H2**         | Bases de datos para producción/test           |
| **RabbitMQ**                | Procesamiento asíncrono de eventos            |
| **Mailhog**                 | Captura de correos electrónicos en desarrollo |
| **Prometheus + Grafana**    | Observabilidad y métricas                     |
| **OpenAPI (Swagger)**       | Documentación interactiva de la API           |
| **JUnit + Mockito**         | Pruebas unitarias y de integración            |
| **Docker + Docker Compose** | Contenedorización del ecosistema              |

---

## 📦 Estructura del Proyecto
```
challenge-app/
├── docker-compose.yml          # Configuración de todos los servicios
├── grafana/                    # Configuración de Grafana
│   ├── provisioning/
│   │   ├── dashboards/         # Dashboards predefinidos
│   │   └── datasources/        # Fuentes de datos
├── prometheus/                 # Configuración de Prometheus
│   └── prometheus.yml
├── src/
│   ├── main/
│   │   ├── java/org/challenge_app/
│   │   │   ├── config/         # Configuraciones Spring
│   │   │   ├── controller/     # Controladores REST
│   │   │   ├── dto/            # Objetos de transferencia de datos
│   │   │   ├── exception/      # Manejo de excepciones
│   │   │   ├── messaging/      # Componentes de mensajería
│   │   │   ├── model/          # Entidades JPA
│   │   │   ├── repository/     # Repositorios JPA
│   │   │   ├── security/       # Configuración de seguridad
│   │   │   ├── service/        # Lógica de negocio
│   │   │   └── ChallengeAppApplication.java  # Clase principal
│   │   └── resources/
│   │       ├── db/             # Migraciones de base de datos
│   │       ├── application.yml # Configuración principal
│   │       ├── application-test.yml # Configuración para tests
│   │       └── logback-spring.xml # Configuración de logging
│   └── test/                   # Pruebas unitarias e integración
└── target/                     # Artefactos generados
```
---

## 🚀 Funcionalidades destacadas

✅ Crear, obtener y listar clientes  
✅ Consultar métricas de clientes 
✅ Documentación Swagger  
✅ JWT con endpoint de autenticación (`/api/auth/token`)  
✅ Eventos asíncronos con RabbitMQ + envío de email  
✅ Dashboards con Prometheus + Grafana  
✅ Pruebas unitarias

---

## ⚙️ Cómo ejecutar el proyecto

### 🔧 Requisitos

- Docker y Docker Compose
- JDK 17+
- Maven 3.8+

---

### 🐳 Despliegue con Docker Compose

```bash
# Clonar el repositorio y ubicarse en la raíz
git clone https://github.com/Gabriel-Arriola/challenge-app.git
cd challenge-app

# Compilar la imagen de la aplicación
./mvnw clean package -DskipTests

# Levantar todo el ecosistema
docker-compose up --build
```
### Los servicios estarán disponibles en:

* Aplicación: http://localhost:8080
* Swagger UI: http://localhost:8080/swagger-ui.html
* RabbitMQ Management: http://localhost:15672 (usuario: guest, password: guest)
* MailHog UI: http://localhost:8025
* Prometheus: http://localhost:9090
* Grafana: http://localhost:3000 (usuario: admin, password: admin)

### 🧪 Endpoints REST disponibles

   | Método | Endpoint               | Descripción                         |
   |--------|------------------------|-------------------------------------|
   | POST   | /api/auth/token        | Autenticación y generación de token |
   | POST   | /api/customers         | Crear nuevo cliente                 |
   | GET    | /api/customers         | Listar todos los clientes           |
   | GET    | /api/customers/{id}    | Buscar cliente por ID               |
   | GET    | /api/customers/metrics | Obtener métricas agregadas          |

### 🔐 Autenticación
```bash
Copy
curl -X POST "http://localhost:8080/api/auth/token?username=admin&password=password"
```
### Respuesta:
```json
"Copy"
{"token":"eyJhbGciOiJIUzI1NiJ9..."}
```

### Ejemplo de creación:
```
bash
Copy
curl -X POST "http://localhost:8080/api/customers" \
-H "Authorization: Bearer [TOKEN]" \
-H "Content-Type: application/json" \
-d '{
"firstName": "Ana",
"lastName": "Gómez",
"age": 28,
"dateOfBirth": "1995-07-20"
}'
```

### Nota: 
#### Para desarrollo local, configurar los servicios dependientes o usar el perfil test con H2:
```
bash
Copy
mvn spring-boot:run -Dspring.profiles.active=test
```