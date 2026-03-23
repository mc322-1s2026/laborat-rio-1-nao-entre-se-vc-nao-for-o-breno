package com.nexus.service;

import com.nexus.model.Project;
import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator;
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

    
    public static void addProjectToMap(String projectName, Project project) {projectFromNameMap.put(projectName,project);}
    public static Project getProjectFromName(String projectName) {return projectFromNameMap.get(projectName);}

    // Retorna uma lista imutavel com os top_n usuarios com mais tasks
    public List<User> topPerformers(int top_n) {

    // Criando um HashMap com a relacao de usuarios e qtd de tarefas
    Map<User, Long> taskCountByUser = tasks.stream()
            .collect(Collectors.groupingBy(Task::getOwner, Collectors.counting()));

    // Retornando os n usuarios com mais tasks
    return taskCountByUser.entrySet().stream()
        .sorted(Map.Entry.<User, Long>comparingByValue(Comparator.reverseOrder()))
        .limit(top_n)
        .map(Map.Entry::getKey)
        .toList();
}


    // Retorna uma lista de usuarios com mais de 10 tasks
    public List<User> overloadedUsers() {
    Map<User, Long> taskCountByUser = tasks.stream()
            .collect(Collectors.groupingBy(Task::getOwner, Collectors.counting()));


    // Retornando os usuarios com mais de 10 tasks
    return taskCountByUser.entrySet().stream()
        .filter(entry -> entry.getValue() > 10)
        .map(Map.Entry::getKey)
        .toList();
    }


    public TaskStatus getGlobalBottlenecks() {
    Map<TaskStatus, Long> taskCountByStatus = tasks.stream()
            .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

    return taskCountByStatus.entrySet().stream()
        .sorted(Map.Entry.<TaskStatus, Long>comparingByValue(Comparator.reverseOrder()))
        .limit(1)
        .map(entry -> entry.getKey())
        .findFirst().orElse(null) ;
}


    public Task getTaskById(int id) {
        Task task = tasks.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
        return task;
    }

    public double getProjectHealth(Project project) {
        long totalTasks = project.getTasks().count();
        long doneTasks = project.getTasks().filter(t -> t.getStatus() == TaskStatus.DONE).count();

        if (totalTasks != 0)
        {
            return (doneTasks * 100.0) / totalTasks;
        }

        return 100.0; // Se nao tem nenhuma task para fazer, entao o projeto esta concluido
    }

}
