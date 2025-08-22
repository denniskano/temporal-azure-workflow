package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class DatabricksActivitiesImpl implements DatabricksActivities {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabricksActivitiesImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS) // Longer timeout for Databricks jobs
            .build();
    
    @Override
    public String submitNotebookJob(String notebookPath, String clusterId, String parameters) {
        logger.info("Submitting Databricks notebook job: {} on cluster: {}", notebookPath, clusterId);
        
        String workspaceUrl = System.getenv("DATABRICKS_WORKSPACE_URL");
        String accessToken = System.getenv("DATABRICKS_ACCESS_TOKEN");
        
        if (workspaceUrl == null || accessToken == null) {
            logger.warn("Databricks not configured, using mock response");
            return "mock_run_id_12345";
        }
        
        try {
            // Create the job submission request using Jackson for proper JSON serialization
            ObjectMapper mapper = new ObjectMapper();
            
            // Create the request object
            ObjectNode requestNode = mapper.createObjectNode();
            requestNode.put("run_name", "Temporal-Orchestrated-Job");
            requestNode.put("existing_cluster_id", clusterId);
            
            ObjectNode notebookTaskNode = mapper.createObjectNode();
            notebookTaskNode.put("notebook_path", notebookPath);
            
            // Handle parameters more safely
            ObjectNode baseParametersNode = mapper.createObjectNode();
            if (parameters != null && !parameters.trim().isEmpty()) {
                try {
                    // Try to parse as JSON first
                    JsonNode parametersNode = mapper.readTree(parameters);
                    if (parametersNode.isObject()) {
                        baseParametersNode = (ObjectNode) parametersNode;
                    }
                } catch (Exception e) {
                    // If parsing fails, treat as simple string
                    logger.warn("Failed to parse parameters as JSON, treating as string: {}", e.getMessage());
                    baseParametersNode.put("input_data", parameters);
                    baseParametersNode.put("source", "temporal-workflow");
                }
            } else {
                // Default parameters
                baseParametersNode.put("input_data", "default_data");
                baseParametersNode.put("source", "temporal-workflow");
            }
            
            notebookTaskNode.set("base_parameters", baseParametersNode);
            requestNode.set("notebook_task", notebookTaskNode);
            
            String requestBody = mapper.writeValueAsString(requestNode);
            
            logger.info("Databricks request URL: {}", workspaceUrl + "/api/2.1/jobs/runs/submit");
            logger.info("Databricks request body: {}", requestBody);
            
            Request request = new Request.Builder()
                    .url(workspaceUrl + "/api/2.1/jobs/runs/submit")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No error body";
                    logger.error("Databricks job submission failed: {} - {} - Body: {}", response.code(), response.message(), errorBody);
                    throw new RuntimeException("Databricks job submission failed: " + response.code() + " - " + errorBody);
                }
                
                String responseBody = response.body().string();
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                String runId = jsonResponse.get("run_id").asText();
                
                logger.info("Databricks job submitted successfully with run_id: {}", runId);
                return runId;
            }
            
        } catch (IOException e) {
            logger.error("Error submitting Databricks job", e);
            throw new RuntimeException("Failed to submit Databricks job", e);
        }
    }
    
    @Override
    public String getJobStatus(String runId) {
        logger.info("Getting Databricks job status for run_id: {}", runId);
        
        String workspaceUrl = System.getenv("DATABRICKS_WORKSPACE_URL");
        String accessToken = System.getenv("DATABRICKS_ACCESS_TOKEN");
        
        if (workspaceUrl == null || accessToken == null) {
            logger.warn("Databricks not configured, using mock status");
            // Simular que el job termina después de algunas verificaciones
            if (runId.contains("mock_run_id_12345")) {
                // Simular progreso del job
                long currentTime = System.currentTimeMillis();
                long startTime = 1755833142832L; // Timestamp aproximado del inicio
                long elapsed = currentTime - startTime;
                
                if (elapsed > 30000) { // Terminar después de 30 segundos
                    return "TERMINATED";
                } else if (elapsed > 15000) { // En progreso después de 15 segundos
                    return "RUNNING";
                } else {
                    return "PENDING";
                }
            }
            return "RUNNING";
        }
        
        try {
            Request request = new Request.Builder()
                    .url(workspaceUrl + "/api/2.1/jobs/runs/get?run_id=" + runId)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .get()
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("Failed to get Databricks job status: {} - {}", response.code(), response.message());
                    throw new RuntimeException("Failed to get Databricks job status: " + response.code());
                }
                
                String responseBody = response.body().string();
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                String state = jsonResponse.get("state").get("life_cycle_state").asText();
                
                logger.info("Databricks job status: {}", state);
                return state;
            }
            
        } catch (IOException e) {
            logger.error("Error getting Databricks job status", e);
            throw new RuntimeException("Failed to get Databricks job status", e);
        }
    }
    
    @Override
    public String getJobResult(String runId) {
        logger.info("Getting Databricks job result for run_id: {}", runId);
        
        String workspaceUrl = System.getenv("DATABRICKS_WORKSPACE_URL");
        String accessToken = System.getenv("DATABRICKS_ACCESS_TOKEN");
        
        if (workspaceUrl == null || accessToken == null) {
            logger.warn("Databricks not configured, using mock result");
            return "{\"result\":\"mock_databricks_result\"}";
        }
        
        try {
            Request request = new Request.Builder()
                    .url(workspaceUrl + "/api/2.1/jobs/runs/get-output?run_id=" + runId)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .get()
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("Failed to get Databricks job result: {} - {}", response.code(), response.message());
                    throw new RuntimeException("Failed to get Databricks job result: " + response.code());
                }
                
                String responseBody = response.body().string();
                logger.info("Databricks job result retrieved successfully");
                return responseBody;
            }
            
        } catch (IOException e) {
            logger.error("Error getting Databricks job result", e);
            throw new RuntimeException("Failed to get Databricks job result", e);
        }
    }
}
