package com.example;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class AzureServicesWorkflowImpl implements AzureServicesWorkflow {
    
    private final ActivityOptions activityOptions = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(5))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setInitialInterval(Duration.ofSeconds(1))
                    .setMaximumInterval(Duration.ofSeconds(10))
                    .setBackoffCoefficient(2.0)
                    .setMaximumAttempts(3)
                    .build())
            .build();
    
    private final AzureServicesActivities azureActivities = 
            Workflow.newActivityStub(AzureServicesActivities.class, activityOptions);
    
    private final DatabricksActivities databricksActivities = 
            Workflow.newActivityStub(DatabricksActivities.class, activityOptions);
    
    @Override
    public String orchestrateAzureServices(String inputData) {
        // Log the start of the workflow
        Workflow.getLogger(AzureServicesWorkflowImpl.class).info("Starting Azure Services orchestration workflow with input: {}", inputData);
        
        // Step 1: Call Azure Function to process the data
        String processedData = azureActivities.callAzureFunction(inputData);
        
        // Step 2: Submit Databricks notebook job
        String databricksRunId = databricksActivities.submitNotebookJob(
            "/Workspace/Users/at10640@bcp.com.pe/notebook-peve-dev", // Ruta a tu notebook
            "0822-022205-xj9o1s00", 
            "{\"input_data\":\"" + processedData + "\", \"source\":\"temporal-workflow\"}"
        );
        
        // Step 3: Wait for Databricks job to complete
        String jobStatus = "RUNNING";
        while ("RUNNING".equals(jobStatus) || "PENDING".equals(jobStatus)) {
            Workflow.sleep(Duration.ofSeconds(10)); // Wait 10 seconds before checking again
            jobStatus = databricksActivities.getJobStatus(databricksRunId);
        }
        
        // Step 4: Get Databricks job result
        String databricksResult = databricksActivities.getJobResult(databricksRunId);
        
        // Step 5: Call Azure Function to validate the processed data
        String validationResult = azureActivities.validateData(databricksResult);
        
        // Step 6: Call Azure Function to store the final result
        String finalResult = azureActivities.storeResult(databricksResult, validationResult);
        
        Workflow.getLogger(AzureServicesWorkflowImpl.class).info("Workflow completed successfully. Final result: {}", finalResult);
        
        return finalResult;
    }
}
