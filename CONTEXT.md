# Contexto del Proyecto: ficohsa-archetype

## Objetivo
Proyecto base implementando Clean Architecture para microservicios reactivos de Ficohsa con logging estructurado integrado.

## Arquitectura del Proyecto

### Estructura de Módulos Gradle

```
ficohsa-archetype/
├── domain/
│   └── model/                          # Entidades y modelos de dominio
├── applications/
│   ├── usecase/                        # Casos de uso (lógica de aplicación)
│   └── app-service/                    # Punto de entrada, configuración, main()
└── infrastructure/
    ├── driven-adapters/
    │   └── external-api/               # Adaptadores para APIs externas
    ├── entry-points/
    │   └── reactive-web/               # Controladores WebFlux (Handler, RouterRest)
    └── helpers/                        # Utilidades y configuración
        └── config/
            └── GlobalExceptionHandler.java
```

## Módulos Implementados

### 1. domain/model
**Propósito**: Entidades de dominio sin dependencias externas

**Estado**: Vacío (sin entidades implementadas)

### 2. applications/usecase
**Propósito**: Casos de uso del sistema

**Archivos clave**:
- `GetCryptoPriceUseCase.java` - Caso de uso para obtener precio de criptomoneda
  - Tiene `@LogExternalCall(provider = "ApiNinjas")` para medir latencia
  - Llama a `CryptoApiAdapter.getCryptoPrice()`

**Dependencias**:
```gradle
implementation project(':model')
implementation project(':external-api')
```

### 3. applications/app-service
**Propósito**: Ensambla módulos, resuelve dependencias, inicia aplicación

**Archivos clave**:
- `MainApplication.java` - Clase principal con main()
- `application.yaml` - Configuración de la aplicación y logging
- `ArchitectureTest.java` - Tests de arquitectura con ArchUnit (excluye ficohsa-logging de reglas)

**Configuración de logging**:
```yaml
ficohsa:
  logging:
    format: JSON
    mask-confidential: true
    record-type: U
    log-group: scaffoldLogs
    environment: DEV
    aop:
      enabled: true
    dynamodb:
      enabled: false
```

### 4. infrastructure/driven-adapters/external-api
**Propósito**: Adaptadores para llamadas a APIs externas

**Archivos clave**:
- `CryptoApiAdapter.java` - Adapter para API Ninjas crypto price
  - Usa `LoggingWebClientCustomizer.customize()` para logging
  - Base URL: `https://api.api-ninjas.com/v1`
  - Endpoint: `/cryptoprice?symbol={symbol}`
- `CryptoPriceResponse.java` - DTO de respuesta (symbol, price, timestamp)

**Dependencias**:
```gradle
implementation 'org.springframework.boot:spring-boot-starter-webflux'
implementation 'co.com.ficohsa.logging:logging-spring-boot-starter:unspecified'
```

### 5. infrastructure/entry-points/reactive-web
**Propósito**: Puntos de entrada HTTP (handlers y routers)

**Archivos clave**:
- `Handler.java` - Handlers funcionales para endpoints
  - `listenGETUseCase()` - GET simple
  - `listenPOSTUseCase()` - POST con ExampleRequest (usa LogContextHelper.addData())
  - `getCryptoPrice()` - GET crypto price (llama a GetCryptoPriceUseCase)
  - `testError()` - GET que lanza error (retorna Mono.error())
- `RouterRest.java` - Configuración de rutas funcionales
  - `/api/usecase` → listenGETUseCase
  - `/api/otherusecase` → listenGETOtherUseCase
  - `/api/path` → listenPOSTUseCase
  - `/api/crypto/price` → getCryptoPrice
  - `/api/test/error` → testError
- `ExampleRequest.java` - DTO con campos sensibles
  - `accountNumber` con `@Sensitive(strategy = PREFIX, maskLength = 4)`
  - `pin` con `@Sensitive(strategy = FULL)`
- `ExampleResponse.java` - DTO de respuesta

**Dependencias**:
```gradle
implementation project(':usecase')
implementation project(':model')
implementation project(':external-api')
implementation 'org.springframework.boot:spring-boot-starter-webflux'
implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'io.micrometer:micrometer-registry-prometheus'
```

### 6. infrastructure/helpers/config
**Propósito**: Configuración global del microservicio

**Archivos clave**:
- `GlobalExceptionHandler.java` - Implementa WebExceptionHandler
  - Intercepta todas las excepciones
  - Crea ErrorInfo con tipo, título, código, detalle
  - Usa `LogContextHelper.addError()` para agregar al contexto
  - Retorna JSON con error (type, title, code, detail)
  - Orden: -2 (alta prioridad)

## Integración con ficohsa-logging

### Logs Generados Actualmente

