package com.nexus.service;

import com.nexus.model.Task;
import com.nexus.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
}
