package com.nexus.service;

import com.nexus.model.*;
import com.nexus.exception.NexusValidationException;
import com.nexus.Main;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LogProcessor {

    public void processLog(String fileName, Workspace workspace, List<User> users) {
        try {
            // Busca o arquivo dentro da pasta de recursos do projeto (target/classes)
            var resource = getClass().getClassLoader().getResourceAsStream(fileName);
            
            if (resource == null) {
                throw new IOException("Arquivo não encontrado no classpath: " + fileName);
            }

            try (java.util.Scanner s = new java.util.Scanner(resource).useDelimiter("\\A")) {
                String content = s.hasNext() ? s.next() : "";
                List<String> lines = List.of(content.split("\\R"));
                
                for (String line : lines) {
                    if (line.isBlank() || line.startsWith("#")) continue;

                    String[] arguments = line.split(";");
                    String action = arguments[0];

                    try {
                        switch (action) {
                            case "CREATE_USER" -> {
                                users.add(new User(arguments[1], arguments[2]));
                                System.out.println("[LOG] Usuário criado: " + arguments[1]);
                            }
                            case "CREATE_TASK" -> {

                                //toda task tem que pertencer a um project?? 

                                String taskName = arguments[1];
                                LocalDate dueDate = LocalDate.parse(arguments[2]);
                                int estimatedEffort = Integer.parseInt(arguments[3]);
                                Project project = Workspace.getProjectFromName(arguments[4]);

                                new Task(taskName, dueDate, estimatedEffort, project);
                                
                                System.out.println("[LOG] Tarefa criada: " + taskName);
                            }
                            case "CREATE_PROJECT" -> {
                                //lancar erro se ja existe project com esse nome? 
                                // checar se ja existe usando getProjectFromName
                                String projectName = arguments[1];
                                int budgetHours = Integer.parseInt(arguments[2]);
                                
                                new Project(projectName, budgetHours);

                                System.out.println("[LOG] Projeto criado: " + projectName);
                                
                            }
                            case "ASSIGN_USER" -> {
                                int taskId = Integer.parseInt(arguments[1]);
                                String userName = arguments[2];
                                User user = Main.getUserByName(userName);
                                //fazer stream p/ achar task por id
                                

                            }
                            case "CHANGE_STATUS" -> {



                            }
                            case "REPORT_STATUS" -> {



                            }
                            default -> System.err.println("[WARN] Ação desconhecida: " + action);
                        }
                    } catch (NexusValidationException e) {
                        System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }
}