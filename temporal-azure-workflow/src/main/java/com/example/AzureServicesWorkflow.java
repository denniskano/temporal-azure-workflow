package com.example;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface AzureServicesWorkflow {
    
    @WorkflowMethod
    String orchestrateAzureServices(String inputData);
}
