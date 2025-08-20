#!/bin/bash

echo "🛑 Deteniendo Temporal y procesos relacionados"
echo "============================================="

# Detener servidor Temporal
echo "🔍 Buscando servidor Temporal..."
if pgrep -f "temporal server" > /dev/null; then
    echo "🛑 Deteniendo servidor Temporal..."
    pkill -f "temporal server"
    echo "✅ Servidor Temporal detenido"
else
    echo "ℹ️  Servidor Temporal no está ejecutándose"
fi

# Detener workers de Temporal
echo "🔍 Buscando workers de Temporal..."
if pgrep -f "com.example.TemporalWorker" > /dev/null; then
    echo "🛑 Deteniendo workers de Temporal..."
    pkill -f "com.example.TemporalWorker"
    echo "✅ Workers de Temporal detenidos"
else
    echo "ℹ️  Workers de Temporal no están ejecutándose"
fi

# Detener procesos Maven relacionados con Temporal
echo "🔍 Buscando procesos Maven de Temporal..."
if pgrep -f "mvn exec:java.*Temporal" > /dev/null; then
    echo "🛑 Deteniendo procesos Maven de Temporal..."
    pkill -f "mvn exec:java.*Temporal"
    echo "✅ Procesos Maven de Temporal detenidos"
else
    echo "ℹ️  Procesos Maven de Temporal no están ejecutándose"
fi

# Verificar que no queden procesos
echo ""
echo "🔍 Verificando que no queden procesos..."
REMAINING=$(pgrep -f "temporal\|TemporalWorker" | wc -l)
if [ "$REMAINING" -eq 0 ]; then
    echo "✅ Todos los procesos de Temporal han sido detenidos"
else
    echo "⚠️  Quedan $REMAINING procesos. Ejecutando limpieza adicional..."
    pkill -f "temporal"
    pkill -f "TemporalWorker"
fi

echo ""
echo "🎯 Temporal detenido completamente"
echo "💡 Para reiniciar: temporal server start-dev"
