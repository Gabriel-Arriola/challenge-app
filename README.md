# 🧩 Challenge - Gestión de Clientes
Este proyecto es una API RESTful para la gestión de clientes. 
Integra funcionalidades modernas como autenticación JWT, envío de correos, eventos asíncronos con RabbitMQ, métricas de monitoreo con Prometheus, documentación OpenAPI, y persistencia con PostgreSQL. 
Todo el entorno puede ejecutarse con Docker Compose.

---

## 🧱 Decisiones arquitectónicas y patrones de diseño
### 📐 Arquitectura
1. [x] Arquitectura en capas: Separación clara entre controladores, servicios, repositorios, modelos y DTOs para favorecer la mantenibilidad, pruebas unitarias y escalabilidad.
2. [x] Orientado a eventos: Uso de RabbitMQ para publicar y consumir eventos asíncronos (Customer creado → envío de email).
3. [x] Configuración externa desacoplada: Uso de application.yml y variables de entorno vía docker-compose para ambientes separados (dev/test/prod).
4. [x] Observabilidad incorporada: Se integra Spring Actuator con Prometheus para métricas y Grafana para visualización.

### 🎯 Patrones de diseño aplicados
1. [x] **DTO Pattern**: Se emplean objetos DTO para desacoplar la API del modelo de dominio y aplicar validaciones de entrada.
2. [x] **Mapper Pattern** (con MapStruct): Transforma objetos DTO ↔ entidades de forma eficiente y declarativa.
3. [x] **Builder Pattern** (con Lombok): Facilita la creación de objetos complejos (Request, Response, Entidades).
4. [x] **Factory Method** (Spring Bean Factories): Se crean beans como CustomerMapper, RabbitTemplate, SecurityFilterChain desde clases de configuración.
5. [x] **Strategy Pattern**: Aplicado en los filtros de autenticación JWT (OncePerRequestFilter) para definir comportamiento condicional en tiempo de ejecución.
6. [x] **Observer/Event-driven Pattern**: RabbitMQ actúa como middleware entre Publisher y Listener desacoplados, facilitando la comunicación asíncrona.

### ⚙️ Herramientas utilizadas
* **Spring Boot**: Framework base para APIs REST y servicios web.
* **Spring Data JPA**: Abstracción para persistencia con PostgreSQL.
* **Liquibase**: Migraciones de base de datos versionadas.
* **RabbitMQ**: Middleware de mensajería para eventos.
* **MailHog**: Simulador de SMTP para pruebas de correo.
* **JWT** (jjwt): Generación y validación de tokens de seguridad.
* **Micrometer** + **Prometheus** + **Grafana**: Monitoreo y métricas del sistema.
* **Swagger** (springdoc-openapi): Documentación automática de endpoints.
* **JUnit** + **Mockito**: Suite de testing robusta para asegurar calidad del código.
* **Docker Compose**: Orquestación de todos los servicios en contenedores.

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

```
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
```
curl -X POST "http://localhost:8080/api/auth/token?username=admin&password=password"
```
### Respuesta:
```json
{"token":"eyJhbGciOiJIUzI1NiJ9..."}
```

### Ejemplo de creación:
```
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

### 🧪 Ejecutar pruebas
```
./mvnw clean test
```
### Incluye:
* Pruebas unitarias de servicios, mapeos, eventos y seguridad
* Pruebas de integración con Spring Boot Test (@SpringBootTest)
* Mocking de servicios con Mockito
* Cobertura de controladores REST

### Nota: 
#### Para desarrollo local, configurar los servicios dependientes o usar el perfil test con H2:
```
mvn spring-boot:run -Dspring.profiles.active=test
```

### 📈 Monitoreo
Prometheus
Expone métricas en:
```
GET /actuator/prometheus
```

#### Métricas personalizadas:

* customer.created → contador de clientes creados
* http_server_requests_seconds_count → métricas HTTP

### Grafana
Dashboard preconfigurado disponible en:

```
http://localhost:3000
```
* Uso de memoria (JVM)
* Errores 500
* Clientes creados
* Uptime de la aplicación

### 📬 Notificaciones
**Cada vez que se crea un nuevo cliente:**
* Se publica un evento en RabbitMQ
* Se consume en un listener asíncrono
* Se envía un correo de notificación (capturado por MailHog)

