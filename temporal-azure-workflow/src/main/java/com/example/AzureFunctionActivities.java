package com.example;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface AzureFunctionActivities {
    
    String callAzureFunction(String inputData);
    
    String validateData(String processedData);
    
    String storeResult(String processedData, String validationResult);
}
