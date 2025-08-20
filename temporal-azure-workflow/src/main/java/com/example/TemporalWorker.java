package com.example;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class TemporalWorker {
    
    private static final String TASK_QUEUE = "AZURE_FUNCTION_TASK_QUEUE";
    
    public static void main(String[] args) {
        // Create a gRPC connection to the Temporal server
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        
        // Create a WorkflowClient
        WorkflowClient client = WorkflowClient.newInstance(service);
        
        // Create a WorkerFactory
        WorkerFactory factory = WorkerFactory.newInstance(client);
        
        // Create a Worker
        Worker worker = factory.newWorker(TASK_QUEUE);
        
        // Register the workflow implementation
        worker.registerWorkflowImplementationTypes(AzureFunctionWorkflowImpl.class);
        
        // Register the activity implementation
        worker.registerActivitiesImplementations(new AzureFunctionActivitiesImpl());
        
        // Start the worker
        factory.start();
        
        System.out.println("Worker started. Listening on task queue: " + TASK_QUEUE);
        System.out.println("Press Ctrl+C to exit.");
        
        // Keep the worker running
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("Worker interrupted. Shutting down...");
        }
    }
}
