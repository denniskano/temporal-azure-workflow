#!/bin/bash

echo "🚀 Ejecutando Temporal Workflow con Azure Functions"
echo "=================================================="

# Configurar variables de entorno
export AZURE_FUNCTION_BASE_URL="https://asp-functions-peve-dev-gcargab6exe5fscu.eastus2-01.azurewebsites.net/api"
export AZURE_FUNCTION_KEY="AuK0XcXHjH1BgDbiOnhhp6doLGYaCyuPC_5C5Vq34EFf4AzFuKy7mEQ=="

echo "🔧 Variables de entorno configuradas:"
echo "   Base URL: $AZURE_FUNCTION_BASE_URL"
echo "   Key: ${AZURE_FUNCTION_KEY:0:10}..."

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
AZURE_FUNCTION_BASE_URL="$AZURE_FUNCTION_BASE_URL" AZURE_FUNCTION_KEY="$AZURE_FUNCTION_KEY" mvn exec:java -Dexec.mainClass="com.example.TemporalWorker" &
WORKER_PID=$!

echo "⏳ Esperando que el worker se inicie..."
sleep 5

# Ejecutar el cliente con las variables de entorno
echo "🎯 Ejecutando workflow..."
AZURE_FUNCTION_BASE_URL="$AZURE_FUNCTION_BASE_URL" AZURE_FUNCTION_KEY="$AZURE_FUNCTION_KEY" mvn exec:java -Dexec.mainClass="com.example.WorkflowStarter"

# Detener el worker
echo "🛑 Deteniendo worker..."
kill $WORKER_PID 2>/dev/null

echo ""
echo "✅ Ejecución completada!"
echo ""
echo "📊 Para ver más detalles:"
echo "   🌐 Temporal UI: http://localhost:8233"
