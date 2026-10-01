package com.example.demo.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Body for POST /api/auth/register. The annotations are checked automatically because of @Valid.
public class RegisterRequest {

    @NotBlank(message = "username is required")
    @Pattern(regexp = "^[A-Za-z0-9._-]{3,20}$",
             message = "username must be 3-20 letters, numbers, dots, dashes or underscores")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 64, message = "password must be at least 8 characters")
    private String password;

    @NotBlank(message = "firstName is required")
    @Size(max = 50, message = "firstName is too long")
    private String firstName;

    @Size(max = 50, message = "lastName is too long")
    private String lastName;

    @Email(message = "email is not valid")
    private String email;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
