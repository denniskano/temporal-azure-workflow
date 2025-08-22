package com.example;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface AzureServicesActivities {
    
    String callAzureFunction(String inputData);
    
    String validateData(String processedData);
    
    String storeResult(String processedData, String validationResult);
}
