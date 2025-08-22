# Databricks notebook source
# MAGIC %md
# MAGIC # Hola Mundo desde Azure Databricks
# MAGIC 
# MAGIC Este es un notebook de ejemplo para demostrar la funcionalidad básica de Databricks.

# COMMAND ----------

# MAGIC %md
# MAGIC ## Configuración inicial

# COMMAND ----------

# Configurar Spark
spark.conf.set("spark.sql.adaptive.enabled", "true")

# COMMAND ----------

# MAGIC %md
# MAGIC ## Crear datos de ejemplo

# COMMAND ----------

# Crear DataFrame de ejemplo
from pyspark.sql import SparkSession
from pyspark.sql.types import StructType, StructField, StringType, IntegerType

# Definir esquema
schema = StructType([
    StructField("id", IntegerType(), False),
    StructField("nombre", StringType(), True),
    StructField("mensaje", StringType(), True)
])

# Crear datos
data = [
    (1, "Usuario1", "Hola Mundo desde Databricks!"),
    (2, "Usuario2", "¡Bienvenido a Azure Databricks!"),
    (3, "Usuario3", "Procesamiento de datos en la nube")
]

# Crear DataFrame
df = spark.createDataFrame(data, schema)

# COMMAND ----------

# MAGIC %md
# MAGIC ## Mostrar datos

# COMMAND ----------

# Mostrar datos
display(df)

# COMMAND ----------

# MAGIC %md
# MAGIC ## Análisis básico

# COMMAND ----------

# Contar registros
print(f"Total de registros: {df.count()}")

# Mostrar esquema
df.printSchema()

# COMMAND ----------

# MAGIC %md
# MAGIC ## Guardar resultados

# COMMAND ----------

# Guardar como tabla temporal
df.createOrReplaceTempView("hello_world_data")

# Mostrar tabla
spark.sql("SELECT * FROM hello_world_data").show()

# COMMAND ----------

# MAGIC %md
# MAGIC ## ¡Hola Mundo completado!
# MAGIC 
# MAGIC Este notebook demuestra:
# MAGIC - Creación de DataFrames
# MAGIC - Manipulación de datos
# MAGIC - Consultas SQL
# MAGIC - Visualización de datos
