package com.nexus.model;

import java.time.LocalDate;

import com.nexus.service.Workspace;
import com.nexus.exception.NexusValidationException;

public class Task {
    // Métricas Globais (Alunos implementam a lógica de incremento/decremento)
    public static int totalTasksCreated = 0;
    public static int totalValidationErrors = 0;
    public static int activeWorkload = 0;

    private static int nextId = 1;

    private int id;
    private int estimatedEffort;
    private LocalDate deadline; // Imutável após o nascimento
    private String title;
    private TaskStatus status;
    private User owner;
    private Project project;

    public Task(String title, LocalDate deadline, int estimatedEffort, Project project) {
        this.id = nextId++;
        this.deadline = deadline;
        this.title = title;
        this.status = TaskStatus.TO_DO;
        this.estimatedEffort = estimatedEffort;
        this.project = project;
        Workspace.addTask(this);
        if (this.project != null) {
            this.project.addTask(this);
        }
        // Ação do Aluno:
        totalTasksCreated++; 
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Só é possível se houver um owner atribuído e não estiver BLOCKED.
     */
    public void moveToInProgress(User user) {

        if (this.status == TaskStatus.BLOCKED) {
            totalValidationErrors++;
            throw new NexusValidationException("Blocked task cannot move to IN_PROGRESS");
        }

        if (user == null)
        {
            totalValidationErrors += 1;
            throw new IllegalArgumentException("User cannot be null");
        }
        this.owner = user;
        this.status = TaskStatus.IN_PROGRESS;
        activeWorkload += 1;
    }

    /**
     * Finaliza a tarefa.
     * Regra: Só pode ser movida para DONE se não estiver BLOCKED.
     */
    public void markAsDone() {
        if (this.status == TaskStatus.BLOCKED)
        {
            totalValidationErrors += 1;
            throw new NexusValidationException("BLOCKED Task cannot be marked as Done");
        }
        if (this.status == TaskStatus.IN_PROGRESS)
        {
            this.status = TaskStatus.DONE;
            activeWorkload -= 1;
        }
    }


    public void setBlocked(boolean blocked) {

    if (blocked) {

        if (this.status == TaskStatus.DONE) {
            totalValidationErrors++;
            throw new NexusValidationException("DONE tasks cannot be blocked");
        }

        this.status = TaskStatus.BLOCKED;

    } else {
        this.status = TaskStatus.TO_DO;
    }
}

    // Getters
    public int getId() { return id; }
    public TaskStatus getStatus() { return status; }
    public String getTitle() { return title; }
    public LocalDate getDeadline() { return deadline; }
    public User getOwner() { return owner; }
    public int getEstimatedEffort() { return estimatedEffort; }
}

