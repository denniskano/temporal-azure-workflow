package com.example;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import java.time.Duration;

public class WorkflowStarter {
    
    private static final String TASK_QUEUE = "AZURE_SERVICES_TASK_QUEUE";
    
    public static void main(String[] args) {
        // Create a gRPC connection to the Temporal server
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        
        // Create a WorkflowClient
        WorkflowClient client = WorkflowClient.newInstance(service);
        
        // Create workflow options
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(TASK_QUEUE)
                .setWorkflowId("azure-services-workflow-" + System.currentTimeMillis())
                .setWorkflowExecutionTimeout(Duration.ofMinutes(10))
                .build();
        
        // Create a workflow stub
        AzureServicesWorkflow workflow = client.newWorkflowStub(AzureServicesWorkflow.class, options);
        
        // Start the workflow
        System.out.println("Starting Azure Services orchestration workflow...");
        String result = workflow.orchestrateAzureServices("Hello from Temporal!");
        
        System.out.println("Workflow completed successfully!");
        System.out.println("Result: " + result);
    }
}
