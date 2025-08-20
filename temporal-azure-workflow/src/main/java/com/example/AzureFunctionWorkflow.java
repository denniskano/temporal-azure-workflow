package com.example;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface AzureFunctionWorkflow {
    
    @WorkflowMethod
    String processWithAzureFunction(String inputData);
}
