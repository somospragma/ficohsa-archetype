# Reglas de Arquitectura

Este documento describe las reglas de arquitectura que se validan automáticamente en el proyecto mediante ArchUnit.

## Ejecución de Pruebas

```bash
./gradlew checkArchitecture
```

## Reglas de Clean Architecture

### 1. Dependencias entre Capas

#### 1.1 Domain no debe depender de Infrastructure
**Regla:** `domainShouldNotDependOnInfrastructure()`

El dominio solo puede depender de:
- `java.*` - Clases base de Java
- `javax.*` - APIs estándar de Java
- `..domain..*` - Otras clases del dominio
- `lombok.*` - Anotaciones de Lombok

❌ **Incorrecto:**
```java
// En domain/model
import org.springframework.data.jpa.repository.JpaRepository; // ❌ Infraestructura
```

✅ **Correcto:**
```java
// En domain/model
import lombok.Data; // ✅ Permitido
```

#### 1.2 Applications no debe depender de Infrastructure
**Regla:** `applicationsShouldNotDependOnInfrastructure()`

Las aplicaciones pueden depender de:
- `java.*`, `javax.*` - APIs de Java
- `..domain..*` - Capa de dominio
- `..applications..*` - Otras aplicaciones
- `lombok.*` - Lombok
- `org.springframework.*` - Framework Spring

❌ **Incorrecto:**
```java
// En applications/usecase
import co.com.ficohsa.infrastructure.adapter.DatabaseAdapter; // ❌ Infraestructura
```

✅ **Correcto:**
```java
// En applications/usecase
import co.com.ficohsa.domain.model.User; // ✅ Dominio
import org.springframework.stereotype.Service; // ✅ Spring
```

#### 1.3 Infrastructure no debe depender de Applications
**Regla:** `infrastructureShouldNotDependOnApplications()`

La infraestructura puede depender de:
- `java.*`, `javax.*` - APIs de Java
- `..domain..*` - Capa de dominio
- `..infrastructure..*` - Otras infraestructuras
- `lombok.*`, `org.springframework.*` - Frameworks
- `reactor.*` - Programación reactiva
- `software.amazon.*` - AWS SDK

❌ **Incorrecto:**
```java
// En infrastructure/driven-adapters
import co.com.ficohsa.applications.usecase.UserUseCase; // ❌ Aplicación
```

✅ **Correcto:**
```java
// En infrastructure/driven-adapters
import co.com.ficohsa.domain.model.User; // ✅ Dominio
```

## Reglas de Naming y Tecnología

### 2.1 Domain sin sufijos técnicos
**Regla:** `domainClassesShouldNotBeNamedWithTechSuffixes()`

Las clases de dominio no deben terminar en:
- `Dto`, `Request`, `Response`

❌ **Incorrecto:**
```java
// En domain/model
public class UserDto { } // ❌ Sufijo técnico
```

✅ **Correcto:**
```java
// En domain/model
public class User { } // ✅ Nombre de dominio
```

### 2.2 Domain sin nombres de tecnología
**Regla:** `domainClassesShouldNotBeNamedWithToolNames()`

Las clases de dominio no deben contener nombres de tecnologías:
- `rabbit`, `sqs`, `sns`, `dynamo`, `mysql`, `redis`, `mongo`, etc.

❌ **Incorrecto:**
```java
// En domain/model
public class UserDynamo { } // ❌ Nombre de tecnología
```

### 2.3 Domain sin campos con nombres técnicos
**Regla:** `domainClassesShouldNotHaveFieldsNamedWithToolNames()`

Los campos no deben contener nombres de tecnologías.

❌ **Incorrecto:**
```java
public class User {
    private String redisKey; // ❌ Nombre técnico
}
```

## Reglas de Implementación

### 3.1 UseCases con campos finales
**Regla:** `useCaseFinalFields()`

Los casos de uso deben tener solo campos finales para evitar problemas de concurrencia.

❌ **Incorrecto:**
```java
public class UserUseCase {
    private UserRepository repository; // ❌ No final
}
```

✅ **Correcto:**
```java
public class UserUseCase {
    private final UserRepository repository; // ✅ Final
    
    public UserUseCase(UserRepository repository) {
        this.repository = repository;
    }
}
```

### 3.2 Beans con campos finales
**Regla:** `beansShouldOnlyHaveFinalFields()`

Los beans de Spring deben usar inyección por constructor.

❌ **Incorrecto:**
```java
@Service
public class UserService {
    @Autowired
    private UserRepository repository; // ❌ Inyección por campo
}
```

✅ **Correcto:**
```java
@Service
public class UserService {
    private final UserRepository repository; // ✅ Final + constructor
    
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}
```

### 3.3 Clientes AWS asíncronos
**Regla:** `reactiveFlowsShouldUseAwsAsyncClients()`

En flujos reactivos, usar clientes asíncronos de AWS.

❌ **Incorrecto:**
```java
@Component
public class S3Adapter {
    private final S3Client s3Client; // ❌ Cliente síncrono
}
```

✅ **Correcto:**
```java
@Component
public class S3Adapter {
    private final S3AsyncClient s3AsyncClient; // ✅ Cliente asíncrono
}
```

## Estructura de Carpetas Recomendada

```
├── domain/
│   └── model/              # Entidades de dominio puras
├── applications/
│   ├── app-service/        # Configuración y main
│   └── usecase/           # Casos de uso
└── infrastructure/
    ├── driven-adapters/    # Adaptadores externos
    └── entry-points/       # Puntos de entrada
```

## Validación de Estructura de Carpetas

### 4.1 UseCases en Applications
**Regla:** `useCasesShouldBeInApplicationsPackage()`

Los casos de uso deben estar en `applications` (excluye clases internas).

### 4.2 Models en Domain
**Regla:** `modelsShouldBeInDomainPackage()`

Las clases del paquete `model` deben estar dentro de `domain`.

### 4.3 Adapters en Infrastructure
**Regla:** `adaptersShouldBeInInfrastructurePackage()`

Las clases terminadas en `Adapter` deben estar en `infrastructure`.

### 4.4 Controllers en Entry Points
**Regla:** `controllersShouldBeInEntryPointsPackage()`

Los controllers deben estar en `infrastructure.entrypoints`.

## Notas Técnicas

- Todas las reglas incluyen `.allowEmptyShould(true)` para evitar fallos cuando no hay clases
- UseCases excluye clases internas con `.areNotMemberClasses()`
- Paquetes usan notación `..package..` para coincidencias parciales
- Controllers validan paquete `entrypoints` (sin guiones)

## Comandos Útiles

```bash
# Ejecutar pruebas de arquitectura
./gradlew checkArchitecture

# Ejecutar solo en app-service
./gradlew :app-service:architectureTest

# Ver reportes de cobertura
./gradlew jacocoMergedReport
```