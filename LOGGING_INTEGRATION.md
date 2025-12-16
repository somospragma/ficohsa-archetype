# 🎉 Integración de ficohsa-logging en ficohsa-archetype

## ✅ Estado: COMPLETADO

La librería ficohsa-logging ha sido integrada exitosamente en el proyecto ficohsa-archetype.

---

## 📦 Cambios Realizados

### 1. **Dependencias (main.gradle)**
```gradle
dependencies {
    // Ficohsa Logging Library
    implementation 'ficohsa-logging:logging-spring-boot-starter:unspecified'
    ...
}
```

### 2. **Repositorios (main.gradle)**
```gradle
allprojects {
    repositories {
        mavenLocal()  // ← Agregado para usar librería local
        mavenCentral()
        ...
    }
}
```

### 3. **Configuración (application.yaml)**
```yaml
ficohsa:
  logging:
    format: CONSOLE
    mask-confidential: true
    record-type: U
    log-group: scaffoldLogs
    environment: DEV
    aop:
      enabled: true
    dynamodb:
      enabled: false
```

### 4. **Ejemplo de Uso (Handler.java)**
```java
@Component
@RequiredArgsConstructor
public class Handler {
    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ExampleRequest.class)
            .doOnNext(request -> log.info("Processing POST request with account: {}", request.getAccountNumber()))
            .map(request -> new ExampleResponse(...))
            .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }
}
```

### 5. **DTO con Campos Sensibles (ExampleRequest.java)**
```java
@Data
public class ExampleRequest {
    
    @Sensitive(strategy = MaskStrategy.PREFIX, maskLength = 4)
    private String accountNumber;  // 0001122334455 → ****4455
    
    @Sensitive(strategy = MaskStrategy.FULL)
    private String pin;  // 1234 → ****
    
    private BigDecimal amount;
    private String description;
}
```

### 6. **Reglas de Arquitectura (ArchitectureTest.java)**
Actualizadas para excluir paquetes de ficohsa-logging:
- `co.com.ficohsa.logging..`
- `com.fasterxml.jackson..`
- `ch.qos.logback..`
- `org.slf4j..`

---

## 🎯 Funcionalidades Activas

### ✅ Logging Automático
- Captura automática de requests HTTP
- Captura automática de responses HTTP
- Captura automática de excepciones
- Cálculo automático de latencias

### ✅ Enmascaramiento
- Campos anotados con `@Sensitive` se enmascaran automáticamente
- 5 estrategias disponibles: FULL, PREFIX, SUFFIX, MIDDLE, NONE

### ✅ Contexto Reactivo
- Propagación automática de correlationId
- Contexto disponible en toda la cadena reactiva

### ✅ Formato CONSOLE
- Logs legibles para desarrollo local
- Incluye correlationId en cada log

---

## 🚀 Cómo Usar

### 1. Logging Simple
```java
@RestController
public class MyController {
    private static final Logger log = LoggerFactory.getLogger(MyController.class);
    
    @GetMapping("/test")
    public Mono<String> test() {
        log.info("Processing request");  // ← Automáticamente enriquecido
        return Mono.just("OK");
    }
}
```

### 2. Anotar Campos Sensibles
```java
public class MyRequest {
    @Sensitive(strategy = MaskStrategy.PREFIX, maskLength = 4)
    private String accountNumber;
}
```

### 3. Logging de Llamadas Externas
```java
@Service
public class MyAdapter {
    
    @LogExternalCall(provider = "ExternalAPI")
    public Mono<Response> callExternal() {
        return webClient.get().uri("/api").retrieve().bodyToMono(Response.class);
    }
}
```

---

## 📊 Ejemplo de Log Generado

```
2025-01-27 15:50:42.111 INFO  [Handler] [a1b2c3d4-e5f6-7890] Processing POST request with account: ****4455
```

---

## 🔧 Configuración por Ambiente

### Desarrollo (application-dev.yaml)
```yaml
ficohsa:
  logging:
    format: CONSOLE
    environment: DEV
```

### Producción (application-prod.yaml)
```yaml
ficohsa:
  logging:
    format: JSON
    environment: PROD
```

---

## ✅ Verificación

```bash
# Compilar proyecto
./gradlew clean build

# Ejecutar aplicación
./gradlew :app-service:bootRun

# Hacer request de prueba
curl -X POST http://localhost:8080/api/usecase/otherpath \
  -H "Content-Type: application/json" \
  -H "X-Correlation-Id: test-123" \
  -H "X-Source-Bank: HN01" \
  -H "X-Application-Id: TEST_APP" \
  -d '{"accountNumber":"1234567890","pin":"1234","amount":100.00,"description":"Test"}'
```

---

## 📝 Notas

- La librería se carga automáticamente desde Maven local
- No requiere configuración adicional de beans
- Los filters se registran automáticamente
- El enmascaramiento funciona automáticamente

---

**Versión Librería**: 1.0.0-SNAPSHOT (unspecified)  
**Fecha Integración**: 2025-01-27  
**Estado**: ✅ BUILD SUCCESSFUL
