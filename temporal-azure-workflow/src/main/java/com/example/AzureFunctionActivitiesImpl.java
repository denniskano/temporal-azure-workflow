package com.example;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AzureFunctionActivitiesImpl implements AzureFunctionActivities {
    
    private static final Logger logger = LoggerFactory.getLogger(AzureFunctionActivitiesImpl.class);
    
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
    
    @Override
    public String callAzureFunction(String inputData) {
        logger.info("Calling Azure Function to process data: {}", inputData);
        
        String baseUrl = System.getenv("AZURE_FUNCTION_BASE_URL");
        String functionKey = System.getenv("AZURE_FUNCTION_KEY");
        

        
        // Si las variables de entorno están configuradas, usar Azure Functions reales
        if (baseUrl != null && functionKey != null && !baseUrl.equals("https://your-azure-function.azurewebsites.net/api")) {
            try {
                String requestBody = "{\"data\":\"" + inputData + "\", \"step\":\"process\"}";
                
                Request request = new Request.Builder()
                        .url(baseUrl + "/hello-world-function/")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("x-functions-key", functionKey)
                        .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                        .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        logger.error("Azure Function call failed: {} - {}", response.code(), response.message());
                        throw new RuntimeException("Azure Function call failed: " + response.code());
                    }
                    
                    String responseBody = response.body().string();
                    logger.info("Azure Function processed data successfully: {}", responseBody);
                    return responseBody;
                }
                
            } catch (IOException e) {
                logger.error("Error calling Azure Function", e);
                throw new RuntimeException("Failed to call Azure Function", e);
            }
        } else {
            // For demo purposes, return a mock response
            logger.info("Using mock response (Azure Function not configured)");
            return "processed_" + inputData;
        }
    }
    
    @Override
    public String validateData(String processedData) {
        logger.info("Validating processed data: {}", processedData);
        
        String baseUrl = System.getenv("AZURE_FUNCTION_BASE_URL");
        String functionKey = System.getenv("AZURE_FUNCTION_KEY");
        
        // Si las variables de entorno están configuradas, usar Azure Functions reales
        if (baseUrl != null && functionKey != null && !baseUrl.equals("https://your-azure-function.azurewebsites.net/api")) {
            try {
                String requestBody = "{\"data\":\"" + processedData + "\", \"step\":\"validate\"}";
                
                Request request = new Request.Builder()
                        .url(baseUrl + "/hello-world-function/")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("x-functions-key", functionKey)
                        .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                        .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        logger.error("Azure Function validation failed: {} - {}", response.code(), response.message());
                        throw new RuntimeException("Azure Function validation failed: " + response.code());
                    }
                    
                    String responseBody = response.body().string();
                    logger.info("Data validation completed: {}", responseBody);
                    return responseBody;
                }
                
            } catch (IOException e) {
                logger.error("Error validating data", e);
                throw new RuntimeException("Failed to validate data", e);
            }
        } else {
            // For demo purposes, return a mock response
            logger.info("Using mock response (Azure Function not configured)");
            return "validated_" + processedData;
        }
    }
    
    @Override
    public String storeResult(String processedData, String validationResult) {
        logger.info("Storing result - processed data: {}, validation: {}", processedData, validationResult);
        
        String baseUrl = System.getenv("AZURE_FUNCTION_BASE_URL");
        String functionKey = System.getenv("AZURE_FUNCTION_KEY");
        
        // Si las variables de entorno están configuradas, usar Azure Functions reales
        if (baseUrl != null && functionKey != null && !baseUrl.equals("https://your-azure-function.azurewebsites.net/api")) {
            try {
                String requestBody = "{\"processedData\":\"" + processedData + "\", \"validationResult\":\"" + validationResult + "\", \"step\":\"store\"}";
                
                Request request = new Request.Builder()
                        .url(baseUrl + "/hello-world-function/")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("x-functions-key", functionKey)
                        .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                        .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        logger.error("Azure Function storage failed: {} - {}", response.code(), response.message());
                        throw new RuntimeException("Azure Function storage failed: " + response.code());
                    }
                    
                    String responseBody = response.body().string();
                    logger.info("Result stored successfully: {}", responseBody);
                    return responseBody;
                }
                
            } catch (IOException e) {
                logger.error("Error storing result", e);
                throw new RuntimeException("Failed to store result", e);
            }
        } else {
            // For demo purposes, return a mock response
            logger.info("Using mock response (Azure Function not configured)");
            return "stored_" + processedData + "_" + validationResult;
        }
    }
}
