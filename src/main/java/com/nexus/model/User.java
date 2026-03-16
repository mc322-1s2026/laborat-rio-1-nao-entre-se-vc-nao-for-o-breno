package com.nexus.model;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {
    private final String username;
    private final String email;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }
    public User(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }
        this.username = username;

        if (!validate(email))
        {
            throw new IllegalArgumentException("Email invalido.");
        }
        this.email = email;
    }

    public String consultEmail() {
        return email;
    }

    public String consultUsername() {
        return username;
    }

    public long calculateWorkload(List<Task> tasks) {
    return tasks.stream()
        .filter(task -> task.getOwner() == this)
        .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
        .count();
}
}
