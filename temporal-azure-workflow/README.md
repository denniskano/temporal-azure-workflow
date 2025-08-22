# Temporal Azure Services Orchestration

Este proyecto demuestra cómo crear un workflow de Temporal que orquesta múltiples servicios de Azure.

## Prerrequisitos

- Java 21
- Maven
- Temporal CLI

## Estructura del Proyecto

```
src/main/java/com/example/
├── AzureServicesWorkflow.java          # Interfaz del workflow
├── AzureServicesWorkflowImpl.java      # Implementación del workflow
├── AzureServicesActivities.java        # Interfaz de actividades de Azure Services
├── AzureServicesActivitiesImpl.java    # Implementación de actividades de Azure Services
├── DatabricksActivities.java           # Interfaz de actividades de Databricks
├── DatabricksActivitiesImpl.java       # Implementación de actividades de Databricks
├── TemporalWorker.java                 # Worker de Temporal
└── WorkflowStarter.java                # Cliente que inicia el workflow

src/main/resources/
└── HelloWorld.py                       # Notebook de Databricks
```

## Configuración

### Variables de Entorno

#### Azure Functions
```bash
export AZURE_FUNCTION_BASE_URL="https://tu-function-app.azurewebsites.net/api"
export AZURE_FUNCTION_KEY="tu-clave-aqui"
```

#### Databricks
```bash
export DATABRICKS_WORKSPACE_URL="https://tu-workspace.azuredatabricks.net"
export DATABRICKS_ACCESS_TOKEN="tu-databricks-token"
```

#### Configuración Automática
Para configurar Databricks fácilmente:
```bash
./configure-databricks.sh
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
2. **Databricks**: Ejecuta notebook de Databricks para análisis de datos
3. **Validación**: Valida los resultados de Databricks
4. **Almacenamiento**: Almacena el resultado final

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
