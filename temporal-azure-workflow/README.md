# Temporal Workflow con Azure Functions

Este proyecto demuestra cómo crear un workflow de Temporal que consume Azure Functions.

## Prerrequisitos

- Java 21
- Maven
- Temporal CLI

## Estructura del Proyecto

```
src/main/java/com/example/
├── AzureFunctionWorkflow.java          # Interfaz del workflow
├── AzureFunctionWorkflowImpl.java      # Implementación del workflow
├── AzureFunctionActivities.java        # Interfaz de actividades
├── AzureFunctionActivitiesImpl.java    # Implementación de actividades
├── TemporalWorker.java                 # Worker de Temporal
└── WorkflowStarter.java                # Cliente que inicia el workflow
```

## Configuración

### Variables de Entorno

Configura estas variables de entorno con tus valores de Azure Functions:

```bash
export AZURE_FUNCTION_BASE_URL="https://tu-function-app.azurewebsites.net/api"
export AZURE_FUNCTION_KEY="tu-clave-aqui"
```

### Compilar el Proyecto

```bash
mvn clean compile
```

## Ejecución

### Opción 1: Script Automático (Recomendado)

```bash
./run-with-env.sh
```

Este script:
- Inicia el servidor Temporal
- Compila el proyecto
- Ejecuta el worker
- Ejecuta el workflow

### Opción 2: Manual

#### Paso 1: Iniciar el Servidor Temporal

```bash
temporal server start-dev
```

#### Paso 2: Ejecutar el Worker

```bash
mvn exec:java -Dexec.mainClass="com.example.TemporalWorker"
```

#### Paso 3: Ejecutar el Cliente

```bash
mvn exec:java -Dexec.mainClass="com.example.WorkflowStarter"
```

## Flujo del Workflow

1. **Procesamiento**: Llama a Azure Function para procesar datos
2. **Validación**: Valida los datos procesados
3. **Almacenamiento**: Almacena el resultado final

## Monitoreo

- **Temporal UI**: http://localhost:8233
- **Logs**: Aparecen en la consola con formato estructurado

## Utilidades

### Detener Temporal

```bash
./stop-temporal.sh
```

## Personalización

Para usar con tu propia Azure Function:

1. Actualiza las variables de entorno en `run-with-env.sh`
2. Modifica el endpoint en `AzureFunctionActivitiesImpl.java` si es necesario
3. Ajusta el formato de los datos según tu Azure Function
