package com.example;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class AzureFunctionWorkflowImpl implements AzureFunctionWorkflow {
    
    private final ActivityOptions activityOptions = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(5))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setInitialInterval(Duration.ofSeconds(1))
                    .setMaximumInterval(Duration.ofSeconds(10))
                    .setBackoffCoefficient(2.0)
                    .setMaximumAttempts(3)
                    .build())
            .build();
    
    private final AzureFunctionActivities activities = 
            Workflow.newActivityStub(AzureFunctionActivities.class, activityOptions);
    
    @Override
    public String processWithAzureFunction(String inputData) {
        // Log the start of the workflow
        Workflow.getLogger(AzureFunctionWorkflowImpl.class).info("Starting Azure Function workflow with input: {}", inputData);
        
        // Step 1: Call Azure Function to process the data
        String processedData = activities.callAzureFunction(inputData);
        
        // Step 2: Call Azure Function to validate the processed data
        String validationResult = activities.validateData(processedData);
        
        // Step 3: Call Azure Function to store the final result
        String finalResult = activities.storeResult(processedData, validationResult);
        
        Workflow.getLogger(AzureFunctionWorkflowImpl.class).info("Workflow completed successfully. Final result: {}", finalResult);
        
        return finalResult;
    }
}
