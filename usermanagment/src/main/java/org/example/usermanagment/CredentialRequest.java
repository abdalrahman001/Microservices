package org.example.usermanagment;

public class CredentialRequest {
    private String username;
    private String password;

    // No-arg constructor (needed by Jackson)
    public CredentialRequest() {}

    // Getters and Setters
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