**1. Request del microservicio (recordType I)**
```json
{
  "level": "INFO",
  "recordType": "I",
  "logger": "co.com.ficohsa.logging.webflux.filter.LoggingWebFilter",
  "httpMethod": "GET",
  "message": "Incoming request: GET /api/crypto/price",
  "version": "1",
  "sourceBank": "HN01",
  "timestampRequest": "2025-12-16T09:35:41.459952Z",
  "environment": "DEV",
  "apiEndpoint": "/api/crypto/price",
  "correlationId": "test-crypto-123",
  "applicationId": "TEST_APP",
  "timestamp": 1765877741460
}
```

**2. Response del microservicio (recordType U)**
```json
{
  "level": "INFO",
  "requestLatencyMs": 671,
  "recordType": "U",
  "logger": "co.com.ficohsa.logging.webflux.filter.ResponseLoggingFilter",
  "httpMethod": "GET",
  "message": "Response completed: 200 - 671ms",
  "version": "1",
  "sourceBank": "HN01",
  "timestampRequest": "2025-12-16T09:35:41.459952Z",
  "environment": "DEV",
  "apiEndpoint": "/api/crypto/price",
  "timestampResponse": "2025-12-16T09:35:42.130052Z",
  "correlationId": "test-crypto-123",
  "applicationId": "TEST_APP",
  "status": "OK",
  "httpStatusCode": 200,
  "timestamp": 1765877742130
}
```

**3. Log del aspecto (sin estructura)**
```json
{
  "level": "INFO",
  "logger": "co.com.ficohsa.logging.clients.annotation.ExternalCallLoggingAspect",
  "message": "External call to ApiNinjas completed in 657ms",
  "timestamp": 1765877742131
}
```

### Logs Esperados pero NO Generados

**4. Request del external API (recordType I)** - ❌ NO aparece
- Debería generarse por LoggingWebClientCustomizer
- Debería heredar correlationId, sourceBank, applicationId del request principal
- Debería tener apiEndpoint del external (https://api.api-ninjas.com/v1/cryptoprice)

**5. Response del external API (recordType U)** - ❌ NO aparece
- Debería generarse por LoggingWebClientCustomizer
- Debería tener providerLatencyMs, httpStatusCode, status

## Endpoints Implementados

### GET /api/crypto/price?symbol=BTCUSDT
**Propósito**: Obtener precio de criptomoneda desde API externa

**Flujo**:
1. Handler.getCryptoPrice() recibe request
2. Llama a GetCryptoPriceUseCase.execute() (con @LogExternalCall)
3. UseCase llama a CryptoApiAdapter.getCryptoPrice()
4. Adapter hace llamada HTTP a API Ninjas
5. Retorna CryptoPriceResponse

**Headers requeridos**:
- `x-correlation-id: test-crypto-123`
- `x-application-id: TEST_APP`
- `x-source-bank: HN01`

**Curl**:
```bash
curl -X GET "http://localhost:8080/api/crypto/price?symbol=BTCUSDT" \
  -H "x-correlation-id: test-crypto-123" \
  -H "x-application-id: TEST_APP" \
  -H "x-source-bank: HN01"
```

### GET /api/test/error
**Propósito**: Probar manejo de errores con GlobalExceptionHandler

**Flujo**:
1. Handler.testError() retorna Mono.error(new RuntimeException())
2. GlobalExceptionHandler intercepta
3. Crea ErrorInfo y lo agrega al contexto con LogContextHelper.addError()
4. Loguea error con todos los campos estructurados
5. Retorna JSON con error

**Resultado esperado**:
- Log con recordType U
- Campo `error` con type, title, code, detail
- httpStatusCode: 500
- status: ERROR

## Problemas Pendientes

### 1. providerLatencyMs NO aparece en log de response del microservicio
**Descripción**: El aspecto @LogExternalCall mide la latencia (657ms) pero NO se agrega al log de response del microservicio

**Evidencia**: 
- Aspecto loguea: "External call to ApiNinjas completed in 657ms"
- Log de response NO tiene campo `providerLatencyMs`

**Causa**: contextWrite() del aspecto se ejecuta DESPUÉS del ResponseLoggingFilter

### 2. Logs estructurados de external calls NO aparecen
**Descripción**: LoggingWebClientCustomizer debería generar 2 logs (recordType I y U) para la llamada externa pero NO aparecen

**Causa**: El contexto NO se propaga al WebClient, deferContextual() retorna contexto vacío

## Compilación y Ejecución

```bash
# Compilar
gradle build -x test

# Ejecutar
gradle :applications:app-service:bootRun

# O ejecutar el JAR
java -jar applications/app-service/build/libs/app-service.jar
```

## Arquitectura de Tests

- `ArchitectureTest.java` - Valida reglas de Clean Architecture con ArchUnit
- Excluye paquetes de ficohsa-logging de las reglas de arquitectura
- Se ejecuta automáticamente en `gradle build`
