#!/bin/bash

echo "🔧 Configurando Databricks para Temporal Workflow"
echo "================================================"

echo ""
echo "📋 Para configurar Databricks, necesitas:"
echo ""
echo "1. 📍 Workspace URL:"
echo "   - Ve a tu workspace de Databricks"
echo "   - Copia la URL (ejemplo: https://adb-1234567890123456.7.azuredatabricks.net)"
echo ""
echo "2. 🔑 Access Token:"
echo "   - Ve a User Settings → Access Tokens"
echo "   - Crea un nuevo token"
echo "   - Copia el token generado"
echo ""
echo "3. 🖥️  Cluster ID:"
echo "   - Ve a Clusters en tu workspace"
echo "   - Selecciona el cluster donde está tu notebook"
echo "   - Copia el Cluster ID"
echo ""
echo "4. 📁 Notebook Path:"
echo "   - Ve a tu notebook HelloWorld.py"
echo "   - Copia la ruta completa (ejemplo: /Repos/your-repo/HelloWorld)"
echo ""

read -p "¿Tienes esta información lista? (y/n): " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo ""
    echo "🔧 Configurando variables de entorno..."
    echo ""
    
    read -p "Workspace URL: " workspace_url
    read -p "Access Token: " access_token
    read -p "Cluster ID: " cluster_id
    read -p "Notebook Path: " notebook_path
    
    echo ""
    echo "📝 Actualizando run-with-env.sh..."
    
    # Actualizar el script con las variables reales
    sed -i.bak "s|export DATABRICKS_WORKSPACE_URL=.*|export DATABRICKS_WORKSPACE_URL=\"$workspace_url\"|" run-with-env.sh
    sed -i.bak "s|export DATABRICKS_ACCESS_TOKEN=.*|export DATABRICKS_ACCESS_TOKEN=\"$access_token\"|" run-with-env.sh
    
    # Actualizar el workflow con el cluster ID y notebook path
    sed -i.bak "s|\"your-cluster-id\"|\"$cluster_id\"|" src/main/java/com/example/AzureFunctionWorkflowImpl.java
    sed -i.bak "s|/Repos/your-repo/HelloWorld|$notebook_path|" src/main/java/com/example/AzureFunctionWorkflowImpl.java
    
    echo "✅ Configuración completada!"
    echo ""
    echo "🎯 Para ejecutar el workflow:"
    echo "   ./run-with-env.sh"
    echo ""
    echo "📊 Para verificar la configuración:"
    echo "   cat run-with-env.sh | grep DATABRICKS"
    
else
    echo ""
    echo "ℹ️  Puedes configurar Databricks más tarde editando:"
    echo "   - run-with-env.sh (variables de entorno)"
    echo "   - src/main/java/com/example/AzureFunctionWorkflowImpl.java (cluster y notebook path)"
fi
