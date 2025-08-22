package com.example;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface DatabricksActivities {
    
    String submitNotebookJob(String notebookPath, String clusterId, String parameters);
    
    String getJobStatus(String runId);
    
    String getJobResult(String runId);
}
