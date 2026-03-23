package com.nexus.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.nexus.exception.NexusValidationException;

public class Project {
    private String name;
    private int totalEffort = 0;
    private int totalBudget;
    private List<Task> tasks;
    

    public Project(String name, int totalBudget) {
        this.name = name;
        this.totalBudget = totalBudget;
        this.tasks = new ArrayList<>();
}

    public void addTask(Task task)
    {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (task.getEstimatedEffort() + this.totalEffort > totalBudget)
        {
            throw new NexusValidationException("Project budget exceeded");
        }
        
        this.totalEffort += task.getEstimatedEffort();
        this.tasks.add(task);
    }
    
    public String getName() { return this.name; };
    public int getBudget() { return this.totalBudget; };
    public Stream<Task> getTasks() { return tasks.stream(); };
}
