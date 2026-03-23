package com.nexus.service;

import com.nexus.exception.NexusValidationException;
import com.nexus.model.Project;
import com.nexus.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;

public class Workspace {
    private static final List<Task> tasks = new ArrayList<>();
    private static HashMap<String,Project> projectFromNameMap = new HashMap<>(); 

    public static void addTask(Task task)
    {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        tasks.add(task);
    }

    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

    // public Task getTaskById(int id) {


    // }
    
    public static void addProjectToMap(String projectName, Project project) {projectFromNameMap.put(projectName,project);}
    public static Project getProjectFromName(String projectName) {return projectFromNameMap.get(projectName);}
}