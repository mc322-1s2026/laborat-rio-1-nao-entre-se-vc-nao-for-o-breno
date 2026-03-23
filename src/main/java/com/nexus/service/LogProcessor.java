package com.nexus.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.nexus.Main;
import com.nexus.exception.NexusValidationException;
import com.nexus.model.Project;
import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;

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
                                break;
                            }
                            case "CREATE_TASK" -> {

                                //toda task tem que pertencer a um project?? 

                                String taskName = arguments[1];
                                LocalDate dueDate = LocalDate.parse(arguments[2]);
                                int estimatedEffort = Integer.parseInt(arguments[3]);
                                Project project = Workspace.getProjectFromName(arguments[4]);

                                new Task(taskName, dueDate, estimatedEffort, project);
                                
                                System.out.println("[LOG] Tarefa criada: " + taskName);
                                
                                break;
                            }
                            case "CREATE_PROJECT" -> {
                                //lancar erro se ja existe project com esse nome? 
                                // checar se ja existe usando getProjectFromName
                                String projectName = arguments[1];
                                int budgetHours = Integer.parseInt(arguments[2]);
                                
                                new Project(projectName, budgetHours);

                                System.out.println("[LOG] Projeto criado: " + projectName);
                                
                                break;
                            }
                            case "ASSIGN_USER" -> {
                                int taskId = Integer.parseInt(arguments[1]);
                                Task task = workspace.getTaskById(taskId);

                                if (task == null) throw new IllegalArgumentException();

                                String userName = arguments[2];
                                User user = Main.getUserByName(userName);
                                //fazer stream p/ achar task por id

                                task.assignOwner(user);

                                System.out.println("[LOG] Tarefa delegada ao usuário: " + userName + " (" + task.getTitle() + ")");

                                break;
                            }
                            case "CHANGE_STATUS" -> {
                                int taskId = Integer.parseInt(arguments[1]);
                                Task task = workspace.getTaskById(taskId);
                                
                                if (task == null) throw new IllegalArgumentException();

                                String stringStatus = arguments[2];
                                
                                try {
                                    switch (stringStatus) {
                                        case "IN_PROGRESS" -> {
                                            task.moveToInProgress();
                                            break;
                                        }
                                        case "DONE" -> {
                                            task.markAsDone();
                                            break;
                                        }
                                        case "BLOCKED" -> {
                                            task.setBlocked();
                                            break;
                                        }
                                        default -> {
                                            throw new IllegalArgumentException();
                                        }
                                    }
                                    System.out.println("[LOG] Estado da Tarefa (" + task.getTitle() + ") alterado para " + task.getStatus() + " com sucesso.");
                                } catch (IllegalArgumentException e) {
                                    System.err.println("[ARGUMENTO INVÁLIDO] Falha no comando '" + line + "': " + e.getMessage());
                                } catch (NexusValidationException e) {
                                    System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                                }
                                break;

                            }
                            case "REPORT_STATUS" -> {
                                System.out.println("");
                                System.out.println("===== RELATORIOS =====");
                                System.out.println("[Usuarios sobrecarregados]:" );
                                workspace.overloadedUsers().forEach(user -> 
                                {
                                    System.out.println("-" + user.consultUsername());
                                });

                                TaskStatus bottleneck = workspace.getGlobalBottlenecks();
                                if (bottleneck != null)
                                {

                                System.out.println("[Status de maior Gargalo]: " + bottleneck);
                                }
                                else {
                                    System.out.println("[Status de maior Gargalo]: Nao ha gargalos!");
                                }
                    
                                System.out.println("[Status dos projetos]");

                                workspace.getProjectNameList().forEach(
                                    projectName -> {
                                        Project project = Workspace.getProjectFromName(projectName);
                                        System.out.println(projectName + ": " + workspace.getProjectHealth(project) + "% Finished.");
                                    }
                                );
                                

                                

                                break;
                            }
                            default -> System.err.println("[WARN] Ação desconhecida: " + action);
                        }
                    } catch (NexusValidationException e) {
                        System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                    }
                    catch (IllegalArgumentException e) {
                        System.err.println("[ARGUMENTO INVÁLIDO] Falha no comando '" + line + "': " + e.getMessage());
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("[NÚMERO INCORRETO DE ARGUMENTOS] Falha no comando '" + line + "': " + e.getMessage());                        
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }
}