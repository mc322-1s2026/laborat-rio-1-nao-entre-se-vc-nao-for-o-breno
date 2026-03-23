package com.nexus.service;

import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator;

public class Workspace {
    private final List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

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
}
