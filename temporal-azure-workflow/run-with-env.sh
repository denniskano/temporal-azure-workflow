#!/bin/bash

echo "🚀 Ejecutando Temporal Workflow con Azure Services"
echo "=================================================="

# Configurar variables de entorno
export AZURE_FUNCTION_BASE_URL="https://your-function-app.azurewebsites.net/api"
export AZURE_FUNCTION_KEY="your-azure-function-key"

# Databricks configuration (opcional - usar mock si no están configuradas)
# export DATABRICKS_WORKSPACE_URL="https://your-databricks-workspace.azuredatabricks.net"
# export DATABRICKS_ACCESS_TOKEN="your-databricks-access-token"

echo "🔧 Variables de entorno configuradas:"
echo "   Azure Function Base URL: $AZURE_FUNCTION_BASE_URL"
echo "   Azure Function Key: ${AZURE_FUNCTION_KEY:0:10}..."
echo "   Databricks Workspace URL: $DATABRICKS_WORKSPACE_URL"
echo "   Databricks Access Token: ${DATABRICKS_ACCESS_TOKEN:0:10}..."

# Verificar que el servidor Temporal esté ejecutándose
echo "🔍 Verificando servidor Temporal..."
if ! curl -s http://localhost:7233/health > /dev/null 2>&1; then
    echo "❌ Servidor Temporal no está ejecutándose"
    echo "🚀 Iniciando servidor Temporal..."
    temporal server start-dev &
    sleep 10
fi

echo "✅ Servidor Temporal listo"

# Compilar el proyecto
echo "📦 Compilando proyecto..."
mvn clean compile

# Ejecutar el worker con las variables de entorno
echo "👷 Iniciando worker con variables de entorno..."
AZURE_FUNCTION_BASE_URL="$AZURE_FUNCTION_BASE_URL" AZURE_FUNCTION_KEY="$AZURE_FUNCTION_KEY" DATABRICKS_WORKSPACE_URL="$DATABRICKS_WORKSPACE_URL" DATABRICKS_ACCESS_TOKEN="$DATABRICKS_ACCESS_TOKEN" mvn exec:java -Dexec.mainClass="com.example.TemporalWorker" &
WORKER_PID=$!

echo "⏳ Esperando que el worker se inicie..."
sleep 5

# Ejecutar el cliente con las variables de entorno
echo "🎯 Ejecutando workflow..."
AZURE_FUNCTION_BASE_URL="$AZURE_FUNCTION_BASE_URL" AZURE_FUNCTION_KEY="$AZURE_FUNCTION_KEY" DATABRICKS_WORKSPACE_URL="$DATABRICKS_WORKSPACE_URL" DATABRICKS_ACCESS_TOKEN="$DATABRICKS_ACCESS_TOKEN" mvn exec:java -Dexec.mainClass="com.example.WorkflowStarter"

# Detener el worker
echo "🛑 Deteniendo worker..."
kill $WORKER_PID 2>/dev/null

echo ""
echo "✅ Ejecución completada!"
echo ""
echo "📊 Para ver más detalles:"
echo "   🌐 Temporal UI: http://localhost:8233"
